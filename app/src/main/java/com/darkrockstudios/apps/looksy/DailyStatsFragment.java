package com.darkrockstudios.apps.looksy;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import icepick.Icicle;

/**
 * Created by Adam on 7/20/2015.
 */
public class DailyStatsFragment extends BaseFragment
{
	private PopulateDataTask m_populateTask;

	@Icicle
	boolean m_animatedIn;

	@Bind(R.id.DAILY_pie_chart)
	PieChart m_pieChart;

	@Bind(R.id.progressBar)
	ProgressBar m_progressBarView;

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

		m_pieChart.setUsePercentValues( true );
		m_pieChart.setHighlightEnabled( false );
		m_pieChart.setDescription( getString( R.string.daily_chart_description ) );

		return view;
	}

	private void updateViews()
	{
		cancelTask();

		m_populateTask = new PopulateDataTask();
		m_populateTask.execute();
	}

	private void cancelTask()
	{
		if( m_populateTask != null )
		{
			m_populateTask.cancel( true );
			m_populateTask = null;
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();

		updateViews();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		cancelTask();
	}

	private class PopulateDataTask extends AsyncTask<Void, Void, PieData>
	{
		private final String m_description;

		public PopulateDataTask()
		{
			m_description = getString( R.string.daily_chart_description );
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			if( isAdded() )
			{
				m_progressBarView.setVisibility( View.VISIBLE );
				m_pieChart.setVisibility( View.GONE );
			}
		}

		@Override
		protected PieData doInBackground( Void... params )
		{
			List<Entry> data = new ArrayList<>( 7 );
			for( int ii = DateTimeConstants.MONDAY; ii <= DateTimeConstants.SUNDAY; ++ii )
			{
				final int unlocks = unlocksOnDayOfWeek( ii );
				if( unlocks > 0 )
				{
					data.add( new Entry( unlocks, 0 ) );
				}
			}

			PieDataSet dataSet = new PieDataSet( data, m_description );
			dataSet.setColors( getColors() );
			dataSet.setValueFormatter( new PercentValueFormatter() );

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

			return pieData;
		}

		@Override
		protected void onPostExecute( PieData pieData )
		{
			super.onPostExecute( pieData );

			if( isAdded() )
			{
				m_pieChart.setData( pieData );
				if( !m_animatedIn )
				{
					m_pieChart.animateY( 750 );
					m_animatedIn = true;
				}
				else
				{
					m_pieChart.invalidate();
				}

				m_progressBarView.setVisibility( View.GONE );
				m_pieChart.setVisibility( View.VISIBLE );
			}
		}
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
