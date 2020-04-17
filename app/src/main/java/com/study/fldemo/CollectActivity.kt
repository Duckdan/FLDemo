package com.study.fldemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.study.fldemo.adapter.CollectAdapter
import com.study.fldemo.dao.DaoManager
import com.study.fldemo.dao.DatabaseBean
import com.study.toastutils.ToastUtils

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.study.fldemo.utils.StatusBarUtils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_collect.*
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.iv_back as iv_back1

class CollectActivity : AppCompatActivity(), View.OnClickListener {

    private var daoManager: DaoManager? = null
    private val lists = ArrayList<Any>()
    private val firstList = ArrayList<DatabaseBean>()
    private val secondList = ArrayList<DatabaseBean>()
    private val thirdList = ArrayList<DatabaseBean>()
    private val forthList = ArrayList<DatabaseBean>()
    private val fifthList = ArrayList<DatabaseBean>()
    private val sixthList = ArrayList<DatabaseBean>()
    private lateinit var adapter: CollectAdapter
    private lateinit var showSa: ScaleAnimation
    private lateinit var hideSa: ScaleAnimation
    //用于记录全选的CheckBox的状态是否是人为改变的
    //true时代表是代码改变，false时代表是人为改变
    private var isUserDeal = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect)
        StatusBarUtils.from(this)
                //设置透明的状态
                .setTransparentStatusbar(true)
                .setActionbarView(mAppbar)
                //适配23版本以上的
                .setLightStatusBar(false)
                //必须设置为true,否则状态栏将会变成白色
                .setActionBarOtherColor(true).process()
        daoManager = DaoManager.getInstance(applicationContext)

        iv_back.setOnClickListener(this)
        iv_stick.setOnClickListener(this)
        iv_delete.setOnClickListener(this)
        tv_cancel.setOnClickListener(this)
        initRvAndEvent()
        queryFromDB()
        iv_stick.isEnabled = false
        iv_delete.isEnabled = false


        cb_check.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { button, b ->
            if (isUserDeal) {  //代码改变CheckBox的状态，不用刷新子条目的UI
                isUserDeal = false
                return@OnCheckedChangeListener
            }
            refreshItemUI(b)
        })

        showSa = ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        showSa.duration = 300
        showSa.interpolator = LinearInterpolator()

        hideSa = ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        hideSa.duration = 300
        hideSa.interpolator = LinearInterpolator()
    }


    /**
     * 从数据库中查询数据
     */
    private fun queryFromDB() {
        firstList.clear()
        secondList.clear()
        thirdList.clear()
        forthList.clear()
        fifthList.clear()
        sixthList.clear()
        lists.clear()
        Observable.just(daoManager!!.queryAllDatabaseBean()).map(object : Function<List<DatabaseBean>, List<Any>> {
            @Throws(Exception::class)
            override fun apply(@NonNull beens: List<DatabaseBean>): List<Any> {
                val lists = ArrayList<Any>()
                if (beens == null) {
                    return lists
                }
                for (been in beens) {
                    //由于greenDao再查询数据的时候有非数据库表中字段的状态值它并没有修改，除非杀死进程
                    been.setCheck(false)
                    if ("Android".equals(been.type, ignoreCase = true)) {
                        firstList.add(been)
                    } else if ("Ios".equals(been.type, ignoreCase = true)) {
                        secondList.add(been)
                    } else if ("福利" == been.type) {
                        thirdList.add(been)
                    } else if ("前端" == been.type) {
                        forthList.add(been)
                    } else if ("拓展视频" == been.type) {
                        fifthList.add(been)
                    } else if ("休息视频" == been.type) {
                        sixthList.add(been)
                    }
                }
                if (firstList.size > 0) {
                    lists.add("Android")
                    lists.addAll(firstList)
                }
                if (secondList.size > 0) {
                    lists.add("IOS")
                    lists.addAll(secondList)
                }
                if (thirdList.size > 0) {
                    lists.add("福利")
                    lists.addAll(thirdList)
                }
                if (forthList.size > 0) {
                    lists.add("前端")
                    lists.addAll(forthList)
                }
                if (fifthList.size > 0) {
                    lists.add("拓展视频")
                    lists.addAll(fifthList)
                }
                if (sixthList.size > 0) {
                    lists.add("休息视频")
                    lists.addAll(sixthList)
                }
                return lists
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<List<Any>> {
            override fun onSubscribe(@NonNull d: Disposable) {

            }

            override fun onNext(@NonNull objects: List<Any>) {
                lists.addAll(objects)
                adapter.notifyDataSetChanged()
                if (lists.size == 0) {
                    rv.visibility = View.GONE
                    iv_empty.visibility = View.VISIBLE
                    ll_bottom.visibility = View.GONE
                    tv_check.text = "全选"
                    cb_check.isChecked = false
                    tv_cancel.visibility = View.GONE
                }
            }

            override fun onError(@NonNull e: Throwable) {
                ToastUtils.showToast(applicationContext, "获取失败！")
            }

            override fun onComplete() {

            }
        })
    }

    /**
     * 刷新条目UI
     *
     * @param b
     */
    private fun refreshItemUI(b: Boolean) {
        for (bean in lists) {
            if (bean is DatabaseBean) {
                bean.setCheck(b)
            }
        }
        tv_check.text = if (b) "取消全选" else "全选"
        adapter.notifyDataSetChanged()
    }

    private fun initRvAndEvent() {
        val layout = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = layout
        adapter = CollectAdapter(this, lists)
        rv.adapter = adapter
        adapter.setOnItemClickListener(object : CollectAdapter.OnItemClickListener {
            override fun onItemClick(bean: DatabaseBean) {
                //当控件显示“取消”时，单击条目就是选中
                if (tv_cancel.visibility == View.VISIBLE) {
                    bean.setCheck(!bean.isCheck())
                    refreshCheckUI()
                    adapter.notifyDataSetChanged()
                } else {
                    val intent = Intent(this@CollectActivity, DetailActivity::class.java)
                    intent.putExtra("data", bean)
                    startActivity(intent)
                }
            }

        })
        adapter.setOnItemLongClickListener(object : CollectAdapter.OnItemLongClickListener {
            override fun onItemLongClick(bean: DatabaseBean, hasCheck: Boolean) {
                if (hasCheck) {
                    iv_stick.isEnabled = true
                    iv_delete.isEnabled = true
                } else {
                    iv_stick.isEnabled = false
                    iv_delete.isEnabled = false
                }
                bean.setCheck(!bean.isCheck())


                tv_cancel.visibility = View.VISIBLE
                if (ll_bottom.visibility != View.VISIBLE) {
                    ll_bottom.visibility = View.VISIBLE
                    ll_bottom.animation = showSa
                    showSa.start()
                }
                adapter.setShowCheckBox(true)
                refreshCheckUI()
                adapter.notifyDataSetChanged()
            }

        })

    }

    private fun refreshCheckUI() {
        var hasChecked = false
        var isAllCheck = true
        var single = 0
        for (bean in lists) {
            if (bean is DatabaseBean) {
                val check = bean.isCheck()
                if (check) {
                    single++
                }
                hasChecked = hasChecked or check
                isAllCheck = isAllCheck and check
            }
        }
        tv_check.text = if (isAllCheck) "取消全选" else "全选"
        iv_delete.isEnabled = hasChecked
        iv_stick.isEnabled = single == 1
        isUserDeal = true
        if (cb_check.isChecked == isAllCheck) {  //由于CheckBox的选中状态较上一次的选中状态一致时不会触发选中的状态监听器
            isUserDeal = false
        }
        cb_check.isChecked = isAllCheck
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.iv_back -> onBackPressed()
            R.id.iv_stick -> ToastUtils.showToast(this, "置顶")
            R.id.iv_delete -> {
                for (bean in lists) {
                    if (bean is DatabaseBean) {
                        if (bean.isCheck()) {
                            daoManager!!.deleteDatabaseBean(bean)
                        }
                    }
                }

                //刷新
                queryFromDB()
            }
            R.id.tv_cancel -> {
                //取消之前选中条目的状态
                for (bean in lists) {
                    if (bean is DatabaseBean) {
                        bean.setCheck(false)
                    }
                }
                tv_cancel.visibility = View.GONE
                ll_bottom.animation = hideSa
                hideSa.start()
                ll_bottom.visibility = View.GONE
                adapter.setShowCheckBox(false)
                refreshCheckUI()
                adapter.notifyDataSetChanged()
            }
        }
    }

}
