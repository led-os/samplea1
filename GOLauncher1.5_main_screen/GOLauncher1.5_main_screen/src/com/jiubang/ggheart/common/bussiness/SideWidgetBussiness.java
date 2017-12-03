/**
 * @author zhangxi
 * @date 2013-10-17
 */
package com.jiubang.ggheart.common.bussiness;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

import com.jiubang.ggheart.bussiness.BaseBussiness;
import com.jiubang.ggheart.common.data.SideWidgetDataModel;
import com.jiubang.ggheart.components.sidemenuadvert.widget.SideWidgetDataInfo;
import com.jiubang.ggheart.components.sidemenuadvert.widget.SideWidgetInfo;
import com.jiubang.ggheart.data.DatabaseException;

/**
 * @author zhangxi
 *
 */
public class SideWidgetBussiness extends BaseBussiness {

	private SideWidgetDataModel mDataModel;
	/**
	 * @param context
	 */
	public SideWidgetBussiness(Context context) {
		super(context);
		mDataModel = new SideWidgetDataModel(context);
	}
	
	public ArrayList<SideWidgetDataInfo> getAllInstalledWidgets() {
		return  mDataModel.getAllInstalledWidgets();
	}
	
	public void addInstalledWidget(SideWidgetDataInfo widgetInfo) {
		if (widgetInfo != null) {
			try {
				mDataModel.addInstalledWidget(widgetInfo.getTitle(),
						widgetInfo.getWidgetPkgName(),
						widgetInfo.getPreViewName());
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void removeUninstalledWidget(SideWidgetInfo widgetInfo) {
		if (widgetInfo != null) {
			try {
				mDataModel.removeUninstalledWidget(widgetInfo.getWidgetPkgName());
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void removeUninstalledWidget(String widgetPkgName) {
		if (widgetPkgName != null) {
			try {
				mDataModel.removeUninstalledWidget(widgetPkgName);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isInstalledWidget(SideWidgetInfo widgetInfo) {
		boolean isInstalled = false;
		if (widgetInfo != null) {
			Cursor cursor = mDataModel.getInstalledWidget(widgetInfo.getWidgetPkgName());
			if (cursor != null && cursor.getCount() != 0) {
				isInstalled = true;
			}
		}
		return isInstalled;
	}
}
