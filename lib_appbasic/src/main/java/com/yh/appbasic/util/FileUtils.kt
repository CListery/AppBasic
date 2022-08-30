@file:Suppress("unused")

package com.yh.appbasic.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.provider.MediaStore
import com.kotlin.timeCurMillisecond
import com.kotlin.timeFormatDate
import com.yh.appbasic.logger.logE
import com.yh.appbasic.logger.logW
import java.io.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs
import kotlin.random.Random

/**
 * Created by CYH on 2020/7/14 14:20
 */
object FileUtils {
    
    @JvmStatic
    var LASTING_DIR_NAME = "FangStar"
    
    @JvmStatic
    private val FILE_SPLIT = File.separator
    
    const val DIRECTORY_MUSIC = "Music"
    const val DIRECTORY_PODCASTS = "Podcasts"
    const val DIRECTORY_RINGTONES = "Ringtones"
    const val DIRECTORY_ALARMS = "Alarms"
    const val DIRECTORY_NOTIFICATIONS = "Notifications"
    const val DIRECTORY_PICTURES = "Pictures"
    const val DIRECTORY_MOVIES = "Movies"
    const val DIRECTORY_DOWNLOADS = "Download"
    const val DIRECTORY_DCIM = "DCIM"
    const val DIRECTORY_DOCUMENTS = "Documents"
    const val DIRECTORY_SCREENSHOTS = "Screenshots"
    const val DIRECTORY_AUDIOBOOKS = "Audiobooks"
    
    @JvmStatic
    private lateinit var mLastingDir: File
    
    @JvmStatic
    private fun ensureLastingDir(context: Context) {
        var parentDir = Environment.getExternalStorageDirectory()
        if (null == parentDir) {
            // /sdcard
            parentDir = File("${FILE_SPLIT}sdcard")
        }
        if (!parentDir.canWrite()) {
            // /sdcard/Download
            parentDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        }
        if (null == parentDir) {
            // /sdcard/Download
            parentDir = File("${FILE_SPLIT}sdcard/Download")
        }
        if (!parentDir.exists()) {
            parentDir.mkdirs()
        }
        if (!parentDir.canWrite()) {
            // /sdcard/Android/data/{package}
            parentDir = context.getExternalFilesDir("")
        }
        if (null == parentDir) {
            // /data/data/{package}
            parentDir = context.getDir("", Context.MODE_PRIVATE)
        }
        if (!parentDir.exists()) {
            parentDir.mkdirs()
        }
        logW("$parentDir")
        mLastingDir = File(parentDir, LASTING_DIR_NAME)
    }
    
    /**
     * 判断外部存储是否可用
     */
    @JvmStatic
    fun isExternalStorageWritable(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }
    
    /**
     * 获取持久目录子目录
     *
     * @return [mLastingDir]
     */
    @JvmStatic
    val Context.lastingDir
        get(): File {
            ensureLastingDir(this)
            return mLastingDir.apply {
                if (!exists()) {
                    mkdirs()
                }
            }
        }
    
