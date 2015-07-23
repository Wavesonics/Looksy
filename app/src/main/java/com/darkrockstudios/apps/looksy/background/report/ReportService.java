package com.darkrockstudios.apps.looksy.background.report;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.darkrockstudios.apps.looksy.MainActivity;
import com.darkrockstudios.apps.looksy.R;
import com.darkrockstudios.apps.looksy.ReportUtils;
import com.darkrockstudios.apps.looksy.data.StatsForDay;
import com.darkrockstudios.apps.looksy.settings.PreferenceKey;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

public class ReportService extends IntentService
{
	public static final String TAG = ReportService.class.getSimpleName();

	public static final int NOTIFICATION_ID_REPORT = 1;

	public ReportService()
	{
		super( ReportService.class.getSimpleName() );
	}

	@Override
	protected void onHandleIntent( Intent intent )
	{
		postReportNotification();

		ReportAlarmReceiver.completeWakefulIntent( intent );
	}

	private StatsForDay getUnlocksYesterday()
	{
		final StatsForDay statsForDay = new StatsForDay( ReportUtils.getStartOfYesterday() );

		return statsForDay;
	}

	private String getReportText( @NonNull final StatsForDay statsForDay )
	{
		Resources res = getResources();
		String[] timesofDay = res.getStringArray( R.array.time_of_day );

		final String timeOfDay;

		if( isLargest( statsForDay.m_evening, statsForDay ) )
		{
			timeOfDay = timesofDay[ 3 ];
		}
		else if( isLargest( statsForDay.m_afterNoon, statsForDay ) )
		{
			timeOfDay = timesofDay[ 2 ];
		}
		else if( isLargest( statsForDay.m_morning, statsForDay ) )
		{
			timeOfDay = timesofDay[ 1 ];
		}
		else
		{
			timeOfDay = timesofDay[ 0 ];
		}

		return getString( R.string.notification_report_full, statsForDay.getTotal(), timeOfDay );
	}

	private boolean isLargest( final int statToCheck, @NonNull final StatsForDay statsForDay )
	{
		return statsForDay.m_earlyMorning <= statToCheck
		       && statsForDay.m_morning <= statToCheck
		       && statsForDay.m_afterNoon <= statToCheck
		       && statsForDay.m_evening <= statToCheck;
	}

	private void postReportNotification()
	{
		NotificationManager notificationManager = (NotificationManager)
				                                          getSystemService( NOTIFICATION_SERVICE );

		Intent intent = new Intent( this, MainActivity.class );
		PendingIntent pendingIntent = PendingIntent.getActivity( this, 0, intent, 0 );

		final StatsForDay looksYesterday = getUnlocksYesterday();
		String report = getReportText( looksYesterday );

		Notification notification = new NotificationCompat.Builder( this )
				                            .setContentTitle( getString( R.string.notification_report_title ) )
				                            .setContentText( getString( R.string.notification_report_text,
				                                                        looksYesterday.getTotal() ) )
				                            .setSmallIcon( R.drawable.ic_app )
				                            .setContentIntent( pendingIntent )
				                            .setAutoCancel( true )
				                            .setStyle( new NotificationCompat.BigTextStyle()
						                                       .bigText( report ) )
				                            .build();

		notificationManager.notify( NOTIFICATION_ID_REPORT, notification );
	}

	public static void scheduleReport( final Context context )
	{
		final Intent intent = new Intent( context, ReportAlarmReceiver.class );
		final PendingIntent alarmIntent = PendingIntent.getBroadcast( context, 0, intent, 0 );

		AlarmManager alarmManager = (AlarmManager) context.getSystemService( ALARM_SERVICE );
		alarmManager.cancel( alarmIntent ); // Cancel any existing alarm

		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );
		if( prefs.getBoolean( PreferenceKey.REPORTS_ENABLED, true ) )
		{
			final String reportTimeStr = prefs.getString( PreferenceKey.REPORTS_TIME, "12:00" );
			LocalTime reportTimeOfDay = new LocalTime( reportTimeStr );

			DateTime today = ReportUtils.getStartOfToday();

			final DateTime reportTime;
			DateTime reportTimeToday = today.withHourOfDay( reportTimeOfDay.getHourOfDay() ).withMinuteOfHour( reportTimeOfDay
					                                                                                                   .getMinuteOfHour() );
			if( reportTimeToday.isAfterNow() )
			{
				reportTime = reportTimeToday;
				Log.i( TAG, "Scheduling a report for today: " + reportTime );
			}
			else
			{
				DateTime tomorrow = ReportUtils.getStartOfTomorrow();
				reportTime = tomorrow.withHourOfDay( reportTimeOfDay.getHourOfDay() ).withMinuteOfHour( reportTimeOfDay
						                                                                                        .getMinuteOfHour() );
				Log.i( TAG, "Scheduling a report for tomorrow: " + reportTime );
			}

			alarmManager
					.setInexactRepeating( AlarmManager.RTC, reportTime.getMillis(), AlarmManager.INTERVAL_DAY, alarmIntent );
		}
		else
		{
			Log.i( TAG, "Canceling daily report." );
		}
	}
}
