package com.vlaksuga.rounding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.vlaksuga.rounding.adapters.FriendListAdapter
import com.vlaksuga.rounding.data.User
import kotlinx.android.synthetic.main.activity_friend.*

class FriendActivity : AppCompatActivity() {

    companion object {
        const val TAG = "FriendActivity"
    }

    private lateinit var friendList : List<User>
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var userEmail : String
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)

        auth = Firebase.auth
        user = auth.currentUser!!
        userEmail = user.email!!


        val toolbar = findViewById<Toolbar>(R.id.friend_toolbar)
        setSupportActionBar(toolbar)

        db.collection("users")
            .whereArrayContains("userAllowFriendList", userEmail)
            .addSnapshotListener { value, error ->
                if(error != null) {

                }
                val myFriends = arrayListOf<com.vlaksuga.rounding.data.User>()
                for(document in value!!) {
                    myFriends.add(document.toObject(User::class.java))
                }
                Log.d(TAG, "Friends Added: $myFriends")
                friendList = myFriends
                val friendRecyclerView : RecyclerView = findViewById(R.id.friendList_recyclerView)
                friendRecyclerView.adapter = FriendListAdapter(this, friendList)
                friendRecyclerView.layoutManager = LinearLayoutManager(this)
                friendRecyclerView.setHasFixedSize(true)
            }


        add_friend_fab.setOnClickListener {
            startActivity(Intent(this, AddFriendActivity::class.java))
        }

        friendActivity_close_imageView.setOnClickListener {
            super.onBackPressed()
        }

    }
}