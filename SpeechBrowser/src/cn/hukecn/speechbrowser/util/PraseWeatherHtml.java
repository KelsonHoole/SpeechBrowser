package cn.hukecn.speechbrowser.util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;
import cn.hukecn.speechbrowser.bean.WeatherBean;

public class PraseWeatherHtml {
	public static List<WeatherBean> praseWeatherList(String html){
		
		if(html == null || html.length() ==0)
			return null;
		List<WeatherBean> list = new ArrayList<WeatherBean>();
		Document doc = Jsoup.parse(html);
		Elements days7 = doc.getElementsByClass("days7");
		Elements lis = days7.get(0).getElementsByTag("li");
		for(Element element:lis)
		{
			WeatherBean bean = new WeatherBean();
			Elements dates = element.getElementsByTag("b");
			Elements temps = element.getElementsByTag("span");
			Elements imgs = element.getElementsByTag("img");
			String weather1 = imgs.get(0).attr("alt");
			String weather2 = imgs.get(1).attr("alt");
			
			bean.date = dates.get(0).text();
			bean.temp = temps.get(0).text().replace("/", "µ½");
			
			//Log.e("1111", weather1+"×ª"+weather2);
			if(weather1.equals(weather2))
			{
				bean.weather = weather1;
			}else
			{
				bean.weather = weather1+"×ª"+weather2;
			}
			list.add(bean);
		}
		
		return list;
	}

}
