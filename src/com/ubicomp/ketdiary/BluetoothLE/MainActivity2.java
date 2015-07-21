package com.ubicomp.ketdiary.BluetoothLE;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ubicomp.ketdiary.R;


public class MainActivity2 extends Activity implements BluetoothListener {

    private static final String TAG = "BluetoothLE";

	private BluetoothLE_old ble = null;
    MainActivity2 mainActivity = this;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);

        
        Button buttonStart = (Button)findViewById(R.id.buttonStart);

        buttonStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(ble != null) {
                    return;
                }
                ble = new BluetoothLE_old(mainActivity, "KetDiary-000");
                ble.bleConnect();
            }

        });
        
        
        Log.i(TAG, "On create"); 
        
        Button buttonClose = (Button)findViewById(R.id.buttonClose);

        buttonClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(ble != null) {
                    ble.bleDisconnect();
                    ble = null;
                }
            }

        });

	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ble != null) {
            ble.bleDisconnect();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ble.onBleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void bleNotSupported() {

//        this.finish();
    }

    @Override
    public void bleConnectionTimeout() {
        Toast.makeText(this, "BLE connection timeout", Toast.LENGTH_SHORT).show();
        if(ble != null) {
            ble = null;
        }
    }

    @Override
    public void bleConnected() {
        Log.i(TAG, "BLE connected");
    }

    @Override
    public void bleDisconnected() {
        Log.i(TAG, "BLE disconnected");
        if(ble != null) {
            ble = null;
        }
    }

    @Override
    public void bleWriteStateSuccess() {
        Log.i(TAG, "BLE ACTION_DATA_WRITE_SUCCESS");
    }

    @Override
    public void bleWriteStateFail() {
        Log.i(TAG, "BLE ACTION_DATA_WRITE_FAIL");
    }

    @Override
    public void bleNoPlug() {
        Log.i(TAG, "No test plug");
    }

    @Override
    public void blePlugInserted(byte[] plugId) {
        Log.i(TAG, "Test plug is inserted");
    }

    @Override
    public void bleColorReadings(byte[] colorReadings) {
        Log.i(TAG, "Color sensor readings");
    }

	@Override
	public void bleElectrodeAdcReading(byte state, byte[] adcReading) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bleTakePictureSuccess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateProcessRate(float rate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearProcesssRate() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void displayCurrentId(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bleTakePictureSuccess(Bitmap bitmap) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bleTakePictureFail(float dropRate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void imgDetect(Bitmap bitmap) {
		// TODO Auto-generated method stub
		
	}
}
