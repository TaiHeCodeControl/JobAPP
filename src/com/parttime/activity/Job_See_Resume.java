package com.parttime.activity;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parttime.constant.Constant;
import com.parttime.data.PartTimeDB;
import com.parttime.msg.ErrorMsgSvr;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.Communication_UserStruct;
import com.parttime.struct.PartTimeStruct.PersonalDetailsStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Utils;

/**
 * 查看简历的activity
 * 
 * @author huxixi
 */
public class Job_See_Resume extends Activity {

	public static final int BACK_INT = 1;
	public static final int CONTENT_LINK_SEESION_INT = 2;
	public static final int CONTENT_JOB_APPLY = 3;
	public static final int CONTENT_JOB_COMMIT = 4;

	private TextView titleText;
	private ImageView home_back;
	private Context context = null;
	private String flag = null;

	private Button content_link_session;
	// private Button content_job_apply;
	// private Button content_job_commit;
	private Intent intent = null;
	private String in_flag = "home";
	private String userid = "uerid";
	private String rid = "";
	private SharedPreferences sharedPreferences;
	private PartTimeDB parttimeDb;
	private HashMap<String, String> resume_map = null;
	private PersonalDetailsStruct mPersonalStruct;
	private long user_company_id = 0;

	private TextView desired_text;
	private TextView desired_job_text;
	private TextView time_text;
	private TextView type_radiogroup;
	private TextView name_edit;

	private TextView sex_text;
	private TextView height_edit;
	private TextView phone_edit;
	private TextView editSms;
	private Button content_job_commit;

