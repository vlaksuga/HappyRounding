package com.vlaksuga.rounding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.vlaksuga.rounding.data.Round
import kotlinx.android.synthetic.main.activity_play_round.*


class PlayRoundActivity : AppCompatActivity() {

    companion object {
        const val TAG = "PlayRoundActivity"
        const val COLLECTION_PATH_ROUNDS = "rounds"
        const val COLLECTION_PATH_USERS = "users"
        const val COLLECTION_PATH_CLUBS = "clubs"
        const val COLLECTION_PATH_COURSES = "courses"
    }

    private lateinit var currentRound: Round
    private var currentClubName = ""
    private var currentHoleIndex = 0
    private var currentFirstCourseName = ""
    private var currentSecondCourseName = ""
    private var currentFirstPlayerName = ""
    private var currentSecondPlayerName = ""
    private var currentThirdPlayerName = ""
    private var currentFourthPlayerName = ""
    private var currentCourseIndex = 0
    private var currentCourseParList = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    private var currentCourseFirstParList = arrayListOf<Int>()
    private var currentCourseSecondParList = arrayListOf<Int>()
    private var currentRoundPlayerIdList = arrayListOf<String>()
    private var currentCourseName = ""
    private var roundOwnerUserId = ""
    private var documentPath = ""
    private var currentPlayersSize = 1
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

        // DUMMY INIT //
        initDummy()

