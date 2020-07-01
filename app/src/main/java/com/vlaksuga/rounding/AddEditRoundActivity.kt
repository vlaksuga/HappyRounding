package com.vlaksuga.rounding

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_add_edit_round.*
import java.util.*
import java.util.Calendar.MONTH
import javax.xml.datatype.DatatypeConstants.MONTHS

class AddEditRoundActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_round)

        addEditRoundSelectedDate_textView.setOnClickListener {
            // TODO : 날짜선택하는 다이얼로그에서 날짜 선택시 선택된 날짜 정보로 떨구기
            val cal = Calendar.getInstance()
            val calendarYear = cal.get(Calendar.YEAR)
            val calendarMonth = cal.get(Calendar.MONTH)
            val calendarDay = cal.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog: DatePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val dateFormat = "yyyy.mm.dd"
                    val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.KOREA)
                    addEditRoundSelectedDate_textView.text = simpleDateFormat.format(cal.time)
                    Toast.makeText(this, "" + dayOfMonth + year, Toast.LENGTH_SHORT).show()
                },
                calendarYear,
                calendarMonth,
                calendarDay
            )
            datePickerDialog.show()
        }

        addEditRoundClubSelect_button.setOnClickListener {
            // TODO : 클럽선택 다이얼로그 실행 또는 검색가능한 플래그먼트 또는 인텐트 실행
        }

        addEditRoundCourseSelect_button.setOnClickListener {
            // TODO : 선택된 클럽내의 코스선택 다이얼로그 실행하기
        }

        addEditRoundAddFriend_button.setOnClickListener {
            // TODO : 내 친구 추가하는 다이얼로그 실행하여 선택한 친구를 리스트 어댑터에 추가하기
        }

        addEditRoundChangeSubmit_button.setOnClickListener {
            // TODO : 인텐트에 내용을 첨부하여 플레이 라운드 활성화 시키기
            startActivity(Intent(this, PlayRoundActivity::class.java))
        }
    }
}
