package io.github.clistery.appbasic.demo

import android.content.isMainProcess
import android.content.killProcessExceptMain
import android.os.Bundle
import android.view.onClick
import com.yh.appbasic.share.AppBasicShare
import com.yh.appbasic.ui.ViewBindingActivity
import io.github.clistery.appbasic.demo.databinding.ActThirdBinding

/**
 * Created by CYH on 2020-03-16 15:58
 */
class ThirdAct : ViewBindingActivity<ActThirdBinding>() {
    override fun binderCreator(savedInstanceState: Bundle?): ActThirdBinding =
        ActThirdBinding.inflate(layoutInflater)
    
    override fun ActThirdBinding.onInit(savedInstanceState: Bundle?) {
        btn.onClick {
            killProcessExceptMain()
        }
        btnEndCall.onClick {
            AppBasicShare.context.isMainProcess()
        }
    }
    
}