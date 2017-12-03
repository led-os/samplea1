// Copyright 2010 Google Inc. All Rights Reserved.

package com.jiubang.ggheart.apps.desks.appfunc.search;

import java.util.List;
/**
 * 
 * <br>类描述: 搜索结果观察者。
 * <br>功能详细描述:
 * 
 * @author  dingzijian
 * @date  [2012-10-19]
 */
public class ResponseHandler {
	private static final String TAG = "ResponseHandler";

	private static SearchObserver sSearchObserver;

	public static synchronized void register(SearchObserver observer) {
		sSearchObserver = observer;
	}

	public static synchronized void unregister(SearchObserver observer) {
		sSearchObserver = null;
	}

	public static void checkSearchSupportedResponse(boolean supported, Object timeStamp) {
		if (sSearchObserver != null) {
			sSearchObserver.onSearchSupported(supported, timeStamp);
		}
	}

	public synchronized static void startResponseReceived(String searchKey, int type, int currentPage) {
		if (sSearchObserver != null) {
			sSearchObserver.onSearchStart(searchKey, type, currentPage);
		}
	}

	public synchronized static void finishResponseReceived(String searchKey, List<?> list,
			int type, int resultCount, int currentPage, Object timestamp) {
		if (sSearchObserver != null) {
			sSearchObserver.onSearchFinsh(searchKey, list, type, resultCount, currentPage,
					timestamp);
		}
	}

	public synchronized static void exceptionResponseReceived(String searchKey, Object object, int type) {
		if (sSearchObserver != null) {
			sSearchObserver.onSearchException(searchKey, object, type);
		}
	}

	public synchronized static void noDataResponseReceived(String searchKey, int type,
			Object timestamp) {
		if (sSearchObserver != null) {
			sSearchObserver.onSearchWithoutData(searchKey, type, timestamp);
		}
	}

	public synchronized static void historyResponseRecevied(List<?> list, int historyType, boolean hasHistory,
			boolean isNotifly) {
		if (sSearchObserver != null) {
			sSearchObserver.onHistoryChange(list, historyType, hasHistory, isNotifly);
		}
	}
}
