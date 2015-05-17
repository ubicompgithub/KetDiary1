package com.ubicomp.ketdiary.dialog;
import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


public class TypePageAdapter extends PagerAdapter{
	
	private ArrayList<View> viewLists;	

	public TypePageAdapter() {}	
	public TypePageAdapter(ArrayList<View> viewLists)
	{
		super();
		this.viewLists = viewLists;
	}
	
	@Override
	public int getCount() {
		return viewLists.size();
	}
	
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(viewLists.get(position));
		return viewLists.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(viewLists.get(position));
	}
}
