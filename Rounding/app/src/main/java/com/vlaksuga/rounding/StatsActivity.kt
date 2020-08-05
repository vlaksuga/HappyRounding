package com.vlaksuga.rounding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar

class StatsActivity : AppCompatActivity() {

    companion object {
        const val TAG = "StatsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val toolbar = findViewById<Toolbar>(R.id.stats_toolbar)
        setSupportActionBar(toolbar)
    }
}