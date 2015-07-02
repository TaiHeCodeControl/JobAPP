package com.parttime.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.parttime.constant.Constant;
import com.parttime.data.PartTimeDB;
import com.parttime.fragment.MainFragment;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.CompanyDetailsStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.MediaProcess;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;
/**
 * 
 * @author 灰色的寂寞
 * @function 企业详情页，处理一些企业的信息数据
 * @date 2015-1-20
 * @time 13:41
 *
 */
@SuppressLint("NewApi")
public class Company_Details_Introduce_Activity extends Activity implements OnGetGeoCoderResultListener{
	private Context mContext;
	/**view 部分****************************************************/
	/* 标题部分*/
	private TextView CenterTitle,RightBtn;
	private TextView mLeftBackBtn;
	
	private RelativeLayout CompanyNameBlock,JobTypeBlock,DetailsPositionBlock,CompanyConfirmBlock,CompanyEmailBlock,CompanyContactBlock,CompanyIdentityBlock;
	private TextView CompanyNameText,DetailsPositionText,CompanyConfirmText,CompanyEmailText,ConpanyContactText;

	private RelativeLayout ModifyHeadBlock,ModifyPropertyBlock;
	private TextView ModifyPropertyText;
	private ImageView ModifyHeadImage,ConpanyIdentityImage;
	
	private TextView CompanyCompleteBtn;
	/*等待框部分*/
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;
	/*编辑对话框部分*/
	private RelativeLayout EditDialogBlock;
	private TextView DialogTitle,DialogCancelBtn,DialogEnsureBtn,ErrorTips;
	private EditText DialogContent;
	/**常量部分****************************************************/
	/* 返回*/
	private static final int CLICK_TYPE_TO_BACK=0;//退出
	private static final int CLICK_TYPE_LOGOUT=1;//注销
	private static final int CLICK_TYPE_COMPLETE_BTN=3;//完成按钮
	
	private static final int CLICK_TYPE_MODIFY_PROPERTY=4;//修改公司性质
	private static final int CLICK_TYPE_MODIFY_NAME=5;//修改公司名称
	private static final int CLICK_TYPE_MODIFY_EMAIL=6;//修改企业邮箱
	private static final int CLICK_TYPE_MODIFY_CONTACT=7;//修改企业联系方式
	private static final int CLICK_TYPE_MODIFY_DETAILS_ADDRESS=8;//修改详细地址
	private static final int CLICK_TYPE_MODIFY_CONFIRM=9;//修改认证
	
	private static final int CLICK_TYPE_DIALOG_CANCEL=10;
	private static final int CLICK_TYPE_DIALOG_ENSURE=11;
	
	private static final int CLICK_TYPE_CHANGE_HEADIMAGE=12;
	private static final int CLICK_TYPE_MODIFY_IDENTITY=13;
	
	/*选图部分*/
	private final static int MENU_PHOTO = 101;
	private final static int DATE_DIALOG_ID = 102;
	private Bitmap mBitmap;
	
	private int VIEW_TYPE = 0;//0:详情查看默认 1：修改企业信息
	
	private SharedPreferences sharedPreferences;
	private long userid=-1;
	private String avatar = "";
	private String name = "";
	private PartTimeDB DB ;
	
	CompanyDetailsStruct mstruct = new CompanyDetailsStruct();
	
