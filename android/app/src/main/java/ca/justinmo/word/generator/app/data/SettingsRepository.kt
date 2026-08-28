package ca.justinmo.word.generator.app.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _temperature = MutableStateFlow(sharedPreferences.getFloat(KEY_TEMPERATURE, 1.0f))
    val temperature: StateFlow<Float> = _temperature.asStateFlow()

    private val _selectedVoiceName = MutableStateFlow(sharedPreferences.getString(KEY_VOICE_NAME, ""))
    val selectedVoiceName: StateFlow<String?> = _selectedVoiceName.asStateFlow()

    fun setTemperature(value: Float) {
        sharedPreferences.edit().putFloat(KEY_TEMPERATURE, value).apply()
        _temperature.value = value
    }

    fun setSelectedVoiceName(name: String?) {
        sharedPreferences.edit().putString(KEY_VOICE_NAME, name).apply()
        _selectedVoiceName.value = name
    }

    companion object {
        private const val KEY_TEMPERATURE = "temperature"
        private const val KEY_VOICE_NAME = "voice_name"
    }
}
