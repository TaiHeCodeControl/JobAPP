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
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.parttime.data.DBManager;
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
import com.parttime.view.ClearEditText;

/**
 * 登录页面
 * 
 * @author huxixi
 * 
 */
public class LoginActivity extends Activity {

	public static final int LOGIN_BTN_INT = 0;
	public static final int FORGET_PWD_INT = 1;
	public static final int REGISTER_INT = 2;
	public static final int FINISH_LOGIN_PAGE = 3;
	/* 等待框部分 */
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;
	/**
	 * 登录按钮
	 */
	private Button login_btn;
	/**
	 * 头像的imageview
	 */
	// private MaskImage head_image;
	/**
	 * 账号输入框
	 */
	private ClearEditText account_edit;
	/**
	 * 密码输入框
	 */
	private ClearEditText pwd_edit;
	/**
	 * 是否记住密码的checkbox
	 */
	// private CheckBox pwd_checkbox;
	/**
	 * 忘记密码的textview
	 */
	private TextView login_forget_pwd;
	/**
	 * 注册的textview
	 * 
	 */
	private RelativeLayout register_text;
	/**
	 * 返回键imageview
	 * 
	 */

	private String remember_int = "0"; // 0表示没有记住密码，1表示的是记住密码

	private Context context = null;
	private Intent intent = null;
	private int back = 0;

	String account;
	String pwd;
	private String remember;

	private SharedPreferences sharedPreferences;

	private PartTimeDB partTimeDB = null;

	public DBManager dbManager = null;
	private HashMap<String, String> login_map = new HashMap<String, String>();
	/**
	 * 通过百度地图得到当前经纬度坐标存储下
	 * */
	private SDKReceiver mReceiver;
	LocationClient mLocClient;// 定位相关
	public MyLocationListenner myListener = new MyLocationListenner();

	private boolean progressShow;
	private ProgressDialog pd;

	// 浏览模式
	private Button login_ger, login_qiy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		context = this;
		Utils.ClearPageFlagToShared(context);
		dbManager = new DBManager(context);
		dbManager.openDatabase();
		dbManager.closeDatabase();
		pd = new ProgressDialog(LoginActivity.this);
		pd.setCanceledOnTouchOutside(false);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

		partTimeDB = PartTimeDB.getInstance(context);
		partTimeDB.ClearJobInfo();

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
		mLocClient = new LocationClient(context);
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

