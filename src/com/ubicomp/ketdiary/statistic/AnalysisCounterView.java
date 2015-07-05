package com.ubicomp.ketdiary.statistic;

import java.util.ArrayList;

import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.data.structure.Rank;
import com.ubicomp.ketdiary.db.DatabaseControl;
import com.ubicomp.ketdiary.dialog.QuestionDialog;
import com.ubicomp.ketdiary.fragment.StatisticFragment;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.Typefaces;

public class AnalysisCounterView extends StatisticPageView {

	private TextView title;
	private TextView levelHelp, levelHelp_value;
	private TextView levelValue, levelText, couponValue, couponText;
	private RelativeLayout titleLayout;
	private ImageView levelCircle, QuestionButton;
	
	private DatabaseControl db;
	private Typeface wordTypeface, digitTypefaceBold;
	private QuestionDialog msgBox;
	//private ShowRadarChart showRadarChart;
	
	private final static int[] levelId = {R.drawable.level0,
		R.drawable.level1, R.drawable.level2, R.drawable.level3, 
		R.drawable.level4, R.drawable.level5, R.drawable.level6, 
	 	R.drawable.level7, R.drawable.level8, R.drawable.level9,
	 	R.drawable.level10};
	
	public AnalysisCounterView() { //TODO: 1.讓加分的時候可以馬上看到圖有變 2. 雷達圖
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
		levelHelp.setTypeface(digitTypefaceBold);
		levelHelp_value=(TextView) view.findViewById(R.id.analysis_level_help_value);
		levelHelp_value.setTypeface(digitTypefaceBold);
		levelValue = (TextView) view
				.findViewById(R.id.analysis_level_value);
		levelValue.setTypeface(digitTypefaceBold);
		
		levelCircle = (ImageView)view
				.findViewById(R.id.analysis_level_img);

		//levelText = (TextView) view.findViewById(R.id.analysis_counter_counter_text);
		//levelText.setTypeface(wordTypeface);

		couponText = (TextView) view
				.findViewById(R.id.analysis_counter_coupon_text);
		couponText.setTypeface(digitTypefaceBold);
		couponValue = (TextView) view
				.findViewById(R.id.analysis_counter_coupon_value);
		couponValue.setTypeface(digitTypefaceBold);
		
		QuestionButton = (ImageView) view
				.findViewById(R.id.analysis_question);
		
		QuestionButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StatisticFragment.showQuestionTest();
			}
		});
			
		
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
		//msgBox = new QuestionDialog();
		
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

		levelCircle.setImageResource(levelId[counter]);
		
		levelValue.setText("Lv."+level);
		levelValue.invalidate();
//		levelHelp.setText("目前等級:\n"+level+"級");
//		levelHelp.invalidate();
		levelHelp_value.setText(String.valueOf(level));
		levelHelp_value.invalidate();
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
		int test = 0, note = 0, question = 0, coping = 0;
		for (int i = 0; i < ranks.length; ++i) {
			if (ranks[i].getUid().equals(uid)) {
				test = ranks[i].getTest();
				note = ranks[i].getNote();
				question = ranks[i].getQuestion();
				coping = ranks[i].getCoping();
				break;
			}
		}

		double test_r, advice_r, manage_r, story_r;
		test_r = (double) ((double) test) / 600.0;
		advice_r = (double) ((double) note) / 600.0;
		manage_r = (double) ((double) question) / 700.0;
		story_r = (double) ((double) coping) / 600.0;

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
