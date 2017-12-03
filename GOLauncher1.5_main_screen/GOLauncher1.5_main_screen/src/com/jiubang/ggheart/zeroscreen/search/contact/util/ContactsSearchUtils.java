package com.jiubang.ggheart.zeroscreen.search.contact.util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

import com.jiubang.ggheart.zeroscreen.search.bean.SearchResultInfo;
import com.jiubang.ggheart.zeroscreen.search.contact.ContactDataItem;
import com.jiubang.ggheart.zeroscreen.search.contact.ContactsDataCache;
import com.jiubang.ggheart.zeroscreen.search.contact.Content;

/**
 * 联系人查询工具类
 * @author liulixia
 *
 */
//CHECKSTYLE:OFF
public class ContactsSearchUtils {
	private static ContactsSearchUtils mSearchUtils = null;
	private Context mContext;
	private List<SearchResultInfo> mSearchList = null;
//	private SearchResultInfo mTotalMatchInfo = null; //完成匹配或相似度最高对象
	
	public static synchronized ContactsSearchUtils getInstance(Context context) {
		if (mSearchUtils == null) {
			mSearchUtils = new ContactsSearchUtils(context);
		}
		return mSearchUtils;
	}
	
	private ContactsSearchUtils(Context context) {
		mContext = context;
	}

	/**
	 * 获取联系人列表信息
	 * @param condition
	 */
	public List<SearchResultInfo> getPersonInfo(String condition) {
		ArrayList<ContactDataItem> contacts = ContactsDataCache.getInstance().getContacts();
//		Log.v("llx", "缓存拿到联系人个数是：" + contacts.size());
		mSearchList = search(contacts, condition);
		
		// 屏蔽掉最佳匹配规则
/*		if (mSearchList == null || mSearchList.size() < 2) {
			//如果只有一个联系人或没有联系人，直接返回
			return mSearchList;
		}
		
		
		List<CallLogInfo> calls = getCallLogs();
		
		
		if (calls.size() > 0) {
			//多个联系人的话获取20条中的通话个数及最近通话时间
			for (int i = 0; i < calls.size(); i++) {
				CallLogInfo call = calls.get(i);
				for (int j = 0; j < mSearchList.size(); j++) {
					ContactDataItem person = mSearchList.get(j).mPersonInfo;
					
					if (person.getName().equals(call.mCallName)) {
						int count = person.getCallCount() + 1;
						person.setCallCount(count);
						if (person.getCallTime() == null) {
							person.setCallTime(call.mCallTime);
						}
						break;
					}
					List<PhoneNumber> phones = person.getPhones();
					for (PhoneNumber phone : phones) {
						String number = phone.number;
						if (number.equals(call.mNumber)) {
							int count = person.getCallCount() + 1;
							person.setCallCount(count);
							if (person.getCallTime() == null) {
								person.setCallTime(call.mCallTime);
							}
							break;
						}
					}
				}
			}
			//根据最佳匹配规则进行匹配（先查看使用频率，后看最近通话时间）
			try {
				ComparatorPerson comparator = new ComparatorPerson();
				Collections.sort(mSearchList, comparator); 
			} catch (Exception e) {
				
			}
		}*/
		return mSearchList;
	}
	
	/**
	 * 
	 * @author liulixia
	 * 联系人排序比较器
	 */
	class ComparatorPerson implements Comparator<SearchResultInfo> {

		@Override
		public int compare(SearchResultInfo lhs, SearchResultInfo rhs) {
			// TODO Auto-generated method stub
			ContactDataItem info1 = lhs.mPersonInfo;
			ContactDataItem info2 = rhs.mPersonInfo;
			if (info1.getCallCount() < info2.getCallCount()) {
				return 1;
			} else if (info1.getCallCount() > info2.getCallCount()) {
				return -1;
			} else {
					String calltime1 = info1.getCallTime();
					String calltime2 = info2.getCallTime();
					if (calltime1 != null && calltime2 != null) {
						return calltime2.compareTo(calltime1);
					}
					else {
						if (calltime1 != null) {
							return -1;
						} else if (calltime2 != null) {
							return 1;
						} else {
							return 0;
						}
					}
			}
			
//			if (info1.getCallCount() > info2.getCallCount()) {
//				return -1;
//			} else {
//				return 1;
//			}
		}
	}
	
//	public SearchResultInfo getTotalMatchInfo() {
//		return mTotalMatchInfo;
//	}
	
