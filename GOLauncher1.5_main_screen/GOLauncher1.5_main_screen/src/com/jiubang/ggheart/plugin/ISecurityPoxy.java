package com.jiubang.ggheart.plugin;

import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.FunAppItemInfo;

/**
 * 安全桌面代理接口
 * @author yejijiong
 *
 */
public interface ISecurityPoxy {
	/**
	 * 表示没有进行过云查杀
	 */
//	public static final int DANGER_LEVEL_NONE = -2;
	/**
	 * 查杀结果未知
	 */
//	public static final int DANGER_LEVEL_UNKNOW = -1;
	/**
	 * 应用安全状态：安全
	 */
//	public static final int DANGER_LEVEL_SAFE = 0;
	/**
	 * 应用安全状态：危险
	 */
//	public static final int DANGER_LEVEL_UNSAFE = 1;
	
	/**
	 * 安全状态发生改变，跟AppItemInfo里面的SECURITY_STATE_CHANGED值要保持一致
	 */
	public static final int SECURITY_STATE_CHANGED = AppItemInfo.SECURITYCHANGED;
	
	/**
	 * 安全状态发生改变，跟AppItemInfo里面的SECURITY_STATE_CHANGED值要保持一致
	 */
	public static final int DOCK_SECURITY_STATE_CHANGED = 4;
	/**
	 * 获取安全级别
	 * @param info
	 * @return
	 */
	public int getDangerLevel(FunAppItemInfo info);
	/**
	 * 获取安全级别
	 * @param info
	 * @return
	 */
	public int getDangerLevel(AppItemInfo info);
	
	/**
	 * 清除安全标志
	 */
	public void clearSecurityResult();
}
