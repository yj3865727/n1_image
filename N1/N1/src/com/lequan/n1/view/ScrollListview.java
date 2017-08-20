package com.lequan.n1.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ScrollListview extends ListView {

	public ScrollListview(Context context) {
		this(context, null);
	}

	public ScrollListview(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScrollListview(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		ListAdapter adapter = this.getAdapter();
		int height = getPaddingBottom() + getPaddingTop();
		// 动态计算高度
		if (adapter != null) {
			for (int i = 0; i < adapter.getCount(); i++) {
				View view = adapter.getView(i, null, this);
				//计算孩子高度
				view.measure(0, 0);
				height += view.getMeasuredHeight();
			}
			int measureSpecHeight = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
			setMeasuredDimension(widthMeasureSpec, measureSpecHeight);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

}
