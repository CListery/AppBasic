@file:JvmName("ExtContext")
package com.yh.appbasic.ext

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Process
import androidx.core.content.getSystemService
import com.yh.appbasic.logger.LibLogs
import com.yh.appbasic.logger.logW

/**
 *
 * ### 检查当前是否处于主进程
 *
 * [isMainProcess] 扩展自 [Context]
 *
 * * 某些情况下APP会被拉起，这些情况下并非处于主进程状态，这些时候有些操作应该被触发，比如某些第三方的库初始化等
 *
 * @return true，当前处于主进程
 */
fun Context.isMainProcess(): Boolean {
    try {
        val am: ActivityManager? = getSystemService()
        val processes = am?.runningAppProcesses
        if(null == am || processes.isNullOrEmpty()) {
            return false
        }
        val mainProcessName = packageName
        val myPid = Process.myPid()
        if(processes.isEmpty()) {
            LibLogs.logW("isMainProcess get getRunningAppProcesses empty", "App")
            val processList = am.getRunningServices(Int.MAX_VALUE)
            if(null == processList || processList.isEmpty()) {
                LibLogs.logW("isMainProcess get getRunningServices empty", "App")
                return false
            } else {
                processList.forEach { rsi ->
                    if(rsi.pid == myPid && mainProcessName == rsi.service.packageName) {
                        return true
                    }
                }
                return false
            }
        } else {
            processes.forEach { rapi ->
                if(rapi.pid == myPid && mainProcessName == rapi.processName) {
                    return true
                }
            }
            return false
        }
    } catch(e: Exception) {
        return false
    }
}

/**
 * 杀死除当前进程之外的APP进程
 */
fun Context?.killAllOtherProcess() {
    this?.getSystemService<ActivityManager>()?.runningAppProcesses?.forEach { ai ->
        if (ai.uid == Process.myUid() && ai.pid != Process.myPid()) {
            Process.killProcess(ai.pid)
        }
    }
}

/**
 * 杀死除主进程之外的APP进程
 */
fun Context?.killProcessExceptMain() {
    this?.getSystemService<ActivityManager>()?.runningAppProcesses?.forEach { ai ->
        if (ai.uid != Process.myUid()) {
            return@forEach
        }
        if (ai.processName == packageName) {
            return@forEach
        }
        Process.killProcess(ai.pid)
    }
}

inline fun Context?.listenScreenOff(crossinline onScreenOff: () -> Unit) {
    if (null == this) return
    val intentFilter = IntentFilter()
    intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
    registerReceiver(object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (null == context || null == intent) {
                return
            }
            unregisterReceiver(this)
            if (Intent.ACTION_SCREEN_OFF == intent.action) {
                logW("screen off!")
                onScreenOff.invoke()
            }
        }
    }, intentFilter)
}