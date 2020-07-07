package com.vlaksuga.rounding.model

data class ResultRound (
    val resultUserName : String,
    val resultClubName : String,
    val resultDate : String,
    val resultCoPlayers : String,
    val resultFirstCourseName : String,
    val resultSecondCourseName : String,
    val resultFirstCourseParList : List<Long>,
    val resultSecondCourseParList : List<Long>,
    val resultFirstScoreList : List<Long>,
    val resultSecondScoreList : List<Long>
)