	public void recyle() {
		if (mSearchList != null) {
			mSearchList.clear();
		}
//		mTotalMatchInfo = null;
	}
	
	public void onDestory() {
		recyle();
		mSearchUtils = null;
		mContext = null;
	}
	
	/**
	 * ------------------------------------------- Go联系人相关查询代码-------------------------------------------
	 */
	private final static int KPinyinMode  = 1;
	private final static int KFirstLettersMode = 2;
	private final static int KPrimitiveMode = 3;

	
	//见正则表达式
	String getEscapeChar(char c)
	{
		switch(c) {
			case '^': case '$': case '(': case ')': 
			case '[': case ']': case '{': case '}':
			case '.': case '?': case '+': case '*':
			case '|': {
				return "\\" + c;
			}
			case '\\':
				return "\\\\";
			default: {
				return "" + c;
			}
		}
	}
    /**
     * 返回匹配权值，可用于排序
     * @param aDstString
     * @param aModuleString
     * @return
     */
    public int getMatchValueByModuleL(String aDstString, String aModuleString)
    {
        int matchValue = 0;
        Pattern pattern = Pattern.compile(aModuleString, Pattern.CASE_INSENSITIVE); //构造正则表达式，大小写不敏感
        
    	Matcher matcher = pattern.matcher(aDstString);
      
        final int KMAX_PINYIN_LEN = 64;  //最大长度
        //final int WEIGHT = 10; 			//匹配值权重，用于计算匹配值
        if( matcher.find() ) 
        {
            int matchLen = (matcher.end()-matcher.start()); //匹配串的长度，用于度量匹配程度
            //匹配串的长度与模式串长度的差值越大，匹配权值越低，关联权为KMAX_LETTER_LEN，对权值大小的影响大
            //匹配开始位置值越大，匹配权值越低，关联权为1，对权值的大小的影响小
            matchValue = (KMAX_PINYIN_LEN- matcher.start())*KMAX_PINYIN_LEN + (KMAX_PINYIN_LEN-matchLen); 
            
            //2011.2.18 这个语句会使得同一个姓可能被其他姓隔开
//            matchValue = WEIGHT*matchValue + (KMAX_PINYIN_LEN - aDstString.length()); //还要考虑结果串越长，匹配值越低 
        } 
        return matchValue;
    }
    
    /**
     * 根据输入的第一个字符自动使用 原始串搜索或者 拼音搜索
     * @param <TPinyinSearchable>
     * @param list
     * @param keyString
     * @return
     */
    public List<SearchResultInfo> search(List<ContactDataItem> list,String keyString)
	{
    	if(keyString == null || keyString.length() == 0) {
    		return null;
    	}
    	char ch = keyString.charAt(0);
    	
    	if( Character.isUpperCase(ch)
    		|| Character.isLowerCase(ch)
    		|| Character.isDigit(ch) ) { //是拉丁字母，使用拼音搜索
    		return searchByPinyin(list, keyString);
    	} else {
    		return searchBySearchField(list, keyString); //直接搜索原生串
    	}
	}
    
    /**
     * 拼音模糊搜索
     * @param <TPinyinSearchable>
     * @param list
     * @param keyString
     * @return
     */
    public List<SearchResultInfo> searchByPinyin(List<ContactDataItem> list,String keyString)
	{
    	return fuzzySearch(list,keyString,KPinyinMode);
	}

    /**
     * 首字母模糊搜索
     * @param <TPinyinSearchable>
     * @param list
     * @param keyString
     * @return
     */
    public List<SearchResultInfo> searchByFirstLetters(List<ContactDataItem> list,String keyString)
	{
    	return fuzzySearch(list,keyString,KFirstLettersMode);
	}
    
    /**
     * 名字模糊搜索
     * @param <TPinyinSearchable>
     * @param list
     * @param keyString
     * @return
     */
    public List<SearchResultInfo> searchBySearchField(List<ContactDataItem> list,String keyString)
	{
    	return fuzzySearch(list,keyString,KPrimitiveMode);
	}
    
