package com.vlaksuga.rounding

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.vlaksuga.rounding.model.ResultRound
import kotlinx.android.synthetic.main.activity_round_result.*
import kotlinx.android.synthetic.main.activity_stats.*
import java.util.*
import kotlin.collections.ArrayList

class StatsActivity : AppCompatActivity() {

    companion object {
        const val TAG = "StatsActivity"
        const val DATE_FORMAT = "yyyy-MM-dd (E)"
        const val START_DATE = 1
        const val FLOAT_SIZE_FORMAT = "%.1f"
    }

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var userEmail: String
    private var startDate : Long = 0
    private var endDate : Long = 0
    private var resultRoundList = arrayListOf<ResultRound>()
    private var startCal = Calendar.getInstance()
    private var endCal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        // TOOLBAR //
        val toolbar = findViewById<Toolbar>(R.id.stats_toolbar)
        setSupportActionBar(toolbar)
        statsActivity_close_imageView.setOnClickListener {
            super.onBackPressed()
        }

        // USER AUTH //
        auth = Firebase.auth
        userEmail = auth.currentUser!!.email!!
        Log.d(MainActivity.TAG, "onCreate: userEmail => $userEmail")

        // DATE PICKER INIT//
        startCal.set(Calendar.DATE, START_DATE)
        startDate_textView.text = SimpleDateFormat(DATE_FORMAT, Locale.KOREA).format(startCal.time)
        endDate_textView.text = SimpleDateFormat(DATE_FORMAT, Locale.KOREA).format(endCal.time)

