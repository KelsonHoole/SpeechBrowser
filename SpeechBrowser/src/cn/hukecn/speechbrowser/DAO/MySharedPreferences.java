package cn.hukecn.speechbrowser.DAO;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
	
	private static SharedPreferences settingInstance = null;
	public static SharedPreferences getInstance(Context context){
		if(settingInstance == null)
			settingInstance = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
		
		return settingInstance;
	}
}
