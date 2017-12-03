package com.go.proxy;

import com.jiubang.ggheart.appgame.download.IDownloadService;

/**
 * 下载控制器代理
 * @author liuheng
 *
 */
public class DownloadControllerProxy {
	private IDownloadService mDownloadController = null;
	private static DownloadControllerProxy sInstance;
	private DownloadControllerProxy() {
		
	}
	
	public static DownloadControllerProxy getInstance() {
		if (null == sInstance) {
			synchronized (DownloadControllerProxy.class) {
				if (null == sInstance) {
					sInstance = new DownloadControllerProxy();
				}
			}
		}

		return sInstance;
	}
	/**
	 * <br>
	 * 功能简述:设置下载服务控制接口 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void setDownloadController(IDownloadService downloadController) {
		mDownloadController = downloadController;
	}
	
	/**
	 * <br>
	 * 功能简述:获取下载控制接口 <br>
	 * 功能详细描述: <br>
	 * 注意:获取下载控制接口后，注意是否为null
	 * 
	 * @return
	 */
	public IDownloadService getDownloadController() {
		return mDownloadController;
	}
}
