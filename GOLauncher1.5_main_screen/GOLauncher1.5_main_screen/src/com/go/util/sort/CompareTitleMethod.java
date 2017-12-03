package com.go.util.sort;

import java.text.Collator;
import java.util.Locale;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.util.device.Machine;
import com.jb.util.pylib.Hanzi2Pinyin;

/**
 * 按标题逻辑排序
 * @author wuziyi
 *
 */
public class CompareTitleMethod extends CompareMethod<ITitleCompareable> {
	
//	private Context mContext;
	private Collator mCollator;
	private Hanzi2Pinyin mpInstance;
	private boolean mIsSDK16 = Machine.IS_JELLY_BEAN;
	
	public CompareTitleMethod() {
//		mContext = context;
		/**
		 * @edit by huangshaotao
		 * @date 2012-7-31
		 *       在4.1的系统使用Locale.CHINESE按名称排序时会把汉字排在英文前面（原因未明），
		 *       因此针对4.1或以上系统，使用Locale.ENGLISH
		 */
		if (mIsSDK16) {
			mCollator = Collator.getInstance(Locale.ENGLISH);
		} else {
			mCollator = Collator.getInstance(Locale.CHINESE);
		}
		if (mCollator == null) {
			mCollator = Collator.getInstance(Locale.getDefault());
		}
		mpInstance = Hanzi2Pinyin.getInstance(ApplicationProxy.getContext(), R.raw.unicode2pinyin);
	}

	@Override
	protected int getCompareResult(ITitleCompareable compareInfoA, ITitleCompareable compareInfoB) {
		String stringA = compareInfoA.getTitle();
		String stringB = compareInfoB.getTitle();
		if (stringA == null && stringB == null) {
			return 0;
		} else if (stringA == null && stringB != null) {
			return -1;
		} else if (stringA != null && stringB == null) {
			return 1;
		} else if (mIsSDK16 && stringA.length() > 0 && stringB.length() > 0) {
			boolean aIsCN = isChinese(stringA.charAt(0));
			boolean bIsCN = isChinese(stringB.charAt(0));
			if (aIsCN && bIsCN) {
				stringA = changeChineseToSpell(stringA);
				stringB = changeChineseToSpell(stringB);
			} else if (!aIsCN && bIsCN) {
				if (mOrder == DESC) {
					return 1;
				} else {
					return -1;
				}
			} else if (aIsCN && !bIsCN) {
				if (mOrder == DESC) {
					return -1;
				} else {
					return 1;
				}
			} else if (isContainChinese(stringA) && isContainChinese(stringB)) {
				stringA = changeChineseToSpell(stringA);
				stringB = changeChineseToSpell(stringB);
			}
		}
		
		int ret = 0;
		if (mOrder == ASC) {
			ret = mCollator.compare(stringA, stringB);
		} else if (mOrder == DESC) {
			ret = mCollator.compare(stringB, stringA);
		}
		return ret;
	}
	
	/**
	 * <br>功能简述:判断一个字符是否汉字
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param key
	 * @return
	 */
	private boolean isChinese(char key) {
		boolean isHanzi = false;
		if (key >= 0x4e00 && key <= 0x9fa5) {
			isHanzi = true;
		}
		return isHanzi;
	}
	
	private boolean isContainChinese(String text) {
		boolean ret = false;
		if (text == null || "".equals(text)) {
			ret = false;
		} else {
			int size = text.length();
			for (int i = 0; i < size; i++) {
				if (isChinese(text.charAt(i))) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 * <br>功能简述:将一个包含有汉字的字符串转换成拼音字符串（如果有的话）
	 * <br>功能详细描述:将一个包含有汉字的字符串转换成拼音字符串（如果有的话）
	 * <br>注意:
	 * @param text 
	 * @return 有可能为null，使用前先判断
	 */
	private String changeChineseToSpell(String text) {
		if (text == null || "".equals(text)) {
			return text;
		}

		StringBuffer results = new StringBuffer();
		if (mpInstance == null) {
			return text;
		}

		int size = text.length();
		for (int i = 0; i < size; i++) {
			// 是汉字则转成拼音，不是则原样转成数组返回
			char key = text.charAt(i);
			if (isChinese(key)) {
				int code = key;
				String[] temp = mpInstance.GetPinyin(code);
				if (temp != null && temp.length > 0) {
					results.append(temp[0]);
				}
			} else {
				results.append(String.valueOf(key));
			}
		}
		return results.toString();
	}

}