		initView();

	}

	private void initView() {

		intent = new Intent(LoginActivity.this, MainFragment.class);

		account_edit = (ClearEditText) findViewById(R.id.account_edit);
		pwd_edit = (ClearEditText) findViewById(R.id.pwd_edit);
		login_forget_pwd = (TextView) findViewById(R.id.login_forget_pwd);
		register_text = (RelativeLayout) findViewById(R.id.regist_login_btn_block);
		/* 等待对话框部分 */
		WaitingText = (TextView) findViewById(R.id.Waiting_text);
		WaitingDlg = (RelativeLayout) findViewById(R.id.Waiting_dlg);

		login_btn = (Button) findViewById(R.id.login_btn);
		login_btn.setOnClickListener(new MyClickListener(LOGIN_BTN_INT));
		login_forget_pwd.setOnClickListener(new MyClickListener(FORGET_PWD_INT));
		register_text.setOnClickListener(new MyClickListener(REGISTER_INT));

		login_ger = (Button) findViewById(R.id.login_ger);
		login_ger.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sharedPreferences.edit().putString("userflag", "personal").commit();
				context.startActivity(intent);
				finish();
			}
		});
		login_qiy = (Button) findViewById(R.id.login_qiy);
		login_qiy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sharedPreferences.edit().putString("userflag", "company").commit();
				context.startActivity(intent);
				finish();
			}
		});
	}

	public void ShowWaiting(String text) {
		WaitingText.setText(text);
		WaitingDlg.setVisibility(View.VISIBLE);
	}

	public void HideWaiting() {
		WaitingDlg.setVisibility(View.GONE);
	}

	public static Bitmap toRoundCorner(Bitmap bitmap, float pixels) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int valth = 0;
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		if (width >= height) {
			valth = height;
		} else {
			valth = width;
		}
		final Rect rect = new Rect(0, 0, valth, valth);
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);

		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public class MyClickListener implements View.OnClickListener {

		int index;

		public MyClickListener(int index) {
			this.index = index;

		}

		@Override
		public void onClick(View arg0) {
			switch (index) {
			case LOGIN_BTN_INT:

				/*****************/
				account = account_edit.getText().toString();
				pwd = pwd_edit.getText().toString();

				// Utils.intent2Class(context, MainFragment.class);
				// finish();

				if (account.equals("")) {
					Utils.ShowToast(context, "账号不能为空");
				} else if (pwd.equals("")) {
					Utils.ShowToast(context, "密码不能为空");
				} else {
					login_map.put("username", account);
					login_map.put("pwd", MD5Encrypt.encryption(pwd));
					login_map.put("remember", remember_int);

					if (Submit.isNetworkAvailable(context)) {

						progressShow = true;

						pd.setOnCancelListener(new OnCancelListener() {

							@Override
							public void onCancel(DialogInterface dialog) {
								progressShow = false;
							}
						});
						// pd.setMessage(getString(R.string.Is_landing));
						pd.setMessage("正在登录...");
						pd.show();

						LoginHx(account, pwd);
						// new LoginTask().execute(login_map);
					} else {
						Utils.ShowToast(context, "没有可用网络");
						// LoginActivity.this.finish();
					}

				}
				/*******************/

				break;
			case FORGET_PWD_INT:
				intent = new Intent(LoginActivity.this, Login_Forgetpwd.class);
				context.startActivity(intent);
				break;

			case REGISTER_INT:
				intent = new Intent(LoginActivity.this, Register_User.class);
				context.startActivity(intent);
				break;

			case FINISH_LOGIN_PAGE:
				finish();

				break;
			}
		}

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

			int iresult = SvrOperation.Login(context, params[0]);
			if (iresult != SvrInfo.SVR_RESULT_SUCCESS) {
				errcode = iresult;
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			// HideWaiting();

			if (result == false) {
				int errmsg = ErrorMsgSvr.ErrorMsg(errcode);
				Utils.ShowToast(context, errmsg);
				return;
			}
			if (!LoginActivity.this.isFinishing())
				pd.dismiss();

			sharedPreferences.edit().putString("login_username", account).commit();
			sharedPreferences.edit().putString("login_pwd", pwd).commit();
			boolean is_exist_flag = partTimeDB.IsExistLoginInfo(account, context);

			if (is_exist_flag) {
				HashMap<String, String> info_map = partTimeDB.getLoginInfo(context, account);
				String type = info_map.get("type");
				if (type.equals("0")) {
					sharedPreferences.edit().putString("userflag", "personal").commit();

				} else if (type.equals("1")) {
					sharedPreferences.edit().putString("userflag", "company").commit();
				}
				LoginStatus.getLoginStatus().setLogin(true);
				context.startActivity(intent);
				LoginActivity.this.finish();
			}
			return;
		}
	}

	/**
	 * 登录环信
	 * 
	 * @param account
	 * @param pwd
	 */
	public void LoginHx(final String account, final String pwd) {
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(account, pwd, new EMCallBack() {

			@Override
			public void onSuccess() {

				if (!progressShow) {
					return;
				}
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
					UserDao dao = new UserDao(LoginActivity.this);
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
				new LoginTask().execute(login_map);
				// // 进入主页面
				// startActivity(new Intent(StartActivity.this,
				// MainFragment.class));
				// finish();
			}

			@Override
			public void onError(int code, final String message) {
				// TODO Auto-generated method stub
				if (!progressShow) {
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message, Toast.LENGTH_SHORT).show();
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			back++;
			if (back == 1) {

				//Toast.makeText(this, "再按一次退出蛋壳儿", 5).show();
				finish();

				new Handler().postDelayed(new Runnable() {
					public void run() {
						back = 0;
					}

				}, 4000);
				return true;
			}
			if (back == 2) {
				this.finish();
				return true;
			}
			break;
		}
		return false;
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
