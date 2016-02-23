package cn.hukecn.speechbrowser.util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.hukecn.speechbrowser.bean.NewsBean;
import cn.hukecn.speechbrowser.http.MyHttp;
import cn.hukecn.speechbrowser.http.MyHttp.HttpCallBackListener;

public class TencentNews {
	public static final String HOMEURL = "http://info.3g.qq.com/g/s?icfa=infocenter&aid=template&tid=news_guoneiss&i_f=703";
	
	
	public static void getNewsList(final NewsCallback instance){
		MyHttp.get(HOMEURL, "", new HttpCallBackListener() {
			
			@Override
			public void onHttpCallBack(int statusCode, String responseStr) {
				// TODO Auto-generated method stub
				
				if(statusCode == 200)
				{
					List<NewsBean> list = new ArrayList<NewsBean>();
					Document doc = Jsoup.parse(responseStr);
					Elements type2s = doc.getElementsByClass("type2");
					Elements type3s = doc.getElementsByClass("type3");
					Elements type4s = doc.getElementsByClass("type4");
					
					for(Element element:type2s)
					{
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
					
					for(Element element:type3s)
					{
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
					
					for(Element element:type4s)
					{
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
					instance.onNewsListCallBack(list);
				}
			}
		});
	}
	
	public interface NewsCallback{
		public void onNewsListCallBack(List<NewsBean> list);
		public void onNewsContentCallBack(String content);
	}
	
	public static void getNewsContent(String url,final NewsCallback callback)
	{
		MyHttp.get(url, "", new HttpCallBackListener() {
			
			@Override
			public void onHttpCallBack(int statusCode, String responseStr) {
				// TODO Auto-generated method stub
				if(statusCode == 200)
				{
					Document doc = Jsoup.parse(responseStr);
					Element article = doc.getElementsByTag("article").get(0);
					Elements ps = article.getElementsByTag("p");
					String content = "";
					for(Element p:ps)
					{
						content += p.text()+"\n";
					}
					callback.onNewsContentCallBack(content);
				}
			}
		});
	}
}
