package com.vlaksuga.rounding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
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

    private lateinit var currentRound : Round
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
    private var currentCourseName = ""
    private var currentFirstPlayerScoreSet = arrayListOf<Int>()
    private var currentSecondPlayerScoreSet = arrayListOf<Int>()
    private var currentThirdPlayerScoreSet = arrayListOf<Int>()
    private var currentFourthPlayerScoreSet = arrayListOf<Int>()
    private var roundOwnerUserId = ""



    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_round)

        // DUMMY INIT //
        initDummy()

        // GET DB FROM FIREBASE //
        getRoundFromFireBase()

        // SET ROUND OWNER ID //


        // SET SCORE LIST//
        setScoreList(currentCourseIndex)


        currentCourseName = currentFirstCourseName
        val currentHoleNumber = if (currentHoleIndex > 8) {
            currentHoleIndex - 8
        } else {
            currentHoleIndex + 1
        }


        // FAB //
        playRoundScoreAdd_fab1.setOnClickListener { addLiveScore(0) }
        playRoundScoreAdd_fab2.setOnClickListener { addLiveScore(1) }
        playRoundScoreAdd_fab3.setOnClickListener { addLiveScore(2) }
        playRoundScoreAdd_fab4.setOnClickListener { addLiveScore(3) }

        playRoundScoreRemove_fab1.setOnClickListener { removeLiveScore(0) }
        playRoundScoreRemove_fab2.setOnClickListener { removeLiveScore(1) }
        playRoundScoreRemove_fab3.setOnClickListener { removeLiveScore(2) }
        playRoundScoreRemove_fab4.setOnClickListener { removeLiveScore(3) }
    }

    private fun setScoreList(courseIndex : Int) {
        currentFirstPlayerScoreSet = when (courseIndex) {
            0 -> currentRound.playerFirstScoreFirstList as ArrayList<Int>
            else -> currentRound.playerFirstScoreSecondList as ArrayList<Int>
        }
        currentSecondPlayerScoreSet = when (courseIndex) {
            0 -> currentRound.playerSecondScoreFirstList as ArrayList<Int>
            else -> currentRound.playerSecondScoreSecondList as ArrayList<Int>
        }
        currentThirdPlayerScoreSet = when (courseIndex) {
            0 -> currentRound.playerThirdScoreFirstList as ArrayList<Int>
            else -> currentRound.playerThirdScoreSecondList as ArrayList<Int>
        }
        currentFourthPlayerScoreSet = when (courseIndex) {
            0 -> currentRound.playerFourthScoreFirstList as ArrayList<Int>
            else -> currentRound.playerFourthScoreSecondList as ArrayList<Int>
        }
    }

    private fun setRoundOwnerId(ownerId : String) {
        this.roundOwnerUserId = ownerId
        Log.d(TAG, "RoundOwnerId: $roundOwnerUserId")
    }

    private fun setPars(currentCourseIndex : Int) {
        when(currentCourseIndex) {
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
            }
            else -> apply {
                Log.d(TAG, "setPars: set 2 ")

            }
        }

    }

    private fun setPlayerScore(playerPosition: Int) {
        when(playerPosition) {
            0 -> apply {
                Log.d(TAG, "setPlayerScore: 0 -> start ")
                player_1_score_0.text = currentFirstPlayerScoreSet[0].toString()
                player_1_score_1.text = currentFirstPlayerScoreSet[1].toString()
                player_1_score_2.text = currentFirstPlayerScoreSet[2].toString()
                player_1_score_3.text = currentFirstPlayerScoreSet[3].toString()
                player_1_score_4.text = currentFirstPlayerScoreSet[4].toString()
                player_1_score_5.text = currentFirstPlayerScoreSet[5].toString()
                player_1_score_6.text = currentFirstPlayerScoreSet[6].toString()
                player_1_score_7.text = currentFirstPlayerScoreSet[7].toString()
                player_1_score_8.text = currentFirstPlayerScoreSet[8].toString()
                player_1_score_total.text = currentFirstPlayerScoreSet.sum().toString()
            }
            1 -> apply {
                Log.d(TAG, "setPlayerScore: 1 -> start ")
                player_2_score_0.text = currentSecondPlayerScoreSet[0].toString()
                player_2_score_1.text = currentSecondPlayerScoreSet[1].toString()
                player_2_score_2.text = currentSecondPlayerScoreSet[2].toString()
                player_2_score_3.text = currentSecondPlayerScoreSet[3].toString()
                player_2_score_4.text = currentSecondPlayerScoreSet[4].toString()
                player_2_score_5.text = currentSecondPlayerScoreSet[5].toString()
                player_2_score_6.text = currentSecondPlayerScoreSet[6].toString()
                player_2_score_7.text = currentSecondPlayerScoreSet[7].toString()
                player_2_score_8.text = currentSecondPlayerScoreSet[8].toString()
                player_2_score_total.text = currentSecondPlayerScoreSet.sum().toString()
            }
            2 -> apply {
                Log.d(TAG, "setPlayerScore: 2 -> start ")
                player_3_score_0.text = currentThirdPlayerScoreSet[0].toString()
                player_3_score_1.text = currentThirdPlayerScoreSet[1].toString()
                player_3_score_2.text = currentThirdPlayerScoreSet[2].toString()
                player_3_score_3.text = currentThirdPlayerScoreSet[3].toString()
                player_3_score_4.text = currentThirdPlayerScoreSet[4].toString()
                player_3_score_5.text = currentThirdPlayerScoreSet[5].toString()
                player_3_score_6.text = currentThirdPlayerScoreSet[6].toString()
                player_3_score_7.text = currentThirdPlayerScoreSet[7].toString()
                player_3_score_8.text = currentThirdPlayerScoreSet[8].toString()
                player_3_score_total.text = currentThirdPlayerScoreSet.sum().toString()
            }
            else -> apply {
                Log.d(TAG, "setPlayerScore: 3 -> start ")
                player_4_score_0.text = currentFourthPlayerScoreSet[0].toString()
                player_4_score_1.text = currentFourthPlayerScoreSet[1].toString()
                player_4_score_2.text = currentFourthPlayerScoreSet[2].toString()
                player_4_score_3.text = currentFourthPlayerScoreSet[3].toString()
                player_4_score_4.text = currentFourthPlayerScoreSet[4].toString()
                player_4_score_5.text = currentFourthPlayerScoreSet[5].toString()
                player_4_score_6.text = currentFourthPlayerScoreSet[6].toString()
                player_4_score_7.text = currentFourthPlayerScoreSet[7].toString()
                player_4_score_8.text = currentFourthPlayerScoreSet[8].toString()
                player_4_score_total.text = currentFourthPlayerScoreSet.sum().toString()
            }
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
            arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
            false
        )
    }

    private fun getRoundFromFireBase() {
        db.collection(COLLECTION_PATH_ROUNDS)
            .whereEqualTo("roundId", "7801d136-dc79-4e69-a24c-915a47d5d059")
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "addOnSuccessListener: Success :) ")
            }
            .addOnFailureListener {
                Log.d(TAG, "addOnFailureListener: Fail => $it ")
            }
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if(task.isComplete) {
                    currentRound = task.result!!.toObjects(Round::class.java)[0]
                    Log.d(TAG, "currentRound: $currentRound")
                    getClubName()
                    setNewHole()
                    setPlayerScore(0)
                    setPlayerScore(1)
                    setPlayerScore(2)
                    setPlayerScore(3)
                }
            }
    }

    private fun setNewHole() {
        Log.d(TAG, "setNewHole: set! ")
        playRoundCurrentHole_textView.text = (currentHoleIndex + 1).toString()
        playRoundPlayerLiveScore_textView1.text = currentFirstPlayerScoreSet[currentHoleIndex].toString()
        playRoundPlayerLiveScore_textView2.text = currentSecondPlayerScoreSet[currentHoleIndex].toString()
        playRoundPlayerLiveScore_textView3.text = currentThirdPlayerScoreSet[currentHoleIndex].toString()
        playRoundPlayerLiveScore_textView4.text = currentFourthPlayerScoreSet[currentHoleIndex].toString()
        Log.d(TAG, "current hole: ${currentHoleIndex+1}")
        Log.d(TAG, "setNewHole: p1 init -> ${currentFirstPlayerScoreSet[currentHoleIndex]} ")
        Log.d(TAG, "setNewHole: p2 init -> ${currentSecondPlayerScoreSet[currentHoleIndex]} ")
        Log.d(TAG, "setNewHole: p3 init -> ${currentThirdPlayerScoreSet[currentHoleIndex]} ")
        Log.d(TAG, "setNewHole: p4 init -> ${currentFourthPlayerScoreSet[currentHoleIndex]} ")
    }

    private fun getClubName() {
        // TODO : LOCAL에서 찾기 또는 FIRESTORE에서 찾기
        Log.d(TAG, "getClubName: start ")
        db.collection(COLLECTION_PATH_CLUBS)
            .whereEqualTo("clubId", currentRound.roundClubId)
            .get()
            .addOnCompleteListener {task: Task<QuerySnapshot> ->
                if(task.isSuccessful) {
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
                if(it.isSuccessful) {
                    currentFirstCourseName = it.result!!.documents[0].get("courseName") as String
                    currentCourseFirstParList = it.result!!.documents[0].get("courseParCount") as ArrayList<Int>
                    Log.d(TAG, "getCourseNames: first is $currentFirstCourseName")
                    Log.d(TAG, "getCourseSets: par => $currentCourseFirstParList ")
                    currentCourseParList = currentCourseFirstParList
                    playRoundCourseName_textView.text = currentFirstCourseName
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
                if(it.isSuccessful) {
                    currentSecondCourseName = it.result!!.documents[0].get("courseName") as String
                    currentCourseSecondParList = it.result!!.documents[0].get("courseParCount") as ArrayList<Int>
                        Log.d(TAG, "getCourseNames: second is $currentSecondCourseName")
                        Log.d(TAG, "getCourseSets: par => $currentCourseSecondParList ")

                    Log.d(TAG, "currentCourseParList: $currentCourseParList ")
                    setRoundOwnerId(currentRound.roundOwnerUserId)
                    getPlayers()
                }
            }
    }

    private fun getPlayers() {
        Log.d(TAG, "getPlayers: start ")
        db.collection(COLLECTION_PATH_USERS)
            .whereEqualTo("userId", currentRound.roundPlayerIdList[0])
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "getPlayers: p1 -> success ")
            }
            .addOnCompleteListener {
                if(it.isSuccessful) {
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
                if(it.isSuccessful) {
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
                if(it.isSuccessful) {
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
                if(it.isSuccessful) {
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
            0 -> currentFirstPlayerScoreSet[currentHoleIndex]
            1 -> currentSecondPlayerScoreSet[currentHoleIndex]
            2 -> currentThirdPlayerScoreSet[currentHoleIndex]
            else -> currentFourthPlayerScoreSet[currentHoleIndex]
        }
        if (currentHit < currentParMaxHit) {
            when (playerPosition) {
                0 -> apply {
                    playRoundPlayerLiveScore_textView1.text = (currentHit + 1).toString()
                    currentFirstPlayerScoreSet[currentHoleIndex] =
                        currentFirstPlayerScoreSet[currentHoleIndex] + 1
                    // TODO : 서버에 해당 홀과 플레이어를 감지해서 바꾸기
                }
                1 -> apply {
                    playRoundPlayerLiveScore_textView2.text = (currentHit + 1).toString()
                    currentSecondPlayerScoreSet[currentHoleIndex] =
                        currentSecondPlayerScoreSet[currentHoleIndex] + 1
                }
                2 -> apply {
                    playRoundPlayerLiveScore_textView3.text = (currentHit + 1).toString()
                    currentThirdPlayerScoreSet[currentHoleIndex] =
                        currentThirdPlayerScoreSet[currentHoleIndex] + 1
                }
                else -> apply {
                    playRoundPlayerLiveScore_textView4.text = (currentHit + 1).toString()
                    currentFourthPlayerScoreSet[currentHoleIndex] =
                        currentFourthPlayerScoreSet[currentHoleIndex] + 1
                }
            }
        } else {

            // TODO : 플레이어 이름을 감지하여 다른 에러 메세지 보내주기
            Snackbar.make(playRound_layout, "응 양파", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun removeLiveScore(playerPosition: Int) {
        val currentHit = when (playerPosition) {
            0 -> currentFirstPlayerScoreSet[currentHoleIndex]
            1 -> currentSecondPlayerScoreSet[currentHoleIndex]
            2 -> currentThirdPlayerScoreSet[currentHoleIndex]
            else -> currentFourthPlayerScoreSet[currentHoleIndex]
        }
        if (currentHit > 0) {
            when (playerPosition) {
                0 -> apply {
                    playRoundPlayerLiveScore_textView1.text = (currentHit - 1).toString()
                    currentFirstPlayerScoreSet[currentHoleIndex] =
                        currentFirstPlayerScoreSet[currentHoleIndex] - 1
                    // TODO : 서버에 해당 홀과 플레이어를 감지해서 바꾸기
                }
                1 -> apply {
                    playRoundPlayerLiveScore_textView2.text = (currentHit - 1).toString()
                    currentSecondPlayerScoreSet[currentHoleIndex] =
                        currentSecondPlayerScoreSet[currentHoleIndex] - 1
                }
                2 -> apply {
                    playRoundPlayerLiveScore_textView3.text = (currentHit - 1).toString()
                    currentThirdPlayerScoreSet[currentHoleIndex] =
                        currentThirdPlayerScoreSet[currentHoleIndex] - 1
                }
                else -> apply {
                    playRoundPlayerLiveScore_textView4.text = (currentHit - 1).toString()
                    currentFourthPlayerScoreSet[currentHoleIndex] =
                        currentFourthPlayerScoreSet[currentHoleIndex] - 1
                }
            }
        } else {
            // TODO : 플레이어 이름을 감지하여 다른 에러 메세지 보내주기
            Snackbar.make(playRound_layout, "응 음수", Snackbar.LENGTH_SHORT).show()
        }
    }
}