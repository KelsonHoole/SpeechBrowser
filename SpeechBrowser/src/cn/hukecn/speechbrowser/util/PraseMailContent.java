package cn.hukecn.speechbrowser.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class PraseMailContent {

	public static String praseMailContent(String html){
		
		Document document = Jsoup.parse(html);
		Elements cs = document.body().getElementsByClass("c");
		//Log.e("1111", cs.get(1).text());
		String mailContent = "";
		for(int i = 0;i < cs.size() - 1;i++)
		{
			mailContent += cs.get(i).text()+"¡£";
		}
		
		return mailContent;
	}
}
