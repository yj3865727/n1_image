package com.lequan.n1.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.lequan.n1.util.BitmapUtil;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.view.DragImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class CheckImageActivity extends BaseActivity{
	@ViewInject(R.id.tv_checkimage_activity)
	private TextView tv_checkimage_activity;
	@ViewInject(R.id.iv_checkimage_activity)
	private ImageView iv_checkimage_activity;
	@ViewInject(R.id.vp_checkimage_activity)
	private ViewPager vp_checkimage_activity;
	
	private Intent intent;
	private Bundle bundle;
	private ArrayList<String> arrayList;
	private int id;
	
	@Override
	protected void initView() {
		setContentView(R.layout.checkimage_activity);
		ViewUtils.inject(this);
	}
	
	
	@Override
	protected void initData() {
		intent = getIntent();
		bundle = intent.getBundleExtra("key");
		arrayList = bundle.getStringArrayList("imageUrl");
		id = bundle.getInt("imageId");
		tv_checkimage_activity.setText(id+"of"+arrayList.size());
		vp_checkimage_activity.setAdapter(new ImageAdapter(arrayList));
		vp_checkimage_activity.setCurrentItem(id-1);
	}
	
	@Override
	protected void initEvent() {
		iv_checkimage_activity.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				finish();		
			}
		});
		
		vp_checkimage_activity.addOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {	
				tv_checkimage_activity.setText((arg0+1) + "of" + arrayList.size());
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {				
			}
		});
	}
	
	//设置图片的适配器
	public class ImageAdapter extends PagerAdapter{
		
		private List<String> list = new ArrayList<String>();
		public ImageAdapter(List<String> list) {
			this.list = list;
		}
		@Override
		public int getCount() {
			if (list != null) {
				return list.size();
			}
			return 0;
		}		
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			DragImageView iv = new DragImageView(UiUtils.getContext());
			iv.setImageResource(R.drawable.backgroudimage2x);
			BitmapUtil.display(iv, list.get(position));
			container.addView(iv);
			return iv;
		}
		
	}
}
