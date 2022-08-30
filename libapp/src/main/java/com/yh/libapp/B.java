package com.yh.libapp;

import com.yh.appbasic.init.AppBasicShare;
import com.yh.appbasic.logger.ILoggable;
import com.yh.appbasic.logger.Logs;

public class B implements ILoggable {

    static {
        LibApp libApp = AppBasicShare.get(LibApp.class);
        Logs.logD("B static: " + (null == libApp ? null : libApp.getAppContext()), libApp);
    }

    public B() {
        Logs.logD("B init", this);
    }
}
