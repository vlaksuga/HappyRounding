package com.vlaksuga.rounding

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vlaksuga.rounding.adapters.ClubListAdapter

class SetRoundClubFragment : Fragment() {

    companion object {
        const val TAG = "SetRoundClubFragment"
    }

    private lateinit var searchView : SearchView
    lateinit var clubListAdapter: ClubListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_set_round_club, container, false)

        // adapter //
        clubListAdapter = ClubListAdapter(activity!!)
        val clubRecyclerView : RecyclerView = rootView.findViewById(R.id.setRoundClub_recyclerView)
        clubRecyclerView.adapter = clubListAdapter
        clubRecyclerView.layoutManager = LinearLayoutManager(activity!!)
        clubRecyclerView.setHasFixedSize(true)

        // searchView //
        // TODO : 파이어베이스와 서치뷰 연결해서 구축하기
        val searchManager = activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = rootView.findViewById(R.id.setRoundClub_searchView)
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))
        searchView.maxWidth = Int.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                clubListAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                clubListAdapter.filter.filter(newText)
                return false
            }
        })
        return rootView
    }
}