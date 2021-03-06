package com.yh.appbasic.logger.impl

import android.os.Process
import android.text.TextUtils
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.yh.appbasic.BuildConfig
import com.yh.appbasic.logger.FormatStrategy
import com.yh.appbasic.logger.LogStrategy
import kotlin.math.min

/**
 * 默认日志格式化工具
 *
 * Created by CYH on 2020/5/15 09:53
 */
class TheLogFormatStrategy private constructor(builder: Builder) : FormatStrategy {

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
        private const val DOUBLE_DIVIDER =
            "────────────────────────────────────────────────────────"
        private const val SINGLE_DIVIDER =
            "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄"
        private const val TOP_BORDER = "$TOP_LEFT_CORNER$DOUBLE_DIVIDER$DOUBLE_DIVIDER"
        private const val BOTTOM_BORDER = "$BOTTOM_LEFT_CORNER$DOUBLE_DIVIDER$DOUBLE_DIVIDER"
        private const val MIDDLE_BORDER = "$MIDDLE_CORNER$SINGLE_DIVIDER$SINGLE_DIVIDER"

        @NonNull
        fun newBuilder(): Builder {
            return Builder()
        }
    }

    private var methodCount = 0
    private var stackFilterClassNames: HashSet<String>
    private var showThreadInfo = false
    @NonNull
    private var logStrategy: LogStrategy
    @Nullable
    private var firstTag: String

    init {
        methodCount = builder.getMethodCount()
        stackFilterClassNames = builder.getStackFilter()
        showThreadInfo = builder.isShowThreadInfo()
        logStrategy = builder.getLogStrategy()
            ?: LogcatLogStrategy()
        firstTag = builder.getFirstTag()
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
                "$HORIZONTAL_LINE Thread:${curThread.name} PID:${Process.myPid()}"
            )
            logDivider(logType, tag)
        }
    }

    private fun logStack(logType: Int, @NonNull tag: String, curThread: Thread) {
        val stackTrace = curThread.stackTrace
        var level = ""
        val reversedTrace = stackTrace.reversed()
        val endIndex = reversedTrace.indexOfFirst { stackFilterClassNames.contains(it.className) }
        val startIndex = if (endIndex > methodCount) {
            endIndex - methodCount
        } else {
            0
        }
        reversedTrace.subList(startIndex, endIndex).forEach { traceElement ->
            if (stackFilterClassNames.contains(traceElement.className)) {
                return
            }
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
            } else if (length > CHUNK_SIZE) {
                var index = 0
                while (index < length) {
                    val size = min(length - index, CHUNK_SIZE)
                    //create a new String with system's default charset (which is UTF-8 for Android)
                    logLine(priority, tag, String(bytes, index, size, Charsets.UTF_8))
                    index += CHUNK_SIZE
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
        return if (!onceOnlyTag.isNullOrEmpty() && !TextUtils.equals(firstTag, onceOnlyTag)) {
            firstTag.plus('-').plus(onceOnlyTag)
        } else {
            firstTag
        }
    }
    
    override fun release() {
        logStrategy.release()
    }
    
    class Builder {
        /**
         * 日志跟踪行数
         */
        private var methodCount = BuildConfig.LOG_METHOD_COUNT
        /**
         * 是否显示线程信息
         */
        private var showThreadInfo = true
        /**
         * 日志输出方式实例
         */
        @Nullable
        private var logStrategy: LogStrategy? = null
        /**
         * 默认日志标签
         */
        @Nullable
        private var firstTag = "|"
        /**
         * 不输出到日志中的类
         */
        private val stackFilterClassNames = hashSetOf(
            Class.forName("com.yh.appbasic.logger.Logs").name,
            Class.forName("com.yh.appbasic.logger.LibLogs").name,
            Class.forName("com.yh.appbasic.logger.ext.ExtILogOwner").name,
            TheLogPrinter::class.java.name
        )

        /**
         * 获取堆栈过滤
         */
        fun getStackFilter() = stackFilterClassNames

        /**
         * 设置堆栈过滤
         */
        fun setStackFilter(vararg clazz: Class<*>): Builder {
            clazz.forEach {
                stackFilterClassNames.add(it.name)
            }
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
        fun setLogStrategy(@Nullable strategy: LogStrategy?): Builder {
            logStrategy = strategy
            return this
        }

        /**
         * 获取默认日志标签
         */
        fun getFirstTag() = firstTag

        /**
         * 设置默认日志标签
         * @param [tag] 日志标签
         */
        @NonNull
        fun setFirstTag(@Nullable tag: String?): Builder {
            if (null == tag) {
                return this
            }
            this.firstTag = tag
            return this
        }

        @NonNull
        fun build(): TheLogFormatStrategy {
            return TheLogFormatStrategy(this)
        }
    }
}