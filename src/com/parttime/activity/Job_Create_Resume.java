package com.parttime.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.parttime.activity.Job_Issue_Activity.Job_Issue_Task;
import com.parttime.constant.Constant;
import com.parttime.data.PartTimeDB;
import com.parttime.msg.ErrorMsgSvr;
import com.parttime.parttimejob.R;
import com.parttime.selecttime.ScreenInfo;
import com.parttime.selecttime.WheelMain;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.BaiduMap_GetLatLng_Utils;
import com.parttime.utils.Utils;
import com.parttime.view.MyImageSpinner;

/**
 * 工作中的新建简历页面和简历查看页面
 * 
 * @author huxixi
 * 
 */
public class Job_Create_Resume extends Activity implements OnGetGeoCoderResultListener {

	public static final int BACK_INT = 1;
	public static final int CONFIRM_INT = 2;
	public static final int PROVINCE_SPINNER_INT = 3;
	public static final int CITY_SPINNER_INT = 4;

	public static final int AREA_SPINNER_INT = 5;
	public static final int START_TIME_BTN_INT = 6;
	public static final int END_TIME_BTN_INT = 7;
	public static final int DESIRED_SPINNER_INT = 8;

	private Context context = null;
	private String create_flag = null;
	private TextView titleText;
	private ImageView home_back;

	private String sex_flag = "男"; // 男是1，女是2
	private String type_flag = "时"; // hour 是1，day是2，week是3

	private MyImageSpinner desired_spinner;
	// private EditText desired_job_location_text;
	private RadioGroup sex_radiogroup;
	private RadioButton sex_male;
	private RadioButton sex_female;
	private RadioGroup type_radiogroup;
	private RadioButton type_hour;
	private RadioButton type_day;
	private RadioButton type_weeks;
	private EditText name_edit;
	private EditText height_edit;
	private EditText phone_edit;
	private EditText editSms;
	private EditText location_text;
	private Button confirm;
	private Button starttime;
	private Button endtime;
	private TextView time_text;

	private MyImageSpinner province_spinner;
	private MyImageSpinner city_spinner;
	private MyImageSpinner area_spinner;
	private PartTimeDB partTimeDB;
	private ArrayList<String> city_list = null;

	int year, month, day, hour, min;

	LayoutInflater inflater;

	private WheelMain wheelMain;
	private String province_text;
	private String city_text;

	private String city = "";
	private String starttimetext = "";
	private String endtimetext = "";
	private HashMap<String, String> data_map = null;
	private String lat_lng;
	private String area_text;
	private String desired_text;
	private String location;

