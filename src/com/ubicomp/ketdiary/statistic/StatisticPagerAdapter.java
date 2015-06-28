package com.ubicomp.ketdiary.statistic;

import java.util.ArrayList;

import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.ubicomp.ketdiary.system.PreferenceControl;

public class StatisticPagerAdapter extends PagerAdapter {

	private ArrayList<View> viewsList;
	private StatisticPageView[] statisticViews;

	public StatisticPagerAdapter() {
		viewsList = new ArrayList<View>();

		Boolean debug = PreferenceControl.isDebugMode();
		Boolean debug_type = PreferenceControl.debugType();


		statisticViews = new StatisticPageView[2];
		if (Build.VERSION.SDK_INT >= 11) {
			statisticViews[0] = new StatisticDayView();
			statisticViews[1] = new StatisticWeekView();
		} else {
			statisticViews[0] = new StatisticDayView();
			statisticViews[1] = new StatisticWeekView();

		}
		viewsList.add(statisticViews[0].getView());
		viewsList.add(statisticViews[1].getView());

	}

	@Override
	public int getCount() {
		return viewsList.size();
	}

	@Override
	public Object instantiateItem(View collection, int position) {

		((ViewPager) collection).addView(viewsList.get(position), 0);

		return viewsList.get(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (View) arg1;
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView(viewsList.get(position));
	}

	public void load() {
		for (int i = 0; i < statisticViews.length; ++i)
			statisticViews[i].load();
	}

	public void onCancel() {
		for (int i = 0; i < statisticViews.length; ++i)
			statisticViews[i].onCancel();
	}

	public void clear() {
		for (int i = 0; i < statisticViews.length; ++i)
			statisticViews[i].clear();
	}

}
