package com.ubicomp.ketdiary.statistic;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.db.DatabaseControl;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.Typefaces;

public class AnalysisProsConsView extends StatisticPageView {

	private TextView title;
	private static final String TAG = "ProsCons";
	private TextView help;
	private TextView target, curMoney, targetMoney;
	private DatabaseControl db;

	private ImageView currentBar;
	private ImageView barStart, barEnd, bar_followed;
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
		
		//bar.setVisibility(View.INVISIBLE);
		help = (TextView) view.findViewById(R.id.analysis_saving_help);
		help.setTypeface(wordTypefaceBold);
		targetMoney = (TextView) view.findViewById(R.id.analysis_target_money);
		targetMoney.setTypeface(wordTypefaceBold);
		
		help.setText(PreferenceControl.getNegativeGoal());
		targetMoney.setText(PreferenceControl.getPostiveGoal());

		currentBar = (ImageView) view
				.findViewById(R.id.analysis_pros_position);
		bar_followed=(ImageView) view
				.findViewById(R.id.analysis_pros_position_followed);
		barStart = (ImageView) view
				.findViewById(R.id.analysis_pros_backward);
		barEnd = (ImageView) view
				.findViewById(R.id.analysis_pros_forward);
		bar = (ImageView) view.findViewById(R.id.analysis_saving_bar);

		layout = (RelativeLayout) view
				.findViewById(R.id.analysis_position_followed_layout);
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

		//String cur_money = dollor_sign + currentMoney;
		//String goal_money = dollor_sign + goal;

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
			
			//int today_situation=19; //pass or fail
			int today_situation = PreferenceControl.getPosition();
			/*
			int result = db.getTodayPrimeResult();
			int today_situation=0;
			
			if(result == 0)
				today_situation = 1;
			else 
				today_situation = -1;
			*/
			//Log.d(TAG, today_situation+"");
//			int curDrink = db.getPrimeDetectionPassTimes();
//			currentMoney = curDrink * drinkCost;

			int barWidth = bar.getRight() - bar.getLeft();
			int positionWidth =currentBar.getRight() - currentBar.getLeft();
			int leftWidth = barStart.getRight() - barStart.getLeft();
			int rightWidth = barEnd.getRight() - barEnd.getLeft();
			int curWidth = bar_followed.getRight()- bar_followed.getLeft();
			int maxWidth = barWidth - positionWidth;
			int width_per_block=maxWidth/21;
			int nextWidth;
			//nextWidth=curWidth+width_per_block*today_situation;
			
			nextWidth = width_per_block*today_situation;
			System.out.println(nextWidth);
			
			
			if(nextWidth>curWidth){
				if(nextWidth>=maxWidth){
					nextWidth=maxWidth;
					barEnd.setVisibility(View.INVISIBLE);
					barStart.setVisibility(View.INVISIBLE);
					}
				else{
					barStart.setVisibility(View.INVISIBLE);
					barEnd.setVisibility(View.VISIBLE);
				}
			}
			else if(nextWidth<curWidth){
				if(nextWidth<=0){
					nextWidth=0;
					barStart.setVisibility(View.INVISIBLE);
					barEnd.setVisibility(View.INVISIBLE);
				}
				else{
					barStart.setVisibility(View.VISIBLE);
					barEnd.setVisibility(View.INVISIBLE);
					
				}
			}
			else{
				barStart.setVisibility(View.INVISIBLE);
				barEnd.setVisibility(View.INVISIBLE);
			}
			RelativeLayout.LayoutParams currentBarParam = (RelativeLayout.LayoutParams) bar_followed.getLayoutParams();
			currentBarParam.width = nextWidth;
			layout.updateViewLayout(bar_followed, currentBarParam);
//			if (currentMoney > goal)
//				width = maxWidth;
//			else {
//				width = maxWidth * currentMoney / goal;
//			}
//
//			RelativeLayout.LayoutParams currentBarParam1 = (RelativeLayout.LayoutParams) currentBar
//					.getLayoutParams();
//			currentBarParam1.width = width;
//
//			layout.updateViewLayout(currentBar, currentBarParam1);
		}
	}

}
