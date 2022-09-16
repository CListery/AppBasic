@file:Suppress("unused")
@file:JvmName("ExtTime")

package com.kotlin

import androidx.annotation.IntDef
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * Created by CYH on 2020-01-08 15:14
 */
const val DATE_PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy"
const val DATE_PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz"
const val DATE_PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz"

/**
 * 秒/天
 */
const val SECOND_OF_DAY = 24 * 60 * 60

/**
 * 毫秒/天
 */
const val SECOND_MILLS_OF_DAY = SECOND_OF_DAY * 1000L

@IntDef(value = [
    Calendar.SUNDAY,
    Calendar.MONDAY,
    Calendar.TUESDAY,
    Calendar.WEDNESDAY,
    Calendar.THURSDAY,
    Calendar.FRIDAY,
    Calendar.SATURDAY
])
@Retention(AnnotationRetention.SOURCE)
private annotation class DayType

enum class TimeDayOnWeek(
    @DayType
    val dayOfWeek: Int,
    @JvmField
    var weekName: String,
) {
    UNKNOWN(-1, "未知"),
    SUNDAY(Calendar.SUNDAY, "日"),
    MONDAY(Calendar.MONDAY, "一"),
    TUESDAY(Calendar.TUESDAY, "二"),
    WEDNESDAY(Calendar.WEDNESDAY, "三"),
    THURSDAY(Calendar.THURSDAY, "四"),
    FRIDAY(Calendar.FRIDAY, "五"),
    SATURDAY(Calendar.SATURDAY, "六"),
    ;
    
    companion object {
        @JvmStatic
        fun week(calendar: Calendar): String =
            values().getOrElse(calendar.get(Calendar.DAY_OF_WEEK)) { UNKNOWN }.weekName
    }
}

/**
 * @see Long.timeDifferenceDay
 */
fun Calendar.differenceDay(base: Calendar): Int = timeInMillis.timeDifferenceDay(base.timeInMillis)

/**
 * 计算当前时间相差基准时间多少天，四舍五入
 *
 * @param [base] 基准时间
 *
 * @return
 *  - 结果为负，则表示位于基准时间之前多少天
 *  - 结果为正，则表示位于基准时间之前多少天
 */
fun Long.timeDifferenceDay(base: Long): Int =
    (minus(base).toFloat() / SECOND_MILLS_OF_DAY).roundToInt()

val Long.time2Calendar: Calendar
    get() = Calendar.getInstance().apply {
        timeInMillis = this@time2Calendar
    }

/**
 * 判断当前时间戳是否大于基准时间至少一天
 * 判断时间戳位于基准时间至少后一天 (now - base) >= 1day
 *
 * @param [base] 基准时间
 */
fun Long.timeIsAfterDay(base: Long): Boolean = timeDifferenceDay(base) > 0

/**
 * 判断时间戳位于基准时间至少前一天 (now - base) <= -1day
 *
 * @param [base] 基准时间
 */
fun Long.timeIsBeforeDay(base: Long): Boolean = timeDifferenceDay(base) > 0

/**
 * 判断是否为同一天或之后 now == base || (now - base) >= 1day
 *
 * @param [base] 基准时间
 */
fun Long.timeIsSameOrAfterDay(base: Long): Boolean = timeDifferenceDay(base) >= 0

/**
 * 字符串转 "上午" or "下午"
 *
 * 字符串时间戳(毫秒\秒)
 */
val String?.timeAmPm: String?
    get() {
        val time = time2Millisecond
        if (time <= 0) {
            return null
        }
        return time.timeAMorPM
    }


/**
 * 时间戳转 "上午" or "下午"
 *
 * 时间戳(毫秒\秒)
 */
val Long.timeAMorPM: String
    get() {
        val calendar = time2Calendar
        if (Calendar.AM == calendar.get(Calendar.AM_PM)) {
            return "上午"
        }
        return "下午"
    }

