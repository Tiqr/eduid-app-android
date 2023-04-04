package nl.eduid.ui

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun Long.getDateTimeString(pattern: String = "EEE dd MMM yyyy - HH:MM"): String {
    return try {
        val sdf = SimpleDateFormat(pattern)
        val netDate = Date(this)
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}

@SuppressLint("SimpleDateFormat")
fun Long.getDateString(pattern: String = "EEEE, MMMM dd, yyyy"): String {
    return try {
        val sdf = SimpleDateFormat(pattern)
        val netDate = Date(this)
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}