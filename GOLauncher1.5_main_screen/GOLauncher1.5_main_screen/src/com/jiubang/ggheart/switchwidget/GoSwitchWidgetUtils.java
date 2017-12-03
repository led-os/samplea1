package com.jiubang.ggheart.switchwidget;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.view.View;

import com.go.util.AppUtils;
import com.go.util.StringUtil;
import com.jiubang.ggheart.data.info.GoWidgetBaseInfo;
import com.jiubang.ggheart.data.tables.GoWidgetTable;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 内置Go开关Widget工具类
 * @author caoyaming
 *
 */
public class GoSwitchWidgetUtils {
	//内置开关对应的View类名--2D
	private static final String GO_SWITCH_WIDGET_VIEW_CLASSNAME_2D = "com.jiubang.ggheart.innerwidgets.goswitchwidget.default2d.GoSwitchWidget";
	//内置开关对应的View类名--2D
	private static final String GO_SWITCH_WIDGET_VIEW_CLASSNAME_3D = "com.jiubang.ggheart.innerwidgets.goswitchwidget.GLGoSwitchWidget";
		
	/**
	 * 是否创建新的Widget View 
	 * @param context
	 * @param view 当前显示的View
	 * @param info Widget信息
	 * @return
	 */
	public static boolean isCreateGoSwitchWidget(Context context, View view, GoWidgetBaseInfo info) {
		if (view != null && info != null && isInstallGoSwitchWidget(context) && (GO_SWITCH_WIDGET_VIEW_CLASSNAME_2D.equals(view.getClass().getName()) || GO_SWITCH_WIDGET_VIEW_CLASSNAME_3D.equals(view.getClass().getName()))) {
			return true;
		} 
		return false;
	}
	
	/**
	 * 获取Go开关Widget信息(包名, Layout文件名称, 类型),以ContentValues方式返回.
	 * @param context
	 * @param info Widget信息
	 * @return
	 */
	public static ContentValues getSwitchWidgetInfoToContentValues(Context context, GoWidgetBaseInfo info) {
		//获取信息
		Map<String, Object> widgetInfoMap = getSwitchWidgetInfo(context, info);
		if (widgetInfoMap.get(GoWidgetTable.PACKAGE) == null) {
			return null;
		}
		//将信息存入ContentValues
		ContentValues values = new ContentValues();
		//Go开关 包名
		values.put(GoWidgetTable.PACKAGE, StringUtil.toString(widgetInfoMap.get(GoWidgetTable.PACKAGE))); 
		//Go开关 Layout文件名称
		values.put(GoWidgetTable.LAYOUT, StringUtil.toString(widgetInfoMap.get(GoWidgetTable.LAYOUT))); 
		//GoWidget 类型 
		values.put(GoWidgetTable.PROTOTYPE, StringUtil.toInteger(widgetInfoMap.get(GoWidgetTable.PROTOTYPE), GoWidgetBaseInfo.PROTOTYPE_NORMAL)); 
		return values;
	}
	
	/**
	 * 获取Go开关Widget信息(包名, Layout文件名称, 类型)
	 * @param context
	 * @param info Widget信息
	 * @return
	 */
	public static Map<String, Object> getSwitchWidgetInfo(Context context, GoWidgetBaseInfo info) {
		Map<String, Object> widgetInfoMap = new HashMap<String, Object>();
		//判断是否为内置Go开关
		if (info == null || info.mPrototype == GoWidgetBaseInfo.PROTOTYPE_GOSWITCH) {
			if (AppUtils.isAppExist(context, PackageName.NEW_SWITCH_PACKAGE)) {
				//安装了Go开关+应用
				widgetInfoMap.put(GoWidgetTable.PACKAGE, PackageName.NEW_SWITCH_PACKAGE); //Go开关+ 包名
				widgetInfoMap.put(GoWidgetTable.LAYOUT, "main_new"); //Go开关+ Layout文件名称
				widgetInfoMap.put(GoWidgetTable.PROTOTYPE, GoWidgetBaseInfo.PROTOTYPE_NORMAL); //GoWidget 类型 
			} else if (AppUtils.isAppExist(context, PackageName.SWITCH_PACKAGE)) {
				//安装了Go开关应用
				widgetInfoMap.put(GoWidgetTable.PACKAGE, PackageName.SWITCH_PACKAGE); //Go开关+ 包名
				widgetInfoMap.put(GoWidgetTable.LAYOUT, "main_new"); //Go开关+ Layout文件名称
				widgetInfoMap.put(GoWidgetTable.PROTOTYPE, GoWidgetBaseInfo.PROTOTYPE_NORMAL); //GoWidget 类型 
			} 
		}
		if (widgetInfoMap.size() <= 0 && info != null) {
			widgetInfoMap.put(GoWidgetTable.PACKAGE, info.mPackage); //包名
			widgetInfoMap.put(GoWidgetTable.LAYOUT, info.mLayout); //Layout文件名称
			widgetInfoMap.put(GoWidgetTable.PROTOTYPE, info.mPrototype); //类型 
		}
		return widgetInfoMap;
	}
	/**
	 * 判断是否为Go开关Widget包名
	 * @return true:是  false:否
	 */
	public static boolean isGoSwitchWidgetPackageName(String packageName) {
		if (PackageName.NEW_SWITCH_PACKAGE.equals(packageName) || PackageName.SWITCH_PACKAGE.equals(packageName)) {
			return true;
		}
		return false;
	}
	/**
	 * 判断是否已安装了开关或开关+应用(Widget)
	 * @param context
	 * @return true:已安装  false:未安装
	 */
	public static boolean isInstallGoSwitchWidget(Context context) {
		if (AppUtils.isAppExist(context, PackageName.NEW_SWITCH_PACKAGE) || AppUtils.isAppExist(context, PackageName.SWITCH_PACKAGE)) {
			return true;
		}
		return false;
	}
}
