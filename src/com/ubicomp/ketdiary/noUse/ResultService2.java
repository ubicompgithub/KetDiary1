package com.ubicomp.ketdiary.noUse;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.ubicomp.ketdiary.MainActivity;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.BluetoothLE.BluetoothLE2;
import com.ubicomp.ketdiary.BluetoothLE.BluetoothListener;
import com.ubicomp.ketdiary.R.drawable;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.db.DatabaseControl;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.CustomToast;

public class ResultService2 extends Service {
	
	private ResultService2 myservice = this;
	
	public  static  final  String TAG =  "MyService" ;  
	private Handler mhandler = new Handler();  
	private long startTime;
	private static long timeout = MainActivity.getMainActivity().WAIT_RESULT_TIME; //1*60*1000;//10*60*1000;
	private Notification notification;
	private PendingIntent pendingIntent;
	
	private BluetoothLE2 ble = null;
	private boolean stateSuccess = false;
	private boolean isConnect = false;
	private int result= -1;
	private DatabaseControl db;
	private int picNum = 0;
	
    @Override  
    public  void  onCreate() {  
        super .onCreate();  
        notification =  new  Notification(R.drawable.app_icon, "有通知到來" , System.currentTimeMillis());  
        Intent notificationIntent =  new  Intent( this , MainActivity.class );  
        pendingIntent = PendingIntent.getActivity( this ,  0 ,  
                notificationIntent,  0 );  
        notification.setLatestEventInfo( this ,  "這是通知的標題" ,  "這是通知的內容" , pendingIntent);  
        startForeground( 1 , notification);  
        
        db = new DatabaseControl();
        
        
        if(ble == null) {
			//ble = new BluetoothLE2( myservice , PreferenceControl.getDeviceId());
			ble.bleConnect();
			
        }
        
        long timestamp = PreferenceControl.getUpdateDetectionTimestamp();
        Log.d(TAG,"1:"+timestamp);
        
        Log.d(TAG,  "onCreate() executed" );  
        startTime = System.currentTimeMillis();
         
        mhandler.postDelayed(updateTimer, 1000);
    }  
    
	private Runnable updateTimer = new Runnable() {
		public void run() {

			long spentTime = System.currentTimeMillis() - PreferenceControl.getLatestTestCompleteTime();
			
			spentTime = timeout - spentTime;
			
			long minius = (spentTime/1000)/60;
			long seconds = (spentTime/1000) % 60;
			
			notification.setLatestEventInfo( myservice ,  "測試結果倒數" ,  minius+":"+seconds , pendingIntent);  
	        startForeground( 1 , notification); 
	        
	        
	        if(!stateSuccess)
	        	ble.bleWriteState((byte)0x06);
			
			mhandler.postDelayed(this, 1000);
			
			if(spentTime < 0){ //想一下時間到要做什麼    跳出不一樣的notification讓他點or直接跳出activity
				mhandler.removeCallbacks(updateTimer);
				
				notification.defaults = Notification.DEFAULT_ALL;
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notification.setLatestEventInfo( myservice , "檢測倒數結束", "前往測試結果", pendingIntent);
				NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				
				boolean inApp = PreferenceControl.getInApp();
				
				Log.d("InApp",String.valueOf(inApp));
				if(!inApp)
					notificationManager.notify(0, notification);
				
				
				/*
				if(ble!=null){
					isConnect = false;
					ble.bleDisconnect();
					ble = null;
				}*/
				
				
				Random rand = new Random();
				result = rand.nextInt(2); //Random Gen Result
				//test_msg.setText(test_guide_msg[idx]);
				PreferenceControl.setTestResult(result);
				
				/*
				//Toast.makeText(myservice, "Result:"+result, Toast.LENGTH_SHORT).show();
				TestResult testResult = new TestResult(result, timestamp, "tmp_id", 
						0, 1, 0, 0); //TODO: check IsFilled
				
				int addScore = db.insertTestResult(testResult, false);
				
				Log.d(TAG,""+timestamp+" "+addScore);
				PreferenceControl.setTestAddScore(addScore);
				/*
				if (addScore == 0 && testResult.getResult()==1) // TestFail & get no credit
					CustomToast.generateToast(R.string.after_test_fail, -1);
				else if (testResult.getResult()==1)
					CustomToast.generateToast(R.string.after_test_fail, addScore);
				else
					CustomToast.generateToast(R.string.after_test_pass, addScore);
				*/
				
				
				//stopSelf();
			}
			/*
			final TextView time = (TextView) findViewById(R.id.timer);
			Long spentTime = System.currentTimeMillis() - startTime;
		            //計算目前已過分鐘數
		    
		            //計算目前已過秒數
		    
		    time.setText(minius+":"+seconds);
		    handler.postDelayed(this, 1000);*/
		}
    };


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override  
    public void onDestroy() {  
        super.onDestroy();  
        
    }  
	
	/**Use startService to call ResultService*/
	public int onStartCommand(Intent intent, int flags, int startId) {
		new  Thread( new  Runnable() {  
	        @Override  
	        public  void  run() {  
	        	Log.d(TAG,  "onStartCommand() executed" ); 
	        	/*
	        	if(ble == null) {
	    			ble = new BluetoothLE2( myservice , PreferenceControl.getDeviceId());
	    			ble.bleConnect();
	    			ble.bleWriteState((byte)4);
	    		
	            }*/ 
	        }  
	    }).start();  
	    return  super .onStartCommand(intent, flags, startId);  
		

	}

}