	/* 等待框部分 */
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.job_create_resume1);
		context = this;
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		create_flag = getIntent().getStringExtra("flag");
		partTimeDB = PartTimeDB.getInstance(context);

		Calendar calendar = Calendar.getInstance();

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		min = calendar.get(Calendar.MINUTE);

		inflater = LayoutInflater.from(Job_Create_Resume.this);

		initView();
		if (create_flag.equals("create")) // 新建简历的标志
		{
			Utils.CommitPageFlagToShared(context, Constant.FINISH_ACTIVITY_FLAG_JOB);
			titleText.setText(getResources().getString(R.string.new_resume));
			confirm.setVisibility(View.VISIBLE);
		} else if (create_flag.equals("look")) // 查看简历的标志
		{
			Utils.CommitPageFlagToShared(context, Constant.FINISH_ACTIVITY_FLAG_JOB);
			titleText.setText(getResources().getString(R.string.resume_detatils));
			confirm.setVisibility(View.GONE);

		} else if (create_flag.equals("home")) {
			titleText.setText(getResources().getString(R.string.resume_detatils));
			confirm.setVisibility(View.GONE);
			Utils.CommitPageFlagToShared(context, Constant.FINISH_ACTIVITY_FLAG_HOMEPAGE);
		}

	}

	public void ShowWaiting(String text) {
		WaitingText.setText(text);
		WaitingDlg.setVisibility(View.VISIBLE);
	}

	public void HideWaiting() {
		WaitingDlg.setVisibility(View.GONE);
	}

	private void initView() {
		titleText = (TextView) findViewById(R.id.title_text);
		home_back = (ImageView) findViewById(R.id.home_back);

		/* 等待对话框部分 */
		WaitingText = (TextView) findViewById(R.id.Waiting_text);
		WaitingDlg = (RelativeLayout) findViewById(R.id.Waiting_dlg);

		ArrayList<String> desired_list = new ArrayList<String>();
		// 学生兼职 模特 礼仪小姐 摄影师 小时工/钟点工 促销员 传单派发 问卷调查 手工制作 网站建设 设计制作 家教 会计 翻译 实习生
		// 酒吧KTV 服务员 销售 其他兼职
		desired_list.add("学生兼职");
		desired_list.add("模特");
		desired_list.add("礼仪小姐");
		desired_list.add("摄影师");
		desired_list.add("小时工/钟点工");
		desired_list.add("促销员");
		desired_list.add("传单派发");
		desired_list.add("问卷调查");
		desired_list.add("手工制作");
		desired_list.add("网站建设");
		desired_list.add("设计制作");
		desired_list.add("家教");
		desired_list.add("会计");
		desired_list.add("翻译");
		desired_list.add("实习生");
		desired_list.add("酒吧KTV");
		desired_list.add("服务员");
		desired_list.add("销售");
		desired_list.add("其他兼职");

		desired_spinner = (MyImageSpinner) findViewById(R.id.desired_spinner);
		setDatatoSpinner(desired_list, desired_spinner);
		// desired_job_location_text = (EditText)
		// findViewById(R.id.desired_job_location_text);
		sex_radiogroup = (RadioGroup) findViewById(R.id.sex_radiogroup);
		sex_male = (RadioButton) findViewById(R.id.sex_male);
		sex_female = (RadioButton) findViewById(R.id.sex_female);
		type_radiogroup = (RadioGroup) findViewById(R.id.type_radiogroup);
		type_hour = (RadioButton) findViewById(R.id.type_hour);
		type_day = (RadioButton) findViewById(R.id.type_day);
		type_weeks = (RadioButton) findViewById(R.id.type_weeks);
		name_edit = (EditText) findViewById(R.id.name_edit);
		height_edit = (EditText) findViewById(R.id.height_edit);
		phone_edit = (EditText) findViewById(R.id.phone_edit);
		location_text = (EditText) findViewById(R.id.location_text);
		editSms = (EditText) findViewById(R.id.editSms);
		confirm = (Button) findViewById(R.id.confirm);
		starttime = (Button) findViewById(R.id.starttime_btn);
		endtime = (Button) findViewById(R.id.endtime_btn);
		time_text = (TextView) findViewById(R.id.time_text);
		province_spinner = (MyImageSpinner) findViewById(R.id.province_spinner);
		city_spinner = (MyImageSpinner) findViewById(R.id.city_spinner);
		area_spinner = (MyImageSpinner) findViewById(R.id.area_spinner);
		city_list = new ArrayList<String>();
		city_list = partTimeDB.getCity(1, "");
		// city_list = partTimeDB.getCity(2, "河北");
		setDatatoSpinner(city_list, province_spinner);
		city_list = partTimeDB.getCity(1, "");
		ArrayList<String> area_list = new ArrayList<String>();
		area_list.add("请选择");
		setDatatoSpinner(area_list, city_spinner);
		setDatatoSpinner(area_list, area_spinner);

		province_spinner.setOnItemSelectedListener(new MyOnItemSelectedListener(PROVINCE_SPINNER_INT));
		city_spinner.setOnItemSelectedListener(new MyOnItemSelectedListener(CITY_SPINNER_INT));
		area_spinner.setOnItemSelectedListener(new MyOnItemSelectedListener(AREA_SPINNER_INT));
		desired_spinner.setOnItemSelectedListener(new MyOnItemSelectedListener(DESIRED_SPINNER_INT));

		home_back.setOnClickListener(new MyClickListener(BACK_INT));
		confirm.setOnClickListener(new MyClickListener(CONFIRM_INT));
		starttime.setOnClickListener(new MyClickListener(START_TIME_BTN_INT));
		endtime.setOnClickListener(new MyClickListener(END_TIME_BTN_INT));

		sex_radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkId) {
				if (checkId == sex_male.getId()) {
					sex_flag = "男";
				} else {
					sex_flag = "女";
				}
			}
		});
		type_radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkId) {
				if (checkId == type_hour.getId()) {
					type_flag = "时";
				} else if (checkId == type_day.getId()) {
					type_flag = "天";
				} else if (checkId == type_weeks.getId()) {
					type_flag = "周";
				}

			}
		});

	}

	/**
	 * 为自定义spinner添加数据
	 * 
	 * @param data
	 * @param spinner
	 */
	private void setDatatoSpinner(ArrayList<String> data, MyImageSpinner spinner) {
		if (data != null && data.size() > 0) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.context, android.R.layout.simple_list_item_1, data);
			spinner.setAdapter(adapter);
		}
	}

	/**
	 * 监听spinner中的item 选项
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyOnItemSelectedListener implements OnItemSelectedListener {
		private int index;

		public MyOnItemSelectedListener(int index) {
			this.index = index;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			switch (index) {
			case PROVINCE_SPINNER_INT:

				province_text = parent.getItemAtPosition(pos).toString();
				if (!province_text.equals("请选择")) {
					city_list = partTimeDB.getCity(2, province_text);
					setDatatoSpinner(city_list, city_spinner);
				}

			case CITY_SPINNER_INT:
				city_text = parent.getItemAtPosition(pos).toString();
				if (!city_text.equals("请选择")) {
					city_list = partTimeDB.getCity(3, city_text);
					setDatatoSpinner(city_list, area_spinner);
				}

				break;
			case AREA_SPINNER_INT:
				area_text = parent.getItemAtPosition(pos).toString();

				city = province_text + "-" + city_text + "-" + area_text;
				break;
			case DESIRED_SPINNER_INT:
				desired_text = parent.getItemAtPosition(pos).toString();
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Job_Create_Resume.this.finish();
	}

	public class MyClickListener implements View.OnClickListener {

		int index;

		public MyClickListener(int index) {
			this.index = index;
		}

		@Override
		public void onClick(View v) {
			switch (index) {
			case BACK_INT:
				Job_Create_Resume.this.finish();
				break;
			case CONFIRM_INT:
				// 向服务器提交一份新简历，并返回
				data_map = new HashMap<String, String>();
				data_map.put("rjob", desired_text);
				data_map.put("radd", city);
				data_map.put("rstart_time", "2014-12-12 09:00:00");
				data_map.put("rend_time", "2014-12-12 20:00:00");
				data_map.put("rtype", type_flag);
				data_map.put("rname", name_edit.getText().toString());
				data_map.put("rsex", sex_flag);
				data_map.put("rheight", height_edit.getText().toString());
				data_map.put("rcall", phone_edit.getText().toString());
				data_map.put("rexperience", editSms.getText().toString());
				new NewResume_Task().execute(data_map);

				String[] addrs = Utils.getLocation(city);
				if (addrs != null && addrs.length == 2)
					if (province_text == null || area_text == null || province_text.equals("") || area_text.equals("") || province_text.equals("请选择") || area_text.equals("请选择") || location_text.getText().equals("")) {
						Utils.ShowToast(context, "期望地点不能为空");
					} else {
						String loa_text = location_text.getText().toString();
						location = province_text + "-" + city_text + "-" + area_text + " " + location_text.getText().toString();
						if (loa_text.contains("省") || loa_text.contains("市")) {
							loa_text = city_text + area_text + loa_text;
							getLocation(province_text, loa_text);
							onBackPressed();
						} else {
							Utils.ShowToast(context, "请输入正确的详细地址,谢谢");
						}
					}
				break;
			case START_TIME_BTN_INT:
				data_select(1);
				break;
			case END_TIME_BTN_INT:
				data_select(2);
				// time_text.setText(starttimetext+"-"+endtimetext);
				break;
			}
		}

	}

	private void data_select(final int flag) {
		final View timepickerview = inflater.inflate(R.layout.timepicker, null);
		ScreenInfo screenInfo = new ScreenInfo(Job_Create_Resume.this);

		wheelMain = new WheelMain(timepickerview, 0);
		wheelMain.screenheight = screenInfo.getHeight();
		wheelMain.initDateTimePicker(year, month, day, hour, min);

		new AlertDialog.Builder(Job_Create_Resume.this).setTitle("选择时间").setView(timepickerview).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// flag=1为开始时间,flag=2为结束时间
				String time = "";
				if (flag == 1) {
					starttimetext = wheelMain.getTime();
					time = starttimetext;
				} else if (flag == 2) {
					endtimetext = wheelMain.getTime();
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						Date d1 = df.parse(starttimetext);
						Date d2 = df.parse(endtimetext);
						long diff = d2.getTime() - d1.getTime();

						if (diff <= 0) {
							Utils.ShowToast(context, "您选择的结束时间必须大于开始时间");
							time = starttimetext;
						} else {
							time = starttimetext + "-" + endtimetext;

						}
					} catch (Exception e) {
					}
				}
				time_text.setText(time);
			}
		}).setNegativeButton("取消", null).show();
	}

	/**
	 * 新建求职简历的异步
	 * 
	 * @author huxixi
	 * 
	 */
	public class NewResume_Task extends AsyncTask<HashMap<String, String>, Void, Boolean> {

		private int errcode;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在提交求职信息...");
		}

		@Override
		protected Boolean doInBackground(HashMap<String, String>... params) {

			int iresult = SvrOperation.New_Resume(context, params[0]);
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
				Utils.ShowToast(context, errmsg);
				return;
			}
			Utils.ShowToast(context, "简历新建成功");
			finish();
			// data_list = parttimeDb.getHotWorkInfo(mContext);
			// setPersonalListViewListener(data_list);
			return;
		}

	}

	private void getLocation(String city, String addr) {
		GeoCoder mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		mSearch.geocode(new GeoCodeOption().city(city).address(addr));

	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result != null) {
			try {
				double lat = result.getLocation().latitude;
				double lgn = result.getLocation().longitude;
				lat_lng = lat + "," + lgn;
			} catch (Exception e) {
				lat_lng = BaiduMap_GetLatLng_Utils.mLatLng + "," + BaiduMap_GetLatLng_Utils.mLocal_LatLng;
			}
			// 向服务器提交一份新简历，并返回
			data_map = new HashMap<String, String>();
			data_map.put("rjob", desired_text);
			data_map.put("radd", location);
			data_map.put("rstart_time", starttimetext);
			data_map.put("rend_time", endtimetext);
			data_map.put("rtype", type_flag);
			data_map.put("rname", name_edit.getText().toString());
			data_map.put("rsex", sex_flag);
			data_map.put("position", lat_lng);
			data_map.put("rheight", height_edit.getText().toString());
			data_map.put("rcall", phone_edit.getText().toString());
			data_map.put("rexperience", editSms.getText().toString());
			new NewResume_Task().execute(data_map);

		}

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		// TODO Auto-generated method stub

	}

}
