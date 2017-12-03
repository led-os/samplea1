package com.jiubang.ggheart.activationcode.invite;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.util.StringUtil;
import com.go.util.StringUtils;
import com.jiubang.ggheart.activationcode.invite.InviteController.ReuqestDataListener;

/**
 * 邀请好友入口
 * @author caoyaming
 *
 */
public class InviteActivity extends Activity implements OnClickListener, OnKeyListener {
	//====================Handler=====================
	//邀请好友成功
	private static final int MESSAGE_INVITE_SUCCESS = 0x0001;
	//使用Toast显示提示消息
	private static final int MESSAGE_SHOW_TOAST = 0x0002;
	//邀请好友控制管理类对象
	private InviteController mInviteController;
	//返回按钮
	private ImageView mBackBtn;
	//剩余邀请人数
	private TextView mInvitePeopleNumberText;
	//邀请邮箱
	private EditText mInviteEmailEditText;
	//邀请按钮
	private Button mInviteBtn;
	//Loading框Dialog
	private Dialog mLoadingDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activationcode_invite_page_layout);
		//获取邀请好友控制管理类对象
		mInviteController = InviteController.getInstance(this);
		//初始化View
		initView();
	}
	/**
	 * 初始化View
	 */
	private void initView() {
		//返回按钮
		mBackBtn = (ImageView) findViewById(R.id.back_btn);
		//设置点击事件
		mBackBtn.setOnClickListener(this);
		//剩余邀请人数
		mInvitePeopleNumberText = (TextView) findViewById(R.id.invite_people_number);
		//设置值
		mInvitePeopleNumberText.setText(getResources().getString(R.string.activationcode_invite_explain_surplus, mInviteController.getInviteNumber()));
		//邀请邮箱
		mInviteEmailEditText = (EditText) findViewById(R.id.invite_email_edittext);
		//邀请按钮
		mInviteBtn = (Button) findViewById(R.id.invite_btn);
		//设置点击事件
		mInviteBtn.setOnClickListener(this);
	}
	/**
	 * Handler
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_INVITE_SUCCESS:
				//邀请好友成功
				//修改剩余邀请人数
				int inviteNumber = mInviteController.updateInviteNumber();
				if (mInvitePeopleNumberText != null) {
					mInvitePeopleNumberText.setText(getResources().getString(R.string.activationcode_invite_explain_surplus, inviteNumber));
				}
				//判断剩余要求人数是否为0,如果为0,则禁用邀请按钮
				if (inviteNumber <= 0 && mInviteBtn != null) {
					//禁用邀请按钮
					mInviteBtn.setEnabled(false);
				}
				//使用Toast提示邀请成功
				Toast.makeText(InviteActivity.this, R.string.activationcode_invite_success, Toast.LENGTH_LONG).show();
				break;
			case MESSAGE_SHOW_TOAST:
				//使用Toast显示提示消息
				if (msg.obj != null) {
					Toast.makeText(InviteActivity.this, StringUtil.toString(msg.obj), Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
	};
	/**
	 * 邀请状态监听器
	 */
	private ReuqestDataListener mRequestDataListener = new ReuqestDataListener() {
		@Override
		public void onFinish(String responseCode, String messageStr) {
			//邀请操作完成
			if (InviteController.HTTP_CODE_INVITE_SUCCESS.equals(responseCode)) {
				//邀请成功
				Message message = new Message();
				message.what = MESSAGE_INVITE_SUCCESS;
				message.obj = messageStr;
				mHandler.sendMessage(message);
			} else {
				//邀请失败,使用Toast显示出失败提示消息
				Message message = new Message();
				message.what = MESSAGE_SHOW_TOAST;
				message.obj = messageStr;
				mHandler.sendMessage(message);
			}
			//关闭Loading框
			dismissLoadingDialog();
		}
		@Override
		public void onException(String errorMessage) {
			//邀请操作异常,使用Toast显示出错误消息
			Message message = new Message();
			message.what = MESSAGE_SHOW_TOAST;
			message.obj = errorMessage;
			mHandler.sendMessage(message);
			//关闭Loading框
			dismissLoadingDialog();
		}
	};
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			//返回按钮
			InviteActivity.this.finish();
			break;
		case R.id.invite_btn:
			//邀请按钮
			//获取邀请邮箱
			String inviteEmail = StringUtil.toString(mInviteEmailEditText.getText());
			if (TextUtils.isEmpty(inviteEmail)) {
				//邀请邮箱为空,使用Toast提示用户
				Toast.makeText(InviteActivity.this, R.string.activationcode_invite_enter_email, Toast.LENGTH_SHORT).show();
				return;
			}
			//验证是否为邮箱格式
			if (!StringUtils.isEmailFormat(inviteEmail)) {
				//不是邮箱格式,使用Toast提示用户
				Toast.makeText(InviteActivity.this, R.string.activationcode_invite_email_format_error, Toast.LENGTH_SHORT).show();
				return;
			}
			//关闭软键盘
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
			//上传邀请人数据到服务器
			if (mInviteController.uploadInviteInfo(inviteEmail, mRequestDataListener)) {
				//显示Loading框
				showLoadingDialog();
			}
			break;
		default:
			break;
		}
		
	}
	@Override
	protected void onResume() {
		Log.d("cym", "onResume");
		super.onResume();
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.d("cym", "onConfigurationChanged   activity");
		super.onConfigurationChanged(newConfig);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("cym", "onDestroy   activity");
	}
	/**
	 * 显示Loading框
	 */
	private void showLoadingDialog() {
		if (mLoadingDialog == null) {
			View loadingView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.full_screen_loading_layout, null);
			//隐藏提示消息View
			loadingView.findViewById(R.id.loading_prompt_text).setVisibility(View.GONE);
			mLoadingDialog = new Dialog(this, R.style.Dialog);
			mLoadingDialog.addContentView(loadingView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mLoadingDialog.setOnKeyListener(this);
		} 
		if (!mLoadingDialog.isShowing()) {
			mLoadingDialog.show();
		}
	}
	/**
	 * 去除Loading框
	 */
	private void dismissLoadingDialog() {
		if (mLoadingDialog != null) {
			mLoadingDialog.dismiss();
		}
	}
	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return false;
	}
	/*@Override
	public void onBackPressed() {
		try {
			super.onBackPressed();
		} catch (Exception e) {
			Log.e(LogConstants.HEART_TAG, "onBackPressed err " + e.getMessage());
		}
	}*/

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
