package com.ubicomp.ketdiary.mydaybook.linechart;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.db.DatabaseControl;
import com.ubicomp.ketdiary.fragment.DaybookFragment;
import com.ubicomp.ketdiary.mydaybook.DummyData;
import com.ubicomp.ketdiary.system.PreferenceControl;

public class LineChartView extends View {
	
	private static final String TAG = "LineChartView";
	
    private static final int LINES = 7;
    private static float offsetY = 90;
    private static float offsetX = 60;
    private static float range;
    public  List<DummyData> datapoints = new ArrayList<DummyData>();
    public  List<DummyData> datapoints2 = new ArrayList<DummyData>();
    private DummyData[] dataset = null;
    		
    private Paint paint = new Paint();

    private int[] dots = {R.drawable.dot_color1, R.drawable.dot_color2, R.drawable.dot_color3, R.drawable.dot_color4, R.drawable.dot_color5, R.drawable.dot_color6, R.drawable.dot_color7, R.drawable.dot_color8};

    private GestureDetector gestureDetector; 
    private DatabaseControl db;
    private Calendar startDay;
    
    private static int NONE = 0;
    private static int ZOOM1 = 1;
    private static int ZOOM2 = 2;
    
    private int mode = 0;  
    private int cursorLinePos = 5;
    private int initHeight;
    private int numOfDays = 0;

	public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(context, new GestureListener());
        db = new DatabaseControl();
        startDay = PreferenceControl.getStartDate();
    	//dummyDataGenerator();
        //setChartData2();
        Calendar currentDay = Calendar.getInstance(); 
    	currentDay.setTimeInMillis(startDay.getTimeInMillis());
    	
    	numOfDays = daysOfTwo(startDay, Calendar.getInstance());
    	
    	dataset = new DummyData[numOfDays];
    	
