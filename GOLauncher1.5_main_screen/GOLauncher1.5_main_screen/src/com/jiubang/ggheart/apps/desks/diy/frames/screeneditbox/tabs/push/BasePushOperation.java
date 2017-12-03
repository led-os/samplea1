package com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.tabs.push;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.tabs.push.util.ScreenEditPushConstants;
import com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.tabs.push.util.ScreenEditPushController;

/**
 * 
 * @author zouguiquan
 *
 */
public abstract class BasePushOperation {
	
	protected ScreenEditPushController mPushController;
	
	protected Context mContext;
	protected String mInstalledAppKey;
	protected String mNewPushAppKey;
	protected String mCurrentPushAppKey;
	protected int mPushType;
	protected boolean mPushAllInstalled;
	
	public BasePushOperation(Context context) {
		mContext = context;
		mPushController = new ScreenEditPushController(context);
	}
	
	protected abstract void onParseInstalled(String packageName);
	
	protected abstract void onParsePush(JSONObject jsonObject);
	
	public void loadInstalledApp() {
		JSONArray jsonArray = mPushController.getJSONArray(mInstalledAppKey);
		if (jsonArray != null) {
			parseInstalledJSONData(jsonArray);
		}
	}
	
	private void parseInstalledJSONData(JSONArray jsonArray) {
		if (jsonArray == null || jsonArray.length() == 0) {
			return;
		}
		
		int msgsSize = jsonArray.length();
		List<String> installedList = new ArrayList<String>();
		
		try {
			for (int i = 0; i < msgsSize; i++) {
				JSONObject msgJsonObject = (JSONObject) jsonArray.get(i);
				String packageName = msgJsonObject.optString(ScreenEditPushConstants.sJsonPackage);

				if (GoAppUtils.isAppExist(mContext, packageName)) {
					if (!installedList.contains(packageName)) {
						installedList.add(packageName);
						onParseInstalled(packageName);
					}
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		installedList.clear();
		installedList = null;
	}
	
	/**
	 * 从缓存中读取推荐列表
	 * @param typeId
	 * @return
	 */
	public void loadPushData() {
		JSONArray jsonArray = mPushController.getJSONArray(mNewPushAppKey);
		if (jsonArray != null) {
			parsePushJSONData(jsonArray);
		}
	}
	
	/**
	 * 解释JSONArray,转换成bean
	 * @param msgsArray
	 * @return
	 */
	private void parsePushJSONData(JSONArray jsonArray) {

		if (jsonArray == null || jsonArray.length() == 0) {
			return;
		}
		
		int pushCount = 0;
		int msgsSize = jsonArray.length();
		List<String> installedList = new ArrayList<String>();
		
		try {
			for (int i = 0; i < msgsSize; i++) {
				JSONObject msgJsonObject = (JSONObject) jsonArray.get(i);
				String packageName = msgJsonObject.optString(ScreenEditPushConstants.sJsonPackage);

				if (GoAppUtils.isAppExist(mContext, packageName)) {
					if (!installedList.contains(packageName)) {
						installedList.add(packageName);
						mPushController.saveInstalledPackage(packageName);
						onParseInstalled(packageName);
					}
				} else {
					onParsePush(msgJsonObject);
					pushCount++;
				}
			}
			
			if (msgsSize > 0 && pushCount == 0) {
				mPushAllInstalled = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		installedList.clear();
		installedList = null;
	}
	
	public void log(String content) {
		if (true) {
			Log.d("widgetpush", content);
		}
	}
}
