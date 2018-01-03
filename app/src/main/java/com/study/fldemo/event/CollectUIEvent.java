package com.study.fldemo.event;

import com.study.fldemo.dao.DatabaseBean;

/**
 * Created by Administrator on 2018/1/2.
 */

public class CollectUIEvent {
    boolean isAllCheck;
    boolean hasCheck;
    boolean isSingle;
    DatabaseBean db;

    public CollectUIEvent(boolean isAllCheck, boolean hasCheck, boolean isSingle,DatabaseBean db) {
        this.isAllCheck = isAllCheck;
        this.hasCheck = hasCheck;
        this.isSingle = isSingle;
        this.db = db;
    }

    public boolean isAllCheck() {
        return isAllCheck;
    }

    public void setAllCheck(boolean allCheck) {
        isAllCheck = allCheck;
    }

    public boolean isHasCheck() {
        return hasCheck;
    }

    public void setHasCheck(boolean hasCheck) {
        this.hasCheck = hasCheck;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    public DatabaseBean getDb() {
        return db;
    }

    public void setDb(DatabaseBean db) {
        this.db = db;
    }
}
