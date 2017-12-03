package com.jiubang.ggheart.apps.desks.diy.themescan.coupon;


/**
 * 促销卷
 * @author rongjinsong
 *
 */
public class PromotionBean extends BaseBean {
	
	
	public PromotionBean(String code, String sTime, String eTime) {
		super(code, BaseBean.TYPE_PROMOTION, sTime, eTime);
	}
}
