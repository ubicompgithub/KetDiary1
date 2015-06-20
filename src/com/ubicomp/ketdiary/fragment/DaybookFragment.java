package com.ubicomp.ketdiary.fragment;

import java.io.File;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.MainActivity;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.db.DatabaseControl;
import com.ubicomp.ketdiary.db.NoteCategory2;
import com.ubicomp.ketdiary.db.TestDataParser2;
import com.ubicomp.ketdiary.dialog.AddNoteDialog2;
import com.ubicomp.ketdiary.dialog.CheckResultDialog;
import com.ubicomp.ketdiary.dialog.TestQuestionCaller2;
import com.ubicomp.ketdiary.file.MainStorage;
import com.ubicomp.ketdiary.file.QuestionFile;
import com.ubicomp.ketdiary.mydaybook.Database;
import com.ubicomp.ketdiary.mydaybook.SectionsPagerAdapter;
import com.ubicomp.ketdiary.mydaybook.linechart.ChartCaller;
import com.ubicomp.ketdiary.mydaybook.linechart.LineChartTitle;
import com.ubicomp.ketdiary.mydaybook.linechart.LineChartView;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.ScaleOnTouchListener;
//import android.view.ViewGroup.LayoutParams;

public class DaybookFragment extends Fragment implements ChartCaller, TestQuestionCaller2 {
	
	public Activity activity = null;
	private DaybookFragment daybookFragment;
	private View view;
	
	private CheckResultDialog msgBox;
	private RelativeLayout fragment_layout;
	
	private static final String TAG = "DayBook";
	
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
		
	private static int sv_item_height;
	
	//file
	private QuestionFile questionFile;
	private File mainDirectory = null;
	private TestDataParser2 TDP;
	
	private int fragmentIdx;
	
	private static final int THIS_MONTH = Calendar.getInstance().get(Calendar.MONTH);
	
	public static int chart_type = 2;
	
	private LinearLayout chartAreaLayout;
	private LineChartView lineChart;
	private LineChartTitle chartTitle;
	private ChartCaller caller;
	
	public View lineChartBar, lineChartView, lineChartFilter, calendarBar, calendarView, filterView;
	
	public ImageView addButton;
	
	private boolean isFilterIsOpen = false;
	private AddNoteDialog2 notePage = null;
	
	
	private DatabaseControl db;
	private NoteCategory2 dict;
	private static final String[] dayOfWeek = {" ", "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
	private static final String[] timeslot = {"上午", "下午", "晚上"};
	
	private final static int[] typeId = {0, R.drawable.book_type1,
		R.drawable.book_type2, R.drawable.book_type3, R.drawable.book_type4, 
		R.drawable.book_type5, 	R.drawable.book_type6, R.drawable.book_type7, 
	 	R.drawable.book_type8};
	
	//public static List<Integer> filterList = new ArrayList<Integer>();

	private ImageView filterAll, filter1, filter2, filter3, filter4, filter5, filter6, filter7, filter8;
	public ImageView lineChartFilterButton, calendarFilterButton, rotateLineChart;
	
	public static boolean[] filterButtonIsPressed = {true, false, false, false, false, false, false, false, false};
	private ImageView[] filterButtonArray = {filterAll, filter1, filter2, filter3, filter4, filter5, filter6, filter7, filter8};
	
	private boolean isFilterOpen = false;
	private boolean isRotated = false;
	
	private int drawerHeight = App.getContext().getResources().getDimensionPixelSize(R.dimen.drawer_normal_height);
	private int drawerHeightWithFilter = App.getContext().getResources().getDimensionPixelSize(R.dimen.drawer_with_filter_height);
	private int filterHeight = App.getContext().getResources().getDimensionPixelSize(R.dimen.filter_normal_height);
	private int filterHeightLandscape = App.getContext().getResources().getDimensionPixelSize(R.dimen.filter_landscape_height);
	
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = App.getContext();
		db = new DatabaseControl();
		dict = new NoteCategory2();
		caller = this;
		daybookFragment = this;
	}
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		view = inflater.inflate(R.layout.fragment_mydaybook, container, false);
		
