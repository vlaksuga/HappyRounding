package com.vlaksuga.rounding

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SignUpActivity"
    }

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userEmail : String
    private lateinit var userPassword : String
    private lateinit var userNickname: String
    private lateinit var userId : String
    private lateinit var userTeeType : String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = Firebase.auth


        signUpSubmit_button.setOnClickListener {
            auth.createUserWithEmailAndPassword(
                signUpEmail_editText.text.toString(),
                signUpPassword_editText.text.toString()
            ).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    userEmail = signUpEmail_editText.text.toString()
                    userNickname = signUpNickname_editText.text.toString()
                    userPassword = signUpPassword_editText.text.toString()
                    userId = signUpId_editText.text.toString()
                    val user = hashMapOf(
                        "userId" to userId,
                        "userEmail" to userEmail,
                        "userNickname" to userNickname,
                        "userPassword" to userPassword,
                        "userId" to userId,
                        "userTeeType" to "normal"
                    )
                    Log.d(TAG, "user: $user")
                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, user["nickname"], Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { _ ->
                            Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show()
                        }
                    // TODO :
                    Toast.makeText(this, "성공", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show()
                }
            }
        }

        toLogInActivity_button.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        }
    }
}
