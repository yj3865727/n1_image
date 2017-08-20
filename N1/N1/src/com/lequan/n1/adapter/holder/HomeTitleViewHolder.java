package com.lequan.n1.adapter.holder;

import com.lequan.n1.activity.R;
import com.lequan.n1.entity.HomeEntity;
import com.lequan.n1.util.UiUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeTitleViewHolder extends BaseViewHolder<HomeEntity> {

	@ViewInject(R.id.tv_home_item_title)
	private TextView tv_home_item_title;

	@ViewInject(R.id.iv_home_item_title_bomb)
	private ImageView iv_home_item_title_bomb;


	@Override
	protected View initView() {
		View view = View.inflate(UiUtils.getContext(), R.layout.rl_home_lv_item_title, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	protected void refreshUi(HomeEntity t, int position) {
		if (position == 0) {
			tv_home_item_title.setText("热门推荐");
			iv_home_item_title_bomb.setVisibility(View.VISIBLE);
		} else if (position == 2) {
			tv_home_item_title.setText("主题悬赏");
			iv_home_item_title_bomb.setVisibility(View.INVISIBLE);
		}
	}

}
