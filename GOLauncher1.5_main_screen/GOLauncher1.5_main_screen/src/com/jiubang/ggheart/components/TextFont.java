package com.jiubang.ggheart.components;

import java.lang.ref.WeakReference;

import android.graphics.Typeface;

import com.go.proxy.ApplicationProxy;
import com.go.util.BroadCaster.BroadCasterObserver;
import com.golauncher.message.IAppCoreMsgId;
import com.jiubang.ggheart.apps.font.FontBean;
import com.jiubang.ggheart.apps.font.FontControler;
import com.jiubang.ggheart.data.DataType;
/**
 * 
 * <br>类描述:字体
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2013-7-3]
 */
public class TextFont implements ISelfObject, BroadCasterObserver {
	private WeakReference<TextFontInterface> mTextFontInterfaceRef;

	/**
	 * @param textFontInterface
	 *            no null
	 */
	public TextFont(TextFontInterface textFontInterface) {
		mTextFontInterfaceRef = new WeakReference<TextFontInterface>(textFontInterface);

		selfConstruct();
	}

	@Override
	public void selfConstruct() {
		FontControler controler = FontControler.getInstance(ApplicationProxy.getContext());
		if (null != controler) {
			// 不发广播，改为直接重启桌面，原因是在TextFont注册了广播监听后，避免忘记反注册而导致内存泄漏
//			controler.registerObserver(this);
			initTypeface(controler.getUsedFontBean().mFontTypeface,
					controler.getUsedFontBean().mFontStyle);
		}
	}

	@Override
	public void selfDestruct() {
		mTextFontInterfaceRef.clear();
		mTextFontInterfaceRef = null;

		// 不发广播，改为直接重启桌面，原因是在TextFont注册了广播监听后，避免忘记反注册而导致内存泄漏
//		FontControler controler = FontControler.getInstance(ApplicationProxy.getContext());
//		controler.unRegisterObserver(this);
//		controler = null;
	}

	@Override
	public void onBCChange(int msgId, int param, Object ...object) {
		switch (msgId) {
			case IAppCoreMsgId.APPCORE_DATACHANGE :
				if (DataType.DATATYPE_DESKFONTCHANGED == param) {
					if (object[0] instanceof FontBean) {
						FontBean bean = (FontBean) object[0];
						initTypeface(bean.mFontTypeface, bean.mFontStyle);
					}
				}
				break;

			default :
				break;
		}
	}

	private void initTypeface(Typeface typeface, int style) {
		TextFontInterface textFontInterface = mTextFontInterfaceRef.get();
		if (null == textFontInterface) {
			return;
		}
		textFontInterface.onTextFontChanged(typeface, style);
	}
}
