package com.jiubang.ggheart.data.info;

import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.Log;

import com.go.util.log.LogConstants;
import com.jiubang.ggheart.launcher.IconUtilities;
/**
 * 系统app信息，包括系统文件夹
 * @author jiangxuwen
 *
 */
public class SysAppInfo {
	private final static String TAG = "SysAppInfo";

	public static ShortCutInfo createFromShortcut(Context context, Intent data) {
		Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
		String name = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
		Bitmap bitmap = null;
		try {
			bitmap = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
		} catch (ClassCastException e) {
			// 修改：敖日明 2011-12-30
			// Catch Exception后，不需要打印信息了
			// e.printStackTrace();
			// Log.e(tag, "Intent.EXTRA_SHORTCUT_ICON: "
			// + data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON)
			// .toString());
			// 这里有会出现空指针 因为无Intent.EXTRA_SHORTCUT_ICON_RESOURCE可以Get出来
			// Log.i(tag, "Intent.EXTRA_SHORTCUT_ICON_RESOURCE: "
			// + data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE)
			// .toString());
		}
		Drawable icon = null;
		boolean filtered = false;
		boolean customIcon = false;
		ShortcutIconResource iconResource = null;

		if (bitmap != null) {
			try {
				final Resources resources = context.getResources();
				icon = new BitmapDrawable(resources, IconUtilities.createBitmapThumbnail(bitmap,
						context));
				filtered = true;
				customIcon = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Parcelable extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
			if (extra != null && extra instanceof ShortcutIconResource) {
				try {
					iconResource = (ShortcutIconResource) extra;
					final PackageManager packageManager = context.getPackageManager();
					Resources resources = packageManager
							.getResourcesForApplication(iconResource.packageName);
					final int id = resources.getIdentifier(iconResource.resourceName, null, null);
					icon = resources.getDrawable(id);
				} catch (Exception e) {
					Log.i(LogConstants.HEART_TAG, "Could not load shortcut icon: " + extra);
				}
			}
		}

		if (icon == null) {
			icon = context.getPackageManager().getDefaultActivityIcon();
		}

		final ShortCutInfo info = new ShortCutInfo();
		info.mFiltered = filtered;
		info.mTitle = name;
		info.mIntent = intent;
		info.mItemType = IItemType.ITEM_TYPE_SHORTCUT;
		info.mIcon = IconUtilities.createIconThumbnail(icon, context);
		if (iconResource != null) {
			info.mIconResource = iconResource;
		}

		return info;
	}
}
