package com.study.fldemo.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/9/20.
 */

public class AllBean<T> {
    private ArrayList<FuLiBean> first;
    private ArrayList<T> second;

    public ArrayList<FuLiBean> getFirst() {
        return first;
    }

    public void setFirst(ArrayList<FuLiBean> first) {
        this.first = first;
    }

    public ArrayList<T> getSecond() {
        return second;
    }

    public void setSecond(ArrayList<T> second) {
        this.second = second;
    }
}
