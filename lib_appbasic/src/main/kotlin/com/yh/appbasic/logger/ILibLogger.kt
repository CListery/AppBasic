package com.yh.appbasic.logger

interface ILibLogger : ILoggable {
    
    val logger: LogOwner get() = LogOwner { this::class.simpleName!! }
    
}