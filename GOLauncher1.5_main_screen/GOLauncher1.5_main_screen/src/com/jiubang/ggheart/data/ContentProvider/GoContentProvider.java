package com.jiubang.ggheart.data.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.jiubang.ggheart.admob.AdTable;
import com.jiubang.ggheart.data.DataProvider;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.DatabaseHelper;
import com.jiubang.ggheart.data.tables.AppFuncSettingTable;
import com.jiubang.ggheart.data.tables.ConfigTable;
import com.jiubang.ggheart.data.tables.NoPromptUpdateAppTable;
import com.jiubang.ggheart.data.tables.PurchaseTable;
import com.jiubang.ggheart.data.tables.ScreenStyleConfigTable;
import com.jiubang.ggheart.data.tables.ShortcutSettingTable;

/**
 * 
 * <br>类描述:提供跨进程访问的数据,以后涉及到跨进程访问androidheart.db的可在这里添加
 * <br>功能详细描述:
 * <br>注意：android:exported="false"，外部应用是不开放的
 * 
 * @author  rongjinsong
 * @date  [2013-7-24]
 */
public class GoContentProvider extends ContentProvider {
	private static final String AUTHORITIE = "com.jiubang.ggheart.data.content.gocontentprovider";
	private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	private static final String DATA_PATH_CURTHEME = "curtheme";
	private static final String DATA_PATH_SCREENSTYLE = "screenstyle";
	private static final String DATA_PATH_SHORTCUTSETTING = "shortcutsetting";
	private static final String DATA_PATH_APPFUNCSETTING = "appfuncsetting";
	private static final String DATA_PATH_NOPROMPTUPDATEAPP = "nopromptupdateapp";
	private static final String DATA_PATH_ADMOB = "admob";
	private static final String DATA_PATH_PURCHASE = "purchase";
	private static final int CODE_CURTHEME = 1;
	private static final int CODE_SCREENSTYLE = 2;
	private static final int CODE_DATA_SHORTCUTSETTING = 3;
	private static final int CODE_DATA_APPFUNCSETTING = 4;
	private static final int CODE_DATA_NOPROMPTUPDATEAPP = 5;
	private static final int CODE_DATA_ADMOB = 6;
	private static final int CODE_DATA_PURCHASE = 7;
	static {
		sUriMatcher.addURI(AUTHORITIE, DATA_PATH_CURTHEME, CODE_CURTHEME);
		sUriMatcher.addURI(AUTHORITIE, DATA_PATH_SCREENSTYLE, CODE_SCREENSTYLE);
		sUriMatcher.addURI(AUTHORITIE, DATA_PATH_SHORTCUTSETTING, CODE_DATA_SHORTCUTSETTING);
		sUriMatcher.addURI(AUTHORITIE, DATA_PATH_APPFUNCSETTING, CODE_DATA_APPFUNCSETTING);
		sUriMatcher.addURI(AUTHORITIE, DATA_PATH_NOPROMPTUPDATEAPP, CODE_DATA_NOPROMPTUPDATEAPP);
		sUriMatcher.addURI(AUTHORITIE, DATA_PATH_ADMOB, CODE_DATA_ADMOB);
		sUriMatcher.addURI(AUTHORITIE, DATA_PATH_PURCHASE, CODE_DATA_PURCHASE);
	}

	public static final Uri CONTENT_CURTHEME_URI = Uri.parse("content://" + AUTHORITIE + "/"
			+ DATA_PATH_CURTHEME);
	public static final Uri CONTENT_SCREENSTYLE_URI = Uri.parse("content://" + AUTHORITIE + "/"
			+ DATA_PATH_SCREENSTYLE);
	public static final Uri CONTENT_SHORTCUTSETTING_URI = Uri.parse("content://" + AUTHORITIE + "/"
			+ DATA_PATH_SHORTCUTSETTING);
	public static final Uri CONTENT_APPFUNCSETTING_URI = Uri.parse("content://" + AUTHORITIE + "/"
			+ DATA_PATH_APPFUNCSETTING);

	public static final Uri CONTENT_NOPROMPTUPDATEAPP_URI = Uri.parse("content://" + AUTHORITIE
			+ "/" + DATA_PATH_NOPROMPTUPDATEAPP);
	
	public static final Uri CONTENT_ADMOB_URI = Uri.parse("content://" + AUTHORITIE
			+ "/" + DATA_PATH_ADMOB);
	
