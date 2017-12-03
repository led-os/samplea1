/**
 * 
 */
package com.jiubang.ggheart.apps.desks.diy.frames.tipsforgl.tutorial;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.gau.go.launcherex.R;
import com.go.proxy.MsgMgrProxy;
import com.go.util.device.Machine;
import com.go.util.graphics.BitmapUtility;
import com.go.util.graphics.DrawUtils;
import com.golauncher.message.IAppDrawerMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.jiubang.core.framework.IFrameworkMsgId;
import com.jiubang.core.message.IMessageHandler;
import com.jiubang.ggheart.apps.desks.diy.StatusBarHandler;
import com.jiubang.ggheart.components.DeskButton;
import com.jiubang.ggheart.components.sidemenuadvert.utils.SideAdvertUtils;
/**
 * @author zhangxi
 *
 */
public class AppDrawerSlideMenuTutorial extends RelativeLayout implements OnClickListener, IMessageHandler {
	
	private DeskButton mOk;
	private BitmapDrawable mScreenMask;   //背景
	private Bitmap mBgBitmap;
	private Bitmap mdstBitmapMask;
	private Context mContext;
	private Paint mPaint;
	
	
	private int mScreenBarHigh;
	
	private boolean mIsGotIt = false;  //是否点击我知道
	
	private final static int SIEDBAR_GUIDE_BGTRANS = 0x00FFFFFF;

	public AppDrawerSlideMenuTutorial(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		MsgMgrProxy.registMsgHandler(this);
//		initViews();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public AppDrawerSlideMenuTutorial(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		MsgMgrProxy.registMsgHandler(this);
//		initViews();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AppDrawerSlideMenuTutorial(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		
		mContext = context;
		MsgMgrProxy.registMsgHandler(this);
//		initViews();
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		initViews();
	}
	
	private void setBgBitmap() {
		
		int width = StatusBarHandler.getDisplayWidth();
		int height = StatusBarHandler.getDisplayHeight();
		if (!StatusBarHandler.isHide()) {
			mScreenBarHigh = StatusBarHandler.getStatusbarHeight();
		}
		height += mScreenBarHigh;
		
		Resources res = mContext.getResources();
		mScreenMask = (BitmapDrawable) res.getDrawable(R.drawable.guide_for_screenfolder_mask);
		mdstBitmapMask = BitmapFactory.decodeResource(res, R.drawable.guide_mask);
		int navBarHeight = Machine.IS_SDK_ABOVE_KITKAT ? DrawUtils.getNavBarHeight() : 0;
		mBgBitmap = BitmapUtility.createScaledBitmap(mScreenMask.getBitmap(), width, height + navBarHeight);
		
	}
	
	private void initViews() {
//		mGuideLightView = new AppDrawGuideLightView(mContext);
//		addView(mGuideLightView);

		mPaint = new Paint();
		
		setBgBitmap();

		mOk = (DeskButton) findViewById(R.id.got_it);
		mOk.setOnClickListener(this);
		
		//设置背景透明色
		setBackgroundColor(SIEDBAR_GUIDE_BGTRANS);
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		mIsGotIt = true;
		
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.REMOVE_FRAME,
				IDiyFrameIds.GUIDE_GL_FRAME, null, null);
		
		MsgMgrProxy.unRegistMsgHandler(this);
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.REMOVE_FRAME,
				IDiyFrameIds.GUIDE_GL_FRAME, null, null);
		
		showGuideView();
		
	}
	
	
	private void showGuideView() {
		
		
		if (!mIsGotIt) {
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
					IFrameworkMsgId.SHOW_FRAME, IDiyFrameIds.GUIDE_GL_FRAME,
					null, null);
		}
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		SideAdvertUtils.log("onDraw");

		mPaint.setFilterBitmap(false);
		SideAdvertUtils.log(" 起始点:" + mScreenBarHigh);

		int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.MATRIX_SAVE_FLAG
				| Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
				| Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);

		canvas.drawBitmap(mBgBitmap, 0, 0, mPaint);

		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
	 
		canvas.drawBitmap(mdstBitmapMask, 0, StatusBarHandler.isHide() ? 0 : StatusBarHandler.getStatusbarHeight(), mPaint);
	 
		mPaint.setXfermode(null);
		canvas.restoreToCount(sc);

		super.onDraw(canvas);
	}
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		SideAdvertUtils.log("draw");
		super.draw(canvas);
	}
	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		SideAdvertUtils.log("dispatchDraw");
		super.dispatchDraw(canvas);	
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		SideAdvertUtils.log("buttom:" + b);
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	public boolean handleMessage(Object who, int msgId, int param, Object ...obj) {
		// TODO Auto-generated method stub
		
		switch (msgId) {
		case IAppDrawerMsgId.SIDE_GUIDE_PORTLAND:
			SideAdvertUtils.log("AppDrawGuideTutorial - 收到消息");
			if (!mIsGotIt) {
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
						IFrameworkMsgId.SHOW_FRAME,
						IDiyFrameIds.GUIDE_GL_FRAME, null, null);
			}
			break;

		default:
			break;
		}
		return false;
	}
	
	@Override
	public int getMsgHandlerId() {
		// TODO Auto-generated method stub
		return IDiyFrameIds.SIDE_GUIDE;
	}

}
