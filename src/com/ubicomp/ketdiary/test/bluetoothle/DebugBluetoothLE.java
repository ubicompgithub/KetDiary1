package com.ubicomp.ketdiary.test.bluetoothle;

public class DebugBluetoothLE extends BluetoothLEWrapper {
	public DebugBluetoothLE(){}
	public int getState(){return 10;}
	@Override
	public boolean isConnected(){return true;}
}
