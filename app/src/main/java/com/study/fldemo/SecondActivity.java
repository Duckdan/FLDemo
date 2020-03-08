package com.study.fldemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.mob.MobSDK;
import com.study.fldemo.adapter.BaseAdapter;
import com.study.fldemo.bean.FuLiBean;
import com.study.fldemo.manager.SecondViewI;
import com.study.fldemo.presenter.GirlPresenter;
import com.study.fldemo.utils.NetStateUtils;

import java.util.List;

public class SecondActivity extends AppCompatActivity implements SecondViewI,
        SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView rv;
    private SwipeRefreshLayout srl;
    private int size = 20;
    private int page = 1;
    private GirlPresenter mainPresenter;
    private BaseAdapter mBaseAdapter;
    private TextView tvLoading;
    private Context context = this;
    private NetStateUtils netStateUtils;
    private boolean netStateFlag;
    private String isFirstId = "";
    private TextView tvNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        MobSDK.init(this,"203acaefb6205","cf155b70894ce61017f26f2ed19af2c3");
        setContentView(R.layout.activity_second);
        rv = (RecyclerView) findViewById(R.id.rv);
        srl = (SwipeRefreshLayout) findViewById(R.id.srl);
        tvLoading = (TextView) findViewById(R.id.tv_loading);
        tvNet = (TextView) findViewById(R.id.tv_net);
        srl.setColorSchemeColors(0x88ffff00, 0x88ff00ff);
        srl.setRefreshing(true);
        srl.setOnRefreshListener(this);
        mainPresenter = new GirlPresenter(this, this);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
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
        netStateUtils = NetStateUtils.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showTvNetWorkState();
        mainPresenter.getFuLiListData(size, page);
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
    public void getFuLiListDataSuccess(List<FuLiBean> results) {
        tvLoading.setVisibility(View.GONE);
//        Toast.makeText(this, "加载成功" + results.size(), Toast.LENGTH_SHORT).show();
        String id = "";
        if (results != null) {
            id = results.get(0)._id;
        }
        if (mBaseAdapter == null) {
            if (results != null) {
                isFirstId = results.get(0)._id;
            }
            mBaseAdapter = new BaseAdapter(this, results);
            mBaseAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClickListener(int position, FuLiBean fuLiBean) {
                    Intent intent = new Intent(SecondActivity.this, BigImageActivity.class);
                    intent.putExtra("big", fuLiBean);
                    startActivity(intent);
                }
            });
            rv.setAdapter(mBaseAdapter);
        } else {
            if (!id.equals(isFirstId)) {
                mBaseAdapter.addData(results, page);
                mBaseAdapter.notifyDataSetChanged();
            }
        }
        srl.setRefreshing(false);
    }

    @Override
    public void getFuLiListDataFail(String str) {
        srl.setRefreshing(false);
//        Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
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
//        Toast.makeText(this, "第" + page + "页", Toast.LENGTH_SHORT).show();
    }

    public void showTvNetWorkState() {
        netStateFlag = netStateUtils.checkNetWorkState();
        if (netStateFlag) {
            tvNet.setVisibility(View.GONE);
        } else {
            tvNet.setVisibility(View.VISIBLE);
        }
    }
}
