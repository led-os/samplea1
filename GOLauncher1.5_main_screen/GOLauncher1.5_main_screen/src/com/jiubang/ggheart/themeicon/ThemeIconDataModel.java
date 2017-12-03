package com.jiubang.ggheart.themeicon;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.go.util.log.LogUnit;
import com.jiubang.ggheart.data.DatabaseHelper;
import com.jiubang.ggheart.data.model.DataModel;
/**
 * 主题Icon功能模块-DB操作类
 * @author caoyaming
 *
 */
public class ThemeIconDataModel extends DataModel {
	//LOG_TAG
	private static final String LOG_TAG = ThemeIconDataModel.class.getName();
	//当前类对象
	private static ThemeIconDataModel sThemeIconDataModel = null;
	//DB操作对象
	private DatabaseHelper mSQLiteOpenHelper = null;
	private ThemeIconDataModel(Context context) {
		super(context);
		if (mDataProvider != null && mDataProvider.getDatabaseHelper() != null && mDataProvider.getDatabaseHelper() instanceof DatabaseHelper) {
			mSQLiteOpenHelper = (DatabaseHelper) mDataProvider.getDatabaseHelper();
		}
	}
	/**
	 * 获取当前类单例对象
	 * @param context
	 * @return
	 */
	public static ThemeIconDataModel getInstance(Context context) {
		if (sThemeIconDataModel == null) {
			sThemeIconDataModel = new ThemeIconDataModel(context);
		}
		return sThemeIconDataModel;
	}
	/**
	 * 根据查询条件返回相应的数据
	 * @return
	 */
	public ArrayList<ThemeIconDataBean> queryThemeIconData(ThemeIconDataBean whereThemeIconDataBean) {
		if (mSQLiteOpenHelper == null) {
			LogUnit.e(LOG_TAG, "query theme icon data error!(mSQLiteOpenHelper is null)");
			return null;
		}
		ArrayList<ThemeIconDataBean> themeIconDataList = new ArrayList<ThemeIconDataBean>();
		Cursor cursor = null;
		try {
			StringBuffer whereBuffer = new StringBuffer(" 1=1");
			List<String> selectionArgList = new ArrayList<String>();
			if (whereThemeIconDataBean != null) {
				//主题PackageName
				if (!TextUtils.isEmpty(whereThemeIconDataBean.mThemePackageName)) {
					whereBuffer.append(" AND " + ThemeIconInfoTable.THEME_PACKAGENAME + " = ?");
					selectionArgList.add(whereThemeIconDataBean.mThemePackageName);
				}
				//应用ComponentName
				if (!TextUtils.isEmpty(whereThemeIconDataBean.mComponentName)) {
					whereBuffer.append(" AND " + ThemeIconInfoTable.APP_COMPONENTNAME + " = ?");
					selectionArgList.add(whereThemeIconDataBean.mComponentName);
				}
			}
			cursor = mSQLiteOpenHelper.query(ThemeIconInfoTable.TABLE_NAME, new String[] {ThemeIconInfoTable.THEME_PACKAGENAME,
					ThemeIconInfoTable.APP_COMPONENTNAME, ThemeIconInfoTable.DOWNLOAD_ICON_URL,
					ThemeIconInfoTable.ICON_UPDATE_TIME, ThemeIconInfoTable.ICON_SAVE_PATH}, whereBuffer.toString(),
					listConvertStringArray(selectionArgList), null);
			if (null != cursor && cursor.moveToFirst()) {
				ThemeIconDataBean themeIconDataBean = null;
				do {
					themeIconDataBean = new ThemeIconDataBean();
					themeIconDataBean.mThemePackageName = cursor.getString(cursor.getColumnIndex(ThemeIconInfoTable.THEME_PACKAGENAME));
					themeIconDataBean.mComponentName = cursor.getString(cursor.getColumnIndex(ThemeIconInfoTable.APP_COMPONENTNAME));
					themeIconDataBean.mAppIconUrl = cursor.getString(cursor.getColumnIndex(ThemeIconInfoTable.DOWNLOAD_ICON_URL));
					themeIconDataBean.mIconUpdateData = cursor.getString(cursor.getColumnIndex(ThemeIconInfoTable.ICON_UPDATE_TIME));
					themeIconDataBean.mIconSavePath = cursor.getString(cursor.getColumnIndex(ThemeIconInfoTable.ICON_SAVE_PATH));
					themeIconDataList.add(themeIconDataBean);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return themeIconDataList;
	}
	/**
	 * 根据查询条件返回相应的数据
	 * @return
	 */
	public int queryThemeIconDataCount(String themePackageName) {
		if (TextUtils.isEmpty(themePackageName)) {
			return -1;
		}
		if (mSQLiteOpenHelper == null) {
			LogUnit.e(LOG_TAG, "query theme icon data count error!(mSQLiteOpenHelper is null)");
			return -1;
		}
		Cursor cursor = null;
		try {
			//查询数据
			cursor = mSQLiteOpenHelper.query(ThemeIconInfoTable.TABLE_NAME, new String[] {ThemeIconInfoTable.APP_COMPONENTNAME}, " " + ThemeIconInfoTable.THEME_PACKAGENAME + " = ?", new String[] {themePackageName}, null);
			if (cursor == null) {
				return -1;
			}
			return cursor.getCount();
		} catch (Exception e) {
			LogUnit.e(LOG_TAG, "query theme icon data Exception!", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return -1;
	}
	
	
	/**
	 * 向主题Icon信息表中添加记录
	 * 
	 * @param themeIconDataList 主题Icon数据列表
	 */
	public boolean insertThemeIconData(List<ThemeIconDataBean> themeIconDataList) {
		if (themeIconDataList == null || themeIconDataList.size() < 1) {
			return false;
		}
		if (mSQLiteOpenHelper == null) {
			Log.e(LOG_TAG, "insert theme icon data error!(mSQLiteOpenHelper is null)");
			return false;
		}
		try {
			//启动事物
			mSQLiteOpenHelper.beginTransaction();
			ThemeIconDataBean themeIconDataBean = null;
			ContentValues values = null;
			for (int index = 0; index < themeIconDataList.size(); index++) {
				themeIconDataBean = themeIconDataList.get(index);
				if (TextUtils.isEmpty(themeIconDataBean.mThemePackageName)) {
					continue;
				}
				//ContentValues
				values = new ContentValues();
				//主题包名
				values.put(ThemeIconInfoTable.THEME_PACKAGENAME, themeIconDataBean.mThemePackageName);
				//Icon对应的应用ComponentName
				values.put(ThemeIconInfoTable.APP_COMPONENTNAME, themeIconDataBean.mComponentName);
				//Icon图片下载地址
				values.put(ThemeIconInfoTable.DOWNLOAD_ICON_URL, themeIconDataBean.mAppIconUrl);
				//Icon更新时间
				values.put(ThemeIconInfoTable.ICON_UPDATE_TIME, themeIconDataBean.mIconUpdateData);
				//Icon资源存放目录(sdcard)
				values.put(ThemeIconInfoTable.ICON_SAVE_PATH, themeIconDataBean.mIconSavePath);
				//先删除DB中该Icon数据再添加,防止存在重复的记录.
				mSQLiteOpenHelper.delete(ThemeIconInfoTable.TABLE_NAME, " "
						+ ThemeIconInfoTable.THEME_PACKAGENAME + " = ? AND "
						+ ThemeIconInfoTable.APP_COMPONENTNAME + " = ?", new String[] {
						themeIconDataBean.mThemePackageName, themeIconDataBean.mComponentName });
				//添加新记录
				mSQLiteOpenHelper.insert(ThemeIconInfoTable.TABLE_NAME, values);
			}
			//设置操作成功
			mSQLiteOpenHelper.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			LogUnit.e(LOG_TAG, "insert theme icon data Exception!", e);
			return false;
		} finally {
			if (mSQLiteOpenHelper != null) {
				//提交事物
				mSQLiteOpenHelper.endTransaction();
			}
		}
	}
	/**
	 * 把List<String>转换为String[]
	 * 
	 * @param paramList
	 * @return
	 */
	public String[] listConvertStringArray(List<String> paramList) {
		if (paramList == null || paramList.size() < 0) {
			return null;
		}
		String[] paramArray = new String[paramList.size()];
		for (int index = 0; index < paramList.size(); index++) {
			paramArray[index] = paramList.get(index);
		}
		return paramArray;
	}
}
