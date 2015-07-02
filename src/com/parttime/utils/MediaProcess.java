package com.parttime.utils;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.parttime.constant.Constant;
import com.parttime.parttimejob.R;


/**
 * @author 灰色的寂寞
 * @function a class to enclose all of the function and features related to media
 *  <br>includes: picture, video, sound etc. 
 * @date 2015-1-18
 * @time 13:47
 */

public class MediaProcess {
	Intent intent = null;
	Context context;
	public int cropX, cropY;
	public int DialogId; //当前处理的事那个alertDlg
	private MediaRecorder mRecorder = null;

	private static final String TAG = "MediaProcess"; 
	public MediaProcess(Context ctx) {
		context = ctx;
		DialogId = -1;
		cropX = 120;
		cropY = 120;
	}
	
	public void setCropRange(int x, int y)
	{
		cropX = x;
		cropY = y;
	}
	
	public void setDlgId(int id)
	{
		DialogId = id;
	}
	
	public int  getDlgId()
	{
		return DialogId;
	}
	/**
	 * 根据用户选择结果，开始获取图片，获取结果会在onActivityResult中取得
	 * 如果外部存储卡没有被mounted，则提示后，退出
	 */
	public void Start2GetPicture(int SelectIdx)
	{
		if (!IsExternalStorageMounted())
			return;
		switch (SelectIdx)
		{
		case 0:
			LaunchIntent2GetMedia(Constant.MESSENGE_REQUEST_CODE_ATTACH_PICTURE);
			break;
		case 1:
			LaunchIntent2GetMedia(Constant.MESSENGE_REQUEST_CODE_TAKE_PHOTO);
			break;
		default:
			return;
		}
		
	}
	public void Start2GetImagePhoto(int SelectIdx)
	{
		if (!IsExternalStorageMounted())
			return;
		switch (SelectIdx)
		{
		case 0:
			LaunchIntent2GetMedia(Constant.MESSENGE_REQUEST_CODE_ATTACH_PHOTO);
			break;
		case 1:
			LaunchIntent2GetMedia(Constant.MESSENGE_REQUEST_CODE_TAKE_PHOTO);
		}
		
	}
	/**
	 * 根据用户选择结果，开始获取用户头像，获取结果会在onActivityResult中取得
	 * 如果外部存储卡没有被mounted，则提示后，退出
	 * @param  SelectIdx 
	 */
	public void Start2GetHeadPhoto(int SelectIdx)
	{
		if (!IsExternalStorageMounted())
			return;
		switch (SelectIdx)
		{
		case 0:
			LaunchIntent2GetMedia(Constant.MESSENGE_REQUEST_CODE_ATTACH_PHOTO);
			break;
		case 1:
			LaunchIntent2GetMedia(Constant.MESSENGE_REQUEST_CODE_TAKE_PHOTO);
		}
		
	}
	
