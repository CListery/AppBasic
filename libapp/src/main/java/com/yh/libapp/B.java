package com.yh.libapp;

import androidx.annotation.NonNull;

import com.yh.appbasic.initializer.AppBasicShare;
import com.yh.appbasic.logger.ILogger;
import com.yh.appbasic.logger.LogOwner;
import com.yh.appbasic.logger.Logs;

public class B implements ILogger {

    static {
        Logs.logD("B static: " + AppBasicShare.getContext(), LibApp.INSTANCE);
    }

    public B() {
        Logs.logD("B init", this);
    }

    @Override
    public void onCreateLogOwner(@NonNull LogOwner logOwner) {

    }
}
