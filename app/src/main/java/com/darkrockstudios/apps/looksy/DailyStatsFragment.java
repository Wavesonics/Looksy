package com.darkrockstudios.apps.looksy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.Select;
import com.darkrockstudios.apps.looksy.data.Unlock;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Adam on 7/20/2015.
 */
public class DailyStatsFragment extends BaseFragment
{
	@Bind(R.id.DAILY_pie_chart)
	PieChart m_pieChart;

	public static DailyStatsFragment newInstance()
	{
		return new DailyStatsFragment();
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.daily_usage_chart;
	}

	@Nullable
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		final View view = super.onCreateView( inflater, container, savedInstanceState );

		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		updateViews();
	}

	private void updateViews()
	{
		List<Entry> data = new ArrayList<>( 7 );
		data.add( new Entry( unlocksOnDayOfWeek( DateTimeConstants.MONDAY ), 0 ) );
		data.add( new Entry( unlocksOnDayOfWeek( DateTimeConstants.TUESDAY ), 0 ) );
		data.add( new Entry( unlocksOnDayOfWeek( DateTimeConstants.WEDNESDAY ), 0 ) );
		data.add( new Entry( unlocksOnDayOfWeek( DateTimeConstants.THURSDAY ), 0 ) );
		data.add( new Entry( unlocksOnDayOfWeek( DateTimeConstants.FRIDAY ), 0 ) );
		data.add( new Entry( unlocksOnDayOfWeek( DateTimeConstants.SATURDAY ), 0 ) );
		data.add( new Entry( unlocksOnDayOfWeek( DateTimeConstants.SUNDAY ), 0 ) );
		PieDataSet dataSet = new PieDataSet( data, getString( R.string.daily_chart_description ) );
		dataSet.setColors( getColors() );

		LocalDate date = new LocalDate();
		String[] xVals = new String[]
				                 {
						                 date.withDayOfWeek( 1 ).dayOfWeek().getAsText(),
						                 date.withDayOfWeek( 2 ).dayOfWeek().getAsText(),
						                 date.withDayOfWeek( 3 ).dayOfWeek().getAsText(),
						                 date.withDayOfWeek( 4 ).dayOfWeek().getAsText(),
						                 date.withDayOfWeek( 5 ).dayOfWeek().getAsText(),
						                 date.withDayOfWeek( 6 ).dayOfWeek().getAsText(),
						                 date.withDayOfWeek( 7 ).dayOfWeek().getAsText()
				                 };

		PieData pieData = new PieData( xVals, dataSet );

		m_pieChart.setData( pieData );
		m_pieChart.animateY( 750 );
		m_pieChart.invalidate();

		m_pieChart.setDescription( getString( R.string.daily_chart_description ) );
	}

	private int unlocksOnDayOfWeek( final int dayOfWeek )
	{
		final List<Unlock> unlocks = new Select()
				                             .from( Unlock.class )
				                             .where( "DayOfWeek = ?", dayOfWeek )
				                             .execute();
		return unlocks.size();
	}

	private int[] getColors()
	{
		int stacksize = 7;
		int[] colors = new int[ 7 ];
		for( int i = 0; i < stacksize; i++ )
		{
			colors[ i ] = ColorTemplate.JOYFUL_COLORS[ i % ColorTemplate.JOYFUL_COLORS.length ];
		}

		return colors;
	}
}
