package com.vlaksuga.rounding

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.dialog_view_user_id.*
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SignUpActivity"
        const val KEY_USER_EMAIL = "com.vlaksuga.rounding.USER_EMAIL"
        const val KEY_USER_NICKNAME = "com.vlaksuga.rounding.USER_NICKNAME"
        const val KEY_USER_PHONE = "com.vlaksuga.rounding.USER_PHONE"
        const val REQUEST_PERMISSION_PHONE_NUMBER = 100
    }

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private var userEmail = ""
    private var userNickname = ""
    private var userPhone = ""
    private var userTeeType = ""


    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // SET USER EMAIL //
        auth = Firebase.auth
        userEmail = auth.currentUser!!.email!!
        userNickname = auth.currentUser!!.displayName!!
        userEmail_textView.text = userEmail
        userNickname_textView.text = userNickname

        // TOOLBAR //
        val toolbar = findViewById<Toolbar>(R.id.sign_in_toolbar)
        setSupportActionBar(toolbar)

        // EMAIL CLICK //
        sign_in_email_cardView.setOnClickListener {
            Snackbar.make(sign_in_layout, "EMAIL은 변경할 수 없습니다.", Snackbar.LENGTH_SHORT).show()
        }

        // NICKNAME DIALOG //
        sign_in_nickname_cardView.setOnClickListener {
            signUpError_textView.text = ""
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_view_nickname, null, false)
            val editNickName = view.findViewById<TextInputEditText>(R.id.editNickName_EditText)
            editNickName.setText(userNickname_textView.text)
            builder.setMessage("닉네임을 입력하세요")
            builder.setView(view)
            builder.setPositiveButton("확인") { _, _ ->
                userNickname_textView.text = editNickName.text
            }
            builder.setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
                .show()
        }

        // USER PHONE NUMBER PERMISSION //
        sign_in_phone_cardView.setOnClickListener {
            checkForPerMissions(android.Manifest.permission.READ_PHONE_NUMBERS, "전화번호", REQUEST_PERMISSION_PHONE_NUMBER)
        }

        // USER TEE BOX SELECT //
        sign_in_tee_cardView.setOnClickListener {
            selectTeeBox()
        }


        // SUBMIT //
        userSubmit_button.setOnClickListener {
            signUpError_textView.text = ""
            val userEmail = userEmail_textView.text
            val userNickname = userNickname_textView.text.toString()

            if (userNickname.contains(" ")) {
                signUpError_textView.text = "닉네임에 빈 칸이 있습니다."
                return@setOnClickListener
            }
            if (userNickname.length < 2 || userNickname.length > 8) {
                signUpError_textView.text = "닉네임은 2-8자만 사용할 수 있습니다."
                return@setOnClickListener
            }

            if (userPhone.isNullOrEmpty()) {
                signUpError_textView.text = "전화번호 입력이 필요합니다."
                return@setOnClickListener
            }

            if(userTeeType == "") {
                signUpError_textView.text = "티 박스를 선택해 주세요"
                return@setOnClickListener
            }

            val user = hashMapOf(
                "userEmail" to userEmail,
                "userNickname" to userNickname,
                "userPhone" to userPhone,
                "userTeeType" to userTeeType
            )

            // VALIDATION //
            db.collection("users")
                .add(user)
                .addOnSuccessListener {
                    Log.d(TAG, "userSubmit_button: success ")
                }
                .addOnFailureListener { _ ->
                    Log.d(TAG, "userSubmit_button: fail ")
                }
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val mainIntent = Intent(this, MainActivity::class.java)
                        startActivityForResult(mainIntent, Activity.RESULT_FIRST_USER)
                    }
                }
        }

        checkForPerMissions(android.Manifest.permission.READ_PHONE_NUMBERS, "전화번호", REQUEST_PERMISSION_PHONE_NUMBER)
    }

    private fun selectTeeBox() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_view_tee, null, false)
        val cardViewLady = view.findViewById<CardView>(R.id.teeLadyList_cardView)
        val cardViewReg = view.findViewById<CardView>(R.id.teeRegList_cardView)
        val cardViewBack = view.findViewById<CardView>(R.id.teeBackList_cardView)
        val cardViewChamp = view.findViewById<CardView>(R.id.teeChampList_cardView)
        builder.setView(view)
        val dialog = builder.create()
        cardViewLady.setOnClickListener {
            dialog.dismiss()
            userTeeType = "RED"
            userTee_textView.text = "RED"
            Log.d(TAG, "selectTeeBox: lady")
        }
        cardViewReg.setOnClickListener {
            dialog.dismiss()
            userTeeType = "WHITE"
            userTee_textView.text = "WHITE"
            Log.d(TAG, "selectTeeBox: reg")
        }
        cardViewBack.setOnClickListener {
            dialog.dismiss()
            userTeeType = "BLACK"
            userTee_textView.text = "BLACK"
            Log.d(TAG, "selectTeeBox: back")
        }
        cardViewChamp.setOnClickListener {
            dialog.dismiss()
            userTeeType = "BLUE"
            userTee_textView.text = "BLUE"
            Log.d(TAG, "selectTeeBox: blue")
        }
        dialog.show()
    }

    private fun checkForPerMissions(permission : String, name: String, requestCode : Int) {
        when {
            ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED -> {
                val manager : TelephonyManager = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val devicePhoneNumber = manager.line1Number
                userPhone = devicePhoneNumber.replace("+82", "0")
                userPhone_textView.text = userPhone
            }
            shouldShowRequestPermissionRationale(permission) -> showDialog(permission, name, requestCode)

            else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        fun innerCheck(name: String) {
            if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(sign_in_layout, "애플리케이션 > 권한의 동의가 필요합니다.", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(sign_in_layout, "$name 사용 권한에 동의하였습니다.", Snackbar.LENGTH_SHORT).show()
            }
        }

        when (requestCode) {
            REQUEST_PERMISSION_PHONE_NUMBER -> innerCheck("전화번호")
        }
    }

    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("앱에서 $name 사용을 위해 권한을 요청합니다.")
            setTitle("앱 권한 요청")
            setPositiveButton("확인") { dialog, which ->
                ActivityCompat.requestPermissions(this@SignUpActivity, arrayOf(permission), requestCode)
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

}
