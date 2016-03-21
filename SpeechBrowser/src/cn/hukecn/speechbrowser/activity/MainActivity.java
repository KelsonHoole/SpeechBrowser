package cn.hukecn.speechbrowser.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import cn.hukecn.speechbrowser.JsonParser;
import cn.hukecn.speechbrowser.R;
import cn.hukecn.speechbrowser.Shake;
import cn.hukecn.speechbrowser.Shake.ShakeListener;
import cn.hukecn.speechbrowser.StaticString;
import cn.hukecn.speechbrowser.bean.MailListBean;
import cn.hukecn.speechbrowser.bean.NewsBean;
import cn.hukecn.speechbrowser.bean.WeatherBean;
import cn.hukecn.speechbrowser.http.MyHttp;
import cn.hukecn.speechbrowser.http.MyHttp.HttpCallBackListener;
import cn.hukecn.speechbrowser.util.BaiduSearch;
import cn.hukecn.speechbrowser.util.CutWebView;
import cn.hukecn.speechbrowser.util.CutWebView.ReceiveHTMLListener;
import cn.hukecn.speechbrowser.util.CutWebView.ShouldOverrideUrlListener;
import cn.hukecn.speechbrowser.util.PraseCommand;
import cn.hukecn.speechbrowser.util.PraseMailContent;
import cn.hukecn.speechbrowser.util.PraseMailList;
import cn.hukecn.speechbrowser.util.PraseNews;
import cn.hukecn.speechbrowser.util.PraseTencentNews;
import cn.hukecn.speechbrowser.util.PraseWeatherHtml;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import android.accounts.Account;
import android.accounts.OnAccountsUpdateListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements ShakeListener,ReceiveHTMLListener,ShouldOverrideUrlListener{
	List<Integer> cmdList = new ArrayList<Integer>();
	private SoundPool sp;//声明一个SoundPool
	private int music;//定义一个整型用load（）；来设置suondID
	private int newsNumber = -1;
	private static Vibrator mVibrator;
	Button btn_exit = null;
	Button btn_stop = null;
	Button btn_setting = null;
	int browserState = PraseCommand.Cmd_Original;
	long lastTime = 0l;
	long lastShakeTime = 0l;
	String mailCookie = "";
	String msid = "";
	// 语音听写对象
	private SpeechRecognizer mIat;
	// 语音听写UI
	private RecognizerDialog mIatDialog;
	TextView title = null;
	TextView tv_info = null;
	SpeechSynthesizer mTts;
	List<NewsBean> newsList = new ArrayList<NewsBean>();
	List<MailListBean> mailList = new ArrayList<MailListBean>();
	CutWebView webView = null;
	public LocationClient mLocationClient = null;
	BDLocation location = null;
	public BDLocationListener myListener = new MyLocationListener();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID +"=568fba83");   
	
		mIatDialog = new RecognizerDialog(MainActivity.this, mInitListener);
		mIatDialog.setListener(mRecognizerDialogListener);
		
		tv_info = (TextView) findViewById(R.id.info);		
		webView = (CutWebView) findViewById(R.id.webview);
		webView.setOnReceiveHTMLListener(this);
		webView.setOnShouldOverrideUrlListener(this);
		title = (TextView) findViewById(R.id.title);
		btn_exit = (Button) findViewById(R.id.btn_exit);
		
		btn_exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				exitApp();
			}
		});
		
		btn_stop = (Button)findViewById(R.id.btn_stop);
		btn_stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(mTts.isSpeaking())
					mTts.stopSpeaking();
			}
		});
		
		btn_setting = (Button) findViewById(R.id.btn_setting);
		btn_setting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,SettingActivity.class);
				startActivity(intent);
			}
		});
		
		mTts= SpeechSynthesizer.createSynthesizer(this, null);  
		mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoqi");
		mTts.setParameter(SpeechConstant.SPEED, "50");
		mTts.setParameter(SpeechConstant.VOLUME, "50");
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端  
		
		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		music = sp.load(this, R.raw.shake, 1);
	
        mVibrator = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);  

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();
	}
	private InitListener mInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
		}
	};
	
	private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			//tv_info.setText("");
			List<String> list= JsonParser.parseIatResult(results.getResultString());

			long current = System.currentTimeMillis();
			if(list.get(0).equals("。") || list.get(0).equals(""))
				return ;
			if(list.get(list.size() -1).equals("。"))
				list.remove(list.size()-1);
			
			Calendar c = Calendar.getInstance();  
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			int secend = c.get(Calendar.SECOND);
			
			if(current - lastTime > 800)
			{
				browserState = PraseCommand.prase(list);
				lastTime = current;
				if(browserState != PraseCommand.Cmd_NewsNum)
					cmdList.add(browserState);
				
				switch (browserState) {
				case PraseCommand.Cmd_Search:
					cmdSearch(list);
					break;
				case PraseCommand.Cmd_News:
					cmdReadNews();
					
					break;
				case PraseCommand.Cmd_Weather:
					String cityname = "";
					for(String item:list)
						cityname += item;
					
					if(cityname.indexOf("天气") != -1)
						cityname = cityname.replace("天气", "");
					
					cmdWeather(cityname);
					
//					else
//					{
//						cityname = location.getCity();
//						if(cityname != null && cityname.length() > 0)
//						{
//							cityname = cityname.replace("市", "");
//							cmdWeather(cityname);
//						}else
//						{
//							cmdWeather("武汉");
//						}
//					}
					break;
				
				case PraseCommand.Cmd_NewsNum:
//					tv_info.append(PraseCommand.praseNewsIndex(list)+"\n");
					if(cmdList.size() == 0)
					{
						mTts.startSpeaking("指令错误，请输入正确指令",mSynListener);
						break;
					}
					
					if(cmdList.get(cmdList.size() -1) == PraseCommand.Cmd_Mail)
					{
						//进入读邮件详情
						if(mailList != null && mailList.size()>0)
						{
							browserState = PraseCommand.Cmd_Mail_MailContent;
							readMailContent(PraseCommand.praseNewsIndex(list));
						}else
							mTts.startSpeaking("获取邮件详情失败，请稍后再试",mSynListener);
						break;
					}
					
					if(cmdList.get(cmdList.size() -1) == PraseCommand.Cmd_News)
					{
						//进入读新闻详情
						if(newsList != null && newsList.size() > 0)
						{	
							readNewsContent(PraseCommand.praseNewsIndex(list));
						}else
							mTts.startSpeaking("获取新闻详情失败，请稍后再试",mSynListener);
						break;
					}
					
					mTts.startSpeaking("指令错误，请输入正确指令",mSynListener);
					break;
				case PraseCommand.Cmd_Location:
//					if(mLocationClient.isStarted())
//						mLocationClient.stop();
//			        mLocationClient.start();
					mTts.startSpeaking("您当前位于："+location.getAddrStr()+"附近",mSynListener);
					String url = "http://m.baidu.com/s?word="+location.getProvince()+location.getCity();
					webView.loadUrl(url);
					break;
				case PraseCommand.Cmd_Exit:
					mTts.startSpeaking("正在关闭机器人。。。", mSynListener);
					handler.sendEmptyMessageDelayed(0, 3000);
					break;
				case PraseCommand.Cmd_Mail:
					cmdMail();
					break;
				case PraseCommand.Cmd_Err:
				case PraseCommand.Cmd_Other:
				default:
					mTts.startSpeaking("指令错误，请输入正确指令",mSynListener);
					break;
				}
			}
		}
		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			//showTip(error.getPlainDescription(true));
		}
	};
	
	private void cmdMail()
	{
		/*
		f:xhtmlmp
		delegate_url:
		f:xhtmlmp
		action:
		tfcont:
		uin:hukecn
		aliastype:@qq.com
		pwd:00220388066
		mss:1
		mtk:
		btlogin:登录
		*/
		
		String url = StaticString.mailLogin;
		String postDate = "f=xhtmlmp&uin=hukecn&aliastype=@qq.com&pwd=00220388066&mss=1&btlogin=登陆";
		webView.postUrl(url, EncodingUtils.getBytes(postDate, "utf-8"));
	}
	
	protected void readMailContent(int praseNewsIndex) {
		// TODO Auto-generated method stub
		if(praseNewsIndex > mailList.size())
			mTts.startSpeaking("该条数不存在，请重新输入指令", mSynListener);
		else
			webView.loadUrl(mailList.get(praseNewsIndex - 1).mailUrl);
	}

	private void readNewsContent(final int praseNewsIndex) {
		// TODO Auto-generated method stub
		newsNumber = praseNewsIndex;
		if(praseNewsIndex > newsList.size())
			mTts.startSpeaking("该条数不存在，请重新输入指令", mSynListener);
		else
			webView.loadUrl(newsList.get(praseNewsIndex - 1).newsUrl);
	}
	@Override
	public void onShake() {
		// TODO Auto-generated method stub
		if(mTts.isSpeaking())
			mTts.stopSpeaking();
		if(System.currentTimeMillis() - lastShakeTime > 1200)
		{	
			mVibrator.vibrate(500);
			sp.play(music, 1, 1, 0, 0, 1);
			mIatDialog.show();
		}
		lastShakeTime = System.currentTimeMillis();
	}
	
	
	private SynthesizerListener mSynListener = new SynthesizerListener()
	{  
	    //会话结束回调接口，没有错误时，error为null  
	    public void onCompleted(SpeechError error) {
	    	btn_stop.setText("开始播放");
	    	//Toast.makeText(getApplicationContext(), "completed", Toast.LENGTH_SHORT).show();
	    }  
	    //缓冲进度回调  
	    //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。  
	    public void onBufferProgress(int percent, int beginPos, int endPos, String info) {}  
	    //开始播放  
	    public void onSpeakBegin() {
	    	//Toast.makeText(getApplicationContext(), "Begin", Toast.LENGTH_SHORT).show();
	    	btn_stop.setText("停止播放");
	    }  
	    
	    
	    //暂停播放  
	    public void onSpeakPaused() {
	    	//Toast.makeText(getApplicationContext(), "pause", Toast.LENGTH_SHORT).show();
	    }  
	    //播放进度回调  
	    //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.  
	    public void onSpeakProgress(int percent, int beginPos, int endPos) {}  
	    //恢复播放回调接口  
	    public void onSpeakResumed() {}  
	//会话事件回调接口  
	    public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
	    	if(arg0 == SpeechEvent.EVENT_TTS_CANCEL)
	    		btn_stop.setText("开始播放");
	    }  
	};
	
	
	private void cmdWeather(final String cityname) {
		// TODO Auto-generated method stub
		MyHttp.get(StaticString.weatherUrl, "cityname="+cityname, new HttpCallBackListener() 
		{
			@Override
			public void onHttpCallBack(int statusCode, String responseStr) {
				// TODO Auto-generated method stub
				if(statusCode == 200)
				{
					//说出了正确的城市名
					try {
						JSONObject jsonObject = new JSONObject(responseStr);
						int errNum = jsonObject.getInt("errNum");
						if(errNum == 0)
						{
							JSONObject jsonData = jsonObject.getJSONObject("retData");
//							String city = jsonData.getString("city");
//							
//							if(city == null || city.length() == 0)
//							{
//								city= "武汉";
//							}
							String url = "http://m.baidu.com/s?word="+cityname+"天气";
							webView.loadUrl(url);
							
							
							String windStr = jsonData.getString("WS");
							int start = windStr.indexOf("(");
							if(start > 0)
								windStr = windStr.substring(0,start);
							
							mTts.startSpeaking(jsonData.getString("city")+",今日天气："+
									jsonData.getString("weather")+",最高气温："+
									jsonData.getString("h_tmp")+"摄氏度，最低气温："+
									jsonData.getString("l_tmp")+"摄氏度，"+
									jsonData.getString("WD")+"，风力："+
									windStr, mSynListener);
							
							return;
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
					}
				}
				//没有说出正确的城市名,默认本地天气
				String cityname = "test";
				if(cityname != null && cityname.length() > 0)
				{
					cityname = location.getCity();
					cityname = cityname.replace("市", "");
					MyHttp.get(StaticString.weatherUrl, "cityname="+cityname, new HttpCallBackListener()
					{
						@Override
						public void onHttpCallBack(int statusCode, String responseStr) {
							// TODO Auto-generated method stub
							if(statusCode == 200)
							{
								try {
									JSONObject jsonObject = new JSONObject(responseStr);
									int errNum = jsonObject.getInt("errNum");
									if(errNum == 0)
									{
										JSONObject jsonData = jsonObject.getJSONObject("retData");
										String citycode = jsonData.getString("city");
										if(citycode == null || citycode.length() == 0)
										{
											citycode= "武汉";
										}
										
										String url = "http://m.baidu.com/s?word="+ citycode +"天气";
										webView.loadUrl(url);
										
										String windStr = jsonData.getString("WS");
										int start = windStr.indexOf("(");
										if(start > 0)
											windStr = windStr.substring(0,start);
										
										mTts.startSpeaking(jsonData.getString("city")+",今日天气："+
												jsonData.getString("weather")+",最高气温："+
												jsonData.getString("h_tmp")+"摄氏度，最低气温："+
												jsonData.getString("l_tmp")+"摄氏度，"+
												jsonData.getString("WD")+"，风力："+
												windStr, mSynListener);
										return;
										}	
											
									} catch(JSONException e) {
										Toast.makeText(getApplicationContext(), "天气失败", Toast.LENGTH_SHORT).show();
										mTts.startSpeaking("获取天气失败，请稍后再试",mSynListener);
									}
								}else
								{
									Toast.makeText(getApplicationContext(), "天气失败", Toast.LENGTH_SHORT).show();
									mTts.startSpeaking("获取天气失败，请稍后再试",mSynListener);
								}
						}});	
					}
				return;
			}
		});
	}
	
	private void cmdReadNews(){
		webView.loadUrl(PraseTencentNews.HOMEURL);
//		
//		PraseTencentNews.getNewsList(new NewsCallback() {
//			@Override
//			public void onNewsListCallBack(List<NewsBean> list) {
//				// TODO Auto-generated method stub
//				newsList = list;
//				String titleStr = "";
//				for(int i = 1;i <= list.size();i++)
//				{
//					titleStr += "第"+i+"条、"+list.get(i-1).newsTitle+"\n";	
////					tv_info.append(i+"、"+list.get(i-1).newsTitle+"\n");
//				}
//				mTts.startSpeaking("即将为您播报今日新闻。\n" + titleStr, mSynListener);
//			}
//
//			@Override
//			public void onNewsContentCallBack(String content) {
//				// TODO Auto-generated method stub
//				//获取标题时不用实现该方法
//			}
//		});
	}
	
	private void cmdSearch(List<String> list) {
		String str = "";
		for(String temp:list)
		{
			str += temp;
		}
		str = str.replace("搜索", "");
		String url = "http://m.baidu.com/s?word="+str;
		webView.loadUrl(url);
	}
	private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Battery_Saving);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=0;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(false);//可选，默认false,设置是否使用gps
        //option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        //option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死  
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }
	
	
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation arg0) {
			location = arg0;
			//Toast.makeText(getApplicationContext(), arg0.getCity(), Toast.LENGTH_SHORT).show();
			//mTts.startSpeaking(arg0.getLocationDescribe(), mSynListener);
		}
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				exitApp();
				break;
			case 1:
				List<String> resList = (List<String>) msg.obj;
				
				break;
			}
		}
	};
	
	protected void onResume() {
		Shake.registerListener(this, this);
		super.onResume();
	}
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Shake.removeListener();
		if(mTts.isSpeaking())
			mTts.stopSpeaking();
		mLocationClient.stop();
		mLocationClient.unRegisterLocationListener(myListener);
		super.onDestroy();
	}
	
	private void exitApp(){
		Shake.removeListener();
		if(mTts.isSpeaking())
			mTts.stopSpeaking();
		mLocationClient.stop();
		mLocationClient.unRegisterLocationListener(myListener);
		finish();
		System.exit(0);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	 
	 	@Override
	    public void onBackPressed() {
	 		if(mTts.isSpeaking())
				mTts.stopSpeaking();
	 		browserState = PraseCommand.Cmd_Original;
	        if(webView.canGoBack())
	            webView.goBack();
	        else
	            super.onBackPressed();
	    }
	 	
	    public static void writeFileSdcard(String fileName,String message)
	    { 
	    	try {  
	    	    File file = new File(Environment.getExternalStorageDirectory(),  
	    	            "c.txt");  
	    	        FileOutputStream fos = new FileOutputStream(file, false);  
	    	 
	    	           fos.write(message.getBytes("utf-8"));  
	    	           fos.close();  
	    	           //Toast.makeText(getApplicationContext(), "写入成功", Toast.LENGTH_SHORT).show();
	    	} catch (Exception e) {
	    	    e.printStackTrace();  
	    	}  
	    }
	    
		@Override
		public void onReceiveHTML(String html) {
			// TODO Auto-generated method stub
			//Document doc = Jsoup.parse(html);
			//title.setText(doc.getElementsByTag("title").get(0).text());
			switch (browserState) {
			case PraseCommand.Cmd_Search:
				List<String> resList = BaiduSearch.praseSearchResultList(html);
				if(resList.size() != 0)
				{
					for(int i = 1;i <= resList.size();i++)
						tv_info.append("第"+i+"条、"+resList.get(i-1)+"\n");
					mTts.startSpeaking("以下是搜索结果："+tv_info.getText().toString(), mSynListener);
				}else
				{
					mTts.startSpeaking("搜索出错，请重试。", mSynListener);
				}
				break;
			case PraseCommand.Cmd_News:
				writeFileSdcard("",html);
				newsList = PraseTencentNews.getNewsList(html);
				String titleStr = "";
				for(int i = 1;i <= newsList.size();i++)
				{
					titleStr += "第"+i+"条、"+newsList.get(i-1).newsTitle+"\n";
				}
				mTts.startSpeaking("即将为您播报今日新闻。\n" + titleStr, mSynListener);
				break;
			case PraseCommand.Cmd_NewsNum:
				String content = PraseTencentNews.getNewsContent(html);
				mTts.startSpeaking("第" + newsNumber + "条新闻，标题：" + newsList.get(newsNumber-1).newsTitle+content, mSynListener);
				break;
			case PraseCommand.Cmd_Mail:
				//Log.e("111",webView.getCookie());
				mailCookie = webView.getCookie();
				int cookieStart = mailCookie.indexOf("msid=") + 5;
				int cookieEnd = mailCookie.indexOf(";", cookieStart);
				if(cookieStart != -1 && cookieEnd != -1 && cookieEnd > cookieStart)
				{
					msid = mailCookie.substring(cookieStart,cookieEnd);
					browserState = PraseCommand.Cmd_Mail_Home;
					
					//webView.loadUrl(StaticString.mailHome + msid + "&first=1&bmkey=");
					webView.loadUrl(StaticString.mailHome2 + msid);

					mTts.startSpeaking("正在登陆邮箱...", mSynListener);
				}else
				{
					mTts.startSpeaking("邮箱登陆失败，请稍后再试", mSynListener);
				}
				break;
			case PraseCommand.Cmd_Weather:
				//Toast.makeText(getApplicationContext(), "天气", Toast.LENGTH_SHORT).show();
//				List<WeatherBean> list = PraseWeatherHtml.praseWeatherList(html);
//				if(list.size() == 0)
//				{
//					mTts.startSpeaking("获取天气信息失败，请稍后再试", mSynListener);
//				}else
//				{
//					int start = html.indexOf("更换城市")+6;
//					int end = html.indexOf("<", start);
//					String weatherStr = "";
//					if(start != -1 && end != -1 && end > start)
//					{
//						String cityname = html.substring(start,end);
//						if(cityname != null && cityname.length() > 0)
//							weatherStr += "即将为您播报"+cityname+"未来七天天气状况：";
//					}
//					if(weatherStr.length() == 0)
//						weatherStr += "即将为您播报未来七天天气状况：";
//
//					for(WeatherBean bean:list)
//					{
//						weatherStr+= bean.date +"天气："+bean.weather+"，温度："+bean.temp+"。\n";
//					}
//					mTts.startSpeaking(weatherStr, mSynListener);
//				}
				
				
				break;
			case PraseCommand.Cmd_Mail_Home:
			{
				browserState = PraseCommand.Cmd_Mail_InBox;
				webView.loadUrl(StaticString.mailInboxList2+msid+"&folderid=1&page=0&pagesize=10&sorttype=time&t=mail_list&loc=today,,,151&version=html");
				break;
			}
			case PraseCommand.Cmd_Mail_InBox:				
				Toast.makeText(getApplicationContext(), "邮件列表", Toast.LENGTH_LONG).show();
				if(html.length() > 0)
				{
					mailList = PraseMailList.parseMailList(html);
					if(mailList.size() == 0)
						mTts.startSpeaking("邮件列表读取失败，请稍后再试", mSynListener);
					else
					{
						String mailListStr = "最近"+mailList.size()+"封邮件信息如下：";
						for(int i = 1;i <= mailList.size();i++)
						{
							mailListStr += "第"+i+"条："+mailList.get(i-1).descStr+"。";
						}
						mTts.startSpeaking(mailListStr, mSynListener);
					}
				}else
				{
					mTts.startSpeaking("邮件列表读取失败，请稍后再试", mSynListener);
				}
				break;
			case PraseCommand.Cmd_Mail_MailContent:
			{
				if(html.length() > 0)
				{
					String mailContent = PraseMailContent.praseMailContent(html);
					mTts.startSpeaking("邮件内容如下："+mailContent, mSynListener);
				}
				else
				{
					mTts.startSpeaking("邮件详情读取失败，请稍后再试", mSynListener);
				}
			}
			default:
				
				break;
			}
			
		}

		@Override
		public void onShouldOverrideUrl(String url) {
			// TODO Auto-generated method stub
			browserState = PraseCommand.Cmd_Original;
			if(mTts.isSpeaking())
				mTts.stopSpeaking();
		}
}
