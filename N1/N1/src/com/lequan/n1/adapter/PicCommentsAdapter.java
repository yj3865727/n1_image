package com.lequan.n1.adapter;

import java.util.ArrayList;
import java.util.List;

import com.lequan.n1.activity.R;
import com.lequan.n1.adapter.holder.CommentsViewHolder;
import com.lequan.n1.entity.CommentsEntity.CommentData.CommentRows;
import com.lequan.n1.util.BitmapUtil;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.StringUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PicCommentsAdapter extends BaseAdapter {
	private List<CommentRows> list = new ArrayList<CommentRows>();
	private Context mContext;
	private int TYPE_COUNT = 2;
	private final int TYPE_1 = 1; // 直接评论作品
	private final int TYPE_2 = 2; // 回复评论
	private LayoutInflater inflater;

	public PicCommentsAdapter(List<CommentRows> list, Context context) {
		this.list = list;
		this.mContext = context;
	}

	public void setItemList(List<CommentRows> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		if (list.get(position).parentid == null) {
			return TYPE_1;
		} else
			return TYPE_2;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_COUNT;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CommentsViewHolder holder1 = null;
		CommentsViewHolder holder2 = null;
		int type = getItemViewType(position);
		if (convertView == null) {
			inflater = LayoutInflater.from(mContext);
			switch (type) {
			case TYPE_1:
				convertView = inflater.inflate(R.layout.item_pic_comment_lv1,
						parent, false);
				holder1 = new CommentsViewHolder();
				holder1.head = (ImageView) convertView
						.findViewById(R.id.pic_comment_head_img);
				holder1.userName = (TextView) convertView
						.findViewById(R.id.pic_comment_username_tv);
				holder1.content = (TextView) convertView
						.findViewById(R.id.pic_comment_content_tv);
				holder1.time = (TextView) convertView
						.findViewById(R.id.pic_comment_time_tv);
				convertView.setTag(holder1);
				break;

			case TYPE_2:
				convertView = inflater.inflate(R.layout.item_pic_comment_lv2,
						parent, false);
				holder2 = new CommentsViewHolder();
				holder2.head = (ImageView) convertView
						.findViewById(R.id.pic_comment_head_img);
				holder2.userName = (TextView) convertView
						.findViewById(R.id.pic_comment_username_tv);
				holder2.content = (TextView) convertView
						.findViewById(R.id.pic_comment_content_tv);
				holder2.time = (TextView) convertView
						.findViewById(R.id.pic_comment_time_tv);
				holder2.toUserName = (TextView) convertView
						.findViewById(R.id.pic_comment_replyername_tv);
				convertView.setTag(holder2);
				break;
			}
		} else {
			switch (type) {
			case TYPE_1:
				holder1 = (CommentsViewHolder) convertView.getTag();
				break;
			case TYPE_2:
				holder2 = (CommentsViewHolder) convertView.getTag();
				break;
			}
		}

		switch (type) {
		case TYPE_1:
			BitmapUtil.display(holder1.head,
					list.get(position).appuser.headphoto);
			holder1.userName.setText(list.get(position).appuser.loginSn);
			holder1.content.setText(list.get(position).content);
			holder1.time
					.setText(StringUtils.timeFormat(list.get(position).time));
			break;

		case TYPE_2:
			BitmapUtil.display(holder2.head,
					list.get(position).appuser.headphoto);
			holder2.userName.setText(list.get(position).appuser.loginSn);
			holder2.content.setText(list.get(position).content);
			holder2.time
					.setText(StringUtils.timeFormat(list.get(position).time));
			holder2.toUserName.setText(list.get(position).toappuser.loginSn);
			break;
		}
		return convertView;
	}

}
