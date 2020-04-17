package com.study.fldemo

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Parcelable

import com.bumptech.glide.Glide


import androidx.appcompat.app.AppCompatActivity

import android.view.Gravity
import android.view.View

import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.study.fldemo.dao.DaoManager
import com.study.fldemo.dao.DatabaseBean
import com.study.fldemo.share.ShareSDKUtils
import com.study.toastutils.ToastUtils

import butterknife.ButterKnife
import cn.sharesdk.framework.Platform
import com.study.fldemo.utils.StatusBarUtils
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.sdv

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    private var dataBean: DatabaseBean? = null
    private var daoManager: DaoManager? = null
    private var databaseBean: DatabaseBean? = null
    private var width: Int = 0
    private lateinit var tvToast: TextView
    private lateinit var toast: Toast
    private lateinit var shareDialog: Dialog

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
        setContentView(R.layout.activity_detail)
        ButterKnife.bind(this)

        iv_back.setOnClickListener(this)
        iv_share.setOnClickListener(this)
        iv_collect.setOnClickListener(this)
        daoManager = DaoManager.getInstance(applicationContext)

        val metrics = resources.displayMetrics
        width = metrics.widthPixels

        val intent = intent
        dataBean = intent.getParcelableExtra<Parcelable>("data") as DatabaseBean
        initData()


    }

    private fun initData() {
        val isCollected = checkIsCollected()
        iv_collect.setImageResource(if (isCollected) R.drawable.icon_collect else R.drawable.icon_stroke_collect)
        Glide
                .with(applicationContext)
                .load(dataBean?.imageUrl)
                .placeholder(R.drawable.header1)
                .into(sdv)
        val settings = wv.settings
        settings.javaScriptEnabled = true
        settings.setSupportZoom(true)
        wv.webChromeClient = WebChromeClient()
        wv.webViewClient = WebViewClient()
        wv.loadUrl(dataBean?.url)
    }

    /**
     * 检测当前文章是否被收藏
     */
    private fun checkIsCollected(): Boolean {
        databaseBean = daoManager?.queryDatabaseBeanById(dataBean?._id)
        return databaseBean != null
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.iv_back -> onBackPressed()
            R.id.iv_share -> if (DefineApplication.loginState) {
                showShareDialog()
            } else {
                ToastUtils.showToast(this, "登录后才可以分享")
            }
            R.id.iv_collect -> {
                val isCollected = checkIsCollected()
                if (isCollected) {
                    //TODO 取消收藏
                    showToast(this, "取消收藏")
                    daoManager!!.deleteDatabaseBean(databaseBean)
                } else {
                    //TODO 已收藏
                    showToast(this, "已收藏")
                    daoManager!!.insertDatabaseBean(dataBean)
                }
                iv_collect.setImageResource(if (isCollected) R.drawable.icon_stroke_collect else R.drawable.icon_collect)
            }
        }
    }

    private fun showShareDialog() {
        if (shareDialog == null) {
            shareDialog = Dialog(this, R.style.defineDialogStyle)
            shareDialog.setContentView(R.layout.share_dialog_layout)
            val window = shareDialog.window
            window!!.setGravity(Gravity.BOTTOM)
            val lp = window.attributes
            lp.width = resources.displayMetrics.widthPixels
            window.attributes = lp
            shareDialog.findViewById<View>(R.id.ll_qq).setOnClickListener {
                ShareSDKUtils.shareQQ(dataBean!!.who + "", dataBean!!.desc + "", dataBean!!.imageUrl, dataBean!!.url, Platform.SHARE_WEBPAGE)
                shareDialog.dismiss()
            }

            shareDialog.findViewById<View>(R.id.ll_wx).setOnClickListener {
                ShareSDKUtils.shareWX(dataBean!!.who + "", dataBean!!.desc + "", dataBean!!.imageUrl, dataBean!!.url, Platform.SHARE_WEBPAGE)

                shareDialog.dismiss()
            }
        }
        shareDialog.show()
    }

    fun showToast(context: Context, message: String) {
        if (toast == null) {
            toast = Toast(context)
            val view = View.inflate(context, R.layout.toast_layout, null)
            val llBg = view.findViewById<View>(R.id.ll_bg) as LinearLayout
            tvToast = view.findViewById<View>(R.id.tv_toast) as TextView
            val layoutParams = FrameLayout.LayoutParams((width * 0.3).toInt(), (width * 0.3).toInt())
            llBg.layoutParams = layoutParams
            toast.view = view
            toast.duration = Toast.LENGTH_SHORT
            toast.setGravity(Gravity.CENTER, 0, 0)
        }
        tvToast.text = message
        toast.show()
    }
}
