package com.vlaksuga.rounding

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SignUpActivity"
        const val KEY_USER_EMAIL = "com.vlaksuga.rounding.USER_EMAIL"
        const val KEY_USER_NICKNAME = "com.vlaksuga.rounding.USER_NICKNAME"
        const val KEY_USER_PHONE = "com.vlaksuga.rounding.USER_PHONE"
    }

    private val db = FirebaseFirestore.getInstance()
    private lateinit var userEmail: String
    private lateinit var userNickname: String
    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = Firebase.auth
        userEmail = auth.currentUser!!.email!!
        Log.d(TAG, "onCreate: currentUserEmail => $userEmail")

        userEmail_textView.text = userEmail


        // TODO : 유저 전화번호 감지해서 가져오기


        userSubmit_button.setOnClickListener {
            val userEmail = userEmail_textView.text
            val userNickname = userNickName_editText.text.toString()
            val user = hashMapOf(
                "userEmail" to userEmail,
                "userNickname" to userNickname
            )
            Log.d(TAG, "user: $user")
            db.collection("users")
                .add(user)
                .addOnSuccessListener {
                    Log.d(TAG, "userSubmit_button: success ")
                }
                .addOnFailureListener { _ ->
                    Log.d(TAG, "userSubmit_button: fail ")
                }
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        val mainIntent = Intent(this, MainActivity::class.java)
                        startActivityForResult(mainIntent, Activity.RESULT_FIRST_USER)
                    }
                }
        }
    }
}
