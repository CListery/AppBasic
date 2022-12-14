@file:Suppress("unused")
@file:JvmName("ExtNumber")

package com.kotlin

import android.content.res.Resources
import kotlin.math.round


private val NUMBER_STR = arrayOf("零", "一", "二", "三", "四", "五", "六", "七", "八", "九")

/**
 * 获取中文数字
 */
fun Number?.toChinaNumber(): String {
    if (null == this) {
        return NUMBER_STR[0]
    }
    runCatchingSafety {
        toInt() % 10
    }.getOrDefault(0).let { pos ->
        return NUMBER_STR[pos]
    }
}

/**
 * dp转px
 */
fun Number.dp2px(): Float {
    return runCatchingSafety {
        toFloat() * Resources.getSystem().displayMetrics.densityDpi / 160f
    }.getOrDefault(0F)
}

/**
 * 四舍五入，保留小数点后一位
 */
fun Float.round1() = round(this * 10.0) / 10.0f

/**
 * 四舍五入，保留小数点后一位
 */
fun Double.round1() = round(this * 10.0) / 10.0f

/**
 * 四舍五入，保留小数点后两位
 */
fun Float.round2() = round(this * 100.0) / 100.0f

/**
 * 四舍五入，保留小数点后两位
 */
fun Double.round2() = round(this * 100.0) / 100.0f
