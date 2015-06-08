package com.ubicomp.ketdiary.statistic;

import android.graphics.Typeface;
import android.os.Build;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.system.Config;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.Typefaces;

public class AnalysisCounterView extends StatisticPageView {

	private TextView title;
	private TextView help;
	private TextView counterValue, counterText, couponValue, couponText;
	private RelativeLayout titleLayout;

	private Typeface wordTypeface, digitTypefaceBold;

	public AnalysisCounterView() {
		super(R.layout.analysis_counter_view2);
		
		
		wordTypeface = Typefaces.getWordTypeface();
		digitTypefaceBold = Typefaces.getDigitTypefaceBold();
		//title = (TextView) view.findViewById(R.id.analysis_counter_title);
		//title.setTypeface(wordTypeface);
		titleLayout = (RelativeLayout) view
				.findViewById(R.id.analysis_counter_title_layout);
		help = (TextView) view.findViewById(R.id.analysis_counter_help);
		//help.setTypeface(wordTypeface);
		couponValue = (TextView) view
				.findViewById(R.id.analysis_counter_coupon_value);
		//couponValue.setTypeface(digitTypefaceBold);
		counterValue = (TextView) view
				.findViewById(R.id.analysis_counter_counter_value);
		//counterValue.setTypeface(digitTypefaceBold);

		counterText = (TextView) view
				.findViewById(R.id.analysis_counter_counter_text);
		//counterText.setTypeface(wordTypeface);

		couponText = (TextView) view
				.findViewById(R.id.analysis_counter_coupon_text);
		//couponText.setTypeface(wordTypeface);
	}

	@Override
	public void clear() {
	}

	@Override
	public void load() {
		updateCounter();
	}

	@SuppressWarnings("deprecation")
	public void updateCounter() {
		/*
		int prev_coupon = PreferenceControl.lastShowedCoupon();
		int total_counter = PreferenceControl.getTotalCounter();

		int coupon = total_counter / Config.COUPON_CREDITS;
		int counter = total_counter % Config.COUPON_CREDITS;

		PreferenceControl.setShowedCoupon(coupon);
		PreferenceControl.setCouponChange(false);

		if (prev_coupon < coupon) {
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
		}
		counterValue.setText(String.valueOf(counter));
		counterValue.invalidate();
		couponValue.setText(String.valueOf(coupon));
		couponValue.invalidate();*/
	}

	@Override
	public void onCancel() {
		clear();
	}

}
