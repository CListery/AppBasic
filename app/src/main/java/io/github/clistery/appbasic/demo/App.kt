package io.github.clistery.appbasic.demo

import android.app.Application
import android.content.Intent
import android.os.Process
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.killAllOtherProcess
import android.content.killProcessExceptMain
import android.content.listenScreenOff
import com.yh.appbasic.init.AppBasicShare
import com.yh.appbasic.logger.LogsManager
import com.yh.appbasic.logger.impl.TheLogFormatStrategy
import com.yh.appbasic.logger.logI
import com.yh.appbasic.logger.owner.AppLogger
import com.yh.appbasic.logger.owner.LibLogger
import com.yh.libapp.B
import com.yh.libapp.LibApp
import io.github.clistery.appbasic.demo.ext.ACTION_KILL_ACT
import kotlin.system.exitProcess

class App : Application() {
    
    companion object {
        @JvmStatic
        private lateinit var instance: App
        
        @JvmStatic
        fun get(): App {
            return instance
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        
        instance = this

//        val logFormatStrategy = DiskLogFormatStrategy.Builder(this, "app").build()
////        logD("log file: ${logFormatStrategy.getRealLogFile()}")
//        AppLogger.onCreateFormatStrategy { logFormatStrategy }
//        LibLogger.onCreateFormatStrategy { logFormatStrategy }
        
        AppLogger.onCreateFormatStrategy {
            TheLogFormatStrategy.newBuilder().setFirstTag("APP").setMethodCount(5)
                .setStackFilter(B::class).build()
        }.on()
        LibLogger.onCreateFormatStrategy {
            TheLogFormatStrategy.newBuilder().setFirstTag("Library").setMethodCount(5).build()
        }.on()

//        AppLogger.on()
//        LibLogger.off()
        
        LogsManager.diskLogKeepDay(3)
        LogsManager.cleanup(this)
        
        AppBasicShare.get<LibApp>()?.logger?.onCreateFormatStrategy {
            TheLogFormatStrategy.newBuilder()
                .setFirstTag(it)
//                .setShowThreadInfo(false)
                .setMethodCount(0)
//                .setMethodCount(5)
                .build()
        }
    }
    
    fun kill(force: Boolean = false) {
        if (force) {
            killAllProcess()
        } else {
            listenScreenOff {
                logI("app is background now, i can kill quietly")
                killAllProcess()
            }
        }
    }
    
    private fun killAllProcess() {
        LocalBroadcastManager.getInstance(this).sendBroadcastSync(Intent(ACTION_KILL_ACT))
        killAllOtherProcess()
        killProcessExceptMain()
        Process.killProcess(Process.myPid())
        System.gc()
        exitProcess(0)
    }
    
}