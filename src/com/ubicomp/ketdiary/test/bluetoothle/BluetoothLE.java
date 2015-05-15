package com.ubicomp.ketdiary.test.bluetoothle;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.db.DBControl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class BluetoothLE extends BluetoothLEWrapper{
	
    private static final UUID SERVICE4_CONFIG_CHAR = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
	
	private volatile boolean is_conn;
	
	private String ble_name;
    private BluetoothAdapter ble_adapter;
    volatile BluetoothDevice ble_device = null;

    private BLEService ble_service = null;
    
    private String TAG = "BLE Wrapper";
    
    private Activity activity;
    
    BluetoothGattCharacteristic write_chara;
    
    public static int STATE_NULL = -1;
    public static int STATE_NO_EMBED = 0;
    public static int STATE_EMBED = 1;
    public static int STATE_NO_SALIVA = 2;
    public static int STATE_1PASS = 3;
    public static int STATE_2PASS = 4;
    public static int STATE_COLOR = 5;
    public int _state = STATE_NULL;
    
    public class Color{ int R, G, B, A; }
    
    public static Color color1, color2;
    
	public BluetoothLE(Activity _activity, String _ble_name){
		is_conn = false;
		ble_name = _ble_name;
		activity = _activity;
		final BluetoothManager bluetoothManager = 
		        (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
		ble_adapter = bluetoothManager.getAdapter();
		// TODO: Error handling
        Log.d(TAG, "start le scan");
        ble_adapter.startLeScan(scan_cb);
        new CountDownTimer(10000, 10000){
        	@Override
        	public void onTick(long ms){}
        	@Override
        	public void onFinish(){
            	ble_adapter.stopLeScan(scan_cb);
        	}
        };
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BLEService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BLEService.ACTION_PROTOCOL_DATA_AVAILABLE);  // Blue Zhong
        intentFilter.addAction(BLEService.ACTION_WRITE_PROTOCOL_DATA_AVAILABLE);  // Blue Zhong
        return intentFilter;
    }
	
	// Code to manage Service lifecycle.
    private ServiceConnection srv_conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            ble_service = ((BLEService.LocalBinder) service).getService();
            Log.d(TAG, "Enter service conn");
            
            if (!ble_service.initialize(activity)) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                activity.finish();
            }
            Log.d(TAG, "Enter service conn");
            // Automatically connects to the device upon successful start-up initialization.
            for(int lx = 0;lx < 10;lx++)
            	if(ble_service.connect(ble_device)){
            		Log.d(TAG, "connect");
            		break;
            	}
            
        }
    
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            ble_service = null;
        }
    };
	
	private BluetoothAdapter.LeScanCallback scan_cb =
	        new BluetoothAdapter.LeScanCallback() {
	    @Override
	    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
	        Log.d(TAG, device.getName() + "]");
	        Log.d(TAG, ble_name + "[");
	    	if(ble_name.equals(device.getName())){
	    		//Log.d(TAG, "[db]");
	        	ble_device = device;
	        	Intent gattServiceIntent = new Intent(activity, BLEService.class);
	            activity.bindService(gattServiceIntent, srv_conn, Context.BIND_AUTO_CREATE);
	            activity.registerReceiver(gatt_cb, makeGattUpdateIntentFilter());
	        	ble_adapter.stopLeScan(scan_cb);
	        	//Log.d(TAG, "[db2]");
	    	}
	    }
	};
	
	private final BroadcastReceiver gatt_cb = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        final String action = intent.getAction();
	        if(action.equals(BLEService.ACTION_GATT_CONNECTED)){
	            is_conn = true;
	        }else if(action.equals(BLEService.ACTION_GATT_DISCONNECTED)){
	            is_conn = false;
            }else if(action.equals(BLEService.ACTION_GATT_SERVICES_DISCOVERED)) {
	        	List<BluetoothGattService> gattServices = ble_service.getSupportedGattServices();
	        	ble_service.setNotify(gattServices.get(3).getCharacteristic(SERVICE4_CONFIG_CHAR));
	        }else if (action.equals(BLEService.ACTION_DATA_AVAILABLE)) {
            	byte[] data = intent.getByteArrayExtra(BLEService.EXTRA_DATA);
                String currentDeviceState = String.format("%02X ", data[0]);
                Log.d(TAG, "[" + currentDeviceState + "]");
                if(currentDeviceState.equals("FA ")){
                    // No connection
                	_state = STATE_NO_EMBED;
                    Log.i("FORTEST", "## No connection!");
                }
                else if(currentDeviceState.equals("FB ")){
                    // Connected
                	_state = STATE_EMBED;
                	//Write((byte)0x02);
                	Log.i("FORTEST", String.format("%02X ", data[1]));
                	Log.i("FORTEST", String.format("%02X ", data[2]));
                	Log.i("FORTEST", String.format("%02X ", data[3]));
                	Log.i("FORTEST", String.format("%02X ", data[4]));
                	Log.i("FORTEST", String.format("%02X ", data[5]));
                    Log.i("FORTEST", "## Connected!");
                }
                else if(currentDeviceState.equals("FC ")){
                    // No saliva
                	_state = STATE_NO_SALIVA;
                    Log.i("FORTEST", "## No saliva!");
                }
                else if(currentDeviceState.equals("FD ")){
                    // 1 pass, 2 not yet
                	_state = STATE_1PASS;
                    Log.i("FORTEST", "## 1 pass, 2 not yet!");
                }
                else if(currentDeviceState.equals("FE ")){
                    // 1 pass, 2 pass
                	_state = STATE_2PASS;
                    Log.i("FORTEST", "## 1 pass, 2 pass!");
                	//Write((byte)0x03);
                }
                else if(currentDeviceState.equals("FF ")){
                    // Color
                	_state = STATE_COLOR;
                    Log.i("FORTEST", "## Color!");
                	Write((byte)0x04);
                    int color_sensor0[] = new int[4];
                    int color_sensor1[] = new int[4];
                    for(int i=0; i<4; i++) {
                        color_sensor0[i] = data[(i*2)+2]*256 + data[i*2+1];
                        color_sensor1[i] = data[(i*2)+10]*256 + data[i*2+9];
                    }
                    Log.i("FORTEST", " "+color_sensor0[0]+" "+color_sensor0[1]+" "+color_sensor0[2]+" "+color_sensor0[3]+" "+color_sensor1[0]+" "+color_sensor1[1]+" "+color_sensor1[2]+" "+color_sensor1[3]);
                }
	        }
	    }
	};
	
	@Override
	public int getState(){
		return _state;
	}
	
	@Override
	public boolean isConnected(){
		return is_conn;
	}
	
	@Override
	public void Close(){
		if(ble_device != null){
			activity.unbindService(srv_conn);
            activity.unregisterReceiver(gatt_cb);
		}
	}
	
	@Override
	public void RetToInitState(){
		Write((byte)0x01);
	}
	
	@Override
	public void SendStartMsg(){
		Write((byte)0x02);
	}

	private int write_count;
	private byte write_byte_;
	private void Write(byte _write_byte){
		write_count = 0;
		write_byte_ = _write_byte;
		WriteLoop();
	}
	
	private void WriteLoop(){
		write_count+=1;
		if(write_count >= 5) return;
		long toap = ((int)Math.random()*49) + 100;
		new CountDownTimer(toap, toap){
			@Override
			public void onTick(long millisUntilFinished) {}
			@Override
			public void onFinish() {
				_Write(write_byte_);
				WriteLoop();
			}
		};
	}
	
	private boolean _Write(byte write_byte){
		boolean isWriteSuccess = false;
		List<BluetoothGattService> gattServices = ble_service.getSupportedGattServices();
		if (gattServices == null) return false;
        for (BluetoothGattService gattService : gattServices) {
        	List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic :
                    gattCharacteristics) {
                String uuid = gattCharacteristic.getUuid().toString();
                // Blue Zhong
                if( uuid.equals(BleUuuid.CHAR_BLE_PROTOCOL) ){
                    gattCharacteristic.setValue(new byte[] { write_byte });
                    ble_service.writeCharacteristic(gattCharacteristic);
                    isWriteSuccess = true;
                }
            }
        }
        return isWriteSuccess;
	}
	
	
}