package com.ubicomp.ketdiary.test.bluetoothle;

/**
 * Define the function that a BluetoothLE should have 
 * @author mudream
 *
 */
public class BluetoothLEWrapper { 
	
	/** 
	 * Contructor
	 */
	public BluetoothLEWrapper(){}
	
	
	/** 
	 * Get the State of BLE
	 * @return State of BLE
	 */
	public int getState(){return 0;}
	
	/** 
	 * Check if it is connected
	 * @return is connect?
	 */
	public boolean isConnected(){return false;}
	
	/** 
	 * Send signal to Device reset to init state. 
	 */
	public void ReturnToInitState(){}
	
	/** 
	 * Send signal to Device to start test.
	 */
	public void SendStartMsg(){}
	
	/** 
	 * Send signal to Device to request color.
	 */
	public void RequestColor(){}
	
	/** 
	 * Send signal to Device to close device. 
	 */
	public void CloseDevice(){}
	
	/** 
	 * Send signal to Device to clean state.
	 */
	public void CleanState(){}
	
	/** Close connection of Bluetooth 
	 */
	public void Close(){}
	
}
