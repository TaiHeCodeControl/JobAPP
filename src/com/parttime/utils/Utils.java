package com.parttime.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract.Contacts.Data;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.parttime.constant.Constant;
import com.parttime.parttimejob.R;

/**
 * 
 * @author 灰色的寂寞
 * @date 2014-11-10
 * @time 9：31
 * @function 处理信息和字符串图片等资源
 */
public class Utils {
	private static final String TAG = "Utils";

	public static boolean detail_flag = false;

	public static String LoginError = "";

	public static boolean insert_flag = false;

	public static String Vcode = ""; // 短信验证码

	public static String BaseMessage = "";

	public static ArrayList<String> HxUserNames = new ArrayList<String>(); // 存最近联系人的list

	public static String interface_flag = "";

	public static final class WORH {
		public static final int BY_WIDTH = 0;
		public static final int BY_HEIGHT = 1;
	}

	/**
	 * 判断手机格式是否正确
	 * 
	 * @param numphone
	 *            电话号码
	 * @return
	 */
	public static boolean IsCorrectNumPhone(String numphone) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(numphone);
		return m.matches();
	}

	/**
	 * 验证邮箱格式是否正确
	 * 
	 * @param logid
	 * @return
	 */
	public static String GetlogIdType(String logid) {
		String youmeiid_patten = "[0-9]*$";
		String imei_patten_phone = "[0-9]{15}";
		String imei_patten_bp = "[0-9]{20}";
		String email_patten = "[a-zA-Z0-9_]+[@][a-zA-Z0-9\\-]+([\\.com]|[\\.cn])";
		String phone_patten = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
		Pattern pat = Pattern.compile(email_patten);
		Matcher mat = pat.matcher(logid);
		if (mat.find()) {
			return "email";
		}
		pat = Pattern.compile(phone_patten);
		mat = pat.matcher(logid);
		if (mat.find()) {
			return "telnum";
		}
		pat = Pattern.compile(imei_patten_phone);
		mat = pat.matcher(logid);
		if (mat.find()) {
			return "imei";
		}
		pat = Pattern.compile(imei_patten_bp);
		mat = pat.matcher(logid);
		if (mat.find()) {
			return "imei";
		}

		pat = Pattern.compile(youmeiid_patten);
		mat = pat.matcher(logid);
		if (mat.find()) {
			return "id";
		}
		return null;
	}

	/**
	 * 判断密码是否过于简单,必须是字母、数字或下划线
	 * 
	 * @param ps
	 * @return
	 */
	public static boolean IsValidPwd(String ps) {
		String pwdsimple = "^(?!(?:[^a-zA-Z]|\\D|[a-zA-Z0-9])$).{8,}$";
		// String pwdsimple="^[a-zA-Z0-9_]+$";
		Pattern pattern = Pattern.compile(pwdsimple);
		Matcher match = pattern.matcher(ps);
		Boolean state = match.matches();

		return state;
	}

	public static boolean IsValidPostCode(String ps) {
		String patten = "^[0-9]{6}$";
		Pattern pat = Pattern.compile(patten);
		Matcher mat = pat.matcher(ps);
		if (mat.find()) {
			return true;
		}
		return false;
	}

	/**
	 * 根据路径和名字判断文件是否已经存在
	 * 
	 * @param strPath
	 * @param name
	 * @return
	 */
	public static boolean getFilesExist(final String strPath, String name) {
		File file = new File(strPath);

		File[] files = file.listFiles();
		if (files == null || files.length < 0 || files.length == 0) {
			return false;
		}
		for (int i = 0; i < files.length; i++) {
			String subName = files[i].getName();
			if (subName.equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据路径和名字判断文件是否已经存在
	 * 
	 * @param strPath
	 * @param name
	 * @return
	 */
	public static boolean getFilesExist(final String strPath) {
		File file = new File(strPath);
		if (file.exists())
			return true;
		return false;
	}

	/**
	 * 获取给定路径里的列表（名称）
	 * 
	 * @param strPath
	 * @return
	 */
	public static ArrayList<String> getFileList(final String strPath) {
		ArrayList<String> list = new ArrayList<String>();

		File file = new File(strPath);

		File[] files = file.listFiles();
		if (files == null || files.length < 0 || files.length == 0) {
			return list;
		}
		for (int i = 0; i < files.length; i++) {
			list.add(files[i].getName());
		}
		return list;
	}

	/**
	 * 获取给定路径里的列表（名称）
	 * 
	 * @param strPath
	 * @return
	 */
	public static boolean CleanFileList(final String strPath) {
		File file = new File(strPath);

		File[] files = file.listFiles();
		if (files == null || files.length < 0 || files.length == 0) {
			return true;
		}
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
		return true;
	}

	/**
	 * 删除文件，从sd卡中
	 * 
	 * @param strPath
	 * @param name
	 */
	public static void deleteFileFromSDcard(String strPath, String name) {
		File file = new File(strPath + name);
		if (file.exists()) {
			file.delete();
		}
	}

	public static boolean deleteFileFromSDcard(String strPath) {
		File file = new File(strPath);
		if (file.exists()) {
			file.delete();
			return true;
		}
		return false;
	}

	/**
	 * 根据路径得到文件个数
	 * 
	 * @param strPath
	 * @return
	 */
	public static int getPathFilesNumber(String strPath) {
		File file = new File(strPath);

		File[] files = file.listFiles();

		if (files == null)
			return 0;
		return files.length;
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			int byteread = 0;
			int nameIndex = oldPath.lastIndexOf("/") + 1;// 获取名字的index
			String oldFileName = oldPath.substring(nameIndex);// 获取名字
			boolean state = Utils.getFilesExist(newPath, oldFileName);// 判断是否存在
			while (state) {
				int sameIndex = oldFileName.lastIndexOf(".");
				String suffix = oldFileName.substring(sameIndex);
				String snameString = oldFileName.substring(0, sameIndex);
				oldFileName = snameString + "_1" + suffix;
				state = Utils.getFilesExist(newPath, oldFileName);
			}
			String newFile = newPath + oldFileName;// 组成新的文件路径
			File newfile = new File(newFile);
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				if (!newfile.exists()) {
					newfile.createNewFile();
				}
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				@SuppressWarnings("resource")
				FileOutputStream fs = new FileOutputStream(newFile);
				byte buffer[] = new byte[1024];
				while ((byteread = inStream.read(buffer)) > 0) {
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 判断是否为平板
	 * 
	 * @return
	 */
	public static boolean isPad(Context mContext) {
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		double x = Math.pow(dm.widthPixels, 2);
		double y = Math.pow(dm.heightPixels, 2);
		double z = Math.sqrt(x + y);
		double screenInches = (z / (dm.density * 160));

		if (screenInches >= 6.0) {
			return true;
		}
		return false;
	}

	/**
	 * 保存数据到data区的一个文件中
	 * 
	 * @param data
	 * @param filePath
	 * @throws IOException
	 */
	public static void saveFile(byte[] data, String filePath) throws IOException {
		File file = new File(filePath);

		File parentFile = file.getParentFile();
		if (!parentFile.exists() && !parentFile.mkdirs()) {
			Log.e(TAG, "[TempFileProvider] tempStoreFd: " + parentFile.getPath() + "does not exist!");
			return;
		}
		if (file.exists()) {
			file.delete();
		}
		file = new File(filePath);
		file.createNewFile();
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(file);
			// 每当执行到这时就抛出异常FileNotFoundException
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (data != null) {
			fOut.write(data);
		}
		try {
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * write data to file use fileName
	 * 
	 * @param context
	 * @param fileName
	 * @param data
	 * @return
	 */
	public static boolean WriteDataFile(Context context, String fileName, String data) {
		FileOutputStream outStream = null;
		try {
			outStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			outStream.write(data.getBytes());
			outStream.close();
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (outStream != null)
					outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * get file use fileName
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String readDataFile(Context context, String fileName) {
		String res = "";
		FileInputStream inStream = null;
		try {
			inStream = context.openFileInput(fileName);
			int length = inStream.available();
			byte[] buffer = new byte[length];
			inStream.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (inStream != null)
					inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return res;
	}

	public static Bitmap getbitmap(String imageUri) {
		Log.e("hu", "getbitmap:" + imageUri);
		// 显示网络上的图片
		Bitmap bitmap = null;
		try {
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();

			Log.v(TAG, "image download finished." + imageUri);
		} catch (IOException e) {
			e.printStackTrace();
			Log.v(TAG, "getbitmap bmp fail---");
			return null;
		}
		return bitmap;
	}

	/**
	 * 根据Uri得到一个图片的绝对文件路径
	 * 
	 * @param mContext
	 * @param picUri
	 * @return
	 */
	public static String PicUri2FilePath(Context mContext, Uri picUri) {

		if (picUri.getScheme().compareTo("content") == 0) {
			String[] proj = { MediaStore.Images.Media.DATA };

			CursorLoader loader = new CursorLoader(mContext, picUri, proj, null, null, null);
			Cursor cursor = loader.loadInBackground();
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);

		} else {// 如果返回的是以file://
				// 开始的
			String mNewFilePath = picUri.getPath();
			return mNewFilePath;
		}
	}

	public static int GetImgRotation(String FilePath) {
		int rotate = 0;
		if (FilePath == null)
			return 0;
		try {
			ExifInterface exif = new ExifInterface(FilePath);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			}

		} catch (IOException e) {
			// Log.w(TAG, msg)
		}
		return rotate;
	}

	public static Bitmap getBitmap3(String path, int width, int height) {
		try {
			File dst = new File(path);
			if (null == dst || !dst.exists())
				return null;

			BitmapFactory.Options opts = null;
			opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(dst.getPath(), opts);
			float widthratio = opts.outWidth / width;
			float heightratio = opts.outHeight / height;
			if (widthratio < heightratio)
				opts.inSampleSize = Math.round(widthratio);
			else
				// 根据高度来计算采样率
				opts.inSampleSize = Math.round(heightratio);
			opts.inJustDecodeBounds = false;
			opts.inInputShareable = true;
			opts.inPurgeable = true;

			Bitmap bitmap = BitmapFactory.decodeFile(dst.getPath(), opts);
			return bitmap;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap RotateImg(Bitmap bmp, int rotate) {
		if (rotate == 0)
			return bmp;
		Matrix matrix = new Matrix();
		matrix.reset();
		matrix.postRotate(rotate);
		Bitmap bMapRotate = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
		return bMapRotate;
	}

	public static Bitmap loadImageWithAutoRotationRectangle(String mPicFile, int mOutImageWidth) {
		Bitmap bitmap = null;
		Bitmap bretbmp = null;
		if (mPicFile != null) {
			int rot = Utils.GetImgRotation(mPicFile);
			switch (rot)// 不用旋转
			{
			case 0:
				bretbmp = Utils.getBitmap3(mPicFile, mOutImageWidth, mOutImageWidth);
				break;
			case 90:
			case 270:
				bitmap = Utils.getBitmap3(mPicFile, mOutImageWidth, mOutImageWidth);
				bretbmp = Utils.RotateImg(bitmap, rot);

				if (bretbmp == null) // 旋转失败，使用原来的图片
					bretbmp = bitmap;
				else
					bitmap.recycle();
				break;
			case 180:
				bitmap = Utils.getBitmap3(mPicFile, mOutImageWidth, mOutImageWidth);
				bretbmp = Utils.RotateImg(bitmap, rot);

				if (bretbmp == null) // 旋转失败，使用原来的图片
					bretbmp = bitmap;
				else
					bitmap.recycle();
				break;
			}
		}
		return bretbmp;
	}

	// 以下为有关image处理的函数
	// ===================================================================
	public static Bitmap loadImageWithAutoRotation(String mPicFile, int mOutImageWidth) {
		Bitmap bitmap = null;
		Bitmap bretbmp = null;
		if (mPicFile != null) {
			int rot = Utils.GetImgRotation(mPicFile);
			switch (rot)// 不用旋转
			{
			case 0:
				bretbmp = Utils.getBitmap(mPicFile, mOutImageWidth, Utils.WORH.BY_WIDTH);
				break;
			case 90:
			case 270:
				bitmap = Utils.getBitmap(mPicFile, mOutImageWidth, Utils.WORH.BY_HEIGHT);
				bretbmp = Utils.RotateImg(bitmap, rot);

				if (bretbmp == null) // 旋转失败，使用原来的图片
					bretbmp = bitmap;
				else
					bitmap.recycle();
				break;
			case 180:
				bitmap = Utils.getBitmap(mPicFile, mOutImageWidth, Utils.WORH.BY_WIDTH);
				bretbmp = Utils.RotateImg(bitmap, rot);

				if (bretbmp == null) // 旋转失败，使用原来的图片
					bretbmp = bitmap;
				else
					bitmap.recycle();
				break;
			}
		}
		return bretbmp;
	}

	/**
	 * 通过路径获取图片
	 * 
	 * @param path
	 *            :图片的路径/yimi/images/
	 *            20150210_333bffdace37528053870dad8e99a530.jpg
	 * @return bitmap
	 * */
	public static Bitmap getBitmapformpath(String path, Context mcontext) {

		if (getFilesExist(path)) {
			Drawable shortcutIcon = Drawable.createFromPath(path);
			Bitmap bitmap = null;
			if (shortcutIcon != null) {
				bitmap = ((BitmapDrawable) shortcutIcon).getBitmap();
			}
			return bitmap;
		} else {
			return DrableToBitmap(mcontext.getResources().getDrawable(R.drawable.signed_on_default));
		}
	}

	/**
	 * 通过路径获取图片
	 * 
	 * @param path
	 *            :图片的路径/yimi/images/
	 *            20150210_333bffdace37528053870dad8e99a530.jpg
	 * @return bitmap
	 * */
	public static Bitmap getBitmapformpath(String path) {

		Drawable shortcutIcon = Drawable.createFromPath(path);
		Bitmap bitmap = null;
		if (shortcutIcon != null) {
			bitmap = ((BitmapDrawable) shortcutIcon).getBitmap();
		}
		return bitmap;
	}

	/**
	 * 打开一个图片文件，返回一个高度/宽度最大为width的bitmap，是高度还是宽度由wORh来决定
	 * 
	 * @param path
	 *            图片文件路径
	 * @param width
	 *            最大宽度/高度
	 * @param wORh
	 *            是高度，还是宽度
	 * @return
	 */
	public static Bitmap getBitmap(String path, int width, int wORh) {
		// int outWidth, outHeight;
		try {
			File dst = new File(path);
			if (null == dst || !dst.exists())
				return null;

			BitmapFactory.Options opts = null;
			opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(dst.getPath(), opts);
			if (wORh == WORH.BY_WIDTH)// 根据宽度来计算采样率
				opts.inSampleSize = opts.outWidth / width;
			else
				// 根据高度来计算采样率
				opts.inSampleSize = opts.outHeight / width;
			opts.inJustDecodeBounds = false;
			opts.inInputShareable = true;
			opts.inPurgeable = true;

			Bitmap bitmap = BitmapFactory.decodeFile(dst.getPath(), opts);
			if (bitmap == null) {
				return null;
			}
			return bitmap;

		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 图片转成string
	 * 
	 * @param bitmap
	 * @return
	 */
	public static String convertIconToString(Bitmap bitmap) {
		synchronized (bitmap) {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
			bitmap.compress(CompressFormat.PNG, 100, baos);
			byte[] appicon = baos.toByteArray();// 转为byte数组
			return Base64.encodeToString(appicon, Base64.DEFAULT);
		}
	}

	/**
	 * 
	 * @param imgPath
	 * @param bitmap
	 * @param imgFormat
	 *            图片格式
	 * @return
	 */
	public static String imgToBase64(Bitmap bitmap) {
		if (bitmap == null) {
			// bitmap not found!!
		}
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

			out.flush();
			out.close();

			byte[] imgBytes = out.toByteArray();
			return Base64.encodeToString(imgBytes, Base64.DEFAULT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 利用BASE64Encoder对文件进行编码
	 */
	public static String FileBase64String(String fname) {
		// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(fname);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		// 返回Base64编码过的字节数组字符串
		return Base64.encodeToString(data, Base64.DEFAULT);
	}

	@SuppressWarnings("rawtypes")
	public static void intent2Class(Context mContext, Class mActivity) {
		Intent intent = new Intent(mContext, mActivity);
		mContext.startActivity(intent);
	}

	public static void ShowToast(Context mContext, String text) {
		Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
	}

	public static void ShowToast(Context mContext, int text) {
		Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
	}

	public static void ShowLog(String tag, String text) {
		Log.i(tag, text);
	}

	public static List<String> StringArray2List(Context mContext, int array) {
		List<String> mlist = new ArrayList<String>();
		int i = 0;
		String array1[] = mContext.getResources().getStringArray(array);
		for (; i < array1.length; i++) {
			mlist.add(array1[i]);
		}
		return mlist;
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号的VersionCode
	 */
	public static int getVersionCode(Context mContext) {
		try {
			PackageManager manager = mContext.getPackageManager();
			PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
			int version = info.versionCode;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号的VersionName
	 */
	public static String getVersionName(Context mContext) {
		try {
			PackageManager manager = mContext.getPackageManager();
			PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "1.0";
		}
	}

	/**
	 * 检查存储卡是否插入
	 * 
	 * @return
	 */
	public static boolean isHasSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 从Url中获取Bitmap
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getBitmapFormUrl(String url) {
		Bitmap bitmap = null;
		HttpURLConnection con = null;
		try {
			URL mImageUrl = new URL(url);
			con = (HttpURLConnection) mImageUrl.openConnection();
			con.setConnectTimeout(1000);
			con.setReadTimeout(1000);
			con.setDoInput(true);
			con.setDoOutput(true);
			bitmap = BitmapFactory.decodeStream(con.getInputStream());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return bitmap;
	}

	public static HashMap<String, Integer> getWindowParams(Context context) {
		WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		window.getDefaultDisplay().getMetrics(outMetrics);
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		int windowwidth = outMetrics.widthPixels;
		int windowheight = outMetrics.heightPixels;
		map.put("width", windowwidth);
		map.put("height", windowheight);
		return map;

	}

	public static String[] SplitStringToList(String splitText) {
		String[] array = splitText.split("-");
		return array;
	}

	/**
	 * 处理电话号码13位，中间为暗文
	 * 
	 * @param telNum
	 */
	public static String ParseTelNumberToHint(String telNum) {

		int length = telNum.length();
		if (length > 7) {
			String head_hint = telNum.substring(0, 3);// 前三位
			String tail_hint = telNum.substring(length - 4);// 后四位
			return head_hint + "****" + tail_hint;
		} else {
			return telNum;
		}
	}

	/**
	 * 处理姓名为暗文，姓名显示
	 * 
	 * @param telNum
	 */
	public static String ParseNameToHint(String name) {
		if (name != null && !name.equals("") && !name.equals("null")) {
			int length = name.length();
			String finalName = name;
			String head_hint = "";
			switch (length) {
			case 1:
				break;
			case 2:
				head_hint = name.substring(0, 1);// 前1位
				finalName = head_hint + "*";
				break;
			case 3:
				head_hint = name.substring(0, 1);// 前1位
				finalName = head_hint + "**";
				break;
			case 4:
				head_hint = name.substring(0, 2);// 前2位
				finalName = head_hint + "**";
				break;
			case 5:
				head_hint = name.substring(0, 2);// 前2位
				finalName = head_hint + "***";
				break;
			}

			if (length > 5) {
				head_hint = name.substring(0, 2);// 前2位
				finalName = head_hint + "***";
			}
			return finalName;
		} else
			return name;
	}

	/**
	 * 处理姓名为暗文，姓名显示
	 * 
	 * @param telNum
	 */
	public static String ParseNameToHintMultiStar(String name) {
		if (name != null && !name.equals("") && !name.equals("null")) {
			int length = name.length();
			String finalName = name;
			String head_hint = "";
			if (length == 4) {
				head_hint = name.substring(0, 2);// 前2位
				finalName = head_hint + "****";

			} else {
				head_hint = name.substring(0, 1);// 前1位
				finalName = head_hint + "****";
			}
			return finalName;
		} else
			return name;
	}

	/**
	 * 遍历sdcard下的某一个文件夹
	 * 
	 * @param files
	 * @return 图片的路径
	 */
	public static ArrayList<String> imagesFromSdcard(File files) {
		ArrayList<String> list = new ArrayList<String>();
		if (files.isDirectory()) {
			File[] fs = files.listFiles();
			if (fs != null) {
				for (int i = 0; i < fs.length; i++) {
					File f = fs[i];
					list.add(f.getPath());
				}
			}
		}
		return list;
	}

	/**
	 * 保存文件
	 * 
	 * @param filename
	 *            文件名称
	 * @param content
	 *            文件内容
	 * @throws IOException
	 */
	public static void save(String filename, String content1, Context context) throws Exception {
		// 利用javaIO实现文件的保存
		FileOutputStream outStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
		outStream.write(content1.getBytes());// 向文件中写入数据，将字符串转换为字节
		outStream.close();
	}

	/**
	 * 把Drawable变成Bitmap
	 * 
	 * @param d
	 * @return
	 */
	public static Bitmap DrableToBitmap(Drawable d) {
		BitmapDrawable bd = (BitmapDrawable) d;
		Bitmap bm = bd.getBitmap();
		return bm;
	}

	/**
	 * 存储不同page的标记
	 * 
	 * @param mContext
	 * @param flag
	 *            1:homepage 2.job 3.volunteer 4.communication 5.personal
	 *            6.company
	 */
	public static void CommitPageFlagToShared(Context mContext, int flag) {
		SharedPreferences.Editor sharedEditor = mContext.getSharedPreferences(Constant.FINISH_ACTIVITY_SHARED, -100).edit();
		sharedEditor.clear().commit();
		sharedEditor.putInt(Constant.FINISH_ACTIVITY_FLAG, flag).commit();
	}

	/**
	 * 清除不同page的标记
	 * 
	 * @param mContext
	 * @param flag
	 *            1:homepage 2.job 3.volunteer 4.communication 5.personal
	 */
	public static void ClearPageFlagToShared(Context mContext) {
		SharedPreferences.Editor sharedEditor = mContext.getSharedPreferences(Constant.FINISH_ACTIVITY_SHARED, -100).edit();
		sharedEditor.clear().commit();
	}

	/**
	 * 获取page标记
	 * 
	 * @return 默认1：homepage
	 */
	public static int getPageFlagFromShared(Context mContext) {
		SharedPreferences shared = mContext.getSharedPreferences(Constant.FINISH_ACTIVITY_SHARED, -100);
		return shared.getInt(Constant.FINISH_ACTIVITY_FLAG, 1);
	}

	/**
	 * 分割详细地址
	 * 
	 * @param location
	 *            详细地址
	 * @return
	 */
	public static String[] getLocation(String location) {
		String[] strs = null;
		if (location.contains("省")) {
			strs = location.split("省");
		} else if (location.contains("市")) {
			strs = location.split("市");
		} else {
			return null;
		}
		return strs;
	}

	/**
	 * 获取系统当前时间
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}

	/**
	 * 把字符串转化为时间戳
	 */
	public static long getTime(String str_time) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = null;
		long time = 0;
		try {
			d = formatter.parse(str_time);
			time = d.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return time;
	}

	/**
	 * 把时间戳转换为日期
	 * 
	 * @param time1
	 *            时间戳
	 * @return
	 */
	public static String getcTime(long time1) {
		// 时间戳转化为Sting或Date
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(time1);// 获取当前时间
		String str = format.format(curDate);
		return str;

	}

}
