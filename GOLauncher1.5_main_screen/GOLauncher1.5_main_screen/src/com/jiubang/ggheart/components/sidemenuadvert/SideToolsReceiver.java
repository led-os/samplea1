package com.jiubang.ggheart.components.sidemenuadvert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.util.xml.XmlUtils;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.base.manage.AsyncImageManager;
import com.jiubang.ggheart.appgame.base.manage.AsyncImageManager.AsyncImageLoadedCallBack;
import com.jiubang.ggheart.common.bussiness.LightAppConnector;
import com.jiubang.ggheart.common.bussiness.LightAppConnector.RequestLightAppDataListener;
import com.jiubang.ggheart.common.bussiness.SideToolsBussiness;
import com.jiubang.ggheart.components.sidemenuadvert.tools.SideToolsDataInfo;
import com.jiubang.ggheart.components.sidemenuadvert.utils.SideAdvertUtils;
import com.jiubang.ggheart.components.sidemenuadvert.utils.SideWidgetJsonUtil;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 
 * @author wuziyi
 *
 */
public class SideToolsReceiver implements RequestLightAppDataListener {

	private static SideToolsReceiver sInstance;

	private Context mContext;
	
	private SideToolsBussiness mSideToolsBussiness;
	
	private AsyncImageManager mImageLoder;

	private ArrayList<String> mRecvToolsPathArrayList; // tools icon path
	private ArrayList<String> mXmlToolsPathArrayList; // tools icon path
	private ArrayList<String> mWhiteListPathArrayList; // tools icon path
	private CopyOnWriteArrayList<SideToolsDataInfo> mGoToolsXmlList;   //gowidget.xml数据
	private CopyOnWriteArrayList<SideToolsDataInfo> mGoToolsNetList;   //gowidget网络数据
	private CopyOnWriteArrayList<SideToolsDataInfo> mGoToolsCacheList;   //gowidget Cache数据
	
	public static synchronized SideToolsReceiver getSideToolsReceiverInstance(
			Context context) {
		if (sInstance == null) {
			sInstance = new SideToolsReceiver(context);
		}
		return sInstance;
	}

	private SideToolsReceiver(Context context) {
		mContext = context;
		mImageLoder = AsyncImageManager.getDefaultInstance();
		mSideToolsBussiness = new SideToolsBussiness(mContext);
		//清理未安装的白名单
		cleanUninstalledInWhite();
		
		//xml的widget数据
		mGoToolsXmlList = new CopyOnWriteArrayList<SideToolsDataInfo>();
		praseRecommendedTools(mGoToolsXmlList);
		
		//cache的widget数据
		mGoToolsCacheList = new CopyOnWriteArrayList<SideToolsDataInfo>();
		loadCachetoList(mGoToolsCacheList);
	}

	
	public boolean refreshDataInfo(String PkgName, boolean isInstalled) {
//		if (PkgName == null || PkgName.length() == 0) {
//			return false;
//		}
//		//卸载
//		if (!isInstalled) {
//			// 从数据库删除
//			SideAdvertUtils.log(PkgName + "widget被删除，数据库清理");
//			mSideToolsBussiness.removeUninstalledWidget(PkgName);
//		}
//		
//		boolean isFindInfo = false;
//		// 更新网络数据
//		if (mGoToolsNetList != null) {
//			for (SideWidgetDataInfo netDataInfo : mGoToolsNetList) {
//				if (netDataInfo.getWidgetPkgName().equals(PkgName)) {
//					netDataInfo.setIsInstalled(isInstalled);
//					if (isInstalled) {
//						// 添加到数据库
//						if (netDataInfo.isIsClickInSlideMenu()) {
//							SideAdvertUtils.log(netDataInfo.getTitle()
//									+ "widget通过小部件点击安装，数据库增加记录");
//							mSideToolsBussiness
//									.addInstalledWidget(netDataInfo);
//						}
//					} 
//					isFindInfo = true;
//				}
//			}
//		} else {
//			// 更新缓存数据  有问题
//			if (mGoToolsCacheList != null && !mGoToolsCacheList.isEmpty()) {
//				for (SideWidgetDataInfo cacheWidgetInfo : mGoToolsCacheList) {
//					if (cacheWidgetInfo.getWidgetPkgName().equals(PkgName)) {
//						cacheWidgetInfo.setIsInstalled(isInstalled);
//						if (isInstalled) {
//							// 添加到数据库
//							if (cacheWidgetInfo.isIsClickInSlideMenu()) {
//								SideAdvertUtils.log(cacheWidgetInfo.getTitle()
//										+ "widget通过小部件点击安装，数据库增加记录");
//								mSideToolsBussiness
//										.addInstalledWidget(cacheWidgetInfo);
//							}
//						}
//						isFindInfo = true;
//					}
//
//				}
//			}
//		}
//		// 更新xml数据
//		if (mGoToolsXmlList != null && !mGoToolsXmlList.isEmpty()) {
//			for (SideToolsDataInfo xmlDataInfo : mGoToolsXmlList) {
//				if (xmlDataInfo.getWidgetPkgName().equals(PkgName)) {
//					xmlDataInfo.setIsInstalled(isInstalled);
//					isFindInfo = true;
//				}
//			}
//		}

		return false;
	}

