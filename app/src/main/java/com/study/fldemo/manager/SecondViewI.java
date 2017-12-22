package com.study.fldemo.manager;

import com.study.fldemo.bean.FuLiBean;

import java.util.List;

/**
 * Created by Administrator on 2017/8/8.
 */

public interface SecondViewI {
    void getFuLiListDataSuccess(List<FuLiBean> results);

    void getFuLiListDataFail(String str);
}
