package com.ubicomp.ketdiary.noUse;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.BluetoothLE.BluetoothLE_old;
import com.ubicomp.ketdiary.BluetoothLE.BluetoothListener;


public class MainActivity2 extends Activity {

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

}
