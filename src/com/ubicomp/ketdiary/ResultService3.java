package com.ubicomp.ketdiary;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.ubicomp.ketdiary.BluetoothLE.BluetoothLE3;
import com.ubicomp.ketdiary.BluetoothLE.BluetoothListener;
import com.ubicomp.ketdiary.color.ColorDetectListener;
import com.ubicomp.ketdiary.color.ImageDetection;
import com.ubicomp.ketdiary.color.TestStripDetection4;
import com.ubicomp.ketdiary.data.structure.TestDetail;
import com.ubicomp.ketdiary.db.DatabaseControl;
import com.ubicomp.ketdiary.db.TestDataParser2;
import com.ubicomp.ketdiary.fragment.TestFragment;
import com.ubicomp.ketdiary.system.PreferenceControl;

public class ResultService3 extends Service implements BluetoothListener, ColorDetectListener{
	
	private ResultService3 myservice = this;
	private Context context = App.getContext();
	
	public  static  final  String TAG =  "MyService" ;  
	private Handler mhandler = new Handler();
	private Handler blehandler = new Handler();
	
	private long startTime;
	private static long timeout = MainActivity.getMainActivity().WAIT_RESULT_TIME; //1*60*1000;//10*60*1000;
	private Notification notification;
	private PendingIntent pendingIntent;
	
	private BluetoothLE3 ble = null;
	private boolean stateSuccess = false;
	private boolean isConnect = false;
	private int result= -1;
	private DatabaseControl db;
	private int picNum = 0;
	private TestStripDetection4 testStripDetection;
	private OpenSensorMsgTimer openSensorMsgTimer;
	
	private ProgressDialog dialog = null;
	private boolean first = true;
	private boolean second = true;
	public TestDataParser2 TDP;
	private ImageDetection imageDetection = null;
	
	private int failedState = 0;
	private float connectionFailRate = 0;
	private String failedReason = "";
	private int colorReading = 0;
	private static final int CONNECT_FAIL = 8;
	private static final int PIC_STATE  = 9;
	private static final int PIC_SEND_FAIL = 10;
	
	
    @Override  
    public  void  onCreate() {  
        super .onCreate();  
        notification =  new  Notification(R.drawable.app_icon, "有通知到來" , System.currentTimeMillis());  
        Intent notificationIntent =  new  Intent( this , MainActivity.class );  
        pendingIntent = PendingIntent.getActivity( this ,  0 ,  
                notificationIntent,  0 );  
        notification.setLatestEventInfo( this ,  "這是通知的標題" ,  "這是通知的內容" , pendingIntent);  
        startForeground( 1 , notification);  
        
        testStripDetection = new TestStripDetection4(myservice);
        db = new DatabaseControl();
        openSensorMsgTimer = new OpenSensorMsgTimer();
        imageDetection = new ImageDetection(this);
       
    }  
    
	private Runnable updateTimer = new Runnable() {
		public void run() {

			long spentTime = System.currentTimeMillis() - PreferenceControl.getLatestTestCompleteTime();
			
			spentTime = timeout - spentTime;
			
			long minius = (spentTime/1000)/60;
			long seconds = (spentTime/1000) % 60;
			
			notification.setLatestEventInfo( myservice ,  "測試結果倒數" ,  minius+":"+seconds , pendingIntent);  
	        startForeground( 1 , notification); 
	        
	        if(first){
				first = false;
				if(!isConnect){
	        		ble.bleConnect();
	        	}				
				if(openSensorMsgTimer!=null)
	        		openSensorMsgTimer.start();
			}
	        if(!stateSuccess && isConnect && picNum == 0)
				ble.bleWriteState((byte)0x03);	
	
	        
			mhandler.postDelayed(this, 1000);
						
			if(spentTime < 2*60*1000){   //剩兩分鐘的時候才開始拍照傳照片
				
				if(second){
					second = false;
					if(!isConnect){
		        		ble.bleConnect();
		        	}				
					if(openSensorMsgTimer!=null)
		        		openSensorMsgTimer.start();
				}
				
				
				if(!stateSuccess && isConnect)
		        	ble.bleWriteState((byte)0x06);
			}
			
			if(spentTime < 0){ //想一下時間到要做什麼    跳出不一樣的notification讓他點or直接跳出activity
				mhandler.removeCallbacks(updateTimer);
				//goResult();
				stopSelf();
			}

		}
    };
    
    private void goResult(){
    	notification.defaults = Notification.DEFAULT_ALL;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo( myservice , "檢測倒數結束", "前往測試結果", pendingIntent);
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	
		boolean inApp = PreferenceControl.getInApp();
	
		Log.d("InApp",String.valueOf(inApp));
		if(!inApp)
			notificationManager.notify(0, notification);
    	
		
		if(ble!=null){
			isConnect = false;
			ble.bleWriteState((byte)0x05);
			ble.bleDisconnect();
			ble = null;
		}
		
		//testStripDetection.sendEmptyMessage(0);
		
		
		//setTestDetail();
		
//		Random rand = new Random();
//		result = rand.nextInt(2); //Random Gen Result
//		//test_msg.setText(test_guide_msg[idx]);
//		PreferenceControl.setTestResult(result);   	
//		PreferenceControl.setTestSuccess();
		
		stopSelf();
    }
    
