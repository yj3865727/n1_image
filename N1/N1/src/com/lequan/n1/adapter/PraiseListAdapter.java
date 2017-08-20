package com.lequan.n1.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lequan.n1.activity.R;
import com.lequan.n1.adapter.holder.PraiseListViewHolder;
import com.lequan.n1.entity.PraisesEntity;
import com.lequan.n1.util.BitmapUtil;
import com.lequan.n1.util.StringUtils;

public class PraiseListAdapter extends BaseAdapter {
	private List<PraisesEntity> list = new ArrayList<PraisesEntity>();
	private LayoutInflater mInflater;

	public PraiseListAdapter(Context mContext, List<PraisesEntity> list) {
		mInflater = LayoutInflater.from(mContext);
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
	public View getView(int position, View convertView, ViewGroup parent) {
		PraiseListViewHolder holder=null;
		if(convertView==null){
			convertView=mInflater.inflate(R.layout.item_praise_list, parent,false);
			holder=new PraiseListViewHolder();
			holder.time=(TextView) convertView.findViewById(R.id.praise_list_time_tv);
			holder.headImg=(ImageView) convertView.findViewById(R.id.praise_list_head_img);
			holder.userName=(TextView) convertView.findViewById(R.id.praise_list_username_tv);
		convertView.setTag(holder);
		}else{
			holder=(PraiseListViewHolder) convertView.getTag();
		}
		BitmapUtil.display(holder.headImg,
				list.get(position).appuser.headphoto);
		holder.time
		.setText(StringUtils.timeFormat(list.get(position).time));
		holder.userName
		.setText(list.get(position).appuser.loginSn);	
		return convertView;
	}

}
