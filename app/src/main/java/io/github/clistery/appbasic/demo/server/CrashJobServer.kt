package io.github.clistery.appbasic.demo.server

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import androidx.core.app.JobIntentService
import com.yh.appbasic.logger.logD
import com.yh.appbasic.logger.owner.LibLogger

class CrashJobServer : JobIntentService() {
    
    companion object {
        
        /**
         * Unique job ID for this service.
         */
        private const val JOB_ID = 1025
        
        @JvmStatic
        fun enqueueWork(context: Context) {
            logD("enqueueWork: $context", LibLogger)
            enqueueWork(context, CrashJobServer::class.java, JOB_ID, Intent())
        }
    }
    
    override fun onHandleWork(intent: Intent) {
        logD("onHandleWork", LibLogger)
    }
    
    override fun onBind(intent: Intent): IBinder? {
        logD("onBind", LibLogger)
        return super.onBind(intent).also {
            Handler().post {
                Thread.sleep(10000)
            }
        }
    }
    
}