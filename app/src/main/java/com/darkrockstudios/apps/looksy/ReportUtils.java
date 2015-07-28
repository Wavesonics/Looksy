package com.darkrockstudios.apps.looksy;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.darkrockstudios.apps.looksy.data.StatsForDay;

import org.joda.time.DateTime;

/**
 * Created by Adam on 7/19/2015.
 */
public class ReportUtils
{
	public static DateTime getStartOfToday()
	{
		return DateTime.now().withTimeAtStartOfDay();
	}

	public static DateTime getStartOfTomorrow()
	{
		return getStartOfToday().plusDays( 1 );
	}

	public static DateTime getNoonTomorrow()
	{
		return getStartOfTomorrow().withHourOfDay( 12 );
	}

	public static DateTime getStartOfYesterday()
	{
		return getStartOfToday().minusDays( 1 );
	}

	public static DateTime getStartOfDay( final int daysBack )
	{
		return getStartOfToday().minusDays( daysBack );
	}

	public static String getTimeOfDay( @NonNull final StatsForDay statsForDay, @NonNull final Context context )
	{
		Resources res = context.getResources();
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

		return timeOfDay;
	}

	private static boolean isLargest( final int statToCheck, @NonNull final StatsForDay statsForDay )
	{
		return statsForDay.m_earlyMorning <= statToCheck
			   && statsForDay.m_morning <= statToCheck
			   && statsForDay.m_afterNoon <= statToCheck
			   && statsForDay.m_evening <= statToCheck;
	}
}
