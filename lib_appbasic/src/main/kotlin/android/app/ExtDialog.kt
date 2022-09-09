package android.app

/**
 * 安全关闭 dialog
 */
fun Dialog?.close() {
    if(null == this) return
    try {
        if(isShowing) {
            dismiss()
        }
    } catch(e: Exception) {
    }
}

/**
 * 安全打开 dialog
 */
fun Dialog?.open() {
    if(null == this) return
    try {
        val ctx = context
        if(!isShowing) {
            if(ctx is Activity) {
                if(ctx.isValid) {
                    show()
                }
            } else {
                show()
            }
        }
    } catch(e: Exception) {
    }
}