/**
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package cn.hukecn.speechbrowser.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.os.Bundle;
import cn.hukecn.speechbrowser.R;

/**
 * @author liweigao 2015Äê9ÔÂ15ÈÕ
 */
public class SettingActivity extends Activity {
    /*
     * @param savedInstanceState
     */
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
    }

}
