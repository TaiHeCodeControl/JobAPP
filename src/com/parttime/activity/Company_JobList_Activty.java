package com.parttime.activity;

import java.util.ArrayList;
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

import com.parttime.adapter.PublishJobListAdapter;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.pullrefresh.PullToRefreshLayout;
import com.parttime.pullrefresh.listener.PJobListRefreshListener;
import com.parttime.struct.PartTimeStruct.CompanyPJobListStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Submit;
/**
 * 
 * @author 灰色的寂寞
 * @function 职位列表界面
 * @date 2015-2-4
 * @time 15：00
 *
 */
public class Company_JobList_Activty extends Activity{
	
	private Context mContext;
	private PublishJobListAdapter JobListAdapter = null;
	private ArrayList<CompanyPJobListStruct> mJobListList = new ArrayList<CompanyPJobListStruct>();
	/**view 部分****************************************************/
	/*等待框部分*/
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;
	/* 标题部分*/
	private TextView CenterTitle;
	private ImageView LeftBackBtn;
	
	private ListView JobListList;
	private PullToRefreshLayout refreshableView;
	/**常量部分****************************************************/
	/* 返回*/
	private static final int CLICK_TYPE_BACKBTN=0;
	/** 用工管理 */
	private static final int MENU_PERSONAL_MANAGEWORKER = 1;
	/** 职位管理 */
	private static final int MENU_PERSONAL_MANAGEJOB = 2;
	private static final int CLICK_SEND_ADAPTER_MSG=3;
	private SharedPreferences sharedPreferences;
	private long userid=-1;
	private String avatar = "";
	private String name = "";
	private int turntype;
	private int flag=0;
	
	private PartTimeDB DB;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.company_joblist_layout);
		mContext = this;
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		userid = sharedPreferences.getLong("userid", -1);
		avatar = sharedPreferences.getString("avatar", "");
		name = sharedPreferences.getString("username", "");
		Intent intent = getIntent();
		turntype = intent.getIntExtra("turn_type", MENU_PERSONAL_MANAGEWORKER);
		DB = PartTimeDB.getInstance(mContext);
		initViews();
		initJobList();
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		flag=0;
	}
	/**
	 * 获取默认的工作列表
	 */
	public void initJobList(){
		if(Submit.isNetworkAvailable(mContext)){
			new GetPJobListAsynTask().execute(10,(int)userid);
		}else{
			mJobListList = DB.getCompanyPJobInfoList(mContext);
			if(mJobListList!=null)
				viewhandler.sendEmptyMessage(CLICK_SEND_ADAPTER_MSG);
		}
	}
	
	/**
	 * 获取求助列表
	 */
	public class GetPJobListAsynTask extends AsyncTask<Integer, Void, Boolean>{

		long errcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(flag==0)
			ShowWaiting("获取职位列表……");
		}
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				errcode = SvrOperation.GetCompanyPJobInforeFreshTask(mContext, params[0],params[1]);
				
				if (errcode!=SvrInfo.SVR_RESULT_SUCCESS)
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
			int pjobnum=DB.getCompanyPJobInfoNum(mContext);
			sharedPreferences.edit().putInt("pjobnum", pjobnum).commit();
			mJobListList = DB.getCompanyPJobInfoList(mContext);
			if(mJobListList!=null)
				viewhandler.sendEmptyMessage(CLICK_SEND_ADAPTER_MSG);
			if(result== false){
				return ;
			}
			flag = 1;
		}
	}

	private void initViews() {
		/** 标题部分*/
		CenterTitle = (TextView)findViewById(R.id.title_text);
		LeftBackBtn = (ImageView)findViewById(R.id.home_back);
		CenterTitle.setText("职位");
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));
		/* 等待对话框部分 */
		WaitingText = (TextView)findViewById(R.id.Waiting_text);
		WaitingDlg  =(RelativeLayout)findViewById(R.id.Waiting_dlg);
		
		JobListList = (ListView) findViewById(R.id.pjob_list);
		refreshableView = (PullToRefreshLayout)findViewById(R.id.pjob_refreshable);
	
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
		default:
			break;
		}
	}
	
	public Handler viewhandler = new Handler(){
		public void handleMessage(Message message) {
			switch (message.what) {
			case CLICK_SEND_ADAPTER_MSG:
				// 设定一个适配器
				JobListAdapter = new PublishJobListAdapter(mContext,mJobListList,turntype,viewhandler);
				JobListList.setAdapter(JobListAdapter);
				refreshableView.setOnRefreshListener(new PJobListRefreshListener(userid,mContext,mJobListList,JobListAdapter,viewhandler));
				break;
			case 30000://处理下拉刷新后，列表没有改变问题
				initJobList();
				break;
			case 40000://删除之后刷新数据
				mJobListList = DB.getCompanyPJobInfoList(mContext);
				if(mJobListList!=null)
					viewhandler.sendEmptyMessage(CLICK_SEND_ADAPTER_MSG);
				break;
			}
			
		};
	};
	
	public void ShowWaiting(String text) {
		WaitingText.setText(text);
		WaitingDlg.setVisibility(View.VISIBLE);
	}

	public void HideWaiting() {
		WaitingDlg.setVisibility(View.GONE);
	}
}
