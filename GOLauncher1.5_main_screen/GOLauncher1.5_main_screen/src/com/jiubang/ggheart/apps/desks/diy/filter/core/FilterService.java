package com.jiubang.ggheart.apps.desks.diy.filter.core;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * 
 * @author guoyiqing
 * 
 */
public class FilterService {

	public static boolean sLoadLibError;
	static {
		try {
			System.loadLibrary("GOFilter");
		} catch (Throwable e) {
			sLoadLibError = true;
			e.printStackTrace();
		}
	}

	public static final int USE_TYPE_WALLPAPER = 0;
	public static final int USE_TYPE_ICON = 1;
	
	private static FilterService sService;
	private FilterParameterBuidler mParameterBuilder;
	private FilterRender mRender;

	private FilterService(Context context) {
		mParameterBuilder = new FilterParameterBuidler(context);
		mRender = new FilterRender(context);
	}

	/**
	 * need setContext(Context)
	 * @return
	 */
	public static synchronized FilterService getService(Context context) {
		if (sService == null) {
			sService = new FilterService(context);
		}
		return sService;
	}

	/**
	 * 
	 * @return may null
	 */
	public List<FilterParameter> getAllFilter(int useType) {
		if (mParameterBuilder != null) {
			return mParameterBuilder.buildParameter(useType);
		}
		return null;
	}

	public Bitmap render(FilterParameter parameter) {
		if (mRender != null) {
			return mRender.render(parameter);
		}
		return null;
	}

	public void cleanUp() {
		if (mParameterBuilder != null) {
			mParameterBuilder.cleanUp();
			mParameterBuilder = null;
		}
		if (mRender != null) {
			mRender.cleanUp();
			mRender = null;
		}
	}

}
