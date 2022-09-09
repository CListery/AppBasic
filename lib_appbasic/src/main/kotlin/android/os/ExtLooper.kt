@file:JvmName("ExtLooper")

package android.os

/**
 * 检查调用线程是否在当前线程
 */
val Looper.isCurrentLooper
    get(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isCurrentThread
        } else {
            Thread.currentThread() == thread
        }
    }