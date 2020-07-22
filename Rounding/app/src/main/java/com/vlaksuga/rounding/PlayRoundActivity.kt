package com.vlaksuga.rounding

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.vlaksuga.rounding.data.Round
import kotlinx.android.synthetic.main.activity_play_round.*


class PlayRoundActivity : AppCompatActivity() {

    companion object {
        const val TAG = "PlayRoundActivity"
        const val KEY_ROUND_ID = "keyRoundId"
        const val KEY_CLUB_NAME = "keyClubName"
        const val KEY_FIRST_COURSE_NAME = "keyFirstCourseName"
        const val KEY_SECOND_COURSE_NAME = "keySecondCourseName"
        const val KEY_FIRST_COURSE_PAR_LIST = "keyFirstCourseParList"
        const val KEY_SECOND_COURSE_PAR_LIST = "keySecondCourseParList"
        const val KEY_PLAYER_1_NAME = "keyPlayer1Name"
        const val KEY_PLAYER_2_NAME = "keyPlayer2Name"
        const val KEY_PLAYER_3_NAME = "keyPlayer3Name"
        const val KEY_PLAYER_4_NAME = "keyPlayer4Name"
        const val COLLECTION_PATH_ROUNDS = "rounds"
        const val COLLECTION_PATH_USERS = "users"
        const val COLLECTION_PATH_CLUBS = "clubs"
        const val COLLECTION_PATH_COURSES = "courses"
        const val CURRENT_HOLE_COLOR_HIGHLIGHT = "#FF8800"
        const val CURRENT_HOLE_COLOR = "#292B2C"
    }

