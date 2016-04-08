package cn.hukecn.speechbrowser.util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.hukecn.speechbrowser.bean.MailListBean;

public class ParseMailList {
	public static List<MailListBean> parseMailList(String html)
	{
		List<MailListBean> list = new ArrayList<MailListBean>();
		Document doc = Jsoup.parse(html);
		Elements maillist_listItems = doc.getElementsByClass("maillist_listItem");
		for(Element item:maillist_listItems)
		{
			MailListBean bean = new MailListBean();
			Element maillist_listItemRight = item.getElementsByClass("maillist_listItemRight").get(0);
			bean.mailFrom = maillist_listItemRight.getElementsByClass("maillist_listItemLineFirst").get(0).text();
			bean.mailDesc = maillist_listItemRight.getElementsByClass("maillist_listItemLineThird").get(0).text();
			int start = maillist_listItemRight.html().indexOf("LineSecond");
			int end = maillist_listItemRight.html().indexOf("</div>",start);
			if(start != -1 && end != -1)
				bean.mailTitle = maillist_listItemRight.html().substring(start+27, end);
			bean.mailUrl = "https://w.mail.qq.com" + maillist_listItemRight.attr("href");
			list.add(bean);
		}
		
		
		return list;
		//Log.e("1111", ""+size);
		//Log.e("2222", gs.get(0).text());
		
	}
}