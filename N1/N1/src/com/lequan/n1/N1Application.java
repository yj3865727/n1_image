package com.lequan.n1;

import java.io.File;

import com.lequan.n1.manager.ConversactionListDbManager;
import com.lequan.n1.util.BitmapUtil;
import com.lequan.n1.util.UiUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import io.rong.imkit.RongIM;

public class N1Application extends Application {

	private static Handler mHandler;
	private static Context mContext;
	private static int mProcessId;
	private static int mMainThreadId;

	@Override
	public void onCreate() {
		super.onCreate();

		mHandler = new Handler();
		mContext = getApplicationContext();
		mProcessId = Process.myPid();
		mMainThreadId = Process.myTid();

		//数据库初始化
		ConversactionListDbManager.init(mContext);
		
		// 初始化融云sdk
		/**
		 * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
		 * io.rong.push 为融云 push 进程名称，不可修改。
		 */
		if (getApplicationInfo().packageName
				.equals(UiUtils.getCurProcessName())
				|| "io.rong.push".equals(UiUtils.getCurProcessName())) {

			/**
			 * IMKit SDK调用第一步 初始化
			 */
			RongIM.init(this);

			initImageLoader(this);
		}
	}

	public static Handler getHandler() {
		return mHandler;
	}

	public static Context getContext() {
		return mContext;
	}

	public static int getProcessId() {
		return mProcessId;
	}

	public static int getMainThreadId() {
		return mMainThreadId;
	}

	/** 初始化ImageLoader */
	private static void initImageLoader(Context context) {
		File cacheDir = new File(BitmapUtil.cachePath);// 获取到缓存的目录地址
		Log.d("cacheDir", cacheDir.getPath());
		// 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.memoryCacheExtraOptions(480, 800)
				// 即保存的每个缓存文件的最大长宽
				.threadPoolSize(3)
				// 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024)
				.discCacheSize(50 * 1024 * 1024)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCacheFileCount(100)
				// 缓存的File数量
				.discCache(new UnlimitedDiscCache(cacheDir))
				// 自定义缓存路径
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(
						new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // 超时时间
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);// 全局初始化此配置
	}
}
