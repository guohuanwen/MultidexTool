package com.bcgtgjyb.multidextool;

import android.app.Application;
import android.content.Context;

import com.bcgtgjyb.multidextool.tool.DexLoadUtil;

/**
 * Created by bigwen on 16/10/11.
 */

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        DexLoadUtil.attachBaseContext(base, android.os.Process.myPid());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (DexLoadUtil.isDexLoadProcess(this, android.os.Process.myPid())) return;
        //next your code

    }
}
