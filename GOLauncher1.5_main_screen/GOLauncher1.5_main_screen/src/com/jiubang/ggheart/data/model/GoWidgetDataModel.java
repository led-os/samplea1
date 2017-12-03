package com.jiubang.ggheart.data.model;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.data.BaseDataModel;
import com.jiubang.ggheart.data.PersistenceManager;
import com.jiubang.ggheart.data.info.GoWidgetBaseInfo;
import com.jiubang.ggheart.data.tables.GoWidgetTable;
import com.jiubang.ggheart.data.tables.PartToScreenTable;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 *
 */
public class GoWidgetDataModel extends BaseDataModel {
	private static boolean sNeedReplaceWidget = true;
	private static boolean sHasReplaceAllGoStoreWidget = false;
	
	public GoWidgetDataModel(Context context) {
		super(context, PersistenceManager.DB_ANDROID_HEART);
		checkSharedPreferences();
	}
	
	private void checkSharedPreferences() {
		PreferencesManager sharedPreferences = new PreferencesManager(mContext,
				IPreferencesIds.USERTUTORIALCONFIG, Context.MODE_PRIVATE);
		sNeedReplaceWidget = sharedPreferences.getBoolean(LauncherEnv.ALREADY_REPLACE_OLD_WIDGET,
				true);
		sHasReplaceAllGoStoreWidget = sharedPreferences.getBoolean(
				LauncherEnv.ALREADY_REPLACE_ALL_GOSTORE_WIDGET, false);
	}

	// Go widget
	public Cursor getAllGoWidget() {
		return mManager.query(GoWidgetTable.TABLENAME, null, null, null, null);
	}

	public ArrayList<GoWidgetBaseInfo> getAllGoWidgetInfos() {
		final Cursor cursor = getAllGoWidget();
		ArrayList<GoWidgetBaseInfo> list = new ArrayList<GoWidgetBaseInfo>();
		if (cursor == null) {
			return list;
		}
		try {
			if (cursor.moveToFirst()) {
				do {
					GoWidgetBaseInfo info = new GoWidgetBaseInfo();
					try {
						info.readObject(cursor, GoWidgetTable.TABLENAME);
						if (sNeedReplaceWidget || !sHasReplaceAllGoStoreWidget) {
							replaceGoStoreWidget(info);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					list.add(info);
				} while (cursor.moveToNext());
			}
			if (sNeedReplaceWidget || !sHasReplaceAllGoStoreWidget) {
				PreferencesManager sharedPreferences = new PreferencesManager(mContext,
						IPreferencesIds.USERTUTORIALCONFIG, Context.MODE_PRIVATE);
				sharedPreferences.putBoolean(LauncherEnv.ALREADY_REPLACE_OLD_WIDGET, false);
				sharedPreferences.putBoolean(LauncherEnv.ALREADY_REPLACE_ALL_GOSTORE_WIDGET, true);
				sharedPreferences.commit();
			}
		} catch (Exception e) {
			Log.e("gowidget", "getAllGoWidgetInfos error");
		} finally {
			cursor.close();
		}
		return list;
	}

	public void deleteGoWidget(int widgetid) {
		try {
			String condition = GoWidgetTable.WIDGETID + " = " + widgetid;
			mManager.delete(GoWidgetTable.TABLENAME, condition, null);
			condition = null;
		} catch (Exception e) {
			Log.i("gowidget", "deleteGoWidget error, widget id = " + widgetid);
		}
	}
	
	/**
	 * 修改GoWidget数据
	 * @param condition 修改条件
	 * @param values 修改的数据
	 */
	public void updateGoWidgetInfo(String condition, ContentValues values) {
		try {
			if (TextUtils.isEmpty(condition) || values == null) {
				return;
			}
			mManager.update(GoWidgetTable.TABLENAME, values, condition, null);
			condition = null;
		} catch (Exception e) {
			Log.i("gowidget", "update gowidget info error, condition  = " + condition);
		}
	}

	public void deleteGoWidget(String pkgName) {
		try {
			String condition = GoWidgetTable.PACKAGE + " = '" + pkgName + "'";
			mManager.delete(GoWidgetTable.TABLENAME, condition, null);
			condition = null;
		} catch (Exception e) {
			Log.i("gowidget", "deleteGoWidget error, pkgName  = " + pkgName);
		}
	}

	public void addGoWidget(GoWidgetBaseInfo info) {
		try {
			// 排序条件
			String where = GoWidgetTable.WIDGETID + " = " + info.mWidgetId;
			Cursor cursor = mManager.query(GoWidgetTable.TABLENAME, null, where, null, null);
			boolean isExist = false;
			if (cursor != null) {
				try {
					isExist = cursor.getCount() > 0;
				} finally {
					cursor.close();
				}
			}

			if (!isExist) {
				ContentValues values = new ContentValues();
				info.writeObject(values, GoWidgetTable.TABLENAME);
				mManager.insert(GoWidgetTable.TABLENAME, values);
				values = null;
			}
		} catch (Exception e) {
			Log.i("gowidget", "addGoWidget error, widget info  = " + info.toString());
		}
	}

	public void updateGoWidgetTheme(GoWidgetBaseInfo info) {
		// update gowidget set theme='test', type=2 where widgetid = -102
		try {
			String sqlString = "update " + GoWidgetTable.TABLENAME + " set " + GoWidgetTable.THEME
					+ " = '" + info.mTheme + "', " + GoWidgetTable.THEMEID + " = " + info.mThemeId
					+ " where " + GoWidgetTable.WIDGETID + " = " + info.mWidgetId + ";";

			mManager.exec(sqlString);
			sqlString = null;
		} catch (Exception e) {
			Log.i("gowidget", "update theme failed, widgetid = " + info.mWidgetId + " theme = "
					+ info.mTheme);
		}
	}

	public static final String NAME_GO_STORE_43 = "gostorewidget";
	public static final String NAME_GO_STORE_42 = "gostorewidget42";
	public static final String NAME_GO_STORE_41_NEW = "gostore41widget";
	public static final String NAME_GO_STORE_41 = "gostorewidget41";
	public static final String NAME_APPGAME_41 = "appgamewidget41";
	/***
	 * 替换GOStroe 4*3和4*2   成新的4*1的 
	 */
	private void replaceGoStoreWidget(GoWidgetBaseInfo info) {
		if (info == null || info.mLayout == null) {
			return;
		}
		if (info.mLayout.equals(NAME_GO_STORE_43) || info.mLayout.equals(NAME_GO_STORE_42)
				|| info.mLayout.equals(NAME_GO_STORE_41_NEW)
				|| info.mLayout.equals(NAME_GO_STORE_41)) {
			info.mLayout = NAME_APPGAME_41;
			try {
				String sqlString = "update " + GoWidgetTable.TABLENAME + " set "
						+ GoWidgetTable.LAYOUT + " = '" + NAME_APPGAME_41 + "', "
						+ GoWidgetTable.THEME + " = '" + "" + "', " + GoWidgetTable.THEMEID + " = "
						+ "-1" + " where " + GoWidgetTable.WIDGETID + " = " + info.mWidgetId + ";";
				mManager.exec(sqlString);
				sqlString = null;
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				String sqlString = "update " + PartToScreenTable.TABLENAME + " set "
						+ PartToScreenTable.SPANX + " = " + 4 + ", " + PartToScreenTable.SPANY
						+ " = " + 1 + " where " + PartToScreenTable.WIDGETID + " = "
						+ info.mWidgetId + ";";
				mManager.exec(sqlString);
				sqlString = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
