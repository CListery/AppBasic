package com.yh.appbasic.logger

import android.util.Log
import com.yh.appbasic.logger.impl.TheLogFormatStrategy

typealias FormatStrategyBuilder = (logTag: String) -> FormatStrategy

/**
 * 日志归属者
 *
 * @param [logTag] 提供一个 LOG TAG
 */
open class LogOwner(val logTag: () -> String) {
    
    private var formatStrategyBuilder: FormatStrategyBuilder = {
        TheLogFormatStrategy.newBuilder(it).build()
    }
    
    /**
     * 创建自定义的日志格式化工具
     */
    fun onCreateFormatStrategy(builder: FormatStrategyBuilder): LogOwner {
        formatStrategyBuilder = builder
        internalLogAdapter = buildLogAdapter()
        return this
    }
    
    private var internalLogAdapter: LogAdapter? = null
        set(value) {
            val loggerConfig = field?.loggerConfig
            if (null != loggerConfig) {
                value?.setConfig(loggerConfig)
            }
            field = value
        }
        get() {
            if (null == field) {
                internalLogAdapter = buildLogAdapter()
            }
            return field!!
        }
    val logAdapter: LogAdapter get() = internalLogAdapter!!
    
    protected open fun buildLogAdapter(): LogAdapter {
        return LogAdapter(formatStrategyBuilder(logTag()))
    }
    
    /**
     * 日志状态切换
     */
    @JvmOverloads
    open fun loggable(enable: Boolean, level: Int = Log.VERBOSE): LogOwner {
        logAdapter.setConfig(enable to level)
        return this
    }
    
    /**
     * 开启日志
     */
    @JvmOverloads
    open fun on(level: Int = Log.VERBOSE): LogOwner {
        logAdapter.setConfig(true to level)
        return this
    }
    
    /**
     * 关闭日志
     */
    open fun off(): LogOwner {
        logAdapter.setConfig(false to Log.ASSERT)
        return this
    }
    
    /**
     * 销毁
     */
    open fun release() {
        internalLogAdapter?.release()
        internalLogAdapter = null
    }
}