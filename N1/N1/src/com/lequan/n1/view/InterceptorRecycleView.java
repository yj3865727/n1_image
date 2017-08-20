package com.lequan.n1.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class InterceptorRecycleView extends RecyclerView {
	
	public InterceptorRecycleView(Context context) {
		this(context,null);
	}

	public InterceptorRecycleView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public InterceptorRecycleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		getParent().requestDisallowInterceptTouchEvent(true);
		return super.dispatchTouchEvent(ev);
	}

}
