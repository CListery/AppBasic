package com.yh.appbasic.logger

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import androidx.core.content.getSystemService
import com.kotlin.differenceDay
import com.kotlin.timeCurMillisecond
import com.kotlin.timeFormatDate
import com.kotlin.timeParseDate
import com.yh.appbasic.share.AppBasicShare
import com.yh.appbasic.logger.impl.DiskLogFormatStrategy
import com.yh.appbasic.logger.impl.TheLogPrinter
import com.yh.appbasic.logger.owner.AppLogger
import com.yh.appbasic.logger.owner.LibLogger
import com.yh.appbasic.util.FileUtils
import com.yh.appbasic.util.FileUtils.getCacheSubDirByName
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.abs

/**
 * 日志管理器
 *
 * Created by CYH on 2020/5/14 13:44
 */
object LogsManager {
    
    /**
     * 日志输出器实例
     */
    private val printer = TheLogPrinter()
    
    @JvmStatic
    var diskLogRootDirName = "logs"
        internal set
    val diskLogLockDirName: String = "${diskLogRootDirName}/lock"
    
    const val DATE_FORMAT = "yyyyMMdd"
    
    @JvmStatic
    val diskLogFileName: () -> String = {
        timeCurMillisecond.timeFormatDate(DATE_FORMAT)
    }
    
    /**
     * 保存实现了 ILogger 的 LogOwner 对象
     * @see ILogger
     */
    private val loggers = hashMapOf<String, LogOwner>()
    
    /**
     * 日志文件保留的最大时间，单位天
     */
    @JvmStatic
    private var diskLogKeepDay = 3
    
    var appLogger: () -> LogOwner = { AppLogger }
    var libLogger: () -> LogOwner = { LibLogger }
    fun diskLogKeepDay(keepDay: Int) {
        this.diskLogKeepDay = keepDay
    }
    
    @JvmStatic
    fun cleanup(context: Context, keepDiskLog: Boolean = true) {
        logD("cleanup", loggable = LogsManager)
        context.cleanupDiskLogs(keepDiskLog)
    }
    
    /**
     * 查找 [logger] 绑定的 [LogOwner]
     *
     * 如果没有则自动创建
     *
     * @see [ILogger.onCreateLogOwner]
     */
    @JvmStatic
    fun findLogOwner(logger: ILogger): LogOwner {
        val printerProviderName = logger::class.simpleName!!
        return loggers.getOrPut(printerProviderName) {
            LogOwner { printerProviderName }.also {
                logger.onCreateLogOwner(it)
            }
        }
    }
    
    @JvmStatic
    fun changeLogOwner(logger: ILogger, owner: LogOwner) {
        val printerProviderName = logger::class.simpleName!!
        loggers[printerProviderName] = owner
    }
    
    /**
     * 根据 [LogOwner] 获取 [Printer]
     *
     * @return [Printer]
     *  - default [AppLogger]
     */
    @JvmStatic
    internal fun switchPrinter(printerProvider: Any?): Printer {
        return when(printerProvider) {
            is LogsManager   -> switchPrinter(libLogger()).t("LogsMgr")
            is AppBasicShare -> switchPrinter(libLogger()).t("AppBasic")
            is AppLogger     -> {
                val owner = appLogger()
                if(owner != AppLogger) {
                    switchPrinter(owner)
                } else {
                    switchPrinter(printerProvider.logAdapter).t(printerProvider.logTag())
                }
            }
            
            is LibLogger     -> {
                val owner = libLogger()
                if(owner != LibLogger) {
                    switchPrinter(owner)
                } else {
                    switchPrinter(printerProvider.logAdapter).t(printerProvider.logTag())
                }
            }
            
            is LogOwner      -> switchPrinter(printerProvider.logAdapter).t(printerProvider.logTag())
            is LogAdapter    -> printer.adapter(printerProvider)
            is ILogger       -> switchPrinter(findLogOwner(printerProvider)).t(printerProvider::class.simpleName!!)
            else             -> switchPrinter(appLogger()).t(printerProvider?.let { it::class.simpleName })
        }
    }
    
