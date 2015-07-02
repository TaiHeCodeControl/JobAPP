package com.parttime.activity;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.parttime.constant.Constant;
import com.parttime.data.PartTimeDB;
import com.parttime.msg.ErrorMsgSvr;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.Communication_UserStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Utils;

/**
 * @function: 首页中的职位详情介绍
 * @author huxixi
 * 
 */
public class Home_Job_Detail extends Activity {

	public static final int BACK_INT = 1;
	public static final int JOB_APPLY_INT = 2;
	public static final int LINK_SESSION_INT = 3;
	public static final int COMMENT_INT = 4;

	private Context context = null;
	/**
	 * 标题
	 */
	private ImageView back_image = null;
	private TextView job_title = null;
	private View job_title_view = null;
	private ImageView id_recyclerview = null;
	private Button content_job_apply = null;
	private Button content_link_session = null;
	private Button content_job_commit = null;

	private TextView detail_title = null;
	private TextView detail_time = null;
	private TextView start_time = null;
	private TextView end_time = null;
	private TextView content_money = null;
	private TextView content_people_number = null;
	private TextView content_parttime_job_detail = null;
	private TextView content_apply_condition_detail = null;
	private TextView content_parttime_job_require_detail = null;

	private String in_flag = null;
	private String userid = null;
	private String job_id = null;
	private String job_name = null;
	private Intent intent = null;
	private PartTimeDB parttimeDb;
	private HashMap<String, String> data_map = null;
	private HashMap<String, String> input_map = null;
	private HashMap<String, String> get_map = null;
	private String company_id = "";
	private long user_company_id = 0;
	private SharedPreferences sharedPreferences;
	// www.xunlvshi.cn/project/jianzhi365/Uploads/images/avatar/头像图片名称
	public static final String PIC_PATH = "http://www.xunlvshi.cn/project/jianzhi365/Uploads/images/job/";

	/* imageloader部分 */
	private DisplayImageOptions options;
	private ImageLoader mImageLoader;

