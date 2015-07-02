package com.parttime.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parttime.adapter.PersonalManageAdapter;
import com.parttime.constant.Constant;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.pullrefresh.PullToRefreshLayout;
import com.parttime.pullrefresh.listener.PJEngagePullfreshListener;
import com.parttime.struct.PartTimeStruct.HiredStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;

/**
 * 
 * @author 灰色的寂寞
 * @date 2015-1-12
 * @time 10：48
 * @function 个人管理（已申请、已雇佣、进行中、已完成）//人要有自己的喜好，有自己的追求，不然空空洞洞的，过着没激情
 */
public class Fragment_PersonalManage_Page extends Fragment {
	private static Context mContext = null;
	private PartTimeDB DB;
	ArrayList<HiredStruct> hirelist = new ArrayList<HiredStruct>();

	private static TextView PmanageAppliedCard;
	private static TextView PmanageHiredCard;
	private static TextView PmanageDoingCard;
	private static TextView PmanageFinishedCard;

	private ListView HireListView;// 列表listview
	private PersonalManageAdapter HirePageAdapter;// 雇佣状态adapter

	private PullToRefreshLayout refreshableView;
	/* 等待框部分 */
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;
	/* manage click */
	private static final int CLICK_TYPE_APPLIED = 1;
	private static final int CLICK_TYPE_HIRED = 2;
	private static final int CLICK_TYPE_DOING = 3;
	private static final int CLICK_TYPE_FINISHED = 4;

	private long userid = -1;
	private int shared_type = CLICK_TYPE_APPLIED;
	private SharedPreferences sharedPreferences;

	private int freshflag = 0;

