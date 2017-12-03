package com.jiubang.ggheart.common.bussiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.jiubang.ggheart.bussiness.BaseBussiness;
import com.jiubang.ggheart.common.data.AppExtraAttribute;
import com.jiubang.ggheart.common.data.AppExtraInfoDataModel;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.tables.AppExtraAttributeTable;

/**
 * 
 * @author wuziyi
 *
 */
public class AppExtraInfoBussiness extends BaseBussiness {
	private HashMap<String, List<AppExtraAttribute>> mAttuributeMap;
	private AppExtraInfoDataModel mDataModel;

	public AppExtraInfoBussiness(Context context) {
		super(context);
		mDataModel = new AppExtraInfoDataModel(mContext);
	}
	
	public void initAttributeMap() {
		Cursor cursor = mDataModel.getAllAppExtraInfo();
		HashMap<String, List<AppExtraAttribute>> map = new HashMap<String, List<AppExtraAttribute>>();
		final int cnIndex = cursor.getColumnIndex(AppExtraAttributeTable.COMPONENT_NAME);
		final int lockIdx = cursor.getColumnIndex(AppExtraAttributeTable.ISLOCK);
		final int newIdIdx = cursor.getColumnIndex(AppExtraAttributeTable.ISNEW);
		final int clickIdx = cursor.getColumnIndex(AppExtraAttributeTable.CLICK_OPEN_TIME);
		final int dataIdx = cursor.getColumnIndex(AppExtraAttributeTable.DATA);
		if (cursor.moveToFirst()) {
			do {
				AppExtraAttribute appExtra = null;
				String cName = cursor.getString(cnIndex);
				ComponentName cn = ComponentName.unflattenFromString(cName);
				AppItemInfo appInfo = mAppDataEngine.getAppItem(cn);
				if (appInfo != null) {
					appExtra = appInfo.getExtraAtturibute();
				} else {
					appExtra = new AppExtraAttribute(cn);
				}
				boolean isLock = cursor.getInt(lockIdx) == 1 ? true : false;
				boolean isNew = cursor.getInt(newIdIdx) == 1 ? true : false;
				long clickTime = cursor.getLong(clickIdx);
				String data = cursor.getString(dataIdx);
				String packageName = cn.getPackageName();
				List<AppExtraAttribute> list = map.get(packageName);
				if (list == null) {
					list = new ArrayList<AppExtraAttribute>();
					list.add(appExtra);
					map.put(packageName, list);
				} else {
					list.add(appExtra);
				}
				appExtra.setComponentName(cn);
				appExtra.setClickTime(clickTime);
				appExtra.setIsLock(isLock);
				appExtra.setIsNew(isNew);
				appExtra.setData(data);
			} while (cursor.moveToNext());
		}
		cursor.close();
		mAttuributeMap = map;
	}
	
	public void addAppExtraInfo(AppExtraAttribute info) {
		ComponentName cn = info.getComponentName();
		String pkg = cn.getPackageName();
		List<AppExtraAttribute> list = mAttuributeMap.get(pkg);
		if (list == null) {
			list = new ArrayList<AppExtraAttribute>();
			list.add(info);
			mAttuributeMap.put(pkg, list);
		} else {
			list.add(info);
		}
		try {
			mDataModel.addAppExtraInfo(info);
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addAppExtraInfos(List<AppExtraAttribute> infos) {
		try {
			mDataModel.beginTransaction();
			for (AppExtraAttribute info : infos) {
				addAppExtraInfo(info);
			}
			mDataModel.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			mDataModel.endTransaction();
		}
	}
	
	public void removeAppExtraInfo(AppExtraAttribute info) throws DatabaseException {
		ComponentName cn = info.getComponentName();
		String pkg = cn.getPackageName();
		List<AppExtraAttribute> list = mAttuributeMap.get(pkg);
		if (list == null) {
			//
		} else {
			list.remove(info);
			if (list.isEmpty()) {
				mAttuributeMap.remove(pkg);
			}
			mDataModel.removeAppExtraInfo(info);
		}
	}
	
	public void removeAppExtraInfos(List<AppExtraAttribute> infos) {
		try {
			mDataModel.beginTransaction();
			for (AppExtraAttribute info : infos) {
				removeAppExtraInfo(info);
			}
			mDataModel.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			mDataModel.endTransaction();
		}
	}
	
	public boolean updateAppExtraInfo(AppExtraAttribute info) {
		boolean ret = false;
		ComponentName cn = info.getComponentName();
		String pkg = cn.getPackageName();
		List<AppExtraAttribute> list = mAttuributeMap.get(pkg);
		if (list != null) {
			for (AppExtraAttribute appExtraAtturibute : list) {
				if (appExtraAtturibute.getComponentName().equals(cn)) {
					appExtraAtturibute.setClickTime(info.getClickTime());
					appExtraAtturibute.setIsLock(info.isLock());
					appExtraAtturibute.setIsNew(info.isNew());
					appExtraAtturibute.setData(info.getData());
					try {
						int row = mDataModel.updateAppExtraInfo(info);
						if (row > 0) {
							ret = true;
						}
					} catch (DatabaseException e) {
						ret = false;
					}
				}
			}
		}
		return ret;
	}
	
	public void updateAppExtraInfos(List<AppExtraAttribute> infos) {
		try {
			mDataModel.beginTransaction();
			for (AppExtraAttribute info : infos) {
				updateAppExtraInfo(info);
			}
			mDataModel.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			mDataModel.endTransaction();
		}
	}
	
	public AppExtraAttribute getAppExtraAtturibute(ComponentName cn) {
		String pkg = cn.getPackageName();
		List<AppExtraAttribute> list = mAttuributeMap.get(pkg);
		if (list != null) {
			for (AppExtraAttribute appExtraAtturibute : list) {
				if (appExtraAtturibute.getComponentName().equals(cn)) {
					return appExtraAtturibute;
				}
			}
		}
		return null;
	}

	public AppExtraAttribute getAppExtraAtturibute(Intent intent) {
		ComponentName cn = intent.getComponent();
		return getAppExtraAtturibute(cn);
	}
	
	public List<AppExtraAttribute> getAppExtraAtturibute(String pkg) {
		return mAttuributeMap.get(pkg);
	}
	
	public void saveAppExtraAtturibute(AppExtraAttribute info) {
		if (!updateAppExtraInfo(info)) {
			addAppExtraInfo(info);
		}
	}
}
