package com.darkrockstudios.apps.looksy.data;

import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Adam on 7/16/2015.
 */
@Table(name = "Unlock")
public class Unlock extends Model
{
	@Column(name = "Timestamp")
	public Long m_timestamp;

	@Column(name = "MonthOfYear")
	public int m_monthOfYear;

	@Column(name = "DayOfMonth")
	public int m_dayOfMonth;

	@Column(name = "DayOfWeek")
	public int m_dayOfWeek;

	@Column(name = "HourOfDay")
	public int m_hourOfDay;

	public Unlock()
	{
		super();
	}

	public Unlock( @NonNull final DateTime timestamp )
	{
		super();
		m_timestamp = timestamp.getMillis();
		m_monthOfYear = timestamp.getMonthOfYear();
		m_dayOfMonth = timestamp.getDayOfMonth();
		m_dayOfWeek = timestamp.getDayOfWeek();
		m_hourOfDay = timestamp.getHourOfDay();
	}

	public static List<Unlock> getAllInRange( @NonNull final DateTime start, @NonNull final DateTime end )
	{
		return new Select()
				       .from( Unlock.class )
					   .where( "Timestamp > ? AND Timestamp < ?", start.getMillis(), end.getMillis() )
				       .orderBy( "Timestamp ASC" )
				       .execute();
	}

	public static List<Unlock> getAll()
	{
		return new Select()
				       .from( Unlock.class )
				       .orderBy( "Timestamp ASC" )
				       .execute();
	}
}
