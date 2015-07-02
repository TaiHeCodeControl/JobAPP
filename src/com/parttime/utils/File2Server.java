package com.parttime.utils;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.parttime.svr.SvrInfo;

/**
 * 
 * @author 灰色的寂寞
 * @date 2014-11-10
 * @time 9：31
 * @function 根据文件uri下载文件，并且做一些处理，比如：存储到本地
 */
public class File2Server {
	private URL url=null;
	public final static int RESULT_ALREADY_EXISTS = 1;
	public final static int RESULT_SUCCEED = 0;
	public final static int RESULT_FAILED = -1;
	
	FileUtils fileUtils=new FileUtils();
	
	public int DownFile(String urlStr,String path,String fileName)
	{
		if(fileUtils.isFileExist(path+fileName)){
			return RESULT_ALREADY_EXISTS;
		}else{
		
		try {
			InputStream input=null;
			input = getInputStream(urlStr);
			boolean bret = fileUtils.write2SDFromInput(path, fileName, input);
			if(! bret)
			{
				return RESULT_FAILED;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return RESULT_FAILED;
		}
		
		}
		return RESULT_SUCCEED;
	}
	
	   /* 上传文件至Server的方法 */
    public int UploadFile(String path,String fileName)
    {
      String end ="\r\n";
      String twoHyphens ="--";
      String boundary ="*****";
      try
      {
        URL url =new URL(SvrInfo.SERVER_ADDR + "/api/upload.jsp");
        HttpURLConnection con=(HttpURLConnection)url.openConnection();
        /* 允许Input、Output，不使用Cache */
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        /* 设置传送的method=POST */
        con.setRequestMethod("POST");
        /* setRequestProperty */
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");
        con.setRequestProperty("Content-Type",
                           "multipart/form-data;boundary="+boundary);
        /* 设置DataOutputStream */
        DataOutputStream ds = new DataOutputStream(con.getOutputStream());
        ds.writeBytes(twoHyphens + boundary + end);
        ds.writeBytes("Content-Disposition: form-data; " +
                      "name=\"file1\";filename=\""+
                      fileName +"\""+ end);
        ds.writeBytes(end);  
        /* 取得文件的FileInputStream */
        FileInputStream fStream =new FileInputStream(path + fileName);
        /* 设置每次写入1024bytes */
        int bufferSize =1024;
        byte[] buffer =new byte[bufferSize];
        int length =-1;
        /* 从文件读取数据至缓冲区 */
        while((length = fStream.read(buffer)) !=-1)
        {
          /* 将资料写入DataOutputStream中 */
          ds.write(buffer, 0, length);
        }
        ds.writeBytes(end);
        ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
        /* close streams */
        fStream.close();
        ds.flush();
        /* 取得Response内容 */
        InputStream is = con.getInputStream();
        int ch;
        StringBuffer b =new StringBuffer();
        while( ( ch = is.read() ) !=-1 )
        {
          b.append( (char)ch );
        }
        ds.close();
        return RESULT_SUCCEED;
      }
      catch(Exception e)
      {
      }
      return RESULT_FAILED;
    }
  //由于得到一个InputStream对象是所有文件处理前必须的操作，所以将这个操作封装成了一个方法
       public InputStream getInputStream(String urlStr) throws IOException
       {     
    	   InputStream is=null;
    	    try {
				url=new URL(urlStr);
				HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
				is=urlConn.getInputStream();
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
    	    return is;
       }
}