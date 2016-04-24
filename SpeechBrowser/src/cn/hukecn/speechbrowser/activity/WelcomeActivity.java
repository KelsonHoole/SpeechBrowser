package cn.hukecn.speechbrowser.activity;

import cn.hukecn.speechbrowser.CrashHandler;
import cn.hukecn.speechbrowser.R;
import cn.hukecn.speechbrowser.DAO.MyDataBase;
import cn.hukecn.speechbrowser.location.BaseAppLocation;
import cn.hukecn.speechbrowser.util.ToastUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.gsm.GsmCellLocation;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;

public class WelcomeActivity extends Activity {

	Handler handler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		setTheme(R.style.AppSplash);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
//		LinearLayout ll_start = (LinearLayout) findViewById(R.id.ll_start);
//		AlphaAnimation aa = new AlphaAnimation(0.2f,1.0f);
//		aa.setDuration(1200);
//		ll_start.startAnimation(aa);
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				initApp();
			}
		},1000);
		
		
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
			}
		}, 2000);
		
		
		
//		aa.setAnimationListener(new AnimationListener() {
//			
//			@Override
//			public void onAnimationStart(Animation animation) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				// TODO Auto-generated method stub
//				initApp();
//			}
//		});
		
	}
	
	private void initApp() {
		// TODO Auto-generated method stub
		ToastUtil.init(getApplicationContext());
		MyDataBase.init(getApplicationContext());
		CrashHandler catchHandler = CrashHandler.getInstance();  
        catchHandler.init(getApplicationContext());
        BaseAppLocation baseAppLocation = BaseAppLocation.getInstance();
        baseAppLocation.init(getApplicationContext());
        
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}
}
