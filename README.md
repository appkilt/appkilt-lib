appkilt-lib
===========

Find out more at http://www.appkilt.com

The AppKilt private market is a personalized App Market for Android that contains only the apps you want. Your users can browse 
the collection and easily download, install, and update any of your apps right from the market app.

The AppKilt private market app makes the process of installing apps incredibly easy for your end-users. The app 
itself is available on Google Play. Once installed, the AppKilt private market ensures that the user's phone is 
setup correctly to install your apps.

Not only does the AppKilt Private Market help users install your apps, it also automatically manages updates.


AppKilt Library
---------------

This library can be embedded in your apps and used in conjunction with the AppKilt service.  This library enables
automatic bug reports and allows your Android app to update itself to the latest version.


Installation
---------------

You can download the latest jar file here: www.appkilt.com/downloads/appkiltlib.jar

Or download the source code and integrate it within your app project.  Simply include the code in your Android app package.


Usage
---------------

### Initializing the Library

First, make sure that your app has the proper permissions.  Network access is required to upload bug reports and download updates

	Add the following permissions to your AndroidManifest.xml:
	android:name="android.permission.INTERNET"

Add the following code to your Android app application class. If you don't have an Application class, 
you can create one easily. Remember to add it to your AndroidManifest.xml.

``` java
	public class MyApplication extends Application {

		@Override
		public void onCreate() {
			super.onCreate();
		
			AppKilt.init(this, "[Your Company ID]");
		}
	}
```

### Enabling Automatic Updates

Add the following code to each activity where you want users to be given the option to upgrade. 
Typically this will just be the main activity for your application.

``` java
	@Override
	protected void onPause() { 
		super.onPause();

		AppKilt.onUpdateableActivityPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	
		AppKilt.onUpdateableActivityResume(this);
```

Additional Help
-----------------

Feel free to e-mail info@appkilt.com with questions.

