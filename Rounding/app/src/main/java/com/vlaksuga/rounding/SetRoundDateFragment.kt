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
        const val BUNDLE_KEY_DATE = "com.vlaksuga.rounding.DATE"
    }

    private var currentDate = Date().time
    private val simpleDateFormat = SimpleDateFormat(AddEditRoundActivity.DATE_FORMAT, Locale.KOREA)
    private val cal = Calendar.getInstance()
    lateinit var selectDateText : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_set_round_date, container, false)
        activity!!.title = "날짜"
        val nextButton : Button = rootView.findViewById(R.id.setDateDone_button)

        selectDateText= rootView.findViewById(R.id.setRoundDate_textView)
        selectDateText.text = simpleDateFormat.format(currentDate)

        selectDateText.setOnClickListener{
            selectDate()
        }
        nextButton.setOnClickListener {
            moveToClubFragment()
        }

        return rootView
    }

    private fun moveToClubFragment() {
        val fragmentManager = activity!!.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val newFragment = SetRoundClubFragment()
        val bundle = Bundle()
        bundle.putLong(BUNDLE_KEY_DATE, currentDate)
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
                currentDate = cal.timeInMillis
                selectDateText.text = simpleDateFormat.format(cal.time)
                Log.d(TAG, "selectDate: ${cal.timeInMillis}")
                Log.d(TAG, "currentDate: $currentDate ")
            },
            calendarYear,
            calendarMonth,
            calendarDay
        )
        datePickerDialog.show()
    }
}