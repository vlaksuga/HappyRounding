package com.vlaksuga.rounding

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import java.util.*

class SetRoundDateFragment : Fragment() {

    companion object {
        const val TAG = "SetRoundDateFragment"
    }

    private val currentDate = Date().time
    private val simpleDateFormat = SimpleDateFormat(AddEditRoundActivity.DATE_FORMAT, Locale.KOREA)
    private val cal = android.icu.util.Calendar.getInstance()
    lateinit var selectDateText : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_set_round_date, container, false)
        val nextButton : Button = view.findViewById(R.id.setRoundDateNext_button)

        selectDateText= view.findViewById(R.id.setRoundDate_textView)
        selectDateText.text = simpleDateFormat.format(currentDate)

        selectDateText.setOnClickListener{
            selectDate()
        }
        nextButton.setOnClickListener {
            moveToNextFragment()
        }

        return view
    }

    private fun moveToNextFragment() {
        val fragmentManager = activity!!.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val newFragment = SetRoundClubFragment()
        transaction.replace(R.id.add_round_fragment_container, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun selectDate() {
        val calendarYear = cal.get(Calendar.YEAR)
        val calendarMonth = cal.get(Calendar.MONTH)
        val calendarDay = cal.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            activity!!,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(year, monthOfYear, dayOfMonth)
                selectDateText.text = simpleDateFormat.format(cal.time)
                Log.d(TAG, "selectDate: ${cal.timeInMillis.toString()} ")
            },
            calendarYear,
            calendarMonth,
            calendarDay
        )
        datePickerDialog.show()
    }
}