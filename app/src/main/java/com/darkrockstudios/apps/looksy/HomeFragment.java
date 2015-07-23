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
public class HomeFragment extends BaseFragment
{
	private PopulateDataTask m_populateTask;

	@Bind(R.id.test_view)
	TextView m_testView;

	@Bind(R.id.chart_fragment_container)
	ViewGroup m_fragmentContainerView;

	public static HomeFragment newInstance()
	{
		return new HomeFragment();
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.home_fragment;
	}

	@Nullable
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		final View view = super.onCreateView( inflater, container, savedInstanceState );

		if( savedInstanceState == null )
		{
			getChildFragmentManager().beginTransaction()
			                         .replace( R.id.chart_fragment_container, LatestWeekChartFragment.newInstance() ).commit();
		}

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

	private class PopulateDataTask extends AsyncTask<Void, Void, Integer>
	{
		@Override
		protected Integer doInBackground( Void... params )
		{
			final DateTime startToday = ReportUtils.getStartOfToday();
			DateTime end = DateTime.now();
			int today = Unlock.countAllInRange( startToday, end );

			return today;
		}

		@Override
		protected void onPostExecute( Integer unlocksToday )
		{
			super.onPostExecute( unlocksToday );

			if( isAdded() )
			{
				m_testView.setText( getString( R.string.home_summary, unlocksToday ) );
			}
		}
	}
}
