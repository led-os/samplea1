package com.jiubang.ggheart.apps.appfunc.controler;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.go.proxy.SettingProxy;
import com.go.util.SortHelper;
import com.go.util.log.Duration;
import com.go.util.sort.CompareClickedMethod;
import com.go.util.sort.CompareMethod;
import com.go.util.sort.ComparePriorityMethod;
import com.go.util.sort.CompareTimeMethod;
import com.go.util.sort.CompareTitleMethod;
import com.go.util.sort.IBaseCompareable;
import com.go.util.sort.IClickedCompareable;
import com.go.util.sort.ICreatTimeCompareable;
import com.go.util.sort.IPriorityLvCompareable;
import com.go.util.sort.ITitleCompareable;
import com.jiubang.ggheart.apps.appfunc.setting.FunAppSetting;
import com.jiubang.ggheart.data.info.FunItemInfo;

/**
 * 功能表数据类型专用排序助手
 * @author wuziyi
 *
 */
public class AppDrawerSortHelper {
	
	public static void sortList(List<? extends FunItemInfo> list, Context context) {
		CompareMethod<IPriorityLvCompareable> method = new ComparePriorityMethod();
		CompareMethod<? extends IBaseCompareable> nextMethod;
		int sortType = SettingProxy.getFunAppSetting().getSortType();
		Duration.setStart("Sort");
		switch (sortType) {
			case FunAppSetting.SORTTYPE_LETTER:
				nextMethod = new CompareTitleMethod();
				break;
			case FunAppSetting.SORTTYPE_TIMENEAR:
				nextMethod = new CompareTimeMethod(context);
				nextMethod.setOrder(CompareMethod.DESC);
				break;
			case FunAppSetting.SORTTYPE_TIMEREMOTE:
				nextMethod = new CompareTimeMethod(context);
				break;
			case FunAppSetting.SORTTYPE_FREQUENCY:
				nextMethod = new CompareClickedMethod(context);
				nextMethod.setOrder(CompareMethod.DESC);
				break;
			default:
				nextMethod = new CompareTitleMethod();
				break;
		}
		method.setNextMethod(nextMethod);
		SortHelper.doSort(list, method);
		Log.i("wuziyi", "Sort time:" + Duration.getDuration("Sort"));
	}

	public static void sortByLetter(int order, List<? extends FunItemInfo> list) {
		CompareMethod<IPriorityLvCompareable> method = new ComparePriorityMethod();
		CompareMethod<ITitleCompareable> nextMethod = new CompareTitleMethod();
		method.setOrder(CompareMethod.ASC);
		nextMethod.setOrder(order);
		method.setNextMethod(nextMethod);
		SortHelper.doSort(list, method);
	}

	public static void sortByTime(int order, List<? extends FunItemInfo> list, Context context) {
		CompareMethod<IPriorityLvCompareable> method = new ComparePriorityMethod();
		CompareMethod<ICreatTimeCompareable> nextMethod = new CompareTimeMethod(context);
		method.setOrder(CompareMethod.ASC);
		nextMethod.setOrder(order);
		method.setNextMethod(nextMethod);
		SortHelper.doSort(list, method);
	}

	public static void sortByFrequency(int order, List<? extends FunItemInfo> list, Context context) {
		CompareMethod<IPriorityLvCompareable> method = new ComparePriorityMethod();
		CompareMethod<IClickedCompareable> nextMethod = new CompareClickedMethod(context);
		method.setOrder(CompareMethod.ASC);
		nextMethod.setOrder(order);
		method.setNextMethod(nextMethod);
		SortHelper.doSort(list, method);
	}
}
