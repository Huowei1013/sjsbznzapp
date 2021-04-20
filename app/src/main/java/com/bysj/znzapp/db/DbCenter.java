package com.bysj.znzapp.db;

import com.beagle.component.db.IDb;
import com.beagle.component.db.ProxyDb;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/22 16:23
 * @Describe:
 * @Version: 1.0.0
 */
public class DbCenter extends ProxyDb {

    private static DbCenter dataCenter;

    private DbCenter() {
    }

    public static synchronized DbCenter getInstance() {
        if (dataCenter == null) {
            dataCenter = new DbCenter();
        }
        return dataCenter;
    }

    @Override
    protected IDb initDbEngine() {
        return (IDb) new GreenDaoDb();
    }
}
