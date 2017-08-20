package com.lequan.n1.activity;

import com.lequan.n1.util.Constants;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.UiUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class GuideActivity extends Activity {

	@ViewInject(R.id.vp_guide)
	private ViewPager vp_guide;

	@ViewInject(R.id.ll_guide_points)
	private LinearLayout ll_guide_points;

	@ViewInject(R.id.btn_guide_set)
	private Button btn_guide_set;

	private int[] mImages;

	private SpUtils mSpUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initView();
		initData();
		initEvent();
	}

	private void initView() {
		setContentView(R.layout.activity_guide);
		ViewUtils.inject(this);
	}

	private void initData() {
		mSpUtils = SpUtils.getInstance(this);
		mImages = new int[] { R.drawable.guide1, R.drawable.guide2, R.drawable.guide3 };

		// 初始化点
		for (int i = 0; i < mImages.length; i++) {
			View view = new View(this);
			view.setBackgroundResource(R.drawable.guide__point_bg);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UiUtils.dip2px(8), UiUtils.dip2px(8));
			params.leftMargin = UiUtils.dip2px(5);
			view.setLayoutParams(params);
			if (i!= 0) {
				view.setEnabled(false);
			}
			ll_guide_points.addView(view);
		}
		this.vp_guide.setAdapter(new GuideApdater());
	}

	private void initEvent() {
		this.btn_guide_set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 记录设置
				mSpUtils.putBoolean(Constants.IS_SETTED, true);
				// 到达主界面
				Intent intent = new Intent(GuideActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
		this.vp_guide.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// 如果不是最后一张
				if (position != vp_guide.getAdapter().getCount() - 1) {
					btn_guide_set.setVisibility(View.GONE);
				} else {
					btn_guide_set.setVisibility(View.VISIBLE);
				}
				// 切换点的显示
				for (int i = 0; i < mImages.length; i++) {
					ll_guide_points.getChildAt(i).setEnabled(i == position);
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	private final class GuideApdater extends PagerAdapter {

		@Override
		public int getCount() {
			if (mImages != null) {
				return mImages.length;
			}
			return 0;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView iv = new ImageView(GuideActivity.this);
			iv.setScaleType(ScaleType.FIT_XY);
			iv.setImageResource(mImages[position]);

			container.addView(iv);
			return iv;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}

}
