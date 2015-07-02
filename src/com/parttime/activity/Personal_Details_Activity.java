package com.parttime.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parttime.constant.Constant;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.PersonalDetailsStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.MediaProcess;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;

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
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author 灰色的寂寞
 * @date 2015-2-5
 * @time 18:08
 * @function 个人信息详情页
 *
 */
public class Personal_Details_Activity extends Activity {
	private Context mContext;
	/*等待框部分*/
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;
	/** 标题部分*/
	private ImageView RightBtn;
	private TextView LeftBackBtn,CenterTitle;
	
	private RelativeLayout HeadImageBlock,NameBlock,GenderBlock,EmailBlock,ChangePwdBlock,WorkExpBlock,QQBlock,CallBlock,CardConfirmBlock;
	private ImageView HeadImage,CardConfirmImage;
	private TextView InfoName,InfoGender,InfoEmail,InfoPwd,InfoWorkExp,InfoQQ,InfoCall;
	
	private TextView SubmitBtn;
	/*编辑对话框部分*/
	private RelativeLayout EditDialogBlock;
	private TextView DialogTitle,DialogCancelBtn,DialogEnsureBtn,ErrorTips;
	private EditText DialogContent;
	//常量部分
	/** 返回*/
	private static final int CLICK_TYPE_BACKBTN=0;
	private static final int CLICK_TYPE_HEADIMAGE=1;
	private static final int CLICK_TYPE_NAME=2;
	private static final int CLICK_TYPE_GENDER=3;
	private static final int CLICK_TYPE_EMAIL=4;
	private static final int CLICK_TYPE_WORKEXP=5;
	private static final int CLICK_TYPE_QQ=6;
	private static final int CLICK_TYPE_CALL=7;
	private static final int CLICK_TYPE_RIGHTBTN=8;
	private static final int CLICK_TYPE_SUBMITBTN=9;
	private static final int CLICK_TYPE_CHANGEPWD=10;
	private static final int CLICK_TYPE_CARDCONFIRM=11;
	
	
	
	/*选图部分*/
	private final static int MENU_PHOTO = 101;
	private final static int DATE_DIALOG_ID = 102;
	
	private static final int CLICK_TYPE_DIALOG_CANCEL=103;
	private static final int CLICK_TYPE_DIALOG_ENSURE=104;
	
	private boolean ModifyHeadState = false;
	private boolean ModifyCardConfirmState = false;
	private boolean ModifyState = false;
	private boolean JudgeFreshFirstState = false;
	private long userid=-1;
	private String avatar = "";
	private SharedPreferences sharedPreferences;
	
