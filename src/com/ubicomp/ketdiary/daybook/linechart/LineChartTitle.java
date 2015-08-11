package com.ubicomp.ketdiary.daybook.linechart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.main.fragment.DaybookFragment;
import com.ubicomp.ketdiary.ui.Typefaces;

public class LineChartTitle extends View {
	
	private static Context context = App.getContext();
	private static Resources resource = context.getResources();
	private ChartCaller caller;
	private boolean chartTouchable = true;
	
	private int t1, t2, t3, t_gap;
	private int chartType;
	
	private static int title_0 = resource.getDimensionPixelSize(R.dimen.chart_title_0);
	private static int title_1 = resource.getDimensionPixelSize(R.dimen.chart_title_1);
	private static int title_2 = resource.getDimensionPixelSize(R.dimen.chart_title_2);
	
	private static int title_0_r = resource.getDimensionPixelSize(R.dimen.chart_title_0_r);
	private static int title_1_r = resource.getDimensionPixelSize(R.dimen.chart_title_1_r);
	private static int title_2_r = resource.getDimensionPixelSize(R.dimen.chart_title_2_r);
	
	private static int gap = resource.getDimensionPixelSize(R.dimen.chart_gap);
	private static int gap_r = resource.getDimensionPixelSize(R.dimen.chart_gap_r);
	
	private static int titleTop = resource.getDimensionPixelSize(R.dimen.chart_title_top);
	private static int textSize = resource.getDimensionPixelSize(R.dimen.large_text_size);
	
	private static final String[] title_str = { "自我狀態", "人際互動" ,"綜合分析" };
	
	private Paint text_paint_large = new Paint();
	private Paint text_paint_large_2 = new Paint();
	
	
	public LineChartTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
		text_paint_large.setTextSize(textSize);
		text_paint_large.setTextAlign(Align.LEFT);
		text_paint_large.setTypeface(Typefaces.getWordTypefaceBold());
		text_paint_large.setColor(getResources().getColor(R.color.dark_gray));
		text_paint_large_2.setColor(getResources().getColor(R.color.linechart_date_color));
		text_paint_large_2.setTextSize(textSize);
		text_paint_large_2.setTextAlign(Align.LEFT);
		text_paint_large_2.setTypeface(Typefaces.getWordTypefaceBold());
		
	
	}
	
	
	public void setting(ChartCaller caller) {
		this.caller = caller;
	}
	
	public void setTouchable(boolean touchable){
		chartTouchable = touchable;
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!chartTouchable)
			return true;
		int x = (int) event.getX();
		//Log.i("OMG", "TYPEAA: " +checkLineChartType());
        chartType = checkLineChartType();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (x < t2 - t_gap) {
				chartType = 0;
			} else if (x < t3 - t_gap) {
				chartType = 1;
			} else {
				chartType = 2;
			}
			caller.setChartType(chartType);
			//Log.i("OMG", "TYPEBB: " +checkLineChartType());
			//Log.i("OMG", "TYPEBB: " +t1);
			invalidate();
		}
		return true;
	}
	
	private int checkLineChartType() {
		//return MainActivity.getChartType();
		return DaybookFragment.chart_type;
    }
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			t1 = title_0;
			t2 = title_1;
			t3 = title_2;
			t_gap = gap;
		}
		else {
			t1 = title_0_r;
			t2 = title_1_r;
			t3 = title_2_r;
			t_gap = gap_r;
		}
		
		canvas.drawText(title_str[0], t1, titleTop, text_paint_large);
		canvas.drawText(title_str[1], t2, titleTop, text_paint_large);
		canvas.drawText(title_str[2], t3, titleTop, text_paint_large);
		//Log.i("OMG", "TYPECC: " +chartType);
		switch (checkLineChartType()) {
		case 0:
			canvas.drawText(title_str[0], t1, titleTop, text_paint_large_2);
			break;
		case 1:
			canvas.drawText(title_str[1], t2, titleTop, text_paint_large_2);
			break;
		case 2:
			canvas.drawText(title_str[2], t3, titleTop, text_paint_large_2);
			break;
		}
	}
}
