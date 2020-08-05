package com.vlaksuga.rounding.model

data class Round(
    val roundId : String = "",
    val roundOwner : String = "",
    val roundDate : Long = 0,
    val roundSeason : Int = 0,
    val roundTeeTime : Long = 0,
    val roundClubId : String = "",
    val roundClubName : String = "",
    val roundCourseIdList : List<String> = arrayListOf(),
    val roundCourseNameList : List<String> = arrayListOf(),
    val roundPlayerEmailList : List<String> = arrayListOf(),
    val roundPlayerNicknameList : List<String> = arrayListOf(),
    val isLiveScoreCreated : Boolean = false,
    val isRoundOpen : Boolean = true
)