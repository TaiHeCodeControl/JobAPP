package com.parttime.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.parttime.activity.Job_Create_Resume.MyOnItemSelectedListener;
import com.parttime.constant.Constant;
import com.parttime.msg.ErrorMsgSvr;
import com.parttime.parttimejob.R;
import com.parttime.selecttime.ScreenInfo;
import com.parttime.selecttime.WheelMain;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.BaiduMap_GetLatLng_Utils;
import com.parttime.utils.MediaProcess;
import com.parttime.utils.Utils;
import com.parttime.view.MyImageSpinner;
/**
 * 职位发布的activity
 * @author Administrator
 *
 */
public class Job_Issue_Activity extends Activity  implements OnGetGeoCoderResultListener{

	public static final int  BACK_INT=1;
	public static final int  CONFIRM_INT=2;
	public static final int STARTTIME_INT=3;
	public static final int  ENDTIME_INT=4;
	public static final int  COMPANY_PIC_INT=5;
	public static final int  CHANGE_PIC_INT=6;
	public static final int DESIRED_SPINNER_INT = 8;
	
	/*选图部分*/
	private final static int MENU_PHOTO = 101;
	private final static int DATE_DIALOG_ID = 102;
	private Bitmap mBitmap;

	
	private String desired_text;
	private Context context = null;
	private TextView titleText;
	private TextView rightText;
	private ImageView home_back;
	
	
//	private EditText job_title_text;
	private EditText job_pay_text;
	private EditText job_num_text;
	private EditText job_address_text;
	private EditText job_required_text;
	private EditText apply_condition_text;
	private Button apply_confirm_btn;
	private RadioGroup type_radiogroup;
	private RadioButton type_hour;
	private RadioButton type_day;
	private RadioButton type_weeks;
	private Button starttime_btn;
	private Button endtime_btn;
	private Button company_pic_btn;
	
	private ImageView company_pic_small;
	private TextView change_pic;
	
	private TextView time_text;
	
	
	
	private String type="时";
	
