package cn.hukecn.speechbrowser.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class ParseBaiduResult {
	//获得百度的搜索页面，前100个搜索结果  
    public static String getHTML(String key) throws IOException  
    {  
        StringBuilder sb=new StringBuilder();  
        String path="http://www.baidu.com/s?tn=ichuner&lm=-1&word="+URLEncoder.encode(key,"gb2312")+"&rn=100";   
        URL url=new URL(path);  
        BufferedReader breader=new BufferedReader(new InputStreamReader(url.openStream()));  
        String line=null;  
        while((line=breader.readLine())!=null)  
        {  
            sb.append(line);  
        }  
        return sb.toString();  
    }  
      
    //对HTML进行析取，析取出100个URL、标题和摘要  
    private static String[][] parseHTML(String key)  
    {  
        String page=null;  
        try  
        {  
            page=getHTML(key);  
        }  
        catch(Exception ex)  
        {  
            ex.printStackTrace();  
        }  
        String[][] pageContent_list=new String[100][3];  
        if(page!=null)  
        {  
            String regx="<table.*?</table>";  
            Pattern pattern=Pattern.compile(regx);  
            Matcher matcher=pattern.matcher(page);  
            for(int i=0;i<101;i++)  
            {  
                if(matcher.find())  
                {  
                    if(i==0)  
                    {  
                        continue;  
                    }  
                    //获得table中的数据  
                    String table_content=matcher.group().toString();  
                    String reg_URL="href=\"(.*?)\"";  
                    Pattern pattern_URL=Pattern.compile(reg_URL);  
                    Matcher matcher_URL=pattern_URL.matcher(table_content);  
                    String page_URL=null;  
                    if(matcher_URL.find())  
                    {  
                        page_URL=matcher_URL.group().toString();  
                    }  
                    page_URL=page_URL.substring(6);  
                    //得到了URL  
                    page_URL=page_URL.substring(0,page_URL.length()-1);  
                    String reg_title="<a.+?href\\s*=\\s*[\"]?(.+?)[\"|\\s].+?>(.+?)</a>";  
                    Pattern patter_title=Pattern.compile(reg_title);  
                    Matcher matcher_title=patter_title.matcher(table_content);  
                    String page_title=null;  
                    if(matcher_title.find())  
                    {  
                        //得到了标题  
                        page_title=matcher_title.group().toString();  
                    }  
                    //从table_content中析取出正文  
                    String page_content = null;  
                    page_content = table_content.substring(table_content.lastIndexOf("</h3>")+5);  
                      
                    pageContent_list[i-1][0]=page_URL;  
                    pageContent_list[i-1][1]=page_title;  
                    pageContent_list[i-1][2]=page_content;  
                  }  
                }  
           }  
           return pageContent_list;  
    }  
    
    public static String[][] baidu(String key){
//    	String[][] result = parseHTML(key);
//    	for(int i=0;i<result.length;i++) {  
//            Log.e("00","第"+(i+1)+"条结果：");  
//            Log.e("00","URL:"+result[i][0]);  
//            Log.e("00","标题:"+result[i][1]);  
//            Log.e("00","摘要:"+result[i][2]);  
//        }  
    	return null;
    }
}
