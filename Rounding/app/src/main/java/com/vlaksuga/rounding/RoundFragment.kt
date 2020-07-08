package com.vlaksuga.rounding

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.vlaksuga.rounding.adapters.RoundListAdapter
import com.vlaksuga.rounding.constructors.ResultRound

class RoundFragment : Fragment() {

    companion object {
        const val TAG = "RoundFragment"
    }

    private lateinit var roundList : List<ResultRound>
    private val db = FirebaseFirestore.getInstance()
    private val roundRef = db.collection("roundResults")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView : View? = inflater.inflate(R.layout.fragment_round, container, false)
        val resultUserName = "오빠바나나"
        roundRef
            .whereEqualTo("resultUserName", resultUserName)
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
                val roundRecyclerView : RecyclerView = rootView!!.findViewById(R.id.roundRecyclerView)
                roundRecyclerView.adapter = RoundListAdapter(activity!!, roundList)
                roundRecyclerView.layoutManager = LinearLayoutManager(activity!!)
                roundRecyclerView.setHasFixedSize(true)
                Log.d(TAG, "onCreateView: countList => ${roundList.size} ")
            }
        return rootView
    }
}
