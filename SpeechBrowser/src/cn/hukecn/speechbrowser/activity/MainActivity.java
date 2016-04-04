package cn.hukecn.speechbrowser.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.jsoup.Jsoup;

import cn.hukecn.speechbrowser.JsonParser;
import cn.hukecn.speechbrowser.R;
import cn.hukecn.speechbrowser.Shake;
import cn.hukecn.speechbrowser.Shake.ShakeListener;
import cn.hukecn.speechbrowser.DAO.MyDataBase;
import cn.hukecn.speechbrowser.bean.HtmlBean;
import cn.hukecn.speechbrowser.bean.LocationBean;
import cn.hukecn.speechbrowser.bean.MailListBean;
import cn.hukecn.speechbrowser.bean.NewsBean;
import cn.hukecn.speechbrowser.util.BaiduSearch;
import cn.hukecn.speechbrowser.util.CutWebView;
import cn.hukecn.speechbrowser.util.MenuPopupWindow;
import cn.hukecn.speechbrowser.util.CutWebView.ReceiveHTMLListener;
import cn.hukecn.speechbrowser.util.CutWebView.ShouldOverrideUrlListener;
import cn.hukecn.speechbrowser.util.PraseCommand;
import cn.hukecn.speechbrowser.util.PraseMailContent;
import cn.hukecn.speechbrowser.util.PraseMailList;
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
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.view.TintableBackgroundView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity extends Activity implements ShakeListener,ReceiveHTMLListener,ShouldOverrideUrlListener,OnClickListener{
	public final int REQUEST_CODE_BOOKMARK = 1;
	List<Integer> cmdList = new ArrayList<Integer>();
	private SoundPool sp;//声明一个SoundPool
	private int music;//定义一个整型用load（）；来设置suondID
	private int newsNumber = -1;
	private static Vibrator mVibrator;
	private HtmlBean htmlBean = new HtmlBean();
	boolean isPause = false;
	int btntate = 0;//0——开始，1——暂停，2——停止
	EditText et_head = null;
	ImageButton btn_menu = null,
			btn_left = null,
			btn_right = null,
			btn_state = null;
	ImageButton btn_microphone = null;
	int browserState = PraseCommand.Cmd_Original;
	long lastTime = 0l;
	long lastShakeTime = 0l;
	String mailCookie = "";
	String msid = "";
	ProgressBar speechProgressBar = null;
	// 语音听写对象
	//private SpeechRecognizer mIat;
	// 语音听写UI
	private RecognizerDialog mIatDialog;
	TextView title = null;
	TextView tv_info = null;
	SpeechSynthesizer mTts;
	List<NewsBean> newsList = new ArrayList<NewsBean>();
	List<MailListBean> mailList = new ArrayList<MailListBean>();
	CutWebView webView = null;
	public LocationClient mLocationClient = null;
//	BDLocation location = null;
	public BDLocationListener myListener = new MyLocationListener();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID +"=568fba83");   
	
		mIatDialog = new RecognizerDialog(MainActivity.this, mInitListener);
		mIatDialog.setListener(mRecognizerDialogListener);
		
		title = (TextView) findViewById(R.id.title);
		tv_info = (TextView) findViewById(R.id.info);		
		webView = (CutWebView) findViewById(R.id.webview);
		et_head = (EditText) findViewById(R.id.et_head);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		btn_right = (ImageButton) findViewById(R.id.btn_right);
		btn_state = (ImageButton) findViewById(R.id.btn_state);
		btn_menu = (ImageButton) findViewById(R.id.btn_menu);
		btn_microphone = (ImageButton) findViewById(R.id.btn_microphone);
		speechProgressBar = (ProgressBar) findViewById(R.id.speechProgressBar);
		
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);
		btn_microphone.setOnClickListener(this);
		btn_state.setOnClickListener(this);
		btn_menu.setOnClickListener(this);
	
		webView.setOnReceiveHTMLListener(this);
		webView.setOnShouldOverrideUrlListener(this);
//		btn_exit = (Button) findViewById(R.id.btn_exit);
		
