package com.yh.appbasic.init

import com.yh.appbasic.logger.logE

object AppBasicShare {
    
    @JvmStatic
    @PublishedApi
    internal val shareInstances = HashMap<String, BasicInitializer?>()
    
    @JvmStatic
    internal fun <I : BasicInitializer> install(initializer: I) {
        shareInstances[initializer::class.java.name] = initializer
    }
    
    @JvmStatic
    internal fun <I : BasicInitializer> uninstall(initializer: I) {
        shareInstances.remove(initializer::class.java.name)
    }
    
    @JvmStatic
    inline fun <reified I : BasicInitializer> get(): I? {
        return get(I::class.java)
    }
    
    @JvmStatic
    fun <I : BasicInitializer> get(iClass: Class<I>): I? {
        val basicName = iClass.name
        if(!shareInstances.containsKey(basicName)){
            logE("AppBasic [${basicName}] not install!")
            shareInstances[basicName] = null
            return null
        }
        @Suppress("UNCHECKED_CAST")
        return shareInstances[basicName] as? I
    }
}