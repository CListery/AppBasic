package com.kotlin

import androidx.test.platform.app.InstrumentationRegistry
import com.yh.appbasic.share.AppBasicShare
import org.junit.Assert.*
import org.junit.Test

class ExtCharSequenceTest {
    
    @Test
    fun testUnicodeString() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        // val appContext = AppBasicShare.context
        appContext.resources.assets.open("data.json").bufferedReader().useLines {
            it.forEach { line ->
                val decodeUnicodeString = line.decodeUnicodeString()
                assertTrue(line == decodeUnicodeString.encodeUnicodeString())
            }
        }
    }
}