//		btn_exit.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
////				exitApp();
//			}
//		});
		
//		btn_stop = (Button)findViewById(R.id.btn_stop);
//		btn_stop.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				if(mTts.isSpeaking())
//					mTts.stopSpeaking();
//			}
//		});
		
		

		
		et_head.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);  
				boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开  
				if(isOpen)
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
				if (actionId==EditorInfo.IME_ACTION_SEARCH)
				{
			        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 5.1.1; zh-cn; PLK-UL00 Build/HONORPLK-UL00) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 MQQBrowser/5.3 Mobile Safari/537.36");
					browserState = PraseCommand.Cmd_Original;
			        if(v.getText().toString().indexOf(".com") != -1 || v.getText().toString().indexOf(".cn") != -1)
					{
						if(v.getText().toString().indexOf("http") == -1)
						{
							v.setText("http://"+v.getText());
						}
						
						webView.loadUrl(v.getText().toString());
					}else
					{
						String url = "http://m.baidu.com/s?word="+v.getText();
						webView.loadUrl(url);
					}
					return true;
				}else
					return false;
			}
		});
		
		et_head.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), hasFocus+"", Toast.LENGTH_SHORT).show();
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
			tv_info.setText("");
			speechProgressBar.setVisibility(View.GONE);
			htmlBean.content = "";
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
//					String url = "http://m.baidu.com/s?word=天气预报";
					String url = "http://weather1.sina.cn/?vt=4";
					webView.loadUrl(url);
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
					webView.loadUrl("http://map.baidu.com/mobile/webapp/index/index/foo=bar/vt=map");
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
		webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
		webView.loadUrl("https://ui.ptlogin2.qq.com/cgi-bin/login?style=9&appid=522005705&daid=4&s_url=https%3A%2F%2Fw.mail.qq.com%2Fcgi-bin%2Flogin%3Fvt%3Dpassport%26vm%3Dwsk%26delegate_url%3D%26f%3Dxhtml%26target%3D&hln_css=http%3A%2F%2Fmail.qq.com%2Fzh_CN%2Fhtmledition%2Fimages%2Flogo%2Fqqmail%2Fqqmail_logo_default_200h.png&low_login=1&hln_autologin=%E8%AE%B0%E4%BD%8F%E7%99%BB%E5%BD%95%E7%8A%B6%E6%80%81&pt_no_onekey=1");
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
//	    	btn_stop.setText("开始播放");
	    	speechProgressBar.setVisibility(View.GONE);
	    	btn_state.setImageResource(R.drawable.start);
			btntate = 0;
	    }  
	    //缓冲进度回调  
	    //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。  
	    public void onBufferProgress(int percent, int beginPos, int endPos, String info) {}  
	    //开始播放  
	    public void onSpeakBegin() {
	    	//Toast.makeText(getApplicationContext(), "Begin", Toast.LENGTH_SHORT).show();
//	    	btn_stop.setText("停止播放");
	    	speechProgressBar.setVisibility(View.VISIBLE);
	    	speechProgressBar.setMax(100);
	    	speechProgressBar.setProgress(0);
	    	btn_state.setImageResource(R.drawable.pause);
			btntate = 1;
	    }  
	    
	    
	    //暂停播放  
	    public void onSpeakPaused() {
//	    	btn_state.setImageResource(R.drawable.start);
//			btntate = 0;
	    }  
	    //播放进度回调  
	    //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.  
	    public void onSpeakProgress(int percent, int beginPos, int endPos) {
	    	speechProgressBar.setProgress(percent);
	    }  
	    //恢复播放回调接口  
	    public void onSpeakResumed() {
//	    	btn_state.setImageResource(R.drawable.pause);
//			btntate = 1;
	    }  
	//会话事件回调接口  
	    public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
