package com.vlaksuga.rounding

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_friend.*

class AddFriendActivity : AppCompatActivity() {

    companion object {
        const val TAG = "AddFriendActivity"
    }

    private val db = FirebaseFirestore.getInstance()
    private var findResultUserNickname = ""
    private var findResultUserId = ""
    private var targetUserDocumentPath = ""
    private var userId = ""
    private var lastFriendList = arrayListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        userId = "OPPABANANA"

        findUser_EditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                notFoundTitle_textView.visibility = View.GONE
                searchResult_textView.visibility = View.GONE
                searchResult_imageView.visibility = View.GONE
                addFriend_button.visibility = View.GONE
                db.collection("users")
                    .whereEqualTo("userId", findUser_EditText.text.toString())
                    .get()
                    .addOnSuccessListener {
                        Log.d(TAG, "findUser: success")
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "findUser: fail")
                    }
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            if(it.result!!.isEmpty) {
                                Log.d(TAG, "finduser : Result is empty ")
                                showResultIsEmpty()
                            } else {
                                Log.d(TAG, "finduser : Result found ")
                                targetUserDocumentPath = it.result!!.documents[0].id
                                findResultUserNickname = it.result!!.documents[0].get("userNickname") as String
                                findResultUserId = it.result!!.documents[0].get("userId") as String
                                Log.d(TAG, "RESULT : $findResultUserNickname, $findResultUserId $findResultUserId")
                                if(userId != findResultUserId) {
                                    checkFriendState()
                                } else {
                                    Toast.makeText(this, "자신은 검색 대상에 포함되지 않습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                return@OnKeyListener true
            }
            false
        })

        addFriend_button.setOnClickListener {
            Log.d(TAG, "onCreate: addFriend_button clicked ")
            db.collection("users")
                .whereEqualTo("userId", findResultUserId)
                .get()
                .addOnSuccessListener {
                    Log.d(TAG, "get friendList: success")
                }
                .addOnFailureListener {
                    Log.d(TAG, "get friendList: fail")
                }
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        if(it.result!!.documents[0].get("userAllowFriendList") == null) {
                            lastFriendList.add(userId)
                            Log.d(TAG, "get friendList : success, targetUserDocumentPath => $targetUserDocumentPath, lastFriendList => $lastFriendList")
                            updateNewFriendList()
                        } else {
                            lastFriendList = it.result!!.documents[0].get("userAllowFriendList") as ArrayList<String>
                            lastFriendList.add(userId)
                            Log.d(TAG, "get friendList : success, targetUserDocumentPath => $targetUserDocumentPath, lastFriendList => $lastFriendList")
                            updateNewFriendList()
                        }
                    }
                }
        }
    }

    private fun showResultExist(ableToAdd : Boolean) {
        addFriend_button.isClickable = ableToAdd
        searchResult_textView.text = findResultUserNickname
        searchResult_textView.visibility = View.VISIBLE
        searchResult_imageView.visibility = View.VISIBLE
        notFoundTitle_textView.visibility = View.GONE
        addFriend_button.visibility = View.VISIBLE
        if(ableToAdd) {
            addFriend_button.text = "친구 추가"
        } else {
            addFriend_button.text = "이미 친구"
        }
    }

    private fun checkFriendState() {
        db.document("users/$targetUserDocumentPath")
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful && it.result!!.get("userAllowFriendList") != null) {
                    if((it.result!!.get("userAllowFriendList") as ArrayList<String>).contains(userId)) {
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
        notFoundTitle_textView.visibility = View.VISIBLE
        searchResult_textView.visibility = View.GONE
        searchResult_imageView.visibility = View.GONE
        addFriend_button.visibility = View.GONE
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
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
    }
}