package com.jiubang.ggheart.data.statistics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Environment;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.go.gostaticsdk.utiltool.Machine;
import com.gau.go.gostaticsdk.utiltool.UtilTool;
import com.gau.utils.cache.CacheManager;
import com.gau.utils.cache.impl.FileCacheImpl;
import com.gau.utils.cache.utils.CacheUtil;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.SettingProxy;
import com.go.util.AppUtils;
import com.go.util.file.FileUtil;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.ScreenIndicator;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.data.info.DesktopSettingInfo;
import com.jiubang.ggheart.data.info.ScreenSettingInfo;
import com.jiubang.ggheart.data.statistics.tables.GUIThemeTable;
import com.jiubang.ggheart.data.theme.GoLockerThemeManager;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.data.theme.bean.ThemeInfoBean;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * GUI收费数据统计模块
 * 
 * @author yangbing
 * 
 */
public class GuiThemeStatistics implements IMonitorAppInstallListener {

	/**
	 * 主题预览界面入口
	 * */
	public static final int THEME_PREVIEW_ENTRY = 1;
	/**
	 * GO精品入口
	 * */
	public static final int GO_STORE_ENTRY = 2;
	/**
	 * 桌面主题类型
	 * */
	public static final int THEME_LAUNCHER_TYPE = 1;
	/**
	 * 锁屏主题类型
	 * */
	public static final int THEME_LOCKER_TYPE = 2;

	private static final String PROTOCOL_TITLE = "15||1||";
	private static final String PROTOCOL_TITLE2 = "15||2||";
	private static final String PROTOCOL_DIVIDER = "||";
	private static GuiThemeStatistics sInstance = null;
	private StatisticsDataProvider mDataProvider;
	private MonitorAppstatisManager mAppsManager;
	private ThemeManager mThemeManager;
	private Context mContext;

	public final static byte ENTRY_TYPE_OTHER = 0; // 其它入口
	public final static byte ENTRY_GO_STORE = 1; // GO精品
	public final static byte ENTRY_GO_LOCKER = 2; // GO锁屏
	public final static byte ENTRY_MENU = 3; // 菜单（无NEW)
	public final static byte ENTRY_MESSAGE_CENTER = 4; // 消息中心
	public final static byte ENTRY_MESSAGE_PUSH = 5; // 消息推送
	public final static byte ENTRY_MENU_NEW = 6; // 菜单（有NEW)
	public final static byte ENTRY_DESK_ICON = 7; // 桌面图标
	public final static byte ENTRY_MENU_LOCKER = 8; // 菜單鎖屏項
	public final static byte ENTRY_MENU_LOCKER_NEW = 9; // 菜單鎖屏項（new）
	public final static byte ENTRY_TAB_DESK_CLICK = 10; // 桌面主题点击
	public final static byte ENTRY_TAB_LOCKER_CLICK = 11; // 锁屏主题点击
	public final static byte ENTRY_MESSAGE_CENTER_LOCKER = 12; // 消息中心
	public final static byte ENTRY_MESSAGE_PUSH_LOCKER = 6; // 消息推送
	public final static String ENTRY_SP_NAME = "gui_current_entry";
	
	//送手机活动入口
	public final static int ENTRY_NOTIFICATION = 1; //通知栏
	public final static int ENTRY_WIN_AWARD = 2; //菜单->更多->活动入口
	public final static int ENTRY_BANNER = 3; //主题预览的banner

	private final static String ENTER_SPERATE = "#"; // 数组分隔符
	private final static String CLASS_SPERATE = "&&"; // 分类分隔符

	/*===================GUI SDK统计===============================*/
	private static final int STATISTICS_FUNID_20 = 20;
	private static final int LOG_SEQUENCE_41 = 41;
	private static final int FUNID_59 = 59;
	private static final int FUNID_70 = 70;
	private static final int FUNID_68 = 68;
	private static final int FUNID_105 = 105;
	private static final int FUNID_112 = 112;
	
	//侧边栏广告FUNID=67
	private static final int SIDEAD_FUNID_67 = 67;
	//应用管理FUNID=77
	private static final int SIDEAD_FUNID_77 = 77;
	
	private static final int AD_TEST_FUNID_77 = 78;
	//国外送手机活动
	private static final int WIN_GALAXY_FUNID = 79;
	
	private static final int GUI_STATISTICS_FUNCTION = 10;
	private static final int PAY_STATISTICS_FUNCTION = 33;
	private static final int PLUGIN_STATISTICS_FUNCTION = 51;

	public static final String OPTION_CODE_LOGIN = "g001"; //登录 用户点击进入桌面主题预览界面
	public static final String OPTION_CODE_TAB_CLICK = "h000"; //tab点击 用户点击tab栏，或者滑 动至tab栏
	public static final String OPTION_CODE_PREVIEW_IMG_CLICK = "c000"; //详情点击用户点击主题省略图，查看主题的详情（包括桌面主题、本地主题的详情点击，按tab分类区分）

	public static final String OPTION_CODE_PREVIEW_BTN_CLICK = "a000"; //下载点击 用户直接点击主题省略图下方的GET按钮（下载）

	public static final String OPTION_CODE_DETAIL_BTN_CLICK = "a003"; //详情下载点击 用户进入主题详情页面后，点击详情页面的GET按钮(下载)

	public static final String OPTION_CODE_THEME_INSTALLED = "b000"; // 下载安装 用户在主题下载完成后，点击确认安装（或者自动 安装成功）

	public static final String OPTION_CODE_APPLY_THEME = "i000"; // 应用点击用 户在本地主题的tab栏中，点击某主题的应用按钮
	
	public static final String OPTION_CODE_PAY = "j000"; // 购买
	
	//国外送手机活动操作代码
	public static final String WIN_GALAXY_NOTIFICATION = "f000";
	public static final String WIN_GALAXY_ENTRY = "g001";
	
	public static final String OPTION_CODE_PLUGIN_AUTOBACKUP_POPUP = "bak_popup"; // 恢复自动备份提示框弹出
	public static final String OPTION_CODE_PLUGIN_AUTOBACKUP_CANCLE = "bak_cancel"; // 恢复自动备份提示框弹出选择取消
	public static final String OPTION_CODE_PLUGIN_AUTOBACKUP_RESTORE = "bak_restore"; // 恢复自动备份提示框弹出选择恢复备份

