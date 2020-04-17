package com.study.fldemo

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity

import android.text.format.DateFormat
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.study.fldemo.bean.FuLiBean
import com.study.fldemo.share.ShareSDKUtils
import com.study.fldemo.utils.HttpConnectUtils
import com.study.toastutils.ToastUtils

import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

import cn.sharesdk.framework.Platform
import cn.sharesdk.onekeyshare.OnekeyShare
import com.study.fldemo.utils.StatusBarUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_big_image.*
import okhttp3.ResponseBody

/**
 * Created by Administrator on 2017/8/9.
 */

class BigImageActivity : AppCompatActivity(), View.OnClickListener {

    private var fuLiBean: FuLiBean? = null

    private var httpConnectUtils: HttpConnectUtils? = null
    var picurl: String = "http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg"
    private var shareDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.from(this)
                //设置透明的状态
                .setTransparentStatusbar(true)
                .setActionbarView(rl_title)
                //适配23版本以上的
                .setLightStatusBar(false)
                //必须设置为true,否则状态栏将会变成白色
                .setActionBarOtherColor(true).process()
        setContentView(R.layout.activity_big_image)


        iv_share.setOnClickListener(this)
        iv_download.setOnClickListener(this)
        iv_back.setOnClickListener(this)
        val intent = intent
        fuLiBean = intent.getParcelableExtra("big") as FuLiBean
        picurl = fuLiBean?.url ?: ""
        Glide.with(applicationContext).load(fuLiBean!!.url).into(sdv)
        httpConnectUtils = HttpConnectUtils.getInstance("")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> onBackPressed()
            R.id.iv_share -> if (DefineApplication.loginState) {
                showShareDialog()
            } else {
                ToastUtils.showToast(this, "登录后才可以分享")
            }
            R.id.iv_download -> downloadImage(fuLiBean!!.url)
        }
    }

    private fun showShareDialog() {
        if (shareDialog == null) {
            shareDialog = Dialog(this, R.style.defineDialogStyle)
            shareDialog!!.setContentView(R.layout.share_dialog_layout)
            val window = shareDialog!!.window
            window!!.setGravity(Gravity.BOTTOM)
            val lp = window.attributes
            lp.width = resources.displayMetrics.widthPixels
            window.attributes = lp
            shareDialog!!.findViewById<View>(R.id.ll_qq).setOnClickListener {
                ShareSDKUtils.shareQQ(fuLiBean!!.who!! + "", fuLiBean!!.desc!! + "", picurl, picurl, Platform.SHARE_IMAGE)
                shareDialog!!.dismiss()
            }

            shareDialog!!.findViewById<View>(R.id.ll_wx).setOnClickListener {
                ShareSDKUtils.shareWX(fuLiBean!!.who!! + "", fuLiBean!!.desc!! + "", picurl, picurl, Platform.SHARE_IMAGE)
                shareDialog!!.dismiss()
            }
        }
        shareDialog!!.show()
    }

    private fun downloadImage(url: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ToastUtils.showToast(this, "请授于应用的读写的权限！")
                    // 帮跳转到该应用的设置界面，让用户手动授权
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } else {
                    // 不需要解释为何需要该权限，直接请求授权
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                }
            } else {
                download(url)
            }
        } else {
            download(url)
        }

    }

    private fun download(url: String?) {
        httpConnectUtils!!
                .manager
                .getImageFile(url)
                .subscribeOn(Schedulers.io())
                .map(Function<ResponseBody, Boolean> { body -> saveToDisk(body) })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Boolean> {
                    override fun onSubscribe(@NonNull d: Disposable) {

                    }

                    override fun onNext(@NonNull aBoolean: Boolean) {
                        Toast.makeText(this@BigImageActivity, "下载成功", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(@NonNull e: Throwable) {

                    }

                    override fun onComplete() {

                    }
                })
    }

    private fun saveToDisk(body: ResponseBody): Boolean {
        var flag = false
        val inputStream = body.byteStream()
        val filePath = Environment.getExternalStorageDirectory().path
        val format = DateFormat.format("yyyy-dd-MM HH:mm:ss", System.currentTimeMillis()).toString()
        val fileName = format + " " + fuLiBean!!.who + fuLiBean!!.url!!.substring(fuLiBean!!.url!!.lastIndexOf("."), fuLiBean!!.url!!.length)
        val fileDir = File(filePath, "fuli")
        if (!fileDir.exists()) {
            fileDir.mkdir()
        }

        val fileChild = File(fileDir, fileName)
        var len = 0
        try {
            val fos = FileOutputStream(fileChild)
            val bis = BufferedInputStream(inputStream)
            val buffer = ByteArray(2048)
            len = bis.read(buffer)
            while (len != -1) {
                len = bis.read(buffer)
                fos.write(buffer, 0, len)
            }
            fos.flush()
            bis.close()
            fos.close()
            inputStream.close()
            flag = true
        } catch (e: Exception) {
            flag = false
            e.printStackTrace()
        }

        return flag
    }

    /**
     * @param title  标题
     * @param text   内容
     * @param picurl 图片链接 *
     * QQ和QQ空间设置分享链接使用setTitleUrl();
     * 设置标题：setTitle
     * 设置内容：setText
     * 设置网络图片：oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
     * 设置本地图片： //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片 *
     * 微信 * url仅在微信（包括好友和朋友圈）中使用 * oks.setUrl("http://qq.com");
     */
    private fun showShare(title: String, text: String, picurl: String?) {
        val oks = OnekeyShare() //关闭sso授权
        oks.disableSSOWhenAuthorize()
        oks.setTitle(title)
        oks.text = text
        if (picurl != null) {
            oks.setImageUrl(picurl)
        }
        // 启动分享
        oks.show(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (permissions.size == 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    download(fuLiBean!!.url)
                }
            }
        }
    }
}
