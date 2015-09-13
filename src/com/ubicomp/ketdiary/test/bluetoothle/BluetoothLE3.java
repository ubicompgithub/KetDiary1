package com.ubicomp.ketdiary.test.bluetoothle;
import com.ubicomp.ketdiary.ResultService3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

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
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.MainActivity;
import com.ubicomp.ketdiary.data.file.MainStorage;
import com.ubicomp.ketdiary.system.PreferenceControl;

@SuppressLint("NewApi")
public class BluetoothLE3 {
	private static final String TAG = "BluetoothLE";

    // Write UUID
    public static final UUID SERVICE4_WRITE_STATE_CHAR1 = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    public static final UUID SERVICE4_WRITE_STATE_CHAR3 = UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb");
    
	public static final UUID SERVICE4_WRITE_STATE_CHAR6 = UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb");
	// Notification UUID
	private static final UUID SERVICE4 = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private static final UUID SERVICE4_NOTIFICATION_CHAR = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
    
	// Intent request codes
    private static final int REQUEST_ENABLE_BT = 2;
	
	private BluetoothListener bluetoothListener = null;
    private String mDeviceName = null;
    private String mDeviceAddress = null;

	private BluetoothAdapter mBluetoothAdapter;

    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private boolean deviceScanned = false;


    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mWriteStateCharacteristic1;
    private BluetoothGattCharacteristic mWriteStateCharacteristic3;
    private BluetoothGattCharacteristic mWriteStateCharacteristic6;
    
    private Handler mHandler;
    private Runnable mRunnable;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 3000;
    
    private DataTransmission dataTransmission = null;

    private int testCount = 0;
    
    // Write binary file
    private File mainStorage = null;
    private File file;
    private FileOutputStream fos;


    byte[] tempBuf;
    int picTotalLen = 0;
    int pktNum = 0;
    int lastPktSize = 0;
    int seqNum = 0;
    

    int tempPktId = 0;
    //int tempUARTPktId = 0;
    int bufOffset = 0;
    boolean picInfoPktRecv = false;
    boolean picDataRecvDone = true;
    private int picNum = 0;
    private Context context;
    
    public int hardware_state = -1;
    protected int power_notenough;
    
    public BluetoothLE3(BluetoothListener bluetoothListener, String mDeviceName) {
    	//super(bluetoothListener, mDeviceName);
        mHandler = new Handler();
        context = App.getContext();
        
        this.bluetoothListener = bluetoothListener;
        this.mDeviceName = mDeviceName;

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            //Toast.makeText(MainActivity.getMainActivity(), "BLE not supported!", Toast.LENGTH_SHORT).show();
            ((BluetoothListener) bluetoothListener).bleNotSupported();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            //Toast.makeText(MainActivity.getMainActivity(), "Bluetooth is not supported!", Toast.LENGTH_SHORT).show();
            ((BluetoothListener) bluetoothListener).bleNotSupported();
            return;
        }
        
        //tempBuf = new byte [128];
		dataTransmission = new DataTransmission(bluetoothListener, this);
    }
     
	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {
	    @Override
	    public void onServiceConnected(ComponentName componentName, IBinder service) {
	        mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
	        if (!mBluetoothLeService.initialize()) {
	            Log.e(TAG, "Unable to initialize Bluetooth");
//	            activity.finish();
	        }
	        // Automatically connects to the device upon successful start-up initialization.
	        mBluetoothLeService.connect(mDeviceAddress);
	    }
	
	    @Override
	    public void onServiceDisconnected(ComponentName componentName) {
	        mBluetoothLeService = null;
            unbindBleService();
	    }
	};

	
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                //Terminate the BLE connection timeout (10sec)
                mHandler.removeCallbacks(mRunnable);
                ((BluetoothListener) bluetoothListener).bleConnected();

//                Toast.makeText(activity, "BLE connected", Toast.LENGTH_SHORT).show();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                ((BluetoothListener) bluetoothListener).bleDisconnected();
                unbindBleService();

