package com.parttime.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.HiredStruct;

/**
 * 
 * @author 灰色的寂寞
 * @function 个人中心任务签到功能
 * @date 2015-1-19
 * @time 15:32
 */
public class PersonalTaskAdapter extends BaseAdapter implements OnClickListener{
	public static final String TAG = PersonalTaskAdapter.class.getSimpleName();
	private Context mContext;
	private PartTimeDB mPartTimeDB = null;
	private String SignDayNum = "";
	private ArrayList<HiredStruct> mtaskList = null;

	private Handler mHandler = null;

	/* 布局部分 */
	private LayoutInflater layoutInflater = null;
	private SignListItem signitemview = null;

	/* 常量部分 */
	/* 任务状态 */
	private int TaskType = -1;
	private final static int TASK_APPLAYED = 103;// 已申请
	private final static int TASK_EMPLOY = 105;// 被录用
	private final static int TASK_FINISHED = 106;//已完成
	private SharedPreferences mshared;
	public PersonalTaskAdapter(Context context, Handler mhandler, int tasktype) {
		mContext = context;
		layoutInflater = LayoutInflater.from(context);
		TaskType = tasktype;
		mPartTimeDB = PartTimeDB.getInstance(mContext);
		mshared = PreferenceManager.getDefaultSharedPreferences(mContext);
		mHandler = mhandler;
		
		
	}

	public ArrayList<HiredStruct> getList(int type) {
		if(mtaskList!=null)
			mtaskList.clear();
		ArrayList<HiredStruct> list = new ArrayList<HiredStruct>();
		if (type == TASK_APPLAYED) {
			list = mPartTimeDB.getHiredJobList(mContext,-1);
		}if(type == TASK_EMPLOY) {
		}else{//TASK_FINISHED
		}
		return list;
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

			/** 已申请、被录用 */
			signitemview.BeforeDoneBlock = (RelativeLayout) convertView.findViewById(R.id.job_before_done_block);
			signitemview.BeforeTile = (TextView) convertView.findViewById(R.id.job_before_done_title);
			signitemview.BeforeTime = (TextView) convertView.findViewById(R.id.job_before_done_time);
			signitemview.BeforeDoneBlock.setOnClickListener(PersonalTaskAdapter.this);
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
			SetHiredState(struct.hire_name,struct.id);
			
			Message msg = new Message();
			msg.what = 10000;
			msg.obj = struct.hire_name;
			mHandler.sendMessage(msg);
			break;
		}
	}
	
	private void SetHiredState(String name,final int hired_id) {
		new AlertDialog.Builder(mContext)
		.setTitle("雇佣状态")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setMessage("设置“"+name+"”为进行中？")
		.setPositiveButton("确定", 
		  new DialogInterface.OnClickListener() {
		                            
		     public void onClick(DialogInterface dialog, int which) {
		    	 
		    	mshared.edit().putInt("hire_id", hired_id).commit();
		        dialog.dismiss();
		     }
		  }
		)
		.setNegativeButton("取消", null).show();
	}
	/**
	 * 处理雇佣和申请的数据
	 * 
	 * @param tasks_item
	 */
	private void PrecessApplayEmploy(HiredStruct tasks_item, int type) {
		if (type == TASK_APPLAYED) {
			signitemview.BeforeTile.setText(tasks_item.hire_name);
			signitemview.BeforeTime.setText(tasks_item.create_time);
		} else if(type == TASK_FINISHED){
			signitemview.BeforeTile.setText(tasks_item.hire_name);
			signitemview.BeforeTime.setText(tasks_item.create_time);
		}else {// TASK_EMPLOY
			signitemview.BeforeTile.setText(tasks_item.hire_name);
			signitemview.BeforeTime.setText(tasks_item.create_time);
		}
	}

	/**
	 * 加载本地数据列表，如果没有从新加载
	 */
	public void ReloadData(int TaskType) {

		switch (TaskType) {
		case TASK_APPLAYED:// 已申请
			mtaskList = getList(TASK_APPLAYED);
			break;
		case TASK_EMPLOY:// 被录用
			mtaskList = getList(TASK_EMPLOY);
			break;
		case TASK_FINISHED:// 已完成
			mtaskList = getList(TASK_FINISHED);
			break;
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
