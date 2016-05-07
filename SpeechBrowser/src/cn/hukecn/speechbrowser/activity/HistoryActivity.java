package cn.hukecn.speechbrowser.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import cn.hukecn.speechbrowser.R;
import cn.hukecn.speechbrowser.DAO.MyDataBase;
import cn.hukecn.speechbrowser.R.id;
import cn.hukecn.speechbrowser.R.layout;
import cn.hukecn.speechbrowser.adapter.BookMarkAdapter;
import cn.hukecn.speechbrowser.adapter.HistoryAdapter;
import cn.hukecn.speechbrowser.bean.BookMarkBean;
import cn.hukecn.speechbrowser.bean.HistoryBean;

public class HistoryActivity extends Activity {

	ListView lv_bookmark = null;
	HistoryAdapter adapter = null;
	Button btn_clearHistory = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
        ((TextView)findViewById(R.id.tv_titlebar)).setText("历史记录");

		adapter = new HistoryAdapter(this);
		lv_bookmark = (ListView) findViewById(R.id.listview);
		lv_bookmark.setAdapter(adapter);
		btn_clearHistory = (Button) findViewById(R.id.btn_clearHistory);
		
		MyDataBase db = MyDataBase.getInstance();
		List<HistoryBean> list = db.queryHistory();
		if(list != null && list.size() > 0)
			adapter.insert(list);
		else
		{
			Toast.makeText(getApplicationContext(), "暂无历史记录...", Toast.LENGTH_SHORT).show();
		}
		
		lv_bookmark.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent();  
				mIntent.putExtra("url", adapter.getUrl(position));
				setResult(RESULT_OK, mIntent);
				finish();
			}
		});
		
		
		btn_clearHistory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "已成功清除所有历史记录", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
