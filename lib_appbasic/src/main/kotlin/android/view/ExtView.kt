package android.view

import com.yh.appbasic.listener.ThrottleClickListener

/**
 * 设置点击事件
 *
 * @param [block] 回调
 * @param [period] 防抖，默认 500ms
 */
@JvmOverloads
fun View?.onClick(period: Long = 500, block: View.() -> Unit) {
    this?.also {
        if (period > 0) {
            setOnClickListener(ThrottleClickListener(period, block))
        } else {
            setOnClickListener(block)
        }
    }
}
