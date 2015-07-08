package com.example.bletest;

import android.app.Activity;
import android.content.Intent;

public interface BluetoothListener {
	
	public Activity activity = null;
	
    void onActivityResult(int requestCode, int resultCode, Intent data);

    /* BLE is not supported in this device */
    void bleNotSupported();

    /* BLE connection establishment timeout (10sec) */
    void bleConnectionTimeout();

    /* BLE connection established successfully */
    void bleConnected();

    /* BLE disconnected */
    void bleDisconnected();

    /* BLE write state success */
    void bleWriteStateSuccess();

    /* BLE write state fail */
    void bleWriteStateFail();

    /* Can not detect test plug */
    void bleNoPlug();

    /* Test plug is detected with its ID */
    void blePlugInserted(byte[] plugId);

    /* Electrode state:
    state: 0xFC=not yet conduct, 0xFD=1st is conducted, 2ed is conducted,
    adcReading: 2 bytes
     */
    void bleElectrodeAdcReading(byte state, byte[] adcReading);

    /* Color sensor readings */
    void bleColorReadings(byte[] colorReadings);
    
    // Add by Larry
    /* Take picture successfully*/
    void bleTakePictureSuccess();

    /* Update process rate*/
    void updateProcessRate(float rate);

    /* Clear process rate*/
    void clearProcesssRate();
}
