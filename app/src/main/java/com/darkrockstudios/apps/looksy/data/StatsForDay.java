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

		final DateTime sixHoursIn = startOfDay.plusHours( 6 );
		final DateTime twelveHoursIn = startOfDay.plusHours( 12 );
		final DateTime eighteenHoursIn = startOfDay.plusHours( 18 );
		final DateTime endOfDay = startOfDay.plusDays( 1 );

		m_earlyMorning = Unlock.countAllInRange( eighteenHoursIn, endOfDay );
		m_morning = Unlock.countAllInRange( twelveHoursIn, eighteenHoursIn );
		m_afterNoon = Unlock.countAllInRange( sixHoursIn, twelveHoursIn );
		m_evening = Unlock.countAllInRange( startOfDay, sixHoursIn );
	}

	public int getTotal()
	{
		return m_earlyMorning + m_morning + m_afterNoon + m_evening;
	}
}
