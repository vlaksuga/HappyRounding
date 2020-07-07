package com.vlaksuga.rounding.model

data class RoundList (
    val roundId : String,
    val roundClub : String,
    val roundDate: Long,
    val roundTotalHit : Int,
    val roundIsNormal : Boolean
)