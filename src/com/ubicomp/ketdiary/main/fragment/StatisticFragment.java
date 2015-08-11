package com.ubicomp.ketdiary.main.fragment;

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
import com.ubicomp.ketdiary.ResultService3;
import com.ubicomp.ketdiary.data.db.DatabaseControl;
import com.ubicomp.ketdiary.data.structure.Rank;
import com.ubicomp.ketdiary.dialog.QuestionCaller;
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
import com.ubicomp.ketdiary.system.clicklog.ClickLog;
import com.ubicomp.ketdiary.system.clicklog.ClickLogId;
import com.ubicomp.ketdiary.ui.LoadingDialogControl;
import com.ubicomp.ketdiary.ui.ScaleOnTouchListener;

public class StatisticFragment extends Fragment implements ShowRadarChart, QuestionCaller{
	
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
				if (motion.getAction() == MotionEvent.ACTION_DOWN)
					ClickLog.Log(ClickLogId.STATISTIC_ANALYSIS);
				return false;
			}
		});
		
		return view;
	}

	public void onResume() {
		super.onResume();
		ClickLog.Log(ClickLogId.STATISTIC_ENTER);
		
		long curTime = System.currentTimeMillis();
		long testTime = PreferenceControl.getLatestTestCompleteTime();
		long pastTime = curTime - testTime;
		long restTime = ResultService3.spentTime;
		boolean testFail = PreferenceControl.isTestFail();	
		
		if(testFail){
			
		}
		else if( PreferenceControl.getCheckResult() && restTime <= 0){
			MainActivity.getMainActivity().checkResultAddPoint();
		}
		else if ( PreferenceControl.getCheckResult() && restTime > 0 ){
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
		analysisViews[1] = new AnalysisCounterView(statisticFragment);
		analysisViews[2] = new AnalysisRankView(statisticFragment);
		
		statisticViewAdapter = new StatisticPagerAdapter();
		msgBox = new QuestionDialog((RelativeLayout) view, statisticFragment);

//		Bundle data = getArguments();
//		if (data != null) {
//			int action = data.getInt("action");
//			data.putInt("action", 0);
//			if (action == MainActivity.ACTION_QUESTIONNAIRE) {
//				notify_action = action;
//			}
//		}
		
		//msgBox.generateCopingDialog();

		loadHandler.sendEmptyMessage(0);
	}

	public void onPause() {
		if (loadHandler != null)
			loadHandler.removeMessages(0);
		clear();
		ClickLog.Log(ClickLogId.STATISTIC_LEAVE);
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
				ClickLog.Log(ClickLogId.STATISTIC_TODAY);
				break;
			case 1:
				ClickLog.Log(ClickLogId.STATISTIC_WEEK);
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

//			questionAnimation = new AlphaAnimation(1.0F, 0.0F);
//			questionAnimation.setDuration(200);
//			questionAnimation.setRepeatCount(Animation.INFINITE);
//			questionAnimation.setRepeatMode(Animation.REVERSE);

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
			ClickLog.Log(ClickLogId.STATISTIC_COPING_BUTTON);
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
	
	public static void showQuestionTest() {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTIONTEST_BUTTON);
		msgBox.show(0);
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
		ClickLog.Log(ClickLogId.STATISTIC_RADAR_CHART_OPEN);
		enablePage(false);
	}

	private class RadarOnClickListener implements View.OnClickListener {

		private int type;

		public RadarOnClickListener(int type) {
			this.type = type;
		}

		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.STATISTIC_RADAR_CHART_CLOSE);
			removeRadarChart();
			if (type >= 0) {
				ClickLog.Log(ClickLogId.STATISTIC_DETAIL_CHART_OPEN + type);
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
				ClickLog.Log(ClickLogId.STATISTIC_DETAIL_CHART_CLOSE);
				ClickLog.Log(ClickLogId.STATISTIC_RADAR_CHART_OPEN);
				//removeDetailChart();
				showRadarChart(calculateRank());
			}
		});
		dv.invalidate();
		enablePage(false);
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

	public void removeDetailChart() {
		if (dv != null && dv.getParent() != null
				&& dv.getParent().equals(allLayout))
			allLayout.removeView(dv);
		detailChart.hide();
		enablePage(true);
	}

	@Override
	public void QuestionDone() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
