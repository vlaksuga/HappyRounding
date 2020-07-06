package com.vlaksuga.rounding.adapters

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.vlaksuga.rounding.R
import com.vlaksuga.rounding.RoundResultActivity
import com.vlaksuga.rounding.data.RoundList

class RoundListAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<RoundListAdapter.RoundListViewHolder>() {

    companion object {
        const val TAG = "RoundListAdapter"
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val mContext: Context = context
    private val roundList = emptyList<RoundList>()

    private var items: List<RoundList> = arrayListOf(
        RoundList("1", "브라자 GC", 1111, 45, false),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("2", "브레이지어 GC", 2222, 103, true),
        RoundList("3", "라온", 3333, 123, true)
    )


    inner class RoundListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardItemView: CardView = itemView.findViewById(R.id.roundList_cardView)
        val clubNameItemView: TextView = itemView.findViewById(R.id.roundClubName_textView)
        val dateItemView: TextView = itemView.findViewById(R.id.roundDate_textView)
        val hitCountItemView: TextView = itemView.findViewById(R.id.roundTotalHit_textView)

        fun bind(roundList: RoundList) {
            if (!roundList.roundIsNormal) {
                cardItemView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF1122"))
            }
            cardItemView.setOnClickListener {
                Toast.makeText(mContext, "clicked: ", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "bind: ${roundList.roundClub}")
                val resultRoundIntent = Intent(mContext, RoundResultActivity::class.java)
                // TODO : 라운드를 클릭하면 라운드 정보를 인텐트에 담아서 라운드 결과 액티비티 실행
                mContext.startActivity(resultRoundIntent)
            }
            clubNameItemView.text = roundList.roundClub
            dateItemView.text = roundList.roundDate.toString()
            hitCountItemView.text = roundList.roundTotalHit.toString()
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