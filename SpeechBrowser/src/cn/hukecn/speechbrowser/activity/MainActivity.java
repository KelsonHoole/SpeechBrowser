package cn.hukecn.speechbrowser.activity;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;

import cn.hukecn.speechbrowser.R;
import cn.hukecn.speechbrowser.Shake;
import cn.hukecn.speechbrowser.Shake.ShakeListener;
import cn.hukecn.speechbrowser.DAO.MyDataBase;
import cn.hukecn.speechbrowser.bean.BookMarkBean;
import cn.hukecn.speechbrowser.bean.HistoryBean;
import cn.hukecn.speechbrowser.bean.HtmlBean;
import cn.hukecn.speechbrowser.bean.MailBean;
import cn.hukecn.speechbrowser.bean.MailListBean;
import cn.hukecn.speechbrowser.bean.NewsBean;
import cn.hukecn.speechbrowser.location.BaseAppLocation;
import cn.hukecn.speechbrowser.util.BaiduSearch;
import cn.hukecn.speechbrowser.util.GestureUtil;
import cn.hukecn.speechbrowser.util.JsonParser;
import cn.hukecn.speechbrowser.util.ParseCommand;
import cn.hukecn.speechbrowser.util.ParseMailContent;
import cn.hukecn.speechbrowser.util.ParseMailList;
import cn.hukecn.speechbrowser.util.ParsePageType;
import cn.hukecn.speechbrowser.util.ParseTencentNews;
import cn.hukecn.speechbrowser.util.ParseWeatherHtml;
import cn.hukecn.speechbrowser.util.ToastUtil;
import cn.hukecn.speechbrowser.util.Trans2PinYin;
import cn.hukecn.speechbrowser.view.CutWebView;
import cn.hukecn.speechbrowser.view.CutWebView.ReceiveTitleListener;
import cn.hukecn.speechbrowser.view.EditUrlPopupWindow;
import cn.hukecn.speechbrowser.view.EditUrlPopupWindow.EditUrlPopupDismissListener;
import cn.hukecn.speechbrowser.view.MenuPopupWindow;
import cn.hukecn.speechbrowser.view.CutWebView.ReceiveHTMLListener;
import cn.hukecn.speechbrowser.view.CutWebView.ShouldOverrideUrlListener;

