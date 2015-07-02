package com.parttime.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.parttime.activity.Job_See_Resume;
import com.parttime.adapter.ApplyJob_ListView_Adapter;
import com.parttime.constant.Constant;
import com.parttime.data.PartTimeDB;
import com.parttime.msg.ErrorMsgSvr;
import com.parttime.parttimejob.R;
import com.parttime.pullrefresh.PullToRefreshLayout;
import com.parttime.pullrefresh.listener.ApplyJobPullfreshListener;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.BaiduMap_GetLatLng_Utils;
import com.parttime.utils.Utils;

/**
 * 求职
 * 
 * @author huxixi
 */
public class Fragement_ApplyJobPage extends Fragment {
	private static Context mContext = null;

	private static PartTimeDB parttimeDb;

	private TextView job_title;
	private TextView job_location;
	private TextView job_commission;

	private ImageView job_title_image;

	/* 等待框部分 */
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;

	// 屏幕的width
	private int mScreenWidth;
	// 屏幕的height
	private int mScreenHeight;
	// PopupWindow的width
	private int mPopupWindowWidth;

	private int kilometre;
	private String select_flag = "";

	private RadioGroup radiogroup;
	private RadioButton radio1, radio2, radio3, radio4, radio5, radio6;
	private Button confirm_btn;
	private PopupWindow popupWindow;

	/**
	 * job页中的listview
	 */
	private ListView job_list;// 列表listview
	private ApplyJob_ListView_Adapter job_listview_adapter = null;
	// private ArrayList<HashMap<String,Object>> list=null;

	private PullToRefreshLayout refreshableView;
	/* manage click */
	private static final int CLICK_TYPE_ASKHELP = 1;
	private static final int CLICK_TYPE_VOLUNTEER = 2;
	private static final int TYPE_OPERATION = CLICK_TYPE_ASKHELP;

	private HashMap<String, String> input_map = null;
	private ArrayList<HashMap<String, String>> data_list = null;
	private ArrayList<HashMap<String, Object>> part_list = null;

	static Fragement_ApplyJobPage newInstance(Context context) {
		Fragement_ApplyJobPage framgment1 = new Fragement_ApplyJobPage();
		mContext = context;
		parttimeDb = PartTimeDB.getInstance(mContext);
		return framgment1;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_jobpager, container, false);

		parttimeDb.ClearPublicResumeList();
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

