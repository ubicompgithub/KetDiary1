package com.ubicomp.ketdiary.fragment;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ubicomp.ketdiary.HelpActivity;
import com.ubicomp.ketdiary.MainActivity;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.ResultService3;
import com.ubicomp.ketdiary.BluetoothLE.BluetoothLE2;
import com.ubicomp.ketdiary.BluetoothLE.BluetoothListener;
import com.ubicomp.ketdiary.camera.CameraCaller;
import com.ubicomp.ketdiary.camera.CameraInitHandler;
import com.ubicomp.ketdiary.camera.CameraRecorder;
import com.ubicomp.ketdiary.camera.CameraRunHandler;
import com.ubicomp.ketdiary.camera.ImageFileHandler;
import com.ubicomp.ketdiary.camera.Tester;
import com.ubicomp.ketdiary.clicklog.ClickLog;
import com.ubicomp.ketdiary.clicklog.ClickLogId;
import com.ubicomp.ketdiary.db.TestDataParser2;
import com.ubicomp.ketdiary.dialog.NoteDialog3;
import com.ubicomp.ketdiary.dialog.TestQuestionCaller2;
import com.ubicomp.ketdiary.file.ColorRawFileHandler;
import com.ubicomp.ketdiary.file.MainStorage;
import com.ubicomp.ketdiary.file.QuestionFile;
import com.ubicomp.ketdiary.file.VoltageFileHandler;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.LoadingDialogControl;
import com.ubicomp.ketdiary.ui.ScaleOnTouchListener;
import com.ubicomp.ketdiary.ui.Typefaces;

public class TestFragment extends Fragment implements BluetoothListener, CameraCaller, TestQuestionCaller2{
	
	private static final String TAG = "BluetoothLE";
	private static final String TAG2 = "debug";
	private static final String TAG3 = "-State-";
	
	private static final String TAG1 = "TEST_PAGE";

	public Activity activity = null;
	private TestFragment testFragment;
	private View view;
	
	
	private RelativeLayout main_layout;
	private LinearLayout water_layout;
	private TextView label_btn, label_subtitle, label_title, debug_msg, test_msg;
	private ImageView img_bg, img_ac, img_btn, img_info, img_water1, img_water2, img_water3, img_face, img_help;
	
	private boolean isDeveloper = PreferenceControl.isDeveloper();
	private boolean debug = PreferenceControl.isDebugMode();
	private boolean collectdata = PreferenceControl.getCollectData();
	
	//debug View
	private ScrollView debugScrollView;
	private EditText debugMsg;
	private ChangeMsgHandler msgHandler;
	private TextView debugBracValueView;
	private Button btn_debug, btn_note;

	private long timestamp = 0;

	private CountDownTimer testCountDownTimer = null;
	private CountDownTimer salivaCountDownTimer = null;
	private CountDownTimer timeoutCountDownTimer= null;
	private CountDownTimer cameraCountDownTimer= null;
	private CountDownTimer openSensorMsgTimer = null;
	private CountDownTimer confirmCountDownTimer = null;
	
	private final boolean[] INIT_PROGRESS = { false, false, false };
	private final boolean[] DONE_PROGRESS = { false, false, false };
	
	public TestDataParser2 TDP;
	public NoteDialog3 msgBox = null;
	
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
	private File mainDirectory = null;
	private ImageFileHandler imgFileHandler;
	private VoltageFileHandler voltageFileHandler;
	private ColorRawFileHandler colorRawFileHandler;
	private ChangeTabsHandler changeTabsHandler;
	//private ColorResultFileHandler colorResultFileHandler;
	private QuestionFile questionFile; 
	
	/** Sound playing variables */
	private SoundPool soundPool;
	
	/*private static final int[] ELECTRODE_RESOURCE = { 0, R.drawable.test_progress_1,
		R.drawable.test_progress_2, R.drawable.test_progress_3,
		R.drawable.test_progress_4, R.drawable.test_progress_5,
		R.drawable.test_progress_5 };*/
	
