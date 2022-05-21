package com.yh.appbasic.logger.impl

import android.database.Cursor
import android.text.TextUtils
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.database.getBlobOrNull
import androidx.core.database.getFloatOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.yh.appbasic.logger.LogAdapter
import com.yh.appbasic.logger.Printer
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.PrintWriter
import java.io.StringReader
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/**
 * 日志输出器实例
 *
 * Created by CYH on 2020/5/14 14:35
 */
internal class TheLogPrinter : Printer {

    /**
     * 提供用于日志消息的一次性标签
     */
    private val localTag = ThreadLocal<String>()
    /**
     * 提供用于日志消息的一日志适配器
     */
    private val localAdapter = ThreadLocal<LogAdapter>()

    override fun clearLogAdapters() {
        localAdapter.remove()
    }

    override fun addAdapter(@NonNull adapter: LogAdapter?) {
        throw RuntimeException("Can not add!")
    }

    /**
     * 设置一次性日志适配器
     */
    fun adapter(adapter: LogAdapter?): Printer {
        if (null != adapter) {
            localAdapter.set(adapter)
        }
        return this
    }

    override fun t(tag: String?): Printer {
        if (tag != null) {
            localTag.set(tag)
        }
        return this
    }

    //    override fun d(@Nullable message: Any?) {
    //        log(Log.DEBUG, null, message?.toString())
    //    }

    override fun d(@NonNull message: String, @Nullable vararg args: Any?) {
        log(Log.DEBUG, null, message, *args)
    }

    override fun i(@NonNull message: String, @Nullable vararg args: Any?) {
        log(Log.INFO, null, message, *args)
    }

    override fun v(@NonNull message: String, @Nullable vararg args: Any?) {
        log(Log.VERBOSE, null, message, *args)
    }

    override fun w(@NonNull message: String, @Nullable vararg args: Any?) {
        log(Log.WARN, null, message, *args)
    }

    override fun e(@NonNull message: String, @Nullable vararg args: Any?) {
        log(Log.ERROR, null, message, *args)
    }

    override fun e(@Nullable throwable: Throwable?, @NonNull message: String, @Nullable vararg args: Any?) {
        log(Log.ERROR, throwable, message, *args)
    }

    override fun wtf(@NonNull message: String, @Nullable vararg args: Any?) {
        log(Log.ASSERT, null, message, *args)
    }

    override fun json(jsonAny: Any?) {
        when (jsonAny) {
            is CharSequence -> {
                json(jsonAny.toString())
            }
            is JSONObject -> {
                json(jsonAny)
            }
            is JSONArray -> {
                json(jsonAny)
            }
            is Any -> {
                d(jsonAny.toString())
            }
            null -> {
                d("Empty/Null json content")
            }
        }
    }

    private fun json(json: JSONArray?) {
        if (null == json) {
            d("Empty json content")
            return
        }
        d(json.toString(2))
    }

    private fun json(json: JSONObject?) {
        if (null == json) {
            d("Null json content")
            return
        }
        d(json.toString(2))
    }

    private fun json(json: String?) {
        if (null == json || TextUtils.isEmpty(json)) {
            d("Empty/Null json content")
            return
        }
        try {
            val jsonTmp = json.trim()
            if (jsonTmp.startsWith("{")) {
                json(JSONObject(jsonTmp))
                return
            }
            if (jsonTmp.startsWith("[")) {
                json(JSONArray(jsonTmp))
                return
            }
            e("Invalid Json: $json")
        } catch (e: JSONException) {
            e("Invalid Json: $json")
        }
    }

    override fun xml(xml: Any?) {
        if (null == xml) {
            d("Null xml content")
            return
        }
        val newXml = (xml as? CharSequence)?.toString()
        if (null == newXml) {
            d("Not is xml content: $xml")
            return
        }
        if (TextUtils.isEmpty(newXml)) {
            d("Empty xml content")
            return
        }
        try {
            val xmlInput: Source = StreamSource(StringReader(newXml))
            val xmlOutput = StreamResult(StringWriter())
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(xmlInput, xmlOutput)
            d(xmlOutput.writer.toString().replaceFirst(">", ">\n"))
        } catch (e: TransformerException) {
            e("Invalid xml: $xml")
        }
    }

