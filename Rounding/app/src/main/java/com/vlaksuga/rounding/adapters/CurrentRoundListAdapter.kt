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
import com.vlaksuga.rounding.PlayRoundActivity
import com.vlaksuga.rounding.R
import com.vlaksuga.rounding.RoundResultActivity
import com.vlaksuga.rounding.model.Round
import java.text.SimpleDateFormat
import java.util.*

class CurrentRoundListAdapter internal constructor(context: Context, currentRoundList: List<Round>) :
    RecyclerView.Adapter<CurrentRoundListAdapter.CurrentRoundListViewHolder>() {

    companion object {
        const val TAG = "CurrentRoundListAdapter"
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val mContext: Context = context
    private var items: List<Round> = currentRoundList

    inner class CurrentRoundListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardItemView: CardView = itemView.findViewById(R.id.currentRoundList_cardView)
        private val clubNameItemView: TextView = itemView.findViewById(R.id.currentRoundClubName_textView)
        private val dateItemView: TextView = itemView.findViewById(R.id.currentRoundDate_textView)
        private val timeItemView: TextView = itemView.findViewById(R.id.currentRoundTeeTime_textView)

        fun bind(currentRoundList: Round) {
            cardItemView.setOnClickListener {
                Log.d(TAG, "bind: ${currentRoundList.roundId}")
                val currentRoundIntent = Intent(mContext, PlayRoundActivity::class.java)
                currentRoundIntent.putExtra(RoundResultActivity.DOCUMENT_ID, currentRoundList.roundId)
                mContext.startActivity(currentRoundIntent)
            }
            clubNameItemView.text = currentRoundList.roundClubName
            dateItemView.text = SimpleDateFormat("yyyy-MM-dd (E)", Locale.KOREA).format(currentRoundList.roundDate)
            timeItemView.text = SimpleDateFormat("a hh:mm", Locale.KOREA).format(currentRoundList.roundTeeTime)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentRoundListViewHolder {
        val itemView = inflater.inflate(R.layout.current_round_list_item, parent, false)
        return CurrentRoundListViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: CurrentRoundListViewHolder, position: Int) {
        val currentRound = items[position]
        holder.bind(currentRound)
    }
}