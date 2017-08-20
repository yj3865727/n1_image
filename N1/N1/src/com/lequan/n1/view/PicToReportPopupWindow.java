package com.lequan.n1.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;

import com.lequan.n1.activity.R;

public class PicToReportPopupWindow extends PopupWindow {
	public PicToReportPopupWindow(Context context, OnClickListener listener) {
		View contentView = View.inflate(context, R.layout.toreport_popupwindow, null);
		this.setContentView(contentView);
		this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		// 点击popupwindows范围以外的地方,让其消失
		this.setBackgroundDrawable(new ColorDrawable());
		//this.setOutsideTouchable(true);
		// 添加动画
		//this.setAnimationStyle(R.style.PicPopupAnimation);
		Button toReport1 = (Button) contentView.findViewById(R.id.toReport1);
		Button toReport2 = (Button) contentView.findViewById(R.id.toReport2);
		Button toReport3 = (Button) contentView.findViewById(R.id.toReport3);
		Button toReport4 = (Button) contentView.findViewById(R.id.toReport4);
		Button toReport_cancel = (Button) contentView.findViewById(R.id.toReport_cancel);

		toReport1.setOnClickListener(listener);
		toReport2.setOnClickListener(listener);
		toReport3.setOnClickListener(listener);
		toReport4.setOnClickListener(listener);
		toReport_cancel.setOnClickListener(listener);
	}
}