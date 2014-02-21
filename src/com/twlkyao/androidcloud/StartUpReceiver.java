package com.twlkyao.androidcloud;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.twlkyao.utils.LogUtils;

public class StartUpReceiver extends BroadcastReceiver {

	private String TAG = "StartUpReceiver";
	private boolean DEBUG = true;
	private LogUtils logUtils = new LogUtils(DEBUG, TAG);
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent i = new Intent(context, ObserverService.class); // Start the ObserverService at statup.
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		logUtils.d(TAG, "Start ObserverService");
		context.startService(i);
	}
	
}
