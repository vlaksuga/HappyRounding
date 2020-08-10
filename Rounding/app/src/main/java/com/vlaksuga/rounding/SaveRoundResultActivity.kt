package com.vlaksuga.rounding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.firebase.firestore.FirebaseFirestore
import com.vlaksuga.rounding.model.Round
import kotlinx.android.synthetic.main.activity_save_round_result.*

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SaveRoundResultActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SaveRoundResultActivity"
        const val DATE_FORMAT = "yyyy-MM-dd (E)"
        const val COLLECTION_ROUND_RESULTS = "roundResults"
    }

    private val db = FirebaseFirestore.getInstance()
    private var documentPath = ""
    private var roundId = ""
    private var roundOwner = ""
    private var roundDate: Long = 0
    private var roundSeason: Int = 0
    private var roundTeeTime: Long = 0
    private var roundClubId = ""
    private var roundClubName = ""
    private var roundCourseIdList = arrayListOf<String>()
    private var roundCourseNameList = arrayListOf<String>()
    private var courseFirstParList = arrayListOf<Int>()
    private var courseSecondParList = arrayListOf<Int>()
    private var playerEmailList = arrayListOf<String>()
    private var playerNicknameList = arrayListOf<String>()
    private var liveScoreFirstTotalList = arrayListOf(
        arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
        arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
        arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
        arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    )
    private var liveScoreSecondTotalList = arrayListOf(
        arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
        arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
        arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
        arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    )
    private val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.KOREA)
    private var roundResultSetList = arrayListOf<HashMap<String, Any>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_round_result)

        // TOOLBAR //
        val toolbar = findViewById<Toolbar>(R.id.save_round_toolbar)
        setSupportActionBar(toolbar)
        save_roundActivity_close_imageView.setOnClickListener {
            super.onBackPressed()
        }

        // GET INTENT INIT //
        documentPath = intent.getStringExtra(PlayRoundActivity.KEY_DOCUMENT_PATH)!!
        roundId = intent.getStringExtra(PlayRoundActivity.KEY_ROUND_ID)!!
        roundOwner = intent.getStringExtra(PlayRoundActivity.KEY_ROUND_OWNER)!!
        roundDate = intent.getLongExtra(PlayRoundActivity.KEY_DATE, 0)
        roundSeason = intent.getIntExtra(PlayRoundActivity.KEY_SEASON, 0)
        roundTeeTime = intent.getLongExtra(PlayRoundActivity.KEY_TEE_TIME, 0)
        roundClubId = intent.getStringExtra(PlayRoundActivity.KEY_CLUB_ID)!!
        roundClubName = intent.getStringExtra(PlayRoundActivity.KEY_CLUB_NAME)!!
        roundCourseIdList = intent.getStringArrayListExtra(PlayRoundActivity.KEY_COURSE_ID_LIST)!!
        roundCourseNameList =
            intent.getStringArrayListExtra(PlayRoundActivity.KEY_COURSE_NAME_LIST)!!
        courseFirstParList =
            intent.getIntegerArrayListExtra(PlayRoundActivity.KEY_FIRST_COURSE_PAR_LIST)!!
        courseSecondParList =
            intent.getIntegerArrayListExtra(PlayRoundActivity.KEY_SECOND_COURSE_PAR_LIST)!!
        playerEmailList = intent.getStringArrayListExtra(PlayRoundActivity.KEY_PLAYER_EMAIL_LIST)!!
        playerNicknameList =
            intent.getStringArrayListExtra(PlayRoundActivity.KEY_PLAYER_NICKNAME_LIST)!!


        // TEST LOG //
        Log.d(TAG, "onCreate: documentPath => $documentPath")
        Log.d(TAG, "onCreate: roundId => $roundId")
        Log.d(TAG, "onCreate: roundOwner => $roundOwner")
        Log.d(TAG, "onCreate: roundDate => $roundDate")
        Log.d(TAG, "onCreate: roundSeason => $roundSeason")
        Log.d(TAG, "onCreate: roundTeeTime => $roundTeeTime")
        Log.d(TAG, "onCreate: roundClubId => $roundClubId")
        Log.d(TAG, "onCreate: roundClubName => $roundClubName")
        Log.d(TAG, "onCreate: roundCourseIdList => $roundCourseIdList")
        Log.d(TAG, "onCreate: roundCourseNameList => $roundCourseNameList")
        Log.d(TAG, "onCreate: courseFirstParList => $courseFirstParList")
        Log.d(TAG, "onCreate: courseSecondParList => $courseSecondParList")
        Log.d(TAG, "onCreate: playerEmailList => $playerEmailList")
        Log.d(TAG, "onCreate: playerNicknameList => $playerNicknameList")

        // LIVE SCORE //
        syncLiveScore()

        // SET UI //
        saveRoundClubName_textView.text = roundClubName
        courseName1_textView.text = roundCourseNameList[0]
        if (roundCourseIdList.size == 1) {
            courseName2_textView.visibility = View.GONE
            saveSecondCourse_table.visibility = View.GONE
        } else {
            courseName2_textView.text = roundCourseNameList[1]
        }

        val course1PlayerNameViewList = arrayListOf<TextView>(
            course1player1name_textView,
            course1player2name_textView,
            course1player3name_textView,
            course1player4name_textView
        )
        val course2PlayerNameViewList = arrayListOf<TextView>(
            course2player1name_textView,
            course2player2name_textView,
            course2player3name_textView,
            course2player4name_textView
        )

        for (i in 0 until playerNicknameList.size) {
            course1PlayerNameViewList[i].text = playerNicknameList[i]
            course2PlayerNameViewList[i].text = playerNicknameList[i]
        }

        saveRoundDate_textView.text = simpleDateFormat.format(roundDate)





        saveRoundResult_button.setOnClickListener {
            it.visibility = View.GONE
            createRoundResult()
        }
    }

        private fun syncLiveScore() {
            Log.d(TAG, "syncLiveScore: invoke")
            for(i in 0 until playerEmailList.size) {
                Log.d(TAG, "syncLiveScore: for $i")
                db.document("rounds/$documentPath/liveScore/${playerEmailList[i]}")
                    .get()
                    .addOnSuccessListener {
                        Log.d(PlayRoundActivity.TAG, "syncLiveScore: ${playerEmailList[i]} -> success")
                    }
                    .addOnFailureListener {
                        Log.d(PlayRoundActivity.TAG, "syncLiveScore: ${playerEmailList[i]} -> fail ")
                    }
                    .addOnCompleteListener {
                        Log.d(TAG, "syncLiveScore: addOnCompleteListener start")
                        if (it.isSuccessful) {
                            val firstSet: ArrayList<Int> = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
                            val secondSet: ArrayList<Int> = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
                            firstSet[0] = (it.result!!.get("hole01") as Long).toInt()
                            firstSet[1] = (it.result!!.get("hole02") as Long).toInt()
                            firstSet[2] = (it.result!!.get("hole03") as Long).toInt()
                            firstSet[3] = (it.result!!.get("hole04") as Long).toInt()
                            firstSet[4] = (it.result!!.get("hole05") as Long).toInt()
                            firstSet[5] = (it.result!!.get("hole06") as Long).toInt()
                            firstSet[6] = (it.result!!.get("hole07") as Long).toInt()
                            firstSet[7] = (it.result!!.get("hole08") as Long).toInt()
                            firstSet[8] = (it.result!!.get("hole09") as Long).toInt()
                            if(roundCourseIdList.size == 2) {
                                secondSet[0] = (it.result!!.get("hole10") as Long).toInt()
                                secondSet[1] = (it.result!!.get("hole11") as Long).toInt()
                                secondSet[2] = (it.result!!.get("hole12") as Long).toInt()
                                secondSet[3] = (it.result!!.get("hole13") as Long).toInt()
                                secondSet[4] = (it.result!!.get("hole14") as Long).toInt()
                                secondSet[5] = (it.result!!.get("hole15") as Long).toInt()
                                secondSet[6] = (it.result!!.get("hole16") as Long).toInt()
                                secondSet[7] = (it.result!!.get("hole17") as Long).toInt()
                                secondSet[8] = (it.result!!.get("hole18") as Long).toInt()
                            }
                            liveScoreFirstTotalList[i] = firstSet
                            liveScoreSecondTotalList[i] = secondSet

                            // SET HASH //
                            roundResultSetList.add(
                                hashMapOf(
                                    "resultRoundId" to UUID.randomUUID().toString(),
                                    "resultUserEmail" to playerEmailList[i],
                                    "resultUserName" to playerNicknameList[i],
                                    "resultDate" to roundDate,
                                    "resultSeason" to roundSeason,
                                    "resultTeeTime" to roundTeeTime,
                                    "resultClubId" to roundClubId,
                                    "resultClubName" to roundClubName,
                                    "resultCourseIdList" to roundCourseIdList,
                                    "resultCourseNameList" to roundCourseNameList,
                                    "resultFirstCourseParList" to courseFirstParList,
                                    "resultSecondCourseParList" to courseSecondParList,
                                    "resultCoPlayersEmailList" to playerEmailList,
                                    "resultCoPlayersNicknameList" to playerNicknameList,
                                    "resultFirstScoreList" to liveScoreFirstTotalList[i],
                                    "resultSecondScoreList" to liveScoreSecondTotalList[i]
                                )
                            )
                            updateScoreBoard()
                        }
                    }
            }

    }

    private fun updateScoreBoard() {
        Log.d(TAG, "updateScoreBoard: invoke")
        val courseFirstParViewList = arrayListOf<TextView>(
            course1par1_textView,
            course1par2_textView,
            course1par3_textView,
            course1par4_textView,
            course1par5_textView,
            course1par6_textView,
            course1par7_textView,
            course1par8_textView,
            course1par9_textView
        )

        val courseSecondParViewList = arrayListOf<TextView>(
            course2par1_textView,
            course2par2_textView,
            course2par3_textView,
            course2par4_textView,
            course2par5_textView,
            course2par6_textView,
            course2par7_textView,
            course2par8_textView,
            course2par9_textView
        )

        val player1Course1ViewList = arrayListOf<TextView>(
            p1c1s0,
            p1c1s1,
            p1c1s2,
            p1c1s3,
            p1c1s4,
            p1c1s5,
            p1c1s6,
            p1c1s7,
            p1c1s8
        )
        val player1Course2ViewList = arrayListOf<TextView>(
            p1c2s0,
            p1c2s1,
            p1c2s2,
            p1c2s3,
            p1c2s4,
            p1c2s5,
            p1c2s6,
            p1c2s7,
            p1c2s8
        )
        val player2Course1ViewList = arrayListOf<TextView>(
            p2c1s0,
            p2c1s1,
            p2c1s2,
            p2c1s3,
            p2c1s4,
            p2c1s5,
            p2c1s6,
            p2c1s7,
            p2c1s8
        )
        val player2Course2ViewList = arrayListOf<TextView>(
            p2c2s0,
            p2c2s1,
            p2c2s2,
            p2c2s3,
            p2c2s4,
            p2c2s5,
            p2c2s6,
            p2c2s7,
            p2c2s8
        )
        val player3Course1ViewList = arrayListOf<TextView>(
            p3c1s0,
            p3c1s1,
            p3c1s2,
            p3c1s3,
            p3c1s4,
            p3c1s5,
            p3c1s6,
            p3c1s7,
            p3c1s8
        )
        val player3Course2ViewList = arrayListOf<TextView>(
            p3c2s0,
            p3c2s1,
            p3c2s2,
            p3c2s3,
            p3c2s4,
            p3c2s5,
            p3c2s6,
            p3c2s7,
            p3c2s8
        )
        val player4Course1ViewList = arrayListOf<TextView>(
            p4c1s0,
            p4c1s1,
            p4c1s2,
            p4c1s3,
            p4c1s4,
            p4c1s5,
            p4c1s6,
            p4c1s7,
            p4c1s8
        )
        val player4Course2ViewList = arrayListOf<TextView>(
            p4c2s0,
            p4c2s1,
            p4c2s2,
            p4c2s3,
            p4c2s4,
            p4c2s5,
            p4c2s6,
            p4c2s7,
            p4c2s8
        )

        // PAR LIST //
        for (i in 0..8) {
            courseFirstParViewList[i].text = courseFirstParList[i].toString()
            if(roundCourseIdList.size == 2) {
                courseSecondParViewList[i].text = courseSecondParList[i].toString()
            }
        }
        course1parTotal_textView.text = courseFirstParList.sum().toString()
        if(roundCourseIdList.size == 2) {
            course2parTotal_textView.text = courseSecondParList.sum().toString()
        }

        // PLAYERS //
        scoreTotal_layout.weightSum = playerNicknameList.size.toFloat()
        for (playerNumber in 1..playerNicknameList.size) {
            when (playerNumber) {
                1 -> apply {
                    // PLAYER 1 //
                    for (i in 0..8) {
                        player1Course1ViewList[i].text = liveScoreFirstTotalList[0][i].toString()
                        player1Course2ViewList[i].text = liveScoreSecondTotalList[0][i].toString()
                    }
                    player1_cardView.visibility = View.VISIBLE
                    row_result1_player1.visibility = View.VISIBLE
                    row_result2_player1.visibility = View.VISIBLE
                    p1c1total.text = liveScoreFirstTotalList[0].sum().toString()
                    p1c2total.text = liveScoreSecondTotalList[0].sum().toString()
                    player_name_total1.visibility = View.VISIBLE
                    player_name_total1.text = playerNicknameList[0]
                    player_total1.text =
                        (liveScoreFirstTotalList[0].sum() + liveScoreSecondTotalList[0].sum()).toString()
                }
                2 -> apply {
                    // PLAYER 2 //
                    for (i in 0..8) {
                        player2Course1ViewList[i].text = liveScoreFirstTotalList[1][i].toString()
                        player2Course2ViewList[i].text = liveScoreSecondTotalList[1][i].toString()
                    }
                    player2_cardView.visibility = View.VISIBLE
                    row_result1_player2.visibility = View.VISIBLE
                    row_result2_player2.visibility = View.VISIBLE
                    p2c1total.text = liveScoreFirstTotalList[1].sum().toString()
                    p2c2total.text = liveScoreSecondTotalList[1].sum().toString()
                    player_name_total2.visibility = View.VISIBLE
                    player_name_total2.text = playerNicknameList[1]
                    player_total2.text =
                        (liveScoreFirstTotalList[1].sum() + liveScoreSecondTotalList[1].sum()).toString()
                }
                3 -> apply {
                    // PLAYER 3 //
                    for (i in 0..8) {
                        player3Course1ViewList[i].text = liveScoreFirstTotalList[2][i].toString()
                        player3Course2ViewList[i].text = liveScoreSecondTotalList[2][i].toString()
                    }
                    player3_cardView.visibility = View.VISIBLE
                    row_result1_player3.visibility = View.VISIBLE
                    row_result2_player3.visibility = View.VISIBLE
                    p3c1total.text = liveScoreFirstTotalList[2].sum().toString()
                    p3c2total.text = liveScoreSecondTotalList[2].sum().toString()
                    player_name_total3.visibility = View.VISIBLE
                    player_name_total3.text = playerNicknameList[2]
                    player_total3.text =
                        (liveScoreFirstTotalList[2].sum() + liveScoreSecondTotalList[2].sum()).toString()
                }
                4 -> apply {
                    // PLAYER 4 //
                    for (i in 0..8) {
                        player4Course1ViewList[i].text = liveScoreFirstTotalList[3][i].toString()
                        player4Course2ViewList[i].text = liveScoreSecondTotalList[3][i].toString()
                    }
                    player4_cardView.visibility = View.VISIBLE
                    row_result1_player4.visibility = View.VISIBLE
                    row_result2_player4.visibility = View.VISIBLE
                    p4c1total.text = liveScoreFirstTotalList[3].sum().toString()
                    p4c2total.text = liveScoreSecondTotalList[3].sum().toString()
                    player_name_total4.visibility = View.VISIBLE
                    player_name_total4.text = playerNicknameList[3]
                    player_total4.text =
                        (liveScoreFirstTotalList[3].sum() + liveScoreSecondTotalList[3].sum()).toString()
                }
                else -> apply {}
            }
        }
        Log.d(PlayRoundActivity.TAG, "updateScoreBoard: updated!! ")
    }

    private fun createRoundResult() {
        Log.d(TAG, "createRoundResult: invoke")
        for (i in 0 until playerEmailList.size) {
            db.collection(COLLECTION_ROUND_RESULTS)
                .add(roundResultSetList[i])
                .addOnSuccessListener { _ ->
                    Log.d(TAG, "createFirstUserResult:  success ")
                }
                .addOnFailureListener { _ ->
                    Log.d(TAG, "createFirstUserResult: fail ")
                }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createFirstUserResult: saved")
                    }
                }
        }
        Toast.makeText(this, "모두 저장했습니다.", Toast.LENGTH_SHORT).show()
        val builder = AlertDialog.Builder(this)
        builder.setMessage("라운드를 종료합니다.")
        builder.setPositiveButton("확인") { _, _ ->
            db.document("rounds/$documentPath")
                .update("isRoundOpen", false)
                .addOnCompleteListener {
                    startActivity(Intent(this, MainActivity::class.java))
                }
        }
        builder.setOnCancelListener { dialog ->
            db.document("rounds/$documentPath")
                .update("isRoundOpen", false)
                .addOnCompleteListener {
                    startActivity(Intent(this, MainActivity::class.java))
                }
        }
            .show()
    }

}