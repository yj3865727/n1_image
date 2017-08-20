package com.lequan.n1.adapter;

import java.util.List;

import com.lequan.n1.activity.R;
import com.lequan.n1.adapter.holder.HomeDetailsRecyclerHolder;
import com.lequan.n1.entity.FriendEntity;
import com.lequan.n1.entity.FriendEntity.Data.Rows;
import com.lequan.n1.util.UiUtils;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;

public class HomeDetailRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	
	private List<Rows> mDatas;

	public HomeDetailRecycleViewAdapter(RecyclerView recyclerView, List<Rows> ts) {
		this.mDatas = ts;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = View.inflate(UiUtils.getContext(), R.layout.fragment_homedetails_image, null);
		HomeDetailsRecyclerHolder hdrHolder = new HomeDetailsRecyclerHolder(v);
		return hdrHolder;
	}


	@Override
	public int getItemCount() {
		return mDatas != null ? mDatas.size() : 0;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		
	}

}
