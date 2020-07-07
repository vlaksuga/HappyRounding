package com.vlaksuga.rounding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.vlaksuga.rounding.data.*
import com.vlaksuga.rounding.databinding.ActivityRoundResultBinding
import com.vlaksuga.rounding.model.ResultRound
import kotlinx.android.synthetic.main.activity_round_result.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.IntStream
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RoundResultActivity : AppCompatActivity() {

    companion object {
        const val TAG = "RoundResultActivity"
        const val DATE_FORMAT = "yyyy-MM-dd"
        const val COLLECTION_PATH_ROOT = "roundResults/"
        const val DOCUMENT_ID = "YK0yUTO2h4qvlmS6xX8O"
    }

    private val roundList: List<Round> = emptyList()
    private val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.KOREA)
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO : GET ROUND INDEX FROM INTENT

        val myUserId = "2wlyzx0pCcNyC2xyh1CB"

        @Suppress("UNCHECKED_CAST")
        db.collection(COLLECTION_PATH_ROOT).document(DOCUMENT_ID)
            .get()
            .addOnSuccessListener { document ->
                if(!document.exists()) {
                    Log.d(TAG, "collection : Not exist ")
                } else {
                    Log.d(TAG, "db received ${document.id} => ${document.data} ")
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "FAIL!")
            }
            .addOnCompleteListener {task: Task<DocumentSnapshot> ->
                if(task.isSuccessful) {
                    val document = task.result
                    val resultUserName = document!!.getString("resultUserName")!!
                    val resultClubName = document.getString("resultClubName")!!
                    val resultDate = document.getString("resultDate")!!
                    val resultCoPlayers = document.getString("resultCoPlayers")!!
                    val resultFirstCourseName = document.getString("resultFirstCourseName")!!
                    val resultSecondCourseName = document.getString("resultSecondCourseName")!!
                    val resultFirstCourseParList : ArrayList<Long> = document.get("resultFirstCourseParList") as ArrayList<Long>
                    val resultSecondCourseParList : ArrayList<Long> = document.get("resultSecondCourseParList") as ArrayList<Long>
                    val resultFirstScoreList : ArrayList<Long> = document.get("resultFirstScoreList") as ArrayList<Long>
                    val resultSecondScoreList : ArrayList<Long> = document.get("resultSecondScoreList") as ArrayList<Long>
                    Log.d(TAG, "oncomplete.resultUserName : $resultUserName")
                    Log.d(TAG, "oncomplete.resultClubName : $resultClubName")
                    Log.d(TAG, "oncomplete.resultDate : $resultDate")
                    Log.d(TAG, "oncomplete.resultCoPlayers : $resultCoPlayers")
                    Log.d(TAG, "oncomplete.resultFirstCourseName : $resultFirstCourseName")
                    Log.d(TAG, "oncomplete.resultSecondCourseName : $resultSecondCourseName")
                    Log.d(TAG, "oncomplete.resultFirstCourseParList : ${resultFirstCourseParList[0]}")
                    Log.d(TAG, "oncomplete.resultFirstCourseParList : ${resultFirstCourseParList[1]}")
                    Log.d(TAG, "oncomplete.resultSecondCourseParList : ${resultSecondCourseParList[0]}")
                    Log.d(TAG, "oncomplete.resultSecondCourseParList : ${resultSecondCourseParList[1]}")
                    Log.d(TAG, "oncomplete.resultFirstScoreList : ${resultFirstScoreList[0]}")
                    Log.d(TAG, "oncomplete.resultFirstScoreList : ${resultFirstScoreList[1]}")
                    Log.d(TAG, "oncomplete.resultSecondScoreList : ${resultSecondScoreList[0]}")
                    Log.d(TAG, "oncomplete.resultSecondScoreList : ${resultSecondScoreList[1]}")

                    val currentResult = ResultRound(
                        resultUserName,
                        resultClubName,
                        resultDate,
                        // TODO : 열람하는 유저를 제외하고 나머지 플레이어를 ,으로 나누어 string으로 만들기
                        resultCoPlayers,
                        resultFirstCourseName,
                        resultSecondCourseName,
                        resultFirstCourseParList,
                        resultSecondCourseParList,
                        resultFirstScoreList,
                        resultSecondScoreList
                    )

                    val binding : ActivityRoundResultBinding = DataBindingUtil.setContentView(this, R.layout.activity_round_result)
                    binding.result = currentResult

                    val firstScoreList = resultFirstScoreList.sum()
                    val secondScoreList = resultSecondScoreList.sum()
                    val totalScore = (firstScoreList + secondScoreList).toString()
                    firstCourse_total_hit_textView.text = firstScoreList.toString()
                    firstCourse_table_score_textView.text = firstScoreList.toString()
                    secondCourse_total_hit_textView.text = secondScoreList.toString()
                    secondCourse_table_score_textView.text = secondScoreList.toString()
                    resultRoundTotalHit_textView.text = totalScore
                }

            }


        val currentUser = User(
            "sidjfsdfofsdf",
            "tekiteki",
            "sadofsdfj",
            "대륭사",
            "tekiteki@naver.com",
            "White",
            "im.jpg"
        )

        val currentRound = Round(
            "sdofwjdf",
            1594037938390,
            "sdkfsdfjiwf",
            arrayListOf("xcksowmx", "gksxksmwk"),
            arrayListOf("sgjskxwm", "gskxmwmx", "xkwmxio", "gksxmwkcm"),
            true
        )

        // SEARCH FROM GET
        val currentClub  = Club(
            currentRound.roundClubId,
            "벨리오스",
            "벨리오스"
        )

        val currentFirstCourse = Course(
            currentRound.roundCourseIdList[0],
            currentRound.roundClubId,
            "LAKE",
            arrayListOf(4,5,4,3,4,4,5,4,3),
            arrayListOf(342,464,164,464,446,344,444,345,150)
        )

        val currentSecondCourse = Course(
            currentRound.roundCourseIdList[1],
            currentRound.roundClubId,
            "VALLEY",
            arrayListOf(4,5,4,3,4,4,5,4,3),
            arrayListOf(342,464,164,464,446,344,444,345,150)
        )

        val currentFirstCourseScore =
            Score(
                "skdfskdfsj",
                currentUser.userUUId,
                DOCUMENT_ID,
                currentRound.roundCourseIdList[0],
                arrayListOf(4,5,4,7,8,4,5,7,9)
            )

        val currentSecondCourseScore =
            Score(
                "skdfskdfsj",
                currentUser.userUUId,
                DOCUMENT_ID,
                currentRound.roundCourseIdList[1],
                arrayListOf(4,5,4,7,8,4,5,7,9)
            )


        // BINDINGS //
        // TODO : ROUND와 USER를 매핑하여 인덱싱하기, ROUND전체를 저장하는것과 USERROUND를 저장하는 것 중 어느 것이 이득인지 따져보기
        // TODO : USERROUND의 정보를 최소화하여 참조하여 가져오는 것이 좋을지 살펴보기


    }
}