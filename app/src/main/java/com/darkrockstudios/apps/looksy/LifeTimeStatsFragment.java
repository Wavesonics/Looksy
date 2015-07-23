package com.darkrockstudios.apps.looksy;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darkrockstudios.apps.looksy.data.Unlock;

import org.joda.time.DateTime;

import butterknife.Bind;

/**
 * Created by Adam on 7/20/2015.
 */
public class LifeTimeStatsFragment extends BaseFragment
{
	private PopulateDataTask m_populateTask;

	@Bind(R.id.LIFETIME_summary)
	TextView m_summaryView;

	public static LifeTimeStatsFragment newInstance()
	{
		return new LifeTimeStatsFragment();
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.lifetime_stats_fragment;
	}

	@Nullable
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		final View view = super.onCreateView( inflater, container, savedInstanceState );

		updateViews();

		return view;
	}

	private void cancelTask()
	{
		if( m_populateTask != null )
		{
			m_populateTask.cancel( true );
			m_populateTask = null;
		}
	}

	private void updateViews()
	{
		cancelTask();

		m_populateTask = new PopulateDataTask();
		m_populateTask.execute();
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

	private static class Data
	{
		public final int m_today;
		public final int m_yesterday;
		public final int m_allTime;

		private Data( int today, int yesterday, int allTime )
		{
			m_today = today;
			m_yesterday = yesterday;
			m_allTime = allTime;
		}
	}

	private class PopulateDataTask extends AsyncTask<Void, Void, Data>
	{
		@Override
		protected Data doInBackground( Void... params )
		{
			final DateTime startToday = ReportUtils.getStartOfToday();

			DateTime end = DateTime.now();

			final int today = Unlock.countAllInRange( startToday, end );
			final int today_1 = Unlock.countAllInRange( ReportUtils.getStartOfYesterday(), startToday );
			final int allUnlocks = Unlock.countAll();

			return new Data( today, today_1, allUnlocks );
		}

		@Override
		protected void onPostExecute( Data data )
		{
			super.onPostExecute( data );

			if( isAdded() )
			{
				m_summaryView
						.setText( getString( R.string.life_time_summary, data.m_today, data.m_yesterday, data.m_allTime ) );
			}
		}
	}
}
