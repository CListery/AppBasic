package com.yh.appbasic.logger

import android.database.Cursor
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import org.json.JSONArray
import org.json.JSONObject

/**
 * 日志输出器的抽象接口
 */
interface Printer {
    /**
     * 添加日志输出适配器
     *
     * @param [adapter] 日志输出适配器
     * @see LogAdapter
     */
    fun addAdapter(@NonNull adapter: LogAdapter?)

    /**
     * 清空该日志输出器的所有日志输出适配器
     */
    fun clearLogAdapters()

    /**
     * 设置下一次输出日志的标签
     * @param [tag] 日志消息的给定标签
     * @return 返回该日志输出器实例
     */
    fun t(@Nullable tag: String?): Printer

    /**
     * 输出 [Log.DEBUG] 日志
     * @param [message] 要输出的日志内容
     * @param [args] 需要格式化到 [message] 中的内容
     * @see Log.DEBUG
     */
    fun d(@Nullable message: String? = null, @Nullable vararg args: Any?)

    /**
     * 输出 [Log.INFO] 日志
     * @param [message] 要输出的日志内容
     * @param [args] 需要格式化到 [message] 中的内容
     * @see Log.INFO
     */
    fun i(@Nullable message: String? = null, @Nullable vararg args: Any?)

    /**
     * 输出 [Log.VERBOSE] 日志
     * @param [message] 要输出的日志内容
     * @param [args] 需要格式化到 [message] 中的内容
     * @see Log.VERBOSE
     */
    fun v(@Nullable message: String? = null, @Nullable vararg args: Any?)

    /**
     * 输出 [Log.WARN] 日志
     * @param [message] 要输出的日志内容
     * @param [args] 需要格式化到 [message] 中的内容
     * @see Log.WARN
     */
    fun w(@Nullable message: String? = null, @Nullable vararg args: Any?)

    /**
     * 输出 [Log.ERROR] 日志
     * @param [message] 要输出的日志内容
     * @param [args] 需要格式化到 [message] 中的内容
     * @see Log.ERROR
     */
    fun e(@Nullable message: String? = null, @Nullable vararg args: Any?)

    /**
     * 输出携带异常信息的 [Log.ERROR] 日志
     * @param [throwable] 异常信息
     * @param [message] 要输出的日志内容
     * @param [args] 需要格式化到 [message] 中的内容
     * @see Log.ERROR
     */
    fun e(@Nullable throwable: Throwable? = null, @Nullable message: String? = null, @Nullable vararg args: Any?)

    /**
     * 输出 [Log.ASSERT] 日志
     * @param [message] 要输出的日志内容
     * @param [args] 需要格式化到 [message] 中的内容
     * @see Log.ASSERT
     */
    fun wtf(@Nullable message: String? = null, @Nullable vararg args: Any?)

    /**
     * 输出 JSON 文本日志
     * @param [jsonAny] JSON文本，支持 [CharSequence]、[JSONObject]、[JSONArray] 类型
     * @see Log.DEBUG
     */
    fun json(@Nullable jsonAny: Any?)

    /**
     * 输出 XML 文本日志
     * @param [xml] XML文本
     */
    fun xml(xml: Any?)

    /**
     * 输出 [Cursor] 日志
     * @param [cursor] Cursor
     * @param [justCurrentRow] 是否仅输出当前行：如果为false，则输出所有行
     * @see Cursor
     */
    fun cursor(@Nullable cursor: Cursor?, justCurrentRow: Boolean)

    /**
     * 输出日志
     * @param [priority] 日志等级
     * @param [tag] 日志TAG
     * @param [message] 要输出的日志内容
     * @param [throwable] 异常信息
     */
    fun log(priority: Int, @Nullable tag: String?, @Nullable message: String?, @Nullable throwable: Throwable?)
}