    	Log.d("drawDate", "numOfDays:"+numOfDays);
    }
	private void setChartData3(){
		Calendar currentDay = Calendar.getInstance(); 
    	currentDay.setTimeInMillis(startDay.getTimeInMillis());
    	
    	TestResult testResult;
    	NoteAdd noteAdd[];
    	//numOfDays = daysOfTwo(startDay, Calendar.getInstance());  	
    	for (int i = 0; i < numOfDays ; i ++) {
    		testResult = db.getDayTestResult(currentDay.get(Calendar.YEAR), 
    				currentDay.get(Calendar.MONTH), currentDay.get(Calendar.DAY_OF_MONTH));
    				
    		
    		
    		//dataset[0] = new DummyData(category, cum_impact, type, month, date, 1);
    		
    		
    		
    		currentDay.add(Calendar.DAY_OF_MONTH, 1);
    	}
		
		
		
	}
	
	
	public void setChartData2() {
    	NoteAdd[] noteAdds = db.getAllNoteAdd();
    	int previous_date = 0;
    	int data_num=-1;
    	int count = 1;
    	float cum_impact = 0;
    	DummyData singleData;
    	
    	if(noteAdds == null)
    		return;
    	
    	Log.d(TAG, String.valueOf(noteAdds.length));
    	
    	
		for(int i=0; i < noteAdds.length; i++){
						
			int category =noteAdds[i].getCategory();
			int month = noteAdds[i].getRecordTv().getMonth();
			int date = noteAdds[i].getRecordTv().getDay();
			int type = noteAdds[i].getType();
			int impact = noteAdds[i].getImpact()-3;
			
			if(category == 1){
				if(date!= previous_date){
					if(count!= 0){
						cum_impact/=(float)count;
						
						singleData = new DummyData(category, cum_impact, cum_impact, type, month, date, 1);
						datapoints.add(singleData);	
						count = 1;
					}
					cum_impact = impact;
				}
				else{
					cum_impact+=impact;
					count++;
				}
				previous_date = date;
			}
			else
				continue;
		}
		for(int i=0; i < noteAdds.length; i++){
			
			int category =noteAdds[i].getCategory();
			int month = noteAdds[i].getRecordTv().getMonth();
			int date = noteAdds[i].getRecordTv().getDay();
			int type = noteAdds[i].getType();
			int impact = noteAdds[i].getImpact()-3;
			
			if(category == 2){
				if(date!= previous_date){
					if(count!= 0){
						cum_impact/=(float)count;
						
						singleData = new DummyData(category, cum_impact, cum_impact, type, month, date, 1);
						datapoints2.add(singleData);	
						count = 1;
					}
					cum_impact = impact;
				}
				else{
					cum_impact+=impact;
					count++;
				}
				previous_date = date;
			}
			else
				continue;
		}
		
		
		
		
		//datapoints = ArrayUtils.toPrimitive(list.toArray(new Float[0]), 0.0F);
		//data.toArray( datapoints );
		Log.d(TAG, datapoints.size()+" "+datapoints2.size());
    }

	
	public void dummyDataGenerator() {
		for (int i = 0; i < 30 ; i++) {
			Random r1 = new Random();
			Random r2 = new Random();
			int activityType;
			int who = r1.nextInt(3 - 1) + 1;
			
			if (who == 1) {
				activityType = r2.nextInt(6 - 1) + 1;
				
			}
			else {
				activityType = r2.nextInt(9 - 6) + 6;
			}
			
			
			Random r3 = new Random();
			float score = r3.nextInt(4 - (-3)) + (-3);
			Random r4 = new Random();
			boolean passTest;
			int pass = r4.nextInt(4 - 1) + 1;
			
			
			DummyData singleData = new DummyData(who, score, score, activityType, 6, i+1, pass);
			datapoints.add(singleData);	
		}
		/*Log.i("OMG", "type:" + MainActivity.getChartType());
		datapoints = new ArrayList<DummyData>();
		if (MainActivity.getChartType() == 1) {
			// Test 1
			for (int i = 0; i < 15 ; i++) {
				DummyData singleData;
				if ( i == 4 || i == 5 || i == 7) {
					singleData = new DummyData(2, -2, 7, d7 , 6, i+1, false);
				}
				else if ( i < 4 || i ==9 || i==10  ) {
					singleData = new DummyData(1, 3, 3, d3 , 6, i+1, true);
				}
				else {
					singleData = new DummyData(1, 0, 1, d1 , 6, i+1, true);
					
				}
				
				datapoints.add(singleData);
			}
		}
		else {
			// Test 2
			for (int i = 0; i < 15 ; i++) {
				DummyData singleData;
				if ( i < 7) {
					Random r1 = new Random();
					Random r2 = new Random();
					int activityType;
					int who = 1;
					
					if (who == 1) {
						activityType = r2.nextInt(6 - 3) + 3;
						
					}
					else {
						activityType = r2.nextInt(7 - 6) + 6;
					}
					
					Bitmap bmp = dotArray[activityType-1];
					
					Random r3 = new Random();
					int score = r3.nextInt(4 - 1) + 1;
					Random r4 = new Random();
					boolean passTest;
					int pass = r4.nextInt(3 - 1) + 1;
					
					passTest = true;
					
					
					singleData = new DummyData(who, score, activityType, bmp , 6, i+1, passTest);
				}
				else if ( i == 7) {
					singleData = new DummyData(1, -3, 2, d2 , 6, i+1, true);
				}
				else if ( i == 8) {
					singleData = new DummyData(1, -3, 2, d2 , 6, i+1, false);
				}
				else  {
					Random r1 = new Random();
					Random r2 = new Random();
					int activityType;
					int who = r1.nextInt(2 - 1) + 1;
					
					if (who == 1) {
						activityType = r2.nextInt(3 - 1) + 1;
						
					}
					else {
						activityType = r2.nextInt(9 - 6) + 6;
					}
					
					Bitmap bmp = dotArray[activityType-1];
					
					Random r3 = new Random();
					int score = r3.nextInt((-1) - (-3)) + (-3);
					Random r4 = new Random();
					boolean passTest;
					int pass = r4.nextInt(3 - 1) + 1;
					
					passTest = false;
					
					singleData = new DummyData(who, score, activityType, bmp , 6, i+1, passTest);
				}
				
				
				datapoints.add(singleData);
			}
		}*/
		

	}

    
 // override onSizeChanged
 	@Override
 	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
 		super.onSizeChanged(w, h, oldw, oldh);

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

    	if  (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { offsetY = 90; }
    	else { offsetY = 30; }
        drawBackground(canvas);
        drawDate2(canvas);
        drawCursor(canvas);
        drawLineChart(canvas);
        drawRectBar(canvas);
        
        canvas.restore();
    }

    private void drawBackground(Canvas canvas) {
        range = getLineDistance();

        paint.setStyle(Style.FILL);
        paint.setColor(getResources().getColor(R.color.linechart_bgline_color));
        paint.setTextAlign(Align.LEFT);
        paint.setTextSize(35);
        paint.setStrokeWidth(1);
        Paint line_paint = paint;
        line_paint.setColor(getResources().getColor(R.color.linechart_score_color));
        for (int y = 1; y <= 7; y++) {
            final float yPos = y*range + offsetY;
            
            if (y == 1 || y == 4 || y == 7) {
            	canvas.drawLine(0, yPos, getWidth(), yPos, paint);        	
            	canvas.drawText(String.valueOf((-1*y)+4), getPaddingLeft(), yPos, line_paint);
            }
        }
    }
    
    public int daysOfTwo(Calendar befor, Calendar after) {
        long m = after.getTimeInMillis() - befor.getTimeInMillis();
        m=m/(24*60*60*1000);
        //判斷是不是同一天
        if(m==0 && after.get(Calendar.DAY_OF_YEAR)!=befor.get(Calendar.DAY_OF_YEAR)){
            m+=1;
        }
        return (int)m;
    }
    
    private void drawDate2(Canvas canvas) {
    	
    	Calendar currentDay = Calendar.getInstance(); 
    	currentDay.setTimeInMillis(startDay.getTimeInMillis());
    	
    	//numOfDays = daysOfTwo(startDay, Calendar.getInstance());
    	
    	
    	for (int i = 0; i < numOfDays ; i ++) {
    		int currentMonth = currentDay.get(Calendar.MONTH)+1;
        	int currentDate= currentDay.get(Calendar.DAY_OF_MONTH);
    		
    		if(currentDate == 1){
    			paint.setStyle(Style.FILL);
    			paint.setColor(getResources().getColor(R.color.linechart_date_color));
    			paint.setTextAlign(Align.CENTER);
    			paint.setTextSize(35);
    			canvas.drawText(currentMonth+"/"+currentDate, getXPos2(i), getHeight() - 120, paint);
    		}
    		else if(currentDate % 5 == 0){
    			paint.setStyle(Style.FILL);
    			paint.setColor(getResources().getColor(R.color.linechart_date_color));
    			paint.setTextAlign(Align.CENTER);
    			paint.setTextSize(35);
    			canvas.drawText(""+currentDate, getXPos2(i), getHeight() - 120, paint);
    		}
    		currentDay.add(Calendar.DAY_OF_MONTH, 1);
    		
    	}
    		
    }

    
    private void drawDate(Canvas canvas) {
    	int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
    	//int currentDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);//TODO: change to start day
    	int currentDate=1;
    	
    	switch (checkLineChartType()) {
    	case 0:
    	int numOfDays = datapoints.size();
    	if(numOfDays > 0){
    		currentDate = datapoints.get(0).day;
    	}
    		
    	for (int i = 0; i < numOfDays ; i ++) {
    		if (mode == ZOOM2) {
    			if (i%5 != 0) continue;
    		}
		
    		paint.setStyle(Style.FILL);
    	    paint.setColor(getResources().getColor(R.color.linechart_date_color));
    	    paint.setTextAlign(Align.CENTER);
    		paint.setTextSize(35);
            canvas.drawText(String.valueOf(currentDate+i), getXPos(i), getHeight() - 120, paint);
    	}
    	break;
    	case 1:
        	numOfDays = datapoints2.size();
        	if(numOfDays > 0){
        		currentDate = datapoints2.get(0).day;
        	}
        	
        	for (int i = 0; i < numOfDays ; i ++) {
        		if (mode == ZOOM2) {
        			if (i%5 != 0) continue;
        		}
    		
        		paint.setStyle(Style.FILL);
        	    paint.setColor(getResources().getColor(R.color.linechart_date_color));
        	    paint.setTextAlign(Align.CENTER);
        		paint.setTextSize(35);
                canvas.drawText(String.valueOf(currentDate+i), getXPos(i), getHeight() - 120, paint);
        	}
        	break; 
    	case 2:
    		numOfDays = datapoints2.size() >=  datapoints.size()? datapoints2.size() : datapoints.size();
    		
    		if(numOfDays > 0){
        		currentDate = datapoints2.size() >=  datapoints.size()? datapoints2.get(0).day : datapoints.get(0).day;
        	}
    		
        	for (int i = 0; i < numOfDays ; i ++) {
        		if (mode == ZOOM2) {
        			if (i%5 != 0) continue;
        		}
    		
        		paint.setStyle(Style.FILL);
        	    paint.setColor(getResources().getColor(R.color.linechart_date_color));
        	    paint.setTextAlign(Align.CENTER);
        		paint.setTextSize(35);
                canvas.drawText(String.valueOf(currentDate+i), getXPos(i), getHeight() - 120, paint);
        	}    		
    		break;
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
        Path path_self = new Path();
        Path path_other = new Path();
        Paint p = new Paint();
        
    	int startPoint = 0;
        for (int i = 0; i < datapoints.size(); i++) {
        	if (datapoints.get(i).activityType < 6) {
        	path_self.moveTo(getXPos(i), getYPos(datapoints.get(i).self_score));
        	startPoint = i;
        	break;
        	}
        }
        for (int i = startPoint; i < datapoints.size(); i++) {
        	if (datapoints.get(i).activityType < 6) {
            path_self.lineTo(getXPos(i), getYPos(datapoints.get(i).self_score));
        	}
        	
        }
        startPoint = 0;
        for (int i = 0; i < datapoints2.size(); i++) {
        	if (datapoints2.get(i).activityType > 5) {
        	path_other.moveTo(getXPos(i), getYPos(datapoints2.get(i).self_score));
        	startPoint = i;
        	break;
        	}
        }
        for (int i = startPoint; i < datapoints2.size(); i++) {
        	if (datapoints2.get(i).activityType > 5) {
            path_other.lineTo(getXPos(i), getYPos(datapoints2.get(i).self_score));
        	}
        	
        }
        /*
        if (checkLineChartType() <2 ) {
	        path.moveTo(getXPos(0), getYPos(datapoints.get(0).score));
	        for (int i = 1; i < datapoints.size(); i++) {
	            path.lineTo(getXPos(i), getYPos(datapoints.get(i).score));
	        }
	
	        paint.setStyle(Style.STROKE);
	        paint.setStrokeWidth(4);
	        paint.setColor(getResources().getColor(R.color.path_normal));
	        paint.setAntiAlias(true);
	        paint.setShadowLayer(4, 2, 2, 0x80000000);
	        canvas.drawPath(path, paint);
	        paint.setShadowLayer(0, 0, 0, 0);
        }
        else {
        
            paint.setStyle(Style.STROKE);
	        paint.setStrokeWidth(4);
	        paint.setColor(getResources().getColor(R.color.path_other_color));
	        paint.setAntiAlias(true);
	        paint.setShadowLayer(4, 2, 2, 0x80000000);
	        canvas.drawPath(path_other, paint);
	        
	        Paint paint_self = paint;
	        paint_self.setColor(getResources().getColor(R.color.path_self_color));
	        canvas.drawPath(path_self, paint_self);
	        
	        paint.setShadowLayer(0, 0, 0, 0);
	        paint_self.setShadowLayer(0, 0, 0, 0);
        }
        */
        switch (checkLineChartType()) {
        
        case 0: {
        	 paint.setStyle(Style.STROKE);
 	         paint.setStrokeWidth(4);
 	         paint.setColor(getResources().getColor(R.color.path_normal));
 	         paint.setAntiAlias(true);
 	         paint.setShadowLayer(4, 2, 2, 0x80000000);
 	         canvas.drawPath(path_self, paint);
 	         paint.setShadowLayer(0, 0, 0, 0);
        	
        	 for (int i = 0; i < datapoints.size(); i++) {
        		if (datapoints.get(i).activityType > 5) continue;
 	        	if (drawTheDotOrNot(datapoints.get(i).activityType)) {
	 	        	Bitmap tmp = BitmapFactory.decodeResource(getResources(), dots[datapoints.get(i).activityType-1]);
	 	            Bitmap resizedImg = getResizedBitmap(tmp, 25, 25);
	 	            tmp.recycle();
	 	            System.gc();
	 	            canvas.drawBitmap(resizedImg, getXPos(i)-12, getYPos(datapoints.get(i).self_score)-10, p);
 	        	}
 	        }
        	break;
        }
        case 1: {
        	 
        	
        	 paint.setStyle(Style.STROKE);
 	         paint.setStrokeWidth(4);
 	         paint.setColor(getResources().getColor(R.color.path_normal));
 	         paint.setAntiAlias(true);
 	         paint.setShadowLayer(4, 2, 2, 0x80000000);
 	         canvas.drawPath(path_other, paint);
 	         paint.setShadowLayer(0, 0, 0, 0);
        	
        	 for (int i = 0; i < datapoints2.size(); i++) {
         		if (datapoints2.get(i).activityType < 6) continue;
  	        	if (drawTheDotOrNot(datapoints2.get(i).activityType)) {
  	        		Bitmap tmp =  BitmapFactory.decodeResource(getResources(), dots[datapoints2.get(i).activityType-1]);
  	 	            Bitmap resizedImg = getResizedBitmap(tmp, 25, 25);
  	 	            tmp.recycle();
  	 	            System.gc();
  	 	            canvas.drawBitmap(resizedImg, getXPos(i)-12, getYPos(datapoints2.get(i).self_score)-10, p);
  	        	}
  	        }
        	break;
        }
        
        case 2: {
        	paint.setStyle(Style.STROKE);
	        paint.setStrokeWidth(4);
	        paint.setColor(getResources().getColor(R.color.path_other_color));
	        paint.setAntiAlias(true);
	        paint.setShadowLayer(4, 2, 2, 0x80000000);
	        canvas.drawPath(path_other, paint);
	        
	        Paint paint_self = paint;
	        paint_self.setColor(getResources().getColor(R.color.path_self_color));
	        canvas.drawPath(path_self, paint_self);
	        
	        paint.setShadowLayer(0, 0, 0, 0);
	        paint_self.setShadowLayer(0, 0, 0, 0);
	        Bitmap tmp = BitmapFactory.decodeResource(getResources(), R.drawable.linechart_legend);
        	Bitmap legend = getResizedBitmap(tmp, 40, 350);
        	tmp.recycle();
        	System.gc();
        	canvas.drawBitmap(legend, getPaddingLeft(), getPaddingTop(), p);
        	break;
        }       
      }
    }
    
    private int checkLineChartType () {
    	//return MainActivity.getChartType();
    	return DaybookFragment.chart_type;
    }
    
    private boolean drawTheDotOrNot (int typeOfActivity) {
    	if (DaybookFragment.filterButtonIsPressed[0]) return true;
    	else return DaybookFragment.filterButtonIsPressed[typeOfActivity]; 
    }
    
    private void drawRectBar(Canvas canvas) { //TODO: 
    	Paint p = new Paint();
    	
    	for (int i = 0; i < datapoints.size(); i++) {
    		if (datapoints.get(i).passTest == 1) {
    			Bitmap temp = getLocalBitmap(App.getContext(), R.drawable.pass_rect);
    			Bitmap passBarBg = getResizedBitmap(temp, 28, 60);
    			temp.recycle();
    			System.gc();
	            canvas.drawBitmap(passBarBg, getXPos(i)-25, getHeight()-offsetY, p);
    		}
    		else if (datapoints.get(i).passTest == 2){
    			Bitmap temp = getLocalBitmap(App.getContext(), R.drawable.nopass_rect);
    			Bitmap noPassBarBg = getResizedBitmap(temp, 28, 60);
    			temp.recycle();
    			System.gc();
	            canvas.drawBitmap(noPassBarBg, getXPos(i)-25, getHeight()-offsetY, p);
    		}
    		else {
    			Bitmap temp = getLocalBitmap(App.getContext(), R.drawable.skip_rect);
    			Bitmap skipBarBg = getResizedBitmap(temp, 28, 60);
    			temp.recycle();
    			System.gc();
	            canvas.drawBitmap(skipBarBg, getXPos(i)-25, getHeight()-offsetY, p);
    		}
        }    	
    }
    
    private void drawCursor(Canvas canvas) {
    	// TODO : only draw above dates
    	
    	Paint p = new Paint();
    	//p.setColor(Color.RED);
    	//p.setStyle(Style.STROKE);
    	//p.setStrokeWidth(5);
    	//p.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
    	Bitmap tmp = getLocalBitmap(App.getContext(), R.drawable.linechart_cursor);
    	Bitmap cursor = getResizedBitmap(tmp, getHeight() - 2*getPaddingTop() - 2*getPaddingBottom() , 25);
    	tmp.recycle();
    	System.gc();
    	canvas.drawBitmap(cursor, getXPos(cursorLinePos) - 13, 0, p);
    	
    }

    private float getYPos(float value) {
      return range*(-1*value+4) + offsetY;
    }

    private float getXPos(float value) {
        float width = getWidth() - getPaddingLeft() - getPaddingRight();
        float maxValue = datapoints.size() - 1;

        // scale it to the view size
        value = (value / maxValue) * width;

        // offset it to adjust for padding
        value += getPaddingLeft();

        return value + offsetX;
    }
    
    private float getXPos2(float value) {
        float width = getWidth() - getPaddingLeft() - getPaddingRight();
        float maxValue = datapoints.size() - 1;


        return 35*value+getPaddingLeft()+offsetX;
    }
    
    
    public Bitmap getLocalBitmap(Context con, int resourceId){
        InputStream inputStream = con.getResources().openRawResource(resourceId);
        return BitmapFactory.decodeStream(inputStream, null, getBitmapOptions(2));
    }
    
    public BitmapFactory.Options getBitmapOptions(int scale){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inSampleSize = scale;
        return options;
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
    
    public Bitmap sizeDownBitmap(String path){
    	 //Only decode image size. Not whole image

        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, option);

        //The new size to decode to 
        final int NEW_SIZE=100;
        
        //Now we have image width and height. We should find the correct scale value. (power of 2)
        int width=option.outWidth;
        int height=option.outHeight;
        int scale=1;
        while(true){

            if(width/2<NEW_SIZE || height/2<NEW_SIZE)
                break;
            width/=2;
            height/=2;
            scale++;
        }
        //Decode again with inSampleSize
        option = new BitmapFactory.Options();
        option.inSampleSize=scale;
        return BitmapFactory.decodeFile(path, option);
    }
    
    public int getCursorPos(float x) {
    	float xWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / datapoints.size();
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

