@file:Suppress("unused")
@file:JvmName("ExtKotlinReflect")

package com.kotlin

import java.lang.reflect.AccessibleObject
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

/**
 * 反射类对象通过指定构造函数创建类实例
 *
 * @param [parameterTypes] 构造函数参数类型列表
 * @param [params] 构造函数参数
 */
fun <T : Any> Class<T>.safeCreator(
    parameterTypes: List<Class<*>> = emptyList(),
    params: List<Any> = emptyList()
): T? {
    return getDeclaredConstructor(*parameterTypes.toTypedArray()).safeNew(params)
}

/**
 * 反射构造函数创建类实例
 *
 * @param [params] 构造函数参数
 */
fun <T : Any> Constructor<T>.safeNew(params: List<Any> = emptyList()): T? {
    return if(!params.isNullOrEmpty()) {
        safeAccess { it.newInstance(*params.toTypedArray()) }
    } else {
        safeAccess { it.newInstance() }
    }
}

/**
 * 反射类对象获取指定属性
 *
 * @param [fieldName] 属性名
 * @param [obj] 从该实例中获取指定属性的值
 */
inline fun <reified R : Any> Class<*>.safeFieldGet(
    fieldName: String,
    obj: Any? = null
): R? {
    return getDeclaredField(fieldName).safeGet(obj)
}

/**
 * 反射类对象获取指定属性
 *
 * @param [fieldName] 属性名
 * @param [fieldClazz] 实例类
 * @param [obj] 从该实例中获取指定属性的值
 */
fun <R : Any> Class<*>.safeFieldGet(
    fieldName: String,
    fieldClazz: Class<R>,
    obj: Any? = null
): R? {
    return getDeclaredField(fieldName).safeGet(fieldClazz, obj)
}

/**
 * 反射属性对象获取值
 *
 * @param [clazzR] 值类型
 * @param [obj] 从该实例中获取指定属性的值
 */
fun <R : Any> Field.safeGet(clazzR: Class<R>, obj: Any?): R? {
    return safeAccess {
        val result = it.get(obj)
        if(clazzR.isInstance(result)) {
            @Suppress("UNCHECKED_CAST")
            return@safeAccess result as R
        }
        return@safeAccess null
    }
}

/**
 * 反射属性对象获取值
 *
 * @param [obj] 从该实例中获取指定属性的值
 */
inline fun <reified R : Any> Field.safeGet(obj: Any?): R? {
    return this.safeAccess {
        val result = it.get(obj)
        if(result is R) {
            return@safeAccess result
        }
        return@safeAccess null
    }
}

/**
 * 反射类对象修改指定属性
 *
 * @param [fieldName] 属性名
 * @param [obj] 从该实例中获取指定属性的值
 * @param [value] 要设置的值
 */
fun Class<*>.safeFieldSet(fieldName: String, obj: Any? = null, value: Any?): Boolean {
    return getDeclaredField(fieldName).safeSet(obj, value)
}

/**
 * 反射属性对象修改值
 *
 * @param [obj] 从该实例中获取指定属性的值
 * @param [value] 要设置的值
 */
fun Field.safeSet(obj: Any? = null, value: Any?): Boolean {
    return this.safeAccess {
        it.set(obj, value)
        return@safeAccess true
    }
        ?: false
}

/**
 * 安全访问
 *
 * @param [block] 访问回调
 */
fun <T : AccessibleObject, R : Any> T.safeAccess(block: (T) -> R?): R? {
    var result: R? = null
    try {
        val originAccessible = isAccessible
        isAccessible = true
        result = block.invoke(this)
        isAccessible = originAccessible
    } catch(e: Exception) {
        e.printStackTrace()
    }
    return result
}

/**
 * 检查 by lazy 或 Lazy<*> 修饰的属性是否已经初始化
 */
fun <T> KProperty0<T>.isInitialized(): Boolean {
    return try {
        isAccessible = true
        (getDelegate() as? Lazy<*>)?.isInitialized()
            ?: true
    } catch (e: Exception) {
        true
    }
}

/**
 * 检查 lateinit 或 by lazy 或 Lazy<*> 修饰的属性是否已经初始化
 */
inline val KProperty0<*>.isLoaded: Boolean
    get() {
//        println("$name isLateinit:${isLateinit}")
        if (isLateinit) {
            return try {
                get()
                true
            } catch (e: Exception) {
                false
            }
        }
        val tmp = isAccessible
        isAccessible = true
        val delegate = getDelegate()
        isAccessible = tmp
        return when (delegate) {
            is Lazy<*> -> delegate.isInitialized()
            null -> true
            else -> throw IllegalArgumentException("only support Lazy type! current type: $delegate")
        }
    }

/**
 * 安全获取
 */
fun <T> T?.safeGet(defaultValue: (() -> T)?): T {
    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    return when (this) {
        is Any -> this
        is Boolean? -> this
            ?: defaultValue?.invoke()
            ?: false
        is String? -> this
            ?: defaultValue?.invoke()
            ?: ""
        is Int? -> this
            ?: defaultValue?.invoke()
            ?: 0
        is Short? -> this
            ?: defaultValue?.invoke()
            ?: (0).toShort()
        is Long? -> this
            ?: defaultValue?.invoke()
            ?: 0L
        is Double? -> this
            ?: defaultValue?.invoke()
            ?: 0.0
        is Float? -> this
            ?: defaultValue?.invoke()
            ?: 0F
        is CharSequence? -> this
            ?: defaultValue?.invoke()
            ?: ""
        else -> throw IllegalArgumentException("不支持的类型: $this")
    } as T
}

fun Boolean?.safeGet(): Boolean {
    return this.safeGet { false }
}

fun String?.safeGet(): String {
    return this.safeGet { "" }
}

fun Int?.safeGet(): Int {
    return this.safeGet { -1 }
}

fun Short?.safeGet(): Short {
    return this.safeGet { -1 }
}

fun Long?.safeGet(): Long {
    return this.safeGet { -1L }
}

fun Double?.safeGet(): Double {
    return this.safeGet { -1.0 }
}

fun Float?.safeGet(): Float {
    return this.safeGet { -1.0F }
}

fun CharSequence?.safeGet(): CharSequence {
    return this.safeGet { "" }
}
