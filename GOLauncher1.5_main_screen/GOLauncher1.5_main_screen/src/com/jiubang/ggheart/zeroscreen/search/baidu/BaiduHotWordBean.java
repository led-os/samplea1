package com.jiubang.ggheart.zeroscreen.search.baidu;

/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  chenshihang
 * @date  [2013-8-15]
 */
public class BaiduHotWordBean {
	public String word = null;
	public String url = null;
	public boolean isHot = false;

	public BaiduHotWordBean() {
		
	}
	
	public BaiduHotWordBean(String word, String url) {
		this.word = word;
		this.url = url;
	}	
}
