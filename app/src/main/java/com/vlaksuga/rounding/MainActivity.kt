package com.vlaksuga.rounding

import android.os.Bundle
import android.view.Menu
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val PAGE_COUNT = 3
    }
    private var viewPager : ViewPager2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        viewPager = findViewById(R.id.viewpager)
        viewPager!!.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager!!.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val tabLayoutTitles = arrayOf("라운드", "친구", "통계")
        TabLayoutMediator(tab_layout, viewPager!!) {tab, position ->
            tab.text = tabLayoutTitles[position]
        }.attach()

        floatingActionButton.setOnClickListener {
            Snackbar.make(main_LinearLayout, "라운딩 만들기 했다침", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    private inner class ViewPagerAdapter(fragmentManager: FragmentManager?, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager!!, lifecycle) {
        override fun getItemCount(): Int {
            return PAGE_COUNT
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> RoundFragment()
                1 -> FriendFragment()
                2 -> StatisticsFragment()
                else -> RoundFragment()
            }
        }
    }
}
