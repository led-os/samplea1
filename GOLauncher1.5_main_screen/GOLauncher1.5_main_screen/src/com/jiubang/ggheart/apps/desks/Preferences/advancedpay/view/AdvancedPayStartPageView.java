package com.jiubang.ggheart.apps.desks.Preferences.advancedpay.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import com.gau.go.launcherex.R;

/**
 * 广告预览起始页面
 * @author yejijiong
 *
 */
public class AdvancedPayStartPageView extends AdvancedPayBaseView {

	public AdvancedPayStartPageView(Context context) {
		super(context);
	}
	
	@Override
	public void draw(Canvas canvas) {
//		super.draw(canvas); // 这里不调用父类的draw方法，因为里面根本没有精灵，并且修复稍微滑动一下放手，文字消失的bug
	}
	
	@Override
	public String getSingleMessage() {
	    if (isPromotion()) {
            return null;
        }
		return mContext.getResources().getString(R.string.desksetting_pay_dialog_message_start_page);
	}

	@Override
	public int getBgColor() {
	    if (isPromotion()) {
	        return Color.parseColor("#FFeab323");
	    }
		return Color.parseColor("#FF3DB970");
	}
	
}
