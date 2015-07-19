package com.ubicomp.ketdiary.BluetoothLE;

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


@SuppressLint("NewApi")
public class BluetoothLE_old {
	private static final String TAG = "BluetoothLE";

    // Write UUID
    public static final UUID SERVICE4_WRITE_STATE_CHAR = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");

	// Notification UUID
	private static final UUID SERVICE4 = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private static final UUID SERVICE4_NOTIFICATION_CHAR = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
    
	// Intent request codes
    private static final int REQUEST_ENABLE_BT = 2;
	
	private Activity activity = null;
    private String mDeviceName = null;
    private String mDeviceAddress = null;

	private BluetoothAdapter mBluetoothAdapter;

    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private boolean deviceScanned = false;


    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mWriteStateCharacteristic;

    private Handler mHandler;
    private Runnable mRunnable;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 3000;

    private int testCount = 0;


    public BluetoothLE_old(Activity activity, String mDeviceName) {
        mHandler = new Handler();

        this.activity = activity;
        this.mDeviceName = mDeviceName;

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(activity, "BLE not supported!", Toast.LENGTH_SHORT).show();
            ((BluetoothListener) activity).bleNotSupported();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(activity, "Bluetooth is not supported!", Toast.LENGTH_SHORT).show();
            ((BluetoothListener) activity).bleNotSupported();
            return;
        }
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
                ((BluetoothListener) activity).bleConnected();

//                Toast.makeText(activity, "BLE connected", Toast.LENGTH_SHORT).show();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                ((BluetoothListener) activity).bleDisconnected();
                unbindBleService();

//                Toast.makeText(activity, "BLE disconnected!", Toast.LENGTH_SHORT).show();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
            	List<BluetoothGattService> gattServices = mBluetoothLeService.getSupportedGattServices();
            	mNotifyCharacteristic = gattServices.get(3).getCharacteristic(SERVICE4_NOTIFICATION_CHAR);
            	mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);

                mWriteStateCharacteristic = gattServices.get(3).getCharacteristic(SERVICE4_WRITE_STATE_CHAR);

                Log.i(TAG, "BLE ACTION_GATT_SERVICES_DISCOVERED");

            } else if (BluetoothLeService.ACTION_DATA_WRITE_SUCCESS.equals(action)) {
                ((BluetoothListener) activity).bleWriteStateSuccess();

            } else if (BluetoothLeService.ACTION_DATA_WRITE_FAIL.equals(action)) {
                ((BluetoothListener) activity).bleWriteStateFail();

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);

//                String dataString = "";
//                for(int i=0; i<data.length; i++) {
//                    dataString += data[i] + " ";
//                }
//                Log.i(TAG, dataString);
//
//                if(testCount == 1) {
//                    Log.i(TAG, "WRITE 0x03");
//                    bleWriteState((byte)0x03);
//                }
//                testCount++;

                switch(data[0]) { // Handling notification depending on types
                    case (byte)0xFA:
                        Log.i(TAG, "----0xFA----");
                        ((BluetoothListener) activity).bleNoPlug();
                        break;
                    case (byte)0xFB:
                        Log.i(TAG, "----0xFB----");
                        byte[] plugId = new byte[data.length-1];
                        System.arraycopy(data, 1, plugId, 0, data.length - 1);
                        ((BluetoothListener) activity).blePlugInserted(plugId);
                        break;
                    case (byte)0xFC:
                    case (byte)0xFD:
                    case (byte)0xFE:
                        byte[] adcReading = new byte[data.length-1];
                        System.arraycopy(data, 1, adcReading, 0, data.length - 1);
                        ((BluetoothListener) activity).bleElectrodeAdcReading(data[0], adcReading);
                        break;

                    case (byte)0xFF:
                        Log.i(TAG, "----0xFF----");
                        byte[] colorReadings = new byte[data.length-1];
                        System.arraycopy(data, 1, colorReadings, 0, data.length-1);
                        ((BluetoothListener) activity).bleColorReadings(colorReadings);
                        break;
                }

//                int color_sensor0[] = new int[4];
//                int color_sensor1[] = new int[4];
//                for(int i=0; i<4; i++) {
//                    color_sensor0[i] = data[(i*2)+1]<<8 + data[i*2];
//                    color_sensor1[i] = data[(i*2)+9]<<8 + data[i*2+8];
                

            }
            else{
                	Log.i(TAG, "----BLE Can't handle data----");
                }
            
            
        }
    };

    private void unbindBleService() {
        activity.unbindService(mServiceConnection);
        activity.unregisterReceiver(mGattUpdateReceiver);
        deviceScanned = false;
    }

	
	public void bleConnect() {
		
		// Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
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

        if((mBluetoothLeService != null) && (mWriteStateCharacteristic != null)) {
            mWriteStateCharacteristic.setValue(new byte[] { state });
            mBluetoothLeService.writeCharacteristic(mWriteStateCharacteristic);
        }
        return;
    }

    private void bleScan() {
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                ((BluetoothListener) activity).bleConnectionTimeout();
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
                Toast.makeText(activity, "Bluetooth did not enable!", Toast.LENGTH_SHORT).show();
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

                        Intent gattServiceIntent = new Intent(activity, BluetoothLeService.class);

                        activity.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
                        activity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                }
            };
}
