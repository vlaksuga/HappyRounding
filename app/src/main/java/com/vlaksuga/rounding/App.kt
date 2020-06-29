package com.vlaksuga.rounding

import android.app.Application
import android.os.Bundle

class App : Application () {

    companion object {
        lateinit var prefs : AppSharedPreferences
    }

    override fun onCreate() {
        prefs = AppSharedPreferences(applicationContext)
        super.onCreate()
    }
}
