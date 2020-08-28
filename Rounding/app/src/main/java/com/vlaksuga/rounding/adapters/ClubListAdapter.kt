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
import com.vlaksuga.rounding.model.Club
import java.util.*
import kotlin.collections.ArrayList

class ClubListAdapter internal constructor(context: Context, clubList: List<Club>) :
    RecyclerView.Adapter<ClubListAdapter.ClubListViewHolder>(), Filterable {

    companion object {
        const val TAG = "ClubListAdapter"
    }

    private val mContext: Context = context
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val emptyClubs = emptyList<Club>()
    private var items: List<Club> = clubList
    private val initClubList = clubList
    private lateinit var mlistener: OnItemClickListener

    inner class ClubListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardItemView: CardView = itemView.findViewById(R.id.clubList_cardView)
        private val titleItemView : TextView = itemView.findViewById(R.id.setRoundClubName_textView)
        private val areaItemView : TextView = itemView.findViewById(R.id.setRoundClubArea_textView)


        fun bind(club: Club) {
            cardItemView.setOnClickListener {
                mlistener.onItemClick(club)
            }
            titleItemView.text = club.clubName
            areaItemView.text = club.clubArea
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClubListAdapter.ClubListViewHolder {
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
                val charSearch: String = charString.toString().trim()
                items = if (charString!!.isEmpty()) {
                    initClubList
                } else {
                    val resultList = arrayListOf<Club>()
                    for (row in initClubList) {
                        Log.d(TAG, "performFiltering: $row")
                        if (row.clubSearchInfo.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                            Log.d(TAG, "performFiltering: added $resultList")
                            Log.d(TAG, "performFiltering: selected row $row")
                        }
                    }
                    resultList
                }

                val filterResult = FilterResults()
                filterResult.values = items
                return filterResult
            }


        @Suppress("UNCHECKED_CAST")
        override fun publishResults(
            constraint: CharSequence?,
            filterResult: FilterResults?
        ) {
            items = filterResult!!.values as List<Club>
            notifyDataSetChanged()
            Log.d(TAG, "publishResults: notifyDataSetChanged()")
        }

    }
}


public interface OnItemClickListener {
    fun onItemClick(club: Club)
}

public fun setOnItemClickListener(listener: OnItemClickListener) {
    this.mlistener = listener
}

}
