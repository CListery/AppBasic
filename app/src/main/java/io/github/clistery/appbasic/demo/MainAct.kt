package io.github.clistery.appbasic.demo

import android.app.checkSoftInputVisibility
import android.content.Intent
import android.database.MatrixCursor
import android.os.Bundle
import android.util.Log
import android.view.onClick
import com.kotlin.encodeUnicodeString
import com.yh.appbasic.logger.LogAdapter
import com.yh.appbasic.logger.LogOwner
import com.yh.appbasic.logger.LogStrategy
import com.yh.appbasic.logger.LogsManager
import com.yh.appbasic.logger.impl.DiskLogFormatStrategy
import com.yh.appbasic.logger.impl.TheLogFormatStrategy
import com.yh.appbasic.logger.logCursor
import com.yh.appbasic.logger.logD
import com.yh.appbasic.logger.logE
import com.yh.appbasic.logger.logJSON
import com.yh.appbasic.logger.logOwner
import com.yh.appbasic.logger.logW
import com.yh.appbasic.logger.owner.AppLogger
import com.yh.appbasic.ui.ViewBindingActivity
import com.yh.libapp.LibApp
import io.github.clistery.appbasic.demo.databinding.ActMainBinding
import io.github.clistery.appbasic.demo.ext.listenKill
import io.github.clistery.appbasic.demo.server.CrashJobServer
import io.github.clistery.appbasic.demo.server.MySafeJobServer
import io.github.clistery.appbasic.demo.server.TimerIntentServer
import io.github.clistery.appbasic.demo.server.TimerJobServer

class MainAct : ViewBindingActivity<ActMainBinding>() {
    
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
        checkSoftInputVisibility
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
                DiskLogFormatStrategy.Builder(mCtx, "disk-log-${Math.random() * 1000000000}").build()
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
        btnChangeLibAppLogger.onClick {
            LibApp.logOwner.onCreateFormatStrategy {
                TheLogFormatStrategy.newBuilder(it)
                    .setShowThreadInfo(false)
                    .setMethodCount(0)
                    .build()
            }
        }
        btnEncodeUnicode.onClick {
            resources.assets.open("data.json").bufferedReader().useLines {
                it.forEach {
                    LogsManager.appLogger = {
                        LogOwner { "MainAct" }.onCreateFormatStrategy { logTag ->
                            TheLogFormatStrategy.newBuilder(logTag)
                                .setLogStrategy(object : LogStrategy {
                                    override fun log(priority: Int, tag: String, message: String) {
                                        Log.println(priority, tag, message)
                                    }
                                    
                                    override fun release() {
                                    
                                    }
                                })
                                .setShowThreadInfo(false)
                                .setMethodCount(0)
                                .build()
                        }.on()
                    }
                    logD(it.encodeUnicodeString())
                    LogsManager.appLogger = { AppLogger }
                }
            }
        }
        btnDecodeUnicode.onClick {
            resources.assets.open("data.json").bufferedReader().useLines {
                it.forEach {
                    logD(it.encodeUnicodeString())
                }
            }
        }
        btnLongLog.onClick {
            resources.assets.open("data.json").bufferedReader().useLines {
                it.forEach {
                    logD(msg = it)
                }
            }
        }
    }
    
    override fun onResume() {
        logD("onResume")
        super.onResume()
    }
    
}