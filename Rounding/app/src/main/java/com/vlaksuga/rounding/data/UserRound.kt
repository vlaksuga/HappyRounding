package com.vlaksuga.rounding.data

import java.util.*

data class UserRound(
    val userUUID: String,
    val userName: String,
    val roundId: String,
    val roundOwnerUserUUID : String,
    val roundDate: Long,
    val roundClubId : String, val roundClubName: String,
    val roundFirstCourseId: String, val roundFirstCourseName: String, val roundFirstCourseParList : List<Int>,
    val roundSecondCourseId: String?, val roundSecondCourseName: String?, val roundSecondCourseParList : List<Int>,
    val roundPlayerFirstScoreList: List<Int>, val roundPlayerSecondScoreList: List<Int>,
    val isRoundEnd: Boolean
)