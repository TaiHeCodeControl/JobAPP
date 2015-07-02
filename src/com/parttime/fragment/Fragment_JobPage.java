package com.parttime.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.parttime.adapter.Job_ListView_Adapter1;
import com.parttime.constant.Constant;
import com.parttime.data.PartTimeDB;
import com.parttime.msg.ErrorMsgSvr;
import com.parttime.parttimejob.R;
import com.parttime.pullrefresh.PullToRefreshLayout;
import com.parttime.pullrefresh.listener.JobPullfreshListener;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.BaiduMap_GetLatLng_Utils;
import com.parttime.utils.Utils;
import com.parttime.view.ClearEditText;

/**
 * 工作
 */
public class Fragment_JobPage extends Fragment {
	private static Context mContext = null;
	public static final int TITLE_CLICK_INT = 1;
	public static final int LOCATION_CLICK_INT = 2;
	public static final int CONFIRM_CLICK_INT = 3;
	public static final int SEARCH_CLICK_INT = 4;
	public static final int CONFIRM_TIME_CLICK_INT = 5;

	private static PartTimeDB parttimeDb;
	private ArrayList<HashMap<String, String>> data_list;

	private HashMap<String, String> input_map = null;

	/* 等待框部分 */
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;

	/**
	 * job页中的listview
	 */
	private ListView job_list;// 列表listview
	private Job_ListView_Adapter1 job_listview_adapter = null;
	ArrayList<HashMap<String, Object>> part_list = null;

	private PullToRefreshLayout refreshableView;

	/**
	 * 筛选框的控件
	 * */
	private RelativeLayout job_title_relative;
	private RelativeLayout job_location_relative;
	private TextView job_title;
	private TextView job_location;

	private PopupWindow popupWindow;
	private PopupWindow popupWindow_position;
	private TextView text;

	private RadioGroup radiogroup;
	private RadioButton radio1, radio2, radio3, radio4, radio5, radio6;
	private Button confirm_btn;

	private ClearEditText searchbox;
	private Button search_btn;
	private Button confirm_btn_position;
	private RadioGroup radiogroup_position;
	private RadioButton radio_day, radio_third_day, radio_week;

	// 屏幕的width
	private int mScreenWidth;
	// 屏幕的height
	private int mScreenHeight;
	// PopupWindow的width
	private int mPopupWindowWidth;

	private int kilometre;
	private String select_flag = "";

	private String time_flag = "1"; // 1表示一天内，3表示3天内，7表示一周内

	/* manage click */
	private static final int CLICK_TYPE_ASKHELP = 1;
	private static final int CLICK_TYPE_VOLUNTEER = 2;
	private static final int TYPE_OPERATION = CLICK_TYPE_ASKHELP;

	static Fragment_JobPage newInstance(Context context) {
		Fragment_JobPage framgment1 = new Fragment_JobPage();
		mContext = context;
		parttimeDb = PartTimeDB.getInstance(mContext);
		return framgment1;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_jobpager, container, false);

