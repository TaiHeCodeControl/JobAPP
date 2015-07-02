package com.parttime.activity;

import java.util.ArrayList;
import java.util.Calendar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.selecttime.ScreenInfo;
import com.parttime.selecttime.WheelMain;
import com.parttime.struct.PartTimeStruct.AllResumeStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;
import com.parttime.view.MyImageSpinner;
/**
 * 
 * @author 灰色的寂寞
 * @date 2015-1-13
 * @time 16:32
 * @function 个人中心简历详情页
 *
 */
public class Personal_Resume_Details_Acitivity  extends Activity {
	private Context mContext;
	private PartTimeDB DB = null;
	
	private ArrayList<String> provinceList = new ArrayList<String>(); 
	private ArrayList<String> cityList = new ArrayList<String>(); 
	private ArrayList<String> districtList = new ArrayList<String>(); 
	
	private String province="",city="",district="";
	
	AllResumeStruct resume = new AllResumeStruct(); 
	/** view 部分****************************************/
	/* 标题部分*/
	private TextView CenterTitle;
	private ImageView LeftBackBtn;
	
	private MyImageSpinner ExpectedProvinceContent,ExpectedCityContent,ExpectedDistrictContent;
	/*修改内容*/
	private TextView ExpectedJobContent,EndPartTimeContent,PartTimeContent,NameContent,BodyHeightContent,TelNumContent,PartExpContent;
	private RelativeLayout ExpectedJobBlock,EndPartTimeBlock,PartTimeBlock,NameBlock,BodyHeightBlock,TelNumBlock,PartExpBlock;

	private RadioGroup TypeGroup,GenderGroup;
	private RadioButton TypeHour,TypeDay,TypeWeak,GenderMan,GenderWoman;
	/*编辑对话框部分*/
	private RelativeLayout EditDialogBlock;
	private TextView DialogTitle,DialogCancelBtn,DialogEnsureBtn,ErrorTips;
	private EditText DialogContent,ShowDetailsPoi;
	
	private TextView SubmitBtn;
	/** 常量部分****************************************/
	/* 返回*/
	private static final int CLICK_TYPE_BACKBTN=0;
	private static final int CLICK_TYPE_DIALOG_CANCEL=1;
	private static final int CLICK_TYPE_DIALOG_ENSURE=2;

	
	private static final int CLICK_TYPE_PARTTIME_TIME=3;
	private static final int CLICK_TYPE_PARTTIME_NAME=4;
	private static final int CLICK_TYPE_PARTTIME_BODYHEIGHT=5;
	private static final int CLICK_TYPE_PARTTIME_TEL=6;
	private static final int CLICK_TYPE_PARTTIME_EXPERENCE=7;
	private static final int CLICK_TYPE_PARTTIME_EXPECTEDJOB=8;
	
	//选择spinner的标记
	private static final int CLICK_TYPE_PROVINCE=9;
	private static final int CLICK_TYPE_CITY=10;
	private static final int CLICK_TYPE_DISTRICT=11;
	private static final int CLICK_TYPE_ENDPARTTIME_TIME=12;
	private static final int CLICK_TYPE_PARTTIME_SUBMIT=13;
	
