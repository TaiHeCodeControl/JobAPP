package com.parttime.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parttime.activity.LoginActivity;
import com.parttime.activity.Personal_Collection_Activity;
import com.parttime.activity.Personal_Details_Activity;
import com.parttime.activity.Personal_Resume_Manage_Activity;
import com.parttime.activity.Personal_Setting_Activity;
import com.parttime.activity.Personal_SpecialCustomer_Activity;
import com.parttime.activity.Personal_Task_Portrait_Activity;
import com.parttime.activity.Personal_Wallet_Activity;
import com.parttime.activity.VolunteerMainActivity;
import com.parttime.constant.Constant;
import com.parttime.parttimejob.R;
import com.parttime.service.LoginStatus;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;
import com.parttime.view.MaskImage;

/**
 * 
 * @author 灰色的寂寞
 * @fucntion 个人中心页面：1>发布职位者2>求职者
 *           内容包括：（1）头像部分（2）内容部分{1.任务，2.保障，3.钱包，4.收藏，6.设置，7.联系客服，8.特惠商户，9.简历管理}
 * @date 2015-1-12
 * @time 11:00
 * 
 */
public class Fragment_PersonalPage extends Fragment {
	private static Context mContext;

	private MaskImage mask_image;
	private ListView contentList;

	private TextView AccountName, ChangeAccount;

	private RelativeLayout VolunteerBlock, SafeGuardBlock, WalletBlock, CollectionBlock, FeedBackBlock, PBusinessBlock, CantactSvrBlock, SettingBlock;

	// 常量部分
	/** 任务 */
	private static final int MENU_PERSONAL_TASK = 0;
	/** 保障 */
	private static final int MENU_PERSONAL_SAFEGUARD = 1;
	/** 钱包 */
	private static final int MENU_PERSONAL_WALLET = 2;
	/** 收藏 */
	private static final int MENU_PERSONAL_COLLECT = 3;
	/** 设置 */
	private static final int MENU_PERSONAL_SETTING = 5;
	/** 联系客服 */
	private static final int MENU_PERSONAL_VOLUNTEER = 6;
	/** 特惠商户 */
	private static final int MENU_PERSONAL_PREFERENCE_BUSINISS = 7;

	/** 更换账号 */
	private static final int MENU_PERSONAL_CHANGEACCOUNT = 8;
	/** 简历管理 */
	private static final int MENU_PERSONAL_RESUME = 10;
	/** 个人信息 */
	private static final int MENU_PERSONAL_INFO = 11;
	/** 意见反馈 */
	private static final int MENU_PERSONAL_FEEDBACK = 12;
	/** 联系客服 */
	private static final int MENU_PERSONAL_CANTACTSVR = 13;

	private long userid = -1;
	private String avatar = "";
	private String name = "";
	private SharedPreferences sharedPreferences;

	static Fragment_PersonalPage newInstance(Context context) {
		Fragment_PersonalPage framgment1 = new Fragment_PersonalPage();
		mContext = context;
		return framgment1;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_personalpager, container, false);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		userid = sharedPreferences.getLong("userid", -1);
		avatar = sharedPreferences.getString("avatar", "");
		name = sharedPreferences.getString("username", "");
		initDisplayOptions();
		initViews(view);

