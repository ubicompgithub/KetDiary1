package com.ubicomp.ketdiary.BluetoothLE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import android.util.Log;

import com.ubicomp.ketdiary.file.MainStorage;
import com.ubicomp.ketdiary.system.PreferenceControl;

/**
 * Created by larry on 15/7/17.
 */
public class DataTransmission {
    private static final String TAG = "DataTransmission";
    private static final int maximumPktNum = 80;

    private BluetoothListener bluetoothListener = null;
    private BluetoothLE2 ble = null;

    private File mainStorage = null;
    private File file;
    private FileOutputStream fos;

    private byte [][] picBuf;
    private byte [] tempBuf;
    private int pktNum = 0;
    private int lastPktSize = 0;

    private int tempPktId = 0;
    private int recvNum = 0;
    private int bufOffset = 0;
    private boolean picInfoPktRecv = false;
    
    private int picNum = 0;

    private Set<Integer> integerSet;

    public DataTransmission(BluetoothListener bluetoothListener, BluetoothLE2 ble){
    	this.bluetoothListener = bluetoothListener; 
        this.ble = ble;
        tempBuf = new byte [128];
        picBuf = new byte [maximumPktNum][];
        integerSet = new HashSet();
    }

    public void parsePackets(byte [] data){
        int seqNum = (data[2] & 0xFF) * 256 + (data[1] & 0xFF);

        // Checksum for BLE packet
        int checksum = 0;
        for(int i = 0; i < data.length-1; i++){
            checksum += (data[i] & 0xFF);
            checksum = checksum & 0xFF;
        }

        if (checksum != (data[data.length-1] & 0xFF)){
            Log.d(TAG, "Checksum error on ble packets ".concat(String.valueOf(seqNum)));
        }

        if( seqNum == 0x7FFF){
            if( picInfoPktRecv == false ) {
                picInfoPktRecv = true;
                tempPktId = 0;
                bufOffset = 0;
                int picTotalLen = (data[4] & 0xFF) * 256 + (data[3] & 0xFF);
                pktNum = picTotalLen / (128 - 6);
                if (pktNum % (128 - 6) != 0) {
                    pktNum++;
                    lastPktSize = picTotalLen % (128 - 6) + 6;
                }
                Log.d(TAG, "Total picture length:".concat(String.valueOf(picTotalLen)));
                Log.d(TAG, "Total packets:".concat(String.valueOf(pktNum)));
                Log.d(TAG, "Last packet size:".concat(String.valueOf(lastPktSize)));

                long ts = PreferenceControl.getUpdateDetectionTimestamp();
	            File dir = MainStorage.getMainStorageDirectory();
	            mainStorage = new File(dir, String.valueOf(ts));
	            String file_name = "PIC_" + ts + "_" + picNum + ".jpg";
	              
	            picNum ++ ;

                //Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                file = new File(mainStorage, file_name);
                try {
                    fos = new FileOutputStream(file, true);
                } catch (IOException e) {
                    Log.d(TAG, "FAIL TO OPEN");
                    fos = null;
                }
            }
            else{
                ble.bleWriteAck((byte) 0x05);
            }
        }
        else{
            if(bufOffset == 0){
                tempPktId = seqNum/8;
            }

            if(integerSet.contains(tempPktId))
                return;

            if( bufOffset/16 != seqNum %8) {
                Log.d(TAG, "Packet is recieved.".concat(String.valueOf(bufOffset / 16)).concat(String.valueOf(seqNum % 8)));
                bufOffset = 0;
                return;
            }

            System.arraycopy(data, 3, tempBuf, bufOffset, data.length - 4);
            bufOffset += (data.length - 4);

            if ( bufOffset == 128 || ((tempPktId == pktNum - 1) && bufOffset == lastPktSize) ) {
                if (tempPktId == pktNum-1) {
                    Log.d(TAG, "LastDataRecvLength: ".concat(String.valueOf(bufOffset)).concat(
                            " LastDataLength: ").concat(String.valueOf(lastPktSize)));
                }

                int sum = 0;
                for(int i = 0; i < bufOffset-2; i++){
                    sum += (tempBuf[i] & 0xFF);
                    sum = sum & 0xFF;
                }

                if (( sum & 0xFF ) == (tempBuf[bufOffset-2] & 0xFF) ){
                    Log.d(TAG, String.valueOf(tempPktId + 1).concat(" packet recieved."));
                    picBuf[tempPktId] = new byte[bufOffset - 6];
                    System.arraycopy(tempBuf, 4, picBuf[tempPktId], 0, bufOffset - 6);

                    integerSet.add(tempPktId);
                    recvNum++;
                    bufOffset = 0;

                    ((BluetoothListener) bluetoothListener).updateProcessRate((float)recvNum*100/pktNum );
                }else{
                    Log.d(TAG, "Checksum error in ".concat(String.valueOf(tempPktId)).concat("th packet."));
                    bufOffset = 0;
                }
            }
        }
    }

    public void checkPackets(){
        Log.i(TAG, "Dropout rate: " + (float) (pktNum - recvNum)*100/ pktNum + "%");
        if(recvNum == pktNum){
            try {
                for(int i = 0; i < pktNum; i++) {
                    fos.write(picBuf[i]);
                }
                bufOffset = 0;

                picInfoPktRecv = false;

                try {
                    fos.close();
                    //ble.bleWriteAck((byte) 0x05);
                    ble.bleWriteState((byte) 0x07);
                    resetParameters();
                    ((BluetoothListener) bluetoothListener).bleTakePictureSuccess();
                    ((BluetoothListener) bluetoothListener).showImgPreview(file.getAbsolutePath());
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
        else{
            int remainPktNum = pktNum - recvNum;
            if(remainPktNum > 18)
                remainPktNum = 18;

            byte [] bytes = new byte [20];
            bytes[0] = (byte)0xA3;
            bytes[1] = (byte)(remainPktNum & 0xFF);
            int j = 0;
            for(int i = 0; i < remainPktNum; i++){
                for(;j < pktNum; j++){
                    if(!integerSet.contains(j)){
                        bytes[i+2] = (byte)(j & 0xFF);
                        j++;
                        break;
                    }
                }
            }

            ble.bleWriteData(bytes);
        }
    }

    public void resetParameters(){
        Log.i(TAG, "Reset all parameters");
        picInfoPktRecv = false;
        recvNum = 0;
        bufOffset = 0;

        for(int i = 0; i < maximumPktNum; i++){
            picBuf[i] = null;
        }
        integerSet.clear();
    }
}
