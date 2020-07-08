package com.vlaksuga.rounding.data

data class Round(
    val roundOwnerUserId : String,
    val roundDate: Long,
    val roundClubId : String,
    val roundCourseIdList: List<String>,
    val roundPlayerIdList: List<String>,
    val isRoundEnd: Boolean
)