package com.lequan.n1.adapter.holder;

import com.lequan.n1.activity.R;
import com.lequan.n1.entity.HomeEntity;
import com.lequan.n1.entity.HomeEntity.Data.Home_Data_Data.Home_Data_Data_Row;
import com.lequan.n1.util.BitmapUtil;
import com.lequan.n1.util.UiUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TopicRewardViewHolder extends BaseViewHolder<HomeEntity> {

	@ViewInject(R.id.tv_home_lv_item_contributePersonNum)
	private TextView tv_home_lv_item_contributePersonNum;

	@ViewInject(R.id.tv_home_lv_item_contributePostNum)
	private TextView tv_home_lv_item_contributePostNum;

	@ViewInject(R.id.tv_home_lv_item_rewardAttachSubject)
	private TextView tv_home_lv_item_rewardAttachSubject;

	@ViewInject(R.id.tv_home_lv_item_rewardEndTime)
	private TextView tv_home_lv_item_rewardEndTime;

	@ViewInject(R.id.tv_home_lv_item_rewardMoney)
	private TextView tv_home_lv_item_rewardMoney;

	@ViewInject(R.id.tv_home_lv_item_rewardSubject)
	private TextView tv_home_lv_item_rewardSubject;

	@ViewInject(R.id.iv_home_lv_item_backgroundUrl)
	private ImageView iv_home_lv_item_backgroundUrl;

	@Override
	protected View initView() {
		View view = View.inflate(UiUtils.getContext(), R.layout.rl_home_lv_normal, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	protected void refreshUi(HomeEntity t, int position) {
		position = position - 3;
		Home_Data_Data_Row data = t.data.xsPage.rows.get(position);

		BitmapUtil.display(iv_home_lv_item_backgroundUrl, data.backgroundUrl);

		tv_home_lv_item_contributePersonNum.setText(data.contributePersonNum + "");
		tv_home_lv_item_contributePostNum.setText(data.contributePostNum + "");
		tv_home_lv_item_rewardAttachSubject.setText(data.rewardAttachSubject);
		TextPaint tp1 = tv_home_lv_item_rewardAttachSubject.getPaint(); 
		tp1.setFakeBoldText(true);
		tv_home_lv_item_rewardMoney.setText("￥" + data.rewardMoney);
		tv_home_lv_item_rewardSubject.setText(data.rewardSubject);
		TextPaint tp = tv_home_lv_item_rewardSubject.getPaint(); 
		tp.setFakeBoldText(true);
		tv_home_lv_item_rewardEndTime.setText(UiUtils.calculateEndTime(data.rewardEndTime)+"天");
	}
}
