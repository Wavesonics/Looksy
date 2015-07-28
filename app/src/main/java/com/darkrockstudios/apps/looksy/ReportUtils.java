package com.darkrockstudios.apps.looksy;

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
}
