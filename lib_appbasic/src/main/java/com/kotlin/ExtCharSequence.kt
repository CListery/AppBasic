@file:Suppress("unused")
@file:JvmName("ExtCharSequence")

package com.kotlin

inline fun String.Companion.safeFormat(format: String, vararg args: Any?): String {
    val safeArgs = args.map { it.safeGet { "" } }.toTypedArray()
    return String.format(format, *safeArgs)
}

inline fun <T : CharSequence> Array<out T?>.filterNotEmpty(): List<T> {
    return filterNotNull().filterNotTo(arrayListOf()) { it.isEmpty() }
}

fun CharSequence.filterNumber(): String {
    var number = ""
    forEach { c ->
        if (c.isDigit()) {
            number += c
        }
    }
    return number
}
