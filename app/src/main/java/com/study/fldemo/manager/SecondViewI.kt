package com.study.fldemo.manager

import com.study.fldemo.bean.FuLiBean

/**
 * Created by Administrator on 2017/8/8.
 */

interface SecondViewI {
    fun getFuLiListDataSuccess(results: MutableList<FuLiBean>)

    fun getFuLiListDataFail(str: String)
}
