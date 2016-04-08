package cn.hukecn.speechbrowser.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	private static Context context = null;
	
	public static void init(Context context)
	{
		ToastUtil.context = context;
	}
	
	public static void toast(String text)
	{
		if(context != null)
		{
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		}
	}
}
