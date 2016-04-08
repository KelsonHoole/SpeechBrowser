package cn.hukecn.speechbrowser.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class ParseMailContent {

	public static String praseMailContent(String html){
		String title = Jsoup.parse(html).getElementsByClass("readmail_item_head_titleText").get(0).text();
		String mailContent="";
		if(title.length() > 0)
			mailContent += "邮件标题："+title+"。\n";
//		else
//		{
//			mailContent = "邮件解析失败，请稍后再试。";
//			return mailContent;
//		}

		int start = -1;
		int end = -1;
		start = html.indexOf("readmail_item_contentNormal qmbox");
		end = html.indexOf("readmail_attachWrap",start);
		String htmlContent;
		if(start != -1 && end != -1)
		{
			htmlContent = html.substring(start - 12,end - 12);
			mailContent += "邮件内容："+Jsoup.parse(htmlContent).text();
		}else
		{
			start = html.indexOf("readmail_item_contentConversation qmbox");
			end = html.indexOf("readmail_attachWrap",start);
			if(start != -1 && end != -1)
			{
				htmlContent = html.substring(start - 12,end - 12);
				mailContent += "邮件内容："+Jsoup.parse(htmlContent).text();
			}else
				mailContent += "邮件内容暂不支持阅读";
		}
		
		return mailContent;
	}
}