import com.baidu.location.BDLocation;
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
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity implements ShakeListener
			,ReceiveHTMLListener,ShouldOverrideUrlListener
			,OnClickListener,ReceiveTitleListener{
	public final int REQUEST_CODE_BOOKMARK = 1;
	public final int REQUEST_CODE_HISTORY = 2;
//	BDLocation location;
	MenuPopupWindow popWindow;
	List<Integer> cmdList = new ArrayList<Integer>();
	private SoundPool sp;//声明一个SoundPool
	private int musicStart;//定义一个整型用load（）；来设置suondID
	private int musicEnd;
	private int newsNumber = -1;
	private static Vibrator mVibrator;
	private HtmlBean htmlBean = new HtmlBean();
	boolean isPause = false;
	int btntate = 0;//0——开始，1——暂停，2——停止
	TextView tv_head = null;
	ImageButton btn_menu = null,
			btn_left = null,
			btn_right = null,
			btn_state = null;
	ImageButton btn_microphone = null;
//	int browserState = ParseCommand.Cmd_Original;
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
	RelativeLayout rl_head = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		initView();
		initSpeechUtil();
		
		String x = "huke";
		
		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		musicStart = sp.load(this, R.raw.shake, 1);
		musicEnd = sp.load(this, R.raw.bdspeech_recognition_success,1);
	
        mVibrator = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);  
	}
	private void initView() {
		// TODO Auto-generated method stub
		title = (TextView) findViewById(R.id.title);
		tv_info = (TextView) findViewById(R.id.info);		 
		webView = (CutWebView) findViewById(R.id.webview);
		tv_head = (TextView) findViewById(R.id.tv_head);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		btn_right = (ImageButton) findViewById(R.id.btn_right);
		btn_state = (ImageButton) findViewById(R.id.btn_state);
		btn_menu = (ImageButton) findViewById(R.id.btn_menu);
		btn_microphone = (ImageButton) findViewById(R.id.btn_microphone);
		speechProgressBar = (ProgressBar) findViewById(R.id.speechProgressBar);
		rl_head = (RelativeLayout) findViewById(R.id.rl_head);
		
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);
		btn_microphone.setOnClickListener(this);
		btn_state.setOnClickListener(this);
		btn_menu.setOnClickListener(this);
	
		webView.setOnReceiveHTMLListener(this);
		webView.setOnReceiveTitleListener(this);
		webView.setOnShouldOverrideUrlListener(this);

		tv_head.setOnClickListener(this);
		popWindow = new MenuPopupWindow(MainActivity.this,MainActivity.this,getWindow(),new OnDismissListener(){
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				btn_menu.setImageResource(R.drawable.menu);
			}
		});
	}
	private void initSpeechUtil(){
		SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID +"=568fba83");   

		mIatDialog = new RecognizerDialog(MainActivity.this, mInitListener);
		mIatDialog.setListener(mRecognizerDialogListener);
		
		mTts= SpeechSynthesizer.createSynthesizer(this, null);  
		mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoqi");
		mTts.setParameter(SpeechConstant.SPEED, "50");
		mTts.setParameter(SpeechConstant.VOLUME, "50");
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_AUTO); //设置云端  
	}
	private InitListener mInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
		}
	};
	
	private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			
			List<String> list= JsonParser.parseIatResult(results.getResultString());

			long current = System.currentTimeMillis();
			if(list.get(0).equals("。") || list.get(0).equals(""))
				return ;
			if(list.get(list.size() -1).equals("。"))
				list.remove(list.size()-1);
			
			if(current - lastTime > 800)
			{
				sp.play(musicEnd, 1, 1, 0, 0, 1);
				tv_info.setText("");
				speechProgressBar.setVisibility(View.GONE);
				htmlBean.content = "";
				
				int browserState = ParseCommand.prase(list);
				lastTime = current;
				if(browserState != ParseCommand.Cmd_NewsNum)
					cmdList.add(browserState);
				
				switch (browserState) {
				case ParseCommand.Cmd_Search:
					cmdSearch(list);
					break;
				case ParseCommand.Cmd_News:
					cmdReadNews();
					
					break;
				case ParseCommand.Cmd_Weather:
					BaseAppLocation baseAppLocation = BaseAppLocation.getInstance();
					BDLocation location  = baseAppLocation.getLocation();
					String url = null;
					if(location != null)
					{
						String cityname = location.getCity().replace("市", "");
						cityname = Trans2PinYin.trans2PinYin(cityname);
						url = "http://weather1.sina.cn/?code="+cityname+"&vt=4";
					}else
						url = "http://weather1.sina.cn/?vt=4";
						
					webView.loadUrl(url);
					break;
				
				case ParseCommand.Cmd_NewsNum:
					int pageType = ParsePageType.getPageType(htmlBean.url);
					if( pageType== ParsePageType.MailListTag || pageType == ParsePageType.MailContentTag)
					{
						//进入读邮件详情
						if(mailList != null && mailList.size()>0)
						{
							browserState = ParseCommand.Cmd_Mail_MailContent;
							readMailContent(ParseCommand.praseNewsIndex(list));
						}else
							mTts.startSpeaking("获取邮件详情失败，请稍后再试",mSynListener);
						break;
					}
					
					if(pageType == ParsePageType.NewsListTag || pageType == ParsePageType.NewsContentTag)
					{
						//进入读新闻详情
						if(newsList != null && newsList.size() > 0)
						{	
							readNewsContent(ParseCommand.praseNewsIndex(list));
						}else
							mTts.startSpeaking("获取新闻详情失败，请稍后再试",mSynListener);
						break;
					}
					
					if(cmdList.size() >0 && cmdList.get(cmdList.size() -1) == ParseCommand.Cmd_Query_Bookmark)
					{
						//进入书签处理
//						if(mailList != null && mailList.size()>0)
//						{
//							browserState = ParseCommand.Cmd_Original;
							openUrlFromBookmark(ParseCommand.praseNewsIndex(list));
//						}else
//							mTts.startSpeaking("打开网页失败，请稍后再试",mSynListener);
						break;
					}
					
					mTts.startSpeaking("指令错误，请输入正确指令",mSynListener);
					break;
				case ParseCommand.Cmd_Location:
					webView.loadUrl("http://map.qq.com/m/index/map");
					break;
				case ParseCommand.Cmd_Exit:
					mTts.startSpeaking("正在关闭机器人。。。", mSynListener);
					handler.sendEmptyMessageDelayed(0, 3000);
					break;
				case ParseCommand.Cmd_Mail:
					cmdMail();
					break;
				case ParseCommand.Cmd_Query_Bookmark:
					cmdQueryBookmark();
					break;
				case ParseCommand.Cmd_Add_Bookmark:
					cmdAddBookmark();
					break;
				case ParseCommand.Cmd_Err:
				case ParseCommand.Cmd_Other:
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
	private void cmdQueryBookmark() {
		// TODO Auto-generated method stub
		MyDataBase db = MyDataBase.getInstance();
		List<BookMarkBean> list = db.queryBookMark();
		if(list.size() == 0)
		{
			mTts.startSpeaking("您尚未添加书签...", mSynListener);
		}
		else
		{
			int count = list.size();
			String str = "您的书签共有" + count+"条：\n";
			for(int i = 1;i <= count;i++)
			{
				str+="第"+i+"条:"+list.get(i - 1).title+"\n";
			}
			tv_info.setText(str);
			htmlBean.content = str;
			mTts.startSpeaking(str, mSynListener);
		}
	}
	
	protected void openUrlFromBookmark(int praseNewsIndex) {
		// TODO Auto-generated method stub
		MyDataBase db = MyDataBase.getInstance();
		List<BookMarkBean> list = db.queryBookMark();
		if(praseNewsIndex > list.size())
			mTts.startSpeaking("书签不存在", mSynListener);
		else
		{
			String url = list.get(praseNewsIndex - 1).url;
			String title = list.get(praseNewsIndex - 1).title;
			webView.loadUrl(url);
			mTts.startSpeaking("正在为您打开"+title+"，请稍后", mSynListener);
		}
	}

	protected void cmdAddBookmark() {
		// TODO Auto-generated method stub
		MyDataBase db = MyDataBase.getInstance();
		BookMarkBean bean = new BookMarkBean();
		bean.url = htmlBean.url;
		bean.title = Jsoup.parse(htmlBean.html).title();
		if(db.insertBookMark(bean) != -1)
		{
			mTts.startSpeaking("已成功将"+bean.title+"添加到书签", mSynListener);
		}else
		{
			mTts.startSpeaking("书签添加失败", mSynListener);
		}
	}

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
//		mBDTts.stop();
		if(mTts.isSpeaking())
			mTts.stopSpeaking();
		if(System.currentTimeMillis() - lastShakeTime > 1200)
		{	
			mVibrator.vibrate(500);
			sp.play(musicStart, 1, 1, 0, 0, 1);
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
		webView.loadUrl(ParseTencentNews.HOMEURL);
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
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				exitApp();
				break;
			case 1:
//				List<String> resList = (List<String>) msg.obj;
				break;
			}
		}
	};
	@Override
	protected void onResume() {
		Shake.registerListener(this, this);
		super.onResume();
	}
	@Override
	protected void onPause() {
		Shake.removeListener();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		webView.destroy();
		exitApp();
	}
	
	private void exitApp()
	{
		if(mTts.isSpeaking())
			mTts.stopSpeaking();
		mTts.destroy();
		webView.destroy();
		finish();
		BaseAppLocation baseAppLocation = BaseAppLocation.getInstance();
		baseAppLocation.removeLocationListener();
		System.exit(0);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	 
	 	@Override
	    public void onBackPressed() {
	 		if(mTts.isSpeaking())
				mTts.stopSpeaking();
	        if(webView.canGoBack())
	            webView.goBack();
	        else
	        {
	        	AlertDialog.Builder builder = new Builder(this);
	        	builder.setMessage("确定退出吗？");  
	        	builder.setTitle("您即将退出语音浏览器");
	        	mTts.startSpeaking("您即将退出语音浏览器，请按确定键退出。", mSynListener);
	        	builder.setPositiveButton("我确定", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						exitApp();
					}
	        	});
	        	
	        	builder.setNegativeButton("按错了", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
	        	});
	        	builder.create().show();
	        }
	    }
	 	
