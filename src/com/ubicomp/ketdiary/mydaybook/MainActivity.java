package com.ubicomp.ketdiary.mydaybook;

import java.util.Calendar;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.mydaybook.linechart.ChartCaller;
import com.ubicomp.ketdiary.mydaybook.linechart.LineChartTitle;
import com.ubicomp.ketdiary.mydaybook.linechart.LineChartView;
//import android.view.ViewGroup.LayoutParams;

public class MainActivity extends FragmentActivity implements ChartCaller {

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private LinearLayout diaryList, boxesLayout, drawerContent;
	private RelativeLayout upperBarContent;
	private TextView titleText, backToTodayText;
	private View diaryItem;
	
	@SuppressWarnings("deprecation")
	private SlidingDrawer drawer;
	private ImageView toggle, toggle_linechart, linechartIcon, calendarIcon;
	private Context context;
	private Database myConstant;
		
	private static int sv_item_height;
	
	private int fragmentIdx;
	
	public int selectedDay, selectedMonth;
	
	private int chart_type = 2;
	private LinearLayout chartAreaLayout;
	private LineChartView lineChart;
	private LineChartTitle chartTitle;
	private ChartCaller caller;
	
	public View lineChartBar, lineChartView, lineChartFilter, calendarBar, calendarView;
	
	public ImageView lineChartFilterButton;
	
	private boolean isFilterIsOpen = false;
	
	private int drawerHeight = App.getContext().getResources().getDimensionPixelSize(R.dimen.drawer_normal_height);
	//public static List<Integer> filterList = new ArrayList<Integer>();

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = this;
		caller = this;
		
		drawerContent = (LinearLayout) findViewById(R.id.drawer_content);
		upperBarContent = (RelativeLayout) findViewById(R.id.upper_bar);
		LayoutInflater inflater = LayoutInflater.from(context);
		calendarView = (View) inflater.inflate(R.layout.calendar_main, null, false);
		calendarBar = (View) inflater.inflate(R.layout.calendar_upperbar, null, false);
		
		drawerContent.addView(calendarView);
		upperBarContent.addView(calendarBar);
		
//		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());  
		myConstant = new Database();
		
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);  
		mViewPager.setAdapter(mSectionsPagerAdapter);	
		
		// Initialize the selectedDay and selectedMonth
		selectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		selectedMonth = Calendar.getInstance().get(Calendar.MONTH);
		
		backToTodayText = (TextView) findViewById(R.id.back_to_today);
		backToTodayText.setText(Integer.toString(selectedDay));
		
		titleText = (TextView) findViewById(R.id.month_text);
		
		drawer = (SlidingDrawer) findViewById(R.id.slidingDrawer1);
		toggle = (ImageView) findViewById(R.id.toggle);
		linechartIcon = (ImageView) findViewById(R.id.linechart_icon);
	
		lineChartBar = (View) inflater.inflate(R.layout.linechart_upperbar, null, false);
		lineChartView = (View) inflater.inflate(R.layout.linechart_main, null, false);
		lineChartFilter = (View) inflater.inflate(R.layout.linechart_filter, null, false);
	    calendarIcon = (ImageView) lineChartBar.findViewById(R.id.back_to_calendar);
	    toggle_linechart = (ImageView) lineChartBar.findViewById(R.id.toggle_linechart);

		showDiary();
				
		drawer.toggle();
		
		setCurrentCalendarPage(selectedMonth + 1 - myConstant.START_MONTH);
		titleText.setText( (selectedMonth + 1)  + "月");
		
		toggle_linechart.setOnClickListener(new ToggleListener());
		toggle.setOnClickListener(new ToggleListener());
		titleText.setOnClickListener(new ToggleListener());
		
		//for ( int i = 0; i < 9; i++ ) { filterList.add(i);} 
	
		linechartIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isFilterIsOpen == false) {				
					drawerContent.removeAllViews();
					upperBarContent.removeAllViews();
					drawerContent.addView(lineChartView);
					upperBarContent.addView(lineChartBar);
				}
				else {
					drawerContent.removeAllViews();
					upperBarContent.removeAllViews();
					drawerContent.addView(lineChartFilter);
					drawerContent.addView(lineChartView);
					upperBarContent.addView(lineChartBar);
				}
				if  (!drawer.isOpened()) { drawer.toggle();}
				
				lineChart = (LineChartView) findViewById(R.id.lineChart);
		        lineChart.setChartData(getRandomData());
		        lineChart.requestLayout();
		        lineChart.getLayoutParams().width = 2200;
		        
		        chartTitle = (LineChartTitle) findViewById(R.id.chart_title);
		        chartTitle.setting(caller);
		        setChartType(2);
		        
		        chartAreaLayout = (LinearLayout) findViewById(R.id.linechart_tabs);
		        chartAreaLayout.setBackgroundResource(R.drawable.linechart_bg);
				
			}
		});
		
		calendarIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("OMG", "hi");
				if (isFilterIsOpen == false) {
					drawerContent.removeAllViews();
					upperBarContent.removeAllViews();
					
					drawerContent.addView(calendarView);
					upperBarContent.addView(calendarBar);
					if  (!drawer.isOpened()) { drawer.toggle();}
				}
				else {
					drawerContent.removeAllViews();
					upperBarContent.removeAllViews();
					
					drawerContent.addView(lineChartFilter);
					drawerContent.addView(calendarView);
					upperBarContent.addView(calendarBar);
					if  (!drawer.isOpened()) { drawer.toggle(); }
				}
				
			}
		});
				
		
		drawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() {
				toggle.setImageResource(R.drawable.dropup_arrow);
				toggle_linechart.setImageResource(R.drawable.dropup_arrow);
			}
		});
		
		drawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			
			@Override
			public void onDrawerClosed() {
				toggle.setImageResource(R.drawable.dropdown_arrow);
				toggle_linechart.setImageResource(R.drawable.dropdown_arrow);
				
			}
		});
			
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageSelected(int arg0) {
				fragmentIdx = arg0;
				titleText.setText( (myConstant.START_MONTH + fragmentIdx) + "月");
			}
			
		});
				
		lineChartFilterButton = (ImageView) lineChartBar.findViewById(R.id.line_chart_filter);
		lineChartFilterButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//		        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
