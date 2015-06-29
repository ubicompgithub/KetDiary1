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
import com.ubicomp.ketdiary.mydaybook.LineChartData;
import com.ubicomp.ketdiary.system.PreferenceControl;

public class LineChartView extends View {
	
	private static final String TAG = "LineChartView";
	
	private Context context = App.getContext();
	
    private static final int LINES = 7;
    private static float offsetY = 90;
    private static float offsetX = 60;
    private static float range;
    
    private static float touchX;
    public  List<DummyData> datapoints = new ArrayList<DummyData>();
    public  List<DummyData> datapoints2 = new ArrayList<DummyData>();
    
    private LineChartData[] dataset = null;
    		
    private Paint paint = new Paint();

    private int[] dots = {0, R.drawable.dot_color1, R.drawable.dot_color2, R.drawable.dot_color3, R.drawable.dot_color4, R.drawable.dot_color5, R.drawable.dot_color6, R.drawable.dot_color7, R.drawable.dot_color8};

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
    private int lastNoteAddNum = 0;

	public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        gestureDetector = new GestureDetector(context, new GestureListener());
        db = new DatabaseControl();
        startDay = PreferenceControl.getStartDate();
    	//dummyDataGenerator();
        //setChartData2();
        
        
        Calendar currentDay = Calendar.getInstance(); 
    	currentDay.setTimeInMillis(startDay.getTimeInMillis());
    	
    	numOfDays = daysOfTwo(startDay, Calendar.getInstance())+1;
        
        dataset = new LineChartData[numOfDays];
        
