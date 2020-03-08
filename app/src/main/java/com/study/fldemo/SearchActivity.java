package com.study.fldemo;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.study.fldemo.define.MyWebChromeClient;
import com.study.fldemo.event.DialogEvent;
import com.study.toastutils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private WebView wv;
    private ImageView ivBack;
    private final String FIRSTURL = "https://www.baidu.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        wv = (WebView) findViewById(R.id.wv);
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setUseWideViewPort(true); // 关键点
        settings.setAllowFileAccess(true); // 允许访问文件
        settings.setSupportZoom(true); // 支持缩放
        settings.setDomStorageEnabled(true);
//        WebChromeClient chromeClient = new WebChromeClient() {
//
//        };
        MyWebChromeClient chromeClient = new MyWebChromeClient();
        wv.setWebChromeClient(chromeClient);
        wv.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {


                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return false;
                }

                // Otherwise allow the OS to handle things like tel, mailto, etc.
//                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
//                startActivity(intent);
                ToastUtils.showToast(SearchActivity.this, "暂不支持下载文件");
                return true;
            }

            //当网页加载url发生变化的时候
            @Override
            public void onLoadResource(WebView view, String url) {
                Log.e("url:", url);
                if (FIRSTURL.equals(url)) {
                    mToolbar.setVisibility(View.VISIBLE);
                } else {
                    if (view.canGoBack()) {
                        mToolbar.setVisibility(View.GONE);
                    } else {
                        mToolbar.setVisibility(View.VISIBLE);
                    }
//                    view.loadUrl(url);
                }
//                ToastUtils.showToast(SearchActivity.this,url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("error:", "==" + description);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (FIRSTURL.equals(url)) {
                    EventBus.getDefault().post(new DialogEvent());
                }
            }


        });

        wv.loadUrl(FIRSTURL);

    }

    @Override
    public void onBackPressed() {
        if (wv.canGoBack()) {
            wv.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                super.onBackPressed();
                break;
        }
    }
}
