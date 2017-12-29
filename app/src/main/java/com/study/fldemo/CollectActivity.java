package com.study.fldemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.study.fldemo.adapter.CollectAdapter;
import com.study.fldemo.dao.DaoManager;
import com.study.fldemo.dao.DatabaseBean;
import com.study.toastutils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CollectActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rv)
    RecyclerView rv;
    private DaoManager daoManager;
    private List<Object> lists = new ArrayList<>();
    private List<DatabaseBean> firstList = new ArrayList<>();
    private List<DatabaseBean> secondList = new ArrayList<>();
    private List<DatabaseBean> thirdList = new ArrayList<>();
    private List<DatabaseBean> forthList = new ArrayList<>();
    private List<DatabaseBean> fifthList = new ArrayList<>();
    private List<DatabaseBean> sixthList = new ArrayList<>();
    private CollectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ButterKnife.bind(this);
        daoManager = DaoManager.getInstance(getApplicationContext());
        initRvAndEvent();
        firstList.clear();
        secondList.clear();
        thirdList.clear();
        forthList.clear();
        fifthList.clear();
        sixthList.clear();
        lists.clear();
        Observable.
                just(daoManager.queryAllDatabaseBean()).
                map(new Function<List<DatabaseBean>, List<Object>>() {
                    @Override
                    public List<Object> apply(@NonNull List<DatabaseBean> beens) throws Exception {
                        List<Object> lists = new ArrayList<Object>();
                        if (beens == null) {
                            return lists;
                        }
                        for (DatabaseBean been : beens) {
                            if ("Android".equalsIgnoreCase(been.getType())) {
                                firstList.add(been);
                            } else if ("Ios".equalsIgnoreCase(been.getType())) {
                                secondList.add(been);
                            } else if ("福利".equals(been.getType())) {
                                thirdList.add(been);
                            } else if ("前端".equals(been.getType())) {
                                forthList.add(been);
                            } else if ("拓展视频".equals(been.getType())) {
                                fifthList.add(been);
                            } else if ("休息视频".equals(been.getType())) {
                                sixthList.add(been);
                            }
                        }
                        if (firstList.size() > 0) {
                            lists.add("Android");
                            lists.addAll(firstList);
                        }
                        if (secondList.size() > 0) {
                            lists.add("IOS");
                            lists.addAll(secondList);
                        }
                        if (thirdList.size() > 0) {
                            lists.add("福利");
                            lists.addAll(thirdList);
                        }
                        if (forthList.size() > 0) {
                            lists.add("前端");
                            lists.addAll(forthList);
                        }
                        if (fifthList.size() > 0) {
                            lists.add("拓展视频");
                            lists.addAll(fifthList);
                        }
                        if (sixthList.size() > 0) {
                            lists.add("休息视频");
                            lists.addAll(sixthList);
                        }
                        return lists;
                    }
                }).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Observer<List<Object>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<Object> objects) {
                        lists.addAll(objects);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastUtils.showToast(getApplicationContext(), "获取失败！");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void initRvAndEvent() {
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layout);
        adapter = new CollectAdapter(this, lists);
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new CollectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DatabaseBean bean) {
                Intent intent = new Intent(CollectActivity.this, DetailActivity.class);
                intent.putExtra("data", bean);
                startActivity(intent);
            }
        });
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        onBackPressed();
    }
}
