package com.yh.appbasic.logger.impl

import android.os.Process
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.yh.appbasic.logger.FormatStrategy
import com.yh.appbasic.logger.LogStrategy
import com.yh.appbasic.share.AppBasicShare
import java.lang.Integer.*
import kotlin.math.min
import kotlin.reflect.KClass

/**
 * 默认日志格式化工具
 *
 * Created by CYH on 2020/5/15 09:53
 */
class TheLogFormatStrategy private constructor(private val builder: Builder) : FormatStrategy {

    companion object {
        /**
         * Android's max limit for a log entry is ~4076 bytes,
         * so 4000 bytes is used as chunk size since default charset
         * is UTF-8
         */
        private const val CHUNK_SIZE = 2000
        /**
         * Drawing toolbox
         */
        private const val TOP_LEFT_CORNER = '┌'
        private const val BOTTOM_LEFT_CORNER = '└'
        private const val MIDDLE_CORNER = '├'
        private const val HORIZONTAL_LINE = '│'
        private const val DOUBLE_DIVIDER = "───────────────────────"
        private const val SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄"
        private const val TOP_BORDER = "$TOP_LEFT_CORNER$DOUBLE_DIVIDER$DOUBLE_DIVIDER"
        private const val BOTTOM_BORDER = "$BOTTOM_LEFT_CORNER$DOUBLE_DIVIDER$DOUBLE_DIVIDER"
        private const val MIDDLE_BORDER = "$MIDDLE_CORNER$SINGLE_DIVIDER$SINGLE_DIVIDER"
        
        private const val DEF_STACK_METHOD_COUNT = 3
        
        @JvmStatic
        @NonNull
        fun newBuilder(firstTag: String): Builder {
            return Builder(firstTag)
        }
    }

    private var methodCount = 0
    private var stackFilterClassNames: HashSet<String>
    private var showThreadInfo = false
    @NonNull
    private var logStrategy: LogStrategy

    init {
        methodCount = builder.getMethodCount()
        stackFilterClassNames = builder.getStackFilter()
        showThreadInfo = builder.isShowThreadInfo()
        logStrategy = builder.getLogStrategy()
    }

    override fun log(priority: Int, @Nullable onceOnlyTag: String?, @NonNull message: String) {
        val newTag: String = formatTag(onceOnlyTag)

        logTopBorder(priority, newTag)
        val curThread = Thread.currentThread()
        logHeaderContent(priority, newTag, curThread)
        if (methodCount > 0) {
            logStack(priority, newTag, curThread)
            logDivider(priority, newTag)
        }
        logContent(priority, newTag, message)
        logBottomBorder(priority, newTag)
    }

    private fun logTopBorder(logType: Int, @NonNull tag: String) {
        logLine(logType, tag, TOP_BORDER)
    }

    private fun logHeaderContent(logType: Int, @NonNull tag: String, curThread: Thread) {
        if (showThreadInfo) {
            logLine(
                logType,
                tag,
                "$HORIZONTAL_LINE Thread:${curThread.name} ${AppBasicShare.processInfo?.processName ?: ""}:${Process.myPid()}"
            )
            logDivider(logType, tag)
        }
    }

    private fun logStack(logType: Int, @NonNull tag: String, curThread: Thread) {
        var reversedTrace = curThread.stackTrace.toList()
        
        var endIndex = -1
        stackFilterClassNames.forEach { end ->
            val index = reversedTrace.indexOfLast {
                it.className.equals(end)
                        || it.className.split(".").lastOrNull()?.equals(end) ?: false
            }
            if(index >= 0) {
                endIndex = max(index, endIndex)
            }
        }
        if(endIndex >= 0 && endIndex < reversedTrace.size) {
            reversedTrace = reversedTrace.subList(endIndex, reversedTrace.size - 1)
        }
        reversedTrace = reversedTrace.reversed()
        if(reversedTrace.size > methodCount) {
            reversedTrace = reversedTrace.subList(
                reversedTrace.size - 1 - methodCount,
                reversedTrace.size - 1
            )
        }
        if(reversedTrace.isNotEmpty()) {
            var level = ""
            reversedTrace.forEach { traceElement ->
                val builder = StringBuilder()
                builder.append(HORIZONTAL_LINE).append(' ') //
                    .append(level) //
                    .append(getSimpleClassName(traceElement.className)).append(".")
                    .append(traceElement.methodName) // ZygoteInit.main
                    .append(" ") //
                    .append("(").append(traceElement.fileName).append(":")
                    .append(traceElement.lineNumber).append(")") // (ZygoteInit.java:987)
                level += "   "
                logLine(logType, tag, builder.toString())
            }
        }
    }

