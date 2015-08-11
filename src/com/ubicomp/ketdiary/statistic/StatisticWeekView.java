package com.ubicomp.ketdiary.statistic;

import java.util.Calendar;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.data.db.DatabaseControl;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.Typefaces;

public class StatisticWeekView extends StatisticPageView {
	
	private static String TAG = "Week";
	
	private DatabaseControl db;
	private TextView[] time_labels;
	private TextView[] date_labels;
	private Drawable[] circleDrawables;
	private Drawable[] linkerDrawables;
	private ImageView[] circles;
	private ImageView[] linkers;

	private LinearLayout dateLayout;
	private LinearLayout timeLayout;
	private GridLayout blockLayout;
	private GridLayout linkLayout;
	private LinearLayout labelLayout;

	private TextView[] labels;
	private ImageView[] labelImgs;

	private TextView title;

	private static final int nBlocks = 3;
	private static final int nDays = 7;
	
	private static final int notest = -1;
	private static final int pass = 0;
	private static final int nopass = 1;

	private static final int FIRST_DAY = Calendar.MONDAY;
	
	//private static final int[] blockHint = { R.string.sunday, R.string.monday, R.string.tuesday, R.string.wednesday, 
	//		R.string.thursday, R.string.friday, R.string.saturday };
	private static final int[] labelHint = { R.string.test_pass,
			R.string.test_fail, R.string.test_none };

	private Typeface digitTypefaceBold;
	private Typeface wordTypefaceBold;

	private Calendar startDate;

	private static final int text_color = App.getContext().getResources()
			.getColor(R.color.text_gray);
	private static final int text_color2 = App.getContext().getResources()
			.getColor(R.color.text_gray2);
	private static final float ALPHA = 0.4F;

	public StatisticWeekView() {
		super(R.layout.statistic_week_view);
		db = new DatabaseControl();
		digitTypefaceBold = Typefaces.getDigitTypefaceBold();
		wordTypefaceBold = Typefaces.getWordTypefaceBold();
		startDate = PreferenceControl.getStartDate();

		dateLayout = (LinearLayout) view
				.findViewById(R.id.statistic_week_date_label_layout);
		timeLayout = (LinearLayout) view
				.findViewById(R.id.statistic_week_timeblock_label_layout);
		blockLayout = (GridLayout) view
				.findViewById(R.id.statistic_week_block_layout);
		linkLayout = (GridLayout) view
				.findViewById(R.id.statistic_week_link_layout);
		title = (TextView) view.findViewById(R.id.statistic_week_title);
		title.setTypeface(wordTypefaceBold);

		labelLayout = (LinearLayout) view
				.findViewById(R.id.statistic_week_label_layout);

		circleDrawables = new Drawable[3];
		circleDrawables[0] = context.getResources().getDrawable(
				R.drawable.statistic_week_none);
		circleDrawables[1] = context.getResources().getDrawable(
				R.drawable.statistic_week_fail);
		circleDrawables[2] = context.getResources().getDrawable(
				R.drawable.statistic_week_pass);
		linkerDrawables = new Drawable[3];
		linkerDrawables[0] = context.getResources().getDrawable(
				R.drawable.statistic_week_linker_none);
		linkerDrawables[1] = context.getResources().getDrawable(
				R.drawable.statistic_week_linker_fail);
		linkerDrawables[2] = context.getResources().getDrawable(
				R.drawable.statistic_week_linker_pass);
	}

	@Override
	public void clear() {
	}

