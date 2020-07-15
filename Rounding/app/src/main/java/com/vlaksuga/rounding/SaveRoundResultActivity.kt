package com.vlaksuga.rounding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.vlaksuga.rounding.data.Round
import kotlinx.android.synthetic.main.activity_play_round.*
import kotlinx.android.synthetic.main.activity_save_round_result.*

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.round

class SaveRoundResultActivity : AppCompatActivity() {

    // TODO : 라운드 시작 중일때 컬랙션을 인스턴스로 정해서 발행하고, 종료하면 roundResult로 user별로 저장한다음 인스턴스 컬랙션을 삭제한다.
    companion object {
        const val TAG = "SaveRoundResultActivity"
        const val DATE_FORMAT = "yyyy-MM-dd"
        const val COLLECTION_ROUND_RESULTS = "roundResults"
    }

    private val db = FirebaseFirestore.getInstance()
    private lateinit var currentRound: Round
    private var roundId = ""
    private var documentPath = ""
    private var clubName = ""
    private var firstCourseName = ""
    private var secondCourseName = ""
    private var firstPlayerName = ""
    private var secondPlayerName = ""
    private var thirdPlayerName = ""
    private var fourthPlayerName = ""
    private var courseFirstParList = arrayListOf<Int>()
    private var courseSecondParList = arrayListOf<Int>()
    private var playerIdList = arrayListOf<String>()
    private var roundOwnerUserId = ""
    private var liveScorePlayerFirstCourse1 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerFirstCourse2 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerSecondCourse1 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerSecondCourse2 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerThirdCourse1 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerThirdCourse2 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerFourthCourse1 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerFourthCourse2 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.KOREA)
    private var firstUserRoundResult = hashMapOf<String, Any>()
    private var secondUserRoundResult = hashMapOf<String, Any>()
    private var thirdUserRoundResult = hashMapOf<String, Any>()
    private var fourthUserRoundResult = hashMapOf<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_round_result)


        roundId = intent.getStringExtra(PlayRoundActivity.KEY_ROUND_ID)!!
        firstCourseName = intent.getStringExtra(PlayRoundActivity.KEY_FIRST_COURSE_NAME)!!
        secondCourseName = intent.getStringExtra(PlayRoundActivity.KEY_SECOND_COURSE_NAME)!!
        clubName = intent.getStringExtra(PlayRoundActivity.KEY_CLUB_NAME)!!
        firstPlayerName = intent.getStringExtra(PlayRoundActivity.KEY_PLAYER_1_NAME)!!
        secondPlayerName = intent.getStringExtra(PlayRoundActivity.KEY_PLAYER_2_NAME)!!
        thirdPlayerName = intent.getStringExtra(PlayRoundActivity.KEY_PLAYER_3_NAME)!!
        fourthPlayerName = intent.getStringExtra(PlayRoundActivity.KEY_PLAYER_4_NAME)!!
        courseFirstParList =
            intent.getIntegerArrayListExtra(PlayRoundActivity.KEY_FIRST_COURSE_PAR_LIST)!!
        courseSecondParList =
            intent.getIntegerArrayListExtra(PlayRoundActivity.KEY_SECOND_COURSE_PAR_LIST)!!

        Log.d(TAG, "onCreate: roundId => $roundId")
        Log.d(TAG, "onCreate: firstCourseName => $firstCourseName")
        Log.d(TAG, "onCreate: secondCourseName => $secondCourseName")
        Log.d(TAG, "onCreate: clubName => $clubName")
        Log.d(TAG, "onCreate: firstPlayerName => $firstPlayerName")
        Log.d(TAG, "onCreate: secondPlayerName => $secondPlayerName")
        Log.d(TAG, "onCreate: thirdPlayerName => $thirdPlayerName")
        Log.d(TAG, "onCreate: fourthPlayerName => $fourthPlayerName")
        Log.d(TAG, "onCreate: courseFirstParList => $courseFirstParList")
        Log.d(TAG, "onCreate: courseSecondParList => $courseSecondParList")


        saveRoundClubName_textView.text = clubName
        courseName1_textView.text = firstCourseName
        courseName2_textView.text = secondCourseName
        course1player1name_textView.text = firstPlayerName
        course2player1name_textView.text = firstPlayerName
        course1player2name_textView.text = secondPlayerName
        course2player2name_textView.text = secondPlayerName
        course1player3name_textView.text = thirdPlayerName
        course2player3name_textView.text = thirdPlayerName
        course1player4name_textView.text = fourthPlayerName
        course2player4name_textView.text = fourthPlayerName

        db.collection("rounds")
            .whereEqualTo("roundId", roundId)
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "FirebaseFirestore : success ")
            }
            .addOnFailureListener {
                Log.d(TAG, "FirebaseFirestore : fail ")
            }
            .addOnCompleteListener {
                Log.d(TAG, "FirebaseFirestore: complete")
                if (it.isSuccessful) {
                    currentRound = it.result!!.toObjects(Round::class.java)[0]
                    saveRoundDate_textView.text = simpleDateFormat.format(currentRound.roundDate)
                    documentPath = it.result!!.documents[0].id
                    playerIdList = currentRound.roundPlayerIdList as ArrayList<String>
                    Log.d(PlayRoundActivity.TAG, "playerIdList: $playerIdList")

                    for (position in 0 until playerIdList.size - 1) {
                        syncLiveScore(playerIdList[position], position)
                    }
                    Log.d(TAG, "onCreate: currentRound => $currentRound")
                    Log.d(TAG, "onCreate: documentPath => $documentPath")
                }
            }





        saveRoundResult_button.setOnClickListener {
            // TODO : 저장중일때 행동 생각해서 적용하기 (아마도 광고)
            it.visibility = View.GONE
            createFirstUserResult()
        }
    }

    private fun syncLiveScore(playerId: String, position: Int) {
        db.document("rounds/$documentPath/liveScore/$playerId")
            .get()
            .addOnSuccessListener {
                Log.d(PlayRoundActivity.TAG, "syncLiveScore: $playerId -> success")
            }
            .addOnFailureListener {
                Log.d(PlayRoundActivity.TAG, "syncLiveScore: $playerId -> fail ")
            }
            .addOnCompleteListener {
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
                    secondSet[0] = (it.result!!.get("hole10") as Long).toInt()
                    secondSet[1] = (it.result!!.get("hole11") as Long).toInt()
                    secondSet[2] = (it.result!!.get("hole12") as Long).toInt()
                    secondSet[3] = (it.result!!.get("hole13") as Long).toInt()
                    secondSet[4] = (it.result!!.get("hole14") as Long).toInt()
                    secondSet[5] = (it.result!!.get("hole15") as Long).toInt()
                    secondSet[6] = (it.result!!.get("hole16") as Long).toInt()
                    secondSet[7] = (it.result!!.get("hole17") as Long).toInt()
                    secondSet[8] = (it.result!!.get("hole18") as Long).toInt()
                    when (position) {
                        0 -> apply {
                            liveScorePlayerFirstCourse1 = firstSet
                            liveScorePlayerFirstCourse2 = secondSet
                            Log.d(
                                PlayRoundActivity.TAG,
                                "syncLiveScore: liveScorePlayerFirstCourse1 -> $liveScorePlayerFirstCourse1"
                            )
                            Log.d(
                                PlayRoundActivity.TAG,
                                "syncLiveScore: liveScorePlayerFirstCourse2 -> $liveScorePlayerFirstCourse2"
                            )
                        }
                        1 -> apply {
                            liveScorePlayerSecondCourse1 = firstSet
                            liveScorePlayerSecondCourse2 = secondSet
                            Log.d(
                                PlayRoundActivity.TAG,
                                "syncLiveScore: liveScorePlayerSecondCourse1 -> $liveScorePlayerSecondCourse1"
                            )
                            Log.d(
                                PlayRoundActivity.TAG,
                                "syncLiveScore: liveScorePlayerSecondCourse2 -> $liveScorePlayerSecondCourse2"
                            )
                        }
                        2 -> apply {
                            liveScorePlayerThirdCourse1 = firstSet
                            liveScorePlayerThirdCourse2 = secondSet
                            Log.d(
                                PlayRoundActivity.TAG,
                                "syncLiveScore: liveScorePlayerThirdCourse1 -> $liveScorePlayerThirdCourse1"
                            )
                            Log.d(
                                PlayRoundActivity.TAG,
                                "syncLiveScore: liveScorePlayerThirdCourse2 -> $liveScorePlayerThirdCourse2"
                            )
                        }
                        else -> apply {
                            liveScorePlayerFourthCourse1 = firstSet
                            liveScorePlayerFourthCourse2 = secondSet
                            Log.d(
                                PlayRoundActivity.TAG,
                                "syncLiveScore: liveScorePlayerFourthCourse1 -> $liveScorePlayerFourthCourse1"
                            )
                            Log.d(
                                PlayRoundActivity.TAG,
                                "syncLiveScore: liveScorePlayerFourthCourse2 -> $liveScorePlayerFourthCourse2"
                            )
                        }
                    }
                    updateScoreBoard()
                    setResultForEachPlayers()
                }
            }
    }

    private fun setResultForEachPlayers() {
        if (!firstPlayerName.isNullOrBlank()) {
            firstUserRoundResult = hashMapOf<String, Any>(
                "resultRoundId" to UUID.randomUUID().toString(),
                "resultUserName" to firstPlayerName,
                "resultClubName" to clubName,
                "resultDate" to simpleDateFormat.format(currentRound.roundDate),
                "resultCoPlayers" to "",
                "resultFirstCourseName" to firstCourseName,
                "resultSecondCourseName" to secondCourseName,
                "resultFirstCourseParList" to courseFirstParList,
                "resultSecondCourseParList" to courseSecondParList,
                "resultFirstScoreList" to liveScorePlayerFirstCourse1,
                "resultSecondScoreList" to liveScorePlayerFirstCourse2
            )
            Log.d(TAG, "setResultForEachPlayers: p1 -> set ")
        }
        if (!secondPlayerName.isNullOrBlank()) {
            secondUserRoundResult = hashMapOf<String, Any>(
                "resultRoundId" to UUID.randomUUID().toString(),
                "resultUserName" to secondPlayerName,
                "resultClubName" to clubName,
                "resultDate" to simpleDateFormat.format(currentRound.roundDate),
                "resultCoPlayers" to "",
                "resultFirstCourseName" to firstCourseName,
                "resultSecondCourseName" to secondCourseName,
                "resultFirstCourseParList" to courseFirstParList,
                "resultSecondCourseParList" to courseSecondParList,
                "resultFirstScoreList" to liveScorePlayerSecondCourse1,
                "resultSecondScoreList" to liveScorePlayerSecondCourse2
            )
            Log.d(TAG, "setResultForEachPlayers: p2 -> set ")
        }
        if (!thirdPlayerName.isNullOrBlank()) {
            thirdUserRoundResult = hashMapOf<String, Any>(
                "resultRoundId" to UUID.randomUUID().toString(),
                "resultUserName" to thirdPlayerName,
                "resultClubName" to clubName,
                "resultDate" to simpleDateFormat.format(currentRound.roundDate),
                "resultCoPlayers" to "",
                "resultFirstCourseName" to firstCourseName,
                "resultSecondCourseName" to secondCourseName,
                "resultFirstCourseParList" to courseFirstParList,
                "resultSecondCourseParList" to courseSecondParList,
                "resultFirstScoreList" to liveScorePlayerThirdCourse1,
                "resultSecondScoreList" to liveScorePlayerThirdCourse2
            )
            Log.d(TAG, "setResultForEachPlayers: p3 -> set ")
        }
        if (!fourthPlayerName.isNullOrBlank()) {
            fourthUserRoundResult = hashMapOf<String, Any>(
                "resultRoundId" to UUID.randomUUID().toString(),
                "resultUserName" to fourthPlayerName,
                "resultClubName" to clubName,
                "resultDate" to simpleDateFormat.format(currentRound.roundDate),
                "resultCoPlayers" to "",
                "resultFirstCourseName" to firstCourseName,
                "resultSecondCourseName" to secondCourseName,
                "resultFirstCourseParList" to courseFirstParList,
                "resultSecondCourseParList" to courseSecondParList,
                "resultFirstScoreList" to liveScorePlayerFourthCourse1,
                "resultSecondScoreList" to liveScorePlayerThirdCourse2
            )
            Log.d(TAG, "setResultForEachPlayers: p4 -> set ")
        }
        saveRoundResult_button.visibility = View.VISIBLE
        Log.d(TAG, "setResultForEachPlayers: SET DONE! ")
    }

    private fun updateScoreBoard() {
        Log.d(PlayRoundActivity.TAG, "updateScoreBoard: start, course 1")
        course1par1_textView.text = courseFirstParList[0].toString()
        course1par2_textView.text = courseFirstParList[1].toString()
        course1par3_textView.text = courseFirstParList[2].toString()
        course1par4_textView.text = courseFirstParList[3].toString()
        course1par5_textView.text = courseFirstParList[4].toString()
        course1par6_textView.text = courseFirstParList[5].toString()
        course1par7_textView.text = courseFirstParList[6].toString()
        course1par8_textView.text = courseFirstParList[7].toString()
        course1par9_textView.text = courseFirstParList[8].toString()
        course1parTotal_textView.text = courseFirstParList.sum().toString()

        course2par1_textView.text = courseSecondParList[0].toString()
        course2par2_textView.text = courseSecondParList[1].toString()
        course2par3_textView.text = courseSecondParList[2].toString()
        course2par4_textView.text = courseSecondParList[3].toString()
        course2par5_textView.text = courseSecondParList[4].toString()
        course2par6_textView.text = courseSecondParList[5].toString()
        course2par7_textView.text = courseSecondParList[6].toString()
        course2par8_textView.text = courseSecondParList[7].toString()
        course2par9_textView.text = courseSecondParList[8].toString()
        course2parTotal_textView.text = courseSecondParList.sum().toString()

        p1c1s0.text = liveScorePlayerFirstCourse1[0].toString()
        p1c1s1.text = liveScorePlayerFirstCourse1[1].toString()
        p1c1s2.text = liveScorePlayerFirstCourse1[2].toString()
        p1c1s3.text = liveScorePlayerFirstCourse1[3].toString()
        p1c1s4.text = liveScorePlayerFirstCourse1[4].toString()
        p1c1s5.text = liveScorePlayerFirstCourse1[5].toString()
        p1c1s6.text = liveScorePlayerFirstCourse1[6].toString()
        p1c1s7.text = liveScorePlayerFirstCourse1[7].toString()
        p1c1s8.text = liveScorePlayerFirstCourse1[8].toString()
        p1c1total.text = liveScorePlayerFirstCourse1.sum().toString()

        p2c1s0.text = liveScorePlayerSecondCourse1[0].toString()
        p2c1s1.text = liveScorePlayerSecondCourse1[1].toString()
        p2c1s2.text = liveScorePlayerSecondCourse1[2].toString()
        p2c1s3.text = liveScorePlayerSecondCourse1[3].toString()
        p2c1s4.text = liveScorePlayerSecondCourse1[4].toString()
        p2c1s5.text = liveScorePlayerSecondCourse1[5].toString()
        p2c1s6.text = liveScorePlayerSecondCourse1[6].toString()
        p2c1s7.text = liveScorePlayerSecondCourse1[7].toString()
        p2c1s8.text = liveScorePlayerSecondCourse1[8].toString()
        p2c1total.text = liveScorePlayerSecondCourse1.sum().toString()

        p3c1s0.text = liveScorePlayerThirdCourse1[0].toString()
        p3c1s1.text = liveScorePlayerThirdCourse1[1].toString()
        p3c1s2.text = liveScorePlayerThirdCourse1[2].toString()
        p3c1s3.text = liveScorePlayerThirdCourse1[3].toString()
        p3c1s4.text = liveScorePlayerThirdCourse1[4].toString()
        p3c1s5.text = liveScorePlayerThirdCourse1[5].toString()
        p3c1s6.text = liveScorePlayerThirdCourse1[6].toString()
        p3c1s7.text = liveScorePlayerThirdCourse1[7].toString()
        p3c1s8.text = liveScorePlayerThirdCourse1[8].toString()
        p3c1total.text = liveScorePlayerThirdCourse1.sum().toString()

        p4c1s0.text = liveScorePlayerFourthCourse1[0].toString()
        p4c1s1.text = liveScorePlayerFourthCourse1[1].toString()
        p4c1s2.text = liveScorePlayerFourthCourse1[2].toString()
        p4c1s3.text = liveScorePlayerFourthCourse1[3].toString()
        p4c1s4.text = liveScorePlayerFourthCourse1[4].toString()
        p4c1s5.text = liveScorePlayerFourthCourse1[5].toString()
        p4c1s6.text = liveScorePlayerFourthCourse1[6].toString()
        p4c1s7.text = liveScorePlayerFourthCourse1[7].toString()
        p4c1s8.text = liveScorePlayerFourthCourse1[8].toString()
        p4c1total.text = liveScorePlayerFourthCourse1.sum().toString()
        Log.d(PlayRoundActivity.TAG, "updateScoreBoard: updated!! ")

        Log.d(PlayRoundActivity.TAG, "updateScoreBoard: start, course 2")
        p1c2s0.text = liveScorePlayerFirstCourse2[0].toString()
        p1c2s1.text = liveScorePlayerFirstCourse2[1].toString()
        p1c2s2.text = liveScorePlayerFirstCourse2[2].toString()
        p1c2s3.text = liveScorePlayerFirstCourse2[3].toString()
        p1c2s4.text = liveScorePlayerFirstCourse2[4].toString()
        p1c2s5.text = liveScorePlayerFirstCourse2[5].toString()
        p1c2s6.text = liveScorePlayerFirstCourse2[6].toString()
        p1c2s7.text = liveScorePlayerFirstCourse2[7].toString()
        p1c2s8.text = liveScorePlayerFirstCourse2[8].toString()
        p1c2total.text = liveScorePlayerFirstCourse2.sum().toString()

        p2c2s0.text = liveScorePlayerSecondCourse2[0].toString()
        p2c2s1.text = liveScorePlayerSecondCourse2[1].toString()
        p2c2s2.text = liveScorePlayerSecondCourse2[2].toString()
        p2c2s3.text = liveScorePlayerSecondCourse2[3].toString()
        p2c2s4.text = liveScorePlayerSecondCourse2[4].toString()
        p2c2s5.text = liveScorePlayerSecondCourse2[5].toString()
        p2c2s6.text = liveScorePlayerSecondCourse2[6].toString()
        p2c2s7.text = liveScorePlayerSecondCourse2[7].toString()
        p2c2s8.text = liveScorePlayerSecondCourse2[8].toString()
        p2c2total.text = liveScorePlayerSecondCourse2.sum().toString()

        p3c2s0.text = liveScorePlayerThirdCourse2[0].toString()
        p3c2s1.text = liveScorePlayerThirdCourse2[1].toString()
        p3c2s2.text = liveScorePlayerThirdCourse2[2].toString()
        p3c2s3.text = liveScorePlayerThirdCourse2[3].toString()
        p3c2s4.text = liveScorePlayerThirdCourse2[4].toString()
        p3c2s5.text = liveScorePlayerThirdCourse2[5].toString()
        p3c2s6.text = liveScorePlayerThirdCourse2[6].toString()
        p3c2s7.text = liveScorePlayerThirdCourse2[7].toString()
        p3c2s8.text = liveScorePlayerThirdCourse2[8].toString()
        p3c2total.text = liveScorePlayerThirdCourse2.sum().toString()

        p4c2s0.text = liveScorePlayerFourthCourse2[0].toString()
        p4c2s1.text = liveScorePlayerFourthCourse2[1].toString()
        p4c2s2.text = liveScorePlayerFourthCourse2[2].toString()
        p4c2s3.text = liveScorePlayerFourthCourse2[3].toString()
        p4c2s4.text = liveScorePlayerFourthCourse2[4].toString()
        p4c2s5.text = liveScorePlayerFourthCourse2[5].toString()
        p4c2s6.text = liveScorePlayerFourthCourse2[6].toString()
        p4c2s7.text = liveScorePlayerFourthCourse2[7].toString()
        p4c2s8.text = liveScorePlayerFourthCourse2[8].toString()
        p4c2total.text = liveScorePlayerFourthCourse2.sum().toString()
        Log.d(PlayRoundActivity.TAG, "updateScoreBoard: updated!! ")
    }

    private fun createFirstUserResult() {
        db.collection(COLLECTION_ROUND_RESULTS)
            .add(firstUserRoundResult)
            .addOnSuccessListener { _ ->
                Log.d(TAG, "createFirstUserResult: success ")
            }
            .addOnFailureListener { _ ->
                Log.d(TAG, "createFirstUserResult: fail ")
            }
            .addOnCompleteListener { firstTask ->
                if (firstTask.isSuccessful) {
                    // TODO : 프로그레스바로 진행중을 알림 : 25% (가능하다면 애니메이션)
                    Log.d(TAG, "createFirstUserResult: saved ")
                    createSecondUserResult()
                }
            }
    }

    private fun createSecondUserResult() {
        db.collection(COLLECTION_ROUND_RESULTS)
            .add(secondUserRoundResult)
            .addOnSuccessListener { _ ->
                Log.d(TAG, "createSecondUserResult: success ")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "createSecondUserResult: success ")
            }
            .addOnCompleteListener { secondTask ->
                if (secondTask.isSuccessful) {
                    // TODO : 프로그레스바로 진행중을 알림 : 50% (가능하다면 애니메이션)
                    Log.d(TAG, "createSecondUserResult: saved ")
                    createThirdUserResult()
                }
            }
    }

    private fun createThirdUserResult() {
        db.collection(COLLECTION_ROUND_RESULTS)
            .add(thirdUserRoundResult)
            .addOnSuccessListener { _ ->
                Log.d(TAG, "createThirdUserResult: success ")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "createThirdUserResult: fail ")
            }
            .addOnCompleteListener { secondTask ->
                if (secondTask.isSuccessful) {
                    // TODO : 프로그레스바로 진행중을 알림 : 75% (가능하다면 애니메이션)
                    Log.d(TAG, "createThirdUserResult: saved ")
                    createFourthUserResult()
                }
            }
    }

    private fun createFourthUserResult() {
        db.collection(COLLECTION_ROUND_RESULTS)
            .add(fourthUserRoundResult)
            .addOnSuccessListener { _ ->
                Log.d(TAG, "createFourthUserResult: success ")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "createFourthUserResult: fail ")
            }
            .addOnCompleteListener { fourthTask ->
                if (fourthTask.isSuccessful) {
                    // TODO : 완료후 행동 고려하기
                    Log.d(TAG, "createThirdUserResult: saved ")
                    Toast.makeText(this, "모두 저장했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }

}