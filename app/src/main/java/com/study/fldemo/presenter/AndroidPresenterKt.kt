package com.study.fldemo.presenter

import android.util.Log
import com.study.fldemo.bean.AllBean
import com.study.fldemo.bean.AndroidBean
import com.study.fldemo.bean.FuLiBean
import com.study.fldemo.contract.AndroidContract
import com.study.fldemo.utils.HttpConnectUtils
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class AndroidPresenterKt( private val view: AndroidContract.View) : AndroidContract.Presenter {


    private var utils: HttpConnectUtils = HttpConnectUtils.getInstance("")
    private var random: Random = Random()

    override fun queryOriginalData(size: Int, page: Int) {
        var randomPage = random.nextInt(2)
        try {
            //在主线程中创建协程
            GlobalScope.launch(Main) {
                //不会创建新的协程，在指定协程上运行挂起代码块，并挂起该协程直至代码块运行完成。
                val androidBean = withContext(IO) {
                    //Thread.currentThread().name==================》DefaultDispatcher-worker-1(异步线程)
                    Log.e("AndroidPresenter", "当前线程22：：" + Thread.currentThread().name)
                    utils.managerKotlin.getAndroidDataKotlin(size, page)
                }

                val fuLiBean = withContext(IO) {
                    utils.managerKotlin.getFuLiListDataKotlin(size, page + 1 + randomPage)
                }

                var allBean = AllBean<AndroidBean>()
                allBean.first = fuLiBean.await().results as ArrayList<FuLiBean>
                allBean.second = androidBean.await().results as ArrayList<AndroidBean>
                view.onGetDataSuccess(allBean)
                //Thread.currentThread().name==================》main
                Log.e("AndroidPresenter", "当前线程11：：" + Thread.currentThread().name)
            }
        } catch (e: Exception) {
            view.onGetDataFail("请求失败")
        }
    }

}