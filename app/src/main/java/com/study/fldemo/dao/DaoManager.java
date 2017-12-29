package com.study.fldemo.dao;

import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2017/12/26.
 */

public class DaoManager {

    private final Context context;
    private final DaoSession daoSession;
    private static DaoManager daoManager;

    private DaoManager(Context context) {
        this.context = context;
        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(context, "collect.db");
        DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDb());
        daoSession = daoMaster.newSession();
    }

    public synchronized static DaoManager getInstance(Context context) {
        if (daoManager == null) {
            daoManager = new DaoManager(context);
        }
        return daoManager;
    }

    public boolean insertDatabaseBean(DatabaseBean DatabaseBean) {
        long insert = daoSession.insert(DatabaseBean);
        return insert > 0 ? true : false;
    }

    public void deleteDatabaseBean(DatabaseBean DatabaseBean) {
        daoSession.delete(DatabaseBean);
    }

    public void deleteAll() {
        daoSession.deleteAll(DatabaseBean.class);
    }

    public void updateDatabaseBean(DatabaseBean DatabaseBean) {
        daoSession.update(DatabaseBean);
    }

    public DatabaseBean queryDatabaseBeanById(String id) {
        DatabaseBean databaseBean = daoSession.queryBuilder(DatabaseBean.class).where(DatabaseBeanDao.Properties._id.eq(id)).build().unique();
        return databaseBean;
    }

    public List<DatabaseBean> queryAllDatabaseBean() {
        List<DatabaseBean> list = daoSession.queryBuilder(DatabaseBean.class).orderDesc(DatabaseBeanDao.Properties.SaveTime).build().list();
        return list;
    }


}
