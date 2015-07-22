package com.darkrockstudios.apps.looksy;

/**
 * Created by Adam on 7/21/2015.
 */
public class PercentValueFormatter implements com.github.mikephil.charting.utils.ValueFormatter
{
	@Override
	public String getFormattedValue( float value )
	{
		return String.format( "%d%%", (long) value );
	}
}