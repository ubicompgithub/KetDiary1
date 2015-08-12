package com.ubicomp.ketdiary.statistic;

import java.util.ArrayList;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.data.db.DatabaseControl;
import com.ubicomp.ketdiary.data.download.UserLevelCollector;
import com.ubicomp.ketdiary.data.structure.Rank;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.system.check.StartDateCheck;
import com.ubicomp.ketdiary.ui.Typefaces;

public class AnalysisRankView extends StatisticPageView {

	private TextView title;

	private TextView helpMonth, helpWeek;
	private TextView changeMonth, changeWeek;
	private TextView lowMonth, lowWeek;
	private DatabaseControl db;

	private ImageView barMonth, barWeek;
	private ImageView pointerMonth, pointerWeek;
	private ImageView decreaseMonth, decreaseWeek;
	private ImageView increaseMonth, increaseWeek;
	private ImageView r1Month, r2Month, r3Month;
	private ImageView r1Week, r2Week, r3Week;

	private RelativeLayout contentLayoutMonth;
	private RelativeLayout contentLayoutWeek;

	private Typeface wordTypeface;

	private String[] helpStr;

	private RelativeLayout.LayoutParams paramMonth, paramWeek;

	private RelativeLayout titleLayout;

	private NetworkTask netTask;
	private PointerHandler pointerHandler = new PointerHandler();
	private ShowRadarChart showRadarChart;
	
	private AlphaAnimation titleAnimation;
	private boolean debug = PreferenceControl.isDebugMode();
	
	public AnalysisRankView(ShowRadarChart showRadarChart) {
		super(R.layout.analysis_rank_view);
		
		db = new DatabaseControl();
		helpStr = context.getResources().getStringArray(
				R.array.analysis_rank_change_help);
		wordTypeface = Typefaces.getWordTypeface();
		this.showRadarChart = showRadarChart;
		//title = (TextView) view.findViewById(R.id.analysis_rank_title);
		//title.setTypeface(wordTypeface);

		helpMonth = (TextView) view.findViewById(R.id.analysis_rank_help_month);
		helpMonth.setTypeface(wordTypeface);
		helpWeek = (TextView) view.findViewById(R.id.analysis_rank_help_week);
		helpWeek.setTypeface(wordTypeface);

		changeMonth = (TextView) view
				.findViewById(R.id.analysis_rank_help_month_text);
		changeMonth.setTypeface(wordTypeface);
		changeWeek = (TextView) view
				.findViewById(R.id.analysis_rank_help_week_text);
		changeWeek.setTypeface(wordTypeface);

		pointerMonth = (ImageView) view
				.findViewById(R.id.analysis_rank_pointer_month);
		paramMonth = (LayoutParams) pointerMonth.getLayoutParams();
		pointerWeek = (ImageView) view
				.findViewById(R.id.analysis_rank_pointer_week);
		paramWeek = (LayoutParams) pointerWeek.getLayoutParams();

		lowMonth = (TextView) view.findViewById(R.id.analysis_rank_low_month);
		lowMonth.setTypeface(wordTypeface);
		lowWeek = (TextView) view.findViewById(R.id.analysis_rank_low_week);
		lowWeek.setTypeface(wordTypeface);

		barMonth = (ImageView) view.findViewById(R.id.analysis_rank_bar_month);
		barWeek = (ImageView) view.findViewById(R.id.analysis_rank_bar_week);

		r1Month = (ImageView) view.findViewById(R.id.analysis_rank_1_month);
		r2Month = (ImageView) view.findViewById(R.id.analysis_rank_2_month);
		r3Month = (ImageView) view.findViewById(R.id.analysis_rank_3_month);

		r1Week = (ImageView) view.findViewById(R.id.analysis_rank_1_week);
		r2Week = (ImageView) view.findViewById(R.id.analysis_rank_2_week);
		r3Week = (ImageView) view.findViewById(R.id.analysis_rank_3_week);

		contentLayoutMonth = (RelativeLayout) view
				.findViewById(R.id.analysis_rank_content_layout_month);

		contentLayoutWeek = (RelativeLayout) view
				.findViewById(R.id.analysis_rank_content_layout_week);

		decreaseMonth = (ImageView) view
				.findViewById(R.id.analysis_rank_decrease_month);
		decreaseMonth.setVisibility(View.INVISIBLE);
		increaseMonth = (ImageView) view
				.findViewById(R.id.analysis_rank_increase_month);
		increaseMonth.setVisibility(View.INVISIBLE);
		decreaseWeek = (ImageView) view
				.findViewById(R.id.analysis_rank_decrease_week);
		decreaseWeek.setVisibility(View.INVISIBLE);
		increaseWeek = (ImageView) view
				.findViewById(R.id.analysis_rank_increase_week);
		increaseWeek.setVisibility(View.INVISIBLE);

		titleLayout = (RelativeLayout) view
				.findViewById(R.id.analysis_rank_title_layout);
	}

	
	
	@Override
	public void clear() {
		if (netTask != null && !netTask.isCancelled()) {
			netTask.cancel(true);
		}
		if (pointerHandler != null)
			pointerHandler.removeMessages(0);
	}

