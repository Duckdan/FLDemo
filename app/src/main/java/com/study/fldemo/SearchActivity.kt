package com.study.fldemo

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView

import com.study.fldemo.define.MyWebChromeClient
import com.study.fldemo.event.DialogEvent
import com.study.toastutils.ToastUtils

import org.greenrobot.eventbus.EventBus

import butterknife.BindView
import butterknife.ButterKnife
import com.study.fldemo.utils.StatusBarUtils
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.iv_back
import kotlinx.android.synthetic.main.activity_search.toolbar
import kotlinx.android.synthetic.main.activity_search.wv

class SearchActivity : AppCompatActivity() {


    private val FIRSTURL = "https://www.baidu.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.from(this)
                //设置透明的状态
                .setTransparentStatusbar(true)
                .setActionbarView(mAppbar)
                //适配23版本以上的
                .setLightStatusBar(false)
                //必须设置为true,否则状态栏将会变成白色
                .setActionBarOtherColor(true).process()
        setContentView(R.layout.activity_search)

        iv_back.setOnClickListener { super.onBackPressed() }

        val settings = wv.settings
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        settings.useWideViewPort = true // 关键点
        settings.allowFileAccess = true // 允许访问文件
        settings.setSupportZoom(true) // 支持缩放
        settings.domStorageEnabled = true

        val chromeClient = MyWebChromeClient()
        wv.webChromeClient = chromeClient
        wv.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {


                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return false
                }


                ToastUtils.showToast(this@SearchActivity, "暂不支持下载文件")
                return true
            }

            //当网页加载url发生变化的时候
            override fun onLoadResource(view: WebView, url: String) {
                Log.e("url:", url)
                if (FIRSTURL == url) {
                    toolbar.visibility = View.VISIBLE
                } else {
                    if (view.canGoBack()) {
                        toolbar.visibility = View.GONE
                    } else {
                        toolbar.visibility = View.VISIBLE
                    }
                }
            }

            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                super.onReceivedError(view, errorCode, description, failingUrl)
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (FIRSTURL == url) {
                    EventBus.getDefault().post(DialogEvent())
                }
            }


        }

        wv!!.loadUrl(FIRSTURL)

    }

    override fun onBackPressed() {
        if (wv.canGoBack()) {
            wv.goBack()
        } else {
            super.onBackPressed()
        }
    }


}
