package com.parttime.utils;

import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.parttime.constant.Constant;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.os.Environment;

/**
 * 
 * @author 灰色的寂寞
 * @date 2014-11-10
 * @time 9：31
 * @function 交互网络的工具
 */
public class Submit {

	private static final String Tag = Submit.class.getSimpleName();

	/**
	 * 通过请求的url和json数据，访问服务器发送请求完成上传和下载
	 * 
	 * @param path
	 *            请求数据的网址
	 * @param in
	 *            JSONObject 传递数据
	 * @return
	 */
	public static JSONObject Request(String path, JSONObject in) {

		try {
			HttpPost request = new HttpPost(path);
			if (in != null) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("json", in.toString()));
				StringEntity se = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
				request.setEntity(se);
			}
			// 设置连接超时时间和数据读取超时时间
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
			HttpConnectionParams.setSoTimeout(httpParams, 30000);
			// 新建HttpClient对象
			HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF_8");

				JSONObject result = new JSONObject(retSrc);
				return result;
			} else {
				switch (httpResponse.getStatusLine().getStatusCode()) {
				case 303:
					Utils.ShowLog(Tag, "/*303重定向*/");
					break;
				case 400:
					Utils.ShowLog(Tag, "/*400请求错误*/");
					break;
				case 401:
					Utils.ShowLog(Tag, "/*401未授权*/");
					break;
				case 403:
					Utils.ShowLog(Tag, "/*403禁止访问*/");
					break;
				case 404:
					Utils.ShowLog(Tag, "/*404文件未找到*/");
					break;
				case 500:
					Utils.ShowLog(Tag, " /*500服务器错误*/");
					break;
				}
			}
			return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		/**
		 * 遇到异常NetworkOnMainThreadException 4.0后的问题，需要重启一个线程执行http操作，或者直接强制执行 if
		 * (android.os.Build.VERSION.SDK_INT > 9) { StrictMode.ThreadPolicy
		 * policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		 * StrictMode.setThreadPolicy(policy); }
		 */
	}

	public static JSONObject Request1(String path, JSONObject in, Context context) {

		try {
			HttpPost request = new HttpPost(path);
			if (in != null) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("json", in.toString()));
				StringEntity se = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
				request.setEntity(se);
			}
			// 设置连接超时时间和数据读取超时时间
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
			HttpConnectionParams.setSoTimeout(httpParams, 30000);
			// 新建HttpClient对象
			HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF_8");
				Utils.save("temp.txt", retSrc, context);

				JSONObject result = new JSONObject(retSrc);
				return result;
			} else {
				switch (httpResponse.getStatusLine().getStatusCode()) {
				case 303:
					Utils.ShowLog(Tag, "/*303重定向*/");
					break;
				case 400:
					Utils.ShowLog(Tag, "/*400请求错误*/");
					break;
				case 401:
					Utils.ShowLog(Tag, "/*401未授权*/");
					break;
				case 403:
					Utils.ShowLog(Tag, "/*403禁止访问*/");
					break;
				case 404:
					Utils.ShowLog(Tag, "/*404文件未找到*/");
					break;
				case 500:
					Utils.ShowLog(Tag, " /*500服务器错误*/");
					break;
				}
			}
			return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
		/**
		 * 遇到异常NetworkOnMainThreadException 4.0后的问题，需要重启一个线程执行http操作，或者直接强制执行 if
		 * (android.os.Build.VERSION.SDK_INT > 9) { StrictMode.ThreadPolicy
		 * policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		 * StrictMode.setThreadPolicy(policy); }
		 */
	}

	/**
	 * 判断网络是否连接或可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return false;
		} else {
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info != null && info.isConnectedOrConnecting())
				return true;
			else
				return false;
		}
	}

	/**
	 * 通过请求的url和json数据，访问服务器发送请求完成上传和下载
	 * 
	 * @param path
	 *            请求数据的网址
	 * @param in
	 *            JSONObject 传递数据
	 * @return
	 */
	public static JSONObject RequestGet(String path, JSONObject in) {

		try {
			HttpGet request = new HttpGet(path);
			if (in != null) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("json", in.toString()));
				StringEntity se = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
				((HttpResponse) request).setEntity(se);
			}
			// 设置连接超时时间和数据读取超时时间
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
			HttpConnectionParams.setSoTimeout(httpParams, 30000);
			// 新建HttpClient对象
			HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF_8");
				// 网路传送数据加密时，使用
				// String decString = new EncDecNdkUtils().dec(retSrc);
				// JSONObject result = new JSONObject(decString);
				JSONObject result = new JSONObject(retSrc);
				return result;
			} else {
				switch (httpResponse.getStatusLine().getStatusCode()) {
				case 303:
					Utils.ShowLog(Tag, "/*303重定向*/");
					break;
				case 400:
					Utils.ShowLog(Tag, "/*400请求错误*/");
					break;
				case 401:
					Utils.ShowLog(Tag, "/*401未授权*/");
					break;
				case 403:
					Utils.ShowLog(Tag, "/*403禁止访问*/");
					break;
				case 404:
					Utils.ShowLog(Tag, "/*404文件未找到*/");
					break;
				case 500:
					Utils.ShowLog(Tag, " /*500服务器错误*/");
					break;
				}
			}
			return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
		/**
		 * 遇到异常NetworkOnMainThreadException 4.0后的问题，需要重启一个线程执行http操作，或者直接强制执行 if
		 * (android.os.Build.VERSION.SDK_INT > 9) { StrictMode.ThreadPolicy
		 * policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		 * StrictMode.setThreadPolicy(policy); }
		 */
	}

	public static boolean sendPostRequest(String path, Map<String, String> params, String enc) throws Exception {
		// title=dsfdsf&timelength=23&method=save
		StringBuilder sb = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), enc)).append('&');
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		byte[] entitydata = sb.toString().getBytes();// 得到实体的二进制数据
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setConnectTimeout(5 * 1000);
		conn.setDoOutput(true);// 如果通过post提交数据，必须设置允许对外输出数据
		// 至少要设置的两个请求头
		// Content-Type: application/x-www-form-urlencoded //内容类型
		// Content-Length: 38 //实体数据的长度
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//
		// 长度是实体的二进制长度
		conn.setRequestProperty("Content-Length", String.valueOf(entitydata.length));
		OutputStream outStream = conn.getOutputStream();
		outStream.write(entitydata);
		outStream.flush();
		outStream.close();
		if (conn.getResponseCode() == 200) {
			return true;
		}
		return false;
	}
}
