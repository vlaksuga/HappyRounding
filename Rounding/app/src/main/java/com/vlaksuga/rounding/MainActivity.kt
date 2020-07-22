package com.vlaksuga.rounding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.vlaksuga.rounding.adapters.CurrentRoundListAdapter
import com.vlaksuga.rounding.adapters.StatsListAdapter
import com.vlaksuga.rounding.constructors.Stats
import com.vlaksuga.rounding.data.Round
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val KEY_USER_ID = "com.vlaksuga.rounding.USER_ID"
    }

    private val db = FirebaseFirestore.getInstance()
    private var viewPager: ViewPager2? = null
    private var tabLayoutTitles = arrayListOf<Long>()
    private lateinit var currentRoundListAdapter: CurrentRoundListAdapter
    private lateinit var statsListAdapter: StatsListAdapter
    private var currentRoundList = arrayListOf<Round>()
    private var mainStatsList = arrayListOf<Stats>()
    private lateinit var userId: String
    private lateinit var userEmail: String
    private lateinit var userNickname : String
    private lateinit var userPhoneNumber: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        userId = "OPPABANANA"
        mainStatsList = arrayListOf(
            Stats("")
        )

        // CURRENT ROUND LIST SECTION //
        currentRoundListAdapter = CurrentRoundListAdapter(this, currentRoundList)
        val currentRoundRecyclerView: RecyclerView = findViewById(R.id.currentRound_recyclerview)
        currentRoundRecyclerView.adapter = currentRoundListAdapter
        currentRoundRecyclerView.layoutManager = LinearLayoutManager(this)
        currentRoundRecyclerView.setHasFixedSize(true)

        // DB CURRENT ROUND //
        db.collection("rounds")
            .whereArrayContains("roundPlayerIdList", userId)
            .addSnapshotListener { value, error ->
                if (!value!!.isEmpty) {
                    Log.d(TAG, "FIREBASE CURRENT ROUNDS : EXIST !!, $value")
                    for (document in value.documents) {
                        currentRoundList.add(document.toObject(Round::class.java)!!)
                        Log.d(TAG, "FIREBASE CURRENT ROUNDS : ADD => $document")
                    }
                    currentRoundListAdapter.notifyDataSetChanged()
                    Log.d(TAG, "currentRoundListAdapter : notifyDataSetChanged")
                } else {
                    currentRoundRecyclerView.visibility = View.GONE
                    Log.d(TAG, "FIREBASE CURRENT ROUNDS: IS EMPTY!!")
                }
            }


        // TO FRIEND ACTIVITY //
        mainFriend_imageView.setOnClickListener {
            val friendIntent = Intent(this, FriendActivity::class.java)
            friendIntent.putExtra(KEY_USER_ID, userId)
            startActivity(friendIntent)
        }

        // TO STATS ACTIVITY //

        mainStats_imageView.setOnClickListener {
            val statsIntent = Intent(this, StatsActivity::class.java)
            statsIntent.putExtra(KEY_USER_ID, userId)
            startActivity(statsIntent)
        }

        mainMenuMore_imageView.setOnClickListener {
            AuthUI.getInstance().signOut(this)
                .addOnCompleteListener {
                    startActivity(Intent(this, LogInActivity::class.java))
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
        }



        // TODO : 라운드를 감지하여 해당 년도 정보를 가져오기
        db.collection("roundResults")
            .whereEqualTo("resultUserName", "오빠바나나")
            .addSnapshotListener { value, error ->
                val mySeason = arrayListOf<Long>()
                if(!value!!.isEmpty) {
                    for(document in value.documents) {
                        mySeason.add(document.get("roundSeason") as Long)
                        Log.d(TAG, "Snap : mySeason = > $mySeason")
                    }
                } else {
                    Log.d(TAG, "onCreate: ${error!!.message} ")
                }
                val myHashSet : HashSet<Long> = hashSetOf()
                myHashSet.addAll(mySeason)
                mySeason.clear()
                mySeason.addAll(myHashSet)
                mySeason.sortDescending()
                tabLayoutTitles = mySeason
                viewPager = findViewById(R.id.viewpager)
                viewPager!!.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
                viewPager!!.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                TabLayoutMediator(tab_layout, viewPager!!) { tab, position ->
                    tab.text = tabLayoutTitles[position].toString()
                }.attach()
            }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Activity.RESULT_FIRST_USER) {
            userEmail = intent.getStringExtra(SignUpActivity.KEY_USER_EMAIL)!!
            userNickname = intent.getStringExtra(SignUpActivity.KEY_USER_NICKNAME)!!
            userPhoneNumber = intent.getStringExtra(SignUpActivity.KEY_USER_PHONE)!!
            Snackbar.make(main_LinearLayout, userNickname, Snackbar.LENGTH_SHORT).show()
            Log.d(TAG, "onActivityResult: $userEmail, $userNickname, $userPhoneNumber")
        }
    }


    private inner class ViewPagerAdapter(fragmentManager: FragmentManager?, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager!!, lifecycle) {
        override fun getItemCount(): Int {
            return tabLayoutTitles.size
        }

        override fun createFragment(position: Int): Fragment {
            val fragment = RoundFragment()
            fragment.roundSeason = tabLayoutTitles[position].toLong()
            return fragment
        }
    }
}

