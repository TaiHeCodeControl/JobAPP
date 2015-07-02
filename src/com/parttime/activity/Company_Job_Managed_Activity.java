package com.parttime.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parttime.adapter.CompanyJobManagedAdapter;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.HiredStruct;
import com.parttime.struct.PartTimeStruct.JobManagedStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;

/**s
 * 
 * @author 灰色的寂寞
 * @function 职位管理界面，对已经发布的职位列表做处理
 * @date 2015-2-4
 * @time 17:54
 *
 */
public class Company_Job_Managed_Activity extends Activity{
	
	private Context mContext;
	private CompanyJobManagedAdapter JobManagedAdapter = null;
	private ArrayList<JobManagedStruct> mJobManagedList = null;
	private ArrayList<JobManagedStruct> mJobEmployList = null;
	private int Jobid = -1;
	/**view 部分****************************************************/
	/*等待框部分*/
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;
	/* 标题部分*/
	private TextView CenterTitle;
	private ImageView LeftBackBtn;
	private TextView EmploymentBtn;
	
	private ListView JobManagedList;
	/**常量部分****************************************************/
	/* 返回*/
	private static final int CLICK_TYPE_BACKBTN=0;
	private static final int CLICK_TYPE_SHOWLIST=1;
	private static final int CLICK_TYPE_EMPLOYMENT_BTN=2;
	private SharedPreferences sharedPreferences;
	private long userid=-1;
	private String avatar = "";
	private String name = "";
	private PartTimeDB DB ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.company_job_managed_layout);
		mContext = this;
		mJobManagedList = new ArrayList<JobManagedStruct>();
		mJobEmployList = new ArrayList<JobManagedStruct>();
		Intent intent = getIntent();
		Jobid = intent.getIntExtra("job_id",-1);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		userid = sharedPreferences.getLong("userid", -1);
		avatar = sharedPreferences.getString("avatar", "");
		name = sharedPreferences.getString("username", "");
		DB =PartTimeDB.getInstance(mContext);
		
		initViews();
		
		
	}

	private void initViews() {
		/** 标题部分*/
		CenterTitle = (TextView)findViewById(R.id.title_text);
		LeftBackBtn = (ImageView)findViewById(R.id.home_back);
		CenterTitle.setText("雇佣管理");
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));
		
		JobManagedList = (ListView) findViewById(R.id.job_managed_list);
		
		
		/* 等待对话框部分 */
		WaitingText = (TextView)findViewById(R.id.Waiting_text);
		WaitingDlg  =(RelativeLayout)findViewById(R.id.Waiting_dlg);
		
		EmploymentBtn = (TextView)findViewById(R.id.employment_btn);
		EmploymentBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_EMPLOYMENT_BTN));
		
		if(Submit.isNetworkAvailable(mContext))
			new GetCompanyInfoAsynTask().execute(Jobid);
		else
			Utils.ShowToast(mContext, "没有可用网络");
	}
		
		public Handler viewhandler = new Handler(){
			@SuppressLint("NewApi")
			public void handleMessage(Message message) {
				int listsize = 0;

				switch (message.what) {
				case CLICK_TYPE_SHOWLIST:
					JobManagedAdapter = new CompanyJobManagedAdapter(mContext,viewhandler,mJobEmployList);
					JobManagedList.setAdapter(JobManagedAdapter);
					
				case 1000:
					JobManagedStruct jmStruct = (JobManagedStruct)message.obj;
					if(jmStruct!=null)
						getCheckedList(jmStruct);
					if(mJobManagedList!=null){
						listsize=mJobManagedList.size();
					}
					if(listsize>0){
						EmploymentBtn.setBackground(getResources().getDrawable(R.drawable.feadback_submit_selsector));
					}
					break;
				case -1000:
					JobManagedStruct jmStruct1 = (JobManagedStruct)message.obj;
					if(jmStruct1!=null)
						RemoveUnCheckedList(jmStruct1);
					if(mJobManagedList!=null){
						listsize=mJobManagedList.size();
					}
					if(listsize<=0){
						EmploymentBtn.setBackgroundColor(getResources().getColor(R.color.darkgray));
					}
					break;
				}
			}
		};
	
	public void getCheckedList(JobManagedStruct jmStruct){
		mJobManagedList.add(jmStruct);
	}
	public void RemoveUnCheckedList(JobManagedStruct jmStruct){
		mJobManagedList.remove(jmStruct);
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
		case CLICK_TYPE_EMPLOYMENT_BTN:
			if (Submit.isNetworkAvailable(mContext))
				ProcessEmployFunction();
			else
				Utils.ShowToast(mContext, "没有可用网！");
			break;
		default:
			break;
		}
	}
	/**
	 * 处理雇佣功能的实现
	 */
	public void ProcessEmployFunction(){
		for(int i=0;i<mJobManagedList.size();i++)
			new GetEmployAsynTask().execute(mJobManagedList.get(i).mHireid,"已雇佣");
	}
	
	
	/**
	 * 获取雇佣列表
	 */
	public class GetCompanyInfoAsynTask extends AsyncTask<Integer, Void, Boolean> {
		long errcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在获取申请人列表...");
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
				Utils.ShowToast(mContext, "没有已申请此职位的人");
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
			if(hid.get(i).engage.equals("已申请"))
			new GetResumeListAsynTask().execute(hid.get(i).r_id, hid.get(i).id);
		}
		HideWaiting();
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
			struct.mUserName = (String) map.get("rname");
			struct.mTime = (String) map.get("rstarttime");
			struct.mAddressDistance = (String) map.get("radd");
			struct.mJobNameType = (String) map.get("rtype");
			struct.mHireid = hireid;
			mJobEmployList.add(struct);
			viewhandler.sendEmptyMessage(CLICK_TYPE_SHOWLIST);
		}
	}

	/**
	 * 企业雇佣员工
	 */
	public class GetEmployAsynTask extends AsyncTask<String, Void, Boolean> {
		long errcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在雇佣...");
		}
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				errcode = SvrOperation.HiredFunctionTask(mContext, params[0],params[1]);

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
					Utils.ShowToast(mContext, "雇佣失败，或已经被雇佣");
				return;
			}
			Utils.ShowToast(mContext, "雇用成功！");
			finish();
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
