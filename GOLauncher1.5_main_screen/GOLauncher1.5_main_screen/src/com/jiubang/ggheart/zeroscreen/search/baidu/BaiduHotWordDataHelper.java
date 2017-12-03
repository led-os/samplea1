package com.jiubang.ggheart.zeroscreen.search.baidu;

import android.content.Context;
import android.os.Handler;

/**
 * 百度热词工具类
 * @author liulixia
 *
 */
public class BaiduHotWordDataHelper {	
	private static BaiduHotWordDataHelper sInstance;
	public static final String HAO360_HOT_WORD_SEARCH_URL = "http://m.so.com/s?q=XXX&src=go_launcher";
	
	public static synchronized BaiduHotWordDataHelper getInstance() {
		if (sInstance == null) {
			sInstance = new BaiduHotWordDataHelper();
		}
		return sInstance;
	}	
	
	/**
	 * 获取热词
	 * @param context
	 */
	public void getHotWord(Context context, Handler handler) {
		
	}
	
	
	
	public void clear() {
		
	}
	
	public boolean isHao360Search() {
		return false;
	}
}
