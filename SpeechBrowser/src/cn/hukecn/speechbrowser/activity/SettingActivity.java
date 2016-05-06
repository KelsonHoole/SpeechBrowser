/**
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package cn.hukecn.speechbrowser.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;
import cn.hukecn.speechbrowser.R;
import cn.hukecn.speechbrowser.DAO.MySharedPreferences;

/**
 * @author liweigao 2015Äê9ÔÂ15ÈÕ
 */
public class SettingActivity extends Activity {
    /*
     * @param savedInstanceState
     */
	ToggleButton btn_autoRead = null;
	ToggleButton btn_blind = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.intent.action.MAIN");
//        filter.addCategory("android.intent.category.HOME"); 
//        filter.addCategory("android.intent.category.DEFAULT");
//        ComponentName preActivity = new ComponentName("com.android.ulauncher", "com.android.ulauncher.Launcher"); 
//        ComponentName[] set = new ComponentName[] {new ComponentName("com.android.launcher", "com.android.launcher2.Launcher"), preActivity}; 
//        mSettings.mPreferredActivities.addFilter(
//                new PreferredActivity(filter, IntentFilter.MATCH_CATEGORY_EMPTY, set, preActivity));
//        scheduleWriteSettingsLocked();
        
        
        btn_autoRead = (ToggleButton) findViewById(R.id.btn_autoread);
        btn_blind = (ToggleButton) findViewById(R.id.btn_blind);
        final SharedPreferences sp = MySharedPreferences.getInstance(getApplicationContext());
        boolean autoread = sp.getBoolean("autoread", false);
        boolean blind = sp.getBoolean("blind", false);
        btn_autoRead.setChecked(autoread);
        btn_autoRead.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Editor edit = sp.edit();
				edit.putBoolean("autoread", isChecked);
				edit.commit();
			}
		});
        
        btn_blind.setChecked(blind);
        btn_blind.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Editor edit = sp.edit();
				edit.putBoolean("blind", isChecked);
				edit.commit();
			}
		});
    }

}
