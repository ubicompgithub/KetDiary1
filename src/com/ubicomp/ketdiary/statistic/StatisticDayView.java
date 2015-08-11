package com.ubicomp.ketdiary.statistic;

import java.text.DecimalFormat;
import java.util.Calendar;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.data.db.DatabaseControl;
import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.ui.CustomTypefaceSpan;
import com.ubicomp.ketdiary.ui.Typefaces;

public class StatisticDayView extends StatisticPageView {

	private TextView bracValue, bracTime, bracHelp, bracTitle;
	private DatabaseControl db;
	private ImageView valueCircle;
	private Drawable valueCircleDrawable;
	private ImageView[] circleImages;
	private Drawable[] circleDrawables;
	private TextView[] circleValues;
	private TextView[] circleTexts;
	private DecimalFormat format;
	private LinearLayout blockLayout;

	private ImageView emotion, craving;
	private Drawable emotionDrawable, desireDrawable;

	private static final int[] blockHint = { R.string.morning, R.string.noon,
			R.string.night, R.string.morning_time, R.string.noon_time,
			R.string.night_time };
	private String[] blockHintStr = new String[3];
	private String[] blockHintTime = new String[3];

	private static final int text_color = App.getContext().getResources()
			.getColor(R.color.text_gray);
	private static final int text_color2 = App.getContext().getResources()
			.getColor(R.color.text_gray2);
	
	private static final int value_color = App.getContext().getResources()
			.getColor(R.color.white);

	private static final int nBlocks = 3;

	private Typeface digitTypeface;
	private Typeface digitTypefaceBold;
	private Typeface wordTypeface;
	private Typeface wordTypefaceBold;
	
	private final static int[] typeId = {0, R.drawable.statistic_note_negative,
		R.drawable.statistic_note_physical, R.drawable.statistic_note_positive, 
		R.drawable.statistic_note_selftest, R.drawable.statistic_note_temptation,
		R.drawable.statistic_note_conflic, R.drawable.statistic_note_social, 
	 	R.drawable.statistic_note_play};

	/*private final static int[] emotionId = { R.drawable.emotion_0,
			R.drawable.emotion_1, R.drawable.emotion_2, R.drawable.emotion_3,
			R.drawable.emotion_4, };

	private final static int[] desireId = { R.drawable.craving_0,
			R.drawable.craving_1, R.drawable.craving_2, R.drawable.craving_3,
			R.drawable.craving_4, R.drawable.craving_5, R.drawable.craving_6,
			R.drawable.craving_7, R.drawable.craving_8, R.drawable.craving_9, };*/

	private int e_idx, c_idx, type_idx;

	public StatisticDayView() {
		super(R.layout.statistic_day_view);
		db = new DatabaseControl();
		digitTypeface = Typefaces.getDigitTypeface();
		digitTypefaceBold = Typefaces.getDigitTypefaceBold();
		wordTypeface = Typefaces.getWordTypeface();
		wordTypefaceBold = Typefaces.getWordTypefaceBold();
		for (int i = 0; i < 3; ++i) {
			blockHintStr[i] = context.getString(blockHint[i]);
			blockHintTime[i] = context.getString(blockHint[i + 3]);
		}
		bracTitle = (TextView) view.findViewById(R.id.statistic_day_title);
		bracTitle.setTypeface(wordTypefaceBold);

		bracValue = (TextView) view.findViewById(R.id.statistic_day_brac_value);
		bracValue.setTypeface(wordTypefaceBold);
		bracTime = (TextView) view.findViewById(R.id.statistic_day_brac_time);
		bracTime.setTypeface(wordTypefaceBold);
	}

	@Override
	public void clear() {
		if (emotion != null)
			emotion.setImageDrawable(null);
		if (craving != null)
			craving.setImageDrawable(null);
	}

	private float result;
	private long brac_time;
	private String output;

