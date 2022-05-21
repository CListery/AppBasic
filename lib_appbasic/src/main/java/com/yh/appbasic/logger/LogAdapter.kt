package com.yh.appbasic.logger

import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable

/**
 * 日志适配器的抽象接口
 */
interface LogAdapter {

    /**
     * 判断该适配器是否能输出这条日志
     *
     * @param [priority] 日志级别，例如 [Log.DEBUG]、[Log.WARN]
     * @param [tag]      日志消息的给定标签
     * @return 是否能输出日志
     */
    fun isLoggable(priority: Int, @Nullable tag: String?): Boolean

    /**
     * 使用该适配器输出这条日志
     *
     * @param [priority] 日志级别，例如 [Log.DEBUG]、[Log.WARN]
     * @param [tag]      日志消息的给定标签
     * @param [message]  要输出的日志内容
     */
    fun log(priority: Int, @Nullable tag: String?, @NonNull message: String)
    
    fun release()
}