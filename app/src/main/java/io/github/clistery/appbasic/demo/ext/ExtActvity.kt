package io.github.clistery.appbasic.demo.ext

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.github.clistery.appbasic.demo.App

internal const val ACTION_KILL_ACT = "action_kill_act"

fun Activity?.listenKill() {
    if (null == this) return
    LocalBroadcastManager.getInstance(App.get())
        .registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                LocalBroadcastManager.getInstance(App.get()).unregisterReceiver(this)
                if (ACTION_KILL_ACT == intent?.action) {
                    finish()
                }
            }
        }, IntentFilter(ACTION_KILL_ACT))
}
