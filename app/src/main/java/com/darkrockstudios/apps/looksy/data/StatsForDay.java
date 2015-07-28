package com.darkrockstudios.apps.looksy.data;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;

/**
 * Created by Adam on 7/22/2015.
 */
public class StatsForDay
{
	public final DateTime m_startOfDay;
	public final int      m_earlyMorning;
	public final int      m_morning;
	public final int      m_afterNoon;
	public final int      m_evening;

	public StatsForDay( @NonNull final DateTime startOfDay )
	{
		m_startOfDay = startOfDay;

		final DateTime sixHoursIn = startOfDay.withHourOfDay( 6 );
		final DateTime twelveHoursIn = startOfDay.withHourOfDay( 12 );
		final DateTime eighteenHoursIn = startOfDay.withHourOfDay( 18 );
		final DateTime endOfDay = startOfDay.plusDays( 1 );

		m_earlyMorning = Unlock.countAllInRange( startOfDay, sixHoursIn );
		m_morning = Unlock.countAllInRange( sixHoursIn, twelveHoursIn );
		m_afterNoon = Unlock.countAllInRange( twelveHoursIn, eighteenHoursIn );
		m_evening = Unlock.countAllInRange( eighteenHoursIn, endOfDay );
	}

	public int getTotal()
	{
		return m_earlyMorning + m_morning + m_afterNoon + m_evening;
	}
}
