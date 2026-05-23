package com.jokerdev.katai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.jokerdev.katai.data.local.SettingsStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsUiState(
    val selectedModel: String = "llama-3.1-8b-instant",
    val darkThemeMode: String = "system",   // "system", "dark", "light"
    val typingAnimation: Boolean = true,
    val responseLanguage: String = "English"
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        val ctx = getApplication<Application>()
        _uiState.value = SettingsUiState(
            selectedModel = SettingsStorage.getSelectedModel(ctx),
            darkThemeMode = SettingsStorage.getDarkThemeMode(ctx),
            typingAnimation = SettingsStorage.getTypingAnimation(ctx),
            responseLanguage = SettingsStorage.getResponseLanguage(ctx)
        )
    }

    fun setModel(model: String) {
        SettingsStorage.setSelectedModel(getApplication(), model)
        _uiState.value = _uiState.value.copy(selectedModel = model)
    }

    fun setDarkThemeMode(mode: String) {
        SettingsStorage.setDarkThemeMode(getApplication(), mode)
        _uiState.value = _uiState.value.copy(darkThemeMode = mode)
    }

    fun setTypingAnimation(enabled: Boolean) {
        SettingsStorage.setTypingAnimation(getApplication(), enabled)
        _uiState.value = _uiState.value.copy(typingAnimation = enabled)
    }

    fun setResponseLanguage(language: String) {
        SettingsStorage.setResponseLanguage(getApplication(), language)
        _uiState.value = _uiState.value.copy(responseLanguage = language)
    }
}
