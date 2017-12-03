package com.jiubang.ggheart.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.go.proxy.SettingProxy;
import com.go.util.BroadCaster.BroadCasterObserver;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.ShortCutInfo;
import com.jiubang.ggheart.launcher.IconUtilities;

public class DeskIcon extends DeskTextView implements BroadCasterObserver {
	public DeskIcon(Context context) {
		super(context);
		selfConstruct();
	}

	public DeskIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
		selfConstruct();
	}

	public DeskIcon(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		selfConstruct();
	}

	/**
	 * 设置图标
	 * 
	 * @param icon
	 *            图标
	 */
	public void setIcon(Drawable icon) {
		icon = IconUtilities.createIconThumbnail(icon, getContext());
		setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onBCChange(int msgId, int param, Object ...object) {
		switch (msgId) {
			case AppItemInfo.INCONCHANGE : {
				if (object != null && object[0] instanceof Drawable) {
					final Object tag = getTag();
					final Drawable icon = (Drawable) object[0];
					post(new Runnable() {
						@Override
						public void run() {
							if (tag != null && tag instanceof ShortCutInfo) {
								final Drawable newIconDrawable = ((ShortCutInfo) tag).mIcon;
								setIcon(newIconDrawable);
							} else {
								setIcon(icon);
							}
						}
					});
				}
				break;
			}

			case AppItemInfo.TITLECHANGE : {
				final Object tag = getTag();
				if (tag != null && tag instanceof ShortCutInfo) {
					final ShortCutInfo info = (ShortCutInfo) tag;
					if (object != null && object[0] instanceof String) {
						final CharSequence title = info.mTitle;
						final boolean showTitle = SettingProxy.getDesktopSettingInfo().isShowTitle();

						post(new Runnable() {
							@Override
							public void run() {
								if (showTitle) {
									if (!info.mIsUserTitle) {
										setText(title);
									}
								} else {
									setText(null);
								}
							}
						});
					}
				}
				break;
			}

			default :
				break;
		}
	}

}
