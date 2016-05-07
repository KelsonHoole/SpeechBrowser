package cn.hukecn.speechbrowser.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import cn.hukecn.speechbrowser.R;
import cn.hukecn.speechbrowser.R.id;
import cn.hukecn.speechbrowser.R.layout;

public class WebviewActivity extends Activity {

	WebView mWebview = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
	}
//		mWebview = (WebView) findViewById(R.id.webView);
//		
//		Intent intent = getIntent();
//		if(intent != null)
//		{
//			String url = intent.getStringExtra("url");
//			if(url != null)
//			{
//				mWebview.loadUrl(url);
//				mWebview.getSettings().setJavaScriptEnabled(true);
//			    mWebview.setWebViewClient(webViewClient);
//			    mWebview.setWebChromeClient(webChromeClient);
//			}
//		}	
//	}
//	
//	 public WebViewClient webViewClient = new WebViewClient(){
//	        @Override
//	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//	            view.loadUrl(url);
//	            return true;
//	        }
//	    };
//
//	    public WebChromeClient webChromeClient = new WebChromeClient(){
//	        @Override
//	        public void onProgressChanged(WebView view, int newProgress) {
//	        	((TextView)(findViewById(R.id.title))).setText(view.getTitle());
//	            super.onProgressChanged(view, newProgress);
//	        }
//	    };
}
