package com.study.fldemo;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.study.fldemo.utils.SpUtils;


import androidx.multidex.MultiDex;

/**
 * Created by zouqianyu on 2017/10/1.
 */

public class DefineApplication extends Application {
    public static boolean loginState = false;
    public static SpUtils spUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        spUtils = SpUtils.getInstance(getApplicationContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}
