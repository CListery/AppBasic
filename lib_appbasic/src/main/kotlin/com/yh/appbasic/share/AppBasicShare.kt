package com.yh.appbasic.share

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Process
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import com.kotlin.memoryId
import com.yh.appbasic.logger.logD
import com.yh.appbasic.logger.logW
import com.yh.appbasic.logger.owner.LibLogger
import kotlin.concurrent.thread

@Suppress("unused")
object AppBasicShare {
    
    /**
     * 当前PID
     */
    @Volatile
    @JvmStatic
    var pid: Int = -1
    
    @JvmStatic
    val processInfo: ActivityManager.RunningAppProcessInfo? by lazy {
        val am: ActivityManager? = innerAppContext?.getSystemService()
        val processes = am?.runningAppProcesses
        processes?.find { it?.pid == pid }
    }
    
    /**
     * 主线程handler
     */
    @JvmStatic
    private val innerUiHandler: Handler by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        Handler(Looper.getMainLooper())
    }
    
    @JvmStatic
    private var innerAppContext: Application? = null
    
    @JvmStatic
    val application: Application
        get() = innerAppContext!!
    
    @JvmStatic
    val context: Context
        get() = innerAppContext!!
    
    @JvmStatic
    fun install(context: Context) {
        if (innerAppContext == null) {
            pid = Process.myPid()
            innerAppContext = context.applicationContext as Application?
            logD("$pid", this)
        }
    }
    
    @JvmStatic
    fun runOnUiThread(runnable: Runnable, delayMillis: Long = 0) {
        logW("runOnUiThread: ${runnable.memoryId} on $delayMillis", LibLogger)
        if (delayMillis > 0) {
            innerUiHandler.postDelayed(runnable, delayMillis)
        } else {
            innerUiHandler.post(runnable)
        }
    }
    
    @JvmStatic
    fun removeRunnable(runnable: Runnable) {
        logW("removeRunnable: ${runnable.memoryId}", LibLogger)
        innerUiHandler.removeCallbacks(runnable)
    }
    
}
