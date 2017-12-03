package com.jiubang.ggheart.apps.desks.Preferences;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import com.gau.go.launcherex.R;
import com.go.util.AppUtils;
import com.go.util.device.Machine;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.MessageManager;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.components.GoProgressBar;

/**
 * 功能预告
 * @author chenguanyu
 *
 */
public class FuncForeActivity extends Activity {
	private WebView mWebView = null;
	private final static float DENSITY_H = 1.5f;
	private final static float DENSITY_L = 2.0f;
	
	private String mImei = "";
	private String mLang = "";
	private String mLocal = "";
	private String mChannel = "";
	
	private String mFuncForeUrl = null;
	
	private LinearLayout mConnectFailLayout = null; // 连接失败提示布局
	private Button mRefreshBtn = null; // 刷新按钮
	private GoProgressBar mProgressBar = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.func_fore_layout);
		
		mConnectFailLayout = (LinearLayout) findViewById(R.id.connect_fail_layout);
		mRefreshBtn = (Button) mConnectFailLayout.findViewById(R.id.refreshBtn);
		mProgressBar = (GoProgressBar) findViewById(R.id.modify_progress);
		
		mWebView = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        mWebView.setVerticalScrollbarOverlay(true);
        
        mLang = Locale.getDefault().getLanguage().toLowerCase();
        mLocal = Machine.getCountry(this);
        mImei = GoStorePhoneStateUtil.getVirtualIMEI(this);
        mChannel = GoStorePhoneStateUtil.getUid(this);
        
        MyWebViewClient webViewClient = new MyWebViewClient();
        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(new MessageWebChromeClient());
        
        setWebViewDensity();
        
        WebviewExchangeInterface exchangeInterface = new WebviewExchangeInterface();
        mWebView.addJavascriptInterface(exchangeInterface, "buttonclick");

        showProgress();
        requestUrl();
	}
	
	private void requestUrl() {
		mFuncForeUrl = MessageManager.getMessageManager(this).getFuncForeUrl();
		if (mFuncForeUrl != null && !mFuncForeUrl.equals("")) {
        	mWebView.loadUrl(mFuncForeUrl);
		} else {
			showFailView();
		}
	}
	
	/**
	 * <br>功能简述:可以让不同的density的情况下，可以让页面进行适配
	 * <br>功能详细描述:
	 * <br>注意:
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
	
	private void showFailView() {
		if (mConnectFailLayout != null) {
			mConnectFailLayout.setVisibility(View.VISIBLE);
			mWebView.setVisibility(View.GONE);
			dimissProgress();
		}
		if (mRefreshBtn != null) {
			mRefreshBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mConnectFailLayout.setVisibility(View.GONE);
					mWebView.setVisibility(View.VISIBLE);
					showProgress();
//					mWebView.reload();
					requestUrl();
				}
			});
		}
	}
	
	private void showProgress() {
		if (mProgressBar != null && mProgressBar.getVisibility() == View.INVISIBLE) {
			mProgressBar.setVisibility(View.VISIBLE);
		}
	}
	
	private void dimissProgress() {
		if (mProgressBar != null && mProgressBar.getVisibility() == View.VISIBLE) {
			mProgressBar.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 
	 * @author liulixia
	 *
	 */
	class MyWebViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		};
		
		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
//			super.onPageFinished(view, url);
			dimissProgress();
			view.loadUrl("javascript:init(\'" + mImei + "\',\'" + mLang + "\',\'" + mLocal + "\',\'" + mChannel + "\')");
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onLoadResource(view, url);
			
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description,
				String failingUrl) {
			showFailView();
		}
		
		// 新开页面时用自己webview来显示，不用系统自带的浏览器来显示
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

	}
	
	/**
	 * 
	 * @author liulixia
	 *
	 */
	class MessageWebChromeClient extends WebChromeClient {
		// 通过JS代码输出log信息
		@Override
		public void onConsoleMessage(String message, int lineNumber, String sourceID) {
			
		}

		// 设置网页加载的进度条
		@Override
		public void onProgressChanged(WebView view, final int newProgress) {
			super.onProgressChanged(view, newProgress);
		}
	}
	/**
	 * 与javascript交互类
	 * @author liulixia
	 *
	 */
	class WebviewExchangeInterface {
		public void uninstalledGoLauncher() {
			AppUtils.uninstallPackage(FuncForeActivity.this, getPackageName());
			FuncForeActivity.this.finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mWebView = null;
	}
}
