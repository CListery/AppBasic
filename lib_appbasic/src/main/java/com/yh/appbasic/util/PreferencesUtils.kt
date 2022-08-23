@file:JvmName("PreferenceUtils")

package com.yh.appbasic.util

import android.content.Context
import android.content.SharedPreferences

private object PrefConst {
    const val DEF_FILE_NAME = "common"
}

private val preferences = hashMapOf<String, SharedPreferences>()

fun Context.preference(preferenceName: String?): SharedPreferences {
    return preferences.getOrPut(
        preferenceName ?: PrefConst.DEF_FILE_NAME,
        defaultValue = {
            getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        },
    )
}

/**
 * @param [key] key
 * @param [value] if null, well be remove this [key]
 */
@JvmOverloads
fun <T : Any> savePref(
    context: Context,
    key: String,
    value: T?,
    clazz: Class<T>? = value?.javaClass,
    preferenceName: String? = null,
): Boolean {
    return context.preference(preferenceName).innerSavePref(key, value, clazz)
}

/**
 * @param [key] key
 * @param [value] if null, well be remove this [key]
 */
inline fun <reified T : Any> Context.savePref(
    key: String,
    value: T?,
    preferenceName: String? = null,
): Boolean {
    return preference(preferenceName).innerSavePref(key, value)
}

private fun <T : Any> SharedPreferences.innerSavePref(
    key: String,
    value: T?,
    clazz: Class<T>?
): Boolean {
    return edit().apply {
        when (clazz) {
            String::class.java -> putString(key, value as String)
            Double::class.java, Float::class.java -> putFloat(key, value as Float)
            Long::class.java -> putLong(key, value as Long)
            Int::class.java -> putInt(key, value as Int)
            Boolean::class.java -> putBoolean(key, value as Boolean)
            MutableSet::class.java -> {
                val set = value as MutableSet<*>
                putStringSet(key, set.map { it.toString() }.toSet())
            }
            
            else -> remove(key)
        }
    }.commit()
}

inline fun <reified T : Any> SharedPreferences.innerSavePref(key: String, value: T?): Boolean {
    return edit().apply {
        when (value) {
            is String -> putString(key, value)
            is Double, Float -> putFloat(key, value as Float)
            is Long -> putLong(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is MutableSet<*> -> putStringSet(key, value.map { it.toString() }.toSet())
            else -> remove(key)
        }
    }.commit()
}

fun <T : Any> getPref(
    context: Context,
    key: String,
    defValue: T,
    clazz: Class<T> = defValue.javaClass,
    preferenceName: String? = null,
): T {
    return context.preference(preferenceName).innerGetPref(key, defValue, clazz)
}

inline fun <reified T : Any> Context.getPref(
    key: String,
    defValue: T,
    preferenceName: String? = null,
): T {
    return preference(preferenceName).innerGetPref(key, defValue)
}

private fun <T : Any> SharedPreferences.innerGetPref(
    key: String,
    defValue: T,
    clazz: Class<T>
): T {
    @Suppress("UNCHECKED_CAST")
    if (contains(key)) {
        @Suppress("IMPLICIT_CAST_TO_ANY")
        return (when (clazz) {
            String::class.java -> getString(key, defValue as String)
            Float::class.java -> getFloat(key, defValue as Float)
            Long::class.java -> getLong(key, defValue as Long)
            Int::class.java -> getInt(key, defValue as Int)
            Boolean::class.java -> getBoolean(key, defValue as Boolean)
            MutableList::class.java -> getStringSet(key, defValue as MutableSet<String>)
            else -> throw RuntimeException("Can not support type:${clazz.simpleName} for get $key")
        }) as T
    }
    return defValue
}

inline fun <reified T : Any> SharedPreferences.innerGetPref(key: String, defValue: T): T {
    if (contains(key)) {
        @Suppress("IMPLICIT_CAST_TO_ANY") return (when (T::class) {
            String::class -> getString(key, defValue as String)
            Float::class -> getFloat(key, defValue as Float)
            Long::class -> getLong(key, defValue as Long)
            Int::class -> getInt(key, defValue as Int)
            Boolean::class -> getBoolean(key, defValue as Boolean)
            MutableList::class -> @Suppress("UNCHECKED_CAST") getStringSet(
                key, defValue as MutableSet<String>
            )
            else -> throw RuntimeException("Can not support type:${T::class.simpleName} for get $key")
        }) as T
    }
    return defValue
}