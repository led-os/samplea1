package com.jiubang.ggheart.apps.desks.appfunc;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.jiubang.ggheart.data.DataProvider;

/**
 * 隐藏程序的content provider
 * 用于侧边了
 * @author dengdazhong
 *
 */
public class HideAppProvider extends ContentProvider {
	
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
//		mList = AppConfigControler.getInstance(getContext()).getAllHideApp();
		return true;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		DataProvider dataProvider = DataProvider.getInstance(getContext());
		Cursor cursor = dataProvider.getAllHideAppItems();
		return cursor;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}
}
