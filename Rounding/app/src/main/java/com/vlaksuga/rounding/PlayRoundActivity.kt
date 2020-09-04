package com.vlaksuga.rounding

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.vlaksuga.rounding.model.Round
import kotlinx.android.synthetic.main.activity_play_round.*


class PlayRoundActivity : AppCompatActivity() {

    companion object {
        const val TAG = "PlayRoundActivity"
        const val KEY_DOCUMENT_PATH = "com.vlaksuga.rounding.KEY_DOCUMENT_PATH"
        const val KEY_ROUND_ID = "com.vlaksuga.rounding.KEY_ROUND_ID"
        const val KEY_ROUND_OWNER = "com.vlaksuga.rounding.KEY_ROUND_OWNER"
        const val KEY_DATE = "com.vlaksuga.rounding.KEY_DATE"
        const val KEY_SEASON = "com.vlaksuga.rounding.KEY_SEASON"
        const val KEY_TEE_TIME = "com.vlaksuga.rounding.KEY_TEE_TIME"
        const val KEY_CLUB_ID = "com.vlaksuga.rounding.KEY_CLUB_ID"
        const val KEY_CLUB_NAME = "com.vlaksuga.rounding.KEY_CLUB_NAME"
        const val KEY_COURSE_ID_LIST = "com.vlaksuga.rounding.KEY_COURSE_ID_LIST"
        const val KEY_COURSE_NAME_LIST = "com.vlaksuga.rounding.KEY_COURSE_NAME_LIST"
        const val KEY_FIRST_COURSE_PAR_LIST = "com.vlaksuga.rounding.KEY_FIRST_COURSE_PAR_LIST"
        const val KEY_SECOND_COURSE_PAR_LIST = "com.vlaksuga.rounding.KEY_SECOND_COURSE_PAR_LIST"
        const val KEY_PLAYER_EMAIL_LIST = "com.vlaksuga.rounding.KEY_PLAYER_EMAIL_LIST"
        const val KEY_PLAYER_NICKNAME_LIST = "com.vlaksuga.rounding.KEY_PLAYER_NICKNAME_LIST"

        const val COLLECTION_PATH_ROUNDS = "rounds"
        const val COLLECTION_PATH_COURSES = "courses"
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
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var userEmail: String
    private var userTeeType = ""
    private lateinit var currentRound : Round
    private var roundOwner = ""
    private var documentPath = ""
    private lateinit var currentRoundId: String
    private var currentDate: Long = 0
    private var currentSeason: Int = 0
    private var currentTeeTime: Long = 0
    private lateinit var currentClubId: String
    private lateinit var currentClubName: String
    private var currentCourseIdList = arrayListOf<String>()
    private var currentCourseNameList = arrayListOf<String>()
    private var currentRoundPlayerEmailList = arrayListOf<String>()
    private var currentRoundPlayerNicknameList = arrayListOf<String>()

    private var currentHoleIndex = 0
    private var currentCourseParList = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    private var currentCourseFirstParList = arrayListOf<Int>()
    private var currentCourseSecondParList = arrayListOf<Int>()
    private var currentCourseFirstTeeList = arrayListOf<Int>()
    private var currentCourseSecondTeeList = arrayListOf<Int>()

    private var liveScorePlayerFirstCourse1 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerFirstCourse2 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerSecondCourse1 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerSecondCourse2 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerThirdCourse1 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerThirdCourse2 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerFourthCourse1 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var liveScorePlayerFourthCourse2 = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_round)

        // USER //
        auth = Firebase.auth
        userEmail = auth.currentUser!!.email!!
        Log.d(TAG, "USER : SET_OK")
        decoUserTeeType()


        // TOOLBAR //
        val toolbar = findViewById<Toolbar>(R.id.playRound_toolbar)
        setSupportActionBar(toolbar)
        playRoundCloseRound_imageView.setOnClickListener {
            closeThisRound()
        }
        playRoundCloseIcon_imageView.setOnClickListener {
            super.onBackPressed()
        }

        // GET ROUND ID BY INTENT //
        currentRoundId = intent.getStringExtra(RoundResultActivity.DOCUMENT_ID)!!
        Log.d(TAG, "ROUND ID INTENT : SET_OK")

        // GET DB FROM FIREBASE //
        getRound(currentRoundId)

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
        commentNext_textView.setOnClickListener {
            when (currentHoleIndex) {
                8 -> apply {
                    if (currentCourseIdList.size == 1) {
                        // ROUND ENDS HERE //
                        checkRoundResult()
                    } else {
                        // TO NEXT COURSE //
                        moveToNextCourse()
                    }
                }
                17 -> apply {
                    // ROUND ENDS HERE //
                    checkRoundResult()
                }
                else -> apply {
                    currentHoleIndex += 1
                    setHoleScore(currentHoleIndex)
                    Log.d(TAG, "toNextHole_button: currentHoleIndex -> $currentHoleIndex")
                }
            }
        }
        toNextHole_button.setOnClickListener {
            when (currentHoleIndex) {
                8 -> apply {
                    if (currentCourseIdList.size == 1) {
                        // ROUND ENDS HERE //
                        checkRoundResult()
                    } else {
                        // TO NEXT COURSE //
                        moveToNextCourse()
                    }
                }
                17 -> apply {
                    // ROUND ENDS HERE //
                    checkRoundResult()
                }
                else -> apply {
                    currentHoleIndex += 1
                    setHoleScore(currentHoleIndex)
                    Log.d(TAG, "toNextHole_button: currentHoleIndex -> $currentHoleIndex")
                }
            }
        }

