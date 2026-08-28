package ca.justinmo.word.generator.app.ui

import android.speech.tts.Voice
import ca.justinmo.word.generator.app.data.WordInfo

/**
 * UiState for the generator screen.
 */
data class GeneratorUiState(
    val word: WordInfo? = null,
    val isPlaying: Boolean = false,
)

/**
 * Exploration types.
 */
enum class ConstraintType {
    PREFIX, SUFFIX
}

/**
 * UiState for the exploration screen.
 */
data class ExplorationUiState(
    val query: String = "",
    val type: ConstraintType = ConstraintType.PREFIX,
    val results: List<WordInfo> = emptyList(),
    val isLoading: Boolean = false,
    val limit: Int = 100,
)

/**
 * Voice item for settings dropdown.
 */
data class VoiceItem(
    val name: String,
    val displayName: String,
)

/**
 * UiState for the settings screen.
 */
data class SettingsUiState(
    val temperature: Float = 1.0f,
    val selectedVoiceName: String? = null,
    val availableVoices: List<VoiceItem> = emptyList(),
)
