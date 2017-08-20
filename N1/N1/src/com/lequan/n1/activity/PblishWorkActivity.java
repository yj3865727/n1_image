package com.lequan.n1.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.AnimationUtils;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.FileUtils;
import com.lequan.n1.util.ImageUtils;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.PhotoUtils;
import com.lequan.n1.util.ShareUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.StringUtils;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.util.ValidateUtils;
import com.lequan.n1.view.SelectPicPopupWindow;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;

public class PblishWorkActivity extends BaseActivity implements OnClickListener, PlatformActionListener {
	private final static int CROP = 200;
	private final static String FILE_SAVEPATH = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/n1/Camera/";
	private Uri origUri;
	private Uri cropUri;
	private String theLarge;
	private File protraitFile;
	private Bitmap protraitBitmap;
	private String protraitPath;
	private static final int PUBLISH_PIC = 1;
	private static final int REQ_TAKE_CODE = 100;
	private static final int REQ_PICK_CODE = 101;
	public static final int PHOTOS_DETAIL=102;
	
	private static final int REQ_ZOOM_CODE = 102;
	@ViewInject(R.id.rl_publish_work)
	private RelativeLayout ll_publish_work;

	@ViewInject(R.id.et_publish_work_des)
	private EditText et_publish_work_des;

	@ViewInject(R.id.iv_publish_work_add_pic)
	private ImageView iv_publish_work_add_pic;

	@ViewInject(R.id.cb_publish_work_checked)
	private CheckBox cb_publish_work_checked;

	@ViewInject(R.id.iv_share_sina)
	private ImageView iv_share_sina;

	@ViewInject(R.id.iv_share_qone)
	private ImageView iv_share_qone;
	
	@ViewInject(R.id.iv_share_wechat)
	private ImageView iv_share_wechat;

	@ViewInject(R.id.ll_pulicsh_work_pic_container)
	private LinearLayout ll_pulicsh_work_pic_container;

	@ViewInject(R.id.viewMask)
	private View mViewMask;

