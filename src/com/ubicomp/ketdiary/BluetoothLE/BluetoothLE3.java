package com.ubicomp.ketdiary.BluetoothLE;

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

import com.ubicomp.ketdiary.MainActivity;
import com.ubicomp.ketdiary.file.MainStorage;
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


    public BluetoothLE3(BluetoothListener bluetoothListener, String mDeviceName) {
    	//super(bluetoothListener, mDeviceName);
        mHandler = new Handler();

        this.bluetoothListener = bluetoothListener;
        this.mDeviceName = mDeviceName;

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!MainActivity.getMainActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            //Toast.makeText(MainActivity.getMainActivity(), "BLE not supported!", Toast.LENGTH_SHORT).show();
            ((BluetoothListener) bluetoothListener).bleNotSupported();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) MainActivity.getMainActivity().getSystemService(Context.BLUETOOTH_SERVICE);
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
                    ((BluetoothListener) bluetoothListener).displayCurrentId(String.valueOf(temp));
                }
                else{
                	Log.i(TAG, "----BLE Can't handle data----");
                }
            	
//            	 switch(data[0]) { // Handling notification depending on types
//                 case (byte)0xFA:
//                     Log.i(TAG, "----0xFA----");
//                     ((BluetoothListener) bluetoothListener).bleNoPlug();
//                     break;
//                 case (byte)0xFB:
//                     Log.i(TAG, "----0xFB----");
//                     byte[] plugId = new byte[data.length-1];
//                     System.arraycopy(data, 1, plugId, 0, data.length - 1);
//                     ((BluetoothListener) bluetoothListener).blePlugInserted(plugId);
//                     break;
//                 case (byte)0xFC:
//                 case (byte)0xFD:
//                 case (byte)0xFE:
//                     byte[] adcReading = new byte[data.length-1];
//                     System.arraycopy(data, 1, adcReading, 0, data.length - 1);
//                     ((BluetoothListener) bluetoothListener).bleElectrodeAdcReading(data[0], adcReading);
//                     break;
//
//                 case (byte)0xFF:
//                     Log.i(TAG, "----0xFF----");
//                     byte[] colorReadings = new byte[data.length-1];
//                     System.arraycopy(data, 1, colorReadings, 0, data.length-1);
//                     ((BluetoothListener) bluetoothListener).bleColorReadings(colorReadings);
//                     break;
//            	 }
				
