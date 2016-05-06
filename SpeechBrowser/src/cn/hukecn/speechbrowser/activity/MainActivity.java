package cn.hukecn.speechbrowser.activity;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.News;
import cn.edu.hfut.dmic.webcollector.util.JsoupUtils;
import cn.hukecn.speechbrowser.R;
import cn.hukecn.speechbrowser.Shake;
import cn.hukecn.speechbrowser.Shake.ShakeListener;
import cn.hukecn.speechbrowser.DAO.MyDataBase;
import cn.hukecn.speechbrowser.DAO.MySharedPreferences;
import cn.hukecn.speechbrowser.bean.BookMarkBean;
import cn.hukecn.speechbrowser.bean.HistoryBean;
import cn.hukecn.speechbrowser.bean.HtmlBean;
import cn.hukecn.speechbrowser.bean.MailBean;
import cn.hukecn.speechbrowser.bean.MailListBean;
import cn.hukecn.speechbrowser.bean.NewsBean;
import cn.hukecn.speechbrowser.location.BaseAppLocation;
import cn.hukecn.speechbrowser.util.BaiduSearch;
import cn.hukecn.speechbrowser.util.JsonParser;
import cn.hukecn.speechbrowser.util.ParseAandP;
import cn.hukecn.speechbrowser.util.ParseCommand;
import cn.hukecn.speechbrowser.util.ParseFengNews;
import cn.hukecn.speechbrowser.util.ParseMailContent;
import cn.hukecn.speechbrowser.util.ParseMailList;
import cn.hukecn.speechbrowser.util.ParsePageType;
import cn.hukecn.speechbrowser.util.ParseTencentNews;
import cn.hukecn.speechbrowser.util.ParseWeatherHtml;
import cn.hukecn.speechbrowser.util.ToastUtil;
import cn.hukecn.speechbrowser.util.Trans2PinYin;
import cn.hukecn.speechbrowser.util.ViewPageAdapter;
import cn.hukecn.speechbrowser.view.CutWebView;
import cn.hukecn.speechbrowser.view.CutWebView.CutWebCallback;
import cn.hukecn.speechbrowser.view.CutWebView.ReceiveHtmlListener;
import cn.hukecn.speechbrowser.view.CutWebView.ReceiveMessageListener;
import cn.hukecn.speechbrowser.view.EditUrlPopupWindow;
import cn.hukecn.speechbrowser.view.EditUrlPopupWindow.EditUrlPopupDismissListener;
import cn.hukecn.speechbrowser.view.MenuPopupWindow;

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
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements ShakeListener
			,OnClickListener,CutWebCallback,ReceiveHtmlListener,ReceiveMessageListener{
	public final int REQUEST_CODE_BOOKMARK = 1;
	public final int REQUEST_CODE_HISTORY = 2;
	public final int REQUEST_CODE_SETTING = 3;
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
	CutWebView webViewMain = null;
	RelativeLayout rl_head = null;
	ViewPager mViewPager = null;
	ViewPageAdapter pageAdapter = null;
	private boolean blind = false;
	private boolean autoread = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
	         getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	    }
		
		final SharedPreferences sharedpref = MySharedPreferences.getInstance(getApplicationContext());
	    autoread = sharedpref.getBoolean("autoread", false);
	    blind = sharedpref.getBoolean("blind", false);
	    
		initSpeechUtil();
		initView();
		
		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		musicStart = sp.load(this, R.raw.shake, 1);
		musicEnd = sp.load(this, R.raw.bdspeech_recognition_success,1);
	
        mVibrator = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);  
        Intent intent = getIntent();
        if(intent != null)
        {
        	String action = intent.getAction();
        	if(action != null)
	        	if (action.equals("android.intent.action.VIEW")) 
	        	{
					Uri uri = intent.getData();
					String url = uri.toString();
					if(url != null && url.length() > 0)
					{
						webViewMain.loadUrl(url);
						mViewPager.setCurrentItem(1);
						return;
					}
				}
       	}
