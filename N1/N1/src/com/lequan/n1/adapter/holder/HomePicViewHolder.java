package com.lequan.n1.adapter.holder;

import com.lequan.n1.activity.HomeTitleActivity;
import com.lequan.n1.activity.HomeTitleActivity2;
import com.lequan.n1.activity.R;
import com.lequan.n1.entity.HomeEntity;
import com.lequan.n1.util.UiUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class HomePicViewHolder extends BaseViewHolder<HomeEntity> {

	@ViewInject(R.id.vp_home_pic)
	private ViewPager pager;

	@ViewInject(R.id.item_home_picture_container_indicator)
	private LinearLayout indicator;
	
	private long time1,time2;

	@Override
	protected View initView() {
		View view = View.inflate(UiUtils.getContext(), R.layout.rl_home_pic, null);
		ViewUtils.inject(this, view);
		//设置数据
		mAdapter = new HomePictureAdapter();
		this.pager.setAdapter(mAdapter);
		// 初始化事件监听
		initEvent();
		return view;
	}

	private void initEvent() {
		if (this.pager != null) {
			pager.addOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int position) {
					refreshPoints(position);
				}

				@Override
				public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

				}

				@Override
				public void onPageScrollStateChanged(int state) {

				}
			});
		}
	}

	@Override
	protected void refreshUi(HomeEntity t, int position) {
		mDatas = new int[] { R.drawable.happy, R.drawable.takephoto };
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
		//先移除点
		indicator.removeAllViews();
		// 点
		for (int i = 0; i < mDatas.length; i++) {
			View point = new View(UiUtils.getContext());
			point.setBackgroundResource(R.drawable.guide__point_bg);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UiUtils.dip2px(8), UiUtils.dip2px(8));
			params.bottomMargin = UiUtils.dip2px(5);
			if (i != 0) {
				params.leftMargin = UiUtils.dip2px(5);
			}
			point.setLayoutParams(params);

			indicator.addView(point);
		}
		refreshPoints(0);
		// 开始调度
		if(mPictureChangeTask==null){
			mPictureChangeTask = new PictureChangeTask();
		}
		mPictureChangeTask.startTask();
	}

	private void refreshPoints(int index) {
		for (int i = 0; i < mDatas.length; i++) {
			indicator.getChildAt(i).setEnabled(i == index);
		}
	}

	private PictureChangeTask mPictureChangeTask;
	private int[] mDatas;

	private HomePictureAdapter mAdapter;

	// 轮播图切换任务
	public final class PictureChangeTask extends Handler implements Runnable {

		public void startTask() {
			stopTask();
			postDelayed(this, 3000);
		}

		public void stopTask() {
			removeCallbacks(this);
		}

		@Override
		public void run() {
			pager.setCurrentItem((pager.getCurrentItem() + 1) % pager.getAdapter().getCount());
			postDelayed(this, 3000);
		}

	}

	private final class HomePictureAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			if (mDatas != null) {
				return mDatas.length;
			}
			return 0;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			ImageView iv = new ImageView(UiUtils.getContext());
			// 匹配xy
			iv.setScaleType(ScaleType.FIT_XY);
			iv.setImageResource(mDatas[position]);
			iv.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						mPictureChangeTask.stopTask();
						time1 = System.currentTimeMillis();
						break;
					case MotionEvent.ACTION_UP:
						mPictureChangeTask.startTask();
						time2 = System.currentTimeMillis();
						if((time2 - time1) < 2000){
							changeView(position);
						}
						break;
					case MotionEvent.ACTION_CANCEL:
						mPictureChangeTask.startTask();
						break;
					}
					return true;
				}
			});
			container.addView(iv);
			return iv;
		}
		
		//根据不同的view显示不同activity
		public void changeView(int position){
			if(position == 0){
				Intent intent = new Intent(UiUtils.getContext(),HomeTitleActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				UiUtils.getContext().startActivity(intent);
			}else if(position == 1){
				Intent intent = new Intent(UiUtils.getContext(),HomeTitleActivity2.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				UiUtils.getContext().startActivity(intent);
			}
		}
	}		
}
