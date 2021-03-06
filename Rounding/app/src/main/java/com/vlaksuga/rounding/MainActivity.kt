package com.vlaksuga.rounding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.vlaksuga.rounding.adapters.CurrentRoundListAdapter
import com.vlaksuga.rounding.model.Round
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val KEY_USER_EMAIL = "com.vlaksuga.rounding.USER_EMAIL"
    }

    private val db = FirebaseFirestore.getInstance()
    lateinit var auth: FirebaseAuth
    private var viewPager: ViewPager2? = null
    private var tabLayoutTitles = arrayListOf<Long>()
    private lateinit var currentRoundListAdapter: CurrentRoundListAdapter
    private var currentRoundList = arrayListOf<Round>()
    lateinit var userEmail: String
    private lateinit var userNickname : String
    private lateinit var userPhoneNumber: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // USER AUTH //
        auth = Firebase.auth
        userEmail = auth.currentUser!!.email!!
        Log.d(TAG, "onCreate: userEmail => $userEmail")

        // TOOLBAR //
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        // CURRENT ROUND LIST SECTION //
        currentRoundListAdapter = CurrentRoundListAdapter(this, currentRoundList)
        val currentRoundRecyclerView: RecyclerView = findViewById(R.id.currentRound_recyclerview)
        currentRoundRecyclerView.adapter = currentRoundListAdapter
        currentRoundRecyclerView.layoutManager = LinearLayoutManager(this)
        currentRoundRecyclerView.setHasFixedSize(true)

        // DB CURRENT ROUND //
        db.collection("rounds")
            .whereArrayContains("roundPlayerEmailList", userEmail)
            .whereEqualTo("isRoundOpen", true)
            .addSnapshotListener { value, _ ->
                if (!value!!.isEmpty) {
                    currentRoundList.clear()
                    currentRoundEmpty_cardView.visibility = View.GONE
                    currentRoundRecyclerView.visibility = View.VISIBLE
                    Log.d(TAG, "FIREBASE CURRENT ROUNDS : EXIST !!, $value")
                    for (document in value.documents) {
                        currentRoundList.add(document.toObject(Round::class.java)!!)
                        Log.d(TAG, "FIREBASE CURRENT ROUNDS : ADD => $document")
                    }
                    currentRoundListAdapter.notifyDataSetChanged()
                    Log.d(TAG, "currentRoundListAdapter : notifyDataSetChanged")
                } else {
                    currentRoundEmpty_cardView.visibility = View.VISIBLE
                    currentRoundRecyclerView.visibility = View.GONE
                    Log.d(TAG, "FIREBASE CURRENT ROUNDS: IS EMPTY!!")
                }
            }

        // RESULT ROUND //
        db.collection("roundResults")
            .whereEqualTo("resultUserEmail", userEmail)
            .addSnapshotListener { value, _ ->
                val mySeason = arrayListOf<Long>()
                if(!value!!.isEmpty) {
                    for(document in value.documents) {
                        mySeason.add(document.get("resultSeason") as Long)
                        Log.d(MainActivity.TAG, "Snap : mySeason = > $mySeason")
                    }

                    // ATTACH VIEW PAGER //
                    val myHashSet : HashSet<Long> = hashSetOf()
                    myHashSet.addAll(mySeason)
                    mySeason.clear()
                    mySeason.addAll(myHashSet)
                    mySeason.sortDescending()
                    tabLayoutTitles = mySeason
                    viewPager = findViewById(R.id.viewpager)
                    viewPager!!.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
                    viewPager!!.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                    TabLayoutMediator(view_tab_layout, viewPager!!) { tab, position ->
                        tab.text = tabLayoutTitles[position].toString()
                    }.attach()

                } else {

                }

            }

        // TO FRIEND ACTIVITY //
        mainFriend_imageView.setOnClickListener {
            val friendIntent = Intent(this, FriendActivity::class.java)
            startActivity(friendIntent)
        }

        // TO STATS ACTIVITY //
        mainStats_imageView.setOnClickListener {
            val statsIntent = Intent(this, StatsActivity::class.java)
            statsIntent.putExtra(KEY_USER_EMAIL, userEmail)
            startActivity(statsIntent)
        }

        // MY PAGE //
        mainMenuSetting_imageView.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        // FAB NEW ROUND //
        addNewRound_fab.setOnClickListener {
            startActivity(Intent(this, AddEditRoundActivity::class.java))
        }

        // EMPTY CARD VIEW NEW ROUND //
        currentRoundEmpty_cardView.setOnClickListener {
            startActivity(Intent(this, AddEditRoundActivity::class.java))
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
            fragment.roundSeason = tabLayoutTitles[position]
            return fragment
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("종료하시겠습니까?")
            setPositiveButton("확인") { _, _ ->
                ActivityCompat.finishAffinity(this@MainActivity)
                exitProcess(0)
            }
            setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

}

