package com.zzti.fengyongge.uploadimages.upload;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

public class UploadHelper {
	
	static String imageurl;
	private static JSONObject parseObject;
	
	/** 
     * 网络连接是否可用 
     */  
    public static boolean isConnnected(Context context) {  
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
        if (null != connectivityManager) {  
            NetworkInfo networkInfo[] = connectivityManager.getAllNetworkInfo();  
            if (null != networkInfo) {  
                for (NetworkInfo info : networkInfo) {  
                    if (info.getState() == NetworkInfo.State.CONNECTED) {  
                        return true;  
                    }  
                }  
            }  
        }  
        return false;  
    }  
	
	public static JSONObject post(Context context,String actionUrl, Map<String, Drawable> files) { 
		if (isConnnected(context)) {
			
			
			try
			{
				 StringBuilder sb2 = new StringBuilder(); 
				  String BOUNDARY = java.util.UUID.randomUUID().toString();
				  String PREFIX = "--" , LINEND = "\r\n";
				  String MULTIPART_FROM_DATA = "multipart/form-data"; 
				  String CHARSET = "UTF-8";

				  URL uri = new URL(actionUrl); 
				  HttpURLConnection conn = (HttpURLConnection) uri.openConnection(); 
				  conn.setReadTimeout(15 * 1000); 
				  conn.setDoInput(true);
				  conn.setDoOutput(true);
				  conn.setUseCaches(false); 
				  conn.setRequestMethod("POST"); 
				  conn.setRequestProperty("Connection", "Keep-Alive");
				  //conn.setRequestProperty("ENCTYPE", MULTIPART_FROM_DATA); 
				  conn.setRequestProperty("Charsert", "UTF-8"); 
				  //conn.setRequestProperty("uploaded_file", "aaa"); 
				  conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY); 

				  
				  StringBuilder sb = new StringBuilder(); 
				  
				  DataOutputStream outStream = new DataOutputStream(conn.getOutputStream()); 
				  
				  
				  if(files!=null ){
				    for (Map.Entry<String, Drawable> file: files.entrySet()) { 
				      StringBuilder sb1 = new StringBuilder(); 
				      sb1.append(PREFIX); 
				      sb1.append(BOUNDARY); 
				      sb1.append(LINEND); 
				      sb1.append("Content-Disposition: form-data; name=\"" + file.getKey() + "\"; filename=\"" + "image.jpg" + "\"" +LINEND);
				      sb1.append("Content-Type: application/octet-stream; charset="+CHARSET+LINEND);
				      sb1.append(LINEND);
				      outStream.write(sb1.toString().getBytes()); 

				      InputStream is = FormatTools.getInstance().Drawable2InputStream(file.getValue());
				      byte[] buffer = new byte[1024]; 
				      int len = 0; 
				      while ((len = is.read(buffer)) != -1) { 
				        outStream.write(buffer, 0, len); 
				      }

				      is.close(); 
				      outStream.write(LINEND.getBytes()); 
				    }
				  }
				  
				  
				  byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes(); 
				  outStream.write(end_data); 
				  outStream.flush(); 
				  

				  int res = conn.getResponseCode(); 
				  
				  Log.d("AA",  "code:" + res);
				  
				  InputStream in = null;
				  if (res == 200) {
				    in = conn.getInputStream(); 
				    int ch; 
				    
				    while ((ch = in.read()) != -1) { 
				      sb2.append((char) ch); 
				    }
				    Log.d("AA",  "data:" + sb2.toString());
				  }
				  
				  JSONObject parseObject = JSON.parseObject(sb2.toString());
				  return parseObject;  
			}
			catch ( Exception e)
			{
				Log.d("AA", "error post: " + e.getMessage());
				e.printStackTrace();
			}
			return null;
		}else {
			
			return null;
		}
		
		}
	
	
	

	public static JSONObject post(String actionUrl, Map<String, String> params, 
		    Map<String, Drawable> files) { 
			try
			{
				 StringBuilder sb2 = new StringBuilder(); 
				  String BOUNDARY = java.util.UUID.randomUUID().toString();
				  String PREFIX = "--" , LINEND = "\r\n";
				  String MULTIPART_FROM_DATA = "multipart/form-data"; 
				  String CHARSET = "UTF-8";

				  URL uri = new URL(actionUrl); 
				  HttpURLConnection conn = (HttpURLConnection) uri.openConnection(); 
				  conn.setReadTimeout(15 * 1000); 
				  conn.setDoInput(true);
				  conn.setDoOutput(true);
				  conn.setUseCaches(false); 
				  conn.setRequestMethod("POST"); 
				  conn.setRequestProperty("Connection", "Keep-Alive");
				  //conn.setRequestProperty("ENCTYPE", MULTIPART_FROM_DATA); 
				  conn.setRequestProperty("Charsert", "UTF-8"); 
				  //conn.setRequestProperty("uploaded_file", "aaa"); 
				  conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY); 

				  
				  StringBuilder sb = new StringBuilder(); 
				  for (Map.Entry<String, String> entry : params.entrySet()) { 
				    sb.append(PREFIX); 
				    sb.append(BOUNDARY); 
				    sb.append(LINEND); 
				    sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
				    sb.append("Content-Type: text/plain; charset=" + CHARSET+LINEND);
				    sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				    sb.append(LINEND);
				    sb.append(entry.getValue()); 
				    sb.append(LINEND); 
				  } 
				  DataOutputStream outStream = new DataOutputStream(conn.getOutputStream()); 
				  outStream.write(sb.toString().getBytes());
				  
				  
				  if(files!=null ){
				    for (Map.Entry<String, Drawable> file: files.entrySet()) { 
				      StringBuilder sb1 = new StringBuilder(); 
				      sb1.append(PREFIX); 
				      sb1.append(BOUNDARY); 
				      sb1.append(LINEND); 
				      sb1.append("Content-Disposition: form-data; name=\"" + file.getKey() + "\"; filename=\"" + (file.getKey()+".jpg") + "\"" +LINEND);
				      sb1.append("Content-Type: application/octet-stream; charset="+CHARSET+LINEND);
				      sb1.append(LINEND);
				      outStream.write(sb1.toString().getBytes()); 

				      InputStream is = FormatTools.getInstance().Drawable2InputStream(file.getValue());
				      byte[] buffer = new byte[1024]; 
				      int len = 0; 
				      while ((len = is.read(buffer)) != -1) { 
				        outStream.write(buffer, 0, len); 
				      }

				      is.close(); 
				      outStream.write(LINEND.getBytes()); 
				    }
				  }
				  
				  
				  byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes(); 
				  outStream.write(end_data); 
				  outStream.flush(); 

				  int res = conn.getResponseCode(); 
				  
				  Log.d("AA",  "code:" + res);
				  
				  InputStream in = null;
				  if (res == 200) {
				    in = conn.getInputStream(); 
				    int ch; 
				    
				    while ((ch = in.read()) != -1) { 
				      sb2.append((char) ch); 

				    }
				    Log.d("AA",  "data:" + sb2.toString());
				  }
				  
				  JSONObject parseObject = JSON.parseObject(sb2.toString());
				  
				  
				  
				  return parseObject; 
			}
			catch ( Exception e)
			{
				Log.d("AA", "error post: " + e.getMessage());
				e.printStackTrace();
			}
			return null;
		}
	
	public static JSONObject postFile(String actionUrl, Map<String, String> params, 
			Map<String, String> files) { 
		try
		{
			StringBuilder sb2 = new StringBuilder(); 
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--" , LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data"; 
			String CHARSET = "UTF-8";
			
			URL uri = new URL(actionUrl); 
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection(); 
			conn.setReadTimeout(15 * 1000); 
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false); 
			conn.setRequestMethod("POST"); 
			conn.setRequestProperty("Connection", "Keep-Alive");
			//conn.setRequestProperty("ENCTYPE", MULTIPART_FROM_DATA); 
			conn.setRequestProperty("Charsert", "UTF-8"); 
			//conn.setRequestProperty("uploaded_file", "aaa"); 
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY); 
			
			
			StringBuilder sb = new StringBuilder(); 
			for (Map.Entry<String, String> entry : params.entrySet()) { 
				sb.append(PREFIX); 
				sb.append(BOUNDARY); 
				sb.append(LINEND); 
				sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET+LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(entry.getValue()); 
				sb.append(LINEND); 
			} 
			DataOutputStream outStream = new DataOutputStream(conn.getOutputStream()); 
			outStream.write(sb.toString().getBytes());
			
			
			if(files!=null ){
				for (Map.Entry<String, String> file: files.entrySet()) { 
					StringBuilder sb1 = new StringBuilder(); 
					sb1.append(PREFIX); 
					sb1.append(BOUNDARY); 
					sb1.append(LINEND); 
					sb1.append("Content-Disposition: form-data; name=\"" + file.getKey() + "\"; filename=\"" + (file.getKey()+".jpg") + "\"" +LINEND);
					sb1.append("Content-Type: application/octet-stream; charset="+CHARSET+LINEND);
					sb1.append(LINEND);
					outStream.write(sb1.toString().getBytes()); 
					
					FileInputStream fileInputStream = new FileInputStream(new File(file.getValue()));
					InputStream is = FormatTools.getInstance().getInputStream(fileInputStream);
					byte[] buffer = new byte[1024]; 
					int len = 0; 
					while ((len = is.read(buffer)) != -1) { 
						outStream.write(buffer, 0, len); 
					}
					
					is.close(); 
					outStream.write(LINEND.getBytes()); 
				}
			}
			
			
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes(); 
			outStream.write(end_data); 
			outStream.flush(); 
			
			int res = conn.getResponseCode(); 
			
			Log.d("AA",  "code:" + res);
			
			InputStream in = null;
			if (res == 200) {
				in = conn.getInputStream(); 
				int ch; 
				
				while ((ch = in.read()) != -1) { 
					sb2.append((char) ch); 
					
				}
				Log.d("AA",  "data:" + sb2.toString());
			}
			
			JSONObject parseObject = JSON.parseObject(sb2.toString());
			
			return parseObject; 
		}
		catch ( Exception e)
		{
			Log.d("AA", "error post: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	//多图上传
	public static JSONObject post(String actionUrl, Map<String, String> params, 
		   List<Drawable> files,String img_name) { 
			try
			{
				 StringBuilder sb2 = new StringBuilder(); 
				  String BOUNDARY = java.util.UUID.randomUUID().toString();
				  String PREFIX = "--" , LINEND = "\r\n";
				  String MULTIPART_FROM_DATA = "multipart/form-data"; 
				  String CHARSET = "UTF-8";

				  URL uri = new URL(actionUrl); 
				  HttpURLConnection conn = (HttpURLConnection) uri.openConnection(); 
				  conn.setReadTimeout(15 * 1000); 
				  conn.setDoInput(true);
				  conn.setDoOutput(true);
				  conn.setUseCaches(false); 
				  conn.setRequestMethod("POST"); 
				  conn.setRequestProperty("Connection", "Keep-Alive");
				  //conn.setRequestProperty("ENCTYPE", MULTIPART_FROM_DATA); 
				  conn.setRequestProperty("Charsert", "UTF-8"); 
				  //conn.setRequestProperty("uploaded_file", "aaa"); 
				  conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY); 

				  StringBuilder sb = new StringBuilder(); 
				  for (Map.Entry<String, String> entry : params.entrySet()) { 
				    sb.append(PREFIX); 
				    sb.append(BOUNDARY); 
				    sb.append(LINEND); 
				    sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
				    sb.append("Content-Type: text/plain; charset=" + CHARSET+LINEND);
				    sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				    sb.append(LINEND);
				    sb.append(entry.getValue()); 
				    sb.append(LINEND); 
				  } 
				  DataOutputStream outStream = new DataOutputStream(conn.getOutputStream()); 
				  outStream.write(sb.toString().getBytes());
				  
				  
				  if(files!=null ){
//				    for (Map.Entry<String, Drawable> file: files.entrySet()) { 
					  for (int i = 0; i < files.size(); i++) {
						
				      StringBuilder sb1 = new StringBuilder(); 
				      sb1.append(PREFIX); 
				      sb1.append(BOUNDARY); 
				      sb1.append(LINEND); 
				      sb1.append("Content-Disposition: form-data; name=\"" + img_name+"[]" + "\"; filename=\"" + (img_name+"[]"+".jpg") + "\"" +LINEND);
				      sb1.append("Content-Type: application/octet-stream; charset="+CHARSET+LINEND);
				      sb1.append(LINEND);
				      outStream.write(sb1.toString().getBytes()); 

				      InputStream is = FormatTools.getInstance().Drawable2InputStream(files.get(i));
				      byte[] buffer = new byte[1024]; 
				      int len = 0; 
				      while ((len = is.read(buffer)) != -1) { 
				        outStream.write(buffer, 0, len); 
				      }

				      is.close(); 
				      outStream.write(LINEND.getBytes()); 
				    }
				  }
				  
				  
				  byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes(); 
				  outStream.write(end_data); 
				  outStream.flush(); 

				  int res = conn.getResponseCode(); 
				  
				  Log.d("AA",  "code:" + res);
				  
				  InputStream in = null;
				  if (res == 200) {
				    in = conn.getInputStream(); 
				    int ch; 
				    
				    while ((ch = in.read()) != -1) { 
				      sb2.append((char) ch); 

				    }
				    Log.d("AA",  "data:" + sb2.toString());
				  }
				  
				  JSONObject parseObject = JSON.parseObject(sb2.toString());
				  
				  
				  
				  return parseObject; 
			}
			catch ( Exception e)
			{
				
				Log.d("AA", "error post: " + e.getMessage());
				
				e.printStackTrace();
			}
			return null;
		}
	
	
	//多图上传
	public static JSONObject addPost(String actionUrl, Map<String, String> params, 
			List<Drawable> files,String img_name) { 
		try
		{
			StringBuilder sb2 = new StringBuilder(); 
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--" , LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data"; 
			String CHARSET = "UTF-8";
			
			URL uri = new URL(actionUrl); 
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection(); 
			conn.setReadTimeout(60 * 500); 
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false); 
			conn.setRequestMethod("POST"); 
			conn.setRequestProperty("Connection", "Keep-Alive");
			//conn.setRequestProperty("ENCTYPE", MULTIPART_FROM_DATA); 
			conn.setRequestProperty("Charsert", "UTF-8"); 
			//conn.setRequestProperty("uploaded_file", "aaa"); 
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY); 
			
			StringBuilder sb = new StringBuilder(); 
			for (Map.Entry<String, String> entry : params.entrySet()) { 
				sb.append(PREFIX); 
				sb.append(BOUNDARY); 
				sb.append(LINEND); 
				sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET+LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(entry.getValue()); 
				sb.append(LINEND); 
			} 
			DataOutputStream outStream = new DataOutputStream(conn.getOutputStream()); 
			outStream.write(sb.toString().getBytes());
			
			
			if(files!=null ){
//				    for (Map.Entry<String, Drawable> file: files.entrySet()) { 
				for (int i = 0; i < files.size(); i++) {
					
					StringBuilder sb1 = new StringBuilder(); 
					sb1.append(PREFIX); 
					sb1.append(BOUNDARY); 
					sb1.append(LINEND); 
					sb1.append("Content-Disposition: form-data; name=\"" + img_name+"[]" + "\"; filename=\"" + (img_name+"[]"+".jpg") + "\"" +LINEND);
					sb1.append("Content-Type: application/octet-stream; charset="+CHARSET+LINEND);
					sb1.append(LINEND);
					outStream.write(sb1.toString().getBytes()); 
					
					InputStream is = FormatTools.getInstance().Drawable2InputStream(files.get(i));
					byte[] buffer = new byte[1024]; 
					int len = 0; 
					while ((len = is.read(buffer)) != -1) { 
						outStream.write(buffer, 0, len); 
					}
					
					is.close(); 
					outStream.write(LINEND.getBytes()); 
				}
			}
			
			
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes(); 
			outStream.write(end_data); 
			outStream.flush(); 
			
			int res = conn.getResponseCode(); 
			
			Log.d("AA",  "code:" + res);
			
			InputStream in = null;
			if (res == 200) {
				in = conn.getInputStream(); 
				int ch; 
				
				while ((ch = in.read()) != -1) { 
					sb2.append((char) ch); 
					
				}
				Log.d("AA",  "data:" + sb2.toString());
			}
			
			parseObject = JSON.parseObject(sb2.toString());
			
			
			
			return parseObject; 
		}
		catch ( Exception e)
		{
			Log.d("AA", "error post: " + e.getMessage());
			e.printStackTrace();
			return JSON.parseObject("由于图片太大，上传超时，请去后台添加。 '\n'http://ws4.eyunju.cn/"); 
			
		}
	}
	
	
	//多图上传
	public static JSONObject addPostFile(String actionUrl, Map<String, String> params, 
			List<String> files,String img_name) { 
		try
		{
			StringBuilder sb2 = new StringBuilder(); 
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--" , LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data"; 
			String CHARSET = "UTF-8";
			
			URL uri = new URL(actionUrl); 
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection(); 
			conn.setReadTimeout(60 * 500); 
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false); 
			conn.setRequestMethod("POST"); 
			conn.setRequestProperty("Connection", "Keep-Alive");
			//conn.setRequestProperty("ENCTYPE", MULTIPART_FROM_DATA); 
			conn.setRequestProperty("Charsert", "UTF-8"); 
			//conn.setRequestProperty("uploaded_file", "aaa"); 
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY); 
			
			StringBuilder sb = new StringBuilder(); 
			for (Map.Entry<String, String> entry : params.entrySet()) { 
				sb.append(PREFIX); 
				sb.append(BOUNDARY); 
				sb.append(LINEND); 
				sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET+LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(entry.getValue()); 
				sb.append(LINEND); 
			} 
			DataOutputStream outStream = new DataOutputStream(conn.getOutputStream()); 
