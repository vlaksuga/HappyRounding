package com.vlaksuga.rounding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vlaksuga.rounding.adapters.FriendListAdapter

class FriendFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView : View? = inflater.inflate(R.layout.fragment_friend, container, false)
        val friendRecyclerView : RecyclerView = rootView!!.findViewById(R.id.friendRecyclerView)
        friendRecyclerView.adapter =
            FriendListAdapter(activity!!)
        friendRecyclerView.layoutManager = LinearLayoutManager(activity!!)
        friendRecyclerView.setHasFixedSize(true)
        return rootView
    }
}
