package com.go.util.graphics.effector.united;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.SparseArray;

import com.go.commomidentify.IGoLauncherClassName;
import com.jiubang.ggheart.data.BaseDataModel;
import com.jiubang.ggheart.data.PersistenceManager;
import com.jiubang.ggheart.data.Setting;
import com.jiubang.ggheart.data.tables.AppFuncSettingTable;
import com.jiubang.ggheart.data.tables.EffectorTable;

/**
 * 特效数据管理器
 * @author yejijiong
 *
 */
public class EffectorDataModel extends BaseDataModel {

	public EffectorDataModel(Context context) {
		super(context, PersistenceManager.DB_ANDROID_HEART);
	}
	
	/**
	 * 初始化特效Map中显示New的三个属性
	 * @param effectorInfoMap
	 */
	public void initEffectorInfoMapShowNewFlag(SparseArray<EffectorInfo> effectorInfoMap) {
		// 查询列数
		String columns[] = { EffectorTable.EFFECTOR_ID, EffectorTable.SCREEN_SETTING_SHOW_NEW,
				EffectorTable.EFFECT_TAB_SHOW_NEW, EffectorTable.APP_DRAWER_SETTING_SHOW_NEW };
		// 查询条件
		String where = EffectorTable.SCREEN_SETTING_SHOW_NEW + " = 1 or "
				+ EffectorTable.EFFECT_TAB_SHOW_NEW + " = 1 or "
				+ EffectorTable.APP_DRAWER_SETTING_SHOW_NEW + " = 1";
		Cursor cursor = mManager.query(EffectorTable.TABLE_NAME, columns, where, null, null);
		try {
			if (cursor != null && cursor.moveToFirst()) {
				int effectorIdIndex = cursor.getColumnIndexOrThrow(EffectorTable.EFFECTOR_ID);
				int screenSettingShowNewIndex = cursor
						.getColumnIndexOrThrow(EffectorTable.SCREEN_SETTING_SHOW_NEW);
				int effectTabShowNewIndex = cursor
						.getColumnIndexOrThrow(EffectorTable.EFFECT_TAB_SHOW_NEW);
				int appDrawerSettingShowNewIndex = cursor
						.getColumnIndexOrThrow(EffectorTable.APP_DRAWER_SETTING_SHOW_NEW);
				do {
					int effectorId = cursor.getInt(effectorIdIndex);
					EffectorInfo effectorInfo = effectorInfoMap.get(effectorId);
					if (effectorInfo != null) {
						effectorInfo.mIsScreenSettingShowNew = cursor
								.getInt(screenSettingShowNewIndex) == 1 ? true : false;
						effectorInfo.mIsEffectTabShowNew = cursor.getInt(effectTabShowNewIndex) == 1
								? true
								: false;
						effectorInfo.mIsAppDrawerSettingShowNew = cursor
								.getInt(appDrawerSettingShowNewIndex) == 1 ? true : false;
					}
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
		} finally {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
		}
	}
	
	/**
	 * 更新特效数据
	 * @param infoList
	 */
	public void updateEffectorInfoList(ArrayList<EffectorInfo> infoList) {
		if (infoList == null || infoList.size() < 1) {
			return;
		}
		mManager.beginTransaction();
		try {
			for (EffectorInfo info : infoList) {
				if (info != null) {
					ContentValues values = new ContentValues();
					values.put(EffectorTable.SCREEN_SETTING_SHOW_NEW, info.mIsScreenSettingShowNew ? 1 : 0);
					values.put(EffectorTable.EFFECT_TAB_SHOW_NEW, info.mIsEffectTabShowNew ? 1 : 0);
					values.put(EffectorTable.APP_DRAWER_SETTING_SHOW_NEW,
							info.mIsAppDrawerSettingShowNew ? 1 : 0);
					String whereStr = EffectorTable.EFFECTOR_ID + " = " + info.mEffectorId;
					mManager.update(EffectorTable.TABLE_NAME, values, whereStr, null);
				}
			}
			mManager.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mManager.endTransaction();
		}
	}
	
	/**
	 * xml文件与数据库进行比对，查找是否有新加特效，若有，将新特效加入数据库
	 * @return
	 */
	public boolean compareHasNewEffector(SparseArray<EffectorInfo> effectorInfoMap) {
		boolean flag = false;
		ArrayList<Integer> dbIdList = new ArrayList<Integer>();
		ArrayList<Integer> newIdList = new ArrayList<Integer>();
		// 查询列数
		String columns[] = { EffectorTable.EFFECTOR_ID };
		Cursor cursor = mManager.query(EffectorTable.TABLE_NAME, columns, null, null, null);
		try {
			if (cursor != null && cursor.moveToFirst()) {
				int effectorIdIndex = cursor.getColumnIndexOrThrow(EffectorTable.EFFECTOR_ID);
				do {
					int effectorId = cursor.getInt(effectorIdIndex);
					dbIdList.add(effectorId);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
		} finally {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
		}
		for (int i = 0; i < effectorInfoMap.size(); i++) { // 比较是否有新特效
			int id = effectorInfoMap.keyAt(i);
			if (!dbIdList.contains(id)) { // 新特效
				newIdList.add(id);
			}
		}
		if (newIdList.size() > 0) { // 有新加的特效
			mManager.beginTransaction();
			try {
				for (int id : newIdList) {
					ContentValues values = new ContentValues();
					values.put(EffectorTable.EFFECTOR_ID, id);
					values.put(EffectorTable.SCREEN_SETTING_SHOW_NEW, 1);
					values.put(EffectorTable.EFFECT_TAB_SHOW_NEW, 1);
					values.put(EffectorTable.APP_DRAWER_SETTING_SHOW_NEW, 1);
					mManager.insert(EffectorTable.TABLE_NAME, values);
				}
				mManager.setTransactionSuccessful();
				flag = true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mManager.endTransaction();
			}
		}
		return flag;
	}
	
	/**
	 * 映射旧的功能表特效ID到新的特效ID
	 * @return
	 */
	public static void mappingNewEffectorIdForAppDrawer(SQLiteDatabase db) {
		int newId = mappingNewEffectorIdForAppDrawer(db, Setting.ICONEFFECT);
		if (newId == IEffectorIds.EFFECTOR_TYPE_RANDOM_CUSTOM) { // 用户选择的是随机自定义
			String tableName = AppFuncSettingTable.TABLENAME;
			String columns[] = { AppFuncSettingTable.SETTINGVALUE, };
			String selection = AppFuncSettingTable.PKNAME + "='" + IGoLauncherClassName.DEFAULT_THEME_PACKAGE + "' and "
					+ AppFuncSettingTable.SETTINGKEY + "=" + Setting.APPICONCUSTOMEFFECTSETTING;
			Cursor cursor = db.query(tableName, columns, selection, null, null, null, null);
			try {
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						int valueIndex = cursor.getColumnIndex(AppFuncSettingTable.SETTINGVALUE);
						String oldCustomEffectsStr = cursor.getString(valueIndex);
						StringBuffer newCustomEffectsStr = new StringBuffer();
						if (oldCustomEffectsStr != null) {
							String[] items = oldCustomEffectsStr.split(";");
							if (items != null) {
								for (int i = 0; i < items.length; i++) {
									try {
										int id = getNewEffectorIdForAppDrawer(Integer.parseInt(items[i]));
										newCustomEffectsStr.append(id + ";");
									} catch (NumberFormatException e) {
										e.printStackTrace();
									}
								}
								ContentValues values = new ContentValues();
								values.put(AppFuncSettingTable.SETTINGVALUE, newCustomEffectsStr.toString());
								db.update(AppFuncSettingTable.TABLENAME, values, selection, null);
							}
						}
					}
				}
			} catch (SQLiteException e) {
				e.printStackTrace();
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
		} else {
			mappingNewEffectorIdForAppDrawer(db, Setting.ICON_EFFECT_FOR_3D);
		}
	}
	
	private static int mappingNewEffectorIdForAppDrawer(SQLiteDatabase db, int settingKey) {
		int oldId = 0;
		int newId = 0;
		String tableName = AppFuncSettingTable.TABLENAME;
		String columns[] = { AppFuncSettingTable.SETTINGVALUE, };
		String selection = AppFuncSettingTable.PKNAME + "='" + IGoLauncherClassName.DEFAULT_THEME_PACKAGE
				+ "' and " + AppFuncSettingTable.SETTINGKEY + "=" + settingKey;
		Cursor cursor = db.query(tableName, columns, selection, null, null, null, null);
		try {
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					int valueIndex = cursor.getColumnIndex(AppFuncSettingTable.SETTINGVALUE);
					oldId = Integer.parseInt(cursor.getString(valueIndex));
					newId = getNewEffectorIdForAppDrawer(oldId);
				}
			}
			if (oldId != newId) {
				ContentValues values = new ContentValues();
				values.put(AppFuncSettingTable.SETTINGVALUE, String.valueOf(newId));
				db.update(AppFuncSettingTable.TABLENAME, values, selection, null);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return newId;
	}
	
	private final static int NEW_GRID_EFFECTOR_START_INDEX = 50;
	private final static int SCREEN_NEW_EFFECTOR_INDEX = 17;
	private final static int GRIDSCREEN_EFFECTOR_COUNT_IN_MENU = 7;
	private final static int EFFECTOR_TYPE_DEFAULT = 0;
	private final static int GRID_EFFECTOR_TYPE_BINARY_STAR = 1;
	private final static int GRID_EFFECTOR_TYPE_CHARIOT = 2;
	private final static int GRID_EFFECTOR_TYPE_SHUTTER = 3;
	private final static int GRID_EFFECTOR_TYPE_CHORD = 4;
	private final static int GRID_EFFECTOR_TYPE_CYLINDER = 5;
	private final static int GRID_EFFECTOR_TYPE_SPHERE = 6;
	/**
	 * 获取新的功能表特效ID
	 * @param oldId 旧的特效ID
	 * @return
	 */
	private static int getNewEffectorIdForAppDrawer(int oldId) {
		if (oldId >= EFFECTOR_TYPE_DEFAULT && oldId < GRIDSCREEN_EFFECTOR_COUNT_IN_MENU
				|| oldId >= NEW_GRID_EFFECTOR_START_INDEX) {
			switch (oldId) {
				case GRID_EFFECTOR_TYPE_BINARY_STAR :
					return 11;
				case GRID_EFFECTOR_TYPE_CHARIOT :
					return 12;
				case GRID_EFFECTOR_TYPE_SHUTTER :
					return 13;
				case GRID_EFFECTOR_TYPE_CHORD :
					return 14;
				case GRID_EFFECTOR_TYPE_CYLINDER :
					return 15;
				case GRID_EFFECTOR_TYPE_SPHERE :
					return 16;
				default :
					return 0;
			}
		} else if (oldId >= GRIDSCREEN_EFFECTOR_COUNT_IN_MENU && oldId < SCREEN_NEW_EFFECTOR_INDEX) {
			return oldId - GRIDSCREEN_EFFECTOR_COUNT_IN_MENU + 1;
		} 
		return oldId;
	}
}
