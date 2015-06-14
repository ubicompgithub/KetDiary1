package com.ubicomp.ketdiary.mydaybook.linechart;

import java.util.Calendar;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.ubicomp.ketdiary.R;

public class LineChartView extends View {

    private static final int LINES = 7;
    private static final int offsetY = 90;
    private static final int offsetX = 60;
    private static int range;
    private float[] datapoints = new float[] {};
    
    private Paint paint = new Paint();
    private Bitmap mBitmap;

    private Bitmap d1 = BitmapFactory.decodeResource(getResources(), R.drawable.green_dot_chart);
    private Bitmap d2 = BitmapFactory.decodeResource(getResources(), R.drawable.blue_dot_chart);
    //private Bitmap d3 = BitmapFactory.decodeResource(getResources(), R.drawable.green_dot_chart);
    private Bitmap d4 = BitmapFactory.decodeResource(getResources(), R.drawable.darkgreen_dot_chart);
    private Bitmap d5 = BitmapFactory.decodeResource(getResources(), R.drawable.orange_dot_chart);
    private Bitmap d6 = BitmapFactory.decodeResource(getResources(), R.drawable.red_dot_chart);
    private Bitmap d7 = BitmapFactory.decodeResource(getResources(), R.drawable.purple_dot_chart);
  
    private Bitmap rectBarBg = BitmapFactory.decodeResource(getResources(), R.drawable.gray_underbar);
    private Bitmap passBarBg = BitmapFactory.decodeResource(getResources(), R.drawable.pass_rect);
    private Bitmap noPassBarBg = BitmapFactory.decodeResource(getResources(), R.drawable.nopass_rect);
    
    private GestureDetector gestureDetector; 
    
    private static int NONE = 0;
    private static int ZOOM1 = 1;
    private static int ZOOM2 = 2;
    
    private int mode = 0;  
    private int cursorLinePos = 5;
    private int initHeight;

