package com.parttime.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parttime.adapter.CompanyEmplymentAdapter;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.HiredStruct;
import com.parttime.struct.PartTimeStruct.JobManagedStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;

/**
 * 
 * @author 灰色的寂寞
 * @function 用工管理页面，对申请者的列表管理
 * @date 2015-1-19
 * @time 17:54
 *
 */
public class Company_Employment_Activity extends Activity{
	
	private Context mContext;
	private CompanyEmplymentAdapter EmploymentAdapter = null;
	private ArrayList<JobManagedStruct> mJobEmployList = null;
	/**view 部分****************************************************/
	/*等待框部分*/
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;
	/* 标题部分*/
	private TextView CenterTitle;
	private ImageView LeftBackBtn;
	
	private ListView EmploymentList;
	/**常量部分****************************************************/
	/* 返回*/
	private static final int CLICK_TYPE_BACKBTN=0;
	private static final int CLICK_TYPE_SHOWLIST=1;
	int jobid=-1;
	String jobname="";
	private PartTimeDB DB ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.company_employment_layout);
		mContext = this;
		Intent intent = getIntent();
		jobid =intent.getIntExtra("job_id", -1); 
		jobname =intent.getStringExtra("job_name"); 
		DB =PartTimeDB.getInstance(mContext);
		mJobEmployList = new ArrayList<JobManagedStruct>();
		initViews();
		
	}

	private void initViews() {
		/** 标题部分*/
		CenterTitle = (TextView)findViewById(R.id.title_text);
		LeftBackBtn = (ImageView)findViewById(R.id.home_back);
		CenterTitle.setText("用工管理");
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));
		/* 等待对话框部分 */
		WaitingText = (TextView)findViewById(R.id.Waiting_text);
		WaitingDlg  =(RelativeLayout)findViewById(R.id.Waiting_dlg);
		
		EmploymentList = (ListView) findViewById(R.id.employment_list);
		
		if(Submit.isNetworkAvailable(mContext)){
			new GetCompanyInfoAsynTask().execute(jobid);
		}else{
			Utils.ShowToast(mContext, "没有可用网络");
		}
		
	}
	
	public Handler viewhandler = new Handler(){
		public void handleMessage(Message message) {
			switch (message.what) {
				case CLICK_TYPE_SHOWLIST:
					EmploymentAdapter = new CompanyEmplymentAdapter(mContext,viewhandler,mJobEmployList,jobname);
					EmploymentList.setAdapter(EmploymentAdapter);
			}
		}
	};
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
		default:
			break;
		}
	}
	/**
	 * 获取雇佣列表
	 */
	public class GetCompanyInfoAsynTask extends AsyncTask<Integer, Void, Boolean> {
		long errcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在获取雇佣列表...");
		}
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				errcode = SvrOperation.getTaskJobList(mContext, params[0]);

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
			
			if (result == false) {
				HideWaiting();
				Utils.ShowToast(mContext, "没有已雇佣的员工");
				return;
			}
			ParseHireToResume(DB.getHiredJobList(mContext,-1));
		}
	}
	/**
	 * 通过rid获取简历列表显示
	 * 
	 * @param hid
	 */
	public void ParseHireToResume(ArrayList<HiredStruct> hid) {
		for (int i = 0; i < hid.size(); i++) {
			if(hid.get(i).engage.equals("进行中"))
			new GetResumeListAsynTask().execute(hid.get(i).r_id, hid.get(i).id);
			else if(i==hid.size()-1){
				Utils.ShowToast(mContext, "没有正在工作的员工");
				HideWaiting();
			}
		}
	}

	/**
	 * 获取企业发布的职位对应的雇佣用人
	 */
	public class GetResumeListAsynTask extends AsyncTask<Integer, Void, Boolean> {
		JobManagedStruct struct = null;
		long errcode = -1;
		String rid;
		String hireid;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				hireid = params[1] + "";
				rid = params[0] + "";
				errcode = SvrOperation.Resume_Info(mContext, params[0] + "");

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
				Utils.ShowToast(mContext, "加载失败，请检查网络");
				return;
			}
			HashMap<String, String> map = DB.getResumeInfo(mContext, rid);
			struct = new JobManagedStruct();
			//{rheight=111, radd=北京-北京-昌平, rstarttime=2014-12-12 09:00:00, ruserid=19, rjob=小时工, rid=32,
			//rtype=时, rname=小白, rcall=111111111, rsex=男, rexperence=旅途他, rendtime=2014-12-12 20:00:00}
			struct.mUserName = (String) map.get("rname");
			struct.mTime = (String) map.get("rstarttime");
			struct.mAddressDistance = (String) map.get("radd");
			struct.mJobNameType = (String) map.get("rtype");
			struct.mHireid = hireid;
			mJobEmployList.add(struct);
			viewhandler.sendEmptyMessage(CLICK_TYPE_SHOWLIST);
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
