package com.vlaksuga.rounding

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class RoundListAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<RoundListAdapter.RoundListViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var listener: OnItemClickListener

    private val roundList = emptyList<RoundList>()

    private var filterRoundListResult : List<RoundList> = arrayListOf(
        RoundList("1","브라자 GC", 1111, 45, false),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("3", "라온", 3333, 123, true))


    inner class RoundListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardItemView : CardView = itemView.findViewById(R.id.roundList_cardView)
        val clubNameItemView : TextView = itemView.findViewById(R.id.roundClubName_textView)
        val dateItemView : TextView = itemView.findViewById(R.id.roundDate_textView)
        val hitCountItemView : TextView = itemView.findViewById(R.id.roundTotalHit_textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoundListViewHolder {
        val itemView = inflater.inflate(R.layout.round_list_item, parent, false)
        return RoundListViewHolder(itemView)
    }

    override fun getItemCount() = filterRoundListResult.size

    override fun onBindViewHolder(holder: RoundListViewHolder, position: Int) {
        val currentRound = filterRoundListResult[position]
        holder.cardItemView.setOnClickListener {
            listener.onItemClick(currentRound)
        }
        if(!currentRound.roundIsNormal) {
            holder.cardItemView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF8800"))
        }
        holder.clubNameItemView.text = currentRound.roundClub
        holder.dateItemView.text = currentRound.roundDate.toString()
        holder.hitCountItemView.text = currentRound.roundTotalHit.toString()
    }

    public interface OnItemClickListener {
        fun onItemClick(roundList: RoundList)
    }

    public fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


}