package com.vlaksuga.rounding.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vlaksuga.rounding.data.FriendList
import com.vlaksuga.rounding.R
import com.vlaksuga.rounding.constructors.ResultRound
import com.vlaksuga.rounding.data.User

class PlayerListAdapter internal constructor(context: Context, playerList: List<User>) :
    RecyclerView.Adapter<PlayerListAdapter.PlayerListViewHolder>() {

    companion object {
        const val TAG = "PlayerListAdapter"
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val mContext: Context = context
    private val playerList = emptyList<User>()
    private var items: List<User> = playerList
    var selectedPlayerList = arrayListOf<String>("오빠바나나")

    inner class PlayerListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardItemView: CardView = itemView.findViewById(R.id.playerList_cardView)
        val nickNameItemView: TextView = itemView.findViewById(R.id.playerNickname_textView)
        val checkItemView: ImageView = itemView.findViewById(R.id.playerChecked_imageView)

        fun bind(playerList: User) {
            cardItemView.setOnClickListener {
                if (checkItemView.visibility == View.GONE) {
                    if(selectedPlayerList.size < 4) {
                        selectedPlayerList.add(playerList.userNickname)
                        checkItemView.visibility = View.VISIBLE
                        notifyDataSetChanged()
                        Log.d(TAG, "click: ${playerList.userNickname} ")
                        Log.d(TAG, "VIEW: VISIBLE ")
                        Log.d(TAG, "PLAYERLIST: ${selectedPlayerList.size} ")
                    } else {
                        Toast.makeText(mContext, "인원이 찼습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    selectedPlayerList.remove(playerList.userNickname)
                    checkItemView.visibility = View.GONE
                    Log.d(TAG, "click: ${playerList.userNickname} ")
                    Log.d(TAG, "VIEW: GONE ")
                }
            }
            nickNameItemView.text = playerList.userNickname
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerListViewHolder {
        val itemView = inflater.inflate(R.layout.player_list_item, parent, false)
        return PlayerListViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: PlayerListViewHolder, position: Int) {
        val currentPlayer = items[position]
        holder.bind(currentPlayer)
    }
}
