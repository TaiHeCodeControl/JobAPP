package com.parttime.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

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
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.MediaProcess;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;
/**
 * 
 * @author 灰色的寂寞
 * @function 发布求助
 * @date 2015-2-4
 * @time 17:54
 *
 */
public class Volunteer_PublishHelp_Activity extends Activity{
	
	private Context mContext;
	/**view 部分****************************************************/
	/*等待框部分*/
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;
	/* 标题部分*/
	private TextView CenterTitle;
	private ImageView LeftBackBtn;
	
	private EditText AskerName,AskTitleContent,AskDetailsContent,AskerTelContent;
	private ImageView AskImageShow;
	private TextView EmploymentBtn,ErrorTips;
	private Bitmap mBitmap;
	/**常量部分****************************************************/
	/* 返回*/
	private static final int CLICK_TYPE_BACKBTN=0;
	private static final int CLICK_TYPE_ASKTO_PUBLISH=1;
	private static final int CLICK_TYPE_UPLOAD_ASKIMAGE=2;
	/*选图部分*/
	private final static int MENU_PHOTO = 101;
	private final static int DATE_DIALOG_ID = 102;
	
	private long userid=-1;
	private SharedPreferences sharedPreferences;
	private AskInfoStruct askstruct = new AskInfoStruct();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.volunteer_publishhelp_layout);
		mContext = this;
		
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		userid = sharedPreferences.getLong("userid", -1);
		initViews();
		
		
		
	}

	private void initViews() {
		/** 标题部分*/
		CenterTitle = (TextView)findViewById(R.id.title_text);
		LeftBackBtn = (ImageView)findViewById(R.id.home_back);
		CenterTitle.setText("发布求助");
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));
		
		/* 等待对话框部分 */
		WaitingText = (TextView)findViewById(R.id.Waiting_text);
		WaitingDlg  =(RelativeLayout)findViewById(R.id.Waiting_dlg);
		
		
		AskerName = (EditText)findViewById(R.id.volunteer_name);
		AskTitleContent = (EditText)findViewById(R.id.volunteer_title_content);
		AskDetailsContent = (EditText)findViewById(R.id.volunteer_details_content);
		AskerTelContent = (EditText)findViewById(R.id.volunteer_tel_content);
	    
		ErrorTips= (TextView)findViewById(R.id.employment_tips);
		EmploymentBtn = (TextView)findViewById(R.id.employment_btn);
		EmploymentBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_ASKTO_PUBLISH));
		AskImageShow = (ImageView)findViewById(R.id.volunteer_image_show);
		AskImageShow.setImageDrawable(getResources().getDrawable(R.drawable.camera_icon));
		AskImageShow.setOnClickListener(new MyOnClickListener(CLICK_TYPE_UPLOAD_ASKIMAGE));
	
	
	
		
	}
	
	public void ShowWaiting(String text) {
		WaitingText.setText(text);
		WaitingDlg.setVisibility(View.VISIBLE);
	}

	public void HideWaiting() {
		WaitingDlg.setVisibility(View.GONE);
	}
	
	public void ShowError(String error){
		ErrorTips.setVisibility(View.VISIBLE);
		ErrorTips.setText(error);
	}
	public void HideError(){
		ErrorTips.setVisibility(View.GONE);
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
	@SuppressWarnings("deprecation")
	private void ManageClick(int mtype) {
		switch (mtype) {
		case CLICK_TYPE_BACKBTN:
			// 返回按钮的监听
			finish();
			break;
		case CLICK_TYPE_UPLOAD_ASKIMAGE:
			showDialog(MENU_PHOTO);
			break;
		case CLICK_TYPE_ASKTO_PUBLISH:
			
			askstruct.mUserid = (int)userid;
			askstruct.mName = AskerName.getText().toString().trim();
			askstruct.mTheme = AskTitleContent.getText().toString().trim();
			askstruct.mContent = AskDetailsContent.getText().toString().trim();
			askstruct.mCall = AskerTelContent.getText().toString().trim();
			if(Submit.isNetworkAvailable(mContext))
				if(askstruct.mName!=null && !askstruct.mName.equals("")){
					if(askstruct.mTheme!=null && !askstruct.mTheme.equals("")){
						if(askstruct.mContent!=null && !askstruct.mContent.equals("")){
							if(askstruct.mCall!=null && !askstruct.mCall.equals("")){
								if(askstruct.mImage!=null && !askstruct.mImage.equals("")){
									HideError();
									new PublishAskInfoAsynTask().execute(askstruct);
								}else{
									ShowError("请添加求助图片，谢谢！");
								}	
							}else{
								ShowError("求助电话不能为空！");
							}	
						}else{
							ShowError("求助内容不能为空！");
						}
						
					}else{
						ShowError("求助标题不能为空！");
					}
				}else{
					ShowError("求助名称不能为空！");
				}
			else
				Utils.ShowToast(mContext, "没有可用网络，请联网后重试！");
			break;
		default:
			break;
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
						AskImageShow.setImageBitmap(mIcon);
						
						String fname = "ask_tmp_image"+".jpg";
						try {
							Utils.saveFile(mUserphotoData, saveDir + fname);
							askstruct.mImage = saveDir + fname;
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
		/**
		 * 发布求助信息
		 */
		public class PublishAskInfoAsynTask extends AsyncTask<AskInfoStruct, Void, Boolean>{

			int errcode = -1;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				ShowWaiting("正在发布...");
			}
			@Override
			protected Boolean doInBackground(AskInfoStruct... params) {
				try {
					errcode = SvrOperation.PublishAskTask(mContext, params[0]);
					
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
				new GetAskListAsynTask().execute(0);
				Utils.ShowToast(mContext, "发布成功！");
				finish();
			}
		}
		/**
		 * 获取求助列表
		 */
		public class GetAskListAsynTask extends AsyncTask<Integer, Void, Boolean>{

			long errcode = -1;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}
			@Override
			protected Boolean doInBackground(Integer... params) {
				try {
					errcode = SvrOperation.GetAskingListTask(mContext, params[0]);
					
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
				if(result== false){
					return ;
				}
				
			}
		}
}
