package com.ubicomp.ketdiary.mydaybook;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
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
import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.db.DatabaseControl;
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
    
    private Context context;

    private LayoutInflater inflater;
    private DatabaseControl db;

    private boolean[] isPageViewInitialized = new boolean[Database.SUSTAINED_MONTHS];
    
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
    }
	
	@Override
	public int getCount() {
		return Database.SUSTAINED_MONTHS;
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

        
        View cellView;
        TextView calDateText;
        ImageView calDot1, calDot2, calDot3;
        NoteAdd[] noteAdds;

        for (int i=0;i<maxWeeksOfMonth*maxDaysOfWeek;++i){
                    
            cellView = inflater.inflate(R.layout.calendar_cell, null, false);
            calDateText = (TextView) cellView.findViewById(R.id.tv_calendar_date);
            calDot1 = (ImageView) cellView.findViewById(R.id.iv_calendar_dot1);
            calDot2 = (ImageView) cellView.findViewById(R.id.iv_calendar_dot2);
            calDot3 = (ImageView) cellView.findViewById(R.id.iv_calendar_dot3);
            
            calDot1.setVisibility(View.INVISIBLE);
            calDot2.setVisibility(View.INVISIBLE);
            calDot3.setVisibility(View.INVISIBLE);
            
            cellView.setTag(TAG_CAL_CELL_DAY, mCalendar.get(Calendar.DAY_OF_MONTH));
            cellView.setTag(TAG_CAL_CELL_MONTH, mCalendar.get(Calendar.MONTH));
            cellView.setTag(TAG_CAL_CELL_PAGE_MONTH, pageViewMonth);
            cellView.setTag(TAG_CAL_CELL_YEAR, mCalendar.get(Calendar.YEAR));
            cellView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(selectedView != v){
                        int selectedPageMonth = Integer.valueOf(selectedView.getTag(TAG_CAL_CELL_PAGE_MONTH).toString());
                        int selectedMonth = Integer.valueOf(selectedView.getTag(TAG_CAL_CELL_MONTH).toString());
                        TextView selectedDayTextView = (TextView) selectedView.findViewById(R.id.tv_calendar_date);

                        if(selectedPageMonth == selectedMonth)  // If selected month is exactly current page month
                            selectedDayTextView.setTextColor(context.getResources().getColor(R.color.text_gray2));
                        else
                            selectedDayTextView.setTextColor(Color.BLACK);

                        // Set the new selected day
                        selectedView = v;
                        TextView newSelectedDayTextView = (TextView) selectedView.findViewById(R.id.tv_calendar_date);
                        newSelectedDayTextView.setTextColor(context.getResources().getColor(R.color.blue));
                    }
                    // sv.smoothScrollTo(0 , 270*(Integer.parseInt(parsed_date[0])+4)-1350-900);
                    
                }
            });
            
            calDateText.setGravity(Gravity.CENTER);
            
            calDateText.setText(mCalendar.get(Calendar.DAY_OF_MONTH) + "");
            //calDateText.setTextColor(context.getResources().getColor(R.color.text_gray2));
            
            
            
            if ( mCalendar.get(Calendar.MONTH) == pageViewMonth ){
            	noteAdds = db.getDayNoteAdd(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH)+1, mCalendar.get(Calendar.DAY_OF_MONTH));
            	
            	
            	if(noteAdds != null){
            		Log.d(TAG,""+noteAdds[0].getRecordTv().getMonth());
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
            		}           	
            	}
            }
            else {
            	//calDateText.setTextColor(context.getResources().getColor(R.color.dark_gray));
            	calDateText.setVisibility(View.INVISIBLE);
            }
            calDateText.setTextColor(context.getResources().getColor(R.color.text_gray2));
            calDateText.setTypeface(Typefaces.getDigitTypefaceBold());
            /*
            // Set cells that belong to current month 
            if ( mCalendar.get(Calendar.MONTH) == pageViewMonth ){
                calDateText.setBackgroundResource(R.drawable.bigbluedot);
                Random r = new Random();
                
                if(mCalendar.get(Calendar.MONTH) == 5){
                    if ( mCalendar.get(Calendar.DAY_OF_WEEK) == 1 || mCalendar.get(Calendar.DAY_OF_WEEK) == 7){
                        calDateText.setBackgroundResource(R.drawable.bigreddot);
                    }
                
                
                    int ran_num = r.nextInt(4 - 1) + 1;
                
                    if (ran_num == 1) {}
                    else if (ran_num == 2) {}//calDateText.setBackgroundResource(R.drawable.bigreddot);}
                    else {calDateText.setBackgroundResource(R.drawable.biggraydot);}
                    
                }               
                else{

                    int ran_num = r.nextInt(4 - 1) + 1;
                
                    if (ran_num == 1) {
                        calDot1.setImageResource(dotId[2]);
                        calDot1.setVisibility(View.VISIBLE);
                        calDot2.setImageResource(dotId[6]);
                        calDot2.setVisibility(View.VISIBLE);
                    }
                    else if (ran_num == 2) {
                        calDateText.setBackgroundResource(R.drawable.bigreddot);
                        calDot1.setImageResource(dotId[2]);
                        calDot1.setVisibility(View.VISIBLE);
                    }
                    
                    else {
                        calDateText.setBackgroundResource(R.drawable.biggraydot);
                        
                    }
                    
                }
                
            }
            else {
            	calDateText.setTextColor(context.getResources().getColor(R.color.text_gray2));
            	calDateText.setVisibility(View.INVISIBLE);
            }*/
            
            
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
                    calDateText.setTextColor(context.getResources().getColor(R.color.blue));
                }
            }
            
            glCalendar.addView(cellView);
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
            
            LayoutParams params=cellView.getLayoutParams();
            params.width=135;
            params.height=160;
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
