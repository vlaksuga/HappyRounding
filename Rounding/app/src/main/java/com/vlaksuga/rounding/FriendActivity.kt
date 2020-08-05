package com.vlaksuga.rounding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.vlaksuga.rounding.adapters.FriendListAdapter
import com.vlaksuga.rounding.model.User
import kotlinx.android.synthetic.main.activity_friend.*

class FriendActivity : AppCompatActivity() {

    companion object {
        const val TAG = "FriendActivity"
    }

    private lateinit var friendList: List<User>
    private lateinit var friendListAdapter: FriendListAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var userEmail: String
    private val db = FirebaseFirestore.getInstance()
    private var allowList = arrayListOf<String>()

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
                if (error != null) {

                }
                val myFriends = arrayListOf<User>()
                for (document in value!!) {
                    myFriends.add(document.toObject(User::class.java))
                }
                Log.d(TAG, "Friends Added: $myFriends")
                friendList = myFriends
                val friendRecyclerView: RecyclerView = findViewById(R.id.friendList_recyclerView)
                friendListAdapter = FriendListAdapter(this, friendList)
                friendRecyclerView.adapter = friendListAdapter
                friendRecyclerView.layoutManager = LinearLayoutManager(this)
                friendRecyclerView.setHasFixedSize(true)
                if (friendList.isEmpty()) {
                    friendEmpty_textView.visibility = View.VISIBLE
                }
                friendListAdapter.setOnItemClickListener(object :
                    FriendListAdapter.OnItemClickListener {
                    override fun onItemClick(user: User) {
                        Log.d(TAG, "onItemClick: invoke")
                        val builder = AlertDialog.Builder(friendListAdapter.mContext)
                        builder.setMessage("친구 목록에서 삭제할까요?")
                        builder.setPositiveButton("확인") { dialog, which ->
                            db.collection("users")
                                .whereEqualTo("userEmail", user.userEmail)
                                .get()
                                .addOnSuccessListener {
                                    val documentPath = it.documents[0].id
                                    Log.d(TAG, "onItemClick: documentPath => $documentPath")
                                    allowList =
                                        it.documents[0].get("userAllowFriendList") as ArrayList<String>
                                    allowList.remove(userEmail)
                                    Log.d(TAG, "onItemClick: allowList => $allowList")
                                    updateAllowFriendList(documentPath)
                                }
                        }
                        builder.setNegativeButton("취소") { dialog, which -> dialog.dismiss() }
                            .show()
                    }
                })
            }

        add_friend_fab.setOnClickListener {
            startActivity(Intent(this, AddFriendActivity::class.java))
        }

        friendActivity_close_imageView.setOnClickListener {
            super.onBackPressed()
        }

    }

    private fun updateAllowFriendList(documentPath: String) {
        Log.d(TAG, "updateAllowFriendList: invoke")
        db.document("users/$documentPath")
            .update("userAllowFriendList", allowList)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "친구가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}