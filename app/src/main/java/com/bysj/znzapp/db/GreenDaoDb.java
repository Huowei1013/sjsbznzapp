package com.bysj.znzapp.db;

import com.beagle.component.db.IDb;
import com.bysj.znzapp.base.BaseApp;
import com.bysj.znzapp.greendao.gen.DaoMaster;
import com.bysj.znzapp.greendao.gen.DaoSession;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;

import java.util.Collection;
import java.util.List;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/22 16:23
 * @Describe:
 * @Version: 1.0.0
 */
public class GreenDaoDb implements IDb {

    private DaoSession daoSession;

    @Override
    public <T> void saveEntity(T entity) {
        if (daoSession == null) { // 防止内存的属性被回收
            initDao();
        }
        daoSession.insertOrReplace(entity);
    }

    @Override
    public <T, K> T findEntity(Class<T> tClass, K key) {
        if (daoSession == null) {
            initDao();
        }
        return daoSession.load(tClass, key);
    }

    @Override
    public <T> List<T> findAllEntity(Class<T> tClass) {
        if (daoSession == null) {
            initDao();
        }
        return daoSession.loadAll(tClass);
    }

    @Override
    public <T> List<T> findAllEntity(Class<T> entityClass, String where, String... selectionArgs) {
        if (daoSession == null) {
            initDao();
        }
        return daoSession.queryRaw(entityClass, where, selectionArgs);
    }

    @Override
    public <T> void updateEntity(T entity) {
        if (daoSession == null) {
            initDao();
        }
        daoSession.update(entity);
    }

    @Override
    public <T> void deleteEntity(T entity) {
        if (daoSession == null) {
            initDao();
        }
        daoSession.delete(entity);
    }

    @Override
    public void deleteDd() {
        if (daoSession == null) {
            initDao();
        }
        Collection<AbstractDao<?, ?>> collection = daoSession.getAllDaos();
        if (collection != null && !collection.isEmpty()) {
            for (AbstractDao<?, ?> abstractDao : collection) {
                abstractDao.deleteAll();
            }
        }
    }

    @Override
    public void createDb() {

    }

    @Override
    public void closeDb() {

    }

    public GreenDaoDb() {
        if (daoSession == null) {
            initDao();
        }
    }

    private void initDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(BaseApp.getApp(), "jczl-db", null);
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }
}
