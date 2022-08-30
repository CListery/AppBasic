@file:Suppress("unused", "FunctionName") @file:JvmName("Logs")

package com.yh.appbasic.logger

import android.database.Cursor
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import org.json.JSONArray
import org.json.JSONObject

/**
 * 输出 [Log.DEBUG] 日志
 * @param [msg] 要输出的日志内容
 * @param [loggable] [LogsManager.switchPrinter]
 * @param [tag] 日志消息的给定标签
 * @param [args] 需要格式化到 [msg] 中的内容
 * @see Log.DEBUG
 */
@JvmOverloads
fun logD(
    @Nullable msg: Any?,
    @Nullable loggable: ILoggable? = null,
    @Nullable tag: String? = null,
    @Nullable vararg args: Any? = emptyArray(),
) {
    LogsManager.switchPrinter(loggable).t(tag).d(message = msg?.toString(), args = args)
}

/**
 * 输出携带异常信息的 [Log.ERROR] 日志
 * @param [msg] 要输出的日志内容
 * @param [throwable] 异常信息
 * @param [loggable] [LogsManager.switchPrinter]
 * @param [tag] 日志消息的给定标签
 * @param [args] 需要格式化到 [msg] 中的内容
 * @see Log.ERROR
 */
@JvmOverloads
fun logE(
    @Nullable msg: Any? = null,
    @Nullable throwable: Throwable? = null,
    @Nullable loggable: ILoggable? = null,
    @Nullable tag: String? = null,
    @Nullable vararg args: Any? = emptyArray(),
) {
    LogsManager.switchPrinter(loggable).t(tag).e(throwable = throwable, message = msg?.toString(), args = args)
}

/**
 * 输出 [Log.INFO] 日志
 * @param [msg] 要输出的日志内容
 * @param [loggable] [LogsManager.switchPrinter]
 * @param [tag] 日志消息的给定标签
 * @param [args] 需要格式化到 [msg] 中的内容
 * @see Log.INFO
 */
@JvmOverloads
fun logI(
    @Nullable msg: Any?,
    @Nullable loggable: ILoggable? = null,
    @Nullable tag: String? = null,
    @Nullable vararg args: Any? = emptyArray(),
) {
    LogsManager.switchPrinter(loggable).t(tag).i(message = msg?.toString(), args = args)
}

/**
 * 输出 [Log.VERBOSE] 日志
 * @param [msg] 要输出的日志内容
 * @param [loggable] [LogsManager.switchPrinter]
 * @param [tag] 日志消息的给定标签
 * @param [args] 需要格式化到 [msg] 中的内容
 * @see Log.VERBOSE
 */
@JvmOverloads
fun logV(
    @Nullable msg: Any?,
    @Nullable loggable: ILoggable? = null,
    @Nullable tag: String? = null,
    @Nullable vararg args: Any? = emptyArray(),
) {
    LogsManager.switchPrinter(loggable).t(tag).v(message = msg?.toString(), args = args)
}

/**
 * 输出 [Log.WARN] 日志
 * @param [msg] 要输出的日志内容
 * @param [loggable] [LogsManager.switchPrinter]
 * @param [tag] 日志消息的给定标签
 * @param [args] 需要格式化到 [msg] 中的内容
 * @see Log.WARN
 */
@JvmOverloads
fun logW(
    @Nullable msg: Any?,
    @Nullable loggable: ILoggable? = null,
    @Nullable tag: String? = null,
    @Nullable vararg args: Any? = emptyArray(),
) {
    LogsManager.switchPrinter(loggable).t(tag).w(message = msg?.toString(), args = args)
}

/**
 * 输出 [Log.ASSERT] 日志
 * @param [msg] 要输出的日志内容
 * @param [loggable] [LogsManager.switchPrinter]
 * @param [tag] 日志消息的给定标签
 * @param [args] 需要格式化到 [msg] 中的内容
 * @see Log.ASSERT
 */
@JvmOverloads
fun logWTF(
    @Nullable msg: Any?,
    @Nullable loggable: ILoggable? = null,
    @Nullable tag: String? = null,
    @Nullable vararg args: Any? = emptyArray(),
) {
    LogsManager.switchPrinter(loggable).t(tag).wtf(message = msg?.toString(), args = args)
}

/**
 * 输出 JSON 文本日志
 * @param [json] JSON文本，支持 [CharSequence]、[JSONObject]、[JSONArray] 类型
 * @param [loggable] [LogsManager.switchPrinter]
 * @param [tag] 日志消息的给定标签
 */
@JvmOverloads
fun logJSON(
    @Nullable json: Any?,
    @Nullable loggable: ILoggable? = null,
    @Nullable tag: String? = null,
) {
    LogsManager.switchPrinter(loggable).t(tag).json(json)
}

/**
 * 输出 XML 文本日志
 * @param [xml] XML文本
 * @param [loggable] [LogsManager.switchPrinter]
 * @param [tag] 日志消息的给定标签
 */
@JvmOverloads
fun logXML(
    @Nullable xml: Any?,
    @Nullable loggable: ILoggable? = null,
    @Nullable tag: String? = null,
) {
    LogsManager.switchPrinter(loggable).t(tag).xml(xml)
}

/**
 * 输出 XML 文本日志
 * @param [cursor] Cursor
 * @param [justCurRow] 是否仅输出当前行：如果为false，则输出所有行
 * @param [loggable] [LogsManager.switchPrinter]
 * @param [tag] 日志消息的给定标签
 */
@JvmOverloads
fun logCursor(
    @Nullable cursor: Cursor?,
    @NonNull justCurRow: Boolean = false,
    @Nullable loggable: ILoggable? = null,
    @Nullable tag: String? = null,
) {
    LogsManager.switchPrinter(loggable).t(tag).cursor(
        cursor = cursor,
        justCurrentRow = justCurRow
    )
}

/**
 * 输出日志
 * @param [priority] 日志等级
 * @param [msg] 要输出的日志内容
 * @param [loggable] [LogsManager.switchPrinter]
 * @param [tag] 日志TAG
 * @param [throwable] 异常信息
 */
@JvmOverloads
fun logP(
    @NonNull priority: Int,
    @Nullable msg: Any?,
    @Nullable throwable: Throwable? = null,
    @Nullable loggable: ILoggable? = null,
    @Nullable tag: String? = null,
) {
    LogsManager.switchPrinter(loggable).t(tag).log(
        priority = priority,
        tag = tag,
        message = msg?.toString(),
        throwable = throwable
    )
}
