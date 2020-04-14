package com.study.fldemo.presenter

import android.content.Context
import android.util.Log
import com.study.fldemo.bean.AllBean
import com.study.fldemo.bean.AndroidBean

import com.study.fldemo.bean.FuLiBean
import com.study.fldemo.bean.FuLiResultBean
import com.study.fldemo.manager.MainPresenterI
import com.study.fldemo.manager.SecondViewI
import com.study.fldemo.utils.HttpConnectUtils

import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Created by Administrator on 2017/8/8.
 */

class GirlPresenterKt(private val viewI: SecondViewI) : MainPresenterI {

    private val utils: HttpConnectUtils = HttpConnectUtils.getInstance("")
    private val random: Random = Random()

    override fun getFuLiListData(size: Int, page: Int) {
        var randomPage = random.nextInt(2)

        try {
            //在主线程中创建协程
            GlobalScope.launch(Dispatchers.Main) {

                val fuLiBean = withContext(Dispatchers.IO) {
                    utils.managerKotlin.getFuLiListDataKotlin(size, page )
                }


                val fuLiBeanData = fuLiBean.await().results as ArrayList<FuLiBean>
                viewI.getFuLiListDataSuccess(fuLiBeanData)
                Log.e("GirlPresenterKt", "当前线程11：：" + Thread.currentThread().name)
            }
        } catch (e: Exception) {
            viewI.getFuLiListDataFail("请求失败")
        }
    }
}
