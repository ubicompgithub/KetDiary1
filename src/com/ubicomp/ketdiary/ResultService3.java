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
import com.ubicomp.ketdiary.dialog.NoteDialog3;
import com.ubicomp.ketdiary.fragment.TestFragment;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.CustomToastSmall;

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
	private final ConnectSensorTimer connectSensorTimer = new ConnectSensorTimer();
	
	private ProgressDialog dialog = null;
	private boolean first = true;
	private boolean second = true;
	public TestDataParser2 TDP;
	private ImageDetection imageDetection = null;
	
	private int failedState = 0;
	private float connectionFailRate = 0;
	private String failedReason = "";
	private int colorReading = 0;
	private static final int REGULAR_CONNECT_FAIL = 8;
	private static final int CONNECT_FAIL  = 9;
	private static final int PIC_SEND_FAIL = 10;
	
	private boolean testSuccess = false;
	private int regular_connect = 0;
	private boolean connect = false;
	private boolean debug = PreferenceControl.isDebugMode();
	
	public static long spentTime = PreferenceControl.getAfterCountDown()*1000;
	
	
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
        //connectSensorTimer =  ;
        imageDetection = new ImageDetection(this);
        Log.d(TAG,  "MyService onCreate" );
    }  
    
	private Runnable updateTimer = new Runnable() {
		public void run() {

			long passTime = System.currentTimeMillis() - PreferenceControl.getLatestTestCompleteTime();
			
			spentTime = timeout - passTime;
			
			long minius = (spentTime/1000)/60;
			long seconds = (spentTime/1000) % 60;
			
			notification.setLatestEventInfo( myservice ,  "測試結果倒數" ,  minius+":"+seconds , pendingIntent);  
	        startForeground( 1 , notification); 
	        mhandler.postDelayed(this, 1000);
	        
	        if(spentTime > 2*60*1000){
	        	
	        	if(seconds == 0){
	        		if(connectSensorTimer != null)
	        			//connectSensorTimer.cancel();
	        			Log.i(TAG, "second = 0");
	        			connect = false;
	        			connectSensorTimer.start();
	        	}
		        if(first){
						
						if(!isConnect){
			        		ble.bleConnect();
			        		first = false;
			        	}				
						if(openSensorMsgTimer!=null)
			        		openSensorMsgTimer.start();
					}
			        if(!stateSuccess && isConnect && picNum == 0)
						ble.bleWriteState((byte)0x03);	
		        }
		        			
				if(spentTime < 2*60*1000){   //剩兩分鐘的時候才開始拍照傳照片
					
					if(second){					
						if(!isConnect){
			        		ble.bleConnect();
			        		second = false;
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
    
    private void goResultSuccess(){   //檢測成功的話
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
		
		failedReason = "檢測成功";
		setTestDetail();
		PreferenceControl.setCheckBars(true);
		PreferenceControl.setTestSuccess();
		testSuccess = true;
		//MainActivity.getMainActivity().setResultSuccess();
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
		
		//Toast.makeText(myservice, "Check: "+ colorReading, Toast.LENGTH_SHORT).show();
		Log.i(TAG, "Check: "+ colorReading);
				
		TestDetail testDetail = new TestDetail(cassetteId, ts, failedState, firstVoltage,
				secondVoltage, devicePower, colorReading,
                connectionFailRate, failedReason);
		
		db.insertTestDetail(testDetail);
    }
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override  
    public void onDestroy() {  
        super.onDestroy();
        
        spentTime = 0;
        
        if(connectSensorTimer!=null){
        	connectSensorTimer.cancel();
        	//connectSensorTimer=null;
		}
        
        if(openSensorMsgTimer!=null){
			openSensorMsgTimer.cancel();
			openSensorMsgTimer=null;
		}
        if(ble!= null){
        	ble.bleDisconnect();
        	ble = null;
        }
        
        PreferenceControl.setResultServiceRun(false);
//        if(!testSuccess){
//        	boolean inApp = PreferenceControl.getInApp();
//			if(inApp)
//        	MainActivity.getMainActivity().setResultFail();
//        }
    }  
	
	/**Use startService to call ResultService*/
	public int onStartCommand(Intent intent, int flags, int startId) {
		
				spentTime = PreferenceControl.getAfterCountDown()*1000;
		
				PreferenceControl.setResultServiceRun(true);
	        	Log.d(TAG,  "onStartCommand() executed" ); 
	        	if(ble == null ) {
	    			ble = new BluetoothLE3( myservice , PreferenceControl.getDeviceId());	
	            }
	        	long timestamp = PreferenceControl.getUpdateDetectionTimestamp();
	            Log.d(TAG,"1:"+timestamp);
	            
	            PreferenceControl.setTestFail();
	            stateSuccess = false;
	            startTime = System.currentTimeMillis();
	          
	            mhandler.postDelayed(updateTimer, 1);
  
	    return  super .onStartCommand(intent, flags, startId);  
	}
	
	private class OpenSensorMsgTimer extends CountDownTimer { //20秒鐘之內沒連到會直接fail

		public OpenSensorMsgTimer() {
			super(20000, 2000);
		}

		@Override
		public void onFinish() {
			
			failedState = CONNECT_FAIL;
			failedReason = "檢測器未開啟 - Take picture";
			Toast.makeText(myservice, "請開啟檢測器", Toast.LENGTH_SHORT).show();
			
			setTestFail();
//			PreferenceControl.setTestFail();
//			setTestDetail();
//			boolean inApp = PreferenceControl.getInApp();
//			if(inApp)
//				MainActivity.getMainActivity().setResultFail();
//			myservice.stopSelf();
			//stop();	
		}

		@Override
		public void onTick(long millisUntilFinished) {
			Log.d(TAG, "Timer: "+ millisUntilFinished);
			if(!isConnect){
        		ble.bleConnect();
			}
			
			//Toast.makeText(myservice, "請開啟檢測器", Toast.LENGTH_SHORT).show();
		}
	}
	
	private class ConnectSensorTimer extends CountDownTimer { //Regular connect
		
		//private boolean connect = false;
		
		public ConnectSensorTimer() {
			super(20000, 2000);
		}

		@Override
		public void onFinish() {		
			failedState = REGULAR_CONNECT_FAIL;
			failedReason = "檢測器未開啟- Regular connect";
			Toast.makeText(myservice, "請開啟檢測器", Toast.LENGTH_SHORT).show();
			setTestFail();
//			PreferenceControl.setTestFail();
//			setTestDetail();
//			boolean inApp = PreferenceControl.getInApp();
//			if(inApp)
//				MainActivity.getMainActivity().setResultFail();
//			myservice.stopSelf();
			//stop();	
		}

		@Override
		public void onTick(long millisUntilFinished) {  //連接在斷掉
			Log.d(TAG, "Timer: "+ millisUntilFinished);
			if(!isConnect){
				if(ble!=null){
					ble.bleConnect();
					connect = true;
					Log.i(TAG, "Regular Connect: " + regular_connect);
				}
			}
			if(isConnect && connect){
				regular_connect++;
				if(ble!=null)
					ble.bleDisconnect();				
			}
			//Toast.makeText(myservice, "請開啟檢測器", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void setTestFail(){
		PreferenceControl.setTestFail();
		PreferenceControl.setAfterTestState(NoteDialog3.STATE_TEST);
		spentTime = 0;
		mhandler.removeCallbacks(updateTimer);
		if(connectSensorTimer!=null){
        	connectSensorTimer.cancel();
        	//connectSensorTimer=null;
		}        
        if(openSensorMsgTimer!=null){
			openSensorMsgTimer.cancel();
			openSensorMsgTimer=null;
        }
        if(ble!= null){
        	ble.bleDisconnect();
        	ble = null;
        }
        
		
		setTestDetail();
		boolean inApp = PreferenceControl.getInApp();
		if(inApp)
			MainActivity.getMainActivity().setResultFail();
		
		notification.defaults = Notification.DEFAULT_ALL;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo( myservice , "檢測失敗", "回到測試頁重測", pendingIntent);
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	
		Log.d("InApp",String.valueOf(inApp));
		if(!inApp)
			notificationManager.notify(0, notification);
		
		myservice.stopSelf();	
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(ble!=null)
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
    	if(debug){
    		//Toast.makeText(this, "請開啟檢測器", Toast.LENGTH_SHORT).show();
    		CustomToastSmall.generateToast("請開啟檢測器");
    	}

    }

    @Override
    public void bleConnected() {
    	isConnect = true;
        Log.i(TAG, "BLE connected");
        if(debug)
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
        if(connectSensorTimer != null)
			connectSensorTimer.cancel();
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
//		picNum++;
//		Log.i(TAG, "Picture: " + picNum + " Save");
//		Toast.makeText(this, "Picture: " + picNum + " Save", Toast.LENGTH_SHORT).show();
//		
//		
//		blehandler.postDelayed(writeBle, 2000);
//	
//		if(picNum == 1){
//			goResultSuccess();
//			Log.i(TAG, "GoResult!");
//		}
		
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
			if(ble!=null){
				ble.bleWriteState((byte)0x04);
				ble.bleDisconnect();
			}
			return;
		}
		
		blehandler.postDelayed(writeBle, 2000);
		//blehandler.postDelayed(writeBle, 2000);
	
		if(picNum == 3){
			colorReading = imageDetection.testStripDetection(bitmap);
			
			goResultSuccess();
			
			Log.i(TAG, "GoResult!");
		}
		
	}

	@Override
	public void bleTakePictureFail(float dropRate) {
		
		failedState = PIC_SEND_FAIL;
		connectionFailRate = dropRate;
		failedReason = "照片傳送失敗";
		Log.i(TAG, "DropRate: " + dropRate);
		
		setTestFail();
		
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