//	    public static void writeFileSdcard(String fileName,String message)
//	    { 
//	    	try {  
//	    	    File file = new File(Environment.getExternalStorageDirectory(),  
//	    	            "c.txt");  
//	    	        FileOutputStream fos = new FileOutputStream(file, false);  
//	    	 
//	    	           fos.write(message.getBytes("utf-8"));  
//	    	           fos.close();  
//	    	           //Toast.makeText(getApplicationContext(), "写入成功", Toast.LENGTH_SHORT).show();
//	    	} catch (Exception e) {
//	    	    e.printStackTrace();  
//	    	}  
//	    }
	    
		@Override
		public void onReceiveHTML(String url,String html) {
			// TODO Auto-geerated method stub
			int tag = ParsePageType.getPageType(url);
			htmlBean.url = url;
			htmlBean.html = html;
			String title = Jsoup.parse(html).title();
//			tv_head.setText(title);
			btn_state.setImageResource(R.drawable.start);
			if(url != null && url.length() >0 && title != null && title.length() > 0)
			{
				MyDataBase myDataBase = MyDataBase.getInstance();
				HistoryBean bean = new HistoryBean();
				bean.time = System.currentTimeMillis()+"";
				bean.url = url;
				bean.title = title;
				myDataBase.insertHistory(bean);
			}
			btntate = 0;
			switch (tag) {
			case ParsePageType.MailLoginTag:
				processLoginQQMail();
				break;
			case ParsePageType.MailHomePageTag:
				processQQMailHome();
				break;
			case ParsePageType.MailListTag:
				processMailList();
				break;
			case ParsePageType.MailContentTag:
				processMailContent();
				break;
			case ParsePageType.SinaWeatherTag:
				processSinaWeather();
				break;
			case ParsePageType.BaiduResultUrlTag:
				processSearchResult();
				break;
			case ParsePageType.NewsListTag:
				processNewsList();
				break;
			case ParsePageType.NewsContentTag:
				processNewsContent();
				break;
			case ParsePageType.TencentMapUrlTag:
				processGetLocation();
				break;
			default:
				htmlBean.content = Jsoup.parse(html).title();
				break;
			}
			
			tv_info.setText(htmlBean.content);
		}

		@Override
		public void onShouldOverrideUrl(String url) {
			// TODO Auto-generated method stub
			mTts.stopSpeaking();
			
			isPause = false;
			
			htmlBean.content = "";
			tv_info.setText("正在为您努力加载...");
			speechProgressBar.setVisibility(View.GONE);
		}

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
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
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						exitApp();
					}
				}, 100);
				break;
			case R.id.btn_menu:
				popWindow.showPopupWindow(findViewById(R.id.toolsBar));
				btn_menu.setImageResource(R.drawable.down);
				break;
			case R.id.btn_left:
				if(mTts.isSpeaking())
					mTts.stopSpeaking();
		        if(webView.canGoBack())
		            webView.goBack();
		        else
		        {
		        	ToastUtil.toast("已经是第一页了");
		        }
				break;
			case R.id.btn_right:
				if(mTts.isSpeaking())
					mTts.stopSpeaking();
				if(webView.canGoForward())
					webView.goForward();
				else
				{
					ToastUtil.toast("已经是最后一页了");
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
			case R.id.btn_m_homepage:
				htmlBean.content = "";
				webView.loadUrl("http://m.baidu.com");
				break;
			case R.id.btn_m_history:
				intent = new Intent(MainActivity.this,HistoryActivity.class);
				startActivityForResult(intent, REQUEST_CODE_HISTORY);
				break;
			case R.id.btn_m_other:
				intent = new Intent(MainActivity.this,LogActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_m_refresh:
				htmlBean.content = "";
				webView.reload();
				break;
			case R.id.tv_head:
//				et_head.setText(htmlBean.url);
				EditUrlPopupWindow urlPopupWindow = new EditUrlPopupWindow(this, new EditUrlPopupDismissListener() {
					@Override
					public void onDismiss(String content) {
						// TODO Auto-generated method stub
						webView.loadUrl(content);
					}
				});
				urlPopupWindow.show(rl_head, htmlBean.url);
				break;
			default:
				break;
			}
			
			if(v.getId() != R.id.btn_menu)
			{
				popWindow.dismiss();
			}
		}
		
		public void processGetLocation()
		{
			BaseAppLocation baseAppLocation = BaseAppLocation.getInstance();
			BDLocation location  = baseAppLocation.getLocation();
			if(location != null){
				String content = "您当前位于："+location.getAddrStr();
				mTts.startSpeaking(content, mSynListener);
				htmlBean.content = content;
			}else
			{
				String content = "暂未获取到您的位置，请稍后再试。";
				mTts.startSpeaking(content, mSynListener);
				htmlBean.content = content;
			}
		}
		
		public void processLoginQQMail()
		{
			MyDataBase db = MyDataBase.getInstance();
			List<MailBean> mailList = db.queryMail("QQ");
			if(mailList != null && mailList.size() > 0)
			{
				MailBean bean = mailList.get(mailList.size() - 1);
				ToastUtil.toast("正在为您登陆"+bean.type+"邮箱...");
				webView.loadUrl("javascript:"
						+ "document.getElementById(\"u\").value= \"" + bean.username + "\";"
						+ "document.getElementById(\"p\").value= \"" + bean.password + "\";"
						+ "document.getElementById(\"go\").click();");
			}else
			{
				ToastUtil.toast("请配置您的QQ邮箱账号，以便使用邮件服务...");
				mTts.startSpeaking("请配置您的QQ邮箱账号，以便使用邮件服务...", mSynListener);
			}
		}
		
		public void processQQMailHome()
		{
			mailCookie = webView.getCookie();
			int cookieStart = mailCookie.indexOf("msid=") + 5;
			int cookieEnd = mailCookie.indexOf(";", cookieStart);
			if(cookieStart != -1 && cookieEnd != -1 && cookieEnd > cookieStart)
			{
				msid = mailCookie.substring(cookieStart,cookieEnd);
				
				String html = htmlBean.html;
				int start = html.indexOf("/cgi-bin/mail_list?");
				//判断是否是邮箱主界面
				if(start != -1)
				{
//					browserState = ParseCommand.Cmd_Mail_InBox;
					int end = html.indexOf(">", start);
					if(end != -1)
					{
						webView.loadUrl("https://w.mail.qq.com/cgi-bin/mail_list?fromsidebar=1&sid="+msid+"&folderid=1&page=0&pagesize=10&sorttype=time&t=mail_list&loc=today,,,151&version=html");	
						return;
					}else
						mTts.startSpeaking("邮箱登陆失败，请稍后再试", mSynListener);
				}
			}else
			{
				mTts.startSpeaking("邮箱登陆失败，请稍后再试", mSynListener);
			}
		}
		public void processMailContent()
		{
			if(htmlBean.html.length() > 0)
			{
				String mailContent = ParseMailContent.praseMailContent(htmlBean.html);
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
//			browserState = ParseCommand.Cmd_Original;
			List<MailListBean> list = ParseMailList.parseMailList(html);
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
			newsList = ParseTencentNews.getNewsList(html);
			String titleStr = "";
			for(int i = 1;i <= 100 && i <= newsList.size();i++)
			{
				titleStr += "第"+i+"条、"+newsList.get(i-1).newsTitle+"\n";
			}
			
			htmlBean.content = titleStr;
			mTts.startSpeaking(htmlBean.content, mSynListener);
		}
		
		public void processNewsContent()
		{
			String html = htmlBean.html;
			String content = ParseTencentNews.getNewsContent(html);
			String title = Jsoup.parse(htmlBean.html).title();
			title = title.replace("-手机腾讯网", "");
			htmlBean.content = "标题：" + title+"\n"+content;
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
		
		public void processSinaWeather()
		{
			htmlBean.content = ParseWeatherHtml.praseWeatherList(htmlBean.html);
			mTts.startSpeaking(htmlBean.content,mSynListener);
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
					webView.loadUrl(url);
				}
				break;
			case REQUEST_CODE_HISTORY:
				if(resultCode == RESULT_OK)
				{
					String url = data.getStringExtra("url");
					webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 5.1.1; zh-cn; PLK-UL00 Build/HONORPLK-UL00) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 MQQBrowser/5.3 Mobile Safari/537.36");
					webView.loadUrl(url);
				}
				break;
			default:
				break;
			}
		}
		@Override
		public void onReceiveTitle(String title) {
			// TODO Auto-generated method stub
			tv_head.setText(title);
		}
}
