package com.ubicomp.ketdiary.statistic;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.ui.Typefaces;

public class RadarChart {

	private FrameLayout layout;

	private RadarChartView rcv;

	private TextView title;

	private RelativeLayout item0, item1, item2, item3;
	private TextView score0, score1, score2, score3;
	private TextView scoreT0, scoreT1, scoreT2, scoreT3;
	private TextView text0, text1, text2, text3;
	private DecimalFormat format;

	public RadarChart(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = (FrameLayout) inflater.inflate(R.layout.dialog_radar_chart,
				null);
		rcv = (RadarChartView) layout.findViewById(R.id.radar_chart);

		format = new DecimalFormat();
		format.setMaximumFractionDigits(0);
		format.setMaximumIntegerDigits(3);

		Typeface wordTypeface = Typefaces.getWordTypeface();
		Typeface wordTypefaceBold = Typefaces.getWordTypefaceBold();
		Typeface digitTypeface = Typefaces.getDigitTypefaceBold();

		title = (TextView) layout.findViewById(R.id.rank_title);
		title.setTypeface(wordTypefaceBold);

		item0 = (RelativeLayout) layout.findViewById(R.id.radar_item_0);
		item1 = (RelativeLayout) layout.findViewById(R.id.radar_item_1);
		item2 = (RelativeLayout) layout.findViewById(R.id.radar_item_2);
		item3 = (RelativeLayout) layout.findViewById(R.id.radar_item_3);

		score0 = (TextView) layout.findViewById(R.id.radar_score_0);
		score1 = (TextView) layout.findViewById(R.id.radar_score_1);
		score2 = (TextView) layout.findViewById(R.id.radar_score_2);
		score3 = (TextView) layout.findViewById(R.id.radar_score_3);

		score0.setTypeface(digitTypeface);
		score1.setTypeface(digitTypeface);
		score2.setTypeface(digitTypeface);
		score3.setTypeface(digitTypeface);

		scoreT0 = (TextView) layout.findViewById(R.id.radar_score_text_0);
		scoreT1 = (TextView) layout.findViewById(R.id.radar_score_text_1);
		scoreT2 = (TextView) layout.findViewById(R.id.radar_score_text_2);
		scoreT3 = (TextView) layout.findViewById(R.id.radar_score_text_3);

		scoreT0.setTypeface(wordTypefaceBold);
		scoreT1.setTypeface(wordTypefaceBold);
		scoreT2.setTypeface(wordTypefaceBold);
		scoreT3.setTypeface(wordTypefaceBold);

		text0 = (TextView) layout.findViewById(R.id.radar_text_0);
		text1 = (TextView) layout.findViewById(R.id.radar_text_1);
		text2 = (TextView) layout.findViewById(R.id.radar_text_2);
		text3 = (TextView) layout.findViewById(R.id.radar_text_3);

		text0.setTypeface(wordTypeface);
		text1.setTypeface(wordTypeface);
		text2.setTypeface(wordTypeface);
		text3.setTypeface(wordTypeface);
	}

	public void setting(ArrayList<Double> scoreList,
			View.OnClickListener[] onClickListener) {
		rcv.setting(scoreList);
		rcv.invalidate();

		int s0 = Math.min((int) (scoreList.get(0) * 100), 100);
		int s1 = Math.min((int) (scoreList.get(1) * 100), 100);
		int s2 = Math.min((int) (scoreList.get(2) * 100), 100);
		int s3 = Math.min((int) (scoreList.get(3) * 100), 100);

		item0.setOnClickListener(onClickListener[0]);
		item1.setOnClickListener(onClickListener[1]);
		item2.setOnClickListener(onClickListener[2]);
		item3.setOnClickListener(onClickListener[3]);

		score0.setText(format.format(s0));
		score1.setText(format.format(s1));
		score2.setText(format.format(s2));
		score3.setText(format.format(s3));
		score0.invalidate();
		score1.invalidate();
		score2.invalidate();
		score3.invalidate();
	}

	public FrameLayout getView() {
		return layout;
	}

}
