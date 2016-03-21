package cn.hukecn.speechbrowser.util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class BaiduSearch {
	public static List<String> praseSearchResultList(String html)
	{
		Document doc = Jsoup.parse(html);
		Elements elements = doc.getElementsByClass("c-container");

		int i = 0;
		int start = -1;
		int end = -1;
		List<String> resList = new ArrayList<String>();
		
		for(i = 0;i < elements.size();i++)
		{
			String container = elements.get(i).html();
			start = container.indexOf("<h3");
			end = container.indexOf("</h3>");
			if(start == -1 || end == -1)
				continue;
			Document h3 = Jsoup.parse(container.substring(start,end)+"</h3>");
			resList.add(h3.text());
		}
		return resList;
	}

}
