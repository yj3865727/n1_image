package com.lequan.n1.adapter.holder;

import android.view.View;

public abstract class BaseViewHolder<T> {

	private View conveter;

	public BaseViewHolder() {
		conveter = initView();
		conveter.setTag(this);
	}
	
	/** 获取根布局
	 * @return
	 */
	public View getConveter() {
		return conveter;
	}

	/** 初始化根布局
	 * @return
	 */
	protected abstract View initView();
	
	/** 设置数据，刷新ui
	 * @param t
	 * @param position
	 */
	public void setDataAndRefreshUi(T t,int position){
		refreshUi(t,position);
	}

	/** 刷新ui
	 * @param t
	 * @param position
	 */
	protected abstract void refreshUi(T t, int position);
	
}
