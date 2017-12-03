package com.jiubang.ggheart.components.appmanager;

import java.util.ArrayList;

import android.content.Context;

import com.gau.utils.net.HttpAdapter;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.appfunc.appsupdate.AppsListUpdateManager;
import com.jiubang.ggheart.apps.gowidget.gostore.controller.BaseController;
import com.jiubang.ggheart.apps.gowidget.gostore.controller.IModeChangeListener;
import com.jiubang.ggheart.apps.gowidget.gostore.net.SimpleHttpAdapter;
import com.jiubang.ggheart.apps.gowidget.gostore.net.databean.AppsBean;
import com.jiubang.ggheart.apps.gowidget.gostore.net.databean.AppsBean.AppBean;
import com.jiubang.ggheart.apps.gowidget.gostore.net.databean.BaseBean;

/**
 * 
 * @author zouguiquan
 *
 */
public class SimpleAppManagerController extends BaseController {
	
	public static final int REQUEST_UPDATE_LIST = 1;
	
	/**
	 * 正在连网拿取更新数据
	 */
	public static final int MSG_ID_START = 0;
	/**
	 * 连网读取完成
	 */
	public static final int MSG_ID_FINISH = 1;
	/**
	 * 连网异常 
	 */
	public static final int MSG_ID_EXCEPTION = 2;

	public SimpleAppManagerController(Context context, IModeChangeListener listener) {
		super(context, listener);
	}

	@Override
	protected Object handleRequest(int action, Object parames) {
		switch (action) {
			case REQUEST_UPDATE_LIST : {
				getUpdateList();
			}
				break;

			default :
				break;
		}
		return null;
	}

	private void getUpdateList() {
		// 请求网络数据
		AppsListUpdateManager appsListUpdateManager = AppsListUpdateManager.getInstance(mContext);
		if (appsListUpdateManager != null) {
			// 设置监听者
			HttpAdapter httpAdapter = SimpleHttpAdapter.getHttpAdapter(mContext);
			IConnectListener updateListener = getUpdateContectListener();
			appsListUpdateManager.startCheckUpdate(httpAdapter, updateListener, false, 1);
		}		
	}

	private IConnectListener getUpdateContectListener() {
		IConnectListener updateListener = new IConnectListener() {
			
			@Override
			public void onStart(THttpRequest arg0) {
			}
			
			public void onException(THttpRequest arg0, int arg1) {
				mChangeListener.onModleChanged(REQUEST_UPDATE_LIST, MSG_ID_EXCEPTION, null);
			}

			@Override
			public void onFinish(THttpRequest arg0, IResponse arg1) {
				AppsBean appsBean = null;
				if (arg1 != null) {
					ArrayList<BaseBean> listBeans = (ArrayList<BaseBean>) arg1.getResponse();
					if (listBeans != null && listBeans.size() > 0) {
						appsBean = (AppsBean) listBeans.get(0);
						if (appsBean != null && appsBean.mListBeans != null) {
							dataProcessing(appsBean);
						}
					}
				}
				
				mChangeListener.onModleChanged(REQUEST_UPDATE_LIST, MSG_ID_FINISH, appsBean);
			}
		};
		return updateListener;
	}

	@Override
	public void destory() {
	}

	public void dataProcessing(final AppsBean appsBean) {
		ArrayList<AppBean> appBeanArrayList = appsBean.mListBeans;
		if (appBeanArrayList != null) {
			filterAppBeans(appBeanArrayList);
		}
	}
	
	/**
	 * 过滤已经卸载的应用程序
	 * 
	 * @param appListBeans
	 */
	private void filterAppBeans(ArrayList<AppBean> appListBeans) {
		if (appListBeans == null || appListBeans.size() <= 0) {
			return;
		}

		int size = appListBeans.size();
		ArrayList<AppBean> tmpList = new ArrayList<AppBean>(size);
		for (AppBean bean : appListBeans) {
			boolean isExist = GoAppUtils.isAppExist(mContext, bean.mPkgName);
			if (!isExist) {
				// 如果不存在，则加入临时列表
				tmpList.add(bean);
			}
		}
		for (AppBean item : tmpList) {
			// 把已经卸载或不存在的应用程序从appListBeans中移除
			appListBeans.remove(item);
		}
	}


}
