package com.ubicomp.ketdiary.test.bluetoothle;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ubicomp.ketdiary.MainActivity;
import com.ubicomp.ketdiary.ResultService3;
import com.ubicomp.ketdiary.data.file.MainStorage;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.test.color.ImageDetection;

/**
 * Created by larry on 15/7/17.
 */
public class DataTransmission {
    private static final String TAG = "DataTransmission";
    private static final int maximumPktNum = 120;
	private static final int timeout = 8 *1000;

    private BluetoothListener bluetoothListener = null;
    private BluetoothLE3 ble = null;

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
    
    private int picNum = 10;

    private Set<Integer> integerSet;
    
    private Timer timer = null;
    private int counter = 0;
    private static int RETRANSMIT_TIMES = 3;
    
    private int retransmit_state = STATE_NOMORE_TRANSMIT; 
    private static int STATE_NOMORE_TRANSMIT = 1;
    private static int STATE_RETRANSMIT = 2;
    private static int STATE_RECEIVE_AFTER_RETRANSMIT = 3;

    public DataTransmission(BluetoothListener bluetoothListener, BluetoothLE3 ble){
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

            }
            else{
                ble.bleWriteAck((byte) 0x05);
                resetTimeoutTimer();
                counter = 0;
            }
        }
        else{
            if(bufOffset == 0){
                tempPktId = seqNum/8;
            }

            if(integerSet.contains(tempPktId)){
            	Log.i(TAG, tempPktId + "has been receive repeatedly");
            	return;
            }
                

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

                    resetTimeoutTimer();
                    
                    if(retransmit_state == STATE_RETRANSMIT)
                    	retransmit_state = STATE_RECEIVE_AFTER_RETRANSMIT;
                    
                    ((BluetoothListener) bluetoothListener).updateProcessRate(""+(float)recvNum*100/pktNum +"%  p: "+ tempPktId);
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

                int currentIdx = 0;
                byte [] pictureBytes = new byte [(pktNum-1) * 122 + (lastPktSize - 6)];
                for(int i = 0; i < pktNum; i++) {
                    System.arraycopy(picBuf[i], 0, pictureBytes, currentIdx, picBuf[i].length);
                    currentIdx += picBuf[i].length;
                }

                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                bmpFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
                ByteArrayInputStream inputStream = new ByteArrayInputStream(pictureBytes);               
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, bmpFactoryOptions);
                bufOffset = 0;

                picInfoPktRecv = false;
                ble.bleWriteState((byte) 0x07);
                         
                if(timer!= null){
                	timer.cancel();
                	timer = null;
                }

                ((BluetoothListener) bluetoothListener).bleTakePictureSuccess(bitmap);                  
                resetParameters();
                
                return;
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
						Log.i(TAG, "Lost " + j + " th packet");
                        bytes[i+2] = (byte)(j & 0xFF);
                        j++;
                        break;
                    }
                }
            }
            
            ((BluetoothListener) bluetoothListener).clearProcesssRate();  
            ble.bleWriteData(bytes);
        }
    }

    public void resetParameters(){
        Log.i(TAG, "Reset all parameters");
        picInfoPktRecv = false;
        recvNum = 0;
        bufOffset = 0;
        retransmit_state = STATE_NOMORE_TRANSMIT; 
        
        for(int i = 0; i < maximumPktNum; i++){
            picBuf[i] = null;
        }
        integerSet.clear();
    }
	 public void resetTimeoutTimer(){
		if(timer != null){
			timer.cancel();
			timer = null;
		}
        timer = new Timer();
        timer.schedule(new TimeoutTask(), timeout);
    }
	class TimeoutTask extends TimerTask{

        @Override
        public void run() {
            Log.i(TAG, "Timeout timer was  fired " + counter);
            if(counter < RETRANSMIT_TIMES ){
            	if(ResultService3.isConnect){
	                checkPackets();
	                ((BluetoothListener) bluetoothListener).PictureRetransmit(counter);
	                counter++;
	                if(retransmit_state == STATE_NOMORE_TRANSMIT)
	                	retransmit_state = STATE_RETRANSMIT;
	                else if(retransmit_state == STATE_RECEIVE_AFTER_RETRANSMIT){
	                	retransmit_state = STATE_RETRANSMIT;
	                	counter = 0;
	                }
	                resetTimeoutTimer();
            	}
            }
            else{
            	if(timer!=null){
            		timer.cancel();
            		timer = null;
            	}
                ((BluetoothListener) bluetoothListener).bleTakePictureFail((float) (pktNum - recvNum) / pktNum);
            }
        }
    }
}
