@file:Suppress("unused", "FunctionName") @file:JvmName("Logs")

package com.yh.appbasic.logger

import android.database.Cursor
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import org.json.JSONArray
import org.json.JSONObject

/**
 * 日志输出工具
 *
 * 根据[com.yh.appbasic.logger.ILogOwner]区分
 *
 * Created by CYH on 2020/5/14 13:59
 */

/**
 * 输出 [Log.DEBUG] 日志
 * @param [msg] 要输出的日志内容
 * @param [tag] 日志消息的给定标签
 * @param [owner] 库注入协助器实例，如为空，默认使用[LogsManager.mDefaultAppAdapter]
 * @param [args] 需要格式化到 [msg] 中的内容
 * @see Log.DEBUG
 */
@JvmOverloads
fun logD(@NonNull msg: Any?, @Nullable tag: String? = null, @Nullable owner: ILogOwner? = null, @Nullable vararg args: Any?) {
    LogsManager.get().with(owner)?.t(tag)?.d(msg?.toString() ?: "", *args)
}

/**
 * 输出携带异常信息的 [Log.ERROR] 日志
 * @param [msg] 要输出的日志内容
 * @param [tag] 日志消息的给定标签
 * @param [owner] 库注入协助器实例，如为空，默认使用[LogsManager.mDefaultAppAdapter]
 * @param [throwable] 异常信息
 * @param [args] 需要格式化到 [msg] 中的内容
 * @see Log.ERROR
 */
@JvmOverloads
fun logE(@NonNull msg: Any?, @Nullable tag: String? = null, @Nullable owner: ILogOwner? = null, @Nullable throwable: Throwable? = null, @Nullable vararg args: Any?) {
    LogsManager.get().with(owner)?.t(tag)?.e(throwable, msg?.toString() ?: "", *args)
}

/**
 * 输出 [Log.INFO] 日志
 * @param [msg] 要输出的日志内容
 * @param [tag] 日志消息的给定标签
 * @param [owner] 库注入协助器实例，如为空，默认使用[LogsManager.mDefaultAppAdapter]
 * @param [args] 需要格式化到 [msg] 中的内容
 * @see Log.INFO
 */
@JvmOverloads
fun logI(@NonNull msg: Any?, @Nullable tag: String? = null, @Nullable owner: ILogOwner? = null, @Nullable vararg args: Any?) {
    LogsManager.get().with(owner)?.t(tag)?.i(msg?.toString() ?: "", *args)
}

/**
 * 输出 [Log.VERBOSE] 日志
 * @param [msg] 要输出的日志内容
 * @param [tag] 日志消息的给定标签
 * @param [owner] 库注入协助器实例，如为空，默认使用[LogsManager.mDefaultAppAdapter]
 * @param [args] 需要格式化到 [msg] 中的内容
 * @see Log.VERBOSE
 */
@JvmOverloads
fun logV(@NonNull msg: Any?, @Nullable tag: String? = null, @Nullable owner: ILogOwner? = null, @Nullable vararg args: Any?) {
    LogsManager.get().with(owner)?.t(tag)?.v(msg?.toString() ?: "", *args)
}

/**
 * 输出 [Log.WARN] 日志
 * @param [msg] 要输出的日志内容
 * @param [tag] 日志消息的给定标签
 * @param [owner] 库注入协助器实例，如为空，默认使用[LogsManager.mDefaultAppAdapter]
 * @param [args] 需要格式化到 [msg] 中的内容
 * @see Log.WARN
 */
@JvmOverloads
fun logW(@NonNull msg: Any?, @Nullable tag: String? = null, @Nullable owner: ILogOwner? = null, @Nullable vararg args: Any?) {
    LogsManager.get().with(owner)?.t(tag)?.w(msg?.toString() ?: "", *args)
}

/**
 * 输出 [Log.ASSERT] 日志
 * @param [msg] 要输出的日志内容
 * @param [tag] 日志消息的给定标签
 * @param [owner] 库注入协助器实例，如为空，默认使用[LogsManager.mDefaultAppAdapter]
 * @param [args] 需要格式化到 [msg] 中的内容
 * @see Log.ASSERT
 */
@JvmOverloads
fun logWTF(@NonNull msg: Any?, @Nullable tag: String? = null, @Nullable owner: ILogOwner? = null, @Nullable vararg args: Any?) {
    LogsManager.get().with(owner)?.t(tag)?.wtf(msg?.toString() ?: "", *args)
}

/**
 * 输出 JSON 文本日志
 * @param [json] JSON文本，支持 [CharSequence]、[JSONObject]、[JSONArray] 类型
 * @param [tag] 日志消息的给定标签
 * @param [owner] 库注入协助器实例，如为空，默认使用[LogsManager.mDefaultAppAdapter]
 */
@JvmOverloads
fun logJSON(@Nullable json: Any?, @Nullable tag: String? = null, @Nullable owner: ILogOwner? = null) {
    LogsManager.get().with(owner)?.t(tag)?.json(json)
}

/**
 * 输出 XML 文本日志
 * @param [xml] XML文本
 * @param [tag] 日志消息的给定标签
 * @param [owner] 库注入协助器实例，如为空，默认使用[LogsManager.mDefaultAppAdapter]
 */
@JvmOverloads
fun logXML(@Nullable xml: Any?, @Nullable tag: String? = null, @Nullable owner: ILogOwner? = null) {
    LogsManager.get().with(owner)?.t(tag)?.xml(xml)
}

/**
 * 输出 XML 文本日志
 * @param [cursor] Cursor
 * @param [justCurRow] 是否仅输出当前行：如果为false，则输出所有行
 * @param [tag] 日志消息的给定标签
 * @param [owner] 库注入协助器实例，如为空，默认使用[LogsManager.mDefaultAppAdapter]
 */
@JvmOverloads
fun logCursor(@Nullable cursor: Cursor?, @NonNull justCurRow: Boolean = false, @Nullable tag: String? = null, @Nullable owner: ILogOwner? = null) {
    LogsManager.get().with(owner)?.t(tag)?.cursor(cursor, justCurRow)
}

/**
 * 输出日志
 * @param [priority] 日志等级
 * @param [msg] 要输出的日志内容
 * @param [tag] 日志TAG
 * @param [owner] 库注入协助器实例，如为空，默认使用[LogsManager.mDefaultAppAdapter]
 * @param [throwable] 异常信息
 */
@JvmOverloads
fun logP(@NonNull priority: Int, @NonNull msg: Any?, @Nullable tag: String? = null, @Nullable owner: ILogOwner? = null, @Nullable throwable: Throwable? = null) {
    LogsManager.get().with(owner)?.log(priority, tag, msg?.toString() ?: "", throwable)
}
