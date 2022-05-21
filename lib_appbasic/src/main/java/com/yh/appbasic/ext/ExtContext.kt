@file:JvmName("ExtContext")
package com.yh.appbasic.ext

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import androidx.core.content.getSystemService
import com.yh.appinject.logger.LibLogs

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
