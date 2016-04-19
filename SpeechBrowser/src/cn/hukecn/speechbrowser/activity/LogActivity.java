package cn.hukecn.speechbrowser.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.EncodingUtils;
import org.w3c.dom.Text;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnScrollChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.hukecn.speechbrowser.R;
import cn.hukecn.speechbrowser.R.id;
import cn.hukecn.speechbrowser.R.layout;
import cn.hukecn.speechbrowser.util.ToastUtil;

public class LogActivity extends Activity {

	TextView tv_info = null;
	ScrollView scrollView = null;
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			scrollView.smoothScrollTo(0, tv_info.getHeight());
		};
	};
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);
		
		tv_info = (TextView) findViewById(R.id.tv_info);
		scrollView = (ScrollView) findViewById(R.id.scroll);
		File file = new File(Environment.getExternalStorageDirectory(),  
	            "zSpeechBrowserLog.txt"); 
		if(file.exists())
		{
			try {
				FileInputStream is = new FileInputStream(file);
				int length = is.available(); 
				byte [] buffer = new byte[length];   
		        is.read(buffer);       
		        String str = "";
		        str = EncodingUtils.getString(buffer, "UTF-8");   
		        is.close();  
		        
		        if(str.length() > 0)
		        {
		        	int start = 0;
		        	int end = 0;
		        	
		        	List<String> list = new ArrayList<String>();
		        	while(true)
		        	{
		        		start = str.indexOf("<log>",end);
		        		if(start != -1)
		        			end = str.indexOf("</log>",start);
		        		else
		        			break;
		        		
		        		if(end != -1 && end >= start)
		        		{
		        			String temp = str.substring(start+6,end);
		        			list.add(temp);
		        		}else
		        			break;
		        	}
		        	
		        	for(String temp:list)
		        	{
		        		tv_info.append("==================LOG=================\n"+temp+"\n");
		        	}
		        	
		        }else
		        	tv_info.setText("暂无日志信息...");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				tv_info.setText("暂无日志信息...");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				tv_info.setText("暂无日志信息...");
			}
		}else
		{
			tv_info.setText("暂无日志信息...");
		}
		handler.sendEmptyMessageDelayed(0, 200);
	}	
}
