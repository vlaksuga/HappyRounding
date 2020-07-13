package com.vlaksuga.rounding.data

data class Round(
    val roundId : String = "",
    val roundOwnerUserId : String = "",
    val roundDate: Long = 0,
    val roundClubId : String = "",
    val roundCourseIdList: List<String> = arrayListOf(),
    val roundPlayerIdList: List<String> = arrayListOf(),
    val playerFirstScoreFirstList : List<Int> = arrayListOf(),
    val playerSecondScoreFirstList : List<Int> = arrayListOf(),
    val playerThirdScoreFirstList : List<Int> = arrayListOf(),
    val playerFourthScoreFirstList : List<Int> = arrayListOf(),
    val playerFirstScoreSecondList : List<Int> = arrayListOf(),
    val playerSecondScoreSecondList : List<Int> = arrayListOf(),
    val playerThirdScoreSecondList : List<Int> = arrayListOf(),
    val playerFourthScoreSecondList : List<Int> = arrayListOf(),
    val isRoundEnd: Boolean = false
)