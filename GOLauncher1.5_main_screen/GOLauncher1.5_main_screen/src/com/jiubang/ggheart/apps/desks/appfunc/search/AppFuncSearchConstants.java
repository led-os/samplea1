package com.jiubang.ggheart.apps.desks.appfunc.search;

public interface AppFuncSearchConstants {
	public static final int LOCAL_ADAPTER_TAG = 0x300;
	public static final int LOCAL_HISTORY_ADAPTER_TAG = 0x301;
	public static final int GOSTORE_ADAPTER_TAG = 0x302;
	public static final int GOSTORE_HISTORY_ADAPTER_TAG = 0x303;
	public static final int KEY_WORDS_ADAPTER_TAG = 0X304;

	public static final int MSG_UPDATE_KEYWORDS = 0x400;
	public static final int MSG_SET_LOCAL_RESULT_ADAPTER = 0x401;
	public static final int MSG_SHOW_GOSTORE_HISTORY = 0x402;
	public static final int MSG_SHOW_GOSTORE_NODATA_VIEW = 0x404;
	public static final int MSG_SHOW_LOCAL_NODATA_VIEW = 0x405;
	public static final int MSG_SET_GOSTORE_RESULT_ADAPTER = 0x406;
	public static final int MSG_DATACHANGE_GOSTORE_ADAPTER = 0x407;
	public static final int MSG_SHOW_GOSTORE_PROGRESS_BAR = 0x408;
	public static final int MSG_CHANGE_IM_STATE = 0x409;
	public static final int MSG_SHOW_LOCAL_HISTORY = 0x410;
	public static final int MSG_SHOW_GOSTORE_LOAD_MORE_PROGRESS = 0x411;
	public static final int MSG_SHOW_INIT_LOAD_MORE_PROGRESS = 0x416;
	public static final int MSG_SHOW_SEARCH_CONNECT_EXCEPTION = 0x417;
	public static final int MSG_MEDIA_DATA_READY = 0x418;
	public static final int MSG_SHOW_LOCAL_PROGRESS_BAR = 0x422;
	public static final int MSG_SHOW_KEY_WORDS = 0x424;
	public static final int MSG_DELETE_SEARCH_HISTORY = 0x425;

	public static final int LOCAL_LIST = 0;
	public static final int GOSTROE_LIST = 1;
}
