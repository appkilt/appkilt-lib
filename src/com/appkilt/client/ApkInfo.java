package com.appkilt.client;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

class ApkInfo implements Serializable {

	private static final long serialVersionUID = -7410667888962555834L;
	
	private final String appId;
	private final long fileSize;
	private final int versionCode;
	private final String versionName;
	
	ApkInfo(JSONObject json) throws JSONException
	{
		appId = json.getString("id");
		fileSize = json.getLong("size");
		versionCode = json.getInt("version_code");
		versionName = json.getString("version_name");
		
	}

	long getFileSize() {
		return fileSize;
	}

	int getVersionCode() {
		return versionCode;
	}

	String getVersionName() {
		return versionName;
	}

	String getPackageName() {
		return appId;
	}
	
	
}