    private val serverHoleNameList = arrayListOf<String>(
        "hole01",
        "hole02",
        "hole03",
        "hole04",
        "hole05",
        "hole06",
        "hole07",
        "hole08",
        "hole09",
        "hole10",
        "hole11",
        "hole12",
        "hole13",
        "hole14",
        "hole15",
        "hole16",
        "hole17",
        "hole18"
    )
    private lateinit var currentRound: Round
    private var currentUserId = "OPPABANANA"
    private var currentRoundId = ""
    private var currentClubName = ""
    private var currentHoleIndex = 0
    private var currentFirstCourseName = ""
    private var currentSecondCourseName = ""
    private var currentFirstPlayerName = ""
    private var currentSecondPlayerName = ""
    private var currentThirdPlayerName = ""
    private var currentFourthPlayerName = ""
    private var currentCourseParList = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    private var currentCourseFirstParList = arrayListOf<Int>()
    private var currentCourseSecondParList = arrayListOf<Int>()
    private var currentRoundPlayerIdList = arrayListOf<String>()
    private var currentCourseName = ""
    private var roundOwnerUserId = ""
    private var documentPath = ""
    private var liveScorePlayerFirstCourse1 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerFirstCourse2 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerSecondCourse1 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerSecondCourse2 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerThirdCourse1 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerThirdCourse2 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerFourthCourse1 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerFourthCourse2 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)


    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_round)

        Log.d(TAG, "onCreate: start ")
        // GET ROUND ID BY INTENT //

        // DUMMY INIT //
        initDummy()


        currentRoundId = "0e0bca05-b32f-4303-88e6-b6b95cfc0f3f"

        // GET DB FROM FIREBASE //
        getRoundFromFireBase(currentRoundId)


        // FAB //
        playRoundScoreAdd_fab1.setOnClickListener { addLiveScore(0) }
        playRoundScoreAdd_fab2.setOnClickListener { addLiveScore(1) }
        playRoundScoreAdd_fab3.setOnClickListener { addLiveScore(2) }
        playRoundScoreAdd_fab4.setOnClickListener { addLiveScore(3) }

        playRoundScoreRemove_fab1.setOnClickListener { removeLiveScore(0) }
        playRoundScoreRemove_fab2.setOnClickListener { removeLiveScore(1) }
        playRoundScoreRemove_fab3.setOnClickListener { removeLiveScore(2) }
        playRoundScoreRemove_fab4.setOnClickListener { removeLiveScore(3) }

        // NEXT HOLE //
        toNextHole_button.setOnClickListener {
            currentHoleIndex += 1
            setHoleScore(currentHoleIndex)
            Log.d(TAG, "toNextHole_button: currentHoleIndex -> $currentHoleIndex")
        }

        // PRE HOLE //
        toPreHole_button.setOnClickListener {
            currentHoleIndex -= 1
            setHoleScore(currentHoleIndex)
            Log.d(TAG, "toPreHole_button: currentHoleIndex -> $currentHoleIndex")
        }

        // NEXT COURSE //
        toNextCourse_button.setOnClickListener {
            moveToNextCourse()
        }

        // PRE COURSE //
        toPreCourse_button.setOnClickListener {
            moveToPreCourse()
        }

        // FINAL RESULT CHECK //
        finalCheckRound_button.setOnClickListener {
            checkRoundResult()
        }
    }

    private fun deployCurrentHoleToServer() {
        db.document("rounds/$documentPath/liveScore/$currentUserId")
            .update("currentHole", currentHoleIndex)
            .addOnSuccessListener {
                Log.d(TAG, "deployCurrentHoleToServer: success ")
            }
            .addOnFailureListener {
                Log.d(TAG, "deployCurrentHoleToServer: fail ")
            }
            .addOnCompleteListener {
                Log.d(TAG, "deployCurrentHoleToServer: currentHoleIndex -> $currentHoleIndex")
            }
    }

    private fun checkRoundResult() {
        // TODO : 마지막 라운드 이후 행동
        val builder = AlertDialog.Builder(this)
        builder.setMessage("라운드 최종 결과를 확인합니까?")
            .setPositiveButton("확인") { _, _ ->
                val resultIntent = Intent(this, SaveRoundResultActivity::class.java)
                resultIntent.putExtra(KEY_ROUND_ID, currentRoundId)
                resultIntent.putExtra(KEY_FIRST_COURSE_NAME, currentFirstCourseName)
                resultIntent.putExtra(KEY_SECOND_COURSE_NAME, currentSecondCourseName)
                resultIntent.putExtra(KEY_CLUB_NAME, currentClubName)
                resultIntent.putExtra(KEY_PLAYER_1_NAME, currentFirstPlayerName)
                resultIntent.putExtra(KEY_PLAYER_2_NAME, currentSecondPlayerName)
                resultIntent.putExtra(KEY_PLAYER_3_NAME, currentThirdPlayerName)
                resultIntent.putExtra(KEY_PLAYER_4_NAME, currentFourthPlayerName)
                resultIntent.putIntegerArrayListExtra(KEY_FIRST_COURSE_PAR_LIST, currentCourseFirstParList)
                resultIntent.putIntegerArrayListExtra(KEY_SECOND_COURSE_PAR_LIST, currentCourseSecondParList)
                startActivity(resultIntent)
            }
            .setNegativeButton("취소") {dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun initDummy() {
        Log.d(TAG, "initDummy: start ")
        currentRound = Round(
            "sdakfjsofjsd",
            "roundOwnerUserId",
            1594272754762,
            "roundClubId",
            arrayListOf("course_001", "course_002"),
            arrayListOf("playerId1", "playerId2", "playerId3", "playerId4"),
            false
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun getRoundFromFireBase(roundId: String) {

        db.collection(COLLECTION_PATH_ROUNDS)
            .whereEqualTo("roundId", roundId)
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "addOnSuccessListener: Success :) ")
            }
            .addOnFailureListener {
                Log.d(TAG, "addOnFailureListener: Fail => $it ")
            }
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isComplete) {
                    // SET CURRENT ROUND //
                    currentRound = task.result!!.toObjects(Round::class.java)[0]
                    Log.d(TAG, "currentRound: ${currentRound.roundId}")

                    // SET CURRENT ROUND PLAYERS //
                    currentRoundPlayerIdList = currentRound.roundPlayerIdList as ArrayList<String>
                    Log.d(TAG, "playerIdList: $currentRoundPlayerIdList")

                    // SET CURRENT DOCUMENT PATH //
                    documentPath = task.result!!.documents[0].id
                    Log.d(TAG, "getRoundFromFireBase: documentPath -> $documentPath")


                    //
                    // SET LIVE SCORE COLLECTION IF NOT CREATED //
                    // TODO : 처음만들어질 때 체크
                    val liveScore : Boolean = task.result!!.documents[0].get("isLiveScoreCreated") as Boolean

                    Log.d(TAG, "liveScore: $liveScore")
                    if (!liveScore) {
                        for (playerId in currentRound.roundPlayerIdList) {
                            createLiveScoreCollectionSet("rounds/$documentPath/liveScore/$playerId")
                            Log.d(
                                TAG,
                                "createScoreCollectionSet: liveScore Collection for $playerId has created!"
                            )
                        }
                    } else {
                        setCurrentHoleIndex()
                    }
                    getClubName()
                    for (position in 0 until currentRoundPlayerIdList.size - 1) {
                        syncLiveScore(currentRoundPlayerIdList[position], position)
                    }
                }
            }
    }

    private fun syncLiveScore(playerId: String, position: Int) {
        db.document("rounds/$documentPath/liveScore/$playerId")
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "syncLiveScore: $playerId -> success")
            }
            .addOnFailureListener {
                Log.d(TAG, "syncLiveScore: $playerId -> fail ")
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
                                TAG,
                                "syncLiveScore: liveScorePlayerFirstCourse1 -> $liveScorePlayerFirstCourse1"
                            )
                            Log.d(
                                TAG,
                                "syncLiveScore: liveScorePlayerFirstCourse2 -> $liveScorePlayerFirstCourse2"
                            )
                        }
                        1 -> apply {
                            liveScorePlayerSecondCourse1 = firstSet
                            liveScorePlayerSecondCourse2 = secondSet
                            Log.d(
                                TAG,
                                "syncLiveScore: liveScorePlayerSecondCourse1 -> $liveScorePlayerSecondCourse1"
                            )
                            Log.d(
                                TAG,
                                "syncLiveScore: liveScorePlayerSecondCourse2 -> $liveScorePlayerSecondCourse2"
                            )
                        }
                        2 -> apply {
                            liveScorePlayerThirdCourse1 = firstSet
                            liveScorePlayerThirdCourse2 = secondSet
                            Log.d(
                                TAG,
                                "syncLiveScore: liveScorePlayerThirdCourse1 -> $liveScorePlayerThirdCourse1"
                            )
                            Log.d(
                                TAG,
                                "syncLiveScore: liveScorePlayerThirdCourse2 -> $liveScorePlayerThirdCourse2"
                            )
                        }
                        else -> apply {
                            liveScorePlayerFourthCourse1 = firstSet
                            liveScorePlayerFourthCourse2 = secondSet
                            Log.d(
                                TAG,
                                "syncLiveScore: liveScorePlayerFourthCourse1 -> $liveScorePlayerFourthCourse1"
                            )
                            Log.d(
                                TAG,
                                "syncLiveScore: liveScorePlayerFourthCourse2 -> $liveScorePlayerFourthCourse2"
                            )
                        }
                    }
                    setHoleScore(currentHoleIndex)
                    updateScoreBoard()
                }
            }
    }

    private fun setCurrentHoleIndex() {
        Log.d(TAG, "setCurrentHoleIndex: start ")
        db.document("rounds/$documentPath/liveScore/$currentUserId")
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "setCurrentHoleIndex: success ")
            }
            .addOnFailureListener {
                Log.d(TAG, "setCurrentHoleIndex: fail ")
            }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    currentHoleIndex = (it.result!!.get("currentHole") as Long).toInt()
                    Log.d(TAG, "setCurrentHoleIndex: currentHoleUpdated -> $currentHoleIndex")
                }
            }
    }

    private fun getClubName() {
        db.collection(COLLECTION_PATH_CLUBS)
            .whereEqualTo("clubId", currentRound.roundClubId)
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    currentClubName = task.result!!.documents[0].get("clubName") as String
                    playRoundClubName_textView.text = currentClubName
                    Log.d(TAG, "getClubName: $currentClubName ")
                    getCourseSets()
                }
            }
    }

    private fun createLiveScoreCollectionSet(path: String) {
        // TODO : 9홀이냐 18홀이냐에 따라서 홀의 갯수 만드는거 바꾸기
        val halfHoleSet = mapOf(
            "hole01" to 0,
            "hole02" to 0,
            "hole03" to 0,
            "hole04" to 0,
            "hole05" to 0,
            "hole06" to 0,
            "hole07" to 0,
            "hole08" to 0,
            "hole09" to 0,
            "currentHole" to 0
        )
        val fullHoleSet = mapOf(
            "hole01" to 0,
            "hole02" to 0,
            "hole03" to 0,
            "hole04" to 0,
            "hole05" to 0,
            "hole06" to 0,
            "hole07" to 0,
            "hole08" to 0,
            "hole09" to 0,
            "hole10" to 0,
            "hole11" to 0,
            "hole12" to 0,
            "hole13" to 0,
            "hole14" to 0,
            "hole15" to 0,
            "hole16" to 0,
            "hole17" to 0,
            "hole18" to 0
        )

        db.document(path)
            .set(
                mapOf(
                    "hole01" to 0,
                    "hole02" to 0,
                    "hole03" to 0,
                    "hole04" to 0,
                    "hole05" to 0,
                    "hole06" to 0,
                    "hole07" to 0,
                    "hole08" to 0,
                    "hole09" to 0,
                    "hole10" to 0,
                    "hole11" to 0,
                    "hole12" to 0,
                    "hole13" to 0,
                    "hole14" to 0,
                    "hole15" to 0,
                    "hole16" to 0,
                    "hole17" to 0,
                    "hole18" to 0,
                    "currentHole" to 0
                )
            )
            .addOnSuccessListener {
                isLiveScoreCreated(true)
                Log.d(TAG, "createScoreCollectionSet: success ")
            }
            .addOnFailureListener {
                Log.d(TAG, "createScoreCollectionSet: fail ")
            }
            .addOnCompleteListener {
                Log.d(TAG, "createScoreCollectionSet: Create Document at $path")
                setCurrentHoleIndex()
            }
    }

    private fun isLiveScoreCreated(state: Boolean) {
        db.document("rounds/$documentPath")
            .update("isLiveScoreCreated", state)
            .addOnSuccessListener {
                Log.d(TAG, "isLiveScoreCreated: success ")
            }
            .addOnFailureListener {
                Log.d(TAG, "isLiveScoreCreated: fail ")
            }
    }

    private fun setRoundOwnerId(ownerId: String) {
        this.roundOwnerUserId = ownerId
        Log.d(TAG, "RoundOwnerId: $roundOwnerUserId")
    }

    private fun setPars() {
        Log.d(TAG, "setPars: start! ")
        currentPar_0.text = currentCourseParList[0].toString()
        currentPar_1.text = currentCourseParList[1].toString()
        currentPar_2.text = currentCourseParList[2].toString()
        currentPar_3.text = currentCourseParList[3].toString()
        currentPar_4.text = currentCourseParList[4].toString()
        currentPar_5.text = currentCourseParList[5].toString()
        currentPar_6.text = currentCourseParList[6].toString()
        currentPar_7.text = currentCourseParList[7].toString()
        currentPar_8.text = currentCourseParList[8].toString()
        updateTotalHit(0)
        updateTotalHit(1)
        updateTotalHit(2)
        updateTotalHit(3)
    }

    private fun setHoleScore(currentHoleIndex: Int) {
        Log.d(TAG, "setHoleScore: started ")
        Log.d(TAG, "current hole: ${currentHoleIndex + 1}")
        resetCounters()
        decoCurrentHoleIndex(currentHoleIndex)
    }

    private fun decoCurrentHoleIndex(currentHoleIndex: Int) {
        Log.d(TAG, "decoCurrentHoleIndex: start")
        hole_1_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR))
        hole_2_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR))
        hole_3_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR))
        hole_4_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR))
        hole_5_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR))
        hole_6_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR))
        hole_7_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR))
        hole_8_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR))
        hole_9_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR))
        when (currentHoleIndex) {
            0 -> apply {
                hole_1_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR_HIGHLIGHT))
                setButtonVisibility("first")
            }
            1, 10 -> apply {
                hole_2_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR_HIGHLIGHT))
                setButtonVisibility("normal")
            }
            2, 11 -> apply {
                hole_3_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR_HIGHLIGHT))
                setButtonVisibility("normal")
            }
            3, 12 -> apply {
                hole_4_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR_HIGHLIGHT))
                setButtonVisibility("normal")
            }
            4, 13 -> apply {
                hole_5_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR_HIGHLIGHT))
                setButtonVisibility("normal")
            }
            5, 14 -> apply {
                hole_6_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR_HIGHLIGHT))
                setButtonVisibility("normal")
            }
            6, 15 -> apply {
                hole_7_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR_HIGHLIGHT))
                setButtonVisibility("normal")
            }
            7, 16 -> apply {
                hole_8_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR_HIGHLIGHT))
                setButtonVisibility("normal")
            }

            9 -> apply {
                hole_1_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR_HIGHLIGHT))
                setButtonVisibility("preCourse")
            }

            else -> apply {
                if (currentHoleIndex == 8) {
                    hole_9_textView.setTextColor(Color.parseColor(CURRENT_HOLE_COLOR_HIGHLIGHT))
                    setButtonVisibility("nextCourse")
                } else {
                    setButtonVisibility("roundLast")
                }
            }
        }
    }

    private fun setButtonVisibility(stateCode: String) {
        when (stateCode) {
            "first" -> apply {
                toPreHole_button.visibility = View.GONE
                toNextHole_button.visibility = View.VISIBLE
                toNextCourse_button.visibility = View.GONE
                toPreCourse_button.visibility = View.GONE
                finalCheckRound_button.visibility = View.GONE
            }
            "preCourse" -> apply {
                toPreHole_button.visibility = View.GONE
                toNextHole_button.visibility = View.VISIBLE
                toNextCourse_button.visibility = View.GONE
                toPreCourse_button.visibility = View.VISIBLE
                finalCheckRound_button.visibility = View.GONE
            }
            "nextCourse" -> apply {
                toPreHole_button.visibility = View.VISIBLE
                toNextHole_button.visibility = View.GONE
                toNextCourse_button.visibility = View.VISIBLE
                toPreCourse_button.visibility = View.GONE
                finalCheckRound_button.visibility = View.GONE
            }
            "roundLast" -> apply {
                toPreHole_button.visibility = View.VISIBLE
                toNextHole_button.visibility = View.GONE
                toNextCourse_button.visibility = View.GONE
                toPreCourse_button.visibility = View.GONE
                finalCheckRound_button.visibility = View.VISIBLE
            }
            else -> apply {
                toPreHole_button.visibility = View.VISIBLE
                toNextHole_button.visibility = View.VISIBLE
                finalCheckRound_button.visibility = View.GONE
                toPreCourse_button.visibility = View.GONE
                toNextCourse_button.visibility = View.GONE

            }
        }
        Log.d(TAG, "setButtonVisibility: state -> $stateCode")
    }

    private fun moveToNextCourse() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("다음 코스로 이동할까요?")
            .setPositiveButton(
                "확인"
            ) { _, _ ->
                currentHoleIndex = 9
                updateScoreBoard()
                updateTotalHit(0)
                updateTotalHit(1)
                updateTotalHit(2)
                updateTotalHit(3)
                setHoleScore(currentHoleIndex)
                playRoundCourseName_textView.text = currentSecondCourseName
                currentCourseParList = currentCourseSecondParList
                Log.d(TAG, "moveToNextCourse: currentCourseParList -> $currentCourseParList ")
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun moveToPreCourse() {
        // TODO : 이전 코스 이동시 행동
        val builder = AlertDialog.Builder(this)
        builder.setMessage("이전 코스로 이동할까요?")
            .setPositiveButton(
                "확인"
            ) { _, _ ->
                currentHoleIndex = 8
                updateScoreBoard()
                updateTotalHit(0)
                updateTotalHit(1)
                updateTotalHit(2)
                updateTotalHit(3)
                setHoleScore(currentHoleIndex)
                playRoundCourseName_textView.text = currentFirstCourseName
                currentCourseParList = currentCourseFirstParList
                Log.d(TAG, "moveToPreCourse: currentCourseParList -> $currentCourseParList ")
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateScoreBoard() {
        if (currentHoleIndex < 9) {
            Log.d(TAG, "updateScoreBoard: start, course 1")
            player_1_score_0.text = liveScorePlayerFirstCourse1[0].toString()
            player_1_score_1.text = liveScorePlayerFirstCourse1[1].toString()
            player_1_score_2.text = liveScorePlayerFirstCourse1[2].toString()
            player_1_score_3.text = liveScorePlayerFirstCourse1[3].toString()
            player_1_score_4.text = liveScorePlayerFirstCourse1[4].toString()
            player_1_score_5.text = liveScorePlayerFirstCourse1[5].toString()
            player_1_score_6.text = liveScorePlayerFirstCourse1[6].toString()
            player_1_score_7.text = liveScorePlayerFirstCourse1[7].toString()
            player_1_score_8.text = liveScorePlayerFirstCourse1[8].toString()
            player_2_score_0.text = liveScorePlayerSecondCourse1[0].toString()
            player_2_score_1.text = liveScorePlayerSecondCourse1[1].toString()
            player_2_score_2.text = liveScorePlayerSecondCourse1[2].toString()
            player_2_score_3.text = liveScorePlayerSecondCourse1[3].toString()
            player_2_score_4.text = liveScorePlayerSecondCourse1[4].toString()
            player_2_score_5.text = liveScorePlayerSecondCourse1[5].toString()
            player_2_score_6.text = liveScorePlayerSecondCourse1[6].toString()
            player_2_score_7.text = liveScorePlayerSecondCourse1[7].toString()
            player_2_score_8.text = liveScorePlayerSecondCourse1[8].toString()
            player_3_score_0.text = liveScorePlayerThirdCourse1[0].toString()
            player_3_score_1.text = liveScorePlayerThirdCourse1[1].toString()
            player_3_score_2.text = liveScorePlayerThirdCourse1[2].toString()
            player_3_score_3.text = liveScorePlayerThirdCourse1[3].toString()
            player_3_score_4.text = liveScorePlayerThirdCourse1[4].toString()
            player_3_score_5.text = liveScorePlayerThirdCourse1[5].toString()
            player_3_score_6.text = liveScorePlayerThirdCourse1[6].toString()
            player_3_score_7.text = liveScorePlayerThirdCourse1[7].toString()
            player_3_score_8.text = liveScorePlayerThirdCourse1[8].toString()
            player_4_score_0.text = liveScorePlayerFourthCourse1[0].toString()
            player_4_score_1.text = liveScorePlayerFourthCourse1[1].toString()
            player_4_score_2.text = liveScorePlayerFourthCourse1[2].toString()
            player_4_score_3.text = liveScorePlayerFourthCourse1[3].toString()
            player_4_score_4.text = liveScorePlayerFourthCourse1[4].toString()
            player_4_score_5.text = liveScorePlayerFourthCourse1[5].toString()
            player_4_score_6.text = liveScorePlayerFourthCourse1[6].toString()
            player_4_score_7.text = liveScorePlayerFourthCourse1[7].toString()
            player_4_score_8.text = liveScorePlayerFourthCourse1[8].toString()
            Log.d(TAG, "updateScoreBoard: updated!! ")
        } else {
            Log.d(TAG, "updateScoreBoard: start, course 2")
            player_1_score_0.text = liveScorePlayerFirstCourse2[0].toString()
            player_1_score_1.text = liveScorePlayerFirstCourse2[1].toString()
            player_1_score_2.text = liveScorePlayerFirstCourse2[2].toString()
            player_1_score_3.text = liveScorePlayerFirstCourse2[3].toString()
            player_1_score_4.text = liveScorePlayerFirstCourse2[4].toString()
            player_1_score_5.text = liveScorePlayerFirstCourse2[5].toString()
            player_1_score_6.text = liveScorePlayerFirstCourse2[6].toString()
            player_1_score_7.text = liveScorePlayerFirstCourse2[7].toString()
            player_1_score_8.text = liveScorePlayerFirstCourse2[8].toString()
            player_2_score_0.text = liveScorePlayerSecondCourse2[0].toString()
            player_2_score_1.text = liveScorePlayerSecondCourse2[1].toString()
            player_2_score_2.text = liveScorePlayerSecondCourse2[2].toString()
            player_2_score_3.text = liveScorePlayerSecondCourse2[3].toString()
            player_2_score_4.text = liveScorePlayerSecondCourse2[4].toString()
            player_2_score_5.text = liveScorePlayerSecondCourse2[5].toString()
            player_2_score_6.text = liveScorePlayerSecondCourse2[6].toString()
            player_2_score_7.text = liveScorePlayerSecondCourse2[7].toString()
            player_2_score_8.text = liveScorePlayerSecondCourse2[8].toString()
            player_3_score_0.text = liveScorePlayerThirdCourse2[0].toString()
            player_3_score_1.text = liveScorePlayerThirdCourse2[1].toString()
            player_3_score_2.text = liveScorePlayerThirdCourse2[2].toString()
            player_3_score_3.text = liveScorePlayerThirdCourse2[3].toString()
            player_3_score_4.text = liveScorePlayerThirdCourse2[4].toString()
            player_3_score_5.text = liveScorePlayerThirdCourse2[5].toString()
            player_3_score_6.text = liveScorePlayerThirdCourse2[6].toString()
            player_3_score_7.text = liveScorePlayerThirdCourse2[7].toString()
            player_3_score_8.text = liveScorePlayerThirdCourse2[8].toString()
            player_4_score_0.text = liveScorePlayerFourthCourse2[0].toString()
            player_4_score_1.text = liveScorePlayerFourthCourse2[1].toString()
            player_4_score_2.text = liveScorePlayerFourthCourse2[2].toString()
            player_4_score_3.text = liveScorePlayerFourthCourse2[3].toString()
            player_4_score_4.text = liveScorePlayerFourthCourse2[4].toString()
            player_4_score_5.text = liveScorePlayerFourthCourse2[5].toString()
            player_4_score_6.text = liveScorePlayerFourthCourse2[6].toString()
            player_4_score_7.text = liveScorePlayerFourthCourse2[7].toString()
            player_4_score_8.text = liveScorePlayerFourthCourse2[8].toString()
            Log.d(TAG, "updateScoreBoard: updated!! ")
        }
    }

    private fun resetCounters() {
        if (currentHoleIndex < 9) {
            playRoundCurrentHole_textView.text = (currentHoleIndex + 1).toString()
            playRoundPlayerLiveScore_textView1.text =
                liveScorePlayerFirstCourse1[currentHoleIndex].toString()
            playRoundPlayerLiveScore_textView2.text =
                liveScorePlayerSecondCourse1[currentHoleIndex].toString()
            playRoundPlayerLiveScore_textView3.text =
                liveScorePlayerThirdCourse1[currentHoleIndex].toString()
            playRoundPlayerLiveScore_textView4.text =
                liveScorePlayerFourthCourse1[currentHoleIndex].toString()
        } else {
            playRoundCurrentHole_textView.text = (currentHoleIndex - 8).toString()
            playRoundPlayerLiveScore_textView1.text =
                liveScorePlayerFirstCourse2[currentHoleIndex - 9].toString()
            playRoundPlayerLiveScore_textView2.text =
                liveScorePlayerSecondCourse2[currentHoleIndex - 9].toString()
            playRoundPlayerLiveScore_textView3.text =
                liveScorePlayerThirdCourse2[currentHoleIndex - 9].toString()
            playRoundPlayerLiveScore_textView4.text =
                liveScorePlayerFourthCourse2[currentHoleIndex - 9].toString()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getCourseSets() {
        Log.d(TAG, "getCourseSets: start ")

        db.collection(COLLECTION_PATH_COURSES)
            .whereEqualTo("courseId", currentRound.roundCourseIdList[0])
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "getCourseSets - 1: success!! ")
            }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    currentFirstCourseName = it.result!!.documents[0].get("courseName") as String
                    currentCourseFirstParList =
                        it.result!!.documents[0].get("courseParCount") as ArrayList<Int>
                    Log.d(TAG, "getCourseNames: first is $currentFirstCourseName")
                    Log.d(TAG, "getCourseSets: par => $currentCourseFirstParList ")
                    currentCourseParList = currentCourseFirstParList
                    currentCourseName = currentFirstCourseName
                    playRoundCourseName_textView.text = currentCourseName
                    setPars()
                    setRoundOwnerId(currentRound.roundOwnerUserId)
                    getPlayers()
                }
            }

        if (currentRound.roundCourseIdList.size == 2) {
            db.collection(COLLECTION_PATH_COURSES)
                .whereEqualTo("courseId", currentRound.roundCourseIdList[1])
                .get()
                .addOnSuccessListener {
                    Log.d(TAG, "getCourseSets - 2: success!! ")
                }
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        currentSecondCourseName =
                            it.result!!.documents[0].get("courseName") as String
                        currentCourseSecondParList =
                            it.result!!.documents[0].get("courseParCount") as ArrayList<Int>
                        Log.d(TAG, "getCourseNames: second is $currentSecondCourseName")
                        Log.d(TAG, "getCourseSets: par => $currentCourseSecondParList ")
                        Log.d(TAG, "currentCourseParList: $currentCourseParList ")
                    }
                }
        }
    }

    private fun getPlayers() {
        Log.d(TAG, "getPlayers: start, total : ${currentRound.roundPlayerIdList.size} players ")
        val playerSize = currentRound.roundPlayerIdList.size
        if (playerSize > 0) {

            db.collection(COLLECTION_PATH_USERS)
                .whereEqualTo("userId", currentRound.roundPlayerIdList[0])
                .get()
                .addOnSuccessListener {
                    Log.d(TAG, "getPlayers: p1 -> success ")
                }
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        currentFirstPlayerName =
                            it.result!!.documents[0].get("userNickname") as String
                        Log.d(TAG, "getPlayers: p1 -> $currentFirstPlayerName")
                        playName_1.text = currentFirstPlayerName
                        liveScorePlayerName_textview1.text = currentFirstPlayerName
                    }
                }

            if (playerSize > 1) {
                db.collection(COLLECTION_PATH_USERS)
                    .whereEqualTo("userId", currentRound.roundPlayerIdList[1])
                    .get()
                    .addOnSuccessListener {
                        Log.d(TAG, "getPlayers: p2 -> success ")
                    }
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            currentSecondPlayerName =
                                it.result!!.documents[0].get("userNickname") as String
                            Log.d(TAG, "getPlayers: p2 -> $currentSecondPlayerName")
                            playName_2.text = currentSecondPlayerName
                            liveScorePlayerName_textview2.text = currentSecondPlayerName
                        }
                    }
                if (playerSize > 2) {
                    db.collection(COLLECTION_PATH_USERS)
                        .whereEqualTo("userId", currentRound.roundPlayerIdList[2])
                        .get()
                        .addOnSuccessListener {
                            Log.d(TAG, "getPlayers: p3 -> success ")
                        }
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                currentThirdPlayerName =
                                    it.result!!.documents[0].get("userNickname") as String
                                Log.d(TAG, "getPlayers: p3 -> $currentThirdPlayerName")
                                playName_3.text = currentThirdPlayerName
                                liveScorePlayerName_textview3.text = currentThirdPlayerName
                            }
                        }
                    if (playerSize > 3) {
                        db.collection(COLLECTION_PATH_USERS)
                            .whereEqualTo("userId", currentRound.roundPlayerIdList[3])
                            .get()
                            .addOnSuccessListener {
                                Log.d(TAG, "getPlayers: p4 -> success ")
                            }
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    currentFourthPlayerName =
                                        it.result!!.documents[0].get("userNickname") as String
                                    Log.d(TAG, "getPlayers: p4 -> $currentFourthPlayerName")
                                    playName_4.text = currentFourthPlayerName
                                    liveScorePlayerName_textview4.text = currentFourthPlayerName
                                }
                            }
                    }
                }

            }

        }
        applyUIByPlayerSize(playerSize)
    }

    private fun applyUIByPlayerSize(playerSize : Int) {
        if(playerSize > 0) {
            row_player1.visibility = View.VISIBLE
            counter_player_1.visibility = View.VISIBLE
            if(playerSize > 1) {
                row_player2.visibility = View.VISIBLE
                counter_player_2.visibility = View.VISIBLE
                if(playerSize > 2) {
                    row_player3.visibility = View.VISIBLE
                    counter_player_3.visibility = View.VISIBLE
                    if(playerSize > 3) {
                        row_player4.visibility = View.VISIBLE
                        counter_player_4.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            // TODO : 에러메세지
        }
    }

    private fun addLiveScore(playerPosition: Int) {

        val currentParMaxHit: Int = if (currentHoleIndex < 9) {
            currentCourseParList[currentHoleIndex] * 2
        } else {
            currentCourseParList[currentHoleIndex - 9] * 2
        }

        val currentHit = if (currentHoleIndex < 9) {
            when (playerPosition) {
                0 -> liveScorePlayerFirstCourse1[currentHoleIndex]
                1 -> liveScorePlayerSecondCourse1[currentHoleIndex]
                2 -> liveScorePlayerThirdCourse1[currentHoleIndex]
                else -> liveScorePlayerFourthCourse1[currentHoleIndex]
            }
        } else {
            when (playerPosition) {
                0 -> liveScorePlayerFirstCourse2[currentHoleIndex - 9]
                1 -> liveScorePlayerSecondCourse2[currentHoleIndex - 9]
                2 -> liveScorePlayerThirdCourse2[currentHoleIndex - 9]
                else -> liveScorePlayerFourthCourse2[currentHoleIndex - 9]
            }
        }

        if (currentHit < currentParMaxHit) {
            disableFab()
            if (currentHoleIndex < 9) {
                when (playerPosition) {
                    0 -> apply {
                        playRoundPlayerLiveScore_textView1.text = (currentHit + 1).toString()
                        liveScorePlayerFirstCourse1[currentHoleIndex] += 1
                        Log.d(TAG, "liveScorePlayerFirstCourse1: -> $liveScorePlayerFirstCourse1")
                    }
                    1 -> apply {
                        playRoundPlayerLiveScore_textView2.text = (currentHit + 1).toString()
                        liveScorePlayerSecondCourse1[currentHoleIndex] += 1
                        Log.d(TAG, "liveScorePlayerSecondCourse1: -> $liveScorePlayerSecondCourse1")
                    }
                    2 -> apply {
                        playRoundPlayerLiveScore_textView3.text = (currentHit + 1).toString()
                        liveScorePlayerThirdCourse1[currentHoleIndex] += 1
                        Log.d(TAG, "liveScorePlayerThirdCourse1: -> $liveScorePlayerThirdCourse1")
                    }
                    else -> apply {
                        playRoundPlayerLiveScore_textView4.text = (currentHit + 1).toString()
                        liveScorePlayerFourthCourse1[currentHoleIndex] += 1
                        Log.d(TAG, "liveScorePlayerFourthCourse1: -> $liveScorePlayerFourthCourse1")
                    }
                }
            } else {
                when (playerPosition) {
                    0 -> apply {
                        playRoundPlayerLiveScore_textView1.text = (currentHit + 1).toString()
                        liveScorePlayerFirstCourse2[currentHoleIndex - 9] += 1
                        Log.d(TAG, "liveScorePlayerFirstCourse2: -> $liveScorePlayerFirstCourse2")
                    }
                    1 -> apply {
                        playRoundPlayerLiveScore_textView2.text = (currentHit + 1).toString()
                        liveScorePlayerSecondCourse2[currentHoleIndex - 9] += 1
                        Log.d(TAG, "liveScorePlayerSecondCourse2: -> $liveScorePlayerSecondCourse2")
                    }
                    2 -> apply {
                        playRoundPlayerLiveScore_textView3.text = (currentHit + 1).toString()
                        liveScorePlayerThirdCourse2[currentHoleIndex - 9] += 1
                        Log.d(TAG, "liveScorePlayerThirdCourse2: -> $liveScorePlayerThirdCourse2")
                    }
                    else -> apply {
                        playRoundPlayerLiveScore_textView4.text = (currentHit + 1).toString()
                        liveScorePlayerFourthCourse2[currentHoleIndex - 9] += 1
                        Log.d(TAG, "liveScorePlayerFourthCourse2: -> $liveScorePlayerFourthCourse2")
                    }
                }
            }

            addToScoreBoard(playerPosition, currentHoleIndex)
            updateTotalHit(playerPosition)
            deployScoreToServer(playerPosition)

        } else {
            Snackbar.make(playRound_layout, "응 양파", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun disableFab() {
        Log.d(TAG, "disableFab: activate ")
        playRoundScoreAdd_fab1.isClickable = false
        playRoundScoreAdd_fab2.isClickable = false
        playRoundScoreAdd_fab3.isClickable = false
        playRoundScoreAdd_fab4.isClickable = false
        playRoundScoreRemove_fab1.isClickable = false
        playRoundScoreRemove_fab2.isClickable = false
        playRoundScoreRemove_fab3.isClickable = false
        playRoundScoreRemove_fab4.isClickable = false
    }

    private fun ableFab() {
        Log.d(TAG, "ableFab: activate ")
        playRoundScoreAdd_fab1.isClickable = true
        playRoundScoreAdd_fab2.isClickable = true
        playRoundScoreAdd_fab3.isClickable = true
        playRoundScoreAdd_fab4.isClickable = true
        playRoundScoreRemove_fab1.isClickable = true
        playRoundScoreRemove_fab2.isClickable = true
        playRoundScoreRemove_fab3.isClickable = true
        playRoundScoreRemove_fab4.isClickable = true
    }

    private fun deployScoreToServer(playerPosition: Int) {
        val score = if (currentHoleIndex < 9) {
            when (playerPosition) {
                0 -> liveScorePlayerFirstCourse1[currentHoleIndex]
                1 -> liveScorePlayerSecondCourse1[currentHoleIndex]
                2 -> liveScorePlayerThirdCourse1[currentHoleIndex]
                else -> liveScorePlayerFourthCourse1[currentHoleIndex]
            }
        } else {
            when (playerPosition) {
                0 -> liveScorePlayerFirstCourse2[currentHoleIndex - 9]
                1 -> liveScorePlayerSecondCourse2[currentHoleIndex - 9]
                2 -> liveScorePlayerThirdCourse2[currentHoleIndex - 9]
                else -> liveScorePlayerFourthCourse2[currentHoleIndex - 9]
            }
        }

        val holeIndex: String = serverHoleNameList[currentHoleIndex]

        db.document("rounds/$documentPath/liveScore/${currentRoundPlayerIdList[playerPosition]}")
            .update(holeIndex, score)
            .addOnSuccessListener {
                Log.d(TAG, "deployToServer: success")
            }
            .addOnFailureListener {
                Log.d(TAG, "deployToServer: fail")
            }
            .addOnCompleteListener {
                ableFab()
                Log.d(TAG, "deployToServer: complete")
                Log.d(
                    TAG,
                    "deployToServer: ${currentRoundPlayerIdList[playerPosition]} to $score"
                )
            }
    }

    private fun updateTotalHit(playerPosition: Int) {
        if (currentHoleIndex < 9) {
            when (playerPosition) {
                0 -> apply {
                    player_1_score_total.text = liveScorePlayerFirstCourse1.sum().toString()
                }
                1 -> apply {
                    player_2_score_total.text = liveScorePlayerSecondCourse1.sum().toString()
                }
                2 -> apply {
                    player_3_score_total.text = liveScorePlayerThirdCourse1.sum().toString()
                }
                else -> apply {
                    player_4_score_total.text = liveScorePlayerFourthCourse1.sum().toString()
                }
            }
        } else {
            when (playerPosition) {
                0 -> apply {
                    player_1_score_total.text = liveScorePlayerFirstCourse2.sum().toString()
                }
                1 -> apply {
                    player_2_score_total.text = liveScorePlayerSecondCourse2.sum().toString()
                }
                2 -> apply {
                    player_3_score_total.text = liveScorePlayerThirdCourse2.sum().toString()
                }
                else -> apply {
                    player_4_score_total.text = liveScorePlayerFourthCourse2.sum().toString()
                }
            }
        }
    }

    private fun addToScoreBoard(playerPosition: Int, currentHoleIndex: Int) {
        when (playerPosition) {
            0 -> apply {
                when (currentHoleIndex) {
                    0, 9 -> apply {
                        var score = player_1_score_0.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_1_score_0.text = score.toString()
                    }
                    1, 10 -> apply {
                        var score = player_1_score_1.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_1_score_1.text = score.toString()
                    }
                    2, 11 -> apply {
                        var score = player_1_score_2.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_1_score_2.text = score.toString()
                    }
                    3, 12 -> apply {
                        var score = player_1_score_3.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_1_score_3.text = score.toString()
                    }
                    4, 13 -> apply {
                        var score = player_1_score_4.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_1_score_4.text = score.toString()
                    }
                    5, 14 -> apply {
                        var score = player_1_score_5.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_1_score_5.text = score.toString()
                    }
                    6, 15 -> apply {
                        var score = player_1_score_6.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_1_score_6.text = score.toString()
                    }
                    7, 16 -> apply {
                        var score = player_1_score_7.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_1_score_7.text = score.toString()
                    }
                    else -> apply {
                        var score = player_1_score_8.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_1_score_8.text = score.toString()
                    }
                }
            }
            1 -> apply {
                when (currentHoleIndex) {
                    0, 9 -> apply {
                        var score = player_2_score_0.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_2_score_0.text = score.toString()
                    }
                    1, 10 -> apply {
                        var score = player_2_score_1.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_2_score_1.text = score.toString()
                    }
                    2, 11 -> apply {
                        var score = player_2_score_2.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_2_score_2.text = score.toString()
                    }
                    3, 12 -> apply {
                        var score = player_2_score_3.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_2_score_3.text = score.toString()
                    }
                    4, 13 -> apply {
                        var score = player_2_score_4.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_2_score_4.text = score.toString()
                    }
                    5, 14 -> apply {
                        var score = player_2_score_5.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_2_score_5.text = score.toString()
                    }
                    6, 15 -> apply {
                        var score = player_2_score_6.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_2_score_6.text = score.toString()
                    }
                    7, 16 -> apply {
                        var score = player_2_score_7.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_2_score_7.text = score.toString()
                    }
                    else -> apply {
                        var score = player_2_score_8.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_2_score_8.text = score.toString()
                    }
                }
            }
            2 -> apply {
                when (currentHoleIndex) {
                    0, 9 -> apply {
                        var score = player_3_score_0.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_3_score_0.text = score.toString()
                    }
                    1, 10 -> apply {
                        var score = player_3_score_1.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_3_score_1.text = score.toString()
                    }
                    2, 11 -> apply {
                        var score = player_3_score_2.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_3_score_2.text = score.toString()
                    }
                    3, 12 -> apply {
                        var score = player_3_score_3.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_3_score_3.text = score.toString()
                    }
                    4, 13 -> apply {
                        var score = player_3_score_4.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_3_score_4.text = score.toString()
                    }
                    5, 14 -> apply {
                        var score = player_3_score_5.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_3_score_5.text = score.toString()
                    }
                    6, 15 -> apply {
                        var score = player_3_score_6.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_3_score_6.text = score.toString()
                    }
                    7, 16 -> apply {
                        var score = player_3_score_7.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_3_score_7.text = score.toString()
                    }
                    else -> apply {
                        var score = player_3_score_8.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_3_score_8.text = score.toString()
                    }
                }
            }
            else -> apply {
                when (currentHoleIndex) {
                    0, 9 -> apply {
                        var score = player_4_score_0.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_4_score_0.text = score.toString()
                    }
                    1, 10 -> apply {
                        var score = player_4_score_1.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_4_score_1.text = score.toString()
                    }
                    2, 11 -> apply {
                        var score = player_4_score_2.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_4_score_2.text = score.toString()
                    }
                    3, 12 -> apply {
                        var score = player_4_score_3.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_4_score_3.text = score.toString()
                    }
                    4, 13 -> apply {
                        var score = player_4_score_4.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_4_score_4.text = score.toString()
                    }
                    5, 14 -> apply {
                        var score = player_4_score_5.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_4_score_5.text = score.toString()
                    }
                    6, 15 -> apply {
                        var score = player_4_score_6.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_4_score_6.text = score.toString()
                    }
                    7, 16 -> apply {
                        var score = player_4_score_7.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_4_score_7.text = score.toString()
                    }
                    else -> apply {
                        var score = player_4_score_8.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score += 1
                        player_4_score_8.text = score.toString()
                    }
                }
            }
        }
    }

    private fun removeLiveScore(playerPosition: Int) {
        val currentHit = if (currentHoleIndex < 9) {
            when (playerPosition) {
                0 -> liveScorePlayerFirstCourse1[currentHoleIndex]
                1 -> liveScorePlayerSecondCourse1[currentHoleIndex]
                2 -> liveScorePlayerThirdCourse1[currentHoleIndex]
                else -> liveScorePlayerFourthCourse1[currentHoleIndex]
            }
        } else {
            when (playerPosition) {
                0 -> liveScorePlayerFirstCourse2[currentHoleIndex - 9]
                1 -> liveScorePlayerSecondCourse2[currentHoleIndex - 9]
                2 -> liveScorePlayerThirdCourse2[currentHoleIndex - 9]
                else -> liveScorePlayerFourthCourse2[currentHoleIndex - 9]
            }
        }
        if (currentHit > 0) {
            disableFab()
            if (currentHoleIndex < 9) {
                when (playerPosition) {
                    0 -> apply {
                        playRoundPlayerLiveScore_textView1.text = (currentHit - 1).toString()
                        liveScorePlayerFirstCourse1[currentHoleIndex] =
                            liveScorePlayerFirstCourse1[currentHoleIndex] - 1
                    }
                    1 -> apply {
                        playRoundPlayerLiveScore_textView2.text = (currentHit - 1).toString()
                        liveScorePlayerSecondCourse1[currentHoleIndex] =
                            liveScorePlayerSecondCourse1[currentHoleIndex] - 1
                    }
                    2 -> apply {
                        playRoundPlayerLiveScore_textView3.text = (currentHit - 1).toString()
                        liveScorePlayerThirdCourse1[currentHoleIndex] =
                            liveScorePlayerThirdCourse1[currentHoleIndex] - 1
                    }
                    else -> apply {
                        playRoundPlayerLiveScore_textView4.text = (currentHit - 1).toString()
                        liveScorePlayerFourthCourse1[currentHoleIndex] =
                            liveScorePlayerFourthCourse1[currentHoleIndex] - 1
                    }
                }
            } else {
                when (playerPosition) {
                    0 -> apply {
                        playRoundPlayerLiveScore_textView1.text = (currentHit - 1).toString()
                        liveScorePlayerFirstCourse2[currentHoleIndex - 9] -= 1
                    }
                    1 -> apply {
                        playRoundPlayerLiveScore_textView2.text = (currentHit - 1).toString()
                        liveScorePlayerSecondCourse2[currentHoleIndex - 9] -= 1
                    }
                    2 -> apply {
                        playRoundPlayerLiveScore_textView3.text = (currentHit - 1).toString()
                        liveScorePlayerThirdCourse2[currentHoleIndex - 9] -= 1
                    }
                    else -> apply {
                        playRoundPlayerLiveScore_textView4.text = (currentHit - 1).toString()
                        liveScorePlayerFourthCourse2[currentHoleIndex - 9] -= 1
                    }
                }
            }

            removeToScoreBoard(playerPosition)
            updateTotalHit(playerPosition)
            deployScoreToServer(playerPosition)
        } else {
            Snackbar.make(playRound_layout, "응 음수", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun removeToScoreBoard(playerPosition: Int) {
        when (playerPosition) {
            0 -> apply {
                when (currentHoleIndex) {
                    0, 9 -> apply {
                        var score = player_1_score_0.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_1_score_0.text = score.toString()
                    }
                    1, 10 -> apply {
                        var score = player_1_score_1.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_1_score_1.text = score.toString()
                    }
                    2, 11 -> apply {
                        var score = player_1_score_2.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_1_score_2.text = score.toString()
                    }
                    3, 12 -> apply {
                        var score = player_1_score_3.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_1_score_3.text = score.toString()
                    }
                    4, 13 -> apply {
                        var score = player_1_score_4.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_1_score_4.text = score.toString()
                    }
                    5, 14 -> apply {
                        var score = player_1_score_5.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_1_score_5.text = score.toString()
                    }
                    6, 15 -> apply {
                        var score = player_1_score_6.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_1_score_6.text = score.toString()
                    }
                    7, 16 -> apply {
                        var score = player_1_score_7.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_1_score_7.text = score.toString()
                    }
                    else -> apply {
                        var score = player_1_score_8.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_1_score_8.text = score.toString()
                    }
                }
            }
            1 -> apply {
                when (currentHoleIndex) {
                    0, 9 -> apply {
                        var score = player_2_score_0.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_2_score_0.text = score.toString()
                    }
                    1, 10 -> apply {
                        var score = player_2_score_1.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_2_score_1.text = score.toString()
                    }
                    2, 11 -> apply {
                        var score = player_2_score_2.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_2_score_2.text = score.toString()
                    }
                    3, 12 -> apply {
                        var score = player_2_score_3.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_2_score_3.text = score.toString()
                    }
                    4, 13 -> apply {
                        var score = player_2_score_4.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_2_score_4.text = score.toString()
                    }
                    5, 14 -> apply {
                        var score = player_2_score_5.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_2_score_5.text = score.toString()
                    }
                    6, 15 -> apply {
                        var score = player_2_score_6.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_2_score_6.text = score.toString()
                    }
                    7, 16 -> apply {
                        var score = player_2_score_7.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_2_score_7.text = score.toString()
                    }
                    else -> apply {
                        var score = player_2_score_8.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_2_score_8.text = score.toString()
                    }
                }
            }
            2 -> apply {
                when (currentHoleIndex) {
                    0, 9 -> apply {
                        var score = player_3_score_0.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_3_score_0.text = score.toString()
                    }
                    1, 10 -> apply {
                        var score = player_3_score_1.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_3_score_1.text = score.toString()
                    }
                    2, 11 -> apply {
                        var score = player_3_score_2.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_3_score_2.text = score.toString()
                    }
                    3, 12 -> apply {
                        var score = player_3_score_3.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_3_score_3.text = score.toString()
                    }
                    4, 13 -> apply {
                        var score = player_3_score_4.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_3_score_4.text = score.toString()
                    }
                    5, 14 -> apply {
                        var score = player_3_score_5.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_3_score_5.text = score.toString()
                    }
                    6, 15 -> apply {
                        var score = player_3_score_6.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_3_score_6.text = score.toString()
                    }
                    7, 16 -> apply {
                        var score = player_3_score_7.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_3_score_7.text = score.toString()
                    }
                    else -> apply {
                        var score = player_3_score_8.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_3_score_8.text = score.toString()
                    }
                }
            }
            else -> apply {
                when (currentHoleIndex) {
                    0, 9 -> apply {
                        var score = player_4_score_0.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_4_score_0.text = score.toString()
                    }
                    1, 10 -> apply {
                        var score = player_4_score_1.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_4_score_1.text = score.toString()
                    }
                    2, 11 -> apply {
                        var score = player_4_score_2.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_4_score_2.text = score.toString()
                    }
                    3, 12 -> apply {
                        var score = player_4_score_3.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_4_score_3.text = score.toString()
                    }
                    4, 13 -> apply {
                        var score = player_4_score_4.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_4_score_4.text = score.toString()
                    }
                    5, 14 -> apply {
                        var score = player_4_score_5.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_4_score_5.text = score.toString()
                    }
                    6, 15 -> apply {
                        var score = player_4_score_6.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_4_score_6.text = score.toString()
                    }
                    7, 16 -> apply {
                        var score = player_4_score_7.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_4_score_7.text = score.toString()
                    }
                    else -> apply {
                        var score = player_4_score_8.text.toString().toInt()
                        Log.d(TAG, "getScore: player_1_score_0 -> $score")
                        score -= 1
                        player_4_score_8.text = score.toString()
                    }
                }
            }
        }
    }



    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: start ")
        deployCurrentHoleToServer()
    }

}