	@Override
	public void load() {

		int textSize = (int) App.getContext().getResources()
				.getDimensionPixelSize(R.dimen.normal_text_size);
		time_labels = new TextView[nDays];
		for (int i = 0; i < nDays; ++i) {
			time_labels[i] = new TextView(context);
			time_labels[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			time_labels[i].setTextColor(text_color2);
			//time_labels[i].setText(blockHint[i]);
			time_labels[i].setTypeface(wordTypefaceBold);
			time_labels[i].setGravity(Gravity.CENTER);
			timeLayout.addView(time_labels[i]);
		}

		date_labels = new TextView[nDays];
		for (int i = 0; i < nDays; ++i) {
			date_labels[i] = new TextView(context);
			date_labels[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			date_labels[i].setTextColor(text_color2);
			date_labels[i].setGravity(Gravity.CENTER);
			date_labels[i].setTypeface(digitTypefaceBold);
			dateLayout.addView(date_labels[i]);
		}

		circles = new ImageView[ nDays];
		linkers = new ImageView[ nDays];
		for (int i = 0; i <  nDays; ++i) {
			circles[i] = new ImageView(context);
			blockLayout.addView(circles[i]);
			circles[i].setScaleType(ScaleType.CENTER);
		}
		for (int i = 0; i <  nDays-1; ++i) {
			linkers[i] = new ImageView(context);
			linkLayout.addView(linkers[i]);
			linkers[i].setScaleType(ScaleType.CENTER);
		}
		
		labels = new TextView[3];
		labelImgs = new ImageView[3];
		for (int i = 0; i < 3; ++i) {
			labelImgs[i] = new ImageView(context);
			labelImgs[i].setScaleType(ScaleType.CENTER);
			labelLayout.addView(labelImgs[i]);
			labels[i] = new TextView(context);
			labels[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			labels[i].setTextColor(text_color2);
			labels[i].setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			labels[i].setTypeface(wordTypefaceBold);
			labels[i].setText(labelHint[i]);
			labelLayout.addView(labels[i]);
		}

		int c_width = context.getResources().getDimensionPixelSize(
				R.dimen.week_circle_width);
		int c_height = context.getResources().getDimensionPixelSize(
				R.dimen.week_circle_height);


		for (int i = 0; i < nDays; ++i) {
			
			LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) date_labels[i]
					.getLayoutParams();
			LinearLayout.LayoutParams param2 = (LinearLayout.LayoutParams) time_labels[i]
					.getLayoutParams();
			param.width = c_width* 14/13;
			param.height = c_height* 2/3;
			param2.width = c_width* 14/13;
			param2.height = c_height* 2/3;
		}

		for (int i = 0; i < nDays; ++i) {
			GridLayout.LayoutParams cParam = (GridLayout.LayoutParams) circles[i]
					.getLayoutParams();
			cParam.width = c_width* 14/13;
			cParam.height = c_height* 2/3;
		}
		for (int i = 0; i < nDays-1; ++i) {
			GridLayout.LayoutParams lParam = (GridLayout.LayoutParams) linkers[i]
					.getLayoutParams();
			lParam.width = c_width* 14/13;
			lParam.height = c_height* 2/3;
		}

		for (int i = 0; i < 3; ++i) {
			LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) labels[i]
					.getLayoutParams();
			param.width = c_width * 3 / 2;
			param.height = c_height;
			param = (LinearLayout.LayoutParams) labelImgs[i].getLayoutParams();
			param.width = c_width * 3 / 4;
			param.height = c_height;
		}
		/*
		Float[] bracs = db.getMultiDaysPrimeBrac(nDays);

		int cur_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

		for (int i = 0; i < bracs.length; ++i) {
			int idx = (i % nBlocks) * nDays + i / nBlocks;
			if (bracs[i] == null) {
				circles[idx].setImageDrawable(circleDrawables[0]);
				if (i >= bracs.length - nBlocks
						&& TimeBlock.isEmpty(i % nBlocks, cur_hour))
					circles[idx].setAlpha(ALPHA);
			} else if (bracs[i] < Detection.BRAC_THRESHOLD)
				circles[idx].setImageDrawable(circleDrawables[2]);
			else
				circles[idx].setImageDrawable(circleDrawables[1]);
		}*/
		//Integer[] brave={1,1,-1,0,-1,1,99}; // 1:pass 0:none -1:fail
		
		//Integer[] brave={0,0,0,0,0,0,0};
		//int[] brave = db.getMultiDaysPrimeBrac(nDays);
		
		int[] brave = db.getWeeklyPrimeBrac(); 
		
		for(int i=0;i<brave.length;++i){
			Log.d(TAG, ""+brave[i]);
			if(brave[i]==1){
				circles[i].setImageDrawable(circleDrawables[1]);				
			}
			else if(brave[i]==-1){
				circles[i].setImageDrawable(circleDrawables[0]);
			}
			else if(brave[i]==0){
				circles[i].setImageDrawable(circleDrawables[2]);
				if(i>=1 && brave[i-1]==0){
					linkers[i-1].setImageDrawable(linkerDrawables[2]);
				}
				else if(i>=1 && brave[i-1]!=0){
					linkers[i-1].setImageDrawable(linkerDrawables[0]);
				}
			}
			else{}	
		}
		
		int count = 0;
		Calendar cal3 = Calendar.getInstance();
		while (cal3.get(Calendar.DAY_OF_WEEK) != FIRST_DAY) {
			cal3.add(Calendar.DATE, -1);
			count++;
        }
		for (int i = 0; i < 7; ++i) {
			int date = cal3.get(Calendar.DAY_OF_MONTH);
			String label = String.valueOf(date);
			date_labels[i].setText(label);
			
			if (cal3.before(startDate)) {
				circles[i].setAlpha(ALPHA);
			}
			
			switch(i){
		    case 6:
		    	time_labels[i].setText("日");
		    	break;
		    case 0:
		    	time_labels[i].setText("一");
		    	break;
		    case 1:
		    	time_labels[i].setText("二");
		    	break;
		    case 2:
		    	time_labels[i].setText("三");
		    	break;
		    case 3:
		    	time_labels[i].setText("四");
		    	break;
		    case 4:
		    	time_labels[i].setText("五");
		    	break;
		    case 5:
		    	time_labels[i].setText("六");
		    }
			cal3.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		
		/*
		Calendar cal = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		for (int i = 6; i >= 0; --i) {
			int date = cal.get(Calendar.DAY_OF_MONTH);
			int dayofweek = cal2.get(Calendar.DAY_OF_WEEK);
			String label = String.valueOf(date);
			date_labels[i].setText(label);
			
			if (cal.before(startDate)) {
				circles[i].setAlpha(ALPHA);
			}
			
			switch(dayofweek){
		    case Calendar.SUNDAY:
		    	time_labels[i].setText("日");
		    	break;
		    case Calendar.MONDAY:
		    	time_labels[i].setText("一");
		    	break;
		    case Calendar.TUESDAY:
		    	time_labels[i].setText("二");
		    	break;
		    case Calendar.WEDNESDAY:
		    	time_labels[i].setText("三");
		    	break;
		    case Calendar.THURSDAY:
		    	time_labels[i].setText("四");
		    	break;
		    case Calendar.FRIDAY:
		    	time_labels[i].setText("五");
		    	break;
		    case Calendar.SATURDAY:
		    	time_labels[i].setText("六");
		    }
			cal.add(Calendar.DAY_OF_MONTH, -1);
			cal2.add(Calendar.DAY_OF_WEEK, -1);
		}*/

//		if (cal.before(startDate)) {
//			for (int j = 0; j < 21; j += 7) {
//				circles[j].setAlpha(ALPHA);
//			}
//		}

//		int month = cal.get(Calendar.MONTH) + 1;
//		int date = cal.get(Calendar.DAY_OF_MONTH);
//		String month_label = month + "/" + date;
//		date_labels[0].setText(month_label);

		labelImgs[0].setImageDrawable(circleDrawables[2]);
		labelImgs[1].setImageDrawable(circleDrawables[1]);
		labelImgs[2].setImageDrawable(circleDrawables[0]);

	}

	@Override
	public void onCancel() {
		clear();
	}

}
