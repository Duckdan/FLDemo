package com.study.fldemo.fragment

import android.content.Intent
import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

import com.study.fldemo.DetailActivity
import com.study.fldemo.R
import com.study.fldemo.adapter.AndroidAdapter
import com.study.fldemo.bean.AllBean
import com.study.fldemo.bean.AndroidBean
import com.study.fldemo.bean.FuLiBean
import com.study.fldemo.contract.ExpandContract
import com.study.fldemo.dao.DatabaseBean
import com.study.fldemo.presenter.ExpandPresenterKt
import com.study.toastutils.ToastUtils

import java.util.ArrayList

import butterknife.BindView
import kotlinx.android.synthetic.main.fragment_girl.*

/**
 * Created by Administrator on 2017/9/29.
 */

class ExpandFragment : BaseFragment(), ExpandContract.View, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var presenter: ExpandPresenterKt
    private lateinit var androidAdapter: AndroidAdapter
    private var netStateFlag: Boolean = false
    private val isFirstId = ""
    private var androidList = ArrayList<AndroidBean>()
    private var fuliList = ArrayList<FuLiBean>()
    private val size = 20
    private var page = 1
    private var layoutManager: LinearLayoutManager? = null


    override fun onCreateFragment(state: Bundle?) {
        setContent(R.layout.fragment_girl)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViewAndListener()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        presenter = ExpandPresenterKt(this)
        if (isVisibleToUser) {
            queryOriginalData()
        }
    }


    private fun initViewAndListener() {
        srl.setColorSchemeColors(-0x77000100, -0x7700ff01)
        srl.isRefreshing = true
        srl.setOnRefreshListener(this)
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = layoutManager

        androidAdapter = AndroidAdapter(activity!!, androidList, fuliList)
        rv.adapter = androidAdapter

        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                var lastPosition = -1
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager
                    if (layoutManager is LinearLayoutManager) {
                        val llm = layoutManager as LinearLayoutManager?
                        lastPosition = llm!!.findLastCompletelyVisibleItemPosition()
                    } else if (layoutManager is GridLayoutManager) {
                        val glm = layoutManager as GridLayoutManager?
                        lastPosition = glm!!.findLastCompletelyVisibleItemPosition()
                    } else {
                        val sglm = layoutManager as StaggeredGridLayoutManager?
                        val count = sglm!!.spanCount
                        val positions = sglm.findLastCompletelyVisibleItemPositions(IntArray(count))
                        lastPosition = findMaxPosition(positions)
                    }

                    if (lastPosition == recyclerView.adapter!!.itemCount - 1) {
                        //再次判断网络状态，避免网络恢复后无法下拉加载
                        showTvNetWorkState()
                        tv_loading.visibility = View.VISIBLE
                        if (netStateFlag) {
                            onLastRefresh()
                            tv_loading.text = "正在加载..."
                        } else {
                            tv_loading.text = "网络未连接~~~"
                        }
                        //                        Toast.makeText(SecondActivity.this, "到达最底部", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })

        androidAdapter.setOnItemClickListener(object : AndroidAdapter.OnItemClickListener {

            override fun onItemClick(bean: DatabaseBean) {

                val intent = Intent(activity, DetailActivity::class.java)
                intent.putExtra("data", bean)
                startActivity(intent)
            }
        })

    }


    override fun onResume() {
        super.onResume()
        showTvNetWorkState()

    }


    private fun findMaxPosition(positions: IntArray): Int {
        var position = positions[0]
        for (i in positions.indices) {
            val max = positions[i]
            if (max > position) {
                position = max
            }
        }
        return position
    }

    override fun onRefresh() {
        showTvNetWorkState()
        page = 1
        queryOriginalData()

    }

    fun onLastRefresh() {
        page++
        queryOriginalData()
    }

    fun showTvNetWorkState() {
        netStateFlag = netStateUtils.checkNetWorkState()
        if (netStateFlag) {
            tv_net.visibility = View.GONE
        } else {
            tv_net.visibility = View.VISIBLE
        }
    }

    override fun queryOriginalData() {
        presenter.queryOriginalData(size, page)
    }

    override fun onGetDataSuccess(bean: AllBean<AndroidBean>) {
        tv_loading.visibility = View.GONE
        ll_bg.visibility = View.GONE
        var id = ""
        val results = bean.second
        if (results != null && results.size > 0) {
            id = results[0]._id
        }

        if (id != isFirstId) {
            androidAdapter!!.addData(results, bean.first, page)
            androidAdapter!!.notifyDataSetChanged()
        }

        srl.isRefreshing = false
    }

    override fun onGetDataFail(str: String) {
        ToastUtils.showToast(activity, str)
        srl.isRefreshing = false
    }
}
