package com.vlaksuga.rounding.model

data class Course(
    val courseClubId : String = "",
    val courseId : String = "",
    val courseName : String = "",
    val courseParCount : List<Int> = arrayListOf(),
    val courseParLengthBack : List<Int> = arrayListOf(),
    val courseParLengthReg : List<Int> = arrayListOf(),
    val courseParLengthLady : List<Int> = arrayListOf(),
    val courseParLengthChamp : List<Int> = arrayListOf()
) {
}