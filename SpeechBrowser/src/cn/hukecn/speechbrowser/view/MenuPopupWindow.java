package cn.hukecn.speechbrowser.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import cn.hukecn.speechbrowser.R;

public class MenuPopupWindow extends PopupWindow implements OnClickListener,OnDismissListener{
	
	TextView btn_m_bookmark,btn_m_setting,btn_m_email,btn_m_exit
				,btn_m_refresh,btn_m_other,btn_m_homepage,btn_m_history;
	OnClickListener listener = null;
	Context context;
	int height;
	int width;
//	Window window = null;
	OnDismissListener dmListener = null;
	private boolean isDismiss = true;
	
	public MenuPopupWindow(Context mContext,OnClickListener listener
			,Window window,OnDismissListener dmListener) {
		// TODO Auto-generated constructor stub
		this.listener = listener;
		this.context = mContext;
//		this.window = window;
		this.dmListener = dmListener;
		
		 final View contentView = LayoutInflater.from(mContext).inflate(R.layout.menu_popup_window, null);
		 btn_m_bookmark = (TextView) contentView.findViewById(R.id.btn_m_bookmark);
		 btn_m_email = (TextView) contentView.findViewById(R.id.btn_m_email);
		 btn_m_exit = (TextView) contentView.findViewById(R.id.btn_m_exit);
		 btn_m_setting = (TextView) contentView.findViewById(R.id.btn_m_setting);
		 btn_m_history = (TextView) contentView.findViewById(R.id.btn_m_history);
		 btn_m_homepage = (TextView) contentView.findViewById(R.id.btn_m_homepage);
		 btn_m_refresh = (TextView) contentView.findViewById(R.id.btn_m_refresh);
		 btn_m_other = (TextView) contentView.findViewById(R.id.btn_m_other);
		 
		 btn_m_bookmark.setOnClickListener(this);
		 btn_m_email.setOnClickListener(this);
		 btn_m_exit.setOnClickListener(this);
		 btn_m_setting.setOnClickListener(this);
		 btn_m_other.setOnClickListener(this);
		 btn_m_homepage.setOnClickListener(this);
		 btn_m_history.setOnClickListener(this);
		 btn_m_refresh.setOnClickListener(this);
		 
		 
		 int w = View.MeasureSpec.makeMeasureSpec(0,
	                View.MeasureSpec.UNSPECIFIED);
	     int h = View.MeasureSpec.makeMeasureSpec(0,
	                View.MeasureSpec.UNSPECIFIED);
	     contentView.measure(w, h);
	     height = contentView.getMeasuredHeight();
	     width = contentView.getMeasuredWidth();
		 
		 
		 this.setContentView(contentView);  
	        //设置SelectPicPopupWindow弹出窗体的宽  
	        this.setWidth(LayoutParams.MATCH_PARENT);  
	        //设置SelectPicPopupWindow弹出窗体的高  
	        this.setHeight(LayoutParams.WRAP_CONTENT);  
	        //设置SelectPicPopupWindow弹出窗体可点击  
	        this.setFocusable(true);  
	        //设置SelectPicPopupWindow弹出窗体动画效果  
//	        this.setAnimationStyle(android.R.style.AnimBottom);  
	        //实例化一个ColorDrawable颜色为半透明  
//	        ColorDrawable dw = new ColorDrawable(0x55000000);  
//	        设置SelectPicPopupWindow弹出窗体的背景  
//	        this.setBackgroundDrawable(dw);  
	        setAnimationStyle(R.style.popwin_anim_style);
	        setBackgroundDrawable(new BitmapDrawable());
	        setOutsideTouchable(true);
	        setOnDismissListener(this);
	}

	
	public boolean isDismiss()
	{
		return isDismiss;
	}
	public void showPopupWindow(View v)
	{
		int[] location = new int[2];
      	v.getLocationOnScreen(location);
        showAtLocation(v, Gravity.NO_GRAVITY,0,location[1]-height);
        isDismiss = false;
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(listener != null)
			listener.onClick(v);
	}

	@Override
	public void onDismiss() {
		// TODO Auto-generated method stub
		dmListener.onDismiss();
		isDismiss = true;
	}
}
