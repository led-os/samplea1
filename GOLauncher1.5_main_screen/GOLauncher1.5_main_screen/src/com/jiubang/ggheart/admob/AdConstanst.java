package com.jiubang.ggheart.admob;

import android.util.SparseArray;

/**
 * 
 * @author  guoyiqing
 * @date  [2013-9-12]
 */
public class AdConstanst {

	public static final String AD_DELETE_COUNT_KEY = "ad_delete_count_key";
	public static final String ADLIMIT_KEY = "adlimit_key";
	public static final String TODAY_KEY = "today_key";
	public static final String PERTIME_KEY = "pertime_key";
	public static final String SUM_KEY = "sum_key";
	public static final String VIEW_SHOWED_KEY = "view_showed_key";

	public static final int PRODUCT_ID_LAUNCHER_THEME = 1;
	public static final int PRODUCT_ID_LAUNCHER_STORE = 2;
	public static final int PRODUCT_ID_LOCKER_THEME = 3;

	/**
	 * 以下位置id,不管是不是相同的产品,pos_id都不能相同
	 */
	public static final int POS_ID_LAUNCHER_THEME_DETAIL_BANNER = 1;
	public static final int POS_ID_LAUNCHER_THEME_DETAIL_FULLSCREEN = 2;
	public static final int POS_ID_LAUNCHER_THEME_FEATURE_BANNER = 3;
	public static final int POS_ID_LAUNCHER_THEME_HOT_BANNER = 4;
	public static final int POS_ID_LAUNCHER_THEME_LOCAL_BANNER = 5;

	public static final int POS_ID_LAUNCHER_STORE_DETAIL_BANNER = 1;
	public static final int POS_ID_LAUNCHER_STORE_DETAIL_FULLSCREEN = 2;
	public static final int POS_ID_LAUNCHER_STORE_LIST_BANNER = 3;
	public static final int POS_ID_LAUNCHER_STORE_APP_DETAIL_BANNER = 4;
	public static final int POS_ID_LAUNCHER_STORE_APP_DETAIL_FULLSCREEN = 5;

	public static final int POS_ID_LOCKER_THEME_DETAIL_BANNER = 1;
	public static final int POS_ID_LOCKER_THEME_DETAIL_FULLSCREEN = 2;
	public static final int POS_ID_LOCKER_THEME_FEATURE_BANNER = 3;
	public static final int POS_ID_LOCKER_THEME_LOCAL_BANNER = 4;

	private static SparseArray<String> sPosToPubIdArray;
	private static final String BANNER_ALL_ID = "a15232bcd5a4529"; //主题管理页面的广告id，包括GoLauncherEX以及GoLocker界面的共用
	private static final String FULL_SCREEN_ALL_ID = "a15232b59d974fd";
	private static final String BANNER_DETAIL_ID = "a15232b627b3abf"; //主题详情界面的广告id
	static {
		sPosToPubIdArray = new SparseArray<String>();
		sPosToPubIdArray.put(
				AdInfo.getSparseId(PRODUCT_ID_LAUNCHER_THEME, POS_ID_LAUNCHER_THEME_DETAIL_BANNER),
				BANNER_DETAIL_ID);
		sPosToPubIdArray.put(AdInfo.getSparseId(PRODUCT_ID_LAUNCHER_THEME,
				POS_ID_LAUNCHER_THEME_DETAIL_FULLSCREEN), FULL_SCREEN_ALL_ID);
		sPosToPubIdArray
				.put(AdInfo.getSparseId(PRODUCT_ID_LAUNCHER_THEME,
						POS_ID_LAUNCHER_THEME_FEATURE_BANNER), BANNER_ALL_ID);
		sPosToPubIdArray.put(
				AdInfo.getSparseId(PRODUCT_ID_LAUNCHER_THEME, POS_ID_LAUNCHER_THEME_HOT_BANNER),
				BANNER_ALL_ID);
		sPosToPubIdArray.put(
				AdInfo.getSparseId(PRODUCT_ID_LAUNCHER_THEME, POS_ID_LAUNCHER_THEME_LOCAL_BANNER),
				BANNER_ALL_ID);

		sPosToPubIdArray.put(
				AdInfo.getSparseId(PRODUCT_ID_LAUNCHER_STORE, POS_ID_LAUNCHER_STORE_DETAIL_BANNER),
				BANNER_ALL_ID);
		sPosToPubIdArray.put(AdInfo.getSparseId(PRODUCT_ID_LAUNCHER_STORE,
				POS_ID_LAUNCHER_STORE_DETAIL_FULLSCREEN), FULL_SCREEN_ALL_ID);
		sPosToPubIdArray.put(
				AdInfo.getSparseId(PRODUCT_ID_LAUNCHER_STORE, POS_ID_LAUNCHER_STORE_LIST_BANNER),
				BANNER_ALL_ID);
		sPosToPubIdArray.put(
				AdInfo.getSparseId(PRODUCT_ID_LAUNCHER_STORE, POS_ID_LAUNCHER_STORE_APP_DETAIL_BANNER),
				BANNER_ALL_ID);
		sPosToPubIdArray.put(
				AdInfo.getSparseId(PRODUCT_ID_LAUNCHER_STORE, POS_ID_LAUNCHER_STORE_APP_DETAIL_FULLSCREEN),
				FULL_SCREEN_ALL_ID);

		sPosToPubIdArray.put(
				AdInfo.getSparseId(PRODUCT_ID_LOCKER_THEME, POS_ID_LOCKER_THEME_DETAIL_BANNER),
				BANNER_DETAIL_ID);
		sPosToPubIdArray.put(
				AdInfo.getSparseId(PRODUCT_ID_LOCKER_THEME, POS_ID_LOCKER_THEME_DETAIL_FULLSCREEN),
				FULL_SCREEN_ALL_ID);
		sPosToPubIdArray.put(
				AdInfo.getSparseId(PRODUCT_ID_LOCKER_THEME, POS_ID_LOCKER_THEME_FEATURE_BANNER),
				BANNER_ALL_ID);
		sPosToPubIdArray.put(
				AdInfo.getSparseId(PRODUCT_ID_LOCKER_THEME, POS_ID_LOCKER_THEME_LOCAL_BANNER),
				BANNER_ALL_ID);

	}

	public static String mapPublishId(int pid, int posId) {
		if (posId > 10000) {
			// go精品内分类id都为5位数,如位置ID大于5位数就认为是列表id,返回banner的广告id
			return BANNER_ALL_ID;
		}
		return sPosToPubIdArray.get(AdInfo.getSparseId(pid, posId));
	}

}