//			ZipOutputStream outStream = new ZipOutputStream(conn.getOutputStream());
			outStream.write(sb.toString().getBytes());
			
			
			if(files!=null ){
//				    for (Map.Entry<String, Drawable> file: files.entrySet()) { 
				for (int i = 0; i < files.size(); i++) {
					if (files.get(i) != null) {
						
					
					StringBuilder sb1 = new StringBuilder(); 
					sb1.append(PREFIX); 
					sb1.append(BOUNDARY); 
					sb1.append(LINEND); 
					sb1.append("Content-Disposition: form-data; name=\"" + img_name +"[]" + "\"; filename=\"" + (img_name+"[]"+".jpg") + "\"" +LINEND);
					sb1.append("Content-Type: application/octet-stream; charset="+CHARSET+LINEND);
					sb1.append(LINEND);
					outStream.write(sb1.toString().getBytes()); 
					
					
//					FileInputStream fileInputStream = new FileInputStream(new File(files.get(i)));
					InputStream is = new FileInputStream(new File(files.get(i)));
//					InputStream is = FormatTools.getInstance().getInputStream(fileInputStream);
					
					byte[] buffer = new byte[1024]; 
					int len = 0; 
					while ((len = is.read(buffer)) != -1) { 
						outStream.write(buffer, 0, len); 
					}
					
					is.close(); 
					outStream.write(LINEND.getBytes());
					}
				}
			}
			
			
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes(); 
			outStream.write(end_data); 
			outStream.flush(); 
			
			int res = conn.getResponseCode(); 
			
			Log.d("AA",  "code:" + res);
			
			InputStream in = null;
			if (res == 200) {
				in = conn.getInputStream(); 
				int ch; 
				
				while ((ch = in.read()) != -1) { 
					sb2.append((char) ch); 
					
				}
				Log.d("AA",  "data:" + sb2.toString());
			}
			
			JSONObject parseObject = JSON.parseObject(sb2.toString());
			
			
			
			return parseObject; 
		}
		catch ( Exception e)
		{
			Log.d("AA", "error post: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	
	//多图上传
	public static JSONObject addZipFile(String actionUrl, Map<String, String> params, 
			File file,String img_name) { 
		try
		{
			StringBuilder sb2 = new StringBuilder(); 
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--" , LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data"; 
			String CHARSET = "UTF-8";
			
			URL uri = new URL(actionUrl); 
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection(); 
			conn.setReadTimeout(60 * 500); 
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false); 
			conn.setRequestMethod("POST"); 
			conn.setRequestProperty("Connection", "Keep-Alive");
			//conn.setRequestProperty("ENCTYPE", MULTIPART_FROM_DATA); 
			conn.setRequestProperty("Charsert", "UTF-8"); 
			//conn.setRequestProperty("uploaded_file", "aaa"); 
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY); 
			
			StringBuilder sb = new StringBuilder(); 
			for (Map.Entry<String, String> entry : params.entrySet()) { 
				sb.append(PREFIX); 
				sb.append(BOUNDARY); 
				sb.append(LINEND); 
				sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET+LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(entry.getValue()); 
				sb.append(LINEND); 
			} 
			DataOutputStream outStream = new DataOutputStream(conn.getOutputStream()); 
//			ZipOutputStream outStream = new ZipOutputStream(conn.getOutputStream());
			outStream.write(sb.toString().getBytes());
			
			
			if(file!=null ){
//				    for (Map.Entry<String, Drawable> file: files.entrySet()) { 
					
					StringBuilder sb1 = new StringBuilder(); 
					sb1.append(PREFIX); 
					sb1.append(BOUNDARY); 
					sb1.append(LINEND); 
					sb1.append("Content-Disposition: form-data; name=\"" + img_name+"[]" + "\"; filename=\"" + (img_name+"[]"+".jpg") + "\"" +LINEND);
					sb1.append("Content-Type: application/octet-stream; charset="+CHARSET+LINEND);
					sb1.append(LINEND);
					outStream.write(sb1.toString().getBytes()); 
					FileInputStream fileInputStream = new FileInputStream(file);
//					InputStream is = FormatTools.getInstance().getInputStream(fileInputStream);
					ZipInputStream is =  new ZipInputStream(fileInputStream);
					
//					ZipEntry ze = is.getNextEntry();//取得下一个文件项
//			        long size = ze.getSize();

					byte[] buffer = new byte[1024]; 
					int len = 0; 
					while ((len = is.read(buffer)) != -1) { 
						outStream.write(buffer, 0, len); 
					}
//
//			        for(int i= 0;i<size;i++){//循环读取文件并写入输出文件对象
//			            byte c = (byte)is.read();
//			            outStream.write(c);
//			        }
//			        
					is.close(); 
					

//			        int BUFFER = 1024;  
////			        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));  
//			        ZipEntry entry;  
//			        while ((entry = is.getNextEntry()) != null) {  
//			            int count;  
//			            byte data[] = new byte[1024];  
//			            while ((count = is.read(data, 0, BUFFER)) != -1) {  
//			            	outStream.write(data, 0, count);  
//			            }  
//			        }  
//			        is.close();
					
					
					outStream.write(LINEND.getBytes()); 
			}
			
			
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes(); 
			outStream.write(end_data); 
			outStream.flush(); 
			
			int res = conn.getResponseCode(); 
			
			Log.d("AA",  "code:" + res);
			
			InputStream in = null;
			if (res == 200) {
				in = conn.getInputStream(); 
				int ch; 
				
				while ((ch = in.read()) != -1) { 
					sb2.append((char) ch); 
					
				}
				Log.d("AA",  "data:" + sb2.toString());
			}
			
			JSONObject parseObject = JSON.parseObject(sb2.toString());
			
			
			
			return parseObject; 
		}
		catch ( Exception e)
		{
			Log.d("AA", "error post: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
		
}
