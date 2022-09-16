package com.yh.libapp

import com.yh.appbasic.initializer.AppBasicShare
import com.yh.appbasic.logger.ILogger
import com.yh.appbasic.logger.LogOwner
import com.yh.appbasic.logger.logD
import com.yh.appbasic.logger.logW

/**
 * Created by CYH on 2020/5/16 22:33
 */
class A : ILogger {
    
    init {
        logW("A static: ${AppBasicShare.context}", LibApp)
    }
    
    constructor() {
        logD("A init", this)
    }
    
    override fun onCreateLogOwner(logOwner: LogOwner) {
    
    }
}