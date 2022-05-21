package com.yh.appbasic.logger.impl

import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.yh.appbasic.logger.FormatStrategy
import com.yh.appbasic.logger.LogAdapter

/**
 * 默认日志适配器
 *
 * @property formatStrategy
 *
 * Created by CYH on 2020/5/14 17:26
 */
class TheLogAdapter(private val formatStrategy: FormatStrategy) : LogAdapter {

    private var loggerConfig: Pair<Boolean, Int> = Pair(false, Log.ASSERT)

    /**
     * 是否启用
     */
    val isEnable get() = loggerConfig.first

    /**
     * 日志等级
     */
    val logLevel get() = loggerConfig.second

    /**
     * 设置日志等级及开关
     */
    fun setConfig(config: Pair<Boolean, Int>): TheLogAdapter {
        loggerConfig = config
        return this
    }

    override fun isLoggable(priority: Int, tag: String?): Boolean {
        return priority >= Log.ERROR || (isEnable && priority >= logLevel)
    }

    override fun log(priority: Int, @Nullable tag: String?, @NonNull message: String) {
        formatStrategy.log(priority, tag, message)
    }
    
    override fun release() {
        formatStrategy.release()
    }
}