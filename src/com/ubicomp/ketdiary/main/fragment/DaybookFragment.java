package com.ubicomp.ketdiary.main.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.MainActivity;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.data.db.DatabaseControl;
import com.ubicomp.ketdiary.data.file.QuestionFile;
import com.ubicomp.ketdiary.data.file.TestDataParser2;
import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.daybook.LineChartData;
import com.ubicomp.ketdiary.daybook.SectionsPagerAdapter;
import com.ubicomp.ketdiary.daybook.linechart.ChartCaller;
import com.ubicomp.ketdiary.daybook.linechart.LineChartTitle;
import com.ubicomp.ketdiary.daybook.linechart.LineChartView;
import com.ubicomp.ketdiary.dialog.AddNoteDialog2;
import com.ubicomp.ketdiary.dialog.CheckResultDialog;
import com.ubicomp.ketdiary.dialog.MyDialog;
import com.ubicomp.ketdiary.dialog.QuestionCaller;
import com.ubicomp.ketdiary.dialog.QuestionDialog2;
import com.ubicomp.ketdiary.dialog.TestQuestionCaller2;
import com.ubicomp.ketdiary.noUse.NoteCatagory3;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.system.clicklog.ClickLog;
import com.ubicomp.ketdiary.system.clicklog.ClickLogId;
import com.ubicomp.ketdiary.ui.LoadingDialogControl;
import com.ubicomp.ketdiary.ui.ScaleOnTouchListener;
import com.ubicomp.ketdiary.ui.Typefaces;
//import android.view.ViewGroup.LayoutParams;

public class DaybookFragment extends Fragment implements ChartCaller, TestQuestionCaller2, QuestionCaller{
	
	public Activity activity = null;
	private DaybookFragment daybookFragment;
	private View view;
	
	private LoadingHandler loadHandler;
	
	private CheckResultDialog msgBox;
	private RelativeLayout fragment_layout;
	
	private UpdateDiaryHandler updateDiaryHandler;
	private UpdateCalendarHandler updateCalendarHandler;
	
	private static final String TAG = "DayBook";
	
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	public static final int TAG_PAGE_YEAR = R.string.TAG_PAGE_YEAR;
	public static final int TAG_PAGE_MONTH = R.string.TAG_PAGE_MONTH;
	private static LinearLayout diaryList;
	private LinearLayout boxesLayout, drawerContent, caltoggleLayout, charttoggleLayout;
	private RelativeLayout upperBarContent;
	private TextView titleText, backToTodayText, linechart_bar_month;
	
	private View diaryItem;//old
	
	private View[] diaryItem2;
	private static ScrollView sv;
	private HorizontalScrollView sv_linechart;
	private int filter_count = 0;
	private AnimationDrawable animation;

	@SuppressWarnings("deprecation")
	private SlidingDrawer drawer;
	private ImageView toggle, toggle_linechart, linechartIcon, calendarIcon;
	private static Context context = App.getContext();
		
	private static int sv_item_height;
	private static Typeface wordTypefaceBold = Typefaces.getWordTypefaceBold();
	private static Typeface wordTypeface = Typefaces.getWordTypeface();
	private static Typeface digitTypefaceBold = Typefaces.getDigitTypefaceBold();
	private static Typeface digitTypeface = Typefaces.getDigitTypeface();
	
	private ArrayList<Integer> diary = new ArrayList<Integer>();
	//file
	private QuestionFile questionFile;
	private File mainDirectory = null;
	private TestDataParser2 TDP;
	
	private View[] pageViewList = null;
	private MyDialog dialog;
	private LoadDiaryTask updateTask;
	
	private static final int THIS_MONTH = Calendar.getInstance().get(Calendar.MONTH);
	
	public static int chart_type = 2; // 0:自我狀態 1:人際互動 3:綜合分析
	public static final int TAG_changedot = -1;
	private static final int TAG_LIST_YEAR = R.string.TAG_LIST_YEAR;
	private static final int TAG_LIST_MONTH = R.string.TAG_LIST_MONTH;
	private static final int TAG_LIST_DAY = R.string.TAG_LIST_DAY;
	
	private static QuestionDialog2 questionBox;
	
	private LinearLayout chartAreaLayout;
	private LineChartView lineChart;
	private LineChartTitle chartTitle;
	private ChartCaller caller;
	
	public View lineChartBar, lineChartView, lineChartFilter, calendarBar, calendarView, filterView;
	
	public ImageView addButton, randomButton;
	
	public AddNoteDialog2 notePage = null;
	public boolean isNotePageShow = false;
	private boolean isContentAdd = true;
	private boolean isFilterOpen = false;
	private boolean isRotated = false;
	
	private static NoteAdd[] noteAdds = null;
	private DatabaseControl db;
	private NoteCatagory3 dict;
	private static final String[] dayOfWeek = {" ", "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
	private static final String[] timeslot = {"上午", "下午", "晚上"};
	private static final String[] monthName = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
	
	private LineChartData[] dataset = null;
	private static final int sustainMonth = PreferenceControl.getSustainMonth();
	private Calendar startDay = PreferenceControl.getStartDate();
	private int startYear = startDay.get(Calendar.YEAR);
	private int startMonth = startDay.get(Calendar.MONTH) + 1;
	private int currentPageIdx = sustainMonth - 1;
	
	private static final int[] iconId = {0, R.drawable.type_icon1, R.drawable.type_icon2, R.drawable.type_icon3,
		R.drawable.type_icon4, R.drawable.type_icon5, R.drawable.type_icon6, R.drawable.type_icon7,
		R.drawable.type_icon8};
	
	private final static int[] typeId = {0, R.drawable.book_type1,
		R.drawable.book_type2, R.drawable.book_type3, R.drawable.book_type4, 
		R.drawable.book_type5, 	R.drawable.book_type6, R.drawable.book_type7, 
	 	R.drawable.book_type8};
	
	//public static List<Integer> filterList = new ArrayList<Integer>();

	private ImageView filterAll, filter1, filter2, filter3, filter4, filter5, filter6, filter7, filter8;
	public ImageView lineChartFilterButton, calendarFilterButton, rotateLineChart;
	
	public static boolean[] filterButtonIsPressed = {true, false, false, false, false, false, false, false, false};
	//private ImageView[] filterButtonArray = {filterAll, filter1, filter2, filter3, filter4, filter5, filter6, filter7, filter8};
	
	private static Resources resources = context.getResources();
	private int drawerHeight = resources.getDimensionPixelSize(R.dimen.drawer_normal_height);
	private int drawerHeightWithFilter = resources.getDimensionPixelSize(R.dimen.drawer_with_filter_height);
	private int filterHeight = resources.getDimensionPixelSize(R.dimen.filter_normal_height);
	private int filterHeightLandscape = resources.getDimensionPixelSize(R.dimen.filter_landscape_height);
	
	private static final int CALENDAR_PAGE = 1;
	private static final int LINECHART_PAGE = 2;
	private int frontState;
		
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		activity = MainActivity.getMainActivity();
		db = new DatabaseControl();
		dict = new NoteCatagory3();
		caller = this;
		daybookFragment = this;
		
		
		updateDiaryHandler = new UpdateDiaryHandler();
		updateCalendarHandler = new UpdateCalendarHandler();
	}
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		view = inflater.inflate(R.layout.fragment_mydaybook, container, false);
		
		fragment_layout = (RelativeLayout) view.findViewById(R.id.mydaybook_layout);
		
		drawerContent = (LinearLayout) view.findViewById(R.id.drawer_content);
		upperBarContent = (RelativeLayout) view.findViewById(R.id.upper_bar);
		
		diaryList = (LinearLayout) view.findViewById(R.id.item);
		
		sv = (ScrollView)view.findViewById(R.id.diary_view);
		
