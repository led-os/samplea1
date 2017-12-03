package com.jiubang.ggheart.apps.desks.diy.themescan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import com.gau.go.launcherex.R;
import com.go.commomidentify.IGoLauncherClassName;
import com.go.proxy.ApplicationProxy;
import com.go.util.BroadCaster.BroadCasterObserver;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.data.statistics.Statistics;
import com.jiubang.ggheart.data.theme.GoLockerThemeManager;
import com.jiubang.ggheart.data.theme.LockerManager;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.data.theme.bean.ThemeBannerBean;
import com.jiubang.ggheart.data.theme.bean.ThemeInfoBean;
import com.jiubang.ggheart.launcher.ICustomAction;

/**
 * 主题数据管理
 * 
 * @author yangbing
 * */
public class ThemeDataManager implements BroadCasterObserver {

	private Context mContext;
	private GoLockerThemeManager mLockerThemeManager; // 锁屏主题管理器
	private ThemeManager mThemeManager;
	private LockerManager mLockerManager;
	private ConcurrentHashMap<Integer, ArrayList<ThemeInfoBean>> mThemeInfoBeansMap = null; // 数据
//	private ThemeBannerBean mBannerData;
//	private ThemeBannerBean mLockerBannerData;
	private String mCurDeskThemePackage; // 当前使用的桌面主题包名
	private String mCurLockerThemePackage; // 当前使用的锁屏主题包名
	private String mGoLockerPkgName; //当前安装的go锁屏的包名
	private static ThemeDataManager sInstance = null;
	private int mSpecId;
	public static final int MSG_GET_BANNER_FINISHED = 1;
	public static final int MSG_GET_THEME_FINISHED = 2;
	private ConcurrentHashMap<Integer, ThemeBannerBean> mBannerData;
	private ThemeDataManager(Context context) {
		mContext = context;
		mLockerThemeManager = new GoLockerThemeManager(mContext);
		mThemeManager = ThemeManager.getInstance(ApplicationProxy.getContext());
		mLockerManager = LockerManager.getInstance(mContext);
		mThemeInfoBeansMap = new ConcurrentHashMap<Integer, ArrayList<ThemeInfoBean>>();
		mCurDeskThemePackage = mThemeManager.getCurThemePackage();
		mGoLockerPkgName = GoAppUtils.getCurLockerPkgName(mContext);
		mBannerData = new ConcurrentHashMap<Integer, ThemeBannerBean>();
	}

