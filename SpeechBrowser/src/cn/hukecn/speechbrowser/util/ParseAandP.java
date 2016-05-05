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
			String aitem = elementA.text();
			if(aitem != null && aitem.length() > 0)
			{			
				String link = elementA.attr("href");
				if(link != null && link.length() > 0)
				{
					aitem.replace("  ", "");
					a.add(aitem);
				}
			}
		}
		
		for(Element elementP:ps)
		{
			p.add(elementP.text());
		}
	}
}
