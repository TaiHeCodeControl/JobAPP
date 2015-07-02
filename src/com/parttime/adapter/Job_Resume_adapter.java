package com.parttime.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.parttime.activity.Job_See_Resume;
import com.parttime.parttimejob.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Job_Resume_adapter extends BaseAdapter {
	private Context context;
	private ArrayList<HashMap<String, String>> arraylist = null;
	private LayoutInflater mInflater;
	private CheckboxState checkboxstate;
	/**
	 * 存储是否选择简历的状态
	 */
	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();

	public Job_Resume_adapter(Context context, ArrayList<HashMap<String, String>> list) {
		this.context = context;
		this.arraylist = list;
		mInflater = LayoutInflater.from(context);
	}

	public void refresh(ArrayList<HashMap<String, String>> list) {
		this.arraylist = list;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arraylist.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.job_all_resume_item, null);
			holder.desired_position = (TextView) convertView.findViewById(R.id.desired_position);
			holder.job_time = (TextView) convertView.findViewById(R.id.job_time);
			holder.view_details = (TextView) convertView.findViewById(R.id.view_details);
			holder.select_check = (CheckBox) convertView.findViewById(R.id.select_check);

			convertView.setTag(holder);
			convertView.setId(position);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.desired_position.setText(String.format(context.getResources().getString(R.string.resume_desired_position), arraylist.get(position).get("rjob")));
		String job_time = "";
		String starttime = arraylist.get(position).get("rstarttime");
		String endtime = arraylist.get(position).get("rendtime");
		job_time = starttime + "--" + endtime;
		holder.job_time.setText(String.format(context.getResources().getString(R.string.resume_part_time), job_time));
		holder.view_details.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, Job_See_Resume.class);
				// intent.putExtra("flag", "look");
				intent.putExtra("rid", arraylist.get(position).get("rid"));
				context.startActivity(intent);

			}
		});

		holder.select_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mSelectMap.put(position, isChecked);
				if (getSelectItems().size() == 0) {
					checkboxstate.getcheckbox(false);
				} else {
					checkboxstate.getcheckbox(true);
				}

			}
		});
		holder.select_check.setChecked(mSelectMap.containsKey(position) ? mSelectMap.get(position) : false);

		return convertView;
	}

	/**
	 * 获取选中的Item的position
	 * 
	 * @return
	 */
	public List<HashMap<String, String>> getSelectItems() {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		for (Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, Boolean> entry = it.next();
			if (entry.getValue()) {
				String rid1 = arraylist.get((entry.getKey().intValue())).get("rid");
				map.put("rid", rid1);
				list.add(map);
			} else {
				map.put("rid", "");
				list.add(map);
			}
		}

		return list;
	}

	// public List<Integer> getSelectItems() {
	// List<Integer> list = new ArrayList<Integer>();
	// List<String> rid=new ArrayList<String>();
	// for (Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet()
	// .iterator(); it.hasNext();) {
	// Map.Entry<Integer, Boolean> entry = it.next();
	// if (entry.getValue()) {
	// list.add(entry.getKey());
	// String rid1= arraylist.get((entry.getKey().intValue())).get("rid");
	// Log.i("hu", "rid   "+rid1);
	// }
	// }
	//
	// return list;
	// }

	/**
	 * listview 所用的viewholder
	 * 
	 * @author huxixi
	 */
	public class ViewHolder {
		public TextView desired_position;
		public TextView job_time;
		public TextView view_details;
		public CheckBox select_check;

	}

	public void setoncheckboxstateListener(CheckboxState state) {
		this.checkboxstate = state;
	}

	public interface CheckboxState {
		public void getcheckbox(boolean click);
	}

}
