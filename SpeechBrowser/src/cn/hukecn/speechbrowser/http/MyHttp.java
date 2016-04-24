package cn.hukecn.speechbrowser.http;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Kelson on 2016/1/7.
 */
public class MyHttp {
    public static void get(final String strUrl, final String requestPara, final HttpCallBackListener listener){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(strUrl+"?"+requestPara);

                    //URL url = new URL("http://apis.baidu.com/showapi_open_bus/weather_showapi/address?area=武汉&needMoreDay=0&needIndex=0&needAlarm=0&need3HourForcast=0");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(1000);
                    conn.setDoInput(true);
                    conn.setRequestProperty("apikey","b0b105d10bcddaf596b11c256a172cff");
                    conn.addRequestProperty("Connection", "Keep-Alive");
                    conn.addRequestProperty("Content-Type", "text/plain; charset=utf-8");

                    InputStreamReader in = new InputStreamReader(conn.getInputStream());
                    BufferedReader buffer = new BufferedReader(in);
                    String inputLine = null;
                    String result = "";

                    while ((inputLine = buffer.readLine()) != null)
                    {
                        result += inputLine;
                    }

                    if(result.length() >0)
                    {
                        Message msg = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString("content",result);
                        bundle.putInt("code",conn.getResponseCode());
                        msg.setData(bundle);
                        msg.what = 0;
                        msg.obj = listener;
                        handler.sendMessage(msg);
                    }
                    in.close();
                    conn.disconnect();
                }
                catch (MalformedURLException e) {}
                catch (IOException e) {}
            }
        }).start();


    }

    public interface HttpCallBackListener{
        void onHttpCallBack(int statusCode,String responseStr);
    }

    private static android.os.Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0)
            {
                HttpCallBackListener listener = (HttpCallBackListener) msg.obj;
                Bundle bundle = msg.getData();
                listener.onHttpCallBack(bundle.getInt("code"),bundle.getString("content"));
            }
        }
    };
    
}