		//LayoutInflater inflater = LayoutInflater.from(context);
		calendarView = (View) inflater.inflate(R.layout.calendar_main, null);
		calendarBar = (View) inflater.inflate(R.layout.calendar_upperbar, null);
		
		drawerContent.addView(calendarView);
		upperBarContent.addView(calendarBar);
		
		loadHandler = new LoadingHandler();
		//MainActivity.getMainActivity().setClickable(false);
		
		//calendarBar.setEnabled(false);

		// Set up the ViewPager with the sections adapter.
		pageViewList = new View[sustainMonth];
		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.set(startYear, startMonth - 1, 1);
		for (int i = 0; i < sustainMonth; i++) {
			pageViewList[i] = (View) inflater.inflate(R.layout.fragment_calendar, null);
			
			pageViewList[i].setTag(TAG_PAGE_YEAR, tempCalendar.get(Calendar.YEAR));
			pageViewList[i].setTag(TAG_PAGE_MONTH, tempCalendar.get(Calendar.MONTH));
			tempCalendar.add(Calendar.MONTH, 1);
		}
		mSectionsPagerAdapter = new SectionsPagerAdapter(pageViewList);

		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);	
		
		
		
		backToTodayText = (TextView) view.findViewById(R.id.back_to_today);
		backToTodayText.setText(Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)));
		
		caltoggleLayout = (LinearLayout) view.findViewById(R.id.cal_toggle_layout);
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
	    charttoggleLayout = (LinearLayout) lineChartBar.findViewById(R.id.toggle_layout);
	    linechart_bar_month = (TextView)lineChartBar.findViewById(R.id.month_text_chart);
	    
	    addButton = (ImageView) view.findViewById(R.id.add_button);
	    
	    randomButton = (ImageView) view.findViewById(R.id.random_question);
	    animation = (AnimationDrawable) randomButton.getDrawable();
	    
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
			
		filterAll.setOnLongClickListener(new FilterLongClickListener());
		filter1.setOnLongClickListener(new FilterLongClickListener());
		filter2.setOnLongClickListener(new FilterLongClickListener());
		filter3.setOnLongClickListener(new FilterLongClickListener());
		filter4.setOnLongClickListener(new FilterLongClickListener());
		filter5.setOnLongClickListener(new FilterLongClickListener());
		filter6.setOnLongClickListener(new FilterLongClickListener());
		filter7.setOnLongClickListener(new FilterLongClickListener());
		filter8.setOnLongClickListener(new FilterLongClickListener());
	    //updateDiaryHandler.sendEmptyMessage(0);//showDiary();
				
		drawer.toggle();
		
		mViewPager.setCurrentItem(THIS_MONTH + 1 - startMonth);
		titleText.setText( (THIS_MONTH + 1)  + "月");
		linechart_bar_month.setText( (THIS_MONTH + 1)  + "月");
		//titleText.setTypeface(wordTypefaceBold);
		
		charttoggleLayout.setOnClickListener(new ToggleListener() );
		caltoggleLayout.setOnClickListener(new ToggleListener() );
		//toggle_linechart.setOnClickListener(new ToggleListener());
		toggle.setOnClickListener(new ToggleListener());
		//titleText.setOnClickListener(new ToggleListener());
		sv.getViewTreeObserver().addOnScrollChangedListener(new OnScrollChangedListener(){
			
			private int last_scrollY = 0;
			@Override
			public void onScrollChanged() {
				
				int scrollY = sv.getScrollY(); //for horizontalScrollView
				if(scrollY == last_scrollY){
					return;
				}
				int index = scrollY/(int)convertDpToPixel(125);
				
				int allNum = diaryList.getChildCount();
				int j=0;
				for(int i=0; i<allNum; i++){
					if(diaryList.getChildAt(i).getVisibility() == View.GONE)
						continue;
					j++;
					if(j == index){
						int month = (Integer)diaryList.getChildAt(i).getTag(TAG_LIST_MONTH);
						Log.i(TAG, "Month: " + (month+1));
						titleText.setText( (month + 1)  + "月");
						linechart_bar_month.setText( (month + 1)  + "月");
					}	
				}				
				last_scrollY = scrollY;
				Log.i(TAG, "Scroll Y: "+ scrollY);
			}
			
		});
		
		//for ( int i = 0; i < 9; i++ ) { filterList.add(i);} 
		
		
		
		lineChart = (LineChartView) lineChartView.findViewById(R.id.lineChart);
		lineChart.setWidth();
		sv_linechart = (HorizontalScrollView) lineChartView.findViewById(R.id.line_chart_scroll);
		sv_linechart.getViewTreeObserver().addOnScrollChangedListener(new OnScrollChangedListener(){
			
			private int last_scrollX = 0 ;
			@Override
			public void onScrollChanged() {
				
				int scrollX = sv_linechart.getScrollX(); //for horizontalScrollView
				if(scrollX == last_scrollX){
					return;
				}
				dataset = lineChart.getLineChartData();
				int pos = lineChart.getCursorPos2(scrollX);
				if(pos > 0 && pos < lineChart.numOfDays){
					if(dataset!=null){
						linechart_bar_month.setText( (dataset[pos].getMonth()+1)  + "月");
					}
				}
				last_scrollX = scrollX;
				Log.i(TAG, "Scroll X: "+ scrollX);
			}
			
		});
//        lineChart.requestLayout();
//        lineChart.getLayoutParams().width = 2200;
        
        chartTitle = (LineChartTitle) lineChartView.findViewById(R.id.chart_title);
        chartTitle.setting(caller);
        chartAreaLayout = (LinearLayout) lineChartView.findViewById(R.id.linechart_tabs);
        chartAreaLayout.setBackgroundResource(R.drawable.linechart_bg);
	
        
        
		linechartIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				ClickLog.Log(ClickLogId.DAYBOOK_CHART);

				if (isFilterOpen == false) {				
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
				if  (!drawer.isOpened()) {
					drawer.toggle();
					setArrow(true);
				}
								
		        
		        isContentAdd = true;
		        setChartType(2);
		                
		        if(rotateLineChart!=null && isContentAdd)
					rotateLineChart.setVisibility(View.VISIBLE);
			}
		});
		
		calendarIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				ClickLog.Log(ClickLogId.DAYBOOK_CALENDAR);
								
				if (isFilterOpen == true) {				
					LayoutParams lp = new LayoutParams(drawer.getLayoutParams());
					//Log.i("OMG", "H: "+lp.height);
					lp.height = drawerHeightWithFilter;
					lp.addRule(RelativeLayout.BELOW, calendarBar.getId());
					drawer.setLayoutParams(lp);
					
					drawerContent.removeAllViews();
					
					drawerContent.addView(lineChartFilter);
					
					setFilterSize();
					setFilterType(3);
					drawerContent.addView(calendarView);
					
				}
				else {
					LayoutParams lp = new LayoutParams(drawer.getLayoutParams());
					lp.height = drawerHeight;
					lp.addRule(RelativeLayout.BELOW, calendarBar.getId());
					drawer.setLayoutParams(lp);
					
					drawerContent.removeAllViews();
					
					drawerContent.addView(calendarView);
					
				}
				if  (!drawer.isOpened())
					drawer.toggle();
				//addDrawerContent(R.id.cal_toggle_layout);
				upperBarContent.removeAllViews();
				upperBarContent.addView(calendarBar);
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
				ClickLog.Log(ClickLogId.DAYBOOK_CHANGE_MONTH);
				currentPageIdx = arg0;
				int month = (startMonth + currentPageIdx)%12;
				if(month == 0)
					titleText.setText("12月");
				else
					titleText.setText( month + "月");
				
