package com.go.util.sort;


/**
 * 按优先等级排序逻辑
 * @author wuziyi
 *
 */
public class ComparePriorityMethod extends CompareMethod<IPriorityLvCompareable> {

	@Override
	protected int getCompareResult(IPriorityLvCompareable compareInfoA, IPriorityLvCompareable compareInfoB) {
		int intA = compareInfoA.getPriorityLv();
		int intB = compareInfoB.getPriorityLv();
		int ret = compareInt(intA, intB);
		return ret;
	}
}
