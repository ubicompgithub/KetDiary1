package com.ubicomp.ketdiary.test.bluetoothle;

/**
 * A Fake BluetoothLE for always success
 * @author mudream
 *
 */
public class DebugBluetoothLE extends BluetoothLEWrapper {
	
	public DebugBluetoothLE(){}
	
	
	public int getState(){return 10;}
	
	
	@Override
	public boolean isConnected(){return true;}
}
