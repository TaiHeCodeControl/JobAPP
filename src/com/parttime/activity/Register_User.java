package com.parttime.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.parttime.application.PartTimeApplication;
import com.parttime.constant.Constant;
import com.parttime.msg.ErrorMsgSvr;
import com.parttime.parttimejob.R;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.MD5Encrypt;
import com.parttime.utils.MediaProcess;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;

/**
 * 注册页面
 * 
 * @author huxixi
 * 
 */
public class Register_User extends Activity {

	private Context mContext = null;
	private int errcode = 0;
	private boolean second_flag = true;

	/* 选图部分 */
	private final static int MENU_PHOTO = 101;
	private final static int DATE_DIALOG_ID = 102;

	public static final int BACK_INT = 1;
	public static final int OBTAIN_SMS_CODE = 2;
	public static final int SET_HEAD_IMAGE_INT = 3;
	public static final int REGISTER_INT = 4;
	public final static int SEND_VCODE_INT = 5;
	public final static int AGREE_INT = 6;

	private Intent intent = null;

	private View register_title_include;
	private TextView mtitle_text;
	private TextView mtitle_back_arrow;
	/**
	 * 设置头像的textview
	 */
	private Bitmap mBitmap;
	/**
	 * 头像的imageview
	 */
	private ImageView register_head_image;
	private EditText vcode_edit;
	private EditText account_edit;
	private EditText pwd_edit;
	private EditText again_pwd_edit;
	private EditText refer_code_edit;
	private TextView send_vcode;
	private RadioButton btn_personal_user;
	private RadioButton btn_company_user;
	private CheckBox agree_check;
	private TextView agree_text;
	private Button register_btn;
	private RadioGroup register_radiogroup;

	/**
	 * 个人用户和企业用户的标志
	 */
	private String user_sign = "个人";

