package cn.hukecn.speechbrowser.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import cn.hukecn.speechbrowser.R;
import cn.hukecn.speechbrowser.DAO.MyDataBase;
import cn.hukecn.speechbrowser.R.id;
import cn.hukecn.speechbrowser.R.layout;
import cn.hukecn.speechbrowser.adapter.BookMarkAdapter;
import cn.hukecn.speechbrowser.bean.BookMarkBean;

public class BookMarkActivity extends Activity {

	ListView lv_bookmark = null;
	BookMarkAdapter adapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_mark);
		
		Button btn_insert = (Button) findViewById(R.id.btn_insert);
		
		Intent intent =getIntent();
		final String url = intent.getStringExtra("url");
		final String title = intent.getStringExtra("title");
		adapter = new BookMarkAdapter(this);
		lv_bookmark = (ListView) findViewById(R.id.listview);
		lv_bookmark.setAdapter(adapter);
		
		MyDataBase db = MyDataBase.getInstance();
		List<BookMarkBean> list = db.queryBookMark();
		if(list != null && list.size() > 0)
			adapter.insert(list);
		else
		{
			Toast.makeText(getApplicationContext(), "暂无书签，请添加...", Toast.LENGTH_SHORT).show();
		}
		
		btn_insert.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyDataBase db = MyDataBase.getInstance();
				BookMarkBean bean = new BookMarkBean();
				bean.title = title;
				bean.url = url;
				if(db.insertBookMark(bean) != -1)
				{
					Toast.makeText(getApplicationContext(), "插入"+title+"成功", Toast.LENGTH_SHORT).show();
					List<BookMarkBean> list = db.queryBookMark();
					adapter.setData(list);
				}
			}
		});
		
		
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
		
		lv_bookmark.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				// TODO Auto-generated method stub
				AlertDialog alert = new AlertDialog.Builder(BookMarkActivity.this).create();
                alert.setIcon(R.drawable.bookmark);
                alert.setTitle("删除？");
                alert.setMessage("确定要删除该书签？");
                alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.deleteItem(position);
                    }
                });

                alert.show();
                return true;
			}
		});
	
	}
}
