package com.lequan.n1.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.lequan.n1.util.ValidateUtils;
import com.lequan.n1.view.DragImageView;

public class PublishWorkShowPhotos extends BaseActivity {

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		
	}

	/*private ViewPager mView;
	private ActionBar mActionBar;
	private PhotosPagerAdaper mAdaper;
	private List<String> mList;

	@Override
	protected void initView() {
		mView = new ViewPager(this);
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		mView.setLayoutParams(params);
		mView.setOffscreenPageLimit(3);
		setContentView(mView);
	}

	@Override
	protected void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			int index = intent.getIntExtra("index", -1);
			String[] extraData = intent.getStringArrayExtra("photos");
			if (ValidateUtils.isValidate(extraData)) {
				// 设置actionbar的数据--->0/size
				// 解析图片数据
				this.mList=new ArrayList<String>(Arrays.asList(extraData));
				//设置图片
				mActionBar.setTitle(index+"of"+mList.size());
				// 初始化viewpager中的数据
				mAdaper = new PhotosPagerAdaper();
				this.mView.setAdapter(mAdaper);
				this.mView.setCurrentItem(index-1);
			}
		}
	}

	@Override
	protected void initActionBar() {
		mActionBar = getSupportActionBar();
		if (mActionBar != null) {
			mActionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	protected void initEvent() {
		this.mView.addOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				changeTitle(arg0+1);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "删除");
		menu.findItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			int currentItem = mView.getCurrentItem();
			//移除数据 TODO 此处有bug 处理没有数据是的问题
			mList.remove(currentItem);
			if(mList.size()>0){
				mAdaper.notifyDataSetChanged();
			}else{
				returnDealData();
			}
			break;
		case android.R.id.home:
			returnDealData();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void returnDealData() {
		Intent data=new Intent();
		data.putExtra("dealResult", this.mList.toArray(new String[mList.size()]));
		setResult(PublishWorkActivity.PHOTOS_DETAIL, data);
		finish();
	}

	private final class PhotosPagerAdaper extends PagerAdapter {

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
			object=null;
		}

		@Override
		public int getCount() {
			if (mList != null) {
				return mList.size();
			}
			return 0;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			DragImageView imageView = new DragImageView(PublishWorkShowPhotos.this);
			Bitmap bitmap = BitmapFactory.decodeFile(mList.get(position));
			if (bitmap != null){
				imageView.setImageBitmap(bitmap);
			}
			container.addView(imageView);
			return imageView;
		}
		
		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
	
	private void changeTitle(int index){
		if(mActionBar!=null){
			mActionBar.setTitle(index+"of"+mList.size());
		}
	}*/

}
