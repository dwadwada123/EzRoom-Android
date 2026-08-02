package com.example.ezroom.util

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    fun formatSmartTime(rawTime: String?): String {
        if (rawTime.isNullOrEmpty()) return ""

        // If it's already a short relative time like "5 phút trước" or "Vừa xong", keep it as is
        if (rawTime.contains("phút") || rawTime.contains("vừa xong") || rawTime.contains("Vừa xong")) {
            return rawTime
        }

        val date = parseToDate(rawTime) ?: return rawTime

        val targetCal = Calendar.getInstance().apply { time = date }
        val nowCal = Calendar.getInstance()

        val isToday = isSameDay(targetCal, nowCal)

        val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val isYesterday = isSameDay(targetCal, yesterdayCal)

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeStr = timeFormat.format(date)

        return when {
            isToday -> "Hôm nay, $timeStr"
            isYesterday -> "Hôm qua, $timeStr"
            else -> {
                val isSameYear = targetCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR)
                val dateFormatPattern = if (isSameYear) "dd/MM, HH:mm" else "dd/MM/yyyy, HH:mm"
                SimpleDateFormat(dateFormatPattern, Locale.getDefault()).format(date)
            }
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun parseToDate(rawTime: String): Date? {
        // Try parsing Long millis
        rawTime.toLongOrNull()?.let {
            return Date(it)
        }

        val patterns = arrayOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",
            "dd/MM/yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm",
            "dd/MM/yyyy",
            "yyyy-MM-dd HH:mm:ss"
        )

        for (pattern in patterns) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.getDefault())
                if (pattern.endsWith("'Z'")) {
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                }
                return sdf.parse(rawTime)
            } catch (_: Exception) {
            }
        }
        return null
    }
}
