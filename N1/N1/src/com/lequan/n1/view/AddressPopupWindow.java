package com.lequan.n1.view;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lequan.n1.activity.R;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.adapters.AbstractWheelTextAdapter;

public class AddressPopupWindow extends PopupWindow implements OnClickListener {

	private Context context;
	private AbstractWheel mProvence;
	private AbstractWheel mCity;
	private JSONArray mCityInfos;

	public AddressPopupWindow(Context context, OnClickListener listener) {
		this.context = context;
		View contentView = View.inflate(context, R.layout.ll_address_popup, null);
		this.setContentView(contentView);
		this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		// 点击popupwindows范围以外的地方,让其消失
		this.setBackgroundDrawable(new ColorDrawable());
		this.setOutsideTouchable(true);
		// 添加动画
		this.setAnimationStyle(R.style.PopupAnimation);

		// 初始化view
		contentView.findViewById(R.id.tv_address_cancel).setOnClickListener(this);
		contentView.findViewById(R.id.tv_address_ok).setOnClickListener(this);
		mProvence = (AbstractWheel) contentView.findViewById(R.id.wvl_provence);
		mProvence.setVisibleItems(3);

		mCity = (AbstractWheel) contentView.findViewById(R.id.wvl_city);
		mCity.setVisibleItems(3);

		// 初始化数据
		initData();
		this.mProvence.setViewAdapter(new ProvenceAdapter(context, mCityInfos, "p"));
		this.mProvence.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				updateCities(mCity, mCityInfos.optJSONObject(newValue).optJSONArray("c"));
			}
		});

		// 用于初始化城市信息
		this.mProvence.setCurrentItem(1);
	}

	// 初始化信息
	private void initData() {
		try {
			StringBuffer sb = new StringBuffer();
			InputStream is = this.context.getAssets().open("city.json");
			int len = -1;
			byte[] buf = new byte[1024];
			while ((len = is.read(buf)) != -1) {
				sb.append(new String(buf, 0, len, "UTF-8"));
			}
			is.close();
			JSONObject data = new JSONObject(sb.toString());
			mCityInfos = data.optJSONArray("citylist");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateCities(AbstractWheel city, JSONArray data) {
		this.mCity.setViewAdapter(new ProvenceAdapter(context, data, "n"));
	}

	private final class ProvenceAdapter extends AbstractWheelTextAdapter {

		private JSONArray mData;
		private String mKey;

		protected ProvenceAdapter(Context context, JSONArray data, String key) {
			super(context);
			this.mData = data;
			this.mKey = key;
		}

		@Override
		public int getItemsCount() {
			if (mData != null) {
				return mData.length();
			}
			return 0;
		}

		@Override
		protected CharSequence getItemText(int index) {
			JSONObject provence = mData.optJSONObject(index);
			return provence.optString(mKey);
		}

	}

	@Override
	public void onClick(View v) {
		this.dismiss();
		switch (v.getId()) {
		case R.id.tv_address_cancel:
			break;
		case R.id.tv_address_ok:
				JSONObject provence = this.mCityInfos.optJSONObject(this.mProvence.getCurrentItem());
				JSONObject city=provence.optJSONArray("c").optJSONObject(this.mCity.getCurrentItem());
				String selcted=provence.optString("p")+" "+city.optString("n");
				if(listener!=null){
					listener.onSelected(selcted);
				}
			break;

		default:
			break;
		}
	}
	
	public interface onSelectedListener{
		void onSelected(String selcted);
	}
	
	private onSelectedListener listener;
	
	public void setonSelectedListener(onSelectedListener listener) {
		this.listener = listener;
	}

}
