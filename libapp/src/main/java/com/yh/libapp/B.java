package com.yh.libapp;

import com.yh.appbasic.initializer.AppBasicShare;
import com.yh.appbasic.logger.ILoggable;
import com.yh.appbasic.logger.Logs;

public class B implements ILoggable {

    static {
        Logs.logD("B static: " + AppBasicShare.getContext(), LibApp.INSTANCE);
    }

    public B() {
        Logs.logD("B init", this);
    }
}
