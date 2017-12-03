package com.jiubang.ggheart.zeroscreen;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2013-8-15]
 */

public abstract class ZeroScreenBaseView extends LinearLayout {
	protected Context mContext;
	protected LayoutInflater mInflater = null;
	
	public ZeroScreenBaseView(Context context) {
		super(context);
		initView(context);
	}
	
	public ZeroScreenBaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public void initView(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/**
	 * <br>功能简述:点击桌面返回按钮
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public abstract void onKeyBack();
	
	/**
	 * <br>功能简述:进入0屏
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public abstract void enterToZeroScreen();
	
	/**
	 * <br>功能简述:离开0屏
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public abstract void leaveToZeroScreen();
	
	/**
	 * <br>功能简述:销毁view
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public abstract void onDestory();
	
}
