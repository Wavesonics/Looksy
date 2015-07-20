package com.darkrockstudios.apps.looksy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darkrockstudios.apps.looksy.data.Unlock;

import org.joda.time.DateTime;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Adam on 7/20/2015.
 */
public class HomeFragment extends Fragment
{
	@Bind(R.id.test_view)
	TextView m_testView;

	@Bind(R.id.chart_fragment_container)
	ViewGroup m_fragmentContainerView;

	public static HomeFragment newInstance()
	{
		return new HomeFragment();
	}

	@Nullable
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		final View view = inflater.inflate( R.layout.home_fragment, container, false );
		ButterKnife.bind( this, view );

		if( savedInstanceState == null )
		{
			getChildFragmentManager().beginTransaction()
			                         .replace( R.id.chart_fragment_container, LatestWeekChartFragment.newInstance() ).commit();
		}

		return view;
	}

	private void updateViews()
	{
		final DateTime startToday = ReportUtils.getStartOfToday();

		DateTime end = DateTime.now();

		List<Unlock> today = Unlock.getAllInRange( startToday, end );
		List<Unlock> today_1 = Unlock.getAllInRange( ReportUtils.getStartOfYesterday(), startToday );
		List<Unlock> allUnlocks = Unlock.getAll();

		m_testView.setText( getString( R.string.summary, today.size(), today_1.size(), allUnlocks.size() ) );
	}

	@Override
	public void onResume()
	{
		super.onResume();

		updateViews();
	}
}
