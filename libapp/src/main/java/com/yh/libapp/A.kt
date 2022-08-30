package com.yh.libapp

import com.yh.appbasic.init.AppBasicShare
import com.yh.appbasic.logger.ILoggable
import com.yh.appbasic.logger.logD
import com.yh.appbasic.logger.logW

/**
 * Created by CYH on 2020/5/16 22:33
 */
class A : ILoggable {
    
    init {
        val libApp = AppBasicShare.get<LibApp>()
        logW("A static: ${libApp?.appContext}", libApp)
    }
    
    constructor(){
        logD("A init", this)
    }
    
}