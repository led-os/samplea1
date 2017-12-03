package com.jiubang.ggheart.billing;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.billing.base.Consts;
import com.jiubang.ggheart.data.ContentProvider.GoContentProvider;
import com.jiubang.ggheart.data.tables.PurchaseTable;

/**
 * 管理付费结果，提供保存和查询付费状态的功能。
 * 
 * @author zhoujun
 * 
 */
public class PurchaseStateManager {

	/**
	 * SharedPreferences文件名
	 */
	private static final String THEME_PAID_STATUS = "theme_paid_status";

	private Context mContext;

	public PurchaseStateManager(Context context) {
		mContext = context;
	}

	/**
	 * 保存付费结果到。
	 * 
	 * @param pkgName 包名
	 * @param pkgPath 包路径
	 */
	public boolean save(String pkgName, String pkgPath) {
		if (Consts.DEBUG) {
			Log.e(Consts.TAG, "save pkgName:" + pkgName + ",pkgPath:" + pkgPath);
		}
		if (pkgName != null && pkgPath != null && mContext != null) {
			saveToSharedPreferences(pkgName, pkgPath);
			return true;
		}

		return false;
	}

	/**
	 * 根据主题包名，检查付费状态
	 * @param pkgName 包名
	 * @return pkgPath 如果付费后，返回改主题在sdcard的路径
	 */
	public static String query(Context context, String pakName) {
		if (pakName != null && context != null) {
			return getFromSharedPreferences(context, pakName) ;
		}
		return null;
	}

	public static boolean isPay(Context context, String productId) {
		boolean result = false;
		if (productId != null && context != null) {
			result = getFromSharedPreferences(context, productId) != null;
			if (!result) {
				ContentResolver resolver = context.getContentResolver();
				String selection = PurchaseTable.PRODUCT_ID + " ='" + productId + "'";
				Cursor cursor = resolver.query(GoContentProvider.CONTENT_PURCHASE_URI, null,
						selection, null, null);
				result = cursor != null && cursor.getCount() > 0;

			}
		}
		return result;
	}
	/**
	 * 保存付费结果到SharedPreferences。
	 * 
	 * @param pkgName 包名
	 * @param pkgPath 包路径
	 */
	private void saveToSharedPreferences(String pakName, String pkgPath) {
		PreferencesManager sharedPreferences = new PreferencesManager(mContext, THEME_PAID_STATUS,
				Context.MODE_WORLD_READABLE);
		sharedPreferences.putString(pakName, pkgPath);
		sharedPreferences.commit();
	}

	/**
	 * 从SharedPreferences中查询指定收费Id的付费状态
	 * 
	 * @param pkgName 包名
	 * @param pkgPath 包路径
	 * @return
	 */
	private static String getFromSharedPreferences(Context context, String pkgName) {
		if (context != null) {
			String mPkgPath = new PreferencesManager(context, THEME_PAID_STATUS,
					Context.MODE_WORLD_READABLE).getString(pkgName, null);
//			if (Consts.DEBUG) {
//				Log.d(Consts.TAG, "checkBillingState packageName: " + pkgName + ",fileName:"
//						+ mPkgPath);
//			}
			return mPkgPath;
		}
		return null;
	}
}
