package com.twlkyao.androidcloud;

import com.twlkyao.utils.ObserverService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartUpReceiver extends BroadcastReceiver {

	String TAG = "StartUpReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent i = new Intent(context, ObserverService.class); // Start the ObserverService at statup.
		context.startService(i);
		Log.d(TAG, "Start ObserverService");
	}
	
}