    /**
     * 
     * @param <TPinyinSearchable>
     * @param list
     * @param keyString
     * @param searchField ： KPinyinMode 拼音 KFirstLettersMode 首字母
     * @return
     */
    private  List<SearchResultInfo> fuzzySearch(List<ContactDataItem> list,String keyString, int searchMode) 
    {
    	if( searchMode != KPinyinMode && searchMode != KFirstLettersMode && searchMode != KPrimitiveMode )
    	{
    		return null;
    	}
    	
    	if (mSearchList != null) {
			mSearchList.clear();
		}
//		mTotalMatchInfo = null;
		
		int keyStrLen = keyString.length();
    	if( keyStrLen <= 0)
    	{
    		return mSearchList;
    	}
    	
		if (mSearchList == null) {
			mSearchList = new ArrayList<SearchResultInfo>();
		} 
		
    	StringBuffer moduleBuf = new StringBuffer(); //模式串
    	String pkgString; //存储模式串中特殊字符的转义
    	pkgString = getEscapeChar(keyString.charAt(0));
    	moduleBuf.append(pkgString);
    	for( int i=1; i<keyStrLen; ++i) 
    	{
    		moduleBuf.append(".*?"); //改为勉强模式
    		pkgString = getEscapeChar(keyString.charAt(i));
        	moduleBuf.append(pkgString);
    	}
    	String module = moduleBuf.toString();
    	
    	int matchValue = 0;
    	ContactDataItem entity  = null; //一个可使用拼音搜索的对象
    	List<Content> searchList = new ArrayList<Content>(); //排序辅助列表
    	
    	String searchField = null;
    	String searchFieldFirstLetters = null;
    	int firstLettersMatchValue = 0; //首字母匹配值
    	for( int i=0; i< list.size(); i++)
		{
    		entity = (ContactDataItem) list.get(i);
    		
    		if( searchMode == KPinyinMode ){	//拼音模式支持拼音与首字母同时搜索
    			searchField = entity.getPinyin();
    			searchFieldFirstLetters = entity.getFirstLetters();
    			if( searchFieldFirstLetters != null) {
    				firstLettersMatchValue = getMatchValueByModuleL(searchFieldFirstLetters, module);
    			}
    		} else if ( searchMode == KFirstLettersMode ) {
    			searchField = entity.getFirstLetters();
    		} else if ( searchMode == KPrimitiveMode ) {
    			searchField = entity.getSearchField();
    		}
    		
    		if( searchField == null ) {
    			continue;
    		}
			matchValue = getMatchValueByModuleL(searchField, module);
			
			//拼音模式时 首字母匹配的结果优先
			if( firstLettersMatchValue > matchValue ) {	//拼音模式时“首字母匹配权值”更高，则更新“匹配权值”
				matchValue = firstLettersMatchValue;
			}
			
			if( matchValue > 0) //匹配
			{
				searchList.add(new Content(matchValue,entity));
			} else if (entity.getPhones().size() > 0 
					&& entity.getPhones().get(0).number.contains(keyString)) {
				searchList.add(new Content(-1, entity));
			}
		}
    	
    	ContentDescComparator comp = new ContentDescComparator(); 

		Collections.sort(searchList,comp);
		 
		Content content;
		for(int i = 0; i < searchList.size(); i++)
		{
			content = (Content) searchList.get(i);
			Object value = content.getValue();

			if(value instanceof PinyinSearchable) {
				SearchResultInfo result = new SearchResultInfo();
				result.mType = SearchResultInfo.ITEM_TYPE_CONTACTS;
				entity = (ContactDataItem) value;
				result.mPersonInfo = entity;
				
				//屏蔽掉最佳匹配规格
/*				if (entity.getName().equals(keyString)
						|| entity.getPinyin().equals(keyString)
						|| entity.getPhones().get(0).number.equals(keyString)) { //完成匹配
					result.mType = SearchResultInfo.ITEM_TYPE_CONTACTS;
					result.mShowBottomLine = false;
					mTotalMatchInfo = result;
				} else {
					mSearchList.add(result);
				}*/
				
				mSearchList.add(result);
			}
			
		}
		
		return mSearchList; 		 	
    }
}
