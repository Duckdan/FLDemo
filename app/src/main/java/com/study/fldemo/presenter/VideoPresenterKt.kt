package com.study.fldemo.presenter

import android.content.Context

import com.study.fldemo.bean.AllBean
import com.study.fldemo.bean.AndroidBean
import com.study.fldemo.bean.AndroidResultBean
import com.study.fldemo.bean.FuLiBean
import com.study.fldemo.bean.FuLiResultBean
import com.study.fldemo.contract.VideoContract
import com.study.fldemo.utils.HttpConnectUtils

import java.util.ArrayList
import java.util.Random

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Administrator on 2017/9/30.
 */
class VideoPresenterKt(private val view: VideoContract.View) : VideoContract.Presenter {
    private val utils: HttpConnectUtils = HttpConnectUtils.getInstance("")
    private val random: Random = Random()

    override fun queryOriginalData(size: Int, page: Int) {
        var randomPage = random.nextInt(2)
        try {
            //在主线程中创建协程
            GlobalScope.launch(Dispatchers.Main) {
                //不会创建新的协程，在指定协程上运行挂起代码块，并挂起该协程直至代码块运行完成。
                val androidBean = withContext(Dispatchers.IO) {
                    utils.managerKotlin.getVideoDataKotlin(size, page)
                }

                val fuLiBean = withContext(Dispatchers.IO) {
                    utils.managerKotlin.getFuLiListDataKotlin(size, page + 1 + randomPage)
                }

                var allBean = AllBean<AndroidBean>()
                allBean.first = fuLiBean.await().results as ArrayList<FuLiBean>
                allBean.second = androidBean.await().results as ArrayList<AndroidBean>
                view.onGetDataSuccess(allBean)
            }
        } catch (e: Exception) {
            view.onGetDataFail("请求失败")
        }
    }
}
