package com.jiubang.ggheart.plugin.mediamanagement;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;

import com.gau.go.launcherex.R;
import com.go.proxy.MsgMgrProxy;
import com.go.util.animation.AnimationFactory;
import com.go.util.file.media.FileInfo;
import com.golauncher.message.IAppCoreMsgId;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IMediaManagementMsgId;
import com.jiubang.core.framework.AbstractFrame;
import com.jiubang.core.framework.IFrameManager;
import com.jiubang.core.framework.IFrameworkMsgId;
import com.jiubang.core.mars.XComponent;
import com.jiubang.ggheart.apps.appfunc.controler.MediaFileSuperVisor;
import com.jiubang.ggheart.apps.appfunc.controler.SwitchControler;
import com.jiubang.ggheart.apps.appfunc.controler.XAEngineControler;

/**
 * 资源管理层
 * @author yejijiong
 *
 */
public class MediaManagementFrame extends AbstractFrame {
	/**
	 * 本层布局
	 */
	private FrameLayout mMediaManagementFrameLayout;
	private MediaManagementMainView mMediaManagementMainView;
	public static boolean sIsMediaMangagementVisable = false;
	public static boolean sIsMediaManagementInited = false;
	
	public MediaManagementFrame(Activity activity, IFrameManager frameManager, int frameId) {
		super(activity, frameManager, frameId);
		
		mMediaManagementFrameLayout = new FrameLayout(activity);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
		mMediaManagementFrameLayout.setLayoutParams(lp);
		MediaManagementMainView.createInstance(mActivity);
		mMediaManagementMainView = MediaManagementMainView.getInstance();
		mMediaManagementFrameLayout.addView(mMediaManagementMainView);
		XComponent.mContext = activity;
	}
	
	public static boolean getMediaManagementIsInited() {
		return sIsMediaManagementInited;
	}
	
	public static void setMediaManagementVisable(boolean visible) {
		sIsMediaMangagementVisable = visible;
	}

	@Override
	public View getContentView() {
		return mMediaManagementFrameLayout;
	}

