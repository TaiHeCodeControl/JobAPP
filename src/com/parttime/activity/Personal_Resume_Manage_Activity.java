package com.parttime.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parttime.adapter.PersonalResumeAdapter;
import com.parttime.constant.Constant;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Utils;
/**
 * 
 * @author 灰色的寂寞
 * @function 管理个人简历界面
 * @date 2015-1-20
 * @time 13:41
 *
 */
public class Personal_Resume_Manage_Activity extends Activity {
	private Context mContext;
	private PartTimeDB mPartTimeDB=null;
	/** 标题部分*/
	private TextView CenterTitle;
	private ImageView LeftBackBtn,RightBtn;
	private ListView ResumeListView;
	private TextView mAddNewResume,mDelResume;
	private PersonalResumeAdapter resumeAdapter;
	
	/*等待框部分*/
	private static TextView WaitingText;
	private static RelativeLayout WaitingDlg;
	private int check_show_flag =0;
	//常量部分
	/** 返回*/
	private static final int CLICK_TYPE_BACKBTN=0;
	private static final int CLICK_TYPE_RESUME_MANAGE=1;
	private static final int CLICK_TYPE_ADDNEWRESUME=2;
	private static final int CLICK_TYPE_DELRESUME=3;
	private static final int CLICK_TYPE_FRESH=4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal_resume_manage_layout);
		mContext = this;
		
		mPartTimeDB = PartTimeDB.getInstance(mContext);
		
		
		
		
		initViews();
		
	}
	
	
	@SuppressLint("NewApi")
	private void initViews() {
		/** 标题部分*/
		CenterTitle = (TextView)findViewById(R.id.title_text);
		LeftBackBtn = (ImageView)findViewById(R.id.home_back);
		RightBtn = (ImageView)findViewById(R.id.right_image);
		RightBtn.setVisibility(View.VISIBLE);
		CenterTitle.setText("简历管理");
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));
		
		mAddNewResume= (TextView)findViewById(R.id.mnew_resume);
		mDelResume= (TextView)findViewById(R.id.mdel_resume);
		mAddNewResume.setOnClickListener(new MyOnClickListener(CLICK_TYPE_ADDNEWRESUME));
		mDelResume.setOnClickListener(new MyOnClickListener(CLICK_TYPE_DELRESUME));
		
		/* 等待对话框部分 */
		WaitingText = (TextView)findViewById(R.id.Waiting_text);
		WaitingDlg  =(RelativeLayout)findViewById(R.id.Waiting_dlg);
		
		ResumeListView = (ListView)findViewById(R.id.personal_resume_manage_list);
		RightBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_FRESH));
	}
	@Override
	protected void onResume() {
		super.onResume();
		viewhandler.sendEmptyMessage(1000);
	}
	public void Refresh(){
		new SubmitResumeInfoAsynTask().execute();
	}
	/**handler实现adapter和fragmen】的数据交互*/
	public Handler viewhandler = new Handler(){
		public void handleMessage(Message message) {
			
			switch (message.what) {
				case CLICK_TYPE_RESUME_MANAGE://已申请
					resumeAdapter = new PersonalResumeAdapter(mContext,viewhandler);
					ResumeListView.setAdapter(resumeAdapter);
					resumeAdapter.notifyDataSetChanged();
					break;
				case 1000:
					Refresh();
				break;
			}
		}	
			
	};
	public static void ShowWaiting(String text) {
		WaitingText.setText(text);
		WaitingDlg.setVisibility(View.VISIBLE);
	}

	public static void HideWaiting() {
		WaitingDlg.setVisibility(View.GONE);
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
			Utils.CommitPageFlagToShared(mContext, Constant.FINISH_ACTIVITY_FLAG_PERSONAL);
			// 返回按钮的监听
			finish();
			break;
		case CLICK_TYPE_ADDNEWRESUME:
			
			Intent intent = new Intent();
			intent.setClass(mContext, Job_Create_Resume.class);
			intent.putExtra("flag", "create");
			startActivity(intent);
			break;
		case CLICK_TYPE_FRESH:
			Refresh();
			break;
		}
	}
	/**
	 * 提交个人信心 
	 */
	public class SubmitResumeInfoAsynTask extends AsyncTask<Void, Void, Boolean>{
		long errcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在获取列表...");
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				errcode = SvrOperation.AllResume(mContext);
				if (errcode !=SvrInfo.SVR_RESULT_SUCCESS)
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
			if(result== false){
//				if(errcode == -9)
//				Utils.ShowToast(mContext, "获取列表失败或为空！");
				return ;
			}
			
			Message msg = new Message();
			msg.what = CLICK_TYPE_RESUME_MANAGE;
			viewhandler.sendMessage(msg);
		}
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Utils.CommitPageFlagToShared(mContext, Constant.FINISH_ACTIVITY_FLAG_PERSONAL);
	}
	
}
