package com.go.util.sort;

/**
 * 比较info大小的方法
 * @author wuziyi
 *
 */
public class CompareSizeMethod extends CompareMethod<IPkgSizeCompareable> {

	@Override
	protected int getCompareResult(IPkgSizeCompareable compareInfoA,
			IPkgSizeCompareable compareInfoB) {
		long timeA = compareInfoA.getSize();
		long timeB = compareInfoB.getSize();
		return compareLong(timeA, timeB);
	}
	
}