	private static final int[] ELECTRODE_RESOURCE = { 0, R.drawable.test_progress_circle_2,
		R.drawable.test_progress_circle_4, R.drawable.test_progress_circle_6,
		R.drawable.test_progress_circle_8, R.drawable.test_progress_circle_10,
		R.drawable.test_progress_circle_10 };
	
	private Typeface digitTypefaceBold, wordTypefaceBold;
	private DecimalFormat format;
	
	/** Sound id*/
	private int count_down_audio_id;
	private int preview_audio_id;
	
	private static SoundPool soundpool;
	private static int soundId;
	
	private TestState CertainState = null;
	private BluetoothLE2 ble = null;
	
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
	private boolean goThroughState = false;
	
	private static Object init_lock = new Object();
	private static Object done_lock = new Object();
	
	private String[] test_guide_msg;

	private int voltage_count=0;
	private int voltage = 0;
	
	private int state;
	private int resultState;
	//private boolean second_pass=false;
	
	// test Detail parameter
	private String cassetteId ="";
	private int failedState;
	private int firstVoltage;
	private int secondVoltage;
	private int devicePower;
	private int colorReading;
	private float connectionFailRate;
	private String failedReason; 
	
	
	
	private static final int COUNT_DOWN_SECOND = 5;
	private static final int WAIT_SALIVA_SECOND = 7;
	/*
	private static final int FIRST_VOLTAGE_THRESHOLD = 25; //之後會是50 跟 40
	private static final int SECOND_VOLTAGE_THRESHOLD= 15;
	
	private static final int FIRST_VOLTAGE_THRESHOLD = 50; 
	private static final int SECOND_VOLTAGE_THRESHOLD= 40;
	*/
	//private static final int FIRST_VOLTAGE_THRESHOLD = 75; 
	//private static final int SECOND_VOLTAGE_THRESHOLD= 60;
	
	private static int FIRST_VOLTAGE_THRESHOLD = PreferenceControl.getVoltag1(); 
	private static int SECOND_VOLTAGE_THRESHOLD= PreferenceControl.getVoltag2();
	private static int CAMERATIMEOUT = PreferenceControl.getVoltageCountDown();
	private static int CAMERATIMEOUT2 = PreferenceControl.getVoltage2CountDown();
	
	private static final int TIMEOUT_SECOND = 30;
	//private static final int CAMERATIMEOUT = 10;
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this.getActivity();
		testFragment = this;
		
		//format = new DecimalFormat();
		//format.setMaximumIntegerDigits(1);
		//format.setMinimumIntegerDigits(1);
		//format.setMinimumFractionDigits(2);
		//format.setMaximumFractionDigits(2);
		digitTypefaceBold = Typefaces.getDigitTypefaceBold();
		wordTypefaceBold = Typefaces.getWordTypefaceBold();
		
		/** Load sound into sound pool*/
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
		count_down_audio_id = soundPool.load(activity, R.raw.short_beep, 1); 
		preview_audio_id = soundPool.load(activity, R.raw.din_ding, 1);
		
		msgHandler = new ChangeMsgHandler();
		resultState = PreferenceControl.getAfterTestState();
		
		Log.d(TAG1, debug+" "+isDeveloper);
		/*
		if (soundpool == null) {
			soundpool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
			soundId = soundpool.load(this.getActivity(), R.raw.short_beep, 1);
		}*/
		//msgLoadingHandler = new MsgLoadingHandler();
		//failBgHandler = new FailMessageHandler();
		//testHandler = new TestHandler();
		
