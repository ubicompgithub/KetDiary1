package com.ubicomp.ketdiary.statistic;

import java.util.ArrayList;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.data.db.DatabaseControl;
import com.ubicomp.ketdiary.data.structure.Rank;
import com.ubicomp.ketdiary.dialog.QuestionDialog;
import com.ubicomp.ketdiary.main.fragment.StatisticFragment;
import com.ubicomp.ketdiary.system.Config;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.CustomToast;
import com.ubicomp.ketdiary.ui.Typefaces;

public class AnalysisCounterView extends StatisticPageView {

	private TextView title;
	private TextView levelHelp, levelHelp_value;
	private TextView levelValue, levelText, couponValue, couponText;
	private RelativeLayout titleLayout;
	private FrameLayout frameLayout;
	public static ImageView levelCircle, QuestionButton;
	
	private DatabaseControl db;
	private Typeface wordTypeface, digitTypefaceBold;
	private QuestionDialog msgBox;
	private ShowRadarChart showRadarChart;
	
	private AlphaAnimation titleAnimation;
	
	private final static int[] levelId = {R.drawable.level0,
		R.drawable.level1, R.drawable.level2, R.drawable.level3, 
		R.drawable.level4, R.drawable.level5, R.drawable.level6, 
	 	R.drawable.level7, R.drawable.level8, R.drawable.level9,
	 	R.drawable.level10};
	
	public AnalysisCounterView(ShowRadarChart showRadarChart) { //TODO: 1.讓加分的時候可以馬上看到圖有變 
		super(R.layout.analysis_counter_view2);
		db = new DatabaseControl();
		//this.showRadarChart = showRadarChart;
		this.showRadarChart = showRadarChart;
		
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
				QuestionButton.setEnabled(false);
				StatisticFragment.showQuestionTest();
			}
		});
			
		frameLayout = (FrameLayout) view.findViewById(R.id.frameLayout1);
		
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
		frameLayout.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				showRadarChart.showRadarChart(calculateRank());
			}
		});
		
		updateCounter();
	}
	private void setAnimation(){
		titleAnimation = new AlphaAnimation(1.0F, 0.0F);
		titleAnimation.setDuration(200);
		titleAnimation.setRepeatCount(Animation.INFINITE);
		titleAnimation.setRepeatMode(Animation.REVERSE);
		titleLayout.setAnimation(titleAnimation);
		titleAnimation.start();
	}

	@SuppressWarnings("deprecation")
	public void updateCounter() {
		
		int prev_coupon = PreferenceControl.lastShowedCoupon();
		int total_point = PreferenceControl.getPoint();
		int coupon = PreferenceControl.getCoupon();

		int level = total_point / Config.COUPON_CREDITS;		
		int counter = ( total_point/((Config.COUPON_CREDITS)/10) ) % 10;

		PreferenceControl.setShowedCoupon(coupon);
		PreferenceControl.setCouponChange(false);
		
		
		if (prev_coupon < coupon) {
			setAnimation();
			if (Build.VERSION.SDK_INT < 16)
				titleLayout.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.analysis_title_bar_highlight));
			else
				titleLayout.setBackground(context.getResources().getDrawable(
						R.drawable.analysis_title_bar_highlight));
		} else {
			if (Build.VERSION.SDK_INT < 16)
				titleLayout.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.analyse_line));
			else
				titleLayout.setBackground(context.getResources().getDrawable(
						R.drawable.analyse_line));
		}

		levelCircle.setImageResource(levelId[counter]);
		
		levelValue.setText("Lv."+level);
		levelValue.invalidate();
//		levelHelp.setText("目前等級:\n"+level+"級");
//		levelHelp.invalidate();
		levelHelp_value.setText(String.valueOf(level));
		levelHelp_value.invalidate();
		couponValue.setText(String.valueOf(coupon));
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
				coping = ranks[i].getCoping();
				question = ranks[i].getQuestion();
				note = ranks[i].getNote();
				break;
			}
		}

		double test_r, coping_r, question_r, note_r;
		test_r = (double) ((double) test) / 200.0;
		coping_r = (double) ((double) coping) / 300.0;
		question_r = (double) ((double) question) / 500.0;
		note_r = (double) ((double) note) / 300.0;

		result.add(test_r);
		result.add(coping_r);
		result.add(question_r);
		result.add(note_r);

		return result;
	}
	

	@Override
	public void onCancel() {
		clear();
	}

}
