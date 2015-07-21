package com.darkrockstudios.apps.looksy;

/**
 * Created by Adam on 7/20/2015.
 */
public class ValueFormatter implements com.github.mikephil.charting.utils.ValueFormatter
{
	@Override
	public String getFormattedValue( float value )
	{
		return String.format( "%d", (long) value );
	}
}