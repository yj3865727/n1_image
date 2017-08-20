package com.lequan.n1.adapter.holder;

import com.lequan.n1.activity.LoginActivity;
import com.lequan.n1.activity.PicDetailActivity;
import com.lequan.n1.activity.R;
import com.lequan.n1.entity.FriendEntity.Data.Rows;
import com.lequan.n1.util.BitmapUtil;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.UiUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

//首页FragmentEntity 显示的数据格式 与FriendEntity一样所以复用

public class HomeDetailsRecyclerHolder extends RecyclerView.ViewHolder{

	@ViewInject(R.id.tv_homefragment_name)
	private TextView name;
	@ViewInject(R.id.iv_homefragment_backimage)
	private ImageView backImage;
	@ViewInject(R.id.iv_homefragment_image)
	private ImageView image;
	@ViewInject(R.id.tv_homefragment_dianzannum)
	private TextView dianzannum;
	@ViewInject(R.id.tv_homefragment_tupiannum)
	private TextView tupiannum;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public HomeDetailsRecyclerHolder(View itemView) {

		super(itemView);
		ViewUtils.inject(this, itemView);		
	}

	public void setDataAndRefreshUi(final Rows rows) {
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
		imageLoader.displayImage(rows.workMainPic,
				backImage, options);
		dianzannum.setText(rows.commentCount + "");
		tupiannum.setText(rows.picCount + "");
		name.setText(rows.appuser.loginSn + "");
		imageLoader.displayImage(rows.appuser.headphoto,
				image, options);


		this.backImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Constants.isLogin){
					Intent intent = new Intent(UiUtils.getContext(),PicDetailActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					Bundle bundle = new Bundle();
					bundle.putInt("workid", rows.id);
					bundle.putInt("userid", rows.appuser.id);
					intent.putExtra("key",bundle);
					UiUtils.getContext().startActivity(intent);
				}else{
					Intent intent = new Intent(UiUtils.getContext(),LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					UiUtils.getContext().startActivity(intent);
				}
			}
		});
	}
}
