package com.jiubang.ggheart.components.appmanager;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.proxy.MsgMgrProxy;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IScreenFrameMsgId;

/**
 * 
 * <br>类描述:清理屏幕
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2013-5-8]
 */
public class CleanAdIconView extends LinearLayout implements OnClickListener {
	
	public Context mContext;
	public RelativeLayout mScanResultLayout; //扫描结果布局
	public Button mBtnClear; //一键清理按钮
	
	public ListView mScanListView; //扫描结果
	public CleanScreenListAdapter mScanResultAdapter;
	
	ArrayList<CleanScreenInfo> mSacnResultList = new ArrayList<CleanScreenInfo>(); //数据源
	ArrayList<CleanScreenInfo> mRecommendList = new ArrayList<CleanScreenInfo>(); //推荐图标临时列表
	ArrayList<CleanScreenInfo> mAutoShortCutList = new ArrayList<CleanScreenInfo>(); //推荐widget临时列表
	
	public boolean mShowExitBtn = true; //是否显示退出按钮
	public static final int MESSAGE_SCAN_AGAIN = 0;
	
	public CleanAdIconView(Context context) {
		super(context);
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.clean_screen_view, this);
		
		initView();
		initListView();
		sacnScreen();
	}
	
	public void initView() {
		mScanResultLayout = (RelativeLayout) findViewById(R.id.sacan_result_layout);
		mBtnClear = (Button) findViewById(R.id.btn_one_key_clernt);
		mBtnClear.setOnClickListener(this);
	}

	public void initListView() {
		mScanListView = (ListView) findViewById(R.id.result_listview);
		mScanResultAdapter = new CleanScreenListAdapter(mContext);
		mScanListView.setAdapter(mScanResultAdapter);
		mScanResultAdapter.updateDataSource(mSacnResultList);
		mScanListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if (mSacnResultList != null && position < mSacnResultList.size()) {
					CleanScreenInfo selectInfo = mSacnResultList.get(position);
					selectInfo.mIsCheck = !selectInfo.mIsCheck;
					//点击标题，设置是否全选
					if (selectInfo.mType == AppManagerUtils.TYPE_FARTHER_TITLE) {
						for (CleanScreenInfo childInfo : mSacnResultList) {
							if (childInfo.mParentId == selectInfo.mId) {
								childInfo.mIsCheck = selectInfo.mIsCheck;
							}
						}
					} else {
						//点击子项，设置标题checkbox是否勾上
						int selectInfoParentId = selectInfo.mParentId;
						boolean flag = true;
						for (CleanScreenInfo info : mSacnResultList) {
							if (info.mParentId == selectInfoParentId) {
								if (!info.mIsCheck) {
									flag = false;
									break;
								}
							}
						}
						setTitleCheckBoxState(selectInfoParentId, flag);
					}
					mScanResultAdapter.notifyDataSetChanged();
				}
				
				setCleanBtnState(); //设置清除按钮的状态和内容
				
			}
		});
	}
	
	/**
	 * <br>功能简述:设置标题项的check数据状态
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param id
	 * @param flag
	 */
	public void setTitleCheckBoxState(int id, boolean flag) {
		for (CleanScreenInfo info : mSacnResultList) {
			if (info.mId == id) {
				info.mIsCheck = flag;
				break;
			}
		}
	}
	
	/**
	 * <br>功能简述:设置清除按钮的状态和内容
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void setCleanBtnState() {
		int slectSize = 0;
		//检查选择了几个选项
		for (CleanScreenInfo info : mSacnResultList) {
			if (info.mType != AppManagerUtils.TYPE_FARTHER_TITLE && info.mType != AppManagerUtils.TYPE_NO_RESULT) {
				if (info.mIsCheck) {
					slectSize = slectSize + 1;
				}
			}
		}
		if (slectSize == 0) {
			mBtnClear.setText(R.string.clear_screen_one_key_clear);
			mBtnClear.setEnabled(false);
			mBtnClear.setTextColor(Color.parseColor("#707070"));
		} else {
			mBtnClear.setText(mContext.getString(R.string.clear_screen_one_key_clear) + "(" + slectSize + ")");
			mBtnClear.setEnabled(true);
			mBtnClear.setTextColor(getResources().getColor(R.color.theme_detail_apply_text));
		}
	}

	@Override
	public void onClick(View v) {
		//一键清理按钮
		if (v == mBtnClear) {
			delSelectIcon(); 
			mHandler.sendEmptyMessageDelayed(MESSAGE_SCAN_AGAIN, 200);
		}

	}

	/**
	 * <br>功能简述:扫描桌面
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	@SuppressWarnings("unchecked")
	public void sacnScreen() {
		mSacnResultList.clear();
		mRecommendList.clear();
		mAutoShortCutList.clear();
		
		ArrayList list = new ArrayList();
		list.add(mRecommendList);
		list.add(mAutoShortCutList);
		
		//扫描获取需要删除的数据列表
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
				IScreenFrameMsgId.CLEAN_SCREEN_SCAN_SCREEN, -1, null, list);
		//一周未使用应用
//		setSacnResultList(1000, R.string.clear_screen_title_week_not_use, R.string.clear_screen_tip_coming_soon, null);
		
		//推荐广告图标
		setSacnResultList(2000, R.string.clear_screen_title_recommend, R.string.clear_screen_no_dirty, mRecommendList);
		
		//自动生成图标
		setSacnResultList(3000, R.string.clear_screen_title_auto, R.string.clear_screen_no_dirty, mAutoShortCutList);
		
		//刷新列表
		mScanResultAdapter.notifyDataSetChanged();
		setCleanBtnState(); //设置清除按钮的状态和内容
	}
	
	/**
	 * <br>功能简述:设置每个分类的数据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param parentId
	 * @param titleResId
	 * @param noResultResId
	 * @param addList
	 */
	public void setSacnResultList(int parentId, int titleResId, int noResultResId, ArrayList<CleanScreenInfo> addList) {
		//添加标题栏
		CleanScreenInfo cleanScreenInfo = new CleanScreenInfo();
		cleanScreenInfo.mId = parentId; //设置标题id
		cleanScreenInfo.mType = AppManagerUtils.TYPE_FARTHER_TITLE;
		cleanScreenInfo.mFartherTitle = mContext.getString(titleResId, 0); //设置标题
		cleanScreenInfo.mIsShowCheckBox = false; //设置显示checkbox,默认不显示
		mSacnResultList.add(cleanScreenInfo);
		
		//添加子项
		if (addList != null && addList.size() > 0) {
			mShowExitBtn = false; //如果有数据就因此退出按钮
			for (CleanScreenInfo info : addList) {
				info.mParentId = parentId; //设置父id
			}
			mSacnResultList.addAll(addList);
			
			cleanScreenInfo.mFartherTitle = mContext.getString(titleResId, String.valueOf(addList.size())); //设置标题
			cleanScreenInfo.mIsShowCheckBox = true; //设置显示checkbox
			addList.clear();
		} else {
			//沒有数据时显示的内容
			CleanScreenInfo cleanScreenInforNoResult = new CleanScreenInfo();
			cleanScreenInforNoResult.mType = AppManagerUtils.TYPE_NO_RESULT;
			cleanScreenInforNoResult.mNoResultTitle = noResultResId; //标题
			mSacnResultList.add(cleanScreenInforNoResult);
		}
	}
	
	
	
	/**
	 * <br>功能简述:删除选择的图标
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void delSelectIcon() {
		ArrayList<CleanScreenInfo> delIconList = new ArrayList<CleanScreenInfo>();
		for (CleanScreenInfo cleanScreenInfo : mSacnResultList) {
			//去掉标题和没有内容显示部分
			if (cleanScreenInfo.mType != AppManagerUtils.TYPE_NO_RESULT 
					&& cleanScreenInfo.mType != AppManagerUtils.TYPE_FARTHER_TITLE
					&& cleanScreenInfo.mIsCheck) {
				delIconList.add(cleanScreenInfo);
			}
		}
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
				IScreenFrameMsgId.CLEAN_SCREEN_DEL_ONE_ICON, -1, delIconList, null);
	}
	
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == MESSAGE_SCAN_AGAIN) {
				Toast.makeText(mContext, R.string.clear_screen_clean_finish, Toast.LENGTH_SHORT).show();
				sacnScreen(); // 重新扫描
			}
		};
	};
	
	
	public void onDestroy() {
		if (mSacnResultList != null) {
			mSacnResultList.clear();
			mSacnResultList = null;
		}
		
		if (mRecommendList != null) {
			mRecommendList.clear();
			mRecommendList = null;
		}
		
		if (mAutoShortCutList != null) {
			mAutoShortCutList.clear();
			mAutoShortCutList = null;
		}
	}
}
