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
import com.vlaksuga.rounding.adapters.RoundListAdapter
import com.vlaksuga.rounding.data.Round
import com.vlaksuga.rounding.model.RoundList
import java.util.*

class RoundFragment : Fragment() {

    companion object {
        const val TAG = "RoundFragment"
    }

    private lateinit var roundList : List<RoundList>
    private val db = FirebaseFirestore.getInstance()
    private val roundRef = db.collection("rounds")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView : View? = inflater.inflate(R.layout.fragment_round, container, false)
        val user = "kangaksjdfkdjf"

        roundRef
            .whereArrayContains("roundPlayerList", user)
            .whereEqualTo("isRoundEnd", false)
            .get()
            .addOnSuccessListener { documents ->
                val roundListHolder = arrayListOf<Round>()
                for(document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "non")
            }
            .addOnCompleteListener { task ->

            }
            roundList = arrayListOf(
            RoundList("1", "브라자 GC", 1111, 45, false),
            RoundList("2", "브레이지어 GC", 2222, 103, true),
            RoundList("3", "브레이지어 GC", 2222, 103, true),
            RoundList("4", "브레이지어 GC", 2222, 103, true),
            RoundList("5", "라온", 3333, 123, true)
        )
        val roundRecyclerView : RecyclerView = rootView!!.findViewById(R.id.roundRecyclerView)
        roundRecyclerView.adapter =
            RoundListAdapter(activity!!, roundList)
        roundRecyclerView.layoutManager = LinearLayoutManager(activity!!)
        roundRecyclerView.setHasFixedSize(true)
        return rootView
    }
}
