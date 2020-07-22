package com.vlaksuga.rounding.constructors

data class ResultRound (
    val resultRoundId : String = "",
    val resultUserId : String = "",
    val resultUserName : String = "",
    val resultClubName : String = "",
    val resultDate : String = "",
    val resultCoPlayers : String = "",
    val resultFirstCourseName : String = "",
    val resultSecondCourseName : String = "",
    val resultFirstCourseParList : List<Int> = arrayListOf(),
    val resultSecondCourseParList : List<Int> = arrayListOf(),
    val resultFirstScoreList : List<Int> = arrayListOf(),
    val resultSecondScoreList : List<Int> = arrayListOf()
)