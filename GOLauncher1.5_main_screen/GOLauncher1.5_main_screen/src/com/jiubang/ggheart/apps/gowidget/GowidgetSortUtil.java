package com.jiubang.ggheart.apps.gowidget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.ScreenEditController;
import com.jiubang.ggheart.data.info.GoWidgetBaseInfo;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 编辑界面Gowidget排序工具类
 * @author dengdazhong
 *
 */
public class GowidgetSortUtil {
	public static void sort(ArrayList<Object> goWidgetInfoList) {
		
		HashMap<String, GoWidgetProviderInfo> mapInfos = new HashMap<String, GoWidgetProviderInfo>();
		HashMap<Integer, InnerWidgetInfo> innerWidgetInfos = new HashMap<Integer, InnerWidgetInfo>();
		List<BaseWidgetInfo> infos = new ArrayList<BaseWidgetInfo>();
		for (Object obj : goWidgetInfoList) {
			
//			GoWidgetProviderInfo info = (GoWidgetProviderInfo) obj;
			
			if (obj instanceof InnerWidgetInfo) {
				InnerWidgetInfo info = (InnerWidgetInfo) obj;
				innerWidgetInfos.put(info.mPrototype, info);
				//modify zgq
//				info.mInstalled = true;
				infos.add(info);
			} else if (obj instanceof GoWidgetProviderInfo) {
				GoWidgetProviderInfo info = (GoWidgetProviderInfo) obj;
				
				if (ScreenEditController.INSTALLED_PACKAGES.contains(info.getPkgName())) {
					info.mInstalled = true;
					infos.add(info);
				}
				mapInfos.put(info.getPkgName(), info);
			}
		}
		goWidgetInfoList.removeAll(infos);
		infos.clear();

		// 加入 一键换主题
//		GoWidgetProviderInfo oneKeyChangeTheme = new GoWidgetProviderInfo();
//		infos.add(oneKeyChangeTheme);
		
		BaseWidgetInfo info = null;
		
		// 内置
		info = innerWidgetInfos.get(GoWidgetBaseInfo.PROTOTYPE_APPGAME);
		if (info != null) {
			infos.add(info);
		}
		info = innerWidgetInfos.get(GoWidgetBaseInfo.PROTOTYPE_GOSTORE);
		if (info != null) {
			infos.add(info);
		}
		//内置开关
		info = innerWidgetInfos.get(GoWidgetBaseInfo.PROTOTYPE_GOSWITCH);
		if (info != null) {
			infos.add(info);
		}
		//内置天气
		info = innerWidgetInfos.get(GoWidgetBaseInfo.PROTOTYPE_GOWEATHER);
		if (info != null) {
			infos.add(info);
		}
		// 非内置
		info = mapInfos.get(PackageName.NEW_SWITCH_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.TIMER_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.SCANNER_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.FLASH_LIGHT_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.SWITCH_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.NEW_CALENDAR_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.CALENDAR_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.NOTE_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.NEW_NOTE_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.RECOMMAND_GOBACKUPEX_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.RECOMMAND_GOWEATHEREX_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.CLOCK_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.RECOMMAND_GOPOWERMASTER_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.TASK_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.RECOMMAND_GOTASKMANAGER_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.RECOMMAND_GOSMS_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.CONTACT_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.EMAIL_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.SEARCH_WIDGET_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.BOOKMARK_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.FACEBOOK_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.TWITTER_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.SINA_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.TENCNT_PACKAGE);
		if (info != null) {
			infos.add(info);
		}
		info = mapInfos.get(PackageName.OK_SCREEN_SHOT);
		if (info != null) {
			infos.add(info);
		}
		goWidgetInfoList.addAll(0, infos);
	}
}
