package com.darkrockstudios.apps.looksy.background.recordcheck;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class RecordCheckReceiver extends WakefulBroadcastReceiver
{
	private static final String TAG = RecordCheckReceiver.class.getSimpleName();

	@Override
	public void onReceive( Context context, Intent intent )
	{
		Intent service = RecordCheckIntentService.startCheckRecordIntent( context );
		startWakefulService( context, service );
	}
}
