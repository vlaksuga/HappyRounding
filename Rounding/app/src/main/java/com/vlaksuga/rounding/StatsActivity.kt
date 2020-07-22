package com.vlaksuga.rounding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class StatsActivity : AppCompatActivity() {

    companion object {
        const val TAG = "StatsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        supportActionBar!!.title = "통계"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}