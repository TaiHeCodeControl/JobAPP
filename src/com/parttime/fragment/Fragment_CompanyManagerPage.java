package com.parttime.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parttime.adapter.PublishJobListAdapter;
import com.parttime.constant.Constant;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.pullrefresh.PullToRefreshLayout;
import com.parttime.pullrefresh.PullableListView;
import com.parttime.pullrefresh.listener.PJobListRefreshListener;
import com.parttime.struct.PartTimeStruct.CompanyPJobListStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;

/**
 * 
 * @author 灰色的寂寞
 * @function 企业账号显示的管理部分（用工管理、职位管理）
 * 
 */
public class Fragment_CompanyManagerPage extends Fragment {
	private static Context mContext;
	private PublishJobListAdapter JobListAdapter = null;
	private ArrayList<CompanyPJobListStruct> mJobListList = new ArrayList<CompanyPJobListStruct>();
	/** view 部分 ****************************************************/
	/* 等待框部分 */
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;

	private TextView EmployerCardBtn, JobCardBtn;
	private LinearLayout TopCardBackBlock;

	private PullableListView JobListList;
	private PullToRefreshLayout refreshableView;
	/** 常量部分 ****************************************************/
	/** 用工管理 */
	private static final int MENU_PERSONAL_MANAGEWORKER = 1;
	/** 职位管理 */
	private static final int MENU_PERSONAL_MANAGEJOB = 2;

	private static final int CLICK_SEND_ADAPTER_MSG = 3;
	private SharedPreferences sharedPreferences;
	private long userid = -1;
	private String avatar = "";
	private String name = "";
	private int turntype;
	private int flag = 0;

	private PartTimeDB DB;

	/**
	 * 标题卡背景
	 */
	private static final int CLICK_TYPE_BACK_RIGHT_PRESSED = R.drawable.ask_vol_btn_block_back_pressed;
	private static final int CLICK_TYPE_BACK_LEFT_NORMAL = R.drawable.ask_vol_btn_block_back_normal;
	private static final int CLICK_TYPE_BACK_LEFT_PRESSED = R.drawable.ask_vol_btn_block_back_pressed1;
	private static final int CLICK_TYPE_BACK_RIGHT_NORMAL = R.drawable.ask_vol_btn_block_back_normal1;

	static Fragment_CompanyManagerPage newInstance(Context context) {
		Fragment_CompanyManagerPage framgment1 = new Fragment_CompanyManagerPage();
		mContext = context;
		return framgment1;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_companymanage_layout, container, false);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		userid = sharedPreferences.getLong("userid", -1);
		avatar = sharedPreferences.getString("avatar", "");
		name = sharedPreferences.getString("username", "");
		turntype = MENU_PERSONAL_MANAGEWORKER;
		DB = PartTimeDB.getInstance(mContext);
		initViews(view);
		initJobList();
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		Utils.CommitPageFlagToShared(mContext, Constant.FINISH_ACTIVITY_FLAG_MANAGER);
	}

	@Override
	public void onResume() {
		super.onResume();
		flag = 0;
	}

	/**
	 * 获取默认的工作列表
	 */
	public void initJobList() {
		if (Submit.isNetworkAvailable(mContext)) {
			new GetPJobListAsynTask().execute(0, (int) userid);
		} else {
			mJobListList = DB.getCompanyPJobInfoList(mContext);
			if (mJobListList != null)
				viewhandler.sendEmptyMessage(CLICK_SEND_ADAPTER_MSG);
		}
	}

	/**
	 * 获取求助列表
	 */
	public class GetPJobListAsynTask extends AsyncTask<Integer, Void, Boolean> {

		long errcode = -1;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (flag == 0)
				ShowWaiting("获取职位列表……");
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				errcode = SvrOperation.GetCompanyPJobInforeFreshTask(mContext, params[0], params[1]);

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
			int pjobnum = DB.getCompanyPJobInfoNum(mContext);
			sharedPreferences.edit().putInt("pjobnum", pjobnum).commit();
			mJobListList = DB.getCompanyPJobInfoList(mContext);
			if (mJobListList != null)
				viewhandler.sendEmptyMessage(CLICK_SEND_ADAPTER_MSG);
			if (result == false) {
				Utils.ShowToast(mContext, "没有职位！");
				return;
			}
			flag = 1;
		}
	}

	private void initViews(View view) {
		EmployerCardBtn = (TextView) view.findViewById(R.id.cmanage_employer);
		JobCardBtn = (TextView) view.findViewById(R.id.cmanage_job);
		TopCardBackBlock = (LinearLayout) view.findViewById(R.id.cmanage_content_card);

		EmployerCardBtn.setOnClickListener(new MyOnClickListener(MENU_PERSONAL_MANAGEWORKER));
		JobCardBtn.setOnClickListener(new MyOnClickListener(MENU_PERSONAL_MANAGEJOB));
		/* 等待对话框部分 */
		WaitingText = (TextView) view.findViewById(R.id.Waiting_text);
		WaitingDlg = (RelativeLayout) view.findViewById(R.id.Waiting_dlg);

		JobListList = (PullableListView) view.findViewById(R.id.cmanage_content_list);
		refreshableView = (PullToRefreshLayout) view.findViewById(R.id.cmanage_refreshable);
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
	@SuppressWarnings("deprecation")
	private void ManageClick(int mtype) {
		switch (mtype) {
		case MENU_PERSONAL_MANAGEWORKER:

			EmployerCardBtn.setBackgroundColor(getResources().getColor(R.color.main_btn_press));
			EmployerCardBtn.setTextColor(getResources().getColor(R.color.white));

			JobCardBtn.setBackgroundColor(getResources().getColor(android.R.color.transparent));
			JobCardBtn.setTextColor(getResources().getColor(R.color.black));

			turntype = MENU_PERSONAL_MANAGEWORKER;
			viewhandler.sendEmptyMessage(CLICK_SEND_ADAPTER_MSG);
			break;

		case MENU_PERSONAL_MANAGEJOB:

			JobCardBtn.setBackgroundColor(getResources().getColor(R.color.main_btn_press));
			JobCardBtn.setTextColor(getResources().getColor(R.color.white));

			EmployerCardBtn.setBackgroundColor(getResources().getColor(android.R.color.transparent));
			EmployerCardBtn.setTextColor(getResources().getColor(R.color.black));

			turntype = MENU_PERSONAL_MANAGEJOB;
			viewhandler.sendEmptyMessage(CLICK_SEND_ADAPTER_MSG);
			break;
		}
	}

	public Handler viewhandler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case CLICK_SEND_ADAPTER_MSG:
				// 设定一个适配器
				JobListAdapter = new PublishJobListAdapter(mContext, mJobListList, turntype, viewhandler);
				JobListList.setAdapter(JobListAdapter);
				refreshableView.setOnRefreshListener(new PJobListRefreshListener(userid, mContext, mJobListList, JobListAdapter, viewhandler));
				break;
			case 30000:// 处理下拉刷新后，列表没有改变问题
				initJobList();
				break;
			case 40000:// 删除之后刷新数据
				mJobListList = DB.getCompanyPJobInfoList(mContext);
				if (mJobListList != null)
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
