package com.jiubang.ggheart.plugin;

/**
 * 安全桌面代理工厂
 * @author yejijiong
 *
 */
public class SecurityPoxyFactory {
	
	public static ISecurityPoxy getSecurityPoxy() throws UnsupportSecurityPoxyException {
		throw new UnsupportSecurityPoxyException();
	}
}