//                if(data[0] == (byte)0xA7){
//                    seqNum = (data[2] & 0xFF)*256 + (data[1] & 0xFF);
//
////                    // Checksum for BLE packet
////                    int checksum = 0;
////                    for(int i = 0; i < data.length-1; i++){
//
////                        checksum += (data[i] & 0xFF);
////                        checksum = checksum & 0xFF;
////                    }
////
////                    if (checksum != (data[data.length-1] & 0xFF)){
////                        Log.d(TAG, "Checksum error on ble packets ".concat(String.valueOf(seqNum)));
////                    }
//
//
//
//                    if( seqNum == 0x7FFF){
//                        if( picInfoPktRecv == false ) {
//                            picInfoPktRecv = true;
//                            picDataRecvDone = false;
//                            tempPktId = 0;
//                            bufOffset = 0;
//                            picTotalLen = (data[4] & 0xFF) * 256 + (data[3] & 0xFF);
//                            pktNum = picTotalLen / (128 - 6);
//                            if (pktNum % (128 - 6) != 0) {
//                                pktNum++;
//                                lastPktSize = picTotalLen % (128 - 6) + 6;
//                            }
//                            //bleWriteData((byte) 0x05);
//                            Log.d(TAG, "Total picture length:".concat(String.valueOf(picTotalLen)));
//                            Log.d(TAG, "Total packets:".concat(String.valueOf(pktNum)));
//                            Log.d(TAG, "Last packet size:".concat(String.valueOf(lastPktSize)));
//
////                            if (mainStorage == null) {
////                                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
////                                    mainStorage = new File(Environment.getExternalStorageDirectory(), "TempPicDir");
////                                else
////                                    mainStorage = new File(App.getContext().getFilesDir(), "TempPicDir");
////                            }
////                            if (!mainStorage.exists())
////                                mainStorage.mkdirs();
////
////                            timestamp = new Timestamp(System.currentTimeMillis());
//                            
//                          
////                        if (mainDirectory == null)
////                            mainDirectory = new File(mainStorage.getAbsolutePath(), String.valueOf(timestamp));
////                        if (!mainDirectory.exists())
////                            mainDirectory.mkdirs();
//                            long ts = PreferenceControl.getUpdateDetectionTimestamp();
//                            File dir = MainStorage.getMainStorageDirectory();
//                            mainStorage = new File(dir, String.valueOf(ts));
//                            String file_name = "PIC_" + ts + "_" + picNum + ".jpg";
//                            
//                            picNum ++ ;
//                          
//                            //file = new File(mainStorage, "PIC_".concat(String.valueOf(ts)).concat(".jpg"));
//                            file = new File(mainStorage, file_name);
//                            try {
//                                fos = new FileOutputStream(file, true);
//                            } catch (IOException e) {
//                                Log.d(TAG, "FAIL TO OPEN");
//                                fos = null;
//                            }
//                        }
//                        else{
//                            bleWriteData((byte) 0x05);
//                        }
//                    }
//                    else{
//                        if( picDataRecvDone == false ) {
//                            if( seqNum / 8 < tempPktId ) {
//                                //bleWriteData((byte) (0xF0|(0xFF & tempPktId)));
//                                bleWriteData((byte) 0x05);
//                                Log.d(TAG, "Packet has been received.".concat(String.valueOf(seqNum / 8)).concat(String.valueOf(tempPktId)));
//                                return;
//                            }
//
//                            if( bufOffset / 16 != seqNum % 8) {
//                                Log.d(TAG, "Packet is recieved.".concat(String.valueOf(bufOffset / 16)).concat(String.valueOf(seqNum % 8)));
//                                return;
//                            }
//
//                            System.arraycopy(data, 3, tempBuf, bufOffset, data.length - 4);
//                            bufOffset += (data.length - 4);
//
//                            if ( bufOffset == 128 || ((tempPktId == pktNum - 1) && bufOffset == lastPktSize) ) {
//                                tempPktId++;
//                                if (tempPktId == pktNum) {
//                                    Log.d(TAG, "LastDataRecvLength: ".concat(String.valueOf(bufOffset)).concat(
//                                            " LastDataLength: ").concat(String.valueOf(lastPktSize)));
//                                }
//                                int sum = 0;
//                                for(int i = 0; i < bufOffset-2; i++){
//                                    sum += (tempBuf[i] & 0xFF);
//                                    sum = sum & 0xFF;
//                                }
//
//                                if (( sum & 0xFF ) == (tempBuf[bufOffset-2] & 0xFF) ){
//                                    Log.d(TAG, String.valueOf(tempPktId).concat(" packets recieved."));
//                                    byte[] byteToWrite = new byte[bufOffset - 6];
//                                    System.arraycopy(tempBuf, 4, byteToWrite, 0, bufOffset - 6);
//                                    try {
//                                        fos.write(byteToWrite);
//                                        ((BluetoothListener) bluetoothListener).updateProcessRate((float)tempPktId*100/pktNum );
//
//                                        bufOffset = 0;
//                                        if(tempPktId == pktNum || tempPktId % 2 == 1) {
////                                            bleWriteData((byte) 0x05);
////                                            bleWriteData((byte) 0x05);
////                                            bleWriteData((byte) 0x05);
////                                            bleWriteData((byte) 0x05);
//                                        }
//                                        picInfoPktRecv = false;
//                                        if (tempPktId == pktNum) {
//                                            Log.d(TAG, "Can not enter here more than 1 time.");
//                                            try {
//                                                fos.close();
//                                                picInfoPktRecv = false;
//                                                picDataRecvDone = true;
//                                                tempPktId = 0;
//                                                bufOffset = 0;
//                                                bleWriteState((byte)0x07);
//                                                ((BluetoothListener) bluetoothListener).bleTakePictureSuccess();
//                                            } catch (IOException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }else{
//                                    Log.d(TAG, "Checksum error in ".concat(String.valueOf(tempPktId)).concat("th packet."));
//                                    tempPktId--;
//                                    bufOffset = 0;
//                                }
//                            }
//                        }
//                    }
                    
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

               

//                int color_sensor0[] = new int[4];
//                int color_sensor1[] = new int[4];
//                for(int i=0; i<4; i++) {
//                    color_sensor0[i] = data[(i*2)+1]<<8 + data[i*2];
//                    color_sensor1[i] = data[(i*2)+9]<<8 + data[i*2+8];
                

            
            
            }
        }
    };

    private void unbindBleService() {
    	MainActivity.getMainActivity().unbindService(mServiceConnection);
    	MainActivity.getMainActivity().unregisterReceiver(mGattUpdateReceiver);
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

                        MainActivity.getMainActivity().bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
                        MainActivity.getMainActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                }
            };
}
