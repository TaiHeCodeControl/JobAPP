package com.parttime.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parttime.adapter.Job_Resume_adapter;
import com.parttime.adapter.Job_Resume_adapter.CheckboxState;
import com.parttime.constant.Constant;
import com.parttime.data.PartTimeDB;
import com.parttime.msg.ErrorMsgSvr;
import com.parttime.parttimejob.R;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;

/**
 * 求职发布的所有简历的界面
 * 
 * @author huxixi
 * 
 */
public class Job_Relase_Resume extends Activity {

	private Context context = null;
	/**
	 * 创建新简历的int值
	 */
	public static final int CREATE_RESUME_INT = 1;
	/**
	 * 选择某份简历的int值
	 */
	public static final int JOB_CONFIRM_INT = 2;

	private TextView titleText;
	private ImageView home_back;
	private ListView job_all_resume_listview = null;
	private Button new_create_resume;
	private Button job_confirm;

	private Intent intent = null;

	private Job_Resume_adapter job_resume_adapter = null;
	private ArrayList<HashMap<String, Object>> data_list = null;
	private PartTimeDB parttimeDb;
	private ArrayList<HashMap<String, String>> resume_list = null;
	private HashMap<String, String> hire_map = null;
	private String r_id = null;
	private String job_id = null;
	private String job_name = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.job_release_resume1);
		context = this;
		hire_map = new HashMap<String, String>();
		job_id = getIntent().getStringExtra("job_id");
		job_name = getIntent().getStringExtra("job_name");
		hire_map.put("job_id", job_id);
		hire_map.put("hire_name", job_name);
		parttimeDb = PartTimeDB.getInstance(context);
		parttimeDb.ClearAllResumeInfo();
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new AllResume_Task().execute();

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		new AllResume_Task().execute();

		if (job_resume_adapter == null) {
			job_resume_adapter.notifyDataSetChanged();
		}
	}

	private void initView() {
		titleText = (TextView) findViewById(R.id.title_text);
		home_back = (ImageView) findViewById(R.id.home_back);
		home_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		titleText.setText(getResources().getString(R.string.job_all_resume));

		job_all_resume_listview = (ListView) findViewById(R.id.job_all_resume_listview);
		new_create_resume = (Button) findViewById(R.id.new_create_resume);
		job_confirm = (Button) findViewById(R.id.job_confirm);

		// job_confirm.setEnabled(false);
		new_create_resume.setOnClickListener(new MyClickListener(CREATE_RESUME_INT));
		job_confirm.setOnClickListener(new MyClickListener(JOB_CONFIRM_INT));

	}

	/**
	 * 对listview进行的操作
	 * 
	 * @param listview
	 */
	private void deal_listview(ListView listview, ArrayList<HashMap<String, String>> list) {
		if (list != null && list.size() > 0) {
			if (job_resume_adapter == null) {
				job_resume_adapter = new Job_Resume_adapter(context, list);
				listview.setAdapter(job_resume_adapter);
			} else {
				job_resume_adapter.refresh(list);
			}
		}

		// listview.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// intent = new Intent(context, Job_Create_Resume.class);
		// intent.putExtra("flag", "look");
		// context.startActivity(intent);
		//
		// }
		// });

		job_resume_adapter.setoncheckboxstateListener(new CheckboxState() {

			@Override
			public void getcheckbox(boolean click) {
				if (click) {
					Log.i("hu", "getcheckbox");
					List<HashMap<String, String>> r_list = null;
					r_list = job_resume_adapter.getSelectItems();
					for (int i = 0; i < r_list.size(); i++) {
						if (!r_list.get(i).get("rid").equals("")) {
							r_id = r_list.get(i).get("rid");
							break;
						}
					}
					hire_map.put("r_id", r_id);
					// int select_pic_num = job_resume_adapter.getSelectItems();
					// Utils.ShowToast(context, "选择了简历"+select_pic_num);
					job_confirm.setEnabled(true);
				} else {
					Utils.ShowToast(context, "选择简历之后，才可以申请职位");
				}
			}

		});

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Utils.CommitPageFlagToShared(this, Constant.FINISH_ACTIVITY_FLAG_JOB);
	}

	public class MyClickListener implements View.OnClickListener {

		int index;

		public MyClickListener(int index) {
			this.index = index;
		}

		@Override
		public void onClick(View v) {
			switch (index) {
			case CREATE_RESUME_INT:
				intent = new Intent(context, Job_Create_Resume.class);
				intent.putExtra("flag", "create");
				context.startActivity(intent);
				break;
			case JOB_CONFIRM_INT:
				// Intent intent = new Intent();
				// intent.setClass(context,
				// Personal_Task_Portrait_Activity.class);
				//
				//
				// startActivity(intent);
				//
				// finish();
				if (r_id == null) {
					Utils.ShowToast(context, "选择简历之后，才可以申请职位");
				} else {

					if (job_id != null && !job_id.equals(""))
						new Add_Hire_Task().execute();

					else {
						new Public_Resume_Task().execute();
					}
				}
				break;
			}
		}

	}

	/**
	 * 所有求职简历的接口
	 * 
	 * @author huxixi
	 * 
	 */
	public class AllResume_Task extends AsyncTask<Void, Void, Boolean> {

		private int errcode;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			int iresult = SvrOperation.AllResume(context);
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
				// Utils.ShowToast(context, errmsg);
				return;
			}
			resume_list = parttimeDb.getAllResumeList(context);
			deal_listview(job_all_resume_listview, resume_list);

			return;
		}

	}

	/**
	 * 新建雇佣关系的异步
	 */
	public class Add_Hire_Task extends AsyncTask<Void, Void, Boolean> {

		private int errcode;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			int iresult = SvrOperation.AddHire(context, hire_map);
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
			Utils.ShowToast(context, "求职已发出");
			Job_Relase_Resume.this.finish();
			return;
		}

	}

	/**
	 * 发布求职信息的异步
	 */
	public class Public_Resume_Task extends AsyncTask<Void, Void, Boolean> {

		private int errcode;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			int iresult = SvrOperation.PublicResume(context, r_id);
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
			Utils.ShowToast(context, "求职已发出");
			Job_Relase_Resume.this.finish();
			return;
		}

	}

}
