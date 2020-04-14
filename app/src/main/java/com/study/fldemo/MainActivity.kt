package com.study.fldemo

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout

import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import android.text.TextUtils
import android.text.format.Formatter
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView


import com.mob.MobSDK
import com.study.fldemo.adapter.VpAdapter
import com.study.fldemo.bean.UserBean
import com.study.fldemo.dao.DaoManager
import com.study.fldemo.event.DialogEvent
import com.study.fldemo.fragment.AndroidFragment
import com.study.fldemo.fragment.BaseFragment
import com.study.fldemo.fragment.ExpandFragment
import com.study.fldemo.fragment.GirlFragment
import com.study.fldemo.fragment.IosFragment
import com.study.fldemo.fragment.VideoFragment
import com.study.fldemo.fragment.WebFragment
import com.study.fldemo.share.ShareSDKUtils
import com.study.fldemo.utils.DensityUtil
import com.study.fldemo.utils.SpUtils
import com.study.fldemo.view.LazyViewPager

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

import java.io.File
import java.util.ArrayList

import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.tencent.qq.QQ
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nv_header_layout.*

class MainActivity : AppCompatActivity() {

    private var tvTip: TextView? = null


    private var choice = -1
    private var dialog: AlertDialog? = null
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
    }
    private var spUtils: SpUtils? = null
    private var platform: Platform? = null
    //数据库管理对象
    private var daoManager: DaoManager? = null

    private var start: Long = 0
    private var end: Long = 0

    private var toggle: ActionBarDrawerToggle? = null
    private val circleRequestOptions = RequestOptions().transform(CircleCrop())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobSDK.init(this@MainActivity, "203acaefb6205", "cf155b70894ce61017f26f2ed19af2c3")
        platform = ShareSDK.getPlatform(QQ.NAME)
        daoManager = DaoManager.getInstance(applicationContext)

        setContentView(R.layout.activity_main)
        spUtils = DefineApplication.spUtils
        initView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
            val padding = DensityUtil.px2dip(this, 25f)
            toolbar.setPadding(0, padding, 0, 0)
            tl.setPadding(0, padding, 0, 0)
        }
        EventBus.getDefault().register(this)


    }


    private fun initView() {


        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle = ActionBarDrawerToggle(this, dl, R.string.app_name, R.string.app_name)
        toggle?.syncState()
        toggle?.apply {
            dl.addDrawerListener(this)
        }

        val metrics = resources.displayMetrics
        val widthPixels = metrics.widthPixels
        val lp = nv!!.layoutParams
        lp.width = (0.6 * widthPixels).toInt()
        nv!!.layoutParams = lp
        val lists = ArrayList<BaseFragment>()
        val titles = ArrayList<String>()
        lists.add(AndroidFragment())
        titles.add("Android")
        lists.add(IosFragment())
        titles.add("Ios")
        lists.add(GirlFragment())
        titles.add("福利")
        lists.add(WebFragment())
        titles.add("前端")
        lists.add(ExpandFragment())
        titles.add("拓展资源")
        lists.add(VideoFragment())
        titles.add("休息视频")

        tl!!.setupWithViewPager(vp)

        vp!!.adapter = VpAdapter(supportFragmentManager, lists, titles)

        dl!!.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerClosed(drawerView: View) {
                if (choice == -1) {
                    return
                }
                when (choice) {
                    1 -> ShareSDKUtils.Login(QQ.NAME, sdv, tv_name)
                    2 -> clearAppCache(cacheDir!!, false)
                    3 -> {
                        val intent = Intent(this@MainActivity, SearchActivity::class.java)
                        startActivity(intent)
                    }
                    4 -> {
                        ShareSDKUtils.logout(QQ.NAME, sdv)
                        spUtils!!.clearData()
                        initHearView()
                        initMenuItem()
                    }
                    5 -> {
                        val collectIntent = Intent(this@MainActivity, CollectActivity::class.java)
                        startActivity(collectIntent)
                    }
                }
                updateMenuItem()
                choice = -1
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                updateMenuItem()
            }

        })

        nv!!.setNavigationItemSelectedListener { item ->
            var string = ""
            when (item.itemId) {
                R.id.menu_login -> {
                    choice = 1
                    string = "去登陆..."
                }
                R.id.menu_cache -> {
                    choice = 2
                    string = "正在清除..."
                    handler.sendEmptyMessageDelayed(0, 1000)
                }
                R.id.menu_search -> {
                    choice = 3
                    string = "去百度..."
                }
                R.id.menu_exit -> {
                    DefineApplication.loginState = false
                    choice = 4
                    string = "正在退出..."
                    handler.sendEmptyMessageDelayed(0, 1000)
                }
                R.id.menu_collect -> {
                    choice = 5
                    string = "前往收藏..."
                    handler.sendEmptyMessageDelayed(0, 1000)
                }
            }

            showDialog(string)

            item.isCheckable = true
            dl!!.closeDrawers()
            true
        }

        Observable.create(ObservableOnSubscribe<Platform> { e ->
            start = System.currentTimeMillis()
            Log.e("TAG", start.toString() + "")

            platform?.apply { e.onNext(this) }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { platform ->
            if (platform.isAuthValid) {
                end = System.currentTimeMillis()
                Log.e("TAG", ((end - start) * 1.0f / 1000).toString() + "")
                DefineApplication.loginState = true
                initMenuItem()
            }
            initHearView()
            Log.e("TAG", (System.currentTimeMillis() - end).toString() + "")
        }


    }


    private fun initHearView() {
        val userBean = spUtils!!.userFromSp
        tv_name.text = userBean.name
        val url = userBean.url
        if (TextUtils.isEmpty(url)) {
            Glide.with(applicationContext).load(R.drawable.header).apply(circleRequestOptions).into(sdv!!)
        } else {
            Glide.with(applicationContext).load(url).apply(circleRequestOptions).into(sdv!!)
        }
    }

    private fun updateMenuItem() {
        val menu = nv.menu
        val cacheItem = menu.findItem(R.id.menu_cache)
        val size = getFileSize(applicationContext.cacheDir)
        val fileSize = Formatter.formatFileSize(this@MainActivity, size)
        if (size > 0) {
            cacheItem.title = "清理缓存  $fileSize"
        } else {
            cacheItem.title = "清理缓存"
        }
    }

    private fun clearAppCache(file: File, isDelete: Boolean) {
        if (file.isDirectory) {
            val listFiles = file.listFiles()
            for (i in listFiles!!.indices) {
                clearAppCache(listFiles[i], true)
            }
        }

        if (isDelete) {
            if (file.isFile) {
                file.delete()
            } else {
                if (file.listFiles()?.isEmpty() == true) {
                    file.delete()
                }
            }
        }
    }

    private fun getFileSize(file: File): Long {
        var size: Long = 0
        if (file.isDirectory) {
            val listFiles = file.listFiles()
            for (i in listFiles!!.indices) {
                val fileItem = listFiles[i]
                size += getFileSize(fileItem)
            }
        } else {
            size += file.length()
        }
        return size
    }

    @Subscribe
    fun onEventMainThread(event: DialogEvent) {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }

        if (event.state == 1) {
            runOnUiThread { initMenuItem() }

        }
    }

    private fun initMenuItem() {
        val itemLogin = nv!!.menu.findItem(R.id.menu_login)
        val itemExit = nv!!.menu.findItem(R.id.menu_exit)
        if (platform!!.isAuthValid) {
            itemLogin.isVisible = false
            itemExit.isVisible = true
        } else {
            itemLogin.isVisible = true
            itemExit.isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    private fun showDialog(string: String) {
        if (dialog == null) {
            val inflate = View.inflate(this, R.layout.dialog_tip, null)
            val builder = AlertDialog.Builder(this)
            builder.setView(inflate)
            tvTip = inflate.findViewById<View>(R.id.tv_tip) as TextView
            dialog = builder.create()
        }
        tvTip?.text = string
        dialog?.show()
    }
}
