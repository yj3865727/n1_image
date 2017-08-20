package com.lequan.n1.activity.fragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Contacts.Intents.UI;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.google.gson.Gson;
import com.lequan.n1.activity.OthersActivity;
import com.lequan.n1.activity.PersonalRewardActivity;
import com.lequan.n1.activity.PersonalSettingActivity;
import com.lequan.n1.activity.PicDetailActivity;
import com.lequan.n1.activity.R;
import com.lequan.n1.activity.fragment.FragmentGeRen_XuanShang.recyclerView.MyViewHolder;
import com.lequan.n1.entity.AppUser;
import com.lequan.n1.entity.FriendEntity.Data.Rows;
import com.lequan.n1.entity.PersonalGuanzhuEntity;
import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.BitmapUtil;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.util.ValidateUtils;
import com.lequan.n1.view.XCRoundRectImageView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
public class FragmentGeRen_GuanZhu extends BaseFragment implements OnClickListener{
	private RecyclerView rv;
	private List<Integer> heights;
	private PersonalGuanzhuEntity personalGuanzhuEntity;
	private List<AppUser> list = new ArrayList<AppUser>();
	private String mUserId;
	private recyclerView adapter;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	@Override
	protected View initView() {
		mUserId = SpUtils.getInstance(getContext()).getString(Constants.USER_ID);
		View views = View.inflate(mContext, R.layout.personal_guanzhu, null);
		rv = (RecyclerView) views.findViewById(R.id.guanzhu_recycler);
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				// // 设置图片在下载期间显示的图片
				.showImageOnLoading(R.drawable.logo_placeholder2x)
				// // 设置图片Uri为空或是错误的时候显示的图片
				.showImageForEmptyUri(R.drawable.logo_placeholder2x)
				// // 设置图片加载/解码过程中错误时候显示的图片
				.showImageOnFail(R.drawable.logo_placeholder2x)
				.cacheInMemory(true)
				// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)
				// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.ARGB_8888)// 设置图片的解码类型
				.considerExifParams(true)
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.build();
		return views;
	} 
	@Override
	@OnClick({ R.id.geren_guanzhu_chang, R.id.personal_guanzhu})
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.geren_guanzhu_chang:
			break;
		case R.id.personal_guanzhu:
			startActivity(new Intent(getActivity(),OthersActivity.class));
			break;
		default:
			break;
		}
	}
	protected void initData() {
		try {
			final String url = Constants.Url.ATTENTIONED_USER;
			HashMap<String, Object> params = new HashMap<String, Object>();
			Bundle bundle = getArguments();
			params.put("id", bundle.getString("userid"));
			params.put("page", 1);
			params.put("rows", 100);
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

	protected void parseData(String result) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		personalGuanzhuEntity = gson.fromJson(result, PersonalGuanzhuEntity.class);
		list = personalGuanzhuEntity.data.rows;
		rv.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
		adapter = new recyclerView(list);
		//getRandomHeight(this.list);
		rv.setAdapter(adapter);
	}
	class recyclerView extends RecyclerView.Adapter<recyclerView.MyViewHolder> {
		private List<AppUser> lists = new ArrayList<AppUser>();

		public recyclerView(List<AppUser> list) {
			this.lists = list;
		}

		@Override
		public int getItemCount() {
			
			return lists.size();
		}
		 @Override
		    public int getItemViewType(int position) {
		        return super.getItemViewType(position);
		    }

		@Override
		public void onBindViewHolder(MyViewHolder holder, int position) {
			holder.setDataAndRefreshUi(lists.get(position));
			
			
		}
		@Override
		public MyViewHolder onCreateViewHolder(ViewGroup arg0,final int arg1) {
			View view = View.inflate(UiUtils.getContext(), R.layout.item_geren_guanzhu_listview, null);
			MyViewHolder holder = new MyViewHolder(view);
			
			return holder;
		}

		public class MyViewHolder extends ViewHolder {
			XCRoundRectImageView geren_guanzhu_pic;
			TextView geren_guanzhu_name;
			Button geren_guanzhu_change;
			RelativeLayout personal_guanzhu;
			int i = 1;
			public MyViewHolder(View itemView) {
				super(itemView);
				personal_guanzhu = (RelativeLayout) itemView.findViewById(R.id.personal_guanzhu);
				geren_guanzhu_change = (Button) itemView.findViewById(R.id.geren_guanzhu_chang);
				geren_guanzhu_pic = (XCRoundRectImageView) itemView.findViewById(R.id.geren_guanzhu_pic);
				geren_guanzhu_name = (TextView) itemView.findViewById(R.id.geren_guanzhu_name);
				initListener();
			}
			
			private void initListener() {
			}

			public void setDataAndRefreshUi(final AppUser data) {
				imageLoader.displayImage(data.headphoto,
						geren_guanzhu_pic, options);
				geren_guanzhu_name.setText(data.loginSn + "");
				String id = data.id+"";
				if(id.equals(mUserId)){
				}else{
					personal_guanzhu.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(UiUtils.getContext(),OthersActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							Bundle bundle = new Bundle();
							bundle.putInt("userid", data.id);
							intent.putExtra("key",bundle);
							UiUtils.getContext().startActivity(intent);
						}
					});
				}
				
				Bundle bundle = getArguments();

				if(bundle.getString("userid").equals(mUserId)){
					geren_guanzhu_change.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if(i>0){
									unAttention();
								}else{
									hanleAttention();
								}
							}
							private long toUser;
							private ProgressDialog mDialog;
							
							private void hanleAttention() {
								toUser =Long.parseLong(data.id+"");
								if (!ValidateUtils.isValidate(toUser + "")) {
									return;
								}
								Map<String, Long> params = new HashMap<String, Long>();
								params.put("touser", toUser);
								long fromuser = Long.parseLong(SpUtils.getInstance(mContext).getString(Constants.USER_ID));
								params.put("fromuser", fromuser);
								if (mDialog == null) {
									mDialog = new ProgressDialog(mContext);
								}
								UiUtils.showSimpleProcessDialog(mDialog, "关注用户，请稍后....");
								try {
									HttpRequestProxy.sendAsyncPost(Constants.Url.ATTENTION_OTHER, params, new RequestCallBack<String>() {

										@Override
										public void onFailure(HttpException arg0, String arg1) {
											UiUtils.closeProcessDialog(mDialog);
											Toast.makeText(mContext, "关注失败,请稍好再试!", Toast.LENGTH_SHORT).show();
										}

										@Override
										public void onSuccess(ResponseInfo<String> arg0) {
											UiUtils.closeProcessDialog(mDialog);
											//修改用户关注的本地数量
											SpUtils spUtils=SpUtils.getInstance(mContext);
											String userInfo = spUtils.getString(Constants.ALL_USERINFO);
											AppUser user=new Gson().fromJson(userInfo, AppUser.class);
											user.attentionCount++;
											geren_guanzhu_change.setText("已关注");
											geren_guanzhu_change.setBackgroundResource(R.drawable.geren_guanzhu_btn_shape);
											i = 1;
											spUtils.setString(Constants.ALL_USERINFO, new Gson().toJson(user));
											spUtils.setString(Constants.ATTENTIONCOUNT, (Integer.parseInt(spUtils.getString(Constants.ATTENTIONCOUNT))+1)+"");
											try {
												JSONObject data = new JSONObject(arg0.result);
												Toast.makeText(mContext, data.optString("desc"), Toast.LENGTH_SHORT).show();
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
									});
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							private void unAttention() {
								toUser =Long.parseLong(data.id+"");
								if (!ValidateUtils.isValidate(toUser + "")) {
									return;
								}
								Map<String, Long> params = new HashMap<String, Long>();
								params.put("touser", toUser);
								long fromuser = Long.parseLong(SpUtils.getInstance(mContext).getString(Constants.USER_ID));
								params.put("fromuser", fromuser);
								if (mDialog == null) {
									mDialog = new ProgressDialog(mContext);
								}
								UiUtils.showSimpleProcessDialog(mDialog, "取消关注，请稍后....");
								try {
									HttpRequestProxy.sendAsyncPost(Constants.Url.UNATTENTION, params, new RequestCallBack<String>() {

										@Override
										public void onFailure(HttpException arg0, String arg1) {
											UiUtils.closeProcessDialog(mDialog);
											Toast.makeText(mContext, "取消关注失败,请稍好再试!", Toast.LENGTH_SHORT).show();
										}

										@Override
										public void onSuccess(ResponseInfo<String> arg0) {
											UiUtils.closeProcessDialog(mDialog);
											//修改用户关注的本地数量
											SpUtils spUtils=SpUtils.getInstance(mContext);
											String userInfo = spUtils.getString(Constants.ALL_USERINFO);
											AppUser user=new Gson().fromJson(userInfo, AppUser.class);
											user.attentionCount--;
											geren_guanzhu_change.setBackgroundResource(R.drawable.geren_guanzhu_shape);
											geren_guanzhu_change.setText("关注");
											i = 0;
											spUtils.setString(Constants.ALL_USERINFO, new Gson().toJson(user));
											spUtils.setString(Constants.ATTENTIONCOUNT, (Integer.parseInt(spUtils.getString(Constants.ATTENTIONCOUNT))-1)+"");
											try {
												JSONObject data = new JSONObject(arg0.result);
												Toast.makeText(mContext, data.optString("desc"), Toast.LENGTH_SHORT).show();
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
									});
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
				}else{
					geren_guanzhu_change.setVisibility(View.INVISIBLE);
				}
			}
		}
	}

}
