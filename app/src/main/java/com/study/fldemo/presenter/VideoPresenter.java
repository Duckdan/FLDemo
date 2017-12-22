package com.study.fldemo.presenter;

import android.content.Context;

import com.study.fldemo.bean.AllBean;
import com.study.fldemo.bean.AndroidBean;
import com.study.fldemo.bean.AndroidResultBean;
import com.study.fldemo.bean.FuLiBean;
import com.study.fldemo.bean.FuLiResultBean;
import com.study.fldemo.contract.VideoContract;
import com.study.fldemo.utils.HttpConnectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/9/30.
 */
public class VideoPresenter implements VideoContract.Presenter {
    private final Context context;
    private final VideoContract.View view;
    private final HttpConnectUtils utils;
    private final Random random;

    public VideoPresenter(Context context, VideoContract.View view) {

        this.context = context;
        this.view = view;
        utils = HttpConnectUtils.getInstance("");

        random = new Random();
    }

    @Override
    public void queryOriginalData(int size, int page) {
        int randomPage = random.nextInt(2);
        Observable<List<AndroidBean>> observable1 = utils
                .getManager()
                .getVideoData(size, page)
                .map(new Function<AndroidResultBean, List<AndroidBean>>() {
                    @Override
                    public List<AndroidBean> apply(@NonNull AndroidResultBean bean) throws Exception {
                        return bean.getResults();
                    }
                })
                .subscribeOn(Schedulers.io());

        Observable<List<FuLiBean>> observable2 = utils
                .getManager()
                .getFuLiListData(size, page + randomPage)
                .map(new Function<FuLiResultBean, List<FuLiBean>>() {
                    @Override
                    public List<FuLiBean> apply(@NonNull FuLiResultBean bean) throws Exception {
                        return bean.results;
                    }
                })
                .subscribeOn(Schedulers.io());

        Observable.
                zip(observable1, observable2, new BiFunction<List<AndroidBean>,
                        List<FuLiBean>, AllBean>() {
                    @Override
                    public AllBean<AndroidBean> apply(@NonNull List<AndroidBean> been,
                                                      @NonNull List<FuLiBean> been2) throws Exception {
                        AllBean<AndroidBean> allBean = new AllBean<AndroidBean>();
                        allBean.setFirst((ArrayList<FuLiBean>) been2);
                        allBean.setSecond((ArrayList<AndroidBean>) been);
                        return allBean;
                    }
                }).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Observer<AllBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull AllBean bean) {
                        view.onGetDataSuccess(bean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.onGetDataFail("请求失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
