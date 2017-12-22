package com.study.fldemo.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/9/20.
 */

public class AndroidResultBean {
    private boolean error;
    private List<AndroidBean> results;
    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<AndroidBean> getResults() {
        return results;
    }

    public void setResults(List<AndroidBean> results) {
        this.results = results;
    }


}
