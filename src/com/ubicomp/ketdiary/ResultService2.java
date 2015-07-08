package com.ubicomp.ketdiary;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.ubicomp.ketdiary.BluetoothLE.BluetoothLE2;
import com.ubicomp.ketdiary.BluetoothLE.BluetoothListener;
import com.ubicomp.ketdiary.color.ColorDetect2;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.db.DatabaseControl;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.CustomToast;

public class ResultService2 extends Service implements BluetoothListener {
	
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
        
        /*
        if(ble == null) {
			ble = new BluetoothLE2( myservice , PreferenceControl.getDeviceId());
			ble.bleConnect();
			
        }*/
        
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
	        
	        /*
	        if(!stateSuccess)
	        	ble.bleWriteState((byte)3);*/
			
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		ble.onBleActivityResult(requestCode, resultCode, data);
	}

    @Override
    public void bleNotSupported() {
    	  //Toast.makeText(this, "BLE not support", Toast.LENGTH_SHORT).show();
//        this.finish();
    }

    @Override
    public void bleConnectionTimeout() {
    	Log.i(TAG, "connect timeout");
        //Toast.makeText(this, "BLE connection timeout", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void bleConnected() {
    	isConnect = true;
        Log.i(TAG, "BLE connected");
        //Toast.makeText(this, "BLE connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void bleDisconnected() {
    	isConnect = false;
        Log.i(TAG, "BLE disconnected");
        //Toast.makeText(this, "BLE disconnected", Toast.LENGTH_SHORT).show();
        //setState(new FailState("連接中斷"));
        
    }

    @Override
    public void bleWriteStateSuccess() {
        Log.i(TAG, "BLE ACTION_DATA_WRITE_SUCCESS");
        //Toast.makeText(this, "BLE write state success", Toast.LENGTH_SHORT).show();
        stateSuccess = true;
    }

    @Override
    public void bleWriteStateFail() {
        Log.i(TAG, "BLE ACTION_DATA_WRITE_FAIL");
        //Toast.makeText(this, "BLE writefstate fail", Toast.LENGTH_SHORT).show();
        stateSuccess = false;
    }

    @Override
    public void bleNoPlug() {
        Log.i(TAG, "No test plug");
       //Toast.makeText(this, "No test plug", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void blePlugInserted(byte[] plugId) {
        //Log.i(TAG, "Test plug is inserted");
    
    }


    @Override
    public void bleColorReadings(byte[] colorReadings) {
    	String feature, feature2;
    	String str1 ="";
    	String str2 ="";
    	int[] color = new int[4];
    	for(int i=0; i<8; i+=2){
    		//color[i/2] = colorReadings[i]+colorReadings[i+1]*256;
    		
    		color[i/2] = ((colorReadings[i+1] & 0xFF) << 8) | (colorReadings[i] & 0xFF);
    		str1 = str1+ " " + String.valueOf(color[i/2]);
    	}
    	//ColorDetect2.colorDetect(color);
    	//feature = ColorDetect2.colorDetect2(color);
    	result = ColorDetect2.colorDetect(color);
    	//writeToColorRawFile(str1+"\n");
    	//writeToColorRawFile(feature+"\n");
    	//writeToColorRawFile(str1+"\n");
    	
    	int[] color2 = new int[4];
    	for(int i=8; i<16; i+=2){
    		//color2[(i-8)/2] = colorReadings[i]+colorReadings[i+1]*256;
    		color2[(i-8)/2] = ((colorReadings[i+1] & 0xFF) << 8) | (colorReadings[i] & 0xFF);
    		str2 = str2+ " " + String.valueOf(color2[(i-8)/2]);
    	}
    	
    	//feature2 = ColorDetect2.colorDetect2(color2);
    	//writeToVoltageFile(feature2+"\n");	
    	
    	//showDebug(">First:"+str1+" Second:"+str2);
    	Log.i(TAG, "First: "+str1);
    	Log.i(TAG, "Second: "+str2);
    	
        //Log.i(TAG, "Color sensor readings");
    }
    


	@Override
	public void bleElectrodeAdcReading(byte header, byte[] adcReading) {
		Log.i(TAG, "State: "+String.valueOf(header));
		
	}

	@Override
	public void bleTakePictureSuccess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateProcessRate(float rate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearProcesssRate() {
		// TODO Auto-generated method stub
		
	}
}
