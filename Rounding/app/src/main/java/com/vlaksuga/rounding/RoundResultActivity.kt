package com.vlaksuga.rounding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.vlaksuga.rounding.data.*
import com.vlaksuga.rounding.databinding.ActivityRoundResultBinding
import kotlinx.android.synthetic.main.activity_round_result.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.IntStream

class RoundResultActivity : AppCompatActivity() {

    companion object {
        const val TAG = "RoundResultActivity"
        const val DATE_FORMAT = "yyyy-MM-dd"
    }

    private val roundList : List<Round> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentUserRound = UserRound(
            "sdofwjdf","그랜노만","skdfjsidfwicjsw","skdfjksdjfksjdf",1594037938390,
            "sdkfsdfjiwf","벨리오스",
            "sdkfjsdof","레이크", arrayListOf(4,3,4,5,4,4,5,3,4,4),
            "dskfddfjw","벨리", arrayListOf(4,4,3,5,4,4,5,3,4,4),
            arrayListOf(4,5,6,5,7,5,4,4,4), arrayListOf(4,5,6,5,7,5,4,4,4),
            true)
        val currentUser =  User("sidjfsdfofsdf","tekiteki","sadofsdfj","대륭사","tekiteki@naver.com","White","im.jpg")

        // TODO : ROUND와 USER를 매핑하여 인덱싱하기, ROUND전체를 저장하는것과 USERROUND를 저장하는 것 중 어느 것이 이득인지 따져보기
        // TODO : USERROUND의 정보를 최소화하여 참조하여 가져오는 것이 좋을지 살펴보기
        val binding : ActivityRoundResultBinding = DataBindingUtil.setContentView(this, R.layout.activity_round_result)
            binding.user = currentUser
            binding.userRound = currentUserRound


        val firstScoreList = currentUserRound.roundPlayerFirstScoreList
        val secondScoreList = currentUserRound.roundPlayerSecondScoreList
        val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.KOREA)
        val roundDate = simpleDateFormat.format(currentUserRound.roundDate)
        firstCourse_total_hit_textView.text = firstScoreList.sum().toString()
        secondCourse_total_hit_textView.text = secondScoreList.sum().toString()
        resultRoundDate_textView.text = roundDate

    }
}