    @JvmStatic
    private fun Context.cleanupDiskLogs(keepDiskLog: Boolean) {
        logD("cleanupDiskLogs", loggable = LogsManager)
        val appProcesses = getSystemService<ActivityManager>()?.runningAppProcesses
        val allPid = appProcesses?.map { it.pid }
        
        fun allLocks(): List<LogLock>? {
            val diskLogLockDir = getCacheSubDirByName(diskLogLockDirName)
            val lockFiles = diskLogLockDir.listFiles()
            
            val locks = lockFiles?.map { lockedFile ->
                val token = lockedFile.nameWithoutExtension.split("-@-")
                val pid = token[0].toIntOrNull() ?: -1
                val time = token[1].toLongOrNull() ?: 0L
                val logFileName = token[2]
                LogLock(pid, time, logFileName, lockedFile)
            }
            
            return locks?.filter {
                val result: Boolean = if (!allPid.isNullOrEmpty()) {
                    allPid.contains(it.pid)
                } else {
                    // 直接干掉超过一天的锁
                    abs(System.currentTimeMillis() - it.time) > 86400000
                }
                if (!result) {
                    it.lockFile.delete()
                }
                return@filter result
            }
        }
        
        thread {
            synchronized(LogsManager) {
                val diskLogRootDir = getCacheSubDirByName(diskLogRootDirName)
                val cleanupLockFile = File(diskLogRootDir, "cleanup.lock")
                if (cleanupLockFile.exists()) {
                    val readTokens = FileReader(cleanupLockFile).readText().trim().split(",")
                    if (readTokens.size == 4) {
                        val pid = readTokens[0].toIntOrNull() ?: -1
                        //                    val tid = readTokens[1].toIntOrNull()
                        //                    val uuid = readTokens[2]
                        val time = readTokens[3].toLongOrNull() ?: 0L
                        if (-1 != pid) {
                            if (!allPid.isNullOrEmpty()) {
                                if (allPid.contains(pid)) {
                                    return@thread
                                } else {
                                    cleanupLockFile.delete()
                                    return@thread
                                }
                            }
                        }
                        // 检查锁是否超过10秒
                        if (abs(System.currentTimeMillis() - time) <= 10000) {
                            return@thread
                        }
                    }
                    cleanupLockFile.delete()
                }
                val token = listOf(
                    Process.myPid(),
                    Process.myTid(),
                    UUID.randomUUID(),
                    System.currentTimeMillis(),
                ).joinToString(",")
                FileWriter(cleanupLockFile, false).use {
                    it.appendLine(token)
                }
                val locks = allLocks()
                logD(
                    "valid locks: $locks",
                    loggable = LogsManager,
                )
                FileUtils.deleteAll(diskLogRootDir) { f: File ->
                    if (f.isDirectory) {
                        return@deleteAll false
                    }
                    if (f == cleanupLockFile) {
                        return@deleteAll true
                    }
                    val fileName = f.nameWithoutExtension
                    if (null != locks?.find { lock -> lock.logFileName == fileName || lock.lockFile == f }) {
                        return@deleteAll true
                    }
                    if (keepDiskLog) {
                        val date =
                            fileName.split(DiskLogFormatStrategy.LOG_FILE_NAME_SEPARATOR)
                                .lastOrNull().timeParseDate(DATE_FORMAT)
                        val day = date?.differenceDay(Calendar.getInstance())
                        if (null != day) {
                            if (day < diskLogKeepDay) {
                                return@deleteAll true
                            }
                        }
                    }
                    return@deleteAll false
                }
                cleanupLockFile.delete()
            }
        }
    }
    
    @JvmStatic
    fun diskLogLocked(logFile: File) {
        thread {
            synchronized(LogsManager) {
                val context = AppBasicShare.context
                val fileName = logFile.nameWithoutExtension
                val diskLogLockDir = context.getCacheSubDirByName(diskLogLockDirName)
                val lockFiles = diskLogLockDir.listFiles()
                
                val lock = lockFiles?.find { it.name.contains(fileName) }?.let {
                    val token = it.nameWithoutExtension.split("-@-")
                    val pid = token[0].toIntOrNull() ?: -1
                    val time = token[1].toLongOrNull() ?: 0L
                    val logFileName = token[2]
                    LogLock(pid, time, logFileName, it)
                }
                if (null != lock) {
                    if (lock.pid == Process.myPid()) {
                        return@thread
                    }
                    lock.lockFile.delete()
                }
                val token = listOf(
                    Process.myPid(),
                    System.currentTimeMillis(),
                    fileName,
                ).joinToString("-@-")
                logD(
                    "diskLogLocked: $token",
                    loggable = LogsManager,
                )
                File(context.getCacheSubDirByName(diskLogLockDirName),
                    token.plus(".lock")).createNewFile()
            }
        }
    }
    
    @JvmStatic
    fun diskLogUnLocked(logFile: File) {
        thread {
            synchronized(LogsManager) {
                val context = AppBasicShare.context
                val fileName = logFile.nameWithoutExtension
                val diskLogLockDir = context.getCacheSubDirByName(diskLogLockDirName)
                val lockFiles = diskLogLockDir.listFiles()
                val lockFile = lockFiles?.find { it.nameWithoutExtension.contains(fileName) }
                if (true == lockFile?.exists()) {
                    logD(
                        "diskLogUnLocked: $fileName ${lockFile.nameWithoutExtension}",
                        loggable = LogsManager,
                    )
                    lockFile.delete()
                }
            }
        }
    }
    
    data class LogLock(
        val pid: Int,
        val time: Long,
        val logFileName: String,
        val lockFile: File,
    )
}