		changeTabsHandler = new ChangeTabsHandler();
		test_guide_msg = getResources().getStringArray(R.array.test_guide_msg);
		//changeTabsHandler.sendEmptyMessage(0);
		
				
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_test2, container, false);
		
		main_layout = (RelativeLayout) view.findViewById(R.id.test_fragment_main_layout); 
		
		label_btn = (TextView) view.findViewById(R.id.tv_btn);
		label_subtitle = (TextView) view.findViewById(R.id.tv_subtitle);
		label_title = (TextView) view.findViewById(R.id.tv_title);
		test_msg = (TextView) view.findViewById(R.id.test_message);

		img_help = (ImageView) view.findViewById(R.id.iv_help);
		
		img_bg = (ImageView)view.findViewById(R.id.iv_bar_bg);
		img_ac = (ImageView)view.findViewById(R.id.iv_bar_ac);
		img_btn = (ImageView)view.findViewById(R.id.vts_iv_cry);
		img_face= (ImageView)view.findViewById(R.id.test_face);
		
		cameraLayout = (FrameLayout)view.findViewById(R.id.cameraLayout);
		water_layout = (LinearLayout)view.findViewById(R.id.water_layout);
		
		
		img_water1 = (ImageView)view.findViewById(R.id.iv_water1);
		img_water2 = (ImageView)view.findViewById(R.id.iv_water2);
		img_water3 = (ImageView)view.findViewById(R.id.iv_water3);
		
		btn_debug = (Button)view.findViewById(R.id.debug_button_1);
		btn_note =( Button)view.findViewById(R.id.debug_button_2);
		debugScrollView = (ScrollView)view.findViewById(R.id.debug_scroll_view);
		
		debugMsg = (EditText)view.findViewById(R.id.debug_msg);
		
		//設定字型
		label_btn.setTypeface(wordTypefaceBold);
		label_subtitle.setTypeface(wordTypefaceBold);
		label_title.setTypeface(wordTypefaceBold);
		
		label_subtitle.setTextColor(getResources().getColor(R.color.text_gray2));
		label_title.setTextColor(getResources().getColor(R.color.text_gray2));
		//messageView.setTypeface(wordTypefaceBold);
		
		setState(new IdleState());
		btn_debug.setOnClickListener(new DebugOnClickListener());
		btn_note.setOnClickListener(new QuestionOnClickListener());
		
		if(!debug){
			btn_debug.setVisibility(View.INVISIBLE);
			btn_note.setVisibility(View.INVISIBLE);
		}
		
		/** State onclick function use here*/
		img_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CertainState.onClick();
			}
		});
		
		msgBox = new NoteDialog3(testFragment, main_layout);
		

		img_help.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				
				ClickLog.Log(ClickLogId.TEST_TUTORIAL_BUTTON);
				
				Intent intent = new Intent();
				intent.setClass(activity, HelpActivity.class); // tmp modify
				startActivity(intent);
			}
		});
			
		img_help.setOnTouchListener(new ScaleOnTouchListener());
		//For Testing Function
		
		/*
		if(resultState == NoteDialog2.STATE_NOTE){
			img_btn.setOnClickListener(null);
			img_btn.setEnabled(false);
			msgBox.initialize();
			msgBox.show();
		}*/
		return view;
	}
	
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
			
			img_water1.setImageResource(R.drawable.saliva1_no);
			img_water2.setImageResource(R.drawable.saliva2_no);
			img_water3.setImageResource(R.drawable.saliva3_no);
			
			test_msg.setText("");
			img_btn.setEnabled(true);
			label_btn.setText("開始");
			label_btn.setTextSize(28);
			
			//String styledText = "請點選<font color='#f2a6a3'>開始</font>進行測試";
			//label_subtitle.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
			
			label_subtitle.setText("請點選開始進行測試");
			label_title.setText("測試尚未開始");
			MainActivity.getMainActivity().enableTabAndClick(true);
			
			//MainActivity.getMainActivity().setTimers(); //for Test
		}
		@Override
		public void onClick(){
			
			ClickLog.Log(ClickLogId.TEST_START_BUTTON);
			
			first_connect =false;
			first_voltage =false;
			ble_pluginserted =false;
			MainActivity.getMainActivity().enableTabAndClick(false);
			reset();
			
			
			
			if(isDeveloper){
				setState(new DoneState());
			}
			else {
				start_test = true;
				setState(new ConnState());
			}
			
			/*
			start_test = true;
			setState(new ConnState());*/
		}
	}
	
	private class FailState extends TestState{
		private String err_msg;
		private int err_id;
		/**
		 * A Fail state with error msg
		 * @param _err_msg
		 */
		public FailState(String _err_msg){ //TODO set subtitle
			err_msg = _err_msg;
		}
		
		public FailState(int _err_id){ //TODO set subtitle
			err_id = _err_id;
		}
		@Override
		public void onStart(){
			state = FAIL_STATE;
			failedReason =  err_msg;
			if( TDP!= null ){
				TDP.startTestDetail(cassetteId, failedState, firstVoltage,
						secondVoltage, devicePower, colorReading,
		                connectionFailRate, failedReason);
			}
			
			test_msg.setText("");
			label_btn.setText("確認");
			label_subtitle.setText("");
			label_title.setText(err_msg);
			img_btn.setEnabled(true);
			
			water_layout.setVisibility(View.INVISIBLE);
			
			//cameraLayout.setVisibility(View.INVISIBLE);
			cameraRecorder.pause();//TODO: Camera Resource Release
			
			stopDueToInit();
			MainActivity.getMainActivity().enableTabAndClick(true);
			//if(ble != null)
				//ble.bleDisconnect();
		}
		@Override
		public void onClick(){ 
			ClickLog.Log(ClickLogId.TEST_END_BUTTON);
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
			if(goThroughState){
				updateInitState(Tester._BT);
			}
			else{
				openSensorMsgTimer = new OpenSensorMsgTimer();
				openSensorMsgTimer.start();
			}
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
			label_btn.setTypeface(digitTypefaceBold);
			label_btn.setTextSize(40);
			label_subtitle.setText("請蓄積口水");
			img_btn.setEnabled(false);
			
			if(ble != null)
				ble.bleWriteState((byte)0x02);
			
			//if(	testCountDownTimer == null )
			testCountDownTimer = new TestCountDownTimer(COUNT_DOWN_SECOND);
			testCountDownTimer.start();
			
		}
		
		public void onExit(){
			if(	testCountDownTimer != null ){
				testCountDownTimer.cancel();
				testCountDownTimer = null;
			}
			label_btn.setTypeface(wordTypefaceBold);
			label_btn.setTextSize(28);
		}
	}
	
	private class CameraState extends TestState{

		@Override
		public void onStart(){
			
			state = CAMERA_STATE;
						
			soundPool.play(preview_audio_id, 1.0F, 1.0F, 0, 0, 1.0F);
			Log.d("Main", "Enter Stage1");
			
			cameraRecorder.start();
			

			label_btn.setText("");
			label_subtitle.setText("請將臉對準中央，並吐口水於管中");
			label_title.setText("請吐口水");
			Random rand = new Random();
			int idx = rand.nextInt(test_guide_msg.length);
			test_msg.setText(test_guide_msg[idx]);
			
			
			water_layout.setVisibility(View.VISIBLE);
			//ble.bleWriteState((byte)3);
			
			//img_face.setVisibility(View.VISIBLE);
			cameraCountDownTimer = new CameraCountDownTimer(CAMERATIMEOUT);
			cameraCountDownTimer.start();
			
			
			/*
			if(ble != null)
				ble.bleWriteState((byte)2);*/

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
			img_bg.setVisibility(View.INVISIBLE);
			img_ac.setVisibility(View.VISIBLE);
			img_btn.setVisibility(View.INVISIBLE);
			//cameraLayout.setVisibility(View.VISIBLE);
			
			img_water1.setImageResource(R.drawable.saliva1_yes);
			img_water2.setImageResource(R.drawable.saliva2_yes);
			
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
			
			
			cameraCountDownTimer = new CameraCountDownTimer(CAMERATIMEOUT2);
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
			//img_bg.setVisibility(View.VISIBLE);
			img_ac.setVisibility(View.VISIBLE);
			img_btn.setVisibility(View.INVISIBLE);
			label_title.setText("口水確認中");
			label_subtitle.setText("請稍後");
			
			img_water3.setImageResource(R.drawable.saliva3_yes);
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
			
			
			img_bg.setVisibility(View.INVISIBLE);
			img_ac.setVisibility(View.INVISIBLE);
			img_btn.setVisibility(View.VISIBLE);
		}
	}
	
	private class DoneState extends TestState{
		@Override
		public void onStart(){
			state = DONE_STATE;
			
			if(ble != null)
				ble.bleDisconnect();
			//DBControl.inst.startTesting();
			stop();
			
			
			label_title.setText("測試完成!");
			label_subtitle.setText("");
			water_layout.setVisibility(View.VISIBLE);
			img_water3.setImageResource(R.drawable.saliva3_yes);
			
			
			test_done = true;
			
			updateDoneState(0);
			
			PreferenceControl.setLatestTestCompleteTime( (long)System.currentTimeMillis() );
			PreferenceControl.setCheckResult( true );
			
			MainActivity.getMainActivity().enableTabAndClick(false);
			MainActivity.getMainActivity().setTimers();
			
			/*
			try {
				 Thread.sleep(2000);
			} catch (InterruptedException e) {
			     Log.d(TAG,"error");  
			}*/
			
			img_btn.setOnClickListener(null);
			img_btn.setEnabled(false);
			msgBox.initialize();
			
			Handler handler=new Handler();
			handler.postDelayed(runnable, 1000);
			//msgBox.show();
			
			//
			Intent startIntent =  new  Intent( getActivity() , ResultService3. class );  
			getActivity().startService(startIntent);
			
			//startActivity(new Intent(, EventCopeSkillActivity.class));
			//startActivity(new Intent(activity, NoteActivity.class));
			//setState(new IdleState());
		}
	}
	
	Runnable runnable=new Runnable(){
		   @Override
		   public void run() {
			   msgBox.show();
		   } 
	};
	
	@SuppressLint("HandlerLeak")
	private class ChangeTabsHandler extends Handler {
		public void handleMessage(Message msg) {
			//MainActivity.getMainActivity().enableTabAndClick(true);
			//MainActivity.getMainActivity().changeTab(1, MainActivity.ACTION_QUESTIONNAIRE);
			//MainActivity.getMainActivity().setNotePage();
			
		}
	}
	
	
	
	
	private void setStorage() {
		File dir = MainStorage.getMainStorageDirectory();

		mainDirectory = new File(dir, String.valueOf(timestamp));
		if (!mainDirectory.exists())
			if (!mainDirectory.mkdirs()) {
				return;
			}
		
		
		TDP = new TestDataParser2(timestamp);  //For testing Function, need removal
		//TDP.start();
		
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
			ble = new BluetoothLE2( testFragment , PreferenceControl.getDeviceId()); // default "ket_000";
			
			Log.d(TAG, PreferenceControl.getDeviceId());
			//PreferenceControl.getDeviceId()
		}
		if(!is_connect)
			ble.bleConnect();
		
		ble.bleWriteState((byte)0x01);
		// initialize camera task
		cameraInitHandler = new CameraInitHandler(this, cameraRecorder);
		cameraInitHandler.sendEmptyMessage(0);
	}
	
	
	public void writeQuestionFile(int day, int timeslot, int type, int items, int impact, String description) {
		if( questionFile!= null )
			questionFile.write(day, timeslot, type, items, impact, description);
		
		if( TDP!= null ){
			//TDP.startAddNote();
			//TDP.getQuestionResult2(textFile)
			TDP.startAddNote3(1, day, timeslot, type, items, impact, description);
		}
	}

	//release resource
	public void stop() { 
		
		if (cameraRecorder != null)
			cameraRecorder.close();
		
		//first_connect = false;
		//first_voltage = false;
		//in_stage1 = false;
		//test_done = false;
		
		if(ble!=null){
			//is_connect = false;
			ble.bleDisconnect();
			ble = null;
		}
		
		
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
		
		
		if (changeTabsHandler != null) {
			changeTabsHandler.removeMessages(0);
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
			
			//while(!is_connect)
			//while(is_connect)
			ble.bleDisconnect(); // 原本註解
			//is_connect = false;
			//ble = null; 
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
		ClickLog.Log(ClickLogId.TEST_LEAVE);
		stop();
		super.onPause();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		ClickLog.Log(ClickLogId.TEST_ENTER);
		// dismiss sleep
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		checkDebug(is_debug);//PreferenceControl.isDebugMode());
		checkPreference();
		//reset();
		long curTime = System.currentTimeMillis();
		long testTime = PreferenceControl.getLatestTestCompleteTime();
		long pastTime = curTime - testTime;
		int note_state = PreferenceControl.getAfterTestState();
		
		
		
		if(note_state == msgBox.STATE_TEST){
			setState(new IdleState());
		}
		else if(PreferenceControl.getCheckResult() && pastTime < MainActivity.WAIT_RESULT_TIME){ //還沒察看結果且時間還沒到
			img_btn.setOnClickListener(null);
			img_btn.setEnabled(false);
			
			msgBox.initialize();
			msgBox.show();			
			if(note_state == msgBox.STATE_KNOW)
				msgBox.knowingSetting();
			else if(note_state == msgBox.STATE_COPE)
				msgBox.copingSetting();
		}
		else if(PreferenceControl.getCheckResult() && pastTime > MainActivity.WAIT_RESULT_TIME){//還沒察看結果且時間到了
			img_btn.setOnClickListener(null);
			img_btn.setEnabled(false);
			msgBox.initialize();
			msgBox.show();
			
			if(note_state == msgBox.STATE_KNOW)
				msgBox.knowingSetting();
			else if(note_state == msgBox.STATE_COPE)
				msgBox.copingSetting();
			

			msgBox.setResult();
			
		}
		else{
			setState(new IdleState());
		}
		
		LoadingDialogControl.dismiss();
	}
	
	
	private void reset() {
		
		first_connect = false;
		first_voltage = false;
		in_stage1 = false;
		test_done = false;
		camera_initial=false;
		
		cassetteId ="";
		failedState=0;
		firstVoltage=0;
		secondVoltage=0;
		devicePower=0;
		colorReading=0;
		connectionFailRate=0;
		failedReason=""; 

		timestamp = System.currentTimeMillis();
		voltage_count = 0;
		//setGuideMessage(R.string.test_guide_reset_top,R.string.test_guide_reset_bottom);

		PreferenceControl.setUpdateDetectionTimestamp(timestamp);
		//long ts = PreferenceControl.getUpdateDetectionTimestamp();
		Log.d(TAG1,""+timestamp);
		
		setStorage();
		
		
		cameraRecorder = new CameraRecorder(this, imgFileHandler);
		cameraRunHandler = new CameraRunHandler(cameraRecorder);
		
		

		//prev_drawable_time = -1;

		for (int i = 0; i < 3; ++i)
			INIT_PROGRESS[i] = DONE_PROGRESS[i] = false;
	}
	
	private void reset2() { //for debug use
		
		first_connect = false;
		first_voltage = false;
		in_stage1 = false;
		test_done = false;
		camera_initial=false;

		timestamp = System.currentTimeMillis();
	
		//setGuideMessage(R.string.test_guide_reset_top,R.string.test_guide_reset_bottom);

		PreferenceControl.setUpdateDetectionTimestamp(timestamp);

		setStorage();
		
		

	}
	
	private class OpenSensorMsgTimer extends CountDownTimer {

		public OpenSensorMsgTimer() {
			super(5000, 50);
		}

		@Override
		public void onFinish() {
			showDebug(">Try to start the device");
			//startConnection();
			failedState = state;
			setState(new FailState("請開啟檢測器"));
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	}
	
	
	//each state timeout
	private class CameraCountDownTimer extends CountDownTimer {
		
			private int count = 0;
			
			public CameraCountDownTimer(int timeout) {
				super(timeout*1000, 500);
			}

			@Override
			public void onFinish() {
				img_face.setVisibility(View.INVISIBLE);
				Log.i(TAG3, "FINISH");
				
				if(state == CAMERA_STATE && goThroughState){
					setState(new Stage2State());
				}
				else if(state == NOTENOUGH_STATE && goThroughState){
					setState(new RunState());
				}
				else{
					if(state == CAMERA_STATE){
						failedState = state;
						setState(new FailState("測試超時"));
					}
					else if (state == NOTENOUGH_STATE){ // 判斷第二個電極是否通過
						if(voltage < SECOND_VOLTAGE_THRESHOLD){
							secondVoltage = voltage;
							setState(new RunState());
						}
						else{
							failedState = state;
							setState(new FailState("測試失敗,請更換試紙匣後重試"));
						}
					}
				}
				//cameraRecorder.closeSuccess();
			}

			@Override
			public void onTick(long millisUntilFinished) {
				
				//if(millisUntilFinished % 2500 == 0)
				if(state == NOTENOUGH_STATE && count%2 == 0){
					if(second_voltage){
						label_title.setText("口水量已足夠");
						label_subtitle.setText("");
						img_water3.setImageResource(R.drawable.saliva3_yes);
					}
					else
						label_title.setText("請在"+millisUntilFinished/1000 +"秒內再吐一口水");
					
				}
				
				if(count % 5 == 0)
					cameraRunHandler.sendEmptyMessage(0);
				
				
				count ++;
			}	
	}
	
	//each state timeout
	private class TimeoutCountDownTimer extends CountDownTimer {
		
		public TimeoutCountDownTimer() {
			super(TIMEOUT_SECOND*1000, 1000);
		}

		@Override
		public void onFinish() {
			if(is_timeout){
				failedState = state;
				setState(new FailState("測試超時"));
			}
			
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
			img_ac.setVisibility(View.INVISIBLE);
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
			if(goThroughState)
				setState(new CameraState());
			else{
				if(voltage < FIRST_VOLTAGE_THRESHOLD)
					if(collectdata){
						Toast.makeText(activity, "Collect Data Mode", Toast.LENGTH_SHORT).show();	
					}
					else{
						setState(new CameraState());
					}
				else{
					failedState = state;
					setState(new FailState("請更換試紙匣後重試"));
				}
			}
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
			if(goThroughState){
				setState(new NotEnoughSavilaState() );
			}
			else{
				in_stage2=true;
				if( voltage < SECOND_VOLTAGE_THRESHOLD ){
					img_water3.setImageResource(R.drawable.saliva3_yes);
					setState(new DoneState());
				}
				else{
					setState(new NotEnoughSavilaState() );
				}
			}
			
		}

		@Override
		public void onTick(long millisUntilFinished) {
			
			//cameraRunHandler.sendEmptyMessage(0);	
			img_ac.setImageResource(ELECTRODE_RESOURCE[ptr++]);		
			
		}
	}
  

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ble.onBleActivityResult(requestCode, resultCode, data);
    }
    
    
    //=======BLE callback function 
    
    @Override
    public void bleNotSupported() {
    	  Toast.makeText(activity, "BLE not support", Toast.LENGTH_SHORT).show();
    	  failedState = state;
    	  setState(new FailState("裝置不支援"));
//        this.finish();
    }

    @Override
    public void bleConnectionTimeout() {
    	Log.i(TAG, "connect timeout");
        Toast.makeText(activity, "BLE connection timeout", Toast.LENGTH_SHORT).show();
        failedState = state;
        if(!goThroughState)
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
        Toast.makeText(activity, "BLE disconnected", Toast.LENGTH_SHORT).show();
        //setState(new FailState("連接中斷"));
        
        is_connect = false;
        
        if(state != IDLE_STATE && state!= FAIL_STATE && state!= DONE_STATE && !goThroughState){
        	failedState = state;
        	setState(new FailState("連接中斷"));
        }
        else if (state == STAGE2_STATE){
        	failedState = state;
        	setState(new FailState("測試完成")); //temporary solution
        }
        //if(ble != null) {
        //    ble = null;
        //}
    }

    @Override
    public void bleWriteStateSuccess() {
        Log.i(TAG, "BLE ACTION_DATA_WRITE_SUCCESS");
        Toast.makeText(activity, "BLE write state success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void bleWriteStateFail() {
        Log.i(TAG, "BLE ACTION_DATA_WRITE_FAIL");
        Toast.makeText(activity, "BLE writefstate fail", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void bleNoPlug() {
        Log.i(TAG, "No test plug");
    	
        if(state != IDLE_STATE && !goThroughState){
        	Toast.makeText(activity, "No test plug", Toast.LENGTH_SHORT).show();
        	failedState = state;
        	setState(new FailState("請將試紙匣插入裝置"));
        }
    }

    @Override
    public void blePlugInserted(byte[] plugId) {
        //Log.i(TAG, "Test plug is inserted");
        ble_pluginserted = true;
        cassetteId = "tmp_id"; //TODO: set cassetteId in here.
        //check ID here
        if( state == CONN_STATE )
        	updateInitState(Tester._BT);
        
        /*)
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
    		//color[i/2] = colorReadings[i]+colorReadings[i+1]*256;
    		
    		color[i/2] = ((colorReadings[i+1] & 0xFF) << 8) | (colorReadings[i] & 0xFF);
    		str1 = str1+ " " + String.valueOf(color[i/2]);
    	}
    	//ColorDetect2.colorDetect(color);
    	//feature = ColorDetect2.colorDetect2(color);
    	
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
		String str2 = String.format("%02x", header & 0xff);
		
		String str3= System.currentTimeMillis()+" "+state+" "+str2+" "+str;
		Log.i(TAG2, str3);
		
		showDebug(">"+str3);
		writeToVoltageFile(str3+"\n");
		
		//if(state == CAMERA_STATE && state == STAGE2_STATE && state == NOTENOUGH_STATE && state == RUN_STATE)
		//	writeToVoltageFile(str+str2+"\n");
		//if(state == RUN_STATE && goThroughState)
		//	setState(new Stage2State());
		
		if(state == CAMERA_STATE && voltage > FIRST_VOLTAGE_THRESHOLD ){
			firstVoltage = voltage;
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
			
			
		//TDP.start();
		//UploadService.startUploadService(activity);

		
	}


	@Override
	public void stopByFail(int fail) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public FrameLayout getPreviewFrameLayout() {
		
		//return cameraLayout;
		return (FrameLayout)view.findViewById(R.id.cameraLayout);
	
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
	
	private class QuestionOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if(mainDirectory == null)
				reset2();
			
			img_btn.setOnClickListener(null);
			img_btn.setEnabled(false);
			msgBox.initialize();
			msgBox.show();	
		}		
	}
	
	private void checkPreference(){
		FIRST_VOLTAGE_THRESHOLD = PreferenceControl.getVoltag1(); 
		SECOND_VOLTAGE_THRESHOLD= PreferenceControl.getVoltag2();
		CAMERATIMEOUT = PreferenceControl.getVoltageCountDown();
		CAMERATIMEOUT2 = PreferenceControl.getVoltage2CountDown();
		isDeveloper = PreferenceControl.isDeveloper();
		debug = PreferenceControl.isDebugMode();
		collectdata = PreferenceControl.getCollectData();
		
		Log.d(TAG1, "V1: "+FIRST_VOLTAGE_THRESHOLD+" V2: "+SECOND_VOLTAGE_THRESHOLD+" TIMEOUT: "+CAMERATIMEOUT);
		Log.d(TAG1, "isDeveloper: "+isDeveloper+" debug: "+debug+" Did: "+PreferenceControl.getDeviceId());
	}
	
	
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

	@Override
	public void resetView() {
		
		
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
	@Override
	public void displayCurrentId(String id) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void bleTakePictureSuccess(Bitmap bitmap) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void bleTakePictureFail(float dropRate) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void imgDetect(Bitmap bitmap) {
		// TODO Auto-generated method stub
		
	}

}
