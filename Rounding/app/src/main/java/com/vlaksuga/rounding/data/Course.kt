package com.vlaksuga.rounding.data

data class Course(
    val courseId : String,
    val courseClubId : String,
    val courseName : String,
    val courseParCount : List<Int>,
    val courseParLength : List<Int>
) {
}