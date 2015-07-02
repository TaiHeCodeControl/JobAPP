package com.parttime.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.parttime.activity.ChatActivity;
import com.parttime.activity.Company_Details_Introduce_Activity;
import com.parttime.activity.Job_Issue_Activity;
import com.parttime.activity.Job_Relase_Resume;
import com.parttime.activity.Personal_Details_Activity;
import com.parttime.activity.Register_User;
import com.parttime.constant.Constant;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.service.LoginStatus;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;

/**
 * @function :盛放所有子fragment的主fragment
 * @author huxixi
 */
public class MainFragment extends FragmentActivity {

	public static FragmentActivity mainFragment;
	public static final int HOMEPAGER_REGISTBTN = 0;

	public static final int HOMEPAGER_INT = 1;
	public static final int JOBPAGER_INT = 2;
	public static final int VOLUNTEER_INT = 3;
	public static final int COMMUNICATION_INT = 4;
	public static final int PERSONAL_INT = 5;
	public static final int JOB_RELEASE_INT = 6;
	/**
	 * 主页面底部的textview
	 */
	private TextView homePager = null;
	private TextView jobPager = null;
	private TextView volunteerPager = null;
	private TextView communicationPager = null;
	private TextView personalPager = null;
	private TextView applyjobPager = null;
	private TextView companypager = null;
	private RelativeLayout personal_relative = null;
	private RelativeLayout company_relative = null;

	private ImageView home_back = null;
	/**
	 * 标题的textview
	 */
	private TextView titleText = null;
	/**
	 * 标题右边的textview
	 */
	private TextView rightText = null;
	private TextView rightTextTwo = null;

	private Context mContext = null;

	private ImageView unreadLabel;
	/**
	 * 企业icon上的点
	 */
	private static ImageView companydot;

	/**
	 * 主页的fragment
	 */
	private Fragment fragment_homepage;
	/**
	 * 工作的fragment
	 */
	private Fragment fragment_jobpage;
	/**
	 * 管理的fragment
	 */
	private Fragment fragment_companymanagerpage;
	/**
	 * 通讯的fragment
	 */
	private Fragment_CommunicationPage fragment_communicationpage;
	/**
	 * 个人的fragment
	 */
	private Fragment fragment_personalpage;
	/**
	 * 公益的fragment
	 */
	private Fragment fragment_volunteerpage;
	/**
	 * 求职的fragment
	 */
	private Fragment fragment_applyjobpage;
	/**
	 * 企业的fragment
	 */
	private Fragment fragment_companypage;

	/**
	 * fragment的事务
	 */
	FragmentTransaction transaction = null;
	private FragmentManager fragmentManager;
	private int back = 0;

	private String userflag = null;
	private SharedPreferences sharedPreferences;
	private PartTimeDB DB;
	/**
	 * 图片和背景颜色资源
	 */
	private static final int CardPressed = R.color.black;
	private static final int CardNormal = R.color.black;

	private static final int CardPressedColor = R.color.main_btn_press;
	private static final int CardNormalColor = R.color.white;

	private static final int HomePagePressed = R.drawable.home_pressed_pink;
	private static final int HomePageNormal = R.drawable.home_unpress;

	private static final int JobPagePressed = R.drawable.job_pressed_pink;
	private static final int JobPageNormal = R.drawable.job_unpress;

	private static final int applyJobPagePressed = R.drawable.job_pressed_pink;
	private static final int applyJobPageNormal = R.drawable.job_unpress;

	private static final int CommunicationPagePressed = R.drawable.community_pink;
	private static final int CommunicationPageNormal = R.drawable.communication_unpress;

	private static final int PersonalPagePressed = R.drawable.personal_pressed_pink;
	private static final int PersonalPageNormal = R.drawable.personal_unpress;

	private static final int CompanyPagePressed = R.drawable.company_pressed_pink;
	private static final int CompanyPageNormal = R.drawable.company_unpress;

