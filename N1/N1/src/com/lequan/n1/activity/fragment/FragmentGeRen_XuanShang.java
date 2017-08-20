package com.lequan.n1.activity.fragment;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;









import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lequan.n1.activity.OthersActivity;
import com.lequan.n1.activity.PicDetailActivity;
import com.lequan.n1.activity.R;
import com.lequan.n1.entity.FindByIDEntity;
import com.lequan.n1.entity.FriendEntity;
import com.lequan.n1.entity.PersonalXuanshangEntity;
import com.lequan.n1.entity.PersonalXuanshangEntity.Data.Rows;
import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.BitmapUtil;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.UiUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;

public class FragmentGeRen_XuanShang extends BaseFragment{
	private RecyclerView rv;
	private ImageView xuanshangImg;
	private List<Integer> heights;
	private PersonalXuanshangEntity personalXuanshangEntity;
	private List<Rows> list = new ArrayList<PersonalXuanshangEntity.Data.Rows>();
	private recyclerView adapter;
	private int total;
	private String mUserId;
	private TextView xuanshang1;
	private Intent intent;
	private ImageView xuanshang_none;
	@Override
	protected View initView() {
		mUserId = SpUtils.getInstance(getContext()).getString(Constants.USER_ID);
		View views = View.inflate(mContext, R.layout.personal_xuanshang, null);
		rv = (RecyclerView) views.findViewById(R.id.geren_recycler);
		xuanshang1 = (TextView) views.findViewById(R.id.xuanshang1);
		xuanshang_none = (ImageView) views.findViewById(R.id.xuanshang_none);
		xuanshang_none.setVisibility(View.VISIBLE);
		xuanshang_none.setVisibility(View.GONE);
		return views;
	} 
	@Override
	protected void initData() {
		try {
			//intent = getIntent();
			//Bundle bundle = intent.getBundleExtra("key");
			final String url = Constants.Url.USER_WORK;
			HashMap<String, Object> params = new HashMap<String, Object>();
			Bundle bundle = getArguments();
			params.put("id", bundle.getString("userid"));
			params.put("page", 1);
			params.put("rows", 20);
			HttpRequestProxy.sendAsyncPost(url, params, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					LogUtils.i("请求失败");
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					// TODO 缓存数据
					String result = arg0.result;
					parseData(result);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@SuppressLint("NewApi")
	protected void parseData(String result) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		personalXuanshangEntity = gson.fromJson(result, PersonalXuanshangEntity.class);

		list = personalXuanshangEntity.data.rows;
		total = personalXuanshangEntity.data.total;
		if(list.size()<=0){
			xuanshang_none.setVisibility(View.GONE);
			xuanshang_none.setVisibility(View.VISIBLE);
		}
		 
		rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

		adapter = new recyclerView(list);
		
		rv.setAdapter(adapter);
	}
	class recyclerView extends RecyclerView.Adapter<recyclerView.MyViewHolder> {
		private List<PersonalXuanshangEntity.Data.Rows> list = new ArrayList<PersonalXuanshangEntity.Data.Rows>();

		public recyclerView(List<PersonalXuanshangEntity.Data.Rows> list) {
			LogUtils.i("图片数据："+list.size());
			this.list = list;
		}

		@Override
		public int getItemCount() {

			return list.size();
		}
		 @Override
		    public int getItemViewType(int position) {
		        return super.getItemViewType(position);
		    }

		@Override
		public void onBindViewHolder(MyViewHolder holder, int position) {
			holder.setDataAndRefreshUi(list.get(position));
		}
		@Override
		public MyViewHolder onCreateViewHolder(ViewGroup arg0, final int arg1) {
			View view = View.inflate(UiUtils.getContext(), R.layout.personal_xuanshang_listview, null);
			MyViewHolder holder = new MyViewHolder(view);
			return holder;
		}

		public class MyViewHolder extends ViewHolder {

			ImageView xuanshangImg;
			ImageView zhanImg;
			TextView zanTv;
			RelativeLayout personal_xuanshang;

			public MyViewHolder(View itemView) {
				super(itemView);
				xuanshangImg = (ImageView) itemView.findViewById(R.id.xuanshangImg);
				zanTv = (TextView) itemView.findViewById(R.id.geren_zan_tv);
				zhanImg = (ImageView) itemView.findViewById(R.id.geren_zan_img);
				personal_xuanshang = (RelativeLayout) itemView.findViewById(R.id.personal_xuanshang);
				initListener();
			}

			private void initListener() {
				
			}

			public void setDataAndRefreshUi(final Rows data) {
				
				BitmapUtil.display(xuanshangImg, data.workMainPic);
				zanTv.setText(data.praiseMatchCount + "");
				xuanshangImg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(UiUtils.getContext(),PicDetailActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					Bundle bundle = new Bundle();
					bundle.putInt("id", Integer.valueOf(mUserId));
					bundle.putInt("workid",data.id);
					intent.putExtra("key",bundle);
					UiUtils.getContext().startActivity(intent);
				}
			});
			}
		}
	}
}