        // GET DB FROM FIREBASE //
        getRoundFromFireBase("bb8ed403-f1aa-4cdc-b35e-249c290a972b")



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
    private fun getRoundFromFireBase(roundId : String) {

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
                    // set currentPlayerSize
                    currentRound = task.result!!.toObjects(Round::class.java)[0]
                    Log.d(TAG, "currentRound: $currentRound")

                    getClubName()
                    currentHoleIndex = 0

                    currentPlayersSize = currentRound.roundPlayerIdList.size
                    Log.d(TAG, "currentPlayersSize: $currentPlayersSize" )

                    currentRoundPlayerIdList = currentRound.roundCourseIdList as ArrayList<String>
                    Log.d(TAG, "playerIdList: $currentRoundPlayerIdList" )
                    documentPath = task.result!!.documents[0].id

                    Log.d(TAG, "currentPlayersSize: $currentPlayersSize")
                    Log.d(TAG, "getRoundFromFireBase: documentPath -> $documentPath")
/*                    createScoreCollectionSet(firstPlayerScorePath)
                    createScoreCollectionSet(secondPlayerScorePath)
                    createScoreCollectionSet(thirdPlayerScorePath)
                    createScoreCollectionSet(fourthPlayerScorePath)*/
                    setHoleScore(currentHoleIndex)
                }
            }
    }

    private fun createScoreCollectionSet(path: String) {
        // TODO : 컬렉션이 한번 만들어졌으면 안만들기
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
                    "hole18" to 0
                )
            )
            .addOnSuccessListener {
                Log.d(TAG, "createScoreCollectionSet: success ")
            }
            .addOnFailureListener {
                Log.d(TAG, "createScoreCollectionSet: fail ")
            }
            .addOnCompleteListener {
                Log.d(TAG, "createScoreCollectionSet: done ")
            }
    }

    private fun setRoundOwnerId(ownerId: String) {
        this.roundOwnerUserId = ownerId
        Log.d(TAG, "RoundOwnerId: $roundOwnerUserId")
    }

    private fun setPars(currentCourseIndex: Int) {
        when (currentCourseIndex) {
            0 -> apply {
                Log.d(TAG, "setPars: set 1 ")
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
            else -> apply {
                Log.d(TAG, "setPars: set 2 ")
            }
        }

    }

    private fun setHoleScore(currentHoleIndex: Int) {
        Log.d(TAG, "setNewHole: set! $currentHoleIndex ")
        if(currentHoleIndex == 9) {
            // TODO : 18홀일 때 코스이동, 9홀일때 종료
            Toast.makeText(this, "다음홀로 이동하기", Toast.LENGTH_SHORT).show()
            return
/*            moveToNextCourse()*/
        }
        if(currentHoleIndex >= 18) {
            // TODO : 라운드 종료
        }
        resetCounters()
        Log.d(TAG, "current hole: ${currentHoleIndex + 1}")
        Log.d(TAG, "setHoleScore: p1 -> ${liveScorePlayerFirstCourse1[currentHoleIndex]} ")
        Log.d(TAG, "setHoleScore: p2 -> ${liveScorePlayerSecondCourse1[currentHoleIndex]} ")
        Log.d(TAG, "setHoleScore: p3 -> ${liveScorePlayerThirdCourse1[currentHoleIndex]} ")
        Log.d(TAG, "setHoleScore: p4 -> ${liveScorePlayerFourthCourse1[currentHoleIndex]} ")
        when(currentHoleIndex) {
            0, 9 -> apply {
                player_1_score_0.text = liveScorePlayerFirstCourse1[currentHoleIndex].toString()
                player_2_score_0.text = liveScorePlayerSecondCourse1[currentHoleIndex].toString()
                player_3_score_0.text = liveScorePlayerThirdCourse1[currentHoleIndex].toString()
                player_4_score_0.text = liveScorePlayerFourthCourse1[currentHoleIndex].toString()
                toPreHole_button.visibility = View.GONE
            }
            1, 10 -> apply {
                player_1_score_1.text = liveScorePlayerFirstCourse1[currentHoleIndex].toString()
                player_2_score_1.text = liveScorePlayerSecondCourse1[currentHoleIndex].toString()
                player_3_score_1.text = liveScorePlayerThirdCourse1[currentHoleIndex].toString()
                player_4_score_1.text = liveScorePlayerFourthCourse1[currentHoleIndex].toString()
                toPreHole_button.visibility = View.VISIBLE
            }
            2, 11 -> apply {
                player_1_score_2.text = liveScorePlayerFirstCourse1[currentHoleIndex].toString()
                player_2_score_2.text = liveScorePlayerSecondCourse1[currentHoleIndex].toString()
                player_3_score_2.text = liveScorePlayerThirdCourse1[currentHoleIndex].toString()
                player_4_score_2.text = liveScorePlayerFourthCourse1[currentHoleIndex].toString()
            }
            3, 12 -> apply {
                player_1_score_3.text = liveScorePlayerFirstCourse1[currentHoleIndex].toString()
                player_2_score_3.text = liveScorePlayerSecondCourse1[currentHoleIndex].toString()
                player_3_score_3.text = liveScorePlayerThirdCourse1[currentHoleIndex].toString()
                player_4_score_3.text = liveScorePlayerFourthCourse1[currentHoleIndex].toString()
            }
            4, 13 -> apply {
                player_1_score_4.text = liveScorePlayerFirstCourse1[currentHoleIndex].toString()
                player_2_score_4.text = liveScorePlayerSecondCourse1[currentHoleIndex].toString()
                player_3_score_4.text = liveScorePlayerThirdCourse1[currentHoleIndex].toString()
                player_4_score_4.text = liveScorePlayerFourthCourse1[currentHoleIndex].toString()
            }
            5, 14 -> apply {
                player_1_score_5.text = liveScorePlayerFirstCourse1[currentHoleIndex].toString()
                player_2_score_5.text = liveScorePlayerSecondCourse1[currentHoleIndex].toString()
                player_3_score_5.text = liveScorePlayerThirdCourse1[currentHoleIndex].toString()
                player_4_score_5.text = liveScorePlayerFirstCourse1[currentHoleIndex].toString()
            }
            6, 15 -> apply {
                player_1_score_6.text = liveScorePlayerFirstCourse1[currentHoleIndex].toString()
                player_2_score_6.text = liveScorePlayerSecondCourse1[currentHoleIndex].toString()
                player_3_score_6.text = liveScorePlayerThirdCourse1[currentHoleIndex].toString()
                player_4_score_6.text = liveScorePlayerFourthCourse1[currentHoleIndex].toString()
            }
            7, 17 -> apply {
                player_1_score_7.text = liveScorePlayerFirstCourse1[currentHoleIndex].toString()
                player_2_score_7.text = liveScorePlayerSecondCourse1[currentHoleIndex].toString()
                player_3_score_7.text = liveScorePlayerThirdCourse1[currentHoleIndex].toString()
                player_4_score_7.text = liveScorePlayerFourthCourse1[currentHoleIndex].toString()
            }
            else-> apply{
                player_1_score_8.text = liveScorePlayerFirstCourse1[currentHoleIndex].toString()
                player_2_score_8.text = liveScorePlayerSecondCourse1[currentHoleIndex].toString()
                player_3_score_8.text = liveScorePlayerThirdCourse1[currentHoleIndex].toString()
                player_4_score_8.text = liveScorePlayerFourthCourse1[currentHoleIndex].toString()
            }
        }
    }

    private fun moveToNextCourse() {
        // TODO : 다음 코스로 이동
    }

    private fun resetCounters() {
        playRoundCurrentHole_textView.text = (currentHoleIndex + 1).toString()
        playRoundPlayerLiveScore_textView1.text =
            liveScorePlayerFirstCourse1[currentHoleIndex].toString()
        playRoundPlayerLiveScore_textView2.text =
            liveScorePlayerSecondCourse1[currentHoleIndex].toString()
        playRoundPlayerLiveScore_textView3.text =
            liveScorePlayerThirdCourse1[currentHoleIndex].toString()
        playRoundPlayerLiveScore_textView4.text =
            liveScorePlayerFourthCourse1[currentHoleIndex].toString()
    }

    private fun getClubName() {
        // TODO : LOCAL에서 찾기 또는 FIRESTORE에서 찾기
        Log.d(TAG, "getClubName: start ")
        db.collection(COLLECTION_PATH_CLUBS)
            .whereEqualTo("clubId", currentRound.roundClubId)
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    currentClubName = task.result!!.documents[0].get("clubName") as String
                    Log.d(TAG, "getClubName: $currentClubName ")
                    playRoundClubName_textView.text = currentClubName
                    getCourseSets()
                }
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
                    currentCourseFirstParList = it.result!!.documents[0].get("courseParCount") as ArrayList<Int>
                    Log.d(TAG, "getCourseNames: first is $currentFirstCourseName")
                    Log.d(TAG, "getCourseSets: par => $currentCourseFirstParList ")
                    currentCourseParList = currentCourseFirstParList
                    currentCourseName = currentFirstCourseName
                    playRoundCourseName_textView.text = currentCourseName
                    setPars(0)
                }
            }

        db.collection(COLLECTION_PATH_COURSES)
            .whereEqualTo("courseId", currentRound.roundCourseIdList[1])
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "getCourseSets - 2: success!! ")
            }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    currentSecondCourseName = it.result!!.documents[0].get("courseName") as String
                    currentCourseSecondParList = it.result!!.documents[0].get("courseParCount") as ArrayList<Int>
                    Log.d(TAG, "getCourseNames: second is $currentSecondCourseName")
                    Log.d(TAG, "getCourseSets: par => $currentCourseSecondParList ")
                    Log.d(TAG, "currentCourseParList: $currentCourseParList ")
                    setRoundOwnerId(currentRound.roundOwnerUserId)
                    getPlayers(currentPlayersSize)
                }
            }
    }

    private fun getPlayers(playersSize : Int) {
        Log.d(TAG, "getPlayers: start, total : $playersSize players ")
        db.collection(COLLECTION_PATH_USERS)
            .whereEqualTo("userId", currentRound.roundPlayerIdList[0])
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "getPlayers: p1 -> success ")
            }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    currentFirstPlayerName = it.result!!.documents[0].get("userNickname") as String
                    Log.d(TAG, "getPlayers: p1 -> $currentFirstPlayerName")
                    playName_1.text = currentFirstPlayerName
                    liveScorePlayerName_textview1.text = currentFirstPlayerName
                }
            }

        db.collection(COLLECTION_PATH_USERS)
            .whereEqualTo("userId", currentRound.roundPlayerIdList[1])
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "getPlayers: p2 -> success ")
            }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    currentSecondPlayerName = it.result!!.documents[0].get("userNickname") as String
                    Log.d(TAG, "getPlayers: p2 -> $currentSecondPlayerName")
                    playName_2.text = currentSecondPlayerName
                    liveScorePlayerName_textview2.text = currentSecondPlayerName
                }
            }

        db.collection(COLLECTION_PATH_USERS)
            .whereEqualTo("userId", currentRound.roundPlayerIdList[2])
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "getPlayers: p3 -> success ")
            }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    currentThirdPlayerName = it.result!!.documents[0].get("userNickname") as String
                    Log.d(TAG, "getPlayers: p3 -> $currentThirdPlayerName")
                    playName_3.text = currentThirdPlayerName
                    liveScorePlayerName_textview3.text = currentThirdPlayerName
                }
            }

        db.collection(COLLECTION_PATH_USERS)
            .whereEqualTo("userId", currentRound.roundPlayerIdList[3])
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "getPlayers: p4 -> success ")
            }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    currentFourthPlayerName = it.result!!.documents[0].get("userNickname") as String
                    Log.d(TAG, "getPlayers: p4 -> $currentFourthPlayerName")
                    playName_4.text = currentFourthPlayerName
                    liveScorePlayerName_textview4.text = currentFourthPlayerName
                }
            }
    }

    private fun addLiveScore(playerPosition: Int) {
        val currentParMaxHit: Int = currentCourseParList[currentHoleIndex] * 2
        val currentHit = when (playerPosition) {
            0 -> liveScorePlayerFirstCourse1[currentHoleIndex]
            1 -> liveScorePlayerSecondCourse1[currentHoleIndex]
            2 -> liveScorePlayerThirdCourse1[currentHoleIndex]
            else -> liveScorePlayerFourthCourse1[currentHoleIndex]
        }
        if (currentHit < currentParMaxHit) {
            when (playerPosition) {
                0 -> apply {
                    playRoundPlayerLiveScore_textView1.text = (currentHit + 1).toString()
                    liveScorePlayerFirstCourse1[currentHoleIndex] =
                        liveScorePlayerFirstCourse1[currentHoleIndex] + 1
                    Log.d(TAG, "liveScorePlayerFirstCourse1: -> $liveScorePlayerFirstCourse1")
                    addToScoreBoard(0, currentHoleIndex)
                    updateTotalHit(0)
                    // TODO : 서버에 해당 홀과 플레이어를 감지해서 바꾸기, 아마도 배포 카운터
                }
                1 -> apply {
                    playRoundPlayerLiveScore_textView2.text = (currentHit + 1).toString()
                    liveScorePlayerSecondCourse1[currentHoleIndex] =
                        liveScorePlayerSecondCourse1[currentHoleIndex] + 1
                    Log.d(TAG, "liveScorePlayerSecondCourse1: -> $liveScorePlayerSecondCourse1")
                    addToScoreBoard(1, currentHoleIndex)
                    updateTotalHit(1)
                }
                2 -> apply {
                    playRoundPlayerLiveScore_textView3.text = (currentHit + 1).toString()
                    liveScorePlayerThirdCourse1[currentHoleIndex] =
                        liveScorePlayerThirdCourse1[currentHoleIndex] + 1
                    Log.d(TAG, "liveScorePlayerThirdCourse1: -> $liveScorePlayerThirdCourse1")
                    addToScoreBoard(2, currentHoleIndex)
                    updateTotalHit(2)
                }
                else -> apply {
                    playRoundPlayerLiveScore_textView4.text = (currentHit + 1).toString()
                    liveScorePlayerFourthCourse1[currentHoleIndex] =
                        liveScorePlayerFourthCourse1[currentHoleIndex] + 1
                    Log.d(TAG, "liveScorePlayerFourthCourse1: -> $liveScorePlayerFourthCourse1")
                    addToScoreBoard(3, currentHoleIndex)
                    updateTotalHit(3)
                }
            }
        } else {
            // TODO : 플레이어 이름을 감지하여 다른 에러 메세지 보내주기
            Snackbar.make(playRound_layout, "응 양파", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun updateTotalHit(playerPosition: Int) {
        when(playerPosition) {
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
    }

    private fun addToScoreBoard(playerPosition: Int, currentHoleIndex : Int) {
        when(playerPosition) {
            0 -> apply {
                when(currentHoleIndex) {
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
            1-> apply {
                when(currentHoleIndex) {
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
            2-> apply {
                when(currentHoleIndex) {
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
                when(currentHoleIndex) {
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
        val currentHit = when (playerPosition) {
            0 -> liveScorePlayerFirstCourse1[currentHoleIndex]
            1 -> liveScorePlayerSecondCourse1[currentHoleIndex]
            2 -> liveScorePlayerThirdCourse1[currentHoleIndex]
            else -> liveScorePlayerFourthCourse1[currentHoleIndex]
        }
        if (currentHit > 0) {
            when (playerPosition) {
                0 -> apply {
                    playRoundPlayerLiveScore_textView1.text = (currentHit - 1).toString()
                    liveScorePlayerFirstCourse1[currentHoleIndex] =
                        liveScorePlayerFirstCourse1[currentHoleIndex] - 1
                    // TODO : 스코어 보드에서 빼기
                    removeToScoreBoard(0)
                    updateTotalHit(0)
                    // TODO : 서버에 해당 홀과 플레이어를 감지해서 바꾸기
                }
                1 -> apply {
                    playRoundPlayerLiveScore_textView2.text = (currentHit - 1).toString()
                    liveScorePlayerSecondCourse1[currentHoleIndex] =
                        liveScorePlayerSecondCourse1[currentHoleIndex] - 1
                    removeToScoreBoard(1)
                    updateTotalHit(1)
                }
                2 -> apply {
                    playRoundPlayerLiveScore_textView3.text = (currentHit - 1).toString()
                    liveScorePlayerThirdCourse1[currentHoleIndex] =
                        liveScorePlayerThirdCourse1[currentHoleIndex] - 1
                    removeToScoreBoard(2)
                    updateTotalHit(2)
                }
                else -> apply {
                    playRoundPlayerLiveScore_textView4.text = (currentHit - 1).toString()
                    liveScorePlayerFourthCourse1[currentHoleIndex] =
                        liveScorePlayerFourthCourse1[currentHoleIndex] - 1
                    removeToScoreBoard(3)
                    updateTotalHit(3)
                }
            }
        } else {
            Snackbar.make(playRound_layout, "응 음수", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun removeToScoreBoard(playerPosition: Int) {
        when(playerPosition) {
            0 -> apply {
                when(currentHoleIndex) {
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
            1-> apply {
                when(currentHoleIndex) {
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
            2-> apply {
                when(currentHoleIndex) {
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
                when(currentHoleIndex) {
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

}