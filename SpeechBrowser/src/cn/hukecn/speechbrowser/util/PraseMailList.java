package cn.hukecn.speechbrowser.util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;
import cn.hukecn.speechbrowser.activity.MainActivity;
import cn.hukecn.speechbrowser.bean.MailListBean;

public class PraseMailList {
	public static List<MailListBean> parseMailList(String html)
	{
		List<MailListBean> list = new ArrayList<MailListBean>();
		Document doc = Jsoup.parse(html);
		String body = doc.body().text();
		Elements gs = doc.getElementsByClass("g");
		
		int size = gs.size();
		int start = html.indexOf("c m_list") + 10;
		int end = html.indexOf("</p>",start);
		
		if(start > 10 && end != -1 && end > start)
		{
			String content = html.substring(start, end);
			
			//Document list = Jsoup.parse(content);
			
			//String cc = list.text();
			start = 0;
			for(int i = 0;i < size;i++)
			{
				MailListBean bean = new MailListBean();
				end = content.indexOf("class=\"hr4\"",start)+16;
				Document item = Jsoup.parse(content.substring(start,end));
				start = end;
				bean.descStr = item.text();
				//bean.mailUrl = item.attr("href");
				bean.mailUrl = "http://w.mail.qq.com"+item.getElementsByTag("a").get(0).attr("href");
				//Log.e("1111", );
				list.add(bean);
			}
			//MainActivity.writeFileSdcard("",cc);
		}
		
		return list;
		//Log.e("1111", ""+size);
		//Log.e("2222", gs.get(0).text());
		
	}
}
