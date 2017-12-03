package com.jiubang.ggheart.apps.systemwidget;

import android.graphics.drawable.Drawable;

import com.jiubang.ggheart.apps.gowidget.BaseWidgetInfo;

/**
 * 
 * @author zgq
 * 
 */
public class SysSubWidgetInfo extends BaseWidgetInfo {

	private Drawable mIcon;
	private Drawable mPreview;
	private String mCustomerInfo;
	private int mWidgetId;
	private int mPosition;
	private int mCellWidth;
	private int mCellHeight;

	public Drawable getIcon() {
		return mIcon;
	}

	public void setIcon(Drawable icon) {
		mIcon = icon;
	}

	public Drawable getPreview() {
		return mPreview;
	}

	public void setPreview(Drawable preview) {
		mPreview = preview;
	}

	public String getCustomerInfo() {
		return mCustomerInfo;
	}

	public void setCustomerInfo(String customerInfo) {
		mCustomerInfo = customerInfo;
	}

	public int getWidgetId() {
		return mWidgetId;
	}

	public void setWidgetId(int widgetId) {
		mWidgetId = widgetId;
	}

	public int getPosition() {
		return mPosition;
	}

	public void setPosition(int position) {
		mPosition = position;
	}

	public int getCellWidth() {
		return mCellWidth;
	}

	public void setCellWidth(int cellWidth) {
		mCellWidth = cellWidth;
	}

	public int getCellHeight() {
		return mCellHeight;
	}

	public void setCellHeight(int cellHeight) {
		mCellHeight = cellHeight;
	}
	
	public int getPreviewImage() {
		if (mProvider != null) {
			return mProvider.previewImage;
		}
		return 0;
	}
	
	public String getLabel() {
		if (mProvider != null) {
			return mProvider.label;
		}
		return null;
	}
	
	public int getIconId() {
		if (mProvider != null) {
			return mProvider.icon;
		}
		return 0;
	}

	@Override
	public void setPkgName(String pkgName) {
	}

	@Override
	public String getPkgName() {
		if (mProvider != null) {
			return mProvider.provider.getPackageName();
		}
		return null;
	}

	@Override
	public String getIconPath() {
		return null;
	}

	@Override
	public String getTitle() {
		return null;
	}
}
