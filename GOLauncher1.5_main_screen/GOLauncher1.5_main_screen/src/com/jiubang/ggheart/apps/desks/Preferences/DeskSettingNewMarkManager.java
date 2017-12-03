package com.jiubang.ggheart.apps.desks.Preferences;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

import com.go.util.graphics.effector.united.EffectorControler;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;

/**
 * 桌面设置New标识管理类
 * @author yangguanxiang
 *
 */
public class DeskSettingNewMarkManager {
	private static final String NEW_MARK_XML = "desksetting_new.xml";
	private static final String TAG_NEW = "new";
	private static final String ATTR_KEY = "key";
	private static DeskSettingNewMarkManager sInstance;
	private Context mContext;
	private HashMap<String, NewMarkNode> mNodeMap;

	/**
	 * 
	 * @author yangguanxiang
	 *
	 */
	public static interface NewMarkKeys {
		public final static String SECURITY_LOCK = "security_lock";
		public final static String SIDE_DOCK = "side_dock";
		public final static String ICON_FILTER = "icon_filter";
		public final static String WALLPAPER_FILTER = "wallpaper_filter";
		public final static String NO_ADS = "no_ads";
		public final static String COMMON = "common";
		public final static String ICON = "icon";
		public final static String BACKGROUND = "background";
		public final static String DOCK = "dock";
		public final static String GESTURE_TRANSITION = "gesture_transition";
		public final static String SCREEN_TRANSITION = "screen_transition";
		public final static String APPDRAWER_HOR_TRANSITION = "appdrawer_hor_transition";
		public final static String SCREEN_GESTURE = "screen_gesture";
		public final static String APPDRAWER_GESTURE_TRANSITION = "appdrawer_gesture_transition";
	}

