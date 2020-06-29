package com.vlaksuga.rounding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        auth = Firebase.auth
        logInSubmit_button.setOnClickListener {
            auth.signInWithEmailAndPassword(logInEmail_editText.text.toString(), logInPassword_editText.text.toString()).addOnCompleteListener(this) {task ->
                if(task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "없어", Toast.LENGTH_SHORT).show()
                }
            }

        }

        toSignUp_button.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        toFindPassword_button.setOnClickListener {
            startActivity(Intent(this, FindPasswordActivity::class.java))
        }
    }

}
