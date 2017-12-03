package com.go.util.sort;

import com.jiubang.ggheart.data.info.FunAppItemInfo;

/**
 * 专用于功能表，文件夹中的按时间排序写的，实在想不到其他办法，投降了
 * @author wuziyi
 *
 */
public class CompareTimeInFolder extends CompareMethod<FunAppItemInfo> {

	@Override
	protected int getCompareResult(FunAppItemInfo compareInfoA,
			FunAppItemInfo compareInfoB) {
		long timeA = compareInfoA.getTimeInFolder();
		long timeB = compareInfoB.getTimeInFolder();
		return compareLong(timeA, timeB);
	}

}
