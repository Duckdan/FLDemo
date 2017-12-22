package com.study.fldemo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.study.fldemo.bean.DataBean;
import com.study.fldemo.utils.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.mAppbar)
    AppBarLayout mAppbar;
    @BindView(R.id.sdv)
    SimpleDraweeView sdv;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.wv)
    WebView wv;
    @BindView(R.id.ctl)
    CollapsingToolbarLayout ctl;
    private DataBean dataBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
            int padding = DensityUtil.px2dip(this, 25);
            ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
            layoutParams.height = layoutParams.height + padding;
            toolbar.setLayoutParams(layoutParams);
            toolbar.setPadding(0,padding,0,0);

            mAppbar.setFitsSystemWindows(false);
            rl.setFitsSystemWindows(false);
            sdv.setFitsSystemWindows(false);
        }
        Intent intent = getIntent();
        dataBean = (DataBean) intent.getParcelableExtra("data");

        initData();
    }

    private void initData() {
        sdv.setImageURI(dataBean.getPictureUrl());
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        wv.setWebChromeClient(new WebChromeClient());
        wv.setWebViewClient(new WebViewClient());
        wv.loadUrl(dataBean.getWebUrl());
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        onBackPressed();
    }
}
