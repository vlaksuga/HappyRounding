package com.vlaksuga.rounding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_round.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val PAGE_COUNT = 3
    }

    private lateinit var roundListAdapter: RoundListAdapter
    private lateinit var recyclerView: RecyclerView
    private var viewPager : ViewPager2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager = findViewById(R.id.viewpager)
        viewPager!!.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager!!.orientation = ViewPager2.ORIENTATION_HORIZONTAL

/*        roundListAdapter = RoundListAdapter(this)
        recyclerView = findViewById<RecyclerView>(R.id.roundRecyclerView)
        recyclerView.adapter = roundListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)*/
    }

    private inner class ViewPagerAdapter(fragmentManager: FragmentManager?, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager!!, lifecycle) {
        override fun getItemCount(): Int {
            return PAGE_COUNT
        }

        override fun createFragment(position: Int): Fragment {
            var fragment : Fragment? = null
            when (position) {
                0 -> fragment = RoundFragment()
                1 -> fragment = FriendFragment()
                2 -> fragment = StatisticsFragment()
                else -> RoundFragment()
            }
            return fragment!!
        }

    }
}
