package com.jiubang.ggheart.zeroscreen.search.contact.pinyin.util;

//CHECKSTYLE:OFF
public class HanyulUtil 
{	
	public static String getFirstByHanyul(char aSrc)
	{
		int word = PinyinTools.GetHanyul(aSrc);
		if (word == -1)
			return null;
		else
			return String.valueOf((char)word);
	}
}