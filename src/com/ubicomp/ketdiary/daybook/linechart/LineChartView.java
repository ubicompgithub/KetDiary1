package com.ubicomp.ketdiary.daybook.linechart;

import java.io.InputStream;
import java.util.Calendar;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.data.db.DatabaseControl;
import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.daybook.LineChartData;
import com.ubicomp.ketdiary.main.fragment.DaybookFragment;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.system.clicklog.ClickLog;
import com.ubicomp.ketdiary.system.clicklog.ClickLogId;

public class LineChartView extends View {
	
	private static final String TAG = "LineChartView";
	
	private static Context context = App.getContext();
	private static Resources resource = context.getResources();
	
    private static final int LINES = 7;
    private static final float offsetY = resource.getDimensionPixelSize(R.dimen.offsetY);
    private static final float offsetX = resource.getDimensionPixelSize(R.dimen.offsetX);
    private static final int unit_scale = resource.getDimensionPixelSize(R.dimen.unit_scale);//50;
    private static final float date_Y_offset = resource.getDimensionPixelSize(R.dimen.date_Y_offset);//120;
    private static final int dot_scale = resource.getDimensionPixelSize(R.dimen.dot_scale);//25;
    private static final int dot_X_offset = resource.getDimensionPixelSize(R.dimen.dot_X_offset);//12;
    private static final int dot_Y_offset = resource.getDimensionPixelSize(R.dimen.dot_Y_offset);//10;
    private static final int rec_height = resource.getDimensionPixelSize(R.dimen.rec_height);//28;
    private static final int rec_width = resource.getDimensionPixelSize(R.dimen.rec_width);//60;
    private static final int rec_X_offset = resource.getDimensionPixelSize(R.dimen.rec_X_offset);//25;
    private static final int cur_X_offset = resource.getDimensionPixelSize(R.dimen.cur_X_offset);//12;
    private static final int legend_height =resource.getDimensionPixelSize(R.dimen.legend_height);//40;
    private static final int legend_width = resource.getDimensionPixelSize(R.dimen.legend_width);//350;
    private static final int window_width = resource.getDimensionPixelSize(R.dimen.window_width);//480dp;
    
    private static float range; 
    private static float touchX;
    
    private static LineChartData[] dataset = null;
    		
    private Paint paint = new Paint();

    private int[] dots = {0, R.drawable.dot_color1, R.drawable.dot_color2, R.drawable.dot_color3, R.drawable.dot_color4, R.drawable.dot_color5, R.drawable.dot_color6, R.drawable.dot_color7, R.drawable.dot_color8};
    
    private Bitmap[] dot_array ; //= {null, dot1, dot2, dot3, dot4, dot5, dot6, dot7, dot8};
    private Bitmap  passBarBg;
    private Bitmap noPassBarBg;
    private Bitmap skipBarBg;
    private Bitmap cursor;
    private Bitmap legend;
    
    private GestureDetector gestureDetector; 
    private DatabaseControl db;
    private Calendar startDay;
    
    private static int NONE = 0;
    private static int ZOOM1 = 1;
    private static int ZOOM2 = 2;
    
    private int mode = 0;  
    private int cursorLinePos = 0;
    private int initHeight;
    public static int numOfDays = 0;
    private int lastNoteAddNum = 0;
    private boolean drawBackground = true;

	public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        gestureDetector = new GestureDetector(context, new GestureListener());
        db = new DatabaseControl();
        startDay = PreferenceControl.getFirstUsedDateMinus();
    	//dummyDataGenerator();
        //setChartData2();
        
        
        Calendar currentDay = Calendar.getInstance(); 
    	currentDay.setTimeInMillis(startDay.getTimeInMillis());
    	
