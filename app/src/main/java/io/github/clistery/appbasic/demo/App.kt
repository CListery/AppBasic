package io.github.clistery.appbasic.demo

import android.content.*
import android.os.Process
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.multidex.MultiDexApplication
import com.yh.appbasic.share.AppBasicShare
import com.yh.appbasic.logger.LogsManager
import com.yh.appbasic.logger.impl.TheLogFormatStrategy
import com.yh.appbasic.logger.logI
import com.yh.appbasic.logger.owner.AppLogger
import com.yh.appbasic.logger.owner.LibLogger
import com.yh.libapp.B
import io.github.clistery.appbasic.demo.ext.ACTION_KILL_ACT
import kotlin.system.exitProcess

class App : MultiDexApplication() {
    
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
        
        AppBasicShare.install(this)

//        val logFormatStrategy = DiskLogFormatStrategy.Builder(this, "app").build()
////        logD("log file: ${logFormatStrategy.getRealLogFile()}")
//        AppLogger.onCreateFormatStrategy { logFormatStrategy }
//        LibLogger.onCreateFormatStrategy { logFormatStrategy }
        
        AppLogger.onCreateFormatStrategy {
            TheLogFormatStrategy.newBuilder("APP").setMethodCount(5)
                .setStackFilter(B::class).build()
        }.on()
        LibLogger.onCreateFormatStrategy {
            TheLogFormatStrategy.newBuilder("Library").setMethodCount(5).build()
        }.on()

//        AppLogger.on()
//        LibLogger.off()
        
        LogsManager.diskLogKeepDay(3)
        LogsManager.cleanup(this)
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