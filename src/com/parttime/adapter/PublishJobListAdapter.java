package com.parttime.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parttime.activity.Company_Employment_Activity;
import com.parttime.activity.Company_Job_Managed_Activity;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.CompanyPJobListStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;

/**
 * 
 * @author 灰色的寂寞
 * @function 企业发布职位列表的适配
 */
public class PublishJobListAdapter extends BaseAdapter implements OnClickListener {
	public static final String TAG = PublishJobListAdapter.class.getSimpleName();
	private Context mContext;
	ArrayList<CompanyPJobListStruct> mjoblist = new ArrayList<CompanyPJobListStruct>();
	long muserid = -1;

	long errorcode = -1;
	VolunteerListItem mVolunteerListItem = null;
	private LayoutInflater layoutInflater = null;

	private DisplayImageOptions options;
	private ImageLoader mImageLoader;
	private int turntype;
	/** 用工管理 */
	private static final int MENU_PERSONAL_MANAGEWORKER = 1;
	/** 职位管理 */
	private static final int MENU_PERSONAL_MANAGEJOB = 2;
	PartTimeDB DB;
	Handler mhandler = new Handler();

	public PublishJobListAdapter(Context context, ArrayList<CompanyPJobListStruct> alist, int type, Handler handler) {
		mContext = context;
		layoutInflater = LayoutInflater.from(context);
		mjoblist = alist;
		turntype = type;
		mhandler = handler;
		DB = PartTimeDB.getInstance(mContext);
		initDisplayOptions();

	}

