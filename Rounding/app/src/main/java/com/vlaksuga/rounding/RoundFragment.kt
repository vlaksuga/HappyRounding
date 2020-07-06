package com.vlaksuga.rounding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vlaksuga.rounding.adapters.RoundListAdapter

class RoundFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView : View? = inflater.inflate(R.layout.fragment_round, container, false)
        val roundRecyclerView : RecyclerView = rootView!!.findViewById(R.id.roundRecyclerView)
        roundRecyclerView.adapter =
            RoundListAdapter(activity!!)
        roundRecyclerView.layoutManager = LinearLayoutManager(activity!!)
        roundRecyclerView.setHasFixedSize(true)
        return rootView
    }
}