	private static final int VolunteerPagePressed = R.drawable.volunteer_pressed_pink;
	private static final int VolunteerPageNormal = R.drawable.volunteer_unpress;
	private long userid = -1;
	private String avatar = "";
	private Fragment_CommunicationPage fragment_communication = null;

	private int currentTabIndex;
	private NewMessageBroadcastReceiver msgReceiver;

	private int PjobNum = 0;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// PartTimeApplication.getInstance().addActivity(this);
		setContentView(R.layout.mainfragment);
		mContext = this;
		mainFragment = this;
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		DB = PartTimeDB.getInstance(mContext);
		userid = sharedPreferences.getLong("userid", -1);
		PjobNum = sharedPreferences.getInt("pjobnum", 0);
		initfragment();
		initView();

		SharedPreferences mPreftips = getSharedPreferences("tips_show_flag_first", 0);
		if (mPreftips.getBoolean("first_login_tips_info", true)) {
			mPreftips.edit().putBoolean("first_login_tips_info", false).commit();
			CleanDownloadTips(mContext);
		}

		// 注册一个接收消息的BroadcastReceiver
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);

	}

	@Override
	protected void onStart() {
		super.onStart();
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				updateUnreadLabel();
			}

		});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		userflag = sharedPreferences.getString("userflag", Constant.USER_TYPE);
		if (userflag != null && userflag.equals("personal")) {

			jobPager.setVisibility(View.VISIBLE);
			applyjobPager.setVisibility(View.GONE);

			personal_relative.setVisibility(View.VISIBLE);
			company_relative.setVisibility(View.GONE);
		} else if (userflag != null && userflag.equals("company")) {
			jobPager.setVisibility(View.GONE);
			applyjobPager.setVisibility(View.VISIBLE);
			personal_relative.setVisibility(View.GONE);
			company_relative.setVisibility(View.VISIBLE);
		}

		fragmentManager = getSupportFragmentManager();
		transaction = fragmentManager.beginTransaction();

		int pageflag = Utils.getPageFlagFromShared(mContext);
		switch (pageflag) {
		case Constant.FINISH_ACTIVITY_FLAG_HOMEPAGE:
			ChangeCardBack(HOMEPAGER_INT);
			HideRegistBtn();
			titleText.setText(getResources().getString(R.string.home_pager));
			transaction.replace(R.id.home_center, fragment_homepage);
			break;
		case Constant.FINISH_ACTIVITY_FLAG_JOB:
			HideRegistBtn();
			if (userflag != null && userflag.equals("personal")) {
				transaction.replace(R.id.home_center, fragment_jobpage);
			} else {
				transaction.replace(R.id.home_center, fragment_applyjobpage);

			}
			break;
		case Constant.FINISH_ACTIVITY_FLAG_MANAGER:
			HideRegistBtn();
			if (userflag != null && userflag.equals("personal")) {
				transaction.replace(R.id.home_center, fragment_volunteerpage);
			} else {
				transaction.replace(R.id.home_center, fragment_companymanagerpage);
			}
			break;
		case Constant.FINISH_ACTIVITY_FLAG_COMMUNICATION:
			transaction.replace(R.id.home_center, fragment_communicationpage);

			break;
		case Constant.FINISH_ACTIVITY_FLAG_PERSONAL:
		case Constant.FINISH_ACTIVITY_FLAG_COMPANY:
			ShowRegistBtn();
			if (userflag != null && userflag.equals("personal")) {
				transaction.replace(R.id.home_center, fragment_personalpage);
			} else {
				transaction.replace(R.id.home_center, fragment_companypage);
			}
			break;
		default:
			HideRegistBtn();
			transaction.replace(R.id.home_center, fragment_homepage);
			break;
		}

		// transaction.addToBackStack(null);
		transaction.commit();

	}

	/**
	 * 初始化所有的子页面的fragment
	 */
	private void initfragment() {
		fragment_homepage = Fragment_HomePage.newInstance(mContext);
		fragment_jobpage = Fragment_JobPage.newInstance(mContext);
		fragment_companymanagerpage = Fragment_CompanyManagerPage.newInstance(mContext);
		fragment_communicationpage = Fragment_CommunicationPage.newInstance(mContext);
		fragment_personalpage = Fragment_PersonalPage.newInstance(mContext);
		fragment_volunteerpage = Fragment_PersonalManage_Page.newInstance(mContext);
		fragment_applyjobpage = Fragement_ApplyJobPage.newInstance(mContext);
		fragment_companypage = Fragment_CompanyPage.newInstance(mContext);
	}

	/**
	 * 初始化
	 */
	private void initView() {

		titleText = (TextView) findViewById(R.id.title_text);
		rightText = (TextView) findViewById(R.id.right_text);
		rightTextTwo = (TextView) findViewById(R.id.right_text_two);
		rightTextTwo.setOnClickListener(new MyOnClickListener(HOMEPAGER_REGISTBTN));
		homePager = (TextView) findViewById(R.id.homepage);

		jobPager = (TextView) findViewById(R.id.jobpage);
		applyjobPager = (TextView) findViewById(R.id.applypage);
		volunteerPager = (TextView) findViewById(R.id.volunteerpage);
		communicationPager = (TextView) findViewById(R.id.communicationpage);
		personalPager = (TextView) findViewById(R.id.personalpage);
		companypager = (TextView) findViewById(R.id.companypage);

		company_relative = (RelativeLayout) findViewById(R.id.company_relative);
		personal_relative = (RelativeLayout) findViewById(R.id.personal_relative);

		unreadLabel = (ImageView) findViewById(R.id.unread_msg_number);
		companydot = (ImageView) findViewById(R.id.company_dot);

		home_back = (ImageView) findViewById(R.id.home_back);
		home_back.setVisibility(View.GONE);

		ChangeCardBack(HOMEPAGER_INT);
		titleText.setText(getResources().getString(R.string.home_pager));

		homePager.setOnClickListener(new MyOnClickListener(HOMEPAGER_INT));
		jobPager.setOnClickListener(new MyOnClickListener(JOBPAGER_INT));
		applyjobPager.setOnClickListener(new MyOnClickListener(JOBPAGER_INT));
		volunteerPager.setOnClickListener(new MyOnClickListener(VOLUNTEER_INT));
		communicationPager.setOnClickListener(new MyOnClickListener(COMMUNICATION_INT));
		personalPager.setOnClickListener(new MyOnClickListener(PERSONAL_INT));
		companypager.setOnClickListener(new MyOnClickListener(PERSONAL_INT));
		rightText.setOnClickListener(new MyOnClickListener(JOB_RELEASE_INT));

		if (Submit.isNetworkAvailable(mContext)) {
			if (userid != -1) {
				new GetPersonalInfoAsynTask().execute(userid);
				// new GetPJobListAsynTask().execute(10,(int)userid);
			}
		} else {
			Utils.ShowToast(mContext, "没有可用网络！");
		}
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
		public void onClick(View arg0) {
			if (!LoginStatus.getLoginStatus().isLogin()) {
				if (mtype != HOMEPAGER_INT && mtype != JOBPAGER_INT) {
					// Toast.makeText(mContext, "请先登录", 0).show();
					LoginStatus.getLoginStatus().showDialog(MainFragment.this);
					return;
				}
			}
			switch (mtype) {
			case HOMEPAGER_INT:
				ChangeCardBack(HOMEPAGER_INT);
				HideRegistBtn();
				rightText.setVisibility(View.GONE);
				titleText.setText(getResources().getString(R.string.home_pager));
				if (!fragment_homepage.isAdded()) {
					transaction = fragmentManager.beginTransaction();
					transaction.replace(R.id.home_center, fragment_homepage);
					transaction.commit();
				}
				break;
			case JOBPAGER_INT:
				ChangeCardBack(JOBPAGER_INT);
				HideRegistBtn();
				if (userflag != null && userflag.equals("personal")) {
					titleText.setText(getResources().getString(R.string.home_job));
					rightText.setVisibility(View.VISIBLE);
					rightText.setText(getResources().getString(R.string.home_job_release));

					if (!fragment_jobpage.isAdded()) {
						transaction = fragmentManager.beginTransaction();
						transaction.replace(R.id.home_center, fragment_jobpage);
						transaction.commit();
					}
				} else if (userflag != null && userflag.equals("company")) {
					titleText.setText(getResources().getString(R.string.home_apply_job));
					rightText.setVisibility(View.VISIBLE);
					rightText.setText(getResources().getString(R.string.home_job_issue));
					if (!fragment_jobpage.isAdded()) {
						transaction = fragmentManager.beginTransaction();
						transaction.replace(R.id.home_center, fragment_applyjobpage);
						transaction.commit();
					}
				}

				break;
			case VOLUNTEER_INT:
				HideRegistBtn();
				ChangeCardBack(VOLUNTEER_INT);
				rightText.setVisibility(View.GONE);
				titleText.setText(getResources().getString(R.string.home_volunteer));
				// if (!fragment_volunteerpage.isAdded()) {
				// transaction = fragmentManager.beginTransaction();
				// transaction.replace(R.id.home_center,
				// fragment_volunteerpage);
				// transaction.commit();
				// }

				if (userflag != null && userflag.equals("personal")) {

					if (!fragment_volunteerpage.isAdded()) {
						transaction = fragmentManager.beginTransaction();
						transaction.replace(R.id.home_center, fragment_volunteerpage);
						transaction.commit();
					}
				} else if (userflag != null && userflag.equals("company")) {
					if (!fragment_companymanagerpage.isAdded()) {
						transaction = fragmentManager.beginTransaction();
						transaction.replace(R.id.home_center, fragment_companymanagerpage);
						transaction.commit();
					}
				}

				break;
			case COMMUNICATION_INT:
				HideRegistBtn();
				ChangeCardBack(COMMUNICATION_INT);
				rightText.setVisibility(View.GONE);
				titleText.setText(getResources().getString(R.string.home_communication));
				if (!fragment_communicationpage.isAdded()) {
					transaction = fragmentManager.beginTransaction();
					transaction.replace(R.id.home_center, fragment_communicationpage);
					transaction.commit();
				}
				break;
			case PERSONAL_INT:
				ShowRegistBtn();
				ChangeCardBack(PERSONAL_INT);
				if (userflag != null && userflag.equals("personal")) {
					titleText.setText(getResources().getString(R.string.home_personal));
					if (!fragment_personalpage.isAdded()) {
						transaction = fragmentManager.beginTransaction();
						transaction.replace(R.id.home_center, fragment_personalpage);
						transaction.commit();
					}
				} else if (userflag != null && userflag.equals("company")) {
					titleText.setText(getResources().getString(R.string.home_company));
					if (!fragment_personalpage.isAdded()) {
						transaction = fragmentManager.beginTransaction();
						transaction.replace(R.id.home_center, fragment_companypage);
						transaction.commit();
					}
				}

				break;
			case JOB_RELEASE_INT:
				Intent intent = null;
				HideRegistBtn();
				if (userflag != null && userflag.equals("personal")) {
					intent = new Intent(MainFragment.this, Job_Relase_Resume.class);
				} else if (userflag != null && userflag.equals("company")) {
					intent = new Intent(MainFragment.this, Job_Issue_Activity.class);

				}
				MainFragment.this.startActivity(intent);
				break;

			case HOMEPAGER_REGISTBTN:
				Utils.intent2Class(mContext, Register_User.class);
				break;
			}
		}
	}

	/**
	 * 根据每一个不同的选项，改变选中的状态，和图片
	 * 
	 * @param type
	 */
	@SuppressLint("NewApi")
	public void ChangeCardBack(int type) {
		switch (type) {
		case HOMEPAGER_INT:
			homePager.setTextColor(getResources().getColor(CardPressedColor));
			applyjobPager.setTextColor(getResources().getColor(CardNormalColor));
			jobPager.setTextColor(getResources().getColor(CardNormalColor));
			volunteerPager.setTextColor(getResources().getColor(CardNormalColor));
			communicationPager.setTextColor(getResources().getColor(CardNormalColor));
			personalPager.setTextColor(getResources().getColor(CardNormalColor));
			companypager.setTextColor(getResources().getColor(CardNormalColor));

			homePager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(HomePagePressed), null, null);
			jobPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(JobPageNormal), null, null);
			applyjobPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(applyJobPageNormal), null, null);
			volunteerPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(VolunteerPageNormal), null, null);
			communicationPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(CommunicationPageNormal), null, null);
			personalPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(PersonalPageNormal), null, null);
			companypager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(CompanyPageNormal), null, null);
			break;
		case JOBPAGER_INT:
			homePager.setBackgroundColor(getResources().getColor(CardNormal));

			jobPager.setBackgroundColor(getResources().getColor(CardPressed));
			applyjobPager.setBackgroundColor(getResources().getColor(CardPressed));
			volunteerPager.setBackgroundColor(getResources().getColor(CardNormal));
			communicationPager.setBackgroundColor(getResources().getColor(CardNormal));
			personalPager.setBackgroundColor(getResources().getColor(CardNormal));
			companypager.setBackgroundColor(getResources().getColor(CardNormal));

			homePager.setTextColor(getResources().getColor(CardNormalColor));
			jobPager.setTextColor(getResources().getColor(CardPressedColor));
			applyjobPager.setTextColor(getResources().getColor(CardPressedColor));
			volunteerPager.setTextColor(getResources().getColor(CardNormalColor));
			communicationPager.setTextColor(getResources().getColor(CardNormalColor));
			personalPager.setTextColor(getResources().getColor(CardNormalColor));
			companypager.setTextColor(getResources().getColor(CardNormalColor));

			homePager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(HomePageNormal), null, null);
			jobPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(JobPagePressed), null, null);
			applyjobPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(applyJobPagePressed), null, null);
			volunteerPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(VolunteerPageNormal), null, null);
			communicationPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(CommunicationPageNormal), null, null);
			personalPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(PersonalPageNormal), null, null);
			companypager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(CompanyPageNormal), null, null);
			break;
		case VOLUNTEER_INT:
			homePager.setBackgroundColor(getResources().getColor(CardNormal));
			jobPager.setBackgroundColor(getResources().getColor(CardNormal));
			volunteerPager.setBackgroundColor(getResources().getColor(CardPressed));
			communicationPager.setBackgroundColor(getResources().getColor(CardNormal));
			personalPager.setBackgroundColor(getResources().getColor(CardNormal));
			companypager.setBackgroundColor(getResources().getColor(CardNormal));

			homePager.setTextColor(getResources().getColor(CardNormalColor));
			applyjobPager.setTextColor(getResources().getColor(CardNormalColor));
			jobPager.setTextColor(getResources().getColor(CardNormalColor));
			volunteerPager.setTextColor(getResources().getColor(CardPressedColor));
			communicationPager.setTextColor(getResources().getColor(CardNormalColor));
			personalPager.setTextColor(getResources().getColor(CardNormalColor));
			companypager.setTextColor(getResources().getColor(CardNormalColor));

			homePager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(HomePageNormal), null, null);
			jobPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(JobPageNormal), null, null);
			applyjobPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(applyJobPageNormal), null, null);
			volunteerPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(VolunteerPagePressed), null, null);
			communicationPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(CommunicationPageNormal), null, null);
			personalPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(PersonalPageNormal), null, null);
			companypager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(CompanyPageNormal), null, null);
			break;
		case COMMUNICATION_INT:
			currentTabIndex = 1;
			homePager.setBackgroundColor(getResources().getColor(CardNormal));
			jobPager.setBackgroundColor(getResources().getColor(CardNormal));
			volunteerPager.setBackgroundColor(getResources().getColor(CardNormal));
			communicationPager.setBackgroundColor(getResources().getColor(CardPressed));
			personalPager.setBackgroundColor(getResources().getColor(CardNormal));
			companypager.setBackgroundColor(getResources().getColor(CardNormal));

			homePager.setTextColor(getResources().getColor(CardNormalColor));
			jobPager.setTextColor(getResources().getColor(CardNormalColor));
			applyjobPager.setTextColor(getResources().getColor(CardNormalColor));
			applyjobPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(applyJobPageNormal), null, null);
			volunteerPager.setTextColor(getResources().getColor(CardNormalColor));
			communicationPager.setTextColor(getResources().getColor(CardPressedColor));
			personalPager.setTextColor(getResources().getColor(CardNormalColor));
			companypager.setTextColor(getResources().getColor(CardNormalColor));

			homePager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(HomePageNormal), null, null);
			jobPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(JobPageNormal), null, null);
			volunteerPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(VolunteerPageNormal), null, null);
			communicationPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(CommunicationPagePressed), null, null);
			personalPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(PersonalPageNormal), null, null);
			companypager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(CompanyPageNormal), null, null);
			break;
		case PERSONAL_INT:
			homePager.setBackgroundColor(getResources().getColor(CardNormal));
			jobPager.setBackgroundColor(getResources().getColor(CardNormal));
			volunteerPager.setBackgroundColor(getResources().getColor(CardNormal));
			communicationPager.setBackgroundColor(getResources().getColor(CardNormal));
			personalPager.setBackgroundColor(getResources().getColor(CardPressed));
			companypager.setBackgroundColor(getResources().getColor(CardPressed));

			homePager.setTextColor(getResources().getColor(CardNormalColor));
			jobPager.setTextColor(getResources().getColor(CardNormalColor));
			applyjobPager.setTextColor(getResources().getColor(CardNormalColor));
			volunteerPager.setTextColor(getResources().getColor(CardNormalColor));
			communicationPager.setTextColor(getResources().getColor(CardNormalColor));
			personalPager.setTextColor(getResources().getColor(CardPressedColor));
			companypager.setTextColor(getResources().getColor(CardPressedColor));

			homePager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(HomePageNormal), null, null);
			jobPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(JobPageNormal), null, null);
			applyjobPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(applyJobPageNormal), null, null);
			volunteerPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(VolunteerPageNormal), null, null);
			communicationPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(CommunicationPageNormal), null, null);
			personalPager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(PersonalPagePressed), null, null);
			companypager.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(CompanyPagePressed), null, null);
			break;
		}
	}

	/**
	 * 把整形的drawable变成Drawable
	 * 
	 * @param drawable_int
	 * @return
	 */
	@SuppressLint("Override")
	public Drawable getDrawable(int drawable_int) {
		Drawable drawable = getResources().getDrawable(drawable_int);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		return drawable;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			back++;
			if (back == 1) {

				Toast.makeText(this, "再按一次退出蛋壳儿兼职", 5).show();

				new Handler().postDelayed(new Runnable() {
					public void run() {
						back = 0;
					}

				}, 4000);
				return true;
			}
			if (back == 2) {
				Utils.ClearPageFlagToShared(mContext);
				ExitHx();

				return true;
			}
			break;
		}
		return false;
	}

	private void ExitHx() {
		EMChatManager.getInstance().logout(new EMCallBack() {

			@Override
			public void onError(int arg0, String arg1) {
				Log.e("hu", "onError   " + arg0 + "   name   " + arg1);

			}

			@Override
			public void onProgress(int progress, String arg1) {
				Log.e("hu", "progress   " + progress + "   name   " + arg1);

			}

			@Override
			public void onSuccess() {
				Log.e("hu", "success exit");
				MainFragment.this.finish();
			}

		});// 此方法为异步方法
	}

	/**
	 * 获取个人信息
	 */
	public class GetPersonalInfoAsynTask extends AsyncTask<Long, Void, Boolean> {

		int errcode = -1;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Long... params) {
			try {
				errcode = SvrOperation.GetPersonalInfo(mContext, params[0]);

				if (errcode == SvrInfo.SVR_RESULT_SUCCESS)
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
		}
	}

	public void finishFragment() {
		MainFragment.this.finish();
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		return unreadMsgCountTotal;
	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			unreadLabel.setVisibility(View.VISIBLE);
		} else {
			unreadLabel.setVisibility(View.GONE);
		}
	}

	/**
	 * 新消息广播接收者
	 * 
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看

			String from = intent.getStringExtra("from");
			// 消息id
			String msgId = intent.getStringExtra("msgid");
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			// 2014-10-22 修复在某些机器上，在聊天页面对方发消息过来时不立即显示内容的bug
			if (ChatActivity.activityInstance != null) {
				if (message.getChatType() == ChatType.GroupChat) {
					if (message.getTo().equals(ChatActivity.activityInstance.getToChatUsername()))
						return;
				} else {
					if (from.equals(ChatActivity.activityInstance.getToChatUsername()))
						return;
				}
			}

			// 注销广播接收者，否则在ChatActivity中会收到这个广播
			abortBroadcast();

			// notifyNewMessage(message);

			// 刷新bottom bar消息未读数
			updateUnreadLabel();
			if (currentTabIndex == 1) {
				// 当前页面如果为聊天历史页面，刷新此页面
				if (fragment_communicationpage != null) {
					fragment_communicationpage.refresh();
				}
			}

		}
	}

	/**
	 * 获取求助列表
	 */
	public class GetPJobListAsynTask extends AsyncTask<Integer, Void, Boolean> {

		long errcode = -1;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				errcode = SvrOperation.GetCompanyPJobInforeFreshTask(mContext, params[0], params[1]);

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
			int pjobnum = DB.getCompanyPJobInfoNum(mContext);
			if (pjobnum > PjobNum) {
				ShowCompanyRedDot();
			}
		}
	}

	public static void ShowCompanyRedDot() {
		companydot.setVisibility(View.VISIBLE);
	}

	public static void HideCompanyRedDot() {
		companydot.setVisibility(View.GONE);
	}

	/**
	 * 设置提醒，提醒完善个人信息
	 * 
	 * @param context
	 * @param name
	 */
	public void CleanDownloadTips(final Context context) {
		new AlertDialog.Builder(context).setTitle("注意").setMessage("为了方便您更好的使用，请您完善您的个人信息^_^").setPositiveButton(getResources().getText(R.string.apk_tips_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Utils.CommitPageFlagToShared(context, Constant.FINISH_ACTIVITY_FLAG_HOMEPAGE);
				userflag = sharedPreferences.getString("userflag", "");
				if (userflag != null && userflag.equals("personal")) {
					Utils.intent2Class(context, Personal_Details_Activity.class);
				} else if (userflag != null && userflag.equals("company")) {
					Utils.intent2Class(context, Company_Details_Introduce_Activity.class);
				}
			}
		}).setNegativeButton(getText(R.string.apk_tips_cancel), null).show();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(msgReceiver);
		} catch (Exception e) {
		}
	}

	public void ShowRegistBtn() {
		if (rightTextTwo != null)
			rightTextTwo.setVisibility(View.VISIBLE);
	}

	public void HideRegistBtn() {
		if (rightTextTwo != null)
			rightTextTwo.setVisibility(View.GONE);
	}
}
