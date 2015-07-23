package com.ubicomp.ketdiary.mydaybook;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.clicklog.ClickLog;
import com.ubicomp.ketdiary.clicklog.ClickLogId;
import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.data.structure.TimeValue;
import com.ubicomp.ketdiary.db.DatabaseControl;
import com.ubicomp.ketdiary.fragment.DaybookFragment;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.Typefaces;

public class SectionsPagerAdapter extends PagerAdapter {
	
	private static final String TAG = "Calendar";
	
    private View[] pageViewList;
    private Calendar mCalendar;
    private static final int THIS_DAY = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    private static final int THIS_MONTH = Calendar.getInstance().get(Calendar.MONTH);
    
    private static View thisDayView;
    private static View selectedView;
    public static final int TAG_CAL_CELL_DAY = R.string.TAG_CAL_CELL_DAY;
    public static final int TAG_CAL_CELL_MONTH = R.string.TAG_CAL_CELL_MONTH;
    public static final int TAG_CAL_CELL_PAGE_MONTH = R.string.TAG_CAL_CELL_PAGE_MONTH;
    public static final int TAG_CAL_CELL_YEAR = R.string.TAG_CAL_CELL_YEAR;
    public static final int TAG_CAL_CELL_TS = R.string.TAG_CAL_CELL_TS;
    
    private static final Calendar startDay = PreferenceControl.getStartDate();
    private static final Calendar today = Calendar.getInstance();
    
    private Context context;

    private LayoutInflater inflater;
    private DatabaseControl db;
    private int sustainMonth;

    private boolean[] isPageViewInitialized ;//= new boolean[Database.SUSTAINED_MONTHS];
    
    private static final int[] dotId2 = { 0, R.drawable.dot_color1, R.drawable.dot_color2,
    	R.drawable.dot_color3, R.drawable.dot_color4, R.drawable.dot_color5,
    	R.drawable.dot_color6, R.drawable.dot_color7, R.drawable.dot_color8
    };
    
    private static final int[] dotId = { 0, R.drawable.cell_dot1, R.drawable.cell_dot2,
    	R.drawable.cell_dot3, R.drawable.cell_dot4, R.drawable.cell_dot5,
    	R.drawable.cell_dot6, R.drawable.cell_dot7, R.drawable.cell_dot8
    };

    public SectionsPagerAdapter(View[] pageViewList){
        this.pageViewList = pageViewList;
        context = App.getContext();
        inflater = LayoutInflater.from(App.getContext());
        
        db = new DatabaseControl();
        sustainMonth = PreferenceControl.getSustainMonth();
        isPageViewInitialized = new boolean[sustainMonth];
    }
	
	@Override
	public int getCount() {
		return sustainMonth;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
        if(isPageViewInitialized[position] != true){
            initPageView(pageViewList[position]);
            isPageViewInitialized[position] = true;
        }
        container.addView(pageViewList[position]);
		return pageViewList[position];
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(pageViewList[position]);
	}
	
	@Override
	public boolean isViewFromObject(View v, Object o) {
		return v == o;
	}
	
	public float getDensity(){
		 DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		 return metrics.density;
		}
	
	public float convertDpToPixel(float dp){
	    float px = dp * getDensity();
	    //Log.d(TAG, "density:" + getDensity());
	    return px;
	}
	
