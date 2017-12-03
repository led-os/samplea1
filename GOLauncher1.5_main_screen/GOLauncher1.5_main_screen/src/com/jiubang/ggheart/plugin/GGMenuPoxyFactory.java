package com.jiubang.ggheart.plugin;


/**
 * GGMenu代理工厂
 * @author yejijiong
 *
 */
public class GGMenuPoxyFactory {
	private static IGGMenuPoxy sGGMenuPoxy;
	public static IGGMenuPoxy getGGMenuPoxy() throws UnsupportGGMenuPoxyException { // 200主包使用
		if (sGGMenuPoxy == null) {
			sGGMenuPoxy = new GGMenuPoxy();
		}
		return sGGMenuPoxy;
	}
}
