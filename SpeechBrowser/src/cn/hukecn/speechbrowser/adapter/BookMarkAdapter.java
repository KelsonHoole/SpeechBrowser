package cn.hukecn.speechbrowser.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.hukecn.speechbrowser.R;
import cn.hukecn.speechbrowser.DAO.MyDataBase;
import cn.hukecn.speechbrowser.bean.BookMarkBean;

public class BookMarkAdapter extends BaseAdapter{

	Context context;
	List<BookMarkBean> list;
	public String getUrl(int location)
	{
		return list.get(location).url;
	}
	public BookMarkAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		list = new ArrayList<BookMarkBean>();
	}
	
	public void insert(List<BookMarkBean> list)
	{
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	
	public void setData(List<BookMarkBean> list)
	{
		this.list = list;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView == null)
		{
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.book_mark_item, null);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			holder.tv_url = (TextView) convertView.findViewById(R.id.tv_url);
			convertView.setTag(holder);
		}else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		bindView(position,holder);
		return convertView;
	}
	
	private void bindView(int position, ViewHolder holder) {
		// TODO Auto-generated method stub
		BookMarkBean bean = list.get(position);
		holder.tv_title.setText(bean.title);
		holder.tv_url.setText(bean.url);
	}

	private class ViewHolder{
		TextView tv_url,tv_title;
	}

	public void deleteItem(int position) {
		// TODO Auto-generated method stub
		BookMarkBean bean = (BookMarkBean) getItem(position);
		MyDataBase db = MyDataBase.getInstance();
        if(db.delete(bean) != 0)
        	Toast.makeText(context, "É¾³ý³É¹¦", Toast.LENGTH_SHORT).show();
        else
        	Toast.makeText(context, "É¾³ýÊ§°Ü", Toast.LENGTH_SHORT).show();

        list.remove(position);
        notifyDataSetChanged();
	}

}
