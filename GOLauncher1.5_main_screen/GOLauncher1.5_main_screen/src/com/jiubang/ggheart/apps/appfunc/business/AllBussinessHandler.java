package com.jiubang.ggheart.apps.appfunc.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.go.proxy.MsgMgrProxy;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IScreenAdvertMsgId;
import com.jiubang.ggheart.apps.appfunc.controler.AppDrawerControler;

/**
 * 所有bussiness的返回消息控制
 * @author wuziyi
 *
 */
public class AllBussinessHandler extends Handler {
	// 内部同步消息
	public static final int MSG_STARTSAVE = 0; // 开始保存
	public static final int MSG_FINISHSAVE = 1; // 保存完成
	public static final int MSG_CACHEDAPPS = 2; // 批量安装卸载
	// public static final int MSG_UNINSTALLAPPS = 3; // 批量卸载
	public static final int MSG_SDCARDAPPS = 3; // SDcard缓存数据
	public static final int MSG_FINISHSORT = 4; // 排序完成
	public static final int MSG_SORTFAILED = 5; // 排序失败
	public static final int MSG_FINISH_INIT_VIEWS = 6; // 初始化线程完成
	public static final int MSG_BATADD = 7; // 分批添加x个
	public static final int MSG_FINISHLOADINGSDCARD = 8; // SD卡加载完毕
	public static final int MSG_STARTLOADINGAPP = 9; // 开始加载
	public static final int MSG_ADDITEM = 10; // 添加元素
	public static final int MSG_ADDITEMS = 11; // 添加多个元素
	public static final int MSG_REMOVEITEM = 12; // 删除元素
	public static final int MSG_REMOVEITEMS = 13; // 删除多个元素
	public static final int MSG_REFRESH_APPDRAWER = 14; // 刷新功能表
	public static final int MSG_REFRESH_FOLDERBAR_TARGET = 15; // 刷新顶部文件夹工具条单个元素
	public static final int MSG_ALL_INIT_DONE = 16;
	public static final int MSG_ARRANGE_END = 17;
	
	private AppDrawerControler mAppDrawerControler;
	
	private boolean mIsInitedAllFunItemInfo = false;
	
	private boolean mIsStartedInitAllApp = false;
	
	private Context mContext;
	
	private boolean mIsFirstInit = true;
	
	private Object mSaveLock = new Object();
	
	public AllBussinessHandler(Context context, AppDrawerControler appDrawerControler) {
		super(Looper.getMainLooper());
		mContext = context;
		mAppDrawerControler = appDrawerControler;
	}

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		switch (msg.what) {
			case MSG_STARTSAVE: {
				// 目前是废渣广播
				mAppDrawerControler.broadCast(AppDrawerControler.STARTSAVE, 0);
			}
				break;
			case MSG_FINISHSAVE: {
				// 目前是废渣广播
				mAppDrawerControler.broadCast(AppDrawerControler.FINISHSAVE, 0);
				setInitedAllFunItemInfo(true, true);
			}
				break;
			case MSG_CACHEDAPPS: {
				mAppDrawerControler.handleCachedAppsList();
			}
				break;
			case MSG_SDCARDAPPS: {
				// 目前是废渣广播
			}
				break;
			case MSG_FINISHSORT: {
				mAppDrawerControler.broadCast(AppDrawerControler.SORTFINISH, 0);
			}
				break;
			case MSG_SORTFAILED: {
//				// 目前是废渣广播
			}
				break;
			case MSG_FINISH_INIT_VIEWS : {
				boolean isFirstCreate = (Boolean) msg.obj;
				if (isFirstCreate) {
					// 如果是第一次写入数据库的话，初始化完成标志延后到排序结束
					mAppDrawerControler.startSaveThread();
					// 下面这一行使用之后，会引起数据库park锁，死锁，可作为案例研究
//					mAppDrawerControler.arrangeAppAuto(1, false);
				} else {
					setInitedAllFunItemInfo(true, false);
				}
				
				// add by licanhui
				//功能表加载成功，开始拉取广告。
//				if (!Machine.isKorea(mActivity)) {
					// Should be deleted 
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_ADVERT_BUSINESS,
						IScreenAdvertMsgId.START_REQUEST_ADVERT_DATA, -1);
//				}
				// add by licanhui 2013-6-9 end
			}
				break;
			case MSG_BATADD: {
				mAppDrawerControler.broadCast(AppDrawerControler.BATADD, 0);
			}
				break;
			case MSG_FINISHLOADINGSDCARD: {
				// 目前是废渣广播
				mAppDrawerControler.broadCast(AppDrawerControler.FINISHLOADINGSDCARD, 0);
			}
			case MSG_STARTLOADINGAPP: {
				mIsStartedInitAllApp = true;
			}
				break;
			case MSG_ADDITEM : {
				mAppDrawerControler.broadCast(AppDrawerControler.ADDITEM, msg.arg1, msg.obj);
			}
				break;
			case MSG_ADDITEMS : {
				mAppDrawerControler.broadCast(AppDrawerControler.ADDITEMS, msg.arg1, msg.obj);
			}
				break;
			case MSG_REMOVEITEM : {
				mAppDrawerControler.broadCast(AppDrawerControler.REMOVEITEM, msg.arg1, msg.obj);
			}
				break;
			case MSG_REMOVEITEMS : {
				mAppDrawerControler.broadCast(AppDrawerControler.REMOVEITEMS, 0, msg.obj);
			}
				break;
			case MSG_REFRESH_APPDRAWER : {
				mAppDrawerControler.broadCast(AppDrawerControler.REFREASH_APPDRAWER, 0);
			}
				break;
			case MSG_REFRESH_FOLDERBAR_TARGET : {
				mAppDrawerControler.broadCast(AppDrawerControler.REFREASH_FOLDERBAR_TARGET, 0, msg.obj);
			}
				break;
			case MSG_ARRANGE_END : {
				mAppDrawerControler.broadCast(AppDrawerControler.ARRANGE_END, 0, msg.obj);
			}
				break;
			default:
				break;
		}
	}

	public boolean isInitedAllFunItemInfo() {
		return mIsInitedAllFunItemInfo;
	}

	public boolean isStartedInitAllApp() {
		return mIsStartedInitAllApp;
	}
	
	public void resetInitFlag() {
		mIsInitedAllFunItemInfo = false;
		mIsStartedInitAllApp = false;
	}
	
	public boolean isFirstInit() {
		return mIsFirstInit;
	}

	public void setInitedAllFunItemInfo(boolean isInitedAllFunItemInfo, boolean isFirstCreatDB) {
		mIsInitedAllFunItemInfo = isInitedAllFunItemInfo;
		if (mIsFirstInit) {
			mAppDrawerControler.broadCast(AppDrawerControler.FIRST_INIT_DONE, 0, isFirstCreatDB);
			mIsFirstInit = false;
		} else {
			mAppDrawerControler.broadCast(AppDrawerControler.RELOAD_INIT_FINISH, 0, isFirstCreatDB);
		}
	}


}
