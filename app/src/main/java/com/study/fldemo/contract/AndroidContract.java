package com.study.fldemo.contract;

import com.study.fldemo.bean.AllBean;
import com.study.fldemo.bean.AndroidBean;

/**
 * Created by Administrator on 2017/9/20.
 */
public interface AndroidContract {
    interface Model {
    }

    interface View {
        void queryOriginalData();

        void onGetDataSuccess(AllBean<AndroidBean> bean);

        void onGetDataFail(String str);
    }

    interface Presenter {
        void queryOriginalData(int size, int page);
    }
}
