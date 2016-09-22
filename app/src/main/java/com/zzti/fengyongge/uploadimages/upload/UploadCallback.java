package com.zzti.fengyongge.uploadimages.upload;

import com.alibaba.fastjson.JSONObject;

public interface UploadCallback {
	
	public void onSuccessData(JSONObject data);
	public void onEorrData(JSONObject data);
	
}