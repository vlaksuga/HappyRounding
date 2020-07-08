package com.vlaksuga.rounding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*


class SetRoundResultFragment : Fragment() {

    companion object {
        const val TAG = "SetRoundResultFragment"
        const val BUNDLE_KEY_DATE = "com.vlaksuga.rounding.DATE"
        const val BUNDLE_KEY_CLUB_ID = "com.vlaksuga.rounding.CLUB_ID"
        const val BUNDLE_KEY_COURSE_ID_LIST = "com.vlaksuga.rounding.COURSE_ID_LIST"
        const val BUNDLE_KEY_PLAYER_ID_LIST = "com.vlaksuga.rounding.PLAYER_ID_LIST"
        const val DEFAULT_DATE_VALUE : Long = 1593602409070
    }

    lateinit var round : HashMap<String, Any>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_set_round_result, container, false)
        val db = FirebaseFirestore.getInstance()
        // TODO : 파이어베이스에서 유저 정보와 클럽 정보 등을 가져와서 해당 정보에 매핑한다
        activity!!.title = "라운드"

        val bundle = this.arguments
        val bundleClubId = bundle!!.getString(BUNDLE_KEY_CLUB_ID)
        val bundleDate = bundle.getLong(BUNDLE_KEY_DATE, DEFAULT_DATE_VALUE)
        val bundleCourseIdList = bundle.getStringArrayList(BUNDLE_KEY_COURSE_ID_LIST)
        val bundlePlayerIdLIst = bundle.getStringArrayList(BUNDLE_KEY_PLAYER_ID_LIST)

        // ArrayList To String //
        val courseIdListToString : String = bundleCourseIdList!!.joinToString(separator = ", ")
        val playerIdListToString : String = bundlePlayerIdLIst!!.joinToString(separator = ", ")

        Log.d(TAG, "bundle : $bundle")

        val resultDate : TextView = rootView.findViewById(R.id.createRoundDate_textView)
        val resultClubName : TextView = rootView.findViewById(R.id.createClub_textView)
        val resultCourseList : TextView = rootView.findViewById(R.id.createCourse_textView)
        val resultPlayers : TextView = rootView.findViewById(R.id.createPlayers_textView)
        val createButton : Button = rootView.findViewById(R.id.createRoundSubmit_button)

        // Display Results From Bundle //
        resultClubName.text = bundleClubId
        resultDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(bundleDate)
        resultCourseList.text = courseIdListToString
        resultPlayers.text = playerIdListToString



        createButton.setOnClickListener {

            round = hashMapOf(
                "roundOwnerUserId" to "ownerUserId",
                "roundDate" to 1594037938390,
                "roundClubId" to "clubid",
                "roundCourseIdList" to arrayListOf<String>("course1", "course2"),
                "roundPlayerIdList" to arrayListOf<String>("kangaksjdfkdjf","asdfsadkfasjdkl","chuqnmchsdfd","sjdafhiasdfwjdh"),
                "isRoundEnd" to false
            )
            // TODO : 중복된 라운드를 생성할 수 없도록 AppShered에 라운드 상태 생성중 변경하기 (만들었다 : 끝났다)
            db.collection("rounds")
                .add(round)
                .addOnSuccessListener { _ ->
                    Log.d(TAG, "set success")
                }
                .addOnFailureListener {exception ->
                    Log.w(TAG, "error",  exception)
                }
                .addOnCompleteListener {task ->
                    if(task.isComplete) {
                        // TODO : 만든 다큐먼트를 콜백해서 해당 정보를 가지고 이동한다.
                        activity!!.startActivity(Intent(activity, PlayRoundActivity::class.java))
                        activity!!.finish()
                    }
                }
        }
        return rootView
    }
}