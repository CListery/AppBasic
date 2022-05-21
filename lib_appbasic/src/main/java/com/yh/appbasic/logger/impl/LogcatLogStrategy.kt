package com.yh.appbasic.logger.impl

import android.util.Log
import androidx.annotation.NonNull
import com.yh.appbasic.logger.LogStrategy

/**
 * LogCat implementation for [LogStrategy]
 *
 * This simply prints out all logs to Logcat by using standard [Log] class.
 */
class LogcatLogStrategy : LogStrategy {

    override fun log(priority: Int, @NonNull tag: String, @NonNull message: String) {
        Log.println(priority, tag, message)
    }
    
    override fun release() {
    
    }
}