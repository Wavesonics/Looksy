package com.darkrockstudios.apps.looksy;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.darkrockstudios.apps.looksy.data.StatsForDay;
import com.darkrockstudios.apps.looksy.data.Unlock;

import org.joda.time.DateTime;

import butterknife.Bind;

/**
 * Created by Adam on 7/23/2015.
 */
public class DailyReportFragment extends BaseFragment
{
	private PopulateDataTask m_populateTask;

	public static DailyReportFragment newInstance()
	{
		return new DailyReportFragment();
	}

	@Bind(R.id.DAILY_REPORT_test)
	TextView m_testView;

	@Bind(R.id.progressBar)
	ProgressBar m_progressBarView;

	@Override
	protected int getLayoutId()
	{
		return R.layout.daily_report_fragment;
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

	private class PopulateDataTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			if( isAdded() )
			{
				m_progressBarView.setVisibility( View.VISIBLE );
				m_testView.setVisibility( View.GONE );
			}
		}

		@Override
		protected String doInBackground( Void... params )
		{
			String report = null;

			final Activity activity = getActivity();
			if( isAdded() && activity != null )
			{
				final DateTime startOfYesterday = ReportUtils.getStartOfYesterday();
				//final DateTime startOfToday = ReportUtils.getStartOfToday();

				final DateTime yesterdayLastWeekStart = startOfYesterday.minusWeeks( 1 );
				final DateTime yesterdayLastWeekEnd = yesterdayLastWeekStart.plusDays( 1 );
				final int unlocksThisDaylastWeek = Unlock.countAllInRange( yesterdayLastWeekStart, yesterdayLastWeekEnd );

				StatsForDay statsForDay = new StatsForDay( ReportUtils.getStartOfYesterday() );
				final String timeOfDay = ReportUtils.getTimeOfDay( statsForDay, activity );

				report = getString( R.string.daily_report_full,
									statsForDay.getTotal(),
									timeOfDay,
									statsForDay.m_earlyMorning,
									statsForDay.m_morning,
									statsForDay.m_afterNoon,
									statsForDay.m_evening,
									unlocksThisDaylastWeek );
			}

			return report;
		}

		@Override
		protected void onPostExecute( String reportStr )
		{
			super.onPostExecute( reportStr );

			if( isAdded() )
			{
				m_testView.setText( reportStr );

				m_progressBarView.setVisibility( View.GONE );
				m_testView.setVisibility( View.VISIBLE );
			}
		}
	}
}
