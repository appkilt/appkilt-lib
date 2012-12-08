/*
 *  Copyright 2010 Emmanuel Astier & Kevin Gaudin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.acra;



import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Provide configuration elemets to the {@link ACRA#init(android.app.Application)} method. The only mandatory
 * configuration item is the {@link #formKey()} parameter which is the Id of your Google Documents form which will
 * receive reports.
 * 
 * @author Kevin Gaudin
 * 
 */

public class ReportSettings {
	private String postURL = "";
	
	public void setPostURL(String url)
	{
		this.postURL = url;
	}
	
	
    /**
     * The id of the Google Doc form.
     * 
     * @return
     */
	public String formKey()
    {
    	return "";
    }

    /**
     * The Uri of your own server-side script that will receive reports. This is to use if you don't want to send
     * reports to Google Docs but to your own script.
     * 
     * @return
     */
    public String formUri()
    {
    	return postURL;
    }

    /**
     * <p>
     * The interaction mode you want to implement. Default is {@link ReportingInteractionMode#SILENT} which does not
     * require any resources configuration.
     * </p>
     * <p>
     * Other modes have resources requirements:
     * <ul>
     * <li>{@link ReportingInteractionMode#TOAST} requires {@link #resToastText()} to be provided to define the text
     * that you want to be displayed to the user when a report is being sent.</li>
     * <li>{@link ReportingInteractionMode#NOTIFICATION} requires {@link #resNotifTickerText()},
     * {@link #resNotifTitle()}, {@link #resNotifText()}, {@link #resDialogText()}</li>
     * </ul>
     * </p>
     * 
     * @return
     */
    public ReportingInteractionMode mode() 
    {
    	return ReportingInteractionMode.SILENT;
    }

    /**
     * Resource id for the user comment input label in the crash dialog. If not provided, disables the input field.
     */
    public int resDialogCommentPrompt() 
    {
    	return 0;
    }

    /**
     * Resource id for the user email address input label in the crash dialog. If not provided, disables the input
     * field.
     */
    public int resDialogEmailPrompt() 
    {
    	return 0;
    }

    /**
     * Resource id for the icon in the crash dialog.
     */
    public int resDialogIcon() 
    {
    	return android.R.drawable.ic_dialog_alert;
    }

    /**
     * Resource id for the Toast text triggered when the user accepts to send a report in the crash dialog.
     */
    public int resDialogOkToast() 
    {
    	return 0;
    }

    /**
     * Resource id for the text in the crash dialog.
     */
    public int resDialogText() 
    {
    	return 0;
    }

    /**
     * Resource id for the title in the crash dialog.
     */
    public int resDialogTitle() 
    {
    	return 0;
    }

    /**
     * Resource id for the icon in the status bar notification.
     */
    public int resNotifIcon() 
    {
    	return android.R.drawable.stat_notify_error;
    }

    /**
     * Resource id for the text in the status bar notification.
     */
    public int resNotifText() 
    {
    	return 0;
    }

    /**
     * Resource id for the ticker text in the status bar notification.
     */
    public int resNotifTickerText() 
    { 
    	return 0;
    }

    /**
     * Resource id for the title in the status bar notification.
     */
    public int resNotifTitle() 
    {
    	return 0;
    }

    /**
     * Resource id for the Toast text triggered when the application crashes if the notification+dialog mode is not
     * used.
     */
    public int resToastText() 
    {
    	return 0;
    }

    /**
     * Name of the SharedPreferences that will host the {@link ACRA#PREF_DISABLE_ACRA} or {@link ACRA#PREF_ENABLE_ACRA}
     * preference. Default is to use the default SharedPreferences, as retrieved with
     * {@link PreferenceManager#getDefaultSharedPreferences(Context)}.
     */
    public String sharedPreferencesName() 
    {
    	return "";
    }

    /**
     * If using a custom {@link ReportSettings#sharedPreferencesName()}, pass here the mode that you need for the
     * SharedPreference file creation: {@link Context#MODE_PRIVATE}, {@link Context#MODE_WORLD_READABLE} or
     * {@link Context#MODE_WORLD_WRITEABLE}. Default is {@link Context#MODE_PRIVATE}.
     * 
     * @see Context#getSharedPreferences(String, int)
     */
    public int sharedPreferencesMode() 
    {
    	return Context.MODE_PRIVATE;
    }

