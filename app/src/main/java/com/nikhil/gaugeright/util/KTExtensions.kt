package com.nikhil.gaugeright.util

import java.sql.Timestamp
import java.text.SimpleDateFormat

fun Timestamp.formatTimestamp(): String {
    val sdf = SimpleDateFormat("hh:mm:ss a dd-MM-yyyy", java.util.Locale.getDefault())
    return sdf.format(this)
}