	private boolean ModifyHeadState = false;
	private boolean ModifyIdentityState = false;
	private boolean ModifyState = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.company_details_introduce_layout);
		
		
		mContext = this;
		
		DB = PartTimeDB.getInstance(mContext);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		userid = sharedPreferences.getLong("userid", -1);
		avatar = sharedPreferences.getString("avatar", "");
		name = sharedPreferences.getString("username", "");
		initDisplayOptions();
		initViews();
		
		
	}

	private void initViews() {
		/** 标题部分*/
		mLeftBackBtn = (TextView)findViewById(R.id.mtitle_back_arrow);
		CenterTitle = (TextView)findViewById(R.id.mtitle_text);
		RightBtn = (TextView)findViewById(R.id.mtitle_right_text);
		CenterTitle.setText("企业详情");

		RightBtn.setVisibility(View.VISIBLE);
		RightBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
		RightBtn.setText("注销");
		
		mLeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_TO_BACK));
		RightBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_LOGOUT));
		
		/* 等待对话框部分 */
		WaitingText = (TextView)findViewById(R.id.Waiting_text);
		WaitingDlg  =(RelativeLayout)findViewById(R.id.Waiting_dlg);
		
		/**
		 * 编辑内容部分
		 */
		
		EditDialogBlock = (RelativeLayout) findViewById(R.id.edit_dialog_layout);
		DialogTitle = (TextView)findViewById(R.id.edit_dialog_title);
		DialogCancelBtn = (TextView)findViewById(R.id.edit_cancel);
		DialogEnsureBtn = (TextView)findViewById(R.id.edit_ensure);
		ErrorTips = (TextView)findViewById(R.id.edit_error_tips);
		DialogContent = (EditText)findViewById(R.id.edit_dialog_content);
		DialogContent.setVisibility(View.VISIBLE);
		
		DialogCancelBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_DIALOG_CANCEL));
		DialogEnsureBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_DIALOG_ENSURE));
		/**
		 * 企业详情内容部分
		 */
		CompanyNameBlock = (RelativeLayout) findViewById(R.id.rl_company_name_block);
		JobTypeBlock = (RelativeLayout) findViewById(R.id.rl_job_type_block);
		DetailsPositionBlock = (RelativeLayout) findViewById(R.id.rl_details_position_block);
		CompanyConfirmBlock = (RelativeLayout) findViewById(R.id.rl_company_confirm_block);
		CompanyEmailBlock = (RelativeLayout) findViewById(R.id.rl_company_email_block);
		CompanyContactBlock = (RelativeLayout) findViewById(R.id.rl_company_contact_block);
		CompanyIdentityBlock = (RelativeLayout) findViewById(R.id.rl_company_identity_image_block);
		 
		CompanyNameText = (TextView) findViewById(R.id.company_name_text);
		DetailsPositionText = (TextView) findViewById(R.id.details_position_text);
		CompanyConfirmText = (TextView) findViewById(R.id.company_confirm_text);
		CompanyEmailText = (TextView) findViewById(R.id.company_email_text);
		ConpanyContactText = (TextView) findViewById(R.id.company_contact_text);
		ConpanyIdentityImage = (ImageView) findViewById(R.id.company_identity_image);
		
		/*修改的内容部分*/
		ModifyHeadBlock = (RelativeLayout) findViewById(R.id.rl_details_modify_headimage_block);
		ModifyPropertyBlock = (RelativeLayout) findViewById(R.id.rl_details_property_block);
		ModifyPropertyText = (TextView) findViewById(R.id.details_property_text);
		ModifyHeadImage = (ImageView) findViewById(R.id.company_details_modify_headimage);
		ModifyHeadImage.setOnClickListener(new MyOnClickListener(CLICK_TYPE_CHANGE_HEADIMAGE));
		
		CompanyCompleteBtn = (TextView) findViewById(R.id.company_complete_btn);
		CompanyCompleteBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_COMPLETE_BTN));
		
		ModifyPropertyText.setOnClickListener(new MyOnClickListener(CLICK_TYPE_MODIFY_PROPERTY));
		CompanyNameText.setOnClickListener(new MyOnClickListener(CLICK_TYPE_MODIFY_NAME));
		DetailsPositionText.setOnClickListener(new MyOnClickListener(CLICK_TYPE_MODIFY_DETAILS_ADDRESS));
		CompanyConfirmText.setOnClickListener(new MyOnClickListener(CLICK_TYPE_MODIFY_CONFIRM));
		
		CompanyEmailText.setOnClickListener(new MyOnClickListener(CLICK_TYPE_MODIFY_EMAIL));
		ConpanyContactText.setOnClickListener(new MyOnClickListener(CLICK_TYPE_MODIFY_CONTACT));
		ConpanyIdentityImage.setOnClickListener(new MyOnClickListener(CLICK_TYPE_MODIFY_IDENTITY));
		
		
		if(Submit.isNetworkAvailable(mContext)){
			new GetCompanyInfoAsynTask().execute((int)userid);
		}else{
			CompanyDetailsStruct struct = DB.getCompanyInfoList(mContext, (int)userid);
			initData(struct);
		}
		
		
		
	}
	
	public void initData(CompanyDetailsStruct struct){
		ModifyPropertyText.setText(struct.mModifyProperty);
		CompanyNameText.setText(struct.mCompanyName);
		DetailsPositionText.setText(struct.mDetailsPosition);
		CompanyConfirmText.setText(struct.mCompanyConfirm);
		CompanyEmailText.setText(struct.mCompanyEmail);
		ConpanyContactText.setText(struct.mCompanyContact);
		
		String path =Environment.getExternalStorageDirectory()+Constant.PARTTIMEJOB_HEAD+avatar.substring(avatar.lastIndexOf("/")+1);
        if(Utils.getFilesExist(Environment.getExternalStorageDirectory()+Constant.PARTTIMEJOB_HEAD, avatar.substring(avatar.lastIndexOf("/")+1))){
        	Bitmap mBp =Utils.getBitmapformpath(path);
        	Bitmap mIcon = ThumbnailUtils.extractThumbnail(mBp,120,120);
        	ModifyHeadImage.setImageBitmap(mIcon);
        }else{
        	if(avatar!=null && !avatar.equals(""))
				mImageLoader.displayImage(avatar, ModifyHeadImage, options);//http://www.xunlvshi.cn/project/jianzhi365//Uploads/avatar/2
//        	ModifyHeadImage.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
        }
        
        mImageLoader.displayImage(struct.mCompanyIdentity, ConpanyIdentityImage, options);
	}
	
	/**
	 * 自定义ClickListener管理Click事件
	 * */
	public class MyOnClickListener implements OnClickListener{
		private int type=-1;//默认为-1，什么也不做
		public MyOnClickListener(int type){
			this.type = type;
		}
		@Override
		public void onClick(View v) {
			mManageClick(type);
		}
		
	}
	
	/**
	 * 通过判断type，管理click事件
	 * @param mtype
	 */
	private int EnsureType=-1;
	@SuppressWarnings("deprecation")
	private void mManageClick(int mtype) {
		switch (mtype) {
		case CLICK_TYPE_TO_BACK:
			finish();
			break;
		case CLICK_TYPE_LOGOUT:
			finish();
			sharedPreferences.edit().putString("login_username", "").commit();
			sharedPreferences.edit().clear().commit();
			Utils.ClearPageFlagToShared(mContext);
			ExitHx();
			Utils.intent2Class(mContext, LoginActivity.class);
			break;
		case CLICK_TYPE_CHANGE_HEADIMAGE:
			EnsureType =CLICK_TYPE_CHANGE_HEADIMAGE; 
			showDialog(MENU_PHOTO);
			break;
		case CLICK_TYPE_COMPLETE_BTN:
			//完成按钮
			getContentFromTextView();
			break;
			
			
		/***修改内容部分，对话框的标题*******************************************/
		case CLICK_TYPE_MODIFY_PROPERTY:
			EnsureType =CLICK_TYPE_MODIFY_PROPERTY;
			ProcessDialogInfo("企业性质");
			break;
		case CLICK_TYPE_MODIFY_NAME:
			EnsureType =CLICK_TYPE_MODIFY_NAME;
			ProcessDialogInfo("公司名称");
			break;
		case CLICK_TYPE_MODIFY_DETAILS_ADDRESS:
			EnsureType =CLICK_TYPE_MODIFY_DETAILS_ADDRESS;
			ProcessDialogInfo("详细地址");
			break;
		case CLICK_TYPE_MODIFY_CONFIRM:
			EnsureType =CLICK_TYPE_MODIFY_CONFIRM;
			ProcessDialogInfo("企业认证");
			break;
		case CLICK_TYPE_MODIFY_EMAIL:
			EnsureType =CLICK_TYPE_MODIFY_EMAIL;
			ProcessDialogInfo("企业邮箱");
			break;
		case CLICK_TYPE_MODIFY_CONTACT:
			EnsureType =CLICK_TYPE_MODIFY_CONTACT;
			ProcessDialogInfo("手机号码");
			break;
		case CLICK_TYPE_MODIFY_IDENTITY:
			EnsureType =CLICK_TYPE_MODIFY_IDENTITY;
			showDialog(MENU_PHOTO);
		
			break;
		case CLICK_TYPE_DIALOG_CANCEL:
			EditDialogBlock.setVisibility(View.GONE);
			break;
		case CLICK_TYPE_DIALOG_ENSURE:
			
			ProcessDialogEnsure(EnsureType);
			break;
		}
	}
	
	/**
	 * 处理对话框的标题和显示
	 */
	public void ProcessDialogInfo(String title){
		CompanyDetailsStruct struct = DB.getCompanyInfoList(mContext, (int)userid);
		CompanyCompleteBtn.setVisibility(View.VISIBLE);
		EditDialogBlock.setVisibility(View.VISIBLE);
		DialogTitle.setText(title);
		if(title.equals("详细地址")){
			DialogContent.setHint("请输入详细地址信息\neg..北京市昌平区生命科学园");
			if(struct.mDetailsPosition!=null&&!struct.mDetailsPosition.equals("")&&!struct.mDetailsPosition.equals("null"))
			DialogContent.setText(struct.mDetailsPosition);
		}else if(title.equals("企业性质")){
			DialogContent.setHint("请输入信息……");
			if(struct.mModifyProperty!=null&&!struct.mModifyProperty.equals("")&&!struct.mModifyProperty.equals("null"))
			DialogContent.setText(struct.mModifyProperty);
		}else if(title.equals("公司名称")){
			DialogContent.setHint("请输入信息……");
			if(struct.mCompanyName!=null&&!struct.mCompanyName.equals("")&&!struct.mCompanyName.equals("null"))
				DialogContent.setText(struct.mCompanyName);
		}else if(title.equals("企业认证")){
			DialogContent.setHint("请输入信息……");
			if(struct.mCompanyConfirm!=null&&!struct.mCompanyConfirm.equals("")&&!struct.mCompanyConfirm.equals("null"))
				DialogContent.setText(struct.mCompanyConfirm);
		}else if(title.equals("企业邮箱")){
			DialogContent.setHint("请输入信息……");
			if(struct.mCompanyEmail!=null&&!struct.mCompanyEmail.equals("")&&!struct.mCompanyEmail.equals("null"))
				DialogContent.setText(struct.mCompanyEmail);
		}else if(title.equals("手机号码")){
			DialogContent.setHint("请输入信息……");
			if(struct.mCompanyContact!=null&&!struct.mCompanyContact.equals("")&&!struct.mCompanyContact.equals("null"))
				DialogContent.setText(struct.mCompanyContact);
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
			ModifyState = true;
			ErrorTips.setVisibility(View.GONE);
		}
		switch(ensuretype){
		case CLICK_TYPE_MODIFY_PROPERTY:
			ModifyPropertyText.setText(content);
			break;
		case CLICK_TYPE_MODIFY_NAME:
			CompanyNameText.setText(content);
			break;
		case CLICK_TYPE_MODIFY_DETAILS_ADDRESS:
			if(content.contains("省")|| content.contains("市")){
				DetailsPositionText.setText(content);
			}else{
				ErrorTips.setVisibility(View.VISIBLE);
				ErrorTips.setText("请输入正确的地址！");
				return;
			}
			break;
		case CLICK_TYPE_MODIFY_CONFIRM:
			CompanyConfirmText.setText(content);
			break;
		case CLICK_TYPE_MODIFY_EMAIL:
			String type = Utils.GetlogIdType(content);
			if(type.equals("email"))
				CompanyEmailText.setText(content);
			else
				Utils.ShowToast(mContext, "请输入有效邮箱");
			break;
		case CLICK_TYPE_MODIFY_CONTACT:
			ConpanyContactText.setText(content);
			break;
		}
		
		EditDialogBlock.setVisibility(View.GONE);
	}
	
	
	/**
	 * 获取企业修改后的信息（头像存储的时候分开弄）
	 * @return
	 */
	public void getContentFromTextView(){
		
		mstruct .mCompanyName = CompanyNameText.getText().toString().trim();
		mstruct .mDetailsPosition = DetailsPositionText.getText().toString().trim();
		
		
		mstruct .mModifyProperty=  ModifyPropertyText.getText().toString().trim();
		mstruct .mCompanyConfirm = CompanyConfirmText.getText().toString().trim();
		mstruct .mCompanyEmail= CompanyEmailText.getText().toString().trim();
		mstruct .mCompanyContact = ConpanyContactText.getText().toString().trim();
		String saveDir = Environment.getExternalStorageDirectory()+ Constant.PARTTIMEJOB_IMAGE;
		mstruct .mModifyHeadImage =saveDir + "company_image.jpg";
		mstruct .mCompanyIdentity =saveDir +"company_identity_image.jpg";
		mstruct .muserid = (int)userid;
		if(mstruct .mDetailsPosition !=null&&!mstruct .mDetailsPosition.equals("")&&!mstruct .mDetailsPosition.equals("null")){
			if(ModifyHeadState == false && ModifyState == false &&ModifyIdentityState == false){
				Utils.ShowToast(mContext, "您没有修改数据！");
			}else{
				getPoi(mstruct .mDetailsPosition);
			}
		}else{
			new SubmitCompanyInfoAsynTask().execute(mstruct);
		}
	}
	
	
	public void getPoi(String postion){
		try {
			String [] list = Utils.getLocation(postion);
			String cityorprovince = list[0];
			// 初始化搜索模块，注册事件监听
			GeoCoder mSearch = GeoCoder.newInstance();
			mSearch.setOnGetGeoCodeResultListener(this);
			mSearch.geocode(new GeoCodeOption().city(cityorprovince).address(postion));
		} catch (Exception e) {
			e.printStackTrace();
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
						
						mGetHeadImage = mIcon;
						String fname ="";
						switch(EnsureType){
						case CLICK_TYPE_CHANGE_HEADIMAGE:
							fname = "company_image"+".jpg";
							ModifyHeadState =true;
							ModifyHeadImage.setImageBitmap(mIcon);
							break;
						case CLICK_TYPE_MODIFY_IDENTITY:
							fname ="company_identity_image"+".jpg";
							ConpanyIdentityImage.setImageBitmap(mIcon);
							ModifyIdentityState=true;
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
		private Bitmap mGetHeadImage=null;
	/**==========================================================================================================================*/	
	/**==========================================================================================================================*/	
	/**==========================================================================================================================*/	

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
		
		ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();  
		private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {  
		        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());  
		         @Override  
		         public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {  
		              if (loadedImage != null) {  
		                  ImageView imageView = (ImageView) view;  
		                  boolean firstDisplay = !displayedImages.contains(imageUri);  
		                  if (firstDisplay) {  
		                       FadeInBitmapDisplayer.animate(imageView, 500);  
		                       displayedImages.add(imageUri);  
		                    }  
		              }  
		        }  
		}  
		public void ShowWaiting(String text) {
			WaitingText.setText(text);
			WaitingDlg.setVisibility(View.VISIBLE);
		}

		public void HideWaiting() {
			WaitingDlg.setVisibility(View.GONE);
		}
		
		/**
		 * 获取个人信心 
		 */
		public class GetCompanyInfoAsynTask extends AsyncTask<Integer, Void, Boolean>{
			long errcode=-1;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				ShowWaiting("正在加载...");
			}
			@Override
			protected Boolean doInBackground(Integer... params) {
				try {
					errcode = SvrOperation.GetCompanyInfoTask(mContext, params[0]);
					
					if (errcode !=SvrInfo.SVR_RESULT_SUCCESS )
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
				CompanyDetailsStruct struct = DB.getCompanyInfoList(mContext, (int)userid);
				initData(struct);
				if(result== false){
					Utils.ShowToast(mContext, "刷新失败，请检查网络");
					return ;
				}
				
				
			}
		}
		
		/**
		 * 提交个人信心 
		 */
		public class SubmitCompanyInfoAsynTask extends AsyncTask<CompanyDetailsStruct, Void, Boolean>{
			long errcode = -1;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if(ModifyHeadState || ModifyState||ModifyIdentityState){
					ShowWaiting("正在提交...");
				}
			}
			@Override
			protected Boolean doInBackground(CompanyDetailsStruct... params) {
				try {
					if(ModifyHeadState){
						errcode = SvrOperation.uploadImage(mContext, userid, params[0].mModifyHeadImage);
						if (errcode !=SvrInfo.SVR_RESULT_SUCCESS)
							return false;
					}
					
					if(ModifyState){
						errcode = SvrOperation.ModifyCompanyInfoTask(mContext, params[0]);
						if (errcode !=SvrInfo.SVR_RESULT_SUCCESS)
							return false;
					}
					
					if(ModifyIdentityState){
						errcode = SvrOperation.uploadConfirmImage(mContext, userid, "企业", params[0].mCompanyIdentity);
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
				if(Submit.isNetworkAvailable(mContext)){
					new GetCompanyInfoAsynTask().execute((int)userid);
				}else{
					CompanyDetailsStruct struct = DB.getCompanyInfoList(mContext, (int)userid);
					initData(struct);
				}
				
				String path =Environment.getExternalStorageDirectory()+Constant.PARTTIMEJOB_HEAD;
				if(Utils.getFilesExist(path, avatar.substring(avatar.lastIndexOf("/")+1))){
					Utils.deleteFileFromSDcard(path, avatar.substring(avatar.lastIndexOf("/")+1));
				}
				new GetPersonalHeadAsynTask().execute(avatar,Constant.PARTTIMEJOB_HEAD);
				
				ModifyHeadState = false;
				ModifyState =false;
				Utils.ShowToast(mContext, "提交成功！");
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
			        	ModifyHeadImage.setImageBitmap(mIcon);
			        }else{
			        	ModifyHeadImage.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar_girl));
			        }
			}
		}
		
		private void ExitHx()
		{
			EMChatManager.getInstance().logout(new EMCallBack(){

				@Override
				public void onError(int arg0, String arg1) {
					Log.e("hu", "onError   "+arg0+"   name   "+arg1);				
					
				}

				@Override
				public void onProgress(int progress, String arg1) {
					Log.e("hu", "progress   "+progress+"   name   "+arg1);				
					
				}

				@Override
				public void onSuccess() {
	              Log.e("hu", "success exit");	
	              
	              MainFragment.mainFragment.finish();
				}
				
			});//此方法为异步方法
		}

		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Utils.ShowToast(mContext, "抱歉，未能找到结果");
				return;
			}
			
			mstruct.mPOI= result.getLocation().latitude+","+result.getLocation().longitude;
			if(ModifyHeadState == false && ModifyState == false&&ModifyIdentityState==false){
				Utils.ShowToast(mContext, "您没有修改数据！");
			}else{
				new SubmitCompanyInfoAsynTask().execute(mstruct);
			}
			
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Utils.ShowToast(mContext,"抱歉，未能找到结果");
				return;
			}
		}
		
		
		@Override
		protected void onDestroy() {
			super.onDestroy();
			mImageLoader.clearMemoryCache();  
			mImageLoader.clearDiscCache();
		}
}
