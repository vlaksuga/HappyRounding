package com.vlaksuga.rounding

import android.content.Context
import android.content.SharedPreferences

class AppSharedPreferences(context : Context) {
    private val preferences : SharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, 0)
    var introPassed : Boolean
        get() = preferences.getBoolean(PREF_KEY_INTRO_PASSED, false)
        set(value) {
            preferences.edit().putBoolean(PREF_KEY_INTRO_PASSED, value).apply()
        }



    companion object {
        const val PREFS_FILE_NAME = "prefs"
        const val PREF_KEY_INTRO_PASSED = "intro"
    }
}