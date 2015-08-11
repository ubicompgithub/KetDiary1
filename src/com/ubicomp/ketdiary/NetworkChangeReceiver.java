package com.ubicomp.ketdiary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.ubicomp.ketdiary.system.check.NetworkCheck;

/**
 * Receiver for getting the network condition
 * 
 * @author Stanley Wang
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

	private static final String TAG = "NETWORK_CHANGE_RECEIVER";

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		Log.d(TAG, action);

		if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION)
				&& !action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
			return;

		if (!NetworkCheck.networkCheck()) {
			Log.d(TAG, "NOT CONNECTED");
			return;
		}

		Intent regularIntent = new Intent(context, UploadService.class);
		context.startService(regularIntent);

	}

}