//	    	if(arg0 == SpeechEvent.Event)
//	    		btn_stop.setText("开始播放");
	    	
	    }  
	};

	
	private void cmdReadNews(){
		webView.loadUrl(PraseTencentNews.HOMEURL);
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
			//Toast.makeText(getApplicationContext(), arg0.getCity(), Toast.LENGTH_SHORT).show();
			//mTts.startSpeaking(arg0.getLocationDescribe(), mSynListener);
			MyDataBase db = new MyDataBase(MainActivity.this);
			LocationBean bean = new LocationBean();
			bean.latitude = arg0.getLatitude()+"";
			bean.longitude = arg0.getLongitude()+"";
			bean.time = arg0.getTime()+"";
			long log = db.insert(bean);
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
		Shake.removeListener();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(mTts.isSpeaking())
			mTts.stopSpeaking();
		mLocationClient.stop();
		mLocationClient.unRegisterLocationListener(myListener);
		super.onDestroy();
	}
	
	private void exitApp(){
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
		public void onReceiveHTML(String url,String html) {
			// TODO Auto-generated method stub
//			tv_info.setText(html);
			int start = 0,end = 0;
			et_head.setHint(Jsoup.parse(html).title());
			htmlBean.url = url;
			htmlBean.html = html;
			
			btn_state.setImageResource(R.drawable.start);
			btntate = 0;
			
			switch (browserState) {
			case PraseCommand.Cmd_Search:
				processSearchResult();
				break;
			case PraseCommand.Cmd_News:
				processNewsList();
				break;
			case PraseCommand.Cmd_NewsNum:
				processNewsContent();
				break;
			case PraseCommand.Cmd_Mail:
				browserState = PraseCommand.Cmd_Mail_Home;
				webView.loadUrl("javascript:"
						+ "document.getElementById(\"u\").value= \"229164940\";"
						+ "document.getElementById(\"p\").value= \"huke2851550\";"
						+ "document.getElementById(\"go\").click();");
				break;
			case PraseCommand.Cmd_Weather:
				
//				int start = html.indexOf("http://baidu.weather.com.cn");
//				if(start == -1)
//				{
//					Toast.makeText(getApplicationContext(), "未获取到天气，请检查位置权限", Toast.LENGTH_SHORT).show();
//					return;
//				}
//				int end = html.indexOf("}", start);
//				if(end == -1)
//				{
//					Toast.makeText(getApplicationContext(), "未获取到天气，请检查位置权限", Toast.LENGTH_SHORT).show();
//					return;
//				}
//				
//				end--;
//				String weatherurl = html.substring(start, end);
//				if(weatherurl != null && weatherurl.length() > 0)
//				{
//					browserState = PraseCommand.Cmd_WeatherComCn;
//					webView.loadUrl(weatherurl);
//				}
//				else
//				{
//					Toast.makeText(getApplicationContext(), "未获取到天气，请检查位置权限", Toast.LENGTH_SHORT).show();
//					return;
//				}
				htmlBean.content = PraseWeatherHtml.praseWeatherList(html);
				mTts.startSpeaking(htmlBean.content,mSynListener);
				break;
//			case PraseCommand.Cmd_WeatherComCn:
//			{
//				processWeather();
//				break;
//			}
			case PraseCommand.Cmd_Mail_Home:
			{
				mailCookie = webView.getCookie();
				int cookieStart = mailCookie.indexOf("msid=") + 5;
				int cookieEnd = mailCookie.indexOf(";", cookieStart);
				if(cookieStart != -1 && cookieEnd != -1 && cookieEnd > cookieStart)
				{
					msid = mailCookie.substring(cookieStart,cookieEnd);
					
					start = html.indexOf("/cgi-bin/mail_list?");
					//判断是否是邮箱主界面
					if(start != -1)
					{
						browserState = PraseCommand.Cmd_Mail_InBox;
						end = html.indexOf(">", start);
						if(end != -1)
						{
							webView.loadUrl("https://w.mail.qq.com/cgi-bin/mail_list?fromsidebar=1&sid="+msid+"&folderid=1&page=0&pagesize=10&sorttype=time&t=mail_list&loc=today,,,151&version=html");	
							break;
						}else
							mTts.startSpeaking("邮箱登陆失败，请稍后再试", mSynListener);
					}
				}else
				{
					mTts.startSpeaking("邮箱登陆失败，请稍后再试", mSynListener);
				}
				break;
			}
			case PraseCommand.Cmd_Mail_InBox:	
				processMailList();
				break;
			case PraseCommand.Cmd_Mail_MailContent:
				processMailContent();
			default:
				break;
			}
			
			if(htmlBean.content.length() == 0)
			{
				String content = Jsoup.parse(html).body().text();
				tv_info.setText(content); 
				htmlBean.content = content;
			}
			else
				tv_info.setText(htmlBean.content);
		}

		@Override
		public void onShouldOverrideUrl(String url) {
			// TODO Auto-generated method stub
			et_head.clearFocus();
			if(browserState != PraseCommand.Cmd_Mail_Home && browserState != PraseCommand.Cmd_Mail_InBox)
				browserState = PraseCommand.Cmd_Original;
			if(mTts.isSpeaking())
				mTts.stopSpeaking();
			
			htmlBean.content = "";
			tv_info.setText("");
			speechProgressBar.setVisibility(View.GONE);
		}

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			et_head.clearFocus();
			switch (v.getId()) 
			{
			case R.id.btn_m_bookmark:
				Intent intent = new Intent(MainActivity.this,BookMarkActivity.class);
				intent.putExtra("url", htmlBean.url);
				intent.putExtra("title", Jsoup.parse(htmlBean.html).title());
				startActivityForResult(intent, REQUEST_CODE_BOOKMARK);
				break;
			case R.id.btn_m_email:
				intent = new Intent(MainActivity.this,MailManagerActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_m_setting:
				intent = new Intent(MainActivity.this,SettingActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_m_exit:
				exitApp();
				break;
			case R.id.btn_menu:
				MenuPopupWindow popWindow = new MenuPopupWindow(MainActivity.this,MainActivity.this,getWindow());
				popWindow.showPopupWindow(findViewById(R.id.toolsBar));
				popWindow.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub
						btn_menu.setImageResource(R.drawable.menu);
					}
				});
				btn_menu.setImageResource(R.drawable.down);
				break;
			case R.id.btn_left:
				if(mTts.isSpeaking())
					mTts.stopSpeaking();
		 		browserState = PraseCommand.Cmd_Original;
		        if(webView.canGoBack())
		            webView.goBack();
		        else
		        {
		        	Toast.makeText(getApplicationContext(), "已经是第一页了", Toast.LENGTH_SHORT).show();
		        }
				break;
			case R.id.btn_right:
				if(mTts.isSpeaking())
					mTts.stopSpeaking();
		 		browserState = PraseCommand.Cmd_Original;
				if(webView.canGoForward())
					webView.goForward();
				else
				{
					Toast.makeText(getApplicationContext(), "已经是最后一页了", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btn_state:
				switch (btntate) {
				case 0:
					if(isPause)
					{
						mTts.resumeSpeaking();
						isPause = false;
						btn_state.setImageResource(R.drawable.pause);
						btntate = 1;
					}else
					{
						if(!mTts.isSpeaking())
							if(htmlBean.content.length() > 0)
								mTts.startSpeaking(htmlBean.content, mSynListener);
							else
								mTts.startSpeaking("暂无可播放内容",mSynListener);
					}
					break;
				case 1:
					mTts.pauseSpeaking();
					isPause = true;
					btn_state.setImageResource(R.drawable.start);
					btntate = 0;
					break;
				case 2:
					if(!mTts.isSpeaking())
						mTts.stopSpeaking();
					btn_state.setImageResource(R.drawable.start);
					btntate = 0;
					break;
				default:
					break;
				}
						
				break;
			case R.id.btn_microphone:
				onShake();
				break;
			default:
				break;
			}
		}
		
		public void processMailContent()
		{
			if(htmlBean.html.length() > 0)
			{
				String mailContent = PraseMailContent.praseMailContent(htmlBean.html);
				htmlBean.content = mailContent;
				mTts.startSpeaking(htmlBean.content, mSynListener);
			}
			else
			{
				mTts.startSpeaking("邮件详情读取失败，请稍后再试", mSynListener);
			}
		}
		
		
		public void processMailList()
		{
			String html = htmlBean.html;
			browserState = PraseCommand.Cmd_Original;
			List<MailListBean> list = PraseMailList.parseMailList(html);
			if(list.size() == 0)
				mTts.startSpeaking("读取失败，请稍后再试", mSynListener);
			else
			{
				mailList = list;
				String speakStr;
				if(list.size() > 0)
				{
					speakStr = "以下是最近"+list.size()+"封邮件：\n";
					int i = 1;
					for(i = 1;i <= list.size();i++)
					{
						speakStr += "第"+i+"条，来自"+list.get(i-1).mailFrom+"，主题："+list.get(i-1).mailTitle+"。\n";
					}
				}
				else
				{
						speakStr = "您的收件箱暂无最新邮件";
				}
				htmlBean.content = speakStr;
				mTts.startSpeaking(htmlBean.content, mSynListener);
			}
		}
		
//		public void processWeather()
//		{
//			String html = htmlBean.html;
//			int end = 0;
//			int start = 0;
//			if(html.length() < 1)
//			{
//				Toast.makeText(getApplicationContext(), "未获取到天气，请检查位置权限", Toast.LENGTH_SHORT).show();
//				return;
//			}
//			List<WeatherBean> weatherList = new ArrayList<WeatherBean>();
////			weatherList = PraseWeatherHtml.praseWeatherList(html);
//			
//			String title = Jsoup.parse(html).title();
//			end = -1;
//			end = title.indexOf(' ');
//			if(end != -1)
//			{
//				title = title.substring(0, end);
//				title = "即将为您播报" + title+":\n";
//			}
//			else
//				title= "即将为您播报未来七天天气状况:";
//			
//			String str = "";
//			for(WeatherBean bean:weatherList)
//			{
//				str += bean.date+"，"+bean.weather+'，'+bean.temp+"；\n";
//			}
//			
//			htmlBean.content = title + str;
//			mTts.startSpeaking(htmlBean.content,mSynListener);
//		}
		
		public void processNewsList()
		{
			String html = htmlBean.html;
			writeFileSdcard("",html);
			newsList = PraseTencentNews.getNewsList(html);
			String titleStr = "";
			for(int i = 1;i <= newsList.size();i++)
			{
				titleStr += "第"+i+"条、"+newsList.get(i-1).newsTitle+"\n";
			}
			
			htmlBean.content = "即将为您播报今日新闻:\n" + titleStr;
			mTts.startSpeaking(htmlBean.content, mSynListener);
		}
		
		public void processSearchResult()
		{
			String html = htmlBean.html;
			String searchResult = "";
			List<String> resList = BaiduSearch.praseSearchResultList(html);
			if(resList.size() != 0)
			{
				for(int i = 1;i <= resList.size();i++)
					searchResult += "第"+i+"条、"+resList.get(i-1)+"\n";
				htmlBean.content = "以下是搜索结果:\n"+searchResult;
				mTts.startSpeaking(htmlBean.content, mSynListener);
			}else
			{
				mTts.startSpeaking("搜索出错，请重试。", mSynListener);
			}
		}
		
		public void processNewsContent()
		{
			String html = htmlBean.html;
			String content = PraseTencentNews.getNewsContent(html);
			htmlBean.content = "第" + newsNumber + "条新闻\n标题：" + newsList.get(newsNumber-1).newsTitle+"\n"+content;
			mTts.startSpeaking(htmlBean.content, mSynListener);
		}
		
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			switch(requestCode)
			{
			case REQUEST_CODE_BOOKMARK:
				if(resultCode == RESULT_OK)
				{
					String url = data.getStringExtra("url");
					webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 5.1.1; zh-cn; PLK-UL00 Build/HONORPLK-UL00) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 MQQBrowser/5.3 Mobile Safari/537.36");
					browserState = PraseCommand.Cmd_Original;
					webView.loadUrl(url);
				}
				break;
			}
		}
}
