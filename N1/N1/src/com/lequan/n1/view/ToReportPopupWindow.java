package com.lequan.n1.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;

import com.lequan.n1.activity.R;

public class ToReportPopupWindow extends PopupWindow {
	public ToReportPopupWindow(Context context, OnClickListener listener) {
		//举报的popupwindow1
		View contentView = View.inflate(context, R.layout.pic_toreport_popupwindow, null);
		this.setContentView(contentView);
		this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		// 点击popupwindows范围以外的地方,让其消失
		this.setBackgroundDrawable(new ColorDrawable());
		//this.setOutsideTouchable(true);
		// 添加动画
		this.setAnimationStyle(R.style.PopupAnimation);
		Button pic_toReport = (Button) contentView.findViewById(R.id.pic_toReport);
		Button pic_cancel = (Button) contentView.findViewById(R.id.pic_cancel);
		pic_toReport.setOnClickListener(listener);
		pic_cancel.setOnClickListener(listener);

	}
}