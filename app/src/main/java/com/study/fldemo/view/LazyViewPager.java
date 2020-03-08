package com.study.fldemo.view;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;

/**
 * Created by zouqianyu on 2017/9/16.
 */

public class LazyViewPager extends ViewPager {
    private static final int DEFAULT_OFFSCREEN_PAGES = 0;

    public LazyViewPager(Context context) {
        super(context);
    }

    public LazyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
