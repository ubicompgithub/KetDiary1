package com.ubicomp.ketdiary.statistic;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.db.DatabaseControl;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.Typefaces;

public class AnalysisProsConsView extends StatisticPageView {

	private TextView title;

	private TextView help;
	private TextView target, curMoney, targetMoney;
	private DatabaseControl db;

	private ImageView currentBar;
	private ImageView barStart, barEnd;
	private ImageView bar;

	private String targetGood;
	private int goal;
	private int drinkCost;
	private int currentMoney;

	private RelativeLayout layout;

	private Typeface wordTypeface, wordTypefaceBold, digitTypefaceBold;

	private String dollor_sign;

	private BarHandler barHandler = new BarHandler();

	public AnalysisProsConsView() {
		super(R.layout.analysis_saving_view);
		db = new DatabaseControl();
		
		wordTypeface = Typefaces.getWordTypeface();
		wordTypefaceBold=Typefaces.getDigitTypefaceBold();
		digitTypefaceBold = Typefaces.getDigitTypefaceBold();

		//targetGood = PreferenceControl.getSavingGoal();
		goal = PreferenceControl.getSavingGoalMoney();
		drinkCost = PreferenceControl.getSavingDrinkCost();

		dollor_sign = context.getResources().getString(R.string.dollor_sign);
		//title = (TextView) view.findViewById(R.id.analysis_saving_title);
		

		help = (TextView) view.findViewById(R.id.analysis_saving_help);
		help.setTypeface(wordTypefaceBold);
		targetMoney = (TextView) view.findViewById(R.id.analysis_target_money);
		targetMoney.setTypeface(wordTypefaceBold);

		currentBar = (ImageView) view
				.findViewById(R.id.analysis_pros_position);
		barStart = (ImageView) view
				.findViewById(R.id.analysis_pros_backward);
		barEnd = (ImageView) view
				.findViewById(R.id.analysis_pros_forward);
		bar = (ImageView) view.findViewById(R.id.analysis_saving_bar);

		layout = (RelativeLayout) view
				.findViewById(R.id.analysis_saving_content_layout);
	}

	@Override
	public void clear() {
		if (barHandler != null)
			barHandler.removeMessages(0);
	}

	@SuppressLint("CutPasteId")
	@Override
	public void load() {

		barHandler.sendEmptyMessage(0);

		//int curDrink = db.getPrimeDetectionPassTimes();
		//currentMoney = curDrink * drinkCost;

		String cur_money = dollor_sign + currentMoney;
		String goal_money = dollor_sign + goal;

		//curMoney.setText(cur_money);
		//targetMoney.setText(goal_money);
		//target.setText(targetGood);

	}

	@Override
	public void onCancel() {
		clear();
	}
	
	private class BarHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			/*
			int curDrink = db.getPrimeDetectionPassTimes();
			currentMoney = curDrink * drinkCost;

			int barWidth = bar.getRight() - bar.getLeft();
			int leftWidth = barStart.getRight() - barStart.getLeft();
			int rightWidth = barEnd.getRight() - barEnd.getLeft();

			int maxWidth = barWidth - leftWidth - rightWidth;
			int width;
			if (currentMoney > goal)
				width = maxWidth;
			else {
				width = maxWidth * currentMoney / goal;
			}

			RelativeLayout.LayoutParams currentBarParam = (RelativeLayout.LayoutParams) currentBar
					.getLayoutParams();
			currentBarParam.width = width;

			layout.updateViewLayout(currentBar, currentBarParam);*/
		}
	}

}
