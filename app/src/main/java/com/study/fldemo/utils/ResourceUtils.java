package com.study.fldemo.utils;

import android.content.Context;
import android.content.res.Resources;

import com.study.fldemo.DefineApplication;

import androidx.annotation.AnimRes;
import androidx.annotation.ArrayRes;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.annotation.StyleableRes;


public class ResourceUtils {


    @DrawableRes
    public static int getDrawableId(Context context, String name) {
        Resources res = context.getResources();
        return res.getIdentifier(name, "drawable", context.getPackageName());
    }

    @LayoutRes
    public static int getLayoutId(Context context, String name) {
        Resources res = context.getResources();
        return res.getIdentifier(name, "layout", context.getPackageName());
    }

    @StringRes
    public static int getStringsId(Context context, String name) {
        Resources res = context.getResources();
        return res.getIdentifier(name, "string", context.getPackageName());
    }

    public static int getMipmapsId(Context context, String name) {
        Resources res = context.getResources();
        return res.getIdentifier(name, "mipmap", context.getPackageName());
    }


    @ColorRes
    public static int getColorId(Context context, String name) {
        Resources res = context.getResources();
        return res.getIdentifier(name, "color", context.getPackageName());
    }

    @ColorInt
    public static int getColor(int colorRes) {
        return DefineApplication.Companion.getContext().getResources().getColor(colorRes);
    }

    @RawRes
    public static int getRawId(Context context, String name) {
        Resources res = context.getResources();
        return res.getIdentifier(name, "raw", context.getPackageName());
    }

    @AnimRes
    public static int getAnimId(Context context, String name) {
        Resources res = context.getResources();
        return res.getIdentifier(name, "anim", context.getPackageName());
    }

    @StyleRes
    public static int getStyleId(Context context, String name) {
        Resources res = context.getResources();
        return res.getIdentifier(name, "style", context.getPackageName());
    }

    @StyleableRes
    public static int getStyleableId(Context context, String name) {
        Resources res = context.getResources();
        return res.getIdentifier(name, "styleable", context.getPackageName());
    }

    @AttrRes
    public static int getAttrId(Context context, String name) {
        Resources res = context.getResources();
        return res.getIdentifier(name, "attr", context.getPackageName());
    }

    @ArrayRes
    public static int getArrayId(Context context, String name) {
        Resources res = context.getResources();
        return res.getIdentifier(name, "array", context.getPackageName());
    }

    public static String getString(int stringRes) {
        String string = DefineApplication.Companion.getContext().getString(stringRes);
        return string;
    }

    public static String getString(int StringRes, Object... objects) {
        String s = DefineApplication.Companion.getContext().getString(StringRes, objects);
        return s;
    }

    public static float getDimen(@DimenRes int dimenRes) {
        return DefineApplication.Companion.getContext().getResources().getDimensionPixelSize(dimenRes);
    }


}
