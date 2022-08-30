package com.yh.appbasic.logger.owner

import com.yh.appbasic.logger.LogOwner

object AppLogger : LogOwner(logTag = { "APP" })
object LibLogger : LogOwner(logTag = { "APP" })