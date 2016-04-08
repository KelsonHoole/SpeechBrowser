package cn.hukecn.speechbrowser;

import android.app.Application;
import cn.hukecn.speechbrowser.DAO.MyDataBase;
import cn.hukecn.speechbrowser.util.ToastUtil;

public class SpeechBrowserApplication extends Application {
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		initApp();
		super.onCreate();
	}

	private void initApp() {
		// TODO Auto-generated method stub
		ToastUtil.init(getApplicationContext());
		MyDataBase.init(getApplicationContext());
	}
}
