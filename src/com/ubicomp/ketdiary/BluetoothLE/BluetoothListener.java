package com.ubicomp.ketdiary.BluetoothLE;

import android.content.Intent;

public interface BluetoothListener {
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
}
