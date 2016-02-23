package cn.hukecn.speechbrowser.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.hukecn.speechbrowser.bean.NewsBean;

public class PraseNews {

	public static List<NewsBean> prase(String content){
		List<NewsBean> list = new ArrayList<NewsBean>();
		try {
			JSONObject pagebean = new JSONObject(content)
			.getJSONObject("showapi_res_body")
			.getJSONObject("pagebean");
			
			JSONArray array = pagebean.getJSONArray("contentlist");
			
			for(int i = 0;i <array.length();i++)
			{
				NewsBean bean = new NewsBean();
				JSONObject jsonItem = array.getJSONObject(i);
				bean.newsTitle = jsonItem.getString("title");
				bean.newsUrl = jsonItem.getString("link");
				bean.newChannel = jsonItem.getString("channelName");
				bean.newsDesc = jsonItem.getString("desc");
				bean.newLongDesc = jsonItem.getString("long_desc");
				list.add(bean);
			}
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
}
