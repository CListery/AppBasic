@file:Suppress("unused")
@file:JvmName("ExtNumber")

package com.kotlin

import android.content.res.Resources


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
