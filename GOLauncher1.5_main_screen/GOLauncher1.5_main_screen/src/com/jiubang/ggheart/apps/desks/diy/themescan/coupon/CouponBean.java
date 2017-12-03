package com.jiubang.ggheart.apps.desks.diy.themescan.coupon;

/**
 * 优惠卷数据bean
 * @author rongjinsong
 *
 */
public class CouponBean extends BaseBean {

	public static final String TAG_FACEVALE = "facevale";

	private int mFaceVale;

	public CouponBean(String code, int value, String stime, String etime) {
		super(code, BaseBean.TYPE_COUPON, stime, etime);
		mFaceVale = value;
	}

	public void setFaceValue(int value) {
		mFaceVale = value;
	}

	public int getFaceValue() {
		return mFaceVale;
	}
}
