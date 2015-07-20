package com.darkrockstudios.apps.looksy.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.darkrockstudios.apps.looksy.R;
import com.darkrockstudios.apps.looksy.background.report.ReportService;

/**
 * Created by Adam on 7/20/2015.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		getFragmentManager().beginTransaction().replace( android.R.id.content, new MyPreferenceFragment() ).commit();
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this );
		prefs.registerOnSharedPreferenceChangeListener( this );
	}

	@Override
	protected void onStop()
	{
		super.onStop();

		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this );
		prefs.unregisterOnSharedPreferenceChangeListener( this );
	}

	@Override
	public void onSharedPreferenceChanged( SharedPreferences sharedPreferences, String key )
	{
		if( PreferenceKey.REPORTS_ENABLED.equals( key )
		    || PreferenceKey.REPORTS_TIME.equals( key ) )
		{
			ReportService.scheduleReport( this );
		}
	}

	public static class MyPreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate( final Bundle savedInstanceState )
		{
			super.onCreate( savedInstanceState );
			addPreferencesFromResource( R.xml.settings );
		}
	}
}
