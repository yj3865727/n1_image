package com.lequan.n1.util;

import com.lequan.n1.activity.R;

import android.content.Context;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

public class ShareUtils {

	/**
	 * 九宫格效果
	 * 
	 * @param context
	 */
	public static void showShare(Context context) {
		ShareSDK.initSDK(context);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("悬赏相册");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://www.leqtech.me");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("一张照片一块钱");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("N1悬赏相册");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(context.getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");

		// 启动分享GUI
		oks.show(context);
	}

	public static void shareSpecialPlat(Context context, String platName, String content, String[] imagePaths,
			PlatformActionListener listener) {
		ShareSDK.initSDK(context);
		ShareParams sp = new ShareParams();
		// 必须设置
		sp.setTitle("N1悬赏相册");
		sp.setText(content);
		// 设置图片
		if (ValidateUtils.isValidate(imagePaths)) {
			sp.setImagePath(imagePaths[0]);
		}
		// qq空间
		if (platName.equals(QZone.NAME)) {
			// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
			sp.setTitleUrl("http:/www.leqtech.me"); // 标题的超链接
			sp.setSite("N1悬赏相册");
			sp.setSiteUrl("http:/www.leqtech.me");
		}
		// 微信
		if (platName.equals(Wechat.NAME)) {
			sp.setShareType(Platform.SHARE_IMAGE);
			// url仅在微信（包括好友和朋友圈）中使用
			sp.setUrl("http:/www.leqtech.me");
		}
		Platform qzone = ShareSDK.getPlatform(platName);
		qzone.setPlatformActionListener(listener);
		// 执行图文分享
		qzone.share(sp);
	}

}
