package com.yh.appbasic.init

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Process
import com.kotlin.isInitialized
import com.kotlin.memoryId
import com.yh.appbasic.logger.ILoggable
import com.yh.appbasic.logger.LogOwner
import com.yh.appbasic.logger.logW
import com.yh.appbasic.logger.owner.LibLogger

open class BasicInitializer : ContentProvider(), ILoggable {
    
    /**
     * Lib 初始化时的 PID
     */
    @Volatile
    protected var mProcessID: Int = -1
    
    private val innerUiHandler: Handler by lazy { createUiHandler() }
    
    protected open fun createUiHandler() = Handler(Looper.getMainLooper())
    
    @Suppress("PropertyName")
    private val _appContext: Context
        get() = context!!.applicationContext
    
    val appContext: Context get() = _appContext
    
    val logger: LogOwner by lazy { LogOwner { this@BasicInitializer.javaClass.simpleName } }
    
    override fun onCreate(): Boolean {
        AppBasicShare.install(this)
        mProcessID = Process.myPid()
        initializer(_appContext)
        return true
    }
    
    protected open fun initializer(context: Context) {}
    
    open fun runOnUiThread(runnable: Runnable, delayMillis: Long = 0) {
        if (!this::innerUiHandler.isInitialized()) {
            throw RuntimeException("must call onCreate() first!")
        }
        logW("runOnUiThread: ${runnable.memoryId} on $delayMillis", LibLogger)
        if (delayMillis > 0) {
            innerUiHandler.postDelayed(runnable, delayMillis)
        } else {
            innerUiHandler.post(runnable)
        }
    }
    
    open fun removeRunnable(runnable: Runnable) {
        if (!this::innerUiHandler.isInitialized()) {
            throw RuntimeException("must call onCreate() first!")
        }
        logW("removeRunnable: ${runnable.memoryId}", LibLogger)
        innerUiHandler.removeCallbacks(runnable)
    }
    
    @Deprecated("", ReplaceWith("0"))
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? = null
    
    @Deprecated("", ReplaceWith("0"))
    override fun getType(uri: Uri): String? = null
    
    @Deprecated("", ReplaceWith("0"))
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    
    @Deprecated("", ReplaceWith("0"))
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    
    @Deprecated("", ReplaceWith("0"))
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int = 0
}