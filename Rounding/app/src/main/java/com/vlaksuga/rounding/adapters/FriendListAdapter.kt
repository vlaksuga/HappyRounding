package com.vlaksuga.rounding.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.vlaksuga.rounding.R
import com.vlaksuga.rounding.model.User

class FriendListAdapter internal constructor(context: Context, friendList: List<User>) :
    RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder>() {

    companion object {
        const val TAG = "FriendListAdapter"
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    val mContext : Context = context
    private var items : List<User> = friendList
    private lateinit var mlistener : OnItemClickListener


    inner class FriendListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardItemView : CardView = itemView.findViewById(R.id.friendList_cardView)
        val nickNameItemView : TextView = itemView.findViewById(R.id.friendNickname_textView)
        val emailItemView : TextView = itemView.findViewById(R.id.friendId_TextView)

        fun bind(user: User) {
            cardItemView.setOnClickListener {
                mlistener.onItemClick(user)
            }
            nickNameItemView.text = user.userNickname
            emailItemView.text = user.userEmail
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

    public interface OnItemClickListener {
        fun onItemClick(user: User)
    }

    public fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mlistener = listener
    }
}