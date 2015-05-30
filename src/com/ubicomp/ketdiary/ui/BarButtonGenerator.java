package com.ubicomp.ketdiary.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.MainActivity;
import com.ubicomp.ketdiary.R;

public class BarButtonGenerator {
	private static final LayoutInflater inflater = (LayoutInflater) App
			.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	private static Typeface wordTypefaceBold = Typefaces.getWordTypefaceBold();
	private static Typeface wordTypeface = Typefaces.getWordTypeface();
	private static Context context = App.getContext();
	
	public static View createTwoButtonView(int leftTextId, int rightTextId,
			OnClickListener leftListener, OnClickListener rightListener) {
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.bar_yes_no_item, null);
		TextView textLeft = (TextView) layout
				.findViewById(R.id.bar_button_left);
		textLeft.setText(leftTextId);
		textLeft.setTypeface(wordTypefaceBold);
		textLeft.setOnClickListener(leftListener);

		TextView textRight = (TextView) layout
				.findViewById(R.id.bar_button_right);
		textRight.setText(rightTextId);
		textRight.setTypeface(wordTypefaceBold);
		textRight.setOnClickListener(rightListener);

		return layout;
	}
	
	public static View createAddNoteView(OnItemSelectedListener itemselectedListener){
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.bar_addnote, null);
		
		TextView note_title = (TextView) layout
				.findViewById(R.id.note_title);
		Spinner sp_date = (Spinner)layout.findViewById(R.id.note_sp_date);
	    Spinner sp_timeslot = (Spinner)layout.findViewById(R.id.note_sp_timeslot);
	    
	    note_title.setTypeface(wordTypefaceBold);
	    note_title.setTextColor(context.getResources().getColor(R.color.text_gray2));
	    
	    SetItem(sp_date, R.array.note_date, itemselectedListener);
	    SetItem(sp_timeslot, R.array.note_time_slot, itemselectedListener);
	    
		return layout;
	}
	
	private static void SetItem(Spinner sp, int array, OnItemSelectedListener itemselectedListener){
		ArrayAdapter adapter = ArrayAdapter.createFromResource(MainActivity.getMainActivity(), array, android.R.layout.simple_spinner_item);
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strs );
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
		sp.setOnItemSelectedListener(itemselectedListener);
		
		//new SpinnerXMLSelectedListener()
		//sp.setPrompt("負面情緒");
        
        //sp.setVisibility(View.VISIBLE);  
       // sp.performClick();
	}
	
/*
	public static View createTextView(int textStr) {
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.bar_text_item, null);
		TextView text = (TextView) layout
				.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(textStr);

		return layout;
	}

	public static View createTextView(String textStr) {

		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.bar_text_item, null);
		TextView text = (TextView) layout
				.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(textStr);

		return layout;
	}

	public static View createQuoteQuestionView(String textStr) {
		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.bar_text_area_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_text);
		String quoteBlank = App.getContext().getString(R.string.quote_blank);
		int firstIdx = textStr.indexOf(quoteBlank);
		int lastIdx = textStr.lastIndexOf(quoteBlank);
		Spannable spannable = new SpannableString(textStr);
		int text_color = App.getContext().getResources()
				.getColor(R.color.text_gray);
		int o_color = App.getContext().getResources()
				.getColor(R.color.lite_orange);
		spannable.setSpan(
				new CustomTypefaceSpan("c1", wordTypeface, text_color), 0,
				firstIdx, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		spannable.setSpan(new CustomTypefaceSpan("c2", wordTypefaceBold,
				o_color), firstIdx, lastIdx + 1,
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		spannable.setSpan(
				new CustomTypefaceSpan("c1", wordTypeface, text_color),
				lastIdx + 1, textStr.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		text.setText(spannable);
		return layout;
	}

	public static View createIconView(String textStr, int DrawableId,
			OnClickListener listener) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.bar_icon_item, null);
		TextView text = (TextView) layout
				.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(textStr);

		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		if (DrawableId > 0)
			icon.setImageDrawable(App.getContext().getResources()
					.getDrawable(DrawableId));

		layout.setOnClickListener(listener);

		return layout;
	}

	public static View createIconView(int textStr, int DrawableId,
			OnClickListener listener) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.bar_icon_item, null);
		TextView text = (TextView) layout
				.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(textStr);

		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		if (DrawableId > 0)
			icon.setImageDrawable(App.getContext().getResources()
					.getDrawable(DrawableId));

		layout.setOnClickListener(listener);

		return layout;
	}

	public static View createIconViewInverse(String textStr, int DrawableId,
			OnClickListener listener) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.bar_icon_item_inv, null);
		TextView text = (TextView) layout
				.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(textStr);

		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		if (DrawableId > 0)
			icon.setImageDrawable(App.getContext().getResources()
					.getDrawable(DrawableId));

		layout.setOnClickListener(listener);

		return layout;
	}

	public static View createTextAreaViewInverse(String textStr, int DrawableId) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.bar_text_item_inv, null);
		TextView text = (TextView) layout
				.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(textStr);

		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		if (DrawableId > 0)
			icon.setImageDrawable(App.getContext().getResources()
					.getDrawable(DrawableId));
		return layout;
	}

	public static View createIconViewInverse(int textStr, int DrawableId,
			OnClickListener listener) {

		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.bar_icon_item_inv, null);
		TextView text = (TextView) layout
				.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(textStr);

		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		if (DrawableId > 0)
			icon.setImageDrawable(App.getContext().getResources()
					.getDrawable(DrawableId));

		layout.setOnClickListener(listener);

		return layout;
	}

	public static View createTitleView(int titleStr) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.bar_titlebar, null);
		TextView text = (TextView) layout.findViewById(R.id.titlebar_text);
		text.setTypeface(wordTypefaceBold);
		text.setText(titleStr);

		return layout;
	}

	public static View createTitleView(String titleStr) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.bar_titlebar, null);
		TextView text = (TextView) layout.findViewById(R.id.titlebar_text);
		text.setTypeface(wordTypefaceBold);
		text.setText(titleStr);

		return layout;
	}

	public static View createBlankView() {

		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.bar_blank_item, null);
		return layout;
	}

	public static View createAnimationView(int anim_id) {
		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.bar_animation_item, null);
		ImageView img = (ImageView) layout
				.findViewById(R.id.question_animation);
		img.setImageResource(anim_id);
		TextView text = (TextView) layout
				.findViewById(R.id.question_animation_right_button);
		text.setTypeface(wordTypefaceBold);
		return layout;
	}

	public static View createTwoButtonView(int leftTextId, int rightTextId,
			OnClickListener leftListener, OnClickListener rightListener) {
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.bar_yes_no_item, null);
		TextView textLeft = (TextView) layout
				.findViewById(R.id.bar_button_left);
		textLeft.setText(leftTextId);
		textLeft.setTypeface(wordTypefaceBold);
		textLeft.setOnClickListener(leftListener);

		TextView textRight = (TextView) layout
				.findViewById(R.id.bar_button_right);
		textRight.setText(rightTextId);
		textRight.setTypeface(wordTypefaceBold);
		textRight.setOnClickListener(rightListener);

		return layout;
	}

	public static View createSettingButtonView(int textStr,
			OnClickListener listener) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.bar_setting_item, null);
		TextView text = (TextView) layout
				.findViewById(R.id.question_description);
		text.setTypeface(wordTypeface);
		text.setText(textStr);

		layout.setOnClickListener(listener);

		return layout;
	}*/

}
