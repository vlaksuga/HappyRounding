package com.vlaksuga.rounding.model

data class User (
    val userEmail : String = "",
    val userNickname: String = "",
    val userId : String = "",
    val userPhone : String = "",
    val userAllowFriendList : ArrayList<String> = arrayListOf(),
    val userTeeType : String = ""
)