package com.parttime.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parttime.parttimejob.R;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Utils;
/**
 * 
 * @author 灰色的寂寞
 * @function 实现反馈和个人申诉功能
 * @date 2015-1-20
 * @time 13:41
 *
 */
public class Personal_FeadBack_Activity extends Activity {
	private Context mContext;
	
	/** 标题部分*/
	private TextView CenterTitle;
	private ImageView LeftBackBtn;
	private EditText mFeadContent;
	private TextView SubmitBtn;
	
	private String FromType ="";
	
	//常量部分
	/** 返回*/
	private static final int CLICK_TYPE_BACKBTN=0;
	private static final int CLICK_TYPE_COMMIT=1;
	
	/*等待框部分*/
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;
	private long userid=-1;
	private String username= "";
	private SharedPreferences sharedPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal_feadback_layout);
		mContext = this;
		Intent intent = getIntent();
		FromType = intent.getStringExtra("type");
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		userid = sharedPreferences.getLong("userid", -1);
		username = sharedPreferences.getString("username","");
		
		initViews();
		
	}
	public void ShowWaiting(String text) {
		WaitingText.setText(text);
		WaitingDlg.setVisibility(View.VISIBLE);
	}

	public void HideWaiting() {
		WaitingDlg.setVisibility(View.GONE);
	}
	
	private void initViews() {
		/** 标题部分*/
		CenterTitle = (TextView)findViewById(R.id.title_text);
		LeftBackBtn = (ImageView)findViewById(R.id.home_back);
		
		/* 等待对话框部分 */
		WaitingText = (TextView)findViewById(R.id.Waiting_text);
		WaitingDlg  =(RelativeLayout)findViewById(R.id.Waiting_dlg);
		
		
		mFeadContent = (EditText) findViewById(R.id.feadback_content);
		SubmitBtn = (TextView) findViewById(R.id.feadback_content_submit_btn);
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));
		if(!FromType.equals("") && FromType.equals("feadback")){
			CenterTitle.setText("意见反馈");
			SubmitBtn.setText("提交反馈");
		}else if(!FromType.equals("") && FromType.equals("appeal")){
			CenterTitle.setText("个人申诉");
			SubmitBtn.setText("提交申诉");
		}
		
		SubmitBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_COMMIT));
		
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
		case CLICK_TYPE_COMMIT:
			String content = mFeadContent.getText().toString().trim();
			if(content.equals("")){
				Utils.ShowToast(mContext, "提交内容不能为空！");
			}else{
				Utils.ShowToast(mContext, "反馈成功，谢谢！");
				if(!FromType.equals("") && FromType.equals("feadback")){
					ProcessFeadback(content);
				}else if(!FromType.equals("") && FromType.equals("appeal")){
					ProcessAppeal();
				}
				finish();
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 提交反馈内容
	 */
	public void ProcessFeadback(String content){
		new FeedBackAsynTask().execute(userid+"",username,content);
	}
	
	/**
	 * 提交申诉内容
	 */
	public void ProcessAppeal(){
		
	}
	
	/**
	 * 反馈
	 */
	public class FeedBackAsynTask extends AsyncTask<String, Void, Boolean> {
		long errcode = -1;
		int hireid=-1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在反馈...");
		}
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				hireid = Integer.parseInt(params[0]);
				errcode = SvrOperation.FeedbackTask(mContext, params[0],params[1],params[2]);

				if (errcode != SvrInfo.SVR_RESULT_SUCCESS)
					return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			HideWaiting();
			if (result == false) {
				if(errcode== -3)
					Utils.ShowToast(mContext, "反馈失败！");
				return;
			}
			Utils.ShowToast(mContext, "反馈成功！");
		}
	}
}
