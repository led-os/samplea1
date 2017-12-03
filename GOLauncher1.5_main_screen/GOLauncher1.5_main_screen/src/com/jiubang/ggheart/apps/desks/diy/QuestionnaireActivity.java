package com.jiubang.ggheart.apps.desks.diy;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gau.go.launcherex.R;
import com.go.util.AppUtils;
import com.go.util.device.Machine;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;

/**
 * 问卷调查
 * @author liulixia
 *
 */
public class QuestionnaireActivity extends Activity {
	private WebView mWebView = null;
	private final static float DENSITY_H = 1.5f;
	private final static float DENSITY_L = 2.0f;
	
	private String mImei = "";
	private String mLang = "";
	private String mLocal = "";
	private String mChannel = "";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.questionnaire_layout);
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
        
        MyWebViewClient mWebViewClient = new MyWebViewClient();
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(new MessageWebChromeClient());
        
        setWebViewDensity();
        
        WebviewExchangeInterface exchangeInterface = new WebviewExchangeInterface();
        mWebView.addJavascriptInterface(exchangeInterface, "buttonclick");
        
        //4.0及以上系统，使用固定底部按钮的页面。4.0以下的，使用滑动页面。
        //默认使用4.0以上的英文页面
        String htmlFilePath = "file:///android_asset/uninstall_en.html";
        if (Machine.IS_ICS) {
        	//4.0及以上
        	if (mLang.equals("zh")) {
        		//中文
        		htmlFilePath = "file:///android_asset/uninstall_cn.html";
        	}
        } else {
        	//4.0以下
        	if (mLang.equals("zh")) {
        		//中文
        		htmlFilePath = "file:///android_asset/uninstall_cn_nobottom.html";
        	} else {
        		//英文 
        		htmlFilePath = "file:///android_asset/uninstall_en_nobottom.html";
        	}
        }
        mWebView.loadUrl(htmlFilePath);
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
			super.onPageFinished(view, url);
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
			// TODO Auto-generated method stub
			super.onReceivedError(view, errorCode, description, failingUrl);
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
			AppUtils.uninstallPackage(QuestionnaireActivity.this, getPackageName());
			QuestionnaireActivity.this.finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mWebView = null;
	}
	
	
}
