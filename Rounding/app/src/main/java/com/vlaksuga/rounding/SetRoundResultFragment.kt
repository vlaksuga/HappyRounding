package com.vlaksuga.rounding

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SetRoundResultFragment : Fragment() {

    companion object {
        const val TAG = "SetRoundResultFragment"
        const val BUNDLE_KEY_DATE = "com.vlaksuga.rounding.DATE"
        const val BUNDLE_KEY_CLUB_ID = "com.vlaksuga.rounding.CLUB_ID"
        const val BUNDLE_KEY_CLUB_NAME = "com.vlaksuga.rounding.CLUB_NAME"
        const val BUNDLE_KEY_COURSE_ID_LIST = "com.vlaksuga.rounding.COURSE_ID_LIST"
        const val BUNDLE_KEY_COURSE_NAME_LIST = "com.vlaksuga.rounding.COURSE_NAME_LIST"
        const val BUNDLE_KEY_PLAYER_ID_LIST = "com.vlaksuga.rounding.PLAYER_ID_LIST"
        const val BUNDLE_KEY_PLAYER_NICKNAME_LIST = "com.vlaksuga.rounding.PLAYER_NICKNAME_LIST"
        const val DEFAULT_DATE_VALUE: Long = 1593602409070
    }

    lateinit var round: HashMap<String, Any>
    private lateinit var bundleClubId: String
    private lateinit var bundleClubName: String
    private var bundleDate: Long = DEFAULT_DATE_VALUE
    private lateinit var bundleCourseIdList: ArrayList<String>
    private lateinit var bundleCourseNameList: ArrayList<String>
    private lateinit var bundlePlayerIdLIst: ArrayList<String>
    private lateinit var bundlePlayerNickNameLIst: ArrayList<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_set_round_result, container, false)
        val db = FirebaseFirestore.getInstance()
        // TODO : 파이어베이스에서 유저 정보와 클럽 정보 등을 가져와서 해당 정보에 매핑한다
        activity!!.title = "라운드"

        // Get Bundle //
        val fromBundle = this.arguments
        bundleClubId = fromBundle!!.getString(BUNDLE_KEY_CLUB_ID)!!
        bundleClubName = fromBundle.getString(BUNDLE_KEY_CLUB_NAME)!!
        bundleDate = fromBundle.getLong(BUNDLE_KEY_DATE, DEFAULT_DATE_VALUE)
        bundleCourseIdList = fromBundle.getStringArrayList(BUNDLE_KEY_COURSE_ID_LIST)!!
        bundleCourseNameList = fromBundle.getStringArrayList(BUNDLE_KEY_COURSE_NAME_LIST)!!
        bundlePlayerIdLIst = fromBundle.getStringArrayList(BUNDLE_KEY_PLAYER_ID_LIST)!!
        bundlePlayerNickNameLIst = fromBundle.getStringArrayList(BUNDLE_KEY_PLAYER_NICKNAME_LIST)!!

        // ArrayList To String //
        var courseNameListToString: String = bundleCourseNameList.joinToString(separator = ", ")
        if (courseNameListToString.contains("null")) {
            courseNameListToString = bundleCourseNameList[0]
            Log.d(TAG, "CourseName null deleted!!")
        }
        val playerNicknameListToString: String =
            bundlePlayerNickNameLIst.joinToString(separator = ", ")

        Log.d(TAG, "bundle : $fromBundle")

        val resultDate: TextView = rootView.findViewById(R.id.createRoundDate_textView)
        val resultClubName: TextView = rootView.findViewById(R.id.createClub_textView)
        val resultCourseList: TextView = rootView.findViewById(R.id.createCourse_textView)
        val resultPlayers: TextView = rootView.findViewById(R.id.createPlayers_textView)
        val createButton: Button = rootView.findViewById(R.id.createRoundSubmit_button)

        // Display Results From Bundle //
        resultDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(bundleDate)
        resultClubName.text = bundleClubName
        resultCourseList.text = courseNameListToString
        resultPlayers.text = playerNicknameListToString



        createButton.setOnClickListener {
            it.isClickable = false
            round = hashMapOf(
                "roundOwnerUserId" to "ownerUserId",
                "roundDate" to bundleDate,
                "roundClubId" to bundleClubId,
                "roundCourseIdList" to bundleCourseIdList,
                "roundPlayerIdList" to bundlePlayerIdLIst,
                "isRoundEnd" to false
            )

            db.collection("rounds")
                .add(round)
                .addOnSuccessListener { _ ->
                    Log.d(TAG, "set success")
                }
                .addOnFailureListener { exception ->
                    App.prefs.roundIsOpen = false
                    it.isClickable = true
                    Log.w(TAG, "error", exception)
                }
                .addOnCompleteListener { task ->
                    if (task.isComplete) {
                        // TODO : 만든 다큐먼트를 콜백해서 해당 정보를 가지고 다이얼로그 때린다.
                        App.prefs.roundIsOpen = true
                        Toast.makeText(activity, "라운드를 생성하였습니다!", Toast.LENGTH_SHORT).show()
                        val builder = AlertDialog.Builder(activity)
                        builder.setMessage("라운드를 시작할까요?")
                            .setPositiveButton("시작"
                            ) { _, _ ->
                                activity!!.startActivity(Intent(activity, PlayRoundActivity::class.java))
                                activity!!.finish()
                            }
                            .setNegativeButton("나중에 시작"
                            ) { dialog, _ ->
                                dialog!!.dismiss()
                                activity!!.startActivity(Intent(activity, MainActivity::class.java))
                                activity!!.finish()
                            }
                            .show()
                    }
                }
        }
        return rootView
    }
}