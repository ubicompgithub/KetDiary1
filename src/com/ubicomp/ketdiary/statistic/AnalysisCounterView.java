package com.ubicomp.ketdiary.statistic;

import java.util.ArrayList;

import android.graphics.Typeface;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.data.structure.Rank;
import com.ubicomp.ketdiary.db.DatabaseControl;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.Typefaces;

public class AnalysisCounterView extends StatisticPageView {

	private TextView title;
	private TextView levelHelp;
	private TextView levelValue, levelText, couponValue, couponText;
	private RelativeLayout titleLayout;
	private ImageView levelCircle;
	
	private DatabaseControl db;
	private Typeface wordTypeface, digitTypefaceBold;
	//private ShowRadarChart showRadarChart;
	
	public AnalysisCounterView() {
		super(R.layout.analysis_counter_view2);
		db = new DatabaseControl();
		//this.showRadarChart = showRadarChart;
		
		wordTypeface = Typefaces.getWordTypeface();
		digitTypefaceBold = Typefaces.getDigitTypefaceBold();
		//title = (TextView) view.findViewById(R.id.analysis_counter_title);
		//title.setTypeface(wordTypeface);
		titleLayout = (RelativeLayout) view
				.findViewById(R.id.analysis_counter_title_layout);
		levelHelp = (TextView) view.findViewById(R.id.analysis_level_help);
		levelHelp.setTypeface(wordTypeface);
		
		levelValue = (TextView) view
				.findViewById(R.id.analysis_level_value);
		levelValue.setTypeface(digitTypefaceBold);
		
		levelCircle = (ImageView)view
				.findViewById(R.id.analysis_level_img);

		//levelText = (TextView) view.findViewById(R.id.analysis_counter_counter_text);
		//levelText.setTypeface(wordTypeface);

		couponText = (TextView) view
				.findViewById(R.id.analysis_counter_coupon_text);
		couponText.setTypeface(wordTypeface);
		couponValue = (TextView) view
				.findViewById(R.id.analysis_counter_coupon_value);
		couponValue.setTypeface(digitTypefaceBold);
	}

	@Override
	public void clear() {
	}

	@Override
	public void load() {
		/*levelValue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showRadarChart.showRadarChart(calculateRank());
			}
		});*/
		updateCounter();
	}

	@SuppressWarnings("deprecation")
	public void updateCounter() {
		
		int prev_coupon = PreferenceControl.lastShowedCoupon();
		int total_point = PreferenceControl.getPoint();

		int level = total_point / 10;		
		int counter = total_point % 10;

		//PreferenceControl.setShowedCoupon(coupon);
		//PreferenceControl.setCouponChange(false);
		
		/*
		if (prev_coupon < level) {
			if (Build.VERSION.SDK_INT < 16)
				titleLayout.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.analysis_title_bar_highlight));
			else
				titleLayout.setBackground(context.getResources().getDrawable(
						R.drawable.analysis_title_bar_highlight));
		} else {
			if (Build.VERSION.SDK_INT < 16)
				titleLayout.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.analysis_title_bar));
			else
				titleLayout.setBackground(context.getResources().getDrawable(
						R.drawable.analysis_title_bar));
		}*/
		switch(counter){
		case 0:
			levelCircle.setImageResource(R.drawable.level0);
			break;
		case 1:
			levelCircle.setImageResource(R.drawable.level1);
			break;
		case 2:
			levelCircle.setImageResource(R.drawable.level2);
			break;
		case 3:
			levelCircle.setImageResource(R.drawable.level3);
			break;
		case 4:
			levelCircle.setImageResource(R.drawable.level4);
			break;
		case 5:
			levelCircle.setImageResource(R.drawable.level5);
			break;
		case 6:
			levelCircle.setImageResource(R.drawable.level6);
			break;
		case 7:
			levelCircle.setImageResource(R.drawable.level7);
			break;
		case 8:
			levelCircle.setImageResource(R.drawable.level8);
			break;
		case 9:
			levelCircle.setImageResource(R.drawable.level9);
			break;
	
		}
	
		levelValue.setText("Lv."+level);
		levelValue.invalidate();
		levelHelp.setText("目前等級:\n"+level+"級");
		levelHelp.invalidate();
		
		couponValue.setText(String.valueOf(level));
		couponValue.invalidate();
	}
	
	
	
	private ArrayList<Double> calculateRank() {
		ArrayList<Double> result = new ArrayList<Double>();
		Rank[] ranks = db.getAllRanks();
		if (ranks == null) {
			result.add(0.1);
			result.add(0.1);
			result.add(0.1);
			result.add(0.1);
			return result;
		}
		String uid = PreferenceControl.getUID();
		int test = 0, advice = 0, manage = 0, story = 0;
		for (int i = 0; i < ranks.length; ++i) {
			if (ranks[i].getUid().equals(uid)) {
				test = ranks[i].getTest();
				advice = ranks[i].getAdvice();
				manage = ranks[i].getManage();
				story = ranks[i].getStory();
				break;
			}
		}

		double test_r, advice_r, manage_r, story_r;
		test_r = (double) ((double) test) / 600.0;
		advice_r = (double) ((double) advice) / 600.0;
		manage_r = (double) ((double) manage) / 700.0;
		story_r = (double) ((double) story) / 600.0;

		result.add(test_r);
		result.add(advice_r);
		result.add(manage_r);
		result.add(story_r);

		return result;
	}
	

	@Override
	public void onCancel() {
		clear();
	}

}
