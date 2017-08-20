package com.lequan.n1.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.lequan.n1.activity.fragment.ScrollViewListener;

public class ObservableScrollView extends ScrollView {  
	private static final int LEN = 0xc8;
	private static final int DURATION = 500;
	private static final int MAX_DY = 200;
	private Scroller mScroller;
	TouchTool tool;
	int left, top;
	float startX, startY , currentX, currentY;
	int imageViewH;
	int rootW, rootH;
	ViewPager viewPager;
	boolean scrollerType;

	public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public ObservableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new Scroller(context);
	}

	public ObservableScrollView(Context context) {
		super(context);

	}

	public void setViewPager(ViewPager viewPager) {
		this.viewPager = viewPager;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if (!mScroller.isFinished()) {
			return super.onTouchEvent(event);
		}
		currentX = event.getX();
		currentY = event.getY();
		viewPager.getTop();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			left = viewPager.getLeft();
			top = viewPager.getBottom();
			rootW = getWidth();
			rootH = getHeight();
			imageViewH = viewPager.getHeight();
			startX = currentX;
			startY = currentY;
			tool = new TouchTool(viewPager.getLeft(), viewPager.getBottom(),
					viewPager.getLeft(), viewPager.getBottom() + LEN);
			break;
		case MotionEvent.ACTION_MOVE:
			if (viewPager.isShown() && viewPager.getTop() >= 0) {
				if (tool != null) {
					int t = tool.getScrollY(currentY - startY);
					if (t >= top && t <= viewPager.getBottom() + LEN) {
						android.view.ViewGroup.LayoutParams params = viewPager
								.getLayoutParams();
						params.height = t;
						viewPager.setLayoutParams(params);
					}
				}
				scrollerType = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			scrollerType = true;
			mScroller.startScroll(viewPager.getLeft(), viewPager.getBottom(),
					0 - viewPager.getLeft(),
					imageViewH -viewPager.getBottom(), DURATION);
			invalidate();
			break;
		}

		return super.dispatchTouchEvent(event);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();
			viewPager.layout(0, 0, x + viewPager.getWidth(), y);
			invalidate();
			if (!mScroller.isFinished() && scrollerType && y > MAX_DY) {
				android.view.ViewGroup.LayoutParams params = viewPager
						.getLayoutParams();
				params.height = y;
				viewPager.setLayoutParams(params);
			}
		}
	}

	public class TouchTool {

		private int startX, startY;

		public TouchTool(int startX, int startY, int endX, int endY) {
			super();
			this.startX = startX;
			this.startY = startY;
		}

		public int getScrollX(float dx) {
			int xx = (int) (startX + dx / 2.5F);
			return xx;
		}

		public int getScrollY(float dy) {
			int yy = (int) (startY + dy / 2.5F);
			return yy;
		}
	}
	  
    private ScrollViewListener scrollViewListener = null;  
  
  
    public void setScrollViewListener(ScrollViewListener scrollViewListener) {  
        this.scrollViewListener = scrollViewListener;  
    }  
  
  
}  
