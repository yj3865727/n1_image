package com.lequan.n1.view;

import com.lequan.n1.activity.R;
import com.lequan.n1.entity.HomeEntity.Data.Home_Data_Data.Home_Data_Data_Row;
import com.lequan.n1.util.BitmapUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ImageWithTextView extends RelativeLayout {
	
	private ImageView mIv;
	private TextView mTv;
	private RelativeLayout mRl_image_text_container;
	private ImageView mIv_image_text_none_data;

	public ImageWithTextView(Context context) {
		this(context,null);
	}

	public ImageWithTextView(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}

	public ImageWithTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		View view = View.inflate(context, R.layout.rl_image_des, this);
		mIv_image_text_none_data = (ImageView) view.findViewById(R.id.iv_image_text_none_data);
		mRl_image_text_container = (RelativeLayout) view.findViewById(R.id.rl_image_text_container);
		mIv = (ImageView) view.findViewById(R.id.iv_image_text_view);
		mTv = (TextView) view.findViewById(R.id.tv_image_text_view);
	}
	
	public void setDataAndRefreshUi(Home_Data_Data_Row data){
		if(data==null){
			mRl_image_text_container.setVisibility(View.INVISIBLE);
			mIv_image_text_none_data.setVisibility(View.VISIBLE);
		}else{
			mIv_image_text_none_data.setVisibility(View.INVISIBLE);
			mRl_image_text_container.setVisibility(View.VISIBLE);
			
			this.mTv.setText(data.rewardSubject);
			BitmapUtil.display(this.mIv, data.backgroundUrl);
		}
	}
	
}
