package android.app

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/*
# FragmentTransaction的提交方法 (https://www.cnblogs.com/mengdd/p/5827045.html)

support library的FragmentTransaction现在提供了四种不同的方法来commit一个transaction:
  commit()
  commitAllowingStateLoss()
  commitNow()
  commitNowAllowingStateLoss()

## commit() vs commitAllowingStateLoss()

用commit()提交有时候会遇到IllegalStateException, 说你在onSaveInstanceState()之后提交,
这里有另一个文章很好地分析了这个问题:Fragment Transactions & Activity State Loss (http://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html)

commit()和commitAllowingStateLoss()在实现上唯一的不同就是当你调用commit()的时候, FragmentManger会检查是否已经存储了它自己的状态, 如果已经存了, 就抛出IllegalStateException.
那么如果你调用的是commitAllowingStateLoss(), 并且是在onSaveInstanceState()之后, 你可能会丢失掉什么状态呢?
答案是你可能会丢掉FragmentManager的状态, 即save之后任何被添加或被移除的Fragments.
举例说明:
  1.在Activity里显示一个FragmentA;
  2.然后Activity被后台, onStop()和onSaveInstanceState()被调用;
  3.在某个事件触发下, 你用FragmentB replace FragmentA , 使用的是 commitAllowingStateLoss().
这时候, 用户再返回应用, 可能会有两种情况发生:
  a.如果系统杀死了你的activity, 你的activity将会重建, 使用了上述步骤2保存的状态, 所以A会显示, B不会显示;
  b.如果系统没有杀死你的activity, 它会被提到前台, FragmentB就会显示出来, 到下次Activity stop的时候, 这个包含了B的状态就会被存下来.
(上述测试可以利用开发者选项中的”Don’t Keep Activities”选项).
那么你要选择哪一种呢? 这就取决于你提交的是什么, 还有你是否能接受丢失.

---------------------------------------------------------------------------------------------------------------------------------------------------------------

## commit(), commitNow() 和 executePendingTransactions()

使用commit()的时候, 一旦调用, 这个commit并不是立即执行的, 它会被发送到主线程的任务队列当中去, 当主线程准备好执行它的时候执行.
popBackStack()的工作也是这样, 发送到主线程任务队列中去. 也即说它们都是异步的.

但是有时候你希望你的操作是立即执行的, 之前的开发者会在commit()调用之后加上 executePendingTransactions()来保证立即执行, 即变异步为同步.
support library从v24.0.0开始提供了 commitNow()方法, 之前用executePendingTransactions()会将所有pending在队列中还有你新提交的transactions都执行了,
而commitNow()将只会执行你当前要提交的transaction. 所以commitNow()避免你会不小心执行了那些你可能并不想执行的transactions.

但是你不能对要加在back stack中的transaction使用commitNow(), 即addToBackStack()和commitNow()不能同时使用.
为什么呢?
想想一下, 如果你有一个提交使用了commit(), 紧接着又有另一个提交使用了commitNow(), 两个都想加入back stack,
那back stack会变成什么样呢? 到底是哪个transaction在上, 哪个在下? 答案将是一种不确定的状态, 因为系统并没有提供任何保证来确保顺序, 所以系统决定干脆不支持这个操作.

前面提过popBackStack()是异步的, 所以它同样也有一个同步的兄弟popBackStackImmediate().

所以实际应用的时候怎么选择呢?

如果你需要同步的操作, 并且你不需要加到back stack里, 使用commitNow().
support library在FragmentPagerAdapter里就使用了commitNow()来保证在更新结束的时候, 正确的页面被加上或移除.
如果你操作很多transactions, 并且不需要同步, 或者你需要把transactions加在back stack里, 那就使用commit().
如果你希望在某一个指定的点, 确保所有的transactions都被执行, 那么使用executePendingTransactions().
*/

fun DialogFragment?.close() {
    if (null == this) return
    try {
        if (isResumed || true == dialog?.isShowing) {
            dismiss()
        }
    } catch (e: Exception) {
    }
}

fun DialogFragment?.open(fa: FragmentActivity?, tag: String? = null) {
    if (null == this) return
    if (null == fa || fa.isInvalid) return
    open(fa.supportFragmentManager, tag)
}

fun DialogFragment?.open(fm: FragmentManager?, tag: String? = null) {
    if (null == this) return
    if (null == fm) return
    try {
        if (!isAdded || !isResumed || false == dialog?.isShowing) {
            showNow(fm, tag)
        }
    } catch (e: Exception) {
    }
}

fun Dialog?.close() {
    if (null == this) return
    try {
        if (isShowing) {
            dismiss()
        }
    } catch (e: Exception) {
    }
}

fun Dialog?.open() {
    if (null == this) return
    try {
        val ctx = context
        if (!isShowing) {
            if (ctx is Activity) {
                if (ctx.isValid) {
                    show()
                }
            } else {
                show()
            }
        }
    } catch (e: Exception) {
    }
}