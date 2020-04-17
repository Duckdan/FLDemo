package com.study.fldemo.contract

import com.study.fldemo.bean.AllBean
import com.study.fldemo.bean.AndroidBean

/**
 * Created by Administrator on 2017/9/29.
 */
interface ExpandContract {
    interface Model
    interface View {
        fun queryOriginalData()

        fun onGetDataSuccess(bean: AllBean<AndroidBean>)

        fun onGetDataFail(str: String)
    }

    interface Presenter {
        fun queryOriginalData(size: Int, page: Int)
    }
}
