package com.appkilt.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;


class ApkDownloader {
	static final String UPGRADE_FILE_APK = "appkilt_upgrade.apk";
	
	private static final String DOWNLOAD_BYTES_COMPLETE_ID = "downloaded_bytes";
	private static final String DOWNLOAD_BYTES_TOTAL_ID = "total_bytes";

	private static final String DOWNLOAD_ERROR_ID = "dl_error";
	private static final String DOWNLOAD_SUCCESS_ID = "dl_success";
	

	private static final String DOWNLOAD_FILEPATH = "dl_filePath";
	private static final String DOWNLOAD_VERSION = "dl_version";
	
	private final Activity context;
	private final AppKiltSettings settings;
	private final ApkInfo apkInfo;

	private ProgressDialog dlDialog = null;			
	
	
	ApkDownloader(ApkInfo apkInfo, AppKiltSettings settings, Activity context) {
		this.context = context;			
		this.settings = settings;
		this.apkInfo = apkInfo;
		
		dlDialog = ProgressDialog.show(context, "", 
	            "Downloading Upgrade...", false, true);

		dlDialog.setProgress(0);
	}
	
	void start()
	{
		Thread downloadThread = new Thread(new DownloadApk());
		downloadThread.start();
	}

	private Handler downloadEventHandler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {

			
			if (msg.getData().containsKey(DOWNLOAD_BYTES_COMPLETE_ID))
			{
				long totalBytes = msg.getData().getLong(DOWNLOAD_BYTES_TOTAL_ID);
				long downloadedBytes = msg.getData().getLong(DOWNLOAD_BYTES_COMPLETE_ID);
				double percentComplete = ((double) downloadedBytes) / ((double) totalBytes) * 100;
				
				if (dlDialog != null)
				{
					dlDialog.setProgress((int) percentComplete);
				}
			}
			else if (msg.getData().containsKey(DOWNLOAD_ERROR_ID))
			{
				String errorMessage = msg.getData().getString(DOWNLOAD_ERROR_ID);
				if (dlDialog != null)
				{
					dlDialog.setMessage("Error: " + errorMessage);
				}
			}
			else if (msg.getData().containsKey(DOWNLOAD_SUCCESS_ID))
			{
				String filename = msg.getData().getString(DOWNLOAD_FILEPATH);
				if (dlDialog != null)
				{
					dlDialog.dismiss();
				}
				
				doInstall(filename);
			}
		}
	};
	
    private void doInstall(String filename)
    {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + filename),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
        //startActivityForResult(intent, REQUESTED_INSTALL);

    }
	
	private void onDownloadProgressUpdate( long bytesDownloaded, long total)
	{

    	Message msg = downloadEventHandler.obtainMessage();
		Bundle b = new Bundle();

		b.putLong(DOWNLOAD_BYTES_COMPLETE_ID,	bytesDownloaded);
		b.putLong(DOWNLOAD_BYTES_TOTAL_ID,	total);
		msg.setData(b);

        downloadEventHandler.sendMessage(msg);

		
		


        
	}

	private void onDownloadSuccess(String filename, int codeVersion)
	{
    	Message msg = downloadEventHandler.obtainMessage();
		Bundle b = new Bundle();

		b.putBoolean(DOWNLOAD_SUCCESS_ID,	true);
		b.putString(DOWNLOAD_FILEPATH, filename);
		b.putInt(DOWNLOAD_VERSION, codeVersion);
		msg.setData(b);

        downloadEventHandler.sendMessage(msg);

		
	}
	
	private void onDownloadError(String errorMessage)
	{

    	Message msg = downloadEventHandler.obtainMessage();
		Bundle b = new Bundle();
		
		b.putString(DOWNLOAD_ERROR_ID,	errorMessage);
		msg.setData(b);

        downloadEventHandler.sendMessage(msg);

		
	}
	

	private class DownloadApk implements Runnable
	{

		@Override
		public void run() {
			
			String downloadURL = "";
			
			try {
				downloadURL = settings.getAppKiltServer() + 
						"/apk?companyid=" + URLEncoder.encode(settings.getCompanyId(), "UTF-8") + 
						"&appid=" + URLEncoder.encode(apkInfo.getPackageName(), "UTF-8");

				File flashDir = findPathWithSpace(apkInfo.getFileSize());
				File localFile = new File(flashDir, UPGRADE_FILE_APK);
				
				downloadFile(downloadURL, localFile, apkInfo.getFileSize());

		        onDownloadSuccess(localFile.getAbsolutePath(), apkInfo.getVersionCode());
		        
			} catch (UnsupportedEncodingException e) {
				onDownloadError(e.getMessage());
			}
			 catch (InsufficientSpaceException e) {
				onDownloadError("Insufficient storage space.  Please clear space on your phone by removing unnecessary applications.");
			} catch (FlashDriveUnavailable e) {
				onDownloadError("Flash drive unavailable.");
			} catch (RestConnectionFailedException e) {
				onDownloadError("Could not download upgrade.  Please try again later.");
			} catch (RestParsingException e) {
				onDownloadError("Could not complete upgrade.  Please try again later.");
			}
		}

	}
	
	private void downloadFile( String urlString, File localFile, long fileSize) throws RestConnectionFailedException, RestParsingException//, DownloadCanceledException
	{

 
		final int READ_TIMEOUT_MS = 30000;
		final int BUFFER_SIZE = 128 * 1024;


		
	    try {
			
	    	//urlString = String.format("%s?api_key=%s", urlString, apikey);
			URL url = new URL (urlString);

			URLConnection uc;// = url.openConnection();
	        if (urlString.toLowerCase().startsWith("https://"))
	        {
	        	uc = url.openConnection();

	        }
	        else
	        {
	        	uc =  url.openConnection();
	        }
			//setRequestProperties(uc);
	    	
	    	
			uc.setReadTimeout(READ_TIMEOUT_MS);
			
			String ct = uc.getContentType();
			//int contentLength = uc.getContentLength();
			if ( ((ct != null) && (ct.startsWith("text/"))) )  {
				Log.w(this.getClass().getSimpleName(), "Non-Binary data returned");
				//System.err.println("This is not a binary file.");
				


				throw new RestConnectionFailedException("Non-binary data returned");
			    //return;
			}

			InputStream stream = uc.getInputStream();

	        //FileOutputStream out = new FileOutputStream(localFile);
	        FileOutputStream out = context.openFileOutput(UPGRADE_FILE_APK, Context.MODE_WORLD_READABLE);
 
	        byte[] b = new byte[BUFFER_SIZE];
	        int count;

	        int bytesDownloaded = 0;
	        int iteration = 0;
	        while ((count = stream.read(b)) >= 0) {
	        	//if (dl.isCanceled())
	        	//	throw new DownloadCanceledException();
	        	
	            out.write(b, 0, count);
	            
	            // Calculate percent complete
	            bytesDownloaded += count;
	            if ((count != 0) && (iteration % 20 == 0))
	            {
            		onDownloadProgressUpdate( bytesDownloaded, fileSize);

	            	//if (onFileDownloadProgressListener != null)
	            	//	onFileDownloadProgressListener.onProgressUpdate(dl_uid, bytesDownloaded, contentLength);
	            }
	            
	            iteration++;
	        }
	        
	        out.flush();
	        out.close();
	        stream.close();

		}
		catch (MalformedURLException e)
		{
			// Bad URL sent in
			//System.out.println(e.getMessage());
			throw new RestParsingException(e.getMessage());
		}
		catch (IOException e)
		{
			// Couldn't connect to URL
			//System.out.println(e.getMessage());
			throw new RestConnectionFailedException(e.getMessage());
		}
	}
	
	
	private File findPathWithSpace(long fileSize) throws InsufficientSpaceException, FlashDriveUnavailable
	{
	
		
		// Make sure that there's sufficient space
		//File baseDir = Environment.getExternalStorageDirectory();
		File baseDir = context.getFilesDir();	
		
		StatFs spaceData = new StatFs( baseDir.getAbsolutePath() );
		
		//long totalBytes = (long) spaceData.getBlockCount() * (long) spaceData.getBlockSize();
		long freeBytes =  (long) spaceData.getFreeBlocks() * (long) spaceData.getBlockSize();
		
		if (freeBytes < fileSize + 2048)
			throw new InsufficientSpaceException();
		
		
		//File appDir = new File(baseDir, "/Android/data/" + apkInfo.getPackageName() + "/cache/");

		
		if (baseDir.canWrite() == false)
			throw new FlashDriveUnavailable();

		


		return baseDir;

	}
}
