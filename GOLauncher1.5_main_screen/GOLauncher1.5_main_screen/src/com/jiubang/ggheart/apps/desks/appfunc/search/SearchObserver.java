// Copyright 2010 Google Inc. All Rights Reserved.

package com.jiubang.ggheart.apps.desks.appfunc.search;

import java.util.List;

import com.jiubang.ggheart.billing.base.Consts.PurchaseState;


/**
 * An interface for observing changes related to purchases. The main application
 * extends this class and registers an instance of that derived class with
 * {@link ResponseHandler}. The main application implements the callbacks
 * {@link #onBillingSupported(boolean)} and
 * {@link #onPurchaseStateChange(PurchaseState, String, int, long)}. These
 * methods are used to update the UI.
 */
public interface SearchObserver {

	public abstract void onSearchSupported(boolean supported, Object timeStamp);

	public abstract void onSearchStart(String searchKey, int type, int requestTimes);

	public abstract void onSearchFinsh(String searchKey, List<?> list, int type, int resultCount,
			int currentPage, Object timestamp);

	public abstract void onSearchException(String searchKey, Object obj, int type);

	public abstract void onHistoryChange(List<?> list, int historyType, boolean hasHistory,
			boolean isNotifly);

	public abstract void onSearchWithoutData(String searchKey, int type, Object searchStamp);
}
