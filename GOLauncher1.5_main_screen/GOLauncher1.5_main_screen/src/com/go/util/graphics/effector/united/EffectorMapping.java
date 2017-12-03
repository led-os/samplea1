package com.go.util.graphics.effector.united;

/**
 * 特效映射（用于旧的特效ID到新的特效ID映射）
 * @author yejijiong
 *
 */
public class EffectorMapping {
	/**
	 * 因为原有判断特性类型的逻辑,所以新加的单元格特效Index从50开始,双屏特效则小于50
	 */
	private final static int NEW_GRID_EFFECTOR_START_INDEX = 50;
	/** 新加的特效类型 （渐隐）目前2D还没加入 */
	/** 4.0 新加的特效类型 （渐隐）目前2D还没加入,新加入的特效都是subscreen型的特效 */
	private final static int SCREEN_NEW_EFFECTOR_INDEX = 17;
//	/** 桌面场所屏幕类特效的总数 */
//	private final static int SUBSCREEN_EFFECTOR_COUNT_IN_DESK = 11;
	/** 功能表场所单元格类特效的总数 */
	private final static int GRIDSCREEN_EFFECTOR_COUNT_IN_MENU = 7;
	private final static int EFFECTOR_TYPE_DEFAULT = 0;
	
//	public static int getNewEffectorIdForDeskTop(int oldId) { // 屏幕特效保留原值，不用转换
//		if (oldId >= SUBSCREEN_EFFECTOR_COUNT_IN_DESK && oldId < SCREEN_NEW_EFFECTOR_INDEX
//				|| oldId >= NEW_GRID_EFFECTOR_START_INDEX) {
//			oldId = oldId - SUBSCREEN_EFFECTOR_COUNT_IN_DESK + 1;
//			return getGridScreenEffectorNewId(oldId);
//		}
//		return oldId;
//	}
	
	public static int getNewEffectorIdForAppDrawer(int oldId) {
		if (oldId >= EFFECTOR_TYPE_DEFAULT && oldId < GRIDSCREEN_EFFECTOR_COUNT_IN_MENU
				|| oldId >= NEW_GRID_EFFECTOR_START_INDEX) {
			return getGridScreenEffectorNewId(oldId);
		} else if (oldId >= GRIDSCREEN_EFFECTOR_COUNT_IN_MENU && oldId < SCREEN_NEW_EFFECTOR_INDEX) {
			return oldId - GRIDSCREEN_EFFECTOR_COUNT_IN_MENU + 1;
		} 
		return oldId;
	}
	
	private final static int GRID_EFFECTOR_TYPE_BINARY_STAR = 1;
	private final static int GRID_EFFECTOR_TYPE_CHARIOT = 2;
	private final static int GRID_EFFECTOR_TYPE_SHUTTER = 3;
	private final static int GRID_EFFECTOR_TYPE_CHORD = 4;
	private final static int GRID_EFFECTOR_TYPE_CYLINDER = 5;
	private final static int GRID_EFFECTOR_TYPE_SPHERE = 6;
	private static int getGridScreenEffectorNewId(int type) {
		switch (type) {
			case GRID_EFFECTOR_TYPE_BINARY_STAR :
				return 11;
			case GRID_EFFECTOR_TYPE_CHARIOT :
				return 12;
			case GRID_EFFECTOR_TYPE_SHUTTER :
				return 13;
			case GRID_EFFECTOR_TYPE_CHORD :
				return 14;
			case GRID_EFFECTOR_TYPE_CYLINDER :
				return 15;
			case GRID_EFFECTOR_TYPE_SPHERE :
				return 16;
			default :
				return 0;
		}
	}
	
//	final static int EFFECTOR_TYPE_CUBOID2 = 1;
//	final static int EFFECTOR_TYPE_FLIP2 = 2;
//	final static int EFFECTOR_TYPE_ROLL = 4;
//	final static int EFFECTOR_TYPE_WINDMILL = 7;
//
//	final static int EFFECTOR_TYPE_BOUNCE = 3;
//	final static int EFFECTOR_TYPE_BULLDOZE = 5;
//	final static int EFFECTOR_TYPE_CUBOID1 = 6;
//	final static int EFFECTOR_TYPE_FLIP = 8;
//	final static int EFFECTOR_TYPE_WAVE = 9;
//	public final static int EFFECTOR_TYPE_WAVE_FLIP = 17;
//	public final static int EFFECTOR_TYPE_CARD_FLIP = 18;
//	final static int EFFECTOR_TYPE_STACK = 10;
//	public final static int EFFECTOR_TYPE_CROSSFADE = 19;
//	public final static int EFFECTOR_TYPE_FLYIN = 20;
//	public final static int EFFECTOR_TYPE_PAGETURN = 21;
//	public final static int EFFECTOR_TYPE_CURVE = 22;
//	public int getSubScreenEffectorNewId(int type) {
//		switch (type) {
//			case EFFECTOR_TYPE_BOUNCE :
//				return 3;
//			case EFFECTOR_TYPE_BULLDOZE :
//				return 5;
//			case EFFECTOR_TYPE_CUBOID1 :
//				return 6;
//			case EFFECTOR_TYPE_CUBOID2 :
//				return 1;
//			case EFFECTOR_TYPE_FLIP :
//				return 8;
//			case EFFECTOR_TYPE_FLIP2 :
//				return 2;
//			case EFFECTOR_TYPE_ROLL :
//				return 4;
//			case EFFECTOR_TYPE_WAVE :
//				return 9;
//			case EFFECTOR_TYPE_WAVE_FLIP :
//				return 17;
//			case EFFECTOR_TYPE_CARD_FLIP:
//				return 18;
//			case EFFECTOR_TYPE_WINDMILL :
//				return 7;
//			case EFFECTOR_TYPE_STACK :
//				return 10;
//			case EFFECTOR_TYPE_CROSSFADE:
//				return 19;
//			case EFFECTOR_TYPE_FLYIN:
//				return 20;
//			case EFFECTOR_TYPE_PAGETURN:
//				return 21;
//			case EFFECTOR_TYPE_CURVE:
//				return 22;
//			default :
//				return 0;
//		}
//	}
}
