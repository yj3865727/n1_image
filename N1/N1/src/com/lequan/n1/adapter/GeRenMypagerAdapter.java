package com.lequan.n1.adapter;

import java.util.List;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class GeRenMypagerAdapter extends PagerAdapter{

	private List<View> viewlist;

	public GeRenMypagerAdapter(List<View> viewlist) {
		this.viewlist = viewlist;
	}

	@Override
	public int getCount() {
		return viewlist.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
		container.removeView(viewlist.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(viewlist.get(position));
		return viewlist.get(position);
		
	}
}
