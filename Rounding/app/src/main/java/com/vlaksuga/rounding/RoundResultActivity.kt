package com.vlaksuga.rounding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.vlaksuga.rounding.databinding.ActivityRoundResultBinding
import com.vlaksuga.rounding.model.ResultRound
import kotlinx.android.synthetic.main.activity_round_result.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue


class RoundResultActivity : AppCompatActivity() {

    companion object {
        const val TAG = "RoundResultActivity"
        const val COLLECTION_PATH_ROOT = "roundResults"
        const val DOCUMENT_ID = "com.vlaksuga.rounding.DOCUMENT_ID"
        const val FLOAT_SIZE_FORMAT = "%.1f"
    }

    private val db = FirebaseFirestore.getInstance()
    private lateinit var resultRoundId : String
    private lateinit var resultRound: ResultRound


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // GET ROUND ID FROM INTENT //
        resultRoundId = intent.getStringExtra(DOCUMENT_ID)!!
        Log.d(TAG, "onCreate:intent.documentId => $resultRoundId ")

        // BINDING VIEW //
        val binding: ActivityRoundResultBinding = DataBindingUtil.setContentView(this, R.layout.activity_round_result)

        // TOOLBAR //
        val toolbar = findViewById<Toolbar>(R.id.resultRound_toolbar)
        setSupportActionBar(toolbar)
        resultRoundActivity_close_imageView.setOnClickListener {
            super.onBackPressed()
        }

        // GET DB //
        @Suppress("UNCHECKED_CAST")
        db.collection(COLLECTION_PATH_ROOT)
            .whereEqualTo("resultRoundId", resultRoundId)
            .get()
            .addOnSuccessListener { document ->
                if (document.isEmpty) {
                    Log.d(TAG, "collection : Not exist ")
                } else {
                    Log.d(TAG, "collection: exist! ")
                }
            }

