package com.ubicomp.ketdiary.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.ubicomp.ketdiary.CopingActivity;
import com.ubicomp.ketdiary.MainActivity;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.db.DatabaseControl;
import com.ubicomp.ketdiary.dialog.QuestionDialog;
import com.ubicomp.ketdiary.statistic.AnalysisCounterView;
import com.ubicomp.ketdiary.statistic.AnalysisProsConsView;
import com.ubicomp.ketdiary.statistic.AnalysisRankView;
import com.ubicomp.ketdiary.statistic.DetailChart;
import com.ubicomp.ketdiary.statistic.RadarChart;
import com.ubicomp.ketdiary.statistic.ShowRadarChart;
import com.ubicomp.ketdiary.statistic.StatisticPageView;
import com.ubicomp.ketdiary.statistic.StatisticPagerAdapter;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.LoadingDialogControl;
import com.ubicomp.ketdiary.ui.ScaleOnTouchListener;

public class StatisticFragment extends Fragment implements ShowRadarChart{
	
	/*
	private View view;
	private Activity activity;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = getActivity();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_statistic, container, false);
		
		return view;
	}*/
	private static final String TAG = "Statistic";
	
	private StatisticPageView[] analysisViews;
	private View view;
	private Activity activity;
	private ViewPager statisticView;
	private StatisticPagerAdapter statisticViewAdapter;
	private RelativeLayout allLayout;
	private ImageView[] dots;
	private Drawable dot_on, dot_off;
	private LinearLayout analysisLayout;
	private ScrollView analysisView;
	private LoadingHandler loadHandler;
	private StatisticFragment statisticFragment;

	private ImageView questionButton;

	private AlphaAnimation questionAnimation;

	private static QuestionDialog msgBox;
	private RadarChart radarChart;
	private DetailChart detailChart;
	

	private DatabaseControl db;

	private int notify_action = 0;

	// private static final String TAG = "STATISTIC";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = getActivity();
		radarChart = new RadarChart(activity);
		detailChart = new DetailChart(activity);
		dot_on = getResources().getDrawable(R.drawable.statistic_node_yes);
		dot_off = getResources().getDrawable(R.drawable.statistic_node_no);
		