//		webViewMain.loadUrl("http://m.baidu.com");
	}
	private void initView() {
		// TODO Auto-generated method stub
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		View view0 = View.inflate(this, R.layout.page_layout_webview, null);
		View view1 = View.inflate(this, R.layout.page_layout_webview, null);
		View view2 = View.inflate(this, R.layout.page_layout_textview, null);

		List<View> viewList = new ArrayList<View>();
		viewList.add(view0);
		viewList.add(view1);
		viewList.add(view2);
		String[] titles = {"首页","网页","内容"};
		pageAdapter = new ViewPageAdapter(viewList, titles);
		mViewPager.setAdapter(pageAdapter);
		mViewPager.setCurrentItem(0);
		mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				if(arg0 == 2)
				{
					webViewMain.loadUrl("javascript:window.HTML.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
				}
				if(arg0 == 1)
				{
					if(mTts.isSpeaking())
					{
						isPause = true;
						btn_state.setImageResource(R.drawable.start);
						btntate = 0;
						mTts.stopSpeaking();
						speechProgressBar.setProgress(0);
						speechProgressBar.setVisibility(View.GONE);
					}
					
					String url = webViewMain.getUrl();
					if(url == null)
						webViewMain.loadUrl("https://m.baidu.com");
					
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
		
		title = (TextView) findViewById(R.id.title);
		tv_info = (TextView) view2.findViewById(R.id.info);
		webViewMain = (CutWebView) view1.findViewById(R.id.webview);
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
		tv_head.setOnClickListener(this);
		
		popWindow = new MenuPopupWindow(MainActivity.this,MainActivity.this,getWindow(),new OnDismissListener(){
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				btn_menu.setImageResource(R.drawable.menu);
			}
		});
		
		CutWebView webViewHome = (CutWebView) view0.findViewById(R.id.webview);
		webViewMain.setCutWebViewCallback(this);
		webViewHome.setCutWebViewCallback(this);
		webViewMain.setReceiveHtmlListener(this);
		webViewHome.setReceiveMessageListener(this);
		
        webViewHome.loadUrl("file:///android_asset/welcomepage/index.html");

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
				
//				int cmdType = ParseCommand.prase(list);
				lastTime = current;
				
				handlerCMD(list);
//				if(cmdType != ParseCommand.Cmd_NewsNum)
//					cmdList.add(cmdType);
//				
//				switch (cmdType) {
//				case ParseCommand.Cmd_Search:
//					cmdSearch(list);
//					break;
//				case ParseCommand.Cmd_News:
//					cmdReadNews();
//					
//					break;
//				case ParseCommand.Cmd_Weather:
//					BaseAppLocation baseAppLocation = BaseAppLocation.getInstance();
//					BDLocation location  = baseAppLocation.getLocation();
//					String url = null;
//					if(location != null)
//					{
//						String cityname = location.getCity().replace("市", "");
//						cityname = Trans2PinYin.trans2PinYin(cityname);
//						url = "http://weather1.sina.cn/?code="+cityname+"&vt=4";
//					}else
//						url = "http://weather1.sina.cn/?vt=4";
//						
//					webView.loadUrl(url);
//					break;
//				
//				case ParseCommand.Cmd_NewsNum:
//					int pageType = ParsePageType.getPageType(htmlBean.url);
//					if( pageType== ParsePageType.MailListTag || pageType == ParsePageType.MailContentTag)
//					{
//						//进入读邮件详情
//						if(mailList != null && mailList.size()>0)
//						{
//							cmdType = ParseCommand.Cmd_Mail_MailContent;
//							readMailContent(ParseCommand.praseNewsIndex(list));
//						}else
//							ToastUtil.toast("获取邮件详情失败，请稍后再试");
////							mTts.startSpeaking("获取邮件详情失败，请稍后再试",mSynListener);
//						break;
//					}
//					
//					if(pageType == ParsePageType.NewsListTag || pageType == ParsePageType.NewsContentTag)
//					{
//						//进入读新闻详情
//						if(newsList != null && newsList.size() > 0)
//						{	
//							readNewsContent(ParseCommand.praseNewsIndex(list));
//						}else
//						{
//							ToastUtil.toast("获取新闻详情失败，请稍后再试");
////							mTts.startSpeaking("获取新闻详情失败，请稍后再试",mSynListener);
//						}
//						
//						break;
//					}
//					
//					if(cmdList.size() >0 && cmdList.get(cmdList.size() -1) == ParseCommand.Cmd_Query_Bookmark)
//					{
//						//进入书签处理
////						if(mailList != null && mailList.size()>0)
////						{
////							browserState = ParseCommand.Cmd_Original;
//							openUrlFromBookmark(ParseCommand.praseNewsIndex(list));
////						}else
////							mTts.startSpeaking("打开网页失败，请稍后再试",mSynListener);
//						break;
//					}
//					
//					mTts.startSpeaking("指令错误，请输入正确指令",mSynListener);
//					break;
//				case ParseCommand.Cmd_Location:
//					webView.loadUrl("http://map.qq.com/m/index/map");
//					break;
//				case ParseCommand.Cmd_Exit:
//					mTts.startSpeaking("正在关闭机器人。。。", mSynListener);
//					handler.sendEmptyMessageDelayed(0, 3000);
//					break;
//				case ParseCommand.Cmd_Mail:
//					cmdMail();
//					break;
//				case ParseCommand.Cmd_Query_Bookmark:
//					cmdQueryBookmark();
//					break;
//				case ParseCommand.Cmd_Add_Bookmark:
//					cmdAddBookmark();
//					break;
//				case ParseCommand.Cmd_Err:
//				case ParseCommand.Cmd_Other:
//				default:
//					ToastUtil.toast("指令错误，请输入正确指令");
////					mTts.startSpeaking("指令错误，请输入正确指令",mSynListener);
//					break;
//				}
			}
		}
		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			//showTip(error.getPlainDescription(true));
		}
	};
	
	
	private void handlerCMD(List<String> list) {
		int cmdType = ParseCommand.prase(list);
		
		if(cmdType != ParseCommand.Cmd_NewsNum)
			cmdList.add(cmdType);
		String str = "";
		for(String temp:list)
		{
			str += temp;
		}
		str = str.replace("搜索", "");
		
		switch (cmdType) {
		case ParseCommand.Cmd_Search:
			cmdSearch(str);
			break;
		case ParseCommand.Cmd_News:
			cmdReadNews();
			
			break;
		case ParseCommand.Cmd_Weather:
			cmdWeather();
			break;
		
		case ParseCommand.Cmd_NewsNum:
			int pageType = ParsePageType.getPageType(htmlBean.url);
			if( pageType== ParsePageType.MailListTag || pageType == ParsePageType.MailContentTag)
			{
				//进入读邮件详情
				if(mailList != null && mailList.size()>0)
				{
					cmdType = ParseCommand.Cmd_Mail_MailContent;
					readMailContent(ParseCommand.praseNewsIndex(list));
				}else
					ToastUtil.toast("获取邮件详情失败，请稍后再试");
//					mTts.startSpeaking("获取邮件详情失败，请稍后再试",mSynListener);
				break;
			}
			
			if(pageType == ParsePageType.NewsListTag || pageType == ParsePageType.NewsContentTag || pageType == ParsePageType.FengNewsContentTag || pageType == ParsePageType.FengNewsTag)
			{
				//进入读新闻详情
				if(newsList != null && newsList.size() > 0)
				{	
					readNewsContent(ParseCommand.praseNewsIndex(list));
				}else
				{
					ToastUtil.toast("获取新闻详情失败，请稍后再试");
//					mTts.startSpeaking("获取新闻详情失败，请稍后再试",mSynListener);
				}
				
				break;
			}
			
			if(cmdList.size() >0 && cmdList.get(cmdList.size() -1) == ParseCommand.Cmd_Query_Bookmark)
			{
				//进入书签处理
//				if(mailList != null && mailList.size()>0)
//				{
//					browserState = ParseCommand.Cmd_Original;
					openUrlFromBookmark(ParseCommand.praseNewsIndex(list));
//				}else
//					mTts.startSpeaking("打开网页失败，请稍后再试",mSynListener);
				break;
			}
			
			mTts.startSpeaking("指令错误，请输入正确指令",mSynListener);
			break;
		case ParseCommand.Cmd_Location:
			cmdLocation();
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
//			ToastUtil.toast("指令错误，请输入正确指令");
			String url1 = "http://m.baidu.com/s?word="+str;
			webViewMain.loadUrl(url1);
//			mViewPager.setCurrentItem(1);

//			mTts.startSpeaking("指令错误，请输入正确指令",mSynListener);
			break;
		}
	}
	private void cmdLocation() {
		// TODO Auto-generated method stub
		webViewMain.loadUrl("http://map.qq.com/m/index/map");
//		mViewPager.setCurrentItem(1);

	}
	private void cmdWeather() {
		BaseAppLocation baseAppLocation = BaseAppLocation.getInstance();
		BDLocation location  = baseAppLocation.getLocation();
		String url = null;
		if(location != null)
		{
			try{
				
			String cityname = location.getCity().replace("市", "");
			cityname = Trans2PinYin.trans2PinYin(cityname);
			url = "http://weather1.sina.cn/?code="+cityname+"&vt=4";
			}catch(NullPointerException e){
				url = "http://weather1.sina.cn/?vt=4";
			}
		}else
			url = "http://weather1.sina.cn/?vt=4";
			
		webViewMain.loadUrl(url);
//		mViewPager.setCurrentItem(1);

	}
	
	private void cmdQueryBookmark() {
		// TODO Auto-generated method stub
		MyDataBase db = MyDataBase.getInstance();
		List<BookMarkBean> list = db.queryBookMark();
		if(list.size() == 0)
		{
			ToastUtil.toast("您尚未添加书签...");

//			mTts.startSpeaking("您尚未添加书签...", mSynListener);
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
//			mTts.startSpeaking(str, mSynListener);
		}
	}
	
	protected void openUrlFromBookmark(int praseNewsIndex) {
		// TODO Auto-generated method stub
		MyDataBase db = MyDataBase.getInstance();
		List<BookMarkBean> list = db.queryBookMark();
		if(praseNewsIndex > list.size())
		{
			ToastUtil.toast("书签不存在");
//			mTts.startSpeaking("书签不存在", mSynListener);
		}
		else
		{
			String url = list.get(praseNewsIndex - 1).url;
			String title = list.get(praseNewsIndex - 1).title;
			webViewMain.loadUrl(url);
//			mTts.startSpeaking("正在为您打开"+title+"，请稍后", mSynListener);
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
//			mTts.startSpeaking("已成功将"+bean.title+"添加到书签", mSynListener);
			ToastUtil.toast("已成功将"+bean.title+"添加到书签");

		}else
		{
			ToastUtil.toast("书签添加失败");
//			mTts.startSpeaking("书签添加失败", mSynListener);
		}
	}

	private void cmdMail()
	{
		webViewMain.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
		webViewMain.loadUrl("https://ui.ptlogin2.qq.com/cgi-bin/login?style=9&appid=522005705&daid=4&s_url=https%3A%2F%2Fw.mail.qq.com%2Fcgi-bin%2Flogin%3Fvt%3Dpassport%26vm%3Dwsk%26delegate_url%3D%26f%3Dxhtml%26target%3D&hln_css=http%3A%2F%2Fmail.qq.com%2Fzh_CN%2Fhtmledition%2Fimages%2Flogo%2Fqqmail%2Fqqmail_logo_default_200h.png&low_login=1&hln_autologin=%E8%AE%B0%E4%BD%8F%E7%99%BB%E5%BD%95%E7%8A%B6%E6%80%81&pt_no_onekey=1");
//		mViewPager.setCurrentItem(1);

	}
	
	protected void readMailContent(int praseNewsIndex) {
		// TODO Auto-generated method stub
		if(praseNewsIndex > mailList.size())
		{
//			mTts.startSpeaking("该条数不存在，请重新输入指令", mSynListener);
			ToastUtil.toast("该条数不存在，请重新输入指令");
		}
		else
			webViewMain.loadUrl(mailList.get(praseNewsIndex - 1).mailUrl);
	}

	private void readNewsContent(final int praseNewsIndex) {
		// TODO Auto-generated method stub
		newsNumber = praseNewsIndex;
		if(praseNewsIndex > newsList.size())
		{
//			mTts.startSpeaking("该条数不存在，请重新输入指令", mSynListener);
			ToastUtil.toast("该条数不存在，请重新输入指令");
		}
		else
			webViewMain.loadUrl(newsList.get(praseNewsIndex - 1).newsUrl);
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
		webViewMain.loadUrl(ParseTencentNews.HOMEURL);
//		mViewPager.setCurrentItem(1);
	}
	
	private void cmdSearch(String str) {
//		String str = "";
//		for(String temp:list)
//		{
//			str += temp;
//		}
//		str = str.replace("搜索", "");
		String url = "http://m.baidu.com/s?word="+str;
		webViewMain.loadUrl(url);
//		mViewPager.setCurrentItem(1);

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
		webViewMain.destroy();
		exitApp();
	}
	
	private void exitApp()
	{
		if(mTts.isSpeaking())
			mTts.stopSpeaking();
		mTts.destroy();
		webViewMain.destroy();
		finish();
		BaseAppLocation baseAppLocation = BaseAppLocation.getInstance();
		baseAppLocation.removeLocationListener();
		System.exit(0);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	 
	 	@Override
	    public void onBackPressed() {
	 		if(mViewPager.getCurrentItem() == 2 || mViewPager.getCurrentItem() == 0)
	 		{
	 			mViewPager.setCurrentItem(1);
	 			return;
	 		}
	 		
	 		if(mTts.isSpeaking())
				mTts.stopSpeaking();
	        if(webViewMain.canGoBack())
	            webViewMain.goBack();
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
//			tv_info.setText("");
			int tag = ParsePageType.getPageType(url);
			htmlBean.url = url;
			htmlBean.html = html;
			String title = Jsoup.parse(html).title();
//			tv_head.setText(title);
			btn_state.setImageResource(R.drawable.start);
			if(url != null && url.length() >0 && title != null && title.length() > 0)
			{//加入历史记录
				MyDataBase myDataBase = MyDataBase.getInstance();
				HistoryBean bean = new HistoryBean();
				bean.time = System.currentTimeMillis()+"";
				bean.url = url;
				bean.title = title;
				myDataBase.insertHistory(bean);
			}
			btntate = 0;
			switch (tag) {
//			case ParsePageType.MailLoginTag:
//				processLoginQQMail();
//				break;
//			case ParsePageType.MailHomePageTag:
//				processQQMailHome();
//				break;
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
			case ParsePageType.FengNewsTag:
				processFengNewsList();
				break;
			case ParsePageType.FengNewsContentTag:
				processFengNewsContent();
				break;
			default:
//				if(url.contains("news") || url.contains("News"))
//				{
//					try {
//						News news = ContentExtractor.getNewsByHtml(html);
//						
//						htmlBean.content = news.getTitle()+'\n';
//						htmlBean.content += news.getContent();
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						ToastUtil.toast("新闻解析失败");
//					}
//					
//				}else {
//					List<String> aList = new ArrayList<String>();
//					List<String> pList = new ArrayList<String>();
//					ParseAandP.parse(htmlBean.html, aList, pList);
//					
//					htmlBean.content = "=================A================\n";
//					for(String str:aList)
//					{
//						htmlBean.content += str+' ';
//					}
//					
//					htmlBean.content += "\n=================P================\n";
//					for(String str:pList)
//					{
//						htmlBean.content += str+"\n";
//					}
//				}
				if(url != null && url.contains("m.baidu.com") && url.length() < 21)
				{
					htmlBean.content = "当前网页暂不支持解析";
					break;
				}

				try {
					String content = ContentExtractor.getContentByHtml(html);
					htmlBean.content = content;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					ToastUtil.toast("解析失败");
				}
				break;
			}
			
			tv_info.setText(htmlBean.content);
			if(autoread || blind)
				mTts.startSpeaking(htmlBean.content, mSynListener);
//			if(htmlBean.content.length()>0)
//				mTts.startSpeaking(htmlBean.content, mSynListener);
		}


		private void processFengNewsContent() {
	// TODO Auto-generated method stub
			News news = ParseFengNews.ParseFengNewsContent(htmlBean.html);
			if(news == null)
			{
				htmlBean.content = "新闻详情读取失败...";
			}else {
				htmlBean.content = "标题："+news.getTitle()+"\n";
				htmlBean.content += news.getContent();
			}
		}
		@Override
		public void onShouldOverrideUrl(String url) {
			// TODO Auto-generated method stub
			if(mTts.isSpeaking())
			{
				mTts.stopSpeaking();
				isPause = false;
				btntate = 0;
			}
			htmlBean.content = "";
			tv_info.setText("正在为您努力加载...");
			htmlBean.url = url;
			speechProgressBar.setVisibility(View.GONE);
//			mViewPager.setCurrentItem(1);

		}

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			switch (v.getId()) 
			{
			case R.id.btn_m_bookmark:
				processBookmark();
				break;
			case R.id.btn_m_email:
				Intent intent = new Intent();
				intent = new Intent(MainActivity.this,MailManagerActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_m_setting:
				processSetting();
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
		        if(webViewMain.canGoBack())
		            webViewMain.goBack();
		        else
		        {
		        	ToastUtil.toast("已经是第一页了");
		        }
				break;
			case R.id.btn_right:
				if(mTts.isSpeaking())
					mTts.stopSpeaking();
				if(webViewMain.canGoForward())
					webViewMain.goForward();
				else
				{
					ToastUtil.toast("已经是最后一页了");
				}
				break;
			case R.id.btn_state:
				switch (btntate) {
				case 0://开始
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
							{
								mViewPager.setCurrentItem(2);
								mTts.startSpeaking(htmlBean.content, mSynListener);
							}
							else
								mTts.startSpeaking("暂无可播放内容",mSynListener);
					}
					break;
				case 1://暂停
					mTts.pauseSpeaking();
					isPause = true;
					btn_state.setImageResource(R.drawable.start);
					btntate = 0;
					break;
				case 2://停止
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
				mViewPager.setCurrentItem(0);
//				webView.loadUrl("http://m.baidu.com");
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
				webViewMain.reload();
				break;
			case R.id.tv_head:
//				et_head.setText(htmlBean.url);
				EditUrlPopupWindow urlPopupWindow = new EditUrlPopupWindow(this, new EditUrlPopupDismissListener() {
					@Override
					public void onDismiss(int type,String content) {
						// TODO Auto-generated method stub
//						mViewPager.setCurrentItem(1);
						switch (type) {
						case EditUrlPopupWindow.TYPE_URL:
							webViewMain.loadUrl(content);
//							mViewPager.setCurrentItem(1);
							break;
						case EditUrlPopupWindow.TYPE_CNT:
							List<String> list = new ArrayList<String>();
							list.add(content);
							handlerCMD(list);
						default:
							break;
						}
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
		
		
		private void processFengNewsList() {
			// TODO Auto-generated method stub
			List<News> newsList = ParseFengNews.parseFengNewsList(htmlBean.html);
			if(newsList != null && newsList.size() > 0)
			{
				htmlBean.content = "";
				List<NewsBean> temp = new ArrayList<NewsBean>();
				for(int i = 1;i <= newsList.size();i++)
				{
					News news = newsList.get(i-1);
					htmlBean.content += "第"+i+"条："+news.getTitle()+"\n";
//					htmlBean.content += news.getUrl()+"\n\n";
					NewsBean bean = new NewsBean();
					bean.newsTitle = news.getTitle();
					bean.newsUrl = news.getUrl();
					temp.add(bean);
				}
				this.newsList = temp;
			}else
			{
//				htmlBean.content = "新闻列表读取失败";

				htmlBean.content = "";
			}
		}
		
		public void processGetLocation()
		{
			BaseAppLocation baseAppLocation = BaseAppLocation.getInstance();
			BDLocation location  = baseAppLocation.getLocation();
			if(location != null && location.getAddrStr() != null && location.getAddrStr().length() > 0){
				String content = "您当前位于："+location.getAddrStr();
//				mTts.startSpeaking(content, mSynListener);
				htmlBean.content = content;
			}else
			{
				String content = "暂未获取到您的位置，请检查是否已授予位置权限.";
//				mTts.startSpeaking(content, mSynListener);
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
				webViewMain.loadUrl("javascript:"
						+ "document.getElementById(\"u\").value= \"" + bean.username + "\";"
						+ "document.getElementById(\"p\").value= \"" + bean.password + "\";"
						+ "document.getElementById(\"go\").click();");
			}else
			{
				ToastUtil.toast("请配置您的QQ邮箱账号，以便使用邮件服务...");
//				mTts.startSpeaking("请配置您的QQ邮箱账号，以便使用邮件服务...", mSynListener);
			}
		}
		
		public void processQQMailHome()
		{
			mailCookie = webViewMain.getCookie();
			int cookieStart = mailCookie.indexOf("msid=") + 5;
			int cookieEnd = mailCookie.indexOf(";", cookieStart);
			if(cookieStart != -1 && cookieEnd != -1 && cookieEnd > cookieStart)
			{
				msid = mailCookie.substring(cookieStart,cookieEnd);
				
//				String html = htmlBean.html;
				String html = htmlBean.url;

				int start = html.indexOf("/cgi-bin/today");
				//判断是否是邮箱主界面
				if(start != -1)
				{
//					browserState = ParseCommand.Cmd_Mail_InBox;
//					int end = html.indexOf(">", start);
//					if(end != -1)
//					{
						webViewMain.loadUrl("https://w.mail.qq.com/cgi-bin/mail_list?fromsidebar=1&sid="+msid+"&folderid=1&page=0&pagesize=10&sorttype=time&t=mail_list&loc=today,,,151&version=html");	
						return;
//					}else
//					{
//						String str = "邮箱登陆失败，请稍后再试";
//						ToastUtil.toast(str);
//						htmlBean.content = str;
////						mTts.startSpeaking(str, mSynListener);
//					}
				}
			}else
			{
				String str = "邮箱登陆失败，请稍后再试";
				ToastUtil.toast(str);
				htmlBean.content = str;
//				mTts.startSpeaking("邮箱登陆失败，请稍后再试", mSynListener);
			}
		}
		public void processMailContent()
		{
			if(htmlBean.html.length() > 0)
			{
				String mailContent = ParseMailContent.praseMailContent(htmlBean.html);
				htmlBean.content = mailContent;
//				mTts.startSpeaking(htmlBean.content, mSynListener);
			}
			else
			{
				String str = "邮件详情读取失败，请稍后再试";
				ToastUtil.toast(str);
				htmlBean.content = str;
//				mTts.startSpeaking("邮件详情读取失败，请稍后再试", mSynListener);
			}
		}
		
		
		public void processMailList()
		{
			String html = htmlBean.html;
//			browserState = ParseCommand.Cmd_Original;
			List<MailListBean> list = ParseMailList.parseMailList(html);
			if(list.size() == 0)
			{
//				String str = "邮件读取失败，请稍后再试";
//				ToastUtil.toast(str);
//				htmlBean.content = str;
//				mTts.startSpeaking("读取失败，请稍后再试", mSynListener);
			}
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
						speakStr += "第"+i+"条，来自"+list.get(i-1).mailFrom+"，主题："+list.get(i-1).mailTitle+"\n";
					}
				}
				else
				{
						speakStr = "您的收件箱暂无最新邮件";
						ToastUtil.toast(speakStr);
				}
				htmlBean.content = speakStr;
//				mTts.startSpeaking(htmlBean.content, mSynListener);
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
//			mTts.startSpeaking(htmlBean.content, mSynListener);
		}
		
		public void processNewsContent()
		{
			String html = htmlBean.html;
			String content = ParseTencentNews.getNewsContent(html);
			String title = Jsoup.parse(htmlBean.html).title();
			title = title.replace("-手机腾讯网", "");
			htmlBean.content = "标题：" + title+"\n"+content;
//			mTts.startSpeaking(htmlBean.content, mSynListener);
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
//				mTts.startSpeaking(htmlBean.content, mSynListener);
			}else
			{	
				ToastUtil.toast("搜索出错，请重试。");
//				mTts.startSpeaking("搜索出错，请重试。", mSynListener);
			}
		}
		
		public void processSinaWeather()
		{
			htmlBean.content = ParseWeatherHtml.praseWeatherList(htmlBean.html);
//			mTts.startSpeaking(htmlBean.content,mSynListener);
		}
		
		public void processBookmark(){
			Intent intent = new Intent(MainActivity.this,BookMarkActivity.class);
			intent.putExtra("url", htmlBean.url);
			intent.putExtra("title", Jsoup.parse(htmlBean.html).title());
			startActivityForResult(intent, REQUEST_CODE_BOOKMARK);
		}
		
		public void processSetting(){
			Intent intent = new Intent();
			intent = new Intent(MainActivity.this,SettingActivity.class);
			startActivityForResult(intent,REQUEST_CODE_SETTING);
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
					webViewMain.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 5.1.1; zh-cn; PLK-UL00 Build/HONORPLK-UL00) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 MQQBrowser/5.3 Mobile Safari/537.36");
					webViewMain.loadUrl(url);
				}
				break;
			case REQUEST_CODE_HISTORY:
				if(resultCode == RESULT_OK)
				{
					String url = data.getStringExtra("url");
					webViewMain.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 5.1.1; zh-cn; PLK-UL00 Build/HONORPLK-UL00) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 MQQBrowser/5.3 Mobile Safari/537.36");
					webViewMain.loadUrl(url);
				}
				break;
			case REQUEST_CODE_SETTING:
			{
				final SharedPreferences sharedpref = MySharedPreferences.getInstance(getApplicationContext());
			    autoread = sharedpref.getBoolean("autoread", false);
			    blind = sharedpref.getBoolean("blind", false);
				break;
			}
			default:
				break;
			}
		}
		@Override
		public void onReceiveTitle(String title) {
			// TODO Auto-generated method stub
			tv_head.setText(title);
		}
		@Override
		public void onReceiveMessage(int tag) {
			// TODO Auto-generated method stub
			webViewMain.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 5.1.1; zh-cn; PLK-UL00 Build/HONORPLK-UL00) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 MQQBrowser/5.3 Mobile Safari/537.36");
			switch (tag) {
			case 1:
				webViewMain.loadUrl("http://m.baidu.com");
//				mViewPager.setCurrentItem(1);
				break;
			case 2:
//				mViewPager.setCurrentItem(1);
				cmdReadNews();
				break;
			case 3:
//				mViewPager.setCurrentItem(1);
				cmdMail();
				break;
			case 4:
//				mViewPager.setCurrentItem(1);
				cmdWeather();
				break;
			case 5:
//				mViewPager.setCurrentItem(1);
				cmdLocation();
				break;
			case 6:
//				mViewPager.setCurrentItem(1);
				webViewMain.loadUrl("http://inews.ifeng.com/index.shtml");
				break;
			case 7:
				processBookmark();
				break;
			case 8:
				processSetting();
				break;
			default:
				break;
			}

		}
		
		@Override
		public void onPageFinished(String url) {
			// TODO Auto-generated method stub
			if (url != null && url.length() > 0) {
			
			htmlBean.url = url;
			int tag = ParsePageType.getPageType(url);
			
			switch (tag) {
			case ParsePageType.MailLoginTag:
				processLoginQQMail();
				break;
			case ParsePageType.MailHomePageTag:
				processQQMailHome();
				break;
			case ParsePageType.MailListTag:
				processMailList();
			default:
				if(blind && !url.contains("android_asset"))
				{
					if(mViewPager.getCurrentItem() == 2)
						webViewMain.loadUrl("javascript:window.HTML.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
					else {
						mViewPager.setCurrentItem(2);
					}
				}
				break;
			}
			
			}
		}
		@Override
		public void onLoadUrl(String url) {
			// TODO Auto-generated method stub
			if(url != null && url.indexOf("javascript:window") == -1)
				mViewPager.setCurrentItem(1);
		}
}
