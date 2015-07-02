package com.parttime.application;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.baidu.mapapi.SDKInitializer;
import com.easemob.EMCallBack;
import com.easemob.chatuidemo.domain.User;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.parttime.constant.Constant;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

/**
 * @function:退出应用程序的application <br>
 *                              初始化imageloader等数据 <br>
 *                              创建文件夹于手机sd卡中（本软件的根目录、临时目录、图片目录、下载目录等）
 * @author huxixi
 * 
 */
public class PartTimeApplication extends Application {

	public static Context applicationContext;
	private static PartTimeApplication instance;
	// login user name
	public final String PREF_USERNAME = "username";

	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";
	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();

	@Override
	public void onCreate() {
		super.onCreate();

		applicationContext = this;
		instance = this;

		/**
		 * this function will initialize the HuanXin SDK
		 * 
		 * @return boolean true if caller can continue to call HuanXin related
		 *         APIs after calling onInit, otherwise false.
		 * 
		 *         环信初始化SDK帮助函数
		 *         返回true如果正确初始化，否则false，如果返回为false，请在后续的调用中不要调用任何和环信相关的代码
		 * 
		 *         for example: 例子：
		 * 
		 *         public class DemoHXSDKHelper extends HXSDKHelper
		 * 
		 *         HXHelper = new DemoHXSDKHelper();
		 *         if(HXHelper.onInit(context)){ // do HuanXin related work }
		 */
		hxSDKHelper.onInit(applicationContext);

		/** 初始化imageloader */
		initImageLoader(getApplicationContext());

		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);

		String externalStorageDir = Environment.getExternalStorageDirectory().toString();
		File dir = null;
		String strDir = externalStorageDir + Constant.PARTTIMEJOB_PATH;
		dir = new File(strDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		strDir = externalStorageDir + Constant.PARTTIMEJOB_IMAGE;
		dir = new File(strDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		strDir = externalStorageDir + Constant.PARTTIMEJOB_DOWNLOAD;
		dir = new File(strDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		strDir = externalStorageDir + Constant.PARTTIMEJOB_TEMP_PATH;
		dir = new File(strDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File nomediaFile = new File(externalStorageDir + Constant.PARTTIMEJOB_PATH + ".nomedia");
		if (!nomediaFile.exists()) {
			try {
				nomediaFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		dir = null;
	}

	public static PartTimeApplication getInstance() {
		return instance;
	}

	/**
	 * 获取内存中好友user list
	 * 
	 * @return
	 */
	public Map<String, User> getContactList() {
		return hxSDKHelper.getContactList();
	}

	/**
	 * 设置好友user list到内存中
	 * 
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
		hxSDKHelper.setContactList(contactList);
	}

	/**
	 * 获取当前登陆用户名
	 * 
	 * @return
	 */
	public String getUserName() {
		return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 * 
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名
	 * 
	 * @param user
	 */
	public void setUserName(String username) {
		hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 * 
	 * @param pwd
	 */
	public void setPassword(String pwd) {
		hxSDKHelper.setPassword(pwd);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
		hxSDKHelper.logout(emCallBack);
	}

	/**
	 * 初始化imageloader
	 * 
	 * @param context
	 */
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		// Create global configuration and initialize ImageLoader with this
		// configuration
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPoolSize(3)
		// default
				.threadPriority(Thread.NORM_PRIORITY - 1)
				// default
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				// default
				.denyCacheImageMultipleSizesInMemory().memoryCache(new LruMemoryCache(2 * 1024 * 1024)).memoryCacheSize(2 * 1024 * 1024).memoryCacheSizePercentage(13)
				// default
				.discCacheSize(50 * 1024 * 1024).discCacheFileCount(100).discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
				.imageDownloader(new BaseImageDownloader(context)) // default
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}

}