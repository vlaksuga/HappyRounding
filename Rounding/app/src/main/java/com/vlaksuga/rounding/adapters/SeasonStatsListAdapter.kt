package com.vlaksuga.rounding.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vlaksuga.rounding.R
import com.vlaksuga.rounding.model.Stats

class SeasonStatsListAdapter internal constructor(context: Context, statsList : List<Stats>) :
    RecyclerView.Adapter<SeasonStatsListAdapter.SeasonStatsListViewHolder>() {

    companion object {
        const val TAG = "SeasonStatsListAdapter"
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val mContext : Context = context
    private var items : List<Stats> = statsList


    inner class SeasonStatsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val keyItemView : TextView = itemView.findViewById(R.id.seasonStatsTitle_textView)
        private val unitItemView : TextView = itemView.findViewById(R.id.seasonStatsUnit_textView)
        private val valueItemView : TextView = itemView.findViewById(R.id.seasonStatsValue_textView)

        fun bind(statsList: Stats) {
            keyItemView.text = statsList.statsKey
            unitItemView.text = statsList.statsUnit
            valueItemView.text = statsList.statsValue.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonStatsListViewHolder {
        val itemView = inflater.inflate(R.layout.season_stats_list_item, parent, false)
        return SeasonStatsListViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: SeasonStatsListViewHolder, position: Int) {
        val currentStats = items[position]
        holder.bind(currentStats)
    }
}