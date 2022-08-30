package io.github.clistery.appbasic.demo.server

import android.content.Context
import android.content.Intent
import androidx.core.app.SafeJobIntentService
import com.yh.appbasic.logger.logD

class TimerJobServer : SafeJobIntentService() {
    
    companion object {
        
        /**
         * Unique job ID for this service.
         */
        private const val JOB_ID = 1026
        
        @JvmStatic
        fun enqueueWork(context: Context) {
            logD("enqueueWork: $context")
            enqueueWork(context, TimerJobServer::class.java, JOB_ID, Intent())
        }
    }
    
    override fun onHandleWork(intent: Intent) {
        for(index in 0..100){
            Thread.sleep(1000)
            logD("onHandleWork: $index")
        }
    }
}