    /**
     * 获取持久目录子目录
     *
     * @return
     * - [mLastingDir]/[dirName]
     */
    @JvmStatic
    fun Context.getLastingSubDirByName(dirName: String): File {
        ensureLastingDir(this)
        return File(mLastingDir, dirName).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    
    /**
     * 获取应用内部目录
     * /data/data/{package}/files
     */
    @JvmStatic
    val Context.internalDir
        get():File = filesDir
    
    /**
     * 获取应用内部目录
     * [internalDir]/[dirName]
     */
    @JvmStatic
    fun Context.getInternalSubDirByName(dirName: String): File {
        return File(internalDir, dirName).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    
    /**
     * 获取缓存目录
     *
     * ```
     *  if(internalStorage){
     *      /data/data/{package}/cache
     *  }else{
     *      /sdcard/Android/data/{package}/cache
     *  }
     * ```
     */
    @JvmStatic
    fun Context.getCacheDir(internalStorage: Boolean = true): File {
        return if (internalStorage || !isExternalStorageWritable()) {
            cacheDir
        } else {
            externalCacheDir ?: cacheDir
        }.apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    
    /**
     * 获取缓存目录子目录
     *
     * @return [getCacheDir]/[dirName]
     */
    @JvmStatic
    fun Context.getCacheSubDirByName(dirName: String, internalStorage: Boolean = true): File {
        val cacheDir = getCacheDir(internalStorage)
        val destDir = File(dirName)
        return if (destDir.absolutePath.startsWith(cacheDir.absolutePath, true)) {
            File(cacheDir, destDir.absolutePath.removePrefix(cacheDir.absolutePath))
        } else {
            File(cacheDir, dirName)
        }.apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    
    /**
     * 创建缓存文件
     */
    @JvmStatic
    fun Context.createCacheFile(dir: String, prefix: String, suffix: String): File {
        return File(getCacheSubDirByName(dir), generateFileName(prefix, suffix))
    }
    
    /**
     * 创建缓存文件
     */
    @JvmStatic
    fun Context.createCacheFile(dir: String, fileName: String): File {
        return File(getCacheSubDirByName(dir), fileName)
    }
    
    /**
     * 创建缓存文件
     */
    @JvmStatic
    fun Context.createCacheFileByType(dir: String, fileType: MimeType): File {
        return createCacheFile(dir, fileType.prefix, fileType.extensions.toTypedArray().first())
    }
    
    /**
     * 创建缓存文件
     */
    @JvmStatic
    fun Context.createCacheFileByName(dir: String, fileName: String): File {
        return File(getCacheSubDirByName(dir), fileName)
    }
    
    /**
     * 创建持久文件
     */
    @JvmStatic
    fun Context.createLastingFile(dir: String, prefix: String, suffix: String): File {
        return createLastingFileByName(dir, generateFileName(prefix, suffix))
    }
    
    /**
     * 创建持久文件
     */
    @JvmStatic
    fun Context.createLastingFileByType(dir: String, fileType: MimeType): File {
        return createLastingFileByName(
            dir,
            generateFileName(fileType.prefix, fileType.extensions.toTypedArray().first())
        )
    }
    
    /**
     * 创建持久文件
     */
    @JvmStatic
    fun Context.createLastingFileByName(dir: String, fileName: String): File {
        return File(getLastingSubDirByName(dir), fileName)
    }
    
    @JvmStatic
    @JvmOverloads
    fun Context.insertImage(
        file: File,
        name: String = file.name,
        bucketName: String = getString(applicationInfo.labelRes),
    ) {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, file.name)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        contentValues.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, bucketName)
        contentValues.put(MediaStore.Images.Media.DATA, file.absolutePath)
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        }
        val uri = applicationContext.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        if (null != uri) {
            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
        }
    }
    
    private object RandomNumberGeneratorHolder {
        
        @JvmStatic
        fun safeInt(bound: Int = 8): Int {
            var num = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ThreadLocalRandom.current().nextInt(bound)
            } else {
                Random.nextInt(bound)
            }
            num = if (num == Int.MIN_VALUE) {
                0 // corner case
            } else {
                abs(num)
            }
            return num
        }
    }
    
    /**
     * 生成文件名
     */
    @JvmStatic
    private fun generateFileName(prefix: String, suffix: String): String {
        return listOf(
            prefix,
            timeCurMillisecond.timeFormatDate("yyyyMMdd_HHmmss"),
            RandomNumberGeneratorHolder.safeInt().toString(),
        ).joinToString("_").plus(".$suffix")
    }
    
    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    @JvmStatic
    fun copy(oldPath: String?, newPath: String?): Boolean {
        if (null == oldPath || null == newPath) {
            return false
        }
        return copyFile(File(oldPath), File(newPath))
    }
    
