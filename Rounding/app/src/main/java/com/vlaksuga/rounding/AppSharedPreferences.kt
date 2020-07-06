package com.vlaksuga.rounding

import android.content.Context
import android.content.SharedPreferences

class AppSharedPreferences(context : Context) {
    private val preferences : SharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, 0)

    var userEmail : String?
        get() = preferences.getString(PREFS_KEY_USER_EMAIL, "")
        set(email) {
            preferences.edit().putString(PREFS_KEY_USER_EMAIL, email).apply()
        }

    var userPassword : String?
        get() = preferences.getString(PREFS_KEY_USER_PASSWORD, "")
        set(password) {
            preferences.edit().putString(PREFS_KEY_USER_PASSWORD, password).apply()
        }

    companion object {
        const val PREFS_FILE_NAME = "prefs"
        const val PREFS_KEY_USER_EMAIL = "userEmail"
        const val PREFS_KEY_USER_PASSWORD = "userPassword"
    }
}