	private PartTimeDB mPartTimeDB=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal_details_info_layout);
		mContext = this;
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		userid = sharedPreferences.getLong("userid", -1);
		avatar = sharedPreferences.getString("avatar", "");
		mPartTimeDB = PartTimeDB.getInstance(mContext);
		initDisplayOptions();
		initViews();
	}
	
	
	@SuppressLint("NewApi")
	private void initViews() {
		
		/** 标题部分*/
		CenterTitle = (TextView)findViewById(R.id.mtitle_text);
		RightBtn = (ImageView)findViewById(R.id.mtitle_right_image);
		LeftBackBtn = (TextView)findViewById(R.id.mtitle_back_arrow);
		CenterTitle.setText("个人详情");
		/* 等待对话框部分 */
		WaitingText = (TextView)findViewById(R.id.Waiting_text);
		WaitingDlg  =(RelativeLayout)findViewById(R.id.Waiting_dlg);
		
		RightBtn.setVisibility(View.VISIBLE);
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));
		RightBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_RIGHTBTN));
		
		
		HeadImageBlock = (RelativeLayout)findViewById(R.id.rl_personal_headimage_block);
		NameBlock = (RelativeLayout)findViewById(R.id.rl_personal_name_block);
		GenderBlock = (RelativeLayout)findViewById(R.id.rl_personal_gender_block);
		EmailBlock = (RelativeLayout)findViewById(R.id.rl_personal_email_block);
		ChangePwdBlock = (RelativeLayout)findViewById(R.id.rl_change_pwd_block);
		
		WorkExpBlock = (RelativeLayout)findViewById(R.id.rl_personal_workexp_block);
		QQBlock = (RelativeLayout)findViewById(R.id.rl_personal_qq_block);
		CallBlock = (RelativeLayout)findViewById(R.id.rl_personal_call_block);
		CardConfirmBlock= (RelativeLayout)findViewById(R.id.rl_personal_cardconfirm_block);
		
		HeadImage = (ImageView)findViewById(R.id.personal_headimage);
		CardConfirmImage = (ImageView)findViewById(R.id.personal_info_cardconfirm);

		InfoName = (TextView)findViewById(R.id.personal_info_name);
		InfoGender = (TextView)findViewById(R.id.personal_info_gender);
		InfoEmail = (TextView)findViewById(R.id.personal_info_email);
		InfoPwd = (TextView)findViewById(R.id.personal_change_pwd);
		InfoWorkExp = (TextView)findViewById(R.id.personal_info_workexp);
		InfoQQ = (TextView)findViewById(R.id.personal_info_qq);
		InfoCall = (TextView)findViewById(R.id.personal_info_call);
		
		HeadImageBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_HEADIMAGE));
		NameBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_NAME));
		GenderBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_GENDER));
		EmailBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_EMAIL));
		ChangePwdBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_CHANGEPWD));
		WorkExpBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_WORKEXP));
		QQBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_QQ));
		CallBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_CALL));
		CardConfirmBlock.setOnClickListener(new MyOnClickListener(CLICK_TYPE_CARDCONFIRM));
		
		
		EditDialogBlock = (RelativeLayout) findViewById(R.id.edit_dialog_layout);
		DialogTitle = (TextView)findViewById(R.id.edit_dialog_title);
		DialogCancelBtn = (TextView)findViewById(R.id.edit_cancel);
		DialogEnsureBtn = (TextView)findViewById(R.id.edit_ensure);
		ErrorTips = (TextView)findViewById(R.id.edit_error_tips);
		DialogContent = (EditText)findViewById(R.id.edit_dialog_content);
		DialogContent.setVisibility(View.VISIBLE);
		DialogCancelBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_DIALOG_CANCEL));
		DialogEnsureBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_DIALOG_ENSURE));
		
		
		SubmitBtn= (TextView)findViewById(R.id.personal_modify_content_submit_btn);
		SubmitBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_SUBMITBTN));
		
		if(Submit.isNetworkAvailable(mContext)){
			new GetPersonalInfoAsynTask().execute(userid);
		}else{
			PersonalDetailsStruct struct= mPartTimeDB.GetPersonalDetailsInfo(mContext, userid);
			initDataToView(struct);
		}
	}
	/**
	 * 初始化数据到view显示
	 */
	private void initDataToView(PersonalDetailsStruct struct) {
			InfoName.setText(struct.mPersonalName);
			if(struct.mPersonalGender==null || struct.mPersonalGender.equals("")||struct.mPersonalGender.equals("null")||InfoGender.getText().toString().equals("")||InfoGender.getText().toString().equals("null")){
				InfoGender.setText("男");
			}else{
			   InfoGender.setText(struct.mPersonalGender);
			}
			
			if(struct.mPersonalEmail==null || struct.mPersonalEmail.equals("")||struct.mPersonalEmail.equals("null"))
				InfoEmail.setText("空");
			else
				InfoEmail.setText(struct.mPersonalEmail);
			
			InfoPwd.setText("*****");
			
			if(struct.mPersonalJobExp==null || struct.mPersonalJobExp.equals("")|| struct.mPersonalJobExp.equals("null"))
				InfoWorkExp.setText("空");
			else
				InfoWorkExp.setText(struct.mPersonalJobExp);
			
			if(struct.mPersonalAddress==null || struct.mPersonalAddress.equals("") || struct.mPersonalAddress.equals("null"))
				InfoQQ.setText("空");
			else
				InfoQQ.setText(struct.mPersonalAddress);
			
			if(struct.mPersonalCall==null || struct.mPersonalCall.equals("") || struct.mPersonalCall.equals("null"))
				InfoCall.setText("空");
			else
				InfoCall.setText(struct.mPersonalCall);
		  String path =Environment.getExternalStorageDirectory()+Constant.PARTTIMEJOB_HEAD+avatar.substring(avatar.lastIndexOf("/")+1);
	        if(Utils.getFilesExist(Environment.getExternalStorageDirectory()+Constant.PARTTIMEJOB_HEAD, avatar.substring(avatar.lastIndexOf("/")+1))){
	        	Bitmap mBp =Utils.getBitmapformpath(path);
	        	Bitmap mIcon = ThumbnailUtils.extractThumbnail(mBp,120,120);
	        	HeadImage.setImageBitmap(mIcon);
	        }else{
	        	if(avatar!=null && !avatar.equals(""))
					mImageLoader.displayImage(avatar, HeadImage, options);
	        	else
	        		HeadImage.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar_girl));
	        }
	        
	        mImageLoader.displayImage(struct.mPersonalCardConfirm, CardConfirmImage, options);
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
	@SuppressWarnings("deprecation")
	private void ManageClick(int mtype) {
		switch (mtype) {
		case CLICK_TYPE_BACKBTN:
			// 返回按钮的监听
			if(ModifyHeadState==true){
				ProcessFinishData(ModifyHeadState);
			}else if(ModifyCardConfirmState == true){
				ProcessFinishData(ModifyCardConfirmState);
			}else{
				ProcessFinishData(ModifyState);
			}
			break;
		case CLICK_TYPE_HEADIMAGE:
			EnsureType =CLICK_TYPE_HEADIMAGE; 
			showDialog(MENU_PHOTO);
			break;
		case CLICK_TYPE_NAME:
			EnsureType = CLICK_TYPE_NAME; 
			ProcessDialogInfo("姓名");
			break;
		case CLICK_TYPE_GENDER:
			String genderString = InfoGender.getText().toString().trim();
			if(genderString.equals("男"))
				SelectionGender(0);
			else if(genderString.equals("女"))
				SelectionGender(1);
			
			break;
		case CLICK_TYPE_EMAIL:
			
			EnsureType = CLICK_TYPE_EMAIL; 
			ProcessDialogInfo("邮箱");
			break;
		case CLICK_TYPE_WORKEXP:
			
			EnsureType = CLICK_TYPE_WORKEXP; 
			ProcessDialogInfo("工作经验");
			break;
		case CLICK_TYPE_QQ:
			
			EnsureType = CLICK_TYPE_QQ; 
			ProcessDialogInfo("QQ");
			break;
		case CLICK_TYPE_CALL:
			
			EnsureType = CLICK_TYPE_CALL; 
			ProcessDialogInfo("手机号");
			break;
		case CLICK_TYPE_CARDCONFIRM:
			EnsureType =CLICK_TYPE_CARDCONFIRM; 
			showDialog(MENU_PHOTO);
			break;
		/***对话框更改信息********************************************************************/
		case CLICK_TYPE_DIALOG_CANCEL:
			EditDialogBlock.setVisibility(View.GONE);
			break;
		case CLICK_TYPE_DIALOG_ENSURE:
			ProcessDialogEnsure(EnsureType);
			break;
			
		case CLICK_TYPE_RIGHTBTN://刷新部分
			if(Submit.isNetworkAvailable(mContext))
				Refresh(userid);
			else
				Utils.ShowToast(mContext, "没有可用网络！");
			
			break;
		case CLICK_TYPE_SUBMITBTN://提交数据
			
			PersonalDetailsStruct struct = new PersonalDetailsStruct();
			struct.mPersonalId = userid;
			struct.mPersonalLinkman =InfoName.getText().toString().trim();
			struct.mPersonalGender=InfoGender.getText().toString().trim();
			struct.mPersonalEmail =InfoEmail.getText().toString().trim();
			struct.mPersonalJobExp =InfoWorkExp .getText().toString().trim();
			struct.mPersonalAddress =InfoQQ .getText().toString().trim();
			struct.mPersonalCall=InfoCall .getText().toString().trim();
			String saveDir = Environment.getExternalStorageDirectory()+ Constant.PARTTIMEJOB_IMAGE;
			String fname = "personal_details_headimage"+".jpg";
			if(ModifyHeadState){
				struct.mPersonalHeadImage =saveDir+fname;
			}
			fname = "personal_details_cardconfirmimage"+".jpg";
			if(ModifyCardConfirmState){
				struct.mPersonalCardConfirm=saveDir+fname;
			}
			if(Submit.isNetworkAvailable(mContext))
				if(ModifyHeadState == true || ModifyState == true || ModifyCardConfirmState==true)
					new SubmitPersonalInfoAsynTask().execute(struct);
				else
					Utils.ShowToast(mContext, "没有作修改,请修改后提交");
			else
				Utils.ShowToast(mContext, "没有可用网络！");
			break;
		case CLICK_TYPE_CHANGEPWD:
			Utils.intent2Class(mContext, Personal_ModifyPwd_Activity.class);
			break;
		default:
			break;
		}
	}
	
	
	public void Refresh(long muserid){
		new GetPersonalInfoAsynTask().execute(muserid);
	}
	/**
	 * 获取个人信心 
	 */
	public class GetPersonalInfoAsynTask extends AsyncTask<Long, Void, Boolean>{
		int errcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在加载...");
		}
		@Override
		protected Boolean doInBackground(Long... params) {
			try {
				errcode = SvrOperation.GetPersonalInfo(mContext, params[0]);
				
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
			PersonalDetailsStruct struct = mPartTimeDB.GetPersonalDetailsInfo(mContext, userid);
			initDataToView(struct);
			if(result== false){
				Utils.ShowToast(mContext, "刷新失败，请检查网络");
				return ;
			}
			if(!JudgeFreshFirstState){
				JudgeFreshFirstState =true;
				return;
			}
			Utils.ShowToast(mContext, "刷新成功");
		}
	}
	
	/**
	 * 提交个人信心 
	 */
	public class SubmitPersonalInfoAsynTask extends AsyncTask<PersonalDetailsStruct, Void, Boolean>{
		long errcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在提交...");
		}
		@Override
		protected Boolean doInBackground(PersonalDetailsStruct... params) {
			try {
				if(ModifyHeadState){
				errcode = SvrOperation.uploadImage(mContext, userid, params[0].mPersonalHeadImage);
				if (errcode !=SvrInfo.SVR_RESULT_SUCCESS)
					return false;
				}
				
				if(ModifyState){
				errcode = SvrOperation.UploadPersonalInfo(mContext, params[0]);
				if (errcode !=SvrInfo.SVR_RESULT_SUCCESS)
					return false;
				}
				if(ModifyCardConfirmState){
					errcode = SvrOperation.uploadConfirmImage(mContext, userid,"个人", params[0].mPersonalCardConfirm);
					if (errcode !=SvrInfo.SVR_RESULT_SUCCESS)
						return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			HideWaiting();
			super.onPostExecute(result);
			if(result== false){
				Utils.ShowToast(mContext, "提交失败！请检查网络");
				return ;
			}
			
			String path =Environment.getExternalStorageDirectory()+Constant.PARTTIMEJOB_HEAD;
			if(Utils.getFilesExist(path, avatar.substring(avatar.lastIndexOf("/")+1))){
				Utils.deleteFileFromSDcard(path, avatar.substring(avatar.lastIndexOf("/")+1));
			}
			if(ModifyHeadState == true)
			new GetPersonalHeadAsynTask().execute(avatar,Constant.PARTTIMEJOB_HEAD);
			
			Refresh(userid);
			ModifyHeadState = false;//表示把头像的标志位置为false  ，下次在选择头像完成后才置为true
			ModifyState = false;//表示把内容信息的标志位置为false  ，下次在修改信息完成后才置为true
			ModifyCardConfirmState = false;//表示把内容信息的标志位置为false  ，下次在修改信息完成后才置为true
			Utils.ShowToast(mContext, "提交成功！");
		}
	}
	
	
	
	/**
	 * 处理对话框的标题和显示
	 */
	@SuppressLint("InlinedApi")
	public void ProcessDialogInfo(String title){
		PersonalDetailsStruct struct= mPartTimeDB.GetPersonalDetailsInfo(mContext, userid);
		EditDialogBlock.setVisibility(View.VISIBLE);
		DialogTitle.setText(title);
		if(title.equals("姓名")){
			DialogContent.setHint("请输入姓名……");
			DialogContent.setInputType(InputType.TYPE_CLASS_TEXT);
			if(struct!=null && struct.mPersonalName!=null&&!struct.mPersonalName.equals("")&&!struct.mPersonalName.equals("null")){
				DialogContent.setText(struct.mPersonalName);
			}
		}else if(title.equals("邮箱")){
			DialogContent.setHint("请输入有效邮箱……");
			DialogContent.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			if(struct!=null && struct.mPersonalEmail!=null&&!struct.mPersonalEmail.equals("")&&!struct.mPersonalEmail.equals("null")){
				DialogContent.setText(struct.mPersonalEmail);
			}
		}else if(title.equals("工作经验")){
			DialogContent.setHint("请输入工作经验……");
			DialogContent.setInputType(InputType.TYPE_CLASS_TEXT);
			if(struct!=null && struct.mPersonalJobExp!=null&&!struct.mPersonalJobExp.equals("")&&!struct.mPersonalJobExp.equals("null")){
				DialogContent.setText(struct.mPersonalJobExp);
			}
		}else if(title.equals("手机号")){
			DialogContent.setHint("请输入手机号……");
			DialogContent.setInputType(InputType.TYPE_CLASS_NUMBER);
			if(struct!=null && struct.mPersonalCall!=null&&!struct.mPersonalCall.equals("")&&!struct.mPersonalCall.equals("null")){
				DialogContent.setText(struct.mPersonalCall);
			}
		}else if(title.equals("QQ")){
			DialogContent.setHint("请输入QQ……");
			DialogContent.setInputType(InputType.TYPE_CLASS_NUMBER);
			if(struct!=null && struct.mPersonalAddress!=null&&!struct.mPersonalAddress.equals("")&&!struct.mPersonalAddress.equals("null")){
				DialogContent.setText(struct.mPersonalAddress);
			}
		}
		
		
	}
	/**
	 * 处理对应的对话框确定值
	 * @param ensuretype
	 */
	@SuppressLint("NewApi")
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
		
		/**
		 * 判断是否改过了数据
		 */
		if(content!=null && !content.equals("")){
			ModifyState = true;
		}else{
			ModifyState = false;
		}
		switch(ensuretype){
		
		case CLICK_TYPE_NAME:InfoName.setText(content);
			break;
		case CLICK_TYPE_EMAIL:
			if(!Utils.GetlogIdType(content).equals("email")){
				ErrorTips.setVisibility(View.VISIBLE);
				ErrorTips.setText("邮箱的格式不对！");
				return;
			}else{
				ErrorTips.setVisibility(View.GONE);
				InfoEmail.setText(content);
			}
			break;
		case CLICK_TYPE_WORKEXP:InfoWorkExp.setText(content);
			break;
		case CLICK_TYPE_QQ:InfoQQ.setText(content);
			break;
		case CLICK_TYPE_CALL:InfoCall.setText(content);
			break;
		}
		
		ModifyState =true;//代表修改过内容
		EditDialogBlock.setVisibility(View.GONE);
	}
	/**
	 * 选择性别，0：男 1：女
	 * @param isel
	 */
	private void SelectionGender(int isel) {
		new AlertDialog.Builder(mContext)
		.setTitle("性别选择")
		.setIcon(android.R.drawable.ic_dialog_info)                
		.setSingleChoiceItems(R.array.gender_selection, isel, 
		  new DialogInterface.OnClickListener() {
		                            
		     public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		        ModifyState = true;
			    ShowGender(which);
		     }
		  }
		)
		.setNegativeButton("取消", null)
		.show();
	}
	
	private void ShowGender(int gen){
		int mgender = R.string.user_profile_gender_man;
		if (gen == 1)
			mgender = R.string.user_profile_gender_woman;
		InfoGender.setText(mgender);
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
		@SuppressLint("NewApi")
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
						String fname ="";
						switch(EnsureType){
						case CLICK_TYPE_HEADIMAGE:
							/*是否改过了头像*/
							ModifyHeadState = true;
							fname = "personal_details_headimage"+".jpg";
							HeadImage.setImageBitmap(mIcon);
							break;
						case CLICK_TYPE_CARDCONFIRM:
							ModifyCardConfirmState = true;
							fname = "personal_details_cardconfirmimage"+".jpg";
							CardConfirmImage.setImageBitmap(mIcon);
							break;
						}
						try {
							Utils.saveFile(mUserphotoData, saveDir + fname);
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
		
		
		
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if(keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0){
				if(ModifyHeadState==true)
					ProcessFinishData(ModifyHeadState);
				else
					ProcessFinishData(ModifyState);
			}
			return super.onKeyDown(keyCode, event);
			
			
		}
		
		/**
		 * 判断是否修改过数据，退出时做出操作
		 * @param flag
		 */
		private void ProcessFinishData(boolean flag) {
			if(flag==true){
			new AlertDialog.Builder(this)
			.setTitle(getResources().getText(R.string.user_profile_warming))
			.setMessage(getResources().getText(R.string.user_profile_warming_content))
			.setPositiveButton(getResources().getText(R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {
						finish();
					}
				}).setNegativeButton(getText(R.string.cancel), null).show();	
			}else{
				finish();
			}
		}
		/**
		 * 下载头像图片到本地head
		 */
		public class GetPersonalHeadAsynTask extends AsyncTask<String, Void, Boolean>{

			long errcode = -1;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}
			@Override
			protected Boolean doInBackground(String... params) {
				try {
					errcode = SvrOperation.getFileFromSvr(mContext, params[0],params[1]);
					
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
				
				 String path =Environment.getExternalStorageDirectory()+Constant.PARTTIMEJOB_HEAD+avatar.substring(avatar.lastIndexOf("/")+1);
			        if(Utils.getFilesExist(Environment.getExternalStorageDirectory()+Constant.PARTTIMEJOB_HEAD, avatar.substring(avatar.lastIndexOf("/")+1))){
			        	Bitmap mBp =Utils.getBitmapformpath(path);
			        	Bitmap mIcon = ThumbnailUtils.extractThumbnail(mBp,120,120);
			        	HeadImage.setImageBitmap(mIcon);
			        }else{
			        	HeadImage.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar_girl));
			        }
			}
		}
		/*imageloader部分*/
		private DisplayImageOptions options;  
		private ImageLoader mImageLoader;
		/**
		 * imageloader 加载图片展示，的一些参数的设置
		 * avatar_user_default 为默认图片
		 */
		@SuppressWarnings("deprecation")
		private void initDisplayOptions() {
			options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.loading_default_img)	//设置正在加载图片
			//.showImageOnLoading(R.drawable.ic_stub) //1.8.7新增
			.showImageForEmptyUri(R.drawable.drop_down_box_bg)	
			.showImageOnFail(R.drawable.loading_failed)	//设置加载失败图片
			.cacheInMemory(true)
			.cacheOnDisc(true)
			//.displayer(new RoundedBitmapDisplayer(20))	//设置图片角度,0为方形，360为圆角
			.build();
			
			
			mImageLoader = ImageLoader.getInstance();
		}
		@Override
		protected void onDestroy() {
			super.onDestroy();
			JudgeFreshFirstState = false;
			mImageLoader.clearMemoryCache();  
			mImageLoader.clearDiscCache();
		}
}
