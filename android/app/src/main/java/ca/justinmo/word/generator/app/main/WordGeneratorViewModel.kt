package ca.justinmo.word.generator.app.main

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ca.justinmo.word.generator.app.data.WordInfo
import ca.justinmo.word.generator.app.data.WordRepository
import ca.justinmo.word.generator.app.ui.ConstraintType
import ca.justinmo.word.generator.app.ui.ExplorationUiState
import ca.justinmo.word.generator.app.ui.GeneratorUiState
import ca.justinmo.word.generator.app.ui.SettingsUiState
import ca.justinmo.word.generator.app.ui.VoiceItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class WordGeneratorViewModel : ViewModel() {

    private val wordRepository: WordRepository = WordGeneratorApplication.wordRepository
    private val settingsRepository = WordGeneratorApplication.settingsRepository

    // --- Generator State ---
    private val _generatorUiState = MutableStateFlow(GeneratorUiState(word = null, isPlaying = false))
    val generatorUiState: StateFlow<GeneratorUiState> = _generatorUiState.asStateFlow()

    private val _sessionHistory = mutableListOf<String>()
    val sessionHistory: List<String> get() = _sessionHistory

    private val _sessionFavorites = mutableSetOf<String>()
    val sessionFavorites: Set<String> get() = _sessionFavorites

    // --- Exploration State ---
    private val _explorationUiState = MutableStateFlow(ExplorationUiState())
    val explorationUiState: StateFlow<ExplorationUiState> = _explorationUiState.asStateFlow()
    private var explorationLoadJob: Job? = null

    // --- Settings State ---
    private val _settingsUiState = MutableStateFlow(SettingsUiState())
    val settingsUiState: StateFlow<SettingsUiState> = _settingsUiState.asStateFlow()

    // --- TTS ---
    private lateinit var tts: TextToSpeech
    private var isTtsReady: Boolean = false

    private val availableVoicesRaw: List<Voice>
        get() {
            return if (!isTtsReady) emptyList() else tts.voices.asSequence().filter {
                it.name.startsWith("fr-")
                        && !it.isNetworkConnectionRequired
                        && !it.features.contains("legacySetLanguageVoice")
            }
                .sortedBy { it.name }
                .toList()
        }

    init {
        loadNextWord()
        observeExplorationInput()
        observeSettings()
    }

    // --- Lifecycle / Init ---
    fun initialize(context: Context) {
        if (::tts.isInitialized) return
        
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
                applySelectedVoice()
                tts.setPitch(1f)
                tts.setSpeechRate(1f)
                
                // Initialize voices list for settings
                val frenchVoices = tts.voices.filter {
                    it.name.startsWith("fr-") &&
                        !it.isNetworkConnectionRequired &&
                        !it.features.contains("legacySetLanguageVoice")
                }.sortedBy { it.name }
                _settingsUiState.update { it.copy(availableVoices = mapVoices(frenchVoices)) }
            }
        }

        tts.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
                override fun onStart(s: String?) = Unit
                override fun onDone(s: String?) {
                    viewModelScope.launch {
                        delay(1.seconds)
                        launch(Dispatchers.Main) {
                            maybeAutoPlayNextWord()
                        }
                    }
                }
                @Deprecated("Deprecated in Java", ReplaceWith(""))
                override fun onError(s: String?) = Unit
            },
        )
    }

    private fun applySelectedVoice() {
        val selectedName = settingsRepository.selectedVoiceName.value
        val voice = tts.voices.find { it.name == selectedName }
            ?: availableVoicesRaw.firstOrNull()
        
        if (voice != null) {
            tts.voice = voice
        }
    }

    private fun maybeAutoPlayNextWord() {
        if (generatorUiState.value.isPlaying) {
            loadNextWord()
        }
    }

    // --- Generator Logic ---
    fun loadNextWord() {
        viewModelScope.launch {
            wordRepository.generateWordTask().let { generatedWord ->
                _generatorUiState.getAndUpdate { uiState ->
                    if (uiState.isPlaying) {
                        readWord(generatedWord.word)
                    }
                    _sessionHistory.add(generatedWord.word)
                    if (generatedWord.isFavorite) {
                        _sessionFavorites.add(generatedWord.word)
                    }
                    uiState.copy(word = generatedWord)
                }
            }
        }
    }

    fun onClickPlay(word: WordInfo) {
        _generatorUiState.getAndUpdate { uiState ->
            val isPlaying = !uiState.isPlaying
            if (isPlaying) {
                readWord(word.word)
            }
            uiState.copy(isPlaying = isPlaying)
        }
    }

    fun pauseAutoPlay() {
        _generatorUiState.update { it.copy(isPlaying = false) }
        if (::tts.isInitialized) {
            tts.stop()
        }
    }

    // --- Exploration Logic ---
    @OptIn(FlowPreview::class)
    private fun observeExplorationInput() {
        viewModelScope.launch {
            explorationUiState
                .map { Triple(it.query, it.type, it.limit) }
                .distinctUntilChanged()
                .debounce { (_, _, limit) ->
                    if (limit == 100) 300.milliseconds else 0.milliseconds
                }
                .collectLatest { (query, type, limit) ->
                    if (query.isEmpty()) {
                        explorationLoadJob?.cancel()
                        _explorationUiState.update { it.copy(results = emptyList(), isLoading = false, limit = 100) }
                        return@collectLatest
                    }
                    if (limit == 100) {
                        _explorationUiState.update { it.copy(results = emptyList(), isLoading = true) }
                    }
                    performExplorationLoad(query, type, limit)
                }
        }
    }

    private fun performExplorationLoad(query: String, type: ConstraintType, limit: Int) {
        explorationLoadJob?.cancel()
        explorationLoadJob = viewModelScope.launch {
            _explorationUiState.update { it.copy(isLoading = true) }
            val flow = if (type == ConstraintType.PREFIX) {
                wordRepository.exploreWordsWithPrefix(query, limit)
            } else {
                wordRepository.exploreWordsWithSuffix(query, limit)
            }
            
            flow.collect { wordInfo ->
                _explorationUiState.update { state ->
                    if (state.query != query || state.type != type) return@update state
                    
                    val isDuplicate = state.results.any { it.word.equals(wordInfo.word, ignoreCase = true) }
                    val isInput = wordInfo.word.equals(query, ignoreCase = true)
                    
                    if (isDuplicate || isInput) {
                        state
                    } else {
                        state.copy(results = state.results + wordInfo)
                    }
                }
            }
            _explorationUiState.update { state ->
                if (state.query != query || state.type != type) state else state.copy(isLoading = false)
            }
        }
    }

    fun onExplorationQueryChanged(newQuery: String) {
        _explorationUiState.update { it.copy(query = newQuery, results = emptyList(), isLoading = newQuery.isNotEmpty(), limit = 100) }
    }

    fun onExplorationTypeChanged(newType: ConstraintType) {
        _explorationUiState.update { it.copy(type = newType, results = emptyList(), isLoading = true, limit = 100) }
    }

    fun onExplorationLoadMore() {
        if (explorationUiState.value.isLoading) return
        _explorationUiState.update { it.copy(limit = it.limit + 100) }
    }

    // --- Settings Logic ---
    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.temperature.collect { temp ->
                _settingsUiState.update { it.copy(temperature = temp) }
            }
        }
        viewModelScope.launch {
            settingsRepository.selectedVoiceName.collect { name ->
                _settingsUiState.update { it.copy(selectedVoiceName = name) }
                if (isTtsReady) applySelectedVoice()
            }
        }
    }

    private fun mapVoices(voices: List<Voice>): List<VoiceItem> {
        val caVoices = voices.filter { it.name.startsWith("fr-ca") }
        val frVoices = voices.filter { it.name.startsWith("fr-fr") }
        fun mapList(list: List<Voice>, country: String): List<VoiceItem> {
            var femaleCount = 0
            var maleCount = 0
            return list.mapIndexed { index, voice ->
                val isFemale = index % 2 == 0
                val label = if (isFemale) { femaleCount++; "Femme $femaleCount" } 
                            else { maleCount++; "Homme $maleCount" }
                VoiceItem(voice.name, "$country ($label)")
            }
        }
        return mapList(caVoices, "Canada") + mapList(frVoices, "France")
    }

    fun onTemperatureChanged(value: Float) {
        settingsRepository.setTemperature(value)
    }

    fun onVoiceSelected(voiceName: String) {
        settingsRepository.setSelectedVoiceName(voiceName)
    }

    fun onPreviewVoice(voiceName: String, displayName: String) {
        if (isTtsReady) {
            val voice = tts.voices.find { it.name == voiceName }
            if (voice != null) {
                tts.voice = voice
                tts.speak(displayName, TextToSpeech.QUEUE_FLUSH, null, "preview")
            }
        }
    }

    // --- Shared Actions ---
    fun onClickFavorite(word: String, isFavorite: Boolean) {
        if (isFavorite) {
            _sessionFavorites.remove(word)
        } else {
            _sessionFavorites.add(word)
        }
        viewModelScope.launch {
            if (isFavorite) {
                wordRepository.removeFavorite(word)
            } else {
                wordRepository.addFavorite(word)
            }
        }
        // Update generator state if current word matches
        if (generatorUiState.value.word?.word == word) {
            _generatorUiState.update { it.copy(word = it.word?.copy(isFavorite = !isFavorite)) }
        }
        // Update exploration results
        _explorationUiState.update { state ->
            state.copy(results = state.results.map {
                if (it.word == word) it.copy(isFavorite = !isFavorite) else it
            })
        }
    }

    fun readWord(word: String) {
        if (isTtsReady) {
            tts.speak("$word.", TextToSpeech.QUEUE_FLUSH, null, word.hashCode().toString())
        }
    }

    fun favorites(): Flow<List<WordInfo>> = wordRepository.geFavoritesStream()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                WordGeneratorViewModel()
            }
        }
    }
}
