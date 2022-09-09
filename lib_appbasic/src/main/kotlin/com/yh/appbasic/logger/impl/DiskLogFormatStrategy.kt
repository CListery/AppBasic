package com.yh.appbasic.logger.impl

import android.content.Context
import android.os.HandlerThread
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.kotlin.isInitialized
import com.kotlin.timeCurMillisecond
import com.kotlin.timeFormatDate
import com.yh.appbasic.logger.FormatStrategy
import com.yh.appbasic.logger.LogStrategy
import com.yh.appbasic.logger.LogsManager
import com.yh.appbasic.util.FileUtils.createCacheFile
import java.io.File

class DiskLogFormatStrategy private constructor(private val builder: Builder) : FormatStrategy {
    
    companion object {
        private const val DATE_FORMAT = "yyyy.MM.dd_HH:mm:ss.SSS"
        
        private const val TOP_LEFT_CORNER = "┌ "
        private const val BOTTOM_LEFT_CORNER = "└ "
        private const val HORIZONTAL_LINE = "│ "
        private const val NEW_LINE = "\n"
        private const val HEAD_SEPARATOR = "-> "
        private const val CONTENT_SEPARATOR = " "
        const val LOG_FILE_NAME_SEPARATOR = "_"
    }
    
    private val logFile: File by lazy {
        builder.context.createCacheFile(
            dir = listOfNotNull(
                LogsManager.diskLogRootDirName,
                builder.moduleName,
            ).joinToString(File.separator),
            fileName = listOfNotNull(
                builder.moduleName,
                LogsManager.diskLogFileName()
            ).joinToString(LOG_FILE_NAME_SEPARATOR).plus(".log"),
        )
    }
    
    private val logStrategy: LogStrategy by lazy {
        LogsManager.diskLogLocked(logFile)
        builder.logStrategy ?: let {
            val logThread = HandlerThread("Logger.${logFile.name}")
            logThread.start()
            DiskLogStrategy(DiskLogStrategy.WriteHandler(logThread.looper, logFile))
        }
    }
    
    fun getRealLogFile(): File = logFile
    
    override fun log(priority: Int, onceOnlyTag: String?, message: String) {
        val tag = formatTag(onceOnlyTag)
        
        val builder = StringBuilder()
        // message
        logContent(buildHeader(priority, tag), builder, message)
        
        logStrategy.log(priority, "", builder.toString())
    }
    
    private fun logContent(
        @NonNull header: String,
        @NonNull builder: StringBuilder,
        @NonNull message: String,
    ) {
        val msgArr = message.split("\n")
        if (msgArr.size > 1) {
            msgArr.forEachIndexed { index, msg ->
                when (index) {
                    0 -> builder.logLine(header, msg, TOP_LEFT_CORNER)
                    msgArr.size - 1 -> builder.logLine(header, msg, BOTTOM_LEFT_CORNER)
                    else -> builder.logLine(header, msg, HORIZONTAL_LINE)
                }
            }
        } else {
            builder.logLine(header, message)
        }
    }
    
    private fun StringBuilder.logLine(
        @NonNull header: String,
        @NonNull content: String,
        @NonNull linePrefix: String = "",
    ) {
        append(header).append(linePrefix).append(content).append(NEW_LINE)
    }
    
    private fun buildHeader(priority: Int, tag: String?): String {
        val builder = StringBuilder()
        
        // date/time
        builder.append(timeCurMillisecond.timeFormatDate(DATE_FORMAT))
        builder.append(CONTENT_SEPARATOR)
        
        // level
        builder.append(level2String(priority))
        if (!tag.isNullOrEmpty()) {
            builder.append("/")
            // tag
            builder.append(tag)
        }
        builder.append(HEAD_SEPARATOR)
        return builder.toString()
    }
    
    @Nullable
    private fun formatTag(@Nullable onceOnlyTag: String?): String? {
        return if (onceOnlyTag.isNullOrEmpty()) {
            builder.firstTag
        } else {
            onceOnlyTag
        }
    }
    
    private fun level2String(value: Int): String {
        return when (value) {
            Log.VERBOSE -> "V"
            Log.INFO -> "I"
            Log.WARN -> "W"
            Log.ERROR -> "E"
            Log.ASSERT -> "A"
            else -> "D"
        }
    }
    
    override fun release() {
        if (this::logStrategy.isInitialized()) {
            logStrategy.release()
            LogsManager.diskLogUnLocked(logFile)
        }
    }
    
    class Builder(val context: Context, val moduleName: String) {
        internal var firstTag: String? = null
        internal var logStrategy: LogStrategy? = null
        
        fun logStrategy(@Nullable logStrategy: LogStrategy?): Builder {
            this.logStrategy = logStrategy
            return this
        }
        
        @NonNull
        fun firstTag(@Nullable tag: String?): Builder {
            this.firstTag = tag
            return this
        }
        
        @NonNull
        fun build(): DiskLogFormatStrategy {
            return DiskLogFormatStrategy(this)
        }
    }
}