package com.vlaksuga.rounding

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class AppSharedPreferences(context : Context) {
    private val preferences : SharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, 0)
    var userUUID : String?
        get() = preferences.getString(PREFS_KEY_USER_UUID, "")
        set(uuid) {
            preferences.edit().putString(PREFS_KEY_USER_UUID, uuid).apply()
        }

    var userId : String?
        get() = preferences.getString(PREFS_KEY_USER_ID, "")
        set(id) {
            preferences.edit().putString(PREFS_KEY_USER_ID, id).apply()
        }

    var userPassword : String?
        get() = preferences.getString(PREFS_KEY_USER_PASSWORD, "")
        set(password) {
            preferences.edit().putString(PREFS_KEY_USER_PASSWORD, password).apply()
        }

    var userNickname : String?
        get() = preferences.getString(PREFS_KEY_USER_NICKNAME, "")
        set(nickname) {
            preferences.edit().putString(PREFS_KEY_USER_NICKNAME, nickname).apply()
        }

    var userEmail : String?
        get() = preferences.getString(PREFS_KEY_USER_EMAIL, "")
        set(email) {
            preferences.edit().putString(PREFS_KEY_USER_EMAIL, email).apply()
        }

    var userTeeType : String?
        get() = preferences.getString(PREFS_KEY_USER_TEE_TYPE, "")
        set(type) {
            preferences.edit().putString(PREFS_KEY_USER_TEE_TYPE, type).apply()
        }

    var roundIsOpen : Boolean
        get() = preferences.getBoolean(PREFS_KEY_ROUND_IS_OPEN, false)
        set(state) {
            preferences.edit().putBoolean(PREFS_KEY_ROUND_IS_OPEN, state).apply()
        }


    companion object {
        const val PREFS_FILE_NAME = "prefs"
        const val PREFS_KEY_USER_UUID = "userUUID"
        const val PREFS_KEY_USER_ID = "userId"
        const val PREFS_KEY_USER_NICKNAME = "userNickname"
        const val PREFS_KEY_USER_EMAIL = "userEmail"
        const val PREFS_KEY_USER_TEE_TYPE = "userTeeType"
        const val PREFS_KEY_USER_PASSWORD = "userPassword"
        const val PREFS_KEY_ROUND_IS_OPEN = "roundIsOpen"
    }
}