	public static final Uri CONTENT_PURCHASE_URI = Uri.parse("content://" + AUTHORITIE
			+ "/" + DATA_PATH_PURCHASE);
	
	
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		// TODO Auto-generated method stub
		Cursor cursor = null;
		String table = null;
		switch (sUriMatcher.match(uri)) {
			case CODE_CURTHEME : {
				table = ConfigTable.TABLENAME;
			}
				break;
			case CODE_SCREENSTYLE : {
				table = ScreenStyleConfigTable.TABLENAME;
			}
				break;
			case CODE_DATA_SHORTCUTSETTING : {
				table = ShortcutSettingTable.TABLENAME;
			}
				break;
			case CODE_DATA_APPFUNCSETTING : {
				table = AppFuncSettingTable.TABLENAME;
			}
			break;
			case CODE_DATA_NOPROMPTUPDATEAPP : {
				table = NoPromptUpdateAppTable.TABLENAME;
			}
			break;
			case CODE_DATA_ADMOB : {
				table = AdTable.TABLE_NAME;
			}
			break;
			case CODE_DATA_PURCHASE : {
				table = PurchaseTable.TABLE_NAME;
			}
			break;
			default :
		}
		if (table != null) {
			try {
				DatabaseHelper helper = (DatabaseHelper) DataProvider.getInstance(getContext())
						.getDatabaseHelper();
				cursor = helper.query(table, projection, selection, selectionArgs, sortOrder);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return cursor;
	}
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		String tableName = null;
		switch (sUriMatcher.match(uri)) {
			case CODE_CURTHEME : {
				tableName = ConfigTable.TABLENAME;
			}
				break;
			case CODE_SCREENSTYLE : {
				tableName = ScreenStyleConfigTable.TABLENAME;
			}
				break;
			case CODE_DATA_SHORTCUTSETTING : {
				tableName = ShortcutSettingTable.TABLENAME;
			}
				break;
			case CODE_DATA_APPFUNCSETTING : {
				tableName = AppFuncSettingTable.TABLENAME;
			}
				break;
			case CODE_DATA_NOPROMPTUPDATEAPP : {
				tableName = NoPromptUpdateAppTable.TABLENAME;
			}
				break;
			case CODE_DATA_ADMOB : {
				tableName = AdTable.TABLE_NAME;
			}
			break;
			case CODE_DATA_PURCHASE : {
				tableName = PurchaseTable.TABLE_NAME;
			}
			break;
		}
		DatabaseHelper helper = (DatabaseHelper) DataProvider.getInstance(getContext())
				.getDatabaseHelper();
		try {
			if (tableName != null) {
				helper.insert(tableName, values);
			}
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		String tableName = null;
		int count = 0;
		switch (sUriMatcher.match(uri)) {
			case CODE_CURTHEME : {
				tableName = ConfigTable.TABLENAME;
			}
				break;
			case CODE_SCREENSTYLE : {
				tableName = ScreenStyleConfigTable.TABLENAME;
			}
				break;
			case CODE_DATA_SHORTCUTSETTING : {
				tableName = ShortcutSettingTable.TABLENAME;
			}
				break;
			case CODE_DATA_APPFUNCSETTING : {
				tableName = AppFuncSettingTable.TABLENAME;
			}
				break;
			case CODE_DATA_NOPROMPTUPDATEAPP : {
				tableName = NoPromptUpdateAppTable.TABLENAME;
			}
				break;
			case CODE_DATA_ADMOB : {
				tableName = AdTable.TABLE_NAME;
			}
			break;
			case CODE_DATA_PURCHASE : {
				tableName = PurchaseTable.TABLE_NAME;
			}
			break;
				
		}
		if (tableName != null) {
			try {
				DatabaseHelper helper = (DatabaseHelper) DataProvider.getInstance(getContext())
						.getDatabaseHelper();
				count = helper.delete(tableName, selection, selectionArgs);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		int count = 0;
		String tableName = null;
		switch (sUriMatcher.match(uri)) {
			case CODE_CURTHEME : {
				tableName = ConfigTable.TABLENAME;
			}
				break;
			case CODE_SCREENSTYLE : {
				tableName = ScreenStyleConfigTable.TABLENAME;
			}
				break;
			case CODE_DATA_SHORTCUTSETTING : {
				tableName = ShortcutSettingTable.TABLENAME;
			}
				break;
			case CODE_DATA_APPFUNCSETTING : {
				tableName = AppFuncSettingTable.TABLENAME;
			}
				break;
			case CODE_DATA_NOPROMPTUPDATEAPP : {
				tableName = NoPromptUpdateAppTable.TABLENAME;
			}
				break;
			case CODE_DATA_ADMOB : {
				tableName = AdTable.TABLE_NAME;
			}
			break;
			case CODE_DATA_PURCHASE : {
				tableName = PurchaseTable.TABLE_NAME;
			}
			break;
		}
		if (tableName != null) {
			try {
				DatabaseHelper helper = (DatabaseHelper) DataProvider.getInstance(getContext())
						.getDatabaseHelper();
				count = helper.update(tableName, values, selection, selectionArgs);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

}
