package com.vlaksuga.rounding

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.vlaksuga.rounding.adapters.ClubListAdapter
import com.vlaksuga.rounding.adapters.CourseListAdapter
import com.vlaksuga.rounding.adapters.PlayerListAdapter
import com.vlaksuga.rounding.model.Club
import com.vlaksuga.rounding.model.Course
import com.vlaksuga.rounding.model.User
import kotlinx.android.synthetic.main.activity_add_edit_round.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class AddEditRoundActivity : AppCompatActivity() {

    companion object {
        const val TAG = "AddEditRoundActivity"
        const val DATE_FORMAT = "yyyy-MM-dd (E)"
        const val TIME_FORMAT = "a hh:mm"
    }

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var userEmail : String
    private lateinit var userNickname : String

    private lateinit var clubListAdapter: ClubListAdapter
    private lateinit var courseListAdapter: CourseListAdapter
    private lateinit var playerListAdapter: PlayerListAdapter

    private lateinit var clubList : List<Club>
    private lateinit var courseList : List<Course>
    private lateinit var playerList : List<User>

    private var roundClubId : String? = null
    private var roundClubName : String? = null
    private var roundDate : Long? = null
    private var roundSeason : Int? = null
    private var roundTeeTime : Long? = null
    private var roundCourseIdList : List<String>? = null
    private var roundCourseNameList : List<String>? = null
    private var roundPlayerEmailList : List<String>? = null
    private var roundPlayerNicknameList : List<String>? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_round)

        // USER //
        auth = Firebase.auth
        userEmail = auth.currentUser!!.email!!
        getUserNickName()


        // TOOLBAR //
        val toolbar = findViewById<Toolbar>(R.id.addRound_toolbar)
        setSupportActionBar(toolbar)
        addRoundCloseIcon_imageView.setOnClickListener {
            super.onBackPressed()
        }



        // CARD VIEW //
        val dateCardView : CardView = findViewById(R.id.date_cardView)
        val timeCardView : CardView = findViewById(R.id.time_cardView)
        val clubCardView : CardView = findViewById(R.id.club_cardView)
        val courseCardView : CardView = findViewById(R.id.course_cardView)
        val playerCardView : CardView = findViewById(R.id.player_cardView)


        // INIT //
        val cal = Calendar.getInstance()
        roundDate = cal.timeInMillis
        roundSeason = cal.get(Calendar.YEAR)

        addRoundDate_textView.text = SimpleDateFormat(DATE_FORMAT, Locale.KOREA).format(cal.time)
        addRoundTime_textView.text = "티타임을 선택해주세요"
        addRoundClub_textView.text = "클럽을 선택해주세요"
        addRoundCourse_textView.text = "코스를 선택해주세요"


        // DATE //
        dateCardView.setOnClickListener {
            errorMsg_textView.text = ""
            val calendarYear = cal.get(Calendar.YEAR)
            val calendarMonth = cal.get(Calendar.MONTH)
            val calendarDay = cal.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    cal.set(year, month, dayOfMonth, 0,0,1)
                    roundDate = cal.timeInMillis
                    roundSeason = year
                    Log.d(TAG, "DatePickerDialog: roundDate > $roundDate, roundSeason > $roundSeason")
                    addRoundDate_textView.text = SimpleDateFormat(DATE_FORMAT, Locale.KOREA).format(cal.time)
                },
                calendarYear,
                calendarMonth,
                calendarDay
            )
            datePickerDialog.show()
        }

        // TEE TIME //
        timeCardView.setOnClickListener {
            errorMsg_textView.text = ""
            val calendarAmPm = cal.get(Calendar.AM_PM)
            val calendarHour = cal.get(Calendar.HOUR_OF_DAY)
            val calendarMinute = cal.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), hourOfDay, minute)
                    Log.d(TAG, "TimePickerDialog: selected")
                    roundTeeTime = cal.timeInMillis
                    addRoundTime_textView.text = SimpleDateFormat(TIME_FORMAT, Locale.KOREA).format(cal.time)
                },
                calendarHour,
                calendarMinute,
                false
            )
            timePickerDialog.show()
        }


        // CLUB //
        clubCardView.setOnClickListener {
            errorMsg_textView.text = ""
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_view_club, null, false)
            val clubListView : RecyclerView = view.findViewById(R.id.club_recyclerView)
            val searchView : SearchView = view.findViewById(R.id.club_searchView)
            builder.setMessage("클럽을 선택해주세요")
            builder.setNegativeButton("취소") { d, _ -> d.dismiss()}
            builder.setView(view)

            val dialog = builder.create()
            db.collection("clubs")
                .addSnapshotListener { value, error ->
                    if(error != null) {
                        Log.w(TAG, "onCreate: error ", error)
                    }
                    val myClubList = arrayListOf<Club>()
                    for(document in value!!) {
                        myClubList.add(document.toObject(Club::class.java))
                    }
                    clubList = myClubList
                    Collections.sort(clubList
                    ) { o1, o2 -> o1!!.clubName.compareTo(o2!!.clubName) }
                    clubListAdapter = ClubListAdapter(this, clubList)
                    clubListView.adapter = clubListAdapter
                    clubListView.layoutManager = LinearLayoutManager(this)
                    clubListView.setHasFixedSize(true)
                    clubListAdapter.setOnItemClickListener(object : ClubListAdapter.OnItemClickListener{
                        override fun onItemClick(club: Club) {
                            roundClubId = club.clubId
                            roundClubName = club.clubName
                            addRoundClub_textView.text = club.clubName
                            Log.d(TAG, "onItemClick: $roundClubId, $roundClubName")
                            dialog.dismiss()
                            roundCourseIdList = arrayListOf()
                            roundCourseNameList = arrayListOf()
                            addRoundCourse_textView.text = "코스를 선택해주세요"
                        }
                    })

                    // SEARCH VIEW //
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            Log.d(TAG, "onQueryTextSubmit: invoke")
                            clubListAdapter.filter.filter(query)
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            Log.d(TAG, "onQueryTextChange: invoke")
                            clubListAdapter.filter.filter(newText)
                            return false
                        }
                    })
                }
            dialog.show()
        }

        // COURSE //
        courseCardView.setOnClickListener {
            errorMsg_textView.text = ""
            if(roundClubId != null) {
                val builder = AlertDialog.Builder(this)
                val view = layoutInflater.inflate(R.layout.dialog_view_course, null, false)
                val courseListView : RecyclerView = view.findViewById(R.id.course_recyclerView)
                builder.setMessage("코스를 선택해 주세요")
                builder.setPositiveButton("확인") { _, _ ->
                    val adapterCourseIdList = arrayListOf<String>()
                    val adapterCourseNameList = arrayListOf<String>()
                    val adapterCourseFirstId = courseListAdapter.selectedFirstCourseId
                    val adapterCourseSecondId = courseListAdapter.selectedSecondCourseId
                    val adapterCourseFirstName = courseListAdapter.selectedFirstCourseName
                    val adapterCourseSecondName = courseListAdapter.selectedSecondCourseName
                    if(adapterCourseFirstName.isNullOrEmpty()) {
                        if(adapterCourseSecondName.isNullOrEmpty()) {
                            addRoundCourse_textView.text = "코스를 선택해주세요"
                        } else {
                            addRoundCourse_textView.text = adapterCourseSecondName
                            adapterCourseIdList.add(adapterCourseSecondId!!)
                            adapterCourseNameList.add(adapterCourseSecondName)
                        }
                    } else {
                        if(adapterCourseSecondName.isNullOrEmpty()) {
                            addRoundCourse_textView.text = adapterCourseFirstName
                            adapterCourseIdList.add(adapterCourseFirstId!!)
                            adapterCourseNameList.add(adapterCourseFirstName)
                            roundCourseIdList = adapterCourseIdList
                            roundCourseNameList = adapterCourseNameList
                        } else {
                            addRoundCourse_textView.text = "$adapterCourseFirstName, $adapterCourseSecondName"
                            adapterCourseIdList.add(adapterCourseFirstId!!)
                            adapterCourseIdList.add(adapterCourseSecondId!!)
                            adapterCourseNameList.add(adapterCourseFirstName)
                            adapterCourseNameList.add(adapterCourseSecondName)
                        }
                    }
                    roundCourseIdList = adapterCourseIdList
                    roundCourseNameList = adapterCourseNameList
                }
                builder.setNegativeButton("취소") { d, which -> d.dismiss() }
                builder.setView(view)

                val dialog = builder.create()
                db.collection("courses")
                    .whereEqualTo("courseClubId", roundClubId)
                    .addSnapshotListener { value, error ->
                        if(error != null) {
                            Log.w(TAG, "onCreate: error", error)
                        }
                        val myCourseList = arrayListOf<Course>()
                        for(document in value!!) {
                            myCourseList.add(document.toObject(Course::class.java))
                        }
                        courseList = myCourseList
                        courseListAdapter = CourseListAdapter(this, courseList)
                        courseListView.adapter = courseListAdapter
                        courseListView.layoutManager = LinearLayoutManager(this)
                        courseListView.setHasFixedSize(true)
                        dialog.show()
                    }

            } else {
                Snackbar.make(addEditRound_layout, "클럽을 먼저 선택해 주세요", Snackbar.LENGTH_SHORT).show()
            }
        }

        // PLAYER //
        playerCardView.setOnClickListener {
            errorMsg_textView.text = ""
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_view_player, null, false)
            val playerListView : RecyclerView = view.findViewById(R.id.player_recyclerView)
            builder.setMessage("동반할 플레이어를 선택해주세요")
            builder.setPositiveButton("확인") { d, _ ->
                roundPlayerEmailList = arrayListOf(userEmail)
                roundPlayerNicknameList = arrayListOf(userNickname)
                val adapterPlayerEmailList = (roundPlayerEmailList as ArrayList<String>).plus(playerListAdapter.selectedPlayerEmailList)
                val adapterPlayerNicknameList = (roundPlayerNicknameList as ArrayList<String>).plus(playerListAdapter.selectedPlayerNickNameList)
                addRoundPlayer_textView.text = "${adapterPlayerNicknameList.size} 명"
                coPlayerResult_textView.text = adapterPlayerNicknameList.joinToString(separator = ", ")
                roundPlayerEmailList = adapterPlayerEmailList
                roundPlayerNicknameList = adapterPlayerNicknameList
                Log.d(TAG, "player : $roundPlayerEmailList, $roundPlayerNicknameList")
            }
            builder.setNegativeButton("취소") { d, _ -> d.dismiss()}
            builder.setView(view)

            val dialog = builder.create()
            db.collection("users")
                .whereArrayContains("userAllowFriendList", userEmail)
                .addSnapshotListener { value, error ->
                    if(error != null) {
                        Log.w(TAG, "onCreate: error", error)
                    }
                    val myFriendList = arrayListOf<User>()
                    for(document in value!!) {
                        myFriendList.add(document.toObject(User::class.java))
                    }
                    playerList = myFriendList
                    Collections.sort(playerList
                    ) { o1, o2 -> o1.userNickname.compareTo(o2.userNickname)}
                    playerListAdapter = PlayerListAdapter(this, playerList)
                    playerListView.adapter = playerListAdapter
                    playerListView.layoutManager = LinearLayoutManager(this)
                    playerListView.setHasFixedSize(true)
                    dialog.show()
                }
        }

        // SUBMIT BUTTON //
        addRoundSubmit_button.setOnClickListener {
            errorMsg_textView.text = ""
            if(roundDate == null || roundSeason == null) {
                errorMsg_textView.text = "날짜를 확인해 주세요"
                return@setOnClickListener
            }
            if(roundTeeTime == null) {
                errorMsg_textView.text = "티타임을 확인해 주세요"
                return@setOnClickListener
            }
            if(roundClubId.isNullOrBlank() || roundClubName.isNullOrBlank()) {
                errorMsg_textView.text = "클럽을 확인해 주세요"
                return@setOnClickListener
            }
            if(roundCourseIdList!!.isNullOrEmpty() || roundCourseNameList!!.isNullOrEmpty()) {
                errorMsg_textView.text = "코스를 확인해 주세요"
                return@setOnClickListener
            }
            if(roundPlayerEmailList!!.isNullOrEmpty() || roundPlayerNicknameList!!.isNullOrEmpty()) {
                errorMsg_textView.text = "플레이어를 확인해 주세요"
                return@setOnClickListener
            }
            val builder = AlertDialog.Builder(this)
            builder.setMessage("라운드를 만들까요?")
                .setNegativeButton("취소") {dialog, _ -> dialog.dismiss() }
                .setPositiveButton("확인") {_, _ -> setNewRound() }
                .show()
        }
    }

    private fun setNewRound() {

        val roundId : String = UUID.randomUUID().toString()
        val round = hashMapOf(
            "roundId" to roundId,
            "roundOwner" to userEmail,
            "roundDate" to roundDate,
            "roundSeason" to roundSeason,
            "roundTeeTime" to roundTeeTime,
            "roundClubId" to roundClubId,
            "roundClubName" to roundClubName,
            "roundCourseIdList" to roundCourseIdList,
            "roundCourseNameList" to roundCourseNameList,
            "roundPlayerEmailList" to roundPlayerEmailList,
            "roundPlayerNicknameList" to roundPlayerNicknameList,
            "isLiveScoreCreated" to false,
            "isRoundOpen" to true
        )

        db.collection("rounds")
            .add(round)
            .addOnSuccessListener {
                Log.d(TAG, "setNewRound: success ")
            }
            .addOnFailureListener {
                Log.w(TAG, "setNewRound: fail ", it)
            }
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("라운드를 생성하였습니다.")
                        .setPositiveButton("라운드 시작") { _, _ ->
                            val playIntent = Intent(this, PlayRoundActivity::class.java)
                            playIntent.putExtra(RoundResultActivity.DOCUMENT_ID, roundId)
                            startActivity(playIntent)
                        }
                        .setNeutralButton("나중에 시작") { _, _ ->
                            val mainIntent = Intent(this, MainActivity::class.java)
                            startActivity(mainIntent)
                        }
                        .show()
                }
            }

    }

    private fun getUserNickName() {
        Log.d(TAG, "getUserNickName: userEmail => $userEmail")
        db.collection("users")
            .whereEqualTo("userEmail", userEmail)
            .addSnapshotListener { value, error ->
                if(error != null) {
                    Log.w(TAG, "getUserNickName: error ", error)
                } else {
                    userNickname = value!!.documents[0].get("userNickname") as String
                    coPlayerResult_textView.text = userNickname
                    Log.d(TAG, "getUserNickName: $userNickname")
                    roundPlayerEmailList = arrayListOf(userEmail)
                    roundPlayerNicknameList = arrayListOf(userNickname)
                }
            }
    }
}
