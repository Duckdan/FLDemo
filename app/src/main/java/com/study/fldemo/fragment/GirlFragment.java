package com.study.fldemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.study.fldemo.BigImageActivity;
import com.study.fldemo.R;
import com.study.fldemo.adapter.BaseAdapter;
import com.study.fldemo.bean.FuLiBean;
import com.study.fldemo.manager.SecondViewI;
import com.study.fldemo.presenter.GirlPresenter;
import com.study.toastutils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by zouqianyu on 2017/9/16.
 */

public class GirlFragment extends BaseFragment implements SecondViewI, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.ll_bg)
    LinearLayout llBg;
    @BindView(R.id.tv_net)
    TextView tvNet;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.tv_loading)
    TextView tvLoading;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;
    private GirlPresenter mainPresenter;
    private BaseAdapter mBaseAdapter;
    private boolean netStateFlag;
    private String isFirstId = "";
    private List<FuLiBean> results = new ArrayList<>();
    private int size = 20;
    private int page = 1;
    private StaggeredGridLayoutManager staggeredmanager;


    @Override
    public void onCreateFragment(Bundle state) {
        setContent(R.layout.fragment_girl);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initViewAndListener();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mainPresenter == null) {
            mainPresenter = new GirlPresenter(context, this);
        }
        if (isVisibleToUser) {
            mainPresenter.getFuLiListData(size, page);
        }
    }


    private void initViewAndListener() {
        srl.setColorSchemeColors(0x88ffff00, 0x88ff00ff);
        srl.setRefreshing(true);
        srl.setOnRefreshListener(this);
        staggeredmanager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(staggeredmanager);
        mBaseAdapter = new BaseAdapter(context, results);
        mBaseAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(int position, FuLiBean fuLiBean) {

                Intent intent = new Intent(context, BigImageActivity.class);
                intent.putExtra("big", fuLiBean);
                startActivity(intent);
            }
        });
        rv.setAdapter(mBaseAdapter);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int lastPosition = -1;
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager llm = (LinearLayoutManager) layoutManager;
                        lastPosition = llm.findLastCompletelyVisibleItemPosition();
                    } else if (layoutManager instanceof GridLayoutManager) {
                        GridLayoutManager glm = (GridLayoutManager) layoutManager;
                        lastPosition = glm.findLastCompletelyVisibleItemPosition();
                    } else {
                        StaggeredGridLayoutManager sglm = (StaggeredGridLayoutManager) layoutManager;
                        int count = sglm.getSpanCount();
                        int[] positions = sglm.findLastCompletelyVisibleItemPositions(new int[count]);
                        lastPosition = findMaxPosition(positions);
                    }

                    if (lastPosition == recyclerView.getAdapter().getItemCount() - 1) {
                        //再次判断网络状态，避免网络恢复后无法下拉加载
                        showTvNetWorkState();
                        tvLoading.setVisibility(View.VISIBLE);
                        if (netStateFlag) {
                            onLastRefresh();
                            tvLoading.setText("正在加载...");
                        } else {
                            tvLoading.setText("网络未连接~~~");
                        }
//                        Toast.makeText(SecondActivity.this, "到达最底部", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        showTvNetWorkState();

    }


    private int findMaxPosition(int[] positions) {
        int position = positions[0];
        for (int i = 0; i < positions.length; i++) {
            int max = positions[i];
            if (max > position) {
                position = max;
            }
        }
        return position;
    }

    @Override
    public void onRefresh() {
        showTvNetWorkState();
        page = 1;
        mainPresenter.getFuLiListData(size, page);

    }

    public void onLastRefresh() {
        page++;
        mainPresenter.getFuLiListData(size, page);
    }

    public void showTvNetWorkState() {
        netStateFlag = netStateUtils.checkNetWorkState();
        if (netStateFlag) {
            tvNet.setVisibility(View.GONE);
        } else {
            tvNet.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void getFuLiListDataSuccess(List<FuLiBean> results) {
        tvLoading.setVisibility(View.GONE);
        llBg.setVisibility(View.GONE);
        String id = "";
        if (results != null) {
            id = results.get(0)._id;
        }

        if (!id.equals(isFirstId)) {
            mBaseAdapter.addData(results, page);
            mBaseAdapter.notifyDataSetChanged();
        }

        srl.setRefreshing(false);
    }

    @Override
    public void getFuLiListDataFail(String str) {
        ToastUtils.showToast(getActivity(), str);
        srl.setRefreshing(false);
    }

}