//                Toast.makeText(activity, "BLE disconnected!", Toast.LENGTH_SHORT).show();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
            	List<BluetoothGattService> gattServices = mBluetoothLeService.getSupportedGattServices();
            	mNotifyCharacteristic = gattServices.get(3).getCharacteristic(SERVICE4_NOTIFICATION_CHAR);
            	mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);

                mWriteStateCharacteristic1 = gattServices.get(3).getCharacteristic(SERVICE4_WRITE_STATE_CHAR1);
                mWriteStateCharacteristic3 = gattServices.get(3).getCharacteristic(SERVICE4_WRITE_STATE_CHAR3);
				mWriteStateCharacteristic6 = gattServices.get(3).getCharacteristic(SERVICE4_WRITE_STATE_CHAR6);

                Log.i(TAG, "BLE ACTION_GATT_SERVICES_DISCOVERED");

            } else if (BluetoothLeService.ACTION_DATA_WRITE_SUCCESS.equals(action)) {
                ((BluetoothListener) bluetoothListener).bleWriteStateSuccess();

            } else if (BluetoothLeService.ACTION_DATA_WRITE_FAIL.equals(action)) {
                ((BluetoothListener) bluetoothListener).bleWriteStateFail();

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
            	
            	StringBuffer Sbuffer = new StringBuffer("");
            	for(int ii=0; ii<data.length; ii++){
            		//String s1 = String.format("%8s", Integer.toBinaryString(data[ii] & 0xFF)).replace(' ', '0');
            		String s1 = Integer.toHexString(data[ii] & 0xFF);
            		Sbuffer.append(s1 + ",");
            	}
            	Log.i(TAG, Sbuffer.toString());

//                String dataString = "";
            	
            	if(data[0] == (byte)0xA9){
                    dataTransmission.checkPackets();
                }
                else if(data[0] == (byte)0xA7){
                    dataTransmission.parsePackets(data);
                }
                else if (data[0] == (byte)0xFA){
                    //((BluetoothListener) bluetoothListener).displayCurrentId("None");
                    Log.i(TAG, "----0xFA----");
                    ((BluetoothListener) bluetoothListener).bleNoPlug();
                }
                else if (data[0] == (byte)0xFB){
                    long temp = (data[4] & 0xFF) + (data[3] & 0xFF)*256 + (data[2] & 0xFF)*256*256 + (data[1] & 0xFF)*256*256*256;
                    hardware_state = (int)(data[5] & 0xFF);
                    power_notenough = (int)(data[6] & 0xFF);
                    
                    int debug1 = (data[7] & 0xFF) + (data[8]&0xFF) * 256;
                    int debug2 = (data[9] & 0xFF) + (data[10]&0xFF) * 256;
                    int voltage = (data[11] & 0xFF) + (data[12]&0xFF) * 256;
                    
                    int camera_state = (int)(data[13] & 0xFF);
                    int retransmit_size = (int)(data[14] & 0xFF);
                    int retransmit_id = (int)(data[15] & 0xFF);
                    
                    ((BluetoothListener) bluetoothListener).displayCurrentId(String.valueOf(temp), hardware_state, power_notenough);
                    
                    
                    
                    ((BluetoothListener) bluetoothListener).writeDebug("Device Debug: "+ hardware_state + " " + debug1+" "+debug2+" voltage: "+
                                                             voltage+" camera: " + Integer.toHexString(data[13] & 0xFF) +" size: "+retransmit_size + " id: " + retransmit_id);
//                    byte[] plugId = new byte[data.length-1];
//                    System.arraycopy(data, 1, plugId, 0, data.length - 1);
//                    ((BluetoothListener) bluetoothListener).blePlugInserted(plugId);
                                     
                }
                else if(data[0] == (byte)0xBB){
                	int voltage = (data[1] & 0xFF) + (data[2] & 0xFF)*256;
                	((BluetoothListener) bluetoothListener).writeDebug("Voltage: " + voltage);
                }
                else if(data[0] == (byte)0xDD){
                	((BluetoothListener) bluetoothListener).displayHardwareVersion(""+(int)data[1]);
                }
                else{
                	Log.i(TAG, "----BLE Can't handle data----");
                }           
            }
        }
    };

    private void unbindBleService() {
    	context.unbindService(mServiceConnection);
    	context.unregisterReceiver(mGattUpdateReceiver);
        deviceScanned = false;
    }

	
	public void bleConnect() {
		
		// Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                MainActivity.getMainActivity().startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        else {
            bleScan();
        }
		
		return;
	}

    public void bleDisconnect() {
        if(mBluetoothLeService != null) {
            mBluetoothLeService.disconnect();
        }

        if(!mConnected) {
            //Terminate the BLE connection timeout (10sec)
            mHandler.removeCallbacks(mRunnable);
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    public void bleWriteState(byte state) {
    	
    	if(state == 0x06 || state == 0x03){
    		ResultService3.pictureSending = true;
			dataTransmission.resetParameters();
		}

        if((mBluetoothLeService != null) && (mWriteStateCharacteristic1 != null)) {
			 mWriteStateCharacteristic1.setValue(new byte[]{state});
             mBluetoothLeService.writeCharacteristic(mWriteStateCharacteristic1);
        }
        return;
    }

	public void bleWriteAck(byte data) {

        if((mBluetoothLeService != null) && (mWriteStateCharacteristic3 != null)) {
            mWriteStateCharacteristic3.setValue(new byte[] { data });
            mBluetoothLeService.writeCharacteristic(mWriteStateCharacteristic3);
        }
        return;
    }

	public void bleWriteData(byte [] bytes){
		
        if((mBluetoothLeService != null) && (mWriteStateCharacteristic6 != null)) {
            mWriteStateCharacteristic6.setValue(bytes);
            mBluetoothLeService.writeCharacteristic(mWriteStateCharacteristic6);
        }
        return;
    }
	
    private void bleScan() {
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                ((BluetoothListener) bluetoothListener).bleConnectionTimeout();
//                    Log.i("BLE", "thread run");
            }
        }, SCAN_PERIOD);

        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }
	
	private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE_SUCCESS);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE_FAIL);
        return intentFilter;
    }

	
	public void onBleActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
        case REQUEST_ENABLE_BT:
        	// When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, enable BLE scan
                bleScan();
            } else{
                // User did not enable Bluetooth or an error occured
                //Toast.makeText(MainActivity.getMainActivity(), "Bluetooth did not enable!", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Bluetooth did not enable!");
//                activity.finish();
            }
        	break;
		}
		
	}

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    // Do nothing if target device is scanned
                    if(deviceScanned)
                        return;
                    
                    Log.d(TAG, "device="+device.getName()+" add="+device.getAddress());
                    String tmpName = device.getName();
                    if(mDeviceName.equals(device.getName())){
                        mDeviceAddress = device.getAddress();
                        deviceScanned = true;

                        Intent gattServiceIntent = new Intent(MainActivity.getMainActivity(), BluetoothLeService.class);

                        context.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
                        context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                }
            };
}
