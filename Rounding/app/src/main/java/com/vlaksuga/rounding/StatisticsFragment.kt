package com.vlaksuga.rounding

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.vlaksuga.rounding.adapters.StatsListAdapter
import com.vlaksuga.rounding.model.ResultRound
import com.vlaksuga.rounding.model.Stats

class StatisticsFragment : Fragment() {

    companion object {
        const val TAG = "StatisticsFragment"
    }

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var userEmail : String
    private var valueLatestRoundScore = 0
    private var valueAverageScore = 0
    private var valueBestScore = 0
    private var valueRoundCount = 0
    private var valueClubCount = 0
    private var valueCourseCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // USER EMAIL //
        auth = Firebase.auth
        userEmail = auth.currentUser!!.email!!

        val rootView = inflater.inflate(R.layout.fragment_statistics, container, false)

        db.collection("roundResults")
            .whereEqualTo("resultUserEmail", userEmail)
            .get()
            .addOnCompleteListener {
                Log.d(TAG, "db.collection(\"roundResults\") : complete")
                if(it.isSuccessful) {
                    val myResultRound = arrayListOf<List<ResultRound>>()
                    myResultRound.add(it.result!!.toObjects(ResultRound::class.java))
                    Log.d(TAG, "db.myResultRound: $myResultRound")
                }
                val statsList = arrayListOf<Stats>()
                val statsRecyclerView : RecyclerView = rootView!!.findViewById(R.id.statsRecyclerView)
                statsRecyclerView.adapter = StatsListAdapter(activity!!, statsList)
                statsRecyclerView.layoutManager = GridLayoutManager(activity!!, 3)
                statsRecyclerView.setHasFixedSize(true)
            }


        val statsList = arrayListOf<Stats>(
            Stats("최근 라운드", "strokes", 67),
            Stats("9라운드 평균", "strokes", 85),
            Stats("베스트 스코어", "strokes", 69),
            Stats("라운드수", "rounds", 85),
            Stats("클럽수", "clubs", 15),
            Stats("코스수", "courses", 37)
        )




        return rootView
    }
}