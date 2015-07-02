package com.parttime.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parttime.adapter.PersonalTaskAdapter;
import com.parttime.constant.Constant;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.SignStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.MediaProcess;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;
import com.parttime.view.MyImageSpinner;

/**
 * 
 * @author 灰色的寂寞
 * @date 2015-1-14
 * @time 12:51
 * @function 个人中的任务页面
 *
 */
public class Personal_Task_Portrait_Activity extends Activity {
	private Context mContext;

	/**view 部分****************************************************/
	/* 标题部分*/
	private TextView CenterTitle;
	private ImageView LeftBackBtn;
	
	/*-------------------------------签到部分--------------------------------*/
	private RelativeLayout SignOfBlock;
	/**标题部分*/
	private RelativeLayout SignTitleBlock;
	/**签到工作标题*/
	private TextView SignTitleName;
	/**签到天数*/
	private MyImageSpinner SignDayNum;

	/** 签到内容 */
	/*标头线*/
	private View SignHeaderLine;
	/*签到第一天*/
	private LinearLayout SignFirstDayBlock;
	private View SignFirstDayLine;
	private ImageView SignFirstDayPoint,SignOnFirstDayImage,SignOffFirstDayImage;
	/*签到第二天*/
	private LinearLayout SignSecondDayBlock;
	private View SignSecondDayLine;
	private ImageView SignSecondDayPoint,SignOnSecondDayImage,SignOffSecondDayImage;
	/*签到第三天*/
	private LinearLayout SignThirdDayBlock;
	private View SignThirdDayLine;
	private ImageView SignThirdDayPoint,SignOnThirdDayImage,SignOffThirdDayImage;
	/*签到第四天*/
	private LinearLayout SignFourthDayBlock;
	private View SignFourthDayLine;
	private ImageView SignFourthDayPoint,SignOnFourthDayImage,SignOffFourthDayImage;
	/*签到第五天*/
	private LinearLayout SignFifthDayBlock;
	private View SignFifthDayLine;
	private ImageView SignFifthDayPoint,SignOnFifthDayImage,SignOffFifthDayImage;
	/*签到第六天*/
	private LinearLayout SignSixthDayBlock;
	private View SignSixthDayLine;
	private ImageView SignSixthDayPoint,SignOnSixthDayImage,SignOffSixthDayImage;
	/*签到第七天*/
	private LinearLayout SignSeventhDayBlock;
	private View SignSeventhDayLine;
	private ImageView SignSeventhDayPoint,SignOnSeventhDayImage,SignOffSeventhDayImage;
	/*签到第八天*/
	private LinearLayout SignEigthDayBlock;
	private View SignEigthDayLine;
	private ImageView SignEigthDayPoint,SignOnEigthDayImage,SignOffEigthDayImage;
	/*签到第九天*/
	private LinearLayout SignNinthDayBlock;
	private View SignNinthDayLine;
	private ImageView SignNinthDayPoint,SignOnNinthDayImage,SignOffNinthDayImage;
	
	
	private TextView FinishJobBtn;
	
	
	
	/*-------------------------------签到部分--------------------------------*/
	
	
	/**数据 部分****************************************************/
	private List<String> mNumList=null;
	private String taskTypeString="";
	private String DaysNumber="";
	private Bitmap mBitmap;
	private PersonalTaskAdapter mPersonalTaskAdapter;
	/**常量部分****************************************************/
	/* 返回*/
	private static final int CLICK_TYPE_BACKBTN=100;
	/*选图部分*/
	private final static int MENU_PHOTO = 101;
	private final static int DATE_DIALOG_ID = 102;
	/*任务状态*/
	private final static int TASK_APPLAYED = 103;//已申请
	private final static int TASK_SIGNING = 104;//进行中
	private final static int TASK_EMPLOY = 105;//被录用
	private final static int TASK_FINISHED = 106;//已完成
	
	/*天数*/
	//签到
	private final static int TASK_DAY_ONE_ON = 1;//1天
	private final static int TASK_DAY_TWO_ON = 2;//2天
	private final static int TASK_DAY_THREE_ON = 3;//3天
	private final static int TASK_DAY_FOUR_ON = 4;//4天
	private final static int TASK_DAY_FIVE_ON = 5;//5天
	private final static int TASK_DAY_SIX_ON = 6;//6天
	private final static int TASK_DAY_SEVEN_ON = 7;//7天
	private final static int TASK_DAY_EIGHT_ON = 8;//8天
	private final static int TASK_DAY_NINE_ON = 9;//9天
	//签退
	private final static int TASK_DAY_OFFE_OFF = 10;//1天
	private final static int TASK_DAY_TWO_OFF = 11;//2天
	private final static int TASK_DAY_THREE_OFF = 12;//3天
	private final static int TASK_DAY_FOUR_OFF = 13;//4天
	private final static int TASK_DAY_FIVE_OFF = 14;//5天
	private final static int TASK_DAY_SIX_OFF = 15;//6天
	private final static int TASK_DAY_SEVEN_OFF = 16;//7天
	private final static int TASK_DAY_EIGHT_OFF = 17;//8天
	private final static int TASK_DAY_NINE_OFF = 18;//9天

	
	
	private final static int TASK_FINISH_JOB_CLICK = 19;//完成工作按钮的监听
	//签到状态
	private boolean TASK_DAY_ONE= false;//1天
	private boolean TASK_DAY_TWO= false;//2天
	private boolean TASK_DAY_THREE= false;//3天
	private boolean TASK_DAY_FOUR= false;//4天
	private boolean TASK_DAY_FIVE= false;//5天
	private boolean TASK_DAY_SIX= false;//6天
	private boolean TASK_DAY_SEVEN= false;//7天
	private boolean TASK_DAY_EIGHT= false;//8天
	private boolean TASK_DAY_NINE= false;//9天
	
	private final static int SignedColor=R.color.btn_green;
	private final static int UnSignedColor=R.color.btn_green;
	
	private final static int SignedPoint=R.drawable.signed_point_icon;
	private final static int UnSignedPoint=R.drawable.signed_point_icon;
	
	private final static int DefaultSignedPoint=R.drawable.signed_on_default;
	private final static int DefaultUnSignedPoint=R.drawable.signed_off_default;
	/*等待框部分*/
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;
	
