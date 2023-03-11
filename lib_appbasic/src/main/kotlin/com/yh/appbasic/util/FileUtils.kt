@file:Suppress("unused")

package com.yh.appbasic.util

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.get
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.kotlin.runCatchingSafety
import com.kotlin.runOnApiUp
import com.kotlin.timeCurMillisecond
import com.kotlin.timeFormatDate
import com.yh.appbasic.logger.logE
import com.yh.appbasic.logger.logW
import com.yh.appbasic.share.AppBasicShare
import java.io.*
import java.security.SecureRandom
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.roundToInt

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
    private val sr = SecureRandom()
    
    @JvmStatic
    private fun ensureLastingDir(context: Context) {
        @Suppress("DEPRECATION")
        var parentDir = Environment.getExternalStorageDirectory()
        if (null == parentDir) {
            // /sdcard
            parentDir = File("${FILE_SPLIT}sdcard")
        }
        if (!parentDir.canWrite()) {
            // /sdcard/Download
            @Suppress("DEPRECATION")
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
    
    /**
     * 向系统数据库中插入一张图片
     */
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
        @Suppress("DEPRECATION")
        contentValues.put(MediaStore.Images.Media.DATA, file.absolutePath)
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, name)
        runOnApiUp(Build.VERSION_CODES.P) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        }
        val uri = applicationContext.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        if (null != uri) {
            @Suppress("DEPRECATION")
            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
        }
    }
    
    internal fun randomStrGenerator(): String {
        val inputs = IntArray(16) { sr.nextInt() }
        
        var msb = 0
        var lsb = 0
        
        inputs.forEachIndexed { index, num ->
            if (index > 7) {
                lsb = lsb.shl(8).or(num.and(0xFF))
            } else {
                msb = msb.shl(8).or(num.and(0xFF))
            }
        }
        
        fun Int.digits(digits: Int, offset: Int = 0): String {
            if (digits > 15) {
                throw IllegalArgumentException("digits must be less than 15")
            }
            val num = shr(offset).toLong()
            val hi = 1L.shl(digits * 4)
            return hi.or(num.and(hi - 1)).toString(32).substring(1).uppercase()
        }
        
        return arrayOf(
            msb.digits(6),
            lsb.digits(6),
        ).joinToString("")
    }
    
    /**
     * 生成文件名
     */
    @JvmStatic
    internal fun generateFileName(prefix: String, suffix: String? = null): String {
        val name = listOf(
            prefix,
            timeCurMillisecond.timeFormatDate("yyMMdd_HHmmss"),
            randomStrGenerator(),
        ).joinToString("_")
        if (suffix.isNullOrEmpty()) {
            return name
        }
        return name.plus(".$suffix")
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
    
    /**
     * 从输入流复制到输出流
     */
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
    
    /**
     * 将文件输出到字节数组
     */
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
                    runOnApiUp(
                        Build.VERSION_CODES.P,
                        { FileUtils.copy(inputStream, outputStream) },
                        { copyInternalUserspace(inputStream, outputStream) },
                    )
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
    
    /**
     * 删除文件或目录下所有文件
     */
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
    
    private fun getName(filename: String?): String? {
        if (filename == null) {
            return null
        }
        val index = filename.lastIndexOf('/')
        return filename.substring(index + 1)
    }
    
    /**
     * 转换文件大小
     *
     * Convert file size to string
     *
     * @param fileS
     * @return
     */
    fun formatFileSize(fileS: Long): String {
        val df = DecimalFormat("#.00")
        val wrongSize = "0B"
        if (fileS <= 0L) {
            return wrongSize
        }
        return if (fileS < 1024) {
            df.format(fileS.toDouble()) + "B"
        } else if (fileS < 1048576) {
            // 小于1MB = 1024 * 1024
            df.format(fileS.toDouble() / 1024) + "KB"
        } else if (fileS < 1073741824) {
            // 小于1GB = 1024 * 1024 * 1024
            df.format(fileS.toDouble() / 1048576) + "MB"
        } else {
            df.format(fileS.toDouble() / 1073741824) + "GB"
        }
    }
    
    /**
     * mime
     */
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
        
        FILE("FILE", "*/*", listOf("")),
        
        ;
    }
    
    fun <R> bitmap(block: BitmapUtils.() -> R): Result<R> {
        return runCatchingSafety { BitmapUtils.get().let(block) }
    }
    
    /**
     * Bitmap相关工具
     */
    class BitmapUtils private constructor() {
        companion object {
            private const val THUMB_SIZE = 200
            
            internal fun get() = BitmapUtils()
        }
        
        /**
         * 将 bitmap 保存为文件
         */
        fun Bitmap?.save(
            dir: String?,
            isLasting: Boolean = false,
            outType: MimeType = MimeType.JPEG,
            quality: Int = 100,
        ): File? {
            if (null == this) {
                return null
            }
            if (dir.isNullOrEmpty()) {
                return null
            }
            val dirF = File(dir)
            if (!dirF.isDirectory || !dirF.exists()) {
                return null
            }
            val format = when (outType) {
                MimeType.PNG -> Bitmap.CompressFormat.PNG
                MimeType.WEBP -> {
                    @Suppress("DEPRECATION")
                    runOnApiUp(Build.VERSION_CODES.Q, {
                        if (quality < 100) {
                            Bitmap.CompressFormat.WEBP_LOSSY
                        } else {
                            Bitmap.CompressFormat.WEBP_LOSSLESS
                        }
                    }, {
                        Bitmap.CompressFormat.WEBP
                    }).getOrDefault(Bitmap.CompressFormat.WEBP)
                }
                else -> Bitmap.CompressFormat.JPEG
            }
            return runCatchingSafety {
                val bitmapF =
                    if (isLasting) AppBasicShare.context.createLastingFileByType(dir, outType)
                    else AppBasicShare.context.createCacheFileByType(dir, outType)
                bitmapF.outputStream().buffered().use {
                    compress(format, quality, it)
                    it.flush()
                }
                return bitmapF
            }.getOrNull()
        }
        
        /**
         * 图片文件转字节数组
         */
        fun File?.img2Bytes(
            quality: Int = 100,
            outW: Int = THUMB_SIZE,
            outH: Int = THUMB_SIZE,
        ): ByteArray? {
            if (null == this) {
                return null
            }
            return runCatchingSafety {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                options.inSampleSize = 1
                BitmapFactory.decodeFile(absolutePath, options)
                options.inSampleSize = computeSize(options, outW, outH)
                options.inJustDecodeBounds = false
                
                val bitmap = BitmapFactory.decodeFile(absolutePath, options)
                
                ByteArrayOutputStream().use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, it)
                    bitmap.recycle()
                    it.toByteArray()
                }
            }.getOrNull()
        }
        
        /**
         * bitmap 转字节数组
         */
        fun Bitmap?.bytes(
            quality: Int = 100,
            outW: Int = THUMB_SIZE,
            outH: Int = THUMB_SIZE,
        ): ByteArray? {
            if (null == this) {
                return null
            }
            return runCatchingSafety {
                val bytes = ByteArrayOutputStream().use {
                    compress(Bitmap.CompressFormat.JPEG, 100, it)
                    it.flush()
                    it.toByteArray()
                }
                val inputStream = ByteArrayInputStream(bytes)
                
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                options.inSampleSize = 1
                BitmapFactory.decodeStream(inputStream, null, options)
                options.inSampleSize = computeSize(options, outW, outH)
                options.inJustDecodeBounds = false
                
                val bitmap = BitmapFactory.decodeStream(inputStream, null, options) ?: return null
                inputStream.close()
                
                return@runCatchingSafety ByteArrayOutputStream().use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, it)
                    bitmap.recycle()
                    it.toByteArray()
                }
            }.getOrNull()
        }
        
        private fun computeSize(options: BitmapFactory.Options, outW: Int, outH: Int): Int {
            var inSampleSize = 1
            
            val srcWidth = options.outWidth
            val srcHeight = options.outHeight
            
            if (srcHeight > outH || srcWidth > outW) {
                val longSide = max(outW, outH)
                
                val heightRatio = (srcHeight.toFloat() / longSide.toFloat()).roundToInt()
                val widthRatio = (srcWidth.toFloat() / longSide.toFloat()).roundToInt()
                inSampleSize = max(heightRatio, widthRatio)
            }
            return inSampleSize
        }
        
    }
    
    fun <R> url(block: UriUtils.() -> R): Result<R> {
        return runCatchingSafety { UriUtils.get().let(block) }
    }
    
    @Suppress("MemberVisibilityCanBePrivate")
    class UriUtils private constructor() {
        companion object {
            internal fun get() = UriUtils()
        }
        
        /**
         * 获取文件路径（19以上会触发文件拷贝）
         */
        fun Uri?.getFilePath(): String? {
            return runOnApiUp(
                Build.VERSION_CODES.JELLY_BEAN_MR2,
                { getPathByCopyFile() },
                { getRealFilePath() }
            ).getOrNull()
        }
        
        fun Uri?.getRealFilePath(): String? {
            if (null == this) {
                return null
            }
            when (scheme) {
                ContentResolver.SCHEME_CONTENT -> {
                    AppBasicShare.context.contentResolver.query(this,
                        null,
                        null,
                        null,
                        null
                    )?.use {
                        if (it.moveToFirst()) {
                            @Suppress("DEPRECATION")
                            return it.get(it.getColumnIndex(MediaStore.MediaColumns.DATA), "")
                        }
                    }
                    return null
                }
                else -> return path
            }
        }
        
        private fun Uri?.getPathByCopyFile(): String? {
            if (null == this) {
                return null
            }
            val fileName = getFileName()
            val file = if (fileName.isNullOrEmpty()) {
                AppBasicShare.context.createCacheFileByType(DIRECTORY_DOCUMENTS, MimeType.FILE)
            } else {
                AppBasicShare.context.createCacheFile(DIRECTORY_DOCUMENTS, fileName)
            }
            val saveSuccess: Boolean = saveFileFromUri(file)
            if (!saveSuccess) {
                file.delete()
                return null
            }
            return file.absolutePath
        }
        
        fun Uri.getFileName(): String? {
            val mimeType = AppBasicShare.context.contentResolver.getType(this)
            if (mimeType == null) {
                return getName(toString())
            } else {
                AppBasicShare.context.contentResolver.query(
                    this,
                    null,
                    null,
                    null,
                    null,
                )?.use {
                    if (it.moveToFirst()) {
                        return it.get(it.getColumnIndex(OpenableColumns.DISPLAY_NAME), "")
                    }
                }
            }
            return null
        }
        
        private fun Uri?.saveFileFromUri(destinationFile: File): Boolean {
            if (null == this) {
                return false
            }
            
            runCatchingSafety {
                AppBasicShare.context.contentResolver.openInputStream(this)?.buffered()
                    ?.use { input ->
                        destinationFile.outputStream().buffered().use { output ->
                            copy(input, output)
                        }
                    }
            }.onFailure {
                return false
            }
            return true
        }
        
        /**
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        fun Uri?.isExternalStorageDocument(): Boolean {
            if (null == this) {
                return false
            }
            return "com.android.externalstorage.documents" == authority
        }
        
        /**
         * @return Whether the Uri authority is DownloadsProvider.
         */
        fun Uri?.isDownloadsDocument(): Boolean {
            if (null == this) {
                return false
            }
            return "com.android.providers.downloads.documents" == authority
        }
        
        /**
         * @return Whether the Uri authority is MediaProvider.
         */
        fun Uri?.isMediaDocument(): Boolean {
            if (null == this) {
                return false
            }
            return "com.android.providers.media.documents" == authority
        }
        
    }
}