		initView(view);
		return view;
	}

	public void ShowWaiting(String text) {
		WaitingText.setText(text);
		WaitingDlg.setVisibility(View.VISIBLE);
	}

	public void HideWaiting() {
		WaitingDlg.setVisibility(View.GONE);
	}

	@Override
	public void onPause() {
		super.onPause();
		Utils.CommitPageFlagToShared(mContext, Constant.FINISH_ACTIVITY_FLAG_JOB);
	}

	private void initView(View v) {

		/** 列表部分 */
		job_list = (ListView) v.findViewById(R.id.job_listview);
		refreshableView = (PullToRefreshLayout) v.findViewById(R.id.job_refreshable);
		input_map = new HashMap<String, String>();
		input_map.put("count", "0");

		/* 等待对话框部分 */
		WaitingText = (TextView) v.findViewById(R.id.Waiting_text);
		WaitingDlg = (RelativeLayout) v.findViewById(R.id.Waiting_dlg);

		job_title_relative = (RelativeLayout) v.findViewById(R.id.job_title_relative);
		job_location_relative = (RelativeLayout) v.findViewById(R.id.job_location_relative);

		job_title = (TextView) v.findViewById(R.id.job_title);
		job_location = (TextView) v.findViewById(R.id.job_location);

		// 获取屏幕密度（方法2）
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();

		mScreenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
		mScreenHeight = dm.heightPixels; // 屏幕高（像素，如：800px）

		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View popupView = layoutInflater.inflate(R.layout.select_location_popup, null);
		initPopup(popupView);
		View popupView_position = layoutInflater.inflate(R.layout.select_position_popup, null);
		initpopup_position(popupView_position);
		popupWindow = new PopupWindow(popupView, mScreenWidth * 2 / 5, LayoutParams.WRAP_CONTENT, true);
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		popupWindow_position = new PopupWindow(popupView_position, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		popupWindow_position.setTouchable(true);
		popupWindow_position.setOutsideTouchable(true);
		popupWindow_position.setBackgroundDrawable(new BitmapDrawable());

		job_title_relative.setOnClickListener(new MyClickListener(TITLE_CLICK_INT));
		job_location_relative.setOnClickListener(new MyClickListener(LOCATION_CLICK_INT));
		job_title.setOnClickListener(new MyClickListener(TITLE_CLICK_INT));
		new Job_Task().execute(input_map);

	}

	private void initPopup(View popupView) {
		radiogroup = (RadioGroup) popupView.findViewById(R.id.radiogroup1);
		radio1 = (RadioButton) popupView.findViewById(R.id.radiobutton1);
		radio2 = (RadioButton) popupView.findViewById(R.id.radiobutton2);
		radio3 = (RadioButton) popupView.findViewById(R.id.radiobutton3);
		radio4 = (RadioButton) popupView.findViewById(R.id.radiobutton4);
		radio5 = (RadioButton) popupView.findViewById(R.id.radiobutton5);
		radio6 = (RadioButton) popupView.findViewById(R.id.radiobutton6);
		confirm_btn = (Button) popupView.findViewById(R.id.confirm);

		radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == radio1.getId()) {
					kilometre = 5000;
					select_flag = "";
				} else if (checkedId == radio2.getId()) {
					kilometre = 7000;
					select_flag = "";
				} else if (checkedId == radio3.getId()) {
					kilometre = 10000;
					select_flag = "";
				} else if (checkedId == radio4.getId()) {
					kilometre = 12000;
					select_flag = "";
				} else if (checkedId == radio5.getId()) {
					kilometre = 16000;
					select_flag = "";
				} else if (checkedId == radio6.getId()) {
					kilometre = 16000;
					select_flag = "rest";
				}

			}
		});
		confirm_btn.setOnClickListener(new MyClickListener(CONFIRM_CLICK_INT));
	}

	private void initpopup_position(View v) {
		searchbox = (ClearEditText) v.findViewById(R.id.search_edit);
		search_btn = (Button) v.findViewById(R.id.search_btn);
		confirm_btn_position = (Button) v.findViewById(R.id.confirm_btn);

		radiogroup_position = (RadioGroup) v.findViewById(R.id.radiogroup_time);
		radio_day = (RadioButton) v.findViewById(R.id.radiobutton1);
		radio_third_day = (RadioButton) v.findViewById(R.id.radiobutton2);
		radio_week = (RadioButton) v.findViewById(R.id.radiobutton3);

		radiogroup_position.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == radio_day.getId()) {
					time_flag = "1";
				} else if (checkedId == radio_third_day.getId()) {
					time_flag = "3";
				} else if (checkedId == radio_week.getId()) {
					time_flag = "7";
				}

			}
		});

		search_btn.setOnClickListener(new MyClickListener(SEARCH_CLICK_INT));
		confirm_btn_position.setOnClickListener(new MyClickListener(CONFIRM_TIME_CLICK_INT));

	}

	public class MyClickListener implements View.OnClickListener {

		int index;

		public MyClickListener(int index) {
			this.index = index;
		}

		@Override
		public void onClick(View v) {
			switch (index) {
			case TITLE_CLICK_INT:

				popupWindow_position.showAsDropDown(v);
				break;
			case LOCATION_CLICK_INT:
				// Utils.ShowToast(mContext, "popupwindow");
				popupWindow.showAsDropDown(v);
				break;
			case CONFIRM_CLICK_INT:
				ArrayList<HashMap<String, Object>> filter_list = parttimeDb.filterJobInfo(mContext, kilometre, select_flag);

				if (filter_list != null && filter_list.size() > 0) {
					if (job_listview_adapter != null) {
						job_listview_adapter.refresh(filter_list);
					} else {
						job_listview_adapter = new Job_ListView_Adapter1(getActivity(), filter_list, "job");
						job_list.setAdapter(job_listview_adapter);
					}
				} else if (filter_list.size() == 0) {
					CleanDownloadTips(mContext, "所查找位置的求职信息不存在，请重新查找");
				}

				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();

				}
				break;
			case SEARCH_CLICK_INT:
				ArrayList<HashMap<String, Object>> filter_list_position = parttimeDb.filterJobInfobyposition(mContext, searchbox.getText().toString());

				if (filter_list_position != null && filter_list_position.size() > 0) {
					if (job_listview_adapter != null) {
						job_listview_adapter.refresh(filter_list_position);
					} else {
						job_listview_adapter = new Job_ListView_Adapter1(getActivity(), filter_list_position, "job");
						job_list.setAdapter(job_listview_adapter);
					}
				} else if (filter_list_position.size() == 0) {
					CleanDownloadTips(mContext, "所查找职位的求职信息不存在，请重新查找");
				}

				if (popupWindow_position != null && popupWindow_position.isShowing()) {
					popupWindow_position.dismiss();

				}
				break;
			case CONFIRM_TIME_CLICK_INT:
				ArrayList<HashMap<String, Object>> filter_list_time = parttimeDb.filterJobInfobytime(mContext, time_flag);

				if (filter_list_time != null && filter_list_time.size() > 0) {
					if (job_listview_adapter != null) {
						job_listview_adapter.refresh(filter_list_time);
					} else {
						job_listview_adapter = new Job_ListView_Adapter1(getActivity(), filter_list_time, "job");
						job_list.setAdapter(job_listview_adapter);
					}
				} else if (filter_list_time.size() == 0) {
					CleanDownloadTips(mContext, "所查找职位的求职信息不存在，请重新查找");
				}

				if (popupWindow_position != null && popupWindow_position.isShowing()) {
					popupWindow_position.dismiss();

				}
				break;
			}

		}

	}

	public Handler viewhandler = new Handler() {
		public void handleMessage(Message message) {
			// final ArrayList<HashMap<String, String>> list1 = data_list;
			switch (message.what) {
			case CLICK_TYPE_ASKHELP:

				ArrayList<Integer> location_list = new ArrayList<Integer>();
				for (int i = 0; i < data_list.size(); i++) {
					String company_addr = data_list.get(i).get("position");
					String[] lat_lng = company_addr.split(",");
					if (lat_lng != null && lat_lng.length == 2) {
						double lat = Double.parseDouble(lat_lng[0]);
						double lgn = Double.parseDouble(lat_lng[1]);
						LatLng ll = BaiduMap_GetLatLng_Utils.mLocal_LatLng;
						LatLng la = new LatLng(lat, lgn);
						double meter = DistanceUtil.getDistance(la, ll);
						location_list.add((int) meter);
					} else {
						location_list.add(0);
					}

				}
				for (int i = 0; i < data_list.size(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("hot_id", data_list.get(i).get("hot_id"));
					map.put("name", data_list.get(i).get("name"));
					map.put("charge", data_list.get(i).get("charge"));
					map.put("type", data_list.get(i).get("type"));
					map.put("num", data_list.get(i).get("num"));
					map.put("create_time", data_list.get(i).get("create_time"));
					map.put("company_name", data_list.get(i).get("company_name"));
					map.put("company_add", data_list.get(i).get("company_add"));
					map.put("company_level", data_list.get(i).get("company_level"));
					map.put("position", data_list.get(i).get("position"));
					map.put("company_id", data_list.get(i).get("company_id"));
					if (parttimeDb.IsExistJobInfo(data_list.get(i).get("hot_id"), mContext)) {
						parttimeDb.UpdateJobInfo1(mContext, map, location_list.get(i), data_list.get(i).get("hot_id"));
					} else {
						parttimeDb.AddJobInfo1(mContext, map, location_list.get(i));
					}
				}
				part_list = parttimeDb.getJobInfo1(mContext);
				job_listview_adapter = new Job_ListView_Adapter1(getActivity(), part_list, "job");
				job_list.setAdapter(job_listview_adapter);

				break;
			case CLICK_TYPE_VOLUNTEER:
				break;
			}

			refreshableView.setOnRefreshListener(new JobPullfreshListener(mContext, part_list, job_listview_adapter));
			refreshableView.setOnRefreshListener(new JobPullfreshListener(mContext, part_list, job_listview_adapter));
			job_list.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					return true;
				}
			});

		};
	};

	public void CleanDownloadTips(Context context, String name) {

		new AlertDialog.Builder(context).setTitle(getResources().getText(R.string.user_profile_warming)).setMessage(name).show();

	}

	/**
	 * 工作列表的异步
	 * 
	 * @author huxixi
	 * 
	 */
	public class Job_Task extends AsyncTask<HashMap<String, String>, Void, Boolean> {

		private int errcode;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在获取工作列表...");
		}

		@Override
		protected Boolean doInBackground(HashMap<String, String>... params) {

			int iresult = SvrOperation.Job_List(mContext, params[0]);
			if (iresult != SvrInfo.SVR_RESULT_SUCCESS) {
				errcode = iresult;
				return false;
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			HideWaiting();
			if (result == false) {
				int errmsg = ErrorMsgSvr.ErrorMsg(errcode);
				if (errcode == -9)
					Utils.ShowToast(mContext, "没有工作信息");
				else
					Utils.ShowToast(mContext, errmsg);
				return;
			}
			data_list = parttimeDb.getJobInfo(mContext);
			viewhandler.sendEmptyMessage(TYPE_OPERATION);

			return;
		}

	}

}