    private void setTestDetail(){
    	String cassetteId = TestFragment.testDetail.cassetteId;
		long ts = PreferenceControl.getUpdateDetectionTimestamp();
		int firstVoltage = TestFragment.testDetail.firstVoltage;
		int secondVoltage= TestFragment.testDetail.secondVoltage;
		int devicePower = TestFragment.testDetail.devicePower;
		
		Toast.makeText(myservice, "Check: "+ colorReading, Toast.LENGTH_SHORT).show();
		Log.i(TAG, "Check: "+ colorReading);
				
		TestDetail testDetail = new TestDetail(cassetteId, ts, failedState, firstVoltage,
				secondVoltage, devicePower, colorReading,
                connectionFailRate, failedReason);
		
		db.insertTestDetail(testDetail);
    }
    
    
    private void stop(){
    	
    	
    	mhandler.removeCallbacks(updateTimer);
    	stopSelf();
    }


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override  
    public void onDestroy() {  
        super.onDestroy();  
        if(openSensorMsgTimer!=null){
			openSensorMsgTimer.cancel();
			openSensorMsgTimer=null;
		}
        if(ble!= null){
        	ble.bleDisconnect();
        	ble = null;
        }      	
    }  
	
	/**Use startService to call ResultService*/
	public int onStartCommand(Intent intent, int flags, int startId) {
				
		//testDialog();
		//testStripDetection.sendEmptyMessage(0);
	        	Log.d(TAG,  "onStartCommand() executed" ); 
	        	if(ble == null ) {
	    			ble = new BluetoothLE3( myservice , PreferenceControl.getDeviceId());	
	            }
	        	long timestamp = PreferenceControl.getUpdateDetectionTimestamp();
	            Log.d(TAG,"1:"+timestamp);
	            
	            PreferenceControl.setTestFail();
	            stateSuccess = false;
	            startTime = System.currentTimeMillis();
	          
	            mhandler.postDelayed(updateTimer, 1000);
  
	    return  super .onStartCommand(intent, flags, startId);  
	}
	
	private class OpenSensorMsgTimer extends CountDownTimer { //20秒鐘之內沒連到會直接fail

		public OpenSensorMsgTimer() {
			super(20000, 2000);
		}

		@Override
		public void onFinish() {
			
			failedState = CONNECT_FAIL;
			failedReason = "檢測器未開啟";
			Toast.makeText(myservice, "請開啟檢測器", Toast.LENGTH_SHORT).show();
			PreferenceControl.setTestFail();
			MainActivity.getMainActivity().setResultFail();
			//stop();	
		}

		@Override
		public void onTick(long millisUntilFinished) {
			if(!isConnect){
        		ble.bleConnect();
			}
			
			Toast.makeText(myservice, "請開啟檢測器", Toast.LENGTH_SHORT).show();
		}
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
        Toast.makeText(this, "BLE connected", Toast.LENGTH_SHORT).show();
        if(openSensorMsgTimer!=null){
			openSensorMsgTimer.cancel();
			openSensorMsgTimer=null;
		}
        //PreferenceControl.setTestSuccess();
    }

    @Override
    public void bleDisconnected() {
    	isConnect = false;
    	stateSuccess = false;
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
        Toast.makeText(this, "BLE writefstate fail", Toast.LENGTH_SHORT).show();
        stateSuccess = false;
    }

    @Override
    public void bleNoPlug() {
        Log.i(TAG, "No test plug");
       //Toast.makeText(this, "No test plug", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void blePlugInserted(byte[] plugId) {
        Log.i(TAG, "Test plug is inserted");
    
    }


    @Override
    public void bleColorReadings(byte[] colorReadings) {
    }
    


	@Override
	public void bleElectrodeAdcReading(byte header, byte[] adcReading) {
		Log.i(TAG, "State: "+String.valueOf(header));
		
	}

	@Override
	public void bleTakePictureSuccess() {
		picNum++;
		Log.i(TAG, "Picture: " + picNum + " Save");
		Toast.makeText(this, "Picture: " + picNum + " Save", Toast.LENGTH_SHORT).show();
		
		
		blehandler.postDelayed(writeBle, 2000);
	
		if(picNum == 1){
			goResult();
			Log.i(TAG, "GoResult!");
		}
		
	}
	
	private Runnable writeBle = new Runnable() {
		public void run() {
			blehandler.removeCallbacks(writeBle);
		if(ble!=null)
				ble.bleWriteState((byte)0x06);
			//blehandler.postDelayed(this, 1000);

		}
    };

	@Override
	public void updateProcessRate(float rate) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, String.valueOf(rate).concat(" %"), Toast.LENGTH_SHORT).show();
		Log.i(TAG, String.valueOf(rate).concat(" %"));
	}

	@Override
	public void clearProcesssRate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayCurrentId(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bleTakePictureSuccess(Bitmap bitmap) {
		picNum++;
		Log.i(TAG, "Picture: " + picNum + " Save");
		Toast.makeText(this, "Picture: " + picNum + " Save", Toast.LENGTH_SHORT).show();
		
		if(picNum == 1){
			imageDetection.roiDetectionOnWhite(bitmap);
			ble.bleWriteState((byte)0x05);
			ble.bleDisconnect();
			return;
		}
		
		blehandler.postDelayed(writeBle, 2000);
		//blehandler.postDelayed(writeBle, 2000);
	
		if(picNum == 3){
			colorReading = imageDetection.testStripDetection(bitmap);
			setTestDetail();
			goResult();
			Log.i(TAG, "GoResult!");
		}
		
	}

	@Override
	public void bleTakePictureFail(float dropRate) {
		
		failedState = PIC_SEND_FAIL;
		connectionFailRate = dropRate;
		
		Log.i(TAG, "DropRate: " + dropRate);
		//Toast.makeText(this, "Picture Fail, DropRate: " + dropRate, Toast.LENGTH_SHORT).show();
		//blehandler.postDelayed(writeBle, 500);
		PreferenceControl.setTestFail();
		MainActivity.getMainActivity().setResultFail();
	}

	@Override
	public void imgDetect(Bitmap bitmap) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void colorDetectSuccess(int check) {
		colorReading = check;
		setTestDetail();
	}
}