/**
 * 时间戳转星期
 *
 * 一,二,三,四,五,六,日
 *
 * 时间戳(毫秒)
 */
val Long.time2Week: String get() = time2Week(emptyList())

/**
 * 时间戳转星期
 *
 * @param [weeks] 星期集合，星期日开始
 */
fun Long.time2Week(weeks: List<TimeDayOnWeek>): String {
    val calendar = time2Calendar
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    return weeks.find { it.dayOfWeek == dayOfWeek }?.weekName ?: TimeDayOnWeek.week(calendar)
}

/**
 * 转换时间戳为秒
 *
 * 时间戳(秒\毫秒)
 */
val Long?.time2Second: Int
    get() {
        if (null == this) {
            return 0
        }
        return (if (compareTo(9999999999L) > 0) div(1000) else this).toInt()
    }

/**
 * 获取当前时间戳(秒)
 */
val timeCurSecond get() = System.currentTimeMillis().time2Second

/**
 * 获取当前时间戳(毫秒)
 */
val timeCurMillisecond get() = System.currentTimeMillis()

/**
 * 转换字符串为毫秒单位时间戳，如果给定的数值单位为秒，则自动转换为毫秒
 */
val String?.time2Millisecond: Long
    get() {
        if (isNullOrEmpty()) {
            return 0
        }
        return toLongOrNull().time2Millisecond
    }

/**
 * 转换数值为毫秒单位时间戳，如果给定的数值单位为秒，则自动转换为毫秒
 */
val Long?.time2Millisecond: Long
    get() {
        if (null == this) {
            return 0L
        }
        return if (compareTo(9999999999L) > 0) this else times(1000L)
    }

/**
 * 格式化字符串时间戳(默认 yyyy-MM-dd HH:mm)
 *
 * 字符串时间戳(秒\毫秒)
 *
 * @param pattern 时间格式
 */
@JvmOverloads
fun String?.timeFormatDate(
    pattern: String = "yyyy-MM-dd HH:mm",
): String = time2Millisecond.timeFormatDate(pattern)

/**
 * 格式化时间戳(默认 yyyy-MM-dd HH:mm)
 *
 * 时间戳(秒\毫秒)
 *
 * @param pattern 时间格式
 */
@JvmOverloads
fun Int?.timeFormatDate(
    pattern: String = "yyyy-MM-dd HH:mm",
): String = this?.toLong().timeFormatDate(pattern)

/**
 * 格式化时间戳
 *
 * 时间戳(秒\毫秒)
 *
 * @param pattern 时间格式
 * @param locale 区域
 */
@JvmOverloads
fun Long?.timeFormatDate(
    pattern: String = "yyyy-MM-dd HH:mm",
    locale: Locale = Locale.getDefault(),
): String {
    val millisecond = time2Millisecond
    if (millisecond > 0) {
        return SimpleDateFormat(pattern, locale).format(millisecond)
    }
    return ""
}

/**
 * 解析时间为日历数据 (默认 yyyy-MM-dd HH:mm:ss)
 *
 * 对应 pattern 格式的字符串时间数据
 *
 * @param pattern 解析格式
 */
fun String?.timeParseDate(
    pattern: String = "yyyy-MM-dd HH:mm:ss",
    locale: Locale = Locale.getDefault(),
): Calendar? {
    if (isNullOrEmpty()) {
        return null
    }
    val cal = Calendar.getInstance()
    val sdf = SimpleDateFormat(pattern, locale)
    cal.time = sdf.parse(this) ?: Date()
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
        
        duration < oneHour -> {
            val min = duration / oneMinute
            val sec = duration % oneMinute
            String.format("%d分%d秒", min, sec)
        }
        
        else -> {
            val hour = duration / oneHour
            val min = duration % oneHour / oneMinute
            val sec = duration % oneHour % oneMinute
            String.format("%d小时%d分%d秒", hour, min, sec)
        }
    }
}
