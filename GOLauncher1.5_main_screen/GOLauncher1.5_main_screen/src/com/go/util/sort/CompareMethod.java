package com.go.util.sort;

/**
 * 排序方法的逻辑基类
 * @author wuziyi
 *
 * @param <T>
 */
public abstract class CompareMethod<T extends IBaseCompareable> {
	public static final int ASC = 0;
	public static final int DESC = 1;
	
	private CompareMethod<IBaseCompareable> mSortMethod;
	/**
	 * 排序方式，默认升序
	 */
	protected int mOrder;

	public void setOrder(int order) {
		mOrder = order;
	}
	/**
	 * 设置下一个比较方法</br>
	 * 解释：如果当前的比较方法比较不出结果，则使用传进来的比较方法继续比较
	 * @param sortMethod 下一个比较方法
	 */
	public void setNextMethod(CompareMethod<? extends IBaseCompareable> sortMethod) {
		mSortMethod = (CompareMethod<IBaseCompareable>) sortMethod;
	}
	
	/**
	 * 外部启动比较的方法接口
	 * @param compareInfoA
	 * @param compareInfoB
	 * @return ASC 0:元素相等  DESC 0:元素相等</br>
	 * 		   ASC -1:A比B小  DESC -1:A比B小</br>
	 * 		   ASC 1:A比B大 DESC 1:A比B小
	 */
	public int doCompare(T compareInfoA, T compareInfoB) {
		if (mOrder != ASC && mOrder != DESC) {
			throw new IllegalAccessError("mOrder is error, current value is:" + mOrder);
		}
		int ret = getCompareResult(compareInfoA, compareInfoB);
		if (ret == 0) {
			if (mSortMethod != null) {
				return mSortMethod.doCompare(compareInfoA, compareInfoB);
			} else {
				return 0;
			}
		} else {
			return ret;
		}
	}
	
	/**
	 * 具体的比较逻辑写在这里
	 * @param compareInfoA
	 * @param compareInfoB
	 * @return 0:元素相等 -1：A比B小 1：A比B大
	 */
	protected abstract int getCompareResult(T compareInfoA, T compareInfoB);
	
	//以下是基础类型的比较方法，供给子类使用
	protected int compareInt(int intA, int intB) {
		int ret = 0;
		if (intA > intB) {
			ret = 1;
		} else if (intA < intB) {
			ret = -1;
		}
		if (ret != 0 && mOrder != ASC) {
			if (ret < 0) {
				ret = 1;
			} else {
				ret = -1;
			}
		}
		return ret;
	}
	
	protected int compareLong(long longA, long longB) {
		int ret = 0;
		if (longA > longB) {
			ret = 1;
		} else if (longA < longB) {
			ret = -1;
		}
		if (ret != 0 && mOrder != ASC) {
			if (ret < 0) {
				ret = 1;
			} else {
				ret = -1;
			}
		}
		return ret;
	}
}
