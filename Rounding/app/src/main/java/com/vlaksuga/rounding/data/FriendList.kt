package com.vlaksuga.rounding.data

import android.provider.ContactsContract

data class FriendList (
    val friendId : String,
    val friendNickname: String,
    val friendAverageHit : Int,
    val friendImage : Int
)