	private String personal_id = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.job_see_resume);
		context = this;
		parttimeDb = PartTimeDB.getInstance(context);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		in_flag = getIntent().getStringExtra("in_flag");
		userid = getIntent().getStringExtra("userid");
		flag = getIntent().getStringExtra("flag");
		rid = getIntent().getStringExtra("rid");
		if (flag != null && flag.equals("seeker")) {
			Utils.CommitPageFlagToShared(context, Constant.FINISH_ACTIVITY_FLAG_JOB);
		} else if (flag != null && flag.equals("home")) {
			Utils.CommitPageFlagToShared(context, Constant.FINISH_ACTIVITY_FLAG_HOMEPAGE);

		}

		initView();

		new Resume_Info_Task().execute(rid);
	}

	private void initView() {
		titleText = (TextView) findViewById(R.id.title_text);
		home_back = (ImageView) findViewById(R.id.home_back);
		titleText.setText(getResources().getString(R.string.resume_detatils));
		home_back.setOnClickListener(new MyClickListener(BACK_INT));

		content_link_session = (Button) findViewById(R.id.content_link_session);
		// content_job_apply=(Button)findViewById(R.id.content_job_apply);
		content_job_commit = (Button) findViewById(R.id.content_job_commit);

		desired_text = (TextView) findViewById(R.id.desired_text);
		desired_job_text = (TextView) findViewById(R.id.desired_job_text);
		time_text = (TextView) findViewById(R.id.time_text);
		type_radiogroup = (TextView) findViewById(R.id.type_radiogroup);
		name_edit = (TextView) findViewById(R.id.name_edit);
		sex_text = (TextView) findViewById(R.id.sex_select);
		height_edit = (TextView) findViewById(R.id.height_edit);
		phone_edit = (TextView) findViewById(R.id.phone_edit);
		editSms = (TextView) findViewById(R.id.editSms);

		String userflag = sharedPreferences.getString("userflag", "personal");
		if (userflag.equals("personal")) {
			content_link_session.setVisibility(View.GONE);
			// content_job_apply.setVisibility(View.GONE);
			content_job_commit.setVisibility(View.GONE);
		} else if (userflag.equals("company")) {
			content_link_session.setVisibility(View.VISIBLE);
			// content_job_apply.setVisibility(View.VISIBLE);
			content_job_commit.setVisibility(View.VISIBLE);

		}
		content_link_session.setOnClickListener(new MyClickListener(CONTENT_LINK_SEESION_INT));
		// content_job_apply.setOnClickListener(new
		// MyClickListener(CONTENT_JOB_APPLY));
		content_job_commit.setOnClickListener(new MyClickListener(CONTENT_JOB_COMMIT));
	}

	private void setDatatoText(HashMap<String, String> map) {

		String rid = map.get("rid");
		String rjob = map.get("rjob");
		String rstarttime = map.get("rstarttime");
		String rendtime = map.get("rendtime");
		String radd = map.get("radd");
		String rtype = map.get("rtype");
		String rname = map.get("rname");
		String rsex = map.get("rsex");
		String rheight = map.get("rheight");
		String experence = map.get("rexperence");
		String rcall = map.get("rcall");

		desired_text.setText(rjob);
		time_text.setText(rstarttime + "--" + rendtime);
		desired_job_text.setText(radd);
		type_radiogroup.setText(getType(rtype));
		name_edit.setText(rname);
		height_edit.setText(rheight);
		phone_edit.setText(rcall);
		editSms.setText(experence);
		sex_text.setText(getSex(rsex));
	}

	public String getType(String msg) {
		String type = "时";
		if (msg.endsWith("0")) {
			type = "时";
		} else if (msg.equals("1")) {
			type = "天";
		} else if (msg.equals("2")) {
			type = "周";
		}
		return type;
	}

	public String getSex(String msg) {
		String sex = "男";
		if (msg.equals("0")) {
			sex = "男";
		} else if (msg.equals("1")) {
			sex = "女";
		}
		return sex;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
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

				Job_See_Resume.this.finish();
				break;
			case CONTENT_LINK_SEESION_INT:
				Long user = Long.parseLong(userid);
				new GetPersonalInfoAsynTask().execute(user);

				break;
			case CONTENT_JOB_APPLY:
				intent = new Intent(context, Job_Relase_Resume.class);
				context.startActivity(intent);
				break;
			case CONTENT_JOB_COMMIT:
				intent = new Intent(Job_See_Resume.this, Comment_Activity_Company.class);
				intent.putExtra("business_id", personal_id);
				context.startActivity(intent);
				break;

			}
		}

	}

	/**
	 * 求职简历详情的接口
	 * 
	 * @author huxixi
	 * 
	 */
	public class Resume_Info_Task extends AsyncTask<String, Void, Boolean> {

		private int errcode;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {

			int iresult = SvrOperation.Resume_Info(context, params[0]);
			if (iresult != SvrInfo.SVR_RESULT_SUCCESS) {
				errcode = iresult;
				return false;
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result == false) {
				int errmsg = ErrorMsgSvr.ErrorMsg(errcode);
				Utils.ShowToast(context, errmsg);
				return;
			}
			resume_map = parttimeDb.getResumeInfo(context, rid);
			// deal_listview(job_all_resume_listview,resume_list);
			personal_id = resume_map.get("ruserid");
			setDatatoText(resume_map);
			return;
		}

	}

	/**
	 * 获取个人信息
	 */
	public class GetPersonalInfoAsynTask extends AsyncTask<Long, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Long... params) {
			try {
				mPersonalStruct = SvrOperation.GetPersonalInfo1(context, params[0]);

				if (mPersonalStruct == null)
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
				Utils.ShowToast(context, "请完善个人信息，否则不能进行会话");
				return;
			}
			if (mPersonalStruct != null) {
				String headimage = mPersonalStruct.mPersonalHeadImage;
				String username = mPersonalStruct.mPersonalName;
				Communication_UserStruct comm_struct = new Communication_UserStruct();
				comm_struct.comm_head_image = headimage;

				// www.xunlvshi.cn/project/jianzhi365/Uploads/images/avatar/头像图片名称
				comm_struct.comm_userid = username;
				user_company_id = sharedPreferences.getLong("userid", 0);
				comm_struct.userid = String.valueOf(user_company_id);
				if (parttimeDb.IsExistCommunication(String.valueOf(user_company_id), username, context)) {
					parttimeDb.UpdateCommunicationInfo(context, comm_struct, String.valueOf(user_company_id), username);
				} else {
					parttimeDb.AddCommunicationInfo(context, comm_struct);
				}
				intent = new Intent(Job_See_Resume.this, ChatActivity.class);
				intent.putExtra("userid", username);
				intent.putExtra("flag", in_flag);

				context.startActivity(intent);
			}
		}
	}

}
