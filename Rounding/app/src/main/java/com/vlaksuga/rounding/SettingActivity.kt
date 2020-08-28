package com.vlaksuga.rounding

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.vlaksuga.rounding.model.Club
import com.vlaksuga.rounding.model.Course
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SettingActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SettingActivity"
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var userEmail : String
    private val db = FirebaseFirestore.getInstance()
    private var documentPath = ""
    private var userNickname = ""
    private var userPhone = ""
    private var userTeeType = ""
    private var courseIndex = 0

    private val listCourse = arrayListOf<Course>(
        Course("club_001", "course_0001", "MOUNTAIN", arrayListOf(4,4,5,4,3,4,3,4,5), arrayListOf(354,374,548,419,213,387,168,453,507), arrayListOf(310,333,524,347,184,352,142,434,488), arrayListOf(276,293,467,335,154,293,118,402,402), arrayListOf(320,365,543,386,204,367,161,445,501))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // SET TOOLBAR //
        val toolbar = findViewById<Toolbar>(R.id.setting_toolbar)
        setSupportActionBar(toolbar)

        // SET USER EMAIL //
        auth = Firebase.auth
        userEmail = auth.currentUser!!.email!!
        settingEmail_textView.text = userEmail

        // SET USER PROFILE //
        db.collection("users")
            .whereEqualTo("userEmail", userEmail)
            .addSnapshotListener { value, error ->
                if(error != null) {

                } else {
                    documentPath = value!!.documents[0].id
                    userNickname = value.documents[0].get("userNickname").toString()
                    userPhone = value.documents[0].get("userPhone").toString()
                    userTeeType = value.documents[0].get("userTeeType").toString()

                    // DRAW UI //
                    settingNickname_textView.text = userNickname
                    settingPhone_textView.text = userPhone
                    settingTee_textView.text = userTeeType
                }
            }

        // EDIT USER NICKNAME //
        setting_nickname_cardView.setOnClickListener {
            changeNickName()
        }

        setting_phone_cardView.setOnClickListener {
            checkForPerMissions(android.Manifest.permission.READ_PHONE_NUMBERS, "전화번호",
                SignUpActivity.REQUEST_PERMISSION_PHONE_NUMBER
            )
        }

        // EDIT USER TEE BOX //
        setting_tee_cardView.setOnClickListener {
            selectTeeBox()
        }


        settingActivity_close_imageView.setOnClickListener {
            super.onBackPressed()
        }



        logOut_button.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setMessage("로그아웃 하시겠습니까?")
                setPositiveButton("확인") { _, _ ->
                    Firebase.auth.signOut()
                    startActivity(Intent(this@SettingActivity, LogInActivity::class.java))
                    finish()}
                setNegativeButton("취소") { dialog, _ ->  dialog.dismiss()}
                show()
            }
        }
    }

    private fun changeNickName() {
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

    private fun selectTeeBox() {
        val builder = android.app.AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_view_tee, null, false)
        val cardViewLady = view.findViewById<CardView>(R.id.teeLadyList_cardView)
        val cardViewReg = view.findViewById<CardView>(R.id.teeRegList_cardView)
        val cardViewBack = view.findViewById<CardView>(R.id.teeBackList_cardView)
        val cardViewChamp = view.findViewById<CardView>(R.id.teeChampList_cardView)
        builder.setView(view)
        val dialog = builder.create()
        cardViewLady.setOnClickListener {
            userTeeType = "RED"
            settingTee_textView.text = "RED"
            db.document("users/$documentPath")
                .update("userTeeType", userTeeType)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(this, "티 박스가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            Log.d(TAG, "selectTeeBox: RED")
        }
        cardViewReg.setOnClickListener {
            userTeeType = "WHITE"
            settingTee_textView.text = "WHITE"
            db.document("users/$documentPath")
                .update("userTeeType", userTeeType)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(this, "티 박스가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            Log.d(TAG, "selectTeeBox: WHITE")
        }
        cardViewBack.setOnClickListener {
            userTeeType = "BLACK"
            settingTee_textView.text = "BLACK"
            db.document("users/$documentPath")
                .update("userTeeType", userTeeType)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(this, "티 박스가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            Log.d(TAG, "selectTeeBox: BLACK")
        }
        cardViewChamp.setOnClickListener {
            userTeeType = "BLUE"
            settingTee_textView.text = "BLUE"
            db.document("users/$documentPath")
                .update("userTeeType", userTeeType)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(this, "티 박스가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            Log.d(TAG, "selectTeeBox: BLUE")
        }
        dialog.show()
    }

    private fun checkForPerMissions(permission : String, name: String, requestCode : Int) {
        when {
            ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED -> {
                val manager : TelephonyManager = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val devicePhoneNumber = manager.line1Number
                userPhone = devicePhoneNumber.replace("+82", "0")
                settingPhone_textView.text = userPhone
                db.document("users/$documentPath")
                    .update("userPhone", userPhone)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            Toast.makeText(this, "전화번호 변경이 적용 되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            shouldShowRequestPermissionRationale(permission) -> showDialog(permission, name, requestCode)

            else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.apply {
            setMessage("앱에서 $name 사용을 위해 권한을 요청합니다.")
            setTitle("앱 권한 요청")
            setPositiveButton("확인") { dialog, which ->
                ActivityCompat.requestPermissions(this@SettingActivity, arrayOf(permission), requestCode)
            }
        }
        val dialog = builder.create()
        dialog.show()
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
            SignUpActivity.REQUEST_PERMISSION_PHONE_NUMBER -> innerCheck("전화번호")
        }
    }

}