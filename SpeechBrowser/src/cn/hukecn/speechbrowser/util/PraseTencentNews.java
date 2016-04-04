package cn.hukecn.speechbrowser.util;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import cn.hukecn.speechbrowser.bean.NewsBean;

public class PraseTencentNews {
	public static final String HOMEURL = "http://info.3g.qq.com/g/s?icfa=infocenter&aid=template&tid=news_guoneiss&i_f=703";
	
	public static List<NewsBean> getNewsList(String html) {
				// TODO Auto-generated method stub
			List<NewsBean> list = new ArrayList<NewsBean>();
			Document doc = Jsoup.parse(html);
			
			Elements elements = doc.getElementsByTag("li");
			for(Element element:elements)
			{
				if(element.html().indexOf("c.gdt.qq.com") != -1)
					continue;
					
				NewsBean bean = new NewsBean();
				Elements tits = element.getElementsByClass("tit");
				String title = "";
				for(Element e_title:tits)
				{
					title += e_title.text(); 
				}
				bean.newsTitle = title;
				
				Elements resources = element.getElementsByClass("resource");
				String resource = "";
				for(Element e_resource:resources)
				{
					resource += e_resource.text();
				}
				bean.newsResource = resource;
				
				Elements e_urls = element.getElementsByTag("a");
				bean.newsUrl="http://info.3g.qq.com"+e_urls.get(0).attr("href");
				if(bean.newsUrl.indexOf("c.gdt.qq.com") == -1)
				{
					list.add(bean);
				}
			}
			return list;
	}
	
	public static String getNewsContent(String html)
	{
			Document doc = Jsoup.parse(html);
			Element article = doc.getElementsByTag("article").get(0);
			Elements ps = article.getElementsByTag("p");
			String content = "";
			for(Element p:ps)
			{
				content += p.text()+"\n";
			}
			return content;
	}
}
