package com.vlaksuga.rounding.data

data class Round(
    val roundId : String = "",
    val roundOwnerUserId : String = "",
    val roundDate: Long = 0,
    val roundClubId : String = "",
    val roundCourseIdList: List<String> = arrayListOf(),
    val roundPlayerIdList: List<String> = arrayListOf(),
    val isRoundEnd: Boolean = false
)