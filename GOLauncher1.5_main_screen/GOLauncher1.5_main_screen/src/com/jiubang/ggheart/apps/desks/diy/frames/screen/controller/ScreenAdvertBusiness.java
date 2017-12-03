package com.jiubang.ggheart.apps.desks.diy.frames.screen.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import android.util.SparseArray;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.go.launcherex.R;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.go.commomidentify.IGoLauncherClassName;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.ValueReturned;
import com.go.proxy.VersionControl;
import com.go.util.AppUtils;
import com.go.util.device.Machine;
import com.golauncher.message.IAdvertCentertMsgId;
import com.golauncher.message.IAppCoreMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IScreenAdvertMsgId;
import com.golauncher.message.IScreenFrameMsgId;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.core.message.IMessageHandler;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingUtils;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.ScreenUtils;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.ConstValue;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.HttpUtil;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.MessageHttp;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageListBean;
import com.jiubang.ggheart.apps.desks.imagepreview.ImagePreviewResultType;
import com.jiubang.ggheart.apps.gowidget.GoWidgetManager;
import com.jiubang.ggheart.apps.gowidget.gostore.net.SimpleHttpAdapter;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.components.advert.AdvertConstants;
import com.jiubang.ggheart.components.advert.AdvertHomeScreenUtils;
import com.jiubang.ggheart.components.advert.AdvertInfo;
import com.jiubang.ggheart.components.advert.AdvertJsonUntil;
import com.jiubang.ggheart.components.advert.AdvertNotification;
import com.jiubang.ggheart.components.advert.AdvertOpenTipDailog;
import com.jiubang.ggheart.components.advert.AdvertTransBean;
import com.jiubang.ggheart.components.advert.AdvertUtils;
import com.jiubang.ggheart.components.advert.NetImageOperator;
import com.jiubang.ggheart.components.gohandbook.SharedPreferencesUtil;
import com.jiubang.ggheart.components.gohandbook.StringOperator;
import com.jiubang.ggheart.data.AppCore;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.FavoriteInfo;
import com.jiubang.ggheart.data.info.GoWidgetBaseInfo;
import com.jiubang.ggheart.data.info.IItemType;
import com.jiubang.ggheart.data.info.ItemInfo;
import com.jiubang.ggheart.data.info.ShortCutInfo;
import com.jiubang.ggheart.data.info.UserFolderInfo;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 屏幕广告Business
 */
public class ScreenAdvertBusiness implements IMessageHandler {

	private ScreenControler mControler;
	private Context mContext;
	public static final int ADD_GO_WIDGET_BY_FAV_INFO = 1;
	private PreferencesManager mPreferencesManager = null;
	public SharedPreferencesUtil mPreferencesUtil;
	public static final String ADVERT_CAN_REQUEST = "advert_can_request"; //是否可以请求广告数据
	public int mImageDownSize = 0;	//已经完成下载的个数（包括成功和失败）
	private boolean mIsRequsetAgain = false; //是否24小时请求
	public int mAddSize = 0; //一共需要添加多少个图标
	private BroadcastReceiver mNetWorkReceiver = null;	//网络监听器
	public ArrayList<AdvertInfo> mAdvertInfosList;
	private JSONArray mAddScreenJsonArray;	//插入屏幕的临时JSON缓存
	private PreferencesManager mOpenPreferencesManager = null;	//8小时提示缓存

	public ScreenAdvertBusiness(ScreenControler screenControler) {
		mControler = screenControler;
		mContext = ApplicationProxy.getApplication();
		mPreferencesManager = new PreferencesManager(mContext, IPreferencesIds.ADVERT_SCREEN_DATA,
				Context.MODE_WORLD_READABLE);
		mPreferencesUtil = new SharedPreferencesUtil(mContext);
		MsgMgrProxy.registMsgHandler(this);
		init();
	}

	private void init() {
		if (VersionControl.getFirstRun()) {
			new Thread() {
				@Override
				public void run() {
					super.run();
					//add by licanhui 2012-12-6
					//如果是第一次运行桌面。代表不是新用户。则请求15屏广告数据
					setCanRequestAdvertState(true);

					//24小时重新请求
					setCanRequestAgainState(true);
					//add by licanhui 2012-12-6 - end
				}

			}.start();
		}
	}

	@Override
	public int getMsgHandlerId() {
		return IDiyFrameIds.SCREEN_ADVERT_BUSINESS;
	}

	@Override
	public boolean handleMessage(Object who, int msgId, int param, Object... obj) {
		boolean ret = false;
		switch (msgId) {
			case IAppCoreMsgId.EVENT_INSTALL_APP :
			case IAppCoreMsgId.EVENT_INSTALL_PACKAGE : {
				String pkgString = (String) obj[0];
				replaceFavoriteGowidget(pkgString);
				break;
			}

			//判断桌面1、5屏/首屏第一次请求成功后判断是否对屏幕做过修改
			case IScreenAdvertMsgId.SCREEN_CAN_CHANGE_ADVERT_SHORT_CUT : {
				ret = isCanChangeAdvertIcon(obj[0]);
				break;
			}

			//判断桌面首屏是否可以插入广告图标
			case IScreenAdvertMsgId.SCREEN_CAN_ADD_ADVERT_TO_HOME_SCREEN : {
				ret = isCanChangeHomeScreenAdvertIcon();
				break;
			}

			//判断桌面1、5屏是否可以插入广告图标	
			case IScreenAdvertMsgId.SCREEN_CAN_ADD_ADVERT_SHORT_CUT : {
				ret = isCanAddAdvertIcon(param);
				break;
			}

			case IScreenAdvertMsgId.SET_CAN_REQUEST_ADVERT_STATE : {
				setCanRequestAdvertState((Boolean) obj[0]);
				break;
			}
			case IScreenAdvertMsgId.SET_CAN_REQUEST_AGAIN_STATE : {
				setCanRequestAgainState((Boolean) obj[0]);
				break;
			}
			case IScreenAdvertMsgId.REQUEST_ADVERT_STAT_CLICK_ACTION : {
				String pkgName = (String) obj[0];
				String idString = (String) obj[1];
				String clickUrl = (String) obj[2];
				String mapId = (String) obj[3];
				requestAdvertStatOnClick(pkgName, idString, clickUrl, mapId);
				break;
			}
			case IScreenAdvertMsgId.START_REQUEST_ADVERT_DATA : {
				requestAdvertData();
				break;
			}
			case IScreenAdvertMsgId.SET_ADVERT_APP_IS_OPEN : {
				Intent intent = (Intent) obj[0];
				setAdvertAppIsOpen(intent);
				break;
			}
			case IScreenAdvertMsgId.SET_OPEN_CACHE : {
				String pkgName = (String) obj[0];
				String content = (String) obj[1];
				setOpenCache(pkgName, content);
				break;
			}
			case IScreenAdvertMsgId.REQUEST_ADVERT_STAT_INSTALL : {
				String appName = (String) obj[0];
				requestAdvertStatInstall(appName);
				break;
			}
			case IScreenAdvertMsgId.REQUEST_ADVERT_STAT_INSTALL_WIDGET : {
				String appName = (String) obj[0];
				requestAdvertStatInstallWidget(appName);
				break;
			}
			case IScreenAdvertMsgId.CHECK_IS_NOT_OPEN : {
				checkIsNotOpen();
				break;
			}
			case IScreenAdvertMsgId.CHECK_REQUEST_AGAIN : {
				checkRequestAgain();
				break;
			}
			case IAdvertCentertMsgId.ADVERT_REQUEST_ON_FINISH : {
				//解析请求数据
				IResponse response = (IResponse) obj[1];
				ArrayList<AdvertInfo> advertInfosList = responseJson(response);
				if (advertInfosList != null && advertInfosList.size() > 0) {
					Message msg = mHandler.obtainMessage(AdvertConstants.GET_ADVERT_DATA_SUCCESS);
					msg.obj = advertInfosList;;
					mHandler.sendMessage(msg);
				} else {
					Message msg = mHandler.obtainMessage(AdvertConstants.GET_ADVERT_DATA_FAIL);
					mHandler.sendMessage(msg);
				}
				break;
			}
			case IAdvertCentertMsgId.ADVERT_REQUEST_ON_EXCEPTION : {
				Message msg = mHandler.obtainMessage(AdvertConstants.GET_ADVERT_DATA_FAIL);
				mHandler.sendMessage(msg);
				break;
			}
			case IScreenAdvertMsgId.CHECK_IS_IN_ADVERT_LIST : {
				ValueReturned value = (ValueReturned) obj[0];
				String pkgName = (String) obj[1];
				value.mConmused = checkInAdvertList(pkgName);
				break;
			}
		}
		return ret;
	}