    private void initPageView(View pageView){

        int pageViewMonth = Integer.valueOf((pageView.getTag()).toString());
        GridLayout glCalendar = (GridLayout) pageView.findViewById(R.id.gl_calendar);
        
        // Initialize the calendar
        Calendar mCalendar = Calendar.getInstance();
        int maxDaysOfWeek = mCalendar.getMaximum(Calendar.DAY_OF_WEEK);
        mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        
        mCalendar.set(Calendar.MONTH, pageViewMonth);
        int maxWeeksOfMonth = mCalendar.getActualMaximum(Calendar.WEEK_OF_MONTH);

        mCalendar.set(Calendar.DAY_OF_MONTH, 1);

        mCalendar.add(Calendar.DAY_OF_MONTH, -(mCalendar.get(Calendar.DAY_OF_WEEK)-2));
        TimeValue tv = TimeValue.generate(mCalendar.getTimeInMillis());
        
        View cellView;
        TextView calDateText;
        ImageView calDot1, calDot2, calDot3, date_result, calDot15,calDot25;
        int result;
        NoteAdd[] noteAdds;
        TestResult testResult=null;
 
        for (int i=0;i<maxWeeksOfMonth*maxDaysOfWeek;++i){
            
            cellView = inflater.inflate(R.layout.calendar_cell, null, false);

            // Set the invisible days(dummy days before 1st day of that month)
            if (mCalendar.get(Calendar.MONTH) != pageViewMonth){
                glCalendar.addView(cellView);
                cellView.setVisibility(View.INVISIBLE);
                mCalendar.add(Calendar.DAY_OF_MONTH, 1);
                
                LayoutParams params = cellView.getLayoutParams();
                params.width = (int)convertDpToPixel((float)39.33);
                params.height =(int)convertDpToPixel((float)43.33);
                cellView.setLayoutParams(params);
                continue;
            }
            
            calDateText = (TextView) cellView.findViewById(R.id.tv_calendar_date);
            calDot1 = (ImageView) cellView.findViewById(R.id.iv_calendar_dot1);
            calDot2 = (ImageView) cellView.findViewById(R.id.iv_calendar_dot2);
            calDot3 = (ImageView) cellView.findViewById(R.id.iv_calendar_dot3);
            date_result = (ImageView) cellView.findViewById(R.id.iv_date_result);
            calDot15 = (ImageView) cellView.findViewById(R.id.iv_calendar_dot15);
            calDot25 = (ImageView) cellView.findViewById(R.id.iv_calendar_dot25);
            
            calDot1.setVisibility(View.INVISIBLE);
            calDot2.setVisibility(View.INVISIBLE);
            calDot3.setVisibility(View.INVISIBLE);
            calDot15.setVisibility(View.INVISIBLE);
            calDot25.setVisibility(View.INVISIBLE);
            
            cellView.setTag(TAG_CAL_CELL_DAY, mCalendar.get(Calendar.DAY_OF_MONTH));
            cellView.setTag(TAG_CAL_CELL_MONTH, mCalendar.get(Calendar.MONTH));
            cellView.setTag(TAG_CAL_CELL_PAGE_MONTH, pageViewMonth);
            cellView.setTag(TAG_CAL_CELL_YEAR, mCalendar.get(Calendar.YEAR));
            cellView.setTag(TAG_CAL_CELL_TS, mCalendar.getTimeInMillis());
            cellView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	
                	ClickLog.Log(ClickLogId.DAYBOOK_SPECIFIC_DAY);

                    if(selectedView != v){
                    	long selectedTs = Long.valueOf(v.getTag(TAG_CAL_CELL_TS).toString());
                    	// if(selectedTs < startDay.getTimeInMillis() || selectedTs > today.getTimeInMillis()+86400000){
                    	// 	return;
                    	// }
                    	
                        int selectedMonth = Integer.valueOf(selectedView.getTag(TAG_CAL_CELL_MONTH).toString());
                        TextView selectedDayTextView = (TextView) selectedView.findViewById(R.id.tv_calendar_date);
                        selectedDayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        selectedDayTextView.setTextColor(context.getResources().getColor(R.color.white));
                        
                        // Set the new selected day
                        selectedView = v;
                        TextView newSelectedDayTextView = (TextView) selectedView.findViewById(R.id.tv_calendar_date);
                        newSelectedDayTextView.setTextColor(context.getResources().getColor(R.color.black));
                        newSelectedDayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
               
                    }
                    int selectedYear = Integer.valueOf(v.getTag(TAG_CAL_CELL_YEAR).toString());
                    int selectedMonth = Integer.valueOf(v.getTag(TAG_CAL_CELL_MONTH).toString());
                    int selectedDay = Integer.valueOf(v.getTag(TAG_CAL_CELL_DAY).toString());
                    DaybookFragment.scrolltoItem(selectedYear, selectedMonth, selectedDay);
                    
                    // sv.smoothScrollTo(0 , 270*(Integer.parseInt(parsed_date[0])+4)-1350-900);
                }
            });
            
            calDateText.setGravity(Gravity.CENTER);
            
            calDateText.setText(mCalendar.get(Calendar.DAY_OF_MONTH) + "");
            calDateText.setTextColor(context.getResources().getColor(R.color.text_gray2));
            calDateText.setTypeface(Typefaces.getDigitTypefaceBold());
            
            
            if ( mCalendar.get(Calendar.MONTH) == pageViewMonth){

            	if(mCalendar.getTimeInMillis()> startDay.getTimeInMillis() && mCalendar.getTimeInMillis() <= today.getTimeInMillis()+86400000){

            		noteAdds = db.getDayNoteAdd(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
            	            	
            		if(noteAdds != null){
            			Log.d(TAG,""+noteAdds[0].getRecordTv().getMonth());
            			
            			int[] typedot = new int[3];
            			int k = -1;
            			for(int j=0; j<noteAdds.length; j++){
            				int type = noteAdds[j].getType();
            				if(k>=2)
            					break;
            				
            				if(DaybookFragment.filterButtonIsPressed[0]){
            					typedot[++k] = type;
            					continue;
            				}

            				if(DaybookFragment.filterButtonIsPressed[type])
            					typedot[++k] = type;
            				
            			}
            			if(k>=2){
//            				calDot3.setImageResource(dotId[typedot[0]]);
//            				calDot3.setVisibility(View.VISIBLE);
            				calDot2.setImageResource(dotId[typedot[1]]);
            				calDot2.setVisibility(View.VISIBLE);
//            				calDot1.setImageResource(dotId[typedot[2]]);
//            				calDot1.setVisibility(View.VISIBLE);
            				calDot25.setImageResource(dotId[typedot[0]]);
            				calDot15.setImageResource(dotId[typedot[2]]);
            				calDot15.setVisibility(View.VISIBLE);
            				calDot25.setVisibility(View.VISIBLE);
            				calDot3.setVisibility(View.INVISIBLE);
            				calDot1.setVisibility(View.INVISIBLE);
            	            
            			}
            			else if(k==1){
//            				calDot3.setImageResource(dotId[typedot[0]]);
//            				calDot3.setVisibility(View.VISIBLE);
//            				calDot2.setImageResource(dotId[typedot[1]]);
//            				calDot2.setVisibility(View.INVISIBLE);
//            				calDot1.setImageResource(dotId[typedot[1]]);
//            				calDot1.setVisibility(View.VISIBLE);
            				calDot1.setVisibility(View.INVISIBLE);
            				calDot2.setVisibility(View.INVISIBLE);
            				calDot3.setVisibility(View.INVISIBLE);
            				calDot15.setImageResource(dotId[typedot[0]]);
            				calDot15.setVisibility(View.VISIBLE);
            				calDot25.setImageResource(dotId[typedot[1]]);
            				calDot25.setVisibility(View.VISIBLE);
            			}
            			else if(k==0){
            				calDot2.setImageResource(dotId[typedot[0]]);
            				calDot2.setVisibility(View.VISIBLE);
            				calDot15.setVisibility(View.INVISIBLE);
            				calDot25.setVisibility(View.INVISIBLE);
            			}  
            			
            			/*
            			if(noteAdds.length>=3){
            				calDot1.setImageResource(dotId[noteAdds[0].getType()]);
            				calDot1.setVisibility(View.VISIBLE);
            				calDot2.setImageResource(dotId[noteAdds[1].getType()]);
            				calDot2.setVisibility(View.VISIBLE);
            				calDot3.setImageResource(dotId[noteAdds[2].getType()]);
            				calDot3.setVisibility(View.VISIBLE);
            			}
            			else if(noteAdds.length==2){
            				calDot1.setImageResource(dotId[noteAdds[0].getType()]);
            				calDot1.setVisibility(View.VISIBLE);
            				calDot2.setImageResource(dotId[noteAdds[1].getType()]);
            				calDot2.setVisibility(View.VISIBLE);
            			}
            			else if(noteAdds.length==1){
            				calDot2.setImageResource(dotId[noteAdds[0].getType()]);
            				calDot2.setVisibility(View.VISIBLE);
            			} */         	
            		}

            		testResult = db.getDayTestResult( mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH) );
            	
            		if(testResult.getTv().getTimestamp() != 0){
            			result = testResult.getResult();
            			if(result == 0)
            				date_result.setImageResource(R.drawable.bigbluedot);
            			else if(result == 1){
            				date_result.setImageResource(R.drawable.bigreddot);
            			}
            		}
            		else{
            			date_result.setImageResource(R.drawable.biggraydot);
            		}
            		calDateText.setTextColor(context.getResources().getColor(R.color.white));
            	}
            	else if(mCalendar.getTimeInMillis() < startDay.getTimeInMillis()){
                    // If current day is before the start day
            		calDateText.setTextColor(context.getResources().getColor(R.color.date_before_gray));
                    cellView.setOnClickListener(null);
                }
            	
            	else{
                    // If current day is a future day
            		calDateText.setTextColor(context.getResources().getColor(R.color.text_gray2));
                    cellView.setOnClickListener(null);
            	}
            }
                        
            // Initialize the selected view on current day
            if (mCalendar.get(Calendar.DAY_OF_MONTH) == THIS_DAY && mCalendar.get(Calendar.MONTH) == THIS_MONTH) {
                thisDayView = cellView;
                if (selectedView == null){
                    selectedView = thisDayView;
                }
            }
            
            // Highlight the selected day
            int selectedDay, selectedMonth;
            if (selectedView != null){
                selectedDay = Integer.valueOf(selectedView.getTag(TAG_CAL_CELL_DAY).toString());
                selectedMonth = Integer.valueOf(selectedView.getTag(TAG_CAL_CELL_MONTH).toString());

                if (mCalendar.get(Calendar.DAY_OF_MONTH) == selectedDay && mCalendar.get(Calendar.MONTH) == selectedMonth) {
                    selectedView = cellView;
                    calDateText.setTextColor(context.getResources().getColor(R.color.black));
                    calDateText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                }
            }
            
            glCalendar.addView(cellView);
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
            
            LayoutParams params = cellView.getLayoutParams();
            params.width = (int)convertDpToPixel((float)39.33);
            params.height =(int)convertDpToPixel((float)43.33);
            //params.width=118;
            //params.height=130;
            cellView.setLayoutParams(params);
        }

    }

    public View getSelectedView(){
        return selectedView;
    }
    public View getThisDayView(){
        return thisDayView;
    }
    public void asignSelecteViewToThisDayView(){
        selectedView = thisDayView;
    }

}
