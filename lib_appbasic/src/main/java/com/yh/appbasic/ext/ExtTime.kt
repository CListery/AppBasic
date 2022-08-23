package com.yh.appbasic.ext

import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by CYH on 2020-01-08 15:14
 */
const val DATE_PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy"
const val DATE_PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz"
const val DATE_PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz"

/**
 * 判断是否为之后的某天
 * @param [base] 基准时间
 * @param [now] 被计算的时间
 * @return true - now与base为同一天或之后
 */
fun isAfterDay(now: Long, base: Long): Boolean {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = now
    val nowYear = calendar.get(Calendar.YEAR)
    val nowMonth = calendar.get(Calendar.MONTH)
    val nowDay = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.timeInMillis = base
    val lastReceiveYear = calendar.get(Calendar.YEAR)
    val lastReceiveMonth = calendar.get(Calendar.MONTH)
    val lastReceiveDay = calendar.get(Calendar.DAY_OF_MONTH)

    return when {
        nowYear >= lastReceiveYear && nowMonth >= lastReceiveMonth && nowDay > lastReceiveDay -> {
            true
        }

        else -> {
            false
        }
    }
}

/**
 * 判断是否为同一天或之后
 * @param [base] 基准时间
 * @param [now] 被计算的时间
 * @return true - now与base为同一天或之后
 */
fun notBeforeDay(now: Long, base: Long): Boolean {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = now
    val nowYear = calendar.get(Calendar.YEAR)
    val nowMonth = calendar.get(Calendar.MONTH)
    val nowDay = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.timeInMillis = base
    val lastReceiveYear = calendar.get(Calendar.YEAR)
    val lastReceiveMonth = calendar.get(Calendar.MONTH)
    val lastReceiveDay = calendar.get(Calendar.DAY_OF_MONTH)
    
    return when {
        nowYear >= lastReceiveYear && nowMonth >= lastReceiveMonth && nowDay >= lastReceiveDay -> {
            true
        }
        
        else -> {
            false
        }
    }
}

/**
 * 获取时间的 "上午" or "下午"
 * @param timeStr 字符串时间戳(毫秒\秒)
 */
fun getAMorPM(timeStr: String?): String? {
    if(TextUtils.isEmpty(timeStr)) {
        return null
    }
    val time = time2Millis(timeStr)
    if(time <= 0) {
        return null
    }
    return getAMorPM(time)
}

/**
 * 获取时间的 "上午" or "下午"
 * @param time 时间戳(毫秒\秒)
 */
fun getAMorPM(time: Long): String {
    val calendar = Calendar.getInstance(Locale.CHINESE)
    calendar.timeInMillis = time
    if(Calendar.AM == calendar.get(Calendar.AM_PM)) {
        return "上午"
    }
    return "下午"
}

/**
 * 获取时间的星期
 * @param millis 时间戳(毫秒)
 */
fun getWeek(millis: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = millis
    return when(calendar.get(Calendar.DAY_OF_WEEK)) {
        1 -> "日"
        2 -> "一"
        3 -> "二"
        4 -> "三"
        5 -> "四"
        6 -> "五"
        7 -> "六"
        else -> ""
    }
}

/**
 * 转换时间戳为秒
 * @param time 时间戳(秒\毫秒)
 */
fun getSecond(time: Long): Int = (time / (if(time < 9999999999L) 1L else 1000L)).toInt()

/**
 * 获取当前时间戳(秒)
 */
fun getCurSecond() = getSecond(System.currentTimeMillis())

/**
 * 转换字符串时间戳为毫秒值
 * @param time 字符串时间戳(秒\毫秒)
 */
fun time2Millis(time: String?): Long {
    if(time?.isEmpty() != false) {
        return 0
    }
    return time2Millis(time.toLong())
}

/**
 * 转换时间戳为毫秒值
 * @param time 时间戳(秒\毫秒)
 */
fun time2Millis(time: Long): Long = if(time < 9999999999L) 1000L * time else time

/**
 * 格式化字符串时间戳(默认 yyyy-MM-dd HH:mm)
 * @param time 字符串时间戳(秒\毫秒)
 * @param pattern 时间格式
 */
fun formatDate(
    time: String?, pattern: String = "yyyy-MM-dd HH:mm"
): String = formatDate(time2Millis(time), pattern)

/**
 * 格式化时间戳(默认 yyyy-MM-dd HH:mm)
 * @param time 时间戳(秒\毫秒)
 * @param pattern 时间格式
 */
fun formatDate(
    time: Int?, pattern: String = "yyyy-MM-dd HH:mm"
): String = formatDate(time?.toLong(), pattern)

/**
 * 格式化时间戳 yyyy-MM-dd HH:mm
 * @param time 时间戳(秒\毫秒)
 */
fun formatDate(time: Long?): String = formatDate(time, "yyyy-MM-dd HH:mm")

/**
 * 格式化时间戳
 * @param time 时间戳(秒\毫秒)
 * @param pattern 时间格式
 */
fun formatDate(time: Long?, pattern: String): String =
    formatDate(time, pattern, Locale.CHINA)

/**
 * 格式化时间戳
 * @param time 时间戳(秒\毫秒)
 * @param pattern 时间格式
 * @param locale 区域
 */
fun formatDate(time: Long?, pattern: String, locale: Locale = Locale.CHINA): String {
    if(null != time && time > 0) {
        return SimpleDateFormat(pattern, locale).format(time2Millis(time))
    }
    return ""
}

/**
 * 解析时间为日历数据 (默认 yyyy-MM-dd HH:mm:ss)
 * @param str 对应pattern格式的字符串时间数据
 * @param pattern 解析格式
 */
fun parseDate(
    str: String?, pattern: String = "yyyy-MM-dd HH:mm:ss", locale: Locale = Locale.CHINESE
): Calendar? {
    val cal = Calendar.getInstance()
    if(null == str || TextUtils.isEmpty(str)) {
        return null
    }
    val sdf = SimpleDateFormat(pattern, locale)
    cal.time = sdf.parse(str) ?: Date()
    return cal
}

/**
 * 格式化通话时长
 */
fun formatDuration(duration: Long): String {
    val oneSecond = 1
    val oneMinute = 60 * oneSecond
    val oneHour = 60 * oneMinute
    return when {
        duration < oneMinute -> "${duration}秒"
        
        duration < oneHour   -> {
            val min = duration / oneMinute
            val sec = duration % oneMinute
            String.format("%d分%d秒", min, sec)
        }
        
        else                 -> {
            val hour = duration / oneHour
            val min = duration % oneHour / oneMinute
            val sec = duration % oneHour % oneMinute
            String.format("%d小时%d分%d秒", hour, min, sec)
        }
    }
}
