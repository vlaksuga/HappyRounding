package com.vlaksuga.rounding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth = Firebase.auth

        val db = FirebaseFirestore.getInstance()



        signUpSubmit_button.setOnClickListener {
            auth.createUserWithEmailAndPassword(
                signUpEmail_editText.text.toString(),
                signUpPassword_editText.text.toString()
            ).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val emailData = signUpEmail_editText.text.toString()
                    val nicknameData = signUpNickName_editText.text.toString()
                    val passwordData = signUpPassword_editText.text.toString()
                    val tagId : String = emailData
                    val user = hashMapOf(
                        "id" to emailData,
                        "email" to emailData,
                        "nickname" to nicknameData,
                        "password" to passwordData,
                        "tagId" to tagId,
                        "teeType" to "normal"
                    )
                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, user["nickname"], Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { _ ->
                            Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show()
                        }
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