	private int type_flag = -1;
	private int gender_flag = -1;
	private String rid = "";
	private long userid=-1;
	private String avatar = "";
	private String name = "";
	private SharedPreferences sharedPreferences;
	LayoutInflater inflater;
	int start_year, start_month, start_day, start_hour, start_min;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal_resume_details_layout);
		mContext = this;
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		userid = sharedPreferences.getLong("userid", -1);
		avatar = sharedPreferences.getString("avatar", "");
		name = sharedPreferences.getString("username", "");
		
		Intent intent = getIntent();
		rid = intent.getStringExtra("rid");
		
		
		DB = PartTimeDB.getInstance(mContext);
		provinceList = DB.getCity(1, "北京");
		
		Calendar calendar = Calendar.getInstance();

		start_year = calendar.get(Calendar.YEAR);
		start_month = calendar.get(Calendar.MONTH);
		start_day = calendar.get(Calendar.DAY_OF_MONTH);
		start_hour = calendar.get(Calendar.HOUR_OF_DAY);
		start_min = calendar.get(Calendar.MINUTE);
		inflater = LayoutInflater.from(Personal_Resume_Details_Acitivity.this);
		
		initViews();
		resume =DB.getAllResumeInfoListByrid(mContext, Integer.parseInt(rid));
		initData(resume);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Refresh();
	}


	private void initViews() {
		/** 标题部分*/
		CenterTitle = (TextView)findViewById(R.id.title_text);
		LeftBackBtn = (ImageView)findViewById(R.id.home_back);
		CenterTitle.setText("简历详情");
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));
		
		ExpectedProvinceContent = (MyImageSpinner)findViewById(R.id.expected_province_content);
		ExpectedCityContent = (MyImageSpinner)findViewById(R.id.expected_city_content);
		ExpectedDistrictContent = (MyImageSpinner)findViewById(R.id.expected_district_content);
		
		
		ExpectedProvinceContent.setOnItemSelectedListener(new MyOnItemSelectedListener(CLICK_TYPE_PROVINCE));
		ExpectedCityContent.setOnItemSelectedListener(new MyOnItemSelectedListener(CLICK_TYPE_CITY));
		ExpectedDistrictContent.setOnItemSelectedListener(new MyOnItemSelectedListener(CLICK_TYPE_DISTRICT));
		
		/* 等待对话框部分 */
		WaitingText = (TextView)findViewById(R.id.Waiting_text);
		WaitingDlg  =(RelativeLayout)findViewById(R.id.Waiting_dlg);
		
		
		/*修改内容的layout部分 */
		ExpectedJobBlock =(RelativeLayout)findViewById(R.id.rl_expected_job_content_block);
		PartTimeBlock =(RelativeLayout)findViewById(R.id.rl_parttime_content_block);
		EndPartTimeBlock =(RelativeLayout)findViewById(R.id.rl_endparttime_content_block);
		NameBlock =(RelativeLayout)findViewById(R.id.rl_name_content_block);
		BodyHeightBlock =(RelativeLayout)findViewById(R.id.rl_body_height_content_block);
		TelNumBlock =(RelativeLayout)findViewById(R.id.rl_tel_num_content_block);
		PartExpBlock =(RelativeLayout)findViewById(R.id.rl_part_exp_content_block);

		
		
		/*修改内容*/
		ExpectedJobContent = (TextView)findViewById(R.id.expected_job_content);
		PartTimeContent = (TextView)findViewById(R.id.start_parttime_content);
		EndPartTimeContent = (TextView)findViewById(R.id.end_parttime_content);
		NameContent = (TextView)findViewById(R.id.name_content);
		BodyHeightContent = (TextView)findViewById(R.id.body_height_content);
		TelNumContent = (TextView)findViewById(R.id.tel_num_content);
		PartExpContent = (TextView)findViewById(R.id.part_exp_content);
		
		SubmitBtn = (TextView)findViewById(R.id.save_btn_height_content);
	
		TypeGroup = (RadioGroup)findViewById(R.id.type_radiogroup);
		TypeHour = (RadioButton)findViewById(R.id.type_hour);
		TypeDay = (RadioButton)findViewById(R.id.type_day);
		TypeWeak = (RadioButton)findViewById(R.id.type_weeks);
		GenderGroup = (RadioGroup)findViewById(R.id.gender_group);
		GenderMan = (RadioButton)findViewById(R.id.gender_maile);
		GenderWoman = (RadioButton)findViewById(R.id.gender_femaile);
		TypeGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup parent, int checkId) {
				if (checkId == TypeHour.getId()) {
					type_flag = 1;
				} else if(checkId==TypeDay.getId()){
					type_flag = 2;
				}else if(checkId==TypeWeak.getId())
				{
					type_flag=3;
				}
			}
		});
		GenderGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup parent, int checkId) {
				if (checkId == GenderMan.getId()) {
					gender_flag = 1;
				} else if(checkId ==GenderWoman.getId() ){
					gender_flag = 2;
				}
			}
		});
		
		SubmitBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_PARTTIME_SUBMIT));
		ExpectedJobBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_PARTTIME_EXPECTEDJOB));
		PartTimeBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_PARTTIME_TIME));
		EndPartTimeBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_ENDPARTTIME_TIME));
		NameBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_PARTTIME_NAME));
		BodyHeightBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_PARTTIME_BODYHEIGHT));
		TelNumBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_PARTTIME_TEL));
		PartExpBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_PARTTIME_EXPERENCE));
		
		EditDialogBlock = (RelativeLayout) findViewById(R.id.edit_dialog_layout);
		DialogTitle = (TextView)findViewById(R.id.edit_dialog_title);
		DialogCancelBtn = (TextView)findViewById(R.id.edit_cancel);
		DialogEnsureBtn = (TextView)findViewById(R.id.edit_ensure);
		ErrorTips = (TextView)findViewById(R.id.edit_error_tips);
		DialogContent = (EditText)findViewById(R.id.edit_dialog_content);
		ShowDetailsPoi = (EditText)findViewById(R.id.location_content_details_text);
		DialogContent.setVisibility(View.VISIBLE);
		
		DialogCancelBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_DIALOG_CANCEL));
		DialogEnsureBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_DIALOG_ENSURE));
	}
	
	public String SplitSpaceToShowPoi(String poi){
		String re=poi.substring(poi.indexOf(" "));
		return re;
	}
	private void initData(AllResumeStruct mresume ) {
		
		setSpinnerSelected(mresume.mRadd);
		ShowDetailsPoi.setText(SplitSpaceToShowPoi(mresume.mRadd));
		if(mresume.mRtype.equals("时")){
			TypeHour.setChecked(true);
			TypeDay.setChecked(false);
			TypeWeak.setChecked(false);
		}else if(mresume.mRtype.equals("天")){
			TypeHour.setChecked(false);
			TypeDay.setChecked(true);
			TypeWeak.setChecked(false);
		}else{
			TypeHour.setChecked(false);
			TypeDay.setChecked(false);
			TypeWeak.setChecked(true);
		}
		
		
		if(mresume.mRsex.equals("男")){
			GenderMan.setChecked(true);
			GenderWoman.setChecked(false);
		}else{
			GenderMan.setChecked(false);
			GenderWoman.setChecked(true);
		}
		ExpectedJobContent.setText(mresume.mRjob);
		NameContent.setText(mresume.mRname);
		PartTimeContent.setText(mresume.mRstart_Time);
		EndPartTimeContent.setText(mresume.mRend_Time);
		BodyHeightContent.setText(mresume.mRheight);
		TelNumContent.setText(mresume.mRcall);
		PartExpContent.setText(mresume.mRexperience);
	}
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
	 * 通过判断type，管理click事件
	 * @param mtype
	 */
	private int EnsureType=-1;
	private void ManageClick(int mtype) {
		switch (mtype) {
		case CLICK_TYPE_BACKBTN:
			// 返回按钮的监听
			finish();
			break;
		case CLICK_TYPE_PARTTIME_EXPECTEDJOB:
			EnsureType =CLICK_TYPE_PARTTIME_EXPECTEDJOB;
			ProcessDialogInfo("修改期望职位");
			break;
		case CLICK_TYPE_PARTTIME_TIME:
			data_select(1);
			
			break;
		case CLICK_TYPE_ENDPARTTIME_TIME:
			data_select(2);
		
			break;
		case CLICK_TYPE_PARTTIME_NAME:
			EnsureType =CLICK_TYPE_PARTTIME_NAME;
			ProcessDialogInfo("姓名");
			break;
		case CLICK_TYPE_PARTTIME_BODYHEIGHT:
			EnsureType =CLICK_TYPE_PARTTIME_BODYHEIGHT;
			ProcessDialogInfo("身高");
			break;
		case CLICK_TYPE_PARTTIME_TEL:
			EnsureType =CLICK_TYPE_PARTTIME_TEL;
			ProcessDialogInfo("手机号");
			break;
		case CLICK_TYPE_PARTTIME_EXPERENCE:
			EnsureType =CLICK_TYPE_PARTTIME_EXPERENCE;
			ProcessDialogInfo("工作经历");
			break;
			
		case CLICK_TYPE_DIALOG_CANCEL:
			EditDialogBlock.setVisibility(View.GONE);
			break;
		case CLICK_TYPE_DIALOG_ENSURE:
			ProcessDialogEnsure(EnsureType);
			break;
		case CLICK_TYPE_PARTTIME_SUBMIT:
			resume.mUserid = userid+"";
			resume.mRid = rid;
			
			resume.mRjob = ExpectedJobContent.getText().toString().trim();
			resume.mRadd= province+"-"+city+"-"+district + " "+ShowDetailsPoi.getText().toString().trim();
			resume.mRstart_Time= PartTimeContent.getText().toString().trim();
			resume.mRend_Time= EndPartTimeContent.getText().toString().trim();
			if(type_flag == 1){
				resume.mRtype= "时";
			}else if(type_flag == 2){
				resume.mRtype= "天";
			}else{
				resume.mRtype= "周";
			}
			resume.mRname= NameContent.getText().toString().trim();
			if(gender_flag ==1){
				resume.mRsex= "男";
			}else{
				resume.mRsex= "女";
			}
			resume.mRheight= BodyHeightContent.getText().toString().trim();
			resume.mRcall= TelNumContent.getText().toString().trim();
			resume.mRexperience= PartExpContent.getText().toString().trim();
			resume.mRname=NameContent.getText().toString().trim();
			if(Submit.isNetworkAvailable(mContext)){
				new ModifyResumeAsynTask().execute(resume);
			}else{
				Utils.ShowToast(mContext, "没有可用网络！");
			}
			break;
		default:
			break;
		}
	}
	/**
	 * 保存信息 
	 */
	/*等待框部分*/
	private static TextView WaitingText;
	private static RelativeLayout WaitingDlg;
	public class ModifyResumeAsynTask extends AsyncTask<AllResumeStruct, Void, Boolean>{
		long errcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在保存...");
		}
		@Override
		protected Boolean doInBackground(AllResumeStruct... params) {
			try {
				errcode = SvrOperation.Modify_Resume(mContext,params[0]);
				if (errcode !=SvrInfo.SVR_RESULT_SUCCESS)
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
				Utils.ShowToast(mContext, "保存失败！请检查网络");
				return ;
			}
			Refresh();
			finish();
		}
	}
	
	public void Refresh(){
		if(Submit.isNetworkAvailable(mContext)){
			new getAllResumeAsynTask().execute();
		}else{
			Utils.ShowToast(mContext, "对不起，没有可用网络！");
		}
	}
	public class getAllResumeAsynTask extends AsyncTask<Void, Void, Boolean>{
		long errcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在加载...");
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				errcode = SvrOperation.AllResume(mContext);
				if (errcode !=SvrInfo.SVR_RESULT_SUCCESS)
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
				Utils.ShowToast(mContext, "保存失败！请检查网络");
				return ;
			}
			AllResumeStruct rresume =DB.getAllResumeInfoListByrid(mContext, Integer.parseInt(rid));
			initData(rresume );
			
		}
	}
	public static void ShowWaiting(String text) {
		WaitingText.setText(text);
		WaitingDlg.setVisibility(View.VISIBLE);
	}

	public static void HideWaiting() {
		WaitingDlg.setVisibility(View.GONE);
	}
	/**
	 * 处理对话框的标题和显示
	 */
	public void ProcessDialogInfo(String title){
		EditDialogBlock.setVisibility(View.VISIBLE);
		DialogTitle.setText(title);
		if(title.equals("身高")||title.equals("手机号")){
			DialogContent.setInputType(InputType.TYPE_CLASS_NUMBER);
			DialogContent.setHint("请输入内容……");
		}else{
			DialogContent.setHint("请输入内容……");
		}
		
		
	}
	
	
	/**
	 * 处理对应的对话框确定值
	 * @param ensuretype
	 */
	public void ProcessDialogEnsure(int ensuretype){
		String content = DialogContent.getText().toString().trim();
		DialogContent.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			@Override
			public void afterTextChanged(Editable s) {
		            if(temp.length()>0){
		            	ErrorTips.setVisibility(View.GONE);
		            }
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {
				temp = s;  				
			}
			
		});
		if(content.equals("")){
			ErrorTips.setVisibility(View.VISIBLE);
			ErrorTips.setText("内容不能为空！");
			return;
		}else{
			ErrorTips.setVisibility(View.GONE);
		}
		switch(ensuretype){
			case CLICK_TYPE_PARTTIME_EXPECTEDJOB:
				ExpectedJobContent.setText(content);
				break;
			case CLICK_TYPE_PARTTIME_NAME:
				NameContent.setText(content);
				break;
			case CLICK_TYPE_PARTTIME_BODYHEIGHT:
				BodyHeightContent.setText(content);
				break;
			case CLICK_TYPE_PARTTIME_TEL:
				TelNumContent.setText(content);
				break;
			case CLICK_TYPE_PARTTIME_EXPERENCE:
				PartExpContent.setText(content);
				break;
		}
		
		EditDialogBlock.setVisibility(View.GONE);
	}
	
	
	
	
	
	
	
	/**
	 * 为自定义spinner添加数据
	 * 
	 * @param data
	 * @param spinner
	 */
	private void setDatatoSpinner(ArrayList<String> data, MyImageSpinner spinner) {
		if(data!=null&&data.size()>0){
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1, data);
			spinner.setAdapter(adapter);
		}
	}

	/**
	 * 监听spinner中的item 选项
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyOnItemSelectedListener implements OnItemSelectedListener {
		private int index;
		private String pos_text;

		public MyOnItemSelectedListener(int index) {
			this.index = index;
		}
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
			switch (index) {
			case CLICK_TYPE_PROVINCE:
				pos_text = parent.getItemAtPosition(pos).toString();
				if(!pos_text.equals("请选择")){
					cityList = DB.getCity(2, pos_text);
					setDatatoSpinner(cityList, ExpectedCityContent);
				}
				province = pos_text;
			case CLICK_TYPE_CITY:
				pos_text = parent.getItemAtPosition(pos).toString();
				if(!pos_text.equals("请选择")){
					districtList = DB.getCity(3, pos_text);
					setDatatoSpinner(districtList, ExpectedDistrictContent);
				}
				city = pos_text;
				break;
			case CLICK_TYPE_DISTRICT:
				pos_text = parent.getItemAtPosition(pos).toString();
				district = pos_text;
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}
	
	/**
	 * 通过给定的地区字符串显示  内容
	 * @param pos
	 */
	public void setSpinnerSelected(String pos){
		//适配省的spinner
		setDatatoSpinner(provinceList,ExpectedProvinceContent);
//		pos="河北-邯郸-曲周";
		//获取省市区的字符串
//		pos="河北-张家口-张家口";
		String [] list =Utils.SplitStringToList(pos);
		if(list.length>=1)
		//设置默认省
		if(list[0]!=null && !list[0].equals("")){
			for(int j=0;j<provinceList.size();j++){
				if(list[0].equals(provinceList.get(j))){
					ExpectedProvinceContent.setSelection(j, false);
				}
			}
		}
		
		//设置默认市
		ArrayList<String> citylistTmp =null;
		if(list.length>=2)
		if(list[0]!=null && !list[0].equals("")){
			citylistTmp = DB.getCity(2, list[0]);
			if(citylistTmp !=null)
			setDatatoSpinner(citylistTmp,ExpectedCityContent);
			
			if(list[1]!=null && !list[1].equals("") && citylistTmp!=null){
				for(int k=0;k<citylistTmp.size();k++){
					if(list[1].equals(citylistTmp.get(k))){
						ExpectedCityContent.setSelection(k,false);
					}
				}
			}
		}
		
		//设置默认县
		ArrayList<String> districtlistTmp=null;
		if(list.length>=3)
		if(list[1]!=null && !list[1].equals("")){
			districtlistTmp = DB.getCity(3, list[1]);
			if(districtlistTmp != null )
			setDatatoSpinner(districtlistTmp,ExpectedDistrictContent);
			
			if(list[2]!=null && !list[2].equals("") && districtlistTmp!=null){
			for(int i=0;i<districtlistTmp.size();i++){
					if(list[2].equals(districtlistTmp.get(i))){
						ExpectedDistrictContent.setSelection(i,false);
					}
				}
			}
		
		}
		
		
	}
	/**
	 * 设置性别部分的显示
	 * @param flag 1:男 2：女
	 */
	public void setGenderChecked(int flag){
		if(flag == 1){
			GenderMan.setChecked(true);
			GenderWoman.setChecked(false);
		}else{
			GenderMan.setChecked(false);
			GenderWoman.setChecked(true);
		}
	}
	/**
	 * 设置类型的checked
	 * @param flag 1：时 2：天 3：周
	 */
	public void setTypeChecked(int flag){
		if(flag ==1){
			TypeHour.setChecked(true);
			TypeDay.setChecked(false);
			TypeWeak.setChecked(false);
		}else if(flag == 2){
			TypeHour.setChecked(false);
			TypeDay.setChecked(true);
			TypeWeak.setChecked(false);
		}else{
			TypeHour.setChecked(false);
			TypeDay.setChecked(false);
			TypeWeak.setChecked(false);
		}
	}
	
	
	
	
	
	
	
	
	/***时间部分**************************************************************************/
	

	private WheelMain wheelMain;

	private String starttimetext,endtimetext;
	private void data_select(final int flag)
	{
		final View timepickerview = inflater.inflate(R.layout.timepicker, null);
		ScreenInfo screenInfo = new ScreenInfo(Personal_Resume_Details_Acitivity.this);

		wheelMain = new WheelMain(timepickerview, 0);
		wheelMain.screenheight = screenInfo.getHeight();
		wheelMain.initDateTimePicker(start_year, start_month, start_day, start_hour, start_min);

		new AlertDialog.Builder(Personal_Resume_Details_Acitivity.this)
				.setTitle("选择时间")
				.setView(timepickerview)
				.setPositiveButton("确定",
		new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int which) {
				//flag=1为开始时间,flag=2为结束时间
				if(flag==1){
					starttimetext=wheelMain.getTime();
					PartTimeContent.setText(starttimetext);
				}else if(flag==2){
					endtimetext=wheelMain.getTime();
					EndPartTimeContent.setText(endtimetext);
				}
			}
		}).setNegativeButton("取消", null).show();
	}

}
