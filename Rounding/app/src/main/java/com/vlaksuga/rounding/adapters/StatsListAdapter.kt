package com.vlaksuga.rounding.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.vlaksuga.rounding.R
import com.vlaksuga.rounding.constructors.Stats

class StatsListAdapter internal constructor(context: Context, statsList : List<Stats>) :
    RecyclerView.Adapter<StatsListAdapter.StatsListViewHolder>() {

    companion object {
        const val TAG = "StatsListAdapter"
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val mContext : Context = context
    private var items : List<Stats> = statsList


    inner class StatsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val keyItemView : TextView = itemView.findViewById(R.id.statsTitle_textView)
        private val unitItemView : TextView = itemView.findViewById(R.id.statsUnit_textView)
        private val valueItemView : TextView = itemView.findViewById(R.id.statsValue_textView)

        fun bind(statsList: Stats) {
            keyItemView.text = statsList.statsKey
            unitItemView.text = statsList.statsUnit
            valueItemView.text = statsList.statsValue.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsListViewHolder {
        val itemView = inflater.inflate(R.layout.stats_list_item, parent, false)
        return StatsListViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: StatsListViewHolder, position: Int) {
        val currentStats = items[position]
        holder.bind(currentStats)
    }
}