	public synchronized static ThemeDataManager getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new ThemeDataManager(context);
		}
		return sInstance;
	}

	/**
	 * 取主题数据
	 * */
	public ArrayList<ThemeInfoBean> getThemeData(int type) {
		if (mThemeInfoBeansMap.containsKey(type)) {
			return mThemeInfoBeansMap.get(type);
		}
		return null;

	}

	/**
	 * <br>功能简述:获取精选banner
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public ThemeBannerBean getBannerData(int type) {
		return mBannerData.get(type);
	}

	/**
	 * 加载主题数据
	 * */
	public void loadThemeData(int type) {
		switch (type) {
			case ThemeConstants.LAUNCHER_FEATURED_THEME_ID :
				loadBannerDatas(ThemeConstants.LAUNCHER_FEATURED_THEME_ID);
				loadLauncherFeaturedThemeDatas();
				break;
			case ThemeConstants.LAUNCHER_INSTALLED_THEME_ID :
				loadLauncherInstalledThemeDatas();
				break;
			case ThemeConstants.LAUNCHER_HOT_THEME_ID :
				loadLauncherHotThemeDatas();
				break;
			case ThemeConstants.LOCKER_FEATURED_THEME_ID :
				loadLockerFeaturedThemeDatas();
				loadBannerDatas(ThemeConstants.LOCKER_FEATURED_THEME_ID);
				break;
			case ThemeConstants.LOCKER_INSTALLED_THEME_ID :
				loadLockerInstalledThemeDatas();
				break;
			case ThemeConstants.LAUNCHER_SPEC_THEME_ID :
				loadLauncherSpecThemeDatas(mSpecId);
				break;

			default :
				break;
		}

	}

	/**
	 * 加载锁屏精选主题
	 * */
	private void loadLockerFeaturedThemeDatas() {
		ArrayList<ThemeInfoBean> featuredThemeInfoBeans = mLockerManager
				.getFeaturedThemeInfoBeans(this);
		filterInstalledTheme(featuredThemeInfoBeans, ThemeConstants.LOCKER_INSTALLED_THEME_ID);
		if (featuredThemeInfoBeans != null && featuredThemeInfoBeans.size() > 0) {
			for (ThemeInfoBean infoBean : featuredThemeInfoBeans) {
				infoBean.setBeanType(ThemeConstants.LOCKER_FEATURED_THEME_ID);
			}
			mThemeInfoBeansMap.put(ThemeConstants.LOCKER_FEATURED_THEME_ID, featuredThemeInfoBeans);
		}

	}

	/**
	 * 加载锁屏本地主题
	 * */
	private void loadLockerInstalledThemeDatas() {
		ArrayList<ThemeInfoBean> themeInfoBeans = new ArrayList<ThemeInfoBean>();
		if (mCurLockerThemePackage == null) {
			mCurLockerThemePackage = mThemeManager.getCurLockerTheme();
		}
		// 默认
		ThemeInfoBean defaultDataBean = new ThemeInfoBean();
		if (GoAppUtils.isGoLockerExist(mContext)) {
			defaultDataBean.setBeanType(ThemeConstants.LOCKER_INSTALLED_THEME_ID);
			defaultDataBean.setThemeName(mContext.getString(R.string.locker_default_theme));
			defaultDataBean.setPackageName(mGoLockerPkgName);
			defaultDataBean.setIsCurTheme(true);
			themeInfoBeans.add(defaultDataBean);
		}
		// 获取随机锁屏
		BitmapDrawable icon = mLockerThemeManager.getRandomPreView(mGoLockerPkgName);
		if (icon != null) {
			ThemeInfoBean dataBean = new ThemeInfoBean();
			dataBean.setBeanType(ThemeConstants.LOCKER_INSTALLED_THEME_ID);
			dataBean.setThemeName(mContext.getString(R.string.random_locker_theme));
			dataBean.setPackageName("com.jiubang.goscreenlock.theme.random");
			if (mCurLockerThemePackage != null
					&& mCurLockerThemePackage.equals(dataBean.getPackageName())) {
				dataBean.setIsCurTheme(true);
				defaultDataBean.setIsCurTheme(false);
			}
			themeInfoBeans.add(dataBean);
		}

		Map<CharSequence, CharSequence> mInstalledLockerThemeMap = mLockerThemeManager
				.queryInstalledTheme();
		Iterator<CharSequence> iterator = mInstalledLockerThemeMap.keySet().iterator();
		while (iterator.hasNext()) {
			CharSequence packageName = iterator.next();
			ThemeInfoBean dataBean = new ThemeInfoBean();
			dataBean.setBeanType(ThemeConstants.LOCKER_INSTALLED_THEME_ID);
			dataBean.setPackageName(packageName.toString());
			dataBean.setThemeName(mInstalledLockerThemeMap.get(packageName).toString());
			if (mCurLockerThemePackage != null
					&& mCurLockerThemePackage.equals(dataBean.getPackageName())) {
				dataBean.setIsCurTheme(true);
				defaultDataBean.setIsCurTheme(false);
			}
			themeInfoBeans.add(dataBean);
		}
		if (themeInfoBeans != null && themeInfoBeans.size() > 0) {
			mThemeInfoBeansMap.put(ThemeConstants.LOCKER_INSTALLED_THEME_ID, themeInfoBeans);
		}

	}

	/**
	 * 加载桌面精选主题
	 * */
	private void loadLauncherFeaturedThemeDatas() {

		ArrayList<ThemeInfoBean> featuredThemeInfoBeans = mThemeManager.getFeaturedThemeInfoBeans(
				ThemeConstants.LAUNCHER_FEATURED_THEME_ID, this);
		// 过滤已安装的主题
		filterInstalledTheme(featuredThemeInfoBeans, ThemeConstants.LAUNCHER_INSTALLED_THEME_ID);
		if (featuredThemeInfoBeans != null && featuredThemeInfoBeans.size() > 0) {
			for (ThemeInfoBean infoBean : featuredThemeInfoBeans) {
				infoBean.setBeanType(ThemeConstants.LAUNCHER_FEATURED_THEME_ID);
			}
			mThemeInfoBeansMap.put(ThemeConstants.LAUNCHER_FEATURED_THEME_ID,
					featuredThemeInfoBeans);
		}

	}
	
	public List<ThemeInfoBean> loadExactLauncherFeaturedThemeDatas(int size) {
		
		List<ThemeInfoBean> featuredList = mThemeManager.getFeaturedThemeInfoBeans(
				ThemeConstants.LAUNCHER_FEATURED_THEME_ID, this);
		
		if (featuredList == null || featuredList.size() <= 0) {
			return null;
		}

		// 过滤已安装的主题
		for (int i = 0; i < featuredList.size();) {
			ThemeInfoBean bean = featuredList.get(i);
			if (ThemeManager.isInstalledTheme(mContext, bean.getPackageName())) {
				featuredList.remove(i);
				continue;
			} else {
				++i;
			}
		}
		
		if (featuredList.size() >= size) {
			featuredList = featuredList.subList(0, size);
		}

		for (ThemeInfoBean infoBean : featuredList) {
			infoBean.setBeanType(ThemeConstants.LAUNCHER_FEATURED_THEME_ID);
		}
		
		return featuredList;
	}

	private void addThemeDataToMap(ArrayList<ThemeInfoBean> list, int type) {
		if (list == null || list.isEmpty()) {
			return;
		}
		int fliterId = 0;
		if (type == ThemeConstants.LAUNCHER_FEATURED_THEME_ID
				|| type == ThemeConstants.LAUNCHER_HOT_THEME_ID) {
			fliterId = ThemeConstants.LAUNCHER_INSTALLED_THEME_ID;
		} else {
			fliterId = ThemeConstants.LOCKER_INSTALLED_THEME_ID;
		}
		// 过滤已安装的主题
		filterInstalledTheme(list, fliterId);
		mThemeInfoBeansMap.put(type, list);
	}

	/**
	 * 加载桌面热门主题
	 * */
	private void loadLauncherHotThemeDatas() {
		ArrayList<ThemeInfoBean> hotThemeInfoBeans = mThemeManager.getFeaturedThemeInfoBeans(
				ThemeConstants.LAUNCHER_HOT_THEME_ID, this);
		// 过滤已安装的主题
		filterInstalledTheme(hotThemeInfoBeans, ThemeConstants.LAUNCHER_INSTALLED_THEME_ID);
		if (hotThemeInfoBeans != null && hotThemeInfoBeans.size() > 0) {
			for (ThemeInfoBean infoBean : hotThemeInfoBeans) {
				infoBean.setBeanType(ThemeConstants.LAUNCHER_HOT_THEME_ID);
			}
			mThemeInfoBeansMap.put(ThemeConstants.LAUNCHER_HOT_THEME_ID, hotThemeInfoBeans);
		}

	}
	/**
	 * 加载桌面热门主题
	 * */
	private void loadLauncherSpecThemeDatas(int ty) {
		ArrayList<ThemeInfoBean> specThemeInfoBeans = mThemeManager.getSpecThemeInfoBeans(ty, this);
		if (specThemeInfoBeans != null) {
			// 过滤已安装的主题
			filterInstalledTheme(specThemeInfoBeans, ThemeConstants.LAUNCHER_INSTALLED_THEME_ID);
			mThemeInfoBeansMap.put(ThemeConstants.LAUNCHER_SPEC_THEME_ID, specThemeInfoBeans);
		}
	}

	/**
	 * 加载Banner数据
	 * 如果本地已有就不要再从网络获取
	 * */
	private void loadBannerDatas(int type) {
		ThemeBannerBean bannerData = mBannerData.get(type);
		if (bannerData == null) {
			if (type == ThemeConstants.LOCKER_FEATURED_THEME_ID
					&& !GoAppUtils.isGoLockerExist(mContext)) {
				bannerData = new ThemeBannerBean();
				bannerData.mType = ThemeConstants.LOCKER_UNINSTALL_BANNER;
			} else {
				bannerData = mThemeManager.getBannerData(type, this);
			}
			if (bannerData != null) {
				mBannerData.put(bannerData.mType, bannerData);
			}
		}
//		if (type == ThemeConstants.LAUNCHER_FEATURED_THEME_ID) {
//			if (bannerData == null) {
//				mBannerData = mThemeManager.getBannerData(type, this);
//			}
//		} else if (type == ThemeConstants.LOCKER_FEATURED_THEME_ID) {
//			if (!AppUtils.isGoLockerExist(mContext)) {
//				mLockerBannerData = new ThemeBannerBean();
//				mLockerBannerData.mType = ThemeConstants.LOCKER_UNINSTALL_BANNER;
//			} else if (mLockerBannerData == null) {
//				mLockerBannerData = mThemeManager.getBannerData(type, this);
//			}
//		}
	}

	/**
	 * 加载桌面本地主题
	 * */
	private void loadLauncherInstalledThemeDatas() {

		ArrayList<ThemeInfoBean> themeInfoBeans = mThemeManager.getAllInstalledThemeInfos();
		if (themeInfoBeans != null && themeInfoBeans.size() > 0) {
			for (ThemeInfoBean infoBean : themeInfoBeans) {
				infoBean.setBeanType(ThemeConstants.LAUNCHER_INSTALLED_THEME_ID);
				if (mCurDeskThemePackage.equals(infoBean.getPackageName())) {
					infoBean.setIsCurTheme(true);
				} else if (mCurDeskThemePackage.equals(IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3)
						&& infoBean.getPackageName().equals(
								IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER)) {
					infoBean.setIsCurTheme(true);
				}
			}
			if (Statistics.is200ChannelUid(mContext) && !GoAppUtils.isCnUser(mContext)) {
				ArrayList<ThemeInfoBean> list = new ArrayList<ThemeInfoBean>();
				ThemeInfoBean bean = new ThemeInfoBean();
				bean.setBeanType(ThemeConstants.THEMEM_COUPON_ID);
				bean.setPackageName(mContext.getPackageName());
				list.add(0, bean); //添加一个用于显示优惠劵入口
				list.addAll(themeInfoBeans);
				mThemeInfoBeansMap.put(ThemeConstants.LAUNCHER_INSTALLED_THEME_ID, list);
			} else {
				mThemeInfoBeansMap.put(ThemeConstants.LAUNCHER_INSTALLED_THEME_ID, themeInfoBeans);

			}
		}

	}

	/**
	 * 过滤已经安装的主题
	 * 
	 * @author yangbing
	 * @param arrayList
	 *            :需要过滤的集合
	 * @param type
	 */
	private void filterInstalledTheme(ArrayList<ThemeInfoBean> arrayList, int type) {
		if (arrayList == null || arrayList.size() <= 0) {
			return;
		}
		if (type == ThemeConstants.LAUNCHER_INSTALLED_THEME_ID) {
			for (int i = 0; i < arrayList.size();) {
				ThemeInfoBean bean = arrayList.get(i);
				if (ThemeManager.isInstalledTheme(mContext, bean.getPackageName())) {
					// 已安装
					arrayList.remove(i);
					continue;
				} else {
					++i;
				}
			}
		} else if (type == ThemeConstants.LOCKER_INSTALLED_THEME_ID) {
			for (int i = 0; i < arrayList.size();) {
				ThemeInfoBean bean = arrayList.get(i);
				String packageName = bean.getPackageName();
				if (packageName != null) {
					if (packageName.startsWith("n") || packageName.startsWith("s")
							|| packageName.startsWith("t")) {
						packageName = packageName.substring(1);
					}
				}
				if (ThemeManager.isInstalledTheme(mContext, bean.getPackageName())) {
					// 已安装
					arrayList.remove(i);
					continue;
				}

				else if (GoAppUtils.isCnUser(mContext) && bean.getPackageName().startsWith("s")) {
					// 国内，过滤收费的锁屏
					arrayList.remove(i);
					continue;
				} else {
					++i;
				}
			}
		}
	}

	public void setmCurLockerThemePackage(String mCurLockerThemePackage) {
		this.mCurLockerThemePackage = mCurLockerThemePackage;
	}

	/**
	 * 清理
	 * */
	public void clearup() {
		mThemeInfoBeansMap.clear();
		mCurDeskThemePackage = mThemeManager.getCurThemePackage();
		mCurLockerThemePackage = mThemeManager.getCurLockerTheme();
	}

	@Override
	public void onBCChange(int msgId, int param, Object ...object) {
		// TODO Auto-generated method stub
		switch (msgId) {
			case MSG_GET_BANNER_FINISHED :
				if (object != null && object[0] instanceof ThemeBannerBean) {
					ThemeBannerBean bean = (ThemeBannerBean) object[0];
					int type = ThemeConstants.LAUNCHER_FEATURED_THEME_ID;
					if (bean.mType == type) {
					} else {
						type = ThemeConstants.LOCKER_FEATURED_THEME_ID;
					}
					mBannerData.put(bean.mType, bean);
					if (mContext != null) {
						Intent intent = new Intent(ICustomAction.ACTION_BANNER_DATA_CHANGEED);
						intent.setData(Uri.parse("package://"));
						intent.putExtra("type", type);
						mContext.sendBroadcast(intent);
					}
				}
				break;
//			case MSG_GET_SPEC_FINISHED :
//				if (object != null) {
//					ArrayList<ThemeInfoBean> infoBeans = (ArrayList<ThemeInfoBean>) object;
//					filterInstalledTheme(infoBeans, ThemeConstants.LAUNCHER_INSTALLED_THEME_ID);
//					mThemeInfoBeansMap.put(ThemeConstants.LAUNCHER_SPEC_THEME_ID, infoBeans);
//				}
//				Intent intent = new Intent(ICustomAction.ACTION_SPEC_THEME_CHANGED);
//				intent.setData(Uri.parse("package://"));
//				mContext.sendBroadcast(intent);
//				break;
			case MSG_GET_THEME_FINISHED : {
				int type = param;
				if (object[0] != null) {
					ArrayList<ThemeInfoBean> beans = (ArrayList<ThemeInfoBean>) object[0];
					addThemeDataToMap(beans, type);
				}
				Intent intent = new Intent(ICustomAction.ACTION_FEATURED_THEME_LOADFINISHED);
				intent.setData(Uri.parse("package://"));
				intent.putExtra("type", type);
				mContext.sendBroadcast(intent);
			}
			default :
				break;
		}
	}
	/**
	 * <br>功能简述:设置专题ID
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param id
	 */
	public void setSpecThemeId(int id) {
		mSpecId = id;
	}
	
	public void removeData(int type) {
		if (mThemeInfoBeansMap != null && mThemeInfoBeansMap.containsKey(type)) {
			mThemeInfoBeansMap.remove(type);
		}
	}
}
