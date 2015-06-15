package com.ubicomp.ketdiary.mydaybook;

import java.util.Calendar;
import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.R;

public class SectionFragment extends Fragment {
	
    public static final String FRAGMENT_MONTH = "fragment_month";

    private int fragmentMonth;
    private static final int thisDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    private static final int thisMonth = Calendar.getInstance().get(Calendar.MONTH);

    private GridLayout glCalendar;
    private ScrollView sv;
    private Context context;
    
    private TextView todayButton;
    private static View LastSelected;

    private static View viewToday;
    private static int selectedDay = thisDay;
    private static int selectedMonth = thisMonth;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = App.getContext();
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

    	sv = (ScrollView) getActivity().findViewById(R.id.diary_view);
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        glCalendar = (GridLayout) rootView.findViewById(R.id.gl_calendar);
        
        fragmentMonth = getArguments().getInt(FRAGMENT_MONTH);
        
        initCalendarView(inflater, container);
        
        // Set the today button
		todayButton = (TextView) getActivity().findViewById(R.id.back_to_today);
		todayButton.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				selectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				selectedMonth = Calendar.getInstance().get(Calendar.MONTH);
				//((MainActivity) getActivity()).setCurrentCalendarPage(thisMonth + 1 - Database.START_MONTH);
				
				sv.smoothScrollTo(0 , 270*(thisDay+4)-1350-900);
				
				// Reset the last selected view			
				String date_month_year_last = (String) LastSelected.getTag();
				String [] parsed_date_last = date_month_year_last.split("-");

				TextView dt = (TextView) LastSelected.findViewById(R.id.tv_calendar_date);
				int lm = Integer.valueOf(parsed_date_last[1]); 
				int lf = Integer.valueOf(parsed_date_last[3]);
				
				int nf = selectedMonth - Database.START_MONTH + 1;

				if (lf == nf ) {
					if (lm == lf + 1) {
						dt.setTextColor(Color.WHITE);
					}
					else {dt.setTextColor(Color.BLACK);}
				}
				else {
					if (lm == lf + 1) {
						dt.setTextColor(Color.WHITE);
					}
					else {dt.setTextColor(Color.BLACK);}
				}
				
				TextView textViewToday = (TextView) viewToday.findViewById(R.id.tv_calendar_date);
				textViewToday.setTextColor(Color.BLUE);
				LastSelected = viewToday;

			}
		});

        return rootView;
    }
    
    
    private void initCalendarView(LayoutInflater inflater, ViewGroup container){
		
		Calendar mCalendar = Calendar.getInstance();
		int maxDaysOfWeek = mCalendar.getMaximum(Calendar.DAY_OF_WEEK);
		
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		
		mCalendar.set(Calendar.MONTH, fragmentMonth);
		int maxWeeksOfMonth = mCalendar.getActualMaximum(Calendar.WEEK_OF_MONTH);

		mCalendar.set(Calendar.DAY_OF_MONTH, 1);

		mCalendar.add(Calendar.DAY_OF_MONTH, -(mCalendar.get(Calendar.DAY_OF_WEEK)-2));

		
		View cellView;
		TextView calDateText;

		for (int i=0;i<maxWeeksOfMonth*maxDaysOfWeek;++i){
					
			cellView = inflater.inflate(R.layout.calendar_cell, null, false);
			calDateText = (TextView) cellView.findViewById(R.id.tv_calendar_date);
						
			cellView.setTag(Integer.toString(mCalendar.get(Calendar.DAY_OF_MONTH))+ "-"+ Integer.toString(mCalendar.get(Calendar.MONTH)+1)+ "-" +Integer.toString(mCalendar.get(Calendar.YEAR)) + "-" + Integer.toString(fragmentMonth));
			cellView.setOnClickListener(new View.OnClickListener() {
				
					@Override
					public void onClick(View v) {
						
						String date_month_year = (String) v.getTag();
						String [] parsed_date = date_month_year.split("-");
						
						String date_month_year_last = (String) LastSelected.getTag();
						String [] parsed_date_last = date_month_year_last.split("-");
						
						TextView date_text = (TextView) v.findViewById(R.id.tv_calendar_date);
    					date_text.setTextColor(Color.BLUE);
						   					
						// check if the two views are the same one
						if (LastSelected != v) {
							TextView dt = (TextView) LastSelected.findViewById(R.id.tv_calendar_date);
							int lm = Integer.valueOf(parsed_date_last[1]); 
							int lf = Integer.valueOf(parsed_date_last[3]);
							
							int nm = Integer.valueOf(parsed_date[1]);
							int nf = Integer.valueOf(parsed_date[3]);
							if (lf == nf ) {
								if (lm == lf + 1) {
									dt.setTextColor(Color.WHITE);
								}
								else {dt.setTextColor(Color.BLACK);}
							}
							else {
								if (lm == lf + 1) {
									dt.setTextColor(Color.WHITE);
								}
								else {dt.setTextColor(Color.BLACK);}
							}
						}
						
    					Toast.makeText(context, date_month_year, Toast.LENGTH_SHORT).show();
    					
    					sv.smoothScrollTo(0 , 270*(Integer.parseInt(parsed_date[0])+4)-1350-900);
				
    					LastSelected = v;
    				    					
    					// Record the selected date
    					selectedDay = Integer.parseInt(parsed_date[0]);
    					selectedMonth = Integer.parseInt(parsed_date[1]) - 1;
    					
					}
			});
			
			calDateText.setGravity(Gravity.CENTER);
			
			calDateText.setText(mCalendar.get(Calendar.DAY_OF_MONTH) + "");
			
			// Set cells that belong to current month 
			if ( mCalendar.get(Calendar.MONTH) == fragmentMonth ){
				calDateText.setBackgroundResource(R.drawable.bigbluedot);
				
				Random r = new Random();
				int ran_num = r.nextInt(4 - 1) + 1;
				
				if (ran_num == 1) {}
				else if (ran_num == 2) {calDateText.setBackgroundResource(R.drawable.bigreddot);}
				else {calDateText.setBackgroundResource(R.drawable.biggraydot);}
				
			}
			else {calDateText.setTextColor(Color.BLACK);}
			
			
			// Initialize the selected view on current day
			if (mCalendar.get(Calendar.DAY_OF_MONTH) == thisDay && mCalendar.get(Calendar.MONTH) == thisMonth) {
				//MainActivity.todayTextForSectionFragment = calDateText;
		        // MainActivity.todayTextForSectionFragment = calDateText;
		        viewToday = cellView;
				
			}
			// Highlight the selected day
			if (mCalendar.get(Calendar.DAY_OF_MONTH) == selectedDay && mCalendar.get(Calendar.MONTH) == selectedMonth) {
				LastSelected = cellView;
				calDateText.setTextColor(Color.BLUE);
			}
			
			glCalendar.addView(cellView);
			mCalendar.add(Calendar.DAY_OF_MONTH, 1);
			
			LayoutParams params=cellView.getLayoutParams();
			params.width=135;
			params.height=160;
			cellView.setLayoutParams(params);
		}

	}

}
