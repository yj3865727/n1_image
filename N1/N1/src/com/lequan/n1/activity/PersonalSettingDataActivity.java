package com.lequan.n1.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;
import com.lequan.n1.entity.AppUser;
import com.lequan.n1.manager.ConversactionListDbManager;
import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.AnimationUtils;
import com.lequan.n1.util.BitmapUtil;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.FileUtils;
import com.lequan.n1.util.ImageUtils;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.PhotoUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.util.ValidateUtils;
import com.lequan.n1.view.AddressPopupWindow;
import com.lequan.n1.view.AddressPopupWindow.onSelectedListener;
import com.lequan.n1.view.HeadPhotoPopupWindow;
import com.lequan.n1.view.SettingSelectPicPopupWindow;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PersonalSettingDataActivity extends BaseActivity implements OnClickListener, onSelectedListener {
	private static final int REQ_TAKE_CODE = 100;
	private static final int REQ_PICK_CODE = 101;
	private static final int REQ_ZOOM_CODE = 102;
	private LinearLayout sexchange;
	private EditText set_name;
	private View set_mViewMask;
	private RelativeLayout set_personal;
	private ImageView set_touxiang;
	private ImageView setBackground;
	private ImageButton set_back;
	private TextView set_sex;
	private TextView set_save;
	private String mUserId;

	private LinearLayout set_address;
	private TextView address;

	private final static String FILE_SAVEPATH = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/n1/Camera/";
	private Uri origUri;
	private Uri cropUri;
	private File protraitFile;
	private Bitmap protraitBitmap;
	private String protraitPath;
	private String theLarge;
	private final static int CROP = 200;

	@Override
	protected void initView() {
		setContentView(R.layout.personal_setting_person);
		findView();

	}

	public void findView() {
		set_back = (ImageButton) findViewById(R.id.set_back);
		set_save = (TextView) findViewById(R.id.set_save);
		set_sex = (TextView) findViewById(R.id.set_sex);
		set_name = (EditText) findViewById(R.id.set_name);
		set_touxiang = (ImageView) findViewById(R.id.set_touxiang);
		set_mViewMask = findViewById(R.id.set_viewMask);
		set_personal = (RelativeLayout) findViewById(R.id.set_personal);
		sexchange = (LinearLayout) findViewById(R.id.sexchange);
		setBackground = (ImageView) findViewById(R.id.set_background);
		set_address = (LinearLayout) findViewById(R.id.set_add);
		address = (TextView) findViewById(R.id.tv_address);
	}

	@Override
	protected void initEvent() {

		setOnClick();
	}

	private void setOnClick() {
		set_back.setOnClickListener(this);
		set_save.setOnClickListener(this);
		sexchange.setOnClickListener(this);
		set_touxiang.setOnClickListener(this);
		set_address.setOnClickListener(this);
		setBackground.setOnClickListener(this);
	}

	// 自定义的弹出框类
	SettingSelectPicPopupWindow menuWindow;
	HeadPhotoPopupWindow headWindow;
	AddressPopupWindow addWindow;
	private SpUtils mSpUtils;
	private AppUser mUser;
	private ProgressDialog mDialog;

	@Override
	@OnClick({ R.id.set_back, R.id.back_person })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sexchange:
			menuWindow = PhotoUtils.settinggetPicPopupWindow(this, this, set_personal);
			AnimationUtils.showAlpha(set_mViewMask);
			break;
		case R.id.set_touxiang:
			headWindow = PhotoUtils.headgetPicPopupWindow(this, this, set_personal);
			AnimationUtils.showAlpha(set_mViewMask);
			break;
		case R.id.set_add:
			if (addWindow == null) {
				addWindow = PhotoUtils.addPopupWindow(this, this, set_personal);
				addWindow.setonSelectedListener(this);
			} else {
				addWindow.showAtLocation(set_personal, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
			AnimationUtils.showAlpha(set_mViewMask);
			break;
		case R.id.head_cancel:
			this.headWindow.dismiss();
			break;
		case R.id.set_takephoto:
			startTakePhoto();
			this.headWindow.dismiss();
			break;
		case R.id.set_pickphoto:
			this.headWindow.dismiss();
			startImagePick();
			break;
		case R.id.set_cancel:
			this.menuWindow.dismiss();
			break;
		case R.id.set_man:
			set_sex.setText("男");
			this.mUser.sex = "男";
			this.menuWindow.dismiss();
			break;
		case R.id.set_woman:
			set_sex.setText("女");
			this.mUser.sex = "女";
			this.menuWindow.dismiss();
			break;
		case R.id.set_back:
			finish();
			break;
		case R.id.set_save:
			updateUserInfo();
		default:
			break;
		}
	}

	// 更新用户信息
	private void updateUserInfo() {
		mUser.loginSn = set_name.getText().toString();
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("id", mUserId);
		params.put("sex", mUser.sex);
		params.put("loginSn", mUser.loginSn);
		params.put("address", mUser.address);
		params.put("headphoto", mUser.headphoto);
		mSpUtils.setString(Constants.LOGINSN, mUser.loginSn);
		mSpUtils.setString(Constants.HEADIMG, mUser.headphoto);
		if (mDialog == null) {
			mDialog = new ProgressDialog(this);
		}
		UiUtils.showSimpleProcessDialog(mDialog, "更新数据中，请稍后....");
		try {
			HttpRequestProxy.sendAsyncPost(Constants.Url.UPDATE, params, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					UiUtils.closeProcessDialog(mDialog);
					Toast.makeText(PersonalSettingDataActivity.this, "保存数据失败，稍后再试!", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					// 更新本地缓存的用户信息
					mSpUtils.setString(Constants.ALL_USERINFO, new Gson().toJson(mUser));
					ConversactionListDbManager.getInstance().updateUserInfo(mUser);
					UiUtils.closeProcessDialog(mDialog);
					finish();
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 选择图片裁剪
	 */
	private void startImagePick() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent();
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			startActivityForResult(Intent.createChooser(intent, "选择图片"), REQ_PICK_CODE);
		} else {
			intent = new Intent(Intent.ACTION_PICK, Images.Media.EXTERNAL_CONTENT_URI);
			intent.setType("image/*");
			startActivityForResult(Intent.createChooser(intent, "选择图片"), REQ_PICK_CODE);
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
			Toast.makeText(this, "无法保存上传的头像，请检查SD卡是否挂载", Toast.LENGTH_SHORT).show();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_TAKE_CODE:// 拍照完成之后会来到这个地方
			 startActionCrop(origUri);// 拍照后裁剪
			break;
		case REQ_PICK_CODE:// 从相册选择好之后的结果
			startActionCrop(data.getData());// 选图后裁剪
			break;
		case REQ_ZOOM_CODE:// 缩放完成之后会来到这个地方
			// 上传头像
			uploadHeadPic();
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	//处理图片的上传
	private void uploadHeadPic() {
		 // 获取头像缩略图
        if (ValidateUtils.isValidate(protraitPath) && protraitFile.exists()) {
            protraitBitmap = ImageUtils
                    .loadImgThumbnail(protraitPath, 200, 200);
        } else {
            Toast.makeText(this, "图像不存在，上传失败", Toast.LENGTH_SHORT).show();
        }
		if (protraitBitmap!=null) {
			List<File> files = new ArrayList<File>();
			files.add(protraitFile);
			HttpRequestProxy.uploadFileAsync(HttpRequestProxy.ImagePathTypeUserHeadImage, files,
					new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> responseinfo) {
							PersonalSettingDataActivity.this.set_touxiang.setImageBitmap(protraitBitmap);
							PersonalSettingDataActivity.this.setBackground.setImageBitmap(protraitBitmap);
							LogUtils.i(responseinfo.result);
							try {
								JSONArray jsonArray=new JSONArray(responseinfo.result);
								mUser.headphoto=jsonArray.optJSONObject(0).optString("downUrl");
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(HttpException httpexception, String s) {

						}
					});
		}
	}

	@Override
	protected void initData() {
		mSpUtils = SpUtils.getInstance(this);
		String userInfo = mSpUtils.getString(Constants.ALL_USERINFO);
		this.mUserId = mSpUtils.getString(Constants.USER_ID);
		LogUtils.i(userInfo);
		mUser = new Gson().fromJson(userInfo, AppUser.class);
		// 初始化修改的用户信息
		if (mUser != null) {
			set_name.setText(mUser.loginSn); // 加载昵称
			BitmapUtil.display(set_touxiang, mUser.headphoto); // 加载头像
			set_sex.setText(mUser.sex);
			BitmapUtil.display(setBackground, mUser.headphoto);
			address.setText(mUser.address);
		}
	}

	@Override
	public void onSelected(String selcted) {
		this.address.setText(selcted);
		this.mUser.address = selcted;
	}

}
