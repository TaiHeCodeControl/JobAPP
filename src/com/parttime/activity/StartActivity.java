package com.parttime.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.chatuidemo.domain.User;
import com.easemob.util.EMLog;
import com.parttime.application.PartTimeApplication;
import com.parttime.broadcast.SDKReceiver;
import com.parttime.constant.Constant;
import com.parttime.data.PartTimeDB;
import com.parttime.fragment.MainFragment;
import com.parttime.msg.ErrorMsgSvr;
import com.parttime.parttimejob.R;
import com.parttime.service.LoginStatus;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.BaiduMap_GetLatLng_Utils;
import com.parttime.utils.MD5Encrypt;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;

public class StartActivity extends Activity {

	private SharedPreferences sharedPreferences;
	private int flag;
	private Context mContext = null;
	private String username;
	private String pwd;
	private HashMap<String, String> map = null;
	private PartTimeDB partTimeDB = null;

	private static final int sleepTime = 2000;
	/**
	 * 通过百度地图得到当前经纬度坐标存储下
	 * */
	private SDKReceiver mReceiver;
	LocationClient mLocClient;// 定位相关
	public MyLocationListenner myListener = new MyLocationListenner();

	private boolean progressShow;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.start);
		mContext = this;
		Utils.ClearPageFlagToShared(mContext);
		pd = new ProgressDialog(StartActivity.this);
		pd.setCanceledOnTouchOutside(false);

		/**
		 * ----------------------------------------地图获取当前经纬度坐标部分----------------
		 * -----------------------------------------------
		 */
		// 注册 SDK 广播监听者,使用地图之前需要判断key是否正确和网络是否可用
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);

		// 定位初始化
		mLocClient = new LocationClient(mContext);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		/**
		 * ----------------------------------------地图获取当前经纬度坐标部分----------------
		 * -----------------------------------------------
		 */

		/* 确定是否第一次登录，记录下来状态 */
		SharedPreferences mPrefs = getSharedPreferences("first_in", 0);
		boolean bFirstStart = mPrefs.getBoolean(Constant.PREF_FIRST_RUN, true);
		if (bFirstStart) { // if first launched, show the introduction page
			// 在IntroActivity中改变PREF_FIRST_RUN的值
			Intent intent = new Intent(StartActivity.this, IntroActivity.class);
			this.startActivity(intent);
			finish();
			return;
		}

		partTimeDB = PartTimeDB.getInstance(mContext);
		map = new HashMap<String, String>();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
					if (sharedPreferences.getString("login_username", "").equals("")) {
						Intent intent = new Intent();
						intent.setClass(mContext, MainFragment.class);
						startActivity(intent);
						finish();
					} else {
						username = sharedPreferences.getString("login_username", "");
						pwd = sharedPreferences.getString("login_pwd", "");
						map.put("username", username);
						map.put("pwd", MD5Encrypt.encryption(pwd));
						// // new LoginTask().execute(map);

						if (Submit.isNetworkAvailable(mContext)) {
							Log.e("pao", "pao dao zhe le");
							// TODO
//							LoginHx(username, pwd);
							// new LoginTask().execute(map);
						} else {
							runOnUiThread(new Runnable() {
								public void run() {
									Utils.ShowToast(mContext, "没有可用网络");
									StartActivity.this.finish();
								}
							});
						}

					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();

		new Thread(new Runnable() {
			public void run() {
				if (sharedPreferences.getString("login_username", "").equals("")) {

					long start = System.currentTimeMillis();
					long costTime = System.currentTimeMillis() - start;
					// 等待sleeptime时长
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					Intent intent = new Intent();
					intent.setClass(mContext, MainFragment.class);
					startActivity(intent);
					finish();

				} else {
					try {
						Thread.sleep(sleepTime);

						username = sharedPreferences.getString("login_username", "");
						pwd = sharedPreferences.getString("login_pwd", "");
						map.put("username", username);
						map.put("pwd", MD5Encrypt.encryption(pwd));
					} catch (InterruptedException e) {
					}
					if (Submit.isNetworkAvailable(mContext)) {
						// 更新ui
						runOnUiThread(new Runnable() {
							public void run() {
								progressShow = true;
								pd.setOnCancelListener(new OnCancelListener() {

									@Override
									public void onCancel(DialogInterface dialog) {
										progressShow = false;
									}
								});
								pd.setMessage("正在登录...");
								pd.show();
//								LoginHx(username, pwd);
								 new LoginTask().execute(map);
							}
						});
					} else {
						// 更新ui
						runOnUiThread(new Runnable() {
							public void run() {
								Utils.ShowToast(mContext, "没有可用网络");
							}
						});
						// StartActivity.this.finish();
					}
				}
			}
		}).start();

	}

	/**
	 * 登录的异步
	 * 
	 * @author huxixi
	 * 
	 */
	public class LoginTask extends AsyncTask<HashMap<String, String>, Void, Boolean> {

		private int errcode;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(HashMap<String, String>... params) {

			int iresult = SvrOperation.Login(mContext, params[0]);
			if (iresult != SvrInfo.SVR_RESULT_SUCCESS) {
				errcode = iresult;
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result == false) {
				int errmsg = ErrorMsgSvr.ErrorMsg(errcode);
				Utils.ShowToast(mContext, errmsg);
				return;
			}

			if (!StartActivity.this.isFinishing())
				pd.dismiss();

			boolean is_exist_flag = partTimeDB.IsExistLoginInfo(username, mContext);
			if (is_exist_flag) {
				HashMap<String, String> info_map = partTimeDB.getLoginInfo(mContext, username);
				String type = info_map.get("type");
				if (type.equals("0")) {
					sharedPreferences.edit().putString("userflag", "personal").commit();
				} else if (type.equals("1")) {
					sharedPreferences.edit().putString("userflag", "company").commit();
				}

				Intent intent = new Intent(StartActivity.this, MainFragment.class);
				LoginStatus.getLoginStatus().setLogin(true);
				mContext.startActivity(intent);
				StartActivity.this.finish();
			}

			return;
		}

	}

	/**
	 * 百度地图定位功能，获取当前的经纬度坐标，存储下 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null)
				return;
			LatLng local = new LatLng(location.getLatitude(), location.getLongitude());
			BaiduMap_GetLatLng_Utils.mLocal_LatLng = local;

		}

		@Override
		public void onReceivePoi(BDLocation location) {

		}
	}

	/**
	 * 登录环信
	 * 
	 * @param account
	 * @param pwd
	 */
	private void LoginHx(final String account, final String pwd) {
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(account, pwd, new EMCallBack() {

			@Override
			public void onSuccess() {

				// 登陆成功，保存用户名密码
				PartTimeApplication.getInstance().setUserName(account);
				PartTimeApplication.getInstance().setPassword(pwd);
				// 更新ui
				runOnUiThread(new Runnable() {
					public void run() {
						pd.setMessage("正在获取好友列表...");
						// Toast.makeText(getApplicationContext(),
						// "正在登录...", 200).show();
					}
				});
				try {
					// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
					// ** manually load all local groups and
					// conversations in case we are auto login
					// EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();

					// demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
					List<String> usernames = EMContactManager.getInstance().getContactUserNames();
					EMLog.d("roster", "contacts size: " + usernames.size());
					Map<String, User> userlist = new HashMap<String, User>();
					for (String username : usernames) {
						User user = new User();
						user.setUsername(username);
						// setUserHearder(username, user);
						userlist.put(username, user);
					}
					// 添加user"申请与通知"
					// User newFriends = new User();
					// newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
					// newFriends.setNick("申请与通知");
					// newFriends.setHeader("");
					// userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);

					// 存入内存
					PartTimeApplication.getInstance().setContactList(userlist);
					// 存入db
					UserDao dao = new UserDao(StartActivity.this);
					List<User> users = new ArrayList<User>(userlist.values());
					dao.saveContactList(users);

				} catch (Exception e) {
					e.printStackTrace();
					// 取好友或者群聊失败，不让进入主页面，也可以不管这个exception继续进到主页面
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							PartTimeApplication.getInstance().logout(null);
							Toast.makeText(getApplicationContext(), "登录失败: 获取好友失败", 1).show();
						}
					});
					return;
				}

				new LoginTask().execute(map);
				// 进入主页面
				// startActivity(new Intent(StartActivity.this,
				// MainFragment.class));
				// finish();
			}

			@Override
			public void onError(int code, final String message) {
				// TODO Auto-generated method stub
				// Log.e("hu", "登陆失败   code   " + code + "   message   " +
				// message);
				if (!progressShow) {
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message, Toast.LENGTH_SHORT).show();
						Utils.intent2Class(mContext, LoginActivity.class);
						StartActivity.this.finish();
					}
				});
			}

			@Override
			public void onProgress(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.e("hu", "正在登陆。。。。");

			}
		});
	}

	@Override
	protected void onDestroy() {
		// 取消监听 SDK 广播
		unregisterReceiver(mReceiver);
		// 退出时销毁定位
		if (mLocClient != null)
			mLocClient.stop();
		super.onDestroy();
	}

}
