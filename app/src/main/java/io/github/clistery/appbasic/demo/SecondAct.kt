package io.github.clistery.appbasic.demo

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import android.view.onClick
import androidx.appcompat.app.AlertDialog
import com.yh.appbasic.logger.logD
import com.yh.appbasic.logger.owner.AppLogger
import com.yh.appbasic.logger.owner.LibLogger
import com.yh.appbasic.ui.ViewBindingActivity
import com.yh.libapp.A
import com.yh.libapp.B
import io.github.clistery.appbasic.demo.databinding.ActSecBinding
import io.github.clistery.appbasic.demo.ext.listenKill

class SecondAct : ViewBindingActivity<ActSecBinding>() {
    override fun binderCreator(savedInstanceState: Bundle?): ActSecBinding =
        ActSecBinding.inflate(layoutInflater)
    
    override fun preInit(savedInstanceState: Bundle?) {
        listenKill()
    }
    
    override fun ActSecBinding.onInit(savedInstanceState: Bundle?) {
        A()
        B()
        logD("onInit", loggable = this@SecondAct)
        btn.onClick {
            App.get().kill(false)
        }
        fab.onClick {
            showAlert()
        }
    }
    
    private fun showAlert() {
        val alertDialog =
            AlertDialog.Builder(
                mCtx,
                androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert
            ).setTitle("haha").create()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            alertDialog.apply {
                window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
            }
        } else {
            if (Settings.canDrawOverlays(mCtx)) {
                alertDialog.apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                    } else {
                        window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
                    }
                    show()
                }
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:${packageName}")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }
    
    override fun onDestroy() {
        LibLogger.on()
        AppLogger.off()
        
        super.onDestroy()
    }
}