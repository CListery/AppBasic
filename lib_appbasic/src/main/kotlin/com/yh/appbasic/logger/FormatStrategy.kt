package com.yh.appbasic.logger

import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable

/**
 * 日志格式化工具抽象接口
 */
interface FormatStrategy {

    /**
     * 格式化输出日志
     *
     * @param [priority] 日志级别，例如 [Log.DEBUG]、[Log.WARN]
     * @param [onceOnlyTag]      日志消息的给定标签
     * @param [message]  要输出的日志内容
     */
    fun log(priority: Int, @Nullable onceOnlyTag: String?, @NonNull message: String)
    
    fun release()
}