//				updateTask = new LoadDiaryTask();
//				updateTask.execute(startMonth + currentPageIdx-1);
				
				//updateDiaryHandler.sendEmptyMessage(startMonth + currentPageIdx-1);
			}
			
		});

		backToTodayText.setOnClickListener(new View.OnClickListener() { 
            @Override
            public void onClick(View v) {
            	
            	ClickLog.Log(ClickLogId.DAYBOOK_TODAY);
            	
            	sv.fullScroll(View.FOCUS_DOWN);

                View selectedView = mSectionsPagerAdapter.getSelectedView();
                View thisDayView = mSectionsPagerAdapter.getThisDayView();

                // Reset the last selected view
                if(selectedView != thisDayView){
                    int selectedPageMonth = Integer.valueOf(selectedView.getTag(SectionsPagerAdapter.TAG_CAL_CELL_PAGE_MONTH).toString());
                    int selectedMonth = Integer.valueOf(selectedView.getTag(SectionsPagerAdapter.TAG_CAL_CELL_MONTH).toString());
                    TextView selectedDayTextView = (TextView) selectedView.findViewById(R.id.tv_calendar_date);
                    selectedDayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    ImageView selectedDayIV = (ImageView) selectedView.findViewById(R.id.iv_date_result);
                    int changedot = (Integer) selectedView.getTag(TAG_changedot);
                     // last time dot
                    if(selectedPageMonth == selectedMonth){  // If selected month is exactly current page month
                    	selectedDayTextView.setTextColor(context.getResources().getColor(R.color.white));
                    	if(changedot == 0)
                        	selectedDayIV.setImageResource(R.drawable.bigbluedot);
                        else if (changedot == 1)
                        	selectedDayIV.setImageResource(R.drawable.bigreddot);
                        else
                        	selectedDayIV.setImageResource(R.drawable.biggraydot);
                    }
                    else{
                    	if(changedot == 0)
                        	selectedDayIV.setImageResource(R.drawable.bigbluedot2);
                        else if (changedot == 1)
                        	selectedDayIV.setImageResource(R.drawable.bigreddot2);
                        else
                        	selectedDayIV.setImageResource(R.drawable.biggraydot2);
                    	selectedDayTextView.setTextColor(Color.BLACK);
                    }
                    
                    // Set the new selected day
                    selectedView = thisDayView;
                    
                    ImageView selectedDayIV2 = (ImageView) selectedView.findViewById(R.id.iv_date_result);
                    int changedot2 = (Integer) selectedView.getTag(TAG_changedot);
                    if(changedot2 == 0)
                    	selectedDayIV2.setImageResource(R.drawable.bigbluedot2);
                    else if (changedot2 == 1)
                    	selectedDayIV2.setImageResource(R.drawable.bigreddot2);
                    else
                    	selectedDayIV2.setImageResource(R.drawable.biggraydot2);
                    // This MUST be called. It modifies selectedView instance in mSectionPagerAdapter.
                    mSectionsPagerAdapter.asignSelecteViewToThisDayView();                   
                    
                    TextView newSelectedDayTextView = (TextView) selectedView.findViewById(R.id.tv_calendar_date);
                    newSelectedDayTextView.setTextColor(context.getResources().getColor(R.color.black));
                    newSelectedDayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                }

                mViewPager.setCurrentItem(sustainMonth - 1);
            }
        });


		lineChartFilterButton = (ImageView) lineChartBar.findViewById(R.id.line_chart_filter);
			
		notePage = new AddNoteDialog2(daybookFragment, fragment_layout);
		addButton.bringToFront();
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				ClickLog.Log(ClickLogId.DAYBOOK_ADDNOTE);
				
				//diaryList.removeAllViews();
				//mViewPager.removeAllViews();
				
				notePage.initialize();
				notePage.show();
				isNotePageShow = true;
				addButton.setVisibility(View.INVISIBLE);
				fragment_layout.setEnabled(false);
			}
		});
		addButton.setOnTouchListener(new ScaleOnTouchListener());
		
