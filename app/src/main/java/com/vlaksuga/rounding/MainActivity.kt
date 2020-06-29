package com.vlaksuga.rounding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var roundListAdapter: RoundListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        roundListAdapter = RoundListAdapter(this)
        roundRecyclerView.adapter = roundListAdapter
        roundRecyclerView.layoutManager = LinearLayoutManager(this)
        roundRecyclerView.setHasFixedSize(true)
    }
}
