package io.github.clistery.appbasic.demo

import android.content.Intent
import android.database.MatrixCursor
import android.os.Bundle
import android.util.Log
import android.view.onClick
import com.yh.appbasic.logger.*
import com.yh.appbasic.logger.impl.DiskLogFormatStrategy
import com.yh.appbasic.ui.ViewBindingActivity
import io.github.clistery.appbasic.demo.databinding.ActMainBinding
import io.github.clistery.appbasic.demo.ext.listenKill
import io.github.clistery.appbasic.demo.server.CrashJobServer
import io.github.clistery.appbasic.demo.server.MySafeJobServer
import io.github.clistery.appbasic.demo.server.TimerIntentServer
import io.github.clistery.appbasic.demo.server.TimerJobServer
import kotlin.random.Random

class MainAct : ViewBindingActivity<ActMainBinding>(), ILoggable {
    
    override fun binderCreator(savedInstanceState: Bundle?): ActMainBinding =
        ActMainBinding.inflate(layoutInflater)
    
    override fun preInit(savedInstanceState: Bundle?) {
        listenKill()
        Log.d("Tag", "I'm a log which you don't see easily, hehe")
        Log.d("json content", "{ \"key\": 3, \n \"value\": something}")
        Log.e("error", "There is a crash somewhere or any warning")
        
        logE("Custom tag for only one use", tag = "tag")
        logJSON("{ \"key\": 3, \"value\": something}")
        logD("this is a arr: ${listOf("foo", "bar")}")
        val map = HashMap<String, String>()
        map["key"] = "value"
        map["key1"] = "value2"
        logD("this is a map: \n$map")
        
        logD(this)
        logE(this)
        logE(RuntimeException("hhhhh"))
        logW(null)
        val cursor = MatrixCursor(arrayOf("id", "name", "age", "len"))
        cursor.newRow().add("id", 1).add("name", "cyh").add("age", 10).add("len", 109.6)
        cursor.newRow().add("id", 2).add("name", "cyh").add("age", 15).add("len", 138.75)
        cursor.newRow().add("id", 3).add("name", "cyh").add("age", 20).add("len", 175.643)
        cursor.newRow().add("id", 4).add("name", "cyh").add("age", 25).add("len", 178.456)
        logCursor(cursor, loggable = this)
        cursor.moveToPosition(2)
        logCursor(cursor, justCurRow = true, this)
        window.decorView.setOnTouchListener { v, event ->
            logW("$v touch $event", this)
            false
        }
    }
    
    override fun ActMainBinding.onInit(savedInstanceState: Bundle?) {
        btnOpenNext.onClick {
            logD("goNext")
            startActivity(Intent(mCtx, SecondAct::class.java))
        }
        btnStartSafeServer.onClick {
            logD("startServer")
            MySafeJobServer.enqueueWork(mCtx)
        }
        btnStartCrashServer.onClick {
            logD("startCrashServer")
            CrashJobServer.enqueueWork(mCtx)
        }
        btnStartTimerIntentServer.onClick {
            logD("startTimerIntentServer")
            startService(Intent(mCtx, TimerIntentServer::class.java))
        }
        btnStartTimerJobServer.onClick {
            logD("startTimerJobServer")
            TimerJobServer.enqueueWork(mCtx)
        }
        val diskLogs = arrayListOf<LogAdapter>()
        btnCreateDiskLog.onClick {
            val logFormatStrategy =
                DiskLogFormatStrategy.Builder(mCtx, "disk-log-${Random.Default.nextInt()}").build()
            Log.d("APP", "log file: ${logFormatStrategy.getRealLogFile()}")
            val logAdapter = LogAdapter(logFormatStrategy)
            diskLogs.add(logAdapter)
            logD(msg = "create disk log", loggable = logAdapter)
        }
        btnReleaseDiskLogAdapter.onClick {
            diskLogs.forEach {
                it.release()
            }
            diskLogs.clear()
        }
        btnCleanUpLogs.onClick {
            LogsManager.cleanup(mCtx, false)
        }
    }
    
    override fun onResume() {
        logD("onResume")
        super.onResume()
    }
    
}