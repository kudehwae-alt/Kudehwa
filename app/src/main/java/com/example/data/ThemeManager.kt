package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeStyle(val displayName: String) {
    DARK_NAVY("Dark Navy"),
    AMOLED_BLACK("AMOLED Pitch Black"),
    EMERALD_GOLD("Emerald Gold"),
    CYBERPUNK_NEON("Cyberpunk Neon"),
    LIGHT("Classic Light")
}

object ThemeManager {
    private val _currentTheme = MutableStateFlow(AppThemeStyle.DARK_NAVY)
    val currentTheme: StateFlow<AppThemeStyle> = _currentTheme.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun setTheme(theme: AppThemeStyle) {
        _currentTheme.value = theme
        _isDarkTheme.value = theme != AppThemeStyle.LIGHT
    }

    fun toggleTheme() {
        if (_currentTheme.value == AppThemeStyle.LIGHT) {
            setTheme(AppThemeStyle.DARK_NAVY)
        } else {
            setTheme(AppThemeStyle.LIGHT)
        }
    }

    fun cycleTheme() {
        val next = when (_currentTheme.value) {
            AppThemeStyle.DARK_NAVY -> AppThemeStyle.AMOLED_BLACK
            AppThemeStyle.AMOLED_BLACK -> AppThemeStyle.EMERALD_GOLD
            AppThemeStyle.EMERALD_GOLD -> AppThemeStyle.CYBERPUNK_NEON
            AppThemeStyle.CYBERPUNK_NEON -> AppThemeStyle.LIGHT
            AppThemeStyle.LIGHT -> AppThemeStyle.DARK_NAVY
        }
        setTheme(next)
    }
}
