package com.jiubang.ggheart.components.sidemenuadvert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;

import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.INetRecord;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.go.commomidentify.IGoLauncherClassName;
import com.go.util.device.Machine;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.base.data.AppJsonOperator;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.ConstValue;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageListBean;
import com.jiubang.ggheart.apps.gowidget.InnerWidgetInfo;
import com.jiubang.ggheart.apps.gowidget.InnerWidgetParser;
import com.jiubang.ggheart.apps.gowidget.gostore.net.SimpleHttpAdapter;
import com.jiubang.ggheart.common.bussiness.SideWidgetBussiness;
import com.jiubang.ggheart.components.sidemenuadvert.utils.SideAdvertUtils;
import com.jiubang.ggheart.components.sidemenuadvert.utils.SideWidgetJsonUtil;
import com.jiubang.ggheart.components.sidemenuadvert.widget.SideWidgetDataInfo;
import com.jiubang.ggheart.components.sidemenuadvert.widget.SideWidgetInfo;
import com.jiubang.ggheart.components.sidemenuadvert.widget.SideWidgetSpecialInfo;
import com.jiubang.ggheart.data.info.GoWidgetBaseInfo;
import com.jiubang.ggheart.data.theme.XmlParserFactory;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 
 * @author zhangxi
 * 
 */
public class SideWidgetReceiver {

	private static SideWidgetReceiver sInstance;

	private Context mContext;
	
	private SideWidgetBussiness mSideWidgetBussiness;

	private ArrayList<String> mRecvWidgetPathArrayList; // widget icon path
	private ArrayList<String> mXmlWidgetPathArrayList; // widget icon path
	private ArrayList<String> mWhiteListPathArrayList; // widget icon path
	private CopyOnWriteArrayList<SideWidgetDataInfo> mGoWidgetXmlList;   //gowidget.xml数据
	private CopyOnWriteArrayList<SideWidgetDataInfo> mGoWidgetNetList;   //gowidget网络数据
	private CopyOnWriteArrayList<SideWidgetDataInfo> mGoWidgetCacheList;   //gowidget Cache数据

	public static synchronized SideWidgetReceiver getSideWidgetReceiverInstance(
			Context context) {
		if (sInstance == null) {
			sInstance = new SideWidgetReceiver(context);
		}
		return sInstance;
	}

	private SideWidgetReceiver(Context context) {
		mContext = context;
		mSideWidgetBussiness = new SideWidgetBussiness(mContext);
		//清理未安装的白名单
		cleanUninstalledInWhite();
		//获取清理后的白名单icon路径
		
		
		//xml的widget数据
		mGoWidgetXmlList = new CopyOnWriteArrayList<SideWidgetDataInfo>();
		loadXmlToList("gowidget.xml", mGoWidgetXmlList);
		
		//cache的widget数据
		mGoWidgetCacheList = new CopyOnWriteArrayList<SideWidgetDataInfo>();
		loadCachetoList(mGoWidgetCacheList);
	}

	
	public boolean refreshDataInfo(String PkgName, boolean isInstalled) {
		if (PkgName == null || PkgName.length() == 0) {
			return false;
		}
		//卸载
		if (!isInstalled) {
			// 从数据库删除
			SideAdvertUtils.log(PkgName + "widget被删除，数据库清理");
			mSideWidgetBussiness.removeUninstalledWidget(PkgName);
		}
		
		boolean isFindInfo = false;
		// 更新网络数据
		if (mGoWidgetNetList != null) {
			for (SideWidgetDataInfo netDataInfo : mGoWidgetNetList) {
				if (netDataInfo.getWidgetPkgName().equals(PkgName)) {
					netDataInfo.setIsInstalled(isInstalled);
					if (isInstalled) {
						// 添加到数据库
						if (netDataInfo.isIsClickInSlideMenu()) {
							SideAdvertUtils.log(netDataInfo.getTitle()
									+ "widget通过小部件点击安装，数据库增加记录");
							mSideWidgetBussiness
									.addInstalledWidget(netDataInfo);
						}
					} 
					isFindInfo = true;
				}
			}
		} else {
			// 更新缓存数据  有问题
			if (mGoWidgetCacheList != null && !mGoWidgetCacheList.isEmpty()) {
				for (SideWidgetDataInfo cacheWidgetInfo : mGoWidgetCacheList) {
					if (cacheWidgetInfo.getWidgetPkgName().equals(PkgName)) {
						cacheWidgetInfo.setIsInstalled(isInstalled);
						if (isInstalled) {
							// 添加到数据库
							if (cacheWidgetInfo.isIsClickInSlideMenu()) {
								SideAdvertUtils.log(cacheWidgetInfo.getTitle()
										+ "widget通过小部件点击安装，数据库增加记录");
								mSideWidgetBussiness
										.addInstalledWidget(cacheWidgetInfo);
							}
						}
						isFindInfo = true;
					}

				}
			}
		}
		// 更新xml数据
		if (mGoWidgetXmlList != null && !mGoWidgetXmlList.isEmpty()) {
			for (SideWidgetDataInfo xmlDataInfo : mGoWidgetXmlList) {
				if (xmlDataInfo.getWidgetPkgName().equals(PkgName)) {
					xmlDataInfo.setIsInstalled(isInstalled);
					isFindInfo = true;
				}
			}
		}

		return isFindInfo;
	}

