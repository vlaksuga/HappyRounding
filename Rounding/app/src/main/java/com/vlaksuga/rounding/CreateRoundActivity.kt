package com.vlaksuga.rounding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_round.*
import java.util.*

class CreateRoundActivity : AppCompatActivity() {

    companion object {
        const val TAG = "CreateRoundActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_round)

        // TODO : 선택된 정보를 나열해서 보여주기
        val roundDocument = UUID.randomUUID().toString()


        createRoundSubmit_button.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val round = hashMapOf<String, Any>(
                "roundOwnerUserUUID" to "ownerUserId",
                "roundDate" to 1594037938390,
                "roundClubId" to "clubid",
                "roundCourseIdList" to arrayListOf<String>("course1", "course2"),
                "roundPlayerList" to arrayListOf<String>("kangaksjdfkdjf","asdfsadkfasjdkl","chuqnmchsdfd","sjdafhiasdfwjdh"),
                "isRoundEnd" to false
            )
            // TODO : 중복된 라운드를 생성할 수 없도록 AppShered에 라운드 상태 생성중 변경하기 (만들었다 : 끝났다)
            db.collection("rounds").document(roundDocument)
                .set(round)
                .addOnSuccessListener { _ ->
                    Log.d(TAG, "set success")
                }
                .addOnFailureListener {exception ->
                    Log.w(TAG, "error",  exception)
                }
        }
    }
}