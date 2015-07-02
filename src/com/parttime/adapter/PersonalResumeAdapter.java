package com.parttime.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parttime.activity.Personal_Resume_Details_Acitivity;
import com.parttime.activity.Personal_Resume_Manage_Activity;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Utils;

/**
 * 
 * @author 灰色的寂寞
 * @function 个人中心简历管理adapter
 * @date 2015-1-19
 * @time 17:54
 *
 */
public class PersonalResumeAdapter extends BaseAdapter implements OnClickListener{
	public static final String TAG = PersonalResumeAdapter.class.getSimpleName();
	private Context mContext;
	private PartTimeDB mPartTimeDB = null;
	private ArrayList<HashMap<String,String>> mResumeList=null;
	private Handler mHandler =null;
	
	/*布局部分*/
	private LayoutInflater layoutInflater = null;
	private ResumeListItem resumeItemView=null;
	
	public PersonalResumeAdapter(Context context,Handler mhandler){
		mContext = context;
		layoutInflater = LayoutInflater.from(context);
		mPartTimeDB = PartTimeDB.getInstance(mContext);
		mHandler = mhandler;
		mResumeList = mPartTimeDB.getAllResumeList(mContext);
	}

	
	@Override
	public int getCount() {
			return mResumeList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup group) {
		if (convertView == null) {
			resumeItemView = new ResumeListItem();
				convertView = layoutInflater.inflate(R.layout.personal_resume_item_data,null);
				resumeItemView.mResumeItem = (LinearLayout) convertView.findViewById(R.id.personal_resume_item_block);
				resumeItemView.mDelBtn = (ImageView) convertView.findViewById(R.id.del_iconbtn);
				resumeItemView.mJobName = (TextView)convertView.findViewById(R.id.mjob_name);
				resumeItemView.mTime= (TextView)convertView.findViewById(R.id.mresume_time);
				
				
				resumeItemView.mResumeItem.setOnClickListener(this);
				resumeItemView.mDelBtn.setOnClickListener(this);
				
			convertView.setTag(resumeItemView);
		}else {
			resumeItemView = (ResumeListItem) convertView.getTag();
		}
		
		 HashMap<String,String> tasks_item =mResumeList.get(position);
		
			resumeItemView.mJobName.setText("期望职位："+tasks_item.get("rjob").toString());
			resumeItemView.mTime.setText(tasks_item.get("rstarttime").toString());
			
			resumeItemView.mResumeItem.setTag(tasks_item.get("rid"));
			resumeItemView.mDelBtn.setTag(tasks_item.get("rid"));
		return convertView;
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.personal_resume_item_block:
			String rid1 = (String)view.getTag();
			Intent intent = new Intent();
			intent.setClass(mContext, Personal_Resume_Details_Acitivity.class);
			intent.putExtra("rid", rid1);
			mContext.startActivity(intent);
			
			break;
		case R.id.del_iconbtn:
			String rid = (String)view.getTag();
			mPartTimeDB.removeOneResume(Integer.parseInt(rid));
			new DelInfoAsynTask().execute(Integer.parseInt(rid));
			break;
		}
	}
	
	/**
	 * 提交个人信心 
	 */
	public class DelInfoAsynTask extends AsyncTask<Integer, Void, Boolean>{
		long errcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Personal_Resume_Manage_Activity.ShowWaiting("正在删除...");
		}
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				errcode = SvrOperation.DelResume(mContext,params[0]);
				if (errcode !=SvrInfo.SVR_RESULT_SUCCESS)
					return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			Personal_Resume_Manage_Activity.HideWaiting();
			if(result== false){
				Utils.ShowToast(mContext, "删除失败！请检查网络");
				return ;
			}
			mResumeList = mPartTimeDB.getAllResumeList(mContext);
			notifyDataSetChanged();
			mHandler.sendEmptyMessage(1000);
		}
	}
	
	/**
	 * fucntion:简历的期望职业，兼职时间
	 */
	public static class ResumeListItem{
		private LinearLayout mResumeItem;
		private TextView mJobName,mTime;
		private ImageView mDelBtn;
	}


	
}