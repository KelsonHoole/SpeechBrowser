package cn.hukecn.speechbrowser.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class CutWebView extends WebView {

	ReceiveHTMLListener listener = null;
	String cookieStr = "";
	ShouldOverrideUrlListener mShouldOverrideUrlListener = null;
	Handler handler = new Handler(){
		public void handleMessage(Message msg) 
		{
			if(listener != null)
				listener.onReceiveHTML((String)msg.obj);
		};
	};
	private ProgressBar progressbar;
	public CutWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
        progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,0,0));
        addView(progressbar);
        setWebChromeClient(new WebChromeClient());
        setWebViewClient(new WebViewCLient());
        WebSettings settings = getSettings();
        settings.setAllowFileAccess(true);
        settings.setUserAgentString("Mozilla/5.0 (Linux; U; Android 6.0; zh-cn; PLK-UL00 Build/HONORPLK-UL00) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 Chrome/37.0.0.0 MQQBrowser/6.0 Mobile Safari/537.36");
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        addJavascriptInterface(new JSLinster(),"HTML");
        loadUrl("http://m.baidu.com");
	}
	
	public void setOnReceiveHTMLListener(ReceiveHTMLListener listener){
		this.listener = listener;
	}

	 public class WebChromeClient extends android.webkit.WebChromeClient {
	        @Override
	        public void onProgressChanged(WebView view, int newProgress) {
	            if (newProgress == 100) {
	                progressbar.setVisibility(GONE);
	            } else {
	                if (progressbar.getVisibility() == GONE)
	                    progressbar.setVisibility(VISIBLE);
	                progressbar.setProgress(newProgress);
	            }
	            super.onProgressChanged(view, newProgress);
	        }

	    }
	 
	 public class WebViewCLient extends WebViewClient{
	        @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	            if(mShouldOverrideUrlListener != null)
	            	mShouldOverrideUrlListener.onShouldOverrideUrl(url);
	        	view.loadUrl(url);
	            return true;
	        }
	        	

	        @Override
	        public void onPageFinished(WebView view, String url) {
	        	view.loadUrl("javascript:window.HTML.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
	        	CookieManager cookieManager = CookieManager.getInstance();
	            cookieStr = cookieManager.getCookie(url);
	        	super.onPageFinished(view, url);
	        }
	    }
	 
	 @Override
	 protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		 LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
		 lp.x = l;
		 lp.y = t;
		 progressbar.setLayoutParams(lp);
		 super.onScrollChanged(l, t, oldl, oldt);
	 }
	
	 @SuppressLint("AddJavascriptInterface")
	    public class JSLinster{
	        @JavascriptInterface
	        public void getHtml(String html)
	        {
	        	html = "<html>"+html+"</html>";
				Message msg = handler.obtainMessage();
				msg.obj = html;
				handler.sendMessage(msg);
	        }
	    }
	 
	 public interface ReceiveHTMLListener{
		 public void onReceiveHTML(String html);
	 }
	 
	 public String getCookie(){
		 return cookieStr;
	 }
	 
	 public void setOnShouldOverrideUrlListener(ShouldOverrideUrlListener listener)
	 {
		 this.mShouldOverrideUrlListener = listener;
	 }
	 
	 public interface ShouldOverrideUrlListener{
		 public void onShouldOverrideUrl(String url);
	 }
}
