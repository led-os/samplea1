package com.jiubang.ggheart.apps.desks.diy.filter.core;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * 
 * @author guoyiqing
 * 
 */
public class FilterRender {

	private List<AbsFilterHandler> mHandlers;

	protected FilterRender(Context context) {
		mHandlers = new ArrayList<AbsFilterHandler>();
		mHandlers.add(new ClothHandler());
		mHandlers.add(new BlackWhiteHandler());
		mHandlers.add(new SunshineHandler());
		mHandlers.add(new ColorToneHandler());
		mHandlers.add(new SketchHandler());
		mHandlers.add(new CloudyHandler());
		mHandlers.add(new GaussianBlurHandler());
		mHandlers.add(new BrightcontrastHandler());
		mHandlers.add(new NeonHandler());
		addPrime(context);
	}

	private void addPrime(Context context) {
//		if (FilterChecker.isHudsonEnable(context)) {
			mHandlers.add(new MilkHandler(context));
//		}
//		if (FilterChecker.isToasterEnable(context)) {
			mHandlers.add(new SunriseHandler(context));
//		}
//		if (FilterChecker.isAmaroEnable(context)) {
			mHandlers.add(new SunSetHandler(context));
//		}
		mHandlers.add(new FilmHandler(context));

	}

	public Bitmap render(FilterParameter parameter) {
		if (parameter == null) {
			return null;
		}
		Bitmap bitmap = null;
		for (AbsFilterHandler filterHandler : mHandlers) {
			if (filterHandler.canHandle(parameter.getTypeId())) {
				bitmap = filterHandler.handler(parameter);
				break;
			}
		}
		return bitmap;
	}

	public void cleanUp() {
		if (mHandlers != null) {
			mHandlers.clear();
		}
	}

}