        //setChartData3();

    	
    	Log.d("drawDate", "numOfDays:"+numOfDays);
    }
	
	
	private void setChartData3(){
		Calendar currentDay = Calendar.getInstance(); 
    	currentDay.setTimeInMillis(startDay.getTimeInMillis());
    	
    	NoteAdd[] noteAdds = db.getAllNoteAdd();
    	if(noteAdds == null){
    		return;
    	}
    	if(noteAdds.length == this.lastNoteAddNum){ //check data update or not
    		return;
    	}
    	lastNoteAddNum = noteAdds.length;
    	
    	TestResult testResult;
    	NoteAdd[] noteAdd = null;
    	//numOfDays = daysOfTwo(startDay, Calendar.getInstance());  	
    	for (int i = 0; i < numOfDays ; i ++) {
    		float count = 0;
    		int self_type=0;
    		float self_score=0;
    		int other_type=0;
    		float other_score=0;
    		int year = currentDay.get(Calendar.YEAR);
    		int month = currentDay.get(Calendar.MONTH);
    		int day = currentDay.get(Calendar.DAY_OF_MONTH);;
    		int result = -1;
    		
    		testResult = db.getDayTestResult(year, month, day);    		
    		result = testResult.getResult();
    		
    		noteAdd = db.getDayNoteAddbyCategory(year, month, day, 0);
    		if(noteAdd!= null){
    			for(int j=0; j<noteAdd.length; j++){
    				count++;
    				self_score+= noteAdd[j].getImpact()-3; //要記得shift
    				int type = noteAdd[j].getType();
    				//if(drawTheDotOrNot(type)); // 假如一天記多種, 沒顯示的要不要可以filter
    					self_type = type;
    				Log.d(TAG, "SelfType: "+ self_type);
    			}
    			if(count>0){
    				self_score/=count;
    			}
    			if(self_type == 0)
    				self_type = noteAdd[0].getType();
    		}
    		count = 0;
    		noteAdd = db.getDayNoteAddbyCategory(year, month, day, 1);
    		if(noteAdd!= null){
    			for(int j=0; j<noteAdd.length; j++){
    				count++;
    				other_score+= noteAdd[j].getImpact()-3;
    				int type = noteAdd[j].getType();
    				//if(drawTheDotOrNot(type));
    					other_type = type;
    				Log.d(TAG, "OtherType: "+ other_type);
    			}    			
    			if(count>0){
    				other_score/=count;
    			}
    			if(other_type == 0)
    				other_type = noteAdd[0].getType();
    		}
    		
    		dataset[i] = new LineChartData(self_type, self_score, other_type, other_score, month, day, result);
    		//dataset[0] = new DummyData(category, cum_impact, type, month, date, 1);
	
    		currentDay.add(Calendar.DAY_OF_MONTH, 1);
    	}
		
		
    	Log.d(TAG, "data_length1: "+dataset.length);
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
    	
    	setChartData3();
    	
    	if  (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
    		offsetY = 90; 
    	}
    	else {
    		offsetY = 90; 
    	}
        drawBackground(canvas);
        drawDate2(canvas);
        drawCursor3(canvas);
        drawLineChart2(canvas);
        drawRectBar2(canvas);
        
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
    		
    		if(currentDate == 1 || i == 0){
    			paint.setStyle(Style.FILL);
    			paint.setColor(getResources().getColor(R.color.linechart_date_color));
    			paint.setTextAlign(Align.CENTER);
    			paint.setTextSize(35);
    			canvas.drawText(currentMonth+"/"+currentDate, getXPos2(i), getHeight() - 120, paint);
    		}
    		else if(i % 5 == 0){
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
	
	private void drawLineChart2(Canvas canvas) {
        Path path = new Path();
        Path path_self = new Path();
        Path path_other = new Path();
        Paint p = new Paint();
        
        //DrawLine
    	int startPoint = 0;
        for (int i = 0; i < dataset.length; i++) {
        	int selfType = dataset[i].getSelfType();
        	if (selfType < 6 && selfType > 0) {
        		path_self.moveTo(getXPos2(i), getYPos(dataset[i].getSelfScore()));
        		startPoint = i;
        		break;
        	}
        }
        for (int i = startPoint; i < dataset.length; i++) {
        	int selfType = dataset[i].getSelfType();
        	if (selfType < 6 && selfType > 0) {
        		path_self.lineTo(getXPos2(i), getYPos(dataset[i].getSelfScore()));
        	}
        	
        }
        startPoint = 0;
        for (int i = 0; i < dataset.length; i++) {
        	int othterType = dataset[i].getOtherType();
        	if (othterType > 5) {
        		path_other.moveTo(getXPos2(i), getYPos(dataset[i].getOtherScore()));
        		startPoint = i;
        		break;
        	}
        }
        for (int i = startPoint; i < dataset.length; i++) {
        	int othterType = dataset[i].getOtherType();
        	if (othterType > 5) {
        		path_other.lineTo(getXPos2(i), getYPos(dataset[i].getOtherScore()));
        	}
        	
        }

        switch (checkLineChartType()) {
        
        case 0: {
        	 paint.setStyle(Style.STROKE);
 	         paint.setStrokeWidth(4);
 	         paint.setColor(getResources().getColor(R.color.path_normal));
 	         paint.setAntiAlias(true);
 	         paint.setShadowLayer(4, 2, 2, 0x80000000);
 	         canvas.drawPath(path_self, paint);
 	         paint.setShadowLayer(0, 0, 0, 0);
        	
        	 for (int i = 0; i < dataset.length; i++) {
        		int selfType = dataset[i].getSelfType();
        		Log.d(TAG, "SelfType:" + selfType + " SelfScore:"+dataset[i].getSelfScore());
        		if (selfType > 5 || selfType < 1) continue;
 	        	if (drawTheDotOrNot(selfType)) {
	 	        	Bitmap tmp = BitmapFactory.decodeResource(getResources(), dots[selfType]);
	 	            Bitmap resizedImg = getResizedBitmap(tmp, 25, 25);
	 	            tmp.recycle();
	 	            System.gc();
	 	            canvas.drawBitmap(resizedImg, getXPos2(i)-12, getYPos(dataset[i].getSelfScore())-10, p);
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
        	
        	 for (int i = 0; i < dataset.length; i++) {
        		int otherType = dataset[i].getOtherType(); 
        		Log.d(TAG, "OtherType:" + otherType + " OtherScore:"+dataset[i].getOtherScore());
        		
        		if (otherType < 6) continue;
  	        	if (drawTheDotOrNot(otherType)) {
  	     
  	        		Bitmap tmp =  BitmapFactory.decodeResource(getResources(), dots[otherType]);
  	 	            Bitmap resizedImg = getResizedBitmap(tmp, 25, 25);
  	 	            tmp.recycle();
  	 	            System.gc();
  	 	            canvas.drawBitmap(resizedImg, getXPos2(i)-12, getYPos(dataset[i].getOtherScore())-10, p);
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
     
    private void drawRectBar2(Canvas canvas) { 
    	Paint p = new Paint();
    	
    	Log.d(TAG, "data_length: "+dataset.length);
    	for (int i = 0; i < dataset.length; i++) {
    		if (dataset[i].getResult() == 0) {
    			Bitmap temp = getLocalBitmap(context, R.drawable.pass_rect);
    			Bitmap passBarBg = getResizedBitmap(temp, 28, 60);
    			temp.recycle();
    			System.gc();
	            canvas.drawBitmap(passBarBg, getXPos2(i)-25, getHeight()-offsetY, p);
    		}
    		else if (dataset[i].getResult() == 1){
    			Bitmap temp = getLocalBitmap(context, R.drawable.nopass_rect);
    			Bitmap noPassBarBg = getResizedBitmap(temp, 28, 60);
    			temp.recycle();
    			System.gc();
	            canvas.drawBitmap(noPassBarBg, getXPos2(i)-25, getHeight()-offsetY, p);
    		}
    		else {
    			Bitmap temp = getLocalBitmap(context, R.drawable.skip_rect);
    			Bitmap skipBarBg = getResizedBitmap(temp, 28, 60);
    			temp.recycle();
    			System.gc();
	            canvas.drawBitmap(skipBarBg, getXPos2(i)-25, getHeight()-offsetY, p);
    		}
        }    	
    }
    
 
    private void drawCursor2(Canvas canvas) {
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
    	canvas.drawBitmap(cursor, touchX, 0, p);
    	
    }
    
    private void drawCursor3(Canvas canvas) {
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
    	canvas.drawBitmap(cursor, getXPos2(cursorLinePos)-12, 0, p);
    	
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


        return 50*value+getPaddingLeft()+offsetX;
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
    
    public int getCursorPos2(float x) {
    	/*
    	float xWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / numOfDays;
    	int temp = (int) Math.round(x/xWidth); 
    	return temp;*/
    	int posX = (int) ((x - offsetX - getPaddingLeft())/50);
    	return posX;
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
            //setZoomMode();
        	//setCanvasWidth();
    		//invalidate();
            return true;
        }
        
        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
        	float x = event.getX();
            float y = event.getY();
            
            touchX = x;
            
            cursorLinePos = getCursorPos2(x);
            if (cursorLinePos < 0) {
            	cursorLinePos = 0;
            }
            else if(cursorLinePos >= numOfDays){
            	cursorLinePos = numOfDays - 1;
            }
            else{
            	int year = Calendar.getInstance().get(Calendar.YEAR);
            	int month = dataset[cursorLinePos].getMonth();
            	int day = dataset[cursorLinePos].getDay();
            	
            	DaybookFragment.scrolltoItem(year, month, day);
            	
            }
            
            
            invalidate();
            return true;
        }
    }
    

}

