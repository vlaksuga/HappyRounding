package com.vlaksuga.rounding

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_log_in.*
import java.util.jar.Manifest

class LogInActivity : AppCompatActivity() {

    companion object {
        const val TAG = "LogInActivity"
    }

    private lateinit var providers: List<List<AuthUI.IdpConfig>>
    private lateinit var auth: FirebaseAuth
    private var userEmail : String? = null
    private val db = FirebaseFirestore.getInstance()

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

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth

        if (auth.currentUser != null) {
            userEmail = auth.currentUser!!.email!!
            Log.d(TAG, "onStart: userEmail is $userEmail")
            hasUserAccount()
        } else {
            showLoginUI()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // GOOGLE //
        if (requestCode == 0) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                if (response!!.isNewUser) {
                    val signUpIntent = Intent(this, SignUpActivity::class.java)
                    startActivity(signUpIntent)
                } else {
                    auth = Firebase.auth
                    userEmail = auth.currentUser!!.email!!
                    hasUserAccount()
                }
            } else {
                Toast.makeText(this, "" + response!!.error!!.message, Toast.LENGTH_SHORT).show()
            }
        }

        // EMAIL //
        if (requestCode == 1) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                if (response!!.isNewUser) {
                    val signUpIntent = Intent(this, SignUpActivity::class.java)
                    startActivity(signUpIntent)
                } else {
                    auth = Firebase.auth
                    userEmail = auth.currentUser!!.email!!
                    hasUserAccount()
                }
            } else {
                Toast.makeText(this, "" + response!!.error!!.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun showSignInOptions(providerIndex: Int) {
        Log.d(TAG, "showSignInOptions: invoke $providerIndex")
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers[providerIndex])
                .build(), providerIndex
        )
    }

    private fun showLoginUI() {
        loginTitle_textView.visibility = View.VISIBLE
        login_desc_textView.visibility = View.VISIBLE
        google_login_button.visibility = View.VISIBLE
        email_login_button.visibility = View.VISIBLE
        loadingComment_textView.visibility = View.GONE
        cartIcon_imageView.visibility = View.GONE
    }

    private fun hasUserAccount() {
        Log.d(TAG, "hasUserAccount: invoke")
        db.collection("users")
            .whereEqualTo("userEmail", userEmail)
            .get()
            .addOnCompleteListener {
                Log.d(TAG, "hasUserAccount: Answered DB")
                if (it.isSuccessful) {
                    if (it.result!!.isEmpty) {
                        Log.d(TAG, "hasUserAccount: Empty -> SignUp")
                        startActivity(Intent(this, SignUpActivity::class.java))
                    } else {
                        Log.d(TAG, "hasUserAccount: Not Empty -> Main")
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                }
            }
    }
}
