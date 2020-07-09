package com.vlaksuga.rounding.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.vlaksuga.rounding.R
import com.vlaksuga.rounding.data.Course


class CourseListAdapter internal constructor(context: Context, courseList : List<Course>) : RecyclerView.Adapter<CourseListAdapter.CourseListViewHolder> () {

    companion object {
        const val TAG = "CourseListAdapter"
    }

    private val mContext : Context = context
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var items : List<Course> = courseList

    var selectedFirstCourseId : String? = null
    var selectedSecondCourseId : String? = null
    var selectedFirstCourseName : String? = null
    var selectedSecondCourseName : String? = null

    inner class CourseListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val cardItemView = itemView.findViewById<CardView>(R.id.courseList_cardView)
        val titleItemView = itemView.findViewById<TextView>(R.id.setRoundCourseName_textView)
        val inOutItemView = itemView.findViewById<TextView>(R.id.setRoundCourseInOut_textView)

        fun bind(courseList: Course) {
            cardItemView.setOnClickListener {
                if(selectedFirstCourseId.isNullOrBlank() && selectedSecondCourseId.isNullOrBlank()) {
                    inOutItemView.text = "IN"
                    inOutItemView.visibility = View.VISIBLE
                    selectedFirstCourseId = courseList.courseId
                    selectedFirstCourseName = courseList.courseName
                    Log.d(TAG, "click: IN course added ")
                    Log.d(TAG, "courseList: 1 => $selectedFirstCourseId ${selectedFirstCourseName}, 2 => $selectedSecondCourseId $selectedSecondCourseName")
                } else if (!selectedFirstCourseId.isNullOrBlank() && selectedSecondCourseId.isNullOrBlank()) {
                    if(inOutItemView.visibility == View.GONE) {
                        inOutItemView.text = "OUT"
                        inOutItemView.visibility = View.VISIBLE
                        selectedSecondCourseId = courseList.courseId
                        selectedSecondCourseName = courseList.courseName
                        Log.d(TAG, "click: OUT course added ")
                        Log.d(TAG, "courseList: 1 => $selectedFirstCourseId ${selectedFirstCourseName}, 2 => $selectedSecondCourseId $selectedSecondCourseName")
                    } else {
                        inOutItemView.text = null
                        inOutItemView.visibility = View.GONE
                        selectedFirstCourseId = null
                        selectedFirstCourseName = null
                        Log.d(TAG, "click: IN course removed ")
                        Log.d(TAG, "courseList: 1 => $selectedFirstCourseId ${selectedFirstCourseName}, 2 => $selectedSecondCourseId $selectedSecondCourseName")
                    }
                } else if(selectedFirstCourseId.isNullOrBlank() && !selectedSecondCourseId.isNullOrBlank()) {
                    if(inOutItemView.visibility == View.GONE) {
                        inOutItemView.text = "IN"
                        inOutItemView.visibility = View.VISIBLE
                        selectedFirstCourseId = courseList.courseId
                        selectedFirstCourseName = courseList.courseName
                        Log.d(TAG, "click: IN course added ")
                        Log.d(TAG, "courseList: 1 => $selectedFirstCourseId ${selectedFirstCourseName}, 2 => $selectedSecondCourseId $selectedSecondCourseName")
                    } else {
                        inOutItemView.text = null
                        inOutItemView.visibility = View.GONE
                        selectedSecondCourseId = null
                        selectedSecondCourseName = null
                        Log.d(TAG, "click: OUT course removed ")
                        Log.d(TAG, "courseList: 1 => $selectedFirstCourseId ${selectedFirstCourseName}, 2 => $selectedSecondCourseId $selectedSecondCourseName")
                    }
                } else {
                    if(inOutItemView.visibility == View.GONE) {
                        Toast.makeText(mContext, "코스는 2개만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "click: safe invalidate ")
                        Log.d(TAG, "courseList: 1 => $selectedFirstCourseId ${selectedFirstCourseName}, 2 => $selectedSecondCourseId $selectedSecondCourseName")
                    } else {
                        inOutItemView.text = null
                        inOutItemView.visibility = View.GONE
                        if(selectedFirstCourseId == courseList.courseId) {
                            selectedFirstCourseId = null
                            selectedFirstCourseName = null
                            Log.d(TAG, "click: IN course removed ")
                            Log.d(TAG, "courseList: 1 => $selectedFirstCourseId ${selectedFirstCourseName}, 2 => $selectedSecondCourseId $selectedSecondCourseName")
                        } else {
                            selectedSecondCourseId = null
                            selectedSecondCourseName = null
                            Log.d(TAG, "click: OUT course removed ")
                            Log.d(TAG, "courseList: 1 => $selectedFirstCourseId ${selectedFirstCourseName}, 2 => $selectedSecondCourseId $selectedSecondCourseName")
                        }
                    }
                }
            }
            titleItemView.text = courseList.courseName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseListAdapter.CourseListViewHolder {
        val itemView = inflater.inflate(R.layout.course_list_item, parent, false)
        return CourseListViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: CourseListAdapter.CourseListViewHolder, position: Int) {
        val currentCourse = items[position]
        holder.bind(currentCourse)
    }
}