	@SuppressWarnings("deprecation")
	private void setPointer(int pMonth, int pWeek) {

		if (!StartDateCheck.afterStartDate()) {
			pointerMonth.setVisibility(View.INVISIBLE);
			pointerWeek.setVisibility(View.INVISIBLE);
			return;
		}

		setMonth(pMonth);
		setWeek(pWeek);

		if (pMonth != 0 || pWeek != 0) {
			setAnimation();
			if (Build.VERSION.SDK_INT < 16)
				titleLayout.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.analysis_title_bar_highlight));
			else
				titleLayout.setBackground(context.getResources().getDrawable(
						R.drawable.analysis_title_bar_highlight));
		}
		titleLayout.invalidate();
	}
	
	private void setAnimation(){
		titleAnimation = new AlphaAnimation(1.0F, 0.0F);
		titleAnimation.setDuration(200);
		titleAnimation.setRepeatCount(Animation.INFINITE);
		titleAnimation.setRepeatMode(Animation.REVERSE);
		titleLayout.setAnimation(titleAnimation);
		titleAnimation.start();
	}

	private void setMonth(int change) {
		int nPeople, rank;
		String uid = PreferenceControl.getUID();
		RankInfo _rank = getRankMonth(uid);
		nPeople = _rank.nPeople;
		rank = _rank.rank;
		
		if(debug)
			Toast.makeText(context, "Month:"+(rank+1), Toast.LENGTH_LONG).show();
		
		increaseMonth.setVisibility(View.INVISIBLE);
		decreaseMonth.setVisibility(View.INVISIBLE);

		switch (rank) {
		case 0:
			pointerMonth.setVisibility(View.INVISIBLE);
			r1Month.setImageResource(R.drawable.analysis_rank_1_on);
			r2Month.setImageResource(R.drawable.analysis_rank_2_off);
			r3Month.setImageResource(R.drawable.analysis_rank_3_off);
			changeMonth.setText(helpStr[rank]);
			break;
		case 1:
			pointerMonth.setVisibility(View.INVISIBLE);
			r1Month.setImageResource(R.drawable.analysis_rank_1_off);
			r2Month.setImageResource(R.drawable.analysis_rank_2_on);
			r3Month.setImageResource(R.drawable.analysis_rank_3_off);
			changeMonth.setText(helpStr[rank]);
			break;
		case 2:
			pointerMonth.setVisibility(View.INVISIBLE);
			r1Month.setImageResource(R.drawable.analysis_rank_1_off);
			r2Month.setImageResource(R.drawable.analysis_rank_2_off);
			r3Month.setImageResource(R.drawable.analysis_rank_3_on);
			changeMonth.setText(helpStr[rank]);
			break;
		default:
			pointerMonth.setVisibility(View.VISIBLE);
			r1Month.setImageResource(R.drawable.analysis_rank_1_off);
			r2Month.setImageResource(R.drawable.analysis_rank_2_off);
			r3Month.setImageResource(R.drawable.analysis_rank_3_off);
			changeMonth.setText(helpStr[3]);
			break;
		}

		if (rank > 2) {// Others
			int left = barMonth.getLeft();
			int width = barMonth.getWidth();
			int marginLeft = width / 20 + left;
			int width_bar = width * 9 / 10;

			int restPeople = nPeople - 4;
			int restRank = rank - 3;
			int len = 0;
			if (restPeople == 0)
				len = width_bar;
			else
				len = width_bar - restRank * width_bar / restPeople;
			paramMonth.leftMargin = marginLeft + len;
			contentLayoutMonth.updateViewLayout(pointerMonth, paramMonth);

			if (change > 0) {
				changeMonth.setText(helpStr[4]);
				increaseMonth.setVisibility(View.VISIBLE);
			} else if (change < 0) {
				changeMonth.setText(helpStr[5]);
				decreaseMonth.setVisibility(View.VISIBLE);
			}
		}

	}

	private void setWeek(int change) {
		int nPeople, rank;
		String uid = PreferenceControl.getUID();
		RankInfo _rank = getRankWeek(uid);
		nPeople = _rank.nPeople;
		rank = _rank.rank;
		
		if(debug)
			Toast.makeText(context, "Week:"+(rank+1), Toast.LENGTH_LONG).show();

		increaseWeek.setVisibility(View.INVISIBLE);
		decreaseWeek.setVisibility(View.INVISIBLE);

		switch (rank) {
		case 0:
			pointerWeek.setVisibility(View.INVISIBLE);
			r1Week.setImageResource(R.drawable.analysis_rank_1_on);
			r2Week.setImageResource(R.drawable.analysis_rank_2_off);
			r3Week.setImageResource(R.drawable.analysis_rank_3_off);
			changeWeek.setText(helpStr[rank]);
			break;
		case 1:
			pointerWeek.setVisibility(View.INVISIBLE);
			r1Week.setImageResource(R.drawable.analysis_rank_1_off);
			r2Week.setImageResource(R.drawable.analysis_rank_2_on);
			r3Week.setImageResource(R.drawable.analysis_rank_3_off);
			changeWeek.setText(helpStr[rank]);
			break;
		case 2:
			pointerWeek.setVisibility(View.INVISIBLE);
			r1Week.setImageResource(R.drawable.analysis_rank_1_off);
			r2Week.setImageResource(R.drawable.analysis_rank_2_off);
			r3Week.setImageResource(R.drawable.analysis_rank_3_on);
			changeWeek.setText(helpStr[rank]);
			break;
		default:
			pointerWeek.setVisibility(View.VISIBLE);
			r1Week.setImageResource(R.drawable.analysis_rank_1_off);
			r2Week.setImageResource(R.drawable.analysis_rank_2_off);
			r3Week.setImageResource(R.drawable.analysis_rank_3_off);
			changeWeek.setText(helpStr[3]);
			break;
		}

		if (rank > 2) {// Others
			int left = barWeek.getLeft();
			int width = barWeek.getWidth();
			int marginLeft = width / 20 + left;
			int width_bar = width * 9 / 10;

			int restPeople = nPeople - 4;
			int restRank = rank - 3;
			int len;
			if (restPeople == 0)
				len = width_bar;
			else
				len = width_bar - restRank * width_bar / restPeople;
			paramWeek.leftMargin = marginLeft + len;
			contentLayoutWeek.updateViewLayout(pointerWeek, paramWeek);

			if (change > 0) {
				changeWeek.setText(helpStr[4]);
				increaseWeek.setVisibility(View.VISIBLE);
			} else if (change < 0) {
				changeWeek.setText(helpStr[5]);
				decreaseWeek.setVisibility(View.VISIBLE);
			}
		}

	}

	private class RankInfo {
		public int nPeople, rank;

		public RankInfo(int nPeople, int rank) {
			this.nPeople = nPeople;
			this.rank = rank;
		}

		public double getScore() {
			if (nPeople == 0)
				return Double.MAX_VALUE;
			return (double) rank / (double) nPeople;
		}
	}

	private RankInfo getRankMonth(String uid) {
		int nPeople, rank;
		Rank[] ranks = db.getAllRanks();
		if (ranks == null) {
			nPeople = 0;
			rank = 0;
		} else {
			rank = ranks.length;
			nPeople = ranks.length;
			int tmp_rank = 0, count = 0;
			int prev_score = ranks[0].getScore();

			for (int i = 0; i < ranks.length; ++i) {
				if (ranks[i].getScore() < prev_score) {
					tmp_rank = count;
				}
				if (ranks[i].getUid().equals(uid)) {
					rank = tmp_rank;
					break;
				}
				++count;
				prev_score = ranks[i].getScore();
			}
		}

		return new RankInfo(nPeople, rank);
	}

	private RankInfo getRankWeek(String uid) {
		int nPeople, rank;
		Rank[] ranks = db.getAllRankShort();
		if (ranks == null) {
			nPeople = 0;
			rank = 0;
		} else {
			rank = ranks.length;
			nPeople = ranks.length;
			int tmp_rank = 0, count = 0;
			int prev_score = ranks[0].getScore();

			for (int i = 0; i < ranks.length; ++i) {
				if (ranks[i].getScore() < prev_score) {
					tmp_rank = count;
				}
				if (ranks[i].getUid().equals(uid)) {
					rank = tmp_rank;
					break;
				}
				++count;
				prev_score = ranks[i].getScore();
			}
		}

		return new RankInfo(nPeople, rank);
	}

	@Override
	public void load() {

		contentLayoutMonth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//showRadarChart.showRadarChart(calculateRank());
			}
		});

		netTask = new NetworkTask();
		netTask.execute();
		pointerHandler.sendEmptyMessage(0);
	}

	@Override
	public void onCancel() {
		clear();
	}
	
	private Rank[] ranks;
	private Rank[] short_ranks;
	private UserLevelCollector levelCollector;

	private class NetworkTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			levelCollector = new UserLevelCollector(view.getContext());
			ranks = levelCollector.update();
			short_ranks = levelCollector.updateShort();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (ranks == null || short_ranks == null) {
				return;
			}

			String uid = PreferenceControl.getUID();
			double prev_score = getRankMonth(uid).getScore();
			double prev_score_week = getRankWeek(uid).getScore();

			db.clearRank();
			db.clearRankShort();
			for (int i = 0; i < ranks.length; ++i)
				db.updateRank(ranks[i]);
			for (int i = 0; i < short_ranks.length; ++i)
				db.updateRankShort(short_ranks[i]);

			double score = getRankMonth(uid).getScore();
			double score_week = getRankWeek(uid).getScore();

			int p1, p2;
			if (score == prev_score)
				p1 = 0;
			else if (score > prev_score)
				p1 = -1;
			else
				p1 = 1;

			if (score_week == prev_score_week)
				p2 = 0;
			else if (score_week > prev_score_week)
				p2 = -1;
			else
				p2 = 1;

			setPointer(p1, p2);
		}

	}


	private class PointerHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			setPointer(0, 0);
		}
	}
}
