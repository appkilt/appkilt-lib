package com.appkilt.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.json.JSONException;
import org.json.JSONObject;




class AppKiltRestClient {


	private static final int MAX_RETRIES = 2;
	
	
	protected OnFileDownloadProgressListener onFileDownloadProgressListener = null;
	protected int zipDownloadPercentComplete = 0;
	
	private String hostname;
	private String companyId;


	public AppKiltRestClient(String companyId, String serverURL)
	{
		this.hostname = serverURL;
			
		this.companyId = companyId.toLowerCase();
	}
	
	
	
	
	
	void sendFeedback(Feedback fb) throws RestUnauthorizedException, RestConnectionFailedException, RestParsingException
	{
		final String APP_URL = "/client/put/sug/";
		

		try {
			String resp = connect(hostname + APP_URL, fb.toJson(), true);

			JSONObject json = new JSONObject(resp);
			if (json.has("error_code"))
			{
				int errorCode;
					errorCode = json.getInt("error_code");
	
				String errorMsg = json.getString("error");
				
				if (errorCode == 403)
					throw new RestUnauthorizedException(errorMsg);
				else
					throw new RestParsingException(errorMsg);
			}
			
			
		
		} catch (JSONException e) {
			throw new RestParsingException(e.getMessage());
		}
	}
	 
	public ApkInfo getApkInfo(String appId) throws RestUnauthorizedException, RestConnectionFailedException, RestParsingException
	{
		final String APK_URL = "/client/get/market/apk/";
		

		try {

			JSONObject parameters = new JSONObject();
			parameters.put("id", appId);
			
			String resp = connect(hostname + APK_URL, parameters, false);
			
			JSONObject json = new JSONObject(resp);
			if (json.has("error_code"))
			{
				int errorCode;
					errorCode = json.getInt("error_code");
	
				String errorMsg = json.getString("error");
				
				if (errorCode == 403)
					throw new RestUnauthorizedException(errorMsg);
				else
					throw new RestParsingException(errorMsg);
			}
			
			
			return new ApkInfo(json);
		
		} catch (JSONException e) {
			throw new RestParsingException(e.getMessage());
		}
	}

	
	private String connect(String urlString, JSONObject parameters, boolean post) throws RestUnauthorizedException, RestConnectionFailedException, RestParsingException
	{
		try {
			parameters.put("companyid", companyId);
			
		} catch (JSONException e1) {
			throw new RestParsingException(e1.getMessage());
			
		}
		
		try {
			if (post == false)
				urlString = urlString +"?json=" + URLEncoder.encode(parameters.toString(), "UTF-8");
			
		} catch (UnsupportedEncodingException e1) {
			// Will never happen, UTF-8 is always supported
			throw new RestParsingException(e1.getMessage());
		}
		
		for (int i = 0; i < MAX_RETRIES; i++)
		{
			try
			{
				String retVal;
				if (post)
					retVal = connectOncePost(urlString, parameters);
				else
					retVal = connectOnce(urlString);
				return retVal;
			}
			catch (RestConnectionFailedException e)
			{
				if (i == MAX_RETRIES - 1)
					throw e;
			}
		}
		
		return ""; // Should never get here...
	}

	public void setOnFileDownloadProgressListener(OnFileDownloadProgressListener listener)
	{
		onFileDownloadProgressListener = listener;
	}
	

	protected String connectOnce(String urlString) throws RestUnauthorizedException, RestConnectionFailedException, RestParsingException
	{
		StringBuilder response = new StringBuilder();
		try
		{
//			URL url = new URL (urlString);
		
			
			InputStream stream = getStreamNoValidation(urlString);

			
			BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    		stream));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) 
			{
				response.append(inputLine);
				//System.out.println(inputLine);
			}
			in.close();
			
			if (response.length() == 0)
				throw new RestConnectionFailedException("No data returned from content provider");
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

		
		return response.toString();
	}
	
	protected String connectOncePost(String urlString, JSONObject data) throws RestUnauthorizedException, RestConnectionFailedException, RestParsingException
	{
		StringBuilder response = new StringBuilder();
		try
		{
//			URL url = new URL (urlString);
		
			
			InputStream stream = getStreamNoValidation(urlString, true, data);

			
			BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    		stream));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) 
			{
				response.append(inputLine);
				//System.out.println(inputLine);
			}
			in.close();
			
			if (response.length() == 0)
				throw new RestConnectionFailedException("No data returned from content provider");
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

		
		return response.toString();
	}
	

	protected URLConnection getFileDownloadConnection(String urlString) throws RestUnauthorizedException, RestConnectionFailedException, RestParsingException
	{

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
			
			return uc;
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
	
	

	protected InputStream getStreamNoValidation(String _URL) throws IOException, RestUnauthorizedException
	{
		return getStreamNoValidation(_URL, false, null);
	}
	protected InputStream getStreamNoValidation(String _URL, boolean post, JSONObject postData) throws IOException, RestUnauthorizedException
	{
		final int timeoutMS = 35000;
		
        URL ArxURL = new URL(_URL);
        //_FakeX509TrustManager.allowAllSSL(); 
        
        URLConnection con;
        if (_URL.toLowerCase().startsWith("https://"))
        {
        	con = ArxURL.openConnection();
        }
        else
        {
			con =  ArxURL.openConnection();
        }


		//con.setRequestMethod("GET"); 
	     con.setConnectTimeout(timeoutMS);
	     con.setReadTimeout(timeoutMS);
		con.setUseCaches (false); 
		
        if (post)
        {
        	con.setDoOutput(true);
        	OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
        	wr.write( "json=" + URLEncoder.encode(postData.toString(), "UTF-8")  );
        	wr.flush();
        	wr.close();
        }

		InputStream newconn = con.getInputStream();

		int statusCode = ((HttpURLConnection) con).getResponseCode(); 
		
		if ((statusCode == 403) || (statusCode == 500)) // 500 errors also show up when the user is unauthorized... weird.
		{
			throw new RestUnauthorizedException("This user is not authorized");
		}
		

		return newconn;


	}
}
