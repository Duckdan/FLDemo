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

import com.study.fldemo.BigImageActivity
import com.study.fldemo.R
import com.study.fldemo.adapter.BaseAdapter
import com.study.fldemo.bean.FuLiBean
import com.study.fldemo.manager.SecondViewI
import com.study.fldemo.presenter.GirlPresenterKt
import com.study.toastutils.ToastUtils

import java.util.ArrayList

import butterknife.BindView
import kotlinx.android.synthetic.main.fragment_girl.*

/**
 * Created by zouqianyu on 2017/9/16.
 */

class GirlFragment : BaseFragment(), SecondViewI, SwipeRefreshLayout.OnRefreshListener {


    private lateinit var mainPresenter: GirlPresenterKt
    private lateinit var mBaseAdapter: BaseAdapter
    private var netStateFlag: Boolean = false
    private val isFirstId = ""
    private val results = ArrayList<FuLiBean>()
    private val size = 20
    private var page = 2
    private var staggeredmanager: StaggeredGridLayoutManager? = null


    override fun onCreateFragment(state: Bundle?) {
        setContent(R.layout.fragment_girl)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViewAndListener()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mainPresenter = GirlPresenterKt(this)
        if (isVisibleToUser) {
            mainPresenter.getFuLiListData(size, page)
        }
    }


    private fun initViewAndListener() {
        srl.setColorSchemeColors(-0x77000100, -0x7700ff01)
        srl.isRefreshing = true
        srl.setOnRefreshListener(this)
        staggeredmanager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rv.layoutManager = staggeredmanager
        mBaseAdapter = BaseAdapter(context!!, results)
        mBaseAdapter.setOnItemClickListener(object : BaseAdapter.OnItemClickListener {
            override fun onItemClickListener(position: Int, fuLiBean: FuLiBean) {

                val intent = Intent(context, BigImageActivity::class.java)
                intent.putExtra("big", fuLiBean)
                startActivity(intent)
            }
        })
        rv.adapter = mBaseAdapter

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
        page = 2
        mainPresenter!!.getFuLiListData(size, page)

    }

    fun onLastRefresh() {
        page++
        mainPresenter!!.getFuLiListData(size, page)
    }

    fun showTvNetWorkState() {
        netStateFlag = netStateUtils.checkNetWorkState()
        if (netStateFlag) {
            tv_net.visibility = View.GONE
        } else {
            tv_net.visibility = View.VISIBLE
        }
    }

    override fun getFuLiListDataSuccess(results: MutableList<FuLiBean>) {
        tv_loading.visibility = View.GONE
        ll_bg.visibility = View.GONE
        var id: String? = ""
        if (results != null && results.size > 0) {
            id = results[0]._id
        }

        if (id != isFirstId) {
            mBaseAdapter.addData(results, page)
            mBaseAdapter.notifyDataSetChanged()
        }

        srl.isRefreshing = false
    }

    override fun getFuLiListDataFail(str: String) {
        ToastUtils.showToast(activity, str)
        srl.isRefreshing = false
    }

}