	@Override
	public boolean handleMessage(Object who, int msgId, int param, Object ...obj) {
		super.handleMessage(who, msgId, param, obj);
		switch (msgId) {
			case IFrameworkMsgId.SYSTEM_CONFIGURATION_CHANGED : {
				mMediaManagementMainView.requestLayout();
				break;
			}
			case IMediaManagementMsgId.SWITCH_MEDIA_MANAGEMENT_CONTENT : {
				Object[] params = (Object[]) obj[0];
				mMediaManagementMainView.switchContent(params);
				break;
			}
			case IMediaManagementMsgId.OPEN_IMAGE_BROWSER : {
				mMediaManagementMainView.openImageBrowser(obj[0]);
				break;
			}
			case IMediaManagementMsgId.LOCATE_MEDIA_ITEM : {
				Object[] params = (Object[]) obj[0];
				FileInfo fileInfo = (FileInfo) params[0];
				boolean needFocus = (Boolean) params[1];
				mMediaManagementMainView.locateMediaItem(fileInfo, needFocus);
				break;
			}
			case ICommonMsgId.INDICATOR_CHANGE_SHOWMODE : {
				mMediaManagementMainView.mediaManagementPluginSettingChange(
						MediaManagementMainView.INDICATOR_SHOW_MODE_CHANGE, obj[0]);
				break;
			}
			case IAppCoreMsgId.INDICATOR_CHANGE_POSITION : {
				mMediaManagementMainView.mediaManagementPluginSettingChange(
						MediaManagementMainView.INDICATOR_POSITION_CHANGE, obj[0]);
				break;
			}
			case IAppCoreMsgId.SCREEN_ORIENTATION_CHANGE : {
				mMediaManagementMainView.mediaManagementPluginSettingChange(
						MediaManagementMainView.ORIENTATION_TYPE_CHANGE, obj[0]);
				break;
			}
			case IAppCoreMsgId.SHOW_STATUS_BAR_SHOW_CHANGE : {
				mMediaManagementMainView.mediaManagementPluginSettingChange(
						MediaManagementMainView.SHOW_STATUS_BAR_CHANGE, obj[0]);
				break;
			}
			// 主题改变
			case IAppCoreMsgId.EVENT_THEME_CHANGED : {
				mMediaManagementMainView.mediaManagementPluginThemeChange();
				break;
			}
			case IMediaManagementMsgId.MEDIA_MANAGEMENT_3D_DRAWER_BG : {
				if (obj[0] != null) {
					BitmapDrawable drawable = (BitmapDrawable) obj[0];
					mMediaManagementMainView.setBackgroundDrawable(drawable);
				}
				break;
			}
			case IMediaManagementMsgId.MEDIA_MANAGEMENT_PROGRESSBAR_SHOW : {
				mMediaManagementMainView.showProgressBar(true);
				break;
			}
			case IMediaManagementMsgId.MEDIA_MANAGEMENT_PROGRESSBAR_HIDE : {
				mMediaManagementMainView.showProgressBar(false);
				break;
			}
			case IMediaManagementMsgId.MEDIA_MANAGEMENT_BACK_TO_MAIN_SCREEN : {
				SwitchControler.getInstance(mActivity).showScreenFrame(true);
				break;
			}
			case IMediaManagementMsgId.OPEN_MEIDA_FILE : {
				if (obj[0] instanceof FileInfo) {
					MediaFileSuperVisor.getInstance(mActivity).openMediaFile((FileInfo) obj[0],
							param, (List) obj[1]);
				}
				break;
			}
			case IMediaManagementMsgId.MEDIA_MANAGEMENT_BACK_FROM_IMAGE_BROWSER : {
				if (obj[0] instanceof Boolean) {
					boolean isBackToSearchFrame = (Boolean) obj[0];
					SwitchControler.getInstance(mActivity).imageBrowserBack(isBackToSearchFrame);
				}
				break;
			}
			case IMediaManagementMsgId.MEDIA_MANAGEMENT_BACK_FROM_MUSIC_PALYER : {
				SwitchControler.getInstance(mActivity).musicPlayBackToSearch();
				break;
			}
			case IMediaManagementMsgId.SHOW_MEDIA_OPEN_SETTING_ACTIVITY : {
				Intent intent = new Intent(mActivity, MediaOpenSettingActivity.class);
				intent.putExtra(MediaOpenSettingActivity.SETTING_EXTRA_KEY, param);
				mActivity.startActivity(intent);
				break;
			}
			case IMediaManagementMsgId.MEDIA_MANAGEMENT_FRAME_HIDE : {
				mMediaManagementMainView.showMenu(false);
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
						IFrameworkMsgId.HIDE_FRAME, IDiyFrameIds.MEDIA_MANAGEMENT_FRAME, true,
						(List) obj[1]);
				if (obj[0] != null && obj[0] instanceof Boolean && (Boolean) obj[0]
						&& getVisibility() == View.VISIBLE) {
					startAnimation(STATE_LEFT);
				} else {
					MsgMgrProxy
							.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.HIDE_FRAME,
									IDiyFrameIds.MEDIA_MANAGEMENT_FRAME, false, null);
				}
			}
			default :
				break;
		}
		return false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		XAEngineControler.resume(mMediaManagementMainView);
	}
	
	@Override
	public void onVisiable(int visibility) {
		super.onVisiable(visibility);
		if (visibility == View.VISIBLE) {
			setMediaManagementVisable(true);
			XAEngineControler.initEngine(mMediaManagementMainView);
			mMediaManagementMainView.setBackgroundResource(R.drawable.guide_black_bg);
		} else {
			setMediaManagementVisable(false);
			XAEngineControler.stop(mMediaManagementMainView);
			mMediaManagementMainView.cleanUp();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		XAEngineControler.destroy();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		XAEngineControler.stop(mMediaManagementMainView);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return mMediaManagementMainView.onKey(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return mMediaManagementMainView.onKey(keyCode, event);
	}
	
	@Override
	public boolean isOpaque() {
		return true;
	}
	
	private static final int STATE_ENTER = 0; // 进入资源管理层
	private static final int STATE_LEFT = 1; // 离开资源管理层
	private void startAnimation(final int state) {
		Animation animation = null;
		if (mMediaManagementMainView != null) {
			int effect = 1;
			switch (state) {
				case STATE_ENTER :
					animation = AnimationFactory.createEnterAnimation(effect, mActivity);
					//为null的时候就是无特效
					if (animation != null && mMediaManagementMainView.getAnimation() == null) {
						mMediaManagementMainView.setDrawingCacheEnabled(true);
						animation.setAnimationListener(new AnimationListener() {
							@Override
							public void onAnimationStart(Animation animation) {
							}
							
							@Override
							public void onAnimationRepeat(Animation animation) {
							}
							
							@Override
							public void onAnimationEnd(Animation animation) {
								clearAnimation(state);
							}
						});
						mMediaManagementMainView.startAnimation(animation);
					} else if (mMediaManagementMainView.getAnimation() == null) {
						clearAnimation(state);
					}
					break;
				case STATE_LEFT :
					animation = AnimationFactory.createExitAnimation(effect, mActivity);
					if (animation != null && mMediaManagementMainView.getAnimation() == null) {
						mMediaManagementMainView.setDrawingCacheEnabled(true);
						animation.setAnimationListener(new AnimationListener() {
							@Override
							public void onAnimationStart(Animation animation) {
							}
							
							@Override
							public void onAnimationRepeat(Animation animation) {
							}
							
							@Override
							public void onAnimationEnd(Animation animation) {
								clearAnimation(state);
							}
						});
						mMediaManagementMainView.startAnimation(animation);
					} else if (mMediaManagementMainView.getAnimation() == null) {
						clearAnimation(state);
					}
					break;
				default :
					break;
			}
		}
	}
	
	private void clearAnimation(int state) {
		if (mMediaManagementMainView != null) {
			mMediaManagementMainView.setDrawingCacheEnabled(false);
			mMediaManagementMainView.clearAnimation();
		}
		if (state == STATE_LEFT) {
		    MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.HIDE_FRAME,
		    		IDiyFrameIds.MEDIA_MANAGEMENT_FRAME, false, null);
		}
	}
}
