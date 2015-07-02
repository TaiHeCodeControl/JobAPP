package com.parttime.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.parttime.adapter.ApplyJob_ListView_Adapter;
import com.parttime.adapter.Job_ListView_Adapter1;
import com.parttime.constant.Constant;
import com.parttime.data.PartTimeDB;
import com.parttime.msg.ErrorMsgSvr;
import com.parttime.parttimejob.R;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.BaiduMap_GetLatLng_Utils;
import com.parttime.utils.Utils;

/**
 * 主页
 */
public class Fragment_HomePage extends Fragment {
	private static Context mContext = null;

	private static PartTimeDB parttimeDb;

	/**
	 * banner图的viewpager
	 */
	
	private ViewPager banner_viewpager = null;
	private ListView center_listview = null;
	private TextView home_hotwork;

	/* 等待框部分 */
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;

	private int[] imageIDs;
	private ArrayList<ImageView> images;
	private ArrayList<View> dots;
	private int currrentItem;

	private ScheduledExecutorService scheduledExecutorService;

	private String userflag = null;
	private SharedPreferences sharedPreferences;
	private ArrayList<HashMap<String, String>> data_list = null;
	private String company_add;
	private ArrayList<Integer> location_list = new ArrayList<Integer>();
	private ArrayList<Integer> level_list = new ArrayList<Integer>();

	static Fragment_HomePage newInstance(Context context) {
		Fragment_HomePage framgment1 = new Fragment_HomePage();
		mContext = context;
		parttimeDb = PartTimeDB.getInstance(mContext);
		return framgment1;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_homepager, container, false);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		userflag = sharedPreferences.getString("userflag", "personal");

		// if (userflag != null && userflag.equals("personal")) {
		parttimeDb.ClearHotWorkInfo();

		initView(view);

		setViewPager(view);

