package com.vlaksuga.rounding


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.vlaksuga.rounding.adapters.RoundListAdapter
import com.vlaksuga.rounding.adapters.SeasonStatsListAdapter
import com.vlaksuga.rounding.model.ResultRound
import com.vlaksuga.rounding.model.Stats

class RoundFragment : Fragment() {

    companion object {
        const val TAG = "RoundFragment"
    }

    lateinit var roundList : List<ResultRound>
    lateinit var statsList : List<Stats>

    var roundSeason : Long = 0
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView : View? = inflater.inflate(R.layout.fragment_round, container, false)
        Log.d(TAG, "roundSeason: $roundSeason")

        auth = Firebase.auth
        val userEmail = auth.currentUser!!.email
        Log.d(TAG, "onCreateView: userEmail is $userEmail")

        db.collection("roundResults")
            .whereEqualTo("resultUserEmail", userEmail)
            .whereEqualTo("resultSeason", roundSeason)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
              if(firebaseFirestoreException != null) {
                  Log.w(TAG, "Listen failed.", firebaseFirestoreException)
                  return@addSnapshotListener
              }
                val myRounds = arrayListOf<ResultRound>()
                val document: QuerySnapshot? = querySnapshot
                for(snapshot in document!!) {
                    myRounds.add(snapshot.toObject(ResultRound::class.java))
                }
                Log.d(TAG, "onComplete: ${document.metadata}}")
                roundList = myRounds
                val roundTotalSize : Int = roundList.size
                val roundTotalScores = arrayListOf<Int>()
                for(round in roundList) {
                    if(round.resultCourseIdList.size == 2) {
                        roundTotalScores.add(round.resultFirstScoreList.sum() + round.resultSecondScoreList.sum())
                    }
                }
                val roundAverage : Int = (roundTotalScores.sum()) / roundTotalScores.size
                roundTotalScores.sort()
                val roundBest = roundTotalScores[0]
                statsList = arrayListOf(
                    Stats(
                        "라운드",
                        "ROUND",
                        roundTotalSize
                    ),
                    Stats(
                        "평균 스코어",
                        "STROKES",
                        roundAverage
                    ),
                    Stats(
                        "베스트 스코어",
                        "STROKES",
                        roundBest
                    )
                )


                // SEASON STATS RECYCLERVIEW //
                val seasonStatsRecyclerView : RecyclerView = rootView!!.findViewById(R.id.seasonStatsRecyclerView)
                seasonStatsRecyclerView.adapter =  SeasonStatsListAdapter(activity!!, statsList)
                seasonStatsRecyclerView.layoutManager = GridLayoutManager(activity!!, 3)
                seasonStatsRecyclerView.setHasFixedSize(true)

                // ROUND LIST RECYCLERVIEW //
                val roundRecyclerView : RecyclerView = rootView.findViewById(R.id.roundRecyclerView)
                roundRecyclerView.adapter = RoundListAdapter(activity!!, roundList)
                roundRecyclerView.layoutManager = LinearLayoutManager(activity!!)
                roundRecyclerView.setHasFixedSize(true)
                Log.d(TAG, "onCreateView: countList => ${roundList.size} ")

            }

        return rootView
    }
}
