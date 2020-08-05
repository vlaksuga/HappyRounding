package com.vlaksuga.rounding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userEmail : String
    private val db = FirebaseFirestore.getInstance()
    private var documentPath = ""
    private var userNickname = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // SET TOOLBAR //
        val toolbar = findViewById<Toolbar>(R.id.setting_toolbar)
        setSupportActionBar(toolbar)

        // USER EMAIL //
        auth = Firebase.auth
        userEmail = auth.currentUser!!.email!!
        setUserEmail_textView.text = userEmail

        // USER NICKNAME //
        db.collection("users")
            .whereEqualTo("userEmail", userEmail)
            .addSnapshotListener { value, error ->
                if(error != null) {

                } else {
                    documentPath = value!!.documents[0].id
                    userNickname = value.documents[0].get("userNickname").toString()
                    setUserNickname_textView.text = userNickname
                }
            }

        // CARD VIEW //

        setUserNickname_textView.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_view_nickname, null, false)
            val nicknameTextInputEditText = view.findViewById<TextInputEditText>(R.id.editNickName_EditText)
            builder.setMessage("닉네임 변경")
            builder.setView(view)
            builder.setPositiveButton("적용") { _, _ ->
                if(nicknameTextInputEditText.text.toString().trim().contains(" ")) {
                    Toast.makeText(this, "빈 칸은 닉네임으로 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if(nicknameTextInputEditText.text.toString().trim().length < 2 || nicknameTextInputEditText.text.toString().trim().length > 8) {
                    Toast.makeText(this, "닉네임은 2-8자만 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                db.document("users/$documentPath")
                    .update("userNickname", nicknameTextInputEditText.text.toString())
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            Toast.makeText(this, "닉네임이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            builder.setNegativeButton("취소") { d, _ -> d.dismiss()}
            builder.show()
        }

        settingActivity_close_imageView.setOnClickListener {
            super.onBackPressed()
        }

        logOut_button.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }
    }
}