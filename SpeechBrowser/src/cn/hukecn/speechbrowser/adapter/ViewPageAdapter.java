package cn.hukecn.speechbrowser.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ViewPageAdapter extends PagerAdapter {

	private List<View> mListViews;  
    private String[] mTitles;  
    public ViewPageAdapter(List<View> views, String[] titles){  
        this.mListViews = views;  
        this.mTitles = titles;  
    }  
    @Override  
    public int getCount() {  
        // TODO Auto-generated method stub  
        return mListViews.size();  
    }  
  
    @Override  
    public boolean isViewFromObject(View arg0, Object arg1) {  
        // TODO Auto-generated method stub  
        return (arg0==arg1);  
    }  
    @Override  
    public void destroyItem(ViewGroup container, int position, Object object) {  
        // TODO Auto-generated method stub  
        container.removeView(mListViews.get(position));  
    }  
    @Override  
    public Object instantiateItem(ViewGroup container, int position) {  
        // TODO Auto-generated method stub  
        container.addView(mListViews.get(position), 0);  
        return mListViews.get(position);  
    }  
    @Override  
    public CharSequence getPageTitle(int position) {  
        // TODO Auto-generated method stub  
        return mTitles[position];  
    }  

}
