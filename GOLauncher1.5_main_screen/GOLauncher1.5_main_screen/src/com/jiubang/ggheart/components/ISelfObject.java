package com.jiubang.ggheart.components;

/**
 * 
 * 自身对象接口 相当于构造、析构函数 避免内存泄露
 * 
 */
public interface ISelfObject {
	public void selfConstruct();

	public void selfDestruct();
}
