package com.jiubang.ggheart.data;

import android.content.Context;

/**
 * 
 * <br>类描述: 数据管理器基类
 * <br>功能详细描述: 所有数据管理器必须继承此类
 * 
 * @author  yangguanxiang
 * @date  [2012-12-27]
 */
public abstract class BaseDataModel {
	protected PersistenceManager mManager;
	protected Context mContext;
	public BaseDataModel(Context context, String dbName) {
		mManager = PersistenceManager.getInstance(context, dbName);
		mContext = context;
	}

	public synchronized void beginTransaction() {
		mManager.beginTransaction();
	}

	public synchronized void setTransactionSuccessful() {
		mManager.setTransactionSuccessful();
	}

	public synchronized void endTransaction() {
		mManager.endTransaction();
	}
}