	public static final int GUI_STATISTICS_TYPE = 1000001;
	public static final String GUI_DOWN_MAPID = "gui_down_mapid";
	private CacheManager mCacheManager;
	public static final String CLICK_THEME_PKGNAME = "click_theme_pkgname";
	/**===========================================================**/
//		entrance
	/**====================桌面付费版功能入口============================**/
	
	
	/**=============================================================**/
	
	private GuiThemeStatistics(Context context) {
		mContext = context;
		mDataProvider = StatisticsDataProvider.getInstance(context);
		mAppsManager = MonitorAppstatisManager.getInstance(context);
		mThemeManager = ThemeManager.getInstance(context);
		mCacheManager = new CacheManager(new FileCacheImpl(LauncherEnv.Path.GOTHEMES_PATH));
	}

	public synchronized static GuiThemeStatistics getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new GuiThemeStatistics(context);
		}
		return sInstance;
	}

	/**
	 * 设置当前Gui统计入口
	 * 
	 * @param entryType
	 */
	public synchronized static void setCurrentEntry(byte entryType, Context context) {
		if (context == null) {
			return;
		}
		PreferencesManager entrySp = new PreferencesManager(context, ENTRY_SP_NAME,
				Context.MODE_PRIVATE);
		if (entrySp != null) {
			entrySp.putInt(ENTRY_SP_NAME, entryType); // 标记当前入口
			entrySp.commit();
			}
		}

	/**
	 * 读取当前入口
	 */
	public static int getCurrentEntry(Context context) {
		if (context == null) {
			return ENTRY_TYPE_OTHER;
		}
		PreferencesManager entrySp = new PreferencesManager(context, ENTRY_SP_NAME,
				Context.MODE_PRIVATE);
		if (entrySp != null) {
			int entryType = entrySp.getInt(ENTRY_SP_NAME, ENTRY_TYPE_OTHER);
			entrySp.putInt("" + entryType, 1); // 标记该渠道号为活跃入口
			entrySp.commit();
			return entryType;
		}
		return ENTRY_TYPE_OTHER;
	}

	@Override
	public void onHandleAppInstalled(String pkgName, String listenKey) {
		// System.out.println("处理安装事件回调，更新安装数");
		// System.out.println(pkgName+"::::"+listenKey);
		// 处理安装事件回调，更新安装数
		if (pkgName == null || listenKey == null) {
			return;
		}
		// 把listenKey还原为寄存的数据
		String[] keys = listenKey.split(ENTER_SPERATE);
		int position = 0;
		String pkgType = null;
		int entry = 0;
		if (keys != null && keys.length > 0) {
			position = Integer.parseInt(keys[0]);
			if (keys.length > 1) {
				pkgType = keys[1];
			}
			if (keys.length > 2) {
				entry = Integer.parseInt(keys[2]);
			}
		}
		if (isPackageInstallCountStatisticsed(pkgName, pkgType)) {
			return;
		}
		try {
			String sql = "update " + GUIThemeTable.TABLENAME + " set "
					+ GUIThemeTable.INSTALL_COUNT + " = 1 " + "where " + GUIThemeTable.PACKAGE
					+ " = '" + pkgName + "'" + " and " + GUIThemeTable.POSITION + " = " + position
					+ " and " + GUIThemeTable.ENTRY + " = " + entry;
			// System.out.println(sql);
			mDataProvider.exeSql(sql);
			// System.out.println("插入数据库");
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * 手动调用安装成功
	 * 
	 * @param pkgName
	 *            包名
	 */
	public void onAppInstalled(String pkgName) {
		try {
			if (mAppsManager != null) {
				mAppsManager.handleAppInstalled(pkgName);
			}
			// System.out.println("插入数据库");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 判断此包名对应的主题安装数是否已经统计过
	 * 
	 * @param pkgName
	 * */
	private boolean isPackageInstallCountStatisticsed(String pkgName, String pkgType) {
		// 判断该包是否已经安装过
		Cursor cursor = null;
		try {
			String selection = "";
			if (pkgType == null) {
				selection = GUIThemeTable.PACKAGE + " = '" + pkgName + "'";
			} else {
				selection = GUIThemeTable.PACKAGE + " = '" + pkgName + "'" + " and "
						+ GUIThemeTable.PKG_TYPE + " = '" + pkgType + "'";
			}
			cursor = mDataProvider
					.queryData(GUIThemeTable.TABLENAME,
							new String[] { GUIThemeTable.INSTALL_COUNT }, selection, null, null,
							null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					int installCount = cursor.getInt(0);
					if (installCount == 1) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return false;
	}

	private String getSelection(String pkgName, int position, String pkgType, int entry) {
		String selection = "";

		selection = GUIThemeTable.PACKAGE + " = '" + pkgName + "'" + " and "
				+ GUIThemeTable.POSITION + " = " + position + " and " + GUIThemeTable.PKG_TYPE
				+ " = '" + pkgType + "'" + " and " + GUIThemeTable.ENTRY + " = " + entry;

		return selection;
	}

	/**
	 * 统计用户点击
	 * 
	 * @param context
	 * @param packageName
	 *            :主题包名
	 * @param position
	 *            :位置
	 * @param entry
	 *            :入口
	 * @param themeType
	 *            :主题分类
	 * */
	public void saveUserTouch(Context context, String packageName, int position, int themeType,
			String pkgType, String classId) {
		// 根据包名和位置判断数据库里是否已经存在该条记录
		int entry = getCurrentEntry(context);
		String pkgTypeAndClass = pkgType + CLASS_SPERATE + classId;
		String selection = getSelection(packageName, position, pkgTypeAndClass, entry);
		boolean isExist = mDataProvider.isExistData(GUIThemeTable.TABLENAME, selection,
				new String[] { GUIThemeTable.THEME_TYPE });
		if (isExist) {
			String sql = "update " + GUIThemeTable.TABLENAME + " set " + GUIThemeTable.CLICK_COUNT
					+ " = " + GUIThemeTable.CLICK_COUNT + " + 1 " + "where " + selection;

			mDataProvider.exeSql(sql);
		} else {

			// 不存在，插入一条新数据
			ContentValues values = new ContentValues();
			values.put(GUIThemeTable.PACKAGE, packageName);
			values.put(GUIThemeTable.POSITION, position);
			values.put(GUIThemeTable.ENTRY, entry);
			values.put(GUIThemeTable.THEME_TYPE, themeType);
			values.put(GUIThemeTable.CLICK_COUNT, 1);
			values.put(GUIThemeTable.INSTALL_COUNT, 0);
			values.put(GUIThemeTable.PKG_TYPE, pkgTypeAndClass);
			mDataProvider.insertData(GUIThemeTable.TABLENAME, values);
		}
		// 需要统计安装数的处理
		String listenrKey = String.valueOf(position) + ENTER_SPERATE + pkgTypeAndClass
				+ ENTER_SPERATE + entry;
		mAppsManager.handleMonitorAppInstall(packageName,
				MonitorAppstatisManager.TYPE_FROM_THEMESMANAGER, listenrKey);
	}

	/**
	 * 统计详情点击
	 * 
	 * @param context
	 * @param packageName
	 *            :主题包名
	 * @param position
	 *            :位置
	 * @param entry
	 *            :入口
	 * @param themeType
	 *            :主题分类
	 * */
	public void saveUserDetailClick(Context context, String packageName, int position,
			int themeType, String pkgType, String classId) {
		// 根据包名和位置判断数据库里是否已经存在该条记录
		int entry = getCurrentEntry(context);
		String pkgTypeAndClass = pkgType + CLASS_SPERATE + classId;
		String selection = getSelection(packageName, position, pkgTypeAndClass, entry);
		boolean isExist = mDataProvider.isExistData(GUIThemeTable.TABLENAME, selection,
				new String[] { GUIThemeTable.THEME_TYPE });
		if (isExist) {
			String sql = "update " + GUIThemeTable.TABLENAME + " set " + GUIThemeTable.CLICK_COUNT
					+ " = " + GUIThemeTable.DETAIL_CLICK + " + 1 " + "where " + selection;

			mDataProvider.exeSql(sql);
		} else {

			// 不存在，插入一条新数据
			ContentValues values = new ContentValues();
			values.put(GUIThemeTable.PACKAGE, packageName);
			values.put(GUIThemeTable.POSITION, position);
			values.put(GUIThemeTable.ENTRY, entry);
			values.put(GUIThemeTable.THEME_TYPE, themeType);
			values.put(GUIThemeTable.CLICK_COUNT, 0);
			values.put(GUIThemeTable.INSTALL_COUNT, 0);
			values.put(GUIThemeTable.PKG_TYPE, pkgTypeAndClass);
			values.put(GUIThemeTable.DETAIL_CLICK, 1);
			values.put(GUIThemeTable.DETAIL_GET_CLICK, 0);
			mDataProvider.insertData(GUIThemeTable.TABLENAME, values);
		}
	}

	/**
	 * 统计用户详情获取点击
	 * 
	 * @param context
	 * @param packageName
	 *            :主题包名
	 * @param position
	 *            :位置
	 * @param entry
	 *            :入口
	 * @param themeType
	 *            :主题分类
	 * */
	public void saveUserDetailGet(Context context, String packageName, int position, int themeType,
			String pkgType, String classId) {
		// 根据包名和位置判断数据库里是否已经存在该条记录
		int entry = getCurrentEntry(context);
		String pkgTypeAndClass = pkgType + CLASS_SPERATE + classId;
		String selection = getSelection(packageName, position, pkgTypeAndClass, entry);
		boolean isExist = mDataProvider.isExistData(GUIThemeTable.TABLENAME, selection,
				new String[] { GUIThemeTable.THEME_TYPE });
		if (isExist) {
			String sql = "update " + GUIThemeTable.TABLENAME + " set " + GUIThemeTable.CLICK_COUNT
					+ " = " + GUIThemeTable.DETAIL_GET_CLICK + " + 1 " + "where " + selection;

			mDataProvider.exeSql(sql);
		} else {

			// 不存在，插入一条新数据
			ContentValues values = new ContentValues();
			values.put(GUIThemeTable.PACKAGE, packageName);
			values.put(GUIThemeTable.POSITION, position);
			values.put(GUIThemeTable.ENTRY, entry);
			values.put(GUIThemeTable.THEME_TYPE, themeType);
			values.put(GUIThemeTable.CLICK_COUNT, 0);
			values.put(GUIThemeTable.INSTALL_COUNT, 0);
			values.put(GUIThemeTable.PKG_TYPE, pkgTypeAndClass);
			values.put(GUIThemeTable.DETAIL_CLICK, 0);
			values.put(GUIThemeTable.DETAIL_GET_CLICK, 1);
			mDataProvider.insertData(GUIThemeTable.TABLENAME, values);
		}
		// 需要统计安装数的处理
		String listenrKey = String.valueOf(position) + ENTER_SPERATE + pkgTypeAndClass
				+ ENTER_SPERATE + entry;
		mAppsManager.handleMonitorAppInstall(packageName,
				MonitorAppstatisManager.TYPE_FROM_THEMESMANAGER, listenrKey);
	}

	/**
	 * 查询所有记录
	 * 
	 * @return 按GUI收费数据统计协议拼装成的字符串 协议格式为：
	 *         15||1||主题类型||主题包名||入口类型||入口位置||点击数||安装数||是否正在使用
	 * */
	public String queryAllData() {
		StringBuffer allBuf = new StringBuffer();
		StringBuffer singleBuf = new StringBuffer();
		Cursor cursor = null;
		try {
			cursor = mDataProvider.queryData(GUIThemeTable.TABLENAME, null, null, null, null, null,
					null);
			if (cursor != null && cursor.getCount() > 0) {
				String curThemeName = mThemeManager.getCurThemePackage();
				int typeIndex = cursor.getColumnIndex(GUIThemeTable.THEME_TYPE);
				int packageNameIndex = cursor.getColumnIndex(GUIThemeTable.PACKAGE);
				int entryIndex = cursor.getColumnIndex(GUIThemeTable.ENTRY);
				int positionIndex = cursor.getColumnIndex(GUIThemeTable.POSITION);
				int clickCountIndex = cursor.getColumnIndex(GUIThemeTable.CLICK_COUNT);
				int installCountIndex = cursor.getColumnIndex(GUIThemeTable.INSTALL_COUNT);
				int pkgTypeIndex = cursor.getColumnIndex(GUIThemeTable.PKG_TYPE);
				int detailClickIndex = cursor.getColumnIndex(GUIThemeTable.DETAIL_CLICK);
				int detailGetIndex = cursor.getColumnIndex(GUIThemeTable.DETAIL_GET_CLICK);
				while (cursor.moveToNext()) {
					String packageName = cursor.getString(packageNameIndex);
					singleBuf.delete(0, singleBuf.length());
					singleBuf.append(PROTOCOL_TITLE);
					singleBuf.append(cursor.getInt(typeIndex) + PROTOCOL_DIVIDER);
					singleBuf.append(packageName + PROTOCOL_DIVIDER);
					singleBuf.append(cursor.getInt(entryIndex) + PROTOCOL_DIVIDER);
					singleBuf.append(cursor.getInt(positionIndex) + PROTOCOL_DIVIDER);
					singleBuf.append(cursor.getInt(clickCountIndex) + PROTOCOL_DIVIDER);
					singleBuf.append(cursor.getInt(installCountIndex) + PROTOCOL_DIVIDER);
					if (curThemeName.equals(packageName)) {
						singleBuf.append(1 + PROTOCOL_DIVIDER);
					} else {
						singleBuf.append(0 + PROTOCOL_DIVIDER);
					}
					String pkyTypeAndClass = cursor.getString(pkgTypeIndex);
					String pkgType = "0";
					String classId = "0";
					if (pkyTypeAndClass != null && !pkyTypeAndClass.equals("")) {
						String[] datas = pkyTypeAndClass.split(CLASS_SPERATE);
						if (datas.length > 0) {
							pkgType = datas[0];
						}
						if (datas.length > 1) {
							classId = datas[1];
						}
					}
					singleBuf.append(pkgType + PROTOCOL_DIVIDER);
					singleBuf.append(classId + PROTOCOL_DIVIDER);
					singleBuf.append(cursor.getInt(detailClickIndex) + PROTOCOL_DIVIDER);
					singleBuf.append(cursor.getInt(detailGetIndex));
					allBuf.append(singleBuf);
					allBuf.append("\r\n");
				}
			}
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		return allBuf.toString();

	}

	/**
	 * 包名统计
	 * 
	 * @return 按GUI收费数据统计协议拼装成的字符串 协议格式为： 15||2||主题类型||主题包名||正在使用包名
	 * */
	public String queryPackageData() {
		StringBuffer allBuf = new StringBuffer();
		StringBuffer singleBuf = new StringBuffer();
		try {
			// 桌面主题
			ArrayList<ThemeInfoBean> themeInfoBeans = mThemeManager.getAllInstalledThemeInfos();
			if (themeInfoBeans != null && themeInfoBeans.size() > 0) {
				String currentLauncherTheme = mThemeManager.getCurThemePackage();
				for (ThemeInfoBean infoBean : themeInfoBeans) {
					if (singleBuf.length() > 0) {
						singleBuf.delete(0, singleBuf.length());
					}
					singleBuf.append(PROTOCOL_TITLE2);
					singleBuf.append(THEME_LAUNCHER_TYPE + PROTOCOL_DIVIDER);
					singleBuf.append(infoBean.getPackageName() + PROTOCOL_DIVIDER);
					singleBuf.append(currentLauncherTheme);
					allBuf.append(singleBuf);
					allBuf.append("\r\n");
				}
			}
			// 锁屏主题
			String currentLockerTheme = mThemeManager.getCurLockerTheme();
			// 默认
			if (singleBuf.length() > 0) {
				singleBuf.delete(0, singleBuf.length());
			}
			singleBuf.append(PROTOCOL_TITLE2);
			singleBuf.append(THEME_LOCKER_TYPE + PROTOCOL_DIVIDER);
			singleBuf.append(PackageName.GO_LOCK_PACKAGE_NAME + PROTOCOL_DIVIDER);
			singleBuf.append(currentLockerTheme);
			allBuf.append(singleBuf);
			allBuf.append("\r\n");
			// 随机主题
			if (singleBuf.length() > 0) {
				singleBuf.delete(0, singleBuf.length());
			}
			singleBuf.append(PROTOCOL_TITLE2);
			singleBuf.append(THEME_LOCKER_TYPE + PROTOCOL_DIVIDER);
			singleBuf.append("com.jiubang.goscreenlock.theme.random" + PROTOCOL_DIVIDER);
			singleBuf.append(currentLockerTheme);
			allBuf.append(singleBuf);
			allBuf.append("\r\n");
			Map<CharSequence, CharSequence> mInstalledLockerThemeMap = new GoLockerThemeManager(
					mContext).queryInstalledTheme();
			Iterator<CharSequence> iterator = mInstalledLockerThemeMap.keySet().iterator();
			while (iterator.hasNext()) {
				if (singleBuf.length() > 0) {
					singleBuf.delete(0, singleBuf.length());
				}
				CharSequence packageName = iterator.next();
				singleBuf.append(PROTOCOL_TITLE2);
				singleBuf.append(THEME_LOCKER_TYPE + PROTOCOL_DIVIDER);
				singleBuf.append(packageName + PROTOCOL_DIVIDER);
				singleBuf.append(currentLockerTheme);
				allBuf.append(singleBuf);
				allBuf.append("\r\n");
			}
		} catch (Exception e) {

		}
		return allBuf.toString();
	}

	/**
	 * 包名统计(For Zip)
	 * 
	 * @author zhouxuewen
	 * @return 按GUI ZIP包收费数据统计协议拼装成的字符串 协议格式为： 15||2||主题类型||主题包名||正在使用包名
	 * */
	public String queryPackageDataForZip() {
		StringBuffer allBuf = new StringBuffer();
		StringBuffer singleBuf = new StringBuffer();
		try {
			// 桌面主题
			ArrayList<ThemeInfoBean> themeInfoBeans = new ArrayList<ThemeInfoBean>();

			ConcurrentHashMap<String, ThemeInfoBean> map = mThemeManager.scanAllZipThemes();
			Set<String> keys = map.keySet();
			for (String key : keys) {
				themeInfoBeans.add(map.get(key));
			}

			if (themeInfoBeans != null && themeInfoBeans.size() > 0) {
				String currentLauncherTheme = mThemeManager.getCurThemePackage();
				for (ThemeInfoBean infoBean : themeInfoBeans) {
					if (singleBuf.length() > 0) {
						singleBuf.delete(0, singleBuf.length());
					}
					singleBuf.append(PROTOCOL_TITLE2);
					singleBuf.append(THEME_LAUNCHER_TYPE + PROTOCOL_DIVIDER);
					singleBuf.append(infoBean.getPackageName() + PROTOCOL_DIVIDER);
					singleBuf.append(currentLauncherTheme);
					allBuf.append(singleBuf);
					allBuf.append("\r\n");
				}
			}
		} catch (Exception e) {

		}
		return allBuf.toString();
	}

	/**
	 * 上传数据后，清空数据
	 * */
	public void clearData() {
		// String sql ="DROP TABLE "+GUIThemeTable.TABLENAME;
		// mDataProvider.exeSql(sql);
		mDataProvider.delete(GUIThemeTable.TABLENAME, null, null);
	}

	public static void guiStaticData(final String logSequence, int funid, final String sendId,
			final String optionCode, final int optionResult, final String entrance,
			final String tabID, final String position, final String associatedObj, final String option) {
		// TODO Auto-generated method stub
		Context context = ApplicationProxy.getContext();

		StringBuffer statisticsDataBuffer = new StringBuffer();

		statisticsDataBuffer.append(logSequence);
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(Machine.getAndroidId(context));
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(UtilTool.getBeiJinTime());
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(funid);
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(sendId);
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(optionCode);
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(optionResult);
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(Machine.getSimCountryIso(context));
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(GoStorePhoneStateUtil.getUid(context));
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		int versionCode = 0;
		String versionName = "";
		PackageManager pm = ApplicationProxy.getContext().getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(ApplicationProxy.getContext().getPackageName(), 0);
			versionCode = info.versionCode;
			versionName = info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		statisticsDataBuffer.append(versionCode);
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
		statisticsDataBuffer.append(versionName);
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(entrance);
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(tabID);
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(position);
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(GoStorePhoneStateUtil.getVirtualIMEI(context));
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
		statisticsDataBuffer.append(StatisticsManager.getGOID(context));

		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
		statisticsDataBuffer.append(associatedObj);
		
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
		statisticsDataBuffer.append(option);
		StatisticsManager manager = StatisticsManager.getInstance(ApplicationProxy.getContext());
		manager.upLoadStaticData(statisticsDataBuffer.toString());
		//								writeToSDCard(statisticsDataBuffer.toString(), null);
	}
	/**
	 * 
	 * @param mapId
	 * @param optionCode
	 * @param optionResult
	 * @author zhangxi
	 * @date 2013-09-23
	 */
	public static void sideAdStaticData(final String mapId, final String optionCode,
			final int optionResult, final String adId) {
		guiStaticData(LOG_SEQUENCE_41 + "", SIDEAD_FUNID_67, mapId, optionCode, optionResult, "0",
				"0", "0", "", adId);
	}
	/**
	 * <br>功能简述:功能表侧边栏操作统计
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param mapId
	 * @param optionCode
	 * @param optionResult
	 * @param adId
	 */
	public static void sideOpStaticData(final String mapId, final String optionCode,
			final int optionResult, final String adId) {
		guiStaticData(STATISTICS_FUNID_20 + "", PLUGIN_STATISTICS_FUNCTION, mapId, optionCode, optionResult, "0",
				"0", "0", "", adId);
	}
	/**
	 * 
	 * @param mapId
	 * @param optionCode
	 * @param optionResult
	 */
	public static void appManagerAdStaticData(final String mapId, final String optionCode,
			final int optionResult, final String adId) {
		guiStaticData(LOG_SEQUENCE_41 + "", SIDEAD_FUNID_77, mapId, optionCode, optionResult, "0",
				"0", "0", "", adId);
	}
	
	
	/**
	 * 
	 * @param logSequence
	 * @param funid
	 * @param sendId
	 * @param optionCode
	 * @param optionResult
	 * @param entrance
	 * @param tabID
	 * @param position
	 * @param associatedObj
	 * @author zhangxi
	 * @date 2013-09-23
	 * @describe 侧边栏广告统计协议
	 * @protocol 日志序列||Android ID||日志打印时间||功能点ID||统计对象||操作代码||操作结果||国家||渠道||版本号||版本名||入口||tab分类||位置||imei||goid||关联对象||备注
	 */
//	public static void guiSideADStatic(final String logSequence, int funId, final String mapId,
//			final String optionCode, final int optionResult, final String entrance,
//			final String tabID, final String position, final String associatedObj, final String adId) {
//		// TODO Auto-generated method stub
//		String tmpEntrance = entrance;
//		Context context = ApplicationProxy.getContext();
//
//		StringBuffer statisticsDataBuffer = new StringBuffer();
//
//		statisticsDataBuffer.append(logSequence);
//		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
//
//		statisticsDataBuffer.append(Machine.getAndroidId(context));
//		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
//
//		statisticsDataBuffer.append(UtilTool.getBeiJinTime());
//		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
//
//		statisticsDataBuffer.append(funId);
//		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
//
//		statisticsDataBuffer.append(mapId);
//		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
//
//		statisticsDataBuffer.append(optionCode);
//		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
//
//		statisticsDataBuffer.append(optionResult);
//		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
//
//		statisticsDataBuffer.append(Machine.getSimCountryIso(context));
//		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
//
//		statisticsDataBuffer.append(GoStorePhoneStateUtil.getUid(context));
//		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
//
//		int versionCode = 0;
//		String versionName = "";
//		PackageManager pm = ApplicationProxy.getContext().getPackageManager();
//		try {
//			PackageInfo info = pm.getPackageInfo(ApplicationProxy.getContext().getPackageName(), 0);
//			versionCode = info.versionCode;
//			versionName = info.versionName;
//		} catch (NameNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		statisticsDataBuffer.append(versionCode);
//		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
//		statisticsDataBuffer.append(versionName);
//		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
//
//		statisticsDataBuffer.append(tmpEntrance);
//		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
//
//		statisticsDataBuffer.append(tabID);
//		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
//
//		statisticsDataBuffer.append(position);
//		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
//
//		statisticsDataBuffer.append(GoStorePhoneStateUtil.getVirtualIMEI(context));
//		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
//		statisticsDataBuffer.append(StatisticsManager.getGOID(context));
//
//		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
//		statisticsDataBuffer.append(associatedObj);
//		
//		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
//		statisticsDataBuffer.append(adId);
//		
//		SideAdvertUtils.log("统计数据为:" + statisticsDataBuffer.toString());
//		StatisticsManager manager = StatisticsManager.getInstance(ApplicationProxy.getContext());
//		manager.upLoadStaticData(statisticsDataBuffer.toString());
//		
//	}
	

	
	/**
	 * <br>功能简述:GO桌面主题统 计协 议
	 * <br>功能详细描述:日志序列||Android ID||日志打印时间||功能点ID||统计对象||操作代码||操作结果||国家||渠道||版本号||版本名||入口||tab分类||位置||当前主题（更换主题时）
	 * <br>注意:
	 * @param sendId
	 * @param optionCode
	 * @param optionResult
	 * @param entrance
	 * @param tabID
	 * @param position
	 */
	public static void guiStaticData(final String sendId, final String optionCode, final int optionResult,
			final String entrance, final String tabID, final String position, final String curTheme) {
		String tmpEntrance = entrance;
		Context context = ApplicationProxy.getContext();;
			PreferencesManager preferencesmanager = new PreferencesManager(context, ENTRY_SP_NAME,
					Context.MODE_PRIVATE);
			if (optionCode != null && optionCode.equals(OPTION_CODE_LOGIN)) {
				preferencesmanager.putInt(ENTRY_SP_NAME, Integer.valueOf(entrance));
				preferencesmanager.commit();
			} else if (optionCode != null && optionCode.equals(OPTION_CODE_THEME_INSTALLED)) {
				tmpEntrance = "";
			} else {
				tmpEntrance = String.valueOf(preferencesmanager.getInt(ENTRY_SP_NAME, 0));
			}

		guiStaticData(GUI_STATISTICS_FUNCTION, sendId, optionCode, optionResult, tmpEntrance, tabID,
				position, curTheme);

	}
	
	public static void guiStaticData(int funid, final String sendId, final String optionCode,
			final int optionResult, final String entrance, final String tabID,
			final String position, final String curTheme) {
		guiStaticData(STATISTICS_FUNID_20 + "", funid, sendId, optionCode, optionResult, entrance,
				tabID, position, curTheme, "-1");
	}
	
	/**
     * <br>功能简述:专为推荐遨游以及360的统计，因为需要上传广告ID
     * <br>功能详细描述:
     * <br>注意:
     * @param funid
     * @param sendId
     * @param optionCode
     * @param optionResult
     * @param entrance
     * @param tabID
     * @param position
     * @param curTheme
     * @param adverId
     */
    public static void guiStaticDataFor360Mathon(int funid, final String sendId, final String optionCode,
            final int optionResult, final String entrance, final String tabID,
            final String position, final String curTheme, final String adverId) {
        guiStaticData(STATISTICS_FUNID_20 + "", funid, sendId, optionCode, optionResult, entrance,
                tabID, position, curTheme, adverId);
    }
    
    /**
     * 带有广告ID的统计。
     */
    public static void guiStaticDataWithAId(int funid, final String sendId, final String optionCode,
            final int optionResult, final String entrance, final String tabID,
            final String position, final String curTheme, final String adverId) {
        guiStaticData(STATISTICS_FUNID_20 + "", funid, sendId, optionCode, optionResult, entrance,
                tabID, position, curTheme, adverId);
    }
    
    /**
     * <br>功能简述:以mapId为键，以aid为值，记录到sharePreferences
     * <br>功能详细描述:
     * <br>注意:
     * @param context
     * @param mapId
     * @param aid
     */
    public static void recordAdvertId(Context context, String mapId, String aId) {
    	PreferencesManager ps = new PreferencesManager(context, IPreferencesIds.PREFERENCE_RECORD_ADVERT_ID, Context.MODE_PRIVATE);
    	ps.putString(mapId, aId);
    	ps.commit();
    }
    
    /**
     * <br>功能简述:通过mapId获取广告aid
     * <br>功能详细描述:
     * <br>注意:
     * @param context
     * @param mapId
     * @return
     */
    public static String getAdvertId(Context context, String mapId) {
    	PreferencesManager ps = new PreferencesManager(context, IPreferencesIds.PREFERENCE_RECORD_ADVERT_ID, Context.MODE_PRIVATE);
    	return ps.getString(mapId, "0");
    }
	
	/**
	 * for folderAd 
	 * @param sendId
	 * @param optionCode
	 * @param optionResult
	 * @param tabID
	 * @param position
	 * @param adID
	 */
	public static void folderAdStaticData(final String sendId, final String optionCode,
			final int optionResult) {
		guiStaticData(LOG_SEQUENCE_41 + "", FUNID_59, sendId, optionCode, optionResult, "0", "0",
				"0", "0", "-1");
	}
	
	public static void arrangeAppStaticData(final String tabID, final String optionCode,
			final String entrance) {
		guiStaticData(LOG_SEQUENCE_41 + "", FUNID_70, "", optionCode, 1, entrance, tabID, "0", "0",
				System.currentTimeMillis() + "");
	}
	
	public static void functionPurchaseStaticData(final String sendId, final String optionCode,
			final int optionResult, final String entrance, String tabId, int opt) {
		guiStaticData(LOG_SEQUENCE_41 + "", FUNID_68, sendId, optionCode, optionResult, entrance,
				tabId, "-1", "-1", opt + "");
	}
	/**
	 * <br>功能简述:桌面功能收费统计
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param optionCode
	 * @param optionResult
	 * @param entrance
	 */
	public static void payStaticData(final String optionCode, final int optionResult,
			final String entrance) {
		guiStaticData(PAY_STATISTICS_FUNCTION, "-1", optionCode, optionResult, entrance, "-1",
				"-1", "-1");

	}
	
	public static void pluginStaticData(final String optionCode) {
		guiStaticData(PLUGIN_STATISTICS_FUNCTION, PackageName.PACKAGE_NAME, optionCode, 1, "-1",
				"-1", "-1", "-1");
	}
	
	public static void adTestStaticData(int adtype) {
		guiStaticData(LOG_SEQUENCE_41 + "", AD_TEST_FUNID_77, "-1", "g001", 1, "-1",
				"-1", "-1", "-1", adtype + "");
	}
	
	public static void ratingDialogStaticData(String optionCode, String entrance, String rateTimes) {
		guiStaticData(LOG_SEQUENCE_41 + "", FUNID_105, "", optionCode, 1, entrance, rateTimes, "",
				"", "");
	}
	/**
	 *  此用户行为统计接口每八小时调用一次，上传这八小时内的数据。
	 * @param optionCode
	 * @param optionResult
	 */
	public static void updateLoadGoLauncherUserBehaviorStaticData() {
		PreferencesManager preferencesManager = new PreferencesManager(
				ApplicationProxy.getContext(),
				IPreferencesIds.GO_LAUNCHER_USER_BEHAVIOR_STATIC_DATA_CACHE, Context.MODE_PRIVATE);
		for (String id : IGoLauncherUserBehaviorStatic.USER_BEHAVIOR_STATIC_DATA) {
			int times = preferencesManager.getInt(id, 0);
			if (times > 0) {
				updateLoadGoLauncherUserBehaviorStaticData(id, times);
			}
		}
		clearGoLauncherUserBehaviorStaticDataCache();
		
		updateLoadGoSettingStaticData();
	}

	private static void updateLoadGoSettingStaticData() {
		DesktopSettingInfo settingInfo = SettingProxy.getDesktopSettingInfo();
		ScreenSettingInfo screenSettingInfo = SettingProxy.getScreenSettingInfo();
		int showAppName = 1;
		int showAppBg = 0;
		switch (settingInfo.mTitleStyle) {
			case 0 :
				showAppName = 1;
				showAppBg = 1;
				break;
			case 1 :
				showAppName = 1;
				showAppBg = 0;
				break;
			case 2 :
				showAppName = 0;
				showAppBg = 0;
				break;
			default :
				break;
		}
		goLauncherUserBehaviorSettingStaticData(
				IGoLauncherUserBehaviorStatic.PREFERENCES_FONT_SSIL, showAppName);
		goLauncherUserBehaviorSettingStaticData(
				IGoLauncherUserBehaviorStatic.PREFERENCES_FONT_SSLB, showAppBg);
		
		int isDefaultLauncher = isGoLauncherDefault() ? 1 : 0;
		goLauncherUserBehaviorSettingStaticData(
				IGoLauncherUserBehaviorStatic.PREFERENCES_CS_SADL, isDefaultLauncher);
		int indicatiorPos = screenSettingInfo.mIndicatorPosition
				.equals(ScreenIndicator.INDICRATOR_ON_TOP) ? 1 : 2;
		goLauncherUserBehaviorSettingStaticData(
				IGoLauncherUserBehaviorStatic.PREFERENCES_SCREENS_IP, indicatiorPos);
		int navBarExist = com.go.util.graphics.DrawUtils.isNavBarAvailable() ? 1 : 2;
		goLauncherUserBehaviorSettingStaticData(IGoLauncherUserBehaviorStatic.KEY_TYPE, navBarExist);
		final PreferencesManager pm = new PreferencesManager(ApplicationProxy.getContext(),
				IPreferencesIds.BACKUP_SHAREDPREFERENCES_SYSTEM_SETTING_FILE, Context.MODE_PRIVATE);
		boolean show = pm.getBoolean(IPreferencesIds.SYSTEM_SETTING_CENTER_SHOW_DIALOG, true);
		if (!show) {
			String mode = pm.getString(IPreferencesIds.SYSTEM_SETTING_CENTER_SHOW_DIALOG_MODE, "1");
			int value = mode.equals("1") ? 1 : 2;
			goLauncherUserBehaviorSettingStaticData(
					IGoLauncherUserBehaviorStatic.QUICK_SETTINGS_02, value);
		}
	}
	
	/**
	 * <br>
	 * 功能简述:判断是否默认使用GO桌面 <br>
	 * 功能详细描述:点击HOME键弹出的默认使用此应用 <br>
	 * 注意:类似这种方法能放到工具类里面吗？
	 * 
	 * @return
	 */
	private static boolean isGoLauncherDefault() {
		final String myPackageName = ApplicationProxy.getContext().getPackageName();
		final String defaultPkg = AppUtils.getDefaultLauncher(ApplicationProxy.getContext());
		if (myPackageName.equals(defaultPkg)) {
			return true;
		}
		return false;
	}
	
	private static void updateLoadGoLauncherUserBehaviorStaticData(String optionCode, int optionResult) {
		guiStaticData(LOG_SEQUENCE_41 + "", FUNID_112, "", optionCode, optionResult, "", "", "",
				"", "");
	}
	private static void goLauncherUserBehaviorSettingStaticData(String settingID, int settingValue) {
		StringBuffer statisticsDataBuffer = new StringBuffer();

		statisticsDataBuffer.append("24");
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(UtilTool.getBeiJinTime());
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(Machine.getAndroidId(ApplicationProxy.getContext()));
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(StatisticsManager.getGOID(ApplicationProxy.getContext()));
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(Machine.getSimCountryIso(ApplicationProxy.getContext()));
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(GoStorePhoneStateUtil.getUid(ApplicationProxy.getContext()));
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		int versionCode = 0;
		String versionName = "";
		PackageManager pm = ApplicationProxy.getContext().getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(ApplicationProxy.getContext().getPackageName(), 0);
			versionCode = info.versionCode;
			versionName = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		statisticsDataBuffer.append(versionCode);
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
		statisticsDataBuffer.append(versionName);
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
		statisticsDataBuffer.append("111");
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
		statisticsDataBuffer.append(settingID);
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
		statisticsDataBuffer.append(settingValue);
		StatisticsManager manager = StatisticsManager.getInstance(ApplicationProxy.getContext());
		manager.upLoadStaticData(statisticsDataBuffer.toString());
	}
	public static void goLauncherUserBehaviorStaticDataCache(String optionCode) {
		PreferencesManager preferencesManager = new PreferencesManager(
				ApplicationProxy.getContext(),
				IPreferencesIds.GO_LAUNCHER_USER_BEHAVIOR_STATIC_DATA_CACHE, Context.MODE_PRIVATE);
		int times = preferencesManager.getInt(optionCode, 0);
		times++;
		preferencesManager.putInt(optionCode, times);
		preferencesManager.commit();
	}
	
	private static void clearGoLauncherUserBehaviorStaticDataCache() {
		PreferencesManager preferencesManager = new PreferencesManager(
				ApplicationProxy.getContext(),
				IPreferencesIds.GO_LAUNCHER_USER_BEHAVIOR_STATIC_DATA_CACHE, Context.MODE_PRIVATE);
		preferencesManager.clear();
	}
	
	/**
	 * 上传应用列表数据信息
	 * @param appListData
	 */
	public static void postAppListData(Context context , final String appListData) {
		StringBuffer statisticsDataBuffer = new StringBuffer();
		String logSequence = "58";
		statisticsDataBuffer.append(logSequence);
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(UtilTool.getBeiJinTime());
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
		
		statisticsDataBuffer.append(GoStorePhoneStateUtil.getVirtualIMEI(context));
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(Machine.getAndroidId(context));
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(StatisticsManager.getGOID(context));
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
		
		statisticsDataBuffer.append(Machine.getSimCountryIso(context));
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		statisticsDataBuffer.append(GoStorePhoneStateUtil.getUid(context));
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);

		String versionName = "";
		int versionCode = 0;
		PackageManager pm = ApplicationProxy.getContext().getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(ApplicationProxy.getContext().getPackageName(), 0);
			versionName = info.versionName;
			versionCode = info.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		statisticsDataBuffer.append(versionName);
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
		
		statisticsDataBuffer.append(versionCode);
		statisticsDataBuffer.append(PROTOCOL_DIVIDER);
		
		statisticsDataBuffer.append(appListData);

		StatisticsManager manager = StatisticsManager.getInstance(ApplicationProxy.getContext());
		manager.upLoadStaticData(statisticsDataBuffer.toString());
		
	}
	
	
	/**
	 * 
	 * 后台自动上传log信息到服务器
	 * @param tag 分类信息，可用来做信息分类
	 * @param logInfo 具体log信息，全部的自定义与堆栈信息用该字段输出
	 */
	public static void autoPostLogInfo(final String tag, final String logInfo) {
		guiStaticData(STATISTICS_FUNID_20 + "", 107, "-1",
				tag, 1, "-1", "-1", "-1", "-1", logInfo);
	}
	
	public void savePaidThemePkg(String pkg, int id) {
		try {
			JSONObject obj = getPaidThemePkg();
			ArrayList<GuiClickThemeBean> list = parsePaidInfo(obj);
			if (list != null) {
				for (GuiClickThemeBean bean : list) {
					if (bean.mPkg != null && bean.mPkg.equals(pkg)) {
						return;
					}
				}
			}
			JSONArray jsonArray = null;

			if (obj != null) {
				jsonArray = obj.getJSONArray("pkgs");
			} else {
				obj = new JSONObject();
			}

			if (jsonArray == null) {
				jsonArray = new JSONArray();
				obj.put("pkgs", jsonArray);
			}

			JSONObject json = new JSONObject();
			json.put("pkg", pkg);
			json.put("id", id);
			jsonArray.put(json);
			mCacheManager.saveCache(CLICK_THEME_PKGNAME, obj.toString().getBytes());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void deleteClickThemePkg(String pkg) {
		try {
			JSONObject obj = getPaidThemePkg();
			ArrayList<GuiClickThemeBean> list = parsePaidInfo(obj);
			if (list != null) {
				JSONArray jsonArray = null;

				if (obj != null) {
					jsonArray = obj.getJSONArray("pkgs");
				}

				if (jsonArray == null) {
					return;
				}

				for (int i = 0; i < list.size(); i++) {
					GuiClickThemeBean bean = list.get(i);
					if (bean.mPkg != null && bean.mPkg.equals(pkg)) {
						list.remove(bean);
						break;
					}
				}

				jsonArray = new JSONArray();

				for (GuiClickThemeBean bean : list) {
					JSONObject json = new JSONObject();
					json.put("pkg", bean.mPkg);
					json.put("id", bean.mId);
					jsonArray.put(json);
				}
				obj = new JSONObject();
				obj.put("pkgs", jsonArray);
				mCacheManager.saveCache(CLICK_THEME_PKGNAME, obj.toString().getBytes());

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private JSONObject getPaidThemePkg() {
		byte[] cacheData = mCacheManager.loadCache(CLICK_THEME_PKGNAME);
		if (cacheData == null) {
			return null;
		}
		JSONObject obj = CacheUtil.byteArrayToJson(cacheData);
		return obj;
	}
	public ArrayList<GuiClickThemeBean> getPaidPkgs() {
		return parsePaidInfo(getPaidThemePkg());
	}

	private ArrayList<GuiClickThemeBean> parsePaidInfo(JSONObject paidJson) {
		if (paidJson != null) {
			try {
				JSONArray jsonArray = paidJson.getJSONArray("pkgs");
				if (jsonArray != null && jsonArray.length() > 0) {
					int length = jsonArray.length();
					ArrayList<GuiClickThemeBean> paidInfoList = new ArrayList<GuiClickThemeBean>(
							length);
					for (int i = 0; i < length; i++) {
						JSONObject json = jsonArray.getJSONObject(i);
						GuiClickThemeBean bean = new GuiClickThemeBean();
						bean.mPkg = json.optString("pkg", "");
						bean.mId = json.optInt("id", 0);
						if (!bean.mPkg.equals("")) {
							paidInfoList.add(bean);
						}
					}
					return paidInfoList;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public void checkInstallPkg(String pkg) {
		if (pkg == null) {
			return;
		}
		ArrayList<GuiClickThemeBean> installList = getPaidPkgs();
		if (installList != null) {
			for (int i = 0; i < installList.size(); i++) {
				GuiClickThemeBean bean = installList.get(i);
				if (bean.mPkg != null && bean.mPkg.equals(pkg)) {
					guiStaticData(String.valueOf(bean.mId), OPTION_CODE_THEME_INSTALLED, 1, "", "",
							"", "");
					deleteClickThemePkg(pkg);
					return;
				}
			}
		}

	}

	/**
	 * 
	 * <br>类描述:点击统计数据bean
	 * <br>功能详细描述:
	 * 
	 * @author  rongjinsong
	 * @date  [2013-3-11]
	 */
	private class GuiClickThemeBean {
		String mPkg;
		int mId;
	}

	/**
	 * 把字符串写到SD卡的方法
	 * 测试时用的方法
	 * @param data
	 */
	public static void writeToSDCard(String data, String filePath) {
		if (data != null) {
			if (filePath == null) {
				filePath = LauncherEnv.Path.SDCARD + LauncherEnv.Path.LAUNCHER_DIR
						+ "/statistics/statisticsSDK" + System.currentTimeMillis() + ".txt";
			}
			try {
				boolean sdCardExist = Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
				if (sdCardExist) {
					FileUtil.saveByteToSDFile(data.getBytes(), filePath);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void winGalaxyStaticData(final String sendId, final String optionCode,
			final int optionResult, final String entrance, final String tabID,
			final String position, final String associatedObj, final String option) {
		// TODO Auto-generated method stub
		GuiThemeStatistics.guiStaticData(LOG_SEQUENCE_41 + "", WIN_GALAXY_FUNID, sendId,
				optionCode, optionResult, entrance, tabID, position, associatedObj, option);
	}

}
