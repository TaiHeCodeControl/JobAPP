package com.parttime.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

/**
 * 
 * @author 灰色的寂寞
 * @date 2014-11-10
 * @time 9：31
 * @function 判断sd卡中的文件，并且做一些操作，创建文件等
 */
public class FileUtils {

	private String SDPATH;

	public String getSDPATH() {
		return SDPATH;
	}
	public FileUtils() {
		//得到当前外部存储设备的目录
		// /SDCARD
		SDPATH = Environment.getExternalStorageDirectory() + "";
	}
	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */
	public File creatSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}
	
	public void  RemoveSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.delete();
	}
	
	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 */
	public File creatSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdirs();
		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 */
	public boolean isFileExist(String fileName){
		File file = new File(SDPATH + fileName);
		return file.exists();
	}
	
	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public boolean write2SDFromInput(String path,String fileName,InputStream input){
		File file = null;
		OutputStream output = null;
		boolean bSuccess = false;
		try{
			creatSDDir(path);
			file = creatSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte buffer [] = new byte[4 * 1024];
			while(true){
				int len = input.read(buffer);
				if (len == -1)
					break;
				output.write(buffer,0,len);
			}
			output.flush();
			bSuccess = true;
		
		}catch(Exception e){
			e.printStackTrace();
			
		}finally{
			try{
				output.close();
				if (bSuccess)
					return true;
				RemoveSDFile(fileName);
				return false;
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return false;
	}
	
	
}