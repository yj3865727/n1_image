package com.lequan.n1.adapter.holder;

import android.widget.ImageView;
import android.widget.TextView;

public class CommentsViewHolder {

	public int id;   //评论的id
	public int parentId;  //如果为空表示该评论回复作品，不为空表示回复某条评论
	public TextView content;
	public TextView time;
	public ImageView head;
	public TextView userName;//发表评论者
	public TextView toUserName;//被回复者
	
}
