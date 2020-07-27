package com.vlaksuga.rounding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vlaksuga.rounding.adapters.StatsListAdapter
import com.vlaksuga.rounding.constructors.Stats

class StatisticsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_statistics, container, false)
        val statsList = arrayListOf<Stats>(
            Stats("최근 라운드", "strokes",67),
            Stats("9라운드 평균", "strokes",85),
            Stats("베스트 스코어", "strokes",69),
            Stats("라운드수", "rounds",85),
            Stats("클럽수", "clubs",15),
            Stats("코스수", "courses",37)
        )

        val statsRecyclerView : RecyclerView = rootView!!.findViewById(R.id.statsRecyclerView)
        statsRecyclerView.adapter = StatsListAdapter(activity!!, statsList)
        statsRecyclerView.layoutManager = GridLayoutManager(activity!!, 3)
        statsRecyclerView.setHasFixedSize(true)

        return rootView
    }
}