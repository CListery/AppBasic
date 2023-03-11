package com.yh.appbasic.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class FileUtilsTest {
    
    @Test
    fun createLastingFileByType() {
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//        val appContext = AppBasicShare.context
        val array = arrayListOf<String>()
        repeat(100000) {
//            array.add(appContext.createLastingFileByType(FileUtils.DIRECTORY_DCIM, FileUtils.MimeType.JPEG).name)
//            array.add(FileUtils.randomStrGenerator())
            array.add(FileUtils.generateFileName(FileUtils.MimeType.JPEG.prefix, FileUtils.MimeType.JPEG.extensions.first()))
        }
        Truth.assertThat(array).containsNoDuplicates()
        println(array)
    }
}