package com.lequan.n1.view;

import com.lequan.n1.activity.R;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

public class HeadPhotoPopupWindow extends PopupWindow {
	public HeadPhotoPopupWindow(Context context, OnClickListener listener) {
		View contentView = View.inflate(context, R.layout.personal_setting_headpopupwindow, null);
		this.setContentView(contentView);
		this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		// 点击popupwindows范围以外的地方,让其消失
		this.setBackgroundDrawable(new ColorDrawable());
		this.setOutsideTouchable(true);
		// 添加动画
		this.setAnimationStyle(R.style.PopupAnimation);

		Button btnTakePhoto = (Button) contentView.findViewById(R.id.set_takephoto);
		Button btnPickPhoto = (Button) contentView.findViewById(R.id.set_pickphoto);
		Button btnCancel = (Button) contentView.findViewById(R.id.head_cancel);

		btnTakePhoto.setOnClickListener(listener);
		btnPickPhoto.setOnClickListener(listener);
		btnCancel.setOnClickListener(listener);
	}
}
