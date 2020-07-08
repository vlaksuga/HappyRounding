package com.vlaksuga.rounding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_save_round_result.*

import java.text.SimpleDateFormat
import java.util.*

class SaveRoundResultActivity : AppCompatActivity() {

    // TODO : 라운드 시작 중일때 컬랙션을 인스턴스로 정해서 발행하고, 종료하면 roundResult로 user별로 저장한다음 인스턴스 컬랙션을 삭제한다.
    companion object {
        const val TAG = "PlayRoundActivity"
        const val DATE_FORMAT = "yyyy-MM-dd"
    }

    private val db = FirebaseFirestore.getInstance()
    private val roundResultRef = db.collection("roundResults")
    private val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.KOREA)
    private var firstUserRoundResult = hashMapOf<String, Any>()
    private var secondUserRoundResult = hashMapOf<String, Any>()
    private var thirdUserRoundResult = hashMapOf<String, Any>()
    private var fourthUserRoundResult = hashMapOf<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_round_result)

        // TODO : 라운드 최종 결과 보여주기
        // TODO : 플레이가 다 완전히 끝났다면 이 버튼을 활성화 시킨다
        // TODO : 라운드 결과를 유저별 해쉬맵으로 각각 저장한다

        val roundDate = simpleDateFormat.format(1594037938390)
        firstUserRoundResult = hashMapOf<String, Any>(
            "resultRoundId" to UUID.randomUUID().toString(),
            "resultUserName" to "오빠바나나",
            "resultClubName" to "발안CC",
            "resultDate" to roundDate,
            "resultCoPlayers" to "누군가, 신주섭",
            "resultFirstCourseName" to "발암",
            "resultSecondCourseName" to "불암",
            "resultFirstCourseParList" to arrayListOf(3,3,5,3,5,4,4,4,5),
            "resultSecondCourseParList" to arrayListOf(4,4,5,3,4,4,5,4,4),
            "resultFirstScoreList" to arrayListOf(4,3,4,4,5,8,4,5,5),
            "resultSecondScoreList" to arrayListOf(4,4,5,3,8,4,5,3,4)
        )
        secondUserRoundResult = hashMapOf<String, Any>(
            "resultRoundId" to UUID.randomUUID().toString(),
            "resultUserName" to "그랜노만",
            "resultClubName" to "발안CC",
            "resultDate" to roundDate,
            "resultCoPlayers" to "김태경, 신주섭",
            "resultFirstCourseName" to "발암",
            "resultSecondCourseName" to "불암",
            "resultFirstCourseParList" to arrayListOf(3,3,5,3,5,4,4,4,5),
            "resultSecondCourseParList" to arrayListOf(4,4,5,3,4,4,5,4,4),
            "resultFirstScoreList" to arrayListOf(4,3,4,4,5,8,4,5,5),
            "resultSecondScoreList" to arrayListOf(4,4,5,3,8,4,5,3,4)
        )
        thirdUserRoundResult = hashMapOf<String, Any>(
            "resultRoundId" to UUID.randomUUID().toString(),
            "resultUserName" to "신주섭",
            "resultClubName" to "발안CC",
            "resultDate" to roundDate,
            "resultCoPlayers" to "김태경, 신주섭",
            "resultFirstCourseName" to "발암",
            "resultSecondCourseName" to "불암",
            "resultFirstCourseParList" to arrayListOf(3,3,5,3,5,4,4,4,5),
            "resultSecondCourseParList" to arrayListOf(4,4,5,3,4,4,5,4,4),
            "resultFirstScoreList" to arrayListOf(4,3,4,4,5,8,4,5,5),
            "resultSecondScoreList" to arrayListOf(4,4,5,3,8,4,5,3,4)
        )
        fourthUserRoundResult = hashMapOf<String, Any>(
            "resultRoundId" to UUID.randomUUID().toString(),
            "resultUserName" to "천개",
            "resultClubName" to "발안CC",
            "resultDate" to roundDate,
            "resultCoPlayers" to "김태경, 신주섭",
            "resultFirstCourseName" to "발암",
            "resultSecondCourseName" to "불암",
            "resultFirstCourseParList" to arrayListOf(3,3,5,3,5,4,4,4,5),
            "resultSecondCourseParList" to arrayListOf(4,4,5,3,4,4,5,4,4),
            "resultFirstScoreList" to arrayListOf(4,3,4,4,5,8,4,5,5),
            "resultSecondScoreList" to arrayListOf(4,4,5,3,8,4,5,3,4)
        )

        saveRoundResult_button.setOnClickListener {
            // TODO : 저장중일때 행동 생각해서 적용하기 (아마도 광고)
            it.visibility = View.GONE
            createFirstUserResult()
        }
    }

    private fun createFirstUserResult() {
        roundResultRef.add(firstUserRoundResult)
            .addOnSuccessListener { _ ->
                Log.d(TAG, "Result Round Saved!")
            }
            .addOnFailureListener {exception ->
                Log.w(TAG, "error",  exception)
                return@addOnFailureListener
            }
            .addOnCompleteListener{ firstTask ->
                if(firstTask.isSuccessful) {
                    // TODO : 프로그레스바로 진행중을 알림 : 25% (가능하다면 애니메이션)
                    Log.d(TAG, "createFirstUserResult: saved ")
                    createSecondUserResult()
                }
            }
    }

    private fun createSecondUserResult() {
        roundResultRef.add(secondUserRoundResult)
            .addOnSuccessListener { _ ->
                Log.d(TAG, "Result Round Saved!")
            }
            .addOnFailureListener {exception ->
                Log.w(TAG, "error",  exception)
                return@addOnFailureListener
            }
            .addOnCompleteListener{ secondTask ->
                if(secondTask.isSuccessful) {
                    // TODO : 프로그레스바로 진행중을 알림 : 50% (가능하다면 애니메이션)
                    Log.d(TAG, "createSecondUserResult: saved ")
                    createThirdUserResult()
                }
            }
    }

    private fun createThirdUserResult() {
        roundResultRef.add(secondUserRoundResult)
            .addOnSuccessListener { _ ->
                Log.d(TAG, "Result Round Saved!")
            }
            .addOnFailureListener {exception ->
                Log.w(TAG, "error",  exception)
                return@addOnFailureListener
            }
            .addOnCompleteListener{ secondTask ->
                if(secondTask.isSuccessful) {
                    // TODO : 프로그레스바로 진행중을 알림 : 75% (가능하다면 애니메이션)
                    Log.d(TAG, "createThirdUserResult: saved ")
                    createFourthUserResult()
                }
            }
    }

    private fun createFourthUserResult() {
        roundResultRef.add(secondUserRoundResult)
            .addOnSuccessListener { _ ->
                Log.d(TAG, "Result Round Saved!")
            }
            .addOnFailureListener {exception ->
                Log.w(TAG, "error",  exception)
                return@addOnFailureListener
            }
            .addOnCompleteListener{ secondTask ->
                if(secondTask.isSuccessful) {
                    // TODO : 완료후 행동 고려하기
                    Log.d(TAG, "createThirdUserResult: saved ")
                    Toast.makeText(this, "모두 저장했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }





}