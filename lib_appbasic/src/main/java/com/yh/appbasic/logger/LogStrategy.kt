package com.yh.appbasic.logger

import android.util.Log
import androidx.annotation.NonNull

/**
 * 日志输出方式的抽象接口，如磁盘、Logcat、网络等
 */
interface LogStrategy {

    /**
     * 每次处理日志消息时，[Printer]都会调用此方法。将此方法解释为整个管道中日志的最后目的地
     *
     * @param [priority] 日志级别，例如 [Log.DEBUG]、[Log.WARN]
     * @param [tag]      日志消息的给定标签
     * @param [message]  要输出的日志内容
     */
    fun log(priority: Int, @NonNull tag: String, @NonNull message: String)
    
    fun release()
}