	/* 等待框部分 */
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home_job_detail);
		context = this;
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		parttimeDb = PartTimeDB.getInstance(context);
		in_flag = getIntent().getStringExtra("in_flag");
		userid = getIntent().getStringExtra("userid");
		job_id = getIntent().getStringExtra("job_id");
		job_name = getIntent().getStringExtra("job_name");
		input_map = new HashMap<String, String>();
		input_map.put("job_id", job_id);
		input_map.put("job_name", job_name);
		Utils.detail_flag = false;
		initDisplayOptions();
		initView();
	}

	@Override
	protected void onStart() {
		super.onStart();
		new Job_Info_Task().execute(input_map);
	}

	public void ShowWaiting(String text) {
		WaitingText.setText(text);
		WaitingDlg.setVisibility(View.VISIBLE);
	}

	public void HideWaiting() {
		WaitingDlg.setVisibility(View.GONE);
	}

	private void initView() {

		job_title_view = (View) findViewById(R.id.job_title_view);
		back_image = (ImageView) job_title_view.findViewById(R.id.home_back);
		job_title = (TextView) job_title_view.findViewById(R.id.title_text);
		content_job_apply = (Button) findViewById(R.id.content_job_apply);
		content_link_session = (Button) findViewById(R.id.content_link_session);
		content_job_commit = (Button) findViewById(R.id.content_job_commit);

		id_recyclerview = (ImageView) findViewById(R.id.pic_image);

		/* 等待对话框部分 */
		WaitingText = (TextView) findViewById(R.id.Waiting_text);
		WaitingDlg = (RelativeLayout) findViewById(R.id.Waiting_dlg);

		detail_title = (TextView) findViewById(R.id.detail_title);
		detail_time = (TextView) findViewById(R.id.detail_time);
		start_time = (TextView) findViewById(R.id.start_time);
		end_time = (TextView) findViewById(R.id.end_time);
		content_money = (TextView) findViewById(R.id.content_money);
		content_people_number = (TextView) findViewById(R.id.content_people_number);
		content_parttime_job_detail = (TextView) findViewById(R.id.content_parttime_job_detail);
		content_apply_condition_detail = (TextView) findViewById(R.id.content_apply_condition_detail);
		content_parttime_job_require_detail = (TextView) findViewById(R.id.content_parttime_job_require_detail);

		// LinearLayoutManager linearLayoutManager = new
		// LinearLayoutManager(this);
		// linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		//
		// id_recyclerview.setLayoutManager(linearLayoutManager);
		job_title.setText(this.getResources().getString(R.string.home_job_detail));

		if (in_flag != null && in_flag.equals("manage")) {
			content_job_apply.setVisibility(View.GONE);
			content_link_session.setVisibility(View.GONE);
			content_job_commit.setVisibility(View.GONE);
		}

		back_image.setOnClickListener(new MyClickListener(BACK_INT));
		content_job_apply.setOnClickListener(new MyClickListener(JOB_APPLY_INT));
		content_link_session.setOnClickListener(new MyClickListener(LINK_SESSION_INT));
		content_job_commit.setOnClickListener(new MyClickListener(COMMENT_INT));
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (in_flag.equals("job")) {
			Utils.CommitPageFlagToShared(this, Constant.FINISH_ACTIVITY_FLAG_JOB);
		} else if (in_flag.equals("home")) {
			Utils.CommitPageFlagToShared(this, Constant.FINISH_ACTIVITY_FLAG_HOMEPAGE);
		} else if (in_flag.equals("manage")) {
			Utils.CommitPageFlagToShared(this, Constant.FINISH_ACTIVITY_FLAG_MANAGER);

		}
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
				if (in_flag != null && in_flag.equals("job")) {
					Utils.CommitPageFlagToShared(context, Constant.FINISH_ACTIVITY_FLAG_JOB);
				} else if (in_flag.equals("home")) {
					Utils.CommitPageFlagToShared(context, Constant.FINISH_ACTIVITY_FLAG_HOMEPAGE);
				}
				Home_Job_Detail.this.finish();
				break;
			case JOB_APPLY_INT:
				intent = new Intent(context, Job_Relase_Resume.class);
				intent.putExtra("job_id", job_id);
				intent.putExtra("job_name", job_name);
				context.startActivity(intent);
				break;
			case LINK_SESSION_INT:
				if (company_id == null || company_id.equals("null") || company_id.equals("") || company_id.equals(" "))
					company_id = "0";
				Long user = Long.parseLong(company_id);
				new GetUserNameTask().execute(user);
				break;
			case COMMENT_INT:
				intent = new Intent(Home_Job_Detail.this, Comment_Activity.class);
				intent.putExtra("business_id", company_id);
				context.startActivity(intent);
				break;

			}
		}

	}

	private void setData(HashMap<String, String> map) {

		String company_name = map.get("company_name");
		String name = map.get("name");
		String type = map.get("type");
		String num = map.get("num");
		String charge = map.get("charge");
		String create_time = map.get("create_time");
		String present = map.get("present");
		String require = map.get("require");
		String condition = map.get("condition");
		String thumb = map.get("thumb");
		String starttime = map.get("starttime");
		String endtime = map.get("endtime");
		String company_level = map.get("company_level");
		String company_add = map.get("company_add");
		detail_title.setText("职位：" + name);
		start_time.setText(String.format(context.getResources().getString(R.string.start_time12), starttime));
		end_time.setText(String.format(context.getResources().getString(R.string.end_time12), endtime));
		detail_time.setText(String.format(context.getResources().getString(R.string.release_time), create_time));
		content_money.setText(String.format(context.getResources().getString(R.string.charge), charge));
		content_people_number.setText(String.format(context.getResources().getString(R.string.hiring), num));
		content_parttime_job_detail.setText(present);
		content_parttime_job_require_detail.setText(require);
		content_apply_condition_detail.setText(condition);
		String pic_path = PIC_PATH + thumb;
		mImageLoader.displayImage(pic_path, id_recyclerview, options);
	}

	/**
	 * 职位详情的异步
	 * 
	 * 
	 * 
	 * @author huxixi
	 * 
	 */
	public class Job_Info_Task extends AsyncTask<HashMap<String, String>, Void, Boolean> {

		private int errcode;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在获取职位详情...");
		}

		@Override
		protected Boolean doInBackground(HashMap<String, String>... params) {

			int iresult = SvrOperation.Job_Info(context, params[0]);

			if (iresult != SvrInfo.SVR_RESULT_SUCCESS) {
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

			data_map = parttimeDb.getJobDetailInfo(context, job_id);
			company_id = data_map.get("company_id");
			setData(data_map);
			return;
		}

	}

	/**
	 * 通过企业id获取用户名的异步
	 */
	public class GetUserNameTask extends AsyncTask<Long, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Long... params) {
			try {
				get_map = SvrOperation.GetUserName(context, params[0]);

				if (get_map == null)
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
				Utils.ShowToast(context, "丰富企业信息才能进入聊天界面");
				return;
			}
			if (get_map != null) {
				// String headimage=mPersonalStruct.mPersonalHeadImage;
				// String username=mPersonalStruct.mPersonalName;
				String headimage = get_map.get("avatar");
				String username = get_map.get("username");
				Communication_UserStruct comm_struct = new Communication_UserStruct();
				comm_struct.comm_head_image = headimage;

				comm_struct.comm_userid = username;
				user_company_id = sharedPreferences.getLong("userid", 0);
				comm_struct.userid = String.valueOf(user_company_id);
				if (parttimeDb.IsExistCommunication(String.valueOf(user_company_id), username, context)) {
					parttimeDb.UpdateCommunicationInfo(context, comm_struct, String.valueOf(user_company_id), username);
				} else {
					parttimeDb.AddCommunicationInfo(context, comm_struct);
				}
				intent = new Intent(Home_Job_Detail.this, ChatActivity.class);
				intent.putExtra("userid", username);
				intent.putExtra("flag", in_flag);

				context.startActivity(intent);
			}
		}
	}

	/**
	 * imageloader 加载图片展示，的一些参数的设置 avatar_user_default 为默认图片
	 */
	@SuppressWarnings("deprecation")
	private void initDisplayOptions() {
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.default_image) // 设置正在加载图片
				// .showImageOnLoading(R.drawable.ic_stub) //1.8.7新增
				.showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image) // 设置加载失败图片
				.cacheInMemory(true).cacheOnDisc(true)
				// .displayer(new RoundedBitmapDisplayer(5))
				// //设置图片角度,0为方形，360为圆角
				.build();

		mImageLoader = ImageLoader.getInstance();
	}

}
