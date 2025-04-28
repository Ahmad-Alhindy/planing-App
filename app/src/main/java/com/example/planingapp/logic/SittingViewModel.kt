package com.example.planingapp.logic

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.core.content.edit

class SettingsManger(sharedPreferences: SharedPreferences) : ViewModel() {


    init {
        initPreferences(sharedPreferences)
    }



    companion object {
        private lateinit var preferencesInitialized: SharedPreferences

        const val DARK_MODE_KEY = "dark_mode"

        private val isDarkModeFlow = MutableStateFlow(false)

        val isDarkMode: StateFlow<Boolean> get() = isDarkModeFlow

        fun initPreferences(sharedPrefs: SharedPreferences) {
            if (!::preferencesInitialized.isInitialized) {   // <== HERE
                preferencesInitialized = sharedPrefs
                val savedValue = preferencesInitialized.getBoolean(DARK_MODE_KEY, false)
                isDarkModeFlow.value = savedValue
            }
        }
        fun toggleTheme() {
            val newValue = !isDarkModeFlow.value
            saveDarkMode(newValue)
        }

        private fun saveDarkMode(newValue: Boolean) {
            isDarkModeFlow.value = newValue
            preferencesInitialized.edit() { putBoolean(DARK_MODE_KEY, newValue) }
        }
    }
}
