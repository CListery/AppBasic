package com.yh.libapp

import com.yh.appbasic.logger.ILogger
import com.yh.appbasic.logger.LogOwner
import com.yh.appbasic.logger.impl.TheLogFormatStrategy

object LibApp : ILogger {
    override fun onCreateLogOwner(logOwner: LogOwner) {
        logOwner.onCreateFormatStrategy {
            TheLogFormatStrategy.newBuilder(it)
                .setShowThreadInfo(true)
                .setMethodCount(5)
                .build()
        }
    }
}