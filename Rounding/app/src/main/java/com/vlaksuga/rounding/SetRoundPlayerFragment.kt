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

    lateinit var playerListAdapter: PlayerListAdapter
    private var dataRoundDate : Long = SetRoundResultFragment.DEFAULT_DATE_VALUE
    private lateinit var dataRoundClubId : String
    private lateinit var dataRoundClubName : String
    private lateinit var dataRoundCourseIdList : ArrayList<String>
    private lateinit var dataRoundCourseNameList : ArrayList<String>
    private lateinit var dataRoundPlayerIdList : ArrayList<String>
    private lateinit var dataRoundPlayerNicknameList : ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_set_round_player, container, false)

        // TODO : GET BUNDLE DATA FROM CLUB FRAGMENT
        val fromBundle = this.arguments
        dataRoundDate = fromBundle!!.getLong(SetRoundResultFragment.BUNDLE_KEY_DATE, SetRoundResultFragment.DEFAULT_DATE_VALUE)
        dataRoundClubId = fromBundle.getString(SetRoundResultFragment.BUNDLE_KEY_CLUB_ID)!!
        dataRoundClubName = fromBundle.getString(SetRoundResultFragment.BUNDLE_KEY_CLUB_NAME)!!
        dataRoundCourseIdList = fromBundle.getStringArrayList(SetRoundResultFragment.BUNDLE_KEY_COURSE_ID_LIST)!!
        dataRoundCourseNameList = fromBundle.getStringArrayList(SetRoundResultFragment.BUNDLE_KEY_COURSE_NAME_LIST)!!

        dataRoundPlayerIdList = arrayListOf()
        dataRoundPlayerNicknameList = arrayListOf()

        activity!!.title = "플레이어"

        // TODO : USER의 FRIEND를 가져온다
        val playerList = arrayListOf(
            User("GOLFGOD","sdffsdfwbds","골프도사","tekitekite@nsdkf","white","sakdfjsdkfsdf"),
            User("HOLEINONE","sdffsdfwbds","호올인원","tekitekite@nsdkf","white","sakdfjsdkfsdf"),
            User("YESVERYGOOD","sdffsdfwbds","으응개잘해","tekitekite@nsdkf","white","sakdfjsdkfsdf"),
            User("YESDOGHONEY","sdffsdfwbds","존나개꿀꿀","tekitekie@nsdkf","white","sakdfjsdkfsdf")
        )

        playerListAdapter = PlayerListAdapter(activity!!, playerList)

        // TODO : 어댑터 리스너를 감지해서 dataRoundPlayerIdList = playerListAdapter.selectedPlayerList를 재처리하기

        // set RecyclerView //
        val playerRecyclerView : RecyclerView = rootView!!.findViewById(R.id.playerRecyclerView)
        playerRecyclerView.adapter = playerListAdapter
        playerRecyclerView.layoutManager = LinearLayoutManager(activity!!)
        playerRecyclerView.setHasFixedSize(true)

        dataRoundPlayerIdList = playerListAdapter.selectedPlayerIdList
        Log.d(TAG, "dataRoundPlayerIdList: $dataRoundPlayerIdList")

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
        val toBundle = Bundle()

        toBundle.putLong(SetRoundResultFragment.BUNDLE_KEY_DATE, dataRoundDate)
        toBundle.putString(SetRoundResultFragment.BUNDLE_KEY_CLUB_ID, dataRoundClubId)
        toBundle.putString(SetRoundResultFragment.BUNDLE_KEY_CLUB_NAME, dataRoundClubName)
        toBundle.putStringArrayList(SetRoundResultFragment.BUNDLE_KEY_COURSE_ID_LIST, dataRoundCourseIdList)
        toBundle.putStringArrayList(SetRoundResultFragment.BUNDLE_KEY_COURSE_NAME_LIST, dataRoundCourseNameList)
        toBundle.putStringArrayList(SetRoundResultFragment.BUNDLE_KEY_PLAYER_ID_LIST, playerListAdapter.selectedPlayerIdList)
        toBundle.putStringArrayList(SetRoundResultFragment.BUNDLE_KEY_PLAYER_NICKNAME_LIST, playerListAdapter.selectedPlayerNickNameList)

        newFragment.arguments = toBundle
        transaction.replace(R.id.add_round_fragment_container, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        Log.d(TAG, "moveToResultFragment: success ")
        Log.d(TAG, "selectedPlayerList: $dataRoundPlayerIdList")
    }
}