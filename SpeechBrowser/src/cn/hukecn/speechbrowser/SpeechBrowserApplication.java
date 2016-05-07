package cn.hukecn.speechbrowser;

import android.app.Application;
import android.content.Context;

public class SpeechBrowserApplication extends Application {
	
	private static Context applicationContext = null;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
//		initApp();
		super.onCreate();
		applicationContext = getApplicationContext();
	}

//	private void initApp() {
//		// TODO Auto-generated method stub
//		ToastUtil.init(getApplicationContext());
//		MyDataBase.init(getApplicationContext());
//		CrashHandler catchHandler = CrashHandler.getInstance();  
//        catchHandler.init(getApplicationContext());
//        BaseAppLocation baseAppLocation = BaseAppLocation.getInstance();
//        baseAppLocation.init(getApplicationContext());
//        
//	}
	
	public static Context getAppContext() {
		// TODO Auto-generated method stub
		return applicationContext;
	}
}
