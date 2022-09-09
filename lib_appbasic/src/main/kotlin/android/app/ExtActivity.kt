package android.app

import android.graphics.Rect
import android.os.Looper
import android.os.isCurrentLooper
import android.view.View
import android.view.onClick
import androidx.annotation.IdRes
import com.yh.appbasic.logger.logD
import com.yh.appbasic.logger.logE
import com.yh.appbasic.logger.owner.LibLogger
import kotlin.concurrent.thread

/**
 * 检查activity是否有效
 */
val Activity?.isValid get() = !(null == this || isDestroyed || isFinishing)

/**
 * @see [isValid]
 */
val Activity?.isInvalid get() = !isValid

/**
 * 检查键盘是否弹出
 */
val Activity.checkSoftInputVisibility
    get(): Boolean {
        val decorView = window?.decorView
        var needHideSoftInput = false
        if (null != decorView) {
            val screenH = decorView.height
            val rect = Rect()
            decorView.getWindowVisibleDisplayFrame(rect)
            logD(
                msg = "checkSoftInputVisibility h:${screenH * 3 / 4} - b:${rect.bottom}",
                tag = "ExtActivity",
                loggable = LibLogger
            )
            needHideSoftInput = screenH / 4F * 3 > rect.bottom
        }
        return needHideSoftInput
    }

/**
 * 执行 block 并在执行过程中显示 Loading
 *
 * @param [printCatching] 是否在日志中输出异常信息
 * @param [onCreateLoading] Loading Dialog
 * @param [block] 被执行的 block
 * @param [callback] 回调
 */
fun <R> Activity.runWithLoading(
    printCatching: Boolean = true,
    onCreateLoading: Activity.() -> Dialog,
    block: () -> R,
    callback: ((Result<R>) -> Unit)? = null,
) {
    fun Dialog?.operator(isOpen: Boolean) {
        this?.also {
            runOnUiThread {
                if (isOpen) it.open() else it.close()
            }
        }
    }
    
    val waitingDialog = if (isInvalid || !Looper.getMainLooper().isCurrentLooper) {
        null
    } else {
        onCreateLoading()
    }
    val runnable = Runnable {
        waitingDialog.operator(true)
        val r = runCatching(block)
        if (r.isFailure && printCatching) {
            logE("invoke block failed!", throwable = r.exceptionOrNull())
        }
        runOnUiThread { callback?.invoke(r) }
        waitingDialog.operator(false)
    }
    if (Looper.getMainLooper().isCurrentLooper) {
        thread { runnable.run() }
    } else {
        runnable.run()
    }
}

/**
 * 执行 AsyncBlock 并在执行过程中显示 Loading
 *
 * @param [onCreateLoading] Loading Dialog
 * @param [asyncBlock] 被执行的 async block
 * @param [onSuccess] 回调
 * @param [onFailure] 回调
 */
fun <R> Activity?.runWithLoadingAsync(
    onCreateLoading: Activity.() -> Dialog,
    asyncBlock: (success: (R) -> Unit, failure: (Throwable?) -> Unit) -> Unit,
    onSuccess: (R) -> Unit,
    onFailure: ((Throwable?) -> Unit)? = null,
) {
    fun Dialog?.operator(isOpen: Boolean) {
        this?.also { if (isOpen) it.open() else it.close() }
    }
    
    val waitingDialog = if (null == this || isInvalid) {
        null
    } else {
        onCreateLoading()
    }
    waitingDialog.operator(true)
    kotlin.runCatching {
        asyncBlock.invoke(
            {
                waitingDialog.operator(false)
                onSuccess.invoke(it)
            },
            {
                waitingDialog.operator(false)
                onFailure?.invoke(it)
            }
        )
    }.onFailure {
        waitingDialog.operator(false)
        onFailure?.invoke(it)
    }
}

/**
 * 根据视图ID获取View(Lazy)
 */
fun <T : View> Activity.getView(
    @IdRes
    id: Int,
): Lazy<T?> = lazy { findViewById(id) }

/**
 * 根据视图ID配置View点击响应(Lazy)
 *
 * @param [callback] 回调
 * @param [period] 防抖，默认 500ms
 */
@JvmOverloads
fun Activity.onClickById(@IdRes id: Int, period: Long = 500, callback: View.() -> Unit) {
    findViewById<View>(id)?.onClick(period, callback)
}
