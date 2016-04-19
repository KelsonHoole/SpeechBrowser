package cn.hukecn.speechbrowser.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.hukecn.speechbrowser.R;
import cn.hukecn.speechbrowser.bean.BookMarkBean;
import cn.hukecn.speechbrowser.bean.HistoryBean;

public class HistoryAdapter extends BaseAdapter{
	Context context;
	List<HistoryBean> list;
	
	public HistoryAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		list = new ArrayList<HistoryBean>();
	}
	public void insert(List<HistoryBean> list)
	{
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public String getUrl(int location)
	{
		return list.get(location).url;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}
	public void setData(List<HistoryBean> list)
	{
		this.list = list;
		notifyDataSetChanged();
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
		HistoryBean bean = list.get(position);
		holder.tv_title.setText(bean.title);
		holder.tv_url.setText(bean.url);
	}
	private class ViewHolder{
		TextView tv_url,tv_title;
	}
}