            .addOnFailureListener {
                Log.d(TAG, "FAIL!")
            }

            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result!!.documents[0]
                    resultRound = document!!.toObject(ResultRound::class.java)!!
                    binding.result = resultRound
                    Log.d(TAG, "resultRound: $resultRound")
                    updateUI()
                    updateReview()
                } else {
                    // TODO : 실패시
                }
            }

    }

    private fun updateReview() {
        Log.d(TAG, "updateReview: invoke")

        // SCORE LIST //
        val par3ScoreList = arrayListOf<Int>()
        val par4ScoreList = arrayListOf<Int>()
        val par5ScoreList = arrayListOf<Int>()

        // PAR 3 //
        for(index in resultRound.resultFirstCourseParList.indices) {
            if(resultRound.resultFirstCourseParList[index] == 3) {
                par3ScoreList.add(resultRound.resultFirstScoreList[index])
            }
            if(resultRound.resultCourseIdList.size == 2 && resultRound.resultSecondCourseParList[index] == 3) {
                par3ScoreList.add(resultRound.resultSecondScoreList[index])
            }
        }
        Log.d(TAG, "onCreate: par3ScoreList = $par3ScoreList")

        // PAR 4 //
        for(index in resultRound.resultFirstCourseParList.indices) {
            if(resultRound.resultFirstCourseParList[index] == 4) {
                par4ScoreList.add(resultRound.resultFirstScoreList[index])
            }
            if(resultRound.resultCourseIdList.size == 2 && resultRound.resultSecondCourseParList[index] == 4) {
                par4ScoreList.add(resultRound.resultSecondScoreList[index])
            }
        }
        Log.d(TAG, "onCreate: par4ScoreList = $par4ScoreList")

        // PAR 5 //
        for(index in resultRound.resultFirstCourseParList.indices) {
            if(resultRound.resultFirstCourseParList[index] == 5) {
                par5ScoreList.add(resultRound.resultFirstScoreList[index])
            }
            if(resultRound.resultCourseIdList.size == 2 && resultRound.resultSecondCourseParList[index] == 5) {
                par5ScoreList.add(resultRound.resultSecondScoreList[index])
            }
        }
        Log.d(TAG, "onCreate: par5ScoreList = $par5ScoreList")

        // AVERAGE PER PAR //
        val avgPar3Score : Double = (par3ScoreList.sum().toDouble() / par3ScoreList.size) - 3.0
        val avgPar4Score : Double = (par4ScoreList.sum().toDouble() / par4ScoreList.size) - 4.0
        val avgPar5Score : Double = (par5ScoreList.sum().toDouble() / par5ScoreList.size) - 5.0
        val avgPar3 = String.format(FLOAT_SIZE_FORMAT, avgPar3Score)
        val avgPar4 = String.format(FLOAT_SIZE_FORMAT, avgPar4Score)
        val avgPar5 = String.format(FLOAT_SIZE_FORMAT, avgPar5Score)
        Log.d(TAG, "avgPar3: $avgPar3")
        Log.d(TAG, "avgPar4: $avgPar4")
        Log.d(TAG, "avgPar5: $avgPar5")


        // SCORE CASE //
        var birdieCount = 0
        var parCount = 0
        var bogeyCount = 0
        var bogey2Count = 0
        var bogey3Count = 0

        for(index in 0..8) {
            val scoreGapFirst = resultRound.resultFirstScoreList[index] - resultRound.resultFirstCourseParList[index]
            when {
                scoreGapFirst <= -1 -> {birdieCount += 1}
                scoreGapFirst == 0 -> {parCount += 1}
                scoreGapFirst == 1 -> {bogeyCount += 1}
                scoreGapFirst == 2 -> {bogey2Count += 1}
                scoreGapFirst >= 3 -> {bogey3Count += 1}
            }
        }

        if(resultRound.resultCourseIdList.size == 2) {
            for(index in 0..8) {
                val scoreGapSecond = resultRound.resultSecondScoreList[index] - resultRound.resultSecondCourseParList[index]
                when {
                    scoreGapSecond <= -1 -> {birdieCount += 1}
                    scoreGapSecond == 0 -> {parCount += 1}
                    scoreGapSecond == 1 -> {bogeyCount += 1}
                    scoreGapSecond == 2 -> {bogey2Count += 1}
                    scoreGapSecond >= 3 -> {bogey3Count += 1}
                }
            }
        }

        Log.d(TAG, "SCORE CASE: birdieCount = $birdieCount")
        Log.d(TAG, "SCORE CASE: parCount = $parCount")
        Log.d(TAG, "SCORE CASE: bogeyCount = $bogeyCount")
        Log.d(TAG, "SCORE CASE: bogey2Count = $bogey2Count")
        Log.d(TAG, "SCORE CASE: bogey3Count = $bogey3Count")

        // VIEW //
        par3_cardView.elevation = (avgPar3Score.absoluteValue * 8).toFloat()
        par4_cardView.elevation = (avgPar4Score.absoluteValue * 8).toFloat()
        par5_cardView.elevation = (avgPar5Score.absoluteValue * 8).toFloat()

        reviewPar3_textView.text = avgPar3
        reviewPar4_textView.text = avgPar4
        reviewPar5_textView.text = avgPar5

        reviewBirdie_textView.text = "$birdieCount HOLES"
        reviewPar_textView.text = "$parCount HOLES"
        reviewBogey_textView.text = "$bogeyCount HOLES"
        reviewBogey2_textView.text = "$bogey2Count HOLES"
        reviewBogey3_textView.text = "$bogey3Count HOLES"
    }

    private fun updateUI() {
        Log.d(TAG, "updateUI: invoke")
        val firstScoreList = resultRound!!.resultFirstScoreList.sum()
        val secondScoreList = resultRound.resultSecondScoreList.sum()
        val totalScore = (firstScoreList + secondScoreList).toString()
        if(resultRound.resultCourseIdList.size == 2) {
            secondCourse_table_title_textView.visibility = View.VISIBLE
            secondCourse_table_score_textView.visibility = View.VISIBLE
            secondCourse_table.visibility = View.VISIBLE
        }
        resultRoundDate_textView.text = SimpleDateFormat("yyyy-MM-dd (E)", Locale.KOREA).format(resultRound.resultDate)
        resultRoundTime_textView.text = SimpleDateFormat("a hh:mm", Locale.KOREA).format(resultRound.resultDate)
        firstCourse_total_hit_textView.text = firstScoreList.toString()
        firstCourse_table_score_textView.text = firstScoreList.toString()
        secondCourse_total_hit_textView.text = secondScoreList.toString()
        secondCourse_table_score_textView.text = secondScoreList.toString()
        resultRoundTotalHit_textView.text = totalScore
    }
}