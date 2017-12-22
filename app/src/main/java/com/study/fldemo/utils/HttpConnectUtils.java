package com.study.fldemo.utils;

import com.study.fldemo.manager.HttpUrlManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络连接工具类
 */

public class HttpConnectUtils {
    private static HttpConnectUtils instance;
    private final HttpUrlManager manager;
    private static final String BASE_URL = "http://gank.io/api/";

    private HttpConnectUtils(String url) {
        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .build();

        manager = new Retrofit
                .Builder()
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(HttpUrlManager.class);
    }

    public static synchronized HttpConnectUtils getInstance(String url) {
        if (instance == null) {
            if ("".equals(url)) {
                instance = new HttpConnectUtils(BASE_URL);
            } else {
                instance = new HttpConnectUtils(url);
            }
        }
        return instance;
    }

    public HttpUrlManager getManager() {
        return manager;
    }
}