    private fun logContent(priority: Int, @NonNull tag: String, @NonNull message: String) {
        val msgArr = message.split("\n")
        if (msgArr.size > 1) {
            msgArr.forEach { msg ->
                logContent(priority, tag, msg)
            }
        } else {
            //get bytes of message with system's default charset (which is UTF-8 for Android)
            val bytes = message.toByteArray()
            val length = bytes.size

            if (length <= CHUNK_SIZE) {
                logLine(priority, tag, "$HORIZONTAL_LINE $message")
            } else {
                var pos = 0
                while (pos < length) {
                    var size = min(length - pos, CHUNK_SIZE)
                    //create a new String with system's default charset (which is UTF-8 for Android)
                    val slice = bytes.slice(pos until pos + size)
                    val lastUnicodeStart = slice.lastIndexOf('\\'.code.toByte())
                    if(lastUnicodeStart != -1) {
                        if(lastUnicodeStart.inc() < slice.size) {
                            val nextChar = slice[lastUnicodeStart.inc()].toInt().toChar()
                            if(nextChar == 'u') {
                                if(slice.size - lastUnicodeStart < 6) {
                                    size = lastUnicodeStart
                                }
                            }
                        } else {
                            size = lastUnicodeStart
                        }
                    }
                    logLine(priority, tag, String(bytes, pos, size, Charsets.UTF_8))
                    pos += size
                }
            }
        }
    }

    private fun logBottomBorder(logType: Int, @NonNull tag: String) {
        logLine(logType, tag, BOTTOM_BORDER)
    }

    private fun logDivider(logType: Int, @NonNull tag: String) {
        logLine(logType, tag, MIDDLE_BORDER)
    }

    private fun getSimpleClassName(@NonNull name: String): String? {
        val lastIndex = name.lastIndexOf(".")
        return name.substring(lastIndex + 1)
    }

    private fun logLine(priority: Int, @NonNull tag: String, @NonNull chunk: String) {
        logStrategy.log(priority, tag, chunk)
    }

    @Nullable
    private fun formatTag(@Nullable onceOnlyTag: String?): String {
        return if (!onceOnlyTag.isNullOrEmpty() && builder.firstTag != onceOnlyTag) {
            "${builder.firstTag}-${onceOnlyTag}"
        } else {
            builder.firstTag
        }
    }
    
    override fun release() {
        logStrategy.release()
    }
    
    class Builder(val firstTag: String) {
        /**
         * 日志跟踪行数
         */
        private var methodCount = DEF_STACK_METHOD_COUNT
        /**
         * 是否显示线程信息
         */
        private var showThreadInfo = true
        /**
         * 日志输出方式实例
         */
        @NonNull
        private var logStrategy: LogStrategy = LogcatLogStrategy()
        
        /**
         * 不输出到日志中的类
         */
        private val stackFilterClassNames = hashSetOf(
            "com.yh.appbasic.logger.Logs",
            TheLogPrinter::class.java.name,
            TheLogFormatStrategy::class.java.name,
        )

        /**
         * 获取堆栈过滤
         */
        fun getStackFilter() = stackFilterClassNames

        /**
         * 设置堆栈过滤
         */
        fun setStackFilter(vararg clazz: KClass<*>): Builder {
            stackFilterClassNames.addAll(clazz.map { it.java.name })
            return this
        }

        /**
         * 设置堆栈过滤
         */
        fun setStackFilter(vararg clazz: Class<*>): Builder {
            stackFilterClassNames.addAll(clazz.map { it.name })
            return this
        }

        /**
         * 设置堆栈过滤
         */
        fun setStackFilter(vararg filter: String): Builder {
            stackFilterClassNames.addAll(filter)
            return this
        }

        /**
         * 获取日志跟踪行数
         */
        fun getMethodCount() = methodCount

        /**
         * 设置日志跟踪行数
         */
        @NonNull
        fun setMethodCount(count: Int): Builder {
            methodCount = count
            return this
        }

        /**
         * 是否显示线程信息
         */
        fun isShowThreadInfo() = showThreadInfo

        /**
         * 设置是否显示线程信息
         */
        @NonNull
        fun setShowThreadInfo(showable: Boolean): Builder {
            showThreadInfo = showable
            return this
        }

        /**
         * 获取日志输出方式实例
         */
        fun getLogStrategy() = logStrategy

        /**
         * 设置日志输出方式实例
         */
        @NonNull
        fun setLogStrategy(@NonNull strategy: LogStrategy): Builder {
            logStrategy = strategy
            return this
        }

        @NonNull
        fun build(): TheLogFormatStrategy {
            return TheLogFormatStrategy(this)
        }
    }
}