package com.yh.appbasic.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import android.os.isCurrentLooper
import com.kotlin.memoryId

abstract class ViewBindingFragment<VB : ViewBinding> : Fragment() {
    
    protected val mTag by lazy { "${this::class.java.simpleName}[${this.memoryId}(${mAct.memoryId})]" }
    
    //
    protected var _binder: VB? = null
    protected val uiLooper by lazy { Looper.getMainLooper() }
    protected val uiHandler by lazy { Handler(uiLooper, null) }
    
    //
    protected val mAct: FragmentActivity? by lazy { this.activity }
    protected val mCtx: Context? by lazy { this.context }
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        beforeOnCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
    }
    
    open fun beforeOnCreate(savedInstanceState: Bundle?) {
    
    }
    
    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        return super.onGetLayoutInflater(savedInstanceState)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binder = binderCreator(inflater, container, savedInstanceState)
        if(null == binder) {
            activity?.finish()
            return null
        }
        
        _binder = binder
        
        return binder.root
    }
    
    abstract fun binderCreator(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): VB?
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        preInit(savedInstanceState)
        _binder?.onInit(savedInstanceState)
        afterInit(savedInstanceState)
    }
    
    open fun preInit(savedInstanceState: Bundle?) {
    
    }
    
    abstract fun VB.onInit(savedInstanceState: Bundle?)
    
    open fun afterInit(savedInstanceState: Bundle?) {
    
    }
    
    open fun changeBinder(changer: VB.() -> Unit): Boolean {
        if(null == _binder) {
            return false
        }
        if(uiLooper.isCurrentLooper) {
            try {
                _binder?.changer()
            } catch(e: Exception) {
                e.printStackTrace()
                return false
            }
        } else {
            uiHandler.post {
                changeBinder(changer)
            }
        }
        return true
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
    
    override fun onStart() {
        super.onStart()
    }
    
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }
    
    override fun onResume() {
        super.onResume()
    }
    
    override fun onPause() {
        super.onPause()
    }
    
    override fun onStop() {
        super.onStop()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
    }
    
    override fun onDestroy() {
        super.onDestroy()
    }
    
    override fun onDetach() {
        super.onDetach()
    }
}