		db = new DatabaseControl();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_statistic, container, false);

		allLayout = (RelativeLayout) view
				.findViewById(R.id.statistic_fragment_layout);
		analysisLayout = (LinearLayout) view
				.findViewById(R.id.brac_analysis_layout);
		analysisView = (ScrollView) view.findViewById(R.id.brac_analysis);
		statisticView = (ViewPager) view.findViewById(R.id.brac_statistics);
		questionButton = (ImageView) view.findViewById(R.id.question_button);
		dots = new ImageView[3];
		dots[0] = (ImageView) view.findViewById(R.id.brac_statistic_dot0);
		dots[1] = (ImageView) view.findViewById(R.id.brac_statistic_dot1);
		dots[2] = (ImageView) view.findViewById(R.id.brac_statistic_dot2);

		questionButton.setOnTouchListener(new ScaleOnTouchListener());
		loadHandler = new LoadingHandler();

		analysisLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent motion) {
				//if (motion.getAction() == MotionEvent.ACTION_DOWN)
					//ClickLog.Log(ClickLogId.STATISTIC_ANALYSIS);
				return false;
			}
		});
		
		return view;
	}

	public void onResume() {
		super.onResume();
		
		long curTime = System.currentTimeMillis();
		long testTime = PreferenceControl.getLatestTestCompleteTime();
		long pastTime = curTime - testTime;
			
		if( PreferenceControl.getCheckResult() && pastTime >= MainActivity.WAIT_RESULT_TIME){
			MainActivity.getMainActivity().checkResultAddPoint();
			/*
			int addScore = PreferenceControl.getTestAddScore();
			
			PreferenceControl.setPoint(addScore);
			
			Log.d(TAG, "AddScore:"+addScore);
			int result = db.getLatestTestResult().getResult(); //TODO: check if no data
			
			if (addScore == 0 && result == 1) // TestFail & get no credit //TODO: check TestResult
				CustomToast.generateToast(R.string.after_test_fail, -1);
			else if(result == 1)
				CustomToast.generateToast(R.string.after_test_fail, addScore);
			else
				CustomToast.generateToast(R.string.after_test_pass, addScore);
			
			PreferenceControl.setCheckResult( false );*/
		}
		else if ( PreferenceControl.getCheckResult() && pastTime < MainActivity.WAIT_RESULT_TIME ){
			
		}
		else{
			//CustomToast.generateToast(R.string.after_test_pass, 2);
		}
		
		//ClickLog.Log(ClickLogId.STATISTIC_ENTER);
		enablePage(true);
		statisticFragment = this;
		
		analysisViews = new StatisticPageView[3];
		//analysisViews[0] = new AnalysisCounterView();
		analysisViews[0] = new AnalysisProsConsView();
		analysisViews[1] = new AnalysisCounterView();
		analysisViews[2] = new AnalysisRankView(statisticFragment);
		
		statisticViewAdapter = new StatisticPagerAdapter();
		msgBox = new QuestionDialog((RelativeLayout) view);

		Bundle data = getArguments();
		if (data != null) {
			int action = data.getInt("action");
			data.putInt("action", 0);
			if (action == MainActivity.ACTION_QUESTIONNAIRE) {
				notify_action = action;
			}
		}
		
		//msgBox.generateCopingDialog();

		loadHandler.sendEmptyMessage(0);
	}

	public void onPause() {
		if (loadHandler != null)
			loadHandler.removeMessages(0);
		clear();
		super.onPause();
	}

	private void clear() {
		//removeRadarChart();
		statisticViewAdapter.clear();
		if (analysisLayout != null)
			analysisLayout.removeAllViews();
		
		for (int i = 0; i < analysisViews.length; ++i) {
			if (analysisViews[i] != null)
				analysisViews[i].clear();
		}
		if (msgBox != null)
			msgBox.clear();
	}

	private class StatisticOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int idx) {

			switch (idx) {
			case 0:
				//ClickLog.Log(ClickLogId.STATISTIC_TODAY);
				break;
			case 1:
				//ClickLog.Log(ClickLogId.STATISTIC_WEEK);
				break;
			case 2:
				//ClickLog.Log(ClickLogId.STATISTIC_MONTH);
				break;
			}
			for (int i = 0; i < 3; ++i)
				dots[i].setImageDrawable(dot_off);
			dots[idx].setImageDrawable(dot_on);
		}

	}

	@SuppressLint("HandlerLeak")
	private class LoadingHandler extends Handler {
		public void handleMessage(Message msg) {
			MainActivity.getMainActivity().enableTabAndClick(false);
			statisticView.setAdapter(statisticViewAdapter);
			statisticView
					.setOnPageChangeListener(new StatisticOnPageChangeListener());
			statisticView.setSelected(true);
			analysisLayout.removeAllViews();

			questionButton.setOnClickListener(new QuestionOnClickListener());
			
			for (int i = 0; i < analysisViews.length; ++i)
				if (analysisViews[i] != null)
					analysisLayout.addView(analysisViews[i].getView());


			statisticViewAdapter.load();
			
			for (int i = 0; i < analysisViews.length; ++i)
				if (analysisViews[i] != null)
					analysisViews[i].load();
			
			statisticView.setCurrentItem(0);

			for (int i = 0; i < 3; ++i)
				dots[i].setImageDrawable(dot_off);
			dots[0].setImageDrawable(dot_on);

			if (msgBox != null)
				msgBox.initialize();

			questionAnimation = new AlphaAnimation(1.0F, 0.0F);
			questionAnimation.setDuration(200);
			questionAnimation.setRepeatCount(Animation.INFINITE);
			questionAnimation.setRepeatMode(Animation.REVERSE);

			//setQuestionAnimation();

			MainActivity.getMainActivity().enableTabAndClick(true);
			LoadingDialogControl.dismiss();

			if (notify_action == MainActivity.ACTION_QUESTIONNAIRE) {
				//openQuestionnaire();
				//msgBox.generateCopingDialog();
				notify_action = 0;
			}
			
		}
	}

	private class QuestionOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			//ClickLog.Log(ClickLogId.STATISTIC_QUESTION_BUTTON);
			//openQuestionnaire();
			Intent intent = new Intent();
			intent.setClass(activity, CopingActivity.class);
			activity.startActivity(intent);
		}
	}

	public void setQuestionAnimation() {
		questionButton.setVisibility(View.VISIBLE);
		int result = PreferenceControl.getTestResult();
		if (result == -1) {
			questionAnimation.cancel();
			questionButton.setAnimation(null);
			if (Build.VERSION.SDK_INT >= 11)
				questionButton.setAlpha(1.0F);
		} else {
			questionButton.setAnimation(questionAnimation);
			questionAnimation.start();
		}
	}

	public void enablePage(boolean enable) {
		statisticView.setEnabled(enable);
		analysisView.setEnabled(enable);
		questionButton.setEnabled(enable);
		MainActivity.getMainActivity().enableTabAndClick(enable);
	}


	public Context getContext() {
		return this.getActivity();
	}
	
	private View qv;
	public static void showQuestionTest() {
		//removeRadarChart();
		//removeDetailChart();
		msgBox.show();
		/*
		qv = QuestionDialog.getView();

		allLayout.addView(rv);
		RelativeLayout.LayoutParams rvParam = (RelativeLayout.LayoutParams) rv
				.getLayoutParams();
		rvParam.width = rvParam.height = LayoutParams.MATCH_PARENT;
		allLayout.invalidate();
		rv.invalidate();
		rv.setOnClickListener(new RadarOnClickListener(-1));
		//ClickLog.Log(ClickLogId.STATISTIC_RADAR_CHART_OPEN);*/
		//enablePage(false);
	}
	
	
	
	private View rv;
	private View dv;

	public void showRadarChart(ArrayList<Double> scoreList) {
		removeRadarChart();
		removeDetailChart();

		rv = radarChart.getView();

		View.OnClickListener[] onClickListeners = new OnClickListener[4];
		for (int i = 0; i < 4; ++i) {
			onClickListeners[i] = new RadarOnClickListener(i);
		}
		radarChart.setting(scoreList, onClickListeners);
		allLayout.addView(rv);
		RelativeLayout.LayoutParams rvParam = (RelativeLayout.LayoutParams) rv
				.getLayoutParams();
		rvParam.width = rvParam.height = LayoutParams.MATCH_PARENT;
		allLayout.invalidate();
		rv.invalidate();
		rv.setOnClickListener(new RadarOnClickListener(-1));
		//ClickLog.Log(ClickLogId.STATISTIC_RADAR_CHART_OPEN);
		enablePage(false);
	}

	private class RadarOnClickListener implements View.OnClickListener {

		private int type;

		public RadarOnClickListener(int type) {
			this.type = type;
		}

		@Override
		public void onClick(View v) {
			//ClickLog.Log(ClickLogId.STATISTIC_RADAR_CHART_CLOSE);
			removeRadarChart();
			if (type >= 0) {
				//ClickLog.Log(ClickLogId.STATISTIC_DETAIL_CHART_OPEN + type);
				addDetailChart(type);
			}
		}

	}

	public void removeRadarChart() {
		if (rv != null && rv.getParent() != null
				&& rv.getParent().equals(allLayout))
			allLayout.removeView(rv);
		enablePage(true);
	}

	public void addDetailChart(int type) {
		removeRadarChart();
		removeDetailChart();

		dv = detailChart.getView();
		allLayout.addView(dv);
		RelativeLayout.LayoutParams dvParam = (RelativeLayout.LayoutParams) dv
				.getLayoutParams();
		dvParam.width = dvParam.height = LayoutParams.MATCH_PARENT;
		detailChart.setting(type, new DatabaseControl().getMyRank());
		dv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//ClickLog.Log(ClickLogId.STATISTIC_DETAIL_CHART_CLOSE);
				removeDetailChart();
			}
		});
		dv.invalidate();
		enablePage(false);
	}

	public void removeDetailChart() {
		if (dv != null && dv.getParent() != null
				&& dv.getParent().equals(allLayout))
			allLayout.removeView(dv);
		detailChart.hide();
		enablePage(true);
	}
	
	
	
}
