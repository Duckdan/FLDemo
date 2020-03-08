package com.study.fldemo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.study.fldemo.dao.DaoManager;
import com.study.fldemo.dao.DatabaseBean;
import com.study.fldemo.share.ShareSDKUtils;
import com.study.fldemo.utils.DensityUtil;
import com.study.toastutils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;

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
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.iv_collect)
    ImageView ivCollect;
    private DatabaseBean dataBean;
    private DaoManager daoManager;
    private DatabaseBean databaseBean;
    private int width;
    private TextView tvToast;
    private Toast toast = null;
    private Dialog shareDialog;

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
            toolbar.setPadding(0, padding, 0, 0);

            mAppbar.setFitsSystemWindows(false);
            rl.setFitsSystemWindows(false);
            sdv.setFitsSystemWindows(false);
        }
        daoManager = DaoManager.getInstance(getApplicationContext());

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        width = metrics.widthPixels;

        Intent intent = getIntent();
        dataBean = (DatabaseBean) intent.getParcelableExtra("data");
        initData();


    }

    private void initData() {
        boolean isCollected = checkIsCollected();
        ivCollect.setImageResource(isCollected ? R.drawable.icon_collect : R.drawable.icon_stroke_collect);
        sdv.setImageURI(dataBean.getImageUrl());
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        wv.setWebChromeClient(new WebChromeClient());
        wv.setWebViewClient(new WebViewClient());
        wv.loadUrl(dataBean.getUrl());
    }

    /**
     * 检测当前文章是否被收藏
     */
    private boolean checkIsCollected() {
        databaseBean = daoManager.queryDatabaseBeanById(dataBean.get_id());
        if (databaseBean != null) {
            return true;
        } else {
            return false;
        }
    }


    @OnClick({R.id.iv_back, R.id.iv_share, R.id.iv_collect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_share:
                if (DefineApplication.loginState) {
                    showShareDialog();
                } else {
                    ToastUtils.showToast(this, "登录后才可以分享");
                }
                break;
            case R.id.iv_collect:
                boolean isCollected = checkIsCollected();
                if (isCollected) {
                    //TODO 取消收藏
                    showToast(this, "取消收藏");
                    daoManager.deleteDatabaseBean(databaseBean);
                } else {
                    //TODO 已收藏
                    showToast(this, "已收藏");
                    daoManager.insertDatabaseBean(dataBean);
                }
                ivCollect.setImageResource(isCollected ? R.drawable.icon_stroke_collect : R.drawable.icon_collect);
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
                            ShareSDKUtils.shareQQ(dataBean.getWho() + "", dataBean.getDesc() + "", dataBean.getImageUrl(), dataBean.getUrl(), Platform.SHARE_WEBPAGE);
                            shareDialog.dismiss();
                        }
                    });

            shareDialog.findViewById(R.id.ll_wx).
                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ShareSDKUtils.shareWX(dataBean.getWho() + "", dataBean.getDesc() + "", dataBean.getImageUrl(), dataBean.getUrl(), Platform.SHARE_WEBPAGE);

                            shareDialog.dismiss();
                        }
                    });
        }
        shareDialog.show();
    }

    public void showToast(Context context, String message) {
        if (toast == null) {
            toast = new Toast(context);
            View view = View.inflate(context, R.layout.toast_layout, null);
            LinearLayout llBg = (LinearLayout) view.findViewById(R.id.ll_bg);
            tvToast = (TextView) view.findViewById(R.id.tv_toast);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (width * 0.3), (int) (width * 0.3));
            llBg.setLayoutParams(layoutParams);
            toast.setView(view);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        tvToast.setText(message);
        toast.show();
    }
}
