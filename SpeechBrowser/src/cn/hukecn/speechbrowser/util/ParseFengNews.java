package cn.hukecn.speechbrowser.util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.News;

public class ParseFengNews {

	public static List<News> parseFengNewsList(String html){
		
		List<News> list = null;
		
		Document doc = Jsoup.parse(html);
		Element slLis = doc.getElementById("slLis");
		if(slLis != null)
		{
			list = new ArrayList<News>();
			Elements lis = slLis.getElementsByTag("li");
			for(Element li:lis)
			{
				Elements ps = li.getElementsByTag("p");
				if(ps != null && ps.size() > 0)
				{
					Elements as = ps.get(0).getElementsByTag("a");
					if(as != null && as.size() > 0)
					{
						Element a = as.get(0);
						News news = new News();
						news.setTitle(a.text());
						String href = a.attr("href");
						news.setUrl("http://inews.ifeng.com"+href);
						//È¥¹ã¸æ
						if(!href.contains("dol.deliver.ifeng.com") && !href.contains("api.3g.ifeng.com"))
							list.add(news);
					}
				}
			}
		}
		return list;
	}
	
	public static News ParseFengNewsContent(String html){
		try {
			News news = ContentExtractor.getNewsByHtml(html);
			return news;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
}