	public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        gestureDetector = new GestureDetector(context, new GestureListener());
        initHeight = getHeight();

    }

    public void setChartData(float[] datapoints) {
        this.datapoints = datapoints.clone();
        invalidate();
    }
    
 // override onSizeChanged
 	@Override
 	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
 		super.onSizeChanged(w, h, oldw, oldh);

 		// your Canvas will draw onto the defined Bitmap
 		mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
 	}
    
    @Override 
    public boolean onTouchEvent(MotionEvent event) {
    	gestureDetector.onTouchEvent(event);
        return true;    	
    }
    
    public void setZoomMode() {
    	if (mode == NONE) {
    		mode = ZOOM1;
    	}
    	else if (mode == ZOOM1) {
    		mode = ZOOM2;
    	}
    	else if (mode == ZOOM2) {
    		mode = NONE;
    	}    	
    }
    
    public int getZoomMode() {
    	return mode;
    }
    
    private void setCanvasWidth() {
    	this.requestLayout();    	
    	switch (mode) {
    	case 0:  
    		this.getLayoutParams().width = 2200;
    		break;
    	case 1:
    		this.getLayoutParams().width = 1600;
    		break;
    	case 2:
    		this.getLayoutParams().width = 1100;
    		break;
   	
    	}
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);   	
    	canvas.save();
    	
        drawBackground(canvas);
        drawDate(canvas);
        drawCursor(canvas);
        drawLineChart(canvas);
        drawRectBar(canvas);
        
        canvas.restore();
    }

    private void drawBackground(Canvas canvas) {
        range = getLineDistance();

        paint.setStyle(Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Align.LEFT);
        paint.setTextSize(35);
        paint.setStrokeWidth(1);
        
        for (int y = 1; y <= 7; y++) {
            final float yPos = y*range + offsetY;
            
            if (y == 1 || y == 4 || y == 7) {
            	canvas.drawLine(0, yPos, getWidth(), yPos, paint);
            	canvas.drawText(String.valueOf((-1*y)+4), getPaddingLeft(), yPos, paint);
            }
        }
    }

    
    private void drawDate(Canvas canvas) {
    	int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
    	int currentDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    	
    	int numOfDays = datapoints.length;
    	for (int i = 0; i < numOfDays ; i ++) {
    		if (mode == ZOOM2) {
    			if (i%5 != 0) continue;
    		}
		
    		paint.setStyle(Style.FILL);
    	    paint.setColor(Color.BLUE);
    	    paint.setTextAlign(Align.CENTER);
    		paint.setTextSize(35);
            canvas.drawText(String.valueOf(currentDate+i), getXPos(i), getHeight() - 2*offsetY, paint);
    	}
    }

	private int getLineDistance() {
        int distance;
        
        distance = getHeight() - getPaddingTop() - getPaddingBottom();
        distance = (distance/8)*5/LINES;

        return distance;
    }
	

    private void drawLineChart(Canvas canvas) {
        Path path = new Path();
        Paint p = new Paint();
        path.moveTo(getXPos(0), getYPos(datapoints[0]));
        for (int i = 1; i < datapoints.length; i++) {
            path.lineTo(getXPos(i), getYPos(datapoints[i]));
        }

        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setColor(Color.LTGRAY);
        paint.setAntiAlias(true);
        paint.setShadowLayer(4, 2, 2, 0x80000000);
        canvas.drawPath(path, paint);
        paint.setShadowLayer(0, 0, 0, 0);
        
        for (int i = 0; i < datapoints.length; i++) {
        	Bitmap bmp = assignDot(datapoints[i]);
            bmp = getResizedBitmap(bmp, 25, 25);
            canvas.drawBitmap(bmp, getXPos(i)-12, getYPos(datapoints[i])-10, p);
        }
    }
    private Bitmap assignDot(float value) {
    	Bitmap bm = null;
    	//Random r = new Random();
		//int ran_num = r.nextInt(7 - 1) + 1;
	    int ran_num = (int) (value + 4);
		switch (ran_num) {
		case 1:
			bm = d1;
			break;
		case 2:
			bm = d2;
			break;
		case 3:
			bm = d7;
			break;
		case 4:
			bm = d4;
			break;
		case 5:
			bm = d5;
			break;
		case 6:
			bm = d6;
			break;
		case 7:
			bm = d1;
			break;
		}
		return bm;
    }
    private void drawRectBar(Canvas canvas) {
    	Paint p = new Paint();
    	rectBarBg = getResizedBitmap(rectBarBg, 30, rectBarBg.getWidth());
    	canvas.drawBitmap(rectBarBg , 0 , getHeight() - 100 , p);
    	
    	for (int i = 0; i < datapoints.length; i++) {
    		Random r = new Random();
    		int ran_num = r.nextInt(4 - 1) + 1;
    		if (ran_num == 1) {
    			passBarBg = getResizedBitmap(passBarBg, 28, 60);
	            canvas.drawBitmap(passBarBg, getXPos(i)-25, getHeight()-100, p);
    		}
    		else if (ran_num == 2){
    			noPassBarBg = getResizedBitmap(noPassBarBg, 28, 60);
	            canvas.drawBitmap(noPassBarBg, getXPos(i)-25, getHeight()-100, p);
    		}   			
        }    	
    }
    
    private void drawCursor(Canvas canvas) {
    	// TODO : only draw above dates
    	
    	Paint p = new Paint();
    	p.setColor(Color.RED);
    	p.setStyle(Style.STROKE);
    	p.setStrokeWidth(5);
    	p.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
    	canvas.drawLine(getXPos(cursorLinePos), 0, getXPos(cursorLinePos), getHeight()-getPaddingTop(), p);
    	
    }

    private float getYPos(float value) {
      return range*(-1*value+4) + offsetY;
    }

    private float getXPos(float value) {
        float width = getWidth() - getPaddingLeft() - getPaddingRight();
        float maxValue = datapoints.length - 1;

        // scale it to the view size
        value = (value / maxValue) * width;

        // offset it to adjust for padding
        value += getPaddingLeft();

        return value + offsetX;
    }
    
    
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth)
    {
      	
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
    
    public int getCursorPos(float x) {
    	float xWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / datapoints.length;
    	int temp = (int) Math.round(x/xWidth); 
    	return temp;
    }
    
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");
            setZoomMode();
        	setCanvasWidth();
    		//invalidate();

            return true;
        }
        
        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
        	float x = event.getX();
            float y = event.getY();
            cursorLinePos = getCursorPos(x) - 2;
            if (cursorLinePos < 1) { return false;}
            invalidate();
            return true;
        }
    }
    

}

