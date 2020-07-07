package com.vlaksuga.rounding

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.vlaksuga.rounding.data.Round
import kotlinx.android.synthetic.main.activity_add_edit_round.*
import java.util.*


class AddEditRoundActivity : AppCompatActivity() {

    companion object {
        const val TAG = "AddEditRoundActivity"
        const val DATE_FORMAT = "yyyy-MM-dd"
    }

    private val currentDate = Date().time
    private val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.KOREA)
    private val cal = Calendar.getInstance()

    lateinit var roundId: String
    var roundDate: Long = currentDate
    lateinit var roundClubId: String
    lateinit var roundFirstCourseId: String
    var roundSecondCourseId: String? = null
    var roundThirdCourseId: String? = null
    lateinit var roundFirstPlayerId: String
    lateinit var roundSecondPlayerId: String
    var roundThirdPlayerId: String? = null
    var roundFourthPlayerId: String? = null
    var isRoundEnd: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_round)

        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val initFragment = SetRoundDateFragment()
        transaction.add(R.id.add_round_fragment_container, initFragment)
        transaction.commit()



        /*addEditRoundSelectedDate_textView.text = simpleDateFormat.format(cal)

        addEditRoundSelectedDate_textView.setOnClickListener {
            selectDate()
        }

        addEditRoundClubSelect_button.setOnClickListener {
            // TODO : 플레이라운드 인스턴스를 만드는 행동 생각하기
            val builder = AlertDialog.Builder(this)
        }

        addEditRoundCourseSelect_button.setOnClickListener {
        }

        addEditRoundAddFriend_button.setOnClickListener {
        }

        addEditRoundChangeSubmit_button.setOnClickListener {
            startActivity(Intent(this, PlayRoundActivity::class.java))
        }
    }

    private fun selectDate() {
        val calendarYear = cal.get(Calendar.YEAR)
        val calendarMonth = cal.get(Calendar.MONTH)
        val calendarDay = cal.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(year, monthOfYear, dayOfMonth)
                addEditRoundSelectedDate_textView.text = simpleDateFormat.format(cal.time)
                Log.d(TAG, "selectDate: ${cal.timeInMillis.toString()} ")
            },
            calendarYear,
            calendarMonth,
            calendarDay
        )
        datePickerDialog.show()
    }*/
    }
}
