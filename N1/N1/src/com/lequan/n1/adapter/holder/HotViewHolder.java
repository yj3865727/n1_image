package com.lequan.n1.adapter.holder;

import java.util.ArrayList;
import java.util.List;

import com.lequan.n1.activity.HomeDetailsActivity;
import com.lequan.n1.activity.R;
import com.lequan.n1.entity.HomeEntity;
import com.lequan.n1.entity.HomeEntity.Data.Home_Data_Data.Home_Data_Data_Row;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.view.ImageWithTextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;

public class HotViewHolder extends BaseViewHolder<HomeEntity>{

	@ViewInject(R.id.itv_home_lv_item_1)
	private ImageWithTextView itv_home_lv_item_1;

	@ViewInject(R.id.itv_home_lv_item_2)
	private ImageWithTextView itv_home_lv_item_2;

	@ViewInject(R.id.itv_home_lv_item_3)
	private ImageWithTextView itv_home_lv_item_3;

	@ViewInject(R.id.itv_home_lv_item_4)
	private ImageWithTextView itv_home_lv_item_4;

	@ViewInject(R.id.itv_home_lv_item_5)
	private ImageWithTextView itv_home_lv_item_5;

	@ViewInject(R.id.itv_home_lv_item_6)
	private ImageWithTextView itv_home_lv_item_6;

	private List<ImageWithTextView> views;

	@Override
	protected View initView() {
		View view = View.inflate(UiUtils.getContext(), R.layout.rl_home_hot_recommend, null);
		// 获取屏幕宽度
		int width = UiUtils.getResource().getDisplayMetrics().widthPixels;
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(width, width);
		view.setLayoutParams(params);
		ViewUtils.inject(this, view);

		// 记录所有的view
		views = new ArrayList<ImageWithTextView>();
		views.add(itv_home_lv_item_1);
		views.add(itv_home_lv_item_2);
		views.add(itv_home_lv_item_3);
		views.add(itv_home_lv_item_4);
		views.add(itv_home_lv_item_5);
		views.add(itv_home_lv_item_6);
		return view;
	}

	@Override
	protected void refreshUi(HomeEntity t, int position) {
		final List<Home_Data_Data_Row> datas = t.data.whPage.rows;

		for (int i = 0; i < datas.size(); i++) {
			final int tempI=i;
			views.get(i).setDataAndRefreshUi(datas.get(i));

			//图片跳转的事件
			views.get(i).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					Intent intent = new Intent(UiUtils.getContext(),HomeDetailsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("themeid", datas.get(tempI).id);
					intent.putExtras(bundle);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					UiUtils.getContext().startActivity(intent);

				}
			});
		}
	}
}
