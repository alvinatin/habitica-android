package com.habitrpg.android.habitica.helpers;

import android.util.Log;

import com.habitrpg.android.habitica.BuildConfig;
import com.habitrpg.android.habitica.proxy.CrashlyticsProxy;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import cz.msebera.android.httpclient.HttpException;
import rx.functions.Action1;

public class RxErrorHandler {

    static private RxErrorHandler instance;
    private CrashlyticsProxy crashlyticsProxy;

    public static void init(CrashlyticsProxy crashlyticsProxy) {
        instance = new RxErrorHandler();
        instance.crashlyticsProxy = crashlyticsProxy;
    }

    public static Action1<Throwable> handleEmptyError() {
        //Can't be turned into a lambda, because it then doesn't work for some reason.
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                RxErrorHandler.reportError(throwable);
            }
        };
    }

    private static void reportError(Throwable throwable) {
        if (BuildConfig.DEBUG) {
            try {
                Log.e("ObservableError", Log.getStackTraceString(throwable));
            } catch (Exception ignored) {}
        } else {
            if (!IOException.class.isAssignableFrom(throwable.getClass()) && !HttpException.class.isAssignableFrom(throwable.getClass())) {
                instance.crashlyticsProxy.logException(throwable);
            }
        }
    }
}
