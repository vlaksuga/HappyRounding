package com.vlaksuga.rounding.data

data class Round(
    val roundOwnerUserUUID : String,
    val roundDate: Long,
    val roundClubId : String,
    val roundCourseIdList: List<String>,
    val roundPlayerList: List<String>,
    val isRoundEnd: Boolean
)