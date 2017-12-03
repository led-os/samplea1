package com.jiubang.ggheart.apps.desks.diy.filter.core;

import android.graphics.Bitmap;

/**
 * 
 * @author guoyiqing
 * 
 */
public class FilterParameter {

	public static final int PAY_EXTRA_IDS_INDEX_EDIT = 0;
	public static final int PAY_EXTRA_IDS_INDEX_SETTING = PAY_EXTRA_IDS_INDEX_EDIT + 1;
	private int mFilterTypeId;
	public Bitmap mSrcBitmap;
	private boolean mCanColorDiy;
	private int[] mDiyColors;
	private int mDiyColor;
	private int mFilterNameResId;
	private int[] mPayExtraIds;

	public FilterParameter(int typeId, int filterNameResId, int[] payExtraIds) {
		mFilterTypeId = typeId;
		mFilterNameResId = filterNameResId;
		mPayExtraIds = payExtraIds;
	}
	
	protected FilterParameter(int typeId, Bitmap bitmap, boolean canColorDiy,
			int[] diyColors, int filterNameResId) {
		mFilterTypeId = typeId;
		mSrcBitmap = bitmap;
		mCanColorDiy = canColorDiy;
		mDiyColors = diyColors;
		mFilterNameResId = filterNameResId;
	}
	
	/**
	 * maybe null
	 * @return
	 */
	public int[] getPayExtraIds() {
		return mPayExtraIds;
	}

	public int getTypeId() {
		return mFilterTypeId;
	}
	
	public boolean canDiyColor() {
		return mCanColorDiy;
	}
	
	public int getDiyColor() {
		return mDiyColor;
	}
	
	public void setDiyColor(int diyColor) {
		mDiyColor = diyColor;
	}
	
	public int[] getDiyColors() {
		return mDiyColors;
	}

	public void cleanUp() {
		mSrcBitmap = null;
		mDiyColors = null;
	}

	public int getFilterNameResId() {
		return mFilterNameResId;
	}
	
	public void setFilterNameResId(int resId) {
		mFilterNameResId = resId;
	}
}