	public static DeskSettingNewMarkManager getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new DeskSettingNewMarkManager(context);
		}
		return sInstance;
	}

	public static void destroy() {
		if (sInstance != null) {
			sInstance.mContext = null;
			if (sInstance.mNodeMap != null) {
				sInstance.mNodeMap.clear();
			}
			sInstance = null;
		}
	}

	private DeskSettingNewMarkManager(Context context) {
		mContext = context;
		init();
	}

	private void init() {
		NewMarkXmlParser parser = new NewMarkXmlParser();
		parser.parseXml();
		initConditions();
	}

	private void initConditions() {
		NewMarkNode node = mNodeMap.get(NewMarkKeys.SECURITY_LOCK);
		node.setCondition(new ICondition() {

			@Override
			public boolean isShowNew(boolean setIsOpen) {
				return DeskSettingUtils.isFirstShowLockTip(mContext, setIsOpen);
			}
		});

		node = mNodeMap.get(NewMarkKeys.SIDE_DOCK);
		node.setCondition(new ICondition() {

			@Override
			public boolean isShowNew(boolean setIsOpen) {
				FunctionPurchaseManager purchaseManager = FunctionPurchaseManager
						.getInstance(mContext);
				if (purchaseManager
						.getPayFunctionState(FunctionPurchaseManager.PURCHASE_ITEM_QUICK_ACTIONS) != FunctionPurchaseManager.STATE_GONE) {
					return DeskSettingUtils.isFirstShowSideDock(mContext, setIsOpen);
				}
				return false;
			}
		});

		node = mNodeMap.get(NewMarkKeys.ICON_FILTER);
		node.setCondition(new ICondition() {

			@Override
			public boolean isShowNew(boolean setIsOpen) {
				return false;
			}
		});

		node = mNodeMap.get(NewMarkKeys.WALLPAPER_FILTER);
		node.setCondition(new ICondition() {

			@Override
			public boolean isShowNew(boolean setIsOpen) {
				return DeskSettingUtils.isFirstShowWallpaperFilter(mContext, setIsOpen);
			}
		});

		node = mNodeMap.get(NewMarkKeys.NO_ADS);
		node.setCondition(new ICondition() {

			@Override
			public boolean isShowNew(boolean setIsOpen) {
				if (FunctionPurchaseManager.getInstance(mContext).getPayFunctionState(
						FunctionPurchaseManager.PURCHASE_ITEM_AD) != FunctionPurchaseManager.STATE_GONE) {
					return DeskSettingUtils.isFirstShowNoAdvertTip(mContext, setIsOpen);
				}
				return false;
			}
		});

		node = mNodeMap.get(NewMarkKeys.SCREEN_TRANSITION);
		node.setCondition(new ICondition() {

			@Override
			public boolean isShowNew(boolean setIsOpen) {
				return EffectorControler.getInstance().checkHasNewEffector(
						EffectorControler.TYPE_SCREEN_SETTING);
			}
		});

		node = mNodeMap.get(NewMarkKeys.APPDRAWER_HOR_TRANSITION);
		node.setCondition(new ICondition() {

			@Override
			public boolean isShowNew(boolean setIsOpen) {
				return EffectorControler.getInstance().checkHasNewEffector(
						EffectorControler.TYPE_APP_DRAWER_SETTING);
			}
		});

		node = mNodeMap.get(NewMarkKeys.SCREEN_GESTURE);
		node.setCondition(new ICondition() {

			@Override
			public boolean isShowNew(boolean setIsOpen) {
				return DeskSettingUtils.isFirstShowFourGestureTip(mContext, setIsOpen);
			}
		});
	}

	public boolean isShowNew(String key, boolean setIsOpen) {
		NewMarkNode node = mNodeMap.get(key);
		if (node != null) {
			return node.isShowNew(setIsOpen);
		}
		return false;
	}

	public int getNewCount(String key) {
		int count = 0;
		NewMarkNode node = mNodeMap.get(key);
		if (node != null) {
			count = node.getNewCount();
		}
		return count;
	}

	/**
	 * 
	 * @author yangguanxiang
	 *
	 */
	private class NewMarkXmlParser {

		public void parseXml() {
			InputStream is = null;
			try {
				is = mContext.getAssets().open(NEW_MARK_XML);
				XmlPullParserFactory pullFactory = XmlPullParserFactory.newInstance();
				XmlPullParser xmlPullParser = pullFactory.newPullParser();
				xmlPullParser.setInput(is, "UTF-8");
				int eventType = xmlPullParser.getEventType();
				Stack<NewMarkNode> stack = new Stack<NewMarkNode>();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					String tag = null;
					switch (eventType) {
						case XmlPullParser.START_DOCUMENT :
							mNodeMap = new HashMap<String, NewMarkNode>();
							break;
						case XmlPullParser.START_TAG :
							tag = xmlPullParser.getName();
							if (TAG_NEW.equals(tag)) {
								String key = xmlPullParser.getAttributeValue(null, ATTR_KEY);
								NewMarkNode node = null;
								if (mNodeMap.containsKey(key)) {
									node = mNodeMap.get(key);
								} else {
									node = new NewMarkNode();
									mNodeMap.put(key, node);
								}
								if (!stack.isEmpty()) {
									NewMarkNode parent = stack.peek();
									parent.addChild(node);
								}
								stack.add(node);
							}
							break;
						case XmlPullParser.END_TAG :
							tag = xmlPullParser.getName();
							if (TAG_NEW.equals(tag)) {
								if (!stack.isEmpty()) {
									stack.pop();
								}
							}
							break;
						default :
							break;
					}
					eventType = xmlPullParser.next();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 
	 * @author yangguanxiang
	 *
	 */
	private class NewMarkNode {
		private ArrayList<NewMarkNode> mChildren;
		private ICondition mCondition;

		public void addChild(NewMarkNode child) {
			if (mChildren == null) {
				mChildren = new ArrayList<NewMarkNode>();
			}
			mChildren.add(child);
		}

		public void setCondition(ICondition condition) {
			mCondition = condition;
		}

		public boolean isShowNew(boolean setIsOpen) {
			boolean ret = false;
			if (mCondition != null) {
				ret = mCondition.isShowNew(setIsOpen);
			}
			if (mChildren != null && !mChildren.isEmpty()) {
				for (NewMarkNode child : mChildren) {
					ret |= child.isShowNew(setIsOpen);
				}
			}
			return ret;
		}

		public int getNewCount() {
			int count = 0;
			if (mCondition != null && mCondition.isShowNew(false)) {
				count++;
			}
			if (mChildren != null && !mChildren.isEmpty()) {
				for (NewMarkNode child : mChildren) {
					count += child.getNewCount();
				}
			}
			return count;
		}
	}
	
	/**
	 * 
	 * @author yangguanxiang
	 *
	 */
	private interface ICondition {
		public boolean isShowNew(boolean setIsOpen);
	}
}
