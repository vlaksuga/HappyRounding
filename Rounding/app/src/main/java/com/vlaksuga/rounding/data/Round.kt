package com.vlaksuga.rounding.data

data class Round(
    val roundId: String,
    val roundOwnerUserUUID : String,
    val roundDate: Long,
    val roundClubId : String, val roundClubName: String,
    val roundFirstCourseId: String, val roundFirstCourseName: String, val roundFirstCourseParList : List<Int>,
    val roundSecondCourseId: String?, val roundSecondCourseName: String?, val roundSecondCourseParList : List<Int>,
    val roundFirstPlayerUUId: String, val roundFirstPlayerFirstScoreList: List<Int>, val roundFirstPlayerSecondScoreList: List<Int>,
    val roundSecondPlayerUUId: String, val roundSecondPlayerFirstScoreList: List<Int>, val roundSecondPlayerSecondScoreList: List<Int>,
    val roundThirdPlayerUUId: String?, val roundThirdPlayerFirstScoreList: List<Int>?, val roundThirdPlayerSecondScoreList: List<Int>?,
    val roundFourthPlayerUUId: String?, val roundFourthPlayerFirstScoreList: List<Int>?, val roundFourthPlayerSecondScoreList: List<Int>?,
    val isRoundEnd: Boolean
)