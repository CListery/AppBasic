package io.github.clistery.appbasic.demo.server

import android.app.IntentService
import android.content.Intent
import com.yh.appbasic.logger.logD

class TimerIntentServer : IntentService("TimerIntentServer") {
    
    override fun onHandleIntent(intent: Intent?) {
        for(index in 0..100){
            Thread.sleep(1000)
            logD("onHandleIntent: $index")
        }
    }
}