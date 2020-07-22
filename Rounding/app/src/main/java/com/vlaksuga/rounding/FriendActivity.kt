package com.vlaksuga.rounding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class FriendActivity : AppCompatActivity() {

    companion object {
        const val TAG = "FriendActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)
        supportActionBar!!.title = "친구"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}