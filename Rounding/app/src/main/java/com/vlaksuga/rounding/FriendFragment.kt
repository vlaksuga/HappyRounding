package com.vlaksuga.rounding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.vlaksuga.rounding.adapters.FriendListAdapter
import com.vlaksuga.rounding.data.User

class FriendFragment : Fragment() {

    companion object {
        const val TAG = "FriendFragment"
    }

    private lateinit var friendList : List<User>
    private val db = FirebaseFirestore.getInstance()
    private var userId : String = ""
    private var userDocumentPath : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView : View? = inflater.inflate(R.layout.fragment_friend, container, false)
        userId = "OPPABANANA"
        userDocumentPath = "erFfmKP7EErJ24smorRd"
        db.collection("users")
            .whereArrayContains("userAllowFriendList", userId)
            .addSnapshotListener { value, error ->
                if(error != null) {
                    Log.w(TAG, "Listen Failed!", error)
                }
                val myFriends = arrayListOf<User>()
                val document : QuerySnapshot? = value
                for(snapshot in document!!) {
                    myFriends.add(snapshot.toObject(User::class.java))
                }
                Log.d(TAG, "Friends Added: $myFriends")
                friendList = myFriends
                val friendRecyclerView : RecyclerView = rootView!!.findViewById(R.id.friendRecyclerView)
                friendRecyclerView.adapter =
                    FriendListAdapter(activity!!, friendList)
                friendRecyclerView.layoutManager = LinearLayoutManager(activity!!)
                friendRecyclerView.setHasFixedSize(true)
                Log.d(TAG, "countFriends => ${friendList.size}")
            }
        val friendAddFab : FloatingActionButton = rootView!!.findViewById(R.id.friend_add_fab)
        friendAddFab.setOnClickListener {
            startActivity(Intent(activity!!, AddFriendActivity::class.java))
        }
        return rootView
    }
}
