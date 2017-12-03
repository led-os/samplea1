package com.jiubang.ggheart.apps.desks.diy.filter.core;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.gau.go.launcherex.R;

/**
 * 
 * @author guoyiqing
 * 
 */
public class FilterParameterBuidler {

	private static List<FilterParameter> sWallpaperParameters;
	private static List<FilterParameter> sIconParameters;

	// private Context mContext;

	protected FilterParameterBuidler(Context context) {
		// mContext = context;
	}

	/**
	 * {@link #USE_TYPE_ICON} {@link #USE_TYPE_WALLPAPER}
	 * 
	 * @param useType
	 * @return may null
	 */
	public List<FilterParameter> buildParameter(int useType) {
		if (useType == FilterService.USE_TYPE_WALLPAPER) {
			addWallPaperParameter();
			return sWallpaperParameters;
		} else if (useType == FilterService.USE_TYPE_ICON) {
			addIconParameter();
			return sIconParameters;
		}
		return null;
	}

	private void addIconParameter() {
		if (sIconParameters == null) {
		}
	}


	// 0XFFBCEE68 绿色lightgreen
	// 0XFF6A5ACD 薰衣草
	// 0XFF1E90FF 蓝色
	// 0xFF8B1C62 紫红色
	// 0xFFFFE4E1 红色
	// 0XFF21A8FE Color.rgb(33, 168, 254) 秋日

	private void addWallPaperParameter() {
		if (sWallpaperParameters == null) {
			sWallpaperParameters = new ArrayList<FilterParameter>();
			FilterParameter parameter;
			sWallpaperParameters.add(new FilterParameter(
					IFilterTypeIds.FILTER_TYPE_FILM, R.string.filter_type_film,
					new int[] { 706, 719 }));
			sWallpaperParameters.add(new FilterParameter(
					IFilterTypeIds.FILTER_TYPE_SUNSET,
					R.string.filter_type_sunSet, new int[] { 707, 720 }));
			sWallpaperParameters.add(new FilterParameter(
					IFilterTypeIds.FILTER_TYPE_SUNRISE,
					R.string.filter_type_sunrise, new int[] { 708, 721 }));
			sWallpaperParameters.add(new FilterParameter(
					IFilterTypeIds.FILTER_TYPE_MILK, R.string.filter_type_milk,
					new int[] { 709, 722 }));
			sWallpaperParameters.add(new FilterParameter(
					IFilterTypeIds.FILTER_TYPE_SUNSHINE,
					R.string.filter_type_dream, new int[] { 704, 717 }));
			sWallpaperParameters.add(new FilterParameter(
					IFilterTypeIds.FILTER_TYPE_CLOUDY,
					R.string.filter_type_darken, new int[] { 705, 718 }));
			sWallpaperParameters.add(new FilterParameter(
					IFilterTypeIds.FILTER_TYPE_BLACKWHITE,
					R.string.filter_type_bw, new int[] { 710, 723 }));
			sWallpaperParameters.add(new FilterParameter(
					IFilterTypeIds.FILTER_TYPE_SKETCH,
					R.string.filter_type_sketch, new int[] { 711, 724 }));
			parameter = new FilterParameter(IFilterTypeIds.FILTER_TYPE_AUTUMN,
					R.string.filter_type_autumn, new int[] { 712, 725 });
			parameter.setDiyColor(0XFFFEA821);
			sWallpaperParameters.add(parameter);

			parameter = new FilterParameter(IFilterTypeIds.FILTER_TYPE_ROSE,
					R.string.filter_type_barry, new int[] { 713, 726 });
			parameter.setDiyColor(0xFF8B1C62);
			sWallpaperParameters.add(parameter);

			parameter = new FilterParameter(
					IFilterTypeIds.FILTER_TYPE_SAPPHIRE,
					R.string.filter_type_avatar, new int[] { 714, 727 });
			parameter.setDiyColor(0XFF1E90FF);
			sWallpaperParameters.add(parameter);

			parameter = new FilterParameter(
					IFilterTypeIds.FILTER_TYPE_LIGHTGREEN,
					R.string.filter_type_glass, new int[] { 715, 728 });
			parameter.setDiyColor(0XFFBCEE68);
			sWallpaperParameters.add(parameter);
		}
	}

	public void cleanUp() {
		if (sWallpaperParameters != null) {
			sWallpaperParameters.clear();
			sWallpaperParameters = null;
		}
		if (sIconParameters != null) {
			sIconParameters.clear();
			sIconParameters = null;
		}
	}

}
