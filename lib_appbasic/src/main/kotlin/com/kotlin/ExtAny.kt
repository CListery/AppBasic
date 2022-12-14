@file:Suppress("unused")
@file:JvmName("ExtAny")

package com.kotlin

import android.os.Build
import com.yh.appbasic.logger.logE

/**
 * 提供该对象的(伪)内存地址，只能用作参考
 *
 * * 请不要通过该值来比较对象是否相同
 */
val Any?.memoryId get() = System.identityHashCode(this).toString(16)

/**
 * 将对象数组转为字符串并排列为 "(字符串1): (字符串2)(字符串3)(字符串4)..."
 * 自动过滤空对象
 *
 * @param stringArray 对象数组
 */
fun getAppendStr(vararg stringArray: Any?) = getAppendStr(
    appendColon = true, stringArray = stringArray
)

/**
 * 将对象数组转为字符串并排列为 "(字符串1): (字符串2)(字符串3)(字符串4)..."
 * 自动过滤空对象
 *
 * @param appendColon 第一个字符串后是否需要追加 ": "
 * @param stringArray 对象数组
 */
fun getAppendStr(appendColon: Boolean, vararg stringArray: Any?) = getAppendStr(
    separator = ": ", appendColon = appendColon, stringArray = stringArray
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
    separator: String? = ": ",
    appendColon: Boolean,
    vararg stringArray: Any?,
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

/**
 * 安全执行 block，并返回执行结果
 *
 * @param [printCatching] 是否将错误信息输出到日志
 * @param [block] 需要执行的 block
 */
@JvmOverloads
inline fun <T, R> T?.runCatchingSafety(
    printCatching: Boolean = true,
    block: T.() -> R,
): Result<R> {
    val result = this?.runCatching(block) ?: Result.failure(NullPointerException("T is null"))
    if (result.isFailure && printCatching) {
        logE("invoke block failed!", throwable = result.exceptionOrNull())
    }
    return result
}

/**
 * 安全执行 block，并返回执行结果
 *
 * @param [printCatching] 是否将错误信息输出到日志
 * @param [block] 需要执行的 block
 */
@JvmOverloads
inline fun <R> runCatchingSafety(
    printCatching: Boolean = true,
    block: () -> R,
): Result<R> {
    val result = runCatching(block)
    if (result.isFailure && printCatching) {
        logE("invoke block failed!", throwable = result.exceptionOrNull())
    }
    return result
}

/**
 * 当运行环境 api 版本达到指定时运行
 */
inline fun <R> runOnApi(api: Int, onApi: () -> R, otherApi: () -> R): Result<R> {
    return runCatchingSafety {
        if (Build.VERSION.SDK_INT == api) {
            onApi()
        } else {
            otherApi()
        }
    }
}

/**
 * 当运行环境 api 版本低于指定版本时
 */
inline fun <R> runOnApiDown(api: Int, onBelow: () -> R): Result<R>? {
    return if (Build.VERSION.SDK_INT < api) {
        runCatchingSafety { onBelow() }
    } else {
        null
    }
}

/**
 * 当运行环境 api 版本高于指定版本时
 */
inline fun <R> runOnApiUp(api: Int, onAbove: () -> R): Result<R>? {
    return if (Build.VERSION.SDK_INT > api) {
        runCatchingSafety {
            onAbove()
        }
    } else {
        null
    }
}

/**
 * 当运行环境 api 版本达到指定时运行
 */
inline fun <R> runOnApiDown(api: Int, onBelow: () -> R, onAbove: () -> R): Result<R> {
    return runCatchingSafety {
        if (Build.VERSION.SDK_INT < api) {
            onBelow()
        } else {
            onAbove()
        }
    }
}

/**
 * 当运行环境 api 版本达到指定时运行
 */
inline fun <R> runOnApiUp(api: Int, onAbove: () -> R, onBelow: () -> R): Result<R> {
    return runCatchingSafety {
        if (Build.VERSION.SDK_INT > api) {
            onAbove()
        } else {
            onBelow()
        }
    }
}
