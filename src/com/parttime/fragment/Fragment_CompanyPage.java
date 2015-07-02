package com.parttime.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parttime.activity.Company_Details_Introduce_Activity;
import com.parttime.activity.Company_JobList_Activty;
import com.parttime.activity.Personal_FeadBack_Activity;
import com.parttime.activity.Personal_Setting_Activity;
import com.parttime.activity.Personal_SpecialCustomer_Activity;
import com.parttime.activity.Personal_Wallet_Activity;
import com.parttime.activity.VolunteerMainActivity;
import com.parttime.constant.Constant;
import com.parttime.parttimejob.R;
import com.parttime.utils.Utils;

/**
 * @author 灰色的寂寞
 * @function 企业页面，企业个人信息部分（企业详情、用工管理、职位管理、企业钱包、意见反馈、特惠商户、设置）
 * @date 2015-1-19
 * @time 18:08
 */
public class Fragment_CompanyPage extends Fragment {
	private static Context mContext = null;

	private ListView contentList;
	private SimpleAdapter simpleAdapter;

	/** 企业详情 */
	private static final int MENU_PERSONAL_COMPANEY_DETAILS = 0;
	// /** 用工管理 */
	// private static final int MENU_PERSONAL_MANAGEWORKER = 1;
	// /** 职位管理 */
	// private static final int MENU_PERSONAL_MANAGEJOB = 2;
	/** 企业钱包 */
	private static final int MENU_PERSONAL_WALLET = 1;
	/** 公益 */
	private static final int MENU_PERSONAL_VOLUNTEER_PRESSED = 2;
	/** 特惠商户 */
	private static final int MENU_PERSONAL_PREFERENCE_BUSINISS = 3;
	/** 设置 */
	private static final int MENU_PERSONAL_SETTING = 4;
	private SharedPreferences sharedPreferences;

