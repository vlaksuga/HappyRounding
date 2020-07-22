package com.vlaksuga.rounding

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
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
    private lateinit var userPassword: String
    private lateinit var userNickname: String
    private lateinit var userId: String
    private lateinit var userTeeType: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        userSubmit_button.setOnClickListener {
            val userEmail = userEmail_textView.text
            val userNickname = userNickName_editText.text.toString()
            val userPhoneNumber = userPhoneNumber_editText.text.toString()
            val user = hashMapOf(
                "userEmail" to userEmail,
                "userNickname" to userNickname,
                "userPhoneNumber" to userPhoneNumber
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
                        mainIntent.putExtra(KEY_USER_EMAIL, userEmail)
                        mainIntent.putExtra(KEY_USER_NICKNAME, userNickname)
                        mainIntent.putExtra(KEY_USER_PHONE, userPhoneNumber)
                        startActivityForResult(mainIntent, Activity.RESULT_FIRST_USER)
                    }
                }
        }
    }
}
