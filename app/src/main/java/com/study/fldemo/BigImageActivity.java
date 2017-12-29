package com.study.fldemo;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.study.fldemo.bean.FuLiBean;
import com.study.fldemo.share.ShareSDKUtils;
import com.study.fldemo.utils.HttpConnectUtils;
import com.study.toastutils.ToastUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2017/8/9.
 */

public class BigImageActivity extends AppCompatActivity implements View.OnClickListener {

    private SimpleDraweeView sdv;
    private FuLiBean fuLiBean;
    private TextView ivShare;
    private TextView ivDownload;
    private HttpConnectUtils httpConnectUtils;
    String picurl = "http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg";
    private Dialog shareDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);

        sdv = (SimpleDraweeView) findViewById(R.id.sdv);
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
        ivShare = (TextView) findViewById(R.id.iv_share);
        ivDownload = (TextView) findViewById(R.id.iv_download);
        ivShare.setOnClickListener(this);
        ivDownload.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        Intent intent = getIntent();
        fuLiBean = intent.getParcelableExtra("big");
        picurl = fuLiBean.url;
        sdv.setImageURI(fuLiBean.url);
        httpConnectUtils = HttpConnectUtils.getInstance("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_share:
//                showShare("sdk测试", "sdk测试", picurl);
//                Toast.makeText(this, "点击了", Toast.LENGTH_SHORT).show();
//                ShareSDKUtils.shareWX("sdk测试", "sdk测试", picurl, Platform.SHARE_IMAGE + "");
//                ShareSDKUtils.shareQzone("sdk测试", "sdk测试", picurl, picurl);
//                ShareSDKUtils.Login(Wechat.NAME);
//                ShareSDKUtils.Login(QQ.NAME);
//                ShareSDKUtils.shareQQ("sdk测试", "sdk测试", picurl, picurl);
                if (DefineApplication.loginState) {
                    showShareDialog();
                } else {
                    ToastUtils.showToast(this, "登录后才可以分享");
                }
                break;
            case R.id.iv_download:
                downloadImage(fuLiBean.url);
                break;
        }
    }

    private void showShareDialog() {
        if (shareDialog == null) {
            shareDialog = new Dialog(this, R.style.defineDialogStyle);
            shareDialog.setContentView(R.layout.share_dialog_layout);
            Window window = shareDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = getResources().getDisplayMetrics().widthPixels;
            window.setAttributes(lp);
            shareDialog.findViewById(R.id.ll_qq).
                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ShareSDKUtils.shareQQ(fuLiBean.who + "", fuLiBean.desc + "", picurl, picurl, Platform.SHARE_IMAGE);
                            shareDialog.dismiss();
                        }
                    });

            shareDialog.findViewById(R.id.ll_wx).
                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ShareSDKUtils.shareWX(fuLiBean.who + "", fuLiBean.desc + "", picurl, picurl, Platform.SHARE_IMAGE);
                            shareDialog.dismiss();
                        }
                    });
        }
        shareDialog.show();
    }

    private void downloadImage(String url) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ToastUtils.showToast(this, "请授于应用的读写的权限！");
                    // 帮跳转到该应用的设置界面，让用户手动授权
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    // 不需要解释为何需要该权限，直接请求授权
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }else {
                download(url);
            }
        } else {
            download(url);
        }

    }

    private void download(String url) {
        httpConnectUtils
                .getManager()
                .getImageFile(url)
                .subscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull ResponseBody body) throws Exception {

                        return saveToDisk(body);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        Toast.makeText(BigImageActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Boolean saveToDisk(ResponseBody body) {
        boolean flag = false;
        InputStream inputStream = body.byteStream();
        String filePath = Environment.getExternalStorageDirectory().getPath();
        String format = DateFormat.format("yyyy-dd-MM HH:mm:ss", System.currentTimeMillis()).toString();
        String fileName = format + " " + fuLiBean.who + fuLiBean.url.substring(fuLiBean.url.lastIndexOf("."), fuLiBean.url.length());
        File fileDir = new File(filePath, "fuli");
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

        File fileChild = new File(fileDir, fileName);
        int len = 0;
        try {
            FileOutputStream fos = new FileOutputStream(fileChild);
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            byte[] buffer = new byte[2048];
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            bis.close();
            fos.close();
            inputStream.close();
            flag = true;
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * @param title  标题
     * @param text   内容
     * @param picurl 图片链接 *
     *               QQ和QQ空间设置分享链接使用setTitleUrl();
     *               设置标题：setTitle
     *               设置内容：setText
     *               设置网络图片：oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
     *               设置本地图片： //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片 *
     *               微信 * url仅在微信（包括好友和朋友圈）中使用 * oks.setUrl("http://qq.com");
     */
    private void showShare(String title, String text, String picurl) {
        OnekeyShare oks = new OnekeyShare(); //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setTitle(title);
        oks.setText(text);
        if (picurl != null) {
            oks.setImageUrl(picurl);
        }
        // 启动分享
        oks.show(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @android.support.annotation.NonNull String[] permissions, @android.support.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (permissions.length == 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    download(fuLiBean.url);
                }
            }
        }
    }
}
