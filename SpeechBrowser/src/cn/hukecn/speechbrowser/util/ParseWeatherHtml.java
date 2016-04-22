package cn.hukecn.speechbrowser.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;
import cn.hukecn.speechbrowser.bean.WeatherBean;

public class ParseWeatherHtml {
	public static String praseWeatherList(String html){
		
		if(html == null || html.length() ==0)
			return "获取天气信息失败，请稍后再试";
//		List<WeatherBean> list = new ArrayList<WeatherBean>();
		Document doc = Jsoup.parse(html);

//		for(Element element:lis)
//		{
//			WeatherBean bean = new WeatherBean();
//			Elements dates = element.getElementsByTag("b");
//			Elements temps = element.getElementsByTag("span");
//			Elements imgs = element.getElementsByTag("img");
//			String weather1 = null;
//			String weather2 = null;
//			if(imgs.size() == 1)
//				weather1 = imgs.get(0).attr("alt");
//			if(imgs.size() == 2)
//			{
//				weather1 = imgs.get(0).attr("alt");
//				weather2 = imgs.get(1).attr("alt");
//			}
//			if(i != 0)
//				bean.date = dates.get(0).text();
//			else
//				bean.date = ""+dates.get(0).text();
//			
//			if(temps.get(0).text().indexOf("/") != -1)
//				bean.temp = temps.get(0).text().replace("/", "到");
//			else
//				bean.temp = temps.get(0).text();
//			
//			//Log.e("1111", weather1+"转"+weather2);
//			if(weather2 == null)
//				bean.weather = weather1;
//			else if(weather1.equals(weather2))
//			{
//				bean.weather = weather1;
//			}else
//			{
//				bean.weather = weather1+"转"+weather2;
//			}
//			list.add(bean);
//			i++;
//		}
//		
//		return list;
		
//		String location = doc.getElementsByClass("ng-binding").get(0).text();
//		String nowTemp = "";
//		int start = html.indexOf("pull-left long-temp ng-binding");
//		int end = html.indexOf("</div>");
		try{
			String city = doc.getElementsByClass("inx_w_city_c").get(0).text();
			String weather = doc.getElementsByClass("inx_w_r_mare").get(0).text();
			String temp = doc.getElementsByClass("inx_w_r_num").get(0).text();
//			String air = doc.getElementsByClass("inx_link_tips").get(0).text();
			
			Elements days7 = doc.getElementsByClass("weather_datemate");
			Elements lis = days7.get(0).getElementsByTag("li");
			List<WeatherBean> list = new ArrayList<WeatherBean>();
			for(Element li:lis)
			{
				WeatherBean bean = new WeatherBean();
				Elements weather_w_20 = li.getElementsByClass("weather_w_20");
				bean.date= weather_w_20.get(0).getElementsByTag("strong").get(0).text();
				Elements weather_w_25 = li.getElementsByClass("weather_w_25").get(0).getElementsByTag("span");
				if(weather_w_25.get(0).text().equals(weather_w_25.get(1).text()))
				{
					bean.weather = weather_w_25.get(0).text();
				}else
				{
					bean.weather = weather_w_25.get(0).text()+"转"+weather_w_25.get(1).text();
				}
				
				Elements e_temp_span = weather_w_20.get(1).getElementsByTag("span");
				bean.temp = e_temp_span.get(1).text()+"到"+e_temp_span.get(0).text();
				list.add(bean);
			}
			
			String towDays = "";
			towDays += list.get(1).date+","+list.get(1).weather+","+list.get(1).temp+'\n';
			towDays += list.get(2).date+","+list.get(2).weather+","+list.get(2).temp+'\n';
			towDays += list.get(3).date+","+list.get(3).weather+","+list.get(3).temp+'\n';

			
			return "您当前位于："+city+"市\n"+"今日天气："+weather+"\n当前气温："+temp+"\n"+towDays;
		}catch (IndexOutOfBoundsException e)
		{
			return "获取天气信息失败，请稍后再试";
		}
		
	}

}