    override fun cursor(cursor: Cursor?, justCurrentRow: Boolean) {
        if (null == cursor || cursor.count <= 0) {
            d("Empty/Null cursor content")
            return
        }
        val builder = StringBuilder()
        if (justCurrentRow) {
            if(cursor.position == -1) {
                if(cursor.moveToFirst()) {
                    cursorCurRow(cursor, builder)
                    d(builder.toString())
                    cursor.moveToPrevious()
                } else {
                    d("Invalid cursor content")
                }
                return
            }
            cursorCurRow(cursor, builder)
            d(builder.toString())
            return
        }
        val startPos = cursor.position
        if (cursor.moveToFirst()) {
            do {
                cursorCurRow(cursor, builder)
                builder.append("\n")
            } while (cursor.moveToNext())
            builder.deleteCharAt(builder.length - 1)
        }
        cursor.moveToPosition(startPos)
        d(builder.toString())
    }

    private fun cursorCurRow(cursor: Cursor, builder: StringBuilder) {
        val cols = cursor.columnNames
        builder.append(cursor.position).append(" {")
        cols.forEachIndexed { index, colName ->
            val colIndex = cursor.getColumnIndex(colName)
            builder.append(colName).append("=").append(getCursorColValue(cursor, colIndex))
            if (index != cols.size - 1) {
                builder.append(", ")
            }
        }
        builder.append("}")
    }

    private fun getCursorColValue(cursor: Cursor, colIndex: Int): String {
        if(-1 == colIndex) return "<ERR INDEX>"
    
        cursor.apply {
            return when(cursor.getType(colIndex)) {
                Cursor.FIELD_TYPE_NULL    -> "<NULL>"
                Cursor.FIELD_TYPE_INTEGER -> getIntOrNull(colIndex)
                Cursor.FIELD_TYPE_FLOAT   -> getFloatOrNull(colIndex)
                Cursor.FIELD_TYPE_STRING  -> getStringOrNull(colIndex)
                Cursor.FIELD_TYPE_BLOB    -> getBlobOrNull(colIndex)
                else                      -> "<UNKNOWN>"
            }?.toString()
                ?: "<NULL>"
        }
    }

    override fun log(priority: Int, tag: String?, message: String?, throwable: Throwable?) {
        val newMsg = StringBuilder(message ?: "")
        if (null != throwable) {
            if (!TextUtils.isEmpty(newMsg)) {
                newMsg.append(" : ")
            }
            newMsg.append(getStackTraceString(throwable))
        }
        if (TextUtils.isEmpty(newMsg)) {
            newMsg.append("Empty/NULL log message")
        }
        val adapter = getAdapter()
            ?: return
        if (adapter.isLoggable(priority, tag)) {
            adapter.log(priority, tag, newMsg.toString())
        }
    }

    /**
     * This method is synchronized in order to avoid messy of logs' order.
     */
    @Synchronized
    private fun log(priority: Int, @Nullable throwable: Throwable?, @Nullable msg: String?, @Nullable vararg args: Any?) {
        val tag = getTag()
        val message = createMessage(msg, *args)
        log(priority, tag, message, throwable)
    }

    /**
     * @return the appropriate tag based on local or global
     */
    @Nullable
    private fun getTag(): String? {
        val tag = localTag.get()
        if (tag != null) {
            localTag.remove()
            return tag
        }
        return null
    }

    @Nullable
    private fun getAdapter(): LogAdapter? {
        val adapter = localAdapter.get()
        localAdapter.remove()
        return adapter
    }

    @NonNull
    private fun createMessage(@Nullable message: String?, @Nullable vararg args: Any?): String {
        val newMsg = message
            ?: ""
        return if (TextUtils.isEmpty(newMsg)) {
            if (args.isNotEmpty()) {
                args.contentToString()
            } else {
                newMsg
            }
        } else {
            if (args.isNotEmpty()) {
                try {
                    String.format(newMsg, *args)
                } catch (e: Exception) {
                    newMsg.plus(args.contentToString())
                }
            } else {
                newMsg
            }
        }
    }

    private fun getStackTraceString(t: Throwable): String {
        // Don't replace this with Log.getStackTraceString() - it hides
        // UnknownHostException, which is not what we want.
        val sw = StringWriter(256)
        val pw = PrintWriter(sw, false)
        t.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }
}