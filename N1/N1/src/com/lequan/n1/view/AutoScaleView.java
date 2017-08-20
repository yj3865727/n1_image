package com.lequan.n1.view;

import com.lequan.n1.activity.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;


//图片除去不必要的部分
public class AutoScaleView extends FrameLayout {

	public static final int RELATIVE_WIDTH = 0;
	public static final int RELATIVE_HEIGHT = 1;

	private int relative = RELATIVE_WIDTH;
	private float scale;

	public AutoScaleView(Context context) {
		this(context, null);
	}

	public AutoScaleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AutoScaleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoScaleView);
		relative = a.getInt(R.styleable.AutoScaleView_relative, relative);
		scale = a.getFloat(R.styleable.AutoScaleView_scale, scale);
		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		// 动态计算宽高比
		// 宽度固定且相对宽度
		if (widthMode == MeasureSpec.EXACTLY && relative == RELATIVE_WIDTH && scale != 0) {
			// 获取宽度
			int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
			// 计算孩子的宽度
			int childrenWidth = parentWidth - getPaddingLeft() - getPaddingRight();
			// 计算孩子的高度
			int childrenHeight = (int) (childrenWidth / scale + .5f);
			// 计算父控件的高度
			int parentHeight = childrenHeight + getPaddingBottom() + getPaddingTop();
			// 设置孩子的宽高
			measureChildren(MeasureSpec.makeMeasureSpec(childrenWidth, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(childrenHeight, MeasureSpec.EXACTLY));
			// 设置父控件的宽高
			setMeasuredDimension(MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(parentHeight, MeasureSpec.EXACTLY));
		} else if (heightMode == MeasureSpec.EXACTLY && relative == RELATIVE_HEIGHT && scale != 0) {
			// 获取父控件的高度
			int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
			// 计算孩子的高度
			int childrenHeight = parentHeight - getPaddingBottom() - getPaddingTop();
			// 计算孩子的宽度
			int childrenWidth = (int) (childrenHeight * scale + .5f);
			// 计算父控件的宽度
			int parentWidth = childrenWidth + getPaddingLeft() + getPaddingRight();
			// 设置孩子的宽高
			measureChildren(MeasureSpec.makeMeasureSpec(childrenWidth, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(childrenHeight, MeasureSpec.EXACTLY));
			// 设置父控件的宽高
			setMeasuredDimension(MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(parentHeight, MeasureSpec.EXACTLY));
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	/**
	 * 设置相对方向
	 */
	public void setRelative(int relative) {
		this.relative = relative;
	}

	/**
	 * 设置图片的缩放比列
	 */
	public void setScale(float scale) {
		this.scale = scale;
	}

}
