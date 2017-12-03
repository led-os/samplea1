package com.jiubang.ggheart.apps.systemwidget;

import java.text.Collator;
import java.util.Comparator;

import com.jiubang.ggheart.apps.gowidget.ScreenEditItemInfo;

/**
 * 
 * @author zouguiquan
 *
 */
public class WidgetAndShortcutNameComparator implements Comparator<Object> {

	private Collator mCollator;

	WidgetAndShortcutNameComparator() {
		mCollator = Collator.getInstance();
	}

	//	private PackageManager mPackageManager;
	//	private HashMap<Object, String> mLabelCache;
	//	
	//	WidgetAndShortcutNameComparator(PackageManager pm) {
	//		mPackageManager = pm;
	//		mLabelCache = new HashMap<Object, String>();
	//		mCollator = Collator.getInstance();
	//	}
	//	
	//	public final int compare(Object a, Object b) {
	//		String labelA, labelB;
	//		if (mLabelCache.containsKey(a)) {
	//			labelA = mLabelCache.get(a);
	//		} else {
	//			labelA = (a instanceof AppWidgetProviderInfo)
	//					? ((AppWidgetProviderInfo) a).label
	//					: ((ResolveInfo) a).loadLabel(mPackageManager).toString().trim();
	//			mLabelCache.put(a, labelA);
	//		}
	//		if (mLabelCache.containsKey(b)) {
	//			labelB = mLabelCache.get(b);
	//		} else {
	//			labelB = (b instanceof AppWidgetProviderInfo)
	//					? ((AppWidgetProviderInfo) b).label
	//					: ((ResolveInfo) b).loadLabel(mPackageManager).toString().trim();
	//			mLabelCache.put(b, labelB);
	//		}
	//		return mCollator.compare(labelA, labelB);
	//	}

	@Override
	public int compare(Object a, Object b) {
		String titleA = ((ScreenEditItemInfo) a).getTitle();
		String titleB = ((ScreenEditItemInfo) b).getTitle();
		//		return mCollator.compare(titleA, titleB);
		return titleA.compareTo(titleB);
	}
}