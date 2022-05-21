@file:JvmName("ExtAny")

package com.yh.appbasic.ext

/**
 * 提供该对象的(伪)内存地址，只能用作参考
 *
 * * 请不要通过该值来比较对象是否相同
 */
val Any?.memoryId get() = System.identityHashCode(this).toString(16)