    /**
     * If enabled, DropBox events collection will include system tags:
     * <ul>
     * <li>system_app_anr</li>
     * <li>system_app_wtf</li>
     * <li>system_app_crash</li>
     * <li>system_server_anr</li>
     * <li>system_server_wtf</li>
     * <li>system_server_crash</li>
     * <li>BATTERY_DISCHARGE_INFO</li>
     * <li>SYSTEM_RECOVERY_LOG</li>
     * <li>SYSTEM_BOOT</li>
     * <li>SYSTEM_LAST_KMSG</li>
     * <li>APANIC_CONSOLE</li>
     * <li>APANIC_THREADS</li>
     * <li>SYSTEM_RESTART</li>
     * <li>SYSTEM_TOMBSTONE</li>
     * <li>data_app_strictmode</li>
     * </ul>
     * Requires {@link #includeDropBox()} true.
     * 
     * @return
     */
    public boolean includeDropBoxSystemTags() 
    { 
    	return false;
    }

    /**
     * You can provide here a list of tags that will be fetched when collecting DropBox entries.
     */
    public String[] additionalDropBoxTags() 
    {
    	return new String[] {};
    }

    /**
     * How many minutes will be looked back when collecting events from DropBoxManager.
     */
    public int dropboxCollectionMinutes() 
    {
    	return 5;
    }

    /**
     * <p>
     * Arguments to be passed to the logcat command line. Default is { "-t", "200", "-v", "time" } for:
     * </p>
     * 
     * <pre>
     * logcat -t 200 -v time
     * </pre>
     * 
     * <p>
     * Do not include -b arguments for buffer selection, use {@link #includeEventsLogcat()} and
     * {@link #includeRadioLogcat()} to activate alternative logcat buffers reporting. They will use the same other
     * arguments as those provided here.
     * </p>
     * 
     * <p>
     * See <a href= "http://developer.android.com/intl/fr/guide/developing/tools/adb.html#logcatoptions" >Listing of
     * logcat Command Options</a>.
     * </p>
     */
    public String[] logcatArguments() 
    {
    	return new String[] { "-t", "200", "-v", "time" };
    }

    /**
     * When using the {@link #formUri()} parameter to send reports to a custom server-side script, you can set here and
     * in {@link #formUriBasicAuthPassword()} the credentials for a BASIC HTTP authentication.
     */
    public String formUriBasicAuthLogin() 
    {
    	return ACRA.NULL_VALUE;
    }

    /**
     * When using the {@link #formUri()} parameter to send reports to a custom server-side script, you can set here and
     * in {@link #formUriBasicAuthLogin()} the credentials for a BASIC HTTP authentication.
     */
    public String formUriBasicAuthPassword() 
    {
    	return ACRA.NULL_VALUE;
    }

    /**
     * Specifies the list of fields to be included in reports with their order.
     */
    public ReportField[] customReportContent() 
    {
    	return new ReportField[] { ReportField.CUSTOM_DATA, ReportField.PACKAGE_NAME, ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, 
    			ReportField.ANDROID_VERSION, ReportField.BRAND, ReportField.PHONE_MODEL,  ReportField.STACK_TRACE, 
    			ReportField.USER_COMMENT, ReportField.DISPLAY, ReportField.AVAILABLE_MEM_SIZE, ReportField.TOTAL_MEM_SIZE};

    }

    /**
     * Add your crash reports mailbox here if you want to send reports via email. This allows to get rid of the INTERNET
     * permission. Reports content can be customized with {@link #reportFields()}. Default fields are:
     * {@link ReportField#USER_COMMENT}, {@link ReportField#ANDROID_VERSION}, {@link ReportField#APP_VERSION_NAME} ,
     * {@link ReportField#BRAND}, {@link ReportField#PHONE_MODEL}, {@link ReportField#CUSTOM_DATA},
     * {@link ReportField#STACK_TRACE}
     */
    public String mailTo() 
    {
    	return "";
    }

    /**
     * Controls whether unapproved reports are deleted on application start or not. Default is true. This is a change
     * from versions of ACRA before 3.2 as in NOTIFICATION mode reports were previously kept until the user explicitly
     * opens the Notification dialog AND choose to send or discard the report. Until then, on application restart, ACRA
     * was issuing a new crash notification for previous reports pending for approval. This could be misunderstood by
     * the user with a new crash, resulting in bad appreciation of the application.
     */
    public boolean deleteUnapprovedReportsOnApplicationStart() 
    {
    	return true;
    }

    /**
     * Value in milliseconds for network operations timeout (default 3000ms).
     */
    public int socketTimeout() 
    {
    	return 3000;
    }

    /**
     * In {@link ReportingInteractionMode#TOAST} mode, set this to true if you prefer displaying the native Force Close
     * dialog after the Toast.
     * 
     * @return true if the Force Close dialog has to be displayed.
     */
    public boolean forceCloseDialogAfterToast() 
    {
    	return false;
    }

    /**
     * Add here your {@link SharedPreferences} identifier Strings if you use others than your application's default.
     * They will be added to the {@link ReportField#SHARED_PREFERENCES} field.
     * 
     * @return
     */
    public String[] additionalSharedPreferences() 
    {
    	return new String[] {};
    }
}
