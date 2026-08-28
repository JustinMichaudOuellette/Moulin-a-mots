package ca.justinmo.word.generator.app.features.settings

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.justinmo.word.generator.app.R
import ca.justinmo.word.generator.app.ui.MainTopAppBar
import ca.justinmo.word.generator.app.ui.SettingsUiState
import ca.justinmo.word.generator.app.main.WordGeneratorViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    openDrawer: () -> Unit,
    viewModel: WordGeneratorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.settingsUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Initialize TTS only once to get voices
    DisposableEffect(Unit) {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.let { viewModel.initialize(context) }
            }
        }
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { MainTopAppBar(R.string.settings_title, openDrawer) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.temperature_label),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.temperature_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Slider(
                value = uiState.temperature,
                onValueChange = viewModel::onTemperatureChanged,
                valueRange = 0.1f..2.0f,
                steps = 18,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = "Value: ${((uiState.temperature * 10).roundToInt() / 10.0)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.voice_selection_label),
                style = MaterialTheme.typography.titleMedium
            )
            
            var expanded by remember { mutableStateOf(false) }
            val selectedVoice = uiState.availableVoices.find { it.name == uiState.selectedVoiceName }
                ?: uiState.availableVoices.firstOrNull()

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
            ) {
                TextField(
                    value = selectedVoice?.displayName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth().padding(bottom = 8.dp),
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    ),
                    textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold),
                    shape = MaterialTheme.shapes.medium
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    uiState.availableVoices.forEach { voice ->
                        val isSelected = voice.name == uiState.selectedVoiceName
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = voice.displayName,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                viewModel.onVoiceSelected(voice.name)
                                viewModel.onPreviewVoice(voice.name, voice.displayName)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