	static Fragment_CompanyPage newInstance(Context context) {
		Fragment_CompanyPage framgment1 = new Fragment_CompanyPage();
		mContext = context;
		return framgment1;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_companypager, container, false);
		makeMenuList();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		initViews(view);
		return view;

	}

	@Override
	public void onPause() {
		super.onPause();
		Utils.CommitPageFlagToShared(mContext, Constant.FINISH_ACTIVITY_FLAG_COMPANY);
	}

	/**
	 * 菜单view显示的数据列表 image 字段暂时无用
	 */
	private void makeMenuList() {
		ArrayList<HashMap<String, Object>> listItems = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map;

		/** 企业详情 */
		map = new HashMap<String, Object>();
		map.put("image", R.drawable.company_details);
		map.put("content", "企业详情");
		map.put("ItemId", MENU_PERSONAL_COMPANEY_DETAILS);
		listItems.add(map);
		// /** 用工管理 */
		// map = new HashMap<String, Object>();
		// map.put("image", R.drawable.company_employ_icon);
		// map.put("content","用工管理");
		// map.put("ItemId", MENU_PERSONAL_MANAGEWORKER);
		// listItems.add(map);
		// /** 职位管理 */
		// map = new HashMap<String, Object>();
		// map.put("image", R.drawable.company_job_icon);
		// map.put("content","职位管理 ");
		// map.put("ItemId", MENU_PERSONAL_MANAGEJOB);
		// listItems.add(map);
		/** 企业钱包 */
		map = new HashMap<String, Object>();
		map.put("image", R.drawable.company_wallet);
		map.put("content", "企业钱包");
		map.put("ItemId", MENU_PERSONAL_WALLET);
		listItems.add(map);
		// /** 意见反馈 */
		// map = new HashMap<String, Object>();
		// map.put("image", R.drawable.feedback);
		// map.put("content", "意见反馈");
		// map.put("ItemId", MENU_PERSONAL_WALLET);
		// listItems.add(map);
		/** 公益 */
		map = new HashMap<String, Object>();
		map.put("image", R.drawable.company_heart);
		map.put("content", "公益");
		map.put("ItemId", MENU_PERSONAL_VOLUNTEER_PRESSED);
		listItems.add(map);
		/** 特惠商户 */
		map = new HashMap<String, Object>();
		map.put("image", R.drawable.company_pre_busi);
		map.put("content", "特惠商户");
		map.put("ItemId", MENU_PERSONAL_PREFERENCE_BUSINISS);
		listItems.add(map);
		/** 设置 */
		map = new HashMap<String, Object>();
		map.put("image", R.drawable.company_setting_gray);
		map.put("content", "设置");
		map.put("ItemId", MENU_PERSONAL_SETTING);
		listItems.add(map);

		String[] from = { "image", "content" };
		// grid_item.xml中对应的ImageView控件和TextView控件
		int[] to = { R.id.company_content_image, R.id.company_content };
		// 设定一个适配器
		simpleAdapter = new SimpleAdapter(mContext, listItems, R.layout.company_center_content_list_item, from, to);

	}

	/**
	 * 初始化界面View
	 * 
	 * @param view
	 */
	private void initViews(View view) {
		contentList = (ListView) view.findViewById(R.id.companey_center_content_list);

		// 对GridView进行适配
		contentList.setAdapter(simpleAdapter);
		contentList.setOnItemClickListener(itemClickListener);
	}

	public OnItemClickListener itemClickListener = new OnItemClickListener() {
		// AdapterView<?> arg0,//The AdapterView where the click happened
		// View arg1,//The view within the AdapterView that was clicked
		// Int arg2,//The position of the view in the adapter
		// long arg3//The row id of the item that was clicked
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(position);
			int contentImage = (Integer) item.get("image");
			String content = (String) item.get("content");
			int CmdId = (Integer) item.get("ItemId");

			Utils.CommitPageFlagToShared(mContext, Constant.FINISH_ACTIVITY_FLAG_COMPANY);

			switch (position) {

			/** 企业详情 */
			case 0:
				Utils.intent2Class(mContext, Company_Details_Introduce_Activity.class);
				break;
			// /** 用工管理 */
			// case MENU_PERSONAL_MANAGEWORKER:
			// MainFragment.HideCompanyRedDot();
			// Intent intent = new Intent();
			// intent.setClass(mContext, Company_JobList_Activty.class);
			// intent.putExtra("turn_type", MENU_PERSONAL_MANAGEWORKER);
			// startActivity(intent);
			// break;
			// /** 职位管理 */
			// case MENU_PERSONAL_MANAGEJOB:
			// sharedPreferences.edit().putInt("managejob_flag", 0).commit();
			// intent = new Intent();
			// intent.setClass(mContext, Company_JobList_Activty.class);
			// intent.putExtra("turn_type", MENU_PERSONAL_MANAGEJOB);
			// startActivity(intent);
			//
			// break;
			/** 企业钱包 */
			case 1:
				Utils.intent2Class(mContext, Personal_Wallet_Activity.class);
				break;
			/** 公益 */
			case 2:
				Utils.intent2Class(mContext, VolunteerMainActivity.class);
				break;
			/** 特惠商户 */
			case 3:
				Utils.intent2Class(mContext, Personal_SpecialCustomer_Activity.class);
				break;
			/** 设置 */
			case 4:
				Utils.intent2Class(mContext, Personal_Setting_Activity.class);
				break;

			}
		}
	};

	/**
	 * 处理红点是否显示
	 * 
	 * @param type
	 */
	public void ProcessCompanyRedDot(int type) {
		int manageemployer_flag = sharedPreferences.getInt("manageemployer_flag", 1);
		int managejob_flag = sharedPreferences.getInt("managejob_flag", 1);
		if (managejob_flag == 0 && manageemployer_flag == 0) {
			sharedPreferences.edit().putInt("manageemployer_flag", 0).commit();
			MainFragment.HideCompanyRedDot();
		}
	}
}
