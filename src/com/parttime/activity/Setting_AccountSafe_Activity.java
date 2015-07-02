package com.parttime.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parttime.parttimejob.R;
import com.parttime.utils.Utils;

/**
 * 
 * @author 灰色的寂寞
 * @function 展示账号安全信息
 * @date 2015-1-20
 * @time 16:40
 * 
 */
public class Setting_AccountSafe_Activity extends Activity {
	private Context mContext;

	/** 标题部分 */
	private TextView CenterTitle;
	private ImageView LeftBackBtn;

	private RelativeLayout mWechatBlock, mQQBlock, mTellBlock, mEmailBlock, mAccountPwdBlock, mPhoneSafeBlock;
	private TextView mWechat, mQQ, mTell, mEmail, mAccountPwd, mPhoneSafe;

	/* 编辑对话框部分 */
	private RelativeLayout EditDialogBlock;
	private TextView DialogTitle, DialogCancelBtn, DialogEnsureBtn, ErrorTips;
	private EditText DialogContent;

	// 常量部分
	/** 返回 */
	private static final int CLICK_TYPE_BACKBTN = 0;
	private static final int CLICK_TYPE_WECHAT = 1;
	private static final int CLICK_TYPE_QQ = 2;
	private static final int CLICK_TYPE_TELL = 3;
	private static final int CLICK_TYPE_EMAIL = 4;
	private static final int CLICK_TYPE_ACCOUNTPWD = 5;
	private static final int CLICK_TYPE_PHONESAFE = 6;

	private static final int CLICK_TYPE_DIALOG_CANCEL = 7;
	private static final int CLICK_TYPE_DIALOG_ENSURE = 8;

