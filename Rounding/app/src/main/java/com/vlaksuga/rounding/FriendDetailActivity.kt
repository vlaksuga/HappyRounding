package com.vlaksuga.rounding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.vlaksuga.rounding.model.ResultRound
import com.vlaksuga.rounding.model.User
import kotlinx.android.synthetic.main.activity_friend_detail.*

class FriendDetailActivity : AppCompatActivity() {

    companion object {
        const val TAG = "FriendDetailActivity"
        const val FRIEND_EMAIL = "com.vlaksuga.rounding.FRIEND_EMAIL"
        const val FRIEND_NICKNAME = "com.vlaksuga.rounding.FRIEND_NICKNAME"

    }

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var userEmail: String
    private lateinit var friendEmail : String
    private lateinit var friendNickname : String
    private lateinit var friendUser : User
    private var documentPath = ""
    private var userAllowFriendList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_detail)

        // USER AUTH //
        auth = Firebase.auth
        userEmail = auth.currentUser!!.email!!
        Log.d(MainActivity.TAG, "onCreate: userEmail => $userEmail")

        // FRIEND INFO //
        friendEmail = intent.getStringExtra(FRIEND_EMAIL)!!
        friendNickname = intent.getStringExtra(FRIEND_NICKNAME)!!

        friendNickName_textView.text = friendNickname
        friendEmail_textView.text = friendEmail

        db.collection("users")
            .whereEqualTo("userEmail", friendEmail)
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "onCreate: success")
            }
            .addOnFailureListener {
                Log.d(TAG, "onCreate: fail")
            }
            .addOnCompleteListener {
                if(it.isSuccessful && it.result!!.documents.isNotEmpty()) {
                    documentPath = it.result!!.documents[0].id
                    friendUser = it.result!!.documents[0].toObject(User::class.java)!!
                    setStats()
                    @Suppress("UNCHECKED_CAST")
                    userAllowFriendList = it.result!!.documents[0].get("userAllowFriendList") as ArrayList<String>
                    Log.d(TAG, "friendUser: $friendUser")
                } else {
                    Log.d(TAG, "onCreate: NO FRIEND")
                }
            }

        // REMOVE FRIEND //
        removeFriend_textView.setOnClickListener {
            removeFriend()
        }

        // BACK STACK //
        closeActivity_imageView.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun setStats() {
        Log.d(TAG, "setStats: invoke")
        db.collection("roundResults")
            .whereEqualTo("resultUserEmail", friendEmail)
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "setStats: success")
            }
            .addOnFailureListener {
                Log.d(TAG, "setStats: fail")
            }
            .addOnCompleteListener {
                Log.d(TAG, "setStats: complete")
                if(it.isSuccessful) {
                    val friendResult = arrayListOf<ResultRound>()
                    for(document in it.result!!) {
                        friendResult.add(document.toObject(ResultRound::class.java))
                        Log.d(TAG, "setStats: round added")
                    }
                    val totalScoreList = arrayListOf<Int>()
                    for(result in friendResult) {
                        if(result.resultCourseIdList.size == 2) {
                            totalScoreList.add(result.resultFirstScoreList.sum() + result.resultSecondScoreList.sum())
                        }
                    }
                    if (totalScoreList.size > 0) {
                        averageScore_textView.text = (totalScoreList.sum() / totalScoreList.size).toString()
                        friendHandicap_textView.text = ((totalScoreList.sum() / totalScoreList.size) - 72).toString()
                        totalScoreList.sort()
                        bestScore_textView.text = totalScoreList[0].toString()
                    } else {
                        averageScore_textView.text = "-"
                        friendHandicap_textView.text = "-"
                        bestScore_textView.text = "-"
                    }
                }
            }
    }

    private fun removeFriend() {
        userAllowFriendList.remove(userEmail)

        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("친구 목록에서 삭제할까요?")
            setPositiveButton("확인") { _, _ ->
                db.document("users/$documentPath")
                    .update("userAllowFriendList", userAllowFriendList)
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            Toast.makeText(this@FriendDetailActivity, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            finishAndRemoveTask()
                        }
                    }
            }
            setNegativeButton("취소") { dialog, _ ->  dialog.dismiss()}
            show()
        }
    }
}