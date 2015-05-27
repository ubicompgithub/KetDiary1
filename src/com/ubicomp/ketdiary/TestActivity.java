package com.ubicomp.ketdiary;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ubicomp.ketdiary.BluetoothLE.BluetoothLE;
import com.ubicomp.ketdiary.BluetoothLE.BluetoothListener;
import com.ubicomp.ketdiary.BluetoothLE.MainActivity;
import com.ubicomp.ketdiary.camera.CameraCaller;
import com.ubicomp.ketdiary.camera.CameraInitHandler;
import com.ubicomp.ketdiary.camera.CameraRecorder;
import com.ubicomp.ketdiary.camera.CameraRunHandler;
import com.ubicomp.ketdiary.camera.ImageFileHandler;
import com.ubicomp.ketdiary.camera.Tester;
import com.ubicomp.ketdiary.color.ColorDetect2;
import com.ubicomp.ketdiary.file.ColorRawFileHandler;
import com.ubicomp.ketdiary.file.MainStorage;
import com.ubicomp.ketdiary.file.QuestionFile;
import com.ubicomp.ketdiary.file.VoltageFileHandler;
import com.ubicomp.ketdiary.system.PreferenceControl;



@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class TestActivity extends Activity implements BluetoothListener, CameraCaller{
	
	
	private static final String TAG = "BluetoothLE";
	private static final String TAG2 = "debug";
	private static final String TAG3 = "-State-";
	
	private TextView label_btn, label_subtitle, label_title, debug_msg;
	private ImageView img_bg, img_ac, img_btn;
	
	private ScrollView debugScrollView;
	private EditText debugMsg;
	private ChangeMsgHandler msgHandler;
	private TextView debugBracValueView;
	private Button btn_debug;

	private long timestamp = 0;

	private CountDownTimer testCountDownTimer = null;
	private CountDownTimer salivaCountDownTimer = null;
	private CountDownTimer timeoutCountDownTimer= null;
	private CountDownTimer cameraCountDownTimer= null;
	private CountDownTimer openSensorMsgTimer = null;
	private CountDownTimer confirmCountDownTimer = null;
	
	private final boolean[] INIT_PROGRESS = { false, false, false };
	private final boolean[] DONE_PROGRESS = { false, false, false };
	/** self activity*/
	Activity that;
	
	/** Camare variables */
	//private Camera mCamera = null;
	//private CameraPreview mCamPreview;
	private FrameLayout cameraLayout;
	
	// Camera
	private CameraInitHandler cameraInitHandler;
	private CameraRecorder cameraRecorder;
	private CameraRunHandler cameraRunHandler;
	//TODO: private ImageView camera_mask;
	
	
	//File 
	private File mainDirectory;
	private ImageFileHandler imgFileHandler;
	private VoltageFileHandler voltageFileHandler;
	private ColorRawFileHandler colorRawFileHandler;
	//private ColorResultFileHandler colorResultFileHandler;
	private QuestionFile questionFile; 
	
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
	
	private boolean start_test = false;
	private boolean is_connect = false;
	private boolean first_connect = false;
	private boolean first_voltage = false;
	private boolean second_voltage= false;
	private boolean in_stage1 = false;
	private boolean test_done = false;
	private boolean in_stage2 = false;
	private boolean camera_initial=false;
	private boolean camera_done=false;
	private boolean is_timeout=false;
	private boolean is_debug = false;
	private boolean ble_disconnect=false;
	private boolean ble_connected = false;
	private boolean ble_pluginserted = false;
	
	private static Object init_lock = new Object();
	private static Object done_lock = new Object();

	private int voltage_count=0;
	private int voltage = 0;
	
	private int state;
	//private boolean second_pass=false;
	
	private static final int COUNT_DOWN_SECOND = 5;
	private static final int WAIT_SALIVA_SECOND = 7;
	private static final int FIRST_VOLTAGE_THRESHOLD = 25;
	private static final int SECOND_VOLTAGE_THRESHOLD= 15;
	private static final int TIMEOUT_SECOND = 30;
	private static final int CAMERATIMEOUT = 10;
	private static final int CONFIRM_SECOND = 5;
	
	private static final int FAIL_STATE = -1;
	private static final int IDLE_STATE = 0;
	private static final int CONN_STATE = 1;
	private static final int FIVESECOND_STATE = 2;
	private static final int CAMERA_STATE = 3;
	private static final int STAGE2_STATE = 4;
	private static final int DONE_STATE = 5;
	private static final int NOTENOUGH_STATE = 6;
	private static final int RUN_STATE = 7;
	
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
			state = IDLE_STATE;
			
			img_btn.setEnabled(true);
			label_btn.setText("開始");
			label_subtitle.setText("請點選開始進行測試");
			label_title.setText("測試尚未開始");
		}
		@Override
		public void onClick(){
			first_connect =false;
			first_voltage =false;
			ble_pluginserted =false;
			
			
			reset();
			
			start_test = true;
			setState(new ConnState());
		}
	}
	
	private class FailState extends TestState{
		private String err_msg;
		/**
		 * A Fail state with error msg
		 * @param _err_msg
		 */
		public FailState(String _err_msg){ //TODO set subtitle
			err_msg = _err_msg;
		}
		@Override
		public void onStart(){
			state = FAIL_STATE;
			
			
			label_btn.setText("確認");
			label_subtitle.setText("");
			label_title.setText(err_msg);
			img_btn.setEnabled(true);
			
			//cameraLayout.setVisibility(View.INVISIBLE);
			cameraRecorder.pause();//TODO: Camera Resource Release
			
			stopDueToInit();
			
			//if(ble != null)
				//ble.bleDisconnect();
		}
		@Override
		public void onClick(){ 
			
			//stop();
			setState(new IdleState());
		}
	}
	
	private class ConnState extends TestState{
		private volatile boolean is_conn = false;
		@Override
		public void onStart(){
			state = CONN_STATE;
			
			img_btn.setEnabled(false);
			label_btn.setText("");
			label_subtitle.setText("");
			label_title.setText("準備中....");
			
			startConnection();
			openSensorMsgTimer = new OpenSensorMsgTimer();
			openSensorMsgTimer.start();
			/*  Next State decide by callback
			 *  1. timeout 2. connect but no saliva(or wrong ID) 3.connect with right salivaId (continue)
			 */
		    
		}
		
		public void onExit(){
			if(openSensorMsgTimer!=null){
				openSensorMsgTimer.cancel();
				openSensorMsgTimer=null;
			}
		}
	}
	
	private class FiveSecondState extends TestState{
		@Override
		public void onStart(){
			
			state = FIVESECOND_STATE;
			
			Log.d("Main", "Enter FiveSecond");
			label_btn.setText("5");
			label_subtitle.setText("請蓄積口水");
			img_btn.setEnabled(false);
			
			//if(	testCountDownTimer == null )
			testCountDownTimer = new TestCountDownTimer(COUNT_DOWN_SECOND);
			testCountDownTimer.start();
			
		}
		
		public void onExit(){
			if(	testCountDownTimer != null ){
				testCountDownTimer.cancel();
				testCountDownTimer = null;
			}
		}
	}
	
	private class CameraState extends TestState{

		@Override
		public void onStart(){
			
			state = CAMERA_STATE;
						
			soundPool.play(preview_audio_id, 1.0F, 1.0F, 0, 0, 1.0F);
			Log.d("Main", "Enter Stage1");
			
			
			/** Search for the front facing camera */
			/*
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
			mCamera = Camera.open(cameraId);
			mCamPreview.set(that, mCamera);
			cameraLayout.setVisibility(View.VISIBLE);
			*/
			
			cameraRecorder.start();
			

			label_btn.setText("");
			label_subtitle.setText("請將臉對準中央，並吐口水於管中");
			label_title.setText("請吐口水");
			
			
			if(ble != null)
				ble.bleWriteState((byte)2);//ble.bleWriteState((byte)3);
			
			
			cameraCountDownTimer = new CameraCountDownTimer();
			cameraCountDownTimer.start();

		}
		@Override
		public void onExit(){
			if(cameraCountDownTimer!=null){
				cameraCountDownTimer.cancel();
				cameraCountDownTimer=null;
			}
			//cameraLayout.setVisibility(View.INVISIBLE);
			//if(test_timer != null)
			//	test_timer.cancel();
		}
	}

	private class Stage2State extends TestState{
		
		@Override
		public void onStart(){
			state = STAGE2_STATE;
			
			label_btn.setText("");
			img_bg.setVisibility(View.VISIBLE);
			img_ac.setVisibility(View.VISIBLE);
			img_btn.setVisibility(View.INVISIBLE);
			//cameraLayout.setVisibility(View.VISIBLE);
			
			
			salivaCountDownTimer = new SalivaCountDownTimer();
			salivaCountDownTimer.start();
			
		}
		@Override
		public void onExit(){
			if (salivaCountDownTimer != null){
				salivaCountDownTimer.cancel();
				salivaCountDownTimer = null;
			}
			
			
			img_bg.setVisibility(View.INVISIBLE);
			img_ac.setVisibility(View.INVISIBLE);
			img_btn.setVisibility(View.VISIBLE);
		}
	}
	
	private class NotEnoughSavilaState extends TestState{

		private boolean move_to_init = false;
		@Override
		public void onStart(){
			
			state = NOTENOUGH_STATE;
			
			img_btn.setEnabled(false);
			//label_btn.setText("繼續");
			label_title.setText("請在10秒內再吐一口水");
			label_subtitle.setText("口水量不足");
			
			
			cameraCountDownTimer = new CameraCountDownTimer();
			cameraCountDownTimer.start();
			
			/*if(	timeoutCountDownTimer==null	)
				timeoutCountDownTimer = new TimeoutCountDownTimer();
			timeoutCountDownTimer.start();*/
			
		}
		@Override
		public void onClick(){
			//move_to_init = true;
			/*is_timeout = false;
			
			if(	timeoutCountDownTimer!=null	)
				timeoutCountDownTimer.cancel();
			
			setState(new Stage2State());*/
		}
		public void onExit(){
			if(	cameraCountDownTimer!=null	)
				cameraCountDownTimer.cancel();
		}
	}
	
	private class RunState extends TestState{
		
		@Override
		public void onStart(){
			state = RUN_STATE;
			
			label_btn.setText("");
			img_bg.setVisibility(View.VISIBLE);
			img_ac.setVisibility(View.VISIBLE);
			img_btn.setVisibility(View.INVISIBLE);
			label_title.setText("口水確認中");
			label_subtitle.setText("請稍後");
			//cameraLayout.setVisibility(View.VISIBLE);
			
			
			confirmCountDownTimer = new ConfirmCountDownTimer();
			confirmCountDownTimer.start();
			
		}
		@Override
		public void onExit(){
			if (confirmCountDownTimer != null){
				confirmCountDownTimer.cancel();
				confirmCountDownTimer = null;
			}
			
			
			//img_bg.setVisibility(View.INVISIBLE);
			//img_ac.setVisibility(View.INVISIBLE);
			//img_btn.setVisibility(View.VISIBLE);
		}
	}
	
	private class DoneState extends TestState{
		@Override
		public void onStart(){
			
			state = DONE_STATE;
			
			label_title.setText("測試完成!");
			label_subtitle.setText("");
			
			test_done = true;
			if(ble != null)
				ble.bleDisconnect();
			//DBControl.inst.startTesting();
			stop();
			
			//startActivity(new Intent(that, EventCopeSkillActivity.class));
			startActivity(new Intent(that, NoteActivity.class));
			//setState(new IdleState());
		}
	}
	private void setStorage() {
		File dir = MainStorage.getMainStorageDirectory();

		mainDirectory = new File(dir, String.valueOf(timestamp));
		if (!mainDirectory.exists())
			if (!mainDirectory.mkdirs()) {
				return;
			}
		voltageFileHandler = new VoltageFileHandler(mainDirectory,
				String.valueOf(timestamp));
		
		colorRawFileHandler = new ColorRawFileHandler(mainDirectory,
				String.valueOf(timestamp));
		
		imgFileHandler = new ImageFileHandler(mainDirectory,
				String.valueOf(timestamp));
		
		questionFile = new QuestionFile(mainDirectory);
	}
	
	public void startConnection() {
		// initialize bt task
		if(ble == null) {
			ble = new BluetoothLE(that, "ket_000");
			//PreferenceControl.getDeviceId()
		}
		if(!is_connect)
			ble.bleConnect();
		
		ble.bleWriteState((byte)1);
		// initialize camera task
		cameraInitHandler = new CameraInitHandler(this, cameraRecorder);
		cameraInitHandler.sendEmptyMessage(0);
	}
	/*
	public void writeQuestionFile(int type, int items, int impact) {
		questionFile.write(type, items, impact);
	}*/
	
	
	//release resource
	public void stop() { 
		
		if (cameraRecorder != null)
			cameraRecorder.close();
		
		//first_connect = false;
		//first_voltage = false;
		//in_stage1 = false;
		//test_done = false;
		
		if (voltageFileHandler != null) {
			voltageFileHandler.close();
			voltageFileHandler = null;
		}
		
		if (colorRawFileHandler != null) {
			colorRawFileHandler.close();
			colorRawFileHandler = null;
		}
		
		if (msgHandler != null) {
			msgHandler.removeMessages(0);
		}
		if (cameraInitHandler != null)
			cameraInitHandler.removeMessages(0);
		
		if (cameraRunHandler != null)
			cameraRunHandler.removeMessages(0);
		
		if(ble!=null){
			is_connect = false;
			ble.bleDisconnect();
			ble = null;
		}
		
		if (cameraCountDownTimer!= null){
			cameraCountDownTimer.cancel();
			cameraCountDownTimer = null;
		}
		
		if (timeoutCountDownTimer!= null){
			timeoutCountDownTimer.cancel();
			timeoutCountDownTimer = null;
		}
		
		if (salivaCountDownTimer!= null){
			salivaCountDownTimer.cancel();
			salivaCountDownTimer = null;
		}
		
		if (testCountDownTimer != null) {
			testCountDownTimer.cancel();
			testCountDownTimer = null;
		}
		if (openSensorMsgTimer != null){
			openSensorMsgTimer.cancel();
			openSensorMsgTimer=null;
		}
		
		if (confirmCountDownTimer != null){
			confirmCountDownTimer.cancel();
			confirmCountDownTimer=null;
		}
		
	}
	
	public void stopDueToInit() {
		
		if (cameraRecorder != null)
			cameraRecorder.close();
		
		first_connect = false;
		first_voltage = false;
		
		test_done = false;
		start_test = false;
		
		if (msgHandler != null) {
			msgHandler.removeMessages(0);
		}

		if (ble != null){
			is_connect = false;
			ble.bleDisconnect(); // 原本註解
			ble = null;
		}

		if (cameraInitHandler != null)
			cameraInitHandler.removeMessages(0);
		
		if (cameraRunHandler != null)
			cameraRunHandler.removeMessages(0);
		
		//if (testHandler != null)
		//	testHandler.removeMessages(0);
		
		if (cameraCountDownTimer != null)
			cameraCountDownTimer.cancel();
		
		if (timeoutCountDownTimer != null)
			timeoutCountDownTimer.cancel();
		
		if (salivaCountDownTimer != null)
			salivaCountDownTimer.cancel();

		if (testCountDownTimer != null)
			testCountDownTimer.cancel();
		
		if (openSensorMsgTimer != null)
			openSensorMsgTimer.cancel();
		
		if (confirmCountDownTimer != null){
			confirmCountDownTimer=null;
		}
	}
	
	
	
	public void onPause() {
		
		stop();
		super.onPause();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		// dismiss sleep
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		checkDebug(is_debug);//PreferenceControl.isDebugMode());
		//reset();
		setState(new IdleState());
		
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
		
		btn_debug = (Button)findViewById(R.id.debug_button_1);
		debugScrollView = (ScrollView)findViewById(R.id.debug_scroll_view);
		debugMsg = (EditText)findViewById(R.id.debug_msg);
		//TODO: camera_mask = (ImageView)findViewById(R.id.test_camera_mask);
		
		/** Load sound into sound pool*/
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
		count_down_audio_id = soundPool.load(this, R.raw.short_beep, 1); 
		preview_audio_id = soundPool.load(this, R.raw.din_ding, 1);
		setState(new IdleState());
		
		btn_debug.setOnClickListener(new DebugOnClickListener());
			
		/** State onclick function use here*/
		img_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CertainState.onClick();
			}
		});
		that = this;
		
		
		msgHandler = new ChangeMsgHandler();
	}

	
	
	private void reset() {
		
		first_connect = false;
		first_voltage = false;
		in_stage1 = false;
		test_done = false;
		camera_initial=false;

		timestamp = System.currentTimeMillis();
		voltage_count = 0;
		//setGuideMessage(R.string.test_guide_reset_top,R.string.test_guide_reset_bottom);

		PreferenceControl.setUpdateDetectionTimestamp(timestamp);

		setStorage();
		
		
		cameraRecorder = new CameraRecorder(this, imgFileHandler);
		cameraRunHandler = new CameraRunHandler(cameraRecorder);
		
		Boolean debug = PreferenceControl.isDebugMode();

		//prev_drawable_time = -1;

		for (int i = 0; i < 3; ++i)
			INIT_PROGRESS[i] = DONE_PROGRESS[i] = false;
	}
	
	private class OpenSensorMsgTimer extends CountDownTimer {

		public OpenSensorMsgTimer() {
			super(5000, 50);
		}

		@Override
		public void onFinish() {
			showDebug(">Try to start the device");
			//startConnection();
			setState(new FailState("請開啟檢測器"));
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	}
	
	
	//each state timeout
	private class CameraCountDownTimer extends CountDownTimer {
			
			public CameraCountDownTimer() {
				super(CAMERATIMEOUT*1000, 1000);
			}

			@Override
			public void onFinish() {
				Log.i(TAG3, "FINISH");
				if(state == CAMERA_STATE)
					setState(new FailState("測試超時"));
				else if (state == NOTENOUGH_STATE){ // 判斷第二個電極是否通過
					if(voltage < SECOND_VOLTAGE_THRESHOLD)
						setState(new RunState());
					else
						setState(new FailState("測試失敗,請更換試紙匣後重試"));
				}
				
				//cameraRecorder.closeSuccess();
			}

			@Override
			public void onTick(long millisUntilFinished) {
				
				if(millisUntilFinished % 2500 == 0)
					cameraRunHandler.sendEmptyMessage(0);
				
				if(state == NOTENOUGH_STATE){
					label_title.setText("請在"+millisUntilFinished/1000 +"秒內再吐一口水");
				}
			}	
	}
	
	//each state timeout
	private class TimeoutCountDownTimer extends CountDownTimer {
		
		public TimeoutCountDownTimer() {
			super(TIMEOUT_SECOND*1000, 1000);
		}

		@Override
		public void onFinish() {
			if(is_timeout)
				setState(new FailState("測試超時"));
			
		}

		@Override
		public void onTick(long millisUntilFinished) {
			is_timeout=true;
		}	
	}
	
	private class ConfirmCountDownTimer extends CountDownTimer {
		
		private int ptr = 0;
		
		public ConfirmCountDownTimer() {
			super(CONFIRM_SECOND*1000, 1000);
		}

		@Override
		public void onFinish() {
			
			setState(new DoneState());
			
		}

		@Override
		public void onTick(long millisUntilFinished) {
			//is_timeout=true;
			img_ac.setImageResource(ELECTRODE_RESOURCE[ptr]);
			ptr+=2;
		}	
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
			showDebug(">Start to run the  device");
			//runBT();
			if(voltage < FIRST_VOLTAGE_THRESHOLD)
				setState(new CameraState());
			else
				setState(new FailState("請更換試紙匣後重試"));
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
			if( voltage < SECOND_VOLTAGE_THRESHOLD ){
				setState(new DoneState());
			}
			else{
				setState(new NotEnoughSavilaState() );
			}
			
			
			//setState(new FormState());
		}

		@Override
		public void onTick(long millisUntilFinished) {
			
			//cameraRunHandler.sendEmptyMessage(0);	
			img_ac.setImageResource(ELECTRODE_RESOURCE[ptr++]);		
			
		}
	}
	
	
	
	
	//Upload all the data
	@Override
	protected void onStart() {
		//UploadService.startUploadService(this);
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
            	//new NoteDialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen).show();
            	startActivity(new Intent(that, EventCopeSkillActivity.class));
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
    
    
    //=======BLE callback function 
    
    @Override
    public void bleNotSupported() {
    	  Toast.makeText(this, "BLE not support", Toast.LENGTH_SHORT).show();
    	  setState(new FailState("裝置不支援"));
//        this.finish();
    }

    @Override
    public void bleConnectionTimeout() {
    	Log.i(TAG, "connect timeout");
        Toast.makeText(this, "BLE connection timeout", Toast.LENGTH_SHORT).show();
        setState(new FailState("連接逾時"));
    }

    @Override
    public void bleConnected() {
    	is_connect = true;
        //Log.i(TAG, "BLE connected");
        //Toast.makeText(this, "BLE connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void bleDisconnected() {
        Log.i(TAG, "BLE disconnected");
        Toast.makeText(this, "BLE disconnected", Toast.LENGTH_SHORT).show();
        //setState(new FailState("連接中斷"));
        
        if(state != IDLE_STATE && state!= FAIL_STATE && state!= DONE_STATE)
        	setState(new FailState("連接中斷"));
        else if (state == STAGE2_STATE)
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
        Toast.makeText(this, "BLE writefstate fail", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void bleNoPlug() {
        Log.i(TAG, "No test plug");
    	
        if(state != IDLE_STATE){
        	Toast.makeText(this, "No test plug", Toast.LENGTH_SHORT).show();
        	setState(new FailState("請將試紙匣插入裝置"));
        }
    }

    @Override
    public void blePlugInserted(byte[] plugId) {
        //Log.i(TAG, "Test plug is inserted");
        ble_pluginserted = true;
        //check ID here
        if( state == CONN_STATE )
        	updateInitState(Tester._BT);
        
        /*
        if(!first_connect){
        	Toast.makeText(this, "Test plug is inserted", Toast.LENGTH_SHORT).show();
        	
        	if(camera_initial)
        		setState(new FiveSecondState());
        	first_connect=true;
        }*/
        
    }


    @Override
    public void bleColorReadings(byte[] colorReadings) {
    	String feature, feature2;
    	String str1 ="";
    	String str2 ="";
    	int[] color = new int[4];
    	for(int i=0; i<8; i+=2){
    		color[i/2] = colorReadings[i]+colorReadings[i+1]*256;
    		str1 = str1+ " " + String.valueOf(color[i/2]);
    	}
    	//ColorDetect2.colorDetect(color);
    	//feature = ColorDetect2.colorDetect2(color);
    	
    	//writeToColorRawFile(str1+"\n");
    	//writeToColorRawFile(feature+"\n");
    	//writeToColorRawFile(str1+"\n");
    	
    	int[] color2 = new int[4];
    	for(int i=8; i<16; i+=2){
    		color2[(i-8)/2] = colorReadings[i]+colorReadings[i+1]*256;
    		str2 = str2+ " " + String.valueOf(color2[(i-8)/2]);
    	}
    	
    	//feature2 = ColorDetect2.colorDetect2(color2);
    	//writeToVoltageFile(feature2+"\n");	
    	
    	showDebug(">First:"+str1+" Second:"+str2);
    	//Log.i(TAG, "First: "+str1);
    	//Log.i(TAG, "Second: "+str2);
    	
        //Log.i(TAG, "Color sensor readings");
    }
    
    protected void writeToColorRawFile(String str) {
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("COLOR", str);
		msg.setData(data);
		colorRawFileHandler.sendMessage(msg);
	}
    
    protected void writeToVoltageFile(String str) {
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("VOLTAGE", str);
		msg.setData(data);
		voltageFileHandler.sendMessage(msg);
	}

	@Override
	public void bleElectrodeAdcReading(byte header, byte[] adcReading) {
		// TODO Auto-generated method stub
		//String str = new String(adcReading);
		voltage = (int)adcReading[0];
		
		if( voltage > FIRST_VOLTAGE_THRESHOLD )
			voltage_count++;
		
		
		
		String str = String.valueOf(voltage);
		String str2 = String.valueOf(header);
		
		String str3= " "+str2+" "+str;
		Log.i(TAG2, str3);
		
		showDebug(">"+str3);
		
		writeToVoltageFile(str+str2+"\n");
		if(state == CAMERA_STATE && voltage > FIRST_VOLTAGE_THRESHOLD ){
			setState(new Stage2State());
			first_voltage=true;
		}
		else if(state==NOTENOUGH_STATE && voltage< SECOND_VOLTAGE_THRESHOLD && !second_voltage){
			//setState(new DoneState());
			second_voltage=true;
		}
	}
	
	
	//要等Camera Initial好才能繼續
	@Override
	public void updateInitState(int type) {
		synchronized (init_lock) {
			if (INIT_PROGRESS[type] == true)
				return;
			INIT_PROGRESS[type] = true;
			if (INIT_PROGRESS[_BT] && INIT_PROGRESS[_CAMERA]) {
				cameraInitHandler.removeMessages(0);
				setState(new FiveSecondState());
			}
		}
		/*
		if (camera_initial == true)
			return;
		camera_initial = true;
		cameraInitHandler.removeMessages(0);
		*/
		//cameraInitHandler.removeMessages(0);
	
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
	
	// DebugMode
	// --------------------------------------------------------------------------------------------------------
	
	private void checkDebug(boolean debug) {
		
		if (debug) {
			debugScrollView.setVisibility(View.VISIBLE);
			msgHandler = new ChangeMsgHandler();
			debugMsg.setText("");
			debugMsg.setOnKeyListener(null);
			//TextView debugText = (TextView)findViewById(R.id.debug_mode_text);

		} else {
			debugScrollView.setVisibility(View.INVISIBLE);
			return;
		}

	}

	private class DebugOnClickListener implements View.OnClickListener {

		private int cond;

		public DebugOnClickListener(){
		}

		@Override
		public void onClick(View v) {
				
			
			if(is_debug){
				//debugScrollView.setVisibility(View.VISIBLE);
				is_debug = false;
			}
			else{
				//debugScrollView.setVisibility(View.INVISIBLE);
				is_debug = true;
			}
			checkDebug(is_debug);
		}		
	}


	public void showDebug(String message, int type) {
		//Boolean debug = PreferenceControl.isDebugMode();
		if (is_debug && msgHandler != null) {
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putInt("type", type);
			data.putString("message", message);
			msg.setData(data);
			msg.what = 0;
			msgHandler.sendMessage(msg);
		}
	}

	public void showDebug(String message) {
		showDebug(message, 0);
	}

	@SuppressLint("HandlerLeak")
	private class ChangeMsgHandler extends Handler {
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			int type = data.getInt("type");
			if (type == 0) {
				debugMsg.append("\n" + data.getString("message"));
				debugScrollView.scrollTo(0, debugMsg.getBottom() + 100);
				debugMsg.invalidate();
			} else if (type == 1) {
				//showDebugVoltage(data.getString("message"));
			}
		}
	}

}
