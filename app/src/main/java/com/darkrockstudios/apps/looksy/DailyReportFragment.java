package com.darkrockstudios.apps.looksy;

/**
 * Created by Adam on 7/23/2015.
 */
public class DailyReportFragment extends BaseFragment
{
	public static DailyReportFragment newInstance()
	{
		return new DailyReportFragment();
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.daily_report_fragment;
	}
}
