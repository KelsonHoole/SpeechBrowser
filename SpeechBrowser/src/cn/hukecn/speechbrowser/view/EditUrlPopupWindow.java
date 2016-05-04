package cn.hukecn.speechbrowser.view;

import cn.hukecn.speechbrowser.R;

import com.iflytek.cloud.InitListener;
import com.sleepycat.asm.Handle;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class EditUrlPopupWindow extends PopupWindow implements OnClickListener,android.widget.PopupWindow.OnDismissListener{

	Context context = null;
	EditText et_url = null;
	ImageButton btn_clear = null;
	ListView lv_url = null;
	EditUrlPopupDismissListener listener = null;
	public final static int TYPE_URL = 0;
	public final static int TYPE_CNT = 1;
	public EditUrlPopupWindow(Context context,EditUrlPopupDismissListener listener) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.listener = listener;
		initView();
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		View content = View.inflate(context, R.layout.edit_url_popupwindow, null);
		et_url = (EditText) content.findViewById(R.id.et_head);
		btn_clear = (ImageButton) content.findViewById(R.id.btn_clear);
		lv_url = (ListView) content.findViewById(R.id.lv_url);
		
		btn_clear.setOnClickListener(this);
		
		et_url.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_SEARCH)
				{
					String input = v.getText().toString();
			        if(input.indexOf("http") != -1||input.indexOf("www") != -1||input.indexOf(".com") != -1 || input.indexOf(".cn") != -1||input.indexOf(".edu") != -1||input.indexOf(".net") != -1)
					{
						if(input.indexOf("http") == -1)
						{
							input = "http://"+input;
						}
						if(listener != null)
							listener.onDismiss(TYPE_URL,input);
						dismiss();
					}else
					{
//						String url = "http://m.baidu.com/s?word="+input;
						if(listener != null)
							listener.onDismiss(TYPE_CNT,input);
						dismiss();
					}
					return true;
				}else
					return false;
			}
		});
		
		setContentView(content);
		setHeight(LayoutParams.WRAP_CONTENT);
		setWidth(LayoutParams.MATCH_PARENT);
		setFocusable(true);
		setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
        setOnDismissListener(this);
        et_url.setTextIsSelectable(true);
	}
	
	public void show(View view,String url)
	{
		et_url.setText(url);
		et_url.selectAll();
		int[] location = new int[2];
      	view.getLocationOnScreen(location);
        showAtLocation(view, Gravity.NO_GRAVITY, 0, location[1]);
        final InputMethodManager imm = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);  
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				imm.showSoftInput(et_url, 0);
			}
		},100);
	}

	Handler handler = new Handler();
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_clear:
			et_url.setText("");
			break;
		default:
			break;
		}
	}
	
	public interface EditUrlPopupDismissListener{
		void onDismiss(int type,String content);
	}

	@Override
	public void onDismiss() {
		// TODO Auto-generated method stub
		InputMethodManager imm = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);  
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

}
