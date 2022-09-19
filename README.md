[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.clistery/appbasic/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.clistery/appbasic)

# AppBasic

基本框架，提供一些功能扩展和实用工具

## Use

```gradle
implementation("io.github.clistery:appbasic:2.3.0")
```

## 全局共享

- AppBasicShare
  - install - 在 Application 中调用该函数实现共享 Context
  - pid - 当前主进程id
  - application - Application
  - context - Context
  - runOnUiThread - 添加一个Runnable到主线程
  - removeRunnable - 从主线程移除一个Runnable

## 日志功能

- ILogger `实现该接口则会自动创建一个 LogOwner`
  
  - kotlin
  
  ```kotlin
  class A : ILogger{
      constructor() {
          logD("A init", this)
      }
      override fun onCreateLogOwner(logOwner: LogOwner) {
          // 可以在此做一些自定义配置
          logOwner.onCreateFormatStrategy {
              TheLogFormatStrategy.newBuilder("AAA")
                  .setShowThreadInfo(false)
                  .setMethodCount(0)
                  .build()
          }
      }
  }
  ```
  
  - java

  ```java
  public class B implements ILogger {
      public B() {
          Logs.logD("B init", this);
      }

      @Override
      public void onCreateLogOwner(@NonNull LogOwner logOwner) {
      }
  }
  ```

- LogsManager
  - diskLogKeepDay - 日志文件保留的最大时间，单位天
  - cleanup - 按规则清理日志文件

- 默认配置
  - AppLogger - 默认的 APP Logger ，所有直接调用 logD\logW\logE... 不指定 loggable 的方式，都会默认使用该 LogOwner
  - LibLogger - 默认的 Library LogOwner ，一般用于库中打印日志，使用时需要指定
- 使用文件输出日志

  ```kotlin
  val logFormatStrategy = DiskLogFormatStrategy.Builder(this, "app").build()
  AppLogger.onCreateFormatStrategy { logFormatStrategy }
  ```

- 修改默认配置
  - kotlin

  ```kotlin
  AppLogger.onCreateFormatStrategy {
      TheLogFormatStrategy
          .newBuilder("APP")
          .setMethodCount(5)
          .setStackFilter(B::class)
          .build()
  }.on()
  ```

  - java

  ```java
  AppLogger.INSTANCE.onCreateFormatStrategy(tag -> {
      TheLogFormatStrategy.newBuilder("APP")
              .setMethodCount(5)
              .setStackFilter(B.class)
              .build();
      return null;
  });
  ```

- 日志输出
  - kotlin
  
  ```kotlin
  logW("A static: ${libApp?.appContext}", libApp) // 指定loggable
  logD("A init") // 使用默认的 AppLogger
  logD("A init", this) // 使用当前类名作为 logtag
  ```

  - java
  
  ```java
  Logs.logD("B static: " + (null == libApp ? null : libApp.getAppContext()), libApp); // 指定loggable
  Logs.logD("B init"); // 使用默认的 AppLogger
  Logs.logD("B init", this); // 使用当前类名作为 logtag
  ```

- 更多关于日志打印功能请参阅
  - com/yh/appinject/logger

## 一些组件

- SafeJobIntentService
  - 该组件重写了 JobIntentService 并捕捉了以下异常

    ```log
    java.lang.SecurityException: Caller no longer running, last stopped +2s5ms because: timed out while starting
    ```

- ViewBindingActivity
  - 该类是一个抽象的 ViewBinding's Activity，使用时需要传入你的 ViewBinding 泛型
    - beforeOnCreate - 在 super.onCreate() 被调用前的函数，可以重写实现你自己的业务代码
    - binderCreator - 创建 ViewBinding 的抽象方法，由子类实现
    - preInit - 在 VB.onInit() 被调用前的函数，可以重写实现你自己的业务代码
    - VB.onInit - 初始化函数，在这里你可以对视图进行一些初始化操作，比如设置视图大小、颜色、状态等
    - changeBinder - 操作视图改变的函数，可以在子线程直接调用该函数
- ViewBindingFragment - 与 ViewBindingActivity 类似

## 扩展功能

- ExtActivity.kt
  - Activity?.isValid - 检查activity是否有效
  - Activity?.isInvalid - 检查activity是否无效
  - Activity.checkSoftInputVisibility - 检查键盘是否弹出
  - Activity.runWithLoading - 执行 block 并在执行过程中显示 Loading
  - Activity?.runWithLoadingAsync - 执行 AsyncBlock 并在执行过程中显示 Loading
  - Activity.getView - 根据视图ID获取View(Lazy)
  - Activity.onClickById - 根据视图ID配置View点击响应(Lazy)
- ExtDialog.kt
  - Dialog?.close - 安全关闭 dialog
  - Dialog?.open - 安全打开 dialog
- ExtContext.kt
  - Context.isMainProcess - 检查当前是否处于主进程
  - Context?.killAllOtherProcess - 杀死除当前进程之外的APP进程
  - Context?.killProcessExceptMain - 杀死除主进程之外的APP进程
  - Context?.listenScreenOff - 监听锁屏（部分手机可能无效）
- ExtLooper.kt
  - Looper.isCurrentLooper - 检查调用线程是否在当前线程
- ExtMotionEvent.kt
  - MotionEvent?.toShortString - 将触摸数据转为字符串(日志)
- ExtView.kt
  - View?.onClick - 设置点击事件
- ExtAny.kt
  - Any?.memoryId - 提供该对象的(伪)内存地址，只能用作参考
  - getAppendStr - 将对象数组转为字符串并排列为 "(字符串1): (字符串2)(字符串3)(字符串4)..."
    - 自动过滤空对象
  - T?.runCatchingSafety - 安全执行 block，并返回执行结果
  - runCatchingSafety - 安全执行 block，并返回执行结果
- ExtCharSequence.kt
  - 提供了一些字符串相关的扩展方法
- ExtKotlinReflect.kt
  - 提供了一些基于反射的扩展用法(所有方法都是安全的，不会抛出异常，最坏的情况就是操作不成功或返回null)

    ```kotlin
    val userClazz = Class.forName("xxx.User")
    // 通过匹配的构造器创建 User 对象
    val user = userClazz.safeCreator(String::class.java, "clistery")
    // 获取 User 对象的 name 字段的值
    userClazz.safeFieldGet("name", user)
    // 设置 User 对象的 email 字段的值
    userClazz.safeFieldSet("email", user, "cai1083088795@gmail.com")
    // 更多方法请参阅 API
    ......
    ```

- ExtNumber.kt
  - 提供 Number 相关的扩展
- ExtTime.kt
  - 提供时间相关的扩展方法

## 文件工具

- FileUtils

## SharedPreferences

- PreferencesUtils.kt
