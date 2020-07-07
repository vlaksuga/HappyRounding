package com.vlaksuga.rounding

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val PAGE_COUNT = 3
    }
    private var viewPager : ViewPager2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // TODO : 현재 유저 값 불러오기
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        mainMenuSearch_imageView.setOnClickListener{
            // TODO : 라운딩 리스트 찾기 실행
        }

        mainMenuMore_imageView.setOnClickListener {
            // TODO : 라운딩 더보기 메뉴 실행
        }

        viewPager = findViewById(R.id.viewpager)
        viewPager!!.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager!!.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val tabLayoutTitles = arrayOf("라운드", "친구", "통계")
        TabLayoutMediator(tab_layout, viewPager!!) {tab, position ->
            tab.text = tabLayoutTitles[position]
        }.attach()

        floatingActionButton.setOnClickListener {
            startActivity(Intent(this, PlayRoundActivity::class.java))
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
