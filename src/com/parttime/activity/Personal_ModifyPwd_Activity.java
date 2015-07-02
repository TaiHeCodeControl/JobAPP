package com.parttime.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parttime.activity.Personal_Details_Activity.GetPersonalHeadAsynTask;
import com.parttime.constant.Constant;
import com.parttime.msg.ErrorMsgSvr;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.PersonalDetailsStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;
/**
 * 
 * @author 灰色的寂寞
 * @function 个人信息部分   修改密码
 * @date 2015-3-2
 * @time 13:41
 *
 */
public class Personal_ModifyPwd_Activity extends Activity {
	private Context mContext;
	
	/** 标题部分*/
	private TextView PwdCenterTitle;
	private ImageView PwdLeftBackBtn;
	
	private RelativeLayout ModifyPwdBlock;
	private EditText OldPwdEdit,NewPwdEdit;
	private Button PwdConfirm;
	/*等待框部分*/
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;
	//常量部分
	/** 返回*/
	private static final int CLICK_TYPE_BACKBTN=0;
	private static final int CLICK_TYPE_PWDCOMMIT=1;
	SharedPreferences sharedPreferences;
	String mEmail = "";
	long userid = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal_changepwd_layout);
		mContext = this;
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		mEmail = sharedPreferences.getString("email", "");
		userid = sharedPreferences.getLong("userid", -1);
		initViews();
		
	}
	
	
	private void initViews() {
		/** 标题部分*/
		PwdCenterTitle = (TextView)findViewById(R.id.title_text);
		PwdLeftBackBtn = (ImageView)findViewById(R.id.home_back);
		PwdCenterTitle.setText("修改密码");
		PwdLeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));
		
		/* 等待对话框部分 */
		WaitingText = (TextView)findViewById(R.id.Waiting_text);
		WaitingDlg  =(RelativeLayout)findViewById(R.id.Waiting_dlg);
		
		ModifyPwdBlock = (RelativeLayout) findViewById(R.id.changepwd_modify_pwd_relative);
		
		OldPwdEdit= (EditText) findViewById(R.id.changepwd_new_pwd);
		NewPwdEdit= (EditText) findViewById(R.id.changepwd_new_pwd_again);
		PwdConfirm= (Button) findViewById(R.id.changepwd_confirm_pwd_btn);

		PwdConfirm.setOnClickListener(new MyOnClickListener(CLICK_TYPE_PWDCOMMIT));
		
	}
	
	/**
	 * 自定义ClickListener管理Click事件
	 * */
	public class MyOnClickListener implements OnClickListener{
		private int mtype=-1;//默认为-1，什么也不做
		public MyOnClickListener(int type){
			mtype = type;
		}
		@Override
		public void onClick(View v) {
			ManageClick(mtype);
		}
		
	}
	
	/**
	 * 通过判断type，管理click事件
	 * @param mtype
	 */
	private void ManageClick(int mtype) {
		switch (mtype) {
		case CLICK_TYPE_BACKBTN:
			// 返回按钮的监听
			finish();
			break;
		case CLICK_TYPE_PWDCOMMIT:
			
			String oldpwd = OldPwdEdit.getText().toString().trim();
			String newpwd = NewPwdEdit.getText().toString().trim();
			if(!oldpwd.equals("")){
				if(!newpwd.equals("")){
					if(Submit.isNetworkAvailable(mContext))
						new ModifyNewPwdAsynTask().execute(oldpwd,newpwd);
					else
						Utils.ShowToast(mContext, "没有可用网络！");
				}else{
					Utils.ShowToast(mContext, "新密码不能为空！");
				}
			}else{
				Utils.ShowToast(mContext, "旧密码不能为空！");
			}
			
			break;
		default:
			break;
		}
	}
	/**
	 *修改密码
	 */
	public class ModifyNewPwdAsynTask extends AsyncTask<String, Void, Boolean>{
		int errcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在提交...");
		}
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				errcode = SvrOperation.ModifyPwdTask(mContext, userid, params[0],params[1]);
				if (errcode !=SvrInfo.SVR_RESULT_SUCCESS)
					return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			HideWaiting();
			super.onPostExecute(result);
			if(result== false){
				Utils.ShowToast(mContext, ErrorMsgSvr.ErrorMsg(errcode));
				return ;
			}
			finish();
			Utils.ShowToast(mContext, "修改成功！");
		}
	}
	public void ShowWaiting(String text) {
		WaitingText.setText(text);
		WaitingDlg.setVisibility(View.VISIBLE);
	}

	public void HideWaiting() {
		WaitingDlg.setVisibility(View.GONE);
	}
}
