package com.jiubang.ggheart.apps.desks.diy.themescan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.MessageManager;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
/**
 * 
 * <br>类描述:从主题banner跳转的webview
 * <br>功能详细描述:
 * 
 * @author  zengyingzhen
 * @date  [2013-10-9]
 */
public class ThemeWebViewActivity extends Activity {
	private final static float DENSITY_H = 1.5f;
	private final static float DENSITY_L = 2.0f;
	private WebView mWebView = null;
	private ProgressBar mProgressBar = null;
	private TextView mProgressText = null;
	private Handler mHandler = new Handler();
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.theme_webview);
		Intent intent = getIntent();
		String url = intent.getStringExtra("url");
		int entryID = intent.getIntExtra("entry", 1);
		Log.i("zyz", "url:" + url);
		// 统计打开活动页面,入口有三个，点击通知栏，点击菜单->活动入口，点击主题预览的banner
		GuiThemeStatistics.winGalaxyStaticData("", GuiThemeStatistics.WIN_GALAXY_ENTRY, 1, entryID
				+ "", "0", "0", "0", "");

		initWebView();
		loadWebView(url);
	}

	private void loadWebView(String url) {
		// TODO Auto-generated method stub
		mWebView.loadUrl(url);

	}

	private void initWebView() {
		// TODO Auto-generated method stub
		if (mWebView == null) {
			mWebView = (WebView) findViewById(R.id.themeWebView);
			mProgressBar = (ProgressBar) findViewById(R.id.progress);
			mProgressText = (TextView) findViewById(R.id.progress_now);
			WebSettings setting = mWebView.getSettings();
			setting.setJavaScriptEnabled(true);
			setting.setDomStorageEnabled(true);
			setting.setDefaultTextEncodingName("utf-8");
			//			setting.setBuiltInZoomControls(true);
			mWebView.setVerticalScrollbarOverlay(true);
			ThemeWebViewClient mWebViewClient = new ThemeWebViewClient();
			ThemeWebViewChromeClient mChromeClient = new ThemeWebViewChromeClient();
			mWebView.setWebViewClient(mWebViewClient);
			mWebView.setWebChromeClient(mChromeClient);
			mWebView.requestFocusFromTouch();
			mWebView.addJavascriptInterface(new ThemeWebViewInterfact(), "clientcallback");
			setWebViewDensity();
		}

	}
	/**
	 * <br>功能简述:显示进度条
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void showProgress() {
		// TODO Auto-generated method stub
		if (mProgressBar != null && mProgressText != null) {
			mProgressBar.setVisibility(View.VISIBLE);
			mProgressText.setVisibility(View.VISIBLE);
		}
	}
	/**
	 * <br>功能简述:隐藏进度条
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void dismissProgress() {
		if (mProgressBar != null && mProgressText != null) {
			mProgressBar.setVisibility(View.GONE);
			mProgressText.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mWebView != null) {
			mWebView.destroy();
			mWebView = null;
		}
	}
	/**
	 * <br>功能简述:可以让不同的density的情况下，可以让页面进行适配
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param mWebView 
	 */
	private void setWebViewDensity() {
		DisplayMetrics dm = new DisplayMetrics();
		dm = this.getResources().getDisplayMetrics();
		float density = dm.density;
		if (density == DENSITY_H) {
			mWebView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
		} else if (density == DENSITY_L) {
			mWebView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
		} else {
			mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
		}
	}
	/**
	 * 
	 * <br>类描述:WebViewClient类
	 * <br>功能详细描述:
	 * 
	 * @author  zengyingzhen
	 * @date  [2013-10-9]
	 */
	public class ThemeWebViewClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
			showProgress();
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return true;
			//			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
			// TODO Auto-generated method stub
			super.onUnhandledKeyEvent(view, event);
			int keyCode = event.getKeyCode();
			if (keyCode == KeyEvent.KEYCODE_BACK && view.canGoBack()) {
				view.goBack();
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			dismissProgress();
		}

	}
	/**
	 * 
	 * <br>类描述:
	 * <br>功能详细描述:
	 * 
	 * @author  zengyingzhen
	 * @date  [2013-11-1]
	 */
	public class ThemeWebViewChromeClient extends WebChromeClient {

		@Override
		public void onProgressChanged(WebView view, final int newProgress) {
			// TODO Auto-generated method stub
			super.onProgressChanged(view, newProgress);

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mProgressText.setText(newProgress + "%");
				}
			});
		}

	}

	/**
	 * 
	 * <br>类描述:js加互
	 * <br>功能详细描述:
	 * 
	 * @author  zengyingzhen
	 * @date  [2013-10-31]
	 */
	public class ThemeWebViewInterfact {

		public void openTheme(String actionValue) {
			Log.i("zyz", "调用了");
			MessageManager manager = MessageManager.getMessageManager(getApplicationContext());
			manager.action(actionValue);
		}

	}

}