	public void start2GetCoverPhoto(int SelectIdx)
	{
		if (!IsExternalStorageMounted())
			return;
		switch (SelectIdx)
		{
		case 0:
			LaunchIntent2GetMedia(Constant.MESSENGE_REQUEST_CODE_CROP_BIG_PHOTO_RESULT);
			break;
		case 1:
			LaunchIntent2GetMedia(Constant.MESSENGE_REQUEST_CODE_TAKE_PHOTO);
			break;
		case 3:
			LaunchIntent2GetMedia(Constant.MESSENGE_REQUEST_CODE_CROP_BIG_PHOTO_CAMERA_RESULT);
			break;
		default:
			break;
		}
		
	}
	/**
	 * 根据用户选择结果，开始获取用视频，获取结果会在onActivityResult中取得
	 * 如果外部存储卡没有被mounted，则提示后，退出
	 * @param SelectIdx
	 */
	public void Start2GetVideo(int SelectIdx)
	{
		if (!IsExternalStorageMounted())
			return;
		switch (SelectIdx)
		{
		case 0:
			LaunchIntent2GetMedia(Constant.MESSENGE_REQUEST_CODE_ATTACH_VIDEO);
			break;
		case 1:
			LaunchIntent2GetMedia(Constant.MESSENGE_REQUEST_CODE_TAKE_VIDEO);
		}
		
	}
	/**
	 * 根据用户选择结果，开始获取用音频，获取结果会在onActivityResult中取得
	 * 如果外部存储卡没有被mounted，则提示后，退出
	 * @param SelectIdx
	 */
	public void Start2GetAudio(int SelectIdx)
	{
		if (!IsExternalStorageMounted())
			return;
		switch (SelectIdx)
		{
		case 0:
			LaunchIntent2GetMedia(Constant.MESSENGE_REQUEST_CODE_ATTACH_SOUND);
			break;
		case 1:
			LaunchIntent2GetMedia(Constant.MESSENGE_REQUEST_CODE_TAKE_SOUND);
		}
	}
	/**
	 * 调用系统的media设备，来获得相应的媒体
	 * @param ReqCode 定义了请求媒体的类型，请参看Qijuzhu.app中的REQUEST_CODE_XXXX的定义
	 */
	public void LaunchIntent2GetMedia(int ReqCode) {
		Intent intent = null;
		switch (ReqCode) {
		case Constant.MESSENGE_REQUEST_CODE_TAKE_PHOTO:{
			File out = new File(Environment.getExternalStorageDirectory() + Constant.PARTTIMEJOB_TEMP_PATH, "job_temp.jpg");
			Uri uri = Uri.fromFile(out);
			intent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			break;
		}
			
		case Constant.MESSENGE_REQUEST_CODE_ATTACH_PHOTO:{
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			break;
		}
		
		case Constant.MESSENGE_REQUEST_CODE_CROP_BIG_PHOTO_CAMERA_RESULT:{
			File out = new File(Environment.getExternalStorageDirectory() + Constant.PARTTIMEJOB_TEMP_PATH, "job_temp.jpg");
			Uri uri = Uri.fromFile(out);
			intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(uri, "image/*");
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 720);
			intent.putExtra("outputY", 720);
			intent.putExtra("scale", true);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			intent.putExtra("return-data", false);
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
			intent.putExtra("noFaceDetection", true); // no face detection			 
			break;
		}
		case Constant.MESSENGE_REQUEST_CODE_CROP_BIG_PHOTO_RESULT:{
			File out = new File(Environment.getExternalStorageDirectory() + Constant.PARTTIMEJOB_TEMP_PATH, "job_temp.jpg");
			Uri uri = Uri.fromFile(out);
			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 720);
			intent.putExtra("outputY", 720);
			intent.putExtra("noFaceDetection", true);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", false);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());		
			break;
		}
		case Constant.MESSENGE_REQUEST_CODE_TAKE_PICTURE:{
			File out = new File(Environment.getExternalStorageDirectory()+ Constant.PARTTIMEJOB_TEMP_PATH, "job_temp.jpg");
			Uri uri = Uri.fromFile(out);
			intent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			break;
		}
		case Constant.MESSENGE_REQUEST_CODE_ATTACH_PICTURE:
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			break;
			
		case Constant.MESSENGE_REQUEST_CODE_ATTACH_VIDEO:
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("video/*");
			break;
			
