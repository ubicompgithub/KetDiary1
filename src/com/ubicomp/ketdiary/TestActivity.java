package com.ubicomp.ketdiary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubicomp.ketdiary.camera.CameraPreview;
import com.ubicomp.ketdiary.db.DBControl;
import com.ubicomp.ketdiary.test.bluetoothle.BLEWrapper;
import com.ubicomp.ketdiary.test.bluetoothle.DBGWrapper;
import com.ubicomp.ketdiary.test.bluetoothle.Wrapper;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class TestActivity extends Activity {

	private TextView label_btn, label_subtitle, label_title;
	private ImageView img_bg, img_ac, img_btn;
	Activity that;
	
	private Camera mCamera = null;
	private CameraPreview mCamPreview;
	private FrameLayout cameraLayout;
	//TODO: private ImageView camera_mask;
	
	private SoundPool soundPool;
	
	private int count_down_audio_id;
	private int preview_audio_id;

	private class TestState{
		public void onStart(){return;}
		public void onExit(){return;}
		public void onClick(){return;}
	}
	
	TestState CertainState = null;
	Wrapper ble_wrapper = null;

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
		public FailState(String _err_msg){
			err_msg = _err_msg;
		}
		@Override
		public void onStart(){
			label_btn.setText("確認");
			label_subtitle.setText("");
			label_title.setText(err_msg);
			if(ble_wrapper != null)
				ble_wrapper.Close();
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
		    if(DBControl.inst.getIsDev(getApplicationContext()))
		    	ble_wrapper = new DBGWrapper();
		    else	
		    	ble_wrapper = new BLEWrapper(that);
			
			label_title.setText("準備中....");
			ToConn();
		}
		
		public int conn_time = 0;
		public void ToConn(){
			Log.d("FORTEST", "Conn" + String.valueOf(conn_time));
			conn_time += 1;
			if(is_conn) return;
			if(conn_time >= 10){
				if(ble_wrapper.isConn() == false){
					setState(new FailState("BLE連線逾時"));
				}else{
	        		ble_wrapper.RetToInitState();
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
		        	if(ble_wrapper.isConn()){
		        		ble_wrapper.RetToInitState();
		        		is_conn = true;
		        		setState(new PlugCheckState());
		        	}else{
						ble_wrapper.Close();
						ble_wrapper = new BLEWrapper(that);
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
					if(ble_wrapper.getState() >= BLEWrapper.STATE_EMBED){
						setState(new CheckIDState());
						plug_ok = true;
					}
				}
				@Override
				public void onFinish() {
					if(plug_ok) return;
					if(ble_wrapper.getState() >= BLEWrapper.STATE_EMBED){
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
					ble_wrapper.SendStartMsg();
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
			// Search for the front facing camera
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
		        	if(ble_wrapper.getState() >= BLEWrapper.STATE_1PASS){
		        		move_to_stage2 = true;
		        		setState(new Stage2State());
		        	}
		        }
		        public void onFinish() {
		        	if(move_to_stage2) return;
		        	if(ble_wrapper.getState() >= BLEWrapper.STATE_1PASS){
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
			Log.d("hi", "hi");
			label_btn.setText("");
			img_bg.setVisibility(0);
			img_ac.setVisibility(0);
			img_btn.setVisibility(4);
			ptr = 0;
			pids = new int[5];
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
		        	// prevent connection close
		        	if(ble_wrapper.getState() >= BLEWrapper.STATE_2PASS){
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
			DBControl.inst.startTesting(getApplicationContext());
			startActivity(new Intent(that, QTipActivity.class));		
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
		img_btn = (ImageView)findViewById(R.id.imageView1);
		cameraLayout = (FrameLayout)findViewById(R.id.cameraLayout);
		//TODO: camera_mask = (ImageView)findViewById(R.id.test_camera_mask);
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
		count_down_audio_id = soundPool.load(this, R.raw.short_beep, 1); 
		preview_audio_id = soundPool.load(this, R.raw.din_ding, 1);
		setState(new IdleState());
		
		img_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CertainState.onClick();
			}
		});
		that = this;
	}
	
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
    
    /* TODO: @Override
	public void onPause() {
		if(mCamera != null){
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		super.onPause();
	}*/
}
