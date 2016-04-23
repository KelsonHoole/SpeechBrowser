package cn.hukecn.speechbrowser.activity;

import cn.hukecn.speechbrowser.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.gsm.GsmCellLocation;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

public class WelcomeActivity extends Activity {

	Handler handler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
		LinearLayout ll_start = (LinearLayout) findViewById(R.id.ll_start);
		AlphaAnimation aa = new AlphaAnimation(0.2f,1.0f);
		aa.setDuration(1200);
		ll_start.startAnimation(aa);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
			}
		}, 2000);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}
}
