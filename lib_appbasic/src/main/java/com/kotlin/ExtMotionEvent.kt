@file:Suppress("unused")
@file:JvmName("ExtMotionEvent")

package com.kotlin

import android.view.MotionEvent


/**
 * 将触摸数据转为字符串(日志)
 */
fun MotionEvent?.toShortString(): String {
    if (null == this) {
        return "null"
    }
    when (action) {
        MotionEvent.ACTION_DOWN -> return "ACTION_DOWN"
        MotionEvent.ACTION_UP -> return "ACTION_UP"
        MotionEvent.ACTION_CANCEL -> return "ACTION_CANCEL"
        MotionEvent.ACTION_OUTSIDE -> return "ACTION_OUTSIDE"
        MotionEvent.ACTION_MOVE -> return "ACTION_MOVE"
        MotionEvent.ACTION_HOVER_MOVE -> return "ACTION_HOVER_MOVE"
        MotionEvent.ACTION_SCROLL -> return "ACTION_SCROLL"
        MotionEvent.ACTION_HOVER_ENTER -> return "ACTION_HOVER_ENTER"
        MotionEvent.ACTION_HOVER_EXIT -> return "ACTION_HOVER_EXIT"
        MotionEvent.ACTION_BUTTON_PRESS -> return "ACTION_BUTTON_PRESS"
        MotionEvent.ACTION_BUTTON_RELEASE -> return "ACTION_BUTTON_RELEASE"
    }
    val index = action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
    return when (action and MotionEvent.ACTION_MASK) {
        MotionEvent.ACTION_POINTER_DOWN -> "ACTION_POINTER_DOWN($index)"
        MotionEvent.ACTION_POINTER_UP -> "ACTION_POINTER_UP($index)"
        else -> action.toString()
    }
}
