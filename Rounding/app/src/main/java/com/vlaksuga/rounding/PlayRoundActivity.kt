package com.vlaksuga.rounding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_play_round.*
import java.text.SimpleDateFormat
import java.util.*

class PlayRoundActivity : AppCompatActivity() {

    companion object {
        const val TAG = "PlayRoundActivity"
        const val DATE_FORMAT = "yyyy-MM-dd"
    }

    private val db = FirebaseFirestore.getInstance()
    private val resultRef = db.collection("roundResults")
    private val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.KOREA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_round)

        playRoundScoreAdd_fab1.setOnClickListener { addLiveScore(0) }
        playRoundScoreAdd_fab2.setOnClickListener { addLiveScore(1) }
        playRoundScoreAdd_fab3.setOnClickListener { addLiveScore(2) }
        playRoundScoreAdd_fab4.setOnClickListener { addLiveScore(3) }

        playRoundScoreRemove_fab1.setOnClickListener {removeLiveScore(0)}
        playRoundScoreRemove_fab2.setOnClickListener {removeLiveScore(1)}
        playRoundScoreRemove_fab3.setOnClickListener {removeLiveScore(2)}
        playRoundScoreRemove_fab4.setOnClickListener {removeLiveScore(3)}

        playRoundSubmit_button.setOnClickListener {
            val roundDate = simpleDateFormat.format(1594037938390)
            val myRoundResult = hashMapOf<String, Any>(
                "resultUserName" to "오빠바나나",
                "resultClubName" to "벨리오스",
                "resultDate" to roundDate,
                "resultCoPlayers" to "강지형",
                "resultFirstCourseName" to "LAKE",
                "resultSecondCourseName" to "VALLEY",
                "resultFirstCourseParList" to arrayListOf(4,3,4,4,5,3,4,5,5),
                "resultSecondCourseParList" to arrayListOf(4,4,5,3,4,4,5,3,4),
                "resultFirstScoreList" to arrayListOf(4,3,4,4,5,3,4,5,5),
                "resultSecondScoreList" to arrayListOf(4,4,5,3,4,4,5,3,4)
            )
            resultRef.add(myRoundResult)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "set success")
                }
                .addOnFailureListener {exception ->
                    Log.w(TAG, "error",  exception)
                }
        }
    }

    private fun addLiveScore(playerPosition: Int) {
        val currentParMaxHit: Int = 8
        val currentHit = when (playerPosition) {
            0 -> playRoundPlayerLiveScore_textView1.text.toString().toInt()
            1 -> playRoundPlayerLiveScore_textView2.text.toString().toInt()
            2 -> playRoundPlayerLiveScore_textView3.text.toString().toInt()
            else -> playRoundPlayerLiveScore_textView4.text.toString().toInt()
        }
        if (currentHit < currentParMaxHit) {
            when (playerPosition) {
                0 -> playRoundPlayerLiveScore_textView1.text = (currentHit + 1).toString()
                1 -> playRoundPlayerLiveScore_textView2.text = (currentHit + 1).toString()
                2 -> playRoundPlayerLiveScore_textView3.text = (currentHit + 1).toString()
                else -> playRoundPlayerLiveScore_textView4.text = (currentHit + 1).toString()
            }
        } else {

            // TODO : 플레이어 이름을 감지하여 다른 에러 메세지 보내주기
            Snackbar.make(playRound_layout, "응 양파", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun removeLiveScore(playerPosition: Int) {
        val currentHit = when (playerPosition) {
            0 -> playRoundPlayerLiveScore_textView1.text.toString().toInt()
            1 -> playRoundPlayerLiveScore_textView2.text.toString().toInt()
            2 -> playRoundPlayerLiveScore_textView3.text.toString().toInt()
            else -> playRoundPlayerLiveScore_textView4.text.toString().toInt()
        }
        if (currentHit > 0) {
            when(playerPosition) {
                0 -> playRoundPlayerLiveScore_textView1.text = (currentHit - 1).toString()
                1 -> playRoundPlayerLiveScore_textView2.text = (currentHit - 1).toString()
                2 -> playRoundPlayerLiveScore_textView3.text = (currentHit - 1).toString()
                else -> playRoundPlayerLiveScore_textView4.text = (currentHit - 1).toString()
            }
        } else {
            // TODO : 플레이어 이름을 감지하여 다른 에러 메세지 보내주기
            Snackbar.make(playRound_layout, "응 음수", Snackbar.LENGTH_SHORT).show()
        }
    }

}