	SharedPreferences shared;
	String call = "";
	String email = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_accountsafe_layout);
		mContext = this;
		shared = PreferenceManager.getDefaultSharedPreferences(mContext);
		call = shared.getString("safe_call", "空");
		email = shared.getString("safe_email", "空");
		initViews();

	}

	private void initViews() {
		/** 标题部分 */
		CenterTitle = (TextView) findViewById(R.id.title_text);
		LeftBackBtn = (ImageView) findViewById(R.id.home_back);
		CenterTitle.setText("账号与安全");
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));

		mWechatBlock = (RelativeLayout) findViewById(R.id.rl_wechate_block);
		mWechat = (TextView) findViewById(R.id.wechate);
		mQQBlock = (RelativeLayout) findViewById(R.id.rl_QQ_block);
		mQQ = (TextView) findViewById(R.id.QQ);
		mTellBlock = (RelativeLayout) findViewById(R.id.rl_tell_block);
		mTell = (TextView) findViewById(R.id.tell);
		mEmailBlock = (RelativeLayout) findViewById(R.id.rl_email_block);
		mEmail = (TextView) findViewById(R.id.email);
		mTell.setText(call);
		mEmail.setText(email);
		mAccountPwdBlock = (RelativeLayout) findViewById(R.id.rl_account_pwd_block);
		mAccountPwd = (TextView) findViewById(R.id.account_pwd);
		mPhoneSafeBlock = (RelativeLayout) findViewById(R.id.rl_phone_safe_block);
		mPhoneSafe = (TextView) findViewById(R.id.phone_safe);

		mWechatBlock.setVisibility(View.GONE);
		mQQBlock.setVisibility(View.GONE);
		mAccountPwdBlock.setVisibility(View.GONE);

		mWechatBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_WECHAT));
		mQQBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_QQ));
		mTellBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_TELL));
		mEmailBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_EMAIL));
		mAccountPwdBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_ACCOUNTPWD));
		mPhoneSafeBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_PHONESAFE));

		EditDialogBlock = (RelativeLayout) findViewById(R.id.edit_dialog_layout);
		DialogTitle = (TextView) findViewById(R.id.edit_dialog_title);
		DialogCancelBtn = (TextView) findViewById(R.id.edit_cancel);
		DialogEnsureBtn = (TextView) findViewById(R.id.edit_ensure);
		ErrorTips = (TextView) findViewById(R.id.edit_error_tips);
		DialogContent = (EditText) findViewById(R.id.edit_dialog_content);
		DialogContent.setVisibility(View.VISIBLE);
		DialogCancelBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_DIALOG_CANCEL));
		DialogEnsureBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_DIALOG_ENSURE));

	}

	/**
	 * 自定义ClickListener管理Click事件
	 * */
	public class MyOnClickListener implements OnClickListener {
		private int mtype = -1;// 默认为-1，什么也不做

		public MyOnClickListener(int type) {
			mtype = type;
		}

		@Override
		public void onClick(View v) {
			ManageClick(mtype);
		}

	}

	/**
	 * 通过判断type，管理click事件
	 * 
	 * @param mtype
	 */
	private int EnsureType = -1;

	private void ManageClick(int mtype) {
		switch (mtype) {
		case CLICK_TYPE_BACKBTN:
			// 返回按钮的监听
			finish();
			break;
		case CLICK_TYPE_WECHAT:
			EnsureType = CLICK_TYPE_WECHAT;
			ProcessDialogInfo("微信号");
			break;
		case CLICK_TYPE_QQ:
			EnsureType = CLICK_TYPE_QQ;
			ProcessDialogInfo("QQ号");
			break;
		case CLICK_TYPE_TELL:
			EnsureType = CLICK_TYPE_TELL;
			ProcessDialogInfo("手机号");
			break;
		case CLICK_TYPE_EMAIL:
			EnsureType = CLICK_TYPE_EMAIL;
			ProcessDialogInfo("邮箱地址");
			break;
		case CLICK_TYPE_ACCOUNTPWD:
			EnsureType = CLICK_TYPE_ACCOUNTPWD;
			ProcessDialogInfo("账号密码");
			break;
		case CLICK_TYPE_PHONESAFE:
			EnsureType = CLICK_TYPE_PHONESAFE;
			// ProcessDialogInfo("手机安全防护");
			Utils.ShowToast(mContext, "亲，稍后会添加，谢谢");
			break;
		case CLICK_TYPE_DIALOG_CANCEL:
			EditDialogBlock.setVisibility(View.GONE);
			break;
		case CLICK_TYPE_DIALOG_ENSURE:
			ProcessDialogEnsure(EnsureType);
			break;

		default:
			break;
		}
	}

	/**
	 * 处理对话框的标题和显示
	 */
	public void ProcessDialogInfo(String title) {
		EditDialogBlock.setVisibility(View.VISIBLE);
		DialogTitle.setText(title);
		DialogContent.setText("");
	}

	/**
	 * 处理对应的对话框确定值
	 * 
	 * @param ensuretype
	 */
	public void ProcessDialogEnsure(int ensuretype) {
		String content = DialogContent.getText().toString().trim();
		DialogContent.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;

			@Override
			public void afterTextChanged(Editable s) {
				if (temp.length() > 0) {
					ErrorTips.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				temp = s;
			}

		});
		if (content.equals("")) {
			ErrorTips.setVisibility(View.VISIBLE);
			ErrorTips.setText("内容不能为空！");
			return;
		} else {
			ErrorTips.setVisibility(View.GONE);
		}
		switch (ensuretype) {
		case CLICK_TYPE_WECHAT:
			mWechat.setText(content);
			break;
		case CLICK_TYPE_QQ:
			mQQ.setText(content);
			break;
		case CLICK_TYPE_TELL:
			shared.edit().putString("safe_call", content).commit();
			mTell.setText(content);
			break;
		case CLICK_TYPE_EMAIL:
			shared.edit().putString("safe_email", content).commit();
			mEmail.setText(content);
			break;
		case CLICK_TYPE_ACCOUNTPWD:
			mAccountPwd.setText(content);
			break;
		case CLICK_TYPE_PHONESAFE:
			mPhoneSafe.setText(content);
			break;
		}

		EditDialogBlock.setVisibility(View.GONE);
	}

}