    	numOfDays = daysOfTwo(startDay, Calendar.getInstance())+1;
        if(numOfDays < 0)
        	numOfDays = 1;
        dataset = new LineChartData[numOfDays];
        initDataset();
        //setChartData3();
        //initBitmap();
        
        
    	Log.d("drawDate", "numOfDays:"+numOfDays);
    }
	public void setWidth(){
		this.requestLayout();
		//this.getLayoutParams().width = (int) (numOfDays*rec_width);
		int width = (int) getXPos2(numOfDays);
		if(width < window_width)
			this.getLayoutParams().width = window_width;
		else
			this.getLayoutParams().width = width;
	}
	
	private void initBitmap(){
		dot_array = new Bitmap[9];
		dot_array[0] = null;
		for(int i=1; i<=8;i++){
			Bitmap tmp = getLocalBitmap(context, dots[i]);		
			Bitmap resizedImg = getResizedBitmap(tmp, dot_scale, dot_scale);
			dot_array[i] = resizedImg;
		}
		
		Bitmap tmp = getLocalBitmap(context, R.drawable.pass_rect);
		passBarBg = getResizedBitmap(tmp, rec_height, rec_width);
		tmp.recycle();
		
		tmp = getLocalBitmap(context, R.drawable.nopass_rect);
		noPassBarBg = getResizedBitmap(tmp, rec_height, rec_width);
		tmp.recycle();
		
		tmp = getLocalBitmap(context, R.drawable.skip_rect);
		skipBarBg = getResizedBitmap(tmp, rec_height, rec_width);
		tmp.recycle();
		/*tmp = getLocalBitmap(context, R.drawable.linechart_cursor);
    	cursor = getResizedBitmap(tmp, getHeight() - 2*getPaddingTop() - 2*getPaddingBottom() , dot_scale);*/
   	
    	tmp = getLocalBitmap(context, R.drawable.linechart_legend);
    	legend = getResizedBitmap(tmp, legend_height, legend_width);
    	tmp.recycle();
    	System.gc();
	}
	private void releaseBitmap(){

		for(int i=1; i<=8;i++){
			dot_array[i].recycle();
		}
		passBarBg.recycle();
		noPassBarBg.recycle();
		skipBarBg.recycle();
    	legend.recycle();
    	if(cursor!= null)
    		cursor.recycle();
    	System.gc();
	
	}
	

	private void initDataset(){ //to make sure dataset with not null object
		Calendar currentDay = Calendar.getInstance(); 
    	currentDay.setTimeInMillis(startDay.getTimeInMillis());
    	
    	for (int i = 0; i < numOfDays ; i ++) {
    		int self_type=0;
    		float self_score=0;
    		int other_type=0;
    		float other_score=0;
    		int year = currentDay.get(Calendar.YEAR);
    		int month = currentDay.get(Calendar.MONTH);
    		int day = currentDay.get(Calendar.DAY_OF_MONTH);;
    		
    		dataset[i] = new LineChartData(self_type, self_score, other_type, other_score, month, day, -1);
    		//dataset[0] = new DummyData(category, cum_impact, type, month, date, 1);
	
    		currentDay.add(Calendar.DAY_OF_MONTH, 1);
    	}
    	
	}
	
	
	private void setChartData3(){
		Calendar currentDay = Calendar.getInstance(); 
    	currentDay.setTimeInMillis(startDay.getTimeInMillis());
    	
    	NoteAdd[] noteAdds = db.getAllNoteAdd(); //假如都沒新增記事顯示會有問題
    	if(noteAdds == null){
    		//return;
    	}else{
	    	if(noteAdds.length == this.lastNoteAddNum){ //check data update or not
	    		Log.d(TAG, "no Update");
	    		return;
	    	}
	    	lastNoteAddNum = noteAdds.length;
    	}
    	
    	
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
    		
    		Log.d(TAG, "Day: " + day);
    		int result = -1;
    		
    		testResult = db.getDayTestResult(year, month, day);    		
    		result = testResult.getResult();
    		
            // 自我狀態
    		noteAdd = db.getDayNoteAddbyCategory(year, month, day, 0);
    		if(noteAdd!= null){
    			for(int j=0; j<noteAdd.length; j++){
    				count++;
    				self_score+= noteAdd[j].getImpact()-3; //要記得shift
    				int type = noteAdd[j].getType();
    				//if(drawTheDotOrNot(type)); // 假如一天記多種, 沒顯示的要不要可以filter
    					self_type = type;
    				//Log.d(TAG, "SelfType: "+ self_type);
    			}
    			if(count>0){
    				self_score/=count;
    			}
    			//if(self_type == 0)
    			//	self_type = noteAdd[0].getType();
    		}
    		count = 0;
            // 人際互動
    		noteAdd = db.getDayNoteAddbyCategory(year, month, day, 1);
    		if(noteAdd!= null){
    			for(int j=0; j<noteAdd.length; j++){
    				count++;
    				other_score+= noteAdd[j].getImpact()-3;
    				int type = noteAdd[j].getType();
    				//if(drawTheDotOrNot(type));
    					other_type = type;
    				//Log.d(TAG, "OtherType: "+ other_type);
    			}    			
    			if(count>0){
    				other_score/=count;
    			}
    			//if(other_type == 0)
    			//	other_type = noteAdd[0].getType();
    		}
    		
    		dataset[i] = new LineChartData(self_type, self_score, other_type, other_score, month, day, result);
    		//dataset[0] = new DummyData(category, cum_impact, type, month, date, 1);
	
    		currentDay.add(Calendar.DAY_OF_MONTH, 1);
    	}
		
		
    	Log.d(TAG, "data_length1: "+dataset.length);
	}

	public float getDensity(){
		 DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		 return metrics.density;
		}
	
	public float convertDpToPixel(float dp){
	    float px = dp * getDensity();
	    //Log.d(TAG, "density:" + getDensity());
	    return px;
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
    	
        
    	initBitmap();
    	setChartData3();
    	
    	if  (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
    		//offsetY = 90; 
    	}
    	else {
    		//offsetY = 90; 
    	}
    	//if(drawBackground){
	        drawBackground(canvas);
	        drawRectBar2(canvas);
	        drawDate2(canvas);
    	
        //看能不能獨立update
        drawCursor3(canvas);
        drawLineChart2(canvas);
        
        releaseBitmap();
        canvas.restore();
    }

    private void drawBackground(Canvas canvas) {
        range = getLineDistance();

        paint.setStyle(Style.FILL);
        paint.setColor(getResources().getColor(R.color.linechart_bgline_color));
        paint.setTextAlign(Align.LEFT);
        paint.setTextSize(convertDpToPixel((float)11.33));
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
    			paint.setTextSize(convertDpToPixel((float)11.33));
    			canvas.drawText(currentMonth+"/"+currentDate, getXPos2(i), getHeight() - date_Y_offset, paint);
    		}
    		else if(i % 5 == 0 || i== numOfDays-1){
    			paint.setStyle(Style.FILL);
    			paint.setColor(getResources().getColor(R.color.linechart_date_color));
    			paint.setTextAlign(Align.CENTER);
    			paint.setTextSize(convertDpToPixel((float)11.33));
    			canvas.drawText(""+currentDate, getXPos2(i), getHeight() - date_Y_offset, paint);
    		}
    		
    		currentDay.add(Calendar.DAY_OF_MONTH, 1);
    		
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
        if(dataset == null)
        	return;
        
    	int startPoint = 0;
        for (int i = 0; i < dataset.length; i++) {
        	//Log.d(TAG, "length: " + dataset.length);
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
        		//Log.d(TAG, "SelfType:" + selfType + " SelfScore:"+dataset[i].getSelfScore());
        		if (selfType > 5 || selfType < 1) continue;
 	        	if (drawTheDotOrNot(selfType)) {
	 	            canvas.drawBitmap(dot_array[selfType], getXPos2(i)-dot_X_offset, getYPos(dataset[i].getSelfScore())-dot_Y_offset, p);
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
        		//Log.d(TAG, "OtherType:" + otherType + " OtherScore:"+dataset[i].getOtherScore());
        		
        		if (otherType < 6) continue;
  	        	if (drawTheDotOrNot(otherType)) {
  	 	            canvas.drawBitmap(dot_array[otherType], getXPos2(i)-dot_X_offset, getYPos(dataset[i].getOtherScore())-dot_Y_offset, p);
  	        	}
  	        }
        	break;
        }
        
        case 2: {
        	paint.setStyle(Style.STROKE);
	        paint.setStrokeWidth(8);
	        paint.setColor(getResources().getColor(R.color.path_other_color));
	        paint.setAntiAlias(true);
	        paint.setShadowLayer(4, 2, 2, 0x80000000);
	        canvas.drawPath(path_other, paint);
	        
	        Paint paint_self = paint;
	        paint_self.setColor(getResources().getColor(R.color.path_self_color));
	        canvas.drawPath(path_self, paint_self);
	        
	        paint.setShadowLayer(0, 0, 0, 0);
	        paint_self.setShadowLayer(0, 0, 0, 0);
	        
        	canvas.drawBitmap(legend, getPaddingLeft(), getPaddingTop(), p);
        	break;
        }       
      }
    }
	
	public LineChartData[] getLineChartData(){
		return dataset;
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
    	
    	//Log.d(TAG, "data_length: "+dataset.length);
    	for (int i = 0; i < dataset.length; i++) {
    		if (dataset[i].getResult() == 0) {
	            canvas.drawBitmap(passBarBg, getXPos2(i)-rec_X_offset, getHeight()-offsetY, p);
    		}
    		else if (dataset[i].getResult() == 1){
	            canvas.drawBitmap(noPassBarBg, getXPos2(i)-rec_X_offset, getHeight()-offsetY, p);
    		}
    		else {
	            canvas.drawBitmap(skipBarBg, getXPos2(i)-rec_X_offset, getHeight()-offsetY, p);
    		}
        }    	
    }
    

    
    private void drawCursor3(Canvas canvas) {
    	// TODO : only draw above dates
    	Paint p = new Paint();
    	Bitmap tmp = getLocalBitmap(App.getContext(), R.drawable.linechart_cursor);
    	Bitmap cursor = getResizedBitmap(tmp, getHeight() - 2*getPaddingTop() - 2*getPaddingBottom() , dot_scale);
    	tmp.recycle();
    	canvas.drawBitmap(cursor, getXPos2(cursorLinePos)-cur_X_offset, 0, p);
    	
    }

    private float getYPos(float value) {
      return range*(-1*value+4) + offsetY;
    }

    
    private float getXPos2(float value) {
        float width = getWidth() - getPaddingLeft() - getPaddingRight();
        return unit_scale*value+getPaddingLeft()+offsetX;
    }
    
    
    public Bitmap getLocalBitmap(Context con, int resourceId){
        InputStream inputStream = con.getResources().openRawResource(resourceId);
        return BitmapFactory.decodeStream(inputStream, null, getBitmapOptions(1));
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
      
    public int getCursorPos2(float x) {
    	/*
    	float xWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / numOfDays;
    	int temp = (int) Math.round(x/xWidth); 
    	return temp;*/

    	int posX = (int) ((x - offsetX - getPaddingLeft())/unit_scale);
        if( posX < 0 )
            posX = 0;
        else if( posX >= numOfDays )
            posX = numOfDays - 1;
    	return posX;
    }
    
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
    	
    	 @Override
         public boolean onScroll(MotionEvent e1, MotionEvent e2,
                 float distanceX, float distanceY) {
    		 
    		 Log.i(TAG, "X: " + distanceX + " Y: "+ distanceY);
    		 ClickLog.Log(ClickLogId.DAYBOOK_CHART_SCROLL);
    		 
             return true;
         }

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
            
            ClickLog.Log(ClickLogId.DAYBOOK_CHART_TAP);
            
            int tempCursorLinePos = getCursorPos2(x);

            // Set the cursor line position
            int offset, self_type, nearbyIdx, nearbySelfType, nearbyOtherType;
            boolean isFindANeighbor = false;

            switch(checkLineChartType()){
            case 0:
                for(int i = 0; i < 11; i++) {
                    offset = (int)(i+1)/2;

                    if( i % 2 == 0 ){
                        if( tempCursorLinePos + offset < numOfDays - 1 )
                            nearbyIdx = tempCursorLinePos + offset;
                        else
                            nearbyIdx = numOfDays - 1;
                    }
                    else{
                        if( tempCursorLinePos - offset > 0 )
                            nearbyIdx = tempCursorLinePos - offset;
                        else
                            nearbyIdx = 0;
                    }
                    
                    if(nearbyIdx < 0){
                    	nearbyIdx = 0;
                    }
                    nearbySelfType = dataset[nearbyIdx].self_type;
                    
                    if( (nearbySelfType > 0) && drawTheDotOrNot(nearbySelfType) ){
                        tempCursorLinePos = nearbyIdx;
                        isFindANeighbor = true;
                        break;
                    }
                }
                if(isFindANeighbor == true) {
                    cursorLinePos = tempCursorLinePos;
                }
                break;
            case 1:
                for(int i = 0; i < 11; i++) {
                    offset = (int)(i+1)/2;

                    if( i % 2 == 0 ){
                        if( tempCursorLinePos + offset < numOfDays - 1 )
                            nearbyIdx = tempCursorLinePos + offset;
                        else
                            nearbyIdx = numOfDays - 1;
                    }
                    else{
                        if( tempCursorLinePos - offset > 0 )
                            nearbyIdx = tempCursorLinePos - offset;
                        else
                            nearbyIdx = 0;
                    }
                    
                    if(nearbyIdx < 0){
                    	nearbyIdx = 0;
                    }
                    nearbyOtherType = dataset[nearbyIdx].other_type;
                    if( (nearbyOtherType > 0) && drawTheDotOrNot(nearbyOtherType) ){
                        tempCursorLinePos = nearbyIdx;
                        isFindANeighbor = true;
                        break;
                    }
                }
                if(isFindANeighbor == true) {
                    cursorLinePos = tempCursorLinePos;
                }
                break;
            case 2:
                for(int i = 0; i < 11; i++) {
                    offset = (int)(i+1)/2;

                    if( i % 2 == 0 ){
                        if( tempCursorLinePos + offset < numOfDays - 1 )
                            nearbyIdx = tempCursorLinePos + offset;
                        else
                            nearbyIdx = numOfDays - 1;
                    }
                    else{
                        if( tempCursorLinePos - offset > 0 )
                            nearbyIdx = tempCursorLinePos - offset;
                        else
                            nearbyIdx = 0;
                    }
                    if(nearbyIdx < 0){
                    	nearbyIdx = 0;
                    }
                    nearbySelfType = dataset[nearbyIdx].self_type;
                    nearbyOtherType = dataset[nearbyIdx].other_type;
                    if( ( (dataset[nearbyIdx].self_type > 0) && (drawTheDotOrNot(nearbySelfType)) )
                        || ( (dataset[nearbyIdx].other_type > 0) && (drawTheDotOrNot(nearbyOtherType)) ) ){
                        tempCursorLinePos = nearbyIdx;
                        isFindANeighbor = true;
                        break;
                    }
                }
                if(isFindANeighbor == true) {
                    cursorLinePos = tempCursorLinePos;
                }
                break;
            }

            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = dataset[cursorLinePos].getMonth();
            int day = dataset[cursorLinePos].getDay();
            
            DaybookFragment.scrolltoItem2(year, month, day);
            
            invalidate();
            return true;
        }
    }
    

}

