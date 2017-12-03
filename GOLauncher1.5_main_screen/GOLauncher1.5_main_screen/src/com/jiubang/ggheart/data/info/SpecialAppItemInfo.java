package com.jiubang.ggheart.data.info;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.go.proxy.ApplicationProxy;
import com.go.util.device.Machine;
import com.jiubang.ggheart.appgame.base.manage.AsyncImageManager.AsyncImageLoadedCallBack;
import com.jiubang.ggheart.apps.desks.diy.AppInvoker.IAppInvokerListener;
import com.jiubang.ggheart.common.data.AppExtraAttribute;
import com.jiubang.ggheart.data.statistics.StatisticsAppsInfoData;
import com.jiubang.ggheart.data.theme.ImageExplorer;
import com.jiubang.ggheart.launcher.IconUtilities;
import com.jiubang.ggheart.plugin.notification.NotificationControler;

/**
 * 桌面假图标info基类
 * @author yangguanxiang
 *
 */
public abstract class SpecialAppItemInfo extends AppItemInfo {
	
	public SpecialAppItemInfo() {
		mItemType = IItemType.ITEM_TYPE_SPECIAL_APP;
		setIsSysApp(0);
		mIntent = createIntent();
		mExtra = new AppExtraAttribute(mIntent.getComponent());
	}

	public abstract boolean doInvoke(int from);
	public abstract String getPackageName();
	public abstract String getAction();
	public abstract int getDefaultIconResId();
	public abstract int getDefaultTitleResId();

	public boolean invoke(ArrayList<IAppInvokerListener> listeners, int from) {
		boolean ret = doInvoke(from);
		addActiveCount(ApplicationProxy.getContext(), 1);
		StatisticsAppsInfoData.addAppInfoClickedCount(mIntent, ApplicationProxy.getContext());
		for (IAppInvokerListener listener : listeners) {
			if (listener instanceof NotificationControler) {
				listener.onInvokeApp(mIntent);
			}
		}
		return ret;
	}

	protected Intent createIntent() {
		if (mIntent == null) {
			mIntent = new Intent(getAction());
			ComponentName com = new ComponentName(getPackageName(), getAction());
			mIntent.setComponent(com);
			mIntent.setPackage(getPackageName());
			mIntent.setData(Uri.parse("package:" + getPackageName()));
		}
		return mIntent;
	}

	@Override
	public String getTitle() {
		if (mTitle == null) {
			mTitle = ApplicationProxy.getContext().getResources().getString(getDefaultTitleResId());
		}
		return super.getTitle();
	}
	
	public Drawable getSpecialAppIcon() {
		return getOriginalIcon(getDefaultIconResId());
	}
	
	public Drawable getSpecialAppIcon(AsyncImageLoadedCallBack callback) {
		return getSpecialAppIcon();
	}

	protected Drawable getOriginalIcon(int resId) {
		Context context = ApplicationProxy.getContext();
		Resources res = context.getResources();
		Drawable activityIcon = null;
		if (Machine.isTablet(ApplicationProxy.getContext())) {
			activityIcon = ImageExplorer.getInstance(context).getDrawableForDensity(res, resId);
		}
		if (null == activityIcon) {
			Bitmap bmp = BitmapFactory.decodeResource(res, resId);
			// 缩放图片
			if (bmp != null) {
				activityIcon = new BitmapDrawable(res, IconUtilities.createBitmapThumbnail(bmp,
						context));
			}
		}
		return activityIcon;
	}
}