    /**
     * 复制单个文件
     *
     * @param oldFile 原文件路径 如：c:/fqf.txt
     * @param newFile 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    @JvmStatic
    @JvmOverloads
    fun copyFile(oldFile: File?, newFile: File?, append: Boolean = false): Boolean {
        if (null == oldFile || null == newFile) {
            return false
        }
        if (oldFile.isDirectory || newFile.isDirectory) {
            return false
        }
        if (oldFile.exists()) { //文件存在时
            return copy(
                BufferedInputStream(FileInputStream(oldFile)),
                BufferedOutputStream(FileOutputStream(newFile, append))
            )
        }
        return false
    }
    
    @JvmStatic
    fun copy(inStream: InputStream, outputStream: OutputStream): Boolean {
        var byteread: Int
        try {
            inStream.use { input ->
                outputStream.use { output ->
                    val buffer = ByteArray(8192)
                    while (input.read(buffer).also { byteread = it } != -1) {
                        output.write(buffer, 0, byteread)
                    }
                    return true
                }
            }
        } catch (e: Exception) {
            logE(throwable = e)
        }
        return false
    }
    
    @JvmStatic
    fun fileToByteArray(file: File): ByteArray? {
        try {
            FileInputStream(file).use { fis ->
                val inputStream = BufferedInputStream(fis)
                val freeMemory =
                    (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory()) / 2
                if (freeMemory < fis.channel.size()) {
                    // avoid OOM
                    return null
                }
                ByteArrayOutputStream().use { outputStream ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        FileUtils.copy(inputStream, outputStream)
                    } else {
                        copyInternalUserspace(inputStream, outputStream)
                    }
                    return outputStream.toByteArray()
                }
            }
        } catch (e: Exception) {
            return null
        }
    }
    
    @JvmStatic
    private fun copyInternalUserspace(inputStream: InputStream, outputStream: OutputStream): Long {
        var progress: Long = 0
        var checkpoint: Long = 0
        val buffer = ByteArray(8192)
        var len: Int
        while (-1 != inputStream.read(buffer).also { len = it }) {
            outputStream.write(buffer, 0, len)
            progress += len
            checkpoint += len
        }
        return progress
    }
    
    @JvmStatic
    @JvmOverloads
    fun deleteAll(file: File?, withOutFilter: ((File) -> Boolean)? = null) {
        if (null == file) {
            return
        }
        if (!file.exists()) {
            return
        }
        if (true == withOutFilter?.invoke(file)) {
            return
        }
        if (file.isFile) {
            file.delete()
        } else {
            fun deleteRecursively(dir: File): Boolean {
                val files = dir.listFiles()
                var canDeleteDir = true
                if (!files.isNullOrEmpty()) {
                    files.forEach { f ->
                        if (true == withOutFilter?.invoke(f)) {
                            if (f.isDirectory) {
                                // 跳过整个目录
                                return false
                            }
                            canDeleteDir = false
                            return@forEach
                        }
                        if (f.isFile) {
                            f.delete()
                        } else {
                            canDeleteDir = deleteRecursively(f)
                        }
                    }
                }
                if (canDeleteDir) {
                    dir.delete()
                }
                return canDeleteDir
            }
            deleteRecursively(file)
        }
    }
    
    
    enum class MimeType(
        val prefix: String,
        val mimeTypeName: String,
        val extensions: List<String>,
    ) {
        // ============== images ==============
        JPEG("IMG", "image/jpeg", listOf("jpg", "jpeg")),
        PNG("IMG", "image/png", listOf("png")),
        GIF("GIF", "image/gif", listOf("gif")),
        BMP("IMG", "image/x-ms-bmp", listOf("bmp")),
        WEBP("IMG", "image/webp", listOf("webp")),
        
        // ============== videos ==============
        MPEG("VID", "video/mpeg", listOf("mpeg", "mpg")),
        MP4("VID", "video/mp4", listOf("mp4", "m4v")),
        QUICKTIME("VID", "video/quicktime", listOf("mov")),
        THREEGPP("VID", "video/3gpp", listOf("3gp", "3gpp")),
        THREEGPP2("VID", "video/3gpp2", listOf("3g2", "3gpp2")),
        MKV("VID", "video/x-matroska", listOf("mkv")),
        WEBM("VID", "video/webm", listOf("webm")),
        TS("VID", "video/mp2ts", listOf("ts")),
        AVI("VID", "video/avi", listOf("avi")),
        
        // =============== text ===============
        PLAIN("TXT", "text/plain", listOf("txt", "text", "TXT", "TEXT")),
        HTML("HTML", "text/html", listOf("html", "htm", "HTML", "HTM")),
        LOG("LOG", "text/log", listOf("log")),
        
        ;
    }
    
}
