package com.yh.appbasic.logger

import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable

/**
 * 日志适配器的抽象接口
 */
open class LogAdapter(val formatStrategy: FormatStrategy) : ILoggable {
    
    open var loggerConfig: Pair<Boolean, Int> = true to Log.VERBOSE
    
    /**
     * 是否启用
     */
    open val isEnable get() = loggerConfig.first
    
    /**
     * 日志等级
     */
    open val logLevel get() = loggerConfig.second
    
    /**
     * 日志状态切换
     */
    @JvmOverloads
    open fun loggable(enable: Boolean, level: Int = Log.VERBOSE) = setConfig(enable to level)
    
    /**
     * 开启日志
     */
    @JvmOverloads
    open fun on(level: Int = Log.VERBOSE) = setConfig(true to level)
    
    /**
     * 关闭日志
     */
    open fun off() = setConfig(false to Log.ASSERT)
    
    /**
     * 设置日志等级及开关
     */
    open fun setConfig(config: Pair<Boolean, Int>): LogAdapter {
        loggerConfig = config
        return this
    }
    
    /**
     * 判断该适配器是否能输出这条日志
     *
     * @param [priority] 日志级别，例如 [Log.DEBUG]、[Log.WARN]
     * @param [tag]      日志消息的给定标签
     * @return 是否能输出日志
     */
    open fun isLoggable(priority: Int, @Nullable tag: String?): Boolean {
        return priority >= Log.ERROR || (isEnable && priority >= logLevel)
    }
    
    /**
     * 使用该适配器输出这条日志
     *
     * @param [priority] 日志级别，例如 [Log.DEBUG]、[Log.WARN]
     * @param [tag]      日志消息的给定标签
     * @param [message]  要输出的日志内容
     */
    open fun log(priority: Int, @Nullable tag: String?, @NonNull message: String) {
        formatStrategy.log(priority, tag, message)
    }
    
    open fun release() {
        formatStrategy.release()
    }
}