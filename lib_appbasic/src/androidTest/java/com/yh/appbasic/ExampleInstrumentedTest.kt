package com.yh.appbasic

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.yh.appbasic.share.AppBasicShare
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
//        val appContext = ApplicationProvider.getApplicationContext<Context>()
        val appContext = AppBasicShare.context
        assertThat("com.yh.appbasic.test").isEqualTo(appContext.packageName)
    }
}