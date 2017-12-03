package com.go.location;

/**
 * 手动选择的城市的地址信息
 */
public class PlaceInfo {
	/** 中文下，本地城市地址信息数据库中字段_id <= 5000 */
	public static final int DOMESTIC = 0x01;
	public static final int WORLDWIDE = 0x02;
	/** 中文下，本地城市地址信息数据库中字段_id > 5000 */
	public static final int LOCALWORLD = 0x07;
	/**
	 * 协议1.0之前的旧城市ID
	 */
	public String oldCityId;
	/**
	 * 城市唯一标识
	 */
	public String yahooCode;
	public String cityName;
	public String countryName;
	public String stateName;
	public String areaName;
	public String zipCode;

	public String label;

	public String url;

	public int iPlaceType = WORLDWIDE; // 洲，国家，州或省，市，

	public boolean balphaSort = false;

	public boolean bHLalpha = false;

	public PlaceInfo(int iPlaceType) {
		this.iPlaceType = iPlaceType;
	}

	public PlaceInfo(int iPlaceType, String label) {
		this.iPlaceType = iPlaceType;
		this.label = label;
	}

	public PlaceInfo(String cityName, String stateName, String countryName, String code, int iType) {
		this.cityName = cityName;
		this.stateName = stateName;
		this.countryName = countryName;
		this.yahooCode = code;
		this.iPlaceType = iType;
		combineName();
	}

	public static PlaceInfo parse(String text, int iType) {
		String temp = new String(text);

		PlaceInfo p = new PlaceInfo(iType);
		if (iType == PlaceInfo.WORLDWIDE) {
			int pos = temp.indexOf("#");
			if (pos > 0) {
				p.setCityName(temp.substring(0, pos));
			}

			temp = temp.substring(pos + 1);
			pos = temp.indexOf("#");
			if (pos > 0) {
				p.setStateName(temp.substring(0, pos));
			}

			temp = temp.substring(pos + 1);
			pos = temp.indexOf("#");
			if (pos > 0) {
				p.setCountryName(temp.substring(0, pos));
			}

			// Ex版本（2.0)开始，在英文本地缓存城市中新增新城市ID，兼容旧版本
			temp = temp.substring(pos + 1);
			pos = temp.indexOf("#");
			if (pos > 0) {
				p.setOldCityId(temp.substring(0, pos));
			}

			temp = temp.substring(pos + 1);
			p.setYahooCode(temp);

			p.combineName();
		} else if (iType == PlaceInfo.DOMESTIC || iType == PlaceInfo.LOCALWORLD) {
			p.setCityName(temp);
			p.label = temp;
		}
		return p;
		// int pos1 = text.indexOf ("#");
		// int pos2 = text.lastIndexOf ("#");
		// if (pos1 == pos2)
		// return new PlaceInfo (text.substring (0, pos1), null, text.substring
		// (pos1+1), null);
		// else if (pos2 > pos1)
		// return new PlaceInfo (text.substring (0, pos1), text.substring
		// (pos2+1), text.substring (pos1+1, pos2), null);
		//
		// return null;
	}

	public void setOldCityId(String str) {
		oldCityId = str;
	}

	public void setYahooCode(String str) {
		this.yahooCode = str;
	}

	public void setCityName(String str) {
		this.cityName = str;
	}

	public void setCountryName(String str) {
		this.countryName = str;
	}

	public void setStateName(String str) {
		this.stateName = str;
	}

	public void setZipCode(String str) {
		this.zipCode = str;
	}

	public void setDistrictName(String str) {
		this.areaName = str;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void combineName() {
		StringBuffer tmp = new StringBuffer();
		if (iPlaceType == WORLDWIDE) {
			tmp.append(cityName);
			tmp.append(", ");
			tmp.append(stateName);
			tmp.append(", (");
			tmp.append(countryName);
			tmp.append(")");
		} else if (iPlaceType == DOMESTIC || iPlaceType == LOCALWORLD) {
			tmp.append(areaName);
			tmp.append(", ");
			tmp.append(cityName);
		}
		this.label = tmp.toString();
	}
}