	private int hireid=-1;
	private SharedPreferences mshared;
	private String HiredName ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal_task_layout);
		mContext = this;
		mshared =PreferenceManager.getDefaultSharedPreferences(mContext);
		mNumList = Utils.StringArray2List(mContext, R.array.sing_day_num);
		initDisplayOptions();
		
//		if(Submit.isNetworkAvailable(mContext)){
//			new GetHiredListInfoAsynTask().execute();
//			}else{
//				Utils.ShowToast(mContext, "没有可用网络！");
//			}
		Intent intent = getIntent();
		hireid = intent.getIntExtra("hire_id", -1);
		HiredName = intent.getStringExtra("hire_name");
		initViews();
		initData();
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		ProcessPointShow();
		
	}

	private void initViews() {
		/** 标题部分*/
		CenterTitle = (TextView)findViewById(R.id.title_text);
		LeftBackBtn = (ImageView)findViewById(R.id.home_back);
		CenterTitle.setText("任务");
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));

		/* 等待对话框部分 */
		WaitingText = (TextView)findViewById(R.id.Waiting_text);
		WaitingDlg  =(RelativeLayout)findViewById(R.id.Waiting_dlg);
		/*-------------------------------签到部分--------------------------------*/
		/**签到部分*/
		SignOfBlock = (RelativeLayout)findViewById(R.id.task_sign_block);
		/**标题部分*/
		SignTitleBlock = (RelativeLayout)findViewById(R.id.task_sign_content_block);
		/**签到工作标题*/
		SignTitleName = (TextView) findViewById(R.id.task_sign_title);
		/**签到天数*/
		SignDayNum = (MyImageSpinner) findViewById(R.id.task_sign_type);

		/** 签到内容 */
		/*头部*/
		SignHeaderLine =(View)findViewById(R.id.sign_header_line);
		SignHeaderLine.setBackgroundColor(getResources().getColor(SignedColor));
		/*第一天*/
		SignFirstDayBlock=(LinearLayout)findViewById(R.id.sign_first_day_portrait_block);
		SignFirstDayLine =(View)findViewById(R.id.sign_first_day_portrait_line);
		SignFirstDayPoint= (ImageView) findViewById(R.id.sign_first_day_portrait_point);
		SignOnFirstDayImage= (ImageView) findViewById(R.id.sign_on_first_day_portrait_image);
		SignOffFirstDayImage = (ImageView) findViewById(R.id.sign_off_first_day_portrait_image);
		/*第二天*/
		SignSecondDayBlock=(LinearLayout)findViewById(R.id.sign_second_day_portrait_block);
		SignSecondDayLine =(View)findViewById(R.id.sign_second_day_portrait_line);
		SignSecondDayPoint= (ImageView) findViewById(R.id.sign_second_day_portrait_point);
		SignOnSecondDayImage= (ImageView) findViewById(R.id.sign_on_second_day_portrait_image);
		SignOffSecondDayImage = (ImageView) findViewById(R.id.sign_off_second_day_portrait_image);
		/*第三天*/
		SignThirdDayBlock=(LinearLayout)findViewById(R.id.sign_third_day_portrait_block);
		SignThirdDayLine =(View)findViewById(R.id.sign_third_day_portrait_line);
		SignThirdDayPoint= (ImageView) findViewById(R.id.sign_third_day_portrait_point);
		SignOnThirdDayImage= (ImageView) findViewById(R.id.sign_on_third_day_portrait_image);
		SignOffThirdDayImage = (ImageView) findViewById(R.id.sign_off_third_day_portrait_image);
		/*第四天*/
		SignFourthDayBlock=(LinearLayout)findViewById(R.id.sign_fourth_day_portrait_block);
		SignFourthDayLine =(View)findViewById(R.id.sign_fourth_day_portrait_line);
		SignFourthDayPoint= (ImageView) findViewById(R.id.sign_fourth_day_portrait_point);
		SignOnFourthDayImage= (ImageView) findViewById(R.id.sign_on_fourth_day_portrait_image);
		SignOffFourthDayImage = (ImageView) findViewById(R.id.sign_off_fourth_day_portrait_image);
		/*第五天*/
		SignFifthDayBlock=(LinearLayout)findViewById(R.id.sign_fifth_day_portrait_block);
		SignFifthDayLine =(View)findViewById(R.id.sign_fifth_day_portrait_line);
		SignFifthDayPoint= (ImageView) findViewById(R.id.sign_fifth_day_portrait_point);
		SignOnFifthDayImage= (ImageView) findViewById(R.id.sign_on_fifth_day_portrait_image);
		SignOffFifthDayImage = (ImageView) findViewById(R.id.sign_off_fifth_day_portrait_image);
		/*第六天*/
		SignSixthDayBlock=(LinearLayout)findViewById(R.id.sign_sixth_day_portrait_block);
		SignSixthDayLine =(View)findViewById(R.id.sign_sixth_day_portrait_line);
		SignSixthDayPoint= (ImageView) findViewById(R.id.sign_sixth_day_portrait_point);
		SignOnSixthDayImage= (ImageView) findViewById(R.id.sign_on_sixth_day_portrait_image);
		SignOffSixthDayImage = (ImageView) findViewById(R.id.sign_off_sixth_day_portrait_image);
		/*第七天*/
		SignSeventhDayBlock=(LinearLayout)findViewById(R.id.sign_seventh_day_portrait_block);
		SignSeventhDayLine =(View)findViewById(R.id.sign_seventh_day_portrait_line);
		SignSeventhDayPoint= (ImageView) findViewById(R.id.sign_seventh_day_portrait_point);
		SignOnSeventhDayImage= (ImageView) findViewById(R.id.sign_on_seventh_day_portrait_image);
		SignOffSeventhDayImage = (ImageView) findViewById(R.id.sign_off_seventh_day_portrait_image);
		/*第八天*/
		SignEigthDayBlock=(LinearLayout)findViewById(R.id.sign_eigth_day_portrait_block);
		SignEigthDayLine =(View)findViewById(R.id.sign_eigth_day_portrait_line);
		SignEigthDayPoint= (ImageView) findViewById(R.id.sign_eigth_day_portrait_point);
		SignOnEigthDayImage= (ImageView) findViewById(R.id.sign_on_eigth_day_portrait_image);
		SignOffEigthDayImage = (ImageView) findViewById(R.id.sign_off_eigth_day_portrait_image);
		/*第九天*/
		SignNinthDayBlock=(LinearLayout)findViewById(R.id.sign_ninth_day_portrait_block);
		SignNinthDayLine =(View)findViewById(R.id.sign_ninth_day_portrait_line);
		SignNinthDayPoint= (ImageView) findViewById(R.id.sign_ninth_day_portrait_point);
		SignOnNinthDayImage= (ImageView) findViewById(R.id.sign_on_ninth_day_portrait_image);
		SignOffNinthDayImage = (ImageView) findViewById(R.id.sign_off_ninth_day_portrait_image);
		
		
		SignOnFirstDayImage.setOnClickListener(new MyOnClickListener(TASK_DAY_ONE_ON));
		SignOnSecondDayImage.setOnClickListener(new MyOnClickListener(TASK_DAY_TWO_ON));
		SignOnThirdDayImage.setOnClickListener(new MyOnClickListener(TASK_DAY_THREE_ON));
		SignOnFourthDayImage.setOnClickListener(new MyOnClickListener(TASK_DAY_FOUR_ON));
		SignOnFifthDayImage.setOnClickListener(new MyOnClickListener(TASK_DAY_FIVE_ON));
		SignOnSixthDayImage.setOnClickListener(new MyOnClickListener(TASK_DAY_SIX_ON));
		SignOnSeventhDayImage.setOnClickListener(new MyOnClickListener(TASK_DAY_SEVEN_ON));
		SignOnEigthDayImage.setOnClickListener(new MyOnClickListener(TASK_DAY_EIGHT_ON));
		SignOnNinthDayImage.setOnClickListener(new MyOnClickListener(TASK_DAY_NINE_ON));
		
		SignOffFirstDayImage.setOnClickListener(new MyOnClickListener(TASK_DAY_OFFE_OFF));
		SignOffSecondDayImage.setOnClickListener(new MyOnClickListener(TASK_DAY_TWO_OFF));
		SignOffThirdDayImage.setOnClickListener(new MyOnClickListener(TASK_DAY_THREE_OFF));
		SignOffFourthDayImage.setOnClickListener(new MyOnClickListener(TASK_DAY_FOUR_OFF));
		SignOffFifthDayImage.setOnClickListener(new MyOnClickListener(TASK_DAY_FIVE_OFF));
		SignOffSixthDayImage.setOnClickListener(new MyOnClickListener(TASK_DAY_SIX_OFF));
		SignOffSeventhDayImage.setOnClickListener(new MyOnClickListener(TASK_DAY_SEVEN_OFF));
		SignOffEigthDayImage.setOnClickListener(new MyOnClickListener(TASK_DAY_EIGHT_OFF));
		SignOffNinthDayImage.setOnClickListener(new MyOnClickListener(TASK_DAY_NINE_OFF));
		
		
		FinishJobBtn = (TextView) findViewById(R.id.finish_job_btn);
		FinishJobBtn.setOnClickListener(new MyOnClickListener(TASK_FINISH_JOB_CLICK));

	}
	
	
	private void initData() {
		ArrayAdapter<String> numadapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,mNumList); 
		SignDayNum .setAdapter(numadapter);
		SignDayNum .setOnItemSelectedListener(new itemlistener_number());
		
		viewhandler.sendEmptyMessage(TASK_SIGNING);
		SignDayNum.setSelection(0);
		InitImageShow();
		PrecessSignInfo("9天");
	}
	
	/**
	 * 监听Spinner的选中事件
	 */
	public class itemlistener_number implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> adapter, View view, int position,long id) {
			//设置Spinner的字体大小
			// TextView tv=(TextView)view;
			// tv.setTextSize(15.0f);
			DaysNumber = mNumList.get(position);
			PrecessSignInfo(DaysNumber);
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapter) {
		}
		
	};

	/**
	 * 处理签到部分的信息
	 * @param msignDayNum
	 */
	private void PrecessSignInfo(String msignDayNum) {
		String newday= msignDayNum.replace("天", "");
		int daynum = Integer.parseInt(newday);
		

		
		getSignImageFromSvr(daynum);
		switch(daynum){
		case 1:
			SignFirstDayBlock.setVisibility(View.VISIBLE);
			SignSecondDayBlock.setVisibility(View.GONE);
			SignThirdDayBlock.setVisibility(View.GONE);
			SignFourthDayBlock.setVisibility(View.GONE);
			SignFifthDayBlock.setVisibility(View.GONE);
			SignSixthDayBlock.setVisibility(View.GONE);
			SignSeventhDayBlock.setVisibility(View.GONE);
			SignEigthDayBlock.setVisibility(View.GONE);
			SignNinthDayBlock.setVisibility(View.GONE);
	
			break;
		case 2:
			SignFirstDayBlock.setVisibility(View.VISIBLE);
			SignSecondDayBlock.setVisibility(View.VISIBLE);
			SignThirdDayBlock.setVisibility(View.GONE);
			SignFourthDayBlock.setVisibility(View.GONE);
			SignFifthDayBlock.setVisibility(View.GONE);
			SignSixthDayBlock.setVisibility(View.GONE);
			SignSeventhDayBlock.setVisibility(View.GONE);
			SignEigthDayBlock.setVisibility(View.GONE);
			SignNinthDayBlock.setVisibility(View.GONE);

			break;
		case 3:
			SignFirstDayBlock.setVisibility(View.VISIBLE);
			SignSecondDayBlock.setVisibility(View.VISIBLE);
			SignThirdDayBlock.setVisibility(View.VISIBLE);
			SignFourthDayBlock.setVisibility(View.GONE);
			SignFifthDayBlock.setVisibility(View.GONE);
			SignSixthDayBlock.setVisibility(View.GONE);
			SignSeventhDayBlock.setVisibility(View.GONE);
			SignEigthDayBlock.setVisibility(View.GONE);
			SignNinthDayBlock.setVisibility(View.GONE);
			break;
		case 4:
			SignFirstDayBlock.setVisibility(View.VISIBLE);
			SignSecondDayBlock.setVisibility(View.VISIBLE);
			SignThirdDayBlock.setVisibility(View.VISIBLE);
			SignFourthDayBlock.setVisibility(View.VISIBLE);
			SignFifthDayBlock.setVisibility(View.GONE);
			SignSixthDayBlock.setVisibility(View.GONE);
			SignSeventhDayBlock.setVisibility(View.GONE);
			SignEigthDayBlock.setVisibility(View.GONE);
			SignNinthDayBlock.setVisibility(View.GONE);
			break;
		case 5:
			SignFirstDayBlock.setVisibility(View.VISIBLE);
			SignSecondDayBlock.setVisibility(View.VISIBLE);
			SignThirdDayBlock.setVisibility(View.VISIBLE);
			SignFourthDayBlock.setVisibility(View.VISIBLE);
			SignFifthDayBlock.setVisibility(View.VISIBLE);
			SignSixthDayBlock.setVisibility(View.GONE);
			SignSeventhDayBlock.setVisibility(View.GONE);
			SignEigthDayBlock.setVisibility(View.GONE);
			SignNinthDayBlock.setVisibility(View.GONE);
			break;
		case 6:
			SignFirstDayBlock.setVisibility(View.VISIBLE);
			SignSecondDayBlock.setVisibility(View.VISIBLE);
			SignThirdDayBlock.setVisibility(View.VISIBLE);
			SignFourthDayBlock.setVisibility(View.VISIBLE);
			SignFifthDayBlock.setVisibility(View.VISIBLE);
			SignSixthDayBlock.setVisibility(View.VISIBLE);
			SignSeventhDayBlock.setVisibility(View.GONE);
			SignEigthDayBlock.setVisibility(View.GONE);
			SignNinthDayBlock.setVisibility(View.GONE);
			break;
		case 7:
			SignFirstDayBlock.setVisibility(View.VISIBLE);
			SignSecondDayBlock.setVisibility(View.VISIBLE);
			SignThirdDayBlock.setVisibility(View.VISIBLE);
			SignFourthDayBlock.setVisibility(View.VISIBLE);
			SignFifthDayBlock.setVisibility(View.VISIBLE);
			SignSixthDayBlock.setVisibility(View.VISIBLE);
			SignSeventhDayBlock.setVisibility(View.VISIBLE);
			SignEigthDayBlock.setVisibility(View.GONE);
			SignNinthDayBlock.setVisibility(View.GONE);
			break;
		case 8:
			SignFirstDayBlock.setVisibility(View.VISIBLE);
			SignSecondDayBlock.setVisibility(View.VISIBLE);
			SignThirdDayBlock.setVisibility(View.VISIBLE);
			SignFourthDayBlock.setVisibility(View.VISIBLE);
			SignFifthDayBlock.setVisibility(View.VISIBLE);
			SignSixthDayBlock.setVisibility(View.VISIBLE);
			SignSeventhDayBlock.setVisibility(View.VISIBLE);
			SignEigthDayBlock.setVisibility(View.VISIBLE);
			SignNinthDayBlock.setVisibility(View.GONE);
			break;
		case 9:
			SignFirstDayBlock.setVisibility(View.VISIBLE);
			SignSecondDayBlock.setVisibility(View.VISIBLE);
			SignThirdDayBlock.setVisibility(View.VISIBLE);
			SignFourthDayBlock.setVisibility(View.VISIBLE);
			SignFifthDayBlock.setVisibility(View.VISIBLE);
			SignSixthDayBlock.setVisibility(View.VISIBLE);
			SignSeventhDayBlock.setVisibility(View.VISIBLE);
			SignEigthDayBlock.setVisibility(View.VISIBLE);
			SignNinthDayBlock.setVisibility(View.VISIBLE);
			break;
		}
		
	}
	
	/**
	 * 获取签到信息并显示
	 */
	public void getSignImageFromSvr(int day){
		if(!Submit.isNetworkAvailable(mContext)){
			Utils.ShowToast(mContext, "没有可用网络！");
		}
		switch(day){
		case 1:
			getSignImage(1);
			break;
		case 2:
			getSignImage(1);
			getSignImage(2);
			break;
		case 3:
			getSignImage(1);
			getSignImage(2);
			getSignImage(3);
			break;
		case 4:
			getSignImage(1);
			getSignImage(2);
			getSignImage(3);
			getSignImage(4);
			break;
		case 5:
			getSignImage(1);
			getSignImage(2);
			getSignImage(3);
			getSignImage(4);
			getSignImage(5);
			break;
		case 6:
			getSignImage(1);
			getSignImage(2);
			getSignImage(3);
			getSignImage(4);
			getSignImage(5);
			getSignImage(6);
			break;
		case 7:
			getSignImage(1);
			getSignImage(2);
			getSignImage(3);
			getSignImage(4);
			getSignImage(5);
			getSignImage(6);
			getSignImage(7);
			break;
		case 8:
			getSignImage(1);
			getSignImage(2);
			getSignImage(3);
			getSignImage(4);
			getSignImage(5);
			getSignImage(6);
			getSignImage(7);
			getSignImage(8);
			break;
		case 9:
			getSignImage(1);
			getSignImage(2);
			getSignImage(3);
			getSignImage(4);
			getSignImage(5);
			getSignImage(6);
			getSignImage(7);
			getSignImage(8);
			getSignImage(9);
			break;
		}
	}
	
	/**
	 * 获取签到图片
	 * @param day
	 */
	public void getSignImage(int day){
		SignStruct struct = new SignStruct();
		struct.hire_id = hireid;
		SignStruct struct1 = new SignStruct();
		struct1.hire_id = hireid;
		struct.day =day;
		struct.type = 1;
		InitSignImage(struct);
		struct1.day =day;
		struct1.type = 2;
		InitSignImage(struct1);
	}
	
	/**handler实现adapter和fragmen】的数据交互*/
	public Handler viewhandler = new Handler(){
		public void handleMessage(Message message) {
			
			switch (message.what) {
				case TASK_SIGNING://进行中
					SignOfBlock.setVisibility(View.VISIBLE);
					SignTitleBlock.setVisibility(View.VISIBLE);
					break;
			}
		}	
			
	};
	/**
	 * 自定义ClickListener管理Click事件
	 * */
	public class MyOnClickListener implements OnClickListener{
		private int mtype=-1;//默认为-1，什么也不做
		public MyOnClickListener(int type){
			mtype = type;
		}
		@Override
		public void onClick(View v) {
			ManageClick(mtype);
		}
		
	}
	/**
	 * 设置提醒，提醒完善个人信息
	 * @param context
	 * @param name
	 */
	public void CleanDownloadTips(final Context context) {
		new AlertDialog.Builder(context).setTitle("注意")
				.setMessage("考勤记录，平台将自动为您保留三个月^_^")
				.setPositiveButton(getResources().getText(R.string.apk_tips_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								if(Submit.isNetworkAvailable(mContext))
									new GetEmployAsynTask().execute(hireid+"","已完成");
								else
									Utils.ShowToast(mContext, "没有可用网络！");
							}
						})
				.setNegativeButton(getText(R.string.apk_tips_cancel), null)
				.show();

	}
	/**
	 * 通过判断type，管理click事件
	 * @param mtype
	 */
	private int TaskImageType=-1;
	@SuppressWarnings("deprecation")
	private void ManageClick(int mtype) {
		switch (mtype) {
		case CLICK_TYPE_BACKBTN:
			FinishFunction();
			
			break;
		case TASK_FINISH_JOB_CLICK:
			CleanDownloadTips(mContext);
			break;
		/***签到  **********************************************************/
		case TASK_DAY_ONE_ON:
			TaskImageType=TASK_DAY_ONE_ON;
			showDialog(MENU_PHOTO);
			break;
		case TASK_DAY_TWO_ON:
			TaskImageType=TASK_DAY_TWO_ON;
			showDialog(MENU_PHOTO);
			break;
		case TASK_DAY_THREE_ON:
			TaskImageType=TASK_DAY_THREE_ON;
			showDialog(MENU_PHOTO);
			break;
		case TASK_DAY_FOUR_ON:
			TaskImageType=TASK_DAY_FOUR_ON;
			showDialog(MENU_PHOTO);
			break;
		case TASK_DAY_FIVE_ON:
			TaskImageType=TASK_DAY_FIVE_ON;
			showDialog(MENU_PHOTO);
			break;
		case TASK_DAY_SIX_ON:
			TaskImageType=TASK_DAY_SIX_ON;
			showDialog(MENU_PHOTO);
			break;
		case TASK_DAY_SEVEN_ON:
			TaskImageType=TASK_DAY_SEVEN_ON;
			showDialog(MENU_PHOTO);
			break;
		case TASK_DAY_EIGHT_ON:
			TaskImageType=TASK_DAY_EIGHT_ON;
			showDialog(MENU_PHOTO);
			break;
		case TASK_DAY_NINE_ON:
			TaskImageType=TASK_DAY_NINE_ON;
			showDialog(MENU_PHOTO);
			break;
		/***签退*********************************************************/
		case TASK_DAY_OFFE_OFF:
			TaskImageType=TASK_DAY_OFFE_OFF;
			showDialog(MENU_PHOTO);
			break;
		case TASK_DAY_TWO_OFF:
			TaskImageType=TASK_DAY_TWO_OFF;
			showDialog(MENU_PHOTO);
			break;
		case TASK_DAY_THREE_OFF:
			TaskImageType=TASK_DAY_THREE_OFF;
			showDialog(MENU_PHOTO);
			break;
		case TASK_DAY_FOUR_OFF:
			TaskImageType=TASK_DAY_FOUR_OFF;
			showDialog(MENU_PHOTO);
			break;
		case TASK_DAY_FIVE_OFF:
			TaskImageType=TASK_DAY_FIVE_OFF;
			showDialog(MENU_PHOTO);
			break;
		case TASK_DAY_SIX_OFF:
			TaskImageType=TASK_DAY_SIX_OFF;
			showDialog(MENU_PHOTO);
			break;
		case TASK_DAY_SEVEN_OFF:
			TaskImageType=TASK_DAY_SEVEN_OFF;
			showDialog(MENU_PHOTO);
			break;
		case TASK_DAY_EIGHT_OFF:
			TaskImageType=TASK_DAY_EIGHT_OFF;
			showDialog(MENU_PHOTO);
			break;
		case TASK_DAY_NINE_OFF:
			TaskImageType=TASK_DAY_NINE_OFF;
			showDialog(MENU_PHOTO);
			break;
		}
	}
	
	
	/**
	 * 处理签到状态的状态线，默认只要签到成功，就改变状态
	 * @param daystype
	 */
	@SuppressLint("NewApi")
	private void PrecessSignStatusLine(int daystype) {
		
		switch(daystype){
		/***签到  **********************************************************/
		case TASK_DAY_ONE_ON:
			SignFirstDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFirstDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignSecondDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignSecondDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignThirdDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignThirdDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignFourthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignFourthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignFifthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignFifthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignSixthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignSixthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignSeventhDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignSeventhDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignEigthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignEigthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignNinthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignNinthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			break;
		case TASK_DAY_TWO_ON:
			SignFirstDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFirstDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignSecondDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignSecondDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignThirdDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignThirdDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignFourthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignFourthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignFifthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignFifthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignSixthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignSixthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignSeventhDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignSeventhDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignEigthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignEigthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignNinthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignNinthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			break;
		case TASK_DAY_THREE_ON:
			SignFirstDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFirstDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignSecondDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignSecondDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignThirdDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignThirdDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignFourthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignFourthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignFifthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignFifthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignSixthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignSixthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignSeventhDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignSeventhDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignEigthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignEigthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignNinthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignNinthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			break;
		case TASK_DAY_FOUR_ON:
			SignFirstDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFirstDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignSecondDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignSecondDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignThirdDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignThirdDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignFourthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFourthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignFifthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignFifthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignSixthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignSixthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignSeventhDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignSeventhDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignEigthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignEigthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignNinthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignNinthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			break;
		case TASK_DAY_FIVE_ON:
			SignFirstDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFirstDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignSecondDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignSecondDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignThirdDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignThirdDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignFourthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFourthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignFifthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFifthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignSixthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignSixthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignSeventhDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignSeventhDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignEigthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignEigthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignNinthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignNinthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			break;
		case TASK_DAY_SIX_ON:
			SignFirstDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFirstDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignSecondDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignSecondDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignThirdDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignThirdDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignFourthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFourthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignFifthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFifthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignSixthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignSixthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignSeventhDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignSeventhDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignEigthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignEigthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignNinthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignNinthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			break;
		case TASK_DAY_SEVEN_ON:
			SignFirstDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFirstDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignSecondDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignSecondDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignThirdDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignThirdDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignFourthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFourthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignFifthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFifthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignSixthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignSixthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignSeventhDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignSeventhDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignEigthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignEigthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			SignNinthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignNinthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			break;
		case TASK_DAY_EIGHT_ON:
			SignFirstDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFirstDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignSecondDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignSecondDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignThirdDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignThirdDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignFourthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFourthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignFifthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFifthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignSixthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignSixthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignSeventhDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignSeventhDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignEigthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignEigthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignNinthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignNinthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
			break;
		case TASK_DAY_NINE_ON:
			SignFirstDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFirstDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignSecondDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignSecondDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignThirdDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignThirdDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignFourthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFourthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignFifthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFifthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignSixthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignSixthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignSeventhDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignSeventhDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignEigthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignEigthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			SignNinthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignNinthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
			break;
		}
	}
	
	/**
	 * 判断签到状态对应的显示内容
	 */
	@SuppressLint("NewApi")
	public void ProcessPointShow(){
		if(TASK_DAY_ONE){
			SignFirstDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFirstDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
		}else{
			SignFirstDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignFirstDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
		}
		if(TASK_DAY_TWO){
			SignSecondDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignSecondDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
		}else{
			SignSecondDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignSecondDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
		}
		if(TASK_DAY_THREE){
			SignThirdDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignThirdDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
		}else{
			SignThirdDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignThirdDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
		}
		if(TASK_DAY_FOUR){
			SignFourthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFourthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
		}else{
			SignFourthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignFourthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
		}
		if(TASK_DAY_FIVE){
			SignFifthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignFifthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
		}else{
			SignFifthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignFifthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
		}
		if(TASK_DAY_SIX){
			SignSixthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignSixthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
		}else{
			SignSixthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignSixthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
		}
		if(TASK_DAY_SEVEN){
			SignSeventhDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignSeventhDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
		}else{
			SignSeventhDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignSeventhDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
		}
		if(TASK_DAY_EIGHT){
			SignEigthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignEigthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
		}else{
			SignEigthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignEigthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
		}
		if(TASK_DAY_NINE){
			SignNinthDayLine.setBackgroundColor(getResources().getColor(SignedColor));
			SignNinthDayPoint.setImageDrawable(getResources().getDrawable(SignedPoint));
		}else{
			SignNinthDayLine.setBackgroundColor(getResources().getColor(UnSignedColor));
			SignNinthDayPoint.setImageDrawable(getResources().getDrawable(UnSignedPoint));
		}
		
		
	}
	
	
	/**==========================================================================================================================*/	
	/**=======================================         通过forrsult获取命令，选取本机图片                  ==========================================================*/	
	/**==========================================================================================================================*/	
		private MediaProcess myMedia = null;
		DialogInterface.OnClickListener ImageSelDlgHandle = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				switch (myMedia.DialogId) {
				case MENU_PHOTO:
					switch (which) {
					case 0:
						myMedia.Start2GetImagePhoto(which);
						break;
					case 1:
						myMedia.Start2GetImagePhoto(which);
						break;
					}
					break;
				}
			}

		};
		protected Dialog onCreateDialog(int id) {
			switch (id) {
			case DATE_DIALOG_ID:
			case MENU_PHOTO:
				if (myMedia == null) {
					myMedia = new MediaProcess(this);
				}
				return myMedia.SelectMethod(R.array.attach_pic_no_del,ImageSelDlgHandle, MENU_PHOTO);
			default:
				return null;
			}
		}
		
		protected void onPrepareDialog(int id, Dialog dialog) {
			switch (id) {
			case DATE_DIALOG_ID:
			default:
				if (myMedia == null) {
					myMedia = new MediaProcess(this);
				}
				myMedia.setDlgId(id);
				break;
			}
		}
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (resultCode != RESULT_OK) {
				Utils.ShowLog("bai", ">>>>>>>>>>> fail due to resultCode=" + resultCode);	
				return;
			}
			
			switch (requestCode) {
			case Constant.MESSENGE_REQUEST_CODE_TAKE_PHOTO:
			case Constant.MESSENGE_REQUEST_CODE_ATTACH_PHOTO:
				try {
					Uri photoUri;
					if (requestCode == Constant.MESSENGE_REQUEST_CODE_ATTACH_PHOTO) {
						photoUri = data.getData();
					} else {
						File out = new File(Environment.getExternalStorageDirectory() + Constant.PARTTIMEJOB_TEMP_PATH, "job_temp.jpg");
						photoUri = Uri.fromFile(out);
					}
					Log.v("bai", ">>>>>>>>>>>>>>>>>  photo uri:" + photoUri);
					if (photoUri == null) { 
						return;
					}
					myMedia.setCropRange(120, 120);
					myMedia.startPhotoZoom(photoUri,Constant.MESSENGE_REQUEST_CODE_CROP_PHOTO);
				} catch (Exception e) {
					Log.e("bai", e.toString());
				}
				break;
			case Constant.MESSENGE_REQUEST_CODE_CROP_PHOTO:
				final Bundle extras = data.getExtras();
				if (extras != null) {
					mBitmap = extras.getParcelable("data");
					if (mBitmap != null) {
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
						final byte[] mUserphotoData = stream.toByteArray();
						String saveDir = Environment.getExternalStorageDirectory()+ Constant.PARTTIMEJOB_IMAGE;

						Bitmap mIcon = ThumbnailUtils.extractThumbnail(mBitmap,120,120);
						String fname = "sign_image"+TaskImageType+".jpg";
						try {
							Utils.saveFile(mUserphotoData, saveDir + fname);
						} catch (IOException e) {
							Toast.makeText(mContext,R.string.photo_save_image_err,Toast.LENGTH_SHORT).show();
							return;
						}
						
						/**
						 * 对应的图片获取展示
						 */
						switch(TaskImageType){

						/***签到*********************************************************/
						case TASK_DAY_ONE_ON:
							TASK_DAY_ONE =true;
							SignOnFirstDayImage.setImageBitmap(mIcon);
							break;
						case TASK_DAY_TWO_ON:
							TASK_DAY_TWO = true;
							SignOnSecondDayImage.setImageBitmap(mIcon);
							break;
						case TASK_DAY_THREE_ON:
							TASK_DAY_THREE= true;
							SignOnThirdDayImage.setImageBitmap(mIcon);
							break;
						case TASK_DAY_FOUR_ON:
							TASK_DAY_FOUR= true;
							SignOnFourthDayImage.setImageBitmap(mIcon);
							break;
						case TASK_DAY_FIVE_ON:
							TASK_DAY_FIVE= true;
							SignOnFifthDayImage.setImageBitmap(mIcon);
							break;
						case TASK_DAY_SIX_ON:
							TASK_DAY_SIX= true;
							SignOnSixthDayImage.setImageBitmap(mIcon);
							break;
						case TASK_DAY_SEVEN_ON:
							TASK_DAY_SEVEN= true;
							SignOnSeventhDayImage.setImageBitmap(mIcon);
							break;
						case TASK_DAY_EIGHT_ON:
							TASK_DAY_EIGHT= true;
							SignOnEigthDayImage.setImageBitmap(mIcon);
							break;
						case TASK_DAY_NINE_ON:
							TASK_DAY_NINE= true;
							SignOnNinthDayImage.setImageBitmap(mIcon);
							break;
						/***签退*********************************************************/
						case TASK_DAY_OFFE_OFF:
							SignOffFirstDayImage.setImageBitmap(mIcon);
							break;
						case TASK_DAY_TWO_OFF:
							SignOffSecondDayImage.setImageBitmap(mIcon);
							break;
						case TASK_DAY_THREE_OFF:
							SignOffThirdDayImage.setImageBitmap(mIcon);
							break;
						case TASK_DAY_FOUR_OFF:
							SignOffFourthDayImage.setImageBitmap(mIcon);
							break;
						case TASK_DAY_FIVE_OFF:
							SignOffFifthDayImage.setImageBitmap(mIcon);
							break;
						case TASK_DAY_SIX_OFF:
							SignOffSixthDayImage.setImageBitmap(mIcon);
							break;
						case TASK_DAY_SEVEN_OFF:
							SignOffSeventhDayImage.setImageBitmap(mIcon);
							break;
						case TASK_DAY_EIGHT_OFF:
							SignOffEigthDayImage.setImageBitmap(mIcon);
							break;
						case TASK_DAY_NINE_OFF:
							SignOffNinthDayImage.setImageBitmap(mIcon);
							break;
						}
						String imageFile = saveDir + fname;
						if(TaskImageType<=9){
							SignStruct st= new SignStruct();
							st.hire_id=hireid;
							st.day =TaskImageType;
							st.type = 1;
							st.image = imageFile ;
							if(Submit.isNetworkAvailable(mContext))
							new SignOnAsynTask().execute(st);
							else
								Utils.ShowToast(mContext, "没有可用网络！");
						}else{
							SignStruct st1= new SignStruct();
							st1.hire_id=hireid;
							st1.day =TaskImageType;
							
							
							st1.type = 2;
							st1.image = imageFile ;
							if(Submit.isNetworkAvailable(mContext))
							new SignOnAsynTask().execute(st1);
							else
								Utils.ShowToast(mContext, "没有可用网络！");
						}
						
					}
					

				}
				break;
			}
		}
		
	/**==========================================================================================================================*/	
	/**==========================================================================================================================*/	
	/**==========================================================================================================================*/	

		/**
		 *  初始化显示签到图片
		 */
		public void InitImageShow(){
			
			Bitmap dus_bmp_be= Utils.DrableToBitmap(mContext.getResources().getDrawable(DefaultUnSignedPoint));
			Bitmap ds_bmp_be= Utils.DrableToBitmap(mContext.getResources().getDrawable(DefaultSignedPoint));
			Bitmap dus_bmp = ThumbnailUtils.extractThumbnail(dus_bmp_be,120,120);
			Bitmap ds_bmp = ThumbnailUtils.extractThumbnail(ds_bmp_be,120,120);
			SignOnFirstDayImage.setImageBitmap(ds_bmp);
			SignOnSecondDayImage.setImageBitmap(ds_bmp);
			SignOnThirdDayImage.setImageBitmap(ds_bmp);
			SignOnFourthDayImage.setImageBitmap(ds_bmp);
			SignOnFifthDayImage.setImageBitmap(ds_bmp);
			SignOnSixthDayImage.setImageBitmap(ds_bmp);
			SignOnSeventhDayImage.setImageBitmap(ds_bmp);
			SignOnEigthDayImage.setImageBitmap(ds_bmp);
			SignOnNinthDayImage.setImageBitmap(ds_bmp);
			SignOffFirstDayImage.setImageBitmap(dus_bmp);
			SignOffSecondDayImage.setImageBitmap(dus_bmp);
			SignOffThirdDayImage.setImageBitmap(dus_bmp);
			SignOffFourthDayImage.setImageBitmap(dus_bmp);
			SignOffFifthDayImage.setImageBitmap(dus_bmp);
			SignOffSixthDayImage.setImageBitmap(dus_bmp);
			SignOffSeventhDayImage.setImageBitmap(dus_bmp);
			SignOffEigthDayImage.setImageBitmap(dus_bmp);
			SignOffNinthDayImage.setImageBitmap(dus_bmp);
		}
		

		/**
		 * imageloader 加载图片展示，的一些参数的设置
		 * avatar_user_default 为默认图片
		 */
		/*imageloader部分*/
		private DisplayImageOptions options;  
		private ImageLoader mImageLoader;
		@SuppressWarnings("deprecation")
		private void initDisplayOptions() {
			options = new DisplayImageOptions.Builder()
//			.showStubImage(R.drawable.loading_default_img)	//设置正在加载图片
			.showStubImage(R.drawable.sign_loading)	//设置正在加载图片
			//.showImageOnLoading(R.drawable.ic_stub) //1.8.7新增
			.showImageForEmptyUri(R.drawable.drop_down_box_bg)	
			.showImageOnFail(R.drawable.sign_loading)	//设置加载失败图片
			.cacheInMemory(true)
			.cacheOnDisc(true)
			//.displayer(new RoundedBitmapDisplayer(20))	//设置图片角度,0为方形，360为圆角
			.build();
			
			mImageLoader = ImageLoader.getInstance();
		}
		
		@Override
		protected void onDestroy() {
			super.onDestroy();
			if(mBitmap!=null)
				mBitmap.recycle();
		}
		public void ShowWaiting(String text) {
			WaitingText.setText(text);
			WaitingDlg.setVisibility(View.VISIBLE);
		}

		public void HideWaiting() {
			WaitingDlg.setVisibility(View.GONE);
		}	
		
		
		/**
		 * 处理签到图片的初始显示
		 * @param st
		 */
		public void InitSignImage(SignStruct st){
			if(Submit.isNetworkAvailable(mContext))
				new GetSignInfoAsynTask().execute(st);
			else
				Utils.ShowToast(mContext, "没有可用网络！");
		}
		
		
		/**
		 * 签到的实现"hire_id":123, "day": 3,"type":"1/2","image": "base64('123.jpg')"
		 */
		public class SignOnAsynTask extends AsyncTask<SignStruct, Void, Boolean>{

			long errcode = -1;
			int signtype =-1;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}
			@Override
			protected Boolean doInBackground(SignStruct... params) {
				try {
					signtype = params[0].day;
					errcode = SvrOperation.SignOnOffTask(mContext, params[0]);
					
					if (errcode!=SvrInfo.SVR_RESULT_SUCCESS)
						return false;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				HideWaiting();
				if(result== false){
					return ;
				}
				if(signtype<=9)
					Utils.ShowToast(mContext, "签到成功");
				else
					Utils.ShowToast(mContext, "签退成功");
			}
		}
		/**
		 * 签退的实现	 //type 1 = 签到,2 = 签离
			Demo:{"hire_id":123, "day": 3,"type":"1/2"}
		 */
		public class GetSignInfoAsynTask extends AsyncTask<SignStruct, Void, Boolean>{
			
			String imageuri = null;
			int day = 0;
			int type = 1;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}
			@Override
			protected Boolean doInBackground(SignStruct... params) {
				try {
					day = params[0].day;
					type = params[0].type;
					imageuri = SvrOperation.GetSignInfoTask(mContext, params[0]);
					
					if (imageuri==null)
						return false;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if(result== false){
					return ;
				}
				
				switch(day){
				case 1:
					if(type == 1){
						mImageLoader.displayImage(imageuri,SignOnFirstDayImage , options);
					}else{
						mImageLoader.displayImage(imageuri,SignOffFirstDayImage , options);
					}
					break;
				case 2:
					if(type == 1){
						mImageLoader.displayImage(imageuri,SignOnSecondDayImage , options);
					}else{
						mImageLoader.displayImage(imageuri,SignOffSecondDayImage , options);
					}
					break;
				case 3:
					if(type == 1){
						mImageLoader.displayImage(imageuri,SignOnThirdDayImage , options);
					}else{
						mImageLoader.displayImage(imageuri,SignOffThirdDayImage , options);
					}
					break;
				case 4:
					if(type == 1){
						mImageLoader.displayImage(imageuri,SignOnFourthDayImage , options);
					}else{
						mImageLoader.displayImage(imageuri,SignOffFourthDayImage , options);
					}
					break;
				case 5:
					if(type == 1){
						mImageLoader.displayImage(imageuri,SignOnFifthDayImage , options);
					}else{
						mImageLoader.displayImage(imageuri,SignOffFifthDayImage , options);
					}
					break;
				case 6:
					if(type == 1){
						mImageLoader.displayImage(imageuri,SignOnSixthDayImage , options);
					}else{
						mImageLoader.displayImage(imageuri,SignOffSixthDayImage , options);
					}
					break;
				case 7:
					if(type == 1){
						mImageLoader.displayImage(imageuri,SignOnSeventhDayImage , options);
					}else{
						mImageLoader.displayImage(imageuri,SignOffSeventhDayImage , options);
					}
					break;
				case 8:
					if(type == 1){
						mImageLoader.displayImage(imageuri,SignOnEigthDayImage , options);
					}else{
						mImageLoader.displayImage(imageuri,SignOffEigthDayImage , options);
					}
					break;
				case 9:
					if(type == 1){
						mImageLoader.displayImage(imageuri,SignOnNinthDayImage , options);
					}else{
						mImageLoader.displayImage(imageuri,SignOffNinthDayImage , options);
					}
					break;
				}
				
			}
		}
		
		/**
		 * 获取雇佣关系列表存储到数据库
		 */
		public class GetHiredListInfoAsynTask extends AsyncTask<Long, Void, Boolean>{
			int errorcode = -1;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				ShowWaiting("正在获取雇佣列表……");
			}
			@Override
			protected Boolean doInBackground(Long... params) {
				try {
					
					errorcode = SvrOperation.getTaskJobList(mContext);
					
					if (errorcode != SvrInfo.SVR_RESULT_SUCCESS)
						return false;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				HideWaiting();
				if(result== false){
					return ;
				}
			}
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
				ShowWaiting("正在提交...");
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
				HideWaiting();
				if (result == false) {
					if(errcode== -3)
						Utils.ShowToast(mContext, "提交失败，请重试！");
					return;
				}
				
				FinishFunction();
				Utils.ShowToast(mContext, "设置成功！");
			}
		}
		
		private void FinishFunction() {
			Utils.CommitPageFlagToShared(mContext, Constant.FINISH_ACTIVITY_FLAG_MANAGER);
			// 返回按钮的监听
			finish();
		}
}
