package com.lequan.n1.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

import com.google.gson.Gson;
import com.lequan.n1.entity.AppUser;
import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.AnimationUtils;
import com.lequan.n1.util.Bimp;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.FileUtils;
import com.lequan.n1.util.ImageItem;
import com.lequan.n1.util.ImageUtils;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.PublicWay;
import com.lequan.n1.util.Res;
import com.lequan.n1.util.ShareUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.StringUtils;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.util.ValidateUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;



public class PublishWorkActivity extends BaseActivity implements OnClickListener, PlatformActionListener {
	private GridView noScrollgridview;
	private GridAdapter adapter;
	private View parentView;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	public static Bitmap bimap ;
	private TextView send;
	private ImageView back;
	private List<Bitmap> photos;
	public static int mRewardid;
	private String mUId;
	private ProgressDialog mDialog;
	public static List<File> allPhotos;
	private File tempFile;
	private EditText et_publish_work_des;
	private static final int PUBLISH_PIC = 1;
	private Intent intent;
	private ImageView share_qone;
	private ImageView share_sina;
	private ImageView share_wechat;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Res.init(this);
		bimap = BitmapFactory.decodeResource(
				getResources(),
				R.drawable.icon_addpic_unfocused);
		PublicWay.activityList.add(this);
		Init();
		
		
	}
	
	public void Init() {
		pop = new PopupWindow(PublishWorkActivity.this);
		View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);
		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);
		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		Button bt1 = (Button) view
				.findViewById(R.id.item_popupwindows_camera);
		Button bt2 = (Button) view
				.findViewById(R.id.item_popupwindows_Photo);
		Button bt3 = (Button) view
				.findViewById(R.id.item_popupwindows_cancel);
		parent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				photo();
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(PublishWorkActivity.this,
						AlbumActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		
		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);	
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		adapter.update();
		noScrollgridview.setAdapter(adapter);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == Bimp.tempSelectBitmap.size()) {
					LogUtils.i("ddddddd", "----------");
					ll_popup.startAnimation(AnimationUtils.loadAnimation(PublishWorkActivity.this,R.anim.activity_translate_in));
					pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
				} else {
					Intent intent = new Intent(PublishWorkActivity.this,
							GalleryActivity.class);
					intent.putExtra("position", "1");
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});

	}

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private int selectedPosition = -1;
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			if(Bimp.tempSelectBitmap.size() == 9){
				return 9;
			}
			return (Bimp.tempSelectBitmap.size() + 1);
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_published_grida,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position ==Bimp.tempSelectBitmap.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.icon_addpic_unfocused));
				if (position == 9) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.tempSelectBitmap.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							Bimp.max += 1;
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
						}
					}
				}
			}).start();
		}
	}

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	protected void onRestart() {
		adapter.update();
		super.onRestart();
	}

	private static final int TAKE_PICTURE = 0x000001;

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
				String fileName = String.valueOf(System.currentTimeMillis());
				Bitmap bm = (Bitmap) data.getExtras().get("data");
				FileUtils.saveBitmap(bm, fileName);
				ImageItem takePhoto = new ImageItem();
				takePhoto.setBitmap(bm);
				Bimp.tempSelectBitmap.add(takePhoto);
				File f = new File(FileUtils.SDPATH, fileName + ".JPEG");
				LogUtils.e("bbbbb","bbbbb"+f);
				this.allPhotos.add(f);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			for(int i=0;i<PublicWay.activityList.size();i++){
				if (null != PublicWay.activityList.get(i)) {
					PublicWay.activityList.get(i).finish();
				}
			}
			finish();
		}
		return true;
	}

	@Override
	public void onCancel(Platform arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initView() {
		parentView = getLayoutInflater().inflate(R.layout.activity_selectimg, null);
		setContentView(parentView);
		findView();
	}
	private void findView() {
		share_qone = (ImageView) findViewById(R.id.iv_share_qone);
		share_sina = (ImageView) findViewById(R.id.iv_share_sina);
		share_wechat = (ImageView) findViewById(R.id.iv_share_wechat);
		back = (ImageView) findViewById(R.id.selecting_back_person);
		send = (TextView) findViewById(R.id.activity_selectimg_send);
		et_publish_work_des = (EditText) findViewById(R.id.et_publish_work_des);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.selecting_back_person:
			finish();
			break;
		case R.id.activity_selectimg_send:
			publishWorks();
			break;
		case R.id.iv_share_qone:
			shareInfos(QZone.NAME);
			break;
		case R.id.iv_share_sina:
			shareInfos(SinaWeibo.NAME);
			break;
		case R.id.iv_share_wechat:
			shareInfos(Wechat.NAME);
		default:
			break;
		}
	}
	@Override
	protected void initEvent() {
		setOnClick();
	}
	private void setOnClick() {
		share_qone.setOnClickListener(this);
		share_sina.setOnClickListener(this);
		share_wechat.setOnClickListener(this);
		send.setOnClickListener(this);
		back.setOnClickListener(this);
		
	}

	@Override
	protected void initData() {
		this.photos = new ArrayList<Bitmap>();
		this.allPhotos = new ArrayList<File>();
		mDialog = new ProgressDialog(this);
		mUId = SpUtils.getInstance(this).getString(Constants.USER_ID);
		Intent intent = getIntent();
		if (intent != null) {
			int rewardid = intent.getIntExtra("rewardid", -1);
			if(rewardid != -1){
				mRewardid = rewardid;
			}
		}
	}
	
	// 发布作品信息
		private void publishWorks() {
			final Map<String, Object> params = processData();
			if (params != null) {
				// 上传数据
				UiUtils.showSimpleProcessDialog(mDialog, "作品上传中....");
				HttpRequestProxy.uploadFileAsync(HttpRequestProxy.ImagePathTypeUserWorks, allPhotos,
						new RequestCallBack<String>() {

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								try {
									LogUtils.i("上传文件成功："+arg0.result);
									JSONArray imageUrls = new JSONArray(arg0.result);
									if (imageUrls != null && imageUrls.length() > 0) {
										updateWorksInfos(params, imageUrls);
									} else {
										UiUtils.closeProcessDialog(mDialog);
										Toast.makeText(PublishWorkActivity.this, "发布作品失败，请稍后再试!", Toast.LENGTH_SHORT)
												.show();
									}
								} catch (JSONException e) {
									e.printStackTrace();
									Toast.makeText(PublishWorkActivity.this, "发布作品失败，请稍后再试!", Toast.LENGTH_SHORT).show();
								}
							}

							@Override
							public void onFailure(HttpException arg0, String arg1) {
								UiUtils.closeProcessDialog(mDialog);
								LogUtils.i("上传文件失败");
								Toast.makeText(PublishWorkActivity.this, "发布作品失败，请稍后再试!", Toast.LENGTH_SHORT).show();
							}
						});
			}
		}

		private Map<String, Object> processData() {
			if (!ValidateUtils.isValidate(allPhotos)) {
				Toast.makeText(this, "请选择图片!", Toast.LENGTH_SHORT).show();
				return null;
			}
			String des = this.et_publish_work_des.getText().toString();
			if (!ValidateUtils.isValidate(des)) {
				Toast.makeText(this, "请填写内容!", Toast.LENGTH_SHORT).show();
				return null;
			}
			// 组织数据
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("userid", mUId);
			data.put("rewardid", mRewardid);
			data.put("description", des);
			data.put("picCount", photos.size());
			return data;
		}

		protected void updateWorksInfos(Map<String, Object> params, JSONArray data) {
			List<Map<String, String>> workPhotos = new ArrayList<Map<String, String>>();
			Map<String, String> workPhoto;
			for (int i = 0; i < data.length(); i++) {
				workPhoto = new HashMap<String, String>();
				workPhoto.put("url", data.optJSONObject(i).optString("downUrl"));
				workPhotos.add(workPhoto);
				workPhoto = null;
			}
			params.put("workPhotos", workPhotos);
			try {
				HttpRequestProxy.sendAsyncPost(Constants.Url.USERWORK_PUBLISH, params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						LogUtils.i("上传失败!");
						UiUtils.closeProcessDialog(mDialog);
						Toast.makeText(PublishWorkActivity.this, "发布作品失败，请稍后再试!", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						UiUtils.closeProcessDialog(mDialog);
						try {
							JSONObject data = new JSONObject(arg0.result);
							LogUtils.i(arg0.result);
							if (data.optInt("code") == 200) {
								SpUtils spUtils=SpUtils.getInstance(PublishWorkActivity.this);
								String userInfo = spUtils.getString(Constants.ALL_USERINFO);
								AppUser user=new Gson().fromJson(userInfo, AppUser.class);
								user.uwcount++;
								finish();
							} else {
								Toast.makeText(PublishWorkActivity.this, data.optString("desc"), Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				UiUtils.closeProcessDialog(mDialog);
				Toast.makeText(PublishWorkActivity.this, "发布作品失败，请稍后再试!", Toast.LENGTH_SHORT).show();
			}
		}
		private void shareInfos(String name) {
			String content=this.et_publish_work_des.getText().toString();
			if(!ValidateUtils.isValidate(content)){
				Toast.makeText(this, "请输入描述信息!", Toast.LENGTH_LONG).show();
				return;
			}
			String[] imagePaths=StringUtils.files2PathArray(allPhotos);
			if(!ValidateUtils.isValidate(imagePaths)){
				Toast.makeText(this, "至少选择一张照片!", Toast.LENGTH_LONG).show();
				return;
			}
			ShareUtils.shareSpecialPlat(this,name, content, imagePaths, this);
		}

}