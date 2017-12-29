package com.study.fldemo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mob.MobSDK;
import com.study.fldemo.adapter.VpAdapter;
import com.study.fldemo.bean.UserBean;
import com.study.fldemo.dao.DaoManager;
import com.study.fldemo.event.DialogEvent;
import com.study.fldemo.fragment.AndroidFragment;
import com.study.fldemo.fragment.BaseFragment;
import com.study.fldemo.fragment.ExpandFragment;
import com.study.fldemo.fragment.GirlFragment;
import com.study.fldemo.fragment.IosFragment;
import com.study.fldemo.fragment.VideoFragment;
import com.study.fldemo.fragment.WebFragment;
import com.study.fldemo.share.ShareSDKUtils;
import com.study.fldemo.utils.DensityUtil;
import com.study.fldemo.utils.SpUtils;
import com.study.fldemo.view.LazyViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tl;
    private LazyViewPager vp;
    private NavigationView nv;
    private DrawerLayout dl;
    private ActionBarDrawerToggle toggle;
    private SimpleDraweeView sdv;
    private TextView tvName;
    private int choice = -1;
    private AlertDialog dialog;
    private File cacheDir;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    };
    private TextView tvTip;
    private SpUtils spUtils;
    private Platform platform;
    //数据库管理对象
    private DaoManager daoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cacheDir = getApplicationContext().getCacheDir();

        Fresco.initialize(MainActivity.this);
        MobSDK.init(MainActivity.this, "203acaefb6205", "cf155b70894ce61017f26f2ed19af2c3");
        daoManager = DaoManager.getInstance(getApplicationContext());

        setContentView(R.layout.activity_main);
        spUtils = DefineApplication.spUtils;
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
            int padding = DensityUtil.px2dip(this, 25);
            toolbar.setPadding(0, padding, 0, 0);
            tl.setPadding(0, padding, 0, 0);
        }
        EventBus.getDefault().register(this);


    }

    private long start = 0;
    private long end = 0;

    private void initView() {


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tl = (TabLayout) findViewById(R.id.tl);
        vp = (LazyViewPager) findViewById(R.id.vp);
        nv = (NavigationView) findViewById(R.id.nv);
        dl = (DrawerLayout) findViewById(R.id.dl);
        View hearView = nv.getHeaderView(0);
        View view = hearView;
        sdv = (SimpleDraweeView) view.findViewById(R.id.sdv);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toggle = new ActionBarDrawerToggle(this, dl, R.string.app_name, R.string.app_name);
        toggle.syncState();
        dl.addDrawerListener(toggle);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        ViewGroup.LayoutParams lp = nv.getLayoutParams();
        lp.width = (int) (0.6 * widthPixels);
        lp.height = heightPixels;

        List<BaseFragment> lists = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        lists.add(new AndroidFragment());
        titles.add("Android");
        lists.add(new IosFragment());
        titles.add("Ios");
        lists.add(new GirlFragment());
        titles.add("福利");
        lists.add(new WebFragment());
        titles.add("前端");
        lists.add(new ExpandFragment());
        titles.add("拓展资源");
        lists.add(new VideoFragment());
        titles.add("休息视频");

        tl.setupWithViewPager(vp);

        vp.setAdapter(new VpAdapter(getSupportFragmentManager(), lists, titles));

        dl.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                if (choice == -1) {
                    return;
                }
                switch (choice) {
                    case 1:
                        ShareSDKUtils.Login(QQ.NAME, sdv, tvName);
                        break;
                    case 2:
                        clearAppCache(cacheDir, false);
                        break;
                    case 3:
                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        ShareSDKUtils.logout(QQ.NAME, sdv);
                        spUtils.clearData();
                        initHearView();
                        initMenuItem();
                        break;
                }
                updateMenuItem();
                choice = -1;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                updateMenuItem();
            }

        });

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                String string = "";
                switch (item.getItemId()) {
                    case R.id.menu_login:
                        choice = 1;
                        string = "去登陆...";
                        break;
                    case R.id.menu_cache:
                        choice = 2;
                        string = "正在清除...";
                        handler.sendEmptyMessageDelayed(0, 1000);
                        break;
                    case R.id.menu_search:
                        choice = 3;
                        string = "去百度...";
                        break;
                    case R.id.menu_exit:
                        DefineApplication.loginState = false;
                        choice = 4;
                        string = "正在退出...";
                        handler.sendEmptyMessageDelayed(0, 1000);
                        break;
                }

                showDialog(string);

                item.setCheckable(true);
                dl.closeDrawers();
                return true;
            }
        });

        Observable.create(new ObservableOnSubscribe<Platform>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<Platform> e) throws Exception {
                start = System.currentTimeMillis();
                Log.e("TAG", start + "");
                platform = ShareSDK.getPlatform(QQ.NAME);
                e.onNext(platform);
            }
        }).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<Platform>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Platform platform) throws Exception {
                        if (platform.isAuthValid()) {
                            end = System.currentTimeMillis();
                            Log.e("TAG", (end - start) * 1.0f / 1000 + "");
                            DefineApplication.loginState = true;
                            initHearView();
                            initMenuItem();
                        }
                        Log.e("TAG", System.currentTimeMillis() - end + "");
                    }
                });


    }

    private void initHearView() {
        UserBean userBean = spUtils.getUserFromSp();
        tvName.setText(userBean.getName());
        String url = userBean.getUrl();
        if (TextUtils.isEmpty(url)) {
            sdv.setActualImageResource(R.drawable.header);
        } else {
            sdv.setImageURI(url);
        }
    }

    private void updateMenuItem() {
        Menu menu = nv.getMenu();
        MenuItem cacheItem = menu.findItem(R.id.menu_cache);
        long size = getFileSize(cacheDir);
        String fileSize = Formatter.formatFileSize(MainActivity.this, size);
        if (size > 0) {
            cacheItem.setTitle("清理缓存  " + fileSize);
        } else {
            cacheItem.setTitle("清理缓存");
        }
    }

    private void clearAppCache(File file, boolean isDelete) {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (int i = 0; i < listFiles.length; i++) {
                clearAppCache(listFiles[i], true);
            }
        }

        if (isDelete) {
            if (file.isFile()) {
                file.delete();
            } else {
                if (file.listFiles().length == 0) {
                    file.delete();
                }
            }
        }
    }

    public long getFileSize(File file) {
        long size = 0;
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (int i = 0; i < listFiles.length; i++) {
                File fileItem = listFiles[i];
                size += getFileSize(fileItem);
            }
        } else {
            size += file.length();
        }
        return size;
    }

    @Subscribe
    public void onEventMainThread(DialogEvent event) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        Log.e("thread::", Thread.currentThread().getName() + "==");
        if (event.getState() == 1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initMenuItem();
                }
            });

        }
    }

    private void initMenuItem() {
        MenuItem itemLogin = nv.getMenu().findItem(R.id.menu_login);
        MenuItem itemExit = nv.getMenu().findItem(R.id.menu_exit);
        if (platform.isAuthValid()) {
            itemLogin.setVisible(false);
            itemExit.setVisible(true);
        } else {
            itemLogin.setVisible(true);
            itemExit.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void showDialog(String string) {
        if (dialog == null) {
            View inflate = View.inflate(this, R.layout.dialog_tip, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(inflate);
            tvTip = (TextView) inflate.findViewById(R.id.tv_tip);
            dialog = builder.create();
        }
        tvTip.setText(string);
        dialog.show();
    }
}
