package com.appkilt.client;

public class AppKiltSettings {

	private final String companyId;
	private String appKiltServer = "https://my.appkilt.com"; 
	
	private boolean forceUpgrade = false;
	private boolean errorReporting = true;
	private String upgradeMessage = "An upgrade for this app is available.  Would you like to download it now?";

	public AppKiltSettings(String companyId)
	{
		this.companyId = companyId;
	}
	
	/**
	 * Uncaught exceptions can be automatically sent to AppKilt for your review.  This is enabled by default.  Update this setting to turn error reporting off.
	 * @param errorReporting
	 */
	public void setErrorReporting(boolean enabled)
	{
		this.errorReporting = enabled;
	}
	
	/**
	 * If set to true, then the user must upgrade in order to use the app.  The default for this value is false.
	 * @param enabled
	 */
	public void setForceUpgrade(boolean enabled)
	{
		this.forceUpgrade = enabled;
	}
	
	/**
	 * This setting should only be changed if you are running an on-premise (Enterprise) version of AppKilt.  The default value is https://my.appkilt.com
	 * @param serverURL
	 */
	public void setAppKiltServer(String serverURL)
	{
		this.appKiltServer = serverURL;
	}
	
	public void setUpgradeMessage(String message)
	{
		this.upgradeMessage = message;
	}
	
	boolean isErrorReporting()
	{
		return errorReporting;
	}
	boolean isForceUpgrade()
	{
		return forceUpgrade;
	}

	String getUpgradeMessage()
	{
		return upgradeMessage;
	}
	
	String getCompanyId()
	{
		return companyId;
	}
	
	String getAppKiltServer()
	{
		return appKiltServer;
	}
}
