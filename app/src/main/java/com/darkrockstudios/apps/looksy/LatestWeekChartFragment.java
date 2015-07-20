package com.darkrockstudios.apps.looksy;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.darkrockstudios.apps.looksy.data.Unlock;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Adam on 7/20/2015.
 */
public class LatestWeekChartFragment extends Fragment
{
	private static ValueFormatter s_valueFormatter = new ValueFormatter();

	private PopulateChartTask m_populateChartTask;

	@Bind(R.id.bar_chart)
	BarChart m_chartView;

	@Bind(R.id.progressBar)
	ProgressBar m_progressBarView;

	public static LatestWeekChartFragment newInstance()
	{
		return new LatestWeekChartFragment();
	}

	@Nullable
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		final View view = inflater.inflate( R.layout.latest_week_chart_fragment, container, false );
		ButterKnife.bind( this, view );

		m_chartView.setDrawValuesForWholeStack( false );
		m_chartView.setDescription( null );
		m_chartView.getAxisRight().setDrawLabels( false );
		m_chartView.setPinchZoom( false );
		m_chartView.setDoubleTapToZoomEnabled( false );
		m_chartView.setHighlightEnabled( false );

		YAxis yLabels = m_chartView.getAxisLeft();
		yLabels.setValueFormatter( s_valueFormatter );

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
		populateChart();
	}

	private void populateChart()
	{
		if( m_populateChartTask != null )
		{
			m_populateChartTask.cancel( true );
			m_populateChartTask = null;
		}

		m_populateChartTask = new PopulateChartTask();
		m_populateChartTask.execute();
	}

	private void addEntriesForDay( final int day, final int index, @NonNull final List<BarEntry> dataEntry )
	{
		DateTime startOfDay = ReportUtils.getStartOfDay( day );
		DateTime sixHoursIn = startOfDay.plusHours( 6 );
		DateTime twelveHoursIn = startOfDay.plusHours( 12 );
		DateTime eighteenHoursIn = startOfDay.plusHours( 18 );
		DateTime endOfDay = startOfDay.plusDays( 1 );

		float values[] = new float[]
				                 {
						                 Unlock.getAllInRange( eighteenHoursIn, endOfDay ).size(),
						                 Unlock.getAllInRange( twelveHoursIn, eighteenHoursIn ).size(),
						                 Unlock.getAllInRange( sixHoursIn, twelveHoursIn ).size(),
						                 Unlock.getAllInRange( startOfDay, sixHoursIn ).size()

				                 };

		BarEntry barEntry = new BarEntry( values, index );

		dataEntry.add( barEntry );
	}

	private String[] getStackLabels()
	{
		String[] labels = new String[]{
				                              getString( R.string.stack_label_4 ),
				                              getString( R.string.stack_label_3 ),
				                              getString( R.string.stack_label_2 ),
				                              getString( R.string.stack_label_1 )

		};

		return labels;
	}

	private int[] getColors()
	{
		Resources res = getResources();

		// have as many colors as stack-values per entry
		int[] colors = new int[]
				               {
						               res.getColor( R.color.stack_evening ),
						               res.getColor( R.color.stack_afternoon ),
						               res.getColor( R.color.stack_morning ),
						               res.getColor( R.color.stack_early_morning )
				               };
		/*
		int stacksize = 4;
		for( int i = 0; i < stacksize; i++ )
		{
			colors[ i ] = ColorTemplate.JOYFUL_COLORS[ i ];
		}
		*/

		return colors;
	}

	private class PopulateChartTask extends AsyncTask<Void, Void, BarData>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			m_progressBarView.setVisibility( View.VISIBLE );
			m_chartView.setVisibility( View.INVISIBLE );
		}

		@Override
		protected BarData doInBackground( Void... params )
		{
			List<String> xVals = new ArrayList<>();

			List<BarEntry> dataEntry = new ArrayList<>();
			final int MAX_DAYS = 7;
			for( int ii = 0; ii < MAX_DAYS; ++ii )
			{
				final int day = MAX_DAYS - (ii + 1);
				addEntriesForDay( ii, day, dataEntry );
				if( day == 0 )
				{
					xVals.add( getString( R.string.chart_today ) );
				}
				else
				{
					xVals.add( ReportUtils.getStartOfDay( day ).dayOfWeek().getAsText() );
				}
			}

			List<BarDataSet> dataSets = new ArrayList<>();
			BarDataSet set = new BarDataSet( dataEntry, null );
			set.setColors( getColors() );
			set.setStackLabels( getStackLabels() );
			set.setValueFormatter( s_valueFormatter );
			dataSets.add( set );

			BarData barData = new BarData( xVals, dataSets );
			return barData;
		}

		@Override
		protected void onPostExecute( BarData barData )
		{
			m_populateChartTask = null;
			m_chartView.setData( barData );
			m_chartView.invalidate();

			m_progressBarView.setVisibility( View.INVISIBLE );
			m_chartView.setVisibility( View.VISIBLE );
		}
	}

	private static class ValueFormatter implements com.github.mikephil.charting.utils.ValueFormatter
	{
		@Override
		public String getFormattedValue( float value )
		{
			return String.format( "%d", (long) value );
		}
	}
}
