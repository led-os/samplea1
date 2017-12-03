package com.jiubang.ggheart.components.appmanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.util.AppUtils;
import com.go.util.device.Machine;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.appcenter.contorler.AppsManageViewController;
import com.jiubang.ggheart.appgame.base.utils.NetworkTipsTool;
import com.jiubang.ggheart.apps.gowidget.gostore.controller.IModeChangeListener;
import com.jiubang.ggheart.apps.gowidget.gostore.net.databean.AppsBean;
import com.jiubang.ggheart.apps.gowidget.gostore.net.databean.AppsBean.AppBean;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.components.DeskTextView;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.IGoLauncherUserBehaviorStatic;

/**
 * 
 * @author zouguiquan
 *
 */
public class UpdateAppView extends LinearLayout implements OnClickListener, IModeChangeListener {

	private SimpleAppManagerController managerController;

	private Context mContext;
	private ListView mListView;
	private Button mBtnUpdateAll;
	private ViewGroup mNetworkTipsView;
	private SimpleAppsUpdateAdapter mAdapter;
	private NetworkTipsTool mNetworkTipsTool;
	
	private Handler mHandler;
	private View mHeadView;
	private boolean mNeedToRequest = true;
	
	/**
	 * 获取更新数据的状态
	 */
	private int mUpdateState = AppsManageViewController.MSG_ID_NOT_START;

	private View getHeadView() {
		View headView = LayoutInflater.from(mContext).inflate(
					R.layout.app_manager_upate_list_title_item, null);
		return headView;
	}
	