	private void loadXmlToList(String strXML,
			List<SideWidgetDataInfo> goWidgetInfoList) {
		
		if (strXML != null && goWidgetInfoList != null) {
			if (mXmlWidgetPathArrayList == null) {
				mXmlWidgetPathArrayList = new ArrayList<String>();
			} else {
				mXmlWidgetPathArrayList.clear();
			}
			InputStream inputStream = null;
			try {

				XmlPullParser parser = XmlPullParserFactory.newInstance()
						.newPullParser();

				if (inputStream == null) {
					inputStream = XmlParserFactory.createInputStream(mContext,
							IGoLauncherClassName.DEFAULT_THEME_PACKAGE, strXML);
				}
				parser.setInput(inputStream, null);

				String value = null;
				String attrName = null;
				while (parser.next() != XmlPullParser.END_DOCUMENT) {
					attrName = parser.getName();
					if (attrName == null) {
						continue;
					}
					// 解析版本
					if (attrName.equals(SideAdvertConstants.XML_GOWIDGET_ITEM)) {
						SideWidgetDataInfo bean = new SideWidgetDataInfo();
						bean.setType(SideWidgetDataInfo.SIDEWIDGET_LOCALXML_INFO);

						value = parser.getAttributeValue("",
								SideAdvertConstants.XML_GOWIDGET_PKGNAME);
						if (value != null) {
							bean.setWidgetPkgName(value);
						} else {
							bean.setWidgetPkgName("");
						}

						value = parser.getAttributeValue("",
								SideAdvertConstants.XML_GOWIDGET_THEMEINFO);
						if (value != null) {
							bean.setWidgetDownLoadURL(value);
						} else {
							bean.setWidgetDownLoadURL("");
						}
				
						value = parser.getAttributeValue("",
								SideAdvertConstants.XML_GOWIDGET_GALINK);
						if (value != null) {
							bean.setGALink(value);
						} else {
							bean.setGALink("");
						}
						
						if (Machine.getCountry(mContext).equals("cn")) {
							value = parser.getAttributeValue("",
									SideAdvertConstants.XML_GOWIDGET_PICURL_CN);
							if (value != null) {
									bean.setPreViewUrl(value);
									bean.setPreViewName(value.hashCode());	
							} else {
								bean.setPreViewUrl("");
								bean.setPreViewName(bean.getPreViewUrl().hashCode());
							}
							
						} else {
							value = parser.getAttributeValue("",
									SideAdvertConstants.XML_GOWIDGET_PICURL_AB);
							if (value != null) {
								bean.setPreViewUrl(value);
								bean.setPreViewName(value.hashCode());
							} else {
								bean.setPreViewUrl("");
								bean.setPreViewName(bean.getPreViewUrl().hashCode());
							}
						}
						String pathString = LauncherEnv.Path.SIDEMENU_WIDGET_PATH
								+ String.valueOf(bean.getPreViewName())
								+ ".png";
						mXmlWidgetPathArrayList.add(pathString);
						
						value = parser.nextText();
						if (value != null) {
							int stringId = mContext.getResources()
									.getIdentifier(value, "string",
											IGoLauncherClassName.DEFAULT_THEME_PACKAGE);
							if (stringId != 0) {
								bean.setTitle(mContext.getString(stringId));
							}
						}
						

						if (GoAppUtils.isAppExist(mContext,
								bean.getWidgetPkgName())) {
							bean.setIsInstalled(true);
						} else {
							bean.setIsInstalled(false);
						}
												
						goWidgetInfoList.add(bean);
					}
				}

			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
						inputStream = null;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	

	private void loadJSONArrayToList(JSONArray gowidgetArray,
			List<SideWidgetDataInfo> goWidgetProviderList) {
		if (goWidgetProviderList == null || gowidgetArray == null) {
			return;
		}
		if (gowidgetArray.length() != 0) {
			int msgsSize = gowidgetArray.length();
			SideAdvertUtils.log("apps array total :" + msgsSize);
			if (mRecvWidgetPathArrayList == null) {
				mRecvWidgetPathArrayList = new ArrayList<String>();
			} else {
				mRecvWidgetPathArrayList.clear();
			}
			for (int i = 0; i < msgsSize; i++) {
				JSONObject msgJsonObject;
				try {
					msgJsonObject = gowidgetArray.getJSONObject(i);
					SideAdvertUtils.log("apps array no :" + i);
					SideAdvertUtils.log(msgJsonObject.toString());

					SideWidgetDataInfo goWidgetInfo = new SideWidgetDataInfo();
					goWidgetInfo.setType(SideWidgetDataInfo.SIDEWIDGET_DOWNLOAD_INFO);
					goWidgetInfo.setPreViewUrl(msgJsonObject.optString("pic"));
					goWidgetInfo.setPreViewName(goWidgetInfo.getPreViewUrl()
							.hashCode());
					String pathString = LauncherEnv.Path.SIDEMENU_WIDGET_PATH
							+ String.valueOf(goWidgetInfo.getPreViewName())
							+ ".png";
					mRecvWidgetPathArrayList.add(pathString);

					JSONObject subAppInfoJsonObject = msgJsonObject
							.getJSONObject("appinfo");
					goWidgetInfo.setWidgetDownLoadURL(subAppInfoJsonObject
							.optString("downloadurl"));
					goWidgetInfo.setWidgetPkgName(subAppInfoJsonObject
							.optString("packname"));
					goWidgetInfo.setTitle(subAppInfoJsonObject
							.optString("name"));

					if (GoAppUtils.isAppExist(mContext,
							goWidgetInfo.getWidgetPkgName())) {
						goWidgetInfo.setIsInstalled(true);
					} else {
						goWidgetInfo.setIsInstalled(false);
					}

					goWidgetProviderList.add(goWidgetInfo);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	

	private void loadCachetoList(
			List<SideWidgetDataInfo> outArrayList) {
		if (outArrayList != null) {
			try {
				JSONObject restJsonObject = SideWidgetJsonUtil.getSideWidgetCache();
				JSONArray goWidInDirectArray = SideWidgetJsonUtil.getGoWidgetArray(restJsonObject);

				// 保存goWidInDirectArray 至缓存文件 todo
				loadJSONArrayToList(goWidInDirectArray, outArrayList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void responseJson(Context context, IResponse response) {
		// 处理下载列表数据
		if (response != null && response.getResponse() != null
				&& (response.getResponse() instanceof JSONObject)) {
			try {
				JSONObject json = (JSONObject) response.getResponse();
				String responseString = json.toString();
				SideAdvertUtils.log("返回数据：" + responseString);

				// 需要保存缓存
				if (SideWidgetJsonUtil.saveSideWidgetCache(responseString.getBytes())) {
					SideAdvertUtils.log("cache just saved");
				}

				JSONArray goWidInDirectArray = SideWidgetJsonUtil.getGoWidgetArray(json);

				// 保存goWidInDirectArray 
				if (mGoWidgetNetList == null) {
					mGoWidgetNetList =  new CopyOnWriteArrayList<SideWidgetDataInfo>();
				} else {
					mGoWidgetNetList.clear();
				}
				loadJSONArrayToList(goWidInDirectArray, mGoWidgetNetList);

				ArrayList<String> tmpDelRestPicList = new ArrayList<String>();
				if (mRecvWidgetPathArrayList != null) {
					tmpDelRestPicList.addAll(mRecvWidgetPathArrayList);
				}
				
				if (mXmlWidgetPathArrayList != null) {
					tmpDelRestPicList.addAll(mXmlWidgetPathArrayList);
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
	}
	
	/**
	 * 
	 * @author zhangxi
	 *
	 */
	private class ResponseListener implements IConnectListener {
		@Override
		public void onStart(THttpRequest arg0) {
			// Log.e("lch", "onStart");
		}

		@Override
		public void onFinish(THttpRequest request, IResponse response) {

			// 判断成功，并解析数据
			String responseString = response.getResponse().toString();
			JSONObject json;
			try {
				json = new JSONObject(responseString);
				JSONObject result = json
						.getJSONObject(MessageListBean.TAG_RESULT);
				int status = result.getInt(MessageListBean.TAG_STATUS);
				// 请求成功
				if (status == ConstValue.STATTUS_OK) {
					// 解析请求数据
					responseJson(mContext, response);
				} else {
					mGoWidgetNetList = null;
					//获取缓存数据
					if (mGoWidgetCacheList != null) {
						mGoWidgetCacheList.clear();
						loadCachetoList(mGoWidgetCacheList);
					}
					//更换地址再次访问
					changeSurfURL();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		public void onException(THttpRequest arg0, int arg1) {
			SideAdvertUtils.log("onException = " + arg1);
			changeSurfURL();

		}
	}

	private void changeSurfURL() {
		// 更换地址再次访问
		if (SideAdvertConstants.sGoWidgetBaseHostUrl.equals(SideAdvertConstants.getGWHostUrl())) {
			SideAdvertConstants
					.setGWHostUrl(SideAdvertConstants.sGoWidgetBackupHostUrl);
			requestSideWidgetData();
		} else {
			SideAdvertConstants
					.setGWHostUrl(SideAdvertConstants.sGoWidgetBaseHostUrl);
		}
	}

	/**
	 * 
	 * @author zhangxi
	 *
	 */
	private class NetRecordListener implements INetRecord {
		@Override
		public void onException(Exception e, Object arg1, Object arg2) {
			// SideAdvertUtils.log("NET Exception");
			// e.printStackTrace(); //打印出HTTP请求真实的错误信息
		}

		@Override
		public void onStartConnect(THttpRequest request, Object arg1,
				Object arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnectSuccess(THttpRequest request, Object arg1,
				Object arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTransFinish(THttpRequest request, Object arg1, Object arg2) {
			// TODO Auto-generated method stub

		}

	}

	private String getSideGoWidgetUrl(Context context) {
		StringBuffer buffer = null;
		if (Machine.getCountry(context).equals("cn")) {
			SideAdvertConstants.setGWHostUrl(SideAdvertConstants.sGoWidgetBaseHostUrl);
			buffer = new StringBuffer(
					SideAdvertConstants.getGWHostUrl());
		} else {
			SideAdvertConstants.setGWHostUrl(SideAdvertConstants.sGoWidgetBackupHostUrl);
			buffer = new StringBuffer(
					SideAdvertConstants.getGWHostUrl());
		}
		Random random = new Random(new Date().getTime()); // 随机数
		buffer.append("?rd=" + random.nextLong());
		random = null;
		return buffer.toString();
	}

	public void requestSideWidgetData() {

		// 获取URL地址
		String url = getSideGoWidgetUrl(mContext);

		// 获取包头
		JSONObject requestJson = SideWidgetJsonUtil.getRequestUrlJson(mContext);

		SideAdvertUtils.log("地址url：" + url);
		try {
			ResponseListener respoenseListener = new ResponseListener();
			NetRecordListener netRecordListener = new NetRecordListener();
			HashMap<String, String> gowidgetMap = new HashMap<String, String>();
			gowidgetMap.put("handle", "0");
			gowidgetMap.put("data", requestJson.toString());
			THttpRequest request = new THttpRequest(url, respoenseListener);
			request.setParamMap(gowidgetMap);
			request.setProtocol(THttpRequest.PROTOCOL_POST);
			request.setOperator(new AppJsonOperator()); // 设置返回数据类型-字符串
			request.setNetRecord(netRecordListener); // 设置报错提示
			SideAdvertUtils.log("请求参数1：" + gowidgetMap.toString());

			SimpleHttpAdapter httpAdapter = SimpleHttpAdapter
					.getInstance(mContext);
			httpAdapter.addTask(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void cleanUninstalledInWhite() {
		//白名单icon路径
		if (mWhiteListPathArrayList == null) {
			mWhiteListPathArrayList = new ArrayList<String>();
		} else {
			mWhiteListPathArrayList.clear();
		}
		
		//获取白名单
		ArrayList<SideWidgetDataInfo> whiteListWidgetList = getWhiteListWidgetList();
		if (whiteListWidgetList.isEmpty()) {
			return;
		}

		for (SideWidgetDataInfo widgetInfo : whiteListWidgetList) {
			if (!GoAppUtils.isAppExist(mContext, widgetInfo.getWidgetPkgName())) {
				mSideWidgetBussiness.removeUninstalledWidget(widgetInfo);
			} else {
				String pathString = LauncherEnv.Path.SIDEMENU_WIDGET_PATH
						+ String.valueOf(widgetInfo.getPreViewName())
						+ ".png";
				mWhiteListPathArrayList.add(pathString);
			}
		}
	}
	
	public ArrayList<SideWidgetDataInfo> getWhiteListWidgetList() {
		 return mSideWidgetBussiness.getAllInstalledWidgets();
	}
	
	public SideWidgetInfo getSysWidgetInfo() {
		
		// for system widget
		SideWidgetSpecialInfo systemWidgetInfo = new SideWidgetSpecialInfo();
		try {
			systemWidgetInfo.setType(SideWidgetSpecialInfo.SIDEWIDGET_SYSTEM_INFO);
			systemWidgetInfo.setWidgetPkgName("");
			systemWidgetInfo.setTitle("");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return systemWidgetInfo;
	}
	
	public SideWidgetInfo getInnerWidgetInfo() {

		// for inner widget
		SideWidgetSpecialInfo innerWidgetInfo = new SideWidgetSpecialInfo();
		try {
			ArrayList<InnerWidgetInfo> innerWidgetInfoList;
			innerWidgetInfoList = InnerWidgetParser.getInnerWidgets(mContext);

			if (innerWidgetInfoList != null && !innerWidgetInfoList.isEmpty()) {
				for (InnerWidgetInfo inner : innerWidgetInfoList) {
					if (inner.mPrototype == GoWidgetBaseInfo.PROTOTYPE_APPGAME) {
						innerWidgetInfo
								.setType(SideWidgetSpecialInfo.SIDEWIDGET_GOMARKET_INFO);
						innerWidgetInfo.setTitle(inner.mTitle);
						innerWidgetInfo.setWidgetPkgName(inner.mWidgetPkg);
						
						int resID = 0;
						Resources resources = mContext.getResources();
						if (resources != null) {
							try {
								final String[] extras = resources
										.getStringArray(inner.mPreviewList);
								for (String extra : extras) {
									resID = resources.getIdentifier(extra,
											"drawable", inner.mWidgetPkg);
									SideAdvertUtils.log("inner get id为:" + resID);
									if (resID != 0) {
										break;
									}
								}
							} catch (NotFoundException e) {
								e.printStackTrace();
							}
						}
						
						innerWidgetInfo.setPreViewResID(resID);
						innerWidgetInfo.setObject(inner);
						break;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return innerWidgetInfo;
	}
	
//	public boolean isInstalledWidgetByClick(SideWidgetInfo widgetinfo) {
//		return mSideWidgetBussiness.isInstalledWidget(widgetinfo);
//	}
	
	public CopyOnWriteArrayList<SideWidgetDataInfo> getGoWidgetXmlList() {
		return mGoWidgetXmlList;
	}
	
	//可能为空，意味着网络存在问题，收不到数据，以便区分服务器下发空数据
	public CopyOnWriteArrayList<SideWidgetDataInfo> getGoWidgetNetList() {
		return mGoWidgetNetList;
	}
	
	public CopyOnWriteArrayList<SideWidgetDataInfo> getGoWidgetCacheList() {
		return mGoWidgetCacheList;
	}
		
}
