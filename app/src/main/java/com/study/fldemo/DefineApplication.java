package com.study.fldemo;

import android.app.Application;

import com.study.fldemo.utils.SpUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zouqianyu on 2017/10/1.
 */

public class DefineApplication extends Application {
    public static boolean loginState = false;
    public static SpUtils spUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        spUtils = SpUtils.getInstance(getApplicationContext());
    }

}
