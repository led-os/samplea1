package com.jiubang.ggheart.net.form;

/**
 * 
 * <br>类描述: 以Get方式获取数据时的请求参数
 * 
 * @author  liuwenqin
 * @date  [2012-9-12]
 */
class Header {
	private final String mKey;
	private final String mValue;

	public Header(String key, String value) {
		if (key == null || "".equals(key)) {
			throw new IllegalArgumentException("Invalid key");
		}
		this.mKey = key;
		this.mValue = value;
	}

	public String getKey() {
		return this.mKey;
	}

	public String getValue() {
		return this.mValue;
	}
}