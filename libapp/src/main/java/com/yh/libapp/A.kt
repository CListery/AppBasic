package com.yh.libapp

import com.yh.appbasic.initializer.AppBasicShare
import com.yh.appbasic.logger.ILoggable
import com.yh.appbasic.logger.logD
import com.yh.appbasic.logger.logW

/**
 * Created by CYH on 2020/5/16 22:33
 */
class A : ILoggable {
    
    init {
        logW("A static: ${AppBasicShare.context}", LibApp)
    }
    
    constructor() {
        logD("A init", this)
    }
    
}