package com.vlaksuga.rounding

import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.vlaksuga.rounding.adapters.PlayerListAdapter
import com.vlaksuga.rounding.data.User

class SetRoundPlayerFragment : Fragment() {

    companion object {
        const val TAG = "SetRoundPlayerFragment"
    }

    private var dataRoundDate : Long = SetRoundResultFragment.DEFAULT_DATE_VALUE
    private lateinit var dataRoundClubId : String
    private lateinit var dataRoundClubName : String
    private lateinit var dataRoundCourseIdList : ArrayList<String>
    private lateinit var dataRoundCourseNameList : ArrayList<String>

    private val db = FirebaseFirestore.getInstance()
    private var userId = ""
    private var userNickname = ""
    private lateinit var playerList : ArrayList<User>
    private var playerIdList = arrayListOf<String>()
    private var playerNicknameList = arrayListOf<String>()
    lateinit var playerListAdapter : PlayerListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_set_round_player, container, false)
        activity!!.title = "플레이어"

        playerList = arrayListOf()

        userId = "OPPABANANA"
        userNickname = "오빠바나나"
        playerIdList.add(userId)
        playerNicknameList.add(userNickname)

        val fromBundle = this.arguments
        dataRoundDate = fromBundle!!.getLong(SetRoundResultFragment.BUNDLE_KEY_DATE, SetRoundResultFragment.DEFAULT_DATE_VALUE)
        dataRoundClubId = fromBundle.getString(SetRoundResultFragment.BUNDLE_KEY_CLUB_ID)!!
        dataRoundClubName = fromBundle.getString(SetRoundResultFragment.BUNDLE_KEY_CLUB_NAME)!!
        dataRoundCourseIdList = fromBundle.getStringArrayList(SetRoundResultFragment.BUNDLE_KEY_COURSE_ID_LIST)!!
        dataRoundCourseNameList = fromBundle.getStringArrayList(SetRoundResultFragment.BUNDLE_KEY_COURSE_NAME_LIST)!!


        playerListAdapter = PlayerListAdapter(activity!!, playerList)
        // TODO : ADD GUEST
        db.collection("users")
            .whereArrayContains("userAllowFriendList", userId)
            .addSnapshotListener { value, error ->
                if(error != null) {
                    Log.w(TAG, "getFriends: Listen Failed ", error)
                }
                val document : QuerySnapshot? = value
                for(snapshot in document!!) {
                    playerList.add(snapshot.toObject(User::class.java))
                    Log.d(TAG, "myFriends: added ")
                }
                playerListAdapter.notifyDataSetChanged()
                Log.d(TAG, "notifyDataSetChanged")
                Log.d(TAG, "playerList: $playerList")
            }

        // TODO : 플레이어 추가하는 모드

        val nextButton = rootView.findViewById<Button>(R.id.setPlayerDone_button)
        nextButton.setOnClickListener {
            moveToResultFragment()
        }
        Log.d(TAG, "playerList: $playerList")
        val playerRecyclerView : RecyclerView = rootView.findViewById(R.id.playerRecyclerView)
        playerRecyclerView.adapter = playerListAdapter
        playerRecyclerView.layoutManager = LinearLayoutManager(activity!!)
        playerRecyclerView.setHasFixedSize(true)
        playerListAdapter.notifyDataSetChanged()
        return rootView
    }

    private fun moveToResultFragment() {

        val fragmentManager = activity!!.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val newFragment = SetRoundResultFragment()
        val toBundle = Bundle()

        for(playerId in playerListAdapter.selectedPlayerEmailList) {
            playerIdList.add(playerId)
        }

        for(playerNickname in playerListAdapter.selectedPlayerNickNameList) {
            playerNicknameList.add(playerNickname)
        }

        toBundle.putLong(SetRoundResultFragment.BUNDLE_KEY_DATE, dataRoundDate)
        toBundle.putString(SetRoundResultFragment.BUNDLE_KEY_CLUB_ID, dataRoundClubId)
        toBundle.putString(SetRoundResultFragment.BUNDLE_KEY_CLUB_NAME, dataRoundClubName)
        toBundle.putStringArrayList(SetRoundResultFragment.BUNDLE_KEY_COURSE_ID_LIST, dataRoundCourseIdList)
        toBundle.putStringArrayList(SetRoundResultFragment.BUNDLE_KEY_COURSE_NAME_LIST, dataRoundCourseNameList)
        toBundle.putStringArrayList(SetRoundResultFragment.BUNDLE_KEY_PLAYER_ID_LIST, playerListAdapter.selectedPlayerEmailList)
        toBundle.putStringArrayList(SetRoundResultFragment.BUNDLE_KEY_PLAYER_NICKNAME_LIST, playerListAdapter.selectedPlayerNickNameList)
        transaction.commit()
        Log.d(TAG, "moveToResultFragment: success")
    }
}