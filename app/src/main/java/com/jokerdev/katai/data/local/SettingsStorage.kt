package com.jokerdev.katai.data.local

import android.content.Context
import android.content.SharedPreferences

object SettingsStorage {

    private const val PREFS_NAME = "katai_settings"
    private const val KEY_MODEL = "selected_model"
    private const val KEY_DARK_THEME = "dark_theme_mode"
    private const val KEY_TYPING_ANIMATION = "typing_animation"
    private const val KEY_RESPONSE_LANGUAGE = "response_language"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getSelectedModel(context: Context): String =
        prefs(context).getString(KEY_MODEL, "llama-3.1-8b-instant") ?: "llama-3.1-8b-instant"

    fun setSelectedModel(context: Context, model: String) =
        prefs(context).edit().putString(KEY_MODEL, model).apply()

    fun getDarkThemeMode(context: Context): String =
        prefs(context).getString(KEY_DARK_THEME, "system") ?: "system"

    fun setDarkThemeMode(context: Context, mode: String) =
        prefs(context).edit().putString(KEY_DARK_THEME, mode).apply()

    fun getTypingAnimation(context: Context): Boolean =
        prefs(context).getBoolean(KEY_TYPING_ANIMATION, true)

    fun setTypingAnimation(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_TYPING_ANIMATION, enabled).apply()

    fun getResponseLanguage(context: Context): String =
        prefs(context).getString(KEY_RESPONSE_LANGUAGE, "English") ?: "English"

    fun setResponseLanguage(context: Context, language: String) =
        prefs(context).edit().putString(KEY_RESPONSE_LANGUAGE, language).apply()
}
