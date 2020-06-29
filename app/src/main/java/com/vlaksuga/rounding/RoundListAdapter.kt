package com.vlaksuga.rounding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class RoundListAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<RoundListAdapter.RoundListViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private val rounds = emptyList<Round>()

    private var filterRoundListResult : List<Round> = rounds


    inner class RoundListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardItemView : CardView = itemView.findViewById(R.id.roundList_cardView)
        val clubNameItemView : TextView = itemView.findViewById(R.id.roundClubName_textView)
        val dateItemView : TextView = itemView.findViewById(R.id.roundDate_textView)
        val userFirstItemView : ImageView = itemView.findViewById(R.id.roundUserFirst_imageView)
        val userSecondItemView : ImageView = itemView.findViewById(R.id.roundUserSecond_imageView)
        val userThirdItemView : ImageView = itemView.findViewById(R.id.roundUserThird_imageView)
        val userFourthItemView : ImageView = itemView.findViewById(R.id.roundUserFourth_imageView)
        val courseNameItemView : TextView = itemView.findViewById(R.id.roundCourseName_textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoundListViewHolder {
        val itemView = inflater.inflate(R.layout.round_list_item, parent, false)
        return RoundListViewHolder(itemView)
    }

    override fun getItemCount() = filterRoundListResult.size

    override fun onBindViewHolder(holder: RoundListViewHolder, position: Int) {
        val currentRound = filterRoundListResult[position]

        holder.clubNameItemView.text = currentRound.roundClub
        holder.dateItemView.text = currentRound.roundDate.toString()
        holder.userFirstItemView.setImageResource(currentRound.roundUser[0].toInt())
        holder.userSecondItemView.setImageResource(currentRound.roundUser[0].toInt())
        holder.userThirdItemView.setImageResource(currentRound.roundUser[0].toInt())
        holder.userFourthItemView.setImageResource(currentRound.roundUser[0].toInt())

        val courseList : String = ""
        for(course in currentRound.roundCourse - 1) {
            // TODO : 코스리스트에서 스트링으로 뽑기
        }
        holder.courseNameItemView.text = courseList
    }
}