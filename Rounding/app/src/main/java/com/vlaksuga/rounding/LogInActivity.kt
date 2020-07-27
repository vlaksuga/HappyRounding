package com.vlaksuga.rounding

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity() {

    companion object {
        const val TAG = "LogInActivity"
        const val RC_SIGN_IN = 1
    }

    private lateinit var providers : List<List<AuthUI.IdpConfig>>
    private lateinit var auth : FirebaseAuth
    private var userEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        providers = listOf(
            listOf(AuthUI.IdpConfig.GoogleBuilder().build()),
            listOf(AuthUI.IdpConfig.EmailBuilder().build())
        )
        google_login_button.setOnClickListener {
            showSignInOptions(0)
        }
        email_login_button.setOnClickListener {
            showSignInOptions(1)
        }
    }

    private fun showSignInOptions(providerIndex : Int) {
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers[providerIndex])
            .build(), RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK) {
                if(response!!.isNewUser){
                    startActivity(Intent(this, SignUpActivity::class.java))
                } else {
                    startActivity(Intent(this, SignUpActivity::class.java))
                }
            } else {
                Toast.makeText(this, ""+response!!.error!!.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        if(auth.currentUser != null) {
            Log.d(TAG, "onStart: currentUser is")
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
