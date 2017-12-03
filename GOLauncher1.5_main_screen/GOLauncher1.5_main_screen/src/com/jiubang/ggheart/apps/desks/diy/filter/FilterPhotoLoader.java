package com.jiubang.ggheart.apps.desks.diy.filter;

import android.content.Context;
import android.graphics.Bitmap;

import com.jiubang.ggheart.apps.desks.diy.filter.core.FilterParameter;
import com.jiubang.ggheart.apps.desks.diy.filter.core.FilterService;

/**
 * 
 * @author zouguiquan
 *
 */
public class FilterPhotoLoader extends AbsPhotoLoader {

	public FilterPhotoLoader(Context context, int defaultResourceId) {
		super(context, defaultResourceId);
	}

	@Override
	public Bitmap activityloadPhoto(Object id) {
		Bitmap bitmap = null;
		if (id instanceof FilterParameter) {
			FilterParameter parameter = (FilterParameter) id;
			if (parameter.getTypeId() == 0) {
				bitmap = FilterManager.getInstance().getThumbBitmap();
			} else {
				parameter.mSrcBitmap = FilterManager.getInstance().getThumbBitmap();
				bitmap = FilterService.getService(mContext).render(parameter);
			}

		}
		return bitmap;
	}
}