	@Override
	public void load() {

		//Detection detection = db.getLatestDetection();
		TestResult testResult = db.getLatestTestResult();
		NoteAdd noteAdd = db.getTsNoteAdd(testResult.getTv().getTimestamp());
		
		type_idx = noteAdd.getType();
		result = testResult.getResult();
		brac_time = testResult.getTv().getTimestamp();
		//brac = detection.getBrac();
		//brac_time = detection.getTv().getTimestamp();
		//e_idx = detection.getEmotion();
		//c_idx = detection.getCraving();

		circleImages = new ImageView[nBlocks];

		int textSize = (int) App.getContext().getResources()
				.getDimensionPixelSize(R.dimen.normal_text_size);
		circleTexts = new TextView[nBlocks];
		circleValues = new TextView[nBlocks];
		for (int i = 0; i < nBlocks; ++i) {
			circleImages[i] = new ImageView(context);
			circleTexts[i] = new TextView(context);
			circleValues[i] = new TextView(context);

			circleTexts[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			Spannable s = new SpannableString(blockHintStr[i] + "\n"
					+ blockHintTime[i]);
			int start = 0;
			int end = blockHintStr[i].length() + 1;
			s.setSpan(new CustomTypefaceSpan("custom1", wordTypefaceBold,
					text_color), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			start = end;
			end = start + blockHintTime[i].length();
			s.setSpan(new CustomTypefaceSpan("custom2", digitTypefaceBold,
					text_color), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			circleTexts[i].setText(s);
			circleTexts[i].setGravity(Gravity.CENTER);
			circleTexts[i].setTypeface(wordTypefaceBold);

			circleValues[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			circleValues[i].setTextColor(value_color);
			circleValues[i].setTypeface(digitTypeface);
		}

		bracHelp = (TextView) view.findViewById(R.id.statistic_day_brac);
		bracHelp.setTypeface(wordTypefaceBold);

		valueCircle = (ImageView) view
				.findViewById(R.id.statistic_day_value_circle);
		blockLayout = (LinearLayout) view
				.findViewById(R.id.statistic_day_block_layout);

		emotion = (ImageView) view.findViewById(R.id.statistic_day_emotion);
		craving = (ImageView) view.findViewById(R.id.statistic_day_craving);

		format = new DecimalFormat();
		format.setMaximumIntegerDigits(3);
		format.setMinimumIntegerDigits(1);
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
		


		
		if (brac_time == 0)
			valueCircleDrawable = view.getResources().getDrawable(
					R.drawable.statistic_notest);
		else if (result == 0)
			valueCircleDrawable = view.getResources().getDrawable(
					R.drawable.statistic_pass);
		else
			valueCircleDrawable = view.getResources().getDrawable(
					R.drawable.statistic_nopass);

		circleDrawables = new Drawable[3];
		circleDrawables[0] = view.getResources().getDrawable(
				R.drawable.statistic_notest);
		circleDrawables[1] = view.getResources().getDrawable(
				R.drawable.statistic_nopass);
		circleDrawables[2] = view.getResources().getDrawable(
				R.drawable.statistic_pass);

		
		
		
		//if (c_idx >= 0)
			//desireDrawable = view.getResources().getDrawable(desireId[c_idx]);

		valueCircle.setImageDrawable(valueCircleDrawable);
		bracValue.setText(output);

		if (brac_time == 0) {
			bracTime.setText(R.string.today_test_none);
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(brac_time);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DATE);
			int hour = cal.get(Calendar.HOUR);
			int min = cal.get(Calendar.MINUTE);
			int am_pm = cal.get(Calendar.AM_PM);
			String min_str;
			if (min < 10)
				min_str = "0" + String.valueOf(min);
			else
				min_str = String.valueOf(min);

			String m_text = context.getString(R.string.month);
			String d_text = context.getString(R.string.day);

			String month_str = String.valueOf(month);
			String day_str = String.valueOf(day);

			String ampm = null;

			if (am_pm == Calendar.AM)
				ampm = " A.M.";
			else {
				ampm = "P.M.";
				if (hour == 0)
					hour = 12;
			}
			String time_str = hour + ":" + min_str + ampm;

			Spannable s = new SpannableString(month_str + m_text + day_str
					+ d_text + "\n" + time_str);
			int start = 0;
			int end = month_str.length();
			s.setSpan(new CustomTypefaceSpan("c1", digitTypefaceBold, text_color2),
					start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			start = end;
			end = start + m_text.length();
			s.setSpan(new CustomTypefaceSpan("c2", wordTypefaceBold, text_color2),
					start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			start = end;
			end = start + day_str.length();
			s.setSpan(new CustomTypefaceSpan("c1", digitTypefaceBold, text_color2),
					start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			start = end;
			end = start + d_text.length() + 1;
			s.setSpan(new CustomTypefaceSpan("c2", wordTypefaceBold, text_color2),
					start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			start = end;
			end = start + time_str.length();
			s.setSpan(new CustomTypefaceSpan("c1", digitTypefaceBold, text_color2),
					start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			bracTime.setText(s);
			bracTime.setTypeface(wordTypefaceBold);
		}
		//type_idx = 1;
		if (type_idx > 0)
			emotionDrawable = view.getResources().getDrawable(typeId[type_idx]);
		
		if (emotionDrawable != null){
			emotion.setImageDrawable(emotionDrawable);
			emotion.setVisibility(View.VISIBLE);
		}

		/*if (desireDrawable != null)
			craving.setImageDrawable(desireDrawable);*/

		//Float[] bracs = db.getTodayPrimeBrac();
		/*
		int blockMargin = App.getContext().getResources()
				.getDimensionPixelSize(R.dimen.day_block_margin_size);
		int circle_size = App.getContext().getResources()
				.getDimensionPixelSize(R.dimen.day_circle_size);

		int cur_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
		for (int i = 0; i < nBlocks; ++i) {
			RelativeLayout lLayout = new RelativeLayout(context);

			RelativeLayout sLayout = new RelativeLayout(context);
			sLayout.addView(circleImages[i]);
			RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) circleImages[i]
					.getLayoutParams();
			param.width = param.height = circle_size;
			sLayout.addView(circleValues[i]);
			sLayout.setId(0x999);
			RelativeLayout.LayoutParams circleParam = (RelativeLayout.LayoutParams) circleImages[i]
					.getLayoutParams();
			circleParam.addRule(RelativeLayout.CENTER_IN_PARENT,
					RelativeLayout.TRUE);
			RelativeLayout.LayoutParams valueParam = (RelativeLayout.LayoutParams) circleValues[i]
					.getLayoutParams();
			valueParam.addRule(RelativeLayout.CENTER_IN_PARENT,
					RelativeLayout.TRUE);

			lLayout.addView(sLayout);
			lLayout.addView(circleTexts[i]);

			RelativeLayout.LayoutParams tParam = (RelativeLayout.LayoutParams) circleTexts[i]
					.getLayoutParams();
			tParam.addRule(RelativeLayout.RIGHT_OF, 0x999);
			tParam.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			
			
			
			if (bracs[i] == null) {
				circleValues[i].setText("");
				circleImages[i].setImageDrawable(circleDrawables[0]);
				if (TimeBlock.isEmpty(i, cur_hour)
						&& Build.VERSION.SDK_INT >= 11)
					circleImages[i].setAlpha(0.4F);
			} else {
				String value = format.format(bracs[i]);

				if (bracs[i] < Detection.BRAC_THRESHOLD) {
					circleImages[i].setImageDrawable(circleDrawables[2]);
					circleValues[i].setText("0.00");
				} else {
					circleImages[i].setImageDrawable(circleDrawables[1]);
					circleValues[i].setText(value);
				}
			}

			blockLayout.addView(lLayout);

			LinearLayout.LayoutParams lParam = (LinearLayout.LayoutParams) lLayout
					.getLayoutParams();
			lParam.leftMargin = lParam.rightMargin = blockMargin;
		}*/
	}

	@Override
	public void onCancel() {
		clear();
	}
}