	/**
	 * <br>功能简述:解析请求数据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param response
	 * @return
	 */
	private ArrayList<AdvertInfo> responseJson(IResponse response) {
		if (response != null && response.getResponse() != null
				&& (response.getResponse() instanceof String)) {
			try {
				String responseString = response.getResponse().toString();
				JSONObject json = new JSONObject(responseString);
				JSONObject result = json.getJSONObject(MessageListBean.TAG_RESULT);
				int status = result.getInt(MessageListBean.TAG_STATUS);

				//请求成功
				if (status == ConstValue.STATTUS_OK) {
					//保存请求时间
					String ltsString = String.valueOf(json.optLong("lts"));
					saveLtsString(mContext, ltsString);

					JSONArray msgsArray = json.getJSONArray("msgs");
					ArrayList<AdvertInfo> advertInfosList = getAdvrtArrary(mContext, msgsArray,
							true);
					return advertInfosList;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * <br>功能简述:
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param msgsArray
	 * @param isFile 是否需要判断是否文件夹
	 * @return
	 */
	public static ArrayList<AdvertInfo> getAdvrtArrary(Context context, JSONArray msgsArray,
			boolean isFile) {
		if (msgsArray == null) {
			return null;
		}
		ArrayList<AdvertInfo> advertInfosList = new ArrayList<AdvertInfo>();
		try {
			int msgsSize = msgsArray.length();
			for (int i = 0; i < msgsSize; i++) {
				AdvertInfo advertInfo = new AdvertInfo();
				JSONObject msgJsonObject = msgsArray.getJSONObject(i);
				advertInfo.mId = msgJsonObject.optString("id");
				advertInfo.mTitle = msgJsonObject.optString("title");
				advertInfo.mDetail = msgJsonObject.optString("detail");
				advertInfo.mPackageName = msgJsonObject.optString("packagename");
				advertInfo.mIcon = msgJsonObject.optString("icon");
				advertInfo.mActtype = msgJsonObject.optInt("acttype");
				advertInfo.mActvalue = msgJsonObject.optString("actvalue");
				int screen = msgJsonObject.optInt("screen", 0);

				if (screen > 0) {
					advertInfo.mScreen = screen - 1;	//后台返回的是第1屏和5.桌面要对应-1
				} else {
					advertInfo.mScreen = 0;
				}
				advertInfo.mPos = msgJsonObject.optInt("pos", -1);
				advertInfo.mStartTime = msgJsonObject.optString("stime_start");
				advertInfo.mEndTime = msgJsonObject.optString("stime_end");
				advertInfo.mIsfile = msgJsonObject.optInt("isfile", 0);
				advertInfo.mIscarousel = msgJsonObject.optInt("iscarousel", 0);
				advertInfo.mClickurl = msgJsonObject.optString("clickurl");
				advertInfo.mMapid = msgJsonObject.optString("mapid");
				advertInfo.mIsAddBottom = msgJsonObject.optInt("addbottom", 0);

				//判断是否文件夹
				if (advertInfo.mIsfile == 1 && isFile) {
					JSONArray fileArray = msgJsonObject.getJSONArray("filemsg");
					advertInfo.mFilemsg = getAdvrtArrary(context, fileArray, false);
				}

				String packName = advertInfo.mPackageName;
				boolean isAppExist = GoAppUtils.isAppExist(context, packName);
				//如果该包名不存在才需要判断，如果存在就不需要插入了
				if (!isAppExist && 1 <= advertInfo.mPos && advertInfo.mPos <= 16) {
					advertInfosList.add(advertInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return advertInfosList;
	}

	/**
	 * <br>功能简述:保存上次获取消息时服务器下发的lts值
	 * <br>功能详细描述:第一次获取消息该值传0
	 * <br>注意:
	 * @return
	 */
	private static void saveLtsString(Context context, String ltsString) {
		if (context != null && ltsString != null) {
			SharedPreferencesUtil preferencesUtil = new SharedPreferencesUtil(context);
			preferencesUtil.saveString(AdvertConstants.LTS_REQUEST_TIME, "0");
		}
	}

	/**
	 * <br>功能简述:检查广告图标是否存在
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param advertInfo
	 * @param screenIndex
	 * @return
	 */
	public boolean isAdvertIconChange(AdvertInfo advertInfo) {
		//		Log.i("lch", "是否文件夹:" + advertInfo.mIsfile);
		//		Log.i("lch", "该图标所在屏幕:" + advertInfo.mScreen);
		//app图标
		if (advertInfo.mIsfile == AdvertConstants.IS_NO_FILE) {
			//判断对应的图标是否改变
			if (isAdvertAppIconChange(advertInfo.mScreen, advertInfo)) {
				return true;
			}
		}

		//文件夹
		else if (advertInfo.mIsfile == AdvertConstants.IS_FILE) {
			//判断对应的图标是否改变
			if (isAdvertAppIconChange(advertInfo.mScreen, advertInfo)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <br>功能简述:15屏广告图标是否改变
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param cellLayout
	 * @param advertInfo
	 * @return
	 */
	private boolean isAdvertAppIconChange(int screenId, AdvertInfo advertInfo) {
		ArrayList<ItemInfo> infoList = mControler.getScreenSparseArray().get(screenId);
		int screenViewSize = infoList.size();
		//遍历对应的屏幕控件
		for (int i = 0; i < screenViewSize; i++) {
			ItemInfo itemInfo = infoList.get(i);
			if (itemInfo != null && itemInfo instanceof ShortCutInfo) {
				ShortCutInfo cutInfo = (ShortCutInfo) itemInfo;

				if (advertInfo.mCellX == cutInfo.mCellX && advertInfo.mCellY == cutInfo.mCellY
						&& advertInfo.mTitle.equals(cutInfo.mTitle)) {
					//判断是否广告图标
					Intent intent = cutInfo.mIntent;
					if (!intent.getAction().equals(ICustomAction.ACTION_SCREEN_ADVERT)) {
						//						Log.i("lch", "退出：action不一样：" + cutInfo.mTitle);
						return true;
					} else {
						//						Log.i("lch", "app图标存在：" + cutInfo.mTitle);
						return false;
					}
				}
			}
		}
		//		Log.i("lch", "已经改变-app图标：" + advertInfo.mTitle);
		return true;
	}

	/**
	 * <br>功能简述:判断桌面1、5屏第一次请求成功后判断是否对屏幕做过修改
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public boolean isCanChangeAdvertIcon(Object object) {
		try {
			if (object == null || !(object instanceof ArrayList)) {
				return false;
			}

			SparseArray<ArrayList<ItemInfo>> screenSparseArray = mControler.getScreenSparseArray();
			//判断是否有5屏
			if (screenSparseArray.size() != 5) {
				return false;
			}

			int firstScreenViewSize = screenSparseArray.get(0).size();
			int fiveScreenViewSize = screenSparseArray.get(4).size();

			int firstAdvertInfoSize = 0;
			int fiveAdvertInfoSize = 0;
			ArrayList<AdvertInfo> advertInfoList = (ArrayList<AdvertInfo>) object;
			for (AdvertInfo advertInfo : advertInfoList) {
				if (advertInfo.mScreen == 0) {
					firstAdvertInfoSize = firstAdvertInfoSize + 1;
				}

				else if (advertInfo.mScreen == 4) {
					fiveAdvertInfoSize = fiveAdvertInfoSize + 1;
				}
			}

			//判断15屏图标个数是否一致
			if (firstAdvertInfoSize != 0 && firstScreenViewSize != firstAdvertInfoSize) {
				//				Log.i("lch", "1屏图标数量和缓存数量不相同");
				return false;
			}

			if (fiveAdvertInfoSize != 0 && fiveScreenViewSize != fiveAdvertInfoSize) {
				//				Log.i("lch", "5屏图标数量和缓存数量不相同");
				return false;
			}

			//先判断首屏当前图标信息是否和上次缓存个数等其他信息一样
			boolean isCanChangeHomeScreenAdvertIcon = isCanChangeHomeScreenAdvertIcon();
			if (!isCanChangeHomeScreenAdvertIcon) {
				//				Log.i("lch", "首屏图标数量和缓存数量不相同，或者首屏其他图标有改变");
				return false;
			}

			for (AdvertInfo advertInfo : advertInfoList) {
				//				Log.i("lch", "=========================");

				if (advertInfo.mScreen == 0 || advertInfo.mScreen == 4 || advertInfo.mScreen == 2) {
					if (isAdvertIconChange(advertInfo)) {
						return false;
					}
				} else {
					//					Log.i("lch", "mScreen不是15屏/首屏");
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * <br>功能简述:判断是否可以重新请求首屏广告图标
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public boolean isCanChangeHomeScreenAdvertIcon() {
		try {
			SparseArray<ArrayList<ItemInfo>> screenSparseArray = mControler.getScreenSparseArray();
			//判断是否有5屏和是否首屏
			if (screenSparseArray.size() != 5) {
				return false;
			}
			String cacheString = AdvertHomeScreenUtils.getHomeScreenCache(mContext);
			if (!TextUtils.isEmpty(cacheString)) {
				String[] cacheList = cacheString.split(";");
				int cacheSize = cacheList.length;

				ArrayList<ItemInfo> infoList = screenSparseArray.get(2); //获取首屏
				int screenViewSize = infoList.size();
				//				Log.i("lch", "首屏缓存：" + cacheString);
				//				Log.i("lch", "当前屏幕图标个数：" + screenViewSize);
				//				Log.i("lch", "首屏缓存图标个数：" + cacheSize);

				if (cacheSize != screenViewSize) {
					//					Log.i("lch", "缓存图标个数和当前屏幕图标个数不一致");
					return false;
				}

				//遍历对应的屏幕控件
				for (int i = 0; i < screenViewSize; i++) {
					Object object = infoList.get(i);
					String iconInfoString = AdvertHomeScreenUtils.getIconInfoString(object);
					if (!cacheString.contains(iconInfoString)) {
						//						Log.i("lch3", "首屏图标已改变：" + iconInfoString);
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * <br>功能简述:判断是否可以在1 5屏幕插入广告图标
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public boolean isCanAddAdvertIcon(int screenIndex) {
		try {
			SparseArray<ArrayList<ItemInfo>> screenSparseArray = mControler.getScreenSparseArray();
			//判断是否有5屏
			if (screenSparseArray.size() != 5 || screenIndex >= 5) {
				return false;
			}

			int screenSize = screenSparseArray.get(screenIndex).size(); 	//判断第1屏是否有图标
			if (screenSize == 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 如果pkgString是gowidget，尝试更换推荐gowidget视图
	 * 
	 * @param pkgString
	 */
	private void replaceFavoriteGowidget(String pkgName) {
		if (pkgName == null
				|| (!pkgName.contains(ICustomAction.MAIN_GOWIDGET_PACKAGE)
						&& !pkgName.equals(PackageName.GOSMS_PACKAGE)
						&& !pkgName.equals(PackageName.CLEAN_MASTER_PACKAGE)
						&& !pkgName.equals(PackageName.RECOMMAND_TIANQITONG_PACKAGE)
						&& !pkgName.equals(PackageName.RECOMMAND_TTDONDTING_PACKAGE)
						&& !pkgName.equals(PackageName.NEXT_BROWSER_PACKAGE_NAME)
						&& !pkgName.equals(PackageName.CHUBAO_PACKAGE_NAME_STRING) && !pkgName
							.equals(PackageName.BAIDU_MUSIC_PACKAGE_NAME_STRING))) {
			return;
		}
		// 有虚拟widget的时候才替换
		if (!mControler.isExistFavorite()) {
			return;
		}
		if (pkgName.equals(PackageName.GOSMS_PACKAGE)) {
			int versionCode = AppUtils.getVersionCodeByPkgName(mContext, PackageName.GOSMS_PACKAGE);
			if (versionCode < 80) {
				return;
			}
		}

		// 任务管理器需要特殊处理
		GoWidgetManager widgetManager = AppCore.getInstance().getGoWidgetManager();
		pkgName = widgetManager.getInflatePackage(pkgName);
		SparseArray<ArrayList<ItemInfo>> screenSparseArray = mControler.getScreenSparseArray();
		int screenCount = screenSparseArray.size();
		for (int i = 0; i < screenCount; i++) {
			ArrayList<ItemInfo> infoList = screenSparseArray.get(i);
			if (infoList == null) {
				continue;
			}

			int index = 0;
			while (index < infoList.size()) {
				ItemInfo info = (ItemInfo) infoList.get(index);
				if (info != null && info.mItemType == IItemType.ITEM_TYPE_FAVORITE) {
					GoWidgetBaseInfo widgetBaseInfo = ((FavoriteInfo) info).mWidgetInfo;
					// 任务管理器的话要转换一下包名
					if (widgetBaseInfo != null && pkgName.equals(widgetBaseInfo.mPackage)) {
						// 防止屏幕可能被删减，所以推荐widget的当前屏数有变动
						info.mScreenIndex = i;
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
								IScreenFrameMsgId.SCREEN_DELETE_VIEW, i, info, null);

						// 添加新视图
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
								IScreenFrameMsgId.SCREEN_ADD_VIEW, ADD_GO_WIDGET_BY_FAV_INFO, info, null);
						// 暂时保留，如果短信等widget初始化时有默认值则去除
						if (!pkgName.equals(IGoLauncherClassName.DEFAULT_THEME_PACKAGE)) {
							MsgMgrProxy
									.sendMessage(this, IDiyFrameIds.SCREEN,
											IScreenFrameMsgId.SCREEN_RELOAD, ADD_GO_WIDGET_BY_FAV_INFO,
											info, null);
						}
						return; // NOTE：请确认时return还是continue
					}
				}
				index++;
			}
		}
	}

	/**
	 * <br>功能简述:请求服务器获取广告数据
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void requestAdvertData() {
		//设置首屏幕首次默认图标的缓存
		setFistHomeScreenCache();
		
		if (!checkNeedRequestData()) {
			return;
		}

		getAdvertData();
		saveRequestTime(); //设置请求时间
	}

	private void getAdvertData() {
		//原来：用户新安装 , 1/5屏放广告图标，已安装的应用不推； 现在：保留现有规则的前提下,如果用户未安装电子市场,则都不推送 针对200渠道（2014-1-13）
		if (GoAppUtils.isMarketNotExitAnd200Channel(mContext)) {
			return;
		}
		
		String url = AdvertConstants.getUrl("1");
		JSONObject requestJson = getRequestUrlJson();
		// JSONObject pheadJson = getPheadJson();
		// long ltsString = getLtsString();
		AdvertTransBean bean = new AdvertTransBean();
		bean.mHandlerId = getMsgHandlerId();
		bean.mUrl = url;
		bean.mPostData = requestJson.toString().getBytes();
		
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.ADVERT_CENTER,
				IAdvertCentertMsgId.START_REQUEST_ADVERT, -1, bean);
	}

	/**
	 * <br>功能简述:设置请求参数
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @return
	 */
	public JSONObject getRequestUrlJson() {
		JSONObject request = new JSONObject();
		JSONObject pheadJson = getPheadJson();
		long ltsString = getLtsString();
		try {
			request.put("phead", pheadJson);
			request.put("lts", ltsString); //上次获取消息时服务器下发的lts值
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return request;
	}

	/**
	 * <br>功能简述:上次获取消息时服务器下发的lts值
	 * <br>功能详细描述:第一次获取消息该值传0
	 * <br>注意:
	 * @return
	 */
	private long getLtsString() {
		SharedPreferencesUtil preferencesUtil = new SharedPreferencesUtil(mContext);
		String ltsString = preferencesUtil.getString(AdvertConstants.LTS_REQUEST_TIME, "0");
		try {
			long lts = Long.parseLong(ltsString);
			return lts;
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * <br>功能简述:设置参数Phead信息
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @return
	 */
	private JSONObject getPheadJson() {
		JSONObject pheadJson = new JSONObject();
		if (mContext != null) {
			String imei = GoStorePhoneStateUtil.getVirtualIMEI(mContext);
			try {
				pheadJson.put("vps", HttpUtil.getVps(mContext, imei)); //设备信息 vps
				pheadJson.put("launcherid", imei); //桌面id
				pheadJson.put("channel", GoStorePhoneStateUtil.getUid(mContext)); // 渠道号
				Locale locale = Locale.getDefault();
				String language = String.format("%s_%s", locale.getLanguage().toLowerCase(), locale
						.getCountry().toLowerCase());
				pheadJson.put("lang", language);
				//				pheadJson.put("local", locale.getCountry().toLowerCase());
				pheadJson.put("local", Machine.getCountry(mContext)); //先取sim卡.没有然后再去本地

				pheadJson.put("pversion", MessageHttp.PVERSION); //协议版本

				String curVersion = mContext.getString(R.string.curVersion);
				pheadJson.put("cversion", curVersion); //桌面的版本号, String例如：3.16
				pheadJson.put("sdklevel", getAndroidSDKVersion()); //sdklevel
				pheadJson.put("androidid", Machine.getAndroidId()); //androidid

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return pheadJson;
	}

	/**
	 * <br>功能简述:设置首屏幕首次默认图标的缓存
	 * <br>功能详细描述:主要用户判断首屏图标是否改变过
	 * <br>注意:
	 */
	public void setFistHomeScreenCache() {
		String cacheString = AdvertHomeScreenUtils.getHomeScreenCache(mContext);
		//为空代表第一次设置缓存
		if (TextUtils.isEmpty(cacheString)) {
			setHomeScreenCache();
		}
	}

	/**
	 * <br>功能简述:检查是否需要请求服务器获取数据
	 * <br>功能详细描述:
	 * <br>如果没有网络时就会注册网络状态监听,存在网络代表会请求服务器获取数据。不管是否获取成功。否代表已经请求过。那下次就不会再请求了
	 * @return
	 */
	public boolean checkNeedRequestData() {
		//判断是否第一次请求
		boolean isCanRequest = mPreferencesUtil.getBoolean(ADVERT_CAN_REQUEST, false);
		if (!isCanRequest) {
			return false;
		}

		if (DeskSettingUtils.isNoAdvert()) {
			setCanRequestAdvertState(false);	//设置下次不能再请求
			setCanRequestAgainState(false); //设置24小时后也不会重新请求
			return false;
		}

		//15屏幕都不为空。且首屏做过修改的。才不下发广发。不然继续下发。然后过滤
		if (!isCanAddIconToScreen(0) && !isCanAddIconToScreen(4) && !isCanAddIconToHomeScreen()) {
			//设置不能在请求数据
			setCanRequestAdvertState(false);
			return false;
		}

		//判断是否SD卡存在
		if (!isSdCardExist()) {
			return false;
		}

		//判断是否有网络
		boolean isHasNetWork = Machine.isNetworkOK(mContext);
		if (!isHasNetWork) {
			registerNetWorkReceiver();
			return false;
		} else {
			//如果存在网络则代表已经请求过。设置下次不能再请求
			setCanRequestAdvertState(false);
		}
		return true;
	}

	/**
	 * <br>功能简述:设置初始化首页的缓存
	 * <br>功能详细描述:
	 * <br>注意:
	 */

	public void setHomeScreenCache() {
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
				IScreenFrameMsgId.SET_HOME_SCREEN_ICON_CACHE, -1, null, null);
	}

	/**
	 * <br>功能简述:处理请求下载图片
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param data
	 */
	public void handleImageDown(Object data) {
		mImageDownSize = mImageDownSize + 1;
		//判断下载数量是否等于请求数量
		if (mImageDownSize == mAddSize) {
			log("全部图片下载完成！");

			//判断是否24小时后的重新请求，是就检查是否可以清空15屏，图标没有做过任何改变才能清空
			if (mIsRequsetAgain) {
				if (!isClecrAdvertIcon()) {
					log("删除15屏图标-失败！");
					return;
				} else {
					setHomeScreenCache(); //删除首屏图标后。要重新设置当前缓存，不然下面的checkCanAddIcon（）判断缓存时会不通过
					log("删除15屏图标--成功！");
				}
			}

			//判断15屏是否可以插入图标（是否为空且是5屏）
			if (!checkCanAddIcon()) {
				return;
			}

			clearInstallListCache(mContext);	//清空安装缓存提示列表

			mAddScreenJsonArray = new JSONArray();

			boolean canAddScreen1 = isCanAddIconToScreen(0);
			boolean canAddScreen5 = isCanAddIconToScreen(4);

			if (mAdvertInfosList != null && mAdvertInfosList.size() != 0) {
				for (AdvertInfo advertInfo : mAdvertInfosList) {
					//如果第1屏已有图标。过滤不加
					if (!canAddScreen1 && advertInfo.mScreen == 0) {
						continue;
					}
					//如果第5屏已有图标。过滤不加
					if (!canAddScreen5 && advertInfo.mScreen == 4) {
						continue;
					}

					if (advertInfo != null) {
						//不是文件夹
						if (advertInfo.mIsfile == AdvertConstants.IS_NO_FILE) {
							addShortCut(advertInfo);
						}

						//是文件夹
						else if (advertInfo.mIsfile == AdvertConstants.IS_FILE) {
							//不是轮换文件夹
							if (advertInfo.mIscarousel == AdvertConstants.IS_NO_CAROUSEL) {
								ArrayList<AdvertInfo> fileAdvertInfoList = advertInfo.mFilemsg;
								addFolder(advertInfo, fileAdvertInfoList);
							}

							//是轮换文件夹
							else if (advertInfo.mIscarousel == AdvertConstants.IS_CAROUSEL) {
								//获取那个图标可以添加上去
								AdvertInfo carouselAdverttInfo = getCarouselIcon(advertInfo);
								if (carouselAdverttInfo != null) {
									addShortCut(carouselAdverttInfo);
								}
							}
						}
					}
				}

				saveAddScreenCache(); //保存请求JSON缓存到xml
				setHomeScreenCache(); //第一次插入图标完成后把首屏图标信息保存起来
			}
			if (mAdvertInfosList != null) {
				mAdvertInfosList.clear();
			}
		}
	}

	/**
	 * <br>功能简述:添加广告图标到桌面
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param advertInfo
	 */
	private void addShortCut(AdvertInfo advertInfo) {
		try {
			ShortCutInfo shortCutInfo = initShortCutInfo(advertInfo);
			if (shortCutInfo != null) {
				setInstallListCache(mContext, shortCutInfo); //保存15屏幕推荐图标包名列表
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
						IScreenFrameMsgId.SCREEN_ADD_ADVERT_SHORT_CUT, advertInfo.mScreen,
						shortCutInfo, null);
				setAppIconCacheJson(advertInfo);	//设置json缓存
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <br>功能简述:设置插入屏幕的图标缓存信息
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void saveAddScreenCache() {
		if (mAddScreenJsonArray != null) {
			//			log("插入到数据库缓存:" + mAddScreenJsonArray.toString());
			mPreferencesUtil.saveString(AdvertConstants.ADVERT_ADD_SCREEN_CACHE,
					mAddScreenJsonArray.toString());
		}
	}

	/**
	 * <br>功能简述:设置请求时间
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void saveRequestTime() {
		long curTime = System.currentTimeMillis();
		mPreferencesUtil.saveString(AdvertConstants.ADVERT_REQUEST_TIME, String.valueOf(curTime));
	}

	/**
	 * <br>功能简述:保存广告缓存
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param keyName
	 * @param id
	 * @param type
	 */
	public void saveAdvertStatOnClickCache(String keyName, String id, String mapId) {
		if (mPreferencesManager == null) {
			mPreferencesManager = new PreferencesManager(mContext,
					IPreferencesIds.ADVERT_SCREEN_DATA, Context.MODE_WORLD_READABLE);
		}
		StringBuffer buffer = new StringBuffer();

		//通过包命拿对应的数据
		String cacheDataString = mPreferencesManager.getString(keyName, "");

		String idString = id;
		int clickCount = 0;
		int installCount = 0;
		String mapIdString = mapId;
		if (mapIdString == null || mapIdString.equals("")) {
			mapIdString = "0";
		}

		//判断是否有缓存
		if (!cacheDataString.equals("")) {
			log("缓存内容：" + keyName + ":" + cacheDataString);

			String[] item = cacheDataString.split(";");

			if (item != null && item.length == 4) {
				//				idString = item[0];	//消息ID
				clickCount = Integer.parseInt(item[1]);	//点击数量
				installCount = Integer.parseInt(item[2]);; //安装数量
				clickCount = clickCount + 1;
			}
		} else {
			clickCount = clickCount + 1;
		}

		buffer.append(idString).append(";"); 	 //消息id,直接每次用新的id。一个情况是不同屏幕推同一个应用，但id不一样
		buffer.append(clickCount).append(";");	 //点击数
		buffer.append(installCount).append(";"); //安装数
		buffer.append(mapIdString); 			 //统计ID

		log("保存内容：" + keyName + ":" + buffer.toString());
		mPreferencesManager.putString(keyName, buffer.toString());
		mPreferencesManager.commit();
	}

	/**
	 * <br>功能简述:保存广告缓存
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param keyName
	 * @param id
	 * @param type
	 */
	public void saveAdvertStatInstallCache(String keyName) {
		if (mPreferencesManager == null) {
			mPreferencesManager = new PreferencesManager(mContext,
					IPreferencesIds.ADVERT_SCREEN_DATA, Context.MODE_WORLD_READABLE);
		}
		StringBuffer buffer = new StringBuffer();

		//通过包命拿对应的数据
		String cacheDataString = mPreferencesManager.getString(keyName, "");

		//判断是否有缓存
		if (!cacheDataString.equals("")) {
			log("缓存内容：" + keyName + ":" + cacheDataString);

			String[] item = cacheDataString.split(";");

			if (item != null && item.length == 4) {
				String idString = item[0];	//消息ID
				int clickCount = Integer.parseInt(item[1]);	//点击数量
				int installCount = Integer.parseInt(item[2]);; //安装数量
				String mapIdString = item[3];	//统计id

				installCount = installCount + 1; //安装数量+1

				buffer.append(idString).append(";"); 	 //消息id
				buffer.append(clickCount).append(";");	 //点击数
				buffer.append(installCount).append(";"); //安装数
				buffer.append(mapIdString);  //统计ID

				log("保存内容：" + keyName + ":" + buffer.toString());
				mPreferencesManager.putString(keyName, buffer.toString());
				mPreferencesManager.commit();
			}
		} else {
			log("install缓存为空");
		}
	}

	/**
	 * <br>功能简述:统计点击数量
	 * <br>功能详细描述:存在包名就用包名作为缓存的key值，否则用id作为缓存的key
	 * <br>注意:
	 * @param packageName
	 */
	public void requestAdvertStatOnClick(String packageName, String idString, String clickUrl,
			String mapId) {
		if (packageName != null && !packageName.equals("")) {
			saveAdvertStatOnClickCache(packageName, idString, mapId);
		} else {
			if (idString != null && !idString.equals("")) {
				saveAdvertStatOnClickCache(idString, idString, mapId);
			}
		}
		requestAdvertStat(clickUrl);
	}

	/**
	 * <br>功能简述:统计安装数量
	 * <br>功能详细描述:因为安装数量只有存在包名的情况下才设置。所以用包名作为缓存的key值
	 * <br>注意:
	 * @param packageName
	 */
	public void requestAdvertStatInstall(String packageName) {
		if (packageName != null && !packageName.equals("")) {
			saveAdvertStatInstallCache(packageName);
			requestAdvertStat(null);
		}
		try {
			long time = System.currentTimeMillis();
			setOpenCache(packageName, String.valueOf(time));	//设置缓存设置该应用安装的时间
			showOpenDailog(packageName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 功能简述:统计推荐widget安装数量
	 * 功能详细描述:因为安装数量只有存在包名的情况下才设置。所以用包名作为缓存的key值
	 * @param packageName widget的包名
	 */
	public void requestAdvertStatInstallWidget(String packageName) {
		if (packageName != null && !packageName.equals("")) {
			saveAdvertStatInstallCache(packageName);
			requestAdvertStat(null);
		}
		//设置缓存代表这个应用已经打开过
		PreferencesManager openPreferencesManager = new PreferencesManager(mContext,
				IPreferencesIds.ADVERT_NEET_OPEN_DATA, Context.MODE_WORLD_READABLE);
		openPreferencesManager.putString(packageName, AdvertConstants.ADVERT_IS_OPENED);
		openPreferencesManager.commit();
	}

	/**
	 * <br>功能简述:请求上传广告点击安装统计
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param clickUrl 对应的回调地址，只有点击时才需要上传,安装传NULL
	 */
	public void requestAdvertStat(String clickUrl) {
		requestAdvertStatistics(mHandler, clickUrl);
		//TODO：为了赶时间。应该不需要做缓存
		clearStatisticsData(); //点击上次后马上清空数据
	}

	/**
	 * <br>功能简述:清除统计数据
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void clearStatisticsData() {
		if (mPreferencesManager == null) {
			mPreferencesManager = new PreferencesManager(mContext,
					IPreferencesIds.ADVERT_SCREEN_DATA, Context.MODE_WORLD_READABLE);
		}

		Map<String, ?> data = mPreferencesManager.getAll();
		if (data != null) {
			Set<String> keys = data.keySet();
			for (String key : keys) {
				StringBuffer oneBuffer = new StringBuffer();

				Object obj = data.get(key);
				String reason = null;
				if (obj != null && obj instanceof String) {
					reason = (String) obj;
				}

				String[] item = null;
				if (reason != null && !reason.equals("")) {
					item = reason.split(";");
				}

				if (item != null && item.length == 4) {
					String idString = item[0];	//消息ID
					int clickCount = 0;	//点击数量
					int installCount = 0; //安装数量
					String mapIdString = item[3];

					oneBuffer.append(idString).append(";");	//消息id
					oneBuffer.append(clickCount).append(";"); 	//点击量
					oneBuffer.append(installCount).append(";"); //安装量
					oneBuffer.append(mapIdString); //安装量
					mPreferencesManager.putString(key, oneBuffer.toString());
					mPreferencesManager.commit();
					//					log("清空缓存：" + key + ":" + oneBuffer.toString());
				}
			}
		}
	}

	/**
	 * <br>功能简述:24小时后检查是否可以重新请求数据
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void checkRequestAgain() {
		try {
			boolean isCanRequest = mPreferencesUtil.getBoolean(ADVERT_CAN_REQUEST, false);
			//判断是否已经请求过，如果还没请求就直接退出，因为会有监听网络重新重新请求
			if (isCanRequest) {
				log("第一次还没请求成功！等待第一次网络监听请求！");
				return;
			}

			//如果不能请求证明屏幕已经改变过
			boolean isCanRequestAgain = mPreferencesUtil.getBoolean(
					AdvertConstants.ADVERT_24_CAN_REQUEST, false); //设置24小时也不能再请求
			if (!isCanRequestAgain) {
				log("屏幕已经改变过，24不能再请求了！！！");
				return;
			}

			String requestTimeString = mPreferencesUtil.getString(
					AdvertConstants.ADVERT_REQUEST_TIME, "0");
			long requestTime = Long.parseLong(requestTimeString);
			long curTime = System.currentTimeMillis();

			log("requestTime：" + requestTime);
			log("curTime：" + curTime);
			log("时间差：" + (curTime - requestTime));

			//如果大于24个小时就进行请求
			//				if ((curTime - requestTime) >= 0) {
			if ((curTime - requestTime) >= AdvertConstants.ADVERT_24_TIME) {
				log("大于24小时");
				//判断是否有网络
				if (!Machine.isNetworkOK(mContext)) {
					log("24小时请求没有网络！");
					return;
				}

				//判断是否可以重新更新15图标
				if (isCanRefreshScreenIcon()) {
					log("24小时重新请求数据！");
					mIsRequsetAgain = true; //设置24小时请求标志
					getAdvertData();	//重新请求
					saveRequestTime(); //设置请求时间
				} else {
					log("屏幕已修改，设置下次不能在24小时请求");
					setCanRequestAgainState(false); //屏幕已修改，设置下次不能在24小时请求
				}
			}
		} catch (Exception e) {
			return;
		}
	}

	/**
	 * <br>功能简述:设置不可以请求广告数据
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void setCanRequestAdvertState(boolean flag) {
		if (mPreferencesUtil != null) {
			mPreferencesUtil.saveBoolean(ADVERT_CAN_REQUEST, flag);
		}
	}

	/**
	 * <br>功能简述:判断可以插入到屏幕（是否一共5屏，是否1 5屏为空白屏幕）
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public boolean isCanAddIconToScreen(int screenIndex) {
		boolean isCanAddIconToScreen;
		isCanAddIconToScreen = MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_ADVERT_BUSINESS,
				IScreenAdvertMsgId.SCREEN_CAN_ADD_ADVERT_SHORT_CUT, screenIndex, null, null);
		return isCanAddIconToScreen;
	}

	/**
	 * <br>功能简述:判断是否SD卡存在
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public boolean isSdCardExist() {
		boolean isSdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		return isSdCardExist;
	}

	/**
	 * <br>功能简述:检查是否需要请求服务器获取数据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public boolean checkCanAddIcon() {

		//判断是否SD卡存在
		if (!isSdCardExist()) {
			return false;
		}

		boolean canAddScreen1 = isCanAddIconToScreen(0);
		boolean canAddScreen5 = isCanAddIconToScreen(4);
		//判断可以插入到15幕（是否一共5屏，是否1 5屏为空白屏幕）
		if (!canAddScreen1 && !canAddScreen5 && !isCanAddIconToHomeScreen()) {
			return false;
		}

		return true;
	}

	/**
	 * <br>功能简述:判断是否可以插入到首屏图标
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public boolean isCanAddIconToHomeScreen() {
		boolean isCanAddIconToHomeScreen;
		isCanAddIconToHomeScreen = MsgMgrProxy.sendMessage(this,
				IDiyFrameIds.SCREEN_ADVERT_BUSINESS,
				IScreenAdvertMsgId.SCREEN_CAN_ADD_ADVERT_TO_HOME_SCREEN, -1, null, null);
		return isCanAddIconToHomeScreen;
	}

	/**
	 * <br>功能简述:设置不可以24小时后再次请求广告数据
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void setCanRequestAgainState(boolean flag) {
		if (mPreferencesUtil != null) {
			mPreferencesUtil.saveBoolean(AdvertConstants.ADVERT_24_CAN_REQUEST, flag); //设置24小时也不能再请求
		}
	}

	/**
	 * <br>功能简述:清空15屏的图标
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public boolean isClecrAdvertIcon() {
		try {
			//判断是否可以重新更新15图标
			if (isCanRefreshScreenIcon()) {
				log("请求删除15屏/首屏图标！");

				ArrayList<AdvertInfo> advertInfoList = new ArrayList<AdvertInfo>();

				String cacheString = mPreferencesUtil.getString(
						AdvertConstants.ADVERT_ADD_SCREEN_CACHE, "");
				//如果缓存为空，证明第一次请求是失败的。没有缓存
				if (!cacheString.equals("")) {
					JSONArray msgsArray = null;
					msgsArray = new JSONArray(cacheString);
					advertInfoList = AdvertJsonUntil.getAdvrtArrary(mContext, msgsArray, true);
				}
				boolean ret;
				ret = MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
						IScreenFrameMsgId.SCREEN_CLEAR_ADVERT_ICON, -1, advertInfoList, null);
				return ret;
			} else {
				setCanRequestAgainState(false); //屏幕已修改，设置下次不能在24小时请求
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * <br>功能简述:下载图片
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param advertInfo
	 */
	public void downLoadImage(AdvertInfo advertInfo) {
		mAddSize = mAddSize + 1;	//总添加数+1
		getNetImageData(mContext, advertInfo, mHandler);
	}

	/**
	 * <br>功能简述:处理请求返回的广告数据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param data
	 */
	public void handleAdvertData(Object data) {
		if (data != null && data instanceof ArrayList) {
			mAdvertInfosList = (ArrayList<AdvertInfo>) data;
			for (AdvertInfo advertInfo : mAdvertInfosList) {
				if (advertInfo != null) {
					//不是文件夹
					if (advertInfo.mIsfile == AdvertConstants.IS_NO_FILE) {
						downLoadImage(advertInfo);
					}
					//是文件夹
					else if (advertInfo.mIsfile == AdvertConstants.IS_FILE) {
						ArrayList<AdvertInfo> fileAdvertInfoList = advertInfo.mFilemsg;
						if (fileAdvertInfoList != null) {
							for (AdvertInfo fileAdvertInfo : fileAdvertInfoList) {
								downLoadImage(fileAdvertInfo);
							}
						}
					}
				}
			}
		}
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			//下载数据成功
				case AdvertConstants.GET_ADVERT_DATA_SUCCESS :
					log("请求服务器数据--成功!");
					handleAdvertData(msg.obj);
					break;

				//下载数据失败	
				case AdvertConstants.GET_ADVERT_DATA_FAIL :
					log("请求服务器数据--失败！");
					//判断域名是否已做修改，并二次请求一次。
					if (AdvertConstants.getADHostUrl().equals(AdvertConstants.sHosturlBase)) {
						//修改为备用域名，并再次请求一次
						AdvertConstants.setADHostUrl(AdvertConstants.HOSTURL_BASE_SPARE);
						getAdvertData();
					} else {
						//已使用了备用域名，则直接切换回来，不用再次请求
						AdvertConstants.setADHostUrl(AdvertConstants.sHosturlBase);
					}

					break;

				//下载图片成功	
				case AdvertConstants.DOWN_IMAGE_SUCCESS :
					log("下载图片--成功");
					handleImageDown(msg.obj);
					break;

				//下载图片失败
				case AdvertConstants.DOWN_IMAGE_FAIL :
					log("下载图片--失败");
					handleImageDown(msg.obj);
					break;

				//统计上传成功
				case AdvertConstants.STATISTICS_REQUEST_SUCCESS :
					log("统计上传--成功");
					// clearStatisticsData(); //上传成功就要清空本地统计数据
					break;

				//统计上传失败
				case AdvertConstants.STATISTICS_REQUEST_FAIL :
					log("统计上传--失败");
					//判断域名是否已做修改，并二次请求一次。
					if (AdvertConstants.getADHostUrl().equals(AdvertConstants.sHosturlBase)) {
						//修改为备用域名，并再次请求一次
						AdvertConstants.setADHostUrl(AdvertConstants.HOSTURL_BASE_SPARE);
						String clickUrl = null;
						if (msg.obj != null && msg.obj instanceof String) {
							clickUrl = (String) msg.obj;
						}
						requestAdvertStatistics(mHandler, clickUrl);
					} else {
						//已使用了备用域名，则直接切换回来，不用再次请求
						AdvertConstants.setADHostUrl(AdvertConstants.sHosturlBase);
					}

					break;

				default :
					break;
			}
		}
	};

	/**
	 * <br>功能简述:判断桌面是否有改变，是否可以重新请求
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public boolean isCanRefreshScreenIcon() {
		try {
			ArrayList<AdvertInfo> advertInfoList = new ArrayList<AdvertInfo>();

			String cacheString = mPreferencesUtil.getString(
					AdvertConstants.ADVERT_ADD_SCREEN_CACHE, "");
			//如果缓存为空，证明第一次请求是失败的。没有缓存
			if (!cacheString.equals("")) {
				JSONArray msgsArray = null;
				msgsArray = new JSONArray(cacheString);
				advertInfoList = AdvertJsonUntil.getAdvrtArrary(mContext, msgsArray, true);
			}
			boolean isCanAddIconToScreen = MsgMgrProxy
					.sendMessage(this, IDiyFrameIds.SCREEN_ADVERT_BUSINESS,
							IScreenAdvertMsgId.SCREEN_CAN_CHANGE_ADVERT_SHORT_CUT, -1,
							advertInfoList, null);
			if (isCanAddIconToScreen) {
				log("可以插入，屏幕没改变");
				return true;
			} else {
				log("不可以插入，屏幕已改变");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * <br>功能简述：获取需要添加的轮换图标
	 * <br>功能详细描述:遍历轮换图标文件夹，判断哪个图标是没有安装的
	 * <br>注意:
	 * @param fileAdvertInfoList
	 * @return
	 */
	public AdvertInfo getCarouselIcon(AdvertInfo folderAdvertInfo) {
		try {
			ArrayList<AdvertInfo> fileAdvertInfoList = folderAdvertInfo.mFilemsg;
			if (fileAdvertInfoList == null || fileAdvertInfoList.size() == 0) {
				return null;
			}

			Object[] list = fileAdvertInfoList.toArray();
			Arrays.sort(list); //对列表进行轮播优先级排序
			int size = list.length;
			for (int i = 0; i < size; i++) {
				AdvertInfo advertInfo = (AdvertInfo) list[i];
				String pathString = advertInfo.mIcon;
				if (!TextUtils.isEmpty(pathString) && !pathString.startsWith("http://")) {
					String packageName = advertInfo.mPackageName;
					if (!TextUtils.isEmpty(packageName)) {
						boolean isExist = GoAppUtils.isAppExist(mContext, packageName);
						if (!isExist) {
							log("轮播图标不存在,可以插入：" + packageName);
							//设置插入的轮换图标位置和屏幕位置为文件夹的位置
							advertInfo.mScreen = folderAdvertInfo.mScreen;
							advertInfo.mPos = folderAdvertInfo.mPos;
							return advertInfo;
						}
					}
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * <br>功能简述:注册网络监听器，监听网络的联通
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void registerNetWorkReceiver() {
		if (mNetWorkReceiver == null) {
			log("注册网络状态监听！");
			mNetWorkReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
						if (Machine.isNetworkOK(mContext)) {
							log("监听到网络联通！");
							unRegisterNetWorkReceiver(); //取消册网络状态监听
							mHandler.postDelayed(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									requestAdvertData();
								}
							}, 60000);
						}
					}
				}
			};
			IntentFilter filter = new IntentFilter();
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			mContext.registerReceiver(mNetWorkReceiver, filter);
		}
	}

	/**
	 * <br>功能简述:取消册网络状态监听
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void unRegisterNetWorkReceiver() {
		if (mNetWorkReceiver != null) {
			log("取消册网络状态监听！");
			mContext.unregisterReceiver(mNetWorkReceiver);
			mNetWorkReceiver = null;
		}
	}

	public void onDestory() {
		mPreferencesUtil = null;
		if (mAdvertInfosList != null) {
			mAdvertInfosList.clear();
			mAdvertInfosList = null;
		}
	}

	/**
	 * <br>功能简述:设置非文件夹图标的json缓存
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param advertInfo
	 */
	public void setAppIconCacheJson(AdvertInfo advertInfo) {
		JSONObject value = AdvertJsonUntil.setAdvertAppJson(advertInfo);
		mAddScreenJsonArray.put(value);
	}

	/**
	 * <br>功能简述:设置文件夹图标的json缓存
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param advertInfo
	 */
	public void setFolderIconCacheJson(AdvertInfo advertInfo, ArrayList<AdvertInfo> advertInfosList) {
		JSONObject value = AdvertJsonUntil.setAdvertFolderJson(advertInfo, advertInfosList);
		mAddScreenJsonArray.put(value);
	}

	/**
	 * <br>功能简述:添加广告文件夹到桌面
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param advertInfo
	 */
	public void addFolder(AdvertInfo advertInfoFolder, ArrayList<AdvertInfo> advertInfosList) {
		//JSON缓存队列
		ArrayList<AdvertInfo> advertInfosListTemp = new ArrayList<AdvertInfo>();

		UserFolderInfo deskFolder = new UserFolderInfo();
		deskFolder.mInScreenId = System.currentTimeMillis();
		deskFolder.mFeatureTitle = advertInfoFolder.mTitle;

		int[] xy = AdvertUtils.getXY(advertInfoFolder.mPos);
		deskFolder.mCellX = xy[0];
		deskFolder.mCellY = xy[1];

		int size = advertInfosList.size();
		for (int i = 0; i < size; i++) {
			AdvertInfo advertInfo = advertInfosList.get(i);
			ShortCutInfo shortCutInfo = initShortCutInfo(advertInfo);
			if (shortCutInfo != null) {
				deskFolder.add(shortCutInfo);
				advertInfosListTemp.add(advertInfo); //成功后的才写入缓存
				setInstallListCache(mContext, shortCutInfo); //保存15屏幕推荐图标包名列表
			}
		}

		//判断个数是否>0
		ArrayList<ItemInfo> contents = deskFolder.getContents();
		if (contents != null && contents.size() > 0) {
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
					IScreenFrameMsgId.SCREEN_ADD_ADVERT_FOLDER, advertInfoFolder.mScreen,
					deskFolder, null);
			setFolderIconCacheJson(advertInfoFolder, advertInfosListTemp); //设置非文件夹图标的json缓存
		}
	}

	/**
	 * <br>功能简述:获取ShortCutInfo对象。
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param advertInfo 广告对象
	 * @return
	 */
	public ShortCutInfo initShortCutInfo(AdvertInfo advertInfo) {
		if (advertInfo == null) {
			return null;
		}

		ShortCutInfo shortCutInfo = new ShortCutInfo();
		Intent intent = new Intent(ICustomAction.ACTION_SCREEN_ADVERT);
		//要设置不同包命，不然会认为是同一个应用。合并文件夹时会合并
		ComponentName advertCN = new ComponentName(advertInfo.mPackageName,
				ICustomAction.ACTION_SCREEN_ADVERT);
		intent.setComponent(advertCN);
		intent.putExtra(AdvertConstants.ADVERT_ID, advertInfo.mId);	//保存ID
		intent.putExtra(AdvertConstants.ADVERT_ACTTYPE, advertInfo.mActtype); // 保存动作类型
		intent.putExtra(AdvertConstants.ADVERT_ACTVALUE, advertInfo.mActvalue);	//保存跳转信息
		intent.putExtra(AdvertConstants.ADVERT_PACK_NAME, advertInfo.mPackageName);	//保存程序包名
		intent.putExtra(AdvertConstants.ADVERT_CLICK_URL, advertInfo.mClickurl);	//对应的回调地址，只有点击时才需要上传
		intent.putExtra(AdvertConstants.ADVERT_MAPID, advertInfo.mMapid);	//统计id
		intent.putExtra(AdvertConstants.ADVERT_TITLE, advertInfo.mTitle); // 保存标题
		intent.putExtra(AdvertConstants.ADVERT_DETAIL, advertInfo.mDetail); // 详细描述
		intent.putExtra(AdvertConstants.ADVERT_IS_ADD_BOTTOM, advertInfo.mIsAddBottom); // 是否需要添加底座

		shortCutInfo.mIntent = intent;
		shortCutInfo.mInScreenId = System.currentTimeMillis();	//图标id
		shortCutInfo.mItemType = IItemType.ITEM_TYPE_APPLICATION; //类型

		int[] xy = AdvertUtils.getXY(advertInfo.mPos);

		shortCutInfo.mCellX = xy[0];
		shortCutInfo.mCellY = xy[1];

		String titleString = advertInfo.mTitle;	//标题
		shortCutInfo.mFeatureTitle = titleString;	//保存到数据库的标题
		shortCutInfo.setTitle(titleString, true);	//缓存的标题
		//设置图片类型为自定义路径
		String pathString = advertInfo.mIcon;
		//		Log.i("lch", "pathString:" + pathString);
		if (pathString.startsWith("http://")) {
			return null;
		}
		shortCutInfo.setFeatureIcon(null, ImagePreviewResultType.TYPE_IMAGE_FILE, null, 0,
				pathString);

		if (shortCutInfo.prepareFeatureIcon()) {
			if (advertInfo.mIsAddBottom == 0) { // 图标不需要加底座
				shortCutInfo.mIcon = shortCutInfo.getFeatureIcon();
			} else {
				shortCutInfo.mIcon = ScreenUtils.composeCustomIconBack(shortCutInfo
						.getFeatureIcon());
			}
			return shortCutInfo;
		} else {
			return null;
		}
	}

	/**
	 * <br>功能简述:设置8小时请求对应的缓存信息
	 * <br>功能详细描述:没有打开过的设置当前时间值，否则设置ture表示已经打开过
	 * <br>注意:
	 * @param packageName
	 * @param content
	 */
	public void setOpenCache(String packageName, String content) {
		log3("设置缓存setOpenCache():" + packageName + ":" + content);

		if (mOpenPreferencesManager == null) {
			mOpenPreferencesManager = new PreferencesManager(mContext,
					IPreferencesIds.ADVERT_NEET_OPEN_DATA, Context.MODE_WORLD_READABLE);
		}
		mOpenPreferencesManager.putString(packageName, content);
		mOpenPreferencesManager.commit();
	}

	public void checkIsNotOpen() {
		if (mOpenPreferencesManager == null) {
			mOpenPreferencesManager = new PreferencesManager(mContext,
					IPreferencesIds.ADVERT_NEET_OPEN_DATA, Context.MODE_WORLD_READABLE);
		}
		Map<String, ?> allCache = mOpenPreferencesManager.getAll();

		if (allCache != null) {
			Set<String> keys = allCache.keySet();
			int i = 0;
			for (String key : keys) {
				i++;
				Object obj = allCache.get(key);
				String cache = null;
				if (obj != null && obj instanceof String) {
					cache = (String) obj;
				}
				log3(key + ":" + cache);
				//判断是否已经打开过
				if (cache != null && !cache.equals(AdvertConstants.ADVERT_IS_OPENED)) {
					try {
						long cacheTime = Long.parseLong(cache);
						long curTime = System.currentTimeMillis();
						long hour = curTime - cacheTime;

						log3("curTime - cacheTime:" + (curTime - cacheTime));

						//如果大于8个小时就进行同事栏提示
						if (hour >= AdvertConstants.ADVERT_TIPS_TIME) {
							log3("大于8小时");
							setOpenCache(key, String.valueOf(curTime));
							final AppItemInfo appItemInfo = AppDataEngine.getAppName(mContext, key);
							if (appItemInfo != null && appItemInfo.mTitle != null
									&& appItemInfo.mProcessName != null) {
								AdvertNotification.showNotification(mContext, appItemInfo.mTitle,
										appItemInfo.mProcessName, appItemInfo.mIcon, i);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * <br>功能简述:安装完后打开对话框提示打开
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param packageName
	 */
	public void showOpenDailog(final String packageName) {
		Intent intent = new Intent(mContext, AdvertOpenTipDailog.class);
		intent.putExtra(AdvertConstants.ADVERT_PACK_NAME, packageName);
		mContext.startActivity(intent);
	}

	public void setAdvertAppIsOpen(Intent intent) {
		try {
			if (intent == null || intent.getComponent() == null
					|| intent.getComponent().getPackageName() == null) {
				return;
			}

			String packageName = intent.getComponent().getPackageName();
			log3("获取intent的包名packageName：" + packageName);

			if (mOpenPreferencesManager == null) {
				mOpenPreferencesManager = new PreferencesManager(mContext,
						IPreferencesIds.ADVERT_NEET_OPEN_DATA, Context.MODE_WORLD_READABLE);
			}
			Map<String, ?> allCache = mOpenPreferencesManager.getAll();

			if (allCache != null) {
				Set<String> keys = allCache.keySet();
				for (String key : keys) {
					if (key.equals(packageName)) {
						log3("设置已经打开过：" + packageName);
						mOpenPreferencesManager.putString(packageName,
								AdvertConstants.ADVERT_IS_OPENED);
						mOpenPreferencesManager.commit();
						return;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <br>功能简述:检测是否需要抖动的图标（推荐图标才抖动）
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param intent
	 * @return
	 */
	public boolean checkNeedShake(Intent intent) {
		//		log3("checkNeedShake()");
		if (intent == null || intent.getComponent() == null
				|| intent.getComponent().getPackageName() == null) {
			return false;
		}
		//获取包名
		String packageName = intent.getComponent().getPackageName();
		//		log3("获取intent的包名packageName：" + packageName);

		if (mOpenPreferencesManager == null) {
			mOpenPreferencesManager = new PreferencesManager(mContext,
					IPreferencesIds.ADVERT_NEET_OPEN_DATA, Context.MODE_WORLD_READABLE);
		}
		Map<String, ?> allCache = mOpenPreferencesManager.getAll();

		if (allCache != null) {
			Set<String> keys = allCache.keySet();
			for (String key : keys) {
				Object obj = allCache.get(key);
				String cache = null;
				if (obj != null && obj instanceof String) {
					cache = (String) obj;
				}
				log3(key + ":" + cache);
				// 判断是否已经打开过
				if (key.equals(packageName)) {
					if (cache != null && !cache.equals(AdvertConstants.ADVERT_IS_OPENED)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	//=====================数据统计

	/**
	 * <br>功能简述:请求接口获取indexUrl
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param handler
	 */
	private void requestAdvertStatistics(final Handler handler,
			final String clickUrl) {
		if (mContext == null) {
			sendHandlerMsg(AdvertConstants.STATISTICS_REQUEST_FAIL, clickUrl);
			return;
		}

		String url = AdvertConstants.getUrl("2"); //获取URL地址

		JSONObject requestJson = getRequestStatisticsJson(clickUrl);

		StatisticsManager.getInstance(ApplicationProxy.getContext()).upLoadAdStaticData(
				requestJson.toString()); //加上sdk统计接口

		try {
			THttpRequest request = new THttpRequest(url, requestJson.toString().getBytes(),
					new IConnectListener() {
						@Override
						public void onStart(THttpRequest arg0) {

						}

						@Override
						public void onFinish(THttpRequest request, IResponse response) {
							boolean isOk = responseStatistics(response);
							if (isOk) {
								sendHandlerMsg(AdvertConstants.STATISTICS_REQUEST_SUCCESS,
										null);
							} else {
								sendHandlerMsg(AdvertConstants.STATISTICS_REQUEST_FAIL,
										clickUrl);
							}
						}

						@Override
						public void onException(THttpRequest arg0, int arg1) {
							sendHandlerMsg(AdvertConstants.STATISTICS_REQUEST_FAIL,
									clickUrl);
						}
					});

			request.setOperator(new StringOperator()); //设置返回数据类型-字符串
			SimpleHttpAdapter httpAdapter = SimpleHttpAdapter.getInstance(mContext);
			httpAdapter.addTask(request);
		} catch (Exception e) {
			e.printStackTrace();
			sendHandlerMsg(AdvertConstants.STATISTICS_REQUEST_FAIL, null);
			return;
		}
	}

	/**
	 * <br>功能简述:解析统计返回数据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param response
	 * @return
	 */
	private boolean responseStatistics(IResponse response) {
		//解析请求数据
		if (response != null && response.getResponse() != null
				&& (response.getResponse() instanceof String)) {
			try {
				String responseString = response.getResponse().toString();
				JSONObject json = new JSONObject(responseString);
				String isok = json.optString("isok");
				if (isok.equals("1")) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * <br>功能简述:设置请求参数
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @return
	 */
	private JSONObject getRequestStatisticsJson(String clickUrl) {
		JSONObject request = new JSONObject();
		JSONObject pheadJson = getPheadJson(mContext);
		String statString = getStatString(mContext);
		try {
			request.put("phead", pheadJson);
			request.put("stat", statString); //上次获取消息时服务器下发的lts值
			if (clickUrl != null) {
				request.put("clickurl", clickUrl);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return request;
	}

	/**
	 * 获取GO桌面桌面安装统计
	 * 
	 * @return
	 */
	private String getStatString(Context context) {
		try {
			StringBuffer stringBuffer = new StringBuffer();
			if (context != null) {
				Map<String, ?> data = getAllAppData(context);
				if (data != null) {
					Set<String> keys = data.keySet();
					for (String key : keys) {
						StringBuffer oneBuffer = new StringBuffer();

						Object obj = data.get(key);
						String reason = null;
						if (obj != null && obj instanceof String) {
							reason = (String) obj;
						}

						String[] item = null;
						if (reason != null && !reason.equals("")) {
							item = reason.split(";");
						}
						//						log("reasonString:" + reason);
						//						log("getStatString item.length:" + item.length);
						if (item != null && item.length == 4) {
							String messageId = item[0];	//消息ID
							String clickCount = item[1];	//点击数量
							String installCount = item[2]; //安装数量
							String mapIdString = item[3]; //统计id
							long time = System.currentTimeMillis();

							//判断点击量和安装量是否为0
							if (!(clickCount.equals("0") && installCount.equals("0"))) {
								oneBuffer.append(time).append("#");			//日志记录的时间戳
								oneBuffer.append(messageId).append("#");	//消息id
								oneBuffer.append(clickCount).append("#"); 	//点击量
								oneBuffer.append(installCount).append("#"); //安装量
								oneBuffer.append(time + 1).append("#"); 	//上传id
								oneBuffer.append(mapIdString);  //统计id
							}
						}
						if (!oneBuffer.toString().equals("")) {
							stringBuffer.append(oneBuffer).append("&");
						}
					}
				}
			}
			return stringBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * <br>功能简述:设置参数Phead信息
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @return
	 */
	private JSONObject getPheadJson(Context context) {
		JSONObject pheadJson = new JSONObject();
		if (context != null) {
			String imei = GoStorePhoneStateUtil.getVirtualIMEI(context);
			try {
				pheadJson.put("vps", HttpUtil.getVps(context, imei)); //设备信息 vps
				pheadJson.put("launcherid", imei); //桌面id
				pheadJson.put("channel", GoStorePhoneStateUtil.getUid(context)); // 渠道号
				Locale locale = Locale.getDefault();
				String language = String.format("%s_%s", locale.getLanguage().toLowerCase(), locale
						.getCountry().toLowerCase());
				pheadJson.put("lang", language);
				//				pheadJson.put("local", locale.getCountry().toLowerCase());
				pheadJson.put("local", Machine.getCountry(context)); //先取sim卡.没有然后再去本地

				pheadJson.put("pversion", MessageHttp.PVERSION); //协议版本

				String curVersion = context.getString(R.string.curVersion);
				pheadJson.put("cversion", curVersion); //桌面的版本号, String例如：3.16
				pheadJson.put("sdklevel", getAndroidSDKVersion()); //sdklevel
				pheadJson.put("androidid", Machine.getAndroidId()); //androidid

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return pheadJson;
	}

	// 获取桌面应用点击及安装统计全部数据
	private Map<String, ?> getAllAppData(Context context) {
		PreferencesManager sp = new PreferencesManager(context, IPreferencesIds.ADVERT_SCREEN_DATA,
				Context.MODE_PRIVATE);

		return sp.getAll();
	}

	/**
	 * <br>功能简述:获取SDK版本号
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	private static int getAndroidSDKVersion() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {

		}
		return version;
	}
	
	private void getNetImageData(final Context context, AdvertInfo advertInfo,	
			final Handler handler) {
		if (context == null || advertInfo == null || advertInfo.mIcon == null || handler == null) {
			sendHandlerMsg(AdvertConstants.DOWN_IMAGE_FAIL, null);
			return;
		}
		String url = advertInfo.mIcon;
		log("地址url：" + url);
		try {
			THttpRequest request = new THttpRequest(url, null, new IConnectListener() {
				@Override
				public void onStart(THttpRequest arg0) {

				}

				@Override
				public void onFinish(THttpRequest request, IResponse response) {
					if (response != null && response.getResponse() != null
							&& (response.getResponse() instanceof Integer)) {
						int state = (Integer) response.getResponse();
						if (state == AdvertConstants.DOWN_IMAGE_SUCCESS) {
							sendHandlerMsg(AdvertConstants.DOWN_IMAGE_SUCCESS, null);
						} else {
							sendHandlerMsg(AdvertConstants.DOWN_IMAGE_FAIL, null);
						}
					} else {
						sendHandlerMsg(AdvertConstants.DOWN_IMAGE_FAIL, null);
					}
				}

				@Override
				public void onException(THttpRequest arg0, int arg1) {
					sendHandlerMsg(AdvertConstants.DOWN_IMAGE_FAIL, null);
				}
			});

			request.setOperator(new NetImageOperator(advertInfo)); //设置返回数据类型-字符串
			SimpleHttpAdapter httpAdapter = SimpleHttpAdapter.getInstance(context);
			httpAdapter.addTask(request);

		} catch (Exception e) {
			e.printStackTrace();
			sendHandlerMsg(AdvertConstants.DOWN_IMAGE_FAIL, null);
			return;
		}
	}
	
	/**
	 * <br>功能简述:保存15屏幕推荐图标包名列表
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param packageName
	 */
	private void setInstallListCache(Context context, ShortCutInfo info) {
		Intent intent = ((ShortCutInfo) info).mIntent;
		if (intent != null
				&& intent.getComponent() != null
				&& intent.getComponent().getPackageName() != null
				&& !intent.getComponent().getPackageName().equals("")) {
			String packageName = intent.getComponent().getPackageName();
			SharedPreferencesUtil preferencesUtil = new SharedPreferencesUtil(context);
			String cacheString = preferencesUtil.getString(AdvertConstants.ADVERT_PACKAGE_NAME_LIST, "");
			cacheString = cacheString + packageName + ";";
			preferencesUtil.saveString(AdvertConstants.ADVERT_PACKAGE_NAME_LIST, cacheString);
			log("cacheString:" + cacheString);
		}
	}
	
	/**
	 * <br>功能简述:清除安装列表缓存
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param packageName
	 */
	private void clearInstallListCache(Context context) {
		log("清空安装列表缓存！");
		SharedPreferencesUtil preferencesUtil = new SharedPreferencesUtil(context);
		preferencesUtil.saveString(AdvertConstants.ADVERT_PACKAGE_NAME_LIST, "");
	}
	
	/**
	 * <br>功能简述:判断是否15屏幕广告推荐的图标
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param packageName
	 * @return
	 */
	private boolean checkInAdvertList(String packageName) {
		if (packageName == null || packageName.equals("")) {
			return false;
		}
		
		SharedPreferencesUtil preferencesUtil = new SharedPreferencesUtil(mContext);
		String cacheString = preferencesUtil.getString(AdvertConstants.ADVERT_PACKAGE_NAME_LIST, "");
		if (cacheString.equals("")) {
			return false;
		}
		
		if (cacheString.contains(packageName)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * <br>功能简述:发送消息
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param handler
	 * @param what
	 * @param urlString
	 */
	private void sendHandlerMsg(int what, Object urlString) {
		if (mHandler != null) {
			Message msg = mHandler.obtainMessage(what);
			msg.obj = urlString;
			mHandler.sendMessage(msg);
		}
	}

	private void log(String content) {
		//	Log.i("lch2", content);
	}

	private void log3(String content) {
		//		Log.i("lch3", content);
	}
}
