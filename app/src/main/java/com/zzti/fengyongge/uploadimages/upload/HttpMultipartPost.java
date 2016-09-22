package com.zzti.fengyongge.uploadimages.upload;

import android.content.Context;
import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.zzti.fengyongge.imagepicker.util.StringUtils;
import com.zzti.fengyongge.uploadimages.dialog.CustomAlertDialog;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Multipart上传
 * @author fengyongge
 *
 */
public class HttpMultipartPost extends AsyncTask<String, Integer, String> {

	private Context context;

	private String urlPath;
	private String fileName;
	private UploadCallback callback;
	private List<String> filePathList = new ArrayList<String>();
	private Map<String, String> dataMap = new HashMap<String, String>();
	private Map<String, String> filePathMap = new HashMap<String, String>();
	private CustomAlertDialog pd;
	private long totalSize;

	public HttpMultipartPost(Context context, CustomAlertDialog pd, String urlPath, String fileName,
							 List<String> filePathList, Map<String, String> dataMap, UploadCallback callback) {
		this.context = context;
		this.urlPath = urlPath;
		this.fileName = fileName;
		this.pd = pd;
		this.callback = callback;
		this.filePathList.clear();
		this.dataMap.clear();
		this.filePathList.addAll(filePathList);
		this.dataMap.putAll(dataMap);
	}
	public HttpMultipartPost(Context context, CustomAlertDialog pd,String urlPath,Map<String, String> filePathMap,Map<String, String> dataMap,UploadCallback callback) {
		this.context = context;
		this.urlPath = urlPath;
		this.pd = pd;
		this.callback = callback;
		this.dataMap.clear();
		this.filePathMap.putAll(filePathMap);
		this.dataMap.putAll(dataMap);
	}

	@Override
	protected void onPreExecute() {
//		pd = new ProgressDialog(context);
//		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		pd.setMessage("Uploading Picture...");
//		pd.setCancelable(false);
//		pd.show();

		pd.setMax(100);
		pd.show();

	}

	@Override
	protected String doInBackground(String... params) {
		String serverResponse = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(urlPath);
		try {

			CustomMultipartEntity multipartContent = new CustomMultipartEntity(
					new CustomMultipartEntity.ProgressListener() {
						@Override
						public void transferred(long num) {
							publishProgress((int) ((num / (float) totalSize) * 100));
						}
					});

			// We use FileBody to transfer an image
			//把上传内容添加到MultipartEntity
			if (filePathMap.size() != 0) {
				for (Entry<String, String> element : filePathMap.entrySet()) {
					if (StringUtils.isNotEmpty(element.getValue()) ) {
						multipartContent.addPart(element.getKey(), new FileBody(new File(
								element.getValue())));
					}
				}

			}else {

				if (filePathList.size() != 0) {

					if (fileName.equals("group_logo")) {

						multipartContent.addPart(fileName, new FileBody(new File(
								filePathList.get(0))));

					}
					else {

						for (int i = 0; i < filePathList.size(); i++) {

							if (filePathList.get(i) != null&&!filePathList.get(i).equals("") ) {

								multipartContent.addPart(fileName+"[]", new FileBody(new File(
										filePathList.get(i))));

							}

						}
					}

				}

			}


			for (Entry<String, String> element : dataMap.entrySet()) {

				multipartContent.addPart(element.getKey(),
						new StringBody(element.getValue(), Charset
								.forName(org.apache.http.protocol.HTTP.UTF_8)));
			}


			totalSize = multipartContent.getContentLength();
			// Send it
			httpPost.setEntity(multipartContent);
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			serverResponse = EntityUtils.toString(response.getEntity());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return serverResponse;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {

		if ((progress[0]) < 3) {
			pd.setProgress(0);
		}else {

			pd.setProgress(((progress[0]) - 3));
		}

	}

	@Override
	protected void onPostExecute(String result) {

//		{
//			"status": 10000,
//				"message": "上传成功",
//				"img": [
//			"http://ktvimage.ediankai.com.cn/uploads/20160922/201609221423201173.jpg"
//			]
//		}
		try {
			if(result!=null){
				JSONObject myJsonObject = new JSONObject(result);
				if (myJsonObject.getString("status").equals("10000")) {
					callback.onSuccessData(JSON.parseObject(result));
				}else {
					callback.onEorrData(JSON.parseObject(result));
				}
			}
//			pd.dismiss();
		} catch (JSONException e) {
//			pd.dismiss();
			e.printStackTrace();
		}

	}

	@Override
	protected void onCancelled() {
		System.out.println("cancle");
	}

}