	@Override
	public int getCount() {
		return mjoblist.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			mVolunteerListItem = new VolunteerListItem();
			convertView = layoutInflater.inflate(R.layout.publish_job_list_item, null);
			mVolunteerListItem.PjobBlock = (RelativeLayout) convertView.findViewById(R.id.pjob_item_block);
			mVolunteerListItem.PjobDeleteBlock = (RelativeLayout) convertView.findViewById(R.id.delete_job_block);
			mVolunteerListItem.PjobName = (TextView) convertView.findViewById(R.id.pjob_name);
			mVolunteerListItem.PjobType = (TextView) convertView.findViewById(R.id.pjob_type);
			mVolunteerListItem.PjobCharge = (TextView) convertView.findViewById(R.id.pjob_charge);
			mVolunteerListItem.PjobNum = (TextView) convertView.findViewById(R.id.pjob_num);
			mVolunteerListItem.PjobCreateTime = (TextView) convertView.findViewById(R.id.pjob_createtime);
			mVolunteerListItem.PjobDeleteBtn = (TextView) convertView.findViewById(R.id.publish_job_delete_btn);

			mVolunteerListItem.PjobBlock.setOnClickListener(PublishJobListAdapter.this);
			mVolunteerListItem.PjobDeleteBtn.setOnClickListener(PublishJobListAdapter.this);

			if (turntype == MENU_PERSONAL_MANAGEJOB) {
				mVolunteerListItem.PjobDeleteBlock.setVisibility(View.VISIBLE);
			} else {
				mVolunteerListItem.PjobDeleteBlock.setVisibility(View.GONE);
			}
			convertView.setTag(mVolunteerListItem);
		} else {
			mVolunteerListItem = (VolunteerListItem) convertView.getTag();
		}
		if (mjoblist != null) {
			CompanyPJobListStruct jobstruct = mjoblist.get(position);
			mVolunteerListItem.PjobName.setText("职位名称:" + jobstruct.mJobName);
			if (jobstruct.mJobType == null || jobstruct.mJobType.equals("null") || jobstruct.mJobType.equals("")) {
				mVolunteerListItem.PjobType.setTextColor(mContext.getResources().getColor(R.color.black));
				mVolunteerListItem.PjobType.setVisibility(View.GONE);
			} else if (jobstruct.mJobType.equals("时")) {
				mVolunteerListItem.PjobType.setBackground(mContext.getResources().getDrawable(R.drawable.job_item_time_bg));
				mVolunteerListItem.PjobType.setTextColor(mContext.getResources().getColor(R.color.red));
				mVolunteerListItem.PjobCharge.setText(jobstruct.mJobCharge + "元/时");
			} else if (jobstruct.mJobType.equals("天")) {
				mVolunteerListItem.PjobType.setBackground(mContext.getResources().getDrawable(R.drawable.job_item_time_green));
				mVolunteerListItem.PjobType.setTextColor(mContext.getResources().getColor(R.color.green));
				mVolunteerListItem.PjobCharge.setText(jobstruct.mJobCharge + "元/天");
			} else if (jobstruct.mJobType.equals("周")) {
				mVolunteerListItem.PjobType.setBackground(mContext.getResources().getDrawable(R.drawable.job_item_time_blue));
				mVolunteerListItem.PjobType.setTextColor(mContext.getResources().getColor(R.color.light_blue));
				mVolunteerListItem.PjobCharge.setText(jobstruct.mJobCharge + "元/周");
			}
			mVolunteerListItem.PjobType.setText(jobstruct.mJobType);
			mVolunteerListItem.PjobNum.setText("共" + jobstruct.mJobEmployNum + "人");
			mVolunteerListItem.PjobCreateTime.setText(jobstruct.mCreateTime);
			mVolunteerListItem.PjobBlock.setTag(jobstruct);
			mVolunteerListItem.PjobDeleteBtn.setTag(jobstruct.mJobId);
		}
		return convertView;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.pjob_item_block:
			if (mjoblist != null) {
				CompanyPJobListStruct jobstruct = (CompanyPJobListStruct) view.getTag();
				Intent intent = new Intent();
				if (turntype == MENU_PERSONAL_MANAGEJOB) {
					intent.setClass(mContext, Company_Job_Managed_Activity.class);
				} else {
					intent.setClass(mContext, Company_Employment_Activity.class);
				}
				intent.putExtra("job_name", jobstruct.mJobName);
				intent.putExtra("job_id", jobstruct.mJobId);
				mContext.startActivity(intent);
			}
			break;
		case R.id.publish_job_delete_btn:
			int jobid = (Integer) view.getTag();
			if (Submit.isNetworkAvailable(mContext)) {
				new DelPJobAsynTask().execute(jobid);
			} else {
				Utils.ShowToast(mContext, "没有可用网络！");
			}
			break;
		}
	}

	/**
	 * 删除发布职位
	 */
	public class DelPJobAsynTask extends AsyncTask<Integer, Void, Boolean> {

		long errcode = -1;
		int jobid = -1;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				jobid = params[0];
				errcode = SvrOperation.DelPJobTask(mContext, params[0]);

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
				if (errcode == -10) {
					Utils.ShowToast(mContext, "删除失败！");
				}
				return;
			}
			DB.DeletePJobInfo(mContext, jobid);
			mhandler.sendEmptyMessage(40000);
			Utils.ShowToast(mContext, "删除成功！");
		}
	}

	/**
	 * imageloader 加载图片展示，的一些参数的设置 avatar_user_default 为默认图片
	 */
	@SuppressWarnings("deprecation")
	private void initDisplayOptions() {
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.loading)
		// 设置正在加载图片
		// .showImageOnLoading(R.drawable.ic_stub) //1.8.7新增
				.showImageForEmptyUri(R.drawable.load_failed).showImageOnFail(R.drawable.loading_default_img)
				// 设置加载失败图片
				.cacheInMemory(true).cacheOnDisc(true)
				// .displayer(new RoundedBitmapDisplayer(20)) //
				// 设置图片角度,0为方形，360为圆角
				.build();

		mImageLoader = ImageLoader.getInstance();
	}

	/**
	 * 图片和标题
	 */
	public static class VolunteerListItem {
		private RelativeLayout PjobBlock, PjobDeleteBlock;
		private TextView PjobName, PjobType, PjobCharge, PjobNum, PjobCreateTime, PjobDeleteBtn;

	}

}