	/**
	 * 数据列表为空时的重试点击监听器，刷新当前tab
	 */
	private OnClickListener mRetryClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			requestUpdateList();
		}
	};

	public UpdateAppView(Context context) {
		super(context);
		mContext = context;		
	}

	public UpdateAppView(Context context, LayoutInflater layoutInflater) {
		this(context);
		layoutInflater.inflate(R.layout.update_app_view, this);
		initView();
		initHandler();
	}
	
	public void requestUpdateList() {

		if (Machine.isNetworkOK(getContext())) {
			if (!mNeedToRequest) {
				return;
			} 
			mNeedToRequest = false;
			
			if (managerController == null) {
				managerController = new SimpleAppManagerController(mContext, this);
			}
			
			managerController.sendRequest(SimpleAppManagerController.REQUEST_UPDATE_LIST, null);
			mNetworkTipsTool.showProgress();
		} else {
			mNetworkTipsTool.showRetryErrorTip(mRetryClickListener, NetworkTipsTool.TYPE_NETWORK_EXCEPTION);
		}
	}

	public void initView() {

		mNetworkTipsView = (ViewGroup) findViewById(R.id.network_tips_view);
		mNetworkTipsTool = new NetworkTipsTool(mNetworkTipsView);
		mBtnUpdateAll = (Button) findViewById(R.id.btn_update_all);
		mBtnUpdateAll.setOnClickListener(this);

		mListView = (ListView) findViewById(R.id.listview);
		mHeadView = getHeadView();
		mListView.addHeaderView(mHeadView);
		mAdapter = new SimpleAppsUpdateAdapter(mContext, null);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				if (view instanceof SimpleAppsUpdateItem) {
					((SimpleAppsUpdateItem) view).actionDownload();
					GuiThemeStatistics
							.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.APP_MANAGER_04);
				}
			}
		});
	}
	
	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.arg1) {
					case SimpleAppManagerController.MSG_ID_START : {
						mUpdateState = SimpleAppManagerController.MSG_ID_START;
					}
						break;

					case SimpleAppManagerController.MSG_ID_FINISH : {
						mUpdateState = SimpleAppManagerController.MSG_ID_FINISH;
						mNetworkTipsTool.dismissProgress();
						
						ArrayList<AppBean> listBeans = null;
						AppsBean appsBean = (AppsBean) msg.obj;
						
						if (appsBean != null) {
							listBeans = appsBean.mListBeans;
						}

						if (listBeans != null && listBeans.size() > 0) {
							mListView.setVisibility(View.VISIBLE);
							String title = mContext.getString(R.string.app_manager_udpate_size_title, listBeans.size());
							((DeskTextView) mHeadView.findViewById(R.id.title)).setText(title);
							mAdapter.setDataSet(listBeans);
						} else {
							mListView.setVisibility(View.GONE);
							mAdapter.setDataSet(null);
							if (mNetworkTipsTool != null) {
								mNetworkTipsTool.showRetryErrorTip(mRetryClickListener,
										NetworkTipsTool.TYPE_NO_UPDATE);
							}
							mNeedToRequest = true;
						}
					}
						break;

					case SimpleAppManagerController.MSG_ID_EXCEPTION : {
						mNetworkTipsTool.dismissProgress();
						mNetworkTipsTool.showRetryErrorTip(mRetryClickListener, NetworkTipsTool.TYPE_NETWORK_EXCEPTION);
						mUpdateState = SimpleAppManagerController.MSG_ID_EXCEPTION;
						mNeedToRequest = true;
					}
						break;

					default :
						break;
				}
			}
		};
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_update_all :
				if (Machine.isNetworkOK(getContext())) {
					if (GoStorePhoneStateUtil.is200ChannelUid(mContext)
							|| !GoAppUtils.isCnUser(mContext)) {
						
						if (!AppUtils.gotoMarketMyApp(mContext)) {
							Toast.makeText(getContext(), R.string.no_googlemarket_tip,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Toast.makeText(mContext, R.string.appgame_network_error_no_connection,
							Toast.LENGTH_SHORT).show();
				}
				GuiThemeStatistics
				.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.APP_MANAGER_05);
				break;

			default :
				break;
		}
	}
	
	private void sendMessage(int eventId, Object object) {
		Message msg = new Message();
		msg.arg1 = eventId;
		msg.obj = object;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onModleChanged(int action, int state, Object value) {
		switch (action) {
			case SimpleAppManagerController.REQUEST_UPDATE_LIST :
				
				if (state == SimpleAppManagerController.MSG_ID_FINISH 
						|| state == SimpleAppManagerController.MSG_ID_EXCEPTION) {
					sendMessage(state, value);
				}
				break;

			default :
				break;
		}
	}
	
	public void onResume() {
		requestUpdateList();
	}

	public void dispatchInstallEvent(Context context, Intent intent) {
		final String action = intent.getAction();
		if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
				|| Intent.ACTION_PACKAGE_REMOVED.equals(action)
				|| Intent.ACTION_PACKAGE_ADDED.equals(action)) {

			final String packageName = intent.getData().getSchemeSpecificPart();

			if (mAdapter != null) {
				List<AppBean> appBeans = mAdapter.getAppBeanList();

				if (appBeans != null && !appBeans.isEmpty()) {
					mListView.setVisibility(View.VISIBLE);
					AppBean appBean = null;
					int size = appBeans.size();
					for (int i = 0; i < size; ) {
						appBean = appBeans.get(i);
						if (appBean.mAppId > 0 && appBean.mPkgName.equals(packageName)) {
							appBeans.remove(i);
							break;
						}
						i++;
					}
					mAdapter.setDataSet(appBeans);
				} else {
					mListView.setVisibility(View.GONE);
					if (mNetworkTipsTool != null) {
						mNetworkTipsTool.showRetryErrorTip(mRetryClickListener,
								NetworkTipsTool.TYPE_NO_UPDATE);
					}
				}
			}
		}
	}

	public void dispatchNetworkEvent(Context context, Intent intent) {
		// 通知图片管理器网络状态发生改变
		if (Machine.isNetworkOK(getContext())) {
			if (mUpdateState == AppsManageViewController.MSG_ID_EXCEPTION
					|| mUpdateState == AppsManageViewController.MSG_ID_NOT_START) {
				requestUpdateList();
			}
		} else {
			mNetworkTipsTool.showRetryErrorTip(mRetryClickListener, NetworkTipsTool.TYPE_NETWORK_EXCEPTION);
			mUpdateState = AppsManageViewController.MSG_ID_EXCEPTION;
		}
	}
}
