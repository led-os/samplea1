package com.jiubang.ggheart.data.tables;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.jiubang.ggheart.data.info.ProManageAppItemInfo;
import com.jiubang.ggheart.data.info.RecentAppItemInfo;
import com.jiubang.ggheart.data.info.SpecialAppItemInfo;

/**
 * 功能表
 * 
 * @author huyong
 * 
 */
//CHECKSTYLE:OFF
public class SpecialApplicationTable {
	static public String TABLE_NAME = "special_application";
	static public String COMPONENT_NAME = "component_name";
	static public String CREATE_SQL = "create table " + TABLE_NAME + "(" + COMPONENT_NAME
			+ " text)";

	public static void initData(SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		insertData(db, values, new ProManageAppItemInfo());
		insertData(db, values, new RecentAppItemInfo());
	}

	private static void insertData(SQLiteDatabase db, ContentValues values, SpecialAppItemInfo info) {
		values.put(COMPONENT_NAME, info.mIntent.getComponent().flattenToString());
		db.insert(TABLE_NAME, null, values);
		values.clear();
	}
}
