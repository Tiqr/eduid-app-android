package nl.eduid.ui

import android.annotation.SuppressLint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
fun Long.getDateTimeString(pattern: String = "EEE dd MMM yyyy - HH:MM"): String = try {
    val sdf = SimpleDateFormat(pattern)
    val netDate = Date(this)
    sdf.format(netDate)
} catch (e: Exception) {
    Timber.e(e, "Failed to format date")
    ""
}

@SuppressLint("SimpleDateFormat")
fun Long.getDateString(pattern: String = "EEEE, MMMM dd, yyyy"): String = try {
    val sdf = SimpleDateFormat(pattern)
    val netDate = Date(this)
    sdf.format(netDate)
} catch (e: Exception) {
    Timber.e(e, "Failed to format date")
    ""
}

@SuppressLint("SimpleDateFormat")
fun String.formatStringDate(): String = try {
    val parse = SimpleDateFormat("yyyy-M-dd'T'HH:mm:ss.SSSXXX")
    val asDate = parse.parse(this)

    val formatter = SimpleDateFormat("EEEE, MMMM dd, yyyy")
    formatter.format(asDate)
} catch (e: Exception) {
    Timber.e(e, "Failed to format string date $this")
    ""
}