	static Fragment_PersonalManage_Page newInstance(Context context) {
		Fragment_PersonalManage_Page framgment1 = new Fragment_PersonalManage_Page();
		mContext = context;
		return framgment1;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_personalmanage_page, container, false);
		DB = PartTimeDB.getInstance(mContext);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		userid = sharedPreferences.getLong("userid", -1);
		shared_type = sharedPreferences.getInt("pjengage_type", CLICK_TYPE_APPLIED);
		initViews(view);
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
		if (Submit.isNetworkAvailable(mContext))
			ReloadData(shared_type);
		else {
			Utils.ShowToast(mContext, "没有可用网络，请联网后刷新查看！");
		}
	}

	/**
	 * 初始化view
	 * 
	 * @param view
	 */
	private void initViews(View view) {
		PmanageAppliedCard = (TextView) view.findViewById(R.id.pmanage_applied);
		PmanageHiredCard = (TextView) view.findViewById(R.id.pmanage_hired);
		PmanageDoingCard = (TextView) view.findViewById(R.id.pmanage_doing);
		PmanageFinishedCard = (TextView) view.findViewById(R.id.pmanage_finished);

		PmanageAppliedCard.setOnClickListener(new MyOnClickListener(CLICK_TYPE_APPLIED));
		PmanageHiredCard.setOnClickListener(new MyOnClickListener(CLICK_TYPE_HIRED));
		PmanageDoingCard.setOnClickListener(new MyOnClickListener(CLICK_TYPE_DOING));
		PmanageFinishedCard.setOnClickListener(new MyOnClickListener(CLICK_TYPE_FINISHED));
		/* 等待对话框部分 */
		WaitingText = (TextView) view.findViewById(R.id.Waiting_text);
		WaitingDlg = (RelativeLayout) view.findViewById(R.id.Waiting_dlg);
		/** 列表部分 */
		HireListView = (ListView) view.findViewById(R.id.pmanage_content_list);
		refreshableView = (PullToRefreshLayout) view.findViewById(R.id.pmanage_refreshable);

		if (Submit.isNetworkAvailable(mContext))
			ReloadData(shared_type);
		else {
			Utils.ShowToast(mContext, "没有可用网络，请联网后刷新查看！");
		}

	}

	public Handler viewhandler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case CLICK_TYPE_APPLIED:
				HirePageAdapter = new PersonalManageAdapter(mContext, userid, hirelist, viewhandler, CLICK_TYPE_APPLIED);
				HireListView.setAdapter(HirePageAdapter);
				refreshableView.setOnRefreshListener(new PJEngagePullfreshListener(userid, mContext, hirelist, HirePageAdapter, CLICK_TYPE_APPLIED, viewhandler));
				break;
			case CLICK_TYPE_HIRED:
				HirePageAdapter = new PersonalManageAdapter(mContext, userid, hirelist, viewhandler, CLICK_TYPE_HIRED);
				HireListView.setAdapter(HirePageAdapter);
				refreshableView.setOnRefreshListener(new PJEngagePullfreshListener(userid, mContext, hirelist, HirePageAdapter, CLICK_TYPE_HIRED, viewhandler));
				break;
			case CLICK_TYPE_DOING:
				HirePageAdapter = new PersonalManageAdapter(mContext, userid, hirelist, viewhandler, CLICK_TYPE_DOING);
				HireListView.setAdapter(HirePageAdapter);
				refreshableView.setOnRefreshListener(new PJEngagePullfreshListener(userid, mContext, hirelist, HirePageAdapter, CLICK_TYPE_DOING, viewhandler));
				break;
			case CLICK_TYPE_FINISHED:
				HirePageAdapter = new PersonalManageAdapter(mContext, userid, hirelist, viewhandler, CLICK_TYPE_FINISHED);
				HireListView.setAdapter(HirePageAdapter);
				refreshableView.setOnRefreshListener(new PJEngagePullfreshListener(userid, mContext, hirelist, HirePageAdapter, CLICK_TYPE_FINISHED, viewhandler));
				break;
			case 50000:
				ReloadData(shared_type);
				break;
			case 10000:
				int type = message.arg2;
				ReloadData(type);
				break;
			}

		}
	};

	public static void ProcessCardUnEnable() {
		PmanageAppliedCard.setClickable(false);
		PmanageHiredCard.setClickable(false);
		PmanageDoingCard.setClickable(false);
		PmanageFinishedCard.setClickable(false);
	}

	public static void ProcessCardEnable() {
		PmanageAppliedCard.setClickable(true);
		PmanageHiredCard.setClickable(true);
		PmanageDoingCard.setClickable(true);
		PmanageFinishedCard.setClickable(true);
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

	@SuppressLint("NewApi")
	private void ManageClick(int mtype) {
		switch (mtype) {
		case CLICK_TYPE_APPLIED:
			if (freshflag == 0) {
				showCard(CLICK_TYPE_APPLIED);
				ReloadData(CLICK_TYPE_APPLIED);
			}
			break;
		case CLICK_TYPE_HIRED:
			if (freshflag == 0) {
				showCard(CLICK_TYPE_HIRED);
				ReloadData(CLICK_TYPE_HIRED);
			}
			break;
		case CLICK_TYPE_DOING:
			if (freshflag == 0) {
				showCard(CLICK_TYPE_DOING);
				ReloadData(CLICK_TYPE_DOING);
			}
			break;
		case CLICK_TYPE_FINISHED:
			if (freshflag == 0) {
				showCard(CLICK_TYPE_FINISHED);
				ReloadData(CLICK_TYPE_FINISHED);
			}
			break;
		}

	}

	/**
	 * 显示不同的字体
	 * 
	 * @param type
	 */
	private int CardSelectTextColor = R.color.red;
	private int CardNormalTextColor = R.color.white;

	private void showCard(int type) {
		switch (type) {
		case CLICK_TYPE_APPLIED:
			PmanageAppliedCard.setTextColor(getResources().getColor(CardSelectTextColor));
			PmanageHiredCard.setTextColor(getResources().getColor(CardNormalTextColor));
			PmanageDoingCard.setTextColor(getResources().getColor(CardNormalTextColor));
			PmanageFinishedCard.setTextColor(getResources().getColor(CardNormalTextColor));
			break;
		case CLICK_TYPE_HIRED:
			PmanageAppliedCard.setTextColor(getResources().getColor(CardNormalTextColor));
			PmanageHiredCard.setTextColor(getResources().getColor(CardSelectTextColor));
			PmanageDoingCard.setTextColor(getResources().getColor(CardNormalTextColor));
			PmanageFinishedCard.setTextColor(getResources().getColor(CardNormalTextColor));
			break;
		case CLICK_TYPE_DOING:
			PmanageAppliedCard.setTextColor(getResources().getColor(CardNormalTextColor));
			PmanageHiredCard.setTextColor(getResources().getColor(CardNormalTextColor));
			PmanageDoingCard.setTextColor(getResources().getColor(CardSelectTextColor));
			PmanageFinishedCard.setTextColor(getResources().getColor(CardNormalTextColor));
			break;
		case CLICK_TYPE_FINISHED:
			PmanageAppliedCard.setTextColor(getResources().getColor(CardNormalTextColor));
			PmanageHiredCard.setTextColor(getResources().getColor(CardNormalTextColor));
			PmanageDoingCard.setTextColor(getResources().getColor(CardNormalTextColor));
			PmanageFinishedCard.setTextColor(getResources().getColor(CardSelectTextColor));
			break;
		}
	}

	/**
	 * 加载本地数据列表，如果没有从新加载
	 */
	public void ReloadData(int TaskType) {
		if (Submit.isNetworkAvailable(mContext)) {
			freshflag = -1;
			new GetHiredListInfoAsynTask().execute(TaskType);
		} else {
			Utils.ShowToast(mContext, "没有可用网络！");
			switch (TaskType) {
			case CLICK_TYPE_APPLIED:// 已申请
				hirelist = getList(CLICK_TYPE_APPLIED);
				viewhandler.sendEmptyMessage(CLICK_TYPE_APPLIED);
				break;
			case CLICK_TYPE_HIRED:// 被雇佣
				hirelist = getList(CLICK_TYPE_HIRED);
				viewhandler.sendEmptyMessage(CLICK_TYPE_HIRED);
				break;
			case CLICK_TYPE_DOING:// 进行中
				hirelist = getList(CLICK_TYPE_DOING);
				viewhandler.sendEmptyMessage(CLICK_TYPE_DOING);
				break;
			case CLICK_TYPE_FINISHED:// 已完成
				hirelist = getList(CLICK_TYPE_FINISHED);
				viewhandler.sendEmptyMessage(CLICK_TYPE_FINISHED);
				break;
			}
		}
	}

	public ArrayList<HiredStruct> getList(int type) {
		if (hirelist != null)
			hirelist.clear();
		ArrayList<HiredStruct> list = DB.getHiredJobList(mContext, type);
		if (type == CLICK_TYPE_APPLIED) {
			if (list == null || list.size() == 0)
				Utils.ShowToast(mContext, "没有已申请的数据！");
		} else if (type == CLICK_TYPE_HIRED) {
			if (list == null || list.size() == 0)
				Utils.ShowToast(mContext, "没有被雇佣的数据！");
		} else if (type == CLICK_TYPE_DOING) {
			if (list == null || list.size() == 0)
				Utils.ShowToast(mContext, "没有进行中的数据！");
		} else if (type == CLICK_TYPE_FINISHED) {
			if (list == null || list.size() == 0)
				Utils.ShowToast(mContext, "没有已完成的数据！");
		}
		return list;
	}

	public void ShowWaiting(String text) {
		WaitingText.setText(text);
		WaitingDlg.setVisibility(View.VISIBLE);
	}

	public void HideWaiting() {
		WaitingDlg.setVisibility(View.GONE);
	}

	/**
	 * 获取雇佣关系列表存储到数据库
	 */
	public class GetHiredListInfoAsynTask extends AsyncTask<Integer, Void, Boolean> {
		int errorcode = -1;
		int type = -1;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ProcessCardUnEnable();
			ShowWaiting("正在获取列表……");
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				type = params[0];
				errorcode = SvrOperation.getTaskJobList(mContext);

				if (errorcode != SvrInfo.SVR_RESULT_SUCCESS)
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
			freshflag = 0;
			ProcessCardEnable();
			if (result == false) {
				return;
			}
			switch (type) {
			case CLICK_TYPE_APPLIED:// 已申请
				hirelist = getList(CLICK_TYPE_APPLIED);
				viewhandler.sendEmptyMessage(CLICK_TYPE_APPLIED);
				break;
			case CLICK_TYPE_HIRED:// 被雇佣
				hirelist = getList(CLICK_TYPE_HIRED);
				viewhandler.sendEmptyMessage(CLICK_TYPE_HIRED);
				break;
			case CLICK_TYPE_DOING:// 进行中
				hirelist = getList(CLICK_TYPE_DOING);
				viewhandler.sendEmptyMessage(CLICK_TYPE_DOING);
				break;
			case CLICK_TYPE_FINISHED:// 已完成
				hirelist = getList(CLICK_TYPE_FINISHED);
				viewhandler.sendEmptyMessage(CLICK_TYPE_FINISHED);
				break;
			}
		}
	}
}
