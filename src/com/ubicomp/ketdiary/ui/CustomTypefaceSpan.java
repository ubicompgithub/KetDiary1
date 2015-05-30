package com.ubicomp.ketdiary.ui;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

public class CustomTypefaceSpan extends TypefaceSpan {

	private final Typeface type;
	private final int color;

	public CustomTypefaceSpan(String family, Typeface type, int color) {
		super(family);
		this.type = type;
		this.color = color;
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setTypeface(type);
		ds.setColor(color);
	}

	@Override
	public void updateMeasureState(TextPaint paint) {
		paint.setTypeface(type);
		paint.setColor(color);
	}

}
