package com.vlaksuga.rounding

import android.content.Context
import android.content.SharedPreferences

class AppSharedPreferences(context : Context) {
    private val preferences : SharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, 0)

    companion object {
        const val PREFS_FILE_NAME = "prefs"
    }
}