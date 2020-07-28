package com.vlaksuga.rounding.constructors

data class ResultRound (
    val resultRoundId : String = "",
    val resultUserEmail : String = "",
    val resultUserName : String = "",
    val resultDate : Long = 0,
    val resultSeason : Int = 0,
    val resultTeeTime : Long = 0,
    val resultClubId : String = "",
    val resultClubName : String = "",
    val resultCourseIdList : List<String> = arrayListOf(),
    val resultCourseNameList : List<String> = arrayListOf(),
    val resultFirstCourseParList : List<Int> = arrayListOf(),
    val resultSecondCourseParList : List<Int> = arrayListOf(),
    val resultCoPlayersEmailList : List<String> = arrayListOf(),
    val resultCoPlayersNicknameList : List<String> = arrayListOf(),
    val resultFirstScoreList : List<Int> = arrayListOf(),
    val resultSecondScoreList : List<Int> = arrayListOf()
)