		case Constant.MESSENGE_REQUEST_CODE_TAKE_VIDEO:	
			intent = new Intent(
					android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
			break;
			
		case Constant.MESSENGE_REQUEST_CODE_ATTACH_SOUND:
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType(ContentType.AUDIO_UNSPECIFIED);
			break;
			
		case Constant.MESSENGE_REQUEST_CODE_TAKE_SOUND:	
			intent = new Intent(
					android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
			break;
			
			
		}
		try {
		((Activity) context).startActivityForResult(intent,ReqCode);
		}catch(ActivityNotFoundException ae) {
			Toast.makeText(context, R.string.no_corresponding_intent, Toast.LENGTH_LONG).show();
		}
	}
	//-----------------------------------------------------------------------------------------
//	public boolean startRecording() {
//		if (mRecorder != null)
//		{
//			stopRecording();
//		}
//	    mRecorder = new MediaRecorder();
//	    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//	    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//	    //先存成临时文件
//	    String mAudioFile = Environment.getExternalStorageDirectory()+ Constant.Constant_SOUND + System.currentTimeMillis()+ ".amr";
//	    mRecorder.setOutputFile(mAudioFile);
//	    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//	    try {
//	            mRecorder.prepare();
//	    } catch (IOException e) {
//	            Log.e(TAG, "prepare() failed");
//	            return false;
//	    }
//	    mRecorder.start();
//	    return true;
//	}
	public void stopRecording() {
		if ( mRecorder != null)
		{
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}

	
	
	//---------------------------------------------------------------------------------------------------------    
	/**
	　　* 裁剪图片方法实现
	 　　*
	　　* @param uri
	　　*/
	public void startPhotoZoom(Uri uri, int RegCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		//intent.setClassName("com.android.camera", "com.android.camera.CropImage");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", cropX);
		intent.putExtra("outputY", cropY);
		intent.putExtra("return-data", true);
		((Activity) context).startActivityForResult(intent, RegCode);
	}
	
	/**
	　　* 返回裁剪之后的图片数据
	 　　*
	　　* @param picdata
	　　*/
	private BitmapDrawable getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			return new BitmapDrawable(photo);
		}
		return null;
	}
	/**
	 * 检测外部SD是否mounted
	 * @return true -- mounted, false -- unmounted
	 */
	public boolean IsExternalStorageMounted(){
		String status = Environment
				.getExternalStorageState();
		if (!status
				.equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(context, R.string.External_storage_unmounted,
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	/**
	 * 打开一个系统缺省选择对话框，让用户选择一个选项
	 * @param TypeArrayID  --选项数组资源ID
	 * @param sellister,  选择结果监听句柄
	 * @param DlgId    对话框的id
	 * @return 选择了那一个选项，从0开始
	 */
	public AlertDialog SelectMethod(int TypeArrayID, DialogInterface.OnClickListener sellister, int DlgId)
	{
		DialogId = DlgId;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.chose);
		builder.setItems(TypeArrayID, sellister);
		return builder.create();
	}
	
//	/**
//	 * 将摄像头拍到的照片或者来自相册的照片，保存到我们自己的qijuzhu目录下 /sd/qijuzhu/Pictures/
//	 * 并返回保存的文件绝对路径 
//	 * @param originalUri 要保存的图片的Uri
//	 * @return String 
//	 */
//	public String SavePicture(Uri originalUri)
//	{
//		String mPicFile = Utils.PicUri2FilePath(context, originalUri);
//		return SavePicture(mPicFile);
//	}
//	/**
//	 * 将摄像头拍到的照片或者来自相册的照片，保存到我们自己的qijuzhu目录下 /sd/qijuzhu/Pictures/
//	 * 并返回保存的文件绝对路径 
//	 * @param mPicFile 要保存的图片的绝对路径
//	 * @return
//	 */
//	public String SavePicture(String mPicFile)
//	{
//		FileOutputStream out = null;
//		String mAttachImageName = null;
//
//		if (mPicFile != null) {
//			try {
//				Bitmap bm = Utils.loadImageWithAutoRotation(mPicFile,Qijuzhu.ATTACHMENT_BMP_MAX_HEIGHT);// 获取图片 
//			
//				mAttachImageName = Environment.getExternalStorageDirectory() + Qijuzhu.PICTURE_LIB_PATH + "img_" + System.currentTimeMillis() + ".jpg";
//
//				File attachedImage = new File(mAttachImageName);
//				out = new FileOutputStream(attachedImage);
//				bm.compress(Bitmap.CompressFormat.JPEG, 100, out);// 把数据写入文件
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					out.flush();
//					out.close();
//					//保存EXIF信息
//					ExifHelper exifH = new ExifHelper();
//					exifH.createInFile(mPicFile);
//					exifH.createOutFile(mAttachImageName);
//					exifH.readExifData();
//					exifH.setOrientation(ExifInterface.ORIENTATION_NORMAL);//图像已经被改正了，因此，将方向改为normal
//					exifH.writeExifData();
//					
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return mAttachImageName;
//	}
//	/*
//	 * 查看一下这个文件是否是起居注目录下的文件
//	 */
//	public boolean IsQijuzhuPic(String PicPath)
//	{
//		String dir = Environment.getExternalStorageDirectory() + Qijuzhu.PICTURE_LIB_PATH;
//		if (PicPath == null)
//			return false;
//		if (PicPath.startsWith(dir))
//			return true;
//		return false;
//	}
	/**
	 * 返回一个正方形的thumbnail
	 * @param originalUri
	 * @param MaxWidth
	 * @return
	 */
	public Bitmap GetRectThumbnail(Uri originalUri,int MaxWidth)
	{
		Bitmap ThumbnailBm = null;
	
		if (originalUri != null) {
			String mPicFile = Utils.PicUri2FilePath(context, originalUri);
			
			Bitmap bm = Utils.loadImageWithAutoRotationRectangle(mPicFile,MaxWidth);// 获取图片 
			
			ThumbnailBm = ThumbnailUtils.extractThumbnail(bm,MaxWidth,MaxWidth);
			
			if (bm != null && !bm.isRecycled())
			{
				bm.recycle();
				bm = null;
			}
		}
		return ThumbnailBm;
	}
	/**
	 * 按比例返回一个宽度最大为MaxWidth的thumbnail
	 * @param originalUri
	 * @param MaxWidth
	 * @return
	 */
	public Bitmap GetThumbnail(Uri originalUri,int MaxWidth)
	{
		Bitmap ThumbnailBm = null;
	
		if (originalUri != null) {
			String mPicFile = Utils.PicUri2FilePath(context, originalUri);
				
			ThumbnailBm = Utils.loadImageWithAutoRotation(mPicFile,MaxWidth);// 获取图片 
		}
		return ThumbnailBm;
	}
}