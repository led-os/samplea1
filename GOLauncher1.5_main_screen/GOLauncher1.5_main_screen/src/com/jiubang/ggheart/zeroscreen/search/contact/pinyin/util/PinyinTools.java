package com.jiubang.ggheart.zeroscreen.search.contact.pinyin.util;

import java.util.ArrayList;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.jb.util.pylib.Hanzi2Pinyin;
import com.jiubang.ggheart.zeroscreen.search.contact.ContactDataItem;



/**
 * 拼音工具
 * @author huangdingwu
 * @date 2011.02.22
 */
//CHECKSTYLE:OFF
public class PinyinTools {
	/**
	* 汉字转换位汉语拼音首字母，英文字符不变
	* @param chines 汉字
	* @return 拼音
	*/  
	
	public static String[] converterToSpell(char c)
	{
		return converterToSpell_hide(c);
	}
	
	private static String[] converterToSpell_hide(char c)
	{
//		Hanzi2Pinyin pInstance = Hanzi2Pinyin.getInstance(null, 0);
		Hanzi2Pinyin pInstance = Hanzi2Pinyin.getInstance(ApplicationProxy.getContext(), R.raw.unicode2pinyin);
		if( pInstance == null)
		{
			return null;
		}
		
		int iUnicode = (int)c;
		String[] strResult = pInstance.GetPinyin(iUnicode);
		return strResult;
	}
	
	public static int GetHanyul(char c)
	{
//		Hanzi2Pinyin pInstance = Hanzi2Pinyin.getInstance(null, 0);
		Hanzi2Pinyin pInstance = Hanzi2Pinyin.getInstance(ApplicationProxy.getContext(), R.raw.unicode2pinyin);
		if( pInstance == null)
		{
			return -1;
		}
		
		int iUnicode = (int)c;
		int iValue = pInstance.GetHanyul(iUnicode);
		return iValue;
	}
	
	public static ArrayList<String[]> converterToSpell(ContactDataItem aName)
	{
		return converterToSpell_hide(aName);
	}
	
	private static ArrayList<String[]> converterToSpell_hide(ContactDataItem aName)
	{       
		String chs = aName.getName();
		StringBuilder sortkeySb = new StringBuilder();
		StringBuilder nameSb = new StringBuilder();
		StringBuilder firstNameSb = new StringBuilder();
		String[]  value;
		ArrayList<String[]> pyList = new ArrayList<String[]>();
		
		for (int i = 0; i < chs.length(); i++) 
		{	
			char c = chs.charAt(i);
			if(c >= 0x4e00 && c <= 0x9fa5)
			{
				value = converterToSpell(c);
				if(value != null && value.length > 0 )
				{
					nameSb.append(value[0]);
					sortkeySb.append(value[0]);
					sortkeySb.append(c);
					firstNameSb.append(value[0].charAt(0));
				}
			}else if (c >= 44032 && c <= 55203)					// 处理韩文
			{

				String val = HanyulUtil.getFirstByHanyul(c);
				
				if ( val != null)
				{
					sortkeySb.append(val);
					nameSb.append(val);
					firstNameSb.append(val);
				}
				else
				{
					sortkeySb.append(c);
					nameSb.append(val);
					firstNameSb.append(val);
				}
			}
			else 
			{
				String strTemp = chs.substring(i, i + 1).toLowerCase();
				sortkeySb.append(strTemp);
				nameSb.append(strTemp);
				firstNameSb.append(strTemp);
			}

		}
		aName.setSortKey(sortkeySb.toString());
		aName.setPinyin(nameSb.toString());
		aName.setFirstLetters(firstNameSb.toString());
		firstNameSb = null;
		sortkeySb = null;
		nameSb = null;
		return pyList;
	}
}