	private HashMap<String,String> input_map=null;
	
	
	private WheelMain wheelMain;
	private String starttimetext="";
	private String endtimetext="";
	LayoutInflater inflater;
	int year, month, day, hour, min;
	private String lat_lng;
	
	
	/*等待框部分*/
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;
	private MyImageSpinner desired_spinner;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.job_issue);
		context = this;
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Calendar calendar = Calendar.getInstance();

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		min = calendar.get(Calendar.MINUTE);

		inflater = LayoutInflater.from(Job_Issue_Activity.this);
		initView();
	}
	@Override
	protected void onStart() {
		super.onStart();
		

	}
	
	private void initView() {
		titleText = (TextView) findViewById(R.id.title_text);
		home_back = (ImageView) findViewById(R.id.home_back);
        titleText.setText(getResources().getString(R.string.job_post));
		home_back.setOnClickListener(new MyClickListener(BACK_INT));
		
		
		/* 等待对话框部分 */
		WaitingText = (TextView)findViewById(R.id.Waiting_text);
		WaitingDlg  =(RelativeLayout)findViewById(R.id.Waiting_dlg);
		
		

		ArrayList<String> desired_list=new ArrayList<String>();
		//学生兼职 模特 礼仪小姐 摄影师 小时工/钟点工 促销员 传单派发 问卷调查 手工制作 网站建设 设计制作 家教 会计 翻译 实习生 酒吧KTV 服务员 销售 其他兼职
	     desired_list.add("学生兼职");  
	     desired_list.add("模特");  
	     desired_list.add("礼仪小姐");  
	     desired_list.add("摄影师");  
	     desired_list.add("小时工/钟点工");  
	     desired_list.add("促销员");  
	     desired_list.add("传单派发");  
	     desired_list.add("问卷调查");  
	     desired_list.add("手工制作");  
	     desired_list.add("网站建设");  
	     desired_list.add("设计制作");  
	     desired_list.add("家教");  
	     desired_list.add("会计");  
	     desired_list.add("翻译");  
	     desired_list.add("实习生");  
	     desired_list.add("酒吧KTV");  
	     desired_list.add("服务员");  
	     desired_list.add("销售");  
	     desired_list.add("其他兼职");  
		
		desired_spinner = (MyImageSpinner) findViewById(R.id.desired_spinner);
		setDatatoSpinner(desired_list, desired_spinner);
		
//		job_title_text=(EditText)findViewById(R.id.job_title_text);
		job_pay_text=(EditText)findViewById(R.id.job_pay_text);
		job_num_text=(EditText)findViewById(R.id.job_num_text);
		
		type_radiogroup=(RadioGroup)findViewById(R.id.type_radiogroup);
		type_hour=(RadioButton)findViewById(R.id.type_hour);
		type_day=(RadioButton)findViewById(R.id.type_day);
		type_weeks=(RadioButton)findViewById(R.id.type_weeks);
		starttime_btn=(Button)findViewById(R.id.starttime_btn);
		endtime_btn=(Button)findViewById(R.id.endtime_btn);
		company_pic_btn=(Button)findViewById(R.id.company_pic_btn);
		company_pic_small=(ImageView)findViewById(R.id.company_pic_small);
		change_pic=(TextView)findViewById(R.id.change_pic);
		time_text=(TextView)findViewById(R.id.time_text);
		job_address_text=(EditText)findViewById(R.id.job_address_text);
		apply_condition_text=(EditText)findViewById(R.id.apply_condition_text);
		job_required_text=(EditText)findViewById(R.id.job_required_text);
		apply_condition_text=(EditText)findViewById(R.id.apply_condition_text);
		apply_confirm_btn=(Button)findViewById(R.id.apply_confirm_btn);
		apply_confirm_btn.setOnClickListener(new MyClickListener(CONFIRM_INT));
		starttime_btn.setOnClickListener(new MyClickListener(STARTTIME_INT));
		endtime_btn.setOnClickListener(new MyClickListener(ENDTIME_INT));
		company_pic_btn.setOnClickListener(new MyClickListener(COMPANY_PIC_INT));
		change_pic.setOnClickListener(new MyClickListener(CHANGE_PIC_INT));
        
		desired_spinner.setOnItemSelectedListener(new MyOnItemSelectedListener(
				DESIRED_SPINNER_INT));
		
		
		type_radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checked) {
               if(checked==type_hour.getId())
               {
            	   type="时";
            	   
               }else if(checked==type_day.getId())
               {
            	   type="天";
               }else if(checked==type_weeks.getId())
               {
            	   type="周";
               }
			}
			
		});
		
	}
	
	/**
	 * 为自定义spinner添加数据
	 * 
	 * @param data
	 * @param spinner
	 */
	private void setDatatoSpinner(ArrayList<String> data, MyImageSpinner spinner) {
		if(data!=null&&data.size()>0){
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.context,android.R.layout.simple_list_item_1, data);
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
		public MyOnItemSelectedListener(int index) {
			this.index = index;
		}
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
			switch (index) {
			
			case DESIRED_SPINNER_INT:
				desired_text = parent.getItemAtPosition(pos).toString();
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Utils.CommitPageFlagToShared(context, Constant.FINISH_ACTIVITY_FLAG_JOB);
	}
 
	public void ShowWaiting(String text) {
		WaitingText.setText(text);
		WaitingDlg.setVisibility(View.VISIBLE);
	}

	public void HideWaiting() {
		WaitingDlg.setVisibility(View.GONE);
	}
	
	
	public class MyClickListener implements View.OnClickListener {

		int index;

		public MyClickListener(int index) {
			this.index = index;
		}

		@Override
		public void onClick(View v) {
			switch (index) {
			case BACK_INT:
				Utils.CommitPageFlagToShared(context, Constant.FINISH_ACTIVITY_FLAG_JOB);
				Job_Issue_Activity.this.finish();
				break;
			case CONFIRM_INT:
				
				String[] addrs=Utils.getLocation(job_address_text.getText().toString());
				if(company_pic_small==null||company_pic_small.getDrawable()==null)
				{
					Utils.ShowToast(context, "公司的图片不能为空");
				}else
				{
				if(addrs!=null&&addrs.length==2)
				{
				getLocation(addrs[0], addrs[1]);
				}
				else
				{
					Utils.ShowToast(context, "请填写正确的地址");
				}
				}
				//TODO 职位发布的异步
//				new Job_Issue_Task().execute();
				break;
			case STARTTIME_INT:
				data_select(1);
				break;
			case ENDTIME_INT:
				data_select(2);
				break;
				//选择企业图片
			case COMPANY_PIC_INT:
				  showDialog(MENU_PHOTO);
				break;
			case CHANGE_PIC_INT:
				showDialog(MENU_PHOTO);
				break;
			}
		}

	}
	

	private void data_select(final int flag)
	{
		final View timepickerview = inflater.inflate(
				R.layout.timepicker, null);
		ScreenInfo screenInfo = new ScreenInfo(Job_Issue_Activity.this);

		wheelMain = new WheelMain(timepickerview, 0);
		wheelMain.screenheight = screenInfo.getHeight();
		wheelMain.initDateTimePicker(year, month, day, hour, min);

		new AlertDialog.Builder(Job_Issue_Activity.this)
				.setTitle("选择时间")
				.setView(timepickerview)
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
//								//flag=1为开始时间,flag=2为结束时间
//								if(flag==1)
//								{
//									starttimetext=wheelMain.getTime();
//								}else if(flag==2)
//								{
//									endtimetext=wheelMain.getTime();
//								}
								time_text.setVisibility(View.VISIBLE);
//								time_text.setText(starttimetext+"-"+endtimetext);
//								Log.e("hu", wheelMain.getTime());
								//flag=1为开始时间,flag=2为结束时间
								String time="";
								if(flag==1)
								{
									starttimetext=wheelMain.getTime();
									time=starttimetext;
								}else if(flag==2)
								{
									endtimetext=wheelMain.getTime();
									DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									try
									{
									    Date d1 = df.parse(starttimetext);
									    Date d2 = df.parse(endtimetext);
									    long diff = d2.getTime() - d1.getTime();
									    
									    if(diff<=0)
									    {
									    	Utils.ShowToast(context, "您选择的结束时间必须大于开始时间");
									    	time=starttimetext;
									    }
									    else
									    {
									    	time=starttimetext+"-"+endtimetext;
									    	
									    }
									}
									catch (Exception e)
									{
									}
								}
								time_text.setText(time);
							}
						}).setNegativeButton("取消", null).show();
	}
	
	
	
	private void getLocation(String city,String addr)
	{
		GeoCoder mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		mSearch.geocode(new GeoCodeOption().city(city).address(addr));
		
		
	}
	
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		// TODO Auto-generated method stub
		if(result!=null)
		{
		double lat=result.getLocation().latitude;
		double lgn= result.getLocation().longitude;
//		LatLng ll =BaiduMap_GetLatLng_Utils.mLocal_LatLng;
//		LatLng la = new LatLng(lat, lgn);
//		double meter=DistanceUtil.getDistance(la, ll);
		lat_lng=lat+","+lgn;
//		lat_lng=(int)(lat)+","+(int)lgn;
		new Job_Issue_Task().execute();
		
		}
		
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	/**
	 * 职位发布的异步
	 * @author huxixi
	 *
	 */
	public class Job_Issue_Task extends AsyncTask<Void, Void, Boolean> {
		
		
		private int errcode;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在发布职位请稍等...");
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			
			input_map=new HashMap<String, String>();
			input_map.put("name", desired_text);
			input_map.put("charge", job_pay_text.getText().toString());
			input_map.put("type", type);
			input_map.put("num", job_num_text.getText().toString());
			input_map.put("present", job_required_text.getText().toString());
			input_map.put("require", job_required_text.getText().toString());
			input_map.put("condition", apply_condition_text.getText().toString());
			input_map.put("position", lat_lng);
			input_map.put("start_time",starttimetext );
			input_map.put("end_time", endtimetext);
			if(!registerHeadPath.equals(""))
				input_map.put("avatar", registerHeadPath);
			errcode = SvrOperation.Job_Issue(context,input_map);
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
				Utils.ShowToast(context, errmsg);
				return;
			}
			Utils.ShowToast(context, "职位已发布");
			Utils.CommitPageFlagToShared(context,Constant.FINISH_ACTIVITY_FLAG_JOB);
			Job_Issue_Activity.this.finish();
			return;
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
		
		String registerHeadPath =""; 
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
						String fname = "register_image"+".jpg";
						try {
							company_pic_small.setVisibility(View.VISIBLE);
							change_pic.setVisibility(View.VISIBLE);
							company_pic_btn.setVisibility(View.GONE);
							company_pic_small.setImageBitmap(mIcon);
							Utils.saveFile(mUserphotoData, saveDir + fname);
							registerHeadPath=saveDir + fname;
						} catch (IOException e) {
							Toast.makeText(context,R.string.photo_save_image_err,Toast.LENGTH_SHORT).show();
							return;
						}
					}
				}
				break;
			}
		}
		
	/**==========================================================================================================================*/	
	/**==========================================================================================================================*/	
	/**==========================================================================================================================*/	
	
	
	
}