	private void initView(View view) {

		job_list = (ListView) view.findViewById(R.id.job_listview);
		refreshableView = (PullToRefreshLayout) view.findViewById(R.id.job_refreshable);
		job_title = (TextView) view.findViewById(R.id.job_title);
		job_location = (TextView) view.findViewById(R.id.job_location);
		job_commission = (TextView) view.findViewById(R.id.job_commission);
		job_title.setText(mContext.getResources().getString(R.string.job_seeker));
		job_commission.setText(mContext.getResources().getString(R.string.job_title));

		// job_title_image=(ImageView)view.findViewById(R.id.job_title_image);
		// job_title_image.setVisibility(View.INVISIBLE);
		job_title.setCompoundDrawables(null, null, null, null);
		// 获取屏幕密度（方法2）
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();

		mScreenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
		mScreenHeight = dm.heightPixels; // 屏幕高（像素，如：800px）

		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View popupView = layoutInflater.inflate(R.layout.select_location_popup, null);
		initPopup(popupView);
		popupWindow = new PopupWindow(popupView, mScreenWidth * 2 / 5, LayoutParams.WRAP_CONTENT, true);
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		/* 等待对话框部分 */
		WaitingText = (TextView) view.findViewById(R.id.Waiting_text);
		WaitingDlg = (RelativeLayout) view.findViewById(R.id.Waiting_dlg);

		job_location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.showAsDropDown(v);
			}
		});

		input_map = new HashMap<String, String>();
		input_map.put("count", "0");

		new GetPublicResume_Task().execute(input_map);

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
		confirm_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ArrayList<HashMap<String, Object>> filter_list = parttimeDb.getPublicReusmeInfobylocation(mContext, kilometre, select_flag);
				if (filter_list != null && filter_list.size() > 0) {
					if (job_listview_adapter != null) {
						job_listview_adapter.refresh(filter_list);
					} else {
						job_listview_adapter = new ApplyJob_ListView_Adapter(getActivity(), filter_list, "seeker");
						job_list.setAdapter(job_listview_adapter);
					}
				} else if (filter_list.size() == 0) {
					CleanDownloadTips(mContext, "您所查找范围内的求职者不存在，请重新查找");
				}

				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();

				}
			}
		});
	}

	public void CleanDownloadTips(Context context, String name) {

		new AlertDialog.Builder(context).setTitle(getResources().getText(R.string.user_profile_warming)).setMessage(name).show();

	}

	public Handler viewhandler = new Handler() {
		public void handleMessage(Message message) {
			// final ArrayList<HashMap<String, String>> list1 = data_list;
			switch (message.what) {
			case CLICK_TYPE_ASKHELP:

				ArrayList<Integer> location_list = new ArrayList<Integer>();
				for (int i = 0; i < data_list.size(); i++) {
					String company_addr = data_list.get(i).get("position");
					if (company_addr != null) {
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
					} else {
						location_list.add(0);

					}
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("height", data_list.get(i).get("height"));
					map.put("type", data_list.get(i).get("type"));
					map.put("name", data_list.get(i).get("name"));
					map.put("sex", data_list.get(i).get("sex"));
					map.put("start_time", data_list.get(i).get("start_time"));
					map.put("experence", data_list.get(i).get("experence"));
					map.put("addr", data_list.get(i).get("addr"));
					map.put("job", data_list.get(i).get("job"));
					map.put("rid", data_list.get(i).get("rid"));
					map.put("user_id", data_list.get(i).get("user_id"));
					map.put("call", data_list.get(i).get("call"));
					map.put("public", data_list.get(i).get("public"));
					map.put("end_time", data_list.get(i).get("end_time"));
					map.put("create_time", data_list.get(i).get("create_time"));
					map.put("position", data_list.get(i).get("position"));
					if (parttimeDb.IsExistPublicResumeInfo(data_list.get(i).get("rid"), mContext)) {
						parttimeDb.UpdatePublicResumeInfo1(mContext, map, location_list.get(i), data_list.get(i).get("rid"));
					} else {
						parttimeDb.AddPublicCustomerInfo1(mContext, map, location_list.get(i));
					}

				}
				part_list = parttimeDb.getPublicReusmeInfo1(mContext);

				job_listview_adapter = new ApplyJob_ListView_Adapter(getActivity(), part_list, "seeker");
				job_list.setAdapter(job_listview_adapter);

				break;
			case CLICK_TYPE_VOLUNTEER:
				break;
			}

			refreshableView.setOnRefreshListener(new ApplyJobPullfreshListener(mContext, part_list, job_listview_adapter));
			job_list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), Job_See_Resume.class);
					intent.putExtra("flag", "job");
					getActivity().startActivity(intent);

				}
			});
		};
	};

	/*
	 * public Handler viewhandler = new Handler(){ public void
	 * handleMessage(Message message) { switch (message.what) { case
	 * CLICK_TYPE_ASKHELP: job_listview_adapter=new
	 * ApplyJob_ListView_Adapter(mContext, data_list);
	 * job_list.setAdapter(job_listview_adapter);
	 * 
	 * break; case CLICK_TYPE_VOLUNTEER: break; }
	 * 
	 * refreshableView.setOnRefreshListener(new
	 * ApplyJobPullfreshListener(mContext,data_list,job_listview_adapter)); //
	 * job_list.setOnItemLongClickListener(new OnItemLongClickListener(){ //
	 * 
	 * @Override // public boolean onItemLongClick(AdapterView<?> parent, View
	 * view,int position, long id){ // return true; // } // }); // //
	 * job_list.setOnItemClickListener(new OnItemClickListener() { // //
	 * 
	 * @Override // public void onItemClick(AdapterView<?> parent, View view, //
	 * int position, long id) { // Intent intent=new Intent(); //
	 * intent.setClass(getActivity(), Job_See_Resume.class); //
	 * intent.putExtra("flag", "job"); // getActivity().startActivity(intent);
	 * // // } // }); }; };
	 */

	/**
	 * 获取求职信息列表的异步
	 * 
	 * @author huxixi
	 * 
	 */
	public class GetPublicResume_Task extends AsyncTask<HashMap<String, String>, Void, Boolean> {

		private int errcode;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在获取求职信息列表...");
		}

		@Override
		protected Boolean doInBackground(HashMap<String, String>... params) {
			int iresult = SvrOperation.getPublicResume(mContext, params[0]);
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
					Utils.ShowToast(mContext, "没有求职信息");
				else
					Utils.ShowToast(mContext, errmsg);
				return;
			}
			data_list = parttimeDb.getPublicReusmeInfo(mContext);

			viewhandler.sendEmptyMessage(TYPE_OPERATION);

			return;
		}

	}

}
