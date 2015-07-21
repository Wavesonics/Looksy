package com.darkrockstudios.apps.looksy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darkrockstudios.apps.looksy.data.Unlock;

import org.joda.time.DateTime;

import java.util.List;

import butterknife.Bind;

/**
 * Created by Adam on 7/20/2015.
 */
public class LifeTimeStatsFragment extends BaseFragment
{
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

	@Override
	public void onResume()
	{
		super.onResume();

		updateViews();
	}

	private void updateViews()
	{
		final DateTime startToday = ReportUtils.getStartOfToday();

		DateTime end = DateTime.now();

		List<Unlock> today = Unlock.getAllInRange( startToday, end );
		List<Unlock> today_1 = Unlock.getAllInRange( ReportUtils.getStartOfYesterday(), startToday );
		List<Unlock> allUnlocks = Unlock.getAll();

		m_summaryView.setText( getString( R.string.life_time_summary, today.size(), today_1.size(), allUnlocks.size() ) );
	}
}
