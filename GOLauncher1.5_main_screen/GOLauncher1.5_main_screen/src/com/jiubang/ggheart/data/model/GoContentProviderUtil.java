package com.jiubang.ggheart.data.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.go.commomidentify.IGoLauncherClassName;
import com.jiubang.ggheart.data.DataProvider;
import com.jiubang.ggheart.data.Setting;
import com.jiubang.ggheart.data.ContentProvider.GoContentProvider;
import com.jiubang.ggheart.data.tables.ScreenStyleConfigTable;
import com.jiubang.ggheart.data.theme.ThemeManager;
/**
 * 
 * <br>类描述:屏幕风格的数据操作工具类
 * <br>功能详细描述:由于多进程问题单独拿出来使用ContentResolver
 * 
 * @author  rongjinsong
 * @date  [2013-8-21]
 */
public class GoContentProviderUtil {

	/**
	 * 添加屏幕个性搭配设置项
	 * 
	 * @param packageName
	 * @param value
	 */
	public static void addScreenStyleSetting(Context context, final String packageName) {
		if (null == packageName) {
			return;
		}
		
		ContentValues value = new ContentValues();
		value.put(ScreenStyleConfigTable.THEMEPACKAGE, packageName);
		value.put(ScreenStyleConfigTable.ICONSTYLEPACKAGE, packageName);
		value.put(ScreenStyleConfigTable.FOLDERSTYLEPACKAGE, packageName);
		value.put(ScreenStyleConfigTable.GGMENUPACKAGE, packageName);
		value.put(ScreenStyleConfigTable.INDICATOR, packageName);

		addScreenStyleSetting(context, packageName, value);
	}

	public static void addScreenStyleSetting(Context context, final String packageName,
			final ContentValues value) {
		String selection = ScreenStyleConfigTable.THEMEPACKAGE + " = '" + packageName + "'";
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(GoContentProvider.CONTENT_SCREENSTYLE_URI, null, selection,
				null, null);
		boolean b = false;
		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {
					b = true;
				}
			} finally {
				cursor.close();
			}
		}

		if (!b) {
			try {
				resolver.insert(GoContentProvider.CONTENT_SCREENSTYLE_URI, value);
			} catch (Exception e) {
				DataProvider.getInstance(context).addScreenStyleSetting(packageName, value);
			}
		}
	}

	/**
	 * 更新个性搭配设置信息
	 * 
	 * @param item
	 * @param value
	 */
	public static void updateScreenStyleSetting(Context context, final String packageName,
			final int item, final String value) {
		String columnName = null;
		switch (item) {
			case Setting.THEMEPACKAGE :
				columnName = ScreenStyleConfigTable.THEMEPACKAGE;
				break;
			case Setting.ICONSTYLEPACKAGE :
				columnName = ScreenStyleConfigTable.ICONSTYLEPACKAGE;
				break;
			case Setting.FOLDERSTYLEPACKAGE :
				columnName = ScreenStyleConfigTable.FOLDERSTYLEPACKAGE;
				break;
			case Setting.GGMENUPACKAGE :
				columnName = ScreenStyleConfigTable.GGMENUPACKAGE;
				break;
			case Setting.INDICATOR :
				columnName = ScreenStyleConfigTable.INDICATOR;
				break;
			default :
				break;
		}

		ContentValues values = new ContentValues();
		values.put(columnName, value);
		String selection = ScreenStyleConfigTable.THEMEPACKAGE + " = '" + packageName + "'";
		ContentResolver resolver = context.getContentResolver();
		resolver.update(GoContentProvider.CONTENT_SCREENSTYLE_URI, values, selection, null);
		values = null;
	}

	public static String getIndicatorStyle(Context context) {
		String str = null;
		// 其他主题
		str = getScreenStyleSetting(context,
				ThemeManager.getInstance(context).getCurThemePackage(), Setting.INDICATOR);

		if (null == str) {
			return IGoLauncherClassName.DEFAULT_THEME_PACKAGE;
		}

		return str;
	}

	/**
	 * 获取screenstyle设置信息
	 * 
	 * @param settingItem
	 * @return 返回对应设置项的值
	 */
	public static String getScreenStyleSetting(Context context, final String packageName, int item) {
		String columnName = null;
		switch (item) {
			case Setting.THEMEPACKAGE :
				columnName = ScreenStyleConfigTable.THEMEPACKAGE;
				break;
			case Setting.ICONSTYLEPACKAGE :
				columnName = ScreenStyleConfigTable.ICONSTYLEPACKAGE;
				break;
			case Setting.FOLDERSTYLEPACKAGE :
				columnName = ScreenStyleConfigTable.FOLDERSTYLEPACKAGE;
				break;
			case Setting.GGMENUPACKAGE :
				columnName = ScreenStyleConfigTable.GGMENUPACKAGE;
				break;
			case Setting.INDICATOR :
				columnName = ScreenStyleConfigTable.INDICATOR;
				break;
			default :
				break;
		}

		Cursor cursor = getScreenStyleSetting(context, packageName, columnName);
		String value = null;
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					int valueIndex = cursor.getColumnIndex(columnName);
					value = cursor.getString(valueIndex);
				}
			} catch (SQLiteException e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return value;
	}

	public static Cursor getScreenStyleSetting(Context context, final String packageName,
			final String columnName) {
		String selection = ScreenStyleConfigTable.THEMEPACKAGE + " = '" + packageName + "'";
		// 查询列数
		String columns[] = { columnName, };
		ContentResolver resolver = context.getContentResolver();
		return resolver.query(GoContentProvider.CONTENT_SCREENSTYLE_URI, columns, selection, null,
				null);
	}
}
