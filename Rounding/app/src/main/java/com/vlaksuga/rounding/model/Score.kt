package com.vlaksuga.rounding.model

class Score (
    val scoreId : String,
    val userId : String,
    val roundId : String,
    val courseId : String,
    val hits : List<Int>
) {
}