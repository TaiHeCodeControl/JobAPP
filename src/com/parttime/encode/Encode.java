package com.parttime.encode;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.util.Base64;
/**
 * 
 * @author 灰色的寂寞
 * @function 做一些加密解码的操作
 * @date 2015-1-18
 * @time 14:41
 *
 */
public class Encode {
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
}
