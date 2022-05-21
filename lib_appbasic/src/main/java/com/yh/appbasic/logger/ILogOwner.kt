package com.yh.appbasic.logger

import com.yh.appbasic.logger.impl.TheLogAdapter
import com.yh.appbasic.logger.impl.TheLogFormatStrategy

interface ILogOwner {
    
    /**
     * 设置日志等级及开关
     *
     * @param [config]
     *  - first - 是否开启日志
     *  - second - 日志等级 [android.util.Log]
     */
    fun loggerConfig(config: Pair<Boolean, Int>)
    
    /**
     * 日志TAG
     *
     * @return 默认为该类的类名
     */
    val logTag: String get() = this::class.java.simpleName
    
    /**
     * 创建 [LogAdapter]
     *
     * @param [config]
     *  - first - 是否开启日志
     *  - second - 日志等级 [android.util.Log]
     * @return 默认 [TheLogAdapter]
     * @see TheLogAdapter
     */
    fun makeLogAdapter(config: Pair<Boolean, Int>): LogAdapter =
        TheLogAdapter(makeFormatStrategy().build()).setConfig(config)
    
    /**
     * 创建 [TheLogFormatStrategy.Builder]
     *
     * @return [TheLogFormatStrategy.Builder]
     * @see TheLogFormatStrategy.Builder
     */
    fun makeFormatStrategy(): TheLogFormatStrategy.Builder =
        TheLogFormatStrategy.newBuilder().setFirstTag(logTag)
    
}