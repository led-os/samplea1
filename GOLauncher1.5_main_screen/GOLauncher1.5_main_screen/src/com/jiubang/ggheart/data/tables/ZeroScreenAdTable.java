package com.jiubang.ggheart.data.tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author zhangkai
 * 快速拨号数据表的行
 */
public class ZeroScreenAdTable {

	public static final String TABLE_NAME = "zreoscreenad";

	/**
	 * ID编号
	 */
	public static final String ID = "_id";

	/**
	 * 快速拨号的名称
	 */
	public static final String TITLE = "title";

	/**
	 * 快速拨号的网址
	 */
	public static final String URL = "url";

	/**
	 * 快速拨号的域名
	 */
	public static final String DOMAIN = "domain";

	/**
	 * 徐老师设计的大图的背景色
	 */
	public static final String DESIGNED_COLOR = "designed_color";

	/**
	 * 快速拨号的高清图标(用于合成大图标)
	 */
	public static final String ICON = "logoicon";

	/**
	 * 根据高清图标计算得到的color
	 */
	public static final String CUSTOM_COLOR = "custom_color";

	/**
	 * 新建时默认给它的随机数
	 */
	public static final String RANDOM = "random";

	/**
	 * 是否显示加号
	 */
	public static final String ISPLUS = "isplus";
	/**
	 * 是否是推荐广告
	 */
	public static final String ISRECOMMEND = "isrecommend";

	/**
	 * 记录每个广告的位置
	 */
	public static final String POSITION = "position";

	//---for database use---
	public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + " (" + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE + " TEXT, " + URL + " TEXT, " + DOMAIN
			+ " TEXT, " + DESIGNED_COLOR + " INTEGER, " + ICON + " BLOB DEFAULT NULL, "
			+ CUSTOM_COLOR + " INTEGER, " + RANDOM + " TEXT," + ISPLUS + " numeric," + ISRECOMMEND
			+ " numeric," + POSITION + " numeric)";

	/**
	 * 0屏广告默认数据
	 */
	public static final void initZeroScreenDefaultAD(SQLiteDatabase db) {
		/*
		ContentValues contentValues1 = new ContentValues();
		contentValues1.put(ZeroScreenAdTable.TITLE, NavigationView.ZERO_SCREEN_POSITION_AD_ONE);
		contentValues1.put(ZeroScreenAdTable.ISRECOMMEND, 1);
		contentValues1.put(ZeroScreenAdTable.ISPLUS, 0);
		contentValues1.put(ZeroScreenAdTable.URL,
				NavigationView.ZERO_SCREEN_POSITION_AD_ONE_WEBURL);
		contentValues1.put(ZeroScreenAdTable.CUSTOM_COLOR, -1);
		contentValues1.put(ZeroScreenAdTable.DESIGNED_COLOR, -1);
		contentValues1.put(ZeroScreenAdTable.POSITION, 0);
		db.insert(ZeroScreenAdTable.TABLE_NAME, null, contentValues1);

		ContentValues contentValues2 = new ContentValues();
		contentValues2.put(ZeroScreenAdTable.TITLE, NavigationView.ZERO_SCREEN_POSITION_AD_TWO);
		contentValues2.put(ZeroScreenAdTable.ISRECOMMEND, 1);
		contentValues2.put(ZeroScreenAdTable.ISPLUS, 0);
		contentValues2.put(ZeroScreenAdTable.CUSTOM_COLOR, -1);
		contentValues2.put(ZeroScreenAdTable.DESIGNED_COLOR, -1);
		contentValues2.put(ZeroScreenAdTable.URL,
				NavigationView.ZERO_SCREEN_POSITION_AD_TWO_WEBURL);
		contentValues2.put(ZeroScreenAdTable.POSITION, 1);
		db.insert(ZeroScreenAdTable.TABLE_NAME, null, contentValues2);

		ContentValues contentValues3 = new ContentValues();
		contentValues3.put(ZeroScreenAdTable.TITLE, NavigationView.ZERO_SCREEN_POSITION_AD_THREE);
		contentValues3.put(ZeroScreenAdTable.ISRECOMMEND, 1);
		contentValues3.put(ZeroScreenAdTable.ISPLUS, 0);
		contentValues3.put(ZeroScreenAdTable.CUSTOM_COLOR, -1);
		contentValues3.put(ZeroScreenAdTable.DESIGNED_COLOR, -1);
		contentValues3.put(ZeroScreenAdTable.URL,
				NavigationView.ZERO_SCREEN_POSITION_AD_THREE_WEBURL);
		contentValues3.put(ZeroScreenAdTable.POSITION, 2);
		db.insert(ZeroScreenAdTable.TABLE_NAME, null, contentValues3);

		ContentValues contentValues4 = new ContentValues();
		contentValues4.put(ZeroScreenAdTable.ISRECOMMEND, 0);
		contentValues4.put(ZeroScreenAdTable.ISPLUS, 1);
		contentValues4.put(ZeroScreenAdTable.CUSTOM_COLOR, -1);
		contentValues4.put(ZeroScreenAdTable.DESIGNED_COLOR, -1);
		contentValues4.put(ZeroScreenAdTable.POSITION, 3);
		db.insert(ZeroScreenAdTable.TABLE_NAME, null, contentValues4);
		
		ContentValues contentValues5 = new ContentValues();
		contentValues5.put(ZeroScreenAdTable.ISRECOMMEND, 0);
		contentValues5.put(ZeroScreenAdTable.ISPLUS, 1);
		contentValues5.put(ZeroScreenAdTable.CUSTOM_COLOR, -1);
		contentValues5.put(ZeroScreenAdTable.DESIGNED_COLOR, -1);
		contentValues5.put(ZeroScreenAdTable.POSITION, 4);
		db.insert(ZeroScreenAdTable.TABLE_NAME, null, contentValues5);
		
		ContentValues contentValues6 = new ContentValues();
		contentValues6.put(ZeroScreenAdTable.ISRECOMMEND, 0);
		contentValues6.put(ZeroScreenAdTable.ISPLUS, 1);
		contentValues6.put(ZeroScreenAdTable.CUSTOM_COLOR, -1);
		contentValues6.put(ZeroScreenAdTable.DESIGNED_COLOR, -1);
		contentValues6.put(ZeroScreenAdTable.POSITION, 5);
		db.insert(ZeroScreenAdTable.TABLE_NAME, null, contentValues6);
		*/
	}
}
