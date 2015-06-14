package com.ubicomp.ketdiary.mydaybook.linechart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;

import com.ubicomp.ketdiary.R;

public class LineChartFilter extends View {
	
	private Bitmap filterBg = BitmapFactory.decodeResource(getResources(), R.drawable.filter_bg);
	private Bitmap d1 = BitmapFactory.decodeResource(getResources(), R.drawable.filter_color1);
    private Bitmap d2 = BitmapFactory.decodeResource(getResources(), R.drawable.filter_color2);
    private Bitmap d3 = BitmapFactory.decodeResource(getResources(), R.drawable.filter_color3);
    private Bitmap d4 = BitmapFactory.decodeResource(getResources(), R.drawable.filter_color4);
    private Bitmap d5 = BitmapFactory.decodeResource(getResources(), R.drawable.filter_color5);
    private Bitmap d6 = BitmapFactory.decodeResource(getResources(), R.drawable.filter_color6);
    private Bitmap d7 = BitmapFactory.decodeResource(getResources(), R.drawable.filter_color7);
    private Bitmap d8 = BitmapFactory.decodeResource(getResources(), R.drawable.filter_color8);
    private Bitmap dall = BitmapFactory.decodeResource(getResources(), R.drawable.filter_all);
    
    private Bitmap[] dot_array = {dall, d1, d2, d3, d4, d5, d6, d7, d8};
    private float offsetX = 30;
    private float[] button_pos_array;

	public LineChartFilter(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
	}


}
