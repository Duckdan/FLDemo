package com.study.fldemo

import android.app.Application
import android.content.Context

import com.study.fldemo.utils.SpUtils


import androidx.multidex.MultiDex

/**
 *
 */

class DefineApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        spUtils = SpUtils.getInstance(applicationContext)
        defineApplicationContext = applicationContext
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        var loginState = false
        lateinit var spUtils: SpUtils
        lateinit var defineApplicationContext: Context

        fun getContext(): Context {
            return defineApplicationContext
        }
    }


}
