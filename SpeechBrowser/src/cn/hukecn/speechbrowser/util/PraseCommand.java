package cn.hukecn.speechbrowser.util;

import java.util.List;

public class PraseCommand {
	public static final int Cmd_Original = -2;
	public static final int Cmd_Err = -1;
	public static final int Cmd_Search = 0;
	public static final int Cmd_Open = 1;
	public static final int Cmd_Weather = 2;
	public static final int Cmd_WeatherComCn = 12;
	public static final int Cmd_News = 3;
	public static final int Cmd_Mail = 4;
	public static final int Cmd_NewsNum = 5;
	public static final int Cmd_Location = 6;
	public static final int Cmd_Route = 7;
	public static final int Cmd_Exit = 8;
	public static final int Cmd_Mail_Home = 9;
	public static final int Cmd_Mail_InBox = 10;
	public static final int Cmd_Mail_MailContent = 11;
	public static final int Cmd_Other = 99;
	
	public static int prase(List<String> list)
	{
		String listStr = "";
		for(String temp:list)
		{
			listStr +=temp;
		}
		if(list.get(0).equals("。") || list.get(0).equals(""))
			return Cmd_Other;
		
		if(list.get(list.size() -1).equals("。"))
			list.remove(list.size()-1);
		
		if(listStr.indexOf("天气") != -1)
		{
			//if(list.size() > 1)
				return Cmd_Weather;
			//else
			//	return Cmd_Err;
		}
		
		if(listStr.indexOf("邮箱") != -1)
			return Cmd_Mail;
		
		if(listStr.indexOf("新闻") != -1)
			return Cmd_News;
		if(list.get(0).indexOf("搜索") != -1)
			return Cmd_Search;
		//if(list.get(0).indexOf("打开") != -1)
			//return Cmd_Open;	
		if(list.get(list.size()-1).equals("条") &&list.get(0).equals("第")&&list.size() == 3)
			return Cmd_NewsNum;
		if(listStr.indexOf("哪") != -1||listStr.indexOf("位置") != -1)
			return Cmd_Location;
		if(listStr.indexOf("关闭") != -1 || listStr.indexOf("退出") != -1)
			return Cmd_Exit;
		return 100;
	}
	
	public static int praseNewsIndex(List<String> list){
			String strNum = list.get(1);
			if(strNum.equals("一"))
				return 1;
			if(strNum.equals("二"))
				return 2;
			if(strNum.equals("三"))
				return 3;
			if(strNum.equals("四"))
				return 4;
			if(strNum.equals("五"))
				return 5;
			if(strNum.equals("六"))
				return 6;
			if(strNum.equals("七"))
				return 7;
			if(strNum.equals("八"))
				return 8;
			if(strNum.equals("九"))
				return 9;
			if(strNum.equals("十"))
				return 10;
			if(strNum.equals("十一"))
				return 11;
			return Integer.parseInt(strNum);
	}
}
