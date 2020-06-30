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

class FriendListAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var listener: OnItemClickListener

    private val friendList = emptyList<FriendList>()

    private var filterFriendListResult : List<FriendList> = arrayListOf(
        FriendList("1","강지형", 98, 45),
        FriendList("2","신주섭", 102, 45),
        FriendList("3","오빠바나나", 108, 45)
    )


    inner class FriendListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardItemView : CardView = itemView.findViewById(R.id.friendList_cardView)
        val nickNameItemView : TextView = itemView.findViewById(R.id.friendNickname_textView)
        val hitCountItemView : TextView = itemView.findViewById(R.id.friendAverageHit_textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendListViewHolder {
        val itemView = inflater.inflate(R.layout.friend_list_item, parent, false)
        return FriendListViewHolder(itemView)
    }

    override fun getItemCount() = filterFriendListResult.size

    override fun onBindViewHolder(holder: FriendListViewHolder, position: Int) {
        val currentFriend = filterFriendListResult[position]
        holder.cardItemView.setOnClickListener {
            listener.onItemClick(currentFriend)
        }
        holder.nickNameItemView.text = currentFriend.friendNickname
        holder.hitCountItemView.text = currentFriend.friendAverageHit.toString()
    }

    public interface OnItemClickListener {
        fun onItemClick(friendList: FriendList)
    }

    public fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


}