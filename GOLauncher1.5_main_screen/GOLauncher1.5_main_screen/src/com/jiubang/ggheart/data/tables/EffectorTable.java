package com.jiubang.ggheart.data.tables;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * 滚屏特效表
 * @author yejijiong
 *
 */
public class EffectorTable {

	public static final String TABLE_NAME = "effector";
	public static final String EFFECTOR_ID = "effector_id";
	public static final String SCREEN_SETTING_SHOW_NEW = "screen_setting_show_new";
	public static final String EFFECT_TAB_SHOW_NEW = "effect_tab_show_new";
	public static final String APP_DRAWER_SETTING_SHOW_NEW = "app_drawer_setting_show_new";
	
	/**
	 * 建表语句
	 */
	static public final String CREATETABLESQL = "create table " + TABLE_NAME + " ( " + EFFECTOR_ID
			+ " numeric, " + SCREEN_SETTING_SHOW_NEW + " numeric, " + EFFECT_TAB_SHOW_NEW
			+ " numeric, " + APP_DRAWER_SETTING_SHOW_NEW + " numeric" + " ) ";
	
	/**
	 * 创建初始数据
	 */
	public static void initDefaultDatas(SQLiteDatabase db) {
		int lastIndex = 18; // 特效ID19-23都显示New
		for (int i = -2; i <= lastIndex; i++) { //ID-2到23的特效都默认不显示New
			ContentValues values = new ContentValues();
			values.put(EFFECTOR_ID, i);
			values.put(SCREEN_SETTING_SHOW_NEW, 0);
			values.put(EFFECT_TAB_SHOW_NEW, 0);
			values.put(APP_DRAWER_SETTING_SHOW_NEW, 0);
			db.insert(TABLE_NAME, null, values);
		}
	}
}
