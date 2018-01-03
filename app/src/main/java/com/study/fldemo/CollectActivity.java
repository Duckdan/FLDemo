package com.study.fldemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    @BindView(R.id.iv_stick)
    ImageView ivStick;
    @BindView(R.id.tv_stick)
    TextView tvStick;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;
    @BindView(R.id.cb_check)
    CheckBox cbCheck;
    @BindView(R.id.tv_check)
    TextView tvCheck;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;
    private DaoManager daoManager;
    private List<Object> lists = new ArrayList<>();
    private List<DatabaseBean> firstList = new ArrayList<>();
    private List<DatabaseBean> secondList = new ArrayList<>();
    private List<DatabaseBean> thirdList = new ArrayList<>();
    private List<DatabaseBean> forthList = new ArrayList<>();
    private List<DatabaseBean> fifthList = new ArrayList<>();
    private List<DatabaseBean> sixthList = new ArrayList<>();
    private CollectAdapter adapter;
    private ScaleAnimation showSa;
    private ScaleAnimation hideSa;
    //用于记录全选的CheckBox的状态是否是人为改变的
    //true时代表是代码改变，false时代表是人为改变
    private boolean isUserDeal = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ButterKnife.bind(this);
        daoManager = DaoManager.getInstance(getApplicationContext());
        initRvAndEvent();
        queryFromDB();
        ivStick.setEnabled(false);
        ivDelete.setEnabled(false);


        cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean b) {
                if (isUserDeal) {  //代码改变CheckBox的状态，不用刷新子条目的UI
                    isUserDeal = false;
                    return;
                }
                refreshItemUI(b);
            }
        });

        showSa = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        showSa.setDuration(300);
        showSa.setInterpolator(new LinearInterpolator());

        hideSa = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        hideSa.setDuration(300);
        hideSa.setInterpolator(new LinearInterpolator());
    }


    /**
     * 从数据库中查询数据
     */
    private void queryFromDB() {
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
                            //由于greenDao再查询数据的时候有非数据库表中字段的状态值它并没有修改，除非杀死进程
                            been.setCheck(false);
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
                        if (lists.size() == 0) {
                            rv.setVisibility(View.GONE);
                            ivEmpty.setVisibility(View.VISIBLE);
                            llBottom.setVisibility(View.GONE);
                            tvCheck.setText("全选");
                            cbCheck.setChecked(false);
                            tvCancel.setVisibility(View.GONE);
                        }
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

    /**
     * 刷新条目UI
     *
     * @param b
     */
    private void refreshItemUI(boolean b) {
        for (Object bean : lists) {
            if (bean instanceof DatabaseBean) {
                DatabaseBean db = (DatabaseBean) bean;
                db.setCheck(b);
            }
        }
        tvCheck.setText(b ? "取消全选" : "全选");
        adapter.notifyDataSetChanged();
    }

    private void initRvAndEvent() {
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layout);
        adapter = new CollectAdapter(this, lists);
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new CollectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DatabaseBean bean) {
                //当控件显示“取消”时，单击条目就是选中
                if (tvCancel.getVisibility() == View.VISIBLE) {
                    bean.setCheck(!bean.isCheck());
                    refreshCheckUI();
                    adapter.notifyDataSetChanged();
                } else {
                    Intent intent = new Intent(CollectActivity.this, DetailActivity.class);
                    intent.putExtra("data", bean);
                    startActivity(intent);
                }
            }
        });
        adapter.setOnItemLongClickListener(new CollectAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(DatabaseBean bean, boolean hasCheck) {
                if (hasCheck) {
                    ivStick.setEnabled(true);
                    ivDelete.setEnabled(true);
                } else {
                    ivStick.setEnabled(false);
                    ivDelete.setEnabled(false);
                }
                bean.setCheck(!bean.isCheck());


                tvCancel.setVisibility(View.VISIBLE);
                if (llBottom.getVisibility() != View.VISIBLE) {
                    llBottom.setVisibility(View.VISIBLE);
                    llBottom.setAnimation(showSa);
                    showSa.start();
                }
                adapter.setShowCheckBox(true);
                refreshCheckUI();
                adapter.notifyDataSetChanged();
            }
        });

    }

    private void refreshCheckUI() {
        boolean hasChecked = false;
        boolean isAllCheck = true;
        int single = 0;
        for (Object bean : lists) {
            if (bean instanceof DatabaseBean) {
                DatabaseBean db = (DatabaseBean) bean;
                boolean check = db.isCheck();
                if (check) {
                    single++;
                }
                hasChecked |= check;
                isAllCheck &= check;
            }
        }
        tvCheck.setText(isAllCheck ? "取消全选" : "全选");
        ivDelete.setEnabled(hasChecked);
        ivStick.setEnabled(single == 1);
        isUserDeal = true;
        if (cbCheck.isChecked() == isAllCheck) {  //由于CheckBox的选中状态较上一次的选中状态一致时不会触发选中的状态监听器
            isUserDeal = false;
        }
        cbCheck.setChecked(isAllCheck);
    }


    @OnClick({R.id.iv_back, R.id.iv_stick, R.id.iv_delete, R.id.tv_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_stick:
                ToastUtils.showToast(this, "置顶");
                break;
            case R.id.iv_delete:
                for (Object bean : lists) {
                    if (bean instanceof DatabaseBean) {
                        DatabaseBean db = (DatabaseBean) bean;
                        if (db.isCheck()) {
                            daoManager.deleteDatabaseBean(db);
                        }
                    }
                }

                //刷新
                queryFromDB();
                break;
            case R.id.tv_cancel:
                //取消之前选中条目的状态
                for (Object bean : lists) {
                    if (bean instanceof DatabaseBean) {
                        DatabaseBean db = (DatabaseBean) bean;
                        db.setCheck(false);
                    }
                }
                tvCancel.setVisibility(View.GONE);
                llBottom.setAnimation(hideSa);
                hideSa.start();
                llBottom.setVisibility(View.GONE);
                adapter.setShowCheckBox(false);
                refreshCheckUI();
                adapter.notifyDataSetChanged();
                break;
        }
    }

}
