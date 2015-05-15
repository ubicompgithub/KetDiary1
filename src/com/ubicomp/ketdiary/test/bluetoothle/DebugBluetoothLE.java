package com.ubicomp.ketdiary.test.bluetoothle;

public class DebugBluetoothLE extends BluetoothLEWrapper {
	public DebugBluetoothLE(){}
	public int getState(){return 10;}
	public boolean isConnnected(){return true;}
	public void Close(){}
}
