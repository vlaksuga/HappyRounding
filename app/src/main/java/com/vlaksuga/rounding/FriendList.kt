package com.vlaksuga.rounding

import android.provider.ContactsContract

data class FriendList (
    val friendId : String,
    val friendNickname: String,
    val friendAverageHit : Int,
    val friendImage : Int
)