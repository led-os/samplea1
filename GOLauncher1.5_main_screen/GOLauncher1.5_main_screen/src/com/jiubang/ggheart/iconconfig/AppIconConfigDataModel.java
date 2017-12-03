package com.jiubang.ggheart.iconconfig;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.go.util.StringUtil;
import com.go.util.log.LogUnit;
import com.jiubang.ggheart.data.DatabaseHelper;
import com.jiubang.ggheart.data.model.DataModel;
/**
 * 屏幕图标配置功能模块-DB操作类
 * @author caoyaming
 *
 */
public class AppIconConfigDataModel extends DataModel {
	//LOG_TAG
	private static final String LOG_TAG = AppIconConfigDataModel.class.getName();
	//当前类对象
	private static AppIconConfigDataModel sThemeIconDataModel = null;
	//DB操作对象
	private DatabaseHelper mSQLiteOpenHelper = null;
	private AppIconConfigDataModel(Context context) {
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
	public static AppIconConfigDataModel getInstance(Context context) {
		if (sThemeIconDataModel == null) {
			sThemeIconDataModel = new AppIconConfigDataModel(context);
		}
		return sThemeIconDataModel;
	}
	/**
	 * 根据查询条件返回相应的数据
	 * @return
	 */
	public List<AppIconConfigBean> queryAppIconConfigData(AppIconConfigBean whereAppIconConfigBean) {
		if (mSQLiteOpenHelper == null) {
			LogUnit.e(LOG_TAG, "query app icon config data error!(mSQLiteOpenHelper is null)");
			return null;
		}
		List<AppIconConfigBean> appIconConfigDataList = new ArrayList<AppIconConfigBean>();
		Cursor cursor = null;
		try {
			StringBuffer whereBuffer = new StringBuffer(" 1=1");
			List<String> selectionArgList = new ArrayList<String>();
			if (whereAppIconConfigBean != null) {
				//图标对应的ComponentName
				if (!TextUtils.isEmpty(whereAppIconConfigBean.getmComponentName())) {
					whereBuffer.append(" AND " + AppIconConfigInfoTable.APP_COMPONENTNAME + " = ?");
					selectionArgList.add(whereAppIconConfigBean.getmComponentName());
				}
				//开始时间
				if (whereAppIconConfigBean.getmValidStartTime() > 0) {
					whereBuffer.append(" AND " + AppIconConfigInfoTable.VALID_START_TIME + " >= ?");
					selectionArgList.add(String.valueOf(whereAppIconConfigBean.getmValidStartTime()));
				}
				//结束时间
				if (whereAppIconConfigBean.getmValidStartTime() > 0) {
					whereBuffer.append(" AND " + AppIconConfigInfoTable.VALID_END_TIME + " <= ?");
					selectionArgList.add(String.valueOf(whereAppIconConfigBean.getmValidEndTime()));
				}
				//未读数字
				if (whereAppIconConfigBean.getmShowNumber() >= 0) {
					whereBuffer.append(" AND " + AppIconConfigInfoTable.SHOW_NUMBER + " > ?");
					selectionArgList.add(String.valueOf(whereAppIconConfigBean.getmShowNumber()));
				}
				
			}
			cursor = mSQLiteOpenHelper.query(
					AppIconConfigInfoTable.TABLE_NAME, new String[] {
							AppIconConfigInfoTable.APP_COMPONENTNAME,
							AppIconConfigInfoTable.SHOW_NUMBER,
							AppIconConfigInfoTable.START_VERSION,
							AppIconConfigInfoTable.END_VERSION,
							AppIconConfigInfoTable.VALID_START_TIME,
							AppIconConfigInfoTable.VALID_END_TIME },
					whereBuffer.toString(),
					listConvertStringArray(selectionArgList), null);
			if (null != cursor && cursor.moveToFirst()) {
				AppIconConfigBean appIconConfigBean = null;
				do {
					appIconConfigBean = new AppIconConfigBean();
					appIconConfigBean.setmComponentName(cursor.getString(cursor.getColumnIndex(AppIconConfigInfoTable.APP_COMPONENTNAME)));
					appIconConfigBean.setmShowNumber(cursor.getInt(cursor.getColumnIndex(AppIconConfigInfoTable.SHOW_NUMBER)));
					appIconConfigBean.setmStartVersion(cursor.getString(cursor.getColumnIndex(AppIconConfigInfoTable.START_VERSION)));
					appIconConfigBean.setmEndVersion(cursor.getString(cursor.getColumnIndex(AppIconConfigInfoTable.END_VERSION)));
					appIconConfigBean.setmValidStartTime(StringUtil.toLong(cursor.getString(cursor.getColumnIndex(AppIconConfigInfoTable.VALID_START_TIME)), 0L));
					appIconConfigBean.setmValidEndTime(StringUtil.toLong(cursor.getString(cursor.getColumnIndex(AppIconConfigInfoTable.VALID_END_TIME)), 0L));
					appIconConfigDataList.add(appIconConfigBean);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return appIconConfigDataList;
	}
	/**
	 * 向屏幕图标配置信息表中添加记录
	 * 
	 * @param appIconConfigDataList 屏幕图标配置数据列表
	 */
	public boolean insertAppIconConfigData(List<AppIconConfigBean> appIconConfigDataList) {
		if (appIconConfigDataList == null || appIconConfigDataList.size() < 1) {
			return false;
		}
		if (mSQLiteOpenHelper == null) {
			Log.e(LOG_TAG, "insert App icon config data error!(mSQLiteOpenHelper is null)");
			return false;
		}
		try {
			//启动事物
			mSQLiteOpenHelper.beginTransaction();
			AppIconConfigBean appIconConfigBean = null;
			ContentValues values = null;
			for (int index = 0; index < appIconConfigDataList.size(); index++) {
				appIconConfigBean = appIconConfigDataList.get(index);
				if (TextUtils.isEmpty(appIconConfigBean.getmComponentName())) {
					continue;
				}
				//ContentValues
				values = new ContentValues();
				//应用ComponentName
				values.put(AppIconConfigInfoTable.APP_COMPONENTNAME, appIconConfigBean.getmComponentName());
				//显示的未读数字
				values.put(AppIconConfigInfoTable.SHOW_NUMBER, appIconConfigBean.getmShowNumber());
				//开始版本(如:4.14)
				values.put(AppIconConfigInfoTable.START_VERSION, appIconConfigBean.getmStartVersion());
				//结束版本(如:4.15)
				values.put(AppIconConfigInfoTable.END_VERSION, appIconConfigBean.getmEndVersion());
				//开始时间(yyyyMMddHHmmss)
				values.put(AppIconConfigInfoTable.VALID_START_TIME, appIconConfigBean.getmValidStartTime());
				//结束时间(yyyyMMddHHmmss)
				values.put(AppIconConfigInfoTable.VALID_END_TIME, appIconConfigBean.getmValidEndTime());
				//先删除DB中该Icon数据再添加,防止存在重复的记录.
				mSQLiteOpenHelper.delete(AppIconConfigInfoTable.TABLE_NAME, " "
						+ AppIconConfigInfoTable.APP_COMPONENTNAME + " = ?", new String[] {
						appIconConfigBean.getmComponentName() });
				//添加新记录
				mSQLiteOpenHelper.insert(AppIconConfigInfoTable.TABLE_NAME, values);
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
	 * 修改屏幕图标配置信息表记录
	 * 
	 * @param appIconConfigBean 要修改的数据对象
	 */
	public boolean updateAppIconConfigData(AppIconConfigBean appIconConfigBean) {
		if (appIconConfigBean == null || TextUtils.isEmpty(appIconConfigBean.getmComponentName())) {
			return false;
		}
		if (mSQLiteOpenHelper == null) {
			Log.e(LOG_TAG, "update App icon config data error!(mSQLiteOpenHelper is null)");
			return false;
		}
		try {
			ContentValues values = new ContentValues();
			//显示的数字
			if (appIconConfigBean.getmShowNumber() >= 0) {
				values.put(AppIconConfigInfoTable.SHOW_NUMBER, appIconConfigBean.getmShowNumber());
			}
			if (values.size() > 0) {
				mSQLiteOpenHelper.update(AppIconConfigInfoTable.TABLE_NAME, values, " " + AppIconConfigInfoTable.APP_COMPONENTNAME + " = ?", new String[] { appIconConfigBean.getmComponentName() });
			}
			return true;
		} catch (Exception e) {
			LogUnit.e(LOG_TAG, "insert theme icon data Exception!", e);
			return false;
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
