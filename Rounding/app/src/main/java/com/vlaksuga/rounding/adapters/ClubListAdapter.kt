package com.vlaksuga.rounding.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.vlaksuga.rounding.R
import com.vlaksuga.rounding.data.Club
import java.util.*
import kotlin.collections.ArrayList

class ClubListAdapter internal constructor(context: Context, clubList : List<Club>) : RecyclerView.Adapter<ClubListAdapter.ClubListViewHolder> (), Filterable {

    companion object {
        const val TAG = "ClubListAdapter"
    }

    private val mContext : Context = context
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val clubs = emptyList<Club>()
    private var items : List<Club> = clubList
    inner class ClubListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val cardItemView = itemView.findViewById<CardView>(R.id.clubList_cardView)
        val titleItemView = itemView.findViewById<TextView>(R.id.setRoundClubName_textView)

        fun bind(club: Club) {
            cardItemView.setOnClickListener {

                Log.d(TAG, "clicked : ${club.clubName}")
            }
            titleItemView.text = club.clubName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubListAdapter.ClubListViewHolder {
        val itemView = inflater.inflate(R.layout.club_list_item, parent, false)
        return ClubListViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ClubListAdapter.ClubListViewHolder, position: Int) {
        val currentClub = items[position]
        holder.bind(currentClub)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charString: CharSequence?): FilterResults {
                val charSearch : String = charString.toString().trim()
                if(charString!!.isEmpty()) {
                    items = clubs
                } else {
                    val resultList = ArrayList<Club>()
                    for (row in items) {
                        if (row.clubSearchInfo.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                }
                val filterResult = FilterResults()
                filterResult.values = items
                return filterResult
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, filterResult: FilterResults?) {
                items = filterResult!!.values as List<Club>
                notifyDataSetChanged()
            }

        }
    }
}