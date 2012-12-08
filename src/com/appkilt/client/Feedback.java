package com.appkilt.client;

import org.json.JSONException;
import org.json.JSONObject;

class Feedback {
	private long id;
	
	private final String appId;
	
	private final String email;
	private final String description;
	private final String versionName;
	private final int versionCode;
	
	Feedback(String appId, String email, String description, String versionName, int versionCode)
	{
		this.appId = appId;
		this.email = email;
		this.description = description;
		this.versionCode = versionCode;
		this.versionName = versionName;
	}

	String getAppId() {
		return appId;
	}

	String getEmail() {
		return email;
	}

	String getDescription() {
		return description;
	}

	String getVersionName() {
		return versionName;
	}

	int getVersionCode() {
		return versionCode;
	}

	long getId() {
		return id;
	}

	void setId(long id) {
		this.id = id;
	}
	
	
	JSONObject toJson() throws JSONException
	{

		JSONObject json = new JSONObject();
		json.put("id", appId);
		json.put("description", description);
		json.put("email", email);
		json.put("android_version", android.os.Build.VERSION.SDK_INT);
		json.put("device", android.os.Build.MANUFACTURER + " " + android.os.Build.PRODUCT + " " + android.os.Build.MODEL);
		json.put("version_code", versionCode);
		json.put("version_name", versionName);
		
		
		return json;
	}
	
	
}

