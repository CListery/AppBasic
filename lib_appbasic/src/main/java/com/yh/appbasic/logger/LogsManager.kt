package com.yh.appbasic.logger

import androidx.annotation.Nullable
import com.yh.appbasic.logger.impl.TheLogAdapter
import com.yh.appbasic.logger.impl.TheLogFormatStrategy
import com.yh.appbasic.logger.impl.TheLogPrinter

/**
 * 日志管理器
 *
 * Created by CYH on 2020/5/14 13:44
 */
class LogsManager private constructor() {
    
    companion object {
        @JvmStatic
        private val mInstances by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { LogsManager() }
        
        /**
         * 获取日志管理器单例
         */
        @JvmStatic
        fun get() = mInstances
    }
    
    /**
     * 日志输出器实例
     */
    private val mPrinter = TheLogPrinter()
    
    /**
     * 库与日志适配器的映射关系
     */
    private val mMap: HashMap<ILogOwner, LogAdapter> = hashMapOf()
    
    /**
     * 库的默认日志适配器
     */
    private val mDefaultLibAdapter by lazy {
        TheLogAdapter(TheLogFormatStrategy.newBuilder().setFirstTag("Library").build())
    }
    
    /**
     * 应用的默认日志适配器
     */
    private val mDefaultAppAdapter by lazy {
        TheLogAdapter(TheLogFormatStrategy.newBuilder().setFirstTag("APP").build())
    }
    
    /**
     * 设置默认日志适配器的配置
     *
     * @param libConfig [mDefaultLibAdapter]
     * @param appConfig [mDefaultAppAdapter]
     * @see ILogOwner.loggerConfig
     */
    fun setDefLoggerConfig(
        @Nullable libConfig: Pair<Boolean, Int>? = null,
        @Nullable appConfig: Pair<Boolean, Int>? = null
    ) {
        if (null != libConfig) {
            mDefaultLibAdapter.setConfig(libConfig)
        }
        if (null != appConfig) {
            mDefaultAppAdapter.setConfig(appConfig)
        }
    }
    
    /**
     * 安装库的日志适配器
     * @param [owner] [ILogOwner]
     * @param [adapter] [LogAdapter]
     */
    fun install(owner: ILogOwner, adapter: LogAdapter) {
        mMap[owner] = adapter
    }
    
    /**
     * 卸载库的日志适配器
     * @param [owner] [ILogOwner]
     */
    fun uninstall(owner: ILogOwner) {
        mMap.remove(owner)
    }
    
    /**
     * 根据[LogAdapter]获取[Printer]
     * @param [adapter] [LogAdapter]
     * @return [Printer]
     */
    @Nullable
    fun with(adapter: LogAdapter?): Printer {
        if (null == adapter) {
            return mPrinter.adapter(mDefaultLibAdapter)
        }
        return mPrinter.adapter(adapter)
    }
    
    /**
     * 根据[ILogOwner]获取[Printer]
     * @param [owner] [ILogOwner]
     * @return [Printer]
     */
    @Nullable
    fun with(owner: ILogOwner?): Printer? {
        if (null == owner) {
            return mPrinter.adapter(mDefaultAppAdapter)
        }
        if (mMap.contains(owner)) {
            return mPrinter.adapter(mMap[owner])
        }
        return null
    }
}