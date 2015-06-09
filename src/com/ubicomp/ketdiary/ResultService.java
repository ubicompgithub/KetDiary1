package com.ubicomp.ketdiary;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.ubicomp.ketdiary.system.PreferenceControl;

public class ResultService extends Service{
	
	private ResultService myservice = this;
	
	public  static  final  String TAG =  "MyService" ;  
	private Handler mhandler = new Handler();  
	private long startTime;
	private long timeout = 1*60*1000;//10*60*1000;
	private Notification notification;
	private PendingIntent pendingIntent;
	
    @Override  
    public  void  onCreate() {  
        super .onCreate();  
        notification =  new  Notification(R.drawable.ntu_logo, "有通知到來" , System.currentTimeMillis());  
        Intent notificationIntent =  new  Intent( this , MainActivity.class );  
        pendingIntent = PendingIntent.getActivity( this ,  0 ,  
                notificationIntent,  0 );  
        notification.setLatestEventInfo( this ,  "這是通知的標題" ,  "這是通知的內容" , pendingIntent);  
        startForeground( 1 , notification);  
        
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
				
				
				
				stopSelf();
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
	            // 開始執行後台任務  
	        }  
	    }).start();  
	    return  super .onStartCommand(intent, flags, startId);  
		

	}
}
