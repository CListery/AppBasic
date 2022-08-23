@file:Suppress("unused")
@file:JvmName("ExtAny")

package com.kotlin

import com.yh.appbasic.logger.logE


/**
 * 将对象数组转为字符串并排列为 "(字符串1): (字符串2)(字符串3)(字符串4)..."
 * 自动过滤空对象
 *
 * @param stringArray 对象数组
 */
fun getAppendStr(vararg stringArray: Any?) = getAppendStr(
    appendColon = true, stringArray = *stringArray
)

/**
 * 将对象数组转为字符串并排列为 "(字符串1): (字符串2)(字符串3)(字符串4)..."
 * 自动过滤空对象
 *
 * @param appendColon 第一个字符串后是否需要追加 ": "
 * @param stringArray 对象数组
 */
fun getAppendStr(appendColon: Boolean, vararg stringArray: Any?) = getAppendStr(
    separator = ": ", appendColon = appendColon, stringArray = *stringArray
)

/**
 * 将对象数组转为字符串并排列为 "(字符串1): (字符串2)(字符串3)(字符串4)..."
 * 自动过滤空对象
 *
 * @param separator 第一个字符串后需要追加的内容 (默认 ": ")
 * @param appendColon 第一个字符串后是否需要追加separator
 * @param stringArray 对象数组
 */
fun getAppendStr(
    separator: String? = ": ", appendColon: Boolean, vararg stringArray: Any?
) = StringBuilder().apply {
    stringArray.forEachIndexed { index, str ->
        when (str) {
            is Any -> {
                append(str.toString())
                if (0 == index && appendColon && stringArray.size > 1) {
                    append(separator)
                }
            }
        }
    }
}

@JvmOverloads
inline fun <T, R> T?.runCatchingSafety(
    printCatching: Boolean = true,
    block: T.() -> R
): Result<R> {
    val result = this?.runCatching(block) ?: Result.failure(NullPointerException("T is null"))
    if (result.isFailure && printCatching) {
        logE("invoke block failed!", throwable = result.exceptionOrNull())
    }
    return result
}
