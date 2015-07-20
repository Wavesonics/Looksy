package com.darkrockstudios.apps.looksy;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.darkrockstudios.apps.looksy.data.Unlock;
import com.darkrockstudios.apps.looksy.settings.SettingsActivity;
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

public class MainActivity extends AppCompatActivity
{
	private static ValueFormatter s_valueFormatter = new ValueFormatter();

	@Bind(R.id.test_view)
	TextView m_testView;

	@Bind(R.id.test_chart)
	BarChart m_chartView;

	@Bind(R.id.progressBar)
	ProgressBar m_progressBarView;

	private PopulateChartTask m_populateChartTask;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		ButterKnife.bind( this );

		m_chartView.setDrawValuesForWholeStack( false );
		m_chartView.setDescription( null );
		m_chartView.getAxisRight().setDrawLabels( false );
		m_chartView.setPinchZoom( false );
		m_chartView.setDoubleTapToZoomEnabled( false );
		m_chartView.setHighlightEnabled( false );

		YAxis yLabels = m_chartView.getAxisLeft();
		yLabels.setValueFormatter( s_valueFormatter );

		updateViews();
	}

	private void updateViews()
	{
		final DateTime startToday = ReportUtils.getStartOfToday();

		DateTime end = DateTime.now();

		List<Unlock> today = Unlock.getAllInRange( startToday, end );
		List<Unlock> today_1 = Unlock.getAllInRange( ReportUtils.getStartOfYesterday(), startToday );
		List<Unlock> allUnlocks = Unlock.getAll();

		m_testView.setText( getString( R.string.summary, today.size(), today_1.size(), allUnlocks.size() ) );

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

	@Override
	protected void onResume()
	{
		super.onResume();

		updateViews();
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.menu_main, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if( id == R.id.action_settings )
		{
			startActivity( new Intent( this, SettingsActivity.class ) );
			return true;
		}

		return super.onOptionsItemSelected( item );
	}
}