	private void praseRecommendedTools(List<SideToolsDataInfo> goToolsInfoList) {
		if (goToolsInfoList == null) {
			throw new IllegalArgumentException();
		}
		try {
			Context context = ApplicationProxy.getContext();
			XmlResourceParser parser = context.getResources().getXml(R.xml.gotools);
			AttributeSet attrs = Xml.asAttributeSet(parser);
			XmlUtils.beginDocument(parser, SideAdvertConstants.XML_GOTOOLS_APPS);

			final int depth = parser.getDepth();
			int type;
			while (((type = parser.next()) != XmlPullParser.END_TAG || parser
					.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {

				if (type != XmlPullParser.START_TAG) {
					continue;
				}

				TypedArray a = context.obtainStyledAttributes(attrs,
						R.styleable.RecommendedTools);
				final String name = parser.getName();
				if (SideAdvertConstants.XML_GOTOOL_APP.equals(name)) {
					final SideToolsDataInfo appInfo = getRecommendedAppInfo(a, context);
					if (appInfo != null) {
						goToolsInfoList.add(appInfo);
					}
				}
				a.recycle();
			}
			parser.close();
			parser = null;
		} catch (XmlPullParserException e) {
		} catch (IOException e) {
		}
		
	}
	
	private SideToolsDataInfo getRecommendedAppInfo(TypedArray a, Context context) {
		if (a == null) {
			throw new IllegalArgumentException();
		}

		SideToolsDataInfo info = new SideToolsDataInfo();
		// 标题
		int titleId = a.getResourceId(R.styleable.RecommendedTools_tool_titleid, 0);
		String title = context.getString(titleId);
		info.setTitle(title);
		// 图标
		int iconId = a.getResourceId(R.styleable.RecommendedTools_tool_iconid, 0);
		Drawable icon = context.getResources().getDrawable(iconId);
		info.setIcon(icon);
		// GAlink
		String gaLink = a.getString(R.styleable.RecommendedTools_tool_galink);
		info.setGALink(gaLink);
		// 下载link
		String downloadUrl = a.getString(R.styleable.RecommendedTools_tool_downloadurl);
		info.setToolsDownLoadURL(downloadUrl);
		// 包名
		String packagename = a.getString(R.styleable.RecommendedTools_tool_pkgname);
		info.setToolsPkgName(packagename);
		// 下载方式
		int downloadtype = a.getInt(R.styleable.RecommendedTools_tool_downloadtype, 0);
		info.setDownloadtype(downloadtype);
		// 是否已安装
		if (GoAppUtils.isAppExist(mContext,	info.getToolsPkgName())) {
			info.setIsInstalled(true);
		} else {
			info.setIsInstalled(false);
		}
//		info.mShowInstallIcon = a.getBoolean(R.styleable.RecommendedApp_showinstallicon, false);
		//实时统计参数
//		info.mClickurl = a.getString(R.styleable.RecommendedApp_clickurl);
//		info.mMapId = a.getString(R.styleable.RecommendedApp_mapid);
		return info;
	}
	

	private void loadJSONArrayToList(JSONArray gowidgetArray,
			List<SideToolsDataInfo> goToolsProviderList) {
		if (goToolsProviderList == null || gowidgetArray == null) {
			return;
		}
		if (gowidgetArray.length() != 0) {
			int msgsSize = gowidgetArray.length();
			SideAdvertUtils.log("apps array total :" + msgsSize);
			if (mRecvToolsPathArrayList == null) {
				mRecvToolsPathArrayList = new ArrayList<String>();
			} else {
				mRecvToolsPathArrayList.clear();
			}
			for (int i = 0; i < msgsSize; i++) {
				JSONObject msgJsonObject;
				try {
					msgJsonObject = gowidgetArray.getJSONObject(i);
					SideAdvertUtils.log("apps array no :" + i);
					SideAdvertUtils.log(msgJsonObject.toString());

					final SideToolsDataInfo goToolsInfo = new SideToolsDataInfo();
					goToolsInfo.setToolsDownLoadURL(msgJsonObject.getString("downloadurl"));
					goToolsInfo.setToolsPkgName(msgJsonObject.getString("packagename"));
					goToolsInfo.setTitle(msgJsonObject.getString("name"));
					goToolsInfo.setIconUrl(msgJsonObject.getString("icon"));
					goToolsInfo.setDownloadtype(msgJsonObject.getInt("downloadtype"));

					if (GoAppUtils.isAppExist(mContext,	goToolsInfo.getToolsPkgName())) {
						goToolsInfo.setIsInstalled(true);
					} else {
						goToolsInfo.setIsInstalled(false);
						String imgUrl = goToolsInfo.getIconUrl();
						String imgPath = LauncherEnv.Path.SIDEMENU_TOOLS_PATH;
						String imgName = String.valueOf(goToolsInfo.getIconFileName()) + ".png";
						mImageLoder.loadImage(imgPath, imgName, imgUrl, true, null, new AsyncImageLoadedCallBack() {
							
							@Override
							public void imageLoaded(Bitmap imageBitmap, String imgUrl) {
								Drawable icon = new BitmapDrawable(mContext.getResources(), imageBitmap);
								goToolsInfo.setIcon(icon);
							}
						});
					}
					goToolsProviderList.add(goToolsInfo);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void loadCachetoList(
			List<SideToolsDataInfo> outArrayList) {
		if (outArrayList != null) {
			try {
				JSONObject restJsonObject = SideWidgetJsonUtil.getSideToolsCache();
				JSONArray goWidInDirectArray = SideWidgetJsonUtil.getGoToolsArray(restJsonObject);

				// 保存goWidInDirectArray 至缓存文件 todo
				loadJSONArrayToList(goWidInDirectArray, outArrayList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void responseJson(Context context, JSONObject json) {
		// 处理下载列表数据
		try {
			String responseString = json.toString();
			Log.i("wuziyi", "responseString" + responseString);

			// 需要保存缓存
			if (SideWidgetJsonUtil.saveSideToolsCache(responseString.getBytes())) {
				SideAdvertUtils.log("cache just saved");
			}

			JSONArray goWidInDirectArray = SideWidgetJsonUtil.getGoToolsArray(json);

			// 保存goWidInDirectArray 
			if (mGoToolsNetList == null) {
				mGoToolsNetList =  new CopyOnWriteArrayList<SideToolsDataInfo>();
			} else {
				mGoToolsNetList.clear();
			}
			loadJSONArrayToList(goWidInDirectArray, mGoToolsNetList);

			ArrayList<String> tmpDelRestPicList = new ArrayList<String>();
			if (mRecvToolsPathArrayList != null) {
				tmpDelRestPicList.addAll(mRecvToolsPathArrayList);
			}
			
			if (mXmlToolsPathArrayList != null) {
				tmpDelRestPicList.addAll(mXmlToolsPathArrayList);
			}
			
			if (mWhiteListPathArrayList != null) {
				tmpDelRestPicList.addAll(mWhiteListPathArrayList);
			}
			// 清空多余图片
			SideAdvertUtils.clearDirExceptInputFiles(
					LauncherEnv.Path.SIDEMENU_WIDGET_PATH,
					tmpDelRestPicList);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void requestSideToolsData() {
		LightAppConnector connector = new LightAppConnector();
		HashMap<String, JSONArray> extraMap = new HashMap<String, JSONArray>();
		try {
			JSONArray array = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("typeid", 1);
			array.put(obj);
			extraMap.put("types", array);
		} catch (JSONException e) {
		}
		connector.requestLightAppData(LightAppConnector.APPDRAWER_TOOLS_DATA, extraMap);
		connector.setRequestLightAppListener(this);
	}
	
	@Override
	public void onRequsetFinish(JSONObject json, int funId) {
		responseJson(mContext, json);
	}
	
	@Override
	public void onRequestException(int funId) {
		mGoToolsNetList = null;
		//获取缓存数据
		if (mGoToolsCacheList != null) {
			mGoToolsCacheList.clear();
			loadCachetoList(mGoToolsCacheList);
		}
		//再次访问
		requestSideToolsData();
	}
	
	private void cleanUninstalledInWhite() {
		//白名单icon路径
		if (mWhiteListPathArrayList == null) {
			mWhiteListPathArrayList = new ArrayList<String>();
		} else {
			mWhiteListPathArrayList.clear();
		}
		
		//获取白名单
		ArrayList<SideToolsDataInfo> whiteListWidgetList = getWhiteListToolsList();
		if (whiteListWidgetList.isEmpty()) {
			return;
		}

		for (SideToolsDataInfo toolsInfo : whiteListWidgetList) {
			if (!GoAppUtils.isAppExist(mContext, toolsInfo.getToolsPkgName())) {
				mSideToolsBussiness.removeUninstalledTools(toolsInfo);
			} else {
				String pathString = LauncherEnv.Path.SIDEMENU_WIDGET_PATH
						+ String.valueOf(toolsInfo.getIconFileName())
						+ ".png";
//				mWhiteListPathArrayList.add(pathString);
			}
		}
	}
	
	public ArrayList<SideToolsDataInfo> getWhiteListToolsList() {
		 return mSideToolsBussiness.getAllInstalledTools();
	}
	
	public CopyOnWriteArrayList<SideToolsDataInfo> getGoWidgetXmlList() {
		return mGoToolsXmlList;
	}
	
	//可能为空，意味着网络存在问题，收不到数据，以便区分服务器下发空数据
	public CopyOnWriteArrayList<SideToolsDataInfo> getGoWidgetNetList() {
		return mGoToolsNetList;
	}
	
	public CopyOnWriteArrayList<SideToolsDataInfo> getGoWidgetCacheList() {
		return mGoToolsCacheList;
	}
		
}