//		        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
//				lineChart.setCanvasHeight( (int) (325*dpHeight) );
				if (isFilterIsOpen == false) {				
					LayoutParams lp = new LayoutParams(drawer.getLayoutParams());
					//Log.i("OMG", "H: "+lp.height);
					lp.height = 1200;
					lp.addRule(RelativeLayout.BELOW, lineChartBar.getId());
					drawer.setLayoutParams(lp);
					
					drawerContent.removeAllViews();
					
					drawerContent.addView(lineChartFilter);
					drawerContent.addView(lineChartView);
					isFilterIsOpen = true;
				}
				else {
					LayoutParams lp = new LayoutParams(drawer.getLayoutParams());
					lp.height = drawerHeight;
					lp.addRule(RelativeLayout.BELOW, lineChartBar.getId());
					drawer.setLayoutParams(lp);
					
					drawerContent.removeAllViews();
					
					drawerContent.addView(lineChartView);
					isFilterIsOpen = false;
				}
				
			}
		});		
	}
	

	private float[] getRandomData() {
        return new float[] { 0, -3, 1, -2, -1, -3, 3, 2, 0, 1, -2, -1, 2, -2, 0, 1, -3, -1, 2, -1, 1, 3};
    }
	
	public void setChartType(int type) {
		chart_type = type;
		switch (chart_type) {
		case 0:
			chartTitle.setBackgroundResource(R.drawable.tab1_pressed);
			break;
		case 1:
			chartTitle.setBackgroundResource(R.drawable.tab2_pressed);
			break;
		case 2:
			chartTitle.setBackgroundResource(R.drawable.tab3_pressed);
			break;		
		}
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void showDiary() {		
		diaryList = (LinearLayout) findViewById(R.id.item);
				
		for (int n = 1  ; n <=30 ; n++) {
			diaryItem = getLayoutInflater().inflate(R.layout.diary_item, null);
			
			//sv_item_height = diaryItem.getMeasuredHeight();
			TextView date_num = (TextView) diaryItem.findViewById(R.id.diary_date);
			diaryList.addView(diaryItem);
			date_num.setText(Integer.toString(n) + "號");
			
			boxesLayout = (LinearLayout) findViewById(R.layout.diary_item);
		
		}
	}
	
	public void setCurrentCalendarPage(int pageIdx){
		mViewPager.setCurrentItem(pageIdx);
	}
			
    private class ToggleListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			drawer.toggle();
		}
    }
		
}