        // PRE HOLE //
        commentPre_textView.setOnClickListener {
            if (currentHoleIndex == 9) {
                moveToPreCourse()
            } else {
                currentHoleIndex -= 1
                setHoleScore(currentHoleIndex)
                Log.d(TAG, "toPreHole_button: currentHoleIndex -> $currentHoleIndex")
            }
        }
        toPreHole_button.setOnClickListener {
            if (currentHoleIndex == 9) {
                moveToPreCourse()
            } else {
                currentHoleIndex -= 1
                setHoleScore(currentHoleIndex)
                Log.d(TAG, "toPreHole_button: currentHoleIndex -> $currentHoleIndex")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getRound(roundId: String) {
        Log.d(TAG, "getRound: invoke")
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
                    Log.d(TAG, "currentRound: $currentRound")
                    Log.d(TAG, "ROUND : SET_OK")

                    // CHECK ROUND OWNER //
                    if(currentRound.roundOwner == userEmail) {
                        playRoundCloseRound_imageView.visibility = View.VISIBLE
                    }

                    // SET CURRENT DOCUMENT PATH //
                    documentPath = task.result!!.documents[0].id
                    Log.d(TAG, "getRound: documentPath -> $documentPath")

                    // SET CURRENT DATE //
                    currentDate = currentRound.roundDate
                    currentSeason = currentRound.roundSeason
                    currentTeeTime = currentRound.roundTeeTime
                    Log.d(TAG, "getRound: currentDate -> $currentDate ")

                    // SET CURRENT CLUB //
                    currentClubName = currentRound.roundClubName
                    currentClubId = currentRound.roundClubId
                    playRoundClubName_textView.text = currentClubName
                    Log.d(TAG, "getRound: currentClubName -> $currentClubName")
                    Log.d(TAG, "getRound: currentClubId -> $currentClubId")

                    // SET CURRENT COURSE //
                    currentCourseNameList = currentRound.roundCourseNameList as ArrayList<String>
                    currentCourseIdList = currentRound.roundCourseIdList as ArrayList<String>
                    Log.d(
                        TAG,
                        "getRound: currentCourseNameList -> $currentCourseNameList"
                    )
                    Log.d(TAG, "getRound: currentCourseIdList -> $currentCourseIdList")

                    // SET CURRENT ROUND PLAYERS //
                    currentRoundPlayerEmailList =
                        currentRound.roundPlayerEmailList as ArrayList<String>
                    currentRoundPlayerNicknameList =
                        currentRound.roundPlayerNicknameList as ArrayList<String>
                    Log.d(TAG, "currentRoundPlayerEmailList: $currentRoundPlayerEmailList")
                    Log.d(TAG, "currentRoundPlayerNicknameList: $currentRoundPlayerNicknameList")

                    // USER POSITION DECO //
                    decoUserPosition(currentRoundPlayerEmailList.indexOf(userEmail))

                    // BOOLEAN DOESN'T CAST //
                    val liveScore: Boolean =
                        task.result!!.documents[0].get("isLiveScoreCreated") as Boolean
                    Log.d(TAG, "getRound: liveScore => $liveScore")

                    // SET LIVE SCORE COLLECTION IF NOT CREATED //
                    if (!liveScore) {
                        for (playerEmail in currentRound.roundPlayerEmailList) {
                            createLiveScoreCollectionSet("rounds/$documentPath/liveScore/$playerEmail")
                            Log.d(
                                TAG,
                                "createScoreCollectionSet: liveScore Collection for $playerEmail has created!"
                            )
                            this.currentHoleIndex = 0
                            Log.d(TAG, "HOLE INDEX : SET_OK")

                            // SYNC LIVE SCORE //
                            for (position in 0 until currentRoundPlayerEmailList.size) {
                                snapLiveScore(currentRoundPlayerEmailList[position], position)
                                Log.d(TAG, "getRound: syncLiveScore sync -> $position")
                            }
                            Log.d(TAG, "getRound: done -> init")
                        }
                        getCourseSets()
                    } else {
                        getCurrentHoleIndex()
                        Log.d(TAG, "getRound: done -> revisited")
                    }
                }
            }
    }

