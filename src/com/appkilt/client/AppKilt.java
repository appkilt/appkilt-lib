package com.appkilt.client;

import java.io.File;
import java.util.Calendar;

import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.acra.ReportSettings;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AppKilt {

	private static final String APK_INFO_KEY = "apk_info";
	
	private static final String APPKILT_BUG_URL = "/client/put/bug";

	
	private static AppKilt appKilt = null;
	private static Activity updateableActivity = null;
	
	/**
	 * Initialize AppKilt.  Automatically checks for updates and sets up error reporting
	 */
	public static void init(Application app, String companyId)
	{
		AppKiltSettings settings = new  AppKiltSettings(companyId);
		init(app, settings);
	}

	/**
	 * Initialize AppKilt.  Provide a customized AppKiltSettings object 
	 */
	public static void init(Application app, AppKiltSettings settings)
	{
		// only initialize once.
		if (appKilt != null)
			return;
		
		// Check for all requirements on init and throw a runtime error if applicable.
		// Better to let the developer know immediately, rather than see strange behavior.
		if (hasPermission(Manifest.permission.INTERNET, app) == false)
			throw new RuntimeException("AppKilt library requires the 'android.permission.INTERNET' permission to be specified in your manifest.xml in order to work properly");
		
		
		appKilt = new AppKilt(app, settings);

	}
	
	public static void onUpdateableActivityResume(Activity activity)
	{
		
		updateableActivity = activity;
		if (appKilt != null)
		{
			appKilt.checkForUpdate();
		}
	}
	
	public static void onUpdateableActivityPause()
	{
		updateableActivity = null;
		if (appKilt != null)
		{
			appKilt.dismissDialogs();
		}
	}
	
	private static boolean hasPermission(String permissionName, Context context)
	{
		try {
			PackageInfo pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
			
			for (String permission : pinfo.requestedPermissions)
			{
				if (permission.equals(permissionName))
				{
					return true;
				}
			}
			
		} catch (NameNotFoundException e) {
			// I should never fail to find my own package...
			Log.w(AppKilt.class.getSimpleName(), "Failed to retrieve package info for myself!!");
		}
		
		return false;
	}

	/**
	 * Send an exception to AppKilt manually.  This is useful for logging exceptions that don't crash your app.
	 * @param e
	 */
	public static void reportRunningException(Throwable e)
	{
		if (appKilt != null)
		{
			appKilt.reportException(e);
		}
	}
	

	/**
	 * Sends a user's feedback to the AppKilt service. 
	 * @param appId
	 * @param email
	 * @param description
	 * @param versionName
	 * @param versionCode
	 * @return true if successful, false otherwise
	 */
	public static boolean sendFeedback(String appId, String email, String description, String versionName, int versionCode)
	{
		Feedback fb = new Feedback(appId, email, description, versionName, versionCode);
		
		if (appKilt != null)
		{
			try {
				appKilt.sendFeedback(fb);
				return true;
			} catch (RestException e) {
				
			}
		}
		
		return false;
	}
	////////////////////////////////////////////////////////////////////
	
	private final Application app;
	private AppKiltSettings settings;
	private AppKiltRestClient client;
	
	private boolean upgradeAlertIsShowing = false;
	private AlertDialog alertDialog = null;

	
	// We don't want to do an upgrade check every time the activity loads -- set a sensible time range here
	private static final long MIN_TIME_BETWEEN_UPDATE_CHECKS_MS = 120 * 60 * 1000; // 120 min * 60 sec * 1000 ms = 2 hours in milliseconds
	private long lastSuccessfulUpdateCheckTimestamp = 0;
	
	private AppKilt(Application app, AppKiltSettings settings)
	{
		this.app = app;
		this.settings = settings;
		
		client = new AppKiltRestClient(settings.getCompanyId(), settings.getAppKiltServer());
		
		if (settings.isErrorReporting())
		{
			ReportSettings reportSettings = new ReportSettings();
			reportSettings.setPostURL(settings.getAppKiltServer() + APPKILT_BUG_URL);
	
			ErrorReporter.getInstance().putCustomData("company", settings.getCompanyId());
			ACRA.init(app, reportSettings);
		}
		

		
		// Rare case, but the user could have set the activity before initializing.
		if (updateableActivity != null)
			checkForUpdate();
	}
	
	private void checkForUpdate()
	{
		if (upgradeAlertIsShowing && alertDialog != null && alertDialog.isShowing() == false)
			alertDialog.show();
			
		long curMs = Calendar.getInstance().getTime().getTime();
		
		if (curMs - lastSuccessfulUpdateCheckTimestamp < MIN_TIME_BETWEEN_UPDATE_CHECKS_MS)
			return;
		
		
		Thread upgradeCheck = new Thread(new UpgradeCheck());
		upgradeCheck.start();
	}
	
	private void dismissDialogs()
	{
		if (alertDialog != null)
			alertDialog.dismiss();
	}
	
	private void reportException(Throwable e)
	{
		if (settings.isErrorReporting())
			ErrorReporter.getInstance().handleSilentException(e);
	}
	
	private Handler upgradeAvailableAlertHandler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {

			if (updateableActivity == null)
				return;
			
			upgradeAlertIsShowing = true;

			final ApkInfo apkInfo = (ApkInfo) msg.getData().getSerializable(APK_INFO_KEY);
			
			AlertDialog.Builder alert = new AlertDialog.Builder(
					updateableActivity);

			alert.setTitle("Upgrade Available");
			alert.setMessage("An upgrade is available for this app.  Do you wish to upgrade now?");
			alert.setIcon(app.getApplicationInfo().icon);
			alert.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							ApkDownloader downloader = new ApkDownloader(apkInfo, settings, updateableActivity);
							downloader.start();
							
							upgradeAlertIsShowing = false;
						}
					});
			alert.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							// Toast.makeText(ShowDialog.this, "Fail",
							// Toast.LENGTH_SHORT).show();
							upgradeAlertIsShowing = false;
						}
					});

			alertDialog = alert.show();
		};
	};


        

	// Checks for upgrade daily, or on app initialization
	private class UpgradeCheck implements Runnable
	{

		@Override
		public void run() {

			try {
				// First clean up any old apk upgrade file that may be lingering:
				File oldApk = new File(app.getFilesDir(), ApkDownloader.UPGRADE_FILE_APK);
				if (oldApk.exists())
					oldApk.delete();
				
				// Check to see if a new version of the app is available.
				ApkInfo apkInfo = client.getApkInfo(app.getPackageName());
				
				PackageInfo pinfo = app.getPackageManager().getPackageInfo(app.getPackageName(), 0);
				
				if (apkInfo.getVersionCode() != pinfo.versionCode)
				{
					// An upgrade is available.  Alert the user.
					
			    	Bundle b = new Bundle();
			    	Message msg = upgradeAvailableAlertHandler.obtainMessage();
			    	b.putSerializable(APK_INFO_KEY, apkInfo);
			        msg.setData(b);
			        
			        upgradeAvailableAlertHandler.sendMessage(msg);
			        
				}
				
				lastSuccessfulUpdateCheckTimestamp = Calendar.getInstance().getTime().getTime();
				


		        
			} catch (RestException e) {
				Log.d(AppKilt.class.getSimpleName(), "Error downloading apk Info from AppKilt. " + e.getMessage());
			} catch (NameNotFoundException e) {
				// This should never happen...  We are requesting package info for a package that we're currently executing within.
				Log.e(AppKilt.class.getSimpleName(), "Error.  Could not request information about my own package name");
			} 
		}
		
		
	}


	private void sendFeedback(Feedback fb) throws RestUnauthorizedException, RestConnectionFailedException, RestParsingException
	{
		client.sendFeedback(fb);
	}
	
}