		avatar = sharedPreferences.getString("avatar", "");
		if (Submit.isNetworkAvailable(mContext)) {
			new GetPersonalHeadAsynTask().execute(avatar, Constant.PARTTIMEJOB_HEAD);
		} else {
			ProcessData();
		}
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		ProcessData();
	}

	@Override
	public void onPause() {
		super.onPause();
		Utils.CommitPageFlagToShared(mContext, Constant.FINISH_ACTIVITY_FLAG_PERSONAL);
	}

	/**
	 * 下载头像图片到本地head
	 */
	public class GetPersonalHeadAsynTask extends AsyncTask<String, Void, Boolean> {

		long errcode = -1;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				errcode = SvrOperation.getFileFromSvr(mContext, params[0], params[1]);

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
				return;
			}

			ProcessData();
		}
	}

	/**
	 * 初始化界面View
	 * 
	 * @param view
	 */
	private void initViews(View view) {
		mask_image = (MaskImage) view.findViewById(R.id.mask_image);

		AccountName = (TextView) view.findViewById(R.id.personal_head_name);
		ChangeAccount = (TextView) view.findViewById(R.id.personal_head_change_account);
		ChangeAccount.setOnClickListener(new MyOnClickListener(MENU_PERSONAL_CHANGEACCOUNT));

		VolunteerBlock = (RelativeLayout) view.findViewById(R.id.rl_volunteer_block);
		SafeGuardBlock = (RelativeLayout) view.findViewById(R.id.rl_safeguard_block);
		WalletBlock = (RelativeLayout) view.findViewById(R.id.rl_wallet_block);
		CollectionBlock = (RelativeLayout) view.findViewById(R.id.rl_collection_block);
		FeedBackBlock = (RelativeLayout) view.findViewById(R.id.rl_feedback_block);
		PBusinessBlock = (RelativeLayout) view.findViewById(R.id.rl_preference_business_block);
		CantactSvrBlock = (RelativeLayout) view.findViewById(R.id.rl_cantactsvr_block);
		SettingBlock = (RelativeLayout) view.findViewById(R.id.rl_setting_block);

		mask_image.setOnClickListener(new MyOnClickListener(MENU_PERSONAL_INFO));
		VolunteerBlock.setOnClickListener(new MyOnClickListener(MENU_PERSONAL_VOLUNTEER));
		SafeGuardBlock.setOnClickListener(new MyOnClickListener(MENU_PERSONAL_SAFEGUARD));
		WalletBlock.setOnClickListener(new MyOnClickListener(MENU_PERSONAL_WALLET));
		CollectionBlock.setOnClickListener(new MyOnClickListener(MENU_PERSONAL_COLLECT));
		FeedBackBlock.setOnClickListener(new MyOnClickListener(MENU_PERSONAL_FEEDBACK));
		PBusinessBlock.setOnClickListener(new MyOnClickListener(MENU_PERSONAL_PREFERENCE_BUSINISS));
		CantactSvrBlock.setOnClickListener(new MyOnClickListener(MENU_PERSONAL_CANTACTSVR));
		SettingBlock.setOnClickListener(new MyOnClickListener(MENU_PERSONAL_SETTING));

	}

	/* imageloader部分 */
	private DisplayImageOptions options;
	private ImageLoader mImageLoader;

	/**
	 * imageloader 加载图片展示，的一些参数的设置 avatar_user_default 为默认图片
	 */
	@SuppressWarnings("deprecation")
	private void initDisplayOptions() {
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.loading_default_img) // 设置正在加载图片
				// .showImageOnLoading(R.drawable.ic_stub) //1.8.7新增
				.showImageForEmptyUri(R.drawable.drop_down_box_bg).showImageOnFail(R.drawable.loading_failed) // 设置加载失败图片
				.cacheInMemory(true).cacheOnDisc(true)
				// .displayer(new RoundedBitmapDisplayer(20))
				// //设置图片角度,0为方形，360为圆角
				.build();

		mImageLoader = ImageLoader.getInstance();
	}

	/**
	 * 处理view显示的数据
	 */
	private void ProcessData() {

		String path = Environment.getExternalStorageDirectory() + Constant.PARTTIMEJOB_HEAD + avatar.substring(avatar.lastIndexOf("/") + 1);
		if (Utils.getFilesExist(Environment.getExternalStorageDirectory() + Constant.PARTTIMEJOB_HEAD, avatar.substring(avatar.lastIndexOf("/") + 1))) {
			Bitmap mBp = Utils.getBitmapformpath(path);
			Bitmap mIcon = ThumbnailUtils.extractThumbnail(mBp, 120, 120);
			mask_image.setImageBitmap(mIcon);
		} else {
			if (avatar != null && !avatar.equals(""))
				mImageLoader.displayImage(avatar, mask_image, options);
		}
		//AccountName.setText("lucy");
		AccountName.setText(name);
	}

	/**
	 * 自定义ClickListener管理Click事件
	 * */
	public class MyOnClickListener implements OnClickListener {
		private int mtype = -1;// 默认为-1，什么也不做

		public MyOnClickListener(int type) {
			mtype = type;
		}

		@Override
		public void onClick(View v) {
			ManageClick(mtype);
		}
	}

	/**
	 * 通过判断type，管理click事件
	 * 
	 * @param mtype
	 */
	private void ManageClick(int mtype) {
		switch (mtype) {
		case MENU_PERSONAL_CHANGEACCOUNT:
			LoginStatus.getLoginStatus().setLogin(false);
			Utils.ClearPageFlagToShared(mContext);
			sharedPreferences.edit().clear().commit();
			ExitHx();
			Utils.intent2Class(mContext, LoginActivity.class);
			break;
		/** 个人信息 */
		case MENU_PERSONAL_INFO:
			Utils.intent2Class(mContext, Personal_Details_Activity.class);
			break;
		/** 任务 */
		case MENU_PERSONAL_TASK:
			Utils.intent2Class(mContext, Personal_Task_Portrait_Activity.class);
			break;
		/** 公益 */
		case MENU_PERSONAL_VOLUNTEER:
			Utils.intent2Class(mContext, VolunteerMainActivity.class);
			break;
		/** 保障 */
		case MENU_PERSONAL_SAFEGUARD:
			Utils.ShowToast(mContext, "暂无此功能！");
			// Utils.intent2Class(mContext, Personal_SafeGuard_Activity.class);
			break;
		/** 钱包 */
		case MENU_PERSONAL_WALLET:
			Utils.intent2Class(mContext, Personal_Wallet_Activity.class);
			break;
		/** 收藏 */
		case MENU_PERSONAL_COLLECT:
			Utils.intent2Class(mContext, Personal_Collection_Activity.class);
			break;
		/** 设置 */
		case MENU_PERSONAL_SETTING:
			Utils.intent2Class(mContext, Personal_Setting_Activity.class);
			break;

		/** 特惠商户 */
		case MENU_PERSONAL_PREFERENCE_BUSINISS:
			Utils.intent2Class(mContext, Personal_SpecialCustomer_Activity.class);
			break;
		/** 简历管理 */
		case MENU_PERSONAL_RESUME:
			Utils.intent2Class(mContext, Personal_Resume_Manage_Activity.class);
			break;
		/** 意见反馈 */
		case MENU_PERSONAL_FEEDBACK:
			// Utils.intent2Class(mContext,.class);
			break;
		/** 联系客服 */
		case MENU_PERSONAL_CANTACTSVR:
			Utils.intent2Class(mContext, Personal_SpecialCustomer_Activity.class);
			break;
		}
	}

	private void ExitHx() {
		EMChatManager.getInstance().logout(new EMCallBack() {
			@Override
			public void onError(int arg0, String arg1) {
			}

			@Override
			public void onProgress(int progress, String arg1) {
			}

			@Override
			public void onSuccess() {
				sharedPreferences.edit().clear().commit();
				MainFragment.mainFragment.finish();
			}

		});// 此方法为异步方法
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mImageLoader.clearMemoryCache();
		mImageLoader.clearDiscCache();
	}
}