        // DATE CARD VIEW BUTTON //
        start_date_cardView.setOnClickListener {
            val calendarYear = startCal.get(Calendar.YEAR)
            val calendarMonth = startCal.get(Calendar.MONTH)
            val calendarDay = startCal.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    Log.d(TAG, "start date selected")
                    startCal.set(year, month, dayOfMonth)
                    startDate = startCal.timeInMillis
                    startDate_textView.text = SimpleDateFormat(DATE_FORMAT, Locale.KOREA).format(startCal.time)
                    drawChart()
                },
                calendarYear,
                calendarMonth,
                calendarDay
            )
            datePickerDialog.datePicker.maxDate = endCal.timeInMillis
            datePickerDialog.show()
        }

        end_date_cardView.setOnClickListener {
            val calendarYear = endCal.get(Calendar.YEAR)
            val calendarMonth = endCal.get(Calendar.MONTH)
            val calendarDay = endCal.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    Log.d(TAG, "end date selected")
                    endCal.set(year, month, dayOfMonth)
                    endDate = endCal.timeInMillis
                    endDate_textView.text = SimpleDateFormat(DATE_FORMAT, Locale.KOREA).format(endCal.time)
                    drawChart()
                },
                calendarYear,
                calendarMonth,
                calendarDay
            )
            datePickerDialog.datePicker.minDate = startCal.timeInMillis
            datePickerDialog.show()
        }

        drawChart() // FIRST INVOKE WITH INIT VALUE //
    }

    private fun drawChart() {
        Log.d(TAG, "drawChart: invoke")
        db.collection("roundResults")
            .whereEqualTo("resultUserEmail", userEmail)
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "drawChart: success")
            }
            .addOnFailureListener {
                Log.d(TAG, "drawChart: failed")
                return@addOnFailureListener
            }
            .addOnCompleteListener { task ->
                Log.d(TAG, "db : complete")
                val entries = ArrayList<Entry>()
                entries.clear()

                if(task.isSuccessful) {
                    Log.d(TAG, "db : success!!")
                    // GET DB WITH FULL COURSE ROUND TO RESULT ROUND OBJECT  //
                    showEmptyMessage(false)
                    val roundStartDate = startCal.timeInMillis
                    val roundEndDate = endCal.timeInMillis
                    resultRoundList = arrayListOf() // CLEAR
                    for(document in task.result!!) {
                        if((document.get("resultCourseIdList") as ArrayList<*>).size == 2) {
                            if(document.get("resultDate") as Long in roundStartDate..roundEndDate) {
                                resultRoundList.add(document.toObject(ResultRound::class.java))
                                Log.d(TAG, "round added")
                            } else {
                                Log.d(TAG, "out of date")
                            }
                        } else {
                            Log.d(TAG, "9H found")
                        }
                    }

                    // NULL CHECK //
                    if(resultRoundList.size == 0) {
                        showEmptyMessage(true)
                        Log.d(TAG, "drawChart: Round is Empty")
                        return@addOnCompleteListener
                    }

                    // SORT BY ROUND DATE //
                    val ascendingObj : AscendingObj = AscendingObj()
                    Collections.sort(resultRoundList, ascendingObj)
                    Log.d(TAG, "ascending works")

                    // ADD TO ENTRY //
                    for(i in resultRoundList.indices) {
                        val totalScore = resultRoundList[i].resultFirstScoreList.sum() + resultRoundList[i].resultSecondScoreList.sum()
                        entries.add(Entry(i.toFloat(), totalScore.toFloat()))
                        Log.d(TAG, "entry: added")
                    }

                    val lineDataSet : LineDataSet = LineDataSet(entries, "스코어")

                    lineDataSet.apply {
                        setDrawValues(false)
                        setDrawFilled(false)
                        color = R.color.colorRed
                        setCircleColor(R.color.colorRed)
                        lineWidth = 5f
                    }
                    lineChart.invalidate()
                    lineChart.clear()
                    lineChart.apply {
                        xAxis.labelRotationAngle = 0f
                        xAxis.isEnabled = false
                        axisRight.isEnabled = false
                        data = LineData(lineDataSet)
                        legend.textSize = 14f
                        description.isEnabled = false
                        animateY(1500, Easing.EaseInExpo)

                        drawSummary()
                    }
                }
            }
    }

    private fun showEmptyMessage(state : Boolean) {
        if(state) {
            emptyData_textView.visibility = View.VISIBLE
            summary_layout.visibility = View.GONE
            lineChart.visibility = View.GONE
            averageCase_layout.visibility = View.GONE
            scoreCaseResult_layout.visibility = View.GONE
        } else {
            emptyData_textView.visibility = View.GONE
            summary_layout.visibility = View.VISIBLE
            lineChart.visibility = View.VISIBLE
            averageCase_layout.visibility = View.VISIBLE
            scoreCaseResult_layout.visibility = View.VISIBLE
        }
    }

    private fun drawSummary() {
        Log.d(TAG, "drawSummary: invoke")

        val totalScoreList = arrayListOf<Int>()
        var handicap = 0
        var average = 0
        var bestScore = 0
        for(result in resultRoundList) {
            totalScoreList.add(result.resultFirstScoreList.sum() + result.resultSecondScoreList.sum())
        }
        average = totalScoreList.sum() / totalScoreList.size
        handicap = average - 72
        totalScoreList.sort()
        bestScore = totalScoreList[0]

        handicap_textView.text = handicap.toString()
        averageScore_textView.text = average.toString()
        bestScore_textView.text = bestScore.toString()

        drawParAverage()
    }

    private fun drawParAverage() {
        Log.d(TAG, "drawParAverage: invoke")
        val par3ScoreList = arrayListOf<Int>()
        val par4ScoreList = arrayListOf<Int>()
        val par5ScoreList = arrayListOf<Int>()

        for(resultRound in resultRoundList) {
            for(index in resultRound.resultFirstCourseParList.indices) {
                if(resultRound.resultFirstCourseParList[index] == 3) {
                    par3ScoreList.add(resultRound.resultFirstScoreList[index])
                }
                if(resultRound.resultSecondCourseParList[index] == 3) {
                    par3ScoreList.add(resultRound.resultSecondScoreList[index])
                }
            }
            for(index in resultRound.resultFirstCourseParList.indices) {
                if(resultRound.resultFirstCourseParList[index] == 4) {
                    par4ScoreList.add(resultRound.resultFirstScoreList[index])
                }
                if(resultRound.resultSecondCourseParList[index] == 4) {
                    par4ScoreList.add(resultRound.resultSecondScoreList[index])
                }
            }
            for(index in resultRound.resultFirstCourseParList.indices) {
                if(resultRound.resultFirstCourseParList[index] == 5) {
                    par5ScoreList.add(resultRound.resultFirstScoreList[index])
                }
                if(resultRound.resultSecondCourseParList[index] == 5) {
                    par5ScoreList.add(resultRound.resultSecondScoreList[index])
                }
            }
        }

        val avgPar3Score : Double = (par3ScoreList.sum().toDouble() / par3ScoreList.size) - 3.0
        val avgPar4Score : Double = (par4ScoreList.sum().toDouble() / par4ScoreList.size) - 4.0
        val avgPar5Score : Double = (par5ScoreList.sum().toDouble() / par5ScoreList.size) - 5.0
        val avgPar3 = String.format(FLOAT_SIZE_FORMAT, avgPar3Score)
        val avgPar4 = String.format(FLOAT_SIZE_FORMAT, avgPar4Score)
        val avgPar5 = String.format(FLOAT_SIZE_FORMAT, avgPar5Score)

        reviewPar3Case_textView.text = avgPar3
        reviewPar4Case_textView.text = avgPar4
        reviewPar5Case_textView.text = avgPar5

        drawHoleScoreCase()
    }

    private fun drawHoleScoreCase() {
        Log.d(TAG, "drawHoleScoreCase: invoke")
        var birdieCount = 0
        var parCount = 0
        var bogeyCount = 0
        var bogey2Count = 0
        var bogey3Count = 0

        for(resultRound in resultRoundList) {
            for(index in 0..8) {
                val scoreGapFirst = resultRound.resultFirstScoreList[index] - resultRound.resultFirstCourseParList[index]
                when {
                    scoreGapFirst <= -1 -> {birdieCount += 1}
                    scoreGapFirst == 0 -> {parCount += 1}
                    scoreGapFirst == 1 -> {bogeyCount += 1}
                    scoreGapFirst == 2 -> {bogey2Count += 1}
                    scoreGapFirst >= 3 -> {bogey3Count += 1}
                }
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

        val totalHoles : Int = birdieCount + parCount + bogeyCount + bogey2Count + bogeyCount
        val birdiePercent = String.format(FLOAT_SIZE_FORMAT, (birdieCount.toFloat() / totalHoles) * 100)
        val parPercent = String.format(FLOAT_SIZE_FORMAT, (parCount.toFloat() / totalHoles) * 100)
        val bogeyPercent = String.format(FLOAT_SIZE_FORMAT, (bogeyCount.toFloat() / totalHoles) * 100)
        val bogey2Percent = String.format(FLOAT_SIZE_FORMAT, (bogey2Count.toFloat() / totalHoles) * 100)
        val bogey3Percent = String.format(FLOAT_SIZE_FORMAT, (bogey3Count.toFloat() / totalHoles) * 100)
        reviewBirdieResult_textView.text = "$birdiePercent %"
        reviewParResult_textView.text = "$parPercent %"
        reviewBogeyResult_textView.text = "$bogeyPercent %"
        reviewBogey2Result_textView.text = "$bogey2Percent %"
        reviewBogey3Result_textView.text = "$bogey3Percent %"
    }

    inner class AscendingObj : Comparator<ResultRound> {
        override fun compare(o1: ResultRound?, o2: ResultRound?): Int {
            return o1?.resultDate!!.compareTo(o2!!.resultDate)
        }

    }
}