	private HashMap<String, String> register_map = null;
	private HashMap<String, String> vcode_map = null;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register_user1);
		mContext = this;
		register_map = new HashMap<String, String>();
		pd = new ProgressDialog(this);

		initView();
	}

	private void initView() {
		register_title_include = (View) findViewById(R.id.register_include);

		mtitle_text = (TextView) register_title_include.findViewById(R.id.mtitle_text);
		mtitle_back_arrow = (TextView) register_title_include.findViewById(R.id.mtitle_back_arrow);
		mtitle_text.setText("注册");

		register_head_image = (ImageView) findViewById(R.id.register_head_image);
		register_head_image = (ImageView) findViewById(R.id.register_head_image);

		send_vcode = (TextView) findViewById(R.id.send_vcode);
		vcode_edit = (EditText) findViewById(R.id.account_edit_code);
		// again_pwd_edit=(EditText)findViewById(R.id.again_pwd_edit);
		account_edit = (EditText) findViewById(R.id.account_edit);
		pwd_edit = (EditText) findViewById(R.id.account_edit_pwd);
		// refer_code_edit =(EditText)findViewById(R.id.refer_code_edit);
		register_radiogroup = (RadioGroup) findViewById(R.id.register_radiogroup);
		btn_personal_user = (RadioButton) findViewById(R.id.btn_personal_user);
		btn_company_user = (RadioButton) findViewById(R.id.btn_company_user);
		agree_check = (CheckBox) findViewById(R.id.agree_check);
		agree_text = (TextView) findViewById(R.id.agree_text);
		agree_text.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线

		register_btn = (Button) findViewById(R.id.register_btn);

		register_head_image.setOnClickListener(new MyClickListener(SET_HEAD_IMAGE_INT));
		register_btn.setOnClickListener(new MyClickListener(REGISTER_INT));
		send_vcode.setOnClickListener(new MyClickListener(SEND_VCODE_INT));
//		agree_text.setOnClickListener(new MyClickListener(AGREE_INT));
		mtitle_back_arrow.setOnClickListener(new MyClickListener(BACK_INT));

		register_radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkId) {
				if (checkId == btn_personal_user.getId()) {
					user_sign = "个人";
				} else {
					user_sign = "企业";
				}

			}
		});

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		second_flag = false;
		this.finish();
	}

	public class MyClickListener implements View.OnClickListener {

		int index;

		public MyClickListener(int index) {
			this.index = index;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			switch (index) {
			case BACK_INT:
				second_flag = false;
				Register_User.this.finish();
				break;
			case SET_HEAD_IMAGE_INT:
				showDialog(MENU_PHOTO);
				break;
			case REGISTER_INT:
				if (!account_edit.getText().toString().equals("")) {
					// 注册的异步
					register_map.put("username", account_edit.getText().toString());

					if (Utils.IsValidPwd(pwd_edit.getText().toString())) {
						register_map.put("password", MD5Encrypt.encryption(pwd_edit.getText().toString()));

						// register_map.put("email",
						// vcode_edit.getText().toString());
						register_map.put("type", user_sign);
						register_map.put("call", account_edit.getText().toString());
						if (!registerHeadPath.equals(""))
							register_map.put("avatar", registerHeadPath);
						if (registerHeadPath == null || registerHeadPath.equals("")) {
							Utils.ShowToast(mContext, "头像还没有上传");
						} else if (agree_check.isChecked()) {
							if (vcode_edit.getText().toString().equals(Utils.Vcode)) {
								if (Submit.isNetworkAvailable(mContext)) {
									String st5 = getResources().getString(R.string.Is_the_registered);
									pd.setMessage(st5);
									pd.show();
									HxResgisterUser();
								} else {
									Utils.ShowToast(mContext, "没有可用网络");
								}
							} else {
								Utils.ShowToast(mContext, "验证码输入不正确");
							}
						} else {
							Utils.ShowToast(mContext, "您还没有同意蛋壳儿协议");
						}
					} else
						Utils.ShowToast(mContext, "密码过于简单，字母数字下划线");
				} else
					Utils.ShowToast(mContext, "账号不能为空");
				// if (registerHeadPath == null || registerHeadPath.equals(""))
				// {
				//
				// Utils.ShowToast(mContext, "头像还没有上传");
				// } else if (agree_check.isChecked()) {
				// if (Submit.isNetworkAvailable(mContext)) {
				// String st5 =
				// getResources().getString(R.string.Is_the_registered);
				// pd.setMessage(st5);
				// pd.show();
				// // new RegisterTask().execute(register_map);
				// HxResgisterUser();
				// } else {
				// Utils.ShowToast(mContext, "没有可用网络");
				// }
				// } else {
				// Utils.ShowToast(mContext, "您还没有同意身边兼职协议");
				// }
				break;
			case SEND_VCODE_INT:
				vcode_map = new HashMap<String, String>();
				String phone = account_edit.getText().toString();
				vcode_map.put("phone", phone);
				new GetVcodeTask().execute(vcode_map);

				break;
			case AGREE_INT:
				Intent intent = new Intent();
				intent.setClass(Register_User.this, EULA_Activity.class);
				Register_User.this.startActivity(intent);
				break;

			}
		}

	}

	/**
	 * 注册的异步
	 * 
	 * @author huxixi
	 * 
	 */
	public class RegisterTask extends AsyncTask<HashMap<String, String>, Void, Boolean> {

		private int errcode;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(HashMap<String, String>... params) {
			int iresult = SvrOperation.Register(mContext, params[0]);
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
			if (!Register_User.this.isFinishing())
				pd.dismiss();
			Register_User.this.finish();
			// Utils.ShowToast(mContext, "注册成功");

			return;
		}

	}

	/**
	 * 环信中的用户注册
	 */

	private void HxResgisterUser() {
		new Thread(new Runnable() {
			public void run() {
				try {
					// 调用sdk注册方法
					EMChatManager.getInstance().createAccountOnServer(account_edit.getText().toString(), pwd_edit.getText().toString());
					runOnUiThread(new Runnable() {
						public void run() {
							if (!Register_User.this.isFinishing()) {

								// 保存用户名
								PartTimeApplication.getInstance().setUserName(account_edit.getText().toString());

								new RegisterTask().execute(register_map);
							}
							// finish();
						}
					});
				} catch (final EaseMobException e) {
					runOnUiThread(new Runnable() {
						private int errorCode;

						public void run() {
							if (!Register_User.this.isFinishing())
								pd.dismiss();
							errorCode = e.getErrorCode();
							if (errorCode == EMError.NONETWORK_ERROR) {
								Toast.makeText(getApplicationContext(), "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
							} else if (errorCode == EMError.USER_ALREADY_EXISTS) {
								Toast.makeText(getApplicationContext(), "用户已存在！", Toast.LENGTH_SHORT).show();
							} else if (errorCode == EMError.UNAUTHORIZED) {
								Toast.makeText(getApplicationContext(), "注册失败，无权限！", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getApplicationContext(), "注册失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 获取短信验证码
	 * 
	 * @author Administrator
	 * 
	 */
	public class GetVcodeTask extends AsyncTask<HashMap<String, String>, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(HashMap<String, String>... params) {
			int iresult = SvrOperation.SendVcode(mContext, params[0]);
			errcode = iresult;
			if (iresult == 200) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				send_vcode.setClickable(false);
				send_vcode.setText("正在获取");
				getCheckNumTime();
			}
			Utils.ShowToast(mContext, Utils.BaseMessage);

			return;
		}

	}

	public Handler msghandler = new Handler() {
		@SuppressLint("NewApi")
		public void handleMessage(Message msg) {
			int number = (Integer) msg.what;
			if (number > 0) {
				send_vcode.setText(String.format(mContext.getResources().getString(R.string.text_get_checknum_time), number));
			} else {
				send_vcode.setClickable(true);
				send_vcode.setText(mContext.getResources().getString(R.string.obtain_verfiy));
			}
		};
	};

	public void getCheckNumTime() {

		new Thread() {
			@Override
			public void run() {
				int numsize = 120;

				while (second_flag) {
					try {
						msghandler.sendEmptyMessage(numsize);
						Thread.sleep(1000);
						if (numsize == 0) {
							second_flag = false;
							break;
						}
						numsize--;

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				msghandler.sendEmptyMessage(numsize);
			}
		}.start();
	}

	/**
	 * ========================================================================
	 * ==================================================
	 */
	/**
	 * ======================================= 通过forrsult获取命令，选取本机图片
	 * ==========================================================
	 */
	/**
	 * ========================================================================
	 * ==================================================
	 */
	private MediaProcess myMedia = null;
	DialogInterface.OnClickListener ImageSelDlgHandle = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {

			switch (myMedia.DialogId) {
			case MENU_PHOTO:
				switch (which) {
				case 0:
					myMedia.Start2GetImagePhoto(which);
					break;
				case 1:
					myMedia.Start2GetImagePhoto(which);
					break;
				}
				break;
			}
		}

	};

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
		case MENU_PHOTO:
			if (myMedia == null) {
				myMedia = new MediaProcess(this);
			}
			return myMedia.SelectMethod(R.array.attach_pic_no_del, ImageSelDlgHandle, MENU_PHOTO);
		default:
			return null;
		}
	}

	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
		default:
			if (myMedia == null) {
				myMedia = new MediaProcess(this);
			}
			myMedia.setDlgId(id);
			break;
		}
	}

	String registerHeadPath = "";

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			Utils.ShowLog("bai", ">>>>>>>>>>> fail due to resultCode=" + resultCode);
			return;
		}

		switch (requestCode) {
		case Constant.MESSENGE_REQUEST_CODE_TAKE_PHOTO:
		case Constant.MESSENGE_REQUEST_CODE_ATTACH_PHOTO:
			try {
				Uri photoUri;
				if (requestCode == Constant.MESSENGE_REQUEST_CODE_ATTACH_PHOTO) {
					photoUri = data.getData();
				} else {
					File out = new File(Environment.getExternalStorageDirectory() + Constant.PARTTIMEJOB_TEMP_PATH, "job_temp.jpg");
					photoUri = Uri.fromFile(out);
				}
				Log.v("bai", ">>>>>>>>>>>>>>>>>  photo uri:" + photoUri);
				if (photoUri == null) {
					return;
				}
				myMedia.setCropRange(120, 120);
				myMedia.startPhotoZoom(photoUri, Constant.MESSENGE_REQUEST_CODE_CROP_PHOTO);
			} catch (Exception e) {
				Log.e("bai", e.toString());
			}
			break;
		case Constant.MESSENGE_REQUEST_CODE_CROP_PHOTO:
			final Bundle extras = data.getExtras();
			if (extras != null) {
				mBitmap = extras.getParcelable("data");
				if (mBitmap != null) {
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					final byte[] mUserphotoData = stream.toByteArray();
					String saveDir = Environment.getExternalStorageDirectory() + Constant.PARTTIMEJOB_IMAGE;

					Bitmap mIcon = ThumbnailUtils.extractThumbnail(mBitmap, 120, 120);
					String fname = "register_image" + ".jpg";
					try {
						register_head_image.setImageBitmap(mIcon);
						Utils.saveFile(mUserphotoData, saveDir + fname);
						registerHeadPath = saveDir + fname;
					} catch (IOException e) {
						Toast.makeText(mContext, R.string.photo_save_image_err, Toast.LENGTH_SHORT).show();
						return;
					}
				}
			}
			break;
		}
	}

	/**
	 * ========================================================================
	 * ==================================================
	 */
	/**
	 * ========================================================================
	 * ==================================================
	 */
	/**
	 * ========================================================================
	 * ==================================================
	 */

}
