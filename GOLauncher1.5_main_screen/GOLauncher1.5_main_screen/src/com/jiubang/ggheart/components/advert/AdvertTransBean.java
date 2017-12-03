package com.jiubang.ggheart.components.advert;

import com.golauncher.message.IDiyFrameIds;

/**
 * 各个广告业务模块传给广告中心的数据bean
 * @author liuheng
 *
 */
public class AdvertTransBean {
	public static final int NET_MODE_BYTESTREAM = 1;   // 字节流方式
	public static final int NET_MODE_FORM = 2;  // 表单模式
	
	
	public String mUrl;
	public byte[] mPostData;
	
	/**
	 * 周期
	 */
	public long mPeriod = 0;
	
	/**
	 * 广告请求者的Id
	 */
	public int mHandlerId = IDiyFrameIds.INVALID_FRAME;
	
	/**
	 * 请求类型，业务模块内部使用
	 */	
	public int mAdType = -1;
	
	/**
	 * 表单模式or字节流
	 */
	public int mMode = NET_MODE_BYTESTREAM;
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// TEST
	///////////////////////////////////////////////////////////////////////////////////////////////
	public static int sPERIOD = 10000;
	public static int sTYPE = 1;
	public static int sHANDLERID = 1;
	
	public static AdvertTransBean genTestBean() {
		AdvertTransBean ret = new AdvertTransBean();
		ret.mAdType = sTYPE;
		ret.mHandlerId = sHANDLERID;
		ret.mPeriod = sPERIOD;
		
		sPERIOD += 13000;
		sHANDLERID += 1;
		sTYPE += 1;
		return ret;
	}
}
