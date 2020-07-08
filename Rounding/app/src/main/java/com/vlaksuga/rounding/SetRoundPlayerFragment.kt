package com.vlaksuga.rounding

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vlaksuga.rounding.adapters.PlayerListAdapter
import com.vlaksuga.rounding.data.User

class SetRoundPlayerFragment : Fragment() {

    companion object {
        const val TAG = "SetRoundPlayerFragment"
    }

    private val adapter = PlayerListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_set_round_player, container, false)
        activity!!.title = "플레이어"
        // USER의 FRIEND를 가져온다
        val playerList = arrayListOf<User>(
            User("123123cxa","sdfsdfcsdfs","sdffsdfwbds","골프도사","tekitekite@nsdkf","white","sakdfjsdkfsdf"),
            User("123123cxa","sdfsdfcsdfs","sdffsdfwbds","호올인원","tekitekite@nsdkf","white","sakdfjsdkfsdf"),
            User("123123cxa","sdfsdfcsdfs","sdffsdfwbds","으응개잘해","tekitekite@nsdkf","white","sakdfjsdkfsdf"),
            User("123123cxa","sdfsdfcsdfs","sdffsdfwbds","존나개꿀꿀","tekitekie@nsdkf","white","sakdfjsdkfsdf")
        )

        val playerRecyclerView : RecyclerView = rootView!!.findViewById(R.id.playerRecyclerView)
        playerRecyclerView.adapter = PlayerListAdapter(activity!!, playerList)
        playerRecyclerView.layoutManager = LinearLayoutManager(activity!!)
        playerRecyclerView.setHasFixedSize(true)

        val nextButton = rootView.findViewById<Button>(R.id.setPlayerDone_button)
        nextButton.setOnClickListener {
            moveToResultFragment()
        }
        return rootView
    }

    private fun moveToResultFragment() {
        val fragmentManager = activity!!.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val newFragment = SetRoundResultFragment()
        val bundle = Bundle()
        bundle.putLong(SetRoundResultFragment.BUNDLE_KEY_DATE, 1593861609070)
        bundle.putString(SetRoundResultFragment.BUNDLE_KEY_CLUB_ID, "골프클럽아이디")
        bundle.putStringArrayList(SetRoundResultFragment.BUNDLE_KEY_COURSE_ID_LIST, arrayListOf("코스아이디1", "코스아이디2"))
        bundle.putStringArrayList(SetRoundResultFragment.BUNDLE_KEY_PLAYER_ID_LIST, arrayListOf("유저아이디1", "유저아이디2", "유저아이디3", "유저아이디4"))
        newFragment.arguments = bundle
        transaction.replace(R.id.add_round_fragment_container, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        Log.d(TAG, "moveToResultFragment: success ")
    }
}