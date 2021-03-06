package com.vlaksuga.rounding.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.vlaksuga.rounding.R
import com.vlaksuga.rounding.RoundResultActivity
import com.vlaksuga.rounding.model.ResultRound
import java.text.SimpleDateFormat
import java.util.*

class RoundListAdapter internal constructor(context: Context, roundList: List<ResultRound>) :
    RecyclerView.Adapter<RoundListAdapter.RoundListViewHolder>() {

    companion object {
        const val TAG = "RoundListAdapter"
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val mContext: Context = context
    private var items: List<ResultRound> = roundList

    inner class RoundListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardItemView: CardView = itemView.findViewById(R.id.roundList_cardView)
        private val clubNameItemView: TextView = itemView.findViewById(R.id.roundClubName_textView)
        private val dateItemView: TextView = itemView.findViewById(R.id.roundDate_textView)
        private val hitCountItemView: TextView = itemView.findViewById(R.id.roundTotalHit_textView)
        private val nineHoleItemView : TextView = itemView.findViewById(R.id.roundNineHole_textView)

        fun bind(roundList: ResultRound) {
            cardItemView.setOnClickListener {
                Log.d(TAG, "bind: ${roundList.resultClubName}")
                val resultRoundIntent = Intent(mContext, RoundResultActivity::class.java)
                resultRoundIntent.putExtra(RoundResultActivity.DOCUMENT_ID, roundList.resultRoundId)
                mContext.startActivity(resultRoundIntent)
            }
            clubNameItemView.text = roundList.resultClubName
            dateItemView.text = SimpleDateFormat("yyyy-MM-dd (E)", Locale.KOREA).format(roundList.resultDate)
            hitCountItemView.text = (roundList.resultFirstScoreList.sum() + roundList.resultSecondScoreList.sum()).toString()
            nineHoleItemView.visibility = if (roundList.resultCourseIdList.size == 2) {View.GONE} else {View.VISIBLE}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoundListViewHolder {
        val itemView = inflater.inflate(R.layout.round_list_item, parent, false)
        return RoundListViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RoundListViewHolder, position: Int) {
        val currentRound = items[position]
        holder.bind(currentRound)
    }
}