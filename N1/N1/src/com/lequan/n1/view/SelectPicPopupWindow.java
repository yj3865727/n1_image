package com.lequan.n1.view;

import com.lequan.n1.activity.R;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

public class SelectPicPopupWindow extends PopupWindow {
	public SelectPicPopupWindow(Context context, OnClickListener listener) {
		View contentView = View.inflate(context, R.layout.layout_pupup, null);
		this.setContentView(contentView);
		this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		// 点击popupwindows范围以外的地方,让其消失
		this.setBackgroundDrawable(new ColorDrawable());
		this.setOutsideTouchable(true);
		// 添加动画
		this.setAnimationStyle(R.style.PopupAnimation);

		Button btnTakePhoto = (Button) contentView.findViewById(R.id.btn_take_photo);
		Button btnPickPhoto = (Button) contentView.findViewById(R.id.btn_pick_photo);
		Button btnCancel = (Button) contentView.findViewById(R.id.btn_cancel);

		btnTakePhoto.setOnClickListener(listener);
		btnPickPhoto.setOnClickListener(listener);
		btnCancel.setOnClickListener(listener);
	}
}
