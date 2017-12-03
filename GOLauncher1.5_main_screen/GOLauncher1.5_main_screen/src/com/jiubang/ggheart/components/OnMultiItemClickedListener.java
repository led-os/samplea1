package com.jiubang.ggheart.components;

/**
 * 
 * @author dengdazhong
 *
 */
public interface OnMultiItemClickedListener {
	/**
	 * 当多选项被选中后的回调
	 * 
	 * @author wuziyi
	 * @param position
	 *            点击图标对应列表中的位置
	 * @param isSelected
	 *            图标是否属于被选中状态
	 */
	public void onMultiItemClicked(int position, boolean isSelected);
	
	/**
	 * 选择超出最大个数
	 */
	public void onReachMaxSeletedCount();
}
