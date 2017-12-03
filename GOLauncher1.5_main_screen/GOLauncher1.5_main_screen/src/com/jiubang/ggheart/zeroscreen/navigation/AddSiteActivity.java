package com.jiubang.ggheart.zeroscreen.navigation;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 
 * @author zhujian
 * 
 */
public class AddSiteActivity extends Activity implements OnClickListener {

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
//
//	private ImageView mBackImageView; // 返回
//	private EditText mUrlEditText; // 网址输入
//	private EditText mUrlNameEditText; // 网址名字输入
//	private TextView mAddTextView; // 添加
//	private ListView mListView; // 列表显示
//	private HotSiteAdapter mHotSiteAdapter;
//	private int mPosition;
//	private ArrayList<ZeroScreenAdSuggestSiteInfo> mHotList;
//	private ArrayList<String> mUrls;
//	// private RotateView mRotateView;
//	private LinearLayout mLinearLayout;
//	private ImageView mLine;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.zero_screen_add_ad_layout);
//		init();
//		Bundle bundle = this.getIntent().getExtras();
//		if (bundle != null) {
//			getUrls(bundle);
//		}
//	}
//
//	private void getUrls(Bundle bundle) {
//		mUrls = new ArrayList<String>();
//		mUrls.add(bundle.getString(NavigationView.ZERO_SCREEN_AD_POSITION_ONE) != null ? bundle
//				.getString(NavigationView.ZERO_SCREEN_AD_POSITION_ONE).replace(
//						ToolUtil.HTTPHEAD, "") : null);
//		mUrls.add(bundle.getString(NavigationView.ZERO_SCREEN_AD_POSITION_TWO) != null ? bundle
//				.getString(NavigationView.ZERO_SCREEN_AD_POSITION_TWO).replace(
//						ToolUtil.HTTPHEAD, "") : null);
//		mUrls.add(bundle
//				.getString(NavigationView.ZERO_SCREEN_AD_POSITION_THREE) != null ? bundle
//				.getString(NavigationView.ZERO_SCREEN_AD_POSITION_THREE)
//				.replace(ToolUtil.HTTPHEAD, "") : null);
//		mUrls.add(bundle.getString(NavigationView.ZERO_SCREEN_AD_POSITION_FOUR) != null ? bundle
//				.getString(NavigationView.ZERO_SCREEN_AD_POSITION_FOUR)
//				.replace(ToolUtil.HTTPHEAD, "") : null);
//		mUrls.add(bundle.getString(NavigationView.ZERO_SCREEN_AD_POSITION_FIVE) != null ? bundle
//				.getString(NavigationView.ZERO_SCREEN_AD_POSITION_FIVE)
//				.replace(ToolUtil.HTTPHEAD, "") : null);
//		mUrls.add(bundle.getString(NavigationView.ZERO_SCREEN_AD_POSITION_SIX) != null ? bundle
//				.getString(NavigationView.ZERO_SCREEN_AD_POSITION_SIX).replace(
//						ToolUtil.HTTPHEAD, "") : null);
//		mUrls.add(bundle
//				.getString(NavigationView.ZERO_SCREEN_AD_POSITION_SEVEN) != null ? bundle
//				.getString(NavigationView.ZERO_SCREEN_AD_POSITION_SEVEN)
//				.replace(ToolUtil.HTTPHEAD, "") : null);
//		mUrls.add(bundle
//				.getString(NavigationView.ZERO_SCREEN_AD_POSITION_EIGHT) != null ? bundle
//				.getString(NavigationView.ZERO_SCREEN_AD_POSITION_EIGHT)
//				.replace(ToolUtil.HTTPHEAD, "") : null);
//		mPosition = bundle.getInt(NavigationView.ZERO_SCREEN_AD_POSITION);
//	}
//
//	private void init() {
//		mLinearLayout = (LinearLayout) findViewById(R.id.list_title);
//		mLine = (ImageView) findViewById(R.id.line);
//		// mRotateView = (RotateView) findViewById(R.id.progressBar);
//		mBackImageView = (ImageView) findViewById(R.id.back);
//		mBackImageView.setOnClickListener(this);
//		mUrlEditText = (EditText) findViewById(R.id.input_url);
//		mUrlNameEditText = (EditText) findViewById(R.id.input_name);
//		mAddTextView = (TextView) findViewById(R.id.add);
//		mAddTextView.setOnClickListener(this);
//		mListView = (ListView) findViewById(R.id.url_listview);
//		ArrayList<ZeroScreenAdSuggestSiteInfo> allHostList = new ArrayList<ZeroScreenAdSuggestSiteInfo>();
//		allHostList = SuggestSiteObtain.getInstance()
//				.getZeroScreenAdSuggestSiteInfoList(getApplicationContext());
//		// setHotsitesViewState(allHostList);
//		makeZeroScreenAdSuggestSiteInfoList(allHostList);
//		mHotSiteAdapter = new HotSiteAdapter(getApplicationContext(), mHotList);
//		mListView.setAdapter(mHotSiteAdapter);
//		mListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				if (mHotList != null && mHotList.size() > position) {
//					ZeroScreenAdSuggestSiteInfo clickItemInfo = mHotList
//							.get(position);
//					mUrlNameEditText.setText(clickItemInfo.mTitle);
//					mUrlEditText.setText(clickItemInfo.mUrl);
//					mUrlEditText.setSelection(clickItemInfo.mUrl.length());
//				}
//			}
//		});
//	}
//
//	private void makeZeroScreenAdSuggestSiteInfoList(
//			ArrayList<ZeroScreenAdSuggestSiteInfo> list) {
//		mHotList = new ArrayList<ZeroScreenAdSuggestSiteInfo>();
//		if (list == null || list.size() == 0) {
//			initDefaultData(mHotList);
//			return;
//		}
//		for (int i = 0; i < list.size(); i++) {
//			if (i >= 6) {
//				mHotList.add(list.get(i));
//			}
//		}
//	}
//
//	// 没有数据使用默认数据
//	private void initDefaultData(ArrayList<ZeroScreenAdSuggestSiteInfo> list) {
//		for (int i = 0; i < SuggestSiteDefaultData.WEB_CATEGORY_TITLE.length; i++) {
//			ZeroScreenAdSuggestSiteInfo info = new ZeroScreenAdSuggestSiteInfo();
//			info.mTitle = SuggestSiteDefaultData.WEB_CATEGORY_TITLE[i];
//			info.mUrl = SuggestSiteDefaultData.WEB_CATEGORY_URL[i];
//			info.mDrawable = this.getResources().getDrawable(
//					SuggestSiteDefaultData.WEB_PIC[i]);
//			list.add(info);
//		}
//	}
//
//	/**
//	 * 根据是否有热门网址，设置不同的view
//	 */
//	private void setHotsitesViewState(
//			ArrayList<ZeroScreenAdSuggestSiteInfo> list) {
//		if (list == null || list.size() == 0) {
//			mLinearLayout.setVisibility(View.GONE);
//			mListView.setVisibility(View.GONE);
//			mLine.setVisibility(View.GONE);
//		} else {
//			mLinearLayout.setVisibility(View.VISIBLE);
//			mListView.setVisibility(View.VISIBLE);
//			mLine.setVisibility(View.VISIBLE);
//		}
//	}
//
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.add:
//
//			String webUrl = mUrlEditText.getText().toString();
//			String webName = mUrlNameEditText.getText().toString();
//
//			if (mUrls.contains(webUrl)) {
//				Toast toast = Toast.makeText(this,
//						R.string.zero_screen_rpt_tip, Toast.LENGTH_SHORT);
//				toast.setGravity(Gravity.CENTER, 0, 0);
//				toast.show();
//				return;
//			}
//			int urlLen = webUrl.trim().length();
//			int nameLen = webName.trim().length();
//			String urlCheck = "[a-zA-z]+://[^\\s]+"; // 匹配网址表达式
//			String urlCheck2 = "[^\\s]+"; // 匹配网址表达式2
//
//			Boolean isValidUrl = false;
//			if (webUrl != null && !webUrl.equals("")) {
//				if (webUrl.startsWith(ToolUtil.HTTPHEAD)
//						|| webUrl.startsWith(ToolUtil.HTTPSHEAD)) {
//					isValidUrl = Pattern.matches(urlCheck, webUrl);
//				} else {
//					isValidUrl = Pattern.matches(urlCheck2, webUrl);
//					webUrl = ToolUtil.HTTPHEAD.concat(webUrl);
//				}
//
//			}
//
//			if (urlLen > 0 && nameLen > 0 && isValidUrl) {
//				// 保存到数据库
//				ZeroScreenAdInfo info = new ZeroScreenAdInfo();
//				info.mIsPlus = false;
//				info.mUrl = webUrl;
//				info.mTitle = webName;
//				info.mPosition = mPosition;
//				info.mIsRecommend = false;
//				NavigationController.getInstance(getApplicationContext())
//						.updateZeroScreenAdInfo(info);
//				GuiThemeStatistics.getInstance(getApplicationContext())
//						.guiStaticData(
//								57,
//								info.mUrl,
//								StatisticsUtils.URL_ADD,
//								1,
//								"0",
//								"2",
//								String.valueOf(info.mPosition),
//								info.mTitle + StatisticsUtils.PROTOCOL_DIVIDER
//										+ (info.mIsRecommend ? "1" : "2"));
//				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
//						IDiyMsgIds.SCREEN_ZERO_SEND_MESSAGE,
//						ZeroScreenParamId.SCREEN_ZERO_NAVIGATION_ADD, info,
//						null);
////				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
////						IDiyMsgIds.SCREEN_ZERO_SEND_MESSAGE,
////						ZeroScreenParamId.SCREEN_ZERO_NAVIGATION_SHOW, null,
////						null);
//				finish();
//			} else {
//				Toast toast = Toast.makeText(this,
//						R.string.zero_screen_enter_url_and_title,
//						Toast.LENGTH_SHORT);
//				toast.setGravity(Gravity.CENTER, 0, 0);
//				toast.show();
//			}
//			break;
//		case R.id.back:
//			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
//					IDiyMsgIds.SCREEN_ZERO_SEND_MESSAGE,
//					ZeroScreenParamId.SCREEN_ZERO_NAVIGATION_SHOW, null, null);
//			finish();
//			break;
//		default:
//			break;
//		}
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//
//			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
//					IDiyMsgIds.SCREEN_ZERO_SEND_MESSAGE,
//					ZeroScreenParamId.SCREEN_ZERO_NAVIGATION_SHOW, null, null);
//			finish();
//
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		// SuggestImageCache.getInstance(getApplicationContext()).clear();
//	}
}
