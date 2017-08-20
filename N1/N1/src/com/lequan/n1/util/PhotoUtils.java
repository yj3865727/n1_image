package com.lequan.n1.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.lequan.n1.view.AddressPopupWindow;
import com.lequan.n1.view.HeadPhotoPopupWindow;
import com.lequan.n1.view.PicToReportPopupWindow;
import com.lequan.n1.view.SelectPicPopupWindow;
import com.lequan.n1.view.SettingSelectPicPopupWindow;
import com.lequan.n1.view.ToReportPopupWindow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class PhotoUtils {

	/**
	 * 因为处理不同
	 * 
	 * @param takePhotoCode
	 *            Uri originalUri = data.getData();
	 *            image=ImageUtil.getBitmapFromUrl(originalUri.toString());
	 ********************************************************************************** 
	 * @param pickPhotoCode
	 *            Bundle extras = data.getExtras(); image = (Bitmap)
	 *            extras.get("data");
	 * @param tempFile
	 *            拍照时的临时文件 需要zoom时
	 **/
	public static boolean getPhotoDialog(final Activity activity, final int takePhotoCode, final int pickPhotoCode,
			final File tempFile) {
		final CharSequence[] items = { "相册", "拍照" };
		AlertDialog dlg = new AlertDialog.Builder(activity).setTitle("选择图片")
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == 1) {
							Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
							getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
							activity.startActivityForResult(getImageByCamera, takePhotoCode);
						} else {
							Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
							getImage.addCategory(Intent.CATEGORY_OPENABLE);
							getImage.setType("image/jpeg");
							activity.startActivityForResult(getImage, pickPhotoCode);
						}
					}
				}).create();
		dlg.show();
		return true;
	}
	public static PicToReportPopupWindow getPicToReportPopupWindow(Context context, OnClickListener itemsOnClick,
			View viewAttach) {
		PicToReportPopupWindow menuWindow = new PicToReportPopupWindow(context, itemsOnClick);
		menuWindow.showAtLocation(viewAttach, Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
		return menuWindow;
	}

	public static ToReportPopupWindow getToReportPopupWindow(Context context, OnClickListener itemsOnClick,
			View viewAttach) {
		ToReportPopupWindow menuWindow = new ToReportPopupWindow(context, itemsOnClick);
		menuWindow.showAtLocation(viewAttach, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
		return menuWindow;
	}
	public static SettingSelectPicPopupWindow settinggetPicPopupWindow(Context context, OnClickListener itemsOnClick,
			View viewAttach) {
		SettingSelectPicPopupWindow menuWindow = new SettingSelectPicPopupWindow(context, itemsOnClick);
		menuWindow.showAtLocation(viewAttach, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
		return menuWindow;
	}

	public static AddressPopupWindow addPopupWindow(Context context, OnClickListener itemsOnClick, View viewAttach) {
		AddressPopupWindow menuWindow = new AddressPopupWindow(context, itemsOnClick);
		menuWindow.showAtLocation(viewAttach, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
		return menuWindow;
	}

	public static HeadPhotoPopupWindow headgetPicPopupWindow(Context context, OnClickListener itemsOnClick,
			View viewAttach) {
		HeadPhotoPopupWindow menuWindow = new HeadPhotoPopupWindow(context, itemsOnClick);
		menuWindow.showAtLocation(viewAttach, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
		return menuWindow;
	}

	public static SelectPicPopupWindow getPicPopupWindow(Context context, OnClickListener itemsOnClick,
			View viewAttach) {
		SelectPicPopupWindow menuWindow = new SelectPicPopupWindow(context, itemsOnClick);
		menuWindow.showAtLocation(viewAttach, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
		return menuWindow;
	}

	public static boolean takePhoto(Activity activity, int takePhotoCode, File tempFile) {
		Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
		getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
		activity.startActivityForResult(getImageByCamera, takePhotoCode);
		return true;
	}

	public static boolean takePhoto(Activity activity, int takePhotoCode, Uri uri) {
		Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
		getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		activity.startActivityForResult(getImageByCamera, takePhotoCode);
		return true;
	}

	public static boolean takePhoto(Activity activity, int takePhotoCode) {
		Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
		activity.startActivityForResult(getImageByCamera, takePhotoCode);
		return true;
	}

	public static boolean pickPhoto(Activity activity, int imageCode) {
		Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
		getImage.addCategory(Intent.CATEGORY_OPENABLE);
		getImage.setType("image/jpeg");
		activity.startActivityForResult(getImage, imageCode);
		return true;
	}

	/**
	 * 拍照获取图片的方式 用于切割的图片大小被限制在500,500
	 * 
	 * @param context
	 * @param zoomCode
	 * @param temppath
	 *            拍照前生成的临时路劲
	 * @return 新的路劲
	 */
	public static String onPhotoFromCamera(Activity context, int zoomCode, String temppath, int aspectX, int aspectY) {
		try {
			Bitmap btp = getLocalImage(new File(temppath), 1000, 1000);
			String temp = temppath.substring(0, temppath.lastIndexOf(".")) + "_temp.jpg";
			// 将文件压缩后存储
			compressImage(btp, new File(temp), 30);
			photoZoom(context, Uri.fromFile(new File(temp)), Uri.fromFile(new File(temppath)), zoomCode, aspectX,
					aspectY);
		} catch (Exception e) {
			Toast.makeText(context, "图片加载失败", 1000).show();
		}

		return temppath;
	}

	/**
	 * 图片切割完调用 如果还需要 Bitmap 调用getLocalImage
	 * 
	 * @param path
	 * @param rw
	 * @param rh
	 * @param compress
	 * @return
	 */
	public static File onPhotoZoom(String path, int rw, int rh, int compress) {

		File f = new File(path);
		Bitmap btp = PhotoUtils.getLocalImage(f, rw, rh);
		compressImage(btp, f, compress);
		return f;
	}

	/**
	 * 相册获取图片,用于切割的图片大小被限制在500,500
	 * 
	 * @param context
	 * @param zoomCode
	 * @param temppath
	 *            希望生成的路劲
	 * @param data
	 */
	public static void onPhotoFromPick(Activity context, int zoomCode, String temppath, Intent data, int aspectX,
			int aspectY) {
		try {
			Bitmap btp = checkImage(context, data);
			String temp = temppath.substring(0, temppath.lastIndexOf(".")) + "_temp.jpg";
			// 将文件压缩后存储
			compressImage(btp, new File(temp), 30);
			PhotoUtils.photoZoom(context, Uri.fromFile(new File(temp)), Uri.fromFile(new File(temppath)), zoomCode,
					aspectX, aspectY);
		} catch (Exception e) {
			Toast.makeText(context, "图片加载失败", 1000).show();
		}
	}

	/**
	 * data 中检出图片
	 * 
	 * @param activity
	 * @param data
	 * @return
	 */
	public static Bitmap checkImage(Activity activity, Intent data) {
		Bitmap bitmap = null;
		try {
			Uri originalUri = data.getData();
			String path = getRealPathFromURI(activity, originalUri);
			File f = activity.getExternalCacheDir();
			String pp = f.getAbsolutePath();
			if (path.indexOf(pp) != -1) {
				path = path.substring(path.indexOf(pp), path.length());
			}
			bitmap = getLocalImage(new File(path), 1000, 1000);
		} catch (Exception e) {
		} finally {
			return bitmap;
		}
	}

	/**
	 * 通过URI 获取真实路劲
	 * 
	 * @param activity
	 * @param contentUri
	 * @return
	 */
	public static String getRealPathFromURI(Activity activity, Uri contentUri) {
		Cursor cursor = null;
		String result = contentUri.toString();
		String[] proj = { MediaStore.Images.Media.DATA };
		cursor = activity.managedQuery(contentUri, proj, null, null, null);
		if (cursor == null)
			throw new NullPointerException("reader file field");
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			cursor.moveToFirst();
			result = cursor.getString(column_index);
			if (Integer.parseInt(Build.VERSION.SDK) < 14) {
				cursor.close();
			}
		}
		return result;
	}

	/**
	 * 图片压缩 上传图片时建议compress为30
	 * 
	 * @param bm
	 * @param f
	 */
	public static void compressImage(Bitmap bm, File f, int compress) {
		if (bm == null)
			return;
		File file = f;
		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bm.compress(android.graphics.Bitmap.CompressFormat.JPEG, compress, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 由本地文件获取希望大小的文件
	 * 
	 * @param f
	 * @return
	 */
	public static Bitmap getLocalImage(File f, int swidth, int sheight) {
		File file = f;
		if (file.exists()) {
			try {
				file.setLastModified(System.currentTimeMillis());
				FileInputStream in = new FileInputStream(file);

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(in, null, options);
				int sWidth = swidth;
				int sHeight = sheight;
				int mWidth = options.outWidth;
				int mHeight = options.outHeight;
				int s = 1;
				while ((mWidth / s > sWidth * 2) || (mHeight / s > sHeight * 2)) {
					s *= 2;
				}
				options = new BitmapFactory.Options();
				options.inSampleSize = s;
				options.inPreferredConfig = Bitmap.Config.RGB_565;
				options.inPurgeable = true;
				options.inInputShareable = true;
				try {
					// 4. inNativeAlloc 属性设置为true，可以不把使用的内存算到VM里
					BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(options, true);
				} catch (Exception e) {
				}
				in.close();
				// 再次获取
				in = new FileInputStream(file);
				Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
				in.close();
				return bitmap;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} catch (Error e) {
				System.gc();
				return null;
			}
		}
		return null;
	}

	/**
	 * @param uri
	 *            数据源
	 * @param outUri
	 *            输出数据
	 * @param aspectX
	 * @param aspectY
	 */
	public static void photoZoom(Activity activity, Uri uri, Uri outUri, int photoResoultCode, int aspectX,
			int aspectY) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		if (aspectY > 0) {
			intent.putExtra("aspectX", aspectX);
			intent.putExtra("aspectY", aspectY);
		}
		intent.putExtra("scale", aspectX == aspectY);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("scaleUpIfNeeded", true);// 去黑边
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
		activity.startActivityForResult(intent, photoResoultCode);
	}

	/**
	 * 保存zoom之后的图片
	 * 
	 * @param data
	 *            zoom后的intent
	 * @param context
	 *            上下文
	 * @return
	 */
	public static Bitmap getZoomBitMap(Intent data, Context context) {
		try {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap bitmap = extras.getParcelable("data");
				return bitmap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "出现未知异常,请尝试其他图片", Toast.LENGTH_SHORT).show();
		}
		return null;
	}
}
