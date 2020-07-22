package com.vlaksuga.rounding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.vlaksuga.rounding.constructors.ResultRound
import com.vlaksuga.rounding.databinding.ActivityRoundResultBinding
import kotlinx.android.synthetic.main.activity_round_result.*


class RoundResultActivity : AppCompatActivity() {

    companion object {
        const val TAG = "RoundResultActivity"
        const val COLLECTION_PATH_ROOT = "roundResults"
        const val DOCUMENT_ID = "YK0yUTO2h4qvlmS6xX8O"
    }

    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO : GET DOCUMENT_ID INDEX FROM INTENT

        val resultRoundId = intent.getStringExtra(DOCUMENT_ID)!!
        Log.d(TAG, "onCreate:intent.documentId => $resultRoundId ")

        @Suppress("UNCHECKED_CAST")
        db.collection(COLLECTION_PATH_ROOT)
            .whereEqualTo("resultRoundId", resultRoundId)
            .get()
            .addOnSuccessListener { document ->
                if (document.isEmpty) {
                    Log.d(TAG, "collection : Not exist ")
                } else {

                }
            }

            .addOnFailureListener {
                Log.d(TAG, "FAIL!")
            }

            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result!!.documents[0]
                    val resultRound = document!!.toObject(ResultRound::class.java)
                    Log.d(TAG, "resultRound: $resultRound")
                    val binding: ActivityRoundResultBinding = DataBindingUtil.setContentView(this, R.layout.activity_round_result)
                        binding.result = resultRound
                    val firstScoreList = resultRound!!.resultFirstScoreList.sum()
                    val secondScoreList = resultRound.resultSecondScoreList.sum()
                    val totalScore = (firstScoreList + secondScoreList).toString()
                    firstCourse_total_hit_textView.text = firstScoreList.toString()
                    firstCourse_table_score_textView.text = firstScoreList.toString()
                    secondCourse_total_hit_textView.text = secondScoreList.toString()
                    secondCourse_table_score_textView.text = secondScoreList.toString()
                    resultRoundTotalHit_textView.text = totalScore
                } else {
                    // TODO : 실패시
                }
            }
    }
}