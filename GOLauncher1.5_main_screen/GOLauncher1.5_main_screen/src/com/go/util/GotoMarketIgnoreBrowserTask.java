/*
 * 文 件 名:  GotoMarketIgnoreBrowserTask.java
 * 版    权:  3G
 * 描    述:  <描述>
 * 修 改 人:  liuxinyang
 * 修改时间:  2014-3-3
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.go.util;

import com.gau.go.launcherex.R;
import com.go.util.device.Machine;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.components.DeskToast;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * <br>类描述:用于将浏览器的链接地址直接跳转到电子市场，不须让用户先跳转到浏览器再跳转到电子市场
 * <br>功能详细描述:
 * 
 * @author  liuxinyang
 * @date  [2014-2-11]
 */
public class GotoMarketIgnoreBrowserTask extends AsyncTask<Integer, String, String> {

	private Context mContext;

	private String mUrl;

	public GotoMarketIgnoreBrowserTask(Context context, String url) {
		mContext = context;
		mUrl = url;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		DeskToast.makeText(mContext, R.string.recommended_click_tip, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected String doInBackground(Integer... params) {
		mUrl = AppUtils.getHttpRedirectUrl(mUrl);
		return mUrl;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (TextUtils.isEmpty(result)) {
			return ;
		}
		// 确保URL地址是谷歌电子市场的
		if ((result.startsWith("https://play.google.com") || result
				.startsWith("http://play.google.com")) && GoAppUtils.isMarketExist(mContext)) {
			GoAppUtils.gotoMarket(mContext, result);
		} else {
			AppUtils.gotoBrowser(mContext, result);
		}
	}
	
	public static boolean isRedirectUrl(String url) {
		if (TextUtils.isEmpty(url)) {
			return false;
		} else if (url.endsWith("#302")) {
			// 与服务器协商，服务器下发的带有重定向的地址，未尾都以#302结束
			return true;
		} else {
			return false;
		}
		
	}
	
	public static void startExecuteTask(Context context, String url) {
		if (Machine.isNetworkOK(context)) {
			GotoMarketIgnoreBrowserTask task = new GotoMarketIgnoreBrowserTask(context, url);
			task.execute(0);
		} else {
			DeskToast.makeText(context, R.string.desksetting_net_error, Toast.LENGTH_SHORT).show();
		}
	}
}
