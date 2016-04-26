package cn.hukecn.speechbrowser.util;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseAandP {

	public static void parse(String html,List<String> a,List<String> p)
	{
		if(a == null || p == null)
			return ;
		Element element = Jsoup.parse(html).body();
		Elements as = element.getElementsByTag("a");
		Elements ps = element.getElementsByTag("p");
		
		for(Element elementA:as)
		{
			a.add(elementA.text());
		}
		
		for(Element elementP:ps)
		{
			p.add(elementP.text());
		}
	}
}
