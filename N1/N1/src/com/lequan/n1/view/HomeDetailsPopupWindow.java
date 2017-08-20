package com.lequan.n1.view;

import java.util.ArrayList;
import java.util.List;

import com.lequan.n1.activity.CheckImageActivity;
import com.lequan.n1.activity.R;
import com.lequan.n1.entity.HomeDetailEntity;
import com.lequan.n1.util.BitmapUtil;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.util.ValidateUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class HomeDetailsPopupWindow extends PopupWindow implements OnClickListener {
	@ViewInject(R.id.iv_popup_exti)
	private ImageView exti;
	@ViewInject(R.id.iv_popupwindow_image1)
	private ImageView popupImage1;
	@ViewInject(R.id.iv_popupwindow_image2)
	private ImageView popupImage2;
	@ViewInject(R.id.iv_popupwindow_image3)
	private ImageView popupImage3;

	@ViewInject(R.id.tv_popup_taskname)
	private TextView tv_popup_taskname;
	@ViewInject(R.id.tv_popup_money)
	private TextView tv_popup_money;
	@ViewInject(R.id.tv_popup_times)
	private TextView tv_popup_times;
	@ViewInject(R.id.tv_popup_details)
	private TextView tv_popup_details;

	private HomeDetailEntity homeDetailEntity;
	private ArrayList<String> list;
	private Intent intent;
	private Bundle bundle;

	public HomeDetailsPopupWindow(Context context, HomeDetailEntity homeDetailEntity) {
		View view = View.inflate(context, R.layout.activity_popupwindow, null);
		ViewUtils.inject(this, view);
		this.homeDetailEntity = homeDetailEntity;
		this.setContentView(view);
		init();
		// 设置进出厂动画
		// this.setAnimationStyle();
		// 设置背景色
		this.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		// 设置点击外围是否关闭
		this.setOutsideTouchable(false);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);

		list = setImage(homeDetailEntity);
		exti.setOnClickListener(this);
		popupImage1.setOnClickListener(this);
		popupImage2.setOnClickListener(this);
		popupImage3.setOnClickListener(this);
	}

	public void init() {
		tv_popup_details.setText(homeDetailEntity.data.rewardInfo.rewardSummary);
		tv_popup_money.setText("￥" + homeDetailEntity.data.rewardInfo.rewardMoney);
		tv_popup_times.setText(UiUtils.calculateEndTime(homeDetailEntity.data.rewardInfo.rewardEndTime) + "天");
		tv_popup_taskname.setText(homeDetailEntity.data.rewardInfo.rewardAttachSubject);
		
		if (ValidateUtils.isValidate(homeDetailEntity.data.rewardInfo.pic1Url)) {
			BitmapUtil.display(popupImage1, homeDetailEntity.data.rewardInfo.pic1Url);
		}
		if (ValidateUtils.isValidate(homeDetailEntity.data.rewardInfo.pic1Url)) {
			BitmapUtil.display(popupImage2, homeDetailEntity.data.rewardInfo.pic2Url);
		}
		if (ValidateUtils.isValidate(homeDetailEntity.data.rewardInfo.pic1Url)) {
			BitmapUtil.display(popupImage3, homeDetailEntity.data.rewardInfo.pic3Url);
		}		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_popup_exti:
			dismiss();
			break;
		case R.id.iv_popupwindow_image1:
			intent = new Intent(UiUtils.getContext(),CheckImageActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			bundle = new Bundle();
			bundle.putStringArrayList("imageUrl", list);
			bundle.putInt("imageId",1);
			intent.putExtra("key",bundle);
			UiUtils.getContext().startActivity(intent);
			break;
		case R.id.iv_popupwindow_image2:
			intent = new Intent(UiUtils.getContext(),CheckImageActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			bundle = new Bundle();
			bundle.putStringArrayList("imageUrl", list);
			bundle.putInt("imageId",2);
			intent.putExtra("key",bundle);
			UiUtils.getContext().startActivity(intent);
			break;
		case R.id.iv_popupwindow_image3:
			intent = new Intent(UiUtils.getContext(),CheckImageActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			bundle = new Bundle();
			bundle.putStringArrayList("imageUrl", list);
			bundle.putInt("imageId",3);
			intent.putExtra("key",bundle);
			UiUtils.getContext().startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public void showAsDropDown(View anchor) {
		if (!this.isShowing()) {
			this.showAsDropDown(anchor, anchor.getLayoutParams().width, anchor.getLayoutParams().height);
		} else {
			this.dismiss();
		}
	}
	
	//获得图片的List集合
	public ArrayList<String> setImage(HomeDetailEntity homeDetailEntity){
		ArrayList<String> list = new ArrayList<String>();
		if (ValidateUtils.isValidate(homeDetailEntity.data.rewardInfo.pic1Url)) {
			list.add(homeDetailEntity.data.rewardInfo.pic1Url);
		}
		if (ValidateUtils.isValidate(homeDetailEntity.data.rewardInfo.pic2Url)) {
			list.add(homeDetailEntity.data.rewardInfo.pic2Url);
		}
		if (ValidateUtils.isValidate(homeDetailEntity.data.rewardInfo.pic3Url)) {
			list.add(homeDetailEntity.data.rewardInfo.pic3Url);
		}
		return list;
	}
}
