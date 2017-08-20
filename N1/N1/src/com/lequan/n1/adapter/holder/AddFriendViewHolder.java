package com.lequan.n1.adapter.holder;

import org.json.JSONObject;

import com.lequan.n1.activity.R;
import com.lequan.n1.util.BitmapUtil;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.util.ValidateUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AddFriendViewHolder extends BaseViewHolder<JSONObject> {

	@ViewInject(R.id.iv_add_friend_head)
	private ImageView iv_add_friend_head;

	@ViewInject(R.id.iv_add_friend_sex)
	private ImageView iv_add_friend_sex;

	@ViewInject(R.id.tv_add_friend_username)
	private TextView userName;

	@ViewInject(R.id.tv_add_friend_level)
	private TextView level;

	@Override
	protected View initView() {
		View view = View.inflate(UiUtils.getContext(), R.layout.ll_add_friend, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	protected void refreshUi(JSONObject t, int position) {
		this.userName.setText(t.optString("loginSn"));
		this.level.setText("Lv" + t.optJSONObject("gradeUser").optInt("grade"));
		// 设置头像
		this.iv_add_friend_head.setImageResource(R.drawable.logo_placeholder2x);
		BitmapUtil.display(this.iv_add_friend_head, t.optString("headphoto"));
		// 设置性别
		String sex = t.optString("sex");
		if (ValidateUtils.isValidate(sex)) {
			if ("男".equals(sex)) {
				this.iv_add_friend_sex.setImageResource(R.drawable.friend_nan);
			} else if ("女".equals(sex)) {
				this.iv_add_friend_sex.setImageResource(R.drawable.friend_nv);
			}
		} else {
			this.iv_add_friend_head.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
		}
	}

}
