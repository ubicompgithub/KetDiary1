package com.ubicomp.ketdiary.test.bluetoothle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

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
    void bleTakePictureSuccess(Bitmap bitmap);
    
    /* Take picture failed*/
    void bleTakePictureFail(float dropRate);

    /* Update process rate*/
    void updateProcessRate(String rate);

    /* Clear process rate*/
    void clearProcesssRate();
    
    /* Retransmit*/
    void PictureRetransmit(int count);

    /* Show image preview*/
    void imgDetect(Bitmap bitmap);

    /* Display current saliva Id*/
    void displayCurrentId(String id, int hardwareState, int power_notenough);
    
    void displayHardwareVersion(String version);
    
    void writeDebug(String msg);
    
    void displayPower(int power);
}
