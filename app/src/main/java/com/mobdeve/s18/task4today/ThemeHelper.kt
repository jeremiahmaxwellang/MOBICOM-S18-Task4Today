// ThemeHelper.kt
package com.mobdeve.s18.task4today

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeHelper {
    private const val PREFS_NAME = "ThemePreferences"
    private const val DARK_MODE_KEY = "DarkModeEnabled"

    fun isDarkModeEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(DARK_MODE_KEY, false)
    }

    fun toggleTheme(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isDarkMode = !prefs.getBoolean(DARK_MODE_KEY, false)

        prefs.edit().putBoolean(DARK_MODE_KEY, isDarkMode).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun applySavedTheme(context: Context) {
        val isDarkMode = isDarkModeEnabled(context)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
