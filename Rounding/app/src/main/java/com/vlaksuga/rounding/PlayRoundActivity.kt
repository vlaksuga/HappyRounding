package com.vlaksuga.rounding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_play_round.*

class PlayRoundActivity : AppCompatActivity() {
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
    }



    private fun addLiveScore(playerPosition: Int) {

        // 커런트 파를 감지하여 최대치를 2배로 설정함
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