	private SelectPicPopupWindow mPicPopupWindow;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_publish_work);
		ViewUtils.inject(this);
	}

	@Override
	protected void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle("发布照片");
		}
	}

	@Override
	protected void initData() {
		this.photos = new ArrayList<Bitmap>();
		this.allPhotos = new ArrayList<File>();
		mDialog = new ProgressDialog(this);
		mUId = SpUtils.getInstance(this).getString(Constants.USER_ID);
		Intent intent = getIntent();
		if (intent != null) {
			mRewardid = intent.getIntExtra("rewardid", -1);
		}
	}

	@Override
	protected void initEvent() {
		this.iv_publish_work_add_pic.setOnClickListener(this);
		this.cb_publish_work_checked.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!isChecked) {
					Toast.makeText(PblishWorkActivity.this, "请保持原创", Toast.LENGTH_LONG).show();
				}
			}
		});
		this.mViewMask.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPicPopupWindow != null) {
					mPicPopupWindow.dismiss();
					AnimationUtils.hideAlpha(mViewMask);
				}
			}
		});
		this.iv_share_sina.setOnClickListener(this);
		this.iv_share_qone.setOnClickListener(this);
		this.iv_share_wechat.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, PUBLISH_PIC, 0, "投稿");
		menu.findItem(PUBLISH_PIC).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case PUBLISH_PIC:
			publishWorks();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
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
									Toast.makeText(PblishWorkActivity.this, "发布作品失败，请稍后再试!", Toast.LENGTH_SHORT)
											.show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
								Toast.makeText(PblishWorkActivity.this, "发布作品失败，请稍后再试!", Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							UiUtils.closeProcessDialog(mDialog);
							LogUtils.i("上传文件失败");
							Toast.makeText(PblishWorkActivity.this, "发布作品失败，请稍后再试!", Toast.LENGTH_SHORT).show();
						}
					});
		}
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
					Toast.makeText(PblishWorkActivity.this, "发布作品失败，请稍后再试!", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					UiUtils.closeProcessDialog(mDialog);
					try {
						JSONObject data = new JSONObject(arg0.result);
						LogUtils.i(arg0.result);
						if (data.optInt("code") == 200) {
							finish();
						} else {
							Toast.makeText(PblishWorkActivity.this, data.optString("desc"), Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			UiUtils.closeProcessDialog(mDialog);
			Toast.makeText(PblishWorkActivity.this, "发布作品失败，请稍后再试!", Toast.LENGTH_SHORT).show();
		}
	}

	private Map<String, Object> processData() {
		if (!ValidateUtils.isValidate(photos)) {
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

	@Override
	public void onClick(View v) {
		if (mPicPopupWindow != null) {
			mPicPopupWindow.dismiss();
			AnimationUtils.hideAlpha(mViewMask);
		}
		switch (v.getId()) {
		case R.id.iv_share_qone:
			shareInfos(QZone.NAME);
			break;
		case R.id.iv_share_sina:
			shareInfos(SinaWeibo.NAME);
			break;
		case R.id.iv_share_wechat:
			shareInfos(Wechat.NAME);
			break;
		case R.id.btn_pick_photo:
			PhotoUtils.pickPhoto(this, REQ_PICK_CODE);
			this.mPicPopupWindow.dismiss();
			break;
		case R.id.btn_take_photo:
			startTakePhoto();
			this.mPicPopupWindow.dismiss();
			break;
		case R.id.iv_publish_work_add_pic:
			mPicPopupWindow = PhotoUtils.getPicPopupWindow(this, this, ll_publish_work);
			AnimationUtils.showAlpha(mViewMask);
			break;
		default:
			break;
		}
	}
	//指定平台分享
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
	private List<Bitmap> photos;
	private int mRewardid;
	private String mUId;
	private ProgressDialog mDialog;
	private List<File> allPhotos;
	private File tempFile;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_TAKE_CODE:// 拍照完成之后会来到这个地方
			startActionCrop(origUri);
			// 保存图片文件
			this.allPhotos.add(tempFile);
			break;
		case REQ_PICK_CODE:// 从相册选择好之后的结果
			refreshPics(PhotoUtils.checkImage(this, data));
			String path = ImageUtils.getAbsoluteImagePath(this, data.getData());
			if (ValidateUtils.isValidate(path)) {
				File file = new File(path);
				if (file.exists()) {
					this.allPhotos.add(file);
				}
			}
			break;
		case PHOTOS_DETAIL:
			//TODO 处理移除的图片 刷新ui
			String[] extraData = data.getStringArrayExtra("dealResult");
			if(ValidateUtils.isValidate(extraData)){
				ll_pulicsh_work_pic_container.removeAllViews();
				//清空数据
				this.allPhotos.clear();
				this.photos.clear();
				for(String pathFile:extraData){
					refreshPics(BitmapFactory.decodeFile(pathFile));
					this.allPhotos.add(new File(pathFile));
				}
			}else{
				this.allPhotos.clear();
				this.photos.clear();
				ll_pulicsh_work_pic_container.removeAllViews();
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void refreshPics(Bitmap bitmap) {
		if (bitmap != null) {
			this.photos.add(bitmap);
			ImageView iv = new ImageView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UiUtils.dip2px(70), UiUtils.dip2px(70));
			params.leftMargin = UiUtils.dip2px(5);
			params.rightMargin = UiUtils.dip2px(5);
			iv.setLayoutParams(params);
			iv.setImageBitmap(bitmap);
			ll_pulicsh_work_pic_container.addView(iv);
			//记录文件的位置
			final int index=photos.size();
			iv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//显示图片详情
					Intent intent=new Intent();
					intent.putExtra("index", index);
					intent.putExtra("photos", StringUtils.files2PathArray(allPhotos));
					intent.setClass(PblishWorkActivity.this, PublishWorkShowPhotos.class);
					startActivityForResult(intent, PHOTOS_DETAIL);
				}
			});
		}
	}

	private void startTakePhoto() {
		Intent intent;
		// 判断是否挂载了SD卡
		String savePath = "";
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
			savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/n1/Camera/";
			File savedir = new File(savePath);
			if (!savedir.exists()) {
				savedir.mkdirs();
			}
		}

		// 没有挂载SD卡，无法保存文件
		if (!ValidateUtils.isValidate(savePath)) {
			Toast.makeText(this, "无法保存照片，请检查SD卡是否挂载", Toast.LENGTH_SHORT).show();
			return;
		}

		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String fileName = "n1_" + timeStamp + ".jpg";// 照片命名
		File out = new File(savePath, fileName);
		Uri uri = Uri.fromFile(out);
		origUri = uri;

		theLarge = savePath + fileName;// 该照片的绝对路径

		intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, REQ_TAKE_CODE);
	}
	// 裁剪头像的绝对路径
		private Uri getUploadTempFile(Uri uri) {
			String storageState = Environment.getExternalStorageState();
			if (storageState.equals(Environment.MEDIA_MOUNTED)) {
				File savedir = new File(FILE_SAVEPATH);
				if (!savedir.exists()) {
					savedir.mkdirs();
				}
			} else {
				Toast.makeText(this, "无法保存上传的图片，请检查SD卡是否挂载", Toast.LENGTH_SHORT).show();
				return null;
			}
			String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

			// 如果是标准Uri
			if (!ValidateUtils.isValidate(thePath)) {
				thePath = ImageUtils.getAbsoluteImagePath(this, uri);
			}
			String ext = FileUtils.getFileFormat(thePath);
			ext = !ValidateUtils.isValidate(ext) ? "jpg" : ext;
			// 照片命名
			String cropFileName = "osc_crop_" + timeStamp + "." + ext;
			// 裁剪头像的绝对路径
			protraitPath = FILE_SAVEPATH + cropFileName;
			protraitFile = new File(protraitPath);

			cropUri = Uri.fromFile(protraitFile);
			return this.cropUri;
		}
	/**
	 * 拍照后裁剪
	 *
	 * @param data
	 *            原始图片
	 */
	private void startActionCrop(Uri data) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(data, "image/*");
		intent.putExtra("output", this.getUploadTempFile(data));
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", CROP);// 输出图片大小
		intent.putExtra("outputY", CROP);
		intent.putExtra("scale", true);// 去黑边
		intent.putExtra("scaleUpIfNeeded", true);// 去黑边
		startActivityForResult(intent, REQ_ZOOM_CODE);
	}


	@Override
	public void onCancel(Platform arg0, int arg1) {
		Toast.makeText(this, "分享取消!", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
		Toast.makeText(this, "分享成功!", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		
	}
}
