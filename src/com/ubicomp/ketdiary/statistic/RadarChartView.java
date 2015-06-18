package com.ubicomp.ketdiary.statistic;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.ubicomp.ketdiary.R;

@SuppressLint("ViewConstructor")
public class RadarChartView extends View {

	private ArrayList<Double> scoreList;
	private PointF topCorner, leftCorner, rightCorner, bottomCorner, center;
	private PointF p0, p1, p2, p3;

	private Paint valueLine, valuePaint;
	private Context context;
	private int bound_width, bound_height;

	public RadarChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		bound_width = bound_height = context.getResources()
				.getDimensionPixelSize(R.dimen.radar_chart_size);
		createPaints();
	}

	public void setting(ArrayList<Double> scoreList) {
		this.scoreList = scoreList;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = bound_width;
		if (getLayoutParams().width == LayoutParams.WRAP_CONTENT)
			;
		else if ((getLayoutParams().width == LayoutParams.MATCH_PARENT)) {
			width = MeasureSpec.getSize(widthMeasureSpec);
		} else
			width = getLayoutParams().width;

		int height = bound_height;
		if (getLayoutParams().height == LayoutParams.WRAP_CONTENT)
			;
		else if ((getLayoutParams().height == LayoutParams.MATCH_PARENT)) {
			height = MeasureSpec.getSize(heightMeasureSpec);
		} else
			height = getLayoutParams().height;

		setMeasuredDimension(width, height);
	}

	private void createPaints() {

		valueLine = new Paint();
		//valueLine.setColor(0xFFF19700);
		valueLine.setColor(context.getResources().getColor(R.color.blue));
		valueLine.setStyle(Style.STROKE);
		valueLine.setStrokeWidth(3);

		valuePaint = new Paint();
		//valuePaint.setColor(0xFFF19700);
		valuePaint.setColor(context.getResources().getColor(R.color.blue));
		valuePaint.setStyle(Style.FILL);
		valuePaint.setAlpha(100);

	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {

		int left = 0;
		int right = bound_width;
		int top = 0;
		int bottom = bound_height;

		topCorner = new PointF((left + right) / 2, top);
		leftCorner = new PointF(left, (top + bottom) / 2);
		rightCorner = new PointF(right, (top + bottom) / 2);
		bottomCorner = new PointF((left + right) / 2, bottom);
		center = new PointF((left + right) / 2, (top + bottom) / 2);

		double s0, s1, s2, s3;
		s0 = scoreList == null ? 0.0 : scoreList.get(0);
		s1 = scoreList == null ? 0.0 : scoreList.get(1);
		s2 = scoreList == null ? 0.0 : scoreList.get(2);
		s3 = scoreList == null ? 0.0 : scoreList.get(3);

		s0 = Math.min(s0, 1.0);
		s1 = Math.min(s1, 1.0);
		s2 = Math.min(s2, 1.0);
		s3 = Math.min(s3, 1.0);

		p0 = new PointF((float) (topCorner.x * s0 + center.x * (1 - s0)),
				(float) (topCorner.y * s0 + center.y * (1 - s0)));
		p1 = new PointF((float) (leftCorner.x * s1 + center.x * (1 - s1)),
				(float) (leftCorner.y * s1 + center.y * (1 - s1)));
		p2 = new PointF((float) (bottomCorner.x * s2 + center.x * (1 - s2)),
				(float) (bottomCorner.y * s2 + center.y * (1 - s2)));
		p3 = new PointF((float) (rightCorner.x * s3 + center.x * (1 - s3)),
				(float) (rightCorner.y * s3 + center.y * (1 - s3)));

		canvas.drawLine(p0.x, p0.y, p1.x, p1.y, valueLine);
		canvas.drawLine(p1.x, p1.y, p2.x, p2.y, valueLine);
		canvas.drawLine(p2.x, p2.y, p3.x, p3.y, valueLine);
		canvas.drawLine(p3.x, p3.y, p0.x, p0.y, valueLine);

		Path path = new Path();
		path.moveTo(p0.x, p0.y);
		path.lineTo(p1.x, p1.y);
		path.lineTo(p2.x, p2.y);
		path.lineTo(p3.x, p3.y);
		path.lineTo(p0.x, p0.y);

		canvas.drawPath(path, valuePaint);

	}
}
