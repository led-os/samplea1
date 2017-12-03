package com.jiubang.ggheart.apps.desks.diy;

import java.util.ArrayList;

import android.content.Intent;

import com.jiubang.ggheart.apps.desks.diy.AppInvoker.IAppInvokerListener;

/**
 * 
 * store the parameter,which will be passed to command class
 * @author Hanson
 */
public class CommandParam {

	private Intent mLaunchIntent;
	ArrayList<IAppInvokerListener> mListener;
	int mFrom;
	boolean mIsCnUser;

	public CommandParam(Intent launchIntent,
			ArrayList<IAppInvokerListener> mListener, int from, boolean isCnUser) {

		this.mLaunchIntent = launchIntent;
		this.mListener = mListener;
		this.mFrom = from;
		this.mIsCnUser = isCnUser;
	}

	public Intent getmLaunchIntent() {
		return mLaunchIntent;
	}

	public void setmLaunchIntent(Intent mLaunchIntent) {
		this.mLaunchIntent = mLaunchIntent;
	}

	public ArrayList<IAppInvokerListener> getmListener() {
		return mListener;
	}

	public void setmListener(ArrayList<IAppInvokerListener> mListener) {
		this.mListener = mListener;
	}

	public int getmFrom() {
		return mFrom;
	}

	public void setmFrom(int mFrom) {
		this.mFrom = mFrom;
	}

	public boolean ismIsCnUser() {
		return mIsCnUser;
	}

	public void setmIsCnUser(boolean mIsCnUser) {
		this.mIsCnUser = mIsCnUser;
	}

}
