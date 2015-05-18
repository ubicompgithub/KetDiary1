package com.ubicomp.ketdiary;

import ubicomp.soberdiary.main.MainActivity;
import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.system.config.PreferenceControl;
import ubicomp.soberdiary.test.bluetooth.Bluetooth;
import ubicomp.soberdiary.test.bluetooth.BluetoothACVMMode;
import ubicomp.soberdiary.test.bluetooth.BluetoothAVMMode;
import ubicomp.soberdiary.test.bluetooth.SimpleBluetooth;
import ubicomp.soberdiary.test.camera.CameraRecorder;
import ubicomp.soberdiary.test.camera.CameraRunHandler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubicomp.ketdiary.camera.CameraPreview;
import com.ubicomp.ketdiary.db.DBControl;
import com.ubicomp.ketdiary.test.bluetoothle.BluetoothLE;
import com.ubicomp.ketdiary.test.bluetoothle.BluetoothLEWrapper;
import com.ubicomp.ketdiary.test.bluetoothle.DebugBluetoothLE;


@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class TestActivity extends Activity {

	private TextView label_btn, label_subtitle, label_title;
	private ImageView img_bg, img_ac, img_btn;
	
	/** self activity*/
	Activity that;
	
	/** Camare variables */
	private Camera mCamera = null;
	private CameraPreview mCamPreview;
	private FrameLayout cameraLayout;
	//TODO: private ImageView camera_mask;
	
	/** Sound playing variables */
	private SoundPool soundPool;
	
	/** Sound id*/
	private int count_down_audio_id;
	private int preview_audio_id;
	
	
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
	
	TestState CertainState = null;
	BluetoothLEWrapper bluetoothle = null;

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
			if(bluetoothle != null)
				bluetoothle.Close();
		}
		@Override
		public void onClick(){
			setState(new IdleState());
		}
	}
	
	private class ConnState extends TestState{
		private volatile boolean is_conn = false;
		@Override
		public void onStart(){
			label_btn.setText("...");
			label_subtitle.setText("");
		    if(DBControl.inst.getIsDev())
		    	bluetoothle = new DebugBluetoothLE();
		    else	
		    	bluetoothle = new BluetoothLE(that, DBControl.inst.getDeviceID());
			
			label_title.setText("準備中....");
			ToConn();
		}
		
		public int conn_time = 0;
		
		/**
		 * Try many time to connect
		 */
		public void ToConn(){
			Log.d("FORTEST", "Conn" + String.valueOf(conn_time));
			conn_time += 1;
			if(is_conn) return;
			if(conn_time >= 10){
				if(bluetoothle.isConnected() == false){
					setState(new FailState("BLE連線逾時"));
				}else{
	        		bluetoothle.ReturnToInitState();
	        		is_conn = true;
	        		setState(new PlugCheckState());
				}
				return;
			}
			new CountDownTimer(6000, 6000){
				@Override
		        public void onTick(long ms){}
		        @Override
				public void onFinish() {
		        	if(is_conn) return;
		        	if(bluetoothle.isConnected()){
		        		bluetoothle.ReturnToInitState();
		        		is_conn = true;
		        		setState(new PlugCheckState());
		        	}else{
						bluetoothle.Close();
						bluetoothle = new BluetoothLE(that, DBControl.inst.getDeviceID());
		        		ToConn();
		        	}
		        }
		    }.start();
		}
	}
	
	private class PlugCheckState extends TestState{
		public volatile boolean plug_ok = false;
		@Override
		public void onStart(){
			label_btn.setText("");
			label_subtitle.setText("");
			label_title.setText("");
			new CountDownTimer(1000, 200){
				@Override
				public void onTick(long millisUntilFinished) {
					if(plug_ok) return;
					if(bluetoothle.getState() >= BluetoothLE.STATE_EMBED){
						setState(new CheckIDState());
						plug_ok = true;
					}
				}
				@Override
				public void onFinish() {
					if(plug_ok) return;
					if(bluetoothle.getState() >= BluetoothLE.STATE_EMBED){
						setState(new CheckIDState());
						plug_ok = true;
					}else{
						setState(new FailState("請檢查試紙匣是否有插入"));
					}
				}
			}.start();
			
		}
	}
	
	private class CheckIDState extends TestState{
		@Override
		public void onStart(){
			label_btn.setText("");
			label_subtitle.setText("");
			label_title.setText("");
			// TODO: to check id
			boolean tester = false;
			if(tester)
				setState(new FailState("試紙匣ID錯誤"));
			else
				setState(new FiveSecondState());
		}
	}
	
	private class FiveSecondState extends TestState{
		private volatile int count_down;
		@Override
		public void onStart(){
			Log.d("Main", "Enter FiveSecond");
			label_btn.setText("5");
			label_subtitle.setText("請蓄積口水");
			count_down = 5;
			new CountDownTimer(5000, 1000){
				@Override
				public void onTick(long ms) {
					count_down--;
					soundPool.play(count_down_audio_id, 1.0F, 1.0F, 0, 0, 1.0F);
					label_btn.setText(String.valueOf(count_down));
				}
				@Override
				public void onFinish() {
					bluetoothle.SendStartMsg();
					setState(new Stage1State());
				}
			}.start();
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
			mCamera = Camera.open(cameraId);
			mCamPreview.set(that, mCamera);
			cameraLayout.setVisibility(0);
			
			// TODO: camera_mask.bringToFront();
			
			label_btn.setText("");
			label_subtitle.setText("請將臉對準中央，並吐口水");
			label_title.setText("請吐口水");
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
		    test_timer.start();
		}
		@Override
		public void onExit(){
			move_to_stage2 = true;
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
		private int ptr;
		private int[] pids;
		@Override
		public void onStart(){
			label_btn.setText("");
			img_bg.setVisibility(0);
			img_ac.setVisibility(0);
			img_btn.setVisibility(4);
			ptr = 0;
			pids = new int[5];
			
			/** Load id of progress bar*/
			pids[0] = R.drawable.test_progress_1;
			pids[1] = R.drawable.test_progress_2;
			pids[2] = R.drawable.test_progress_3;
			pids[3] = R.drawable.test_progress_4;
			pids[4] = R.drawable.test_progress_5;
			img_ac.setImageResource(pids[0]);
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
		    }.start();
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
			bluetoothle.Close();
			DBControl.inst.startTesting();
			startActivity(new Intent(that, EventCopeSkillActivity.class));		
		}
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
	/*
	private void reset() {
		SimpleBluetooth.closeConnection();

		timestamp = System.currentTimeMillis();
		MainActivity.getMainActivity().closeTimers();
		setGuideMessage(R.string.test_guide_reset_top,
				R.string.test_guide_reset_bottom);

		if (MainActivity.getMainActivity().canUpdate())
			PreferenceControl.setUpdateDetectionTimestamp(timestamp);
		else
			PreferenceControl.setUpdateDetectionTimestamp(0);

		setStorage();
		locationManager = (LocationManager) activity
				.getSystemService(Context.LOCATION_SERVICE);
		cameraRecorder = new CameraRecorder(testFragment, imgFileHandler);

		cameraRunHandler = new CameraRunHandler(cameraRecorder);
		Boolean debug = PreferenceControl.isDebugMode();
		Boolean debug_type = PreferenceControl.debugType();

		prev_drawable_time = -1;

		if (debug) {
			if (debug_type)
				bt = new BluetoothAVMMode(testFragment, testFragment,
						cameraRunHandler, bracFileHandler, bracDebugHandler);
			else
				bt = new BluetoothACVMMode(testFragment, testFragment,
						cameraRunHandler, bracFileHandler, bracDebugHandler);
		} else
			bt = new Bluetooth(testFragment, testFragment, cameraRunHandler,
					bracFileHandler, true);
		for (int i = 0; i < 3; ++i)
			INIT_PROGRESS[i] = DONE_PROGRESS[i] = false;
	}*/
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //參數1:群組id, 參數2:itemId, 參數3:item順序, 參數4:item名稱
        menu.add(0, 0, 0, "說明");
        menu.add(0, 1, 1, "離開");
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
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
