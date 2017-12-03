package com.jiubang.ggheart.apps.appfunc.business;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.go.util.BroadCaster.BroadCasterObserver;
import com.go.util.ConvertUtils;
import com.go.util.DbUtil;
import com.jiubang.ggheart.apps.appfunc.controler.AppConfigControler;
import com.jiubang.ggheart.apps.appfunc.data.RecentAppDataModel;
import com.jiubang.ggheart.bussiness.BaseBussiness;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.tables.RecentAppTable;

/**
 * 
 * <br>类描述: 最近打开业务逻辑处理器
 * <br>功能详细描述:
 * 
 * @author  yangguanxiang
 * @date  [2012-12-27]
 */
public class RecentAppBussiness extends BaseBussiness implements BroadCasterObserver {

	private RecentAppDataModel mDataModel;
	private ArrayList<Intent> mRecAppItems;
	private int mAppListMaxLength = 24;

	public RecentAppBussiness(Context context) {
		super(context);
		mDataModel = new RecentAppDataModel(context);
		AppConfigControler.getInstance(mContext).registerObserver(this);
	}

	/**
	 * <br>功能简述: 该方法特供给2D的后台用，3D上请勿调用
	 * <br>功能详细描述:效率很低，嵌入了多套循环
	 * <br>注意:
	 * @return
	 */
	public synchronized ArrayList<AppItemInfo> getRecentAppItems() {
		if (mRecAppItems == null) {
			initRecentAppItems();
		}
		ArrayList<AppItemInfo> itemList = new ArrayList<AppItemInfo>();
		for (Intent intent : mRecAppItems) {
			AppItemInfo appItemInfo = mAppDataEngine.getAppItemExceptHide(intent);
			if (appItemInfo != null) {
				itemList.add(appItemInfo);
			}
		}
		return itemList;
	}
	
	public ArrayList<Intent> getRecentAppIntents() {
		if (mRecAppItems == null) {
			initRecentAppItems();
		}
		return mRecAppItems;
	}

	private void initRecentAppItems() {
		Cursor cur = mDataModel.getRecentAppItems();
		if (cur != null) {
			try {
				mRecAppItems = new ArrayList<Intent>(cur.getCount());
				if (cur.moveToLast()) {
					do {
						String intentStr = DbUtil.getString(cur, RecentAppTable.INTENT);
						Intent intent = ConvertUtils.stringToIntent(intentStr);
						if (intent != null) {
							// 清理一些intent不同，但实质是同一个应用的数据
							if (dontNeedAddRecentApp(intent)) {
								mDataModel.removeRecentAppItem(intent);
							} else {
								mRecAppItems.add(intent);
							}
						}
					} while (cur.moveToPrevious());
				}
			} finally {
				cur.close();
			}
		}
	}

	/**
	 * <br>功能简述: 添加最近打开数据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param intent
	 * @param index
	 * @throws DatabaseException 
	 */
	@SuppressWarnings("unchecked")
	public synchronized void addRecentAppItem(final Intent intent) throws DatabaseException {
		if (intent == null) {
			return;
		}
		if (mRecAppItems == null) {
			initRecentAppItems();
		}
		AppItemInfo appItemInfo = mAppDataEngine.getAppItem(intent);
		if (appItemInfo == null) {
			return;
		}
		if (dontNeedAddRecentApp(intent)) {
			return;
		}
		try {
			mDataModel.beginTransaction();
			try {
				int count = 0;
				// 先清除该id在最近打开已存在的
				removeRecentAppItem(intent);
				mRecAppItems.add(0, intent);
				count = mRecAppItems.size();
				// 添加到数据库(第二个参数保留，可用于管理顺序)
				mDataModel.addRecentAppItem(intent, 0);

				if (count > mAppListMaxLength) {
					// 删除最后一条记录
					// 内存中的
					Intent removeIntent = null;
					removeIntent = mRecAppItems.remove(count - 1);
					if (removeIntent != null) {
						// 数据库中的
						mDataModel.removeRecentAppItem(removeIntent);
					}
				}
				mDataModel.setTransactionSuccessful();
			} finally {
				mDataModel.endTransaction();
			}
		} catch (Exception e) {
			if (e instanceof DatabaseException) {
				throw (DatabaseException) e;
			} else {
				throw new DatabaseException(e);
			}
		}
	}

	private boolean dontNeedAddRecentApp(Intent compareIntent) {
		if (compareIntent != null) {
			for (Intent intent : mRecAppItems) {
				if (compareIntent.toString().equals(intent.toString())) { //同一个intent被激活，添加到队列最前面
					return false;
				}
				ComponentName cn = intent.getComponent();
				if (cn.equals(compareIntent.getComponent())) { //排除一个应用多个入口的情况
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * <br>功能简述: 删除最近打开数据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param intent
	 */
	public synchronized void removeRecentAppItem(final Intent intent) {
		if (intent == null) {
			return;
		}
		if (mRecAppItems != null) {
			for (Intent appIntent : mRecAppItems) {
				if (ConvertUtils.intentCompare(appIntent, intent)) {
					mRecAppItems.remove(appIntent);
					break;
				}
			}
		}

		mDataModel.removeRecentAppItem(intent);
	}

	/**
	 * <br>功能简述: 删除所有最近打开数据
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public synchronized void removeAllRecentAppItems() {
		if (mRecAppItems != null) {
			mRecAppItems.clear();
		}
		mDataModel.removeAllRecentAppItems();
	}

	/**
	 * 处理卸载
	 * 
	 * @param appItemInfos
	 */
	public void handleUnInstall(final ArrayList<AppItemInfo> appItemInfos) {
		if (null == appItemInfos) {
			return;
		}

		// 批量删除
		AppItemInfo appItemInfo = null;
		int size = appItemInfos.size();
		for (int i = 0; i < size; ++i) {
			appItemInfo = appItemInfos.get(i);
			if (appItemInfo == null) {
				continue;
			}
			removeRecentAppItem(appItemInfo.mIntent);
		}
	}

	/**
	 * 设置最近打开程序节点信息列表最大长度
	 * 
	 * @param appListMaxLenth
	 *            最近打开程序节点信息列表最大长度
	 * 
	 */
	public void setAppListMaxLenth(int appListMaxLenth) {
		mAppListMaxLength = appListMaxLenth;
	}

	@Override
	public synchronized void cleanup() {
		super.cleanup();
		if (mRecAppItems != null) {
			mRecAppItems.clear();
		}
	}

	@Override
	public void onBCChange(int msgId, int param, Object ...object) {
		switch (msgId) {
			case AppConfigControler.ADDHIDEITEMS :
				mRecAppItems = null;
				initRecentAppItems();
				break;

			default :
				break;
		}
		
	}
}