		return view;
	}

	public void ShowWaiting(String text) {
		WaitingText.setText(text);
		WaitingDlg.setVisibility(View.VISIBLE);
	}

	public void HideWaiting() {
		WaitingDlg.setVisibility(View.GONE);
	}

	/**
	 * 设置viewpager的图片
	 * 
	 * @param view
	 */
	private void setViewPager(View view) {
		imageIDs = new int[] {

		R.drawable.banner_1, R.drawable.banner_2, R.drawable.banner_3,

		};
		images = new ArrayList<ImageView>();
		int size = imageIDs.length + 2;
		for (int i = 0; i < size; i++) {
			// ImageView imageView = new ImageView(mContext);
			// imageView.setBackgroundResource(imageIDs[i]);

			ImageView imageView = new ImageView(mContext);
			ViewGroup.LayoutParams viewPagerImageViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
			imageView.setLayoutParams(viewPagerImageViewParams);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);

			images.add(imageView);
		}
		dots = new ArrayList<View>();
		dots.add(view.findViewById(R.id.dot_2));
		dots.add(view.findViewById(R.id.dot_0));
		dots.add(view.findViewById(R.id.dot_1));
		dots.add(view.findViewById(R.id.dot_2));
		dots.add(view.findViewById(R.id.dot_0));
		banner_viewpager.setAdapter(new MyAdapter());
		banner_viewpager.setCurrentItem(1);

		banner_viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			int oldPosition = 0;

			@Override
			public void onPageSelected(int position) {

				dots.get(position).setBackgroundResource(R.drawable.dot_focused);

				dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
				oldPosition = position;
				int pageIndex = position;

				if (position == 0) {
					// 当视图在第一个时，将页面号设置为图片的最后一张。
					pageIndex = imageIDs.length;
				} else if (position == imageIDs.length + 1) {
					// 当视图在最后一个是,将页面号设置为图片的第一张。
					pageIndex = 1;
				}

				if (position == imageIDs.length + 1) {
					oldPosition = 0;
				} else {
					oldPosition = pageIndex;
				}

				currrentItem = pageIndex;
				if (position != pageIndex) {
					banner_viewpager.setCurrentItem(pageIndex, false);
					return;
				}

			}

			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int position) {

			}
		});

	}

	private void initView(View view) {
		banner_viewpager = (ViewPager) view.findViewById(R.id.home_viewpager);
		center_listview = (ListView) view.findViewById(R.id.home_listview);
		home_hotwork = (TextView) view.findViewById(R.id.home_hotwork);

		/* 等待对话框部分 */
		WaitingText = (TextView) view.findViewById(R.id.Waiting_text);
		WaitingDlg = (RelativeLayout) view.findViewById(R.id.Waiting_dlg);

		if (userflag != null && userflag.equals("personal")) {
			home_hotwork.setText(mContext.getResources().getString(R.string.home_hotwork));
			new HotWork_Task().execute();
		} else if (userflag != null && userflag.equals("company")) {
			home_hotwork.setText(mContext.getResources().getString(R.string.home_jobseeker));
			new HotSeeker_Task().execute();
		}

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new ViewpagerTask(), 0, 10, TimeUnit.SECONDS);
	}

	@Override
	public void onResume() {
		super.onResume();
		banner_viewpager.setCurrentItem(1);

	}

	@Override
	public void onPause() {
		super.onPause();
		Utils.CommitPageFlagToShared(mContext, Constant.FINISH_ACTIVITY_FLAG_HOMEPAGE);
	}

	/**
	 * 设置个人用户的listview的一系列操作
	 */
	private void setPersonalListViewListener(final ArrayList<HashMap<String, String>> list) {

		ArrayList<Integer> location_list = new ArrayList<Integer>();

		for (int i = 0; i < list.size(); i++) {
			String company_addr = list.get(i).get("position");
			if (company_addr != null) {
				String[] lat_lng = company_addr.split(",");
				if (lat_lng != null && lat_lng.length == 2) {
					double lat = Double.parseDouble(lat_lng[0]);
					double lgn = Double.parseDouble(lat_lng[1]);
					LatLng ll = BaiduMap_GetLatLng_Utils.mLocal_LatLng;
					LatLng la = new LatLng(lat, lgn);
					double meter = DistanceUtil.getDistance(la, ll);
					location_list.add((int) meter);
				} else {
					location_list.add(0);
				}
			}
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("hot_id", list.get(i).get("hot_id"));
			map.put("name", list.get(i).get("name"));
			map.put("charge", list.get(i).get("charge"));
			map.put("type", list.get(i).get("type"));
			map.put("num", list.get(i).get("num"));
			map.put("create_time", list.get(i).get("create_time"));
			map.put("company_name", list.get(i).get("company_name"));
			map.put("company_add", list.get(i).get("company_add"));
			map.put("company_level", list.get(i).get("company_level"));
			map.put("company_id", list.get(i).get("company_id"));
			map.put("position", list.get(i).get("position"));

			// map.put("position", location_list.get(i));

			if (parttimeDb.IsExistHotWorkInfo(list.get(i).get("hot_id"), mContext)) {
				parttimeDb.UpdateHotWorkInfo1(mContext, map, location_list.get(i), list.get(i).get("hot_id"));
			} else {
				parttimeDb.AddHotWorkInfo1(mContext, map, location_list.get(i));
			}
			// String company_id = list.get(i).get("company_id");
			// new GetCompanyLevel_Task().execute(company_id);

		}
		ArrayList<HashMap<String, Object>> part_list = parttimeDb.getHotWorkInfo1(mContext);

		Job_ListView_Adapter1 home_adapter = new Job_ListView_Adapter1(getActivity(), part_list, "home");

		center_listview.setAdapter(home_adapter);

	}

	/**
	 * 设置企业用户的listview的一系列操作
	 */
	private void setCompanyListViewListener(final ArrayList<HashMap<String, String>> list) {

		ArrayList<Integer> location_list = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			String company_addr = list.get(i).get("position");
			String[] lat_lng = company_addr.split(",");
			if (lat_lng != null && lat_lng.length == 2) {
				double lat = Double.parseDouble(lat_lng[0]);
				double lgn = Double.parseDouble(lat_lng[1]);
				LatLng ll = BaiduMap_GetLatLng_Utils.mLocal_LatLng;
				LatLng la = new LatLng(lat, lgn);
				double meter = DistanceUtil.getDistance(la, ll);
				location_list.add((int) meter);
			} else {
				location_list.add(0);
			}

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("height", list.get(i).get("height"));
			map.put("type", list.get(i).get("type"));
			map.put("name", list.get(i).get("name"));
			map.put("sex", list.get(i).get("sex"));
			map.put("start_time", list.get(i).get("start_time"));
			map.put("experence", list.get(i).get("experence"));
			map.put("addr", list.get(i).get("addr"));
			map.put("job", list.get(i).get("job"));
			map.put("position", list.get(i).get("position"));
			map.put("rid", list.get(i).get("rid"));
			map.put("user_id", list.get(i).get("user_id"));
			map.put("call", list.get(i).get("call"));
			map.put("end_time", list.get(i).get("end_time"));
			map.put("create_time", list.get(i).get("create_time"));
			if (parttimeDb.IsExistHotSeekerInfo(list.get(i).get("rid"), mContext)) {
				parttimeDb.UpdateHotSeekerInfo1(mContext, map, location_list.get(i), list.get(i).get("rid"));
			} else {
				parttimeDb.AddHotSeekerInfo1(mContext, map, location_list.get(i));
			}

		}

		ArrayList<HashMap<String, Object>> part_list = parttimeDb.getHotWeekerInfo1(mContext);

		ApplyJob_ListView_Adapter home_adapter = new ApplyJob_ListView_Adapter(getActivity(), part_list, "home");
		center_listview.setAdapter(home_adapter);

	}

	@Override
	public void onStop() {
		super.onStop();
		scheduledExecutorService.shutdown();
	}

	class ViewpagerTask implements Runnable {
		@Override
		public void run() {
			
			if (currrentItem == 0) {
				// 当视图在第一个时，将页面号设置为图片的最后一张。
				currrentItem = imageIDs.length;
			} else if (currrentItem == imageIDs.length + 1) {
				// 当视图在最后一个是,将页面号设置为图片的第一张。
				currrentItem = 1;
			}
			currrentItem++;
			handler.sendEmptyMessage(0);
		}
	}

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			banner_viewpager.setCurrentItem(currrentItem);
		}

	};

	class MyAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup view, int position, Object object) {
			view.removeView(images.get(position));
			// ImageView view1 = images.get(position);
			// view.removeView(view);
			// view1.setImageBitmap(null);
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			if (position == 0) {
				images.get(position).setImageResource(imageIDs[2]);
			} else if (position == (images.size() - 1)) {
				images.get(position).setImageResource(imageIDs[0]);
			} else {
				images.get(position).setImageResource(imageIDs[position - 1]);
			}
			view.addView(images.get(position));
			return images.get(position);
		}
	}

	/**
	 * 热门工作推荐的异步
	 * 
	 * @author huxixi
	 * 
	 */
	public class HotWork_Task extends AsyncTask<Void, Void, Boolean> {
		private int errcode;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在获取热门工作推荐列表……");
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			int iresult = SvrOperation.Hot_Work(mContext);
			if (iresult != SvrInfo.SVR_RESULT_SUCCESS) {
				errcode = iresult;
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
				if (errcode == -9)
					Utils.ShowToast(mContext, "没有推荐职位");
				else
					Utils.ShowToast(mContext, errmsg);

				return;
			}
			data_list = parttimeDb.getHotWorkInfo(mContext);
			setPersonalListViewListener(data_list);
			return;
		}

	}

	/**
	 * 求职者推荐的异步
	 * 
	 * @author huxixi
	 * 
	 */
	public class HotSeeker_Task extends AsyncTask<Void, Void, Boolean> {

		private int errcode;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在获取求职者推荐列表……");
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			errcode = SvrOperation.Hot_Seeker(mContext);
			if (errcode != SvrInfo.SVR_RESULT_SUCCESS) {
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
				if (errcode == -9)
					Utils.ShowToast(mContext, "没有推荐的求职者");
				else
					Utils.ShowToast(mContext, errmsg);

				return;
			}
			data_list = parttimeDb.getHotWeekerInfo(mContext);
			setCompanyListViewListener(data_list);
			return;
		}

	}

}
