package com.yh.appbasic.logger.impl

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.yh.appbasic.logger.LogStrategy
import java.io.File
import java.io.FileWriter
import kotlin.concurrent.thread

class DiskLogStrategy(private val handler: WriteHandler) : LogStrategy {
    
    companion object {
        private const val MSG_END = 0x999
    }
    
    override fun log(priority: Int, tag: String, message: String) {
        if(message.isNotEmpty()) {
            try {
                handler.sendMessage(handler.obtainMessage(priority, message))
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    override fun release() {
        try {
            handler.sendMessage(handler.obtainMessage(MSG_END))
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }
    
    class WriteHandler(
        looper: Looper,
        private val logFile: File
    ) : Handler(looper) {
        
        override fun handleMessage(msg: Message) {
            if(MSG_END == msg.what) {
                thread {
                    try {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            looper.quitSafely()
                        } else {
                            looper.quit()
                        }
                    } catch(e: Exception) {
                        e.printStackTrace()
                    }
                }
                return
            }
            val content = msg.obj
            if(content !is String || content.isEmpty()) {
                return
            }
            FileWriter(logFile, true).use {
                it.append(content)
                it.flush()
            }
        }
    }
    
}