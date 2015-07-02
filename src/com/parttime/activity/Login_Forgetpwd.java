package com.parttime.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parttime.msg.ErrorMsgSvr;
import com.parttime.msg.ErrorMsgSvrMsg;
import com.parttime.parttimejob.R;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.MD5Encrypt;
import com.parttime.utils.Utils;

/**
 * 忘记密码的activity
 * 
 * @author huxixi
 * 
 */
public class Login_Forgetpwd extends Activity {

	public static final int BACK_INT = 1;
	public static final int CONFIRM_PWD_INT = 2;
	public static final int OBTAIN_VERIFY_INT = 3;
	public static final int OBTAIN_EDIT_INT = 4;
	private Context mContext = null;
	private View forget_pwd_include;
	private TextView back_image;
	private TextView login_text;
	private TextView register_text;
	private RelativeLayout modify_pwd_relative;
	private Button confirm_pwd_btn;
	private Button confirm_obtain_btn;
	private TextView obtain_verify_btn;
	// private EditText username_edittext;
	private EditText email_edittext;
	private EditText obtain_edit;
	private EditText new_pwd;
	private EditText new_pwd_again;
	private boolean second_flag = true;
	public int errcode = -1;
	private String email_str = null;
	private String username_str = null;
	private String pwd_new_str = null;
	private String pwd_new_again_str = null;
	private HashMap<String, String> forget_map = null;
	private HashMap<String, String> newpass_map = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_forget_pwd);
		mContext = this;
		initView();
	}

	private void initView() {
		forget_pwd_include = (View) findViewById(R.id.forget_pwd_include);
		back_image = (TextView) forget_pwd_include.findViewById(R.id.home_back);
		login_text = (TextView) forget_pwd_include.findViewById(R.id.login_text);
		register_text = (TextView) forget_pwd_include.findViewById(R.id.register_text);

		login_text.setText(getResources().getString(R.string.retrieve_pwd));
		register_text.setVisibility(View.GONE);

		back_image.setOnClickListener(new MyClickListener(BACK_INT));

		modify_pwd_relative = (RelativeLayout) findViewById(R.id.modify_pwd_relative);
		confirm_pwd_btn = (Button) findViewById(R.id.confirm_pwd_btn);
		confirm_obtain_btn = (Button) findViewById(R.id.confirm_obtain_btn);
		obtain_verify_btn = (TextView) findViewById(R.id.obtain_verify_btn);
		// username_edittext=(EditText)findViewById(R.id.username_edittext);
		email_edittext = (EditText) findViewById(R.id.email_edittext);
		obtain_edit = (EditText) findViewById(R.id.obtain_edit);
		new_pwd = (EditText) findViewById(R.id.new_pwd);
		new_pwd_again = (EditText) findViewById(R.id.new_pwd_again);
		confirm_pwd_btn.setOnClickListener(new MyClickListener(CONFIRM_PWD_INT));
		obtain_verify_btn.setOnClickListener(new MyClickListener(OBTAIN_VERIFY_INT));
		confirm_obtain_btn.setOnClickListener(new MyClickListener(OBTAIN_EDIT_INT));
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	public class MyClickListener implements View.OnClickListener {

		int index;

		public MyClickListener(int index) {
			this.index = index;
		}

		@Override
		public void onClick(View v) {
			switch (index) {
			case BACK_INT:
				Login_Forgetpwd.this.finish();
				break;
			case CONFIRM_PWD_INT:
				newpass_map = new HashMap<String, String>();
				username_str = email_edittext.getText().toString();
				pwd_new_str = new_pwd.getText().toString();
				pwd_new_again_str = new_pwd_again.getText().toString();
				if (!pwd_new_str.equals(pwd_new_again_str)) {
					Utils.ShowToast(mContext, "两次输入的密码不正确");
				} else {

					newpass_map.put("username", username_str);
					newpass_map.put("new_pass", MD5Encrypt.encryption(pwd_new_str));
					new ForgetPassTask().execute(newpass_map);
				}

				break;
			case OBTAIN_VERIFY_INT:
				forget_map = new HashMap<String, String>();
				email_str = email_edittext.getText().toString();
				// username_str=username_edittext.getText().toString();
				forget_map.put("phone", email_str);
				// forget_map.put("username",username_str);
				new GetVcodeTask().execute(forget_map);
				break;
			case OBTAIN_EDIT_INT:
				String obtain = obtain_edit.getText().toString();
				if (obtain.equals(Utils.Vcode)) {
					modify_pwd_relative.setVisibility(View.VISIBLE);
				} else {
					modify_pwd_relative.setVisibility(View.GONE);
				}
				break;
			}
		}

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
				int errmsg = ErrorMsgSvrMsg.ErrorMsg(errcode);
				Utils.ShowToast(mContext, errmsg);
				return;
			}
			obtain_verify_btn.setClickable(false);
			getCheckNumTime();

			return;
		}

	}

	public Handler msghandler = new Handler() {
		@SuppressLint("NewApi")
		public void handleMessage(Message msg) {
			int number = (Integer) msg.what;
			if (number > 0) {
				obtain_verify_btn.setText(String.format(mContext.getResources().getString(R.string.text_get_checknum_time), number));
			} else {
				obtain_verify_btn.setClickable(true);
				obtain_verify_btn.setText(mContext.getResources().getString(R.string.obtain_verfiy1));
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
	 * 忘记密码，设置新密码的接口
	 */

	public class ForgetPassTask extends AsyncTask<HashMap<String, String>, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(HashMap<String, String>... params) {
			int iresult = SvrOperation.ForgetPass(mContext, params[0]);
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
			obtain_verify_btn.setClickable(false);
			getCheckNumTime();

			return;
		}

	}

}
