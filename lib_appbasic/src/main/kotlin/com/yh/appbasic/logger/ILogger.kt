package com.yh.appbasic.logger

/**
 * 实现该接口则会自动创建一个 [LogOwner]
 */
interface ILogger {
    
    /**
     * 当创建 [LogOwner] 时回调该函数
     */
    fun onCreateLogOwner(logOwner: LogOwner)
    
}

/**
 * 获取 [LogOwner]
 */
val ILogger.logOwner: LogOwner get() = LogsManager.findLogOwner(this)
