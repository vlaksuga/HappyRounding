package com.vlaksuga.rounding.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.vlaksuga.rounding.R
import com.vlaksuga.rounding.data.User

class FriendListAdapter internal constructor(context: Context, friendList: List<User>) :
    RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder>() {

    companion object {
        const val TAG = "FriendListAdapter"
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val mContext : Context = context
    private var items : List<User> = friendList


    inner class FriendListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardItemView : CardView = itemView.findViewById(R.id.friendList_cardView)
        val nickNameItemView : TextView = itemView.findViewById(R.id.friendNickname_textView)
        val idItemView : TextView = itemView.findViewById(R.id.friendId_TextView)

        fun bind(user: User) {
            nickNameItemView.text = user.userNickname
            idItemView.text = user.userId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendListViewHolder {
        val itemView = inflater.inflate(R.layout.friend_list_item, parent, false)
        return FriendListViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: FriendListViewHolder, position: Int) {
        val currentFriend = items[position]
        holder.bind(currentFriend)
    }
}