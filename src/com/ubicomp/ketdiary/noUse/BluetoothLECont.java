package com.ubicomp.ketdiary.noUse;

import android.app.Activity;
import android.os.CountDownTimer;


/**
 * 10 mins client part, should be a background service.
 * @author mudream
 *
 */
public class BluetoothLECont{
	
	private static String TAG = "BLECont";
	public BluetoothLECont inst = new BluetoothLECont();
	private BluetoothLECont(){}
	
	private BluetoothLE bluetoothle = null;
	
	public void startWaiting(Activity activity){
		DBControl.inst.startTesting();
		new CountDownTimer(360000, 360000){
			@Override
			public void onTick(long ms){}
			@Override
			public void onFinish(){
				//bluetoothle = new BluetoothLE(activity, DBControl.inst.getDeviceID(activity.getApplicationContext()));
		
			}
		};
	}
}
