package com.ubicomp.ketdiary.noUse;

import java.io.File;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ubicomp.ketdiary.AlarmService;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.BluetoothLE.BluetoothLE_old;
import com.ubicomp.ketdiary.BluetoothLE.BluetoothListener;
import com.ubicomp.ketdiary.R.id;
import com.ubicomp.ketdiary.R.layout;
import com.ubicomp.ketdiary.color.ColorDetect2;
import com.ubicomp.ketdiary.file.ColorRawFileHandler;
import com.ubicomp.ketdiary.file.MainStorage;
import com.ubicomp.ketdiary.file.VoltageFileHandler;
import com.ubicomp.ketdiary.system.PreferenceControl;
//import com.ubicomp.ketdiary.dialog.NoteDialog;

/** Event Cope Skill Page
 * 
 * @author Andy Chen
 *
 */
public class EventCopeSkillActivity extends Activity{
	
	private static final String TAG = "Bluetooth_Event"; 
	
	/** self activity*/
	private Activity activity;
	
	private TextView tv_timer;
	private TextView tv_tips;
	private Button btn_know;
	private Button btn_tipup;
	private Button btn_tipdown;
	
	private BluetoothLE_old ble = null;
	private TestDataParser testDataParser;
	private ColorRawFileHandler colorRawFileHandler;
	private VoltageFileHandler voltageFileHandler;
	//private long timestamp = 0;
	private File mainDirectory;
	
	private boolean first_connect=true;
	private long startTime;
	private Handler handler2 = new Handler();
	private Handler handler3 = new Handler();
	private long timeout = 2000;
	private boolean write_success = false;
	private boolean start_write = false;
	
	//Debug
	private ScrollView debugScrollView;
	private EditText debugMsg;
	//private ChangeMsgHandler msgHandler;
	private TextView debugBracValueView;
	private Button btn_debug, btn_note;
	private boolean is_debug = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//new NoteDialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen).show();
		setContentView(R.layout.activity_qtip);
		activity = this;
		tv_timer = (TextView)findViewById(R.id.qtip_tv_timer);
		tv_tips = (TextView)findViewById(R.id.qtip_tv_tips);
		btn_know = (Button)findViewById(R.id.qtip_btn_know);
		btn_tipup = (Button)findViewById(R.id.qtip_btn_tipup);
		btn_tipdown = (Button)findViewById(R.id.qtip_btn_tipdown);
		
		btn_debug = (Button)findViewById(R.id.debug_button_1);
		btn_note =( Button)findViewById(R.id.debug_button_2);
		debugScrollView = (ScrollView)findViewById(R.id.debug_scroll_view);
		
		debugMsg = (EditText)findViewById(R.id.debug_msg);
		//btn_debug.setOnClickListener(new DebugOnClickListener());
		//btn_note.setOnClickListener(new QuestionOnClickListener());
		
		
		btn_know.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btn_know.setVisibility(8);
				btn_tipup.setVisibility(0);
				btn_tipdown.setVisibility(0);
				tv_tips.setText(DBTip.inst.getTip());
			}
		});;
		
		btn_tipup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//tv_tips.setText(DBTip.inst.getTip());
				start_write = true;
			}
		});
		
		btn_tipdown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//tv_tips.setText(DBTip.inst.getTip());
				bleConnection();
			}
		});
		
		
		startTime = System.currentTimeMillis();
		
		File dir = MainStorage.getMainStorageDirectory();
		mainDirectory = new File(dir, String.valueOf(startTime));
		if (!mainDirectory.exists())
			if (!mainDirectory.mkdirs()) {
				return;
			}
		colorRawFileHandler = new ColorRawFileHandler(mainDirectory,String.valueOf(startTime));
		voltageFileHandler = new VoltageFileHandler(mainDirectory,String.valueOf(startTime));
		
		
		
		//timestamp = System.currentTimeMillis();
		

		
		
		
		
		//設定定時要執行的方法
		handler2.removeCallbacks(updateTimer);
		//new Timer().schedule(task, 0, 1000);
		handler2.postDelayed(updateTimer, 0);
		
		//設定定時要執行的方法
		handler3.removeCallbacks(updateTimer2);
		//new Timer().schedule(task, 0, 1000);
		handler3.postDelayed(updateTimer2, 0);
	}
	
	private Runnable updateTimer = new Runnable() {
		public void run() {
			
			
			
			long spentTime = System.currentTimeMillis() - startTime;
			
			spentTime = timeout - spentTime;
			
			long minius = (spentTime/1000)/60;
			long seconds = (spentTime/1000) % 60;
			
			tv_timer.setText(minius+":"+seconds);
			if(ble!=null)
				ble.bleWriteState((byte)3);
			
			handler2.postDelayed(this, 1000);
			
			if(spentTime < 0)
				handler2.removeCallbacks(updateTimer);
			/*
			final TextView time = (TextView) findViewById(R.id.timer);
			Long spentTime = System.currentTimeMillis() - startTime;
		            //計算目前已過分鐘數
		    
		            //計算目前已過秒數
		    
		    time.setText(minius+":"+seconds);
		    handler.postDelayed(this, 1000);*/
		}
    };
    
    private Runnable updateTimer2 = new Runnable() {
		public void run() {
			bleConnection();
			Toast.makeText(activity, "Start writing File", Toast.LENGTH_SHORT).show();
			//handler2.postDelayed(this, 1000);
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
    public void onResume(){
    	super.onResume();

    }
    
    private void bleConnection(){
    	if(ble == null) {
			//ble = new BluetoothLE(this, "ket_020");
			ble = new BluetoothLE_old(this, PreferenceControl.getDeviceId());//PreferenceControl.getDeviceId();
		}
		ble.bleConnect();
		//ble.bleWriteState((byte)1);
		
		
    }
	
	private TimerTask task = new TimerTask(){
		@Override
		public void run() {
			testDataParser = new TestDataParser(System.currentTimeMillis());
			testDataParser.start();
			//UploadService.startUploadService(that);
			
			handler.sendEmptyMessage(1);
		}
	};
	
	private Handler handler = new Handler(){
		long ms = 600;
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch(msg.what){
			case 1:
				ms -= 1;
				long minutes = ms/60;
				long second = ms%60;
				tv_timer.setText(String.valueOf(minutes) + ":" + String.valueOf(second));
				
				
				if(ms<=0){
					Intent a_intent = new Intent(activity, AlarmService.class);
					activity.startService(a_intent);
				}
				
				break;
			}
		}
	};
	
	public void onPause() {
		
		stop();
		super.onPause();
	}
	
	
	public void stop() { 
		if(ble!=null)
			ble.bleDisconnect();
		
		if (colorRawFileHandler != null) {
			colorRawFileHandler.close();
			colorRawFileHandler = null;
		}
		
		if (voltageFileHandler != null) {
			voltageFileHandler.close();
			voltageFileHandler = null;
		}
	}
	
	@Override  
	public void onBackPressed() {
	    super.onBackPressed(); 
		//App.onTerminate();
	    // Do extra stuff here
	}
	
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ble.onBleActivityResult(requestCode, resultCode, data);
    }
	
 
}
