package com.darkrockstudios.apps.looksy;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.darkrockstudios.apps.looksy.data.StatsForDay;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import icepick.Icicle;

/**
 * Created by Adam on 7/20/2015.
 */
public class LatestWeekChartFragment extends BaseFragment
{
	private static ValueFormatter s_valueFormatter = new ValueFormatter();

	private PopulateChartTask m_populateChartTask;

	@Icicle
	boolean m_animatedIn;

	@Bind(R.id.bar_chart)
	BarChart m_chartView;

	@Bind(R.id.progressBar)
	ProgressBar m_progressBarView;

	private int[] m_color;

	public static LatestWeekChartFragment newInstance()
	{
		return new LatestWeekChartFragment();
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.latest_week_chart_fragment;
	}

	@Override
	public void onCreate( @Nullable Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		Resources res = getResources();
		m_color = new int[]
				          {
						          res.getColor( R.color.stack_evening ),
						          res.getColor( R.color.stack_afternoon ),
						          res.getColor( R.color.stack_morning ),
						          res.getColor( R.color.stack_early_morning )
				          };
	}

	@Nullable
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		final View view = super.onCreateView( inflater, container, savedInstanceState );

		//m_chartView.setDrawValuesForWholeStack( true );
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
		final StatsForDay statsForDay = new StatsForDay( ReportUtils.getStartOfDay( day ) );

		float values[] = new float[]
				                 {
						                 statsForDay.m_earlyMorning,
						                 statsForDay.m_morning,
						                 statsForDay.m_afterNoon,
						                 statsForDay.m_evening
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

	private class PopulateChartTask extends AsyncTask<Void, Void, BarData>
	{
		private String m_todayStr;
		private String[] m_stackLabels;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			m_progressBarView.setVisibility( View.VISIBLE );
			m_chartView.setVisibility( View.GONE );

			m_todayStr = getString( R.string.chart_today );
			m_stackLabels = getStackLabels();
		}

		@Override
		protected BarData doInBackground( Void... params )
		{
			BarData barData = null;

			if( isAdded() )
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
						xVals.add( m_todayStr );
					}
					else
					{
						xVals.add( ReportUtils.getStartOfDay( day ).dayOfWeek().getAsText() );
					}
				}

				List<BarDataSet> dataSets = new ArrayList<>();
				BarDataSet set = new BarDataSet( dataEntry, null );
				set.setColors( m_color );
				set.setStackLabels( m_stackLabels );
				set.setValueFormatter( s_valueFormatter );
				dataSets.add( set );

				barData = new BarData( xVals, dataSets );
			}

			return barData;
		}

		@Override
		protected void onPostExecute( BarData barData )
		{
			m_populateChartTask = null;
			if( isAdded() )
			{
				m_chartView.setData( barData );
				if( !m_animatedIn )
				{
					m_chartView.animateY( 250 );
					m_animatedIn = true;
				}
				else
				{
					m_chartView.invalidate();
				}

				m_progressBarView.setVisibility( View.GONE );
				m_chartView.setVisibility( View.VISIBLE );
			}
		}
	}
}
