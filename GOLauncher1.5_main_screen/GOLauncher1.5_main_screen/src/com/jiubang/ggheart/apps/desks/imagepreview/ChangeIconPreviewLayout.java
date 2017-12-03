/**
 * 
 */
package com.jiubang.ggheart.apps.desks.imagepreview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.gau.go.launcherex.R;
import com.go.util.device.Machine;
import com.jiubang.ggheart.apps.desks.diy.StatusBarHandler;

/**
 * @author liuxinyang
 *
 */
public class ChangeIconPreviewLayout extends RelativeLayout {
	private View mActionBar;
	public ChangeIconPreviewLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	public ChangeIconPreviewLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	public ChangeIconPreviewLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	Drawable mBg = null;
	
	void init(Context context) {
		mBg = context.getResources().getDrawable(R.drawable.new_change_icon_tab_bg);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mActionBar = findViewById(R.id.title);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if (mBg != null) {
			int statusBarHeight = Machine.IS_SDK_ABOVE_KITKAT ? StatusBarHandler
					.getStatusbarHeight() : 0;
			mBg.setBounds(0, 0, getWidth(), mActionBar.getHeight()
					+ statusBarHeight);
			mBg.draw(canvas);
		}
		super.dispatchDraw(canvas);
	}

}
