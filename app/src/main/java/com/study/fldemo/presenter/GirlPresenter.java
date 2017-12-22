package com.study.fldemo.presenter;

import android.content.Context;

import com.study.fldemo.bean.FuLiBean;
import com.study.fldemo.bean.FuLiResultBean;
import com.study.fldemo.manager.MainPresenterI;
import com.study.fldemo.manager.SecondViewI;
import com.study.fldemo.utils.HttpConnectUtils;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/8.
 */

public class GirlPresenter implements MainPresenterI {

    private final HttpConnectUtils utils;
    private final Context context;
    private final SecondViewI viewI;

    public GirlPresenter(Context context, SecondViewI viewI) {
        this.context = context;
        this.viewI = viewI;
        utils = HttpConnectUtils.getInstance("");
    }

    @Override
    public void getFuLiListData(int size, int page) {
        utils
                .getManager()
                .getFuLiListData(size, page)
                .map(new Function<FuLiResultBean, List<FuLiBean>>() {
                    @Override
                    public List<FuLiBean> apply(@NonNull FuLiResultBean bean) throws Exception {
                        return bean.results;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<FuLiBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<FuLiBean> results) {
                        viewI.getFuLiListDataSuccess(results);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        viewI.getFuLiListDataFail("请求失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
