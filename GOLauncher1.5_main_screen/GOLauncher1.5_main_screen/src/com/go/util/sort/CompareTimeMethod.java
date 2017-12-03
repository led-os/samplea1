package com.go.util.sort;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * 按时间排序逻辑
 * @author wuziyi
 *
 */
public class CompareTimeMethod extends CompareMethod<ICreatTimeCompareable> {

	private PackageManager mPackageManager;
	
	public CompareTimeMethod(Context context) {
		mPackageManager = context.getPackageManager();
	}
	@Override
	protected int getCompareResult(ICreatTimeCompareable compareInfoA,
			ICreatTimeCompareable compareInfoB) {
		long longA = compareInfoA.getTime(mPackageManager);
		long longB = compareInfoB.getTime(mPackageManager);
		int ret = compareLong(longA, longB);
		return ret;
	}


}