		fragment_layout = (RelativeLayout) view.findViewById(R.id.mydaybook_layout);
		
		drawerContent = (LinearLayout) view.findViewById(R.id.drawer_content);
		upperBarContent = (RelativeLayout) view.findViewById(R.id.upper_bar);
		
		
		//LayoutInflater inflater = LayoutInflater.from(context);
		calendarView = (View) inflater.inflate(R.layout.calendar_main, null);
		calendarBar = (View) inflater.inflate(R.layout.calendar_upperbar, null);
		
		drawerContent.addView(calendarView);
		upperBarContent.addView(calendarBar);
		
		//MainActivity.getMainActivity().setClickable(false);
		
		//calendarBar.setEnabled(false);

		// Set up the ViewPager with the sections adapter.
		View[] pageViewList = new View[Database.SUSTAINED_MONTHS];
		for (int i = 0; i < Database.SUSTAINED_MONTHS; i++) {
			pageViewList[i] = (View) inflater.inflate(R.layout.fragment_calendar, null);
			pageViewList[i].setTag(i + Database.START_MONTH - 1);
		}
		mSectionsPagerAdapter = new SectionsPagerAdapter(pageViewList);

		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);	
				
		backToTodayText = (TextView) view.findViewById(R.id.back_to_today);
		backToTodayText.setText(Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)));
		
		titleText = (TextView) view.findViewById(R.id.month_text);
		
		drawer = (SlidingDrawer) view.findViewById(R.id.slidingDrawer1);
		toggle = (ImageView) view.findViewById(R.id.toggle);
		linechartIcon = (ImageView) view.findViewById(R.id.linechart_icon);
	
		lineChartBar = (View) inflater.inflate(R.layout.linechart_upperbar, null, false);
		lineChartView = (View) inflater.inflate(R.layout.linechart_main, null, false);
		lineChartFilter = (View) inflater.inflate(R.layout.linechart_filter, null, false);
		rotateLineChart = (ImageView) lineChartBar.findViewById(R.id.rotate_button);
	    calendarIcon = (ImageView) lineChartBar.findViewById(R.id.back_to_calendar);
	    toggle_linechart = (ImageView) lineChartBar.findViewById(R.id.toggle_linechart);
	    
	    addButton = (ImageView) view.findViewById(R.id.add_button);
		filterAll = (ImageView) lineChartFilter.findViewById(R.id.filter_all);
	    filter1 = (ImageView) lineChartFilter.findViewById(R.id.filter_1);
	    filter2 = (ImageView) lineChartFilter.findViewById(R.id.filter_2);
	    filter3 = (ImageView) lineChartFilter.findViewById(R.id.filter_3);
	    filter4 = (ImageView) lineChartFilter.findViewById(R.id.filter_4);
	    filter5 = (ImageView) lineChartFilter.findViewById(R.id.filter_5);
	    filter6 = (ImageView) lineChartFilter.findViewById(R.id.filter_6);
	    filter7 = (ImageView) lineChartFilter.findViewById(R.id.filter_7);
	    filter8 = (ImageView) lineChartFilter.findViewById(R.id.filter_8);
	    
	    filterAll.setOnClickListener(new FilterListener());
	    filter1.setOnClickListener(new FilterListener());
	    filter2.setOnClickListener(new FilterListener());
	    filter3.setOnClickListener(new FilterListener());
	    filter4.setOnClickListener(new FilterListener());
	    filter5.setOnClickListener(new FilterListener());
	    filter6.setOnClickListener(new FilterListener());
	    filter7.setOnClickListener(new FilterListener());
	    filter8.setOnClickListener(new FilterListener());	
		
		showDiary();
				
		drawer.toggle();
		
		setCurrentCalendarPage(THIS_MONTH + 1 - Database.START_MONTH);
		titleText.setText( (THIS_MONTH + 1)  + "月");
		
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
				
				lineChart = (LineChartView) view.findViewById(R.id.lineChart);
		        lineChart.requestLayout();
		        lineChart.getLayoutParams().width = 2200;
		        
		        chartTitle = (LineChartTitle) view.findViewById(R.id.chart_title);
		        chartTitle.setting(caller);
		        setChartType(2);
		        
		        chartAreaLayout = (LinearLayout) view.findViewById(R.id.linechart_tabs);
		        chartAreaLayout.setBackgroundResource(R.drawable.linechart_bg);
				
			}
		});
		
		calendarIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
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
				titleText.setText( (Database.START_MONTH + fragmentIdx) + "月");
			}
			
		});

		backToTodayText.setOnClickListener(new View.OnClickListener() { 
            @Override
            public void onClick(View v) {
                // sv.smoothScrollTo(0 , 270*(thisDay+4)-1350-900);
                
                View selectedView = mSectionsPagerAdapter.getSelectedView();
                View thisDayView = mSectionsPagerAdapter.getThisDayView();

                // Reset the last selected view
                if(selectedView != thisDayView){
                    int selectedPageMonth = Integer.valueOf(selectedView.getTag(SectionsPagerAdapter.TAG_CAL_CELL_PAGE_MONTH).toString());
                    int selectedMonth = Integer.valueOf(selectedView.getTag(SectionsPagerAdapter.TAG_CAL_CELL_MONTH).toString());
                    TextView selectedDayTextView = (TextView) selectedView.findViewById(R.id.tv_calendar_date);

                    if(selectedPageMonth == selectedMonth)  // If selected month is exactly current page month
                    	selectedDayTextView.setTextColor(Color.WHITE);
                    else
                    	selectedDayTextView.setTextColor(Color.BLACK);

                    // Set the new selected day
                    selectedView = thisDayView;
                    // This MUST be called. It modifies selectedView instance in mSectionPagerAdapter.
                    mSectionsPagerAdapter.asignSelecteViewToThisDayView();

                    TextView newSelectedDayTextView = (TextView) selectedView.findViewById(R.id.tv_calendar_date);
                    newSelectedDayTextView.setTextColor(Color.BLUE);
                }

                mViewPager.setCurrentItem(Calendar.getInstance().get(Calendar.MONTH) + 1 - Database.START_MONTH);
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
		
		
		notePage = new AddNoteDialog2(daybookFragment, fragment_layout);
		addButton.bringToFront();
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				notePage.initialize();
				notePage.show();
				addButton.setVisibility(View.INVISIBLE);
				fragment_layout.setEnabled(false);
			}
		});
		addButton.setOnTouchListener(new ScaleOnTouchListener());
		
		
		rotateLineChart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				if (isRotated) {
					MainActivity.getMainActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					isRotated = false;
				}
				else {

					MainActivity.getMainActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					isRotated = true;
				}
				
			}
		});
		
		lineChartFilterButton = (ImageView) lineChartBar.findViewById(R.id.line_chart_filter);
		calendarFilterButton = (ImageView) calendarBar.findViewById(R.id.calendar_filter);
		lineChartFilterButton.setOnClickListener(new FilterButtonListener());
		calendarFilterButton.setOnClickListener(new FilterButtonListener());
		MainActivity.getMainActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		
		return view;		
	}
	

	private float[] getRandomData() {
        return new float[] { 0, -3, 1, -2, -1, -3, 3, 2, 0, 1, -2, -1, 2, -2, 0, 1, -3, -1, 2, -1, 1, 3};
    }
	
	public void setChartType(int type) {
		chart_type = type;
		switch (chart_type) {
		case 0:
			chartTitle.setBackgroundResource(R.drawable.tab1_pressed);
			setFilterType(chart_type);
			break;
		case 1:
			chartTitle.setBackgroundResource(R.drawable.tab2_pressed);
			setFilterType(chart_type);
			break;
		case 2:
			chartTitle.setBackgroundResource(R.drawable.tab3_pressed);
			setFilterType(chart_type);
			break;		
		}
	}
	
		public void setFilterType(int type) {
		switch (type) {
		case 0: {
			//Log.i("OMG", "CASE0");
			if (isFilterOpen) {
				filter1.setVisibility(View.VISIBLE);
				filter2.setVisibility(View.VISIBLE);
				filter3.setVisibility(View.VISIBLE);
				filter4.setVisibility(View.VISIBLE);
				filter5.setVisibility(View.VISIBLE);
				filter6.setVisibility(View.GONE); filterButtonIsPressed[6] = false;
				filter7.setVisibility(View.GONE); filterButtonIsPressed[7] = false;
				filter8.setVisibility(View.GONE); filterButtonIsPressed[8] = false;
				filterView.setPadding(100, 0, 100, 0);
			}
			lineChartFilterButton.setVisibility(View.VISIBLE);
			lineChart.invalidate();
			break;
		}
		case 1: {
			//Log.i("OMG", "CASE1");
			if (isFilterOpen)  {
				filter1.setVisibility(View.GONE); filterButtonIsPressed[1] = false;
				filter2.setVisibility(View.GONE); filterButtonIsPressed[2] = false;
				filter3.setVisibility(View.GONE); filterButtonIsPressed[3] = false;
				filter4.setVisibility(View.GONE); filterButtonIsPressed[4] = false;
				filter5.setVisibility(View.GONE); filterButtonIsPressed[5] = false;
				filter6.setVisibility(View.VISIBLE);
				filter7.setVisibility(View.VISIBLE);
				filter8.setVisibility(View.VISIBLE);
				filterView.setPadding(100, 0, 100, 0);
			}
			lineChartFilterButton.setVisibility(View.VISIBLE);
			lineChart.invalidate();
			break;
		}
		
		case 2: {
			//Log.i("OMG", "CASE2");
			if (isFilterOpen) {
				LayoutParams lp = new LayoutParams(drawer.getLayoutParams());
				lp.height = drawerHeight;
				lp.addRule(RelativeLayout.BELOW, lineChartBar.getId());
				drawer.setLayoutParams(lp);
				
				drawerContent.removeAllViews();
				
				drawerContent.addView(lineChartView);
				isFilterOpen = false;
			}
			lineChartFilterButton.setVisibility(View.INVISIBLE);
			lineChart.invalidate();
			break;
		}
	  }
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		//setCurrentCalendarPage(selectedMonth + 1 - Database.START_MONTH);
		
		
		msgBox = new CheckResultDialog(fragment_layout);
		
		long curTime = System.currentTimeMillis();
		long testTime = PreferenceControl.getLatestTestCompleteTime();
		long pastTime = curTime - testTime;
		int note_state = PreferenceControl.getAfterTestState();
		
		if(PreferenceControl.getCheckResult() && pastTime < MainActivity.WAIT_RESULT_TIME){ //還沒察看結果且時間還沒到
		
		}
		else if(PreferenceControl.getCheckResult() && pastTime > MainActivity.WAIT_RESULT_TIME){//還沒察看結果且時間到了
			msgBox.initialize();
			msgBox.show();	
		}
		else{
			
		}
	}
	
	private void setFilterSize() {
		filterView = (View) view.findViewById(R.id.linechart_filter_area);
		filterView.requestLayout();
		if (isRotated) {
			filterView.getLayoutParams().height = filterHeightLandscape;
			//filterView.setPadding(10, 10, 10, 10);
		}
		else {
			filterView.getLayoutParams().height = filterHeight;
			//filterView.setPadding(30, 30, 30, 30);
		}
		
	}
	
	private void showDiary() {		
		diaryList = (LinearLayout) view.findViewById(R.id.item);
		diaryList.removeAllViews();
		
		NoteAdd[] noteAdds = db.getAllNoteAdd();
		if(noteAdds == null){
			return;
		}
		
		//Log.d(TAG, String.valueOf(noteAdds.length));
		
		LayoutInflater inflater = LayoutInflater.from(context);
		RelativeLayout lineView = (RelativeLayout)inflater.inflate(R.layout.white_line, null);
		RelativeLayout white_line = (RelativeLayout)lineView.findViewById(R.id.white_line);
		
		int last_day = 0;
		int last_timeslot = -1;
		if(noteAdds.length!=0){
			for(int i=0; i < noteAdds.length; i++){
				//LayoutInflater inflater = LayoutInflater.from(context);
				diaryItem = inflater.inflate(R.layout.diary_item, null);
				//LinearLayout layout = (LinearLayout)diaryItem.findViewById(R.id.diary_layout);
			
			TextView date_num = (TextView) diaryItem.findViewById(R.id.diary_date);
			TextView week_num = (TextView) diaryItem.findViewById(R.id.diary_week);
			TextView timeslot_num = (TextView) diaryItem.findViewById(R.id.diary_timeslot);
			ImageView type_img = (ImageView) diaryItem.findViewById(R.id.diary_image_type);
			TextView items_txt = (TextView) diaryItem.findViewById(R.id.diary_items);
			//TextView description_txt = (TextView) diaryItem.findViewById(R.id.diary_description);
			TextView impact_txt = (TextView) diaryItem.findViewById(R.id.diary_impact);
			
			int date = noteAdds[i].getRecordTv().getDay();
			int dayOfweek = noteAdds[i].getRecordTv().getDayOfWeek();
			int slot = noteAdds[i].getTimeSlot();
			int type = noteAdds[i].getType();
			int items = noteAdds[i].getItems();
			String descripton = noteAdds[i].getDescription();
			int impact = noteAdds[i].getImpact();
			
			if(type > 0 && type <=8)
				type_img.setImageResource(typeId[type]);
			
			
			date_num.setText(""+ date + "號");
			week_num.setText(dayOfWeek[ dayOfweek ]);
			timeslot_num.setText(timeslot[ slot ] );
			/*
			if(date!= last_day){
				date_num.setText(""+ date + "號");
				week_num.setText(dayOfWeek[ dayOfweek ]);
				timeslot_num.setText(timeslot[ slot ] );
				
			}
			if(date == last_day){
				if(slot!= last_timeslot){
					date_num.setText("");
					week_num.setText("");
					timeslot_num.setText(timeslot[ slot ] );
				}
				else{
					date_num.setText("");
					week_num.setText("");
					timeslot_num.setText("");
				}
			}*/
			items_txt.setText( dict.getItems(items) );
			//description_txt.setText(descripton);
			impact_txt.setText(String.valueOf(impact -4));
			
				
			last_day = date;
			last_timeslot = slot;
			Log.d(TAG, date+"號,星期"+dayOfweek+"時段"+slot+"項目"+items);
			
			diaryList.addView(diaryItem);
			//diaryList.addView(white_line);
			boxesLayout = (LinearLayout) view.findViewById(R.layout.diary_item);
			}
		}
		
	
		else{	//using dummy data
			for (int n = 1  ; n <=30 ; n++) {
				//LayoutInflater inflater = LayoutInflater.from(context);
				diaryItem = inflater.inflate(R.layout.diary_item, null);
			
				//sv_item_height = diaryItem.getMeasuredHeight();
				TextView date_num = (TextView) diaryItem.findViewById(R.id.diary_date);
				diaryList.addView(diaryItem);
				date_num.setText(Integer.toString(n) + "號");
			
				boxesLayout = (LinearLayout) view.findViewById(R.layout.diary_item);
		
			}
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
    
	    private class FilterButtonListener implements View.OnClickListener {
    	
    	@Override
    	public void onClick(View v) {
    		if (v.getId() == R.id.line_chart_filter) {
    			if (isFilterOpen == false) {				
					LayoutParams lp = new LayoutParams(drawer.getLayoutParams());
					//Log.i("OMG", "H: "+lp.height);
					lp.height = drawerHeightWithFilter;
					lp.addRule(RelativeLayout.BELOW, lineChartBar.getId());
					drawer.setLayoutParams(lp);
					
					drawerContent.removeAllViews();
					
					drawerContent.addView(lineChartFilter);
					isFilterOpen = true;
					setFilterSize();
					Log.i("OMG", "CASE: "+ chart_type);
					setFilterType(chart_type);
					drawerContent.addView(lineChartView);
					
				}
				else {
					LayoutParams lp = new LayoutParams(drawer.getLayoutParams());
					lp.height = drawerHeight;
					lp.addRule(RelativeLayout.BELOW, lineChartBar.getId());
					drawer.setLayoutParams(lp);
					
					drawerContent.removeAllViews();
					
					drawerContent.addView(lineChartView);
					isFilterOpen = false;
				}
    			
    		} else {
    			if (isFilterOpen == false) {				
					LayoutParams lp = new LayoutParams(drawer.getLayoutParams());
					//Log.i("OMG", "H: "+lp.height);
					lp.height = drawerHeightWithFilter;
					lp.addRule(RelativeLayout.BELOW, calendarBar.getId());
					drawer.setLayoutParams(lp);
					
					drawerContent.removeAllViews();
					
					drawerContent.addView(lineChartFilter);
					isFilterOpen = true;
					setFilterSize();
					drawerContent.addView(calendarView);
					
				}
				else {
					LayoutParams lp = new LayoutParams(drawer.getLayoutParams());
					lp.height = drawerHeight;
					lp.addRule(RelativeLayout.BELOW, calendarBar.getId());
					drawer.setLayoutParams(lp);
					
					drawerContent.removeAllViews();
					
					drawerContent.addView(calendarView);
					isFilterOpen = false;
				}
    		} 		
    	}    	
    }
    

    private class FilterListener implements View.OnClickListener {
    	

    	@Override
    	public void onClick(View v) {
    		switch (v.getId()) {
    		case (R.id.filter_all): {  
    		    filterButtonIsPressed[0] = true; 
    		    filterAll.setImageResource(R.drawable.filter_all_selected);
    			setAllButtonImage();
    			lineChart.invalidate();
    			break;
    		}
    		case (R.id.filter_1): {
    			if (filterButtonIsPressed[1]) { filterButtonIsPressed[1] = false; filter1.setImageResource(R.drawable.filter_color1); }
    			else {filterButtonIsPressed[1] = true; filter1.setImageResource(R.drawable.filter_color1_selected); filterButtonIsPressed[0] = false;}
    			setAllButtonImage();
    			lineChart.invalidate();
    			break;	
    		}
    		
    		case (R.id.filter_2): {
    			if (filterButtonIsPressed[2]) { filterButtonIsPressed[2] = false; filter2.setImageResource(R.drawable.filter_color2); }
    			else {filterButtonIsPressed[2] = true; filter2.setImageResource(R.drawable.filter_color2_selected); filterButtonIsPressed[0] = false;}
    			setAllButtonImage();
    			lineChart.invalidate();
    			break;
    			
    		}
    		
    		case (R.id.filter_3): {
    			if (filterButtonIsPressed[3]) { filterButtonIsPressed[3] = false; filter3.setImageResource(R.drawable.filter_color3);}
    			else {filterButtonIsPressed[3] = true; filter3.setImageResource(R.drawable.filter_color3_selected); filterButtonIsPressed[0] = false; }
    			setAllButtonImage();
    			lineChart.invalidate();
    			break;
    			
    		}
    		
    		case (R.id.filter_4): {
    			if (filterButtonIsPressed[4]) { filterButtonIsPressed[4] = false; filter4.setImageResource(R.drawable.filter_color4);}
    			else {filterButtonIsPressed[4] = true; filter4.setImageResource(R.drawable.filter_color4_selected); filterButtonIsPressed[0] = false;}
    			setAllButtonImage();
    			lineChart.invalidate();
    			break;
    			
    		}
    		case (R.id.filter_5): {
    			if (filterButtonIsPressed[5]) { filterButtonIsPressed[5] = false; filter5.setImageResource(R.drawable.filter_color5);}
    			else {filterButtonIsPressed[5] = true; filter5.setImageResource(R.drawable.filter_color5_selected); filterButtonIsPressed[0] = false;}
    			setAllButtonImage();
    			lineChart.invalidate();
    			break;    			
    		}
    		case (R.id.filter_6): {
    			if (filterButtonIsPressed[6]) { filterButtonIsPressed[6] = false; filter6.setImageResource(R.drawable.filter_color6);}
    			else {filterButtonIsPressed[6] = true; filter6.setImageResource(R.drawable.filter_color6_selected); filterButtonIsPressed[0] = false;}
    			setAllButtonImage();
    			lineChart.invalidate();
    			break;
    		}
    		case (R.id.filter_7): {
    			if (filterButtonIsPressed[7]) { filterButtonIsPressed[7] = false; filter7.setImageResource(R.drawable.filter_color7);}
    			else {filterButtonIsPressed[7] = true; filter7.setImageResource(R.drawable.filter_color7_selected); filterButtonIsPressed[0] = false;}
    			setAllButtonImage();
    			lineChart.invalidate();
    			break;
    		}
    		case (R.id.filter_8): {
    			if (filterButtonIsPressed[8]) { filterButtonIsPressed[8] = false; filter8.setImageResource(R.drawable.filter_color8);}
    			else {filterButtonIsPressed[8] = true; filter8.setImageResource(R.drawable.filter_color8_selected); filterButtonIsPressed[0] = false;}
    			setAllButtonImage();
    			lineChart.invalidate();
    			break;
    		}	
    		}
    	}
    }
    	








    	private void setAllButtonImage() {
    		if (filterButtonIsPressed[0]) {
    			filter1.setImageResource(R.drawable.filter_color1);
    			filter2.setImageResource(R.drawable.filter_color2);
    			filter3.setImageResource(R.drawable.filter_color3);
    			filter4.setImageResource(R.drawable.filter_color4);
    			filter5.setImageResource(R.drawable.filter_color5);
    			filter6.setImageResource(R.drawable.filter_color6);
    			filter7.setImageResource(R.drawable.filter_color7);
    			filter8.setImageResource(R.drawable.filter_color8);
    		}
    		else {
    			filterAll.setImageResource(R.drawable.filter_all);
    		}
    	}
    
    public void writeQuestionFile(int day, int timeslot, int type, int items, int impact, String description) {
    	
    	setStorage();
    	
		if( questionFile!= null )
			questionFile.write(day, timeslot, type, items, impact, description);
		
		if( TDP!= null ){
			//TDP.startAddNote();
			//TDP.getQuestionResult2(textFile)
			TDP.startAddNote3(day, timeslot, type, items, impact, description);
		}
	}
    
    private void setStorage() {
		File dir = MainStorage.getMainStorageDirectory();
		
		long timestamp = System.currentTimeMillis();
		
		mainDirectory = new File(dir, String.valueOf(0));
		if (!mainDirectory.exists())
			if (!mainDirectory.mkdirs()) {
				return;
			}
		
		
		TDP = new TestDataParser2(0); 
		//TDP.start();0
		
		
		questionFile = new QuestionFile(mainDirectory);
	}

    public static int getChartType () {
		return chart_type;
	}
    
    
	@Override
	public void resetView() {
		addButton.setVisibility(View.VISIBLE);
		fragment_layout.setEnabled(true);
		showDiary();
	}
		





}
