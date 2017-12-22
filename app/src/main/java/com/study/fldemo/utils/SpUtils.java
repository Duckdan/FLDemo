package com.study.fldemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.study.fldemo.bean.UserBean;

/**
 * Created by zouqianyu on 2017/10/2.
 */

public class SpUtils {
    private Context context;
    private static SpUtils instance;
    private final SharedPreferences.Editor editor;
    private final SharedPreferences sp;

    private SpUtils(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public static synchronized SpUtils getInstance(Context context) {
        if (instance == null) {
            instance = new SpUtils(context);
        }
        return instance;
    }

    public void saveUser2Sp(String name, String imageUrl) {
        if (editor != null) {
            editor.putString("name", name).commit();
            editor.putString("url", imageUrl).commit();
        }
    }

    public UserBean getUserFromSp() {
        UserBean userBean = new UserBean();
        if (sp != null) {
            String name = sp.getString("name", "技术杂谈");
            userBean.setName(name);
            String url = sp.getString("url", "");
            userBean.setUrl(url);
        }
        return userBean;
    }

    public void clearData() {
        if (editor != null) {
            editor.clear().commit();
        }
    }
}
