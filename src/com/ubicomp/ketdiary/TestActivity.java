package com.ubicomp.ketdiary;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ubicomp.ketdiary.BluetoothLE.BluetoothLE;
import com.ubicomp.ketdiary.BluetoothLE.BluetoothListener;
import com.ubicomp.ketdiary.BluetoothLE.MainActivity;
import com.ubicomp.ketdiary.camera.CameraCaller;
import com.ubicomp.ketdiary.camera.CameraInitHandler;
import com.ubicomp.ketdiary.camera.CameraPreview;
import com.ubicomp.ketdiary.camera.CameraRecorder;
import com.ubicomp.ketdiary.camera.CameraRunHandler;
import com.ubicomp.ketdiary.camera.ImageFileHandler;
import com.ubicomp.ketdiary.camera.Tester;
import com.ubicomp.ketdiary.dialog.NoteDialog;
import com.ubicomp.ketdiary.file.MainStorage;
import com.ubicomp.ketdiary.system.PreferenceControl;



@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class TestActivity extends Activity implements BluetoothListener, CameraCaller{
	
	
	private static final String TAG = "BluetoothLE";
	private static final String TAG2 = "debug";
	
	private TextView label_btn, label_subtitle, label_title;
	private ImageView img_bg, img_ac, img_btn;
	private long timestamp = 0;

	private CountDownTimer testCountDownTimer = null;
	private CountDownTimer salivaCountDownTimer = null;
	
	/** self activity*/
	Activity that;
	
	/** Camare variables */
	private Camera mCamera = null;
	private CameraPreview mCamPreview;
	private FrameLayout cameraLayout;
	
	// Camera
	private CameraInitHandler cameraInitHandler;
	private CameraRecorder cameraRecorder;
	private CameraRunHandler cameraRunHandler;
	//TODO: private ImageView camera_mask;
	
	
	//File 
	private File mainDirectory;
	private ImageFileHandler imgFileHandler;
	
	/** Sound playing variables */
	private SoundPool soundPool;
	
	private static final int[] ELECTRODE_RESOURCE = { 0, R.drawable.test_progress_1,
		R.drawable.test_progress_2, R.drawable.test_progress_3,
		R.drawable.test_progress_4, R.drawable.test_progress_5,
		R.drawable.test_progress_5 };
	
	/** Sound id*/
	private int count_down_audio_id;
	private int preview_audio_id;
	
	private TestState CertainState = null;
	private BluetoothLE ble = null;
	
	private boolean first_connect = false;
	private boolean first_voltage = false;
	private boolean second_voltage= false;
	private boolean in_stage1 = false;
	private boolean test_done = false;
	private boolean in_stage2 = false;
	private boolean camera_initial=false;
	private boolean camera_done=false;
	
	private static final int COUNT_DOWN_SECOND = 5;
	private static final int WAIT_SALIVA_SECOND = 7;
	private static final int FIRST_VOLTAGE_THRESHOLD = 25;
	private static final int SECOND_VOLTAGE_THRESHOLD= 15;
	
	
	/**
	 * Define state's function
	 * @author mudream
	 *
	 */
	private class TestState{
		public void onStart(){return;}
		public void onExit(){return;}
		public void onClick(){return;}
	}
	
	/**
	 * Set the state of certain state machine
	 * @param sts
	 */
	protected void setState(TestState sts){
		if(CertainState != null)
			CertainState.onExit();
		CertainState = sts;
		CertainState.onStart();
	}
	
	private class IdleState extends TestState{
		@Override
		public void onStart(){
			label_btn.setText("開始");
			label_subtitle.setText("請點選開始進行測試");
			label_title.setText("測試尚未開始");
		}
		@Override
		public void onClick(){
			first_connect =false;
			first_voltage =false;
			reset();
			
			setState(new ConnState());
		}
	}
	
	private class FailState extends TestState{
		private String err_msg;
		/**
		 * A Fail state with error msg
		 * @param _err_msg
		 */
		public FailState(String _err_msg){
			err_msg = _err_msg;
		}
		@Override
		public void onStart(){
			label_btn.setText("確認");
			label_subtitle.setText("");
			label_title.setText(err_msg);
			//if(ble != null)
				//ble.bleDisconnect();
		}
		@Override
		public void onClick(){
			stopDueToInit();
			setState(new IdleState());
		}
	}
	
	private class ConnState extends TestState{
		private volatile boolean is_conn = false;
		@Override
		public void onStart(){
			label_btn.setText("...");
			label_subtitle.setText("");
			label_title.setText("準備中....");
			
			
			cameraInitHandler = new CameraInitHandler( (Tester)that, cameraRecorder);
			cameraInitHandler.sendEmptyMessage(0);
			if(ble != null) {
                return;
            }
		    ble = new BluetoothLE(that, "ket_000"); //PreferenceControl.getDeviceId()
		    ble.bleConnect();
		    
		}
	}
	
	private class FiveSecondState extends TestState{
		@Override
		public void onStart(){
			Log.d("Main", "Enter FiveSecond");
			label_btn.setText("5");
			label_subtitle.setText("請蓄積口水");
			
			testCountDownTimer = new TestCountDownTimer(
					COUNT_DOWN_SECOND);
			testCountDownTimer.start();
			
		}
	}
	
	private class Stage1State extends TestState{
		private CountDownTimer test_timer = null;
		private volatile boolean move_to_stage2 = false;
		@Override
		public void onStart(){
			
			
						
			soundPool.play(preview_audio_id, 1.0F, 1.0F, 0, 0, 1.0F);
			Log.d("Main", "Enter Stage1");
			
			
			/** Search for the front facing camera */
			int cameraId = -1;
			int numberOfCameras = Camera.getNumberOfCameras();
			for (int i = 0; i < numberOfCameras; i++) {
				CameraInfo info = new CameraInfo();
				Camera.getCameraInfo(i, info);
				if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
					cameraId = i;
					break;
				}
			}
			
			
			
			mCamPreview = new CameraPreview(that);
			cameraLayout.addView(mCamPreview);
			Log.i("FORTEST", "cameraId: " + cameraId);
			//mCamera = Camera.open(cameraId);
			//mCamPreview.set(that, mCamera);
			
			//cameraRecorder.initialize();
			cameraRecorder.start();
			
			
			
			cameraLayout.setVisibility(View.VISIBLE);
			
			// TODO: camera_mask.bringToFront();
			
			label_btn.setText("");
			label_subtitle.setText("請將臉對準中央，並吐口水");
			label_title.setText("請吐口水");
			
			in_stage1 = true;
			ble.bleWriteState((byte)2);
			/*
			test_timer = new CountDownTimer(60000, 10000){
		        public void onTick(long ms){
		        	if(move_to_stage2) return;
		        	if(bluetoothle.getState() >= BluetoothLE.STATE_1PASS){
		        		move_to_stage2 = true;
		        		setState(new Stage2State());
		        	}
		        }
		        public void onFinish() {
		        	if(move_to_stage2) return;
		        	if(bluetoothle.getState() >= BluetoothLE.STATE_1PASS){
		        		move_to_stage2 = true;
		        		setState(new Stage2State());
		        	}else{
		        		setState(new FailState("口水逾時"));
		        	}
		        }
		    };
		    test_timer.start();*/
		}
		@Override
		public void onExit(){
			//move_to_stage2 = true;
			cameraLayout.setVisibility(4);
			if(mCamera != null){
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}
			//if(test_timer != null)
			//	test_timer.cancel();
		}
	}

	private class Stage2State extends TestState{
		
		@Override
		public void onStart(){
			label_btn.setText("");
			img_bg.setVisibility(0);
			img_ac.setVisibility(0);
			img_btn.setVisibility(4);

			salivaCountDownTimer = new SalivaCountDownTimer();
			salivaCountDownTimer.start();
			
			/*
			new CountDownTimer(5000, 1000){
		        public void onTick(long ms){
		        	ptr++;
		        	img_ac.setImageResource(pids[ptr]);
		        }
		        public void onFinish() {
		        	if(bluetoothle.getState() >= BluetoothLE.STATE_2PASS){
		        		setState(new FormState());
		        	}else{
		        		setState(new NoMuchSavilaState());
		        	}
		        }
		    }.start();*/
		}
		@Override
		public void onExit(){
			img_bg.setVisibility(4);
			img_ac.setVisibility(4);
			img_btn.setVisibility(0);
		}
	}
	
	private class NoMuchSavilaState extends TestState{
		private CountDownTimer timer;
		private boolean move_to_init = false;
		@Override
		public void onStart(){
			label_btn.setText("確認");
			label_title.setText("口水量不足，請再多吐一些");
			label_subtitle.setText("仍在檢測中");
			timer = new CountDownTimer(10000, 10000){
				public void onTick(long ms){}
				public void onFinish(){
					if(move_to_init) return;
					setState(new FailState("二階段口水逾時"));
				}
			};
			timer.start();
		}
		@Override
		public void onClick(){
			move_to_init = true;
			timer.cancel();
			setState(new Stage2State());
		}
	}
	
	private class FormState extends TestState{
		@Override
		public void onStart(){
			test_done = true;
			ble.bleDisconnect();
			//DBControl.inst.startTesting();
			
			startActivity(new Intent(that, EventCopeSkillActivity.class));
			setState(new IdleState());
		}
	}
	private void setStorage() {
		File dir = MainStorage.getMainStorageDirectory();

		mainDirectory = new File(dir, String.valueOf(timestamp));
		if (!mainDirectory.exists())
			if (!mainDirectory.mkdirs()) {
				return;
			}

		imgFileHandler = new ImageFileHandler(mainDirectory,
				String.valueOf(timestamp));
		//questionFile = new QuestionFile(mainDirectory);
	}
	
	
	//release resource
	public void stop() { 
		
		if (cameraRecorder != null)
			cameraRecorder.close();
		
		first_connect = false;
		first_voltage = false;
		in_stage1 = false;
		test_done = false;
		
		if(ble!=null){
			ble.bleDisconnect();
			ble = null;
		}
		
		if (salivaCountDownTimer!= null){
			salivaCountDownTimer.cancel();
			salivaCountDownTimer = null;
		}
		
		if (testCountDownTimer != null) {
			testCountDownTimer.cancel();
			testCountDownTimer = null;
		}
	}
	
	public void stopDueToInit() {
		
		if (cameraRecorder != null)
			cameraRecorder.close();
		
		first_connect = false;
		first_voltage = false;
		in_stage1 = false;
		test_done = false;
		//if (cameraRecorder != null)
		//	cameraRecorder.close();

		if (ble != null)
			ble = null;
		//if (btInitHandler != null)
		//	btInitHandler.removeMessages(0);
		//if (cameraInitHandler != null)
		//	cameraInitHandler.removeMessages(0);
		//if (btRunTask != null)
		//	btRunTask.cancel(true);

		//if (testHandler != null)
		//	testHandler.removeMessages(0);
		
		if (salivaCountDownTimer != null)
			salivaCountDownTimer.cancel();

		if (testCountDownTimer != null)
			testCountDownTimer.cancel();
		//if (openSensorMsgTimer != null)
		//	openSensorMsgTimer.cancel();
	}
	
	
	
	public void onPause() {
		
		stop();
		super.onPause();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		// dismiss dormancy
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		label_btn = (TextView)findViewById(R.id.tv_btn);
		label_subtitle = (TextView)findViewById(R.id.tv_subtitle);
		label_title = (TextView)findViewById(R.id.tv_title);
		
		img_bg = (ImageView)findViewById(R.id.iv_bar_bg);
		img_ac = (ImageView)findViewById(R.id.iv_bar_ac);
		img_btn = (ImageView)findViewById(R.id.vts_iv_cry);
		cameraLayout = (FrameLayout)findViewById(R.id.cameraLayout);
		//TODO: camera_mask = (ImageView)findViewById(R.id.test_camera_mask);
		
		/** Load sound into sound pool*/
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
		count_down_audio_id = soundPool.load(this, R.raw.short_beep, 1); 
		preview_audio_id = soundPool.load(this, R.raw.din_ding, 1);
		setState(new IdleState());
			
		/** State onclick function use here*/
		img_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CertainState.onClick();
			}
		});
		that = this;
	}

	
	private void reset() {

		timestamp = System.currentTimeMillis();

		//setGuideMessage(R.string.test_guide_reset_top,R.string.test_guide_reset_bottom);

		PreferenceControl.setUpdateDetectionTimestamp(timestamp);

		setStorage();
		cameraRecorder = new CameraRecorder(this, imgFileHandler);
		cameraRunHandler = new CameraRunHandler(cameraRecorder);
		
		Boolean debug = PreferenceControl.isDebugMode();

		//prev_drawable_time = -1;

		//for (int i = 0; i < 3; ++i)
		//	INIT_PROGRESS[i] = DONE_PROGRESS[i] = false;
	}
	
	private class TestCountDownTimer extends CountDownTimer {

		private static final int SECOND_FIX = 1300;
		private long prevSecond = 99;

		public TestCountDownTimer(long second) {
			super(second * SECOND_FIX, 100);
		}

		@Override
		public void onFinish() {
			//startButton.setVisibility(View.INVISIBLE);
			//countDownText.setText("");
			//showDebug(">Start to run the  device");
			//runBT();
			setState(new Stage1State());
		}

		@Override
		public void onTick(long millisUntilFinished) {
			long displaySecond = millisUntilFinished / SECOND_FIX;
			if (displaySecond < prevSecond) {
				
				soundPool.play(count_down_audio_id, 0.6f, 0.6f, 0, 0, 1.0F);
				label_btn.setText(String.valueOf(displaySecond));
				prevSecond = displaySecond;
				
			}
		}
	}
	
	private class SalivaCountDownTimer extends CountDownTimer {
		
		private int ptr=0;
		

		public SalivaCountDownTimer() {
			super(WAIT_SALIVA_SECOND*1000, 1000);
		}

		@Override
		public void onFinish() {
			//startButton.setVisibility(View.INVISIBLE);
			//countDownText.setText("");
			//showDebug(">Start to run the  device");
			//runBT();
			in_stage2=true;
			//setState(new FormState());
		}

		@Override
		public void onTick(long millisUntilFinished) {
			
			img_ac.setImageResource(ELECTRODE_RESOURCE[ptr++]);		
			
		}
	}
	
	
	
	
	//Upload all the data
	@Override
	protected void onStart() {
		UploadService.startUploadService(this);
		super.onStart();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //參數1:群組id, 參數2:itemId, 參數3:item順序, 參數4:item名稱
        menu.add(0, 0, 0, "說明");
        menu.add(0, 1, 1, "離開");
        menu.add(0, 2, 2, "記事");
        menu.add(0, 3, 3, "BLE");
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //依據itemId來判斷使用者點選哪一個item
        switch(item.getItemId()) {
            case 0:
                //在TextView上顯示說明
    			startActivity(new Intent(that, InfoActivity.class));
                break;
            case 1:
                //結束此程式
                finish();
                break;
            case 2:
            	new NoteDialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen).show();
            	break;
            case 3:
            	//startActivity(new Intent(that, MainActivity.class));
            	startActivityForResult(new Intent(that, MainActivity.class), 0);
            	break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
    

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ble.onBleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void bleNotSupported() {
    	  Toast.makeText(this, "BLE not support", Toast.LENGTH_SHORT).show();
//        this.finish();
    }

    @Override
    public void bleConnectionTimeout() {
        Toast.makeText(this, "BLE connection timeout", Toast.LENGTH_SHORT).show();
        setState(new FailState("連接逾時"));
    }

    @Override
    public void bleConnected() {
        Log.i(TAG, "BLE connected");
        Toast.makeText(this, "BLE connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void bleDisconnected() {
        Log.i(TAG, "BLE disconnected");
        Toast.makeText(this, "BLE disconnected", Toast.LENGTH_SHORT).show();
        
        if(!test_done)
        	setState(new FailState("連接中斷"));
        else
        	setState(new FailState("測試完成")); //temporary solution
        //if(ble != null) {
        //    ble = null;
        //}
    }

    @Override
    public void bleWriteStateSuccess() {
        Log.i(TAG, "BLE ACTION_DATA_WRITE_SUCCESS");
        Toast.makeText(this, "BLE write state success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void bleWriteStateFail() {
        Log.i(TAG, "BLE ACTION_DATA_WRITE_FAIL");
        Toast.makeText(this, "BLE write state fail", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void bleNoPlug() {
        Log.i(TAG, "No test plug");
        Toast.makeText(this, "No test plug", Toast.LENGTH_SHORT).show();
        setState(new FailState("請將試紙匣插入裝置"));
    }

    @Override
    public void blePlugInserted(byte[] plugId) {
        Log.i(TAG, "Test plug is inserted");
        Toast.makeText(this, "Test plug is inserted", Toast.LENGTH_SHORT).show();
        
        if(!first_connect){
        	setState(new FiveSecondState());
        	first_connect=true;
        }
        //check ID here
    }


    @Override
    public void bleColorReadings(byte[] colorReadings) {
        Log.i(TAG, "Color sensor readings");
    }

	@Override
	public void bleElectrodeAdcReading(byte header, byte[] adcReading) {
		// TODO Auto-generated method stub
		//String str = new String(adcReading);
		String str = String.valueOf(adcReading[0]);
		String str2 = String.valueOf(header);
		Log.i(TAG2, "State: "+str2+" "+str);
		if(in_stage1 && (int)adcReading[0]> FIRST_VOLTAGE_THRESHOLD && !first_voltage){
			setState(new Stage2State());
			first_voltage=true;
		}
		else if(in_stage2 && (int)adcReading[0]< SECOND_VOLTAGE_THRESHOLD && !second_voltage){
			setState(new FormState());
			second_voltage=true;
		}
	}
	
	
	//要等Camera Initial好才能繼續
	@Override
	public void updateInitState(int type) {
		
		if (camera_initial == true)
			return;
		camera_initial = true;
		cameraInitHandler.removeMessages(0);
	
	}
	
	@Override
	public void updateDoneState(int type) {
		
		if (camera_done == true)
			return;
			camera_done = true;

		UploadService.startUploadService(this);

		
	}


	@Override
	public void stopByFail(int fail) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public FrameLayout getPreviewFrameLayout() {
		
		//return cameraLayout;
		return (FrameLayout)findViewById(R.id.cameraLayout);
	
	}


	@Override
	public Point getPreviewSize() {
		
		int left = img_btn.getLeft();
		int right = img_btn.getRight();
		int top = img_btn.getTop();
		int bottom = img_btn.getBottom();
		return new Point(right - left, bottom - top);
		
	}
}
