package com.vlaksuga.rounding

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity() {

    companion object {
        const val TAG = "LogInActivity"
        const val RC_SIGN_IN = 1
    }

    lateinit var providers : List<List<AuthUI.IdpConfig>>

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
                // TODO : 로그인 방식에 따라 회원가입에 내용 미리 채우기
                val user = FirebaseAuth.getInstance().currentUser
                startActivity(Intent(this, SignUpActivity::class.java))
            } else {
                Toast.makeText(this, ""+response!!.error!!.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
    }
}
