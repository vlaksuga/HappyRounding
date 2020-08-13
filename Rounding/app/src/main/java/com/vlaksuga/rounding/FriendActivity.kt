package com.vlaksuga.rounding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
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
    private var findResultUserNickname = ""
    private var findResultUserEmail = ""
    private var targetUserDocumentPath = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var userEmail: String
    private val db = FirebaseFirestore.getInstance()
    private var allowList = arrayListOf<String>()
    private var lastFriendList = arrayListOf<String>()
    private var isAbleToAdd = false

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
                    Log.d(TAG, "onCreate: it has null")
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
                        Log.d(TAG, "onItemClick: invoke user -> $user")
                        val detailIntent = Intent(this@FriendActivity, FriendDetailActivity::class.java)
                        detailIntent.putExtra(FriendDetailActivity.FRIEND_EMAIL, user.userEmail)
                        detailIntent.putExtra(FriendDetailActivity.FRIEND_NICKNAME, user.userNickname)
                        startActivity(detailIntent)
                    }
                })
            }

        // FIND USER //
        findUser_EditText.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                friendAdd_cardView.visibility = View.GONE
                db.collection("users")
                    .whereEqualTo("userPhone", findUser_EditText.text.toString())
                    .get()
                    .addOnSuccessListener {
                        Log.d(TAG, "findUser: success")
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "findUser: fail")
                    }
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            friendAdd_cardView.visibility = View.VISIBLE
                            if(it.result!!.isEmpty) {
                                Log.d(TAG, "findUser : Result is empty ")
                                showResultIsEmpty()
                            } else {
                                Log.d(TAG, "findUser : Result found ")
                                targetUserDocumentPath = it.result!!.documents[0].id
                                findResultUserNickname = it.result!!.documents[0].get("userNickname") as String
                                findResultUserEmail = it.result!!.documents[0].get("userEmail") as String
                                Log.d(TAG, "RESULT : $findResultUserNickname, $findResultUserEmail")
                                if(userEmail != findResultUserEmail) {
                                    checkFriendState()
                                } else {
                                    searchMyself()
                                }
                            }
                        }
                    }
                return@OnKeyListener true
            }
            false
        })

        add_friend_fab.setOnClickListener {
            startAddFriend()
        }


        // CARD VIEW //
        friendAdd_cardView.setOnClickListener {
            if(!isAbleToAdd) {
                it.visibility = View.GONE
                return@setOnClickListener
            }
            Log.d(TAG, "onCreate: addFriend_button clicked ")
            db.collection("users")
                .whereEqualTo("userEmail", findResultUserEmail)
                .get()
                .addOnSuccessListener {
                    Log.d(TAG, "get friendList: success")
                }
                .addOnFailureListener {
                    Log.d(TAG, "get friendList: fail")
                }
                .addOnCompleteListener {task ->
                    if(task.isSuccessful) {
                        if(task.result!!.documents[0].get("userAllowFriendList") == null) {
                            lastFriendList.add(userEmail)
                            Log.d(TAG, "get friendList : success, targetUserDocumentPath => $targetUserDocumentPath, lastFriendList => $lastFriendList")
                            updateNewFriendList()
                        } else {
                            @Suppress("UNCHECKED_CAST")
                            lastFriendList = task.result!!.documents[0].get("userAllowFriendList") as ArrayList<String>
                            lastFriendList.add(userEmail)
                            Log.d(TAG, "get friendList : success, targetUserDocumentPath => $targetUserDocumentPath, lastFriendList => $lastFriendList")
                            updateNewFriendList()
                        }
                    }
                }
        }

        friendActivity_close_imageView.setOnClickListener {
            onBackPressed()
        }

    }

    private fun endAddFriend() {
        friendList_recyclerView.visibility = View.VISIBLE
        addFriend_layout.visibility = View.GONE
        add_friend_fab.visibility = View.VISIBLE
    }

    private fun startAddFriend() {
        findUser_EditText.text = null
        friendAdd_cardView.visibility = View.GONE
        friendList_recyclerView.visibility = View.GONE
        addFriend_layout.visibility = View.VISIBLE
        add_friend_fab.visibility = View.GONE
    }

    private fun searchMyself() {
        isAbleToAdd = false
        friendAdd_cardView.visibility = View.VISIBLE
        searchResult_imageView.setImageResource(R.drawable.ic_case_bogey)
        notFoundTitle_textView.text = "나는 나의 가장 소중한 친구입니다"
        searchResult_textView.text = findResultUserNickname
        addFriendIcon_imageView.setImageResource(R.drawable.ic_close)
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


    private fun showResultExist(ableToAdd : Boolean) {
        searchResult_textView.text = findResultUserNickname
        searchResult_imageView.setImageResource(R.drawable.ic_account_circle)
        if(ableToAdd) {
            isAbleToAdd = true
            notFoundTitle_textView.text = "친구로 추가할 수 있습니다."
            addFriendIcon_imageView.setImageResource(R.drawable.ic_add)
        } else {
            isAbleToAdd = false
            notFoundTitle_textView.text = "이미 등록된 친구입니다."
            addFriendIcon_imageView.setImageResource(R.drawable.ic_close)
        }
    }

    private fun checkFriendState() {
        db.document("users/$targetUserDocumentPath")
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful && it.result!!.get("userAllowFriendList") != null) {
                    if((it.result!!.get("userAllowFriendList") as ArrayList<String>).contains(userEmail)) {
                        showResultExist(false)
                    } else {
                        showResultExist(true)
                    }
                } else {
                    showResultExist(true)
                }
            }

    }

    private fun showResultIsEmpty() {
        friendAdd_cardView.visibility = View.VISIBLE
        searchResult_imageView.setImageResource(R.drawable.ic_case_bogey2)
        notFoundTitle_textView.text = "사용자를 찾을 수 없습니다."
        searchResult_textView.text = ""
        addFriendIcon_imageView.setImageResource(R.drawable.ic_close)
        isAbleToAdd = false
    }

    private fun updateNewFriendList() {
        db.document("users/$targetUserDocumentPath")
            .update("userAllowFriendList", lastFriendList)
            .addOnSuccessListener {
                Log.d(TAG, "updateNewFriendList: success ")
            }
            .addOnFailureListener {
                Log.d(TAG, "updateNewFriendList: fail ")
            }
            .addOnCompleteListener {
                Log.d(TAG, "updateNewFriendList: complete! ")
                Toast.makeText(this, "친구가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                friendListAdapter.notifyDataSetChanged()
                addFriend_layout.visibility = View.GONE
                add_friend_fab.visibility = View.VISIBLE
                friendList_recyclerView.visibility = View.VISIBLE
            }
    }

    override fun onBackPressed() {
        if(addFriend_layout.visibility == View.VISIBLE) {
            endAddFriend()
        } else {
            super.onBackPressed()
        }
    }
}