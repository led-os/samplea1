package com.go.util.sort;

/**
 * 想比较优先级的Info，请接上我吧
 * @author wuziyi
 *
 */
public interface IPriorityLvCompareable extends IBaseCompareable {
	//类似这种方法接口写得有点死，如果可以的话，最好还是接入compareable接口，通过compareTo实现
	public int getPriorityLv();
}
