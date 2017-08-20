package com.lequan.n1.adapter.holder;

import org.json.JSONObject;

import com.lequan.n1.activity.MessageNoticeDetailActivity;
import com.lequan.n1.activity.R;
import com.lequan.n1.util.BitmapUtil;
import com.lequan.n1.util.StringUtils;
import com.lequan.n1.util.UiUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageNoticeDetailViewHolder extends BaseViewHolder<JSONObject> {

	@ViewInject(R.id.iv_message_notice_detail_item_headPic)
	private ImageView headPic;

	@ViewInject(R.id.iv_message_notice_detail_item_photo)
	private ImageView photo;

	@ViewInject(R.id.tv_message_notice_detail_item_person)
	private TextView person;

	@ViewInject(R.id.tv_message_notice_detail_item_message)
	private TextView message;

	@ViewInject(R.id.tv_message_notice_detail_item_time)
	private TextView time;

	private int notice_type;// 显示提示的类型

	@Override
	protected View initView() {
		View view = View.inflate(UiUtils.getContext(), R.layout.lv_messsage_notice_detail_item, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	protected void refreshUi(JSONObject t, int position) {
		//绑定数据
		if (notice_type == MessageNoticeDetailActivity.TYPE_ATTENTION) {// 关注
			//设置用户头像
			String headphoto = t.optString("headphoto");
			String username = t.optString("username");
			this.person.setText(Html.fromHtml(String.format(UiUtils.getString(R.string.message_notice_detail_attention), username)));
			BitmapUtil.display(headPic, headphoto);
		} else {
			// 设置评论的时间
			String praiseTime = t.optString("time");
			this.time.setText(StringUtils.timeFormat(praiseTime));
			// 设置作品的信息
			JSONObject userWork = t.optJSONObject("userwork");
			String workMainPic = userWork.optString("workMainPic");
			BitmapUtil.display(this.photo, workMainPic);

			JSONObject appuser = t.optJSONObject("appuser");
			String username = appuser.optString("username");
			String head = appuser.optString("headphoto");
			BitmapUtil.display(headPic, head);
			if (notice_type == MessageNoticeDetailActivity.TYPE_COMMENT) {//评论
				//设置评论的数据
				String content=t.optString("content");
				this.message.setText(content);
				//设置操作的人
				this.person.setText(Html.fromHtml(String.format(UiUtils.getString(R.string.message_notice_detail_comment), username)));
			} else if (notice_type == MessageNoticeDetailActivity.TYPE_PRAISE) {// 点赞
				this.person.setText(Html.fromHtml(String.format(UiUtils.getString(R.string.message_notice_detail_praise), username)));
			}
		}
	}

	public void setNoticeType(int type) {
		this.notice_type = type;
		//控制试图显示
		//如果当前显示的不是关注，则不显示作品
		this.photo.setVisibility(type!=MessageNoticeDetailActivity.TYPE_ATTENTION?View.VISIBLE:View.INVISIBLE);
		this.time.setVisibility(type!=MessageNoticeDetailActivity.TYPE_ATTENTION?View.VISIBLE:View.INVISIBLE);
		//如果当前显示的是评论则显示评论
		this.message.setVisibility(type==MessageNoticeDetailActivity.TYPE_COMMENT?View.VISIBLE:View.INVISIBLE);
	}

}
