package com.vlaksuga.rounding.data

data class Round(
    val roundId: String,
    val roundDate: Long,
    val roundClubId: String,
    val roundFirstCourseId: String,
    val roundSecondCourseId: String?,
    val roundThirdCourseId: String?,
    val roundFirstPlayerId: String,
    val roundSecondPlayerId: String,
    val roundThirdPlayerId: String?,
    val roundFourthPlayerId: String?,
    val isRoundEnd: Boolean
)