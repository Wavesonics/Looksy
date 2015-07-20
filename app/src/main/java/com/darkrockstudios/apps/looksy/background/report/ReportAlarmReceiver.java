package com.darkrockstudios.apps.looksy.background.report;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class ReportAlarmReceiver extends WakefulBroadcastReceiver
{
	public ReportAlarmReceiver()
	{
	}

	@Override
	public void onReceive( Context context, Intent intent )
	{
		Intent reportIntent = new Intent( context, ReportService.class );
		startWakefulService( context, reportIntent );
	}
}
