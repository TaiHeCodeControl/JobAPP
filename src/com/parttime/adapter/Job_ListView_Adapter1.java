package com.parttime.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.parttime.activity.Home_Job_Detail;
import com.parttime.constant.Constant;
import com.parttime.parttimejob.R;
import com.parttime.service.LoginStatus;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Utils;

public class Job_ListView_Adapter1 extends BaseAdapter {

	private Activity mActivity;
	private ArrayList<HashMap<String, Object>> arraylist = null;
	private LayoutInflater mInflater;
	private String flag = "";
	private HashMap<String, String> input_map = null;

	public Job_ListView_Adapter1(Activity mActivity, ArrayList<HashMap<String, Object>> list, String flag) {
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
		// return arraylist.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arraylist.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.job_listview_item1, null);
			holder.job_linear = (LinearLayout) convertView.findViewById(R.id.job_linear);
			holder.job_name = (TextView) convertView.findViewById(R.id.job_name);
			holder.job_sign_day = (TextView) convertView.findViewById(R.id.job_sign_day);
			holder.job_sign_time = (TextView) convertView.findViewById(R.id.job_sign_time);
			holder.job_sign_week = (TextView) convertView.findViewById(R.id.job_sign_week);
			holder.job_time = (TextView) convertView.findViewById(R.id.job_time);
			holder.job_price = (TextView) convertView.findViewById(R.id.job_price);
			holder.job_distance = (TextView) convertView.findViewById(R.id.job_distance);
			holder.company_name = (TextView) convertView.findViewById(R.id.company_name);
			holder.job_num = (TextView) convertView.findViewById(R.id.job_num);
			holder.home_ratingBar = (ProgressBar) convertView.findViewById(R.id.home_ratingBar);
			holder.location_relative = (RelativeLayout) convertView.findViewById(R.id.location_relative);
			holder.job_distance_sign = (ImageView) convertView.findViewById(R.id.job_distance_sign);

			convertView.setTag(holder);
			convertView.setId(position);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (flag.equals("home")) {
			holder.job_distance_sign.setVisibility(View.GONE);
			holder.job_distance.setVisibility(View.GONE);
			holder.job_distance.setClickable(false);
			holder.job_distance_sign.setClickable(false);
			holder.location_relative.setClickable(false);
		} else {
			holder.location_relative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
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

		holder.job_name.setText(arraylist.get(position).get("name").toString());
		String time_type = arraylist.get(position).get("type").toString();
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
		String time = arraylist.get(position).get("create_time").toString();
		if (time.contains(" "))
			time = time.substring(0, time.indexOf(" "));
		holder.job_time.setText(time);

		holder.job_price.setText(arraylist.get(position).get("charge").toString() + "元");
		holder.job_num.setText("招" + arraylist.get(position).get("num").toString() + "人");
		holder.company_name.setText(arraylist.get(position).get("company_name").toString());

		int location = (Integer) arraylist.get(position).get("location");
		holder.job_distance.setText(location / 1000.0 + "km");

		String company_id = arraylist.get(position).get("company_id").toString();

		input_map = new HashMap<String, String>();
		input_map.put("type", "0");
		input_map.put("business_id", company_id);

		new GetCompanyLevel_Task(holder).execute(input_map);

		holder.job_linear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				if (!LoginStatus.getLoginStatus().isLogin()) {
//					LoginStatus.getLoginStatus().showDialog(mActivity);
//					return;
//				}
				Intent intent = new Intent();
				intent.setClass(mActivity, Home_Job_Detail.class);
				intent.putExtra("in_flag", flag);
				intent.putExtra("userid", arraylist.get(position).get("hot_id").toString());
				String job_id = String.valueOf(arraylist.get(position).get("hot_id"));
				intent.putExtra("job_id", job_id);
				String job_name = arraylist.get(position).get("name").toString();
				intent.putExtra("job_name", job_name);
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
		public LinearLayout job_linear;
		public TextView job_name;
		public TextView job_sign_time;
		public TextView job_sign_day;
		public TextView job_sign_week;
		public TextView company_name;
		public TextView job_time;
		public TextView job_price;
		public TextView job_distance;
		public TextView job_num;
		public ImageView job_distance_sign;
		public ProgressBar home_ratingBar;
		public RelativeLayout location_relative;

	}

	/**
	 * 获取企业信誉度的异步
	 * 
	 * @author huxixi
	 * 
	 */
	public class GetCompanyLevel_Task extends AsyncTask<HashMap<String, String>, Void, Boolean> {

		private String errcode;
		private ViewHolder viewholder;

		private GetCompanyLevel_Task(ViewHolder viewholder) {
			this.viewholder = viewholder;
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
			viewholder.home_ratingBar.setProgress(le);

			return;
		}

	}

}