//		if(!db.randomQuestionDone()){
//			Random rand = new Random();
//			int prob = rand.nextInt(100);
//			if(prob >= 50 ){
//				randomButton.setVisibility(View.VISIBLE);
//				randomButton.setImageResource(R.anim.animation_random_question);
//				animation = (AnimationDrawable) randomButton.getDrawable();
//				animation.start();
//			}
//			
//			randomButton.bringToFront();
//			randomButton.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					ClickLog.Log(ClickLogId.DAYBOOK_RANDOMTEST);
//					
//					questionBox.show(1);
//					randomButton.setVisibility(View.GONE);
//				}
//			});
//			randomButton.setOnTouchListener(new ScaleOnTouchListener());
//		}
		if(PreferenceControl.getRandomQustion()){
		//if(true){ //for debug
			randomButton.setVisibility(View.VISIBLE);
			randomButton.setImageResource(R.anim.animation_random_question);
			animation = (AnimationDrawable) randomButton.getDrawable();
			animation.start();
			
			randomButton.bringToFront();
			randomButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ClickLog.Log(ClickLogId.DAYBOOK_RANDOMTEST);
				
					questionBox.show(1);
					//randomButton.setVisibility(View.GONE);
				}
			});
			randomButton.setOnTouchListener(new ScaleOnTouchListener());
						
		}
	
		rotateLineChart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				ClickLog.Log(ClickLogId.DAYBOOK_CHART_ROTATE);
				
				if (isRotated) {
					MainActivity.getMainActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					MainActivity.getMainActivity().setTabHostVisible(View.VISIBLE);
					addButton.setVisibility(View.VISIBLE);
					calendarIcon.setVisibility(View.VISIBLE);
					toggle_linechart.setVisibility(View.VISIBLE);
					charttoggleLayout.setOnClickListener(new ToggleListener() );
					diaryList.setVisibility(View.VISIBLE);
					isRotated = false;
					
					if(PreferenceControl.getRandomQustion())
						randomButton.setVisibility(View.VISIBLE);
					
				}
				else {

					MainActivity.getMainActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					MainActivity.getMainActivity().setTabHostVisible(View.GONE);
					randomButton.setVisibility(View.INVISIBLE);
					addButton.setVisibility(View.INVISIBLE);
					calendarIcon.setVisibility(View.INVISIBLE);
					toggle_linechart.setVisibility(View.INVISIBLE);
					charttoggleLayout.setOnClickListener( null );
					diaryList.setVisibility(View.INVISIBLE);
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
	
	private void setArrow(boolean open){
		if(open){
			toggle.setImageResource(R.drawable.dropup_arrow);
			toggle_linechart.setImageResource(R.drawable.dropup_arrow);
			
			if(rotateLineChart!=null && isContentAdd)
				rotateLineChart.setVisibility(View.VISIBLE);
		}
		else{
			toggle.setImageResource(R.drawable.dropdown_arrow);
			toggle_linechart.setImageResource(R.drawable.dropdown_arrow);
			
			if(rotateLineChart!=null && isContentAdd)
				rotateLineChart.setVisibility(View.INVISIBLE);
		}
		
	}
	
	
	@SuppressLint("HandlerLeak")
	private class LoadingHandler extends Handler {
		public void handleMessage(Message msg) {
			MainActivity.getMainActivity().enableTabAndClick(false);
			
			
			//updateDiaryHandler.sendEmptyMessage(0);//showDiary();			
			
			if(sustainMonth >= 1){
				updateTask = new LoadDiaryTask();
				updateTask.execute(Calendar.getInstance().get(Calendar.MONTH));
			}

			
			updateCalendarHandler.sendEmptyMessage(currentPageIdx);//updateCalendarView(currentPageIdx);
			updateFilterButton();
			
			 
			
			questionBox = new QuestionDialog2((RelativeLayout) view, daybookFragment);
			questionBox.initialize();

			MainActivity.getMainActivity().enableTabAndClick(true);
			LoadingDialogControl.dismiss();
		}
	}
	
	private void updateFilterButton(){
		if(!filterButtonIsPressed[0]){
			lineChartFilterButton.setImageResource(R.drawable.filter1_color);
			calendarFilterButton.setImageResource(R.drawable.filter1_color);
		}
		else{
			lineChartFilterButton.setImageResource(R.drawable.button_filter);
			calendarFilterButton.setImageResource(R.drawable.button_filter);
		}
	}
	

	public void setChartType(int type) {
		chart_type = type;
		switch (chart_type) {
		case 0:
			ClickLog.Log(ClickLogId.DAYBOOK_CHART_TYPE0);
			chartTitle.setBackgroundResource(R.drawable.tab1_pressed);
			setFilterType(chart_type);
			break;
		case 1:
			ClickLog.Log(ClickLogId.DAYBOOK_CHART_TYPE1);
			chartTitle.setBackgroundResource(R.drawable.tab2_pressed);
			setFilterType(chart_type);
			break;
		case 2:
			ClickLog.Log(ClickLogId.DAYBOOK_CHART_TYPE2);
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
				filter6.setVisibility(View.GONE); //filterButtonIsPressed[6] = false; 
				filter7.setVisibility(View.GONE); //filterButtonIsPressed[7] = false; 
				filter8.setVisibility(View.GONE); //filterButtonIsPressed[8] = false; 
				filterView.setPadding(100, 0, 100, 0);
			}
			lineChartFilterButton.setVisibility(View.VISIBLE);
			lineChart.invalidate();
			break;
		}
		case 1: {
			//Log.i("OMG", "CASE1");
			if (isFilterOpen)  {
				filter1.setVisibility(View.GONE); //filterButtonIsPressed[1] = false; 
				filter2.setVisibility(View.GONE); //filterButtonIsPressed[2] = false; 
				filter3.setVisibility(View.GONE); //filterButtonIsPressed[3] = false; 
				filter4.setVisibility(View.GONE); //filterButtonIsPressed[4] = false; 
				filter5.setVisibility(View.GONE); //filterButtonIsPressed[5] = false; 
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
//			if (isFilterOpen)  {
//				filter1.setVisibility(View.VISIBLE); 
//				filter2.setVisibility(View.VISIBLE);
//				filter3.setVisibility(View.VISIBLE); 
//				filter4.setVisibility(View.VISIBLE); 
//				filter5.setVisibility(View.VISIBLE); 
//				filter6.setVisibility(View.VISIBLE);
//				filter7.setVisibility(View.VISIBLE);
//				filter8.setVisibility(View.VISIBLE);
//				filterView.setPadding(10, 0, 10, 0);	
//			}
//			lineChartFilterButton.setVisibility(View.VISIBLE);
//			break;
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
		case 3: {
			//Log.i("OMG", "CASE2");
			if (isFilterOpen)  {
				filter1.setVisibility(View.VISIBLE); 
				filter2.setVisibility(View.VISIBLE);
				filter3.setVisibility(View.VISIBLE); 
				filter4.setVisibility(View.VISIBLE); 
				filter5.setVisibility(View.VISIBLE); 
				filter6.setVisibility(View.VISIBLE);
				filter7.setVisibility(View.VISIBLE);
				filter8.setVisibility(View.VISIBLE);
				filterView.setPadding(10, 0, 10, 0);	
			}
			lineChartFilterButton.setVisibility(View.VISIBLE);
			break;
		 
		}
	  }
	}

	
	@Override
	public void onPause(){
		ClickLog.Log(ClickLogId.DAYBOOK_LEAVE);
		
		diaryList.removeAllViews();
		updateDiaryHandler.removeMessages(0);
		
		
		super.onPause();
		//TODO: release some resource
	}
	
	@Override
	public void onResume() {
		super.onResume();
		ClickLog.Log(ClickLogId.DAYBOOK_ENTER);
		filter_count = 0;
		isFilterOpen = false;
		
		for(int i=0; i<filterButtonIsPressed.length; i++)
			filterButtonIsPressed[i] = false;
		filterButtonIsPressed[0] = true;
		
		//setCurrentCalendarPage(selectedMonth + 1 - Database.START_MONTH);
		Log.d(TAG, "StartMonth: "+startMonth + "SustainMonth: "+ sustainMonth);
		sv.fullScroll(View.FOCUS_DOWN);
		
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
		loadHandler.sendEmptyMessage(0);
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
	
	private class LoadDiaryTask extends AsyncTask<Integer, Void, Void> {//TODO

		@Override
		protected Void doInBackground(Integer... month) {
						
			showDiary(month[0]);	
			return null;
		}
		
//		@Override
//	    protected void onPreExecute() {
//	        super.onPreExecute();
//	        
//	        
//	    }

		@Override
		protected void onPostExecute(Void result) {		
			addDairy();
			updateDiaryView();
			sv.fullScroll(View.FOCUS_DOWN);
		}
	}
	
	private void showDiary(int title_month) {		
			
		noteAdds = db.getAllNoteAdd();
		if(noteAdds == null){
			return;
		}
		
		//Log.d(TAG, String.valueOf(noteAdds.length));
		
		LayoutInflater inflater = LayoutInflater.from(context);
		//RelativeLayout lineView = (RelativeLayout)inflater.inflate(R.layout.white_line, null);
		//RelativeLayout white_line = (RelativeLayout)lineView.findViewById(R.id.white_line);
		
		int last_day = 0;
		int last_timeslot = -1;
		int last_result = -1;
		
		
		if(noteAdds.length!=0){
			diaryItem2 = new View[noteAdds.length];
			for(int i=0; i < noteAdds.length; i++){
				int type = noteAdds[i].getType();
				diary.add(type);
//				if(type > 0 && type <=8){
//					if(!filterButtonIsPressed[type] && !filterButtonIsPressed[0])
//						continue;
//				}
				
				int date = noteAdds[i].getRecordTv().getDay();
				int month = noteAdds[i].getRecordTv().getMonth();
				
//				if(month != title_month){
//					continue;
//				}
				int year = noteAdds[i].getRecordTv().getYear();
				//LayoutInflater inflater = LayoutInflater.from(context);
				diaryItem2[i] = inflater.inflate(R.layout.diary_item, null);
				LinearLayout layout = (LinearLayout)diaryItem2[i].findViewById(R.id.diary_layout);
			
				TextView date_num = (TextView) diaryItem2[i].findViewById(R.id.diary_date);
				TextView week_num = (TextView) diaryItem2[i].findViewById(R.id.diary_week);
				TextView timeslot_num = (TextView) diaryItem2[i].findViewById(R.id.diary_timeslot);
				ImageView type_img = (ImageView) diaryItem2[i].findViewById(R.id.diary_image_type);
				TextView items_txt = (TextView) diaryItem2[i].findViewById(R.id.diary_items);
				//TextView description_txt = (TextView) diaryItem.findViewById(R.id.diary_description);
				TextView impact_word = (TextView) diaryItem2[i].findViewById(R.id.diary_impact_word);
				TextView impact_txt = (TextView) diaryItem2[i].findViewById(R.id.diary_impact);
				
				diaryItem2[i].setTag(TAG_LIST_YEAR, year);
				diaryItem2[i].setTag(TAG_LIST_MONTH, month);
				diaryItem2[i].setTag(TAG_LIST_DAY, date);
				
				date_num.setTypeface(wordTypefaceBold);
				week_num.setTypeface(wordTypefaceBold);
				timeslot_num.setTypeface(wordTypefaceBold);
				items_txt.setTypeface(wordTypefaceBold);
				impact_word.setTypeface(wordTypefaceBold);
				impact_txt.setTypeface(wordTypefaceBold);
				
				int result = last_result;
				if(date != last_day){
					TestResult testResult = 
							db.getDayTestResult( year, month, date );
        	
					if(testResult.getTv().getTimestamp() != 0){
						result = testResult.getResult();
					}
					else
						result = -1;
						
				}
				diaryItem2[i].setTag(TAG_changedot, result);	
				if(result == 0)
					layout.setBackgroundResource(R.drawable.diary_pass);
				else if(result == 1){
					layout.setBackgroundResource(R.drawable.diary_nopass);
				}
				else{
					layout.setBackgroundResource(R.drawable.diary_notest);
				}	
			
			int dayOfweek = noteAdds[i].getRecordTv().getDayOfWeek();
			int slot = noteAdds[i].getTimeSlot();
			type = noteAdds[i].getType();
			int items = noteAdds[i].getItems();
			String descripton = noteAdds[i].getDescription();
			int impact = noteAdds[i].getImpact();
			

			
			//type_img.setOnLongClickListener(new TypeLongClickListener(date, dayOfweek, slot, type, items,	impact, descripton));
			layout.setOnLongClickListener(new TypeLongClickListener(month, date, dayOfweek, slot, type, items, 
					impact, descripton));
			
			if(type > 0 && type <=8)
				type_img.setImageResource(typeId[type]);
			
			
			date_num.setText((month+1)+"月"+ date + "號");
			week_num.setText(dayOfWeek[ dayOfweek ]);
			timeslot_num.setText(timeslot[ slot ] );

			items_txt.setText( dict.dictionary.get(items) );
			//description_txt.setText(descripton);
			if(impact-3 <=0)
				impact_txt.setText(String.valueOf(impact -3));
			else
				impact_txt.setText("+" + String.valueOf(impact -3));
			
			last_result = result;
			last_day = date;
			last_timeslot = slot;
			//Log.d(TAG, date+"號,星期"+dayOfweek+"時段"+slot+"項目"+items);
			
			//diaryList.addView(diaryItem);

			}					
		}			
	}
	private void addDairy(){
		diaryList.removeAllViews();
		if(diaryItem2!=null){
			for(int i=0; i<diaryItem2.length; i++){
				if(diaryItem2[i]!=null)
					diaryList.addView(diaryItem2[i]);
			}
		}
		sv.fullScroll(View.FOCUS_DOWN);
	}
	
	
	private class UpdateDiaryHandler extends Handler{
		
		public void handleMessage(Message msg) {
			
			updateTask = new LoadDiaryTask();
			updateTask.execute(Calendar.getInstance().get(Calendar.MONTH));
			
			
//			if(msg.what == 0){
//				showDiary();
//			}
//			else{
//				showDiary(msg);
//			}
		}
		private void showDiary(Message msg) {		
			
			diaryList.removeAllViews();
			
			noteAdds = db.getAllNoteAdd();
			if(noteAdds == null){
				return;
			}
			
			//Log.d(TAG, String.valueOf(noteAdds.length));
			
			LayoutInflater inflater = LayoutInflater.from(context);
			//RelativeLayout lineView = (RelativeLayout)inflater.inflate(R.layout.white_line, null);
			//RelativeLayout white_line = (RelativeLayout)lineView.findViewById(R.id.white_line);
			
			int last_day = 0;
			int last_timeslot = -1;
			int last_result = -1;
			
			
			if(noteAdds.length!=0){
				diaryItem2 = new View[noteAdds.length];
				for(int i=0; i < noteAdds.length; i++){
					int type = noteAdds[i].getType();
					diary.add(type);
//					if(type > 0 && type <=8){
//						if(!filterButtonIsPressed[type] && !filterButtonIsPressed[0])
//							continue;
//					}
					
					int date = noteAdds[i].getRecordTv().getDay();
					int month = noteAdds[i].getRecordTv().getMonth();
					if(month != msg.what){
						continue;
					}
					int year = noteAdds[i].getRecordTv().getYear();
					//LayoutInflater inflater = LayoutInflater.from(context);
					diaryItem2[i] = inflater.inflate(R.layout.diary_item, null);
					LinearLayout layout = (LinearLayout)diaryItem2[i].findViewById(R.id.diary_layout);
				
					TextView date_num = (TextView) diaryItem2[i].findViewById(R.id.diary_date);
					TextView week_num = (TextView) diaryItem2[i].findViewById(R.id.diary_week);
					TextView timeslot_num = (TextView) diaryItem2[i].findViewById(R.id.diary_timeslot);
					ImageView type_img = (ImageView) diaryItem2[i].findViewById(R.id.diary_image_type);
					TextView items_txt = (TextView) diaryItem2[i].findViewById(R.id.diary_items);
					//TextView description_txt = (TextView) diaryItem.findViewById(R.id.diary_description);
					TextView impact_word = (TextView) diaryItem2[i].findViewById(R.id.diary_impact_word);
					TextView impact_txt = (TextView) diaryItem2[i].findViewById(R.id.diary_impact);
					
					date_num.setTypeface(wordTypefaceBold);
					week_num.setTypeface(wordTypefaceBold);
					timeslot_num.setTypeface(wordTypefaceBold);
					items_txt.setTypeface(wordTypefaceBold);
					impact_word.setTypeface(wordTypefaceBold);
					impact_txt.setTypeface(wordTypefaceBold);
					
					int result = last_result;
					if(date != last_day){
						TestResult testResult = 
								db.getDayTestResult( year, month, date );
	        	
						if(testResult.getTv().getTimestamp() != 0){
							result = testResult.getResult();
						}
						else
							result = -1;
							
					}
					diaryItem2[i].setTag(TAG_changedot, result);	
					if(result == 0)
						layout.setBackgroundResource(R.drawable.diary_pass);
					else if(result == 1){
						layout.setBackgroundResource(R.drawable.diary_nopass);
					}
					else{
						layout.setBackgroundResource(R.drawable.diary_notest);
					}	
				
				int dayOfweek = noteAdds[i].getRecordTv().getDayOfWeek();
				int slot = noteAdds[i].getTimeSlot();
				type = noteAdds[i].getType();
				int items = noteAdds[i].getItems();
				String descripton = noteAdds[i].getDescription();
				int impact = noteAdds[i].getImpact();
				
	
				
				//type_img.setOnLongClickListener(new TypeLongClickListener(date, dayOfweek, slot, type, items,	impact, descripton));
				layout.setOnLongClickListener(new TypeLongClickListener(month, date, dayOfweek, slot, type, items, 
						impact, descripton));
				
				if(type > 0 && type <=8)
					type_img.setImageResource(typeId[type]);
				
				
				date_num.setText(""+ date + "號");
				week_num.setText(dayOfWeek[ dayOfweek ]);
				timeslot_num.setText(timeslot[ slot ] );

				items_txt.setText( dict.dictionary.get(items) );
				//description_txt.setText(descripton);
				if(impact-3 <=0)
					impact_txt.setText(String.valueOf(impact -3));
				else
					impact_txt.setText("+" + String.valueOf(impact -3));
				
				last_result = result;
				last_day = date;
				last_timeslot = slot;
				//Log.d(TAG, date+"號,星期"+dayOfweek+"時段"+slot+"項目"+items);
				
				//diaryList.addView(diaryItem);

				}
				
				for(int i=0; i<diaryItem2.length; i++){
					if(diaryItem2[i]!=null)
						diaryList.addView(diaryItem2[i]);
				}
				
				
			}	

			sv.fullScroll(View.FOCUS_DOWN);

		}
		private void showDiary() {		
			
			diaryList.removeAllViews();
			
			noteAdds = db.getAllNoteAdd();
			if(noteAdds == null){
				return;
			}
			
			//Log.d(TAG, String.valueOf(noteAdds.length));
			
			LayoutInflater inflater = LayoutInflater.from(context);
			//RelativeLayout lineView = (RelativeLayout)inflater.inflate(R.layout.white_line, null);
			//RelativeLayout white_line = (RelativeLayout)lineView.findViewById(R.id.white_line);
			
			int last_day = 0;
			int last_timeslot = -1;
			int last_result = -1;
			
			
			if(noteAdds.length!=0){
				for(int i=0; i < noteAdds.length; i++){
					int type = noteAdds[i].getType();
					diary.add(type);
					if(type > 0 && type <=8){
						if(!filterButtonIsPressed[type] && !filterButtonIsPressed[0])
							continue;
					}
					int date = noteAdds[i].getRecordTv().getDay();
					int month = noteAdds[i].getRecordTv().getMonth();
					int year = noteAdds[i].getRecordTv().getYear();
					//LayoutInflater inflater = LayoutInflater.from(context);
					diaryItem = inflater.inflate(R.layout.diary_item, null);
					LinearLayout layout = (LinearLayout)diaryItem.findViewById(R.id.diary_layout);
				
					TextView date_num = (TextView) diaryItem.findViewById(R.id.diary_date);
					TextView week_num = (TextView) diaryItem.findViewById(R.id.diary_week);
					TextView timeslot_num = (TextView) diaryItem.findViewById(R.id.diary_timeslot);
					ImageView type_img = (ImageView) diaryItem.findViewById(R.id.diary_image_type);
					TextView items_txt = (TextView) diaryItem.findViewById(R.id.diary_items);
					//TextView description_txt = (TextView) diaryItem.findViewById(R.id.diary_description);
					TextView impact_word = (TextView) diaryItem.findViewById(R.id.diary_impact_word);
					TextView impact_txt = (TextView) diaryItem.findViewById(R.id.diary_impact);
					
					date_num.setTypeface(wordTypefaceBold);
					week_num.setTypeface(wordTypefaceBold);
					timeslot_num.setTypeface(wordTypefaceBold);
					items_txt.setTypeface(wordTypefaceBold);
					impact_word.setTypeface(wordTypefaceBold);
					impact_txt.setTypeface(wordTypefaceBold);
					
					int result = last_result;
					if(date != last_day){
						TestResult testResult = 
								db.getDayTestResult( year, month, date );
	        	
						if(testResult.getTv().getTimestamp() != 0){
							result = testResult.getResult();
						}
						else
							result = -1;
							
					}
					diaryItem.setTag(TAG_changedot, result);	
					if(result == 0)
						layout.setBackgroundResource(R.drawable.diary_pass);
					else if(result == 1){
						layout.setBackgroundResource(R.drawable.diary_nopass);
					}
					else{
						layout.setBackgroundResource(R.drawable.diary_notest);
					}	
				
				int dayOfweek = noteAdds[i].getRecordTv().getDayOfWeek();
				int slot = noteAdds[i].getTimeSlot();
				type = noteAdds[i].getType();
				int items = noteAdds[i].getItems();
				String descripton = noteAdds[i].getDescription();
				int impact = noteAdds[i].getImpact();
				
	
				
				//type_img.setOnLongClickListener(new TypeLongClickListener(date, dayOfweek, slot, type, items,	impact, descripton));
				layout.setOnLongClickListener(new TypeLongClickListener(month, date, dayOfweek, slot, type, items, 
						impact, descripton));
				
				if(type > 0 && type <=8)
					type_img.setImageResource(typeId[type]);
				
				
				date_num.setText(date + "號");
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
				items_txt.setText( dict.dictionary.get(items) );
				//description_txt.setText(descripton);
				if(impact-3 <=0)
					impact_txt.setText(String.valueOf(impact -3));
				else
					impact_txt.setText("+" + String.valueOf(impact -3));
				
				last_result = result;
				last_day = date;
				last_timeslot = slot;
				//Log.d(TAG, date+"號,星期"+dayOfweek+"時段"+slot+"項目"+items);
				
				diaryList.addView(diaryItem);
				//diaryList.addView(white_line);
				//boxesLayout = (LinearLayout) view.findViewById(R.layout.diary_item);
				}
				
				
				
			}	

			sv.fullScroll(View.FOCUS_DOWN);
			//sv.smoothScrollTo(0 , (int)convertDpToPixel(125)*(noteAdds.length) +1000000);
			//updateDiaryView();
		}
	}
	
	private void updateDiaryView(){
		//diaryList.getChildAt(1).setVisibility(View.GONE);
		int allNum = diaryList.getChildCount();
		for(int i=0; i<allNum; i++){
			if(filterButtonIsPressed[diary.get(i)] || filterButtonIsPressed[0]){
				diaryList.getChildAt(i).setVisibility(View.VISIBLE);
			}
			else{
				diaryList.getChildAt(i).setVisibility(View.GONE);
			}
		}
	}
	
	
	
	public static float getDensity(){
		 DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		 return metrics.density;
		}
	
	public static float convertDpToPixel(float dp){
	    float px = dp * getDensity();
	    return px;
	}
	
	
	private void addDrawerContent(int id){
		//0805 add
		int month = (startMonth + currentPageIdx)%12;
		if(month == 0)
			titleText.setText("12月");
		else
			titleText.setText( month + "月");
		
		Log.d(TAG, "chart_type: "+chart_type);
		setArrow(true);
		isFilterOpen = false;
		isContentAdd = true;
		LayoutParams lp = new LayoutParams(drawer.getLayoutParams());
		lp.height = drawerHeight;
		lp.addRule(RelativeLayout.BELOW, calendarBar.getId());
		drawer.setLayoutParams(lp);
		drawerContent.removeAllViews();
		switch(id){
			case R.id.toggle_layout: //linechart			
				drawerContent.addView(lineChartView);
				if(chart_type == 2){ 
					lineChartFilterButton.setVisibility(View.INVISIBLE);
				}
				if(rotateLineChart!=null && isContentAdd)
					rotateLineChart.setVisibility(View.VISIBLE);
				break;
			
			case R.id.cal_toggle_layout: //calendar
			case R.id.toggle:  
				drawerContent.addView(calendarView);
				break;
		}	
	}
			
    private class ToggleListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.DAYBOOK_TOGGLE);
			
			if  (drawer.isOpened()) {
				//drawer.toggle();
				if(!isContentAdd){
					addDrawerContent(v.getId());
				}
				else{
					drawer.toggle();
					setArrow(false);
					lineChartFilterButton.setVisibility(View.VISIBLE);
				}
			}
			else{
				addDrawerContent(v.getId());
				drawer.toggle();				
			}
			
		}
    }
    
    private class FilterLongClickListener implements View.OnLongClickListener{

		@Override
		public boolean onLongClick(View v) {
			
			ClickLog.Log(ClickLogId.DAYBOOK_FILTER_LONGCLICK);  
			
			final Dialog dialog = new Dialog(activity);
		
			dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
	        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	        
	        dialog.getWindow().setContentView(R.layout.dialog_filter_detail);
	        dialog.show();
			/*
			Dialog dialog = new Dialog(MainActivity.getMainActivity(), R.style.selectorDialog);
			dialog.setContentView(R.layout.dialog_diary_detail);
			WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
			
			dialog.show();*/
			
			return false;
		}
    	
    }
    
    private class TypeLongClickListener implements View.OnLongClickListener{
    	
    	int month;
    	int date;
    	int dayOfweek;
		int slot;
		int type;
		int items;
		int impact;
		String descripton;
		String[] typeText = context.getResources().getStringArray(R.array.trigger_list);
		   	
    	public TypeLongClickListener(int month, int date, int dayOfweek, int slot, int type, int items, int impact, String descripton){
    		this.month = month;
    		this.date = date;
    		this.dayOfweek = dayOfweek;
    		this.slot = slot;
    		this.type = type;
    		this.items = items;
    		this.impact = impact;
    		this.descripton = descripton;		
    	}

		@Override
		public boolean onLongClick(View v) {
			
			ClickLog.Log(ClickLogId.DAYBOOK_SHOWDETAIL);  
			
			final Dialog dialog = new Dialog(activity);
			
			dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
	        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	        
	        dialog.getWindow().setContentView(R.layout.dialog_detail_activity);
						
			ImageView type_icon = (ImageView) dialog.findViewById(R.id.type_icon);
	    	type_icon.setImageResource(iconId[type]);
	    	TextView detail_time = (TextView) dialog.findViewById(R.id.detail_time);
			detail_time.setText((month+1)+"月"+date+"號\n"+dayOfWeek[dayOfweek]+"\n"+timeslot[slot]);
			TextView detail_type = (TextView) dialog.findViewById(R.id.detail_type_content);
			detail_type.setText(typeText[type-1]);
			TextView detail_item = (TextView) dialog.findViewById(R.id.detail_item_content);
			detail_item.setText(dict.dictionary.get(items));
			TextView detail_impact = (TextView) dialog.findViewById(R.id.detail_impact_content);
			detail_impact.setText(""+(impact-3));
			TextView detail_description = (TextView) dialog.findViewById(R.id.detail_description_content);
			detail_description.setText(descripton);
			
			
			dialog.show();
			return false;
		}
    	
    }
    
    
	private class FilterButtonListener implements View.OnClickListener {
    	
    	@Override
    	public void onClick(View v) {
    		
    		ClickLog.Log(ClickLogId.DAYBOOK_FILTER_BUTTON);   
    		
    		if  (!drawer.isOpened()) {
    			LayoutParams lp = new LayoutParams(drawer.getLayoutParams());
				//Log.i("OMG", "H: "+lp.height);
				lp.height = filterHeight;
				lp.addRule(RelativeLayout.BELOW, calendarBar.getId());
				drawer.setLayoutParams(lp);
				
				drawerContent.removeAllViews();
				
				drawerContent.addView(lineChartFilter);
				isFilterOpen = true;
				isContentAdd = false;
				setFilterSize();
				setFilterType(3);
				
				drawer.toggle();
    		}
    		else{
    			if(!isContentAdd){
    				drawer.toggle();
    			}
    			else{
    				isContentAdd = true;
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
						setFilterType(3);
						drawerContent.addView(calendarView);
						
						
						/*if  (!drawer.isOpened()) {
							drawer.toggle();
						}*/
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
    		
    	}    	
    }
	private void setAllFilter(boolean enable){
		for(int i=1; i<filterButtonIsPressed.length; i++){
			filterButtonIsPressed[i] = enable;
		}
	}
    

    private class FilterListener implements View.OnClickListener {
    	

    	@Override
    	public void onClick(View v) {
    		
    		ClickLog.Log(ClickLogId.DAYBOOK_FILTER);
    		
    		switch (v.getId()) {
    		case (R.id.filter_all): { 
    			filter_count = 0;
    			setAllFilter(false);
    		    filterButtonIsPressed[0] = true; 
    		    filterAll.setImageResource(R.drawable.filter_all_selected);
    			setAllButtonImage();
    			if(lineChart!=null)
    				lineChart.invalidate();
    			break;
    		}
    		case (R.id.filter_1): {
    			if (filterButtonIsPressed[1]) { filterButtonIsPressed[1] = false; filter1.setImageResource(R.drawable.filter_color1); filter_count--;}
    			else {filterButtonIsPressed[1] = true; filter1.setImageResource(R.drawable.filter_color1_selected); filterButtonIsPressed[0] = false; filter_count++;}
    			setAllButtonImage();
    			if(lineChart!=null)
    				lineChart.invalidate();
    			break;	
    		}
    		
    		case (R.id.filter_2): {
    			if (filterButtonIsPressed[2]) { filterButtonIsPressed[2] = false; filter2.setImageResource(R.drawable.filter_color2); filter_count--;}
    			else {filterButtonIsPressed[2] = true; filter2.setImageResource(R.drawable.filter_color2_selected); filterButtonIsPressed[0] = false; filter_count++;}
    			setAllButtonImage();
    			if(lineChart!=null)
    				lineChart.invalidate();
    			break;
    			
    		}
    		
    		case (R.id.filter_3): {
    			if (filterButtonIsPressed[3]) { filterButtonIsPressed[3] = false; filter3.setImageResource(R.drawable.filter_color3); filter_count--;}
    			else {filterButtonIsPressed[3] = true; filter3.setImageResource(R.drawable.filter_color3_selected); filterButtonIsPressed[0] = false; filter_count++;}
    			setAllButtonImage();
    			if(lineChart!=null)
    				lineChart.invalidate();
    			break;
    			
    		}
    		
    		case (R.id.filter_4): {
    			if (filterButtonIsPressed[4]) { filterButtonIsPressed[4] = false; filter4.setImageResource(R.drawable.filter_color4); filter_count--;}
    			else {filterButtonIsPressed[4] = true; filter4.setImageResource(R.drawable.filter_color4_selected); filterButtonIsPressed[0] = false; filter_count++;}
    			setAllButtonImage();
    			if(lineChart!=null)
    				lineChart.invalidate();
    			break;
    			
    		}
    		case (R.id.filter_5): {
    			if (filterButtonIsPressed[5]) { filterButtonIsPressed[5] = false; filter5.setImageResource(R.drawable.filter_color5); filter_count--;}
    			else {filterButtonIsPressed[5] = true; filter5.setImageResource(R.drawable.filter_color5_selected); filterButtonIsPressed[0] = false; filter_count++;}
    			setAllButtonImage();
    			if(lineChart!=null)
    				lineChart.invalidate();
    			break;    			
    		}
    		case (R.id.filter_6): {
    			if (filterButtonIsPressed[6]) { filterButtonIsPressed[6] = false; filter6.setImageResource(R.drawable.filter_color6); filter_count--;}
    			else {filterButtonIsPressed[6] = true; filter6.setImageResource(R.drawable.filter_color6_selected); filterButtonIsPressed[0] = false; filter_count++;}
    			setAllButtonImage();
    			if(lineChart!=null)
    				lineChart.invalidate();
    			break;
    		}
    		case (R.id.filter_7): {
    			if (filterButtonIsPressed[7]) { filterButtonIsPressed[7] = false; filter7.setImageResource(R.drawable.filter_color7); filter_count--;}
    			else {filterButtonIsPressed[7] = true; filter7.setImageResource(R.drawable.filter_color7_selected); filterButtonIsPressed[0] = false; filter_count++;}
    			setAllButtonImage();
    			if(lineChart!=null)
    				lineChart.invalidate();
    			break;
    		}
    		case (R.id.filter_8): {
    			if (filterButtonIsPressed[8]) { filterButtonIsPressed[8] = false; filter8.setImageResource(R.drawable.filter_color8); filter_count--;}
    			else {filterButtonIsPressed[8] = true; filter8.setImageResource(R.drawable.filter_color8_selected); filterButtonIsPressed[0] = false; filter_count++;}
    			setAllButtonImage();
    			if(lineChart!=null)
    				lineChart.invalidate();
    			break;
    		}	
    		}
    		Log.d(TAG, "filter_count: "+ filter_count);
    		if(filter_count == 0){
    			filterButtonIsPressed[0]=true;
    			filterAll.setImageResource(R.drawable.filter_all_selected);
    		}
    		//updateCalendarHandler.sendEmptyMessage(currentPageIdx);//updateCalendarView(currentPageIdx);
    		//updateDiaryHandler.sendEmptyMessage(0);//showDiary();
    		mSectionsPagerAdapter.updateCalendar();
    		
    		updateDiaryView();
    		updateFilterButton();
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
    
    public void writeQuestionFile(int day, int slot, int type, int items, int impact, String description) {
    	
    	TDP = new TestDataParser2(0); 
		if( TDP!= null ){
			//TDP.startAddNote();
			//TDP.getQuestionResult2(textFile)
			TDP.startAddNote2(0, day, slot, type, items, impact, description);
		}
		//update Recent Calendar
		mSectionsPagerAdapter.updateRecentDay();
		
		updateDiaryHandler.sendEmptyMessage(0);
		//loadHandler.sendEmptyMessage(0);
		
//		NoteAdd note = db.getLatestNoteAdd();
//		diary.add(type);
//		int date = note.getRecordTv().getDay();
//		int month = note.getRecordTv().getMonth();
//		int year = note.getRecordTv().getYear();
//		LayoutInflater inflater = LayoutInflater.from(context);
//		diaryItem = inflater.inflate(R.layout.diary_item, null);
//		LinearLayout layout = (LinearLayout)diaryItem.findViewById(R.id.diary_layout);
//	
//		TextView date_num = (TextView) diaryItem.findViewById(R.id.diary_date);
//		TextView week_num = (TextView) diaryItem.findViewById(R.id.diary_week);
//		TextView timeslot_num = (TextView) diaryItem.findViewById(R.id.diary_timeslot);
//		ImageView type_img = (ImageView) diaryItem.findViewById(R.id.diary_image_type);
//		TextView items_txt = (TextView) diaryItem.findViewById(R.id.diary_items);
//		//TextView description_txt = (TextView) diaryItem.findViewById(R.id.diary_description);
//		TextView impact_word = (TextView) diaryItem.findViewById(R.id.diary_impact_word);
//		TextView impact_txt = (TextView) diaryItem.findViewById(R.id.diary_impact);
//		
//		date_num.setTypeface(wordTypefaceBold);
//		week_num.setTypeface(wordTypefaceBold);
//		timeslot_num.setTypeface(wordTypefaceBold);
//		items_txt.setTypeface(wordTypefaceBold);
//		impact_word.setTypeface(wordTypefaceBold);
//		impact_txt.setTypeface(wordTypefaceBold);
//		
//		int result;
//		TestResult testResult = 
//			db.getDayTestResult( year, month, date );
//	
//			if(testResult.getTv().getTimestamp() != 0){
//				result = testResult.getResult();
//			}
//			else
//				result = -1;
//			
//		
//			
//		if(result == 0)
//			layout.setBackgroundResource(R.drawable.diary_pass);
//		else if(result == 1){
//			layout.setBackgroundResource(R.drawable.diary_nopass);
//		}
//		else{
//			layout.setBackgroundResource(R.drawable.diary_notest);
//		}	
//	
//	int dayOfweek = note.getRecordTv().getDayOfWeek();
//
//	//type_img.setOnLongClickListener(new TypeLongClickListener(date, dayOfweek, slot, type, items,	impact, descripton));
//	layout.setOnLongClickListener(new TypeLongClickListener(month, date, dayOfweek, slot, type, items, 
//			impact, description));
//	
//	if(type > 0 && type <=8)
//		type_img.setImageResource(typeId[type]);
//	
//	
//	date_num.setText(""+ date + "號");
//	week_num.setText(dayOfWeek[ dayOfweek ]);
//	timeslot_num.setText(timeslot[ slot ] );
//	items_txt.setText( dict.dictionary.get(items) );
//	//description_txt.setText(descripton);
//	if(impact-3 <=0)
//		impact_txt.setText(String.valueOf(impact -3));
//	else
//		impact_txt.setText("+" + String.valueOf(impact -3));
//
//	
//	diaryList.addView(diaryItem);
		sv.fullScroll(View.FOCUS_DOWN);
	}

    public static int getChartType () {
		return chart_type;
	}
    
    private void addNewItem(){
    	
    	
    	
    	
    }
    
    
	@Override
	public void resetView() {
		isNotePageShow = false;
		addButton.setVisibility(View.VISIBLE);
		fragment_layout.setEnabled(true);
		//updateDiaryHandler.sendEmptyMessage(0);//showDiary();
		//updateDiaryView();
		
		if(lineChart!=null)
			lineChart.invalidate();
		
		//update Calendar View
		//updateCalendarHandler.sendEmptyMessage(-1);//updateCalendarView(currentPageIdx);
		
		//sv.fullScroll(View.FOCUS_DOWN);
		Log.d(TAG, "DiaryCount:"+diaryList.getChildCount());
	}
	

	/**
	 * @param {int} pageIdx Index of page to be shown. Assign pageIdx to -1 for showing this month.
	 */
	
	private class UpdateCalendarHandler extends Handler{
		
		public void handleMessage(Message msg) {
			
			updateCalendarView(msg.what);
		
		}
		
		private void updateCalendarView(int pageIdx){
			mViewPager.removeAllViews();
			
			LayoutInflater inflater = LayoutInflater.from(context);
			pageViewList = new View[sustainMonth];
			Calendar tempCalendar = Calendar.getInstance();
			tempCalendar.set(startYear, startMonth - 1, 1);
			for (int i = 0; i < sustainMonth; i++) {
				pageViewList[i] = (View) inflater.inflate(R.layout.fragment_calendar, null);
				
				pageViewList[i].setTag(TAG_PAGE_YEAR, tempCalendar.get(Calendar.YEAR));
				pageViewList[i].setTag(TAG_PAGE_MONTH, tempCalendar.get(Calendar.MONTH));
				tempCalendar.add(Calendar.MONTH, 1);
			}

			mSectionsPagerAdapter = new SectionsPagerAdapter(pageViewList);
			mViewPager.setAdapter(mSectionsPagerAdapter);
			
			if (pageIdx == -1)
				// mViewPager.setCurrentItem(Calendar.getInstance().get(Calendar.MONTH) + 1 - startMonth);
				mViewPager.setCurrentItem(sustainMonth - 1);
			else
				mViewPager.setCurrentItem(pageIdx);
		}
	}
	public static void scrolltoItem(int year, int month, int day){ // old version
		
		if(noteAdds == null)
			return;
		if(noteAdds.length == 0)
			return;
		for(int i=0; i < noteAdds.length; i++){
			int rYear = noteAdds[i].getRecordTv().getYear();
			int rMonth = noteAdds[i].getRecordTv().getMonth();
			int rDay = noteAdds[i].getRecordTv().getDay();
			
			if(rYear == year && rMonth == month && rDay == day){
				sv.smoothScrollTo(0 , (int)convertDpToPixel(125)*(i-2));				
			}		
		}
	}
	
	public static void scrolltoItem2(int year, int month, int day) {
			
		int allNum = diaryList.getChildCount();
		int j=0;
		for(int i=0; i<allNum; i++){
			View item = diaryList.getChildAt(i);
			if(item.getVisibility() == View.GONE)
				continue;
			j++;
			int y = (Integer)item.getTag(TAG_LIST_YEAR);
			int m = (Integer)item.getTag(TAG_LIST_MONTH);
			int d = (Integer)item.getTag(TAG_LIST_DAY);
			if(y == year && m == month && d == day){
				sv.smoothScrollTo(0 , (int)convertDpToPixel(125)*(j-3));	
			}	
		}				
		
	}


	@Override
	public void QuestionDone() {
		
		randomButton.setVisibility(View.GONE);
	}
		
	public class DataInitTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			
			//settingBars();
			//checkHasRecorder();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {


		}
	}




}
