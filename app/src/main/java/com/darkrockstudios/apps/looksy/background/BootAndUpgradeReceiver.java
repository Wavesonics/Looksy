package com.darkrockstudios.apps.looksy.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.darkrockstudios.apps.looksy.background.report.ReportService;

/**
 * Created by Adam on 7/19/2015.
 */
public class BootAndUpgradeReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive( Context context, Intent intent )
	{
		final String action = intent.getAction();
		if( action.equals( "android.intent.action.BOOT_COMPLETED" )
		    || action.equals( "android.intent.action.MY_PACKAGE_REPLACED" ) )
		{
			ReportService.scheduleReport( context );
		}
	}
}