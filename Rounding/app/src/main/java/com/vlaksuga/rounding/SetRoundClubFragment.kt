package com.vlaksuga.rounding

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.vlaksuga.rounding.adapters.ClubListAdapter
import com.vlaksuga.rounding.adapters.CourseListAdapter
import com.vlaksuga.rounding.data.Club
import com.vlaksuga.rounding.data.Course

class SetRoundClubFragment : Fragment() {

    companion object {
        const val TAG = "SetRoundClubFragment"
    }

    private lateinit var searchView: SearchView
    private lateinit var currentClubId: String
    private lateinit var currentClubName: String
    private lateinit var currentCourseIdList: ArrayList<String?>
    private lateinit var currentCourseNameList: ArrayList<String?>
    private var currentDate: Long = SetRoundResultFragment.DEFAULT_DATE_VALUE
    lateinit var clubListAdapter: ClubListAdapter
    lateinit var courseListAdapter: CourseListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_set_round_club, container, false)
        activity!!.title = "클럽"

        // from Bundle //
        val fromBundle = this.arguments
        currentDate = fromBundle!!.getLong(
            SetRoundResultFragment.BUNDLE_KEY_DATE,
            SetRoundResultFragment.DEFAULT_DATE_VALUE
        )
        Log.d(TAG, "onCreateView: fromBundle => $fromBundle")

        // set database from fireStore //
        val clubList = arrayListOf<Club>()
        var courseList = arrayListOf<Course>()


        val db = FirebaseFirestore.getInstance()
        db.collection("clubs")
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "Club db : Success!! :)")
            }
            .addOnFailureListener {
                Log.d(TAG, "Club db : Fail!! :(")
            }
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        clubList.add(document.toObject(Club::class.java))
                    }
                    Log.d(TAG, "Club db load complete: Done")
                    clubListAdapter.notifyDataSetChanged()
                    Log.d(TAG, "Club db : notifyDataSetChanged!!")
                }
            }

        db.collection("courses")
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "Course db : Success!! :)")
            }
            .addOnFailureListener {
                Log.d(TAG, "Club db : Fail!! :(")
            }
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        courseList.add(document.toObject(Course::class.java))
                    }
                    Log.d(TAG, "Club db load complete: Done")
                    courseListAdapter.notifyDataSetChanged()
                    Log.d(TAG, "Club db : notifyDataSetChanged!!")
                }
            }

        // adapters //
        clubListAdapter = ClubListAdapter(activity!!, clubList)
        courseListAdapter = CourseListAdapter(activity!!, courseList)
        Log.d(TAG, "onCreateView: adapter Attached")

        // RecyclerViews //
        val clubRecyclerView: RecyclerView = rootView.findViewById(R.id.setRoundClub_recyclerView)
        clubRecyclerView.adapter = clubListAdapter
        clubRecyclerView.layoutManager = LinearLayoutManager(activity!!)
        clubRecyclerView.setHasFixedSize(true)

        val courseRecyclerView: RecyclerView =
            rootView.findViewById(R.id.setRoundCourse_recyclerView)


        // next //
        val nextButton = rootView.findViewById<Button>(R.id.setClubDone_button)
        nextButton.setOnClickListener {
            moveToPlayerFragment()
        }

        clubListAdapter.setOnItemClickListener(object : ClubListAdapter.OnItemClickListener {
            override fun onItemClick(club: Club) {
                val filteredCourseList = arrayListOf<Course>()
                Log.d(TAG, "onItemClick: dataSet init")
                activity!!.title = "코스"
                currentClubId = club.clubId
                currentClubName = club.clubName
                for (course in courseList) {
                    if (course.courseClubId == currentClubId) {
                        filteredCourseList.add(course)
                    }
                }
                courseList = filteredCourseList
                Log.d(TAG, "onItemClick: ${club.clubName} ")
                Log.d(TAG, "onItemClick: ${club.clubId} ")
                Log.d(TAG, "onItemClick:notifyDataSetChanged")
                Log.d(TAG, "onItemClick: courseList => $courseList")

                courseListAdapter = CourseListAdapter(activity!!, courseList)
                clubRecyclerView.visibility = View.GONE
                courseRecyclerView.visibility = View.VISIBLE
                courseRecyclerView.adapter = courseListAdapter
                courseRecyclerView.layoutManager = LinearLayoutManager(activity!!)
                courseRecyclerView.setHasFixedSize(true)
                courseListAdapter.notifyDataSetChanged()
                nextButton.visibility = View.VISIBLE
            }
        })

        // searchView //
        // TODO : 파이어베이스와 서치뷰 연결해서 구축하기
        val searchManager = activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = rootView.findViewById(R.id.setRoundClub_searchView)
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))
        searchView.maxWidth = Int.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                clubListAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                clubListAdapter.filter.filter(newText)
                return false
            }
        })

        return rootView
    }

    private fun moveToPlayerFragment() {
        var firstCourseId: String? = courseListAdapter.selectedFirstCourseId
        var secondCourseId: String? = courseListAdapter.selectedSecondCourseId
        var firstCourseName: String? = courseListAdapter.selectedFirstCourseName
        var secondCourseName: String? = courseListAdapter.selectedSecondCourseName

        if (firstCourseId.isNullOrBlank() && secondCourseId.isNullOrBlank()) {
            Toast.makeText(context, "코스를 선택해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        if (firstCourseId.isNullOrBlank() && !secondCourseId.isNullOrBlank()) {
            firstCourseId = secondCourseId
            secondCourseId = null
            firstCourseName = secondCourseName
            secondCourseName = null
        }
        currentCourseIdList = arrayListOf(firstCourseId, secondCourseId)
        currentCourseNameList = arrayListOf(firstCourseName, secondCourseName)
        val fragmentManager = activity!!.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val newFragment = SetRoundPlayerFragment()
        val toBundle = Bundle()
        toBundle.putLong(SetRoundResultFragment.BUNDLE_KEY_DATE, currentDate)
        toBundle.putString(SetRoundResultFragment.BUNDLE_KEY_CLUB_ID, currentClubId)
        toBundle.putString(SetRoundResultFragment.BUNDLE_KEY_CLUB_NAME, currentClubName)
        toBundle.putStringArrayList(
            SetRoundResultFragment.BUNDLE_KEY_COURSE_ID_LIST,
            currentCourseIdList
        )
        toBundle.putStringArrayList(
            SetRoundResultFragment.BUNDLE_KEY_COURSE_NAME_LIST,
            currentCourseNameList
        )
        newFragment.arguments = toBundle
        transaction.replace(R.id.add_round_fragment_container, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        Log.d(TAG, "moveToPlayerFragment: Data Sent!")
        Log.d(TAG, "moveToPlayerFragment: toBundle => $toBundle")
    }
}