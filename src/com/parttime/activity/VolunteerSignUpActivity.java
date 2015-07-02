package com.parttime.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
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
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parttime.constant.Constant;
import com.parttime.msg.ErrorMsgSvr;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.AskInfoStruct;
import com.parttime.struct.PartTimeStruct.VolunteerSignInfoStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.MediaProcess;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;
/**
 * 
 * @author 灰色的寂寞
 * @function 志愿者签名页面
 * @date 2015-2-5
 * @time 11:30
 *
 */
public class VolunteerSignUpActivity extends Activity{
	private Context mContext;
	private int mid =-1;
	/**view 部分****************************************************/
	/* 标题部分*/
	private TextView CenterTitle;
	private ImageView LeftBackBtn;
	/*等待框部分*/
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;
	private EditText VolunteerNameEdit,VolunteerTellEdit,VolunteerIDCardEdit,VolunteerDiscEdit;
	private ImageView VolunteerSignHeadImage;
	private TextView SignUpEnsureBtn,SignUpEnsureTips;
	/**常量部分****************************************************/
	/* 返回*/
	private static final int CLICK_TYPE_BACKBTN=0;
	private static final int CLICK_TYPE_SIGNUP_ENSURE=1;
	private static final int CLICK_TYPE_SIGNUP_HEADIMAGE=2;
	
	/*选图部分*/
	private final static int MENU_PHOTO = 101;
	private final static int DATE_DIALOG_ID = 102;
	private long userid=-1;
	private SharedPreferences sharedPreferences;
	VolunteerSignInfoStruct vsstruct = new VolunteerSignInfoStruct();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.volunteer_sign_up_layout);
		mContext = this;
		
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		userid = sharedPreferences.getLong("userid", -1);
		initViews();
		Intent intent = getIntent();
		mid = intent.getIntExtra("mid", -1);
		
	}

	private void initViews() {
		/** 标题部分*/
		CenterTitle = (TextView)findViewById(R.id.title_text);
		LeftBackBtn = (ImageView)findViewById(R.id.home_back);
		CenterTitle.setText("报名");

		/* 等待对话框部分 */
		WaitingText = (TextView)findViewById(R.id.Waiting_text);
		WaitingDlg  =(RelativeLayout)findViewById(R.id.Waiting_dlg);
		
		VolunteerSignHeadImage = (ImageView)findViewById(R.id.volunteer_signup_head_image);
		VolunteerNameEdit = (EditText)findViewById(R.id.signup_name_content);
		VolunteerTellEdit = (EditText)findViewById(R.id.tel_content);
		VolunteerIDCardEdit = (EditText)findViewById(R.id.IDcard_content);
		VolunteerDiscEdit = (EditText)findViewById(R.id.disc_myself_content);
		SignUpEnsureBtn = (TextView)findViewById(R.id.sign_up_ensure_btn);
		SignUpEnsureTips = (TextView)findViewById(R.id.sign_up_ensure_tips);
		
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));
		VolunteerSignHeadImage.setOnClickListener(new MyOnClickListener(CLICK_TYPE_SIGNUP_HEADIMAGE));
		SignUpEnsureBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_SIGNUP_ENSURE));
		
		
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
	public void ShowError(String error){
		SignUpEnsureTips.setVisibility(View.VISIBLE);
		SignUpEnsureTips.setText(error);
	}
	
	public void HideError(){
		SignUpEnsureTips.setVisibility(View.GONE);
	}
	/**
	 * 通过判断type，管理click事件
	 * @param mtype
	 */
	@SuppressWarnings("deprecation")
	private void ManageClick(int mtype) {
		switch (mtype) {
		case CLICK_TYPE_BACKBTN:
			// 返回按钮的监听
			finish();
			break;
		case CLICK_TYPE_SIGNUP_HEADIMAGE:
			showDialog(MENU_PHOTO);
			break;
		case CLICK_TYPE_SIGNUP_ENSURE:
			vsstruct.userid = (int)userid;
			vsstruct.volunteerid = (int)mid;
			vsstruct.name = VolunteerNameEdit.getText().toString().trim();
			vsstruct.call = VolunteerTellEdit.getText().toString().trim();
			vsstruct.papers = VolunteerIDCardEdit.getText().toString().trim();
			vsstruct.content =VolunteerDiscEdit.getText().toString().trim();
			
			if(Submit.isNetworkAvailable(mContext)){
				if(vsstruct.name!=null && !vsstruct.name.equals("") ){
					if(vsstruct.call!=null && !vsstruct.call.equals("") ){
						if(vsstruct.papers!=null && !vsstruct.papers.equals("") ){
							if(vsstruct.content!=null && !vsstruct.content.equals("") ){
								HideError();
								new VolunteerSignAsynTask().execute(vsstruct);
							}else{
								ShowError("志愿者描述不能为空！");
							}	
						}else{
							ShowError("志愿者证件不能为空！");
						}	
					}else{
						ShowError( "志愿者电话不能为空！");
					}	
				}else{
					ShowError("志愿者姓名不能为空！");
				}
			
			}else{
				Utils.ShowToast(mContext, "没有可用网络，请联网后重试！");
			}
			break;
		default:
			break;
		}
	}
	
	/**==========================================================================================================================*/	
	/**=======================================         通过forrsult获取命令，选取本机图片                  ==========================================================*/	
	/**==========================================================================================================================*/	
		private MediaProcess myMedia = null;
		private Bitmap mBitmap = null;
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
						String fname = "volunteer_signed_headimage"+".jpg";
						VolunteerSignHeadImage.setImageBitmap(mIcon);
						try {
							Utils.saveFile(mUserphotoData, saveDir + fname);
							vsstruct.image =saveDir+fname;
						} catch (IOException e) {
							Toast.makeText(mContext,R.string.photo_save_image_err,Toast.LENGTH_SHORT).show();
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
		public void ShowWaiting(String text) {
			WaitingText.setText(text);
			WaitingDlg.setVisibility(View.VISIBLE);
		}

		public void HideWaiting() {
			WaitingDlg.setVisibility(View.GONE);
		}
		/**
		 * 发布求助信息
		 */
		public class VolunteerSignAsynTask extends AsyncTask<VolunteerSignInfoStruct, Void, Boolean>{

			int errcode = -1;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				ShowWaiting("正在报名...");
			}
			@Override
			protected Boolean doInBackground(VolunteerSignInfoStruct... params) {
				try {
					errcode = SvrOperation.VolunteerSignTask(mContext, params[0]);
					
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
					Utils.ShowToast(mContext, ErrorMsgSvr.ErrorMsg(errcode));
					return ;
				}
				finish();
				
			}
		}

	
}
