package com.darkrockstudios.apps.looksy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Adam on 7/20/2015.
 */
public class DailyStatsFragment extends Fragment
{
	public static DailyStatsFragment newInstance()
	{
		return new DailyStatsFragment();
	}

	@Nullable
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		final View view = inflater.inflate( R.layout.daily_usage_chart, container, false );

		return view;
	}
}
