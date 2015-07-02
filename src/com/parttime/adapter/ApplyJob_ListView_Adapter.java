package com.parttime.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parttime.activity.BaiduMap_Activity;
import com.parttime.activity.Job_See_Resume;
import com.parttime.constant.Constant;
import com.parttime.parttimejob.R;
import com.parttime.service.LoginStatus;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Utils;

public class ApplyJob_ListView_Adapter extends BaseAdapter {

	private Activity mActivity;
	private ArrayList<HashMap<String, Object>> arraylist = null;
	private LayoutInflater mInflater;
	private String flag = "";
	private Handler handler = null;
	private HashMap<String, String> input_map = null;

	public ApplyJob_ListView_Adapter(Activity mActivity, ArrayList<HashMap<String, Object>> list, String flag) {
		this.mActivity = mActivity;
		this.arraylist = list;
		this.flag = flag;

		mInflater = LayoutInflater.from(mActivity);
	}

	public void refresh(ArrayList<HashMap<String, Object>> list) {
		this.arraylist = list;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
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
			convertView = mInflater.inflate(R.layout.job_seeker_item, null);
			holder.seeker_linear = (LinearLayout) convertView.findViewById(R.id.seeker_linear);
			holder.release_time_relative = (RelativeLayout) convertView.findViewById(R.id.release_time_relative);
			holder.job_seeker_name = (TextView) convertView.findViewById(R.id.job_seeker_name);
			holder.position = (TextView) convertView.findViewById(R.id.position);
			holder.job_time = (TextView) convertView.findViewById(R.id.job_time);
			holder.location = (TextView) convertView.findViewById(R.id.job_location);
			holder.distance_icon = (ImageView) convertView.findViewById(R.id.distance_icon);
			holder.job_sign_day = (TextView) convertView.findViewById(R.id.job_sign_day);
			holder.job_sign_time = (TextView) convertView.findViewById(R.id.job_sign_time);
			holder.job_sign_week = (TextView) convertView.findViewById(R.id.job_sign_week);
			holder.home_ratingBar = (ProgressBar) convertView.findViewById(R.id.home_ratingBar);
			convertView.setTag(holder);
			convertView.setId(position);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (flag.equals("home")) {
			holder.distance_icon.setVisibility(View.GONE);
			holder.location.setVisibility(View.GONE);
			holder.distance_icon.setClickable(false);
			holder.location.setClickable(false);
			holder.release_time_relative.setClickable(false);
		} else {
			holder.release_time_relative.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Log.i("hu", "release_time");
					Utils.ShowToast(mActivity, "进入地图界面");
					Intent intent = new Intent();
					intent.setClass(mActivity, BaiduMap_Activity.class);
					String company_addr = String.valueOf(arraylist.get(position).get("position"));
					if (company_addr != null) {
						String[] lat_lng = company_addr.split(",");
						if (lat_lng != null && lat_lng.length == 2) {
							double lat = Double.parseDouble(lat_lng[0]);
							double lgn = Double.parseDouble(lat_lng[1]);
							intent.putExtra("lat", lat);
							intent.putExtra("lng", lgn);
							Log.e("hu", "lat   " + lat + "   lgn   " + lgn);
							mActivity.startActivity(intent);
							if (flag.equals("job"))
								Utils.CommitPageFlagToShared(mActivity, Constant.FINISH_ACTIVITY_FLAG_JOB);
							else if (flag.equals("home")) {
								Utils.CommitPageFlagToShared(mActivity, Constant.FINISH_ACTIVITY_FLAG_HOMEPAGE);
							}
						}
					}
				}
			});
		}

		String time_type = String.valueOf(arraylist.get(position).get("type"));
		if (time_type != null && time_type.equals("时")) {
			holder.job_sign_time.setVisibility(View.VISIBLE);
			holder.job_sign_day.setVisibility(View.INVISIBLE);
			holder.job_sign_week.setVisibility(View.INVISIBLE);
		} else if (time_type.equals("天")) {
			holder.job_sign_time.setVisibility(View.INVISIBLE);
			holder.job_sign_day.setVisibility(View.VISIBLE);
			holder.job_sign_week.setVisibility(View.INVISIBLE);

		} else if (time_type.equals("周")) {
			holder.job_sign_time.setVisibility(View.INVISIBLE);
			holder.job_sign_day.setVisibility(View.INVISIBLE);
			holder.job_sign_week.setVisibility(View.VISIBLE);

		}

		String company_id = String.valueOf(arraylist.get(position).get("user_id"));

		input_map = new HashMap<String, String>();
		input_map.put("type", "1");
		input_map.put("personal_id", company_id);

		new GetCompanyLevel_Task(holder).execute(input_map);

		holder.job_seeker_name.setText(String.valueOf(arraylist.get(position).get("name")));
		holder.position.setText(String.valueOf(arraylist.get(position).get("job")));
		holder.job_time.setText(String.valueOf(arraylist.get(position).get("create_time")));
		holder.location.setText(((Integer) (arraylist.get(position).get("location")) / 1000.0) + "km");

		String creat_time = String.valueOf(arraylist.get(position).get("create_time"));
		String[] times = creat_time.split(" ");
		holder.job_time.setText(String.valueOf(times[0]));

		holder.seeker_linear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!LoginStatus.getLoginStatus().isLogin()) {
					LoginStatus.getLoginStatus().showDialog(mActivity);
					return;
				}
				Intent intent = new Intent();
				intent.setClass(mActivity, Job_See_Resume.class);
				intent.putExtra("flag", flag);
				intent.putExtra("userid", String.valueOf(arraylist.get(position).get("user_id")));
				intent.putExtra("rid", String.valueOf(arraylist.get(position).get("rid")));
				mActivity.startActivity(intent);
			}

		});

		return convertView;
	}

	/**
	 * listview 所用的viewholder
	 * 
	 * @author huxixi
	 */
	public class ViewHolder {
		public TextView job_seeker_name;
		public TextView position; // 职位
		public TextView job_time;
		public TextView location;
		public TextView job_sign_time;
		public TextView job_sign_day;
		public TextView job_sign_week;
		public ImageView distance_icon;
		public LinearLayout seeker_linear;
		public ProgressBar home_ratingBar;
		public RelativeLayout release_time_relative;

	}

	/**
	 * 获取企业信誉度的异步
	 * 
	 * @author huxixi
	 * 
	 */
	public class GetCompanyLevel_Task extends AsyncTask<HashMap<String, String>, Void, Boolean> {

		private String errcode;
		private ViewHolder holder = null;

		private GetCompanyLevel_Task(ViewHolder viewholder) {
			this.holder = viewholder;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(HashMap<String, String>... params) {
			errcode = SvrOperation.getCompanyLevel(mActivity, params[0]);

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result == false) {
				Utils.ShowToast(mActivity, "数据查询失败");
				return;
			}
			int le = 0;
			if (errcode == null || errcode.equals("") || errcode.equals("null")) {
				errcode = "0";
				le = 0;
			} else if (errcode != null) {
				le = (int) Double.parseDouble(errcode);
			}
			holder.home_ratingBar.setProgress(le);
			return;
		}
	}

}
