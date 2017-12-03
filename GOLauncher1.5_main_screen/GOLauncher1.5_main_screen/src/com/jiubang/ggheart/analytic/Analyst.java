package com.jiubang.ggheart.analytic;

import android.content.Context;

import com.go.util.file.FileUtil;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

/**
 * Analyst
 * 
 * @author kuangsunny
 * @version 1.0
 */
public class Analyst {
	private final static boolean DEBUG = false;
	private final static String DUMMY_PAGE_INSTALL = "/GOLauncherEXIntalled";
	private final static int INTERVAL = 30 * 10;
	private GoogleAnalyticsTracker mTracker;
	private String mUaNumber;
	private Context mContext;

	public Analyst(Context context, int rawId) {
		this.mContext = context;
		initUANumber(rawId);
	}

	private void initUANumber(int rawId) {
		mUaNumber = FileUtil.getShortStrDataFromRaw(mContext, rawId);
	}

	public void startAnalysation() {
		if (mTracker == null) {
			mTracker = GoogleAnalyticsTracker.getInstance();
			mTracker.setDebug(DEBUG);
			mTracker.startNewSession(mUaNumber, INTERVAL, mContext);
		}
	}

	public void uploadReferrerInfo() {
		if (mTracker != null) {
			if (!InstallationUtils.isRefInfoStored(mContext)) {
				mTracker.trackPageView(DUMMY_PAGE_INSTALL);
				InstallationUtils.refInfoStored(mContext);
			}
		}
	}
	
	public void stopAnalysation() {
		if (mTracker != null) {
			mTracker.stopSession();
			mTracker = null;
		}
	}
}
