package com.lequan.n1.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.lequan.n1.activity.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RefreshListView extends ListView {

	public static final int PULL_DOWN = 1;// 下拉刷新
	public static final int RELEASE = 2;// 松开刷新
	public static final int REFRESHING = 3;// 正在刷新
	private int current_state = PULL_DOWN;// 当前状态

	private View mFoot;// 底部加载更多
	private int mFoot_Height;// 底部控件的高度
	private LinearLayout mHeaderContainer;// 顶部容器
	private View mRefresh_head;// 顶部刷新控件
	private int mRerfresh_Height;// 顶部刷新控件的高度
	private float mDownY; // 按下的位置
	private View mLunboView;// 轮播图
	private int mListViewY;// listview在屏幕中的位置Y轴

	private TextView head_state;

	private TextView head_time;

	private ImageView head_arrow;

	private ProgressBar head_pb;

	private RotateAnimation mUp_arrow;
	private RotateAnimation mDown_arrow;
	private boolean enableRefreshHeader;// 是否需要下拉刷新头
	private boolean enableLoadMore;//是否需要加载更多

	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RefreshListView(Context context) {
		this(context, null);
	}

	private void initView() {
		initHead();
		initFoot();
		initAnimation();
		initEvent();
	}

	/**
	 * 是否需要加载更多数据
	 * @param enableLoadMore
	 */
	public void setEnableLoadMore(boolean enableLoadMore) {
		this.enableLoadMore = enableLoadMore;
	}
	
	private void initEvent() {
		this.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//不需要加载更多
				if(!enableLoadMore){
					return;
				}
				// 如果是最后一条数据
				if (!mIsLoadMore && getLastVisiblePosition() == getAdapter().getCount() - 1) {
					// 标记为加载更多数据
					mIsLoadMore = true;
					// 显示加载更多
					mFoot.setPadding(0, 0, 0, 0);
					setSelection(getAdapter().getCount() - 1);
					// 加载更多数据
					if (listener != null) {
						listener.loadMore();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		});
	}

	private void initFoot() {
		mFoot = View.inflate(getContext(), R.layout.referesh_list_view_foot, null);

		// 测量该控件大小
		mFoot.measure(0, 0);
		mFoot_Height = mFoot.getMeasuredHeight();

		// 隐藏
		mFoot.setPadding(0, -mFoot_Height, 0, 0);
		// 添加加载更多
		this.addFooterView(mFoot);
	}

	private void initHead() {
		//头部容器
		mHeaderContainer = (LinearLayout) View.inflate(getContext(), R.layout.referesh_list_view_head_container, null);
		
		//获取下拉刷新头
		mRefresh_head = mHeaderContainer.findViewById(R.id.ll_refresh_list_view_header);
		this.head_arrow = (ImageView) mRefresh_head.findViewById(R.id.iv_refresh_list_view_head_arrow);
		this.head_pb = (ProgressBar) mRefresh_head.findViewById(R.id.pb_refresh_list_view_head);
		this.head_state = (TextView) mRefresh_head.findViewById(R.id.tv_refresh_list_view_head_state);
		this.head_time = (TextView) mRefresh_head.findViewById(R.id.tv_refresh_list_view_head_time);
		
		// 计算刷新控件的高度
		mRefresh_head.measure(0, 0);
		mRerfresh_Height = mRefresh_head.getMeasuredHeight();
		// 隐藏
		mRefresh_head.setPadding(0, -mRerfresh_Height, 0, 0);

		// 添加
		this.addHeaderView(mHeaderContainer);
	}

	/**
	 * 设置是否需要下拉刷新头
	 * 
	 * @param enableRefreshHeader
	 */
	public void setEnableRefreshHeader(boolean enableRefreshHeader) {
		this.enableRefreshHeader = enableRefreshHeader;
	}

	@Override
	public void addHeaderView(View view) {
		// 如果需要下拉刷新头，则讲控件添加到头布局中否则添加到listView中
		if (enableRefreshHeader) {
			this.mLunboView = view;
			this.mHeaderContainer.addView(view);
		} else {
			super.addHeaderView(view);
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			//不需要下拉刷新头
			if (!enableRefreshHeader) {
				break;
			}
			// 1、如果轮播图显示完后才显示下拉刷新
			if (lunboIsShowOver()) {
				if (mDownY == -1) {
					mDownY = ev.getY();
				}
				float moveY = ev.getY();
				// 计算滑动的相对位置
				float dy = moveY - mDownY;
				// 向下滑动，且为第一条数据并且不是正在刷新
				if (dy > 0 && this.getFirstVisiblePosition() == 0&&current_state!=REFRESHING) {
					// 计算刷新头的相对显示的位置
					int scrollDis = (int) (-mRerfresh_Height + dy);
					// 刷新头还没显示完
					if (scrollDis < 0 && current_state != PULL_DOWN) {
						this.current_state = PULL_DOWN;
						refreshState();
					} else if (scrollDis >= 0 && current_state != RELEASE) {
						this.current_state = RELEASE;
						refreshState();
					}
					this.mRefresh_head.setPadding(0, scrollDis, 0, 0);
					return true;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			mDownY = -1;
			// 如果不为正在刷新
			if (current_state == PULL_DOWN) {
				this.mRefresh_head.setPadding(0, -mRerfresh_Height, 0, 0);
			} else if (current_state == RELEASE) {
				this.mRefresh_head.setPadding(0, 0, 0, 0);
				current_state = REFRESHING;
				refreshState();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	private void initAnimation() {
		mUp_arrow = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mUp_arrow.setDuration(500);
		mUp_arrow.setFillAfter(true);

		mDown_arrow = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mDown_arrow.setDuration(500);
		mDown_arrow.setFillAfter(true);
	}

	private void refreshState() {
		switch (current_state) {
		case PULL_DOWN:
			this.head_state.setText("下拉刷新");
			this.head_arrow.startAnimation(mDown_arrow);
			break;
		case RELEASE:
			this.head_state.setText("松开刷新");
			this.head_arrow.startAnimation(mUp_arrow);
			break;
		case REFRESHING:
			this.head_state.setText("正在刷新");
			this.head_arrow.clearAnimation();
			this.head_arrow.setVisibility(View.INVISIBLE);
			this.head_pb.setVisibility(View.VISIBLE);
			// 刷新数据
			if (listener != null) {
				this.listener.refreshData();
			}
			break;
		}
	}

	// 轮播图是否显示完全：listview中屏幕中的位置为固定
	private boolean lunboIsShowOver() {
		//如果没有设置轮播图
		if(this.mLunboView==null){
			return true;
		}
		int[] location = new int[2];
		// 获取listview的位置
		getLocationOnScreen(location);
		if (this.mListViewY == 0) {
			mListViewY = location[1];
		}
		// 计算轮播图在屏幕中的位置
		this.mLunboView.getLocationOnScreen(location);
		int lunboY = location[1];
		if (lunboY < mListViewY) {
			return false;
		}
		return true;
	}

	private OnRefreshListener listener;
	private boolean mIsLoadMore;// 加载更过数据

	/**
	 * 设置刷新数据监听器
	 * @param listener
	 */
	public void setOnRefreshListener(OnRefreshListener listener) {
		this.listener = listener;
	}

	public interface OnRefreshListener {
		/**
		 * 刷新数据
		 */
		public void refreshData();

		/**
		 * 加载更多
		 */
		public void loadMore();
	}

	/**
	 * 刷新状态
	 */
	public void refreshFinish() {
		if (mIsLoadMore) {
			this.mIsLoadMore = false;
			this.mFoot.setPadding(0, -mFoot_Height, 0, 0);
		} else {
			this.current_state = PULL_DOWN;
			this.head_pb.setVisibility(View.INVISIBLE);
			this.head_arrow.setVisibility(View.VISIBLE);
			this.head_state.setText("下拉刷新");
			this.head_time.setText(getCurrentTime());
			this.mRefresh_head.setPadding(0, -mRerfresh_Height, 0, 0);
		}
	}

	private CharSequence getCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}

}
