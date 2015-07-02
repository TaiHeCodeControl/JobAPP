package com.parttime.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parttime.activity.Home_Job_Detail;
import com.parttime.activity.Personal_Task_Portrait_Activity;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.HiredStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Utils;
/**
 * 
 * @author 灰色的寂寞
 * @function 个人管理界面的adapter
 *
 */
public class PersonalManageAdapter extends BaseAdapter implements OnClickListener{
	public static final String TAG = PersonalTaskAdapter.class.getSimpleName();
	private Context mContext;
	private PartTimeDB mPartTimeDB = null;
	private String SignDayNum = "";
	private ArrayList<HiredStruct> mtaskList = new ArrayList<HiredStruct>();

	private Handler mHandler = null;

	/* 布局部分 */
	private LayoutInflater layoutInflater = null;
	private SignListItem signitemview = null;

	/* 常量部分 */
	/* 任务状态 */
	private int TaskType = -1;
	private static final int CLICK_TYPE_APPLIED= 1;
	private static final int CLICK_TYPE_HIRED= 2;
	private static final int CLICK_TYPE_DOING= 3;
	private static final int CLICK_TYPE_FINISHED= 4;
	private SharedPreferences mshared;
	long muserid= -1;
	public PersonalManageAdapter(Context context,long userid, ArrayList<HiredStruct> hireList,Handler mhandler, int tasktype) {
		mContext = context;
		layoutInflater = LayoutInflater.from(context);
		TaskType = tasktype;
		mtaskList = hireList;
		muserid = userid;
		mPartTimeDB = PartTimeDB.getInstance(mContext);
		mshared = PreferenceManager.getDefaultSharedPreferences(mContext);
		mHandler = mhandler;
	}

	

	@Override
	public int getCount() {
		if (mtaskList == null)
			return 0;
		return mtaskList.size();
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
	public View getView(int position, View convertView, ViewGroup group) {
		if (convertView == null) {
			signitemview = new SignListItem();
			convertView = layoutInflater.inflate(R.layout.personal_task_item,null);

			/** 已申请、被雇佣、进行中、已完成 */
			signitemview.BeforeDoneBlock = (RelativeLayout) convertView.findViewById(R.id.job_before_done_block);
			signitemview.BeforeTile = (TextView) convertView.findViewById(R.id.job_before_done_title);
			signitemview.BeforeTime = (TextView) convertView.findViewById(R.id.job_before_done_time);
			signitemview.BeforeDoneBlock.setOnClickListener(PersonalManageAdapter.this);
			convertView.setTag(signitemview);
		} else {
			signitemview = (SignListItem) convertView.getTag();
		}
		HiredStruct tasks_item = mtaskList.get(position);

		/* 根据不同的任务状态加载不同的布局显示数据 */
		signitemview.BeforeDoneBlock.setVisibility(View.VISIBLE);
		PrecessApplayEmploy(tasks_item, TaskType);
		signitemview.BeforeDoneBlock.setTag(tasks_item);

		return convertView;
	}
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.job_before_done_block:
			HiredStruct struct = (HiredStruct)view.getTag();
			 
			if(struct.engage.equals("已申请")){
				Intent intent = new Intent();
				intent.setClass(mContext, Home_Job_Detail.class);
				intent.putExtra("job_id", struct.job_id+"");
				intent.putExtra("in_flag", "manage");
				intent.putExtra("userid", muserid+"");
				intent.putExtra("job_name", struct.hire_name);
				mContext.startActivity(intent);
			}else if(struct.engage.equals("被雇佣")){
				SetHiredState(struct.hire_name,struct.id,"进行中");
			}else if(struct.engage.equals("进行中")){
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
				sharedPreferences.edit().putInt("pjengage_type", CLICK_TYPE_DOING).commit();
				Intent intent = new Intent();
				intent.setClass(mContext, Personal_Task_Portrait_Activity.class);
				intent.putExtra("hire_id", struct.id);
				intent.putExtra("hire_name", struct.hire_name);
				mContext.startActivity(intent);
			}else if(struct.engage.equals("已完成")){
				Intent intent = new Intent();
				intent.setClass(mContext, Home_Job_Detail.class);
				intent.putExtra("job_id", struct.job_id+"");
				intent.putExtra("in_flag", "manage");
				intent.putExtra("userid", muserid+"");
				intent.putExtra("job_name", struct.hire_name);
				mContext.startActivity(intent);
			}
			break;
		}
	}
	
	private void SetHiredState(String name,final int hired_id,final String engage) {
		new AlertDialog.Builder(mContext)
		.setTitle("雇佣状态")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setMessage("设置“"+name+"”为进行中？")
		.setPositiveButton("确定", 
		  new DialogInterface.OnClickListener() {
		                            
		     public void onClick(DialogInterface dialog, int which) {
		    	 
		    	mshared.edit().putInt("hire_id", hired_id).commit();
		        dialog.dismiss();
		        new GetEmployAsynTask().execute(hired_id+"",engage);
		     }
		  }
		)
		.setNegativeButton("取消", null).show();
	}
	
	/**
	 * 设置工作的状态
	 */
	public class GetEmployAsynTask extends AsyncTask<String, Void, Boolean> {
		long errcode = -1;
		int hireid=-1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			ShowWaiting("正在雇佣...");
		}
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				hireid = Integer.parseInt(params[0]);
				errcode = SvrOperation.HiredFunctionTask(mContext, params[0],params[1]);

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
//			HideWaiting();
			if (result == false) {
				if(errcode== -3)
					Utils.ShowToast(mContext, "设置失败！");
				return;
			}
			Message msg = new Message();
			msg.what =10000;
			msg.arg1=hireid;
			msg.arg2 =CLICK_TYPE_HIRED;
			mHandler.sendMessage(msg);
			Utils.ShowToast(mContext, "设置成功！");
		}
	}
	/**
	 * 处理雇佣和申请的数据
	 * 
	 * @param tasks_item
	 */
	private void PrecessApplayEmploy(HiredStruct tasks_item, int type) {
		if (type == CLICK_TYPE_APPLIED &&tasks_item.engage.equals("已申请")) {
			signitemview.BeforeTile.setText(tasks_item.hire_name);
			signitemview.BeforeTime.setText(tasks_item.create_time);
		} else if(type == CLICK_TYPE_HIRED&&tasks_item.engage.equals("被雇佣")){
			signitemview.BeforeTile.setText(tasks_item.hire_name);
			signitemview.BeforeTime.setText(tasks_item.create_time);
		}else if(type == CLICK_TYPE_DOING&&tasks_item.engage.equals("进行中")){
			signitemview.BeforeTile.setText(tasks_item.hire_name);
			signitemview.BeforeTime.setText(tasks_item.create_time);
		}else if(type == CLICK_TYPE_FINISHED&&tasks_item.engage.equals("已完成")){
			signitemview.BeforeTile.setText(tasks_item.hire_name);
			signitemview.BeforeTime.setText(tasks_item.create_time);
		}
	}
	/**
	 * fucntion: 签到工作的标题、天数;<br>
	 * 对应的天数对应的布局显示
	 */
	public static class SignListItem {

		/** 已申请、被录用 */
		private RelativeLayout BeforeDoneBlock;
		private TextView BeforeTile, BeforeTime;
	}
}