    private fun getCurrentHoleIndex() {
        Log.d(TAG, "getCurrentHoleIndex: invoke ")

        // GET CURRENT HOLE INDEX FROM DB //
        db.document("rounds/$documentPath/liveScore/$userEmail")
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "getCurrentHoleIndex: success ")
            }
            .addOnFailureListener {
                Log.d(TAG, "getCurrentHoleIndex: fail ")
            }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    this.currentHoleIndex = (it.result!!.get("currentHole") as Long).toInt()
                    Log.d(TAG, "HOLE INDEX : SET_OK")
                    Log.d(TAG, "getCurrentHoleIndex: currentHoleUpdated -> $currentHoleIndex")
                    // SYNC LIVE SCORE //
                    for (position in 0 until currentRoundPlayerEmailList.size) {
                        snapLiveScore(currentRoundPlayerEmailList[position], position)
                        Log.d(TAG, "getRoundFromFireBase: syncLiveScore sync -> $position ")
                    }
                    Log.d(TAG, "getCurrentHoleIndex: done")
                    getCourseSets()
                }
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getCourseSets() {
        Log.d(TAG, "getCourseSets: invoke")
        if(currentRound.roundCourseIdList.size == 1) {
            db.collection(COLLECTION_PATH_COURSES)
                .whereEqualTo("courseId", currentRound.roundCourseIdList[0])
                .get()
                .addOnSuccessListener {
                    Log.d(TAG, "getCourseSets - FIRST COURSE : success!! ")
                }
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        currentCourseFirstParList =
                            it.result!!.documents[0].get("courseParCount") as ArrayList<Int>
                        Log.d(TAG, "getCourseSets: par first => $currentCourseFirstParList")
                        currentCourseFirstTeeList = when(userTeeType) {
                            "RED" -> it.result!!.documents[0].get("courseParLengthLady") as ArrayList<Int>
                            "WHITE" -> it.result!!.documents[0].get("courseParLengthReg") as ArrayList<Int>
                            "BLACK" -> it.result!!.documents[0].get("courseParLengthBack") as ArrayList<Int>
                            "BLUE" -> it.result!!.documents[0].get("courseParLengthChamp") as ArrayList<Int>
                            else -> arrayListOf(0,0,0,0,0,0,0,0,0)
                        }
                        Log.d(TAG, "currentCourseFirstTeeList : $currentCourseFirstTeeList")
                        Log.d(TAG, "FIRST COURSE : SET_OK")
                        Log.d(TAG, "getCourseSets: done -> first course ONLY")
                        drawCourseInfo()
                        setRoundOwnerEmail(currentRound.roundOwner)
                        drawPlayers()
                        setPars()
                        resetCounters()
                    }
                }
        } else {
            db.collection(COLLECTION_PATH_COURSES)
                .whereEqualTo("courseId", currentRound.roundCourseIdList[0])
                .get()
                .addOnSuccessListener {
                    Log.d(TAG, "getCourseSets - FIRST COURSE : success!! ")
                }
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        currentCourseFirstParList =
                            it.result!!.documents[0].get("courseParCount") as ArrayList<Int>
                        Log.d(TAG, "getCourseSets: par first => $currentCourseFirstParList")
                        currentCourseFirstTeeList = when(userTeeType) {
                            "RED" -> it.result!!.documents[0].get("courseParLengthLady") as ArrayList<Int>
                            "WHITE" -> it.result!!.documents[0].get("courseParLengthReg") as ArrayList<Int>
                            "BLACK" -> it.result!!.documents[0].get("courseParLengthBack") as ArrayList<Int>
                            "BLUE" -> it.result!!.documents[0].get("courseParLengthChamp") as ArrayList<Int>
                            else -> arrayListOf(0,0,0,0,0,0,0,0,0)
                        }
                        Log.d(TAG, "currentCourseFirstTeeList : $currentCourseFirstTeeList")
                        Log.d(TAG, "FIRST COURSE : SET_OK")
                        Log.d(TAG, "getCourseSets: done -> first course ")
                        db.collection(COLLECTION_PATH_COURSES)
                            .whereEqualTo("courseId", currentRound.roundCourseIdList[1])
                            .get()
                            .addOnSuccessListener {
                                Log.d(TAG, "getCourseSets - SECOND COURSE : success!! ")
                            }
                            .addOnCompleteListener {secondTask ->
                                if (secondTask.isSuccessful) {
                                    currentCourseSecondParList =
                                        secondTask.result!!.documents[0].get("courseParCount") as ArrayList<Int>
                                    Log.d(TAG, "getCourseSets: par second => $currentCourseSecondParList")
                                    currentCourseSecondTeeList = when(userTeeType) {
                                        "RED" -> secondTask.result!!.documents[0].get("courseParLengthLady") as ArrayList<Int>
                                        "WHITE" -> secondTask.result!!.documents[0].get("courseParLengthReg") as ArrayList<Int>
                                        "BLACK" -> secondTask.result!!.documents[0].get("courseParLengthBack") as ArrayList<Int>
                                        "BLUE" -> secondTask.result!!.documents[0].get("courseParLengthChamp") as ArrayList<Int>
                                        else -> arrayListOf(0,0,0,0,0,0,0,0,0)
                                    }
                                    Log.d(TAG, "SECOND COURSE : SET_OK")
                                    Log.d(TAG, "currentCourseSecondTeeList : $currentCourseSecondTeeList")
                                    Log.d(TAG, "getCourseSets: done -> second course ")
                                    drawCourseInfo()
                                    setRoundOwnerEmail(currentRound.roundOwner)
                                    drawPlayers()
                                    setPars()
                                    resetCounters()
                                }
                            }
                    }
                }
        }

    }

    private fun decoUserTeeType() {
        Log.d(TAG, "decoUserTeeType: invoke")
        db.collection("users")
            .whereEqualTo("userEmail", userEmail)
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful) {

                    // USER TEE TYPE //
                    userTeeType = it.result!!.documents[0].get("userTeeType") as String
                    playRoundTeeType_textView.text = userTeeType
                    Log.d(TAG, "userTeeType => $userTeeType")
                    when(userTeeType) {
                        "RED" -> apply {
                            imageView_teeType_start.imageTintList = ColorStateList.valueOf(Color.parseColor("#FF8800"))
                            imageView_teeType_end.imageTintList = ColorStateList.valueOf(Color.parseColor("#FF8800"))
                        }

                        "WHITE" -> apply {
                            imageView_teeType_start.imageTintList = ColorStateList.valueOf(Color.parseColor("#EEEEEE"))
                            imageView_teeType_end.imageTintList = ColorStateList.valueOf(Color.parseColor("#EEEEEE"))
                        }

                        "BLACK" -> apply {
                            imageView_teeType_start.imageTintList = ColorStateList.valueOf(Color.parseColor("#000000"))
                            imageView_teeType_end.imageTintList = ColorStateList.valueOf(Color.parseColor("#000000"))
                        }

                        "BLUE" -> apply {
                            imageView_teeType_start.imageTintList = ColorStateList.valueOf(Color.parseColor("#20639b"))
                            imageView_teeType_end.imageTintList = ColorStateList.valueOf(Color.parseColor("#20639b"))
                        }
                        else -> apply {
                            imageView_teeType_start.imageTintList = ColorStateList.valueOf(Color.parseColor("#EEEEEE"))
                            imageView_teeType_end.imageTintList = ColorStateList.valueOf(Color.parseColor("#EEEEEE"))
                        }
                    }
                }
            }
    }

    private fun closeThisRound() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("라운드를 저장하지 않고 종료합니다.")
            setPositiveButton("확인") { _ , _ ->
                db.document("rounds/$documentPath").update("isRoundOpen", false)
                    .addOnCompleteListener {
                        if(it.isComplete) {
                            Log.d(TAG, "closeThisRound: round closed")
                            Toast.makeText(this@PlayRoundActivity, "라운드가 종료되었습니다.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@PlayRoundActivity, MainActivity::class.java))

                        }
                    }
            }
            setNegativeButton("취소") { dialog, _ ->  dialog.dismiss()}
            show()
        }
    }

    private fun decoUserPosition(index: Int) {
        Log.d(TAG, "decoUserPosition: invoke")
        when(index) {
            0 -> apply {
                playName_1.setTextColor(Color.parseColor("#FF8800"))
                player_1_score_total.setTextColor(Color.parseColor("#FF8800"))
                liveScorePlayerName_textview1.setTextColor(Color.parseColor("#FF8800"))
                playRoundPlayerLiveScore_textView1.setTextColor(Color.parseColor("#FF8800"))
            }
            1 -> apply {
                playName_2.setTextColor(Color.parseColor("#FF8800"))
                player_2_score_total.setTextColor(Color.parseColor("#FF8800"))
                liveScorePlayerName_textview2.setTextColor(Color.parseColor("#FF8800"))
                playRoundPlayerLiveScore_textView2.setTextColor(Color.parseColor("#FF8800"))
            }
            2 -> apply {
                playName_3.setTextColor(Color.parseColor("#FF8800"))
                player_3_score_total.setTextColor(Color.parseColor("#FF8800"))
                liveScorePlayerName_textview3.setTextColor(Color.parseColor("#FF8800"))
                playRoundPlayerLiveScore_textView3.setTextColor(Color.parseColor("#FF8800"))
            }
            else -> apply {
                playName_4.setTextColor(Color.parseColor("#FF8800"))
                player_4_score_total.setTextColor(Color.parseColor("#FF8800"))
                liveScorePlayerName_textview4.setTextColor(Color.parseColor("#FF8800"))
                playRoundPlayerLiveScore_textView4.setTextColor(Color.parseColor("#FF8800"))
            }
        }

    }

    private fun drawCourseInfo() {
        Log.d(TAG, "drawCourseInfo: invoke")
        if(currentHoleIndex < 9) {
            currentCourseParList = currentCourseFirstParList
            playRoundCourseName_textView.text = currentCourseNameList[0]
            currentPar_total.text = "36"
            Log.d(TAG, "DRAW COURSE : SET_OK")
        } else {
            currentCourseParList = currentCourseSecondParList
            playRoundCourseName_textView.text = currentCourseNameList[1]
            currentPar_total.text = "72"
            Log.d(TAG, "DRAW COURSE : SET_OK")
        }
    }

    private fun deployCurrentHoleToServer() {
        Log.d(TAG, "deployCurrentHoleToServer: invoke")
        db.document("rounds/$documentPath/liveScore/$userEmail")
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
        Log.d(TAG, "checkRoundResult: invoke")
        val builder = AlertDialog.Builder(this)
        builder.setMessage("라운드를 종료할까요?")
            .setPositiveButton("확인") { _, _ ->
                val resultIntent = Intent(this, SaveRoundResultActivity::class.java)
                resultIntent.putExtra(KEY_DOCUMENT_PATH, documentPath)
                resultIntent.putExtra(KEY_ROUND_ID, currentRoundId)
                resultIntent.putExtra(KEY_ROUND_OWNER, roundOwner)
                resultIntent.putExtra(KEY_DATE, currentDate)
                resultIntent.putExtra(KEY_SEASON, currentSeason)
                resultIntent.putExtra(KEY_TEE_TIME, currentTeeTime)
                resultIntent.putExtra(KEY_CLUB_ID, currentClubId)
                resultIntent.putExtra(KEY_CLUB_NAME, currentClubName)
                resultIntent.putStringArrayListExtra(KEY_COURSE_ID_LIST, currentCourseIdList)
                resultIntent.putStringArrayListExtra(KEY_COURSE_NAME_LIST, currentCourseNameList)
                resultIntent.putIntegerArrayListExtra(
                    KEY_FIRST_COURSE_PAR_LIST,
                    currentCourseFirstParList
                )
                resultIntent.putIntegerArrayListExtra(
                    KEY_SECOND_COURSE_PAR_LIST,
                    currentCourseSecondParList
                )
                resultIntent.putStringArrayListExtra(
                    KEY_PLAYER_EMAIL_LIST,
                    currentRoundPlayerEmailList
                )
                resultIntent.putStringArrayListExtra(
                    KEY_PLAYER_NICKNAME_LIST,
                    currentRoundPlayerNicknameList
                )
                Log.d(
                    TAG,
                    "checkRoundResult: currentRoundPlayerEmailList $currentRoundPlayerEmailList"
                )
                Log.d(
                    TAG,
                    "checkRoundResult: currentRoundPlayerNicknameList $currentRoundPlayerNicknameList"
                )
                startActivity(resultIntent)
            }
            .setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun snapLiveScore(playerEmail: String, position: Int) {
        Log.d(TAG, "syncLiveScore: invoke")
        db.document("rounds/$documentPath/liveScore/$playerEmail")
            .addSnapshotListener { value, _ ->
                val firstSet: ArrayList<Int> = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
                val secondSet: ArrayList<Int> = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
                firstSet[0] = (value!!.get("hole01") as Long).toInt()
                firstSet[1] = (value.get("hole02") as Long).toInt()
                firstSet[2] = (value.get("hole03") as Long).toInt()
                firstSet[3] = (value.get("hole04") as Long).toInt()
                firstSet[4] = (value.get("hole05") as Long).toInt()
                firstSet[5] = (value.get("hole06") as Long).toInt()
                firstSet[6] = (value.get("hole07") as Long).toInt()
                firstSet[7] = (value.get("hole08") as Long).toInt()
                firstSet[8] = (value.get("hole09") as Long).toInt()

                if (currentRound.roundCourseIdList.size == 2) {
                    secondSet[0] = (value.get("hole10") as Long).toInt()
                    secondSet[1] = (value.get("hole11") as Long).toInt()
                    secondSet[2] = (value.get("hole12") as Long).toInt()
                    secondSet[3] = (value.get("hole13") as Long).toInt()
                    secondSet[4] = (value.get("hole14") as Long).toInt()
                    secondSet[5] = (value.get("hole15") as Long).toInt()
                    secondSet[6] = (value.get("hole16") as Long).toInt()
                    secondSet[7] = (value.get("hole17") as Long).toInt()
                    secondSet[8] = (value.get("hole18") as Long).toInt()
                }
                when (position) {
                    0 -> apply {
                        liveScorePlayerFirstCourse1 = firstSet
                        liveScorePlayerFirstCourse2 = secondSet
                        Log.d(TAG, "LIVESCORE: p1 SET_OK")
                    }
                    1 -> apply {
                        liveScorePlayerSecondCourse1 = firstSet
                        liveScorePlayerSecondCourse2 = secondSet
                        Log.d(TAG, "LIVESCORE: p2 SET_OK")
                    }
                    2 -> apply {
                        liveScorePlayerThirdCourse1 = firstSet
                        liveScorePlayerThirdCourse2 = secondSet
                        Log.d(TAG, "LIVESCORE: p3 SET_OK")
                    }
                    else -> apply {
                        liveScorePlayerFourthCourse1 = firstSet
                        liveScorePlayerFourthCourse2 = secondSet
                        Log.d(TAG, "LIVESCORE: p4 SET_OK")
                    }
                }
                setHoleScore(currentHoleIndex)
                updateScoreBoard()
            }
    }

    private fun createLiveScoreCollectionSet(path: String) {
        Log.d(TAG, "createLiveScoreCollectionSet: invoke ")
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
            "hole18" to 0,
            "currentHole" to 0
        )

        val mySet = if (currentRound.roundCourseIdList.size == 1) {
            halfHoleSet
        } else {
            fullHoleSet
        }

        db.document(path)
            .set(mySet)
            .addOnSuccessListener {
                Log.d(TAG, "createScoreCollectionSet: success ")
            }
            .addOnFailureListener {
                Log.d(TAG, "createScoreCollectionSet: fail ")
            }
            .addOnCompleteListener {
                isLiveScoreCreated(true)
                Log.d(TAG, "createScoreCollectionSet: Create Document at $path")
            }
    }

    private fun isLiveScoreCreated(state: Boolean) {
        Log.d(TAG, "isLiveScoreCreated: invoke ")
        db.document("rounds/$documentPath")
            .update("isLiveScoreCreated", state)
            .addOnSuccessListener {
                Log.d(TAG, "isLiveScoreCreated: success ")
            }
            .addOnFailureListener {
                Log.d(TAG, "isLiveScoreCreated: fail ")
            }
    }

    private fun setRoundOwnerEmail(ownerEmail: String) {
        Log.d(TAG, "setRoundOwnerEmail: invoke")
        // SHOW ADD, REMOVE SCORE BUTTON ONLY FOR ROUND OWNER //
        this.roundOwner = ownerEmail
        if (this.roundOwner == userEmail) {
            Log.d(TAG, "setRoundOwnerEmail: YOU ARE ROUND OWNER")
            playRoundScoreAdd_fab1.visibility = View.VISIBLE
            playRoundScoreAdd_fab2.visibility = View.VISIBLE
            playRoundScoreAdd_fab3.visibility = View.VISIBLE
            playRoundScoreAdd_fab4.visibility = View.VISIBLE

            playRoundScoreRemove_fab1.visibility = View.VISIBLE
            playRoundScoreRemove_fab2.visibility = View.VISIBLE
            playRoundScoreRemove_fab3.visibility = View.VISIBLE
            playRoundScoreRemove_fab4.visibility = View.VISIBLE
            Log.d(TAG, "ROUND OWNER : SET_OK")
        } else {
            Log.d(TAG, "setRoundOwnerEmail: YOU CAN'T USE BUTTON")
            Log.d(TAG, "ROUND OWNER : SET_OK")
        }
        Log.d(TAG, "roundOwner: $roundOwner")
    }

    private fun setPars() {
        Log.d(TAG, "setPars: invoke")
        val currentParList = arrayListOf<TextView>(
            currentPar_0,
            currentPar_1,
            currentPar_2,
            currentPar_3,
            currentPar_4,
            currentPar_5,
            currentPar_6,
            currentPar_7,
            currentPar_8
        )
        if(currentHoleIndex < 9) {
            Log.d(TAG, "setPars: first course ")
            currentCourseParList  = currentCourseFirstParList
        } else {
            Log.d(TAG, "setPars: second course ")
            currentCourseParList = currentCourseSecondParList
        }
        for (i in 0..8) {
            currentParList[i].text = currentCourseParList[i].toString()
        }
        for (i in 0 until currentRoundPlayerEmailList.size) {
            updateTotalHit(i)
        }
        Log.d(TAG, "PAR : SET_OK")
    }

    private fun setHoleScore(currentHoleIndex: Int) {
        Log.d(TAG, "setHoleScore: invoke")
        resetCounters()
        decoCurrentHoleIndex(currentHoleIndex)
    }

    private fun decoCurrentHoleIndex(currentHoleIndex: Int) {
        Log.d(TAG, "decoCurrentHoleIndex: invoke")
        val decoHoleList = arrayListOf<TextView>(
            hole_1_textView,
            hole_2_textView,
            hole_3_textView,
            hole_4_textView,
            hole_5_textView,
            hole_6_textView,
            hole_7_textView,
            hole_8_textView,
            hole_9_textView
        )
        val decoParList = arrayListOf<TextView>(
            currentPar_0,
            currentPar_1,
            currentPar_2,
            currentPar_3,
            currentPar_4,
            currentPar_5,
            currentPar_6,
            currentPar_7,
            currentPar_8
        )
        val decoPlayer1ScoreList = arrayListOf<TextView>(
            player_1_score_0,
            player_1_score_1,
            player_1_score_2,
            player_1_score_3,
            player_1_score_4,
            player_1_score_5,
            player_1_score_6,
            player_1_score_7,
            player_1_score_8
        )
        val decoPlayer2ScoreList = arrayListOf<TextView>(
            player_2_score_0,
            player_2_score_1,
            player_2_score_2,
            player_2_score_3,
            player_2_score_4,
            player_2_score_5,
            player_2_score_6,
            player_2_score_7,
            player_2_score_8
        )
        val decoPlayer3ScoreList = arrayListOf<TextView>(
            player_3_score_0,
            player_3_score_1,
            player_3_score_2,
            player_3_score_3,
            player_3_score_4,
            player_3_score_5,
            player_3_score_6,
            player_3_score_7,
            player_3_score_8
        )
        val decoPlayer4ScoreList = arrayListOf<TextView>(
            player_4_score_0,
            player_4_score_1,
            player_4_score_2,
            player_4_score_3,
            player_4_score_4,
            player_4_score_5,
            player_4_score_6,
            player_4_score_7,
            player_4_score_8
        )
        for (i in 0..8) {
            decoHoleList[i].setBackgroundColor(Color.parseColor("#FFFFFF"))
            decoParList[i].setBackgroundResource(R.drawable.table_row_par_background)
            decoPlayer1ScoreList[i].setBackgroundColor(Color.parseColor("#FFFFFF"))
            decoPlayer2ScoreList[i].setBackgroundColor(Color.parseColor("#FFFFFF"))
            decoPlayer3ScoreList[i].setBackgroundColor(Color.parseColor("#FFFFFF"))
            decoPlayer4ScoreList[i].setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        when (currentHoleIndex) {
            0 -> apply {
                hole_1_textView.setBackgroundResource(R.drawable.table_row_highlight_background)
                currentPar_0.setBackgroundResource(R.drawable.table_row_par_current_background)
                player_1_score_0.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_2_score_0.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_3_score_0.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_4_score_0.setBackgroundResource(R.drawable.table_row_highlight_background)
                setButtonVisibility("first")
            }
            1, 10 -> apply {
                hole_2_textView.setBackgroundResource(R.drawable.table_row_highlight_background)
                currentPar_1.setBackgroundResource(R.drawable.table_row_par_current_background)
                player_1_score_1.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_2_score_1.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_3_score_1.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_4_score_1.setBackgroundResource(R.drawable.table_row_highlight_background)
                setButtonVisibility("normal")
            }
            2, 11 -> apply {
                hole_3_textView.setBackgroundResource(R.drawable.table_row_highlight_background)
                currentPar_2.setBackgroundResource(R.drawable.table_row_par_current_background)
                player_1_score_2.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_2_score_2.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_3_score_2.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_4_score_2.setBackgroundResource(R.drawable.table_row_highlight_background)
                setButtonVisibility("normal")
            }
            3, 12 -> apply {
                hole_4_textView.setBackgroundResource(R.drawable.table_row_highlight_background)
                currentPar_3.setBackgroundResource(R.drawable.table_row_par_current_background)
                player_1_score_3.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_2_score_3.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_3_score_3.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_4_score_3.setBackgroundResource(R.drawable.table_row_highlight_background)
                setButtonVisibility("normal")
            }
            4, 13 -> apply {
                hole_5_textView.setBackgroundResource(R.drawable.table_row_highlight_background)
                currentPar_4.setBackgroundResource(R.drawable.table_row_par_current_background)
                player_1_score_4.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_2_score_4.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_3_score_4.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_4_score_4.setBackgroundResource(R.drawable.table_row_highlight_background)
                setButtonVisibility("normal")
            }
            5, 14 -> apply {
                hole_6_textView.setBackgroundResource(R.drawable.table_row_highlight_background)
                currentPar_5.setBackgroundResource(R.drawable.table_row_par_current_background)
                player_1_score_5.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_2_score_5.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_3_score_5.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_4_score_5.setBackgroundResource(R.drawable.table_row_highlight_background)
                setButtonVisibility("normal")
            }
            6, 15 -> apply {
                hole_7_textView.setBackgroundResource(R.drawable.table_row_highlight_background)
                currentPar_6.setBackgroundResource(R.drawable.table_row_par_current_background)
                player_1_score_6.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_2_score_6.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_3_score_6.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_4_score_6.setBackgroundResource(R.drawable.table_row_highlight_background)
                setButtonVisibility("normal")
            }
            7, 16 -> apply {
                hole_8_textView.setBackgroundResource(R.drawable.table_row_highlight_background)
                currentPar_7.setBackgroundResource(R.drawable.table_row_par_current_background)
                player_1_score_7.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_2_score_7.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_3_score_7.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_4_score_7.setBackgroundResource(R.drawable.table_row_highlight_background)
                setButtonVisibility("normal")
            }

            9 -> apply {
                hole_1_textView.setBackgroundResource(R.drawable.table_row_highlight_background)
                currentPar_0.setBackgroundResource(R.drawable.table_row_par_current_background)
                player_1_score_0.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_2_score_0.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_3_score_0.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_4_score_0.setBackgroundResource(R.drawable.table_row_highlight_background)
                setButtonVisibility("preCourse")
            }
            else -> apply {
                hole_9_textView.setBackgroundResource(R.drawable.table_row_highlight_background)
                currentPar_8.setBackgroundResource(R.drawable.table_row_par_current_background)
                player_1_score_8.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_2_score_8.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_3_score_8.setBackgroundResource(R.drawable.table_row_highlight_background)
                player_4_score_8.setBackgroundResource(R.drawable.table_row_highlight_background)
                if (currentHoleIndex == 8) {
                    if (currentCourseIdList.size == 1) {
                        // ROUND ENDS HERE //
                        setButtonVisibility("roundLast")
                    } else {
                        setButtonVisibility("nextCourse")
                    }
                } else {
                    setButtonVisibility("roundLast")
                }
            }
        }
    }

    private fun setButtonVisibility(stateCode: String) {
        Log.d(TAG, "setButtonVisibility: invoke")
        when (stateCode) {
            "first" -> apply {
                toPreHole_button.visibility = View.GONE
                commentPre_textView.visibility = View.GONE
                commentNext_textView.text = "HOLE ${currentHoleIndex + 2}"
            }
            "preCourse" -> apply {
                toPreHole_button.visibility = View.VISIBLE
                commentPre_textView.visibility = View.VISIBLE
                commentPre_textView.text = "${currentCourseNameList[0]}"
                toNextHole_button.visibility = View.VISIBLE
                commentNext_textView.text = "HOLE ${currentHoleIndex - 7}"
            }
            "nextCourse" -> apply {
                toPreHole_button.visibility = View.VISIBLE
                commentPre_textView.visibility = View.VISIBLE
                commentPre_textView.text = "HOLE $currentHoleIndex"
                commentNext_textView.text = "${currentCourseNameList[1]}"
            }
            "roundLast" -> apply {
                toPreHole_button.visibility = View.VISIBLE
                commentPre_textView.visibility = View.VISIBLE
                commentPre_textView.text = if (currentHoleIndex == 8) {
                    "HOLE 8"
                } else {
                    "HOLE ${currentHoleIndex - 9}"
                }
                commentNext_textView.text = "라운드 종료"
            }
            else -> apply {
                toPreHole_button.visibility = View.VISIBLE
                commentPre_textView.visibility = View.VISIBLE
                commentPre_textView.text = if (currentHoleIndex < 8) {
                    "HOLE $currentHoleIndex"
                } else {
                    "HOLE ${currentHoleIndex - 9}"
                }
                commentNext_textView.text = if (currentHoleIndex < 8) {
                    "HOLE ${currentHoleIndex + 2}"
                } else {
                    "HOLE ${currentHoleIndex - 7}"
                }
            }
        }
        Log.d(TAG, "setButtonVisibility: state -> $stateCode")
    }

    private fun moveToNextCourse() {
        Log.d(TAG, "moveToNextCourse: invoke")
        val builder = AlertDialog.Builder(this)
        builder.setMessage("${currentCourseNameList[1]} 코스로 이동할까요?")
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
                playRoundCourseName_textView.text = currentCourseNameList[1]
                currentCourseParList = currentCourseSecondParList
                setPars()
                Log.d(TAG, "moveToNextCourse: currentCourseParList -> $currentCourseParList")
                currentPar_total.text = "72"
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun moveToPreCourse() {
        Log.d(TAG, "moveToPreCourse: invoke")
        val builder = AlertDialog.Builder(this)
        builder.setMessage("${currentCourseNameList[0]} 코스로 이동할까요?")
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
                playRoundCourseName_textView.text = currentCourseNameList[0]
                currentCourseParList = currentCourseFirstParList
                setPars()
                Log.d(TAG, "moveToPreCourse: currentCourseParList -> $currentCourseParList")
                currentPar_total.text = "36"
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateScoreBoard() {
        Log.d(TAG, "updateScoreBoard: invoke")
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
        Log.d(TAG, "resetCounters: invoke")
        if (currentHoleIndex < 9) {
            textView_par_info.text = "HOLE " + (currentHoleIndex + 1).toString()
            if(currentCourseFirstTeeList == arrayListOf<Int>()) {
                playRoundCurrentLength_textView.text = ""
            } else {
                playRoundCurrentLength_textView.text = currentCourseFirstTeeList[currentHoleIndex].toString() + " m"
            }
            playRoundCurrentHole_textView.text = "PAR " + currentCourseParList[currentHoleIndex].toString()
            playRoundPlayerLiveScore_textView1.text =
                liveScorePlayerFirstCourse1[currentHoleIndex].toString()
            playRoundPlayerLiveScore_textView2.text =
                liveScorePlayerSecondCourse1[currentHoleIndex].toString()
            playRoundPlayerLiveScore_textView3.text =
                liveScorePlayerThirdCourse1[currentHoleIndex].toString()
            playRoundPlayerLiveScore_textView4.text =
                liveScorePlayerFourthCourse1[currentHoleIndex].toString()
        } else {
            textView_par_info.text = "HOLE " + (currentHoleIndex - 8).toString()
            if(currentCourseSecondTeeList == arrayListOf<Int>()) {
                playRoundCurrentLength_textView.text = ""
            } else {
                playRoundCurrentLength_textView.text = currentCourseSecondTeeList[currentHoleIndex - 9].toString() + " m"
            }
            playRoundCurrentHole_textView.text = "PAR " + currentCourseParList[currentHoleIndex - 9].toString()
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

    private fun drawPlayers() {
        Log.d(TAG, "getPlayers: invoke")
        Log.d(
            TAG,
            "getPlayers: start, total : ${currentRound.roundPlayerNicknameList.size} players "
        )
        val playerSize = currentRound.roundPlayerEmailList.size
        val playerNicknameList = currentRound.roundPlayerNicknameList
        val playerTableNameViewList: ArrayList<TextView> =
            arrayListOf(playName_1, playName_2, playName_3, playName_4)
        val playerScoreNameViewList: ArrayList<TextView> = arrayListOf(
            liveScorePlayerName_textview1,
            liveScorePlayerName_textview2,
            liveScorePlayerName_textview3,
            liveScorePlayerName_textview4
        )
        val playerRowViewList: ArrayList<TableRow> =
            arrayListOf(row_player1, row_player2, row_player3, row_player4)
        val playerCounterViewList: ArrayList<CardView> =
            arrayListOf(counter_player_1, counter_player_2, counter_player_3, counter_player_4)
        for (i in 0 until playerSize) {
            playerTableNameViewList[i].text = playerNicknameList[i]
            playerScoreNameViewList[i].text = playerNicknameList[i]
            playerRowViewList[i].visibility = View.VISIBLE
            playerCounterViewList[i].visibility = View.VISIBLE
        }
    }

    private fun addLiveScore(playerPosition: Int) {
        Log.d(TAG, "addLiveScore: invoke")
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
            Snackbar.make(playRound_layout, "더블 파입니다.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun disableFab() {
        Log.d(TAG, "disableFab: invoke")
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
        Log.d(TAG, "ableFab: invoke")
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
        Log.d(TAG, "deployScoreToServer: invoke")
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

        db.document("rounds/$documentPath/liveScore/${currentRoundPlayerEmailList[playerPosition]}")
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
                    "deployToServer: ${currentRoundPlayerEmailList[playerPosition]} to $score"
                )
            }
    }

    private fun updateTotalHit(playerPosition: Int) {
        Log.d(TAG, "updateTotalHit: invoke")
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
            Log.d(TAG, "TOTAL HIT : SET_OK")
        } else {
            when (playerPosition) {
                0 -> apply {
                    player_1_score_total.text = (liveScorePlayerFirstCourse1.sum() + liveScorePlayerFirstCourse2.sum()).toString()
                }
                1 -> apply {
                    player_2_score_total.text = (liveScorePlayerSecondCourse1.sum() + liveScorePlayerSecondCourse2.sum()).toString()
                }
                2 -> apply {
                    player_3_score_total.text = (liveScorePlayerThirdCourse1.sum() + liveScorePlayerThirdCourse2.sum()).toString()
                }
                else -> apply {
                    player_4_score_total.text = (liveScorePlayerFourthCourse1.sum() + liveScorePlayerFourthCourse2.sum()).toString()
                }
            }
            Log.d(TAG, "TOTAL HIT : SET_OK")
        }
    }

    private fun addToScoreBoard(playerPosition: Int, currentHoleIndex: Int) {
        Log.d(TAG, "addToScoreBoard: invoke")
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
        Log.d(TAG, "removeLiveScore: invoke")
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
            Snackbar.make(playRound_layout, "0보다 낮은 점수로 설정할 수 없습니다.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun removeToScoreBoard(playerPosition: Int) {
        Log.d(TAG, "removeToScoreBoard: invoke")
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