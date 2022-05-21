@file:Suppress("unused") @file:JvmName("ExtILogOwner")

package com.yh.appbasic.logger.ext

import android.database.Cursor
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.yh.appbasic.logger.*

/**
 * 库日志输出工具
 *
 * 根据[com.yh.appinject.InjectHelper]区分
 *
 * Created by CYH on 2020/5/16 22:29
 */

/**
 * @see com.yh.appinject.logger.logD
 */
@JvmOverloads
fun ILogOwner.libD(@NonNull msg: Any?, @Nullable tag: String? = null, @Nullable vararg args: Any?) {
    logD(msg, tag, this, *args)
}

/**
 * @see com.yh.appinject.logger.logE
 */
@JvmOverloads
fun ILogOwner.libE(
    @NonNull msg: Any?,
    @Nullable tag: String? = null,
    @Nullable throwable: Throwable? = null,
    @Nullable vararg args: Any?
) {
    logE(msg, tag, this, throwable, *args)
}

/**
 * @see com.yh.appinject.logger.logI
 */
@JvmOverloads
fun ILogOwner.libI(@NonNull msg: Any?, @Nullable tag: String? = null, @Nullable vararg args: Any?) {
    logI(msg, tag, this, *args)
}

/**
 * @see com.yh.appinject.logger.logV
 */
@JvmOverloads
fun ILogOwner.libV(@NonNull msg: Any?, @Nullable tag: String? = null, @Nullable vararg args: Any?) {
    logV(msg, tag, this, *args)
}

/**
 * @see com.yh.appinject.logger.logW
 */
@JvmOverloads
fun ILogOwner.libW(@NonNull msg: Any?, @Nullable tag: String? = null, @Nullable vararg args: Any?) {
    logW(msg, tag, this, *args)
}

/**
 * @see com.yh.appinject.logger.logWTF
 */
@JvmOverloads
fun ILogOwner.libWTF(
    @NonNull msg: Any?,
    @Nullable tag: String? = null,
    @Nullable vararg args: Any?
) {
    logWTF(msg, tag, this, *args)
}

/**
 * @see com.yh.appinject.logger.logJSON
 */
@JvmOverloads
fun ILogOwner.libJSON(@Nullable json: Any?, @Nullable tag: String? = null) {
    logJSON(json, tag, this)
}

/**
 * @see com.yh.appinject.logger.logXML
 */
@JvmOverloads
fun ILogOwner.libXML(@Nullable xml: Any?, @Nullable tag: String? = null) {
    logXML(xml, tag, this)
}

/**
 * @see com.yh.appinject.logger.logCursor
 */
@JvmOverloads
fun ILogOwner.libCursor(
    @Nullable cursor: Cursor?,
    @NonNull justCurRow: Boolean = false,
    @Nullable tag: String? = null
) {
    logCursor(cursor, justCurRow, tag, this)
}

/**
 * @see com.yh.appinject.logger.logP
 */
@JvmOverloads
fun ILogOwner.libP(
    @NonNull priority: Int,
    @NonNull msg: Any?,
    @Nullable tag: String? = null,
    @Nullable throwable: Throwable? = null
) {
    logP(priority, msg, tag, this, throwable)
}
