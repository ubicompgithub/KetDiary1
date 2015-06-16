package com.ubicomp.ketdiary.fragment;

import java.io.File;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.ubicomp.ketdiary.dialog.AddNoteDialog;
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
	private Database myConstant;
		
	private static int sv_item_height;
	
	//file
	private QuestionFile questionFile;
	private File mainDirectory = null;
	private TestDataParser2 TDP;
	
	private int fragmentIdx;
	
	public int selectedDay, selectedMonth;
	
	private int chart_type = 2;
	private LinearLayout chartAreaLayout;
	private LineChartView lineChart;
	private LineChartTitle chartTitle;
	private ChartCaller caller;
	
	public View lineChartBar, lineChartView, lineChartFilter, calendarBar, calendarView;
	
	public ImageView lineChartFilterButton, addButton;
	
	private boolean isFilterIsOpen = false;
	private AddNoteDialog2 notePage = null;
	
	private DatabaseControl db;
	private NoteCategory2 dict;
	private static final String[] dayOfWeek = {" ", "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
	private static final String[] timeslot = {"上午", "下午", "晚上"};
	
	private int drawerHeight = App.getContext().getResources().getDimensionPixelSize(R.dimen.drawer_normal_height);
	//public static List<Integer> filterList = new ArrayList<Integer>();

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
		
		mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());  
		myConstant = new Database();
		
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) view.findViewById(R.id.pager);  
		mViewPager.setAdapter(mSectionsPagerAdapter);	
		
		// Initialize the selectedDay and selectedMonth
		selectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		selectedMonth = Calendar.getInstance().get(Calendar.MONTH);
		
		backToTodayText = (TextView) view.findViewById(R.id.back_to_today);
		backToTodayText.setText(Integer.toString(selectedDay));
		
		titleText = (TextView) view.findViewById(R.id.month_text);
		
		drawer = (SlidingDrawer) view.findViewById(R.id.slidingDrawer1);
		toggle = (ImageView) view.findViewById(R.id.toggle);
		linechartIcon = (ImageView) view.findViewById(R.id.linechart_icon);
	
		lineChartBar = (View) inflater.inflate(R.layout.linechart_upperbar, null, false);
		lineChartView = (View) inflater.inflate(R.layout.linechart_main, null, false);
		lineChartFilter = (View) inflater.inflate(R.layout.linechart_filter, null, false);
	    calendarIcon = (ImageView) lineChartBar.findViewById(R.id.back_to_calendar);
	    toggle_linechart = (ImageView) lineChartBar.findViewById(R.id.toggle_linechart);
	    
	    addButton = (ImageView) view.findViewById(R.id.add_button);
	    
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
				
				lineChart = (LineChartView) view.findViewById(R.id.lineChart);
		        lineChart.setChartData(getRandomData());
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
	public void onResume() {
		super.onResume();
		
		//setCurrentCalendarPage(selectedMonth + 1 - myConstant.START_MONTH);
		
		
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
	
	private void showDiary() {		
		diaryList = (LinearLayout) view.findViewById(R.id.item);
		diaryList.removeAllViews();
		
		NoteAdd[] noteAdds = db.getAllNoteAdd();
		Log.d(TAG, String.valueOf(noteAdds.length));
		
		LayoutInflater inflater = LayoutInflater.from(context);
		RelativeLayout lineView = (RelativeLayout)inflater.inflate(R.layout.white_line, null);
		RelativeLayout white_line = (RelativeLayout)lineView.findViewById(R.id.white_line);
		
		int last_day = 0;
		int last_timeslot = -1;
		if(noteAdds != null){
			for(int i=0; i < noteAdds.length; i++){
				//LayoutInflater inflater = LayoutInflater.from(context);
				diaryItem = inflater.inflate(R.layout.diary_item2, null);
				LinearLayout layout = (LinearLayout)diaryItem.findViewById(R.id.diary_layout);
			
			TextView date_num = (TextView) diaryItem.findViewById(R.id.diary_date);
			TextView week_num = (TextView) diaryItem.findViewById(R.id.diary_week);
			TextView timeslot_num = (TextView) diaryItem.findViewById(R.id.diary_timeslot);
			ImageView type_img = (ImageView) diaryItem.findViewById(R.id.diary_image_type);
			TextView items_txt = (TextView) diaryItem.findViewById(R.id.diary_items);
			TextView description_txt = (TextView) diaryItem.findViewById(R.id.diary_description);
			TextView impact_txt = (TextView) diaryItem.findViewById(R.id.diary_impact);
			
			int date = noteAdds[i].getRecordTv().getDay();
			int dayOfweek = noteAdds[i].getRecordTv().getDayOfWeek();
			int slot = noteAdds[i].getTimeSlot();
			int type = noteAdds[i].getType();
			int items = noteAdds[i].getItems();
			String descripton = noteAdds[i].getDescription();
			int impact = noteAdds[i].getImpact();
			
			switch (type){
				case 1:
					type_img.setImageResource(R.drawable.emoji5);
					break;
				case 2:
					type_img.setImageResource(R.drawable.emoji2);
					break;
				case 3:
					type_img.setImageResource(R.drawable.emoji4);
					break;
				case 4:
					type_img.setImageResource(R.drawable.emoji1);
					break;
				case 5:
					type_img.setImageResource(R.drawable.emoji3);
					break;
				case 6:
					type_img.setImageResource(R.drawable.others_emoji3);
					break;
				case 7:
					type_img.setImageResource(R.drawable.others_emoji3);
					break;
				case 8:
					break;
			}
			
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
			}
			items_txt.setText( dict.getItems(items) );
			//description_txt.setText(descripton);
			//impact_txt.setText("影響：　"+String.valueOf(impact -4));
			
				
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


	@Override
	public void resetView() {
		addButton.setVisibility(View.VISIBLE);
		fragment_layout.setEnabled(true);
		showDiary();
	}
		
}
