package com.vlaksuga.rounding

data class Round (
    val roundId : String,
    val roundClub : String,
    val roundCourse : List<String>,
    val roundUser : List<String>